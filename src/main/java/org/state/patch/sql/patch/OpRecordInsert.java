package org.state.patch.sql.patch;

import java.util.HashMap;
import java.util.Map;

public class OpRecordInsert extends Operation {
    public String              id;
    public Map<String, Object> attrs = new HashMap<>();
}
