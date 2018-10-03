package org.state.patch.sql.patch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;
import org.state.patch.sql.control.op.ControlOp;
import org.state.patch.sql.control.op.ControlOpBackup;
import org.state.patch.sql.control.op.ControlOpPing;
import org.state.patch.sql.control.op.ControlOpSuspend;
import org.state.patch.sql.data.ReferenceExternal;
import org.state.patch.sql.data.ReferenceInteger;
import org.state.patch.sql.data.ReferenceString;
import org.state.patch.sql.data.op.DataOp;
import org.state.patch.sql.data.op.DataOpDelete;
import org.state.patch.sql.data.op.DataOpInsert;
import org.state.patch.sql.data.op.DataOpUpdate;
import org.state.patch.sql.model.Model;
import org.state.patch.sql.model.PrimitiveType;
import org.state.patch.sql.model.ReferenceType;
import org.state.patch.sql.model.ValueType;
import org.state.patch.sql.model.op.ModelOp;
import org.state.patch.sql.model.op.ModelOpAppendAttribute;
import org.state.patch.sql.model.op.ModelOpCreateType;
import org.state.patch.sql.model.op.ModelOpDeleteAttribute;
import org.state.patch.sql.model.op.ModelOpDeleteType;
import org.state.patch.sql.patch.v1.JsonControlOp;
import org.state.patch.sql.patch.v1.JsonDataOp;
import org.state.patch.sql.patch.v1.JsonModelOp;
import org.state.patch.sql.test.Asserts;
import org.state.patch.sql.util.Json;
import org.state.patch.sql.util.ResourceString;

