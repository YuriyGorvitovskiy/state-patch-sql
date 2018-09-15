package org.state.patch.sql.model.db;

import org.state.patch.sql.config.DatabaseConfig;
import org.state.patch.sql.model.Model;

public interface Database extends DatabaseModel, DatabaseSave, DatabaseLoad {

    public static Database create(Model model, DatabaseConfig config) {
        throw new RuntimeException("Unsupported Database engine: " + config.engine);
    }
}
