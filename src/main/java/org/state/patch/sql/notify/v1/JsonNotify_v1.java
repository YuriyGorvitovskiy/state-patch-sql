package org.state.patch.sql.notify.v1;

import java.util.Date;

import org.service.common.util.Json;
import org.state.patch.sql.notify.JsonNotify;

import com.fasterxml.jackson.annotation.JsonFormat;

public class JsonNotify_v1 extends JsonNotify {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Json.DATE_PATTERN, timezone = Json.DATE_TIMEZONE)
    public Date   processed_at;
    public String processed_by;
    public long   event_id;
    public long   patch_id;
}
