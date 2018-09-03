package org.state.patch.sql.processor;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.state.patch.sql.database.Database;
import org.state.patch.sql.patch.CreateColumn;
import org.state.patch.sql.patch.CreateTable;
import org.state.patch.sql.patch.DeleteColumn;
import org.state.patch.sql.patch.DeleteTable;
import org.state.patch.sql.patch.Operation;
import org.state.patch.sql.patch.Patch;

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
                        int primaryIndex = result.getInt(4);
                        boolean primary = !result.wasNull();

                        Column column = new Column(columnName, columnType, primary);
                        if (primary) {
                            table.primary.add(primaryIndex, column);
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
        for (Operation operation : patch.operations) {
            if (operation instanceof CreateTable) {
                createTable((CreateTable) operation, updateSchema);
            } else if (operation instanceof DeleteTable) {
                deleteTable((DeleteTable) operation, updateSchema);
            } else if (operation instanceof CreateColumn) {
                createColumn((CreateColumn) operation, updateSchema);
            } else if (operation instanceof DeleteColumn) {
                deleteColumn((DeleteColumn) operation, updateSchema);
            }
        }
    }

    private void createTable(CreateTable operation, boolean updateSchema) {
        System.out.println("Creating table '" + operation.name + "'.");
        String sql = null;
        try (Connection connection = database.datasource.getConnection()) {
            sql = database.sqlCreateTable(operation);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.execute();
            }

            if (!updateSchema) {
                return;
            }
            sql = database.sqlInsert("schema_column", "table_name", "column_name", "type", "primary_ix");
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                long index = 1;
                for (CreateTable.Column column : operation.columns) {
                    statement.setString(1, operation.name);
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

            Table table = new Table(operation.name);
            tables.put(table.name, table);
            for (CreateTable.Column column : operation.columns) {
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

    private void deleteTable(DeleteTable operation, boolean updateSchema) {
        System.out.println("Deleting table '" + operation.name + "'.");
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
                statement.setString(1, operation.name);
                statement.executeUpdate();
            }

            tables.remove(operation.name);

        } catch (Throwable ex) {
            throw new RuntimeException("Failed SQL:\n" + sql, ex);
        }
    }

    private void createColumn(CreateColumn operation, boolean updateSchema) {
        System.out.println("Creating column '" + operation.table + "." + operation.name + "'.");
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
                statement.setString(2, operation.name);
                statement.setString(3, operation.type);
                statement.setNull(4, database.getNullType("integer"));

                statement.execute();
            }

            Table table = tables.get(operation.table);
            table.columns.put(operation.name, new Column(operation.name, operation.table, false));
        } catch (Throwable ex) {
            throw new RuntimeException("Failed SQL:\n" + sql, ex);
        }
    }

    private void deleteColumn(DeleteColumn operation, boolean updateSchema) {
        System.out.println("Deleting column '" + operation.table + "." + operation.name + "'.");
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
                statement.setString(2, operation.name);
                statement.executeUpdate();
            }

            Table table = tables.get(operation.table);
            if (null != table) {
                table.columns.remove(operation.name);
            }

        } catch (Throwable ex) {
            throw new RuntimeException("Failed SQL:\n" + sql, ex);
        }
    }
}
