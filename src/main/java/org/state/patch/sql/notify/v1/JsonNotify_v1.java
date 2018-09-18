package org.state.patch.sql.notify.v1;

import org.state.patch.sql.notify.JsonNotify;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JsonNotify_v1 extends JsonNotify {

    @JsonProperty("@notify_type")
    public String notify_type = "patch.processed.v1";

    public String processed_by;

    public String processed_at;

    public long event_id;

    public long patch_id;
}
