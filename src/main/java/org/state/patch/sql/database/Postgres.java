package org.state.patch.sql.database;

import org.apache.commons.lang3.StringUtils;
import org.state.patch.sql.config.DatabaseConfig;
import org.state.patch.sql.patch.Column;
import org.state.patch.sql.patch.CreateTable;
import org.state.patch.sql.patch.DeleteTable;

public class Postgres extends Database {
    public static class PostgresQueryBuilder implements SelectBuilder {
        StringBuilder mainSql = new StringBuilder();

        public PostgresQueryBuilder(String... columns) {
            mainSql.append("SELECT ");
            mainSql.append(StringUtils.joinWith(", ", (Object[]) columns));
            mainSql.append("\n");
        }

        @Override
        public SelectBuilder from(String table) {
            mainSql.append("    FROM ");
            mainSql.append(table);
            mainSql.append("\n");

            return this;
        }

        @Override
        public SelectBuilder whereMatch(String... columns) {
            mainSql.append("    WHERE ");
            mainSql.append(StringUtils.joinWith(" = ? AND ", (Object[]) columns));
            mainSql.append(" = ?\n");

            return this;
        }

        @Override
        public String toSql() {
            mainSql.append(";");

            return mainSql.toString();
        }

    }

    public static final String NAME = "POSTGRES";

    public Postgres(DatabaseConfig config) {
        super(config);
    }

    @Override
    public String sqlCreateTable(CreateTable table) {
        StringBuilder mainSql = new StringBuilder();
        StringBuilder primaryKeySql = new StringBuilder();
        mainSql.append("CREATE TABLE ");
        mainSql.append(table.name);
        mainSql.append(" (\n");
        String tableSQLSeparator = "    ";
        String primarySQLSeparator = "";
        for (Column column : table.columns) {
            mainSql.append(tableSQLSeparator);
            mainSql.append(column.name);
            mainSql.append("  ");
            mainSql.append(toSQLType(column.type));
            tableSQLSeparator = ",\n    ";

            if (column.primary) {
                primaryKeySql.append(primarySQLSeparator);
                primaryKeySql.append(column.name);
                primarySQLSeparator = ", ";
            }
        }
        mainSql.append(tableSQLSeparator);
        mainSql.append("PRIMARY KEY (");
        mainSql.append(primaryKeySql);
        mainSql.append(")\n");
        mainSql.append(");");

        return mainSql.toString();
    }

    @Override
    public String sqlDeleteTable(DeleteTable operation) {
        StringBuilder mainSql = new StringBuilder();
        mainSql.append("DROP TABLE IF EXISTS ");
        mainSql.append(operation.name);
        mainSql.append(" CASCADE;");

        return mainSql.toString();
    }

    public String toSQLType(String type) {
        switch (type) {
            case "boolean":
                return "boolean";
            case "datetime":
                return "timestamp with time zone";
            case "floating":
                return "double precision";
            case "integer":
                return "bigint";
            case "text":
                return "text";
            case "name":
                return "character varying (256)";
            default:
                throw new RuntimeException("Unknown datatype: " + type);
        }
    }

    @Override
    public String sqlCheckTableExists() {
        StringBuilder mainSql = new StringBuilder();
        mainSql.append("SELECT EXISTS (\n");
        mainSql.append("   SELECT 1\n");
        mainSql.append("       FROM  pg_tables\n");
        mainSql.append("       WHERE   schemaname = 'public'\n");
        mainSql.append("           AND tablename = ?\n");
        mainSql.append(");");

        return mainSql.toString();
    }

    @Override
    public SelectBuilder sqlSelect(String... columns) {
        return new PostgresQueryBuilder(columns);
    }

    @Override
    public String sqlInsert(String table, String... columns) {
        StringBuilder mainSql = new StringBuilder();
        mainSql.append("INSERT INTO ");
        mainSql.append(table);
        mainSql.append(" (");
        mainSql.append(StringUtils.joinWith(", ", (Object[]) columns));
        mainSql.append(")\n");
        mainSql.append("    VALUES (");
        mainSql.append(StringUtils.repeat("?", ", ", columns.length));
        mainSql.append(")\n");
        mainSql.append(";");

        return mainSql.toString();
    }

    @Override
    public String sqlDelete(String table, String... columns) {
        StringBuilder mainSql = new StringBuilder();
        mainSql.append("DELETE FROM ");
        mainSql.append(table);
        mainSql.append("\n");
        mainSql.append("    WHERE ");
        mainSql.append(StringUtils.joinWith(" = ? AND ", (Object[]) columns));
        mainSql.append(" = ?\n");
        mainSql.append(";");

        return mainSql.toString();
    }

}
