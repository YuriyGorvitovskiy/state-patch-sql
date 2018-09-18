package org.state.patch.sql.zzz.patch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.state.patch.sql.zzz.patch.OpColumnCreate;
import org.state.patch.sql.zzz.patch.OpColumnDelete;
import org.state.patch.sql.zzz.patch.OpRecordChange;
import org.state.patch.sql.zzz.patch.OpRecordDelete;
import org.state.patch.sql.zzz.patch.OpRecordInsert;
import org.state.patch.sql.zzz.patch.OpTableCreate;
import org.state.patch.sql.zzz.patch.OpTableDelete;
import org.state.patch.sql.zzz.patch.Patch;

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
        assertEquals(9, resource.ops.size());

        OpTableCreate opTableCreate = (OpTableCreate) resource.ops.get(0);
        assertEquals("test_table_a", opTableCreate.table);
        assertEquals(6, opTableCreate.columns.size());

        OpTableCreate.Column column = opTableCreate.columns.get(0);
        assertEquals("id", column.name);
        assertEquals("integer", column.type);
        assertEquals(Boolean.TRUE, column.primary);

        column = opTableCreate.columns.get(1);
        assertEquals("num", column.name);
        assertEquals("floating", column.type);
        assertEquals(Boolean.FALSE, column.primary);

        column = opTableCreate.columns.get(2);
        assertEquals("bool", column.name);
        assertEquals("boolean", column.type);
        assertEquals(Boolean.FALSE, column.primary);

        column = opTableCreate.columns.get(3);
        assertEquals("name", column.name);
        assertEquals("name", column.type);
        assertEquals(Boolean.FALSE, column.primary);

        column = opTableCreate.columns.get(4);
        assertEquals("txt_extra", column.name);
        assertEquals("text", column.type);
        assertEquals(Boolean.FALSE, column.primary);

        column = opTableCreate.columns.get(5);
        assertEquals("stamp", column.name);
        assertEquals("datetime", column.type);
        assertEquals(Boolean.FALSE, column.primary);

        opTableCreate = (OpTableCreate) resource.ops.get(1);
        assertEquals("test_table_b", opTableCreate.table);
        assertEquals(2, opTableCreate.columns.size());

        column = opTableCreate.columns.get(0);
        assertEquals("id", column.name);
        assertEquals("integer", column.type);
        assertEquals(Boolean.TRUE, column.primary);

        column = opTableCreate.columns.get(1);
        assertEquals("num", column.name);
        assertEquals("floating", column.type);
        assertEquals(Boolean.FALSE, column.primary);

        OpColumnCreate createColumn = (OpColumnCreate) resource.ops.get(2);
        assertEquals("test_table_a", createColumn.table);
        assertEquals("txt", createColumn.column);
        assertEquals("text", createColumn.type);

        OpColumnDelete deleteColumn = (OpColumnDelete) resource.ops.get(3);
        assertEquals("test_table_a", deleteColumn.table);
        assertEquals("txt_extra", deleteColumn.column);

        OpTableDelete deleteTable = (OpTableDelete) resource.ops.get(4);
        assertEquals("test_table_b", deleteTable.table);

        OpRecordInsert insertRecord = (OpRecordInsert) resource.ops.get(5);
        assertEquals("test_table_a:1", insertRecord.id);
        assertEquals(Double.valueOf(12.3), insertRecord.attrs.get("num"));
        assertEquals(Boolean.TRUE, insertRecord.attrs.get("bool"));
        assertEquals("Tom", insertRecord.attrs.get("name"));
        assertEquals("Hello, world!", insertRecord.attrs.get("txt"));
        assertEquals("2018-09-02T17:15:07.123Z", insertRecord.attrs.get("stamp"));
        assertEquals(5, insertRecord.attrs.size());

        insertRecord = (OpRecordInsert) resource.ops.get(6);
        assertEquals("test_table_a:2", insertRecord.id);
        assertEquals(Double.valueOf(23.4), insertRecord.attrs.get("num"));
        assertEquals(Boolean.FALSE, insertRecord.attrs.get("bool"));
        assertEquals("John", insertRecord.attrs.get("name"));
        assertEquals("Hello, space!", insertRecord.attrs.get("txt"));
        assertEquals("2018-09-02T17:16:43.321Z", insertRecord.attrs.get("stamp"));
        assertEquals("Should be skipped", insertRecord.attrs.get("extra"));
        assertEquals(6, insertRecord.attrs.size());

        OpRecordChange changeRecord = (OpRecordChange) resource.ops.get(7);
        assertEquals("test_table_a:1", changeRecord.id);
        assertEquals("Hello, Universe!", changeRecord.attrs.get("txt"));
        assertEquals("Should be skipped", changeRecord.attrs.get("extra"));
        assertEquals(2, changeRecord.attrs.size());

        OpRecordDelete deleteRecord = (OpRecordDelete) resource.ops.get(8);
        assertEquals("test_table_a:2", deleteRecord.id);
    }
}
