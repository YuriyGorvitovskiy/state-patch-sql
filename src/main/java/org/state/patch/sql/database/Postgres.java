package org.state.patch.sql.database;

import org.state.patch.sql.config.DatabaseConfig;
import org.state.patch.sql.patch.Column;
import org.state.patch.sql.patch.Table;

public class Postgres extends Database {

    public static final String NAME = "POSTGRES";

    public Postgres(DatabaseConfig config) {
        super(config);
    }

    @Override
    public String sqlCreateTable(Table table) {
        StringBuilder tableSQL = new StringBuilder();
        StringBuilder primarySQL = new StringBuilder();
        tableSQL.append("CREATE TABLE ");
        tableSQL.append(table.name);
        tableSQL.append(" (\n");
        String tableSQLSeparator = "    ";
        String primarySQLSeparator = "";
        for (Column column : table.columns) {
            tableSQL.append(tableSQLSeparator);
            tableSQL.append(column.name);
            tableSQL.append("  ");
            tableSQL.append(toSQLType(column.type));
            tableSQLSeparator = ",\n    ";

            if (column.primary) {
                primarySQL.append(primarySQLSeparator);
                primarySQL.append(column.name);
                primarySQLSeparator = ", ";
            }
        }
        tableSQL.append(tableSQLSeparator);
        tableSQL.append("PRIMARY KEY (");
        tableSQL.append(primarySQL);
        tableSQL.append(")\n");
        tableSQL.append(");");
        return tableSQL.toString();
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
