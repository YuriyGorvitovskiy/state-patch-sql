package org.state.patch.sql.patch.v1;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(property = "@patch_type", use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
                @Type(value = JsonPatchData.class, name = "data"),
                @Type(value = JsonPatchModel.class, name = "model"),
                @Type(value = JsonPatchControl.class, name = "control"),
})
public class JsonPatch {
    public long         event_id;
    public String       event_by;
    public String       event_at;
    public List<String> target_ids = new ArrayList<>();
}
