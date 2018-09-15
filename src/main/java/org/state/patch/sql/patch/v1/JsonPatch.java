package org.state.patch.sql.patch.v1;

import java.util.ArrayList;
import java.util.List;

public abstract class JsonPatch extends org.state.patch.sql.patch.JsonPatch {
    public long         event_id;
    public String       event_by;
    public String       event_at;
    public List<String> target_ids = new ArrayList<>();
}
