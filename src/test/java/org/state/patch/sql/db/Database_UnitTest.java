package org.state.patch.sql.db;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.state.patch.sql.config.DatabaseConfig;
import org.state.patch.sql.db.postgres.Postgres;
import org.state.patch.sql.model.Model;

public class Database_UnitTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void create_postgres() {
        // Setup
        Model model = new Model();
        DatabaseConfig config = new DatabaseConfig();
        config.engine = Postgres.ENGINE;

        //Execute
        Database subject = Database.create(model, config);

        // Validate
        assertNotNull(subject);
        Postgres postgres = (Postgres) subject;
        assertSame(config, postgres.config);
        assertSame(model, postgres.model);
        assertNotNull(postgres.datasource);
    }

    @Test
    public void create_unknown() {
        // Setup
        Model model = new Model();
        DatabaseConfig config = new DatabaseConfig();
        config.engine = "unknown";

        exception.expect(RuntimeException.class);
        exception.expectMessage("Unsupported Database engine: unknown");

        //Execute
        Database.create(model, config);
    }
}
