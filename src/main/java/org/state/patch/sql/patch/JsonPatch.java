package org.state.patch.sql.patch;

import org.state.patch.sql.message.JsonMessage;
import org.state.patch.sql.patch.v1.JsonPatchControl_v1;
import org.state.patch.sql.patch.v1.JsonPatchData_v1;
import org.state.patch.sql.patch.v1.JsonPatchModel_v1;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(property = "@patch_type", use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
                @Type(value = JsonPatchData_v1.class, name = "data.v1"),
                @Type(value = JsonPatchModel_v1.class, name = "model.v1"),
                @Type(value = JsonPatchControl_v1.class, name = "control.v1"),
})
public class JsonPatch extends JsonMessage {
}
