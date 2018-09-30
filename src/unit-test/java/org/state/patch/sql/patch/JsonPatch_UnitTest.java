package org.state.patch.sql.patch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.state.patch.sql.Asserts;
import org.state.patch.sql.patch.v1.JsonControlOp;
import org.state.patch.sql.patch.v1.JsonControlOpBackup;
import org.state.patch.sql.patch.v1.JsonControlOpPing;
import org.state.patch.sql.patch.v1.JsonControlOpSuspend;
import org.state.patch.sql.patch.v1.JsonDataOp;
import org.state.patch.sql.patch.v1.JsonDataOpDelete;
import org.state.patch.sql.patch.v1.JsonDataOpInsert;
import org.state.patch.sql.patch.v1.JsonDataOpUpdate;
import org.state.patch.sql.patch.v1.JsonModelAttribute;
import org.state.patch.sql.patch.v1.JsonModelOp;
import org.state.patch.sql.patch.v1.JsonModelOpAppendAttr;
import org.state.patch.sql.patch.v1.JsonModelOpCreateType;
import org.state.patch.sql.patch.v1.JsonModelOpDeleteAttr;
import org.state.patch.sql.patch.v1.JsonModelOpDeleteType;
import org.state.patch.sql.patch.v1.JsonPatchControl_v1;
import org.state.patch.sql.patch.v1.JsonPatchData_v1;
import org.state.patch.sql.patch.v1.JsonPatchModel_v1;
import org.state.patch.sql.util.ResourceString;

import com.fasterxml.jackson.databind.ObjectMapper;

@FixMethodOrder(MethodSorters.JVM)
public class JsonPatch_UnitTest {
    final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void read_patch_data_v1() throws Exception {
        // Setup
        ResourceString resource = new ResourceString(JsonPatch_UnitTest.class, "JsonPatch_UnitTest.data.v1.json");

        // Execute
        JsonPatch patch = mapper.readValue(resource.toString(), JsonPatch.class);

        //Validate
        assertNotNull(patch);
        JsonPatchData_v1 patch_v1 = (JsonPatchData_v1) patch;

        // Validate JsonMessage
        assertEquals(0L, patch_v1.message_id);

        // Validate JsonPatch_v1
        assertEquals(1L, patch_v1.event_id);
        assertEquals("user:2", patch_v1.event_by);
        assertEquals("2018-09-29T20:21:24.123Z", patch_v1.event_at);
        assertEquals("service1", patch_v1.target_ids.get(0));
        assertEquals("service2", patch_v1.target_ids.get(1));
        assertEquals(2, patch_v1.target_ids.size());

        // Validate JsonPatchData_v1
        assertDataOpInsert(patch_v1.ops.get(0));
        assertDataOpUpdate(patch_v1.ops.get(1));
        assertDataOpDelete(patch_v1.ops.get(2));
        assertEquals(3, patch_v1.ops.size());
    }

    @Test
    public void write_patch_data_v1() throws Exception {
        // Setup
        ResourceString resource = new ResourceString(JsonPatch_UnitTest.class, "JsonPatch_UnitTest.data.v1.json");

        // Execute & Validate
        Asserts.asserJsonRoundtrip(mapper, resource.toString(), JsonPatch.class);
    }

    @Test
    public void read_patch_model_v1() throws Exception {
        // Setup
        ResourceString resource = new ResourceString(JsonPatch_UnitTest.class, "JsonPatch_UnitTest.model.v1.json");

        // Execute
        JsonPatch patch = mapper.readValue(resource.toString(), JsonPatch.class);

        //Validate
        assertNotNull(patch);
        JsonPatchModel_v1 patch_v1 = (JsonPatchModel_v1) patch;

        // Validate JsonMessage
        assertEquals(0L, patch_v1.message_id);

        // Validate JsonPatch_v1
        assertEquals(2L, patch_v1.event_id);
        assertEquals("user:3", patch_v1.event_by);
        assertEquals("2018-09-30T06:31:57.123Z", patch_v1.event_at);
        assertEquals(0, patch_v1.target_ids.size());

        // Validate JsonPatchData_v1
        assertModelOpCreateType(patch_v1.ops.get(0));
        assertModelOpAppendAttr(patch_v1.ops.get(1));
        assertModelOpDeleteAttr(patch_v1.ops.get(2));
        assertModelOpDeleteType(patch_v1.ops.get(3));
        assertEquals(4, patch_v1.ops.size());
    }

