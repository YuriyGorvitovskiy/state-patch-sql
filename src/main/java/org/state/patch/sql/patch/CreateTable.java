package org.state.patch.sql.patch;

import java.util.ArrayList;
import java.util.List;

public class CreateTable extends Operation {
    public String       name;
    public List<Column> columns = new ArrayList<>();
}
