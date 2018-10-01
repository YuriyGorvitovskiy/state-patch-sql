package org.state.patch.sql.data;

import java.util.Objects;

public abstract class Reference {
    public static final String SEPARATOR = ":";

    public final String stringValue;

    protected Reference(String stringValue) {
        this.stringValue = stringValue;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Reference))
            return false;

        Reference other = (Reference) o;
        return Objects.equals(this.stringValue, other.stringValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stringValue);
    }

    @Override
    public String toString() {
        return stringValue;
    }
}
