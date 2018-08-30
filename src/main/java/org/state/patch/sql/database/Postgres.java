package org.state.patch.sql.database;

import org.state.patch.sql.config.DatabaseConfig;
import org.state.patch.sql.patch.Column;
import org.state.patch.sql.patch.CreateTable;
import org.state.patch.sql.patch.DeleteTable;

public class Postgres extends Database {

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

}
