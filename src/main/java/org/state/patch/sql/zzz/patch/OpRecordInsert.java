package org.state.patch.sql.zzz.patch;

import java.util.HashMap;
import java.util.Map;

public class OpRecordInsert extends Operation {
    public String              id;
    public Map<String, Object> attrs = new HashMap<>();
}
