package org.state.patch.sql.db;

import org.state.patch.sql.config.DatabaseConfig;
import org.state.patch.sql.db.postgres.Postgres;
import org.state.patch.sql.model.Model;

public interface Database extends DatabaseModel, DatabaseSave, DatabaseLoad {

    public static Database create(Model model, DatabaseConfig config) {
        if (Postgres.ENGINE.equals(config.engine)) {
            return new Postgres(model, config);
        }
        throw new RuntimeException("Unsupported Database engine: " + config.engine);
    }
}