import com.fasterxml.jackson.databind.ObjectMapper;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JsonPatchTranslator_UnitTest {

    final ObjectMapper        mapper  = new ObjectMapper();
    final Model               model   = new Model();
    final JsonPatchTranslator subject = new JsonPatchTranslator(model);

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setupModel() throws Exception {
        PatchModel patchModel = (PatchModel) extractPatch("JsonPatchTranslator_UnitTest.setup.model.json");
        model.apply(patchModel);
    }

    @Test
    public void description() {
        // Execute & Validate
        assertEquals(Patch.class, subject.getEntityClass());
        assertEquals(JsonPatch.class, subject.getJsonClass());
    }

    @Test
    public void from_patch_data_v1() throws Exception {
        // Execute
        Patch patch = extractPatch("JsonPatchTranslator_UnitTest.data.v1.json");

        //Validate
        assertNotNull(patch);
        PatchData patchData = (PatchData) patch;

        // Validate PatchData
        assertEquals(0L, patchData.modifiedPatchId);
        assertEquals(1L, patchData.modifiedEventId);
        assertEquals(Json.DATE_PARSE.parse("2018-09-29T20:21:24.123Z"), patchData.modifiedAt);
        assertEquals(new ReferenceExternal("user:2"), patchData.modifiedBy);

        // Validate PatchDataOps
        assertDataOpInsert(patchData.operations.get(0));
        assertDataOpUpdate(patchData.operations.get(1));
        assertDataOpDelete(patchData.operations.get(2));
        assertEquals(3, patchData.operations.size());
    }

    @Test
    public void to_patch_data() throws Exception {
        // Execute & Validate
        assertRoundTrip("JsonPatchTranslator_UnitTest.data.v1.json");
    }

    @Test
    public void unknown_patch_data() throws Exception {
        // Execute & Validate
        assertRoundTrip("JsonPatchTranslator_UnitTest.unknown.data.v1.json");
    }

    @Test
    public void from_patch_model_v1() throws Exception {
        // Execute
        Patch patch = extractPatch("JsonPatchTranslator_UnitTest.model.v1.json");

        //Validate
        assertNotNull(patch);
        PatchModel patchModel = (PatchModel) patch;

        // Validate PatchModel
        assertEquals(0L, patchModel.modifiedPatchId);
        assertEquals(2L, patchModel.modifiedEventId);
        assertEquals(Json.DATE_PARSE.parse("2018-09-30T06:31:57.123Z"), patchModel.modifiedAt);
        assertEquals(new ReferenceExternal("user:3"), patchModel.modifiedBy);

        // Validate JsonPatchData_v1
        assertModelOpCreateType(patchModel.operations.get(0));
        assertModelOpAppendAttr(patchModel.operations.get(1));
        assertModelOpDeleteAttr(patchModel.operations.get(2));
        assertModelOpDeleteType(patchModel.operations.get(3));
        assertEquals(4, patchModel.operations.size());
    }

    @Test
    public void to_patch_model() throws Exception {
        // Execute & Validate
        assertRoundTrip("JsonPatchTranslator_UnitTest.model.v1.json");
    }

    @Test
    public void from_patch_control_v1() throws Exception {
        // Execute
        Patch patch = extractPatch("JsonPatchTranslator_UnitTest.control.v1.json");

        //Validate
        assertNotNull(patch);
        PatchControl patchControl = (PatchControl) patch;

        // Validate PatchControl
        assertEquals(0L, patchControl.modifiedPatchId);
        assertEquals(3L, patchControl.modifiedEventId);
        assertEquals(Json.DATE_PARSE.parse("2018-09-30T21:45:58.567Z"), patchControl.modifiedAt);
        assertEquals(new ReferenceExternal("user:4"), patchControl.modifiedBy);

        // Validate JsonPatchData_v1
        assertControlOpSuspend(patchControl.operations.get(0));
        assertControlOpBackup(patchControl.operations.get(1));
        assertControlOpPing(patchControl.operations.get(2));
        assertEquals(3, patchControl.operations.size());
    }

    @Test
    public void to_patch_control() throws Exception {
        // Execute & Validate
        assertRoundTrip("JsonPatchTranslator_UnitTest.control.v1.json");
    }

    @Test
    public void from_json_null() throws Exception {
        // Setup
        exception.expect(RuntimeException.class);
        exception.expectMessage("Unknown patch: null");

        // Execute
        subject.fromJson((JsonPatch) null);
    }

    @Test
    public void to_json_null() throws Exception {
        // Setup
        exception.expect(RuntimeException.class);
        exception.expectMessage("Unknown patch: null");

        // Execute
        subject.toJson((Patch) null);
    }

    @Test
    public void dataOp_from_json_null() throws Exception {
        // Setup
        exception.expect(RuntimeException.class);
        exception.expectMessage("Unknown data operation: null");

        // Execute
        subject.fromJson((JsonDataOp) null);
    }

    @Test
    public void dataOp_to_json_null() throws Exception {
        // Setup
        exception.expect(RuntimeException.class);
        exception.expectMessage("Unknown data operation: null");

        // Execute
        subject.toJson((DataOp) null);
    }

    @Test
    public void modelOp_from_json_null() throws Exception {
        // Setup
        exception.expect(RuntimeException.class);
        exception.expectMessage("Unknown model operation: null");

        // Execute
        subject.fromJson((JsonModelOp) null);
    }

    @Test
    public void modelOp_to_json_null() throws Exception {
        // Setup
        exception.expect(RuntimeException.class);
        exception.expectMessage("Unknown model operation: null");

        // Execute
        subject.toJson((ModelOp) null);
    }

    @Test
    public void controlOp_from_json_null() throws Exception {
        // Setup
        exception.expect(RuntimeException.class);
        exception.expectMessage("Unknown control operation: null");

        // Execute
        subject.fromJson((JsonControlOp) null);
    }

    @Test
    public void controlOp_to_json_null() throws Exception {
        // Setup
        exception.expect(RuntimeException.class);
        exception.expectMessage("Unknown control operation: null");

        // Execute
        subject.toJson((ControlOp) null);
    }

    @Test
    public void valueTypeFromJson_unknown() throws Exception {
        // Setup
        String unknown = "unknown";

        exception.expect(RuntimeException.class);
        exception.expectMessage("Unknown value type: unknown");

        // Execute
        subject.valueTypeFromJson(unknown);
    }

    @Test
    public void valueTypeToJson_unknown() throws Exception {
        // Setup
        ValueType unknown = new ValueType() {
        };

        exception.expect(RuntimeException.class);
        exception.expectMessage("Unknown value type: ");

        // Execute
        subject.valueTypeToJson(unknown);
    }

    @Test
    public void valueTypeToJson_unknown_reference() throws Exception {
        // Setup
        ReferenceType unknown = new ReferenceType("entity", PrimitiveType.DOUBLE);

        exception.expect(RuntimeException.class);
        exception.expectMessage("Unknown value type: ");

        // Execute
        subject.valueTypeToJson(unknown);
    }

    @Test
    public void value_unknown_from_json() throws Exception {
        // Setup
        ValueType unknown = new ValueType() {
        };

        exception.expect(RuntimeException.class);
        exception.expectMessage("Unknown value type: ");

        // Execute
        subject.fromJson(unknown, "value");
    }

    @Test
    public void value_unknown_to_json() throws Exception {
        // Setup
        ValueType unknown = new ValueType() {
        };

        exception.expect(RuntimeException.class);
        exception.expectMessage("Unknown value type: ");

        // Execute
        subject.toJson(unknown, "value");
    }

    @Test
    public void booleanFromJson() throws Exception {
        // Execute && Validate
        assertEquals(Boolean.FALSE, subject.booleanFromJson(Boolean.FALSE));
        assertEquals(Boolean.FALSE, subject.booleanFromJson(0.0));
        assertEquals(Boolean.FALSE, subject.booleanFromJson("anything"));

        assertEquals(Boolean.TRUE, subject.booleanFromJson(Boolean.TRUE));
        assertEquals(Boolean.TRUE, subject.booleanFromJson(0.1));
        assertEquals(Boolean.TRUE, subject.booleanFromJson("true"));
    }

    @Test
    public void integerFromJson() throws Exception {
        // Execute && Validate
        assertEquals(Long.valueOf(123L), subject.integerFromJson(123));
        assertEquals(Long.valueOf(4L), subject.integerFromJson(4.56));
        assertEquals(Long.valueOf(7890123456789L), subject.integerFromJson("7890123456789"));
    }

    @Test
    public void doubleFromJson() throws Exception {
        // Execute && Validate
        assertEquals(Double.valueOf(123), subject.doubleFromJson(123));
        assertEquals(Double.valueOf(4.56), subject.doubleFromJson(4.56));
        assertEquals(Double.valueOf(7890123456789L), subject.doubleFromJson("7890123456789"));
    }

    @Test
    public void dateFromJson() throws Exception {
        // Execute && Validate
        assertEquals(Json.parseDate("2018-10-01T19:23:16.123Z"), subject.dateFromJson("2018-10-01T19:23:16.123Z"));
        assertEquals(Json.parseDate("2018-10-01T16:23:16.123Z"), subject.dateFromJson("2018-10-01T19:23:16.123+0300"));
        assertEquals(Json.parseDate("1970-01-01T00:02:03.456Z"), subject.dateFromJson(123456L));
    }

    private Patch extractPatch(String resourceName) throws Exception {
        ResourceString resource = new ResourceString(JsonPatchTranslator_UnitTest.class, resourceName);
        JsonPatch patch = mapper.readValue(resource.toString(), JsonPatch.class);
        return subject.fromJson(patch);
    }

    private void assertRoundTrip(String resourceName) throws Exception {
        ResourceString resource = new ResourceString(JsonPatchTranslator_UnitTest.class, resourceName);
        Asserts.asserJsonTranslatorRoundtrip(subject, mapper, resource.toString());
    }

    private void assertDataOp(DataOp dataOp) {
        assertNotNull(dataOp);
        assertEquals("entity:52", dataOp.id.toString());
    }

    private void assertDataOpInsert(DataOp dataOp) throws Exception {
        // Verify DataOp
        assertDataOp(dataOp);

        // Verify DataOpInsert
        DataOpInsert op = (DataOpInsert) dataOp;
        assertNotNull(op.attrs);
        assertEquals(true, op.attrs.get("attr_boolean"));
        assertEquals(new Long(12), op.attrs.get("attr_integer"));
        assertEquals(new Double(34.5), op.attrs.get("attr_double"));
        assertEquals("String Value", op.attrs.get("attr_string"));
        assertEquals("Text Value", op.attrs.get("attr_text"));
        assertEquals(Json.DATE_PARSE.parse("2018-09-29T20:52:19.345Z"), op.attrs.get("attr_time"));
        assertEquals(new ReferenceExternal("external:reference"), op.attrs.get("attr_external_ref"));
        assertEquals(new ReferenceInteger("entity", 23L), op.attrs.get("attr_ref_integer"));
        assertEquals(new ReferenceString("type2", "001-234-23"), op.attrs.get("attr_ref_string"));
        assertEquals("Optional Value", op.attrs.get("attr_optional_a"));
        assertEquals(null, op.attrs.get("attr_optional_b"));
        assertEquals(11, op.attrs.size());
    }

    private void assertDataOpUpdate(DataOp dataOp) throws Exception {
        // Verify DataOp
        assertDataOp(dataOp);

        // Verify DataOpUpdate
        DataOpUpdate op = (DataOpUpdate) dataOp;
        assertNotNull(op.attrs);
        assertEquals(false, op.attrs.get("attr_boolean"));
        assertEquals(new Long(67), op.attrs.get("attr_integer"));
        assertEquals(new Double(89.01), op.attrs.get("attr_double"));
        assertEquals("New String Value", op.attrs.get("attr_string"));
        assertEquals("New Text Value", op.attrs.get("attr_text"));
        assertEquals(Json.DATE_PARSE.parse("2018-09-29T23:30:37.678Z"), op.attrs.get("attr_time"));
        assertEquals(new ReferenceExternal("external:reference2"), op.attrs.get("attr_external_ref"));
        assertEquals(new ReferenceInteger("entity", 45L), op.attrs.get("attr_ref_integer"));
        assertEquals(new ReferenceString("type2", "001-234-56"), op.attrs.get("attr_ref_string"));
        assertEquals(null, op.attrs.get("attr_optional-a"));
        assertEquals("Optional Value", op.attrs.get("attr_optional_b"));
        assertEquals(11, op.attrs.size());
    }

    private void assertDataOpDelete(DataOp dataOp) {
        // Verify DataOp
        assertDataOp(dataOp);

        // Verify JsonDataOpInsert
        DataOpDelete op = (DataOpDelete) dataOp;
        assertNotNull(op);
    }

    private void assertModelOp(ModelOp modelOp) {
        assertNotNull(modelOp);
        assertEquals("entity", modelOp.type);
    }

    private void assertModelAttr(String id, ValueType type, Object initial, ModelOp.Attribute attr) {
        assertNotNull(attr);
        assertEquals(id, attr.name);
        assertEquals(type, attr.type);
        assertEquals(initial, attr.initial);
    }

    private void assertModelOpCreateType(ModelOp modelOp) throws Exception {
        // Verify ModelOp
        assertModelOp(modelOp);

        // Verify ModelOpCreateType
        ModelOpCreateType op = (ModelOpCreateType) modelOp;
        assertModelAttr("id", new ReferenceType("entity", PrimitiveType.INTEGER), null, op.identity);

        assertNotNull(op.attrs);
        assertModelAttr("attr_boolean", PrimitiveType.BOOLEAN, true, op.attrs.get(0));
        assertModelAttr("attr_integer", PrimitiveType.INTEGER, 1L, op.attrs.get(1));
        assertModelAttr("attr_double", PrimitiveType.DOUBLE, 2.34, op.attrs.get(2));
        assertModelAttr("attr_string", PrimitiveType.STRING, "S", op.attrs.get(3));
        assertModelAttr("attr_text", PrimitiveType.TEXT, "", op.attrs.get(4));
        assertModelAttr("attr_time", PrimitiveType.TIMESTAMP, Json.DATE_PARSE.parse("1970-01-01T00:00:00.000Z"),
                        op.attrs.get(5));
        assertModelAttr("attr_external_ref", PrimitiveType.REFERENCE_EXTERNAL, null, op.attrs.get(6));
        assertModelAttr("attr_ref_integer", new ReferenceType("entity", PrimitiveType.INTEGER), null, op.attrs.get(7));
        assertModelAttr("attr_ref_string", new ReferenceType("type2", PrimitiveType.STRING), null, op.attrs.get(8));
        assertEquals(9, op.attrs.size());
    }

    private void assertModelOpDeleteType(ModelOp modelOp) {
        // Verify ModelOp
        assertModelOp(modelOp);

        // Verify ModelOpDeleteType
        ModelOpDeleteType op = (ModelOpDeleteType) modelOp;
        assertNotNull(op);
    }

    private void assertModelOpAppendAttr(ModelOp modelOp) {
        // Verify ModelOp
        assertModelOp(modelOp);

        // Verify ModelOpAppendAttribute
        ModelOpAppendAttribute op = (ModelOpAppendAttribute) modelOp;
        assertModelAttr("attr_optional", PrimitiveType.STRING, null, op.attr);
    }

    private void assertModelOpDeleteAttr(ModelOp modelOp) {
        // Verify ModelOp
        assertModelOp(modelOp);

        // Verify ModelOpDeleteAttribute
        ModelOpDeleteAttribute op = (ModelOpDeleteAttribute) modelOp;
        assertEquals("attr_optional", op.attribName);
    }

    private void assertControlOp(ControlOp controlOp) {
        assertNotNull(controlOp);
    }

    private void assertControlOpSuspend(ControlOp controlOp) {
        // Verify ControlOp
        assertControlOp(controlOp);

        // Verify JsonControlOpSuspend
        ControlOpSuspend op = (ControlOpSuspend) controlOp;
        assertEquals(true, op.shutdown);
    }

    private void assertControlOpBackup(ControlOp controlOp) {
        // Verify ControlOp
        assertControlOp(controlOp);

        // Verify JsonControlOpBackup
        ControlOpBackup op = (ControlOpBackup) controlOp;
        assertEquals(false, op.incremental);
        assertEquals("/etc/backup/2018-09-30.bkp", op.backupFile);
    }

    private void assertControlOpPing(ControlOp controlOp) {
        // Verify ControlOp
        assertControlOp(controlOp);

        // Verify JsonControlOpPing
        ControlOpPing op = (ControlOpPing) controlOp;
        assertNotNull(op);
    }

}
