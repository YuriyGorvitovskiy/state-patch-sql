package org.state.patch.sql.zzz.processor;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.state.patch.sql.zzz.database.Database;
import org.state.patch.sql.zzz.patch.OpColumnCreate;
import org.state.patch.sql.zzz.patch.OpColumnDelete;
import org.state.patch.sql.zzz.patch.OpRecordChange;
import org.state.patch.sql.zzz.patch.OpRecordDelete;
import org.state.patch.sql.zzz.patch.OpRecordInsert;
import org.state.patch.sql.zzz.patch.OpTableCreate;
import org.state.patch.sql.zzz.patch.OpTableDelete;
import org.state.patch.sql.zzz.patch.Operation;
import org.state.patch.sql.zzz.patch.Patch;

import com.fasterxml.jackson.databind.ObjectMapper;

public class PatchProcessor implements Consumer<Patch> {
    public static final String CURRENT_VERSION = "0.0.1";

    final Database           database;
    final Map<String, Table> tables = new HashMap<>();

    public PatchProcessor(Database database) {
        this.database = database;
    }

    public void prepareDatabase() {
        String version = getSchemaVersion();
        if (StringUtils.isEmpty(version)) {
            version = initializeSchema();
        }
        if (!CURRENT_VERSION.equals(version)) {
            throw new RuntimeException("Database Schema version '" + version
                    + "' is not matching service version '" + CURRENT_VERSION + "'");
        }

        loadTables();
    }

    @Override
    public void accept(Patch patch) {
        process(patch, true);
    }

