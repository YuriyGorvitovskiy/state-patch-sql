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
        assertEquals(9, resource.operations.size());

        CreateTable createTable = (CreateTable) resource.operations.get(0);
        assertEquals("test_table_a", createTable.name);
        assertEquals(6, createTable.columns.size());

        CreateTable.Column column = createTable.columns.get(0);
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
        assertEquals("txt_extra", column.name);
        assertEquals("text", column.type);
        assertEquals(Boolean.FALSE, column.primary);

        column = createTable.columns.get(5);
        assertEquals("stamp", column.name);
        assertEquals("datetime", column.type);
        assertEquals(Boolean.FALSE, column.primary);

        createTable = (CreateTable) resource.operations.get(1);
        assertEquals("test_table_b", createTable.name);
        assertEquals(2, createTable.columns.size());

        column = createTable.columns.get(0);
        assertEquals("id", column.name);
        assertEquals("integer", column.type);
        assertEquals(Boolean.TRUE, column.primary);

        column = createTable.columns.get(1);
        assertEquals("num", column.name);
        assertEquals("floating", column.type);
        assertEquals(Boolean.FALSE, column.primary);

        CreateColumn createColumn = (CreateColumn) resource.operations.get(2);
        assertEquals("test_table_a", createColumn.table);
        assertEquals("txt", createColumn.name);
        assertEquals("text", createColumn.type);

        DeleteColumn deleteColumn = (DeleteColumn) resource.operations.get(3);
        assertEquals("test_table_a", deleteColumn.table);
        assertEquals("txt_extra", deleteColumn.name);

        DeleteTable deleteTable = (DeleteTable) resource.operations.get(4);
        assertEquals("test_table_b", deleteTable.name);

        InsertRecord insertRecord = (InsertRecord) resource.operations.get(5);
        assertEquals("test_table_a:1", insertRecord.id);
        assertEquals(Double.valueOf(12.3), insertRecord.attributes.get("num"));
        assertEquals(Boolean.TRUE, insertRecord.attributes.get("bool"));
        assertEquals("Tom", insertRecord.attributes.get("name"));
        assertEquals("Hello, world!", insertRecord.attributes.get("txt"));
        assertEquals("2018-09-02T17:15:07.123Z", insertRecord.attributes.get("stamp"));
        assertEquals(5, insertRecord.attributes.size());

        insertRecord = (InsertRecord) resource.operations.get(6);
        assertEquals("test_table_a:2", insertRecord.id);
        assertEquals(Double.valueOf(23.4), insertRecord.attributes.get("num"));
        assertEquals(Boolean.FALSE, insertRecord.attributes.get("bool"));
        assertEquals("John", insertRecord.attributes.get("name"));
        assertEquals("Hello, space!", insertRecord.attributes.get("txt"));
        assertEquals("2018-09-02T17:16:43.321Z", insertRecord.attributes.get("stamp"));
        assertEquals("Should be skipped", insertRecord.attributes.get("extra"));
        assertEquals(6, insertRecord.attributes.size());

        ChangeRecord changeRecord = (ChangeRecord) resource.operations.get(7);
        assertEquals("test_table_a:1", changeRecord.id);
        assertEquals("Hello, Universe!", changeRecord.attributes.get("txt"));
        assertEquals("Should be skipped", changeRecord.attributes.get("extra"));
        assertEquals(2, changeRecord.attributes.size());

        DeleteRecord deleteRecord = (DeleteRecord) resource.operations.get(8);
        assertEquals("test_table_a:2", deleteRecord.id);
    }
}
