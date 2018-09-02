package org.state.patch.sql.processor;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.function.Consumer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.state.patch.sql.database.Database;
import org.state.patch.sql.patch.Column;
import org.state.patch.sql.patch.CreateColumn;
import org.state.patch.sql.patch.CreateTable;
import org.state.patch.sql.patch.DeleteColumn;
import org.state.patch.sql.patch.DeleteTable;
import org.state.patch.sql.patch.Operation;
import org.state.patch.sql.patch.Patch;

import com.fasterxml.jackson.databind.ObjectMapper;

public class PatchProcessor implements Consumer<Patch> {
    public static final String CURRENT_VERSION = "0.0.1";

    final Database database;

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

            sql = database.sqlInsert("schema_table", "id");
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, operation.name);
                statement.executeUpdate();
            }
            sql = database.sqlInsert("schema_column", "table_id", "name", "type", "primary_ix");
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                long index = 1;
                for (Column column : operation.columns) {
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

            sql = database.sqlDelete("schema_column", "table_id");
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, operation.name);
                statement.executeUpdate();
            }

            sql = database.sqlDelete("schema_table", "id");
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, operation.name);
                statement.executeUpdate();
            }

        } catch (Throwable ex) {
            throw new RuntimeException("Failed SQL:\n" + sql, ex);
        }
    }

    private void createColumn(CreateColumn operation, boolean updateSchema) {
        System.out.println("Creating column '" + operation.table_id + "." + operation.name + "'.");
        String sql = null;
        try (Connection connection = database.datasource.getConnection()) {
            sql = database.sqlCreateColumn(operation);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.execute();
            }

            if (!updateSchema) {
                return;
            }

            sql = database.sqlInsert("schema_column", "table_id", "name", "type", "primary_ix");
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, operation.table_id);
                statement.setString(2, operation.name);
                statement.setString(3, operation.type);
                statement.setNull(4, database.getNullType("integer"));

                statement.execute();
            }
        } catch (Throwable ex) {
            throw new RuntimeException("Failed SQL:\n" + sql, ex);
        }
    }

    private void deleteColumn(DeleteColumn operation, boolean updateSchema) {
        System.out.println("Deleting column '" + operation.table_id + "." + operation.name + "'.");
        String sql = null;
        try (Connection connection = database.datasource.getConnection()) {
            sql = database.sqlDeleteColumn(operation);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.execute();
            }

            if (!updateSchema) {
                return;
            }

            sql = database.sqlDelete("schema_column", "table_id", "name");
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, operation.table_id);
                statement.setString(2, operation.name);
                statement.executeUpdate();
            }
        } catch (Throwable ex) {
            throw new RuntimeException("Failed SQL:\n" + sql, ex);
        }
    }
}
