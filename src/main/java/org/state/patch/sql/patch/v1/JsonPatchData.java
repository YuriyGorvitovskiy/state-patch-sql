package org.state.patch.sql.patch.v1;

import java.util.ArrayList;
import java.util.List;

public class JsonPatchData extends JsonPatch {
    public List<JsonDataOp> ops = new ArrayList<>();
}
