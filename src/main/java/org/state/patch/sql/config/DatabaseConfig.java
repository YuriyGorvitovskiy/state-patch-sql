package org.state.patch.sql.config;

import org.state.patch.sql.zzz.database.Postgres;

public class DatabaseConfig {
    public String engine   = Postgres.NAME;
    public String driver   = "org.postgresql.Driver";
    public String url      = "jdbc:postgresql://localhost:5432/state_patch_sql";
    public String username = "state_patch_sql";
    public String password = "secret";
}
