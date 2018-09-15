package org.state.patch.sql.zzz.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Table {
    public String                    name;
    public final List<Column>        primary = new ArrayList<>();
    public final Map<String, Column> columns = new HashMap<>();

    public Table(String name) {
        this.name = name;
    }

}
