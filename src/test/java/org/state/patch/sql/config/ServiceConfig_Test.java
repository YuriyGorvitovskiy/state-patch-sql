package org.state.patch.sql.config;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ServiceConfig_Test {
    @Test
    public void constructor() {
        // Execute
        ServiceConfig config = new ServiceConfig();

        // Validate
        assertEquals("state-patch-sql", config.name);
    }
}
