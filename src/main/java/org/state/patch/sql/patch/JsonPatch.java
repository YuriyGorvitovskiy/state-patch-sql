package org.state.patch.sql.patch;

import org.state.patch.sql.patch.v1.JsonPatchControl;
import org.state.patch.sql.patch.v1.JsonPatchData;
import org.state.patch.sql.patch.v1.JsonPatchModel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(property = "@patch_type", use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
                @Type(value = JsonPatchData.class, name = "data.v1"),
                @Type(value = JsonPatchModel.class, name = "model.v1"),
                @Type(value = JsonPatchControl.class, name = "control.v1"),
})
public class JsonPatch {
    /**
     * Should be set by consumer
     */
    @JsonIgnore
    public long patch_id;
}
