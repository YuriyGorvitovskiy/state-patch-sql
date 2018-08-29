package org.state.patch.sql.database;

import org.apache.commons.dbcp2.BasicDataSource;
import org.state.patch.sql.config.DatabaseConfig;
import org.state.patch.sql.patch.Table;

public abstract class Database {

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

    public abstract String sqlCreateTable(Table table);
}
