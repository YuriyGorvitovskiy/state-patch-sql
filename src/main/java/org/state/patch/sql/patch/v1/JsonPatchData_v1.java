package org.state.patch.sql.patch.v1;

import java.util.ArrayList;
import java.util.List;

public class JsonPatchData_v1 extends JsonPatch {
    public List<JsonDataOp> ops = new ArrayList<>();
}
