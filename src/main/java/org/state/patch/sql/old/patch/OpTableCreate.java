package org.state.patch.sql.old.patch;

import java.util.ArrayList;
import java.util.List;

public class OpTableCreate extends Operation {

    public static class Column {
        public String  name;
        public String  type;
        public boolean primary = false;
    }

    public String       table;
    public List<Column> columns = new ArrayList<>();
}
