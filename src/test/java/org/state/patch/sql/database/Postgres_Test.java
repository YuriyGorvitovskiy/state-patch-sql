package org.state.patch.sql.database;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.state.patch.sql.config.DatabaseConfig;
import org.state.patch.sql.patch.Table;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Postgres_Test {

    @Test
    public void test_CreateTable() throws Exception {
        // Setup
        Table resource = null;
        try (InputStream in = Postgres_Test.class.getResourceAsStream("create-table.json")) {
            byte[] bytes = IOUtils.toByteArray(in);
            resource = new ObjectMapper().readValue(bytes, Table.class);
        }
        String expectedSql = null;
        try (InputStream in = Postgres_Test.class.getResourceAsStream("create-table.sql")) {
            expectedSql = IOUtils.toString(in, StandardCharsets.UTF_8);
        }
        Postgres postgres = new Postgres(new DatabaseConfig());

        // Execute
        String actualSql = postgres.sqlCreateTable(resource);

        // Validate
        assertEquals(expectedSql, actualSql);
    }

}
