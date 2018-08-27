package org.state.patch.sql.config;

public class DatabaseConfig {
    public String engine   = "POSTGRES";
    public String driver   = "org.postgresql.Driver";
    public String url      = "jdbc:postgresql://localhost:5432/state_patch_sql";
    public String username = "state_patch_sql";
    public String password = "secret";
}
