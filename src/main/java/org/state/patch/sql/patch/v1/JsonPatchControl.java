package org.state.patch.sql.patch.v1;

import java.util.ArrayList;
import java.util.List;

public class JsonPatchControl extends JsonPatch {
    public List<JsonControlOp> ops = new ArrayList<>();
}
