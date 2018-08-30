package org.state.patch.sql.database;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.BiFunction;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.state.patch.sql.config.DatabaseConfig;
import org.state.patch.sql.patch.CreateTable;
import org.state.patch.sql.patch.DeleteTable;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Postgres_Test {

    @Test
    public void test_CreateTable() throws Exception {
        test_Generator("create-table", CreateTable.class, (p, r) -> p.sqlCreateTable(r));
    }

    @Test
    public void test_DeleteTable() throws Exception {
        test_Generator("delete-table", DeleteTable.class, (p, r) -> p.sqlDeleteTable(r));
    }

    private <T> void test_Generator(String testName, Class<T> clazz, BiFunction<Postgres, T, String> processor)
            throws Exception {
        // Setup
        T resource = null;
        try (InputStream in = Postgres_Test.class.getResourceAsStream(testName + ".json")) {
            byte[] bytes = IOUtils.toByteArray(in);
            resource = new ObjectMapper().readValue(bytes, clazz);
        }
        String expectedSql = null;
        try (InputStream in = Postgres_Test.class.getResourceAsStream(testName + ".sql")) {
            expectedSql = IOUtils.toString(in, StandardCharsets.UTF_8);
        }
        Postgres postgres = new Postgres(new DatabaseConfig());

        // Execute
        String actualSql = processor.apply(postgres, resource);

        // Validate
        assertEquals(expectedSql, actualSql);
    }
}
