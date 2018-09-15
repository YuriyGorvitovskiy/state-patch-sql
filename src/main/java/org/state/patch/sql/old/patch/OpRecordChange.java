package org.state.patch.sql.old.patch;

import java.util.HashMap;
import java.util.Map;

public class OpRecordChange extends Operation {
    public String              id;
    public Map<String, Object> attrs = new HashMap<>();
}
