package org.state.patch.sql.patch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Patch_Test {

    @Test
    public void test_loadPatch() throws Exception {
        // Execute
        Patch resource = null;
        try (InputStream in = Patch_Test.class.getResourceAsStream("test-patch.json")) {
            byte[] bytes = IOUtils.toByteArray(in);
            resource = new ObjectMapper().readValue(bytes, Patch.class);
        }

        // Validate
        assertNotNull(resource);
        assertEquals(2, resource.operations.size());

        CreateTable createTable = (CreateTable) resource.operations.get(0);
        assertEquals("test_table_a", createTable.name);
        assertEquals(6, createTable.columns.size());

        Column column = createTable.columns.get(0);
        assertEquals("id", column.name);
        assertEquals("integer", column.type);
        assertEquals(Boolean.TRUE, column.primary);

        column = createTable.columns.get(1);
        assertEquals("num", column.name);
        assertEquals("floating", column.type);
        assertEquals(Boolean.FALSE, column.primary);

        column = createTable.columns.get(2);
        assertEquals("bool", column.name);
        assertEquals("boolean", column.type);
        assertEquals(Boolean.FALSE, column.primary);

        column = createTable.columns.get(3);
        assertEquals("name", column.name);
        assertEquals("name", column.type);
        assertEquals(Boolean.FALSE, column.primary);

        column = createTable.columns.get(4);
        assertEquals("txt", column.name);
        assertEquals("text", column.type);
        assertEquals(Boolean.FALSE, column.primary);

        column = createTable.columns.get(5);
        assertEquals("stamp", column.name);
        assertEquals("datetime", column.type);
        assertEquals(Boolean.FALSE, column.primary);

        DeleteTable deleteTable = (DeleteTable) resource.operations.get(1);
        assertEquals("test_table_a", deleteTable.name);
    }
}
