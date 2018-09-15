package org.state.patch.sql.zzz.database;

import java.sql.PreparedStatement;
import java.util.List;

import org.apache.commons.dbcp2.BasicDataSource;
import org.state.patch.sql.config.DatabaseConfig;
import org.state.patch.sql.zzz.patch.OpColumnCreate;
import org.state.patch.sql.zzz.patch.OpColumnDelete;
import org.state.patch.sql.zzz.patch.OpTableCreate;
import org.state.patch.sql.zzz.patch.OpTableDelete;

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
         * contribute to SQL the following
         *
         * ORDER BY columns[0] ASC, ..., columns[n] ASC
         */
        public SelectBuilder orderBy(String... columns);

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

    public abstract String sqlCreateTable(OpTableCreate table);

    public abstract String sqlDeleteTable(OpTableDelete operation);

    public abstract String sqlCreateColumn(OpColumnCreate operation);

    public abstract String sqlDeleteColumn(OpColumnDelete operation);

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

    public abstract void setJSON(PreparedStatement statement, int i, String type, Object object) throws Exception;

    public abstract String sqlUpdate(String table, List<String> updateColumns, List<String> primaryColumns);

}
