package org.state.patch.sql.patch.v1;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.state.patch.sql.util.Json;

import com.fasterxml.jackson.annotation.JsonFormat;

public abstract class JsonPatch_v1 extends org.state.patch.sql.patch.JsonPatch {
    public long   event_id;
    public String event_by;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Json.DATE_PATTERN, timezone = Json.DATE_TIMEZONE)
    public Date         event_at;
    public List<String> target_ids = new ArrayList<>();
}
