package org.state.patch.sql.patch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.state.patch.sql.Asserts;
import org.state.patch.sql.patch.v1.JsonDataOp;
import org.state.patch.sql.patch.v1.JsonDataOpDelete;
import org.state.patch.sql.patch.v1.JsonDataOpInsert;
import org.state.patch.sql.patch.v1.JsonDataOpUpdate;
import org.state.patch.sql.patch.v1.JsonPatchData_v1;
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
        fail("Not yet implemented");
    }

    @Test
    public void write_patch_model_v1() throws Exception {
        fail("Not yet implemented");
    }

    @Test
    public void read_patch_control_v1() throws Exception {
        fail("Not yet implemented");
    }

    @Test
    public void write_patch_control_v1() throws Exception {
        fail("Not yet implemented");
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
}
