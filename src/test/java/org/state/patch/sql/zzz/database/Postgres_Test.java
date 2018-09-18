package org.state.patch.sql.zzz.database;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.BiFunction;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.state.patch.sql.config.DatabaseConfig;
import org.state.patch.sql.zzz.database.Postgres;
import org.state.patch.sql.zzz.patch.OpTableCreate;
import org.state.patch.sql.zzz.patch.OpTableDelete;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Postgres_Test {

    @Test
    public void test_CreateTable() throws Exception {
        test_Generator("create-table", OpTableCreate.class, (p, r) -> p.sqlCreateTable(r));
    }

    @Test
    public void test_DeleteTable() throws Exception {
        test_Generator("delete-table", OpTableDelete.class, (p, r) -> p.sqlDeleteTable(r));
    }

    @Test
    public void test_CheckTableExists() throws Exception {
        // Setup
        Postgres postgres = new Postgres(new DatabaseConfig());

        // Execute
        String actualSql = postgres.sqlCheckTableExists();

        // Verify
        assertSql("check-table-exists.sql", actualSql);
    }

    @Test
    public void test_Select() throws Exception {
        // Setup
        Postgres postgres = new Postgres(new DatabaseConfig());

        // Execute
        String actualSql = postgres
            .sqlSelect("col_a", "col_b", "col_c")
            .from("tbl")
            .whereMatch("id", "name")
            .toSql();

        // Verify
        assertSql("select.sql", actualSql);
    }

    @Test
    public void test_Insert() throws Exception {
        // Setup
        Postgres postgres = new Postgres(new DatabaseConfig());

        // Execute
        String actualSql = postgres.sqlInsert("tbl", "col_a", "col_b", "col_c");

        // Verify
        assertSql("insert.sql", actualSql);
    }

    @Test
    public void test_Delete() throws Exception {
        // Setup
        Postgres postgres = new Postgres(new DatabaseConfig());

        // Execute
        String actualSql = postgres.sqlDelete("tbl", "id");

        // Verify
        assertSql("delete.sql", actualSql);
    }

    private <T> void test_Generator(
            String testName,
            Class<T> clazz,
            BiFunction<Postgres, T, String> processor)
            throws Exception {
        // Setup
        T resource = null;
        try (InputStream in = Postgres_Test.class.getResourceAsStream(testName + ".json")) {
            byte[] bytes = IOUtils.toByteArray(in);
            resource = new ObjectMapper().readValue(bytes, clazz);
        }
        Postgres postgres = new Postgres(new DatabaseConfig());

        // Execute
        String actualSql = processor.apply(postgres, resource);

        // Validate
        assertSql(testName + ".sql", actualSql);
    }

    private void assertSql(String expectedResource, String actualSql) throws Exception {
        String expectedSql = null;
        try (InputStream in = Postgres_Test.class.getResourceAsStream(expectedResource)) {
            expectedSql = IOUtils.toString(in, StandardCharsets.UTF_8);
        }
        assertEquals(expectedSql, actualSql);
    }

}