    private String initializeSchema() {
        try (InputStream in = PatchProcessor.class.getResourceAsStream("initial-schema.json")) {
            byte[] bytes = IOUtils.toByteArray(in);
            Patch resource = new ObjectMapper().readValue(bytes, Patch.class);
            process(resource, false);
            setSchemaVersion(CURRENT_VERSION);
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
        System.out.println("New Schema initialized with version '" + CURRENT_VERSION + "'.");
        return CURRENT_VERSION;
    }

    private void setSchemaVersion(String version) {
        String sql = null;
        try (Connection connection = database.datasource.getConnection()) {
            sql = database.sqlInsert("schema_version", "id", "version");
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, "state-patch-sql");
                statement.setString(2, version);
                statement.executeUpdate();
            }
        } catch (Throwable ex) {
            throw new RuntimeException("Failed SQL:\n" + sql, ex);
        }
    }

    private String getSchemaVersion() {
        String sql = null;
        try (Connection connection = database.datasource.getConnection()) {
            sql = database.sqlCheckTableExists();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, "schema_version");
                try (ResultSet result = statement.executeQuery()) {
                    if (!result.next()) {
                        throw new Exception("Can't check for table existence.");
                    }
                    if (!result.getBoolean(1)) {
                        return null;
                    }
                }
            }

            sql = database.sqlSelect("version").from("schema_version").whereMatch("id").toSql();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, "state-patch-sql");
                try (ResultSet result = statement.executeQuery()) {
                    if (!result.next()) {
                        throw new Exception("Can't check for table existence.");
                    }
                    return result.getString(1);
                }
            }
        } catch (Throwable ex) {
            throw new RuntimeException("Failed SQL:\n" + sql, ex);
        }
    }

    private void loadTables() {
        String sql = null;
        try (Connection connection = database.datasource.getConnection()) {
            sql = database.sqlSelect("table_name", "column_name", "type", "primary_ix")
                .from("schema_column")
                .orderBy("table_name", "primary_ix")
                .toSql();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        Table table = tables.computeIfAbsent(result.getString(1), (k) -> new Table(k));

                        String columnName = result.getString(2);
                        String columnType = result.getString(3);

                        result.getInt(4);
                        boolean primary = !result.wasNull();

                        Column column = new Column(columnName, columnType, primary);
                        if (primary) {
                            table.primary.add(column);
                        }
                        table.columns.put(column.name, column);
                    }
                }
            }
        } catch (Throwable ex) {
            throw new RuntimeException("Failed SQL:\n" + sql, ex);
        }
    }

    public void process(Patch patch, boolean updateSchema) {
        for (Operation operation : patch.ops) {
            if (operation instanceof OpTableCreate) {
                createTable((OpTableCreate) operation, updateSchema);
            } else if (operation instanceof OpTableDelete) {
                deleteTable((OpTableDelete) operation, updateSchema);
            } else if (operation instanceof OpColumnCreate) {
                createColumn((OpColumnCreate) operation, updateSchema);
            } else if (operation instanceof OpColumnDelete) {
                deleteColumn((OpColumnDelete) operation, updateSchema);
            } else if (operation instanceof OpRecordInsert) {
                insertRecord((OpRecordInsert) operation, updateSchema);
            } else if (operation instanceof OpRecordChange) {
                changeRecord((OpRecordChange) operation, updateSchema);
            } else if (operation instanceof OpRecordDelete) {
                deleteRecord((OpRecordDelete) operation, updateSchema);
            }
        }
    }

    private void insertRecord(OpRecordInsert operation, boolean updateSchema) {
        System.out.println("Insert record '" + operation.id + "'.");
        String[] parts = operation.id.split(":");
        Table table = tables.get(parts[0]);
        if (null == table) {
            System.out.println("Unsupproted table '" + parts + "'.");
            return;
        }
        if (table.primary.size() != parts.length - 1) {
            throw new RuntimeException(
                "Primary key has " + table.primary + " columns, but id contains " + (parts.length - 1) + " parts.");
        }

        List<String> columnNames = new ArrayList<>();
        for (Column column : table.primary) {
            columnNames.add(column.name);
        }

        for (String key : operation.attrs.keySet()) {
            Column column = table.columns.get(key);
            if (null != column && !column.primary) {
                columnNames.add(column.name);
            }
        }

        String sql = null;
        try (Connection connection = database.datasource.getConnection()) {
            sql = database.sqlInsert(table.name, columnNames.toArray(new String[columnNames.size()]));
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                int i = 1;
                for (String columnName : columnNames) {
                    Column column = table.columns.get(columnName);
                    database.setJSON(
                        statement,
                        i,
                        column.type,
                        (i < parts.length ? parts[i] : operation.attrs.get(columnName)));
                    i++;
                }
                statement.executeUpdate();
            }

        } catch (Throwable ex) {
            throw new RuntimeException("Failed SQL:\n" + sql, ex);
        }
    }

    private void changeRecord(OpRecordChange operation, boolean updateSchema) {
        System.out.println("Update record '" + operation.id + "'.");
        String[] parts = operation.id.split(":");
        Table table = tables.get(parts[0]);
        if (null == table) {
            System.out.println("Unsupproted table '" + parts + "'.");
            return;
        }
        if (table.primary.size() != parts.length - 1) {
            throw new RuntimeException(
                "Primary key has " + table.primary + " columns, but id contains " + (parts.length - 1) + " parts.");
        }

        List<String> primaryColumns = new ArrayList<>();
        for (Column column : table.primary) {
            primaryColumns.add(column.name);
        }

        List<String> updateColumns = new ArrayList<>();
        for (String key : operation.attrs.keySet()) {
            Column column = table.columns.get(key);
            if (null != column && !column.primary) {
                updateColumns.add(column.name);
            }
        }

        String sql = null;
        try (Connection connection = database.datasource.getConnection()) {
            sql = database.sqlUpdate(table.name, updateColumns, primaryColumns);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                int i = 1;
                for (String columnName : updateColumns) {
                    Column column = table.columns.get(columnName);
                    database.setJSON(
                        statement,
                        i,
                        column.type,
                        operation.attrs.get(columnName));
                    i++;
                }
                for (Column column : table.primary) {
                    database.setJSON(
                        statement,
                        i,
                        column.type,
                        parts[i - updateColumns.size()]);
                    i++;
                }
                statement.executeUpdate();
            }

        } catch (Throwable ex) {
            throw new RuntimeException("Failed SQL:\n" + sql, ex);
        }
    }

    private void deleteRecord(OpRecordDelete operation, boolean updateSchema) {
        System.out.println("Delete record '" + operation.id + "'.");
        String[] parts = operation.id.split(":");
        Table table = tables.get(parts[0]);
        if (null == table) {
            System.out.println("Unsupproted table '" + parts + "'.");
            return;
        }
        if (table.primary.size() != parts.length - 1) {
            throw new RuntimeException(
                "Primary key has " + table.primary + " columns, but id contains " + (parts.length - 1) + " parts.");
        }

        List<String> primaryColumns = new ArrayList<>();
        for (Column column : table.primary) {
            primaryColumns.add(column.name);
        }

        String sql = null;
        try (Connection connection = database.datasource.getConnection()) {
            sql = database.sqlDelete(table.name, primaryColumns.toArray(new String[primaryColumns.size()]));
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                int i = 1;
                for (Column column : table.primary) {
                    database.setJSON(
                        statement,
                        i,
                        column.type,
                        parts[i]);
                    i++;
                }
                statement.executeUpdate();
            }
        } catch (Throwable ex) {
            throw new RuntimeException("Failed SQL:\n" + sql, ex);
        }
    }

    private void createTable(OpTableCreate op, boolean updateSchema) {
        System.out.println("Creating table '" + op.table + "'.");
        String sql = null;
        try (Connection connection = database.datasource.getConnection()) {
            sql = database.sqlCreateTable(op);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.execute();
            }

            if (!updateSchema) {
                return;
            }
            sql = database.sqlInsert("schema_column", "table_name", "column_name", "type", "primary_ix");
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                long index = 1;
                for (OpTableCreate.Column column : op.columns) {
                    statement.setString(1, op.table);
                    statement.setString(2, column.name);
                    statement.setString(3, column.type);
                    if (column.primary) {
                        statement.setLong(4, index++);
                    } else {
                        statement.setNull(4, database.getNullType("integer"));
                    }
                    statement.addBatch();
                }
                statement.executeBatch();
            }

            Table table = new Table(op.table);
            tables.put(table.name, table);
            for (OpTableCreate.Column column : op.columns) {
                Column col = new Column(column.name, column.type, column.primary);
                if (col.primary) {
                    table.primary.add(col);
                }
                table.columns.put(col.name, col);
            }
        } catch (Throwable ex) {
            throw new RuntimeException("Failed SQL:\n" + sql, ex);
        }
    }

    private void deleteTable(OpTableDelete operation, boolean updateSchema) {
        System.out.println("Deleting table '" + operation.table + "'.");
        String sql = null;
        try (Connection connection = database.datasource.getConnection()) {
            sql = database.sqlDeleteTable(operation);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.execute();
            }

            if (!updateSchema) {
                return;
            }

            sql = database.sqlDelete("schema_column", "table_name");
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, operation.table);
                statement.executeUpdate();
            }

            tables.remove(operation.table);

        } catch (Throwable ex) {
            throw new RuntimeException("Failed SQL:\n" + sql, ex);
        }
    }

    private void createColumn(OpColumnCreate operation, boolean updateSchema) {
        System.out.println("Creating column '" + operation.table + "." + operation.column + "'.");
        String sql = null;
        try (Connection connection = database.datasource.getConnection()) {
            sql = database.sqlCreateColumn(operation);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.execute();
            }

            if (!updateSchema) {
                return;
            }

            sql = database.sqlInsert("schema_column", "table_name", "column_name", "type", "primary_ix");
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, operation.table);
                statement.setString(2, operation.column);
                statement.setString(3, operation.type);
                statement.setNull(4, database.getNullType("integer"));

                statement.execute();
            }

            Table table = tables.get(operation.table);
            table.columns.put(operation.column, new Column(operation.column, operation.type, false));
        } catch (Throwable ex) {
            throw new RuntimeException("Failed SQL:\n" + sql, ex);
        }
    }

    private void deleteColumn(OpColumnDelete operation, boolean updateSchema) {
        System.out.println("Deleting column '" + operation.table + "." + operation.column + "'.");
        String sql = null;
        try (Connection connection = database.datasource.getConnection()) {
            sql = database.sqlDeleteColumn(operation);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.execute();
            }

            if (!updateSchema) {
                return;
            }

            sql = database.sqlDelete("schema_column", "table_name", "column_name");
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, operation.table);
                statement.setString(2, operation.column);
                statement.executeUpdate();
            }

            Table table = tables.get(operation.table);
            if (null != table) {
                table.columns.remove(operation.column);
            }

        } catch (Throwable ex) {
            throw new RuntimeException("Failed SQL:\n" + sql, ex);
        }
    }
}
