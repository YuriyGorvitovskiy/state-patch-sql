package org.state.patch.sql.database;

import org.apache.commons.dbcp2.BasicDataSource;
import org.state.patch.sql.config.DatabaseConfig;
import org.state.patch.sql.patch.CreateColumn;
import org.state.patch.sql.patch.CreateTable;
import org.state.patch.sql.patch.DeleteColumn;
import org.state.patch.sql.patch.DeleteTable;

public abstract class Database {

    public static interface SelectBuilder {

        /**
         * contribute to SQL the following
         *
         * FROM table
         */
        public SelectBuilder from(String table);

        /**
         * contribute to SQL the following
         *
         * WHERE columns[0] = ? AND ... AND columns[n] = ?
         */
        public SelectBuilder whereMatch(String... columns);

        /**
         *
         * @return complete Query statement SQL
         */
        public String toSql();
    }

    public final BasicDataSource datasource;

    public static Database createDatabase(DatabaseConfig config) {
        if (Postgres.NAME.equals(config.engine)) {
            return new Postgres(config);
        }
        throw new RuntimeException("Unkown patch topic engine: " + config.engine);
    }

    public Database(DatabaseConfig config) {
        this.datasource = new BasicDataSource();
        this.datasource.setDriverClassName(config.driver);
        this.datasource.setUrl(config.url);
        this.datasource.setUsername(config.username);
        this.datasource.setPassword(config.password);
    }

    public abstract String sqlCreateTable(CreateTable table);

    public abstract String sqlDeleteTable(DeleteTable operation);

    public abstract String sqlCreateColumn(CreateColumn operation);

    public abstract String sqlDeleteColumn(DeleteColumn operation);

    /**
     * @return prepared SQL statement with 1 parameter – table name.
     */
    public abstract String sqlCheckTableExists();

    public abstract SelectBuilder sqlSelect(String... columns);

    /**
     * @return prepared SQL statement with #columns parameters.
     */
    public abstract String sqlInsert(String table, String... columns);

    /**
     * @return prepared SQL statement with #columns parameters.
     */
    public abstract String sqlDelete(String table, String... columns);

    public abstract int getNullType(String type);

}