    @Test
    public void write_patch_model_v1() throws Exception {
        // Setup
        ResourceString resource = new ResourceString(JsonPatch_UnitTest.class, "JsonPatch_UnitTest.model.v1.json");

        // Execute & Validate
        Asserts.asserJsonRoundtrip(mapper, resource.toString(), JsonPatch.class);
    }

    @Test
    public void read_patch_control_v1() throws Exception {
        // Setup
        ResourceString resource = new ResourceString(JsonPatch_UnitTest.class, "JsonPatch_UnitTest.control.v1.json");

        // Execute
        JsonPatch patch = mapper.readValue(resource.toString(), JsonPatch.class);

        //Validate
        assertNotNull(patch);
        JsonPatchControl_v1 patch_v1 = (JsonPatchControl_v1) patch;

        // Validate JsonMessage
        assertEquals(0L, patch_v1.message_id);

        // Validate JsonPatch_v1
        assertEquals(3L, patch_v1.event_id);
        assertEquals("user:4", patch_v1.event_by);
        assertEquals("2018-09-30T21:45:58.567Z", patch_v1.event_at);
        assertEquals("service3", patch_v1.target_ids.get(0));
        assertEquals(1, patch_v1.target_ids.size());

        // Validate JsonPatchData_v1
        assertControlOpSuspend(patch_v1.ops.get(0));
        assertControlOpBackup(patch_v1.ops.get(1));
        assertControlOpPing(patch_v1.ops.get(2));
        assertEquals(3, patch_v1.ops.size());
    }

    @Test
    public void write_patch_control_v1() throws Exception {
        // Setup
        ResourceString resource = new ResourceString(JsonPatch_UnitTest.class, "JsonPatch_UnitTest.control.v1.json");

        // Execute & Validate
        Asserts.asserJsonRoundtrip(mapper, resource.toString(), JsonPatch.class);
    }

    private void assertDataOp(JsonDataOp jsonDataOp) {
        assertNotNull(jsonDataOp);
        assertEquals("entity:52", jsonDataOp.entity_id);
    }

    private void assertDataOpInsert(JsonDataOp jsonDataOp) {
        // Verify JsonDataOp
        assertDataOp(jsonDataOp);

        // Verify JsonDataOpInsert
        JsonDataOpInsert op = (JsonDataOpInsert) jsonDataOp;
        assertNotNull(op.attrs);
        assertEquals(true, op.attrs.get("attr_boolean"));
        assertEquals(new Integer(12), op.attrs.get("attr_integer"));
        assertEquals(new Double(34.5), op.attrs.get("attr_double"));
        assertEquals("String Value", op.attrs.get("attr_string"));
        assertEquals("Text Value", op.attrs.get("attr_text"));
        assertEquals("2018-09-29T20:52:19.345Z", op.attrs.get("attr_time"));
        assertEquals("external:reference", op.attrs.get("attr_external_ref"));
        assertEquals("entity:23", op.attrs.get("attr_internal_ref"));
        assertEquals("Optional Value", op.attrs.get("attr_optional_a"));
        assertEquals(null, op.attrs.get("attr_optional_b"));
        assertEquals(10, op.attrs.size());
    }

    private void assertDataOpUpdate(JsonDataOp jsonDataOp) {
        // Verify JsonDataOp
        assertDataOp(jsonDataOp);

        // Verify JsonDataOpInsert
        JsonDataOpUpdate op = (JsonDataOpUpdate) jsonDataOp;
        assertNotNull(op.attrs);
        assertEquals(false, op.attrs.get("attr_boolean"));
        assertEquals(new Integer(67), op.attrs.get("attr_integer"));
        assertEquals(new Double(89.01), op.attrs.get("attr_double"));
        assertEquals("New String Value", op.attrs.get("attr_string"));
        assertEquals("New Text Value", op.attrs.get("attr_text"));
        assertEquals("2018-09-29T23:30:37.678Z", op.attrs.get("attr_time"));
        assertEquals("external:reference2", op.attrs.get("attr_external_ref"));
        assertEquals("entity:45", op.attrs.get("attr_internal_ref"));
        assertEquals(null, op.attrs.get("attr_optional-a"));
        assertEquals("Optional Value", op.attrs.get("attr_optional_b"));
        assertEquals(10, op.attrs.size());
    }

