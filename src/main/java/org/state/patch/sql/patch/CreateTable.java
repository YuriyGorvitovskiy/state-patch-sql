package org.state.patch.sql.patch;

import java.util.ArrayList;
import java.util.List;

public class CreateTable extends Operation {

    public static class Column {
        public String  name;
        public String  type;
        public boolean primary = false;
    }

    public String       name;
    public List<Column> columns = new ArrayList<>();
}
