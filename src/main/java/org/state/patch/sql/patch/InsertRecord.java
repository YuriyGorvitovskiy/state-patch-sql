package org.state.patch.sql.patch;

import java.util.HashMap;
import java.util.Map;

public class InsertRecord extends Operation {
    public String              id;
    public Map<String, Object> attributes = new HashMap<>();
}
