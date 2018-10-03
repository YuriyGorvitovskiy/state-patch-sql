package org.state.patch.sql.zzz.processor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.state.patch.sql.config.Configurator;
import org.state.patch.sql.config.ServiceConfig;
import org.state.patch.sql.zzz.database.Database;
import org.state.patch.sql.zzz.patch.Patch;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Single Scenario to cover use cases; <br/>
 * <br/>
 * 1.1. Create 'Test_Config' Schema; <br/>
 * 1.2. Create 'Test' Schema; <br/>
 * <br/>
 * 2.1. Create Test Schema; <br/>
 * 2.2. Create Version Table; <br/>
 * 2.3. Populate Version Table; <br/>
 * 2.4. Create Schema Table; <br/>
 * <br/>
 * 3.1. Create Table x2; <br/>
 * 3.2. Create Column x1; <br/>
 * 3.3. Delete Column x1; <br/>
 * 3.4. Delete Table x1; <br/>
 * <br/>
 * 4.1. Insert Row x2; <br/>
 * 4.2. Update Row x1; <br/>
 * 4.3. Delete Row x1; <br/>
 * <br/>
 * 5.1. Drop 'Test' Schema; <br/>
 * 5.2. Drop 'Test_Config' Schema; <br/>
 */
@Ignore
public class PatchProcessor_Test {

    Database       database;
    PatchProcessor processor;

    @Before
    public void setUp() throws Exception {
        /*
        String url = System.getenv("TEST_POSTGRES_URL");
        String username = System.getenv("TEST_POSTGRES_USERNAME");
        String password = System.getenv("TEST_POSTGRES_PASSWORD");
        if (null == url || null == username || null == password) {
            throw new Exception(
                "The following environment variables has to be set: TEST_POSTGRES_URL, TEST_POSTGRES_USERNAME, TEST_POSTGRES_PASSWORD.");
        }
        */
        ServiceConfig config = Configurator.extract(System.getProperties(), "org.state.patch.sql", new ServiceConfig());
        config.entity.database.engine = "POSTGRES";
        config.entity.database.driver = "org.postgresql.Driver";

        System.out.println("URL: " + config.entity.database.url);
        System.out.println("Username: " + config.entity.database.username);
        System.out.println("Username: " + config.entity.database.password);

        database = Database.createDatabase(config.entity.database);

        processor = new PatchProcessor(database);
        processor.prepareDatabase();
    }

    @After
    public void tearDown() throws Exception {
        processor.accept(readPatch("cleanup-patch.json"));
    }

    @Test
    public void postgresIntegrationTest() throws Exception {
        // Step 1 Execute
        processor.accept(readPatch("schema-patch.json"));

        // Step 1 Verify
        try (Connection conn = database.datasource.getConnection()) {
            try (PreparedStatement pr = conn.prepareStatement(database.sqlCheckTableExists())) {
                pr.setString(1, "pptest_alpha");
                try (ResultSet rs = pr.executeQuery()) {
                    assertTrue(rs.next());
                    assertTrue(rs.getBoolean(1));
                    assertFalse(rs.next());
                }
                pr.setString(1, "pptest_beta");
                try (ResultSet rs = pr.executeQuery()) {
                    assertTrue(rs.next());
                    assertFalse(rs.getBoolean(1));
                    assertFalse(rs.next());
                }
            }
        }

        // Step 2 Execute
        processor.accept(readPatch("records-patch.json"));

        // Step 2 Verify
        try (Connection conn = database.datasource.getConnection()) {
            String sql = database.sqlSelect("num", "bool", "name", "txt", "stamp").from("pptest_alpha").whereMatch("id")
                .toSql();
            try (PreparedStatement pr = conn.prepareStatement(sql)) {
                pr.setLong(1, 1);
                try (ResultSet rs = pr.executeQuery()) {
                    assertTrue(rs.next());
                    assertEquals(12.3, rs.getDouble(1), 0.00001);
                    assertEquals(true, rs.getBoolean(2));
                    assertEquals("Tom", rs.getString(3));
                    assertEquals("Hello, Universe!", rs.getString(4));
                    assertEquals(1535908507123L, rs.getTimestamp(5).getTime());
                }
                pr.setLong(1, 2);
                try (ResultSet rs = pr.executeQuery()) {
                    assertFalse(rs.next());
                }
            }
        }
    }

    Patch readPatch(String resourceName) throws Exception {
        try (InputStream in = PatchProcessor_Test.class.getResourceAsStream(resourceName)) {
            byte[] bytes = IOUtils.toByteArray(in);
            return new ObjectMapper().readValue(bytes, Patch.class);
        }
    }
}