    private void assertDataOpDelete(JsonDataOp jsonDataOp) {
        // Verify JsonDataOp
        assertDataOp(jsonDataOp);

        // Verify JsonDataOpInsert
        JsonDataOpDelete op = (JsonDataOpDelete) jsonDataOp;
        assertNotNull(op);
    }

    private void assertModelOp(JsonModelOp jsonModelOp) {
        assertNotNull(jsonModelOp);
        assertEquals("entity", jsonModelOp.entity_type);
    }

    private void assertModelAttr(String id, String type, Object initial, JsonModelAttribute attr) {
        assertNotNull(attr);
        assertEquals(id, attr.name);
        assertEquals(type, attr.type);
        assertEquals(initial, attr.initial);
    }

    private void assertModelOpCreateType(JsonModelOp jsonModelOp) {
        // Verify JsonModelOp
        assertModelOp(jsonModelOp);

        // Verify JsonModelOpCreateType
        JsonModelOpCreateType op = (JsonModelOpCreateType) jsonModelOp;
        assertModelAttr("id", "integer", null, op.id);

        assertNotNull(op.attrs);
        assertModelAttr("attr_boolean", "boolean", true, op.attrs.get(0));
        assertModelAttr("attr_integer", "integer", 1, op.attrs.get(1));
        assertModelAttr("attr_double", "double", 2.34, op.attrs.get(2));
        assertModelAttr("attr_string", "string", "S", op.attrs.get(3));
        assertModelAttr("attr_text", "text", "", op.attrs.get(4));
        assertModelAttr("attr_time", "timestamp", "1970-01-01T00:00:00.000Z", op.attrs.get(5));
        assertModelAttr("attr_external_ref", "refext", null, op.attrs.get(6));
        assertModelAttr("attr_ref_integer", "ref-integer:entity", null, op.attrs.get(7));
        assertModelAttr("attr_ref_string", "ref-string:type2", null, op.attrs.get(8));
        assertEquals(9, op.attrs.size());
    }

    private void assertModelOpDeleteType(JsonModelOp jsonModelOp) {
        // Verify JsonModelOp
        assertModelOp(jsonModelOp);

        // Verify JsonModelOpDeleteType
        JsonModelOpDeleteType op = (JsonModelOpDeleteType) jsonModelOp;
        assertNotNull(op);
    }

    private void assertModelOpAppendAttr(JsonModelOp jsonModelOp) {
        // Verify JsonModelOp
        assertModelOp(jsonModelOp);

        // Verify JsonModelOpAppendAttr
        JsonModelOpAppendAttr op = (JsonModelOpAppendAttr) jsonModelOp;
        assertModelAttr("attr_optional", "string", null, op.attr);
    }

    private void assertModelOpDeleteAttr(JsonModelOp jsonModelOp) {
        // Verify JsonModelOp
        assertModelOp(jsonModelOp);

        // Verify JsonModelOpDeleteAttr
        JsonModelOpDeleteAttr op = (JsonModelOpDeleteAttr) jsonModelOp;
        assertEquals("attr_optional", op.attr_name);
    }

    private void assertControlOp(JsonControlOp jsonControlOp) {
        assertNotNull(jsonControlOp);
    }

    private void assertControlOpSuspend(JsonControlOp jsonControlOp) {
        // Verify jsonControlOp
        assertControlOp(jsonControlOp);

        // Verify JsonControlOpSuspend
        JsonControlOpSuspend op = (JsonControlOpSuspend) jsonControlOp;
        assertEquals(true, op.shutdown);
    }

    private void assertControlOpBackup(JsonControlOp jsonControlOp) {
        // Verify jsonControlOp
        assertControlOp(jsonControlOp);

        // Verify JsonControlOpBackup
        JsonControlOpBackup op = (JsonControlOpBackup) jsonControlOp;
        assertEquals(false, op.incremental);
        assertEquals("/etc/backup/2018-09-30.bkp", op.backup_file);
    }

    private void assertControlOpPing(JsonControlOp jsonControlOp) {
        // Verify jsonControlOp
        assertControlOp(jsonControlOp);

        // Verify JsonControlOpPing
        JsonControlOpPing op = (JsonControlOpPing) jsonControlOp;
        assertNotNull(op);
    }

}
