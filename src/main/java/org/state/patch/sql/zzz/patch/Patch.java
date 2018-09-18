package org.state.patch.sql.zzz.patch;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Patch {

    @JsonIgnore
    public long patch_id;

    public List<Operation> ops = new ArrayList<>();

}
