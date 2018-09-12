package org.state.patch.sql.model;

import java.util.Collections;
import java.util.List;

public class Identity {

    public static class Part {

        public final String        columnName;
        public final PrimitiveType primitive;

        public Part(String columnName, PrimitiveType primitive) {
            this.columnName = columnName;
            this.primitive = primitive;
        }
    }

    public final List<Part> parts;

    public Identity(List<Identity.Part> parts) {
        this.parts = Collections.unmodifiableList(parts);
    }
}
