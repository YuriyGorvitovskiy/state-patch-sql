package org.state.patch.sql.model;

import java.util.Objects;

public abstract class ReferenceAbstract {

    public final String stringValue;

    protected ReferenceAbstract(String stringValue) {
        this.stringValue = stringValue;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ReferenceAbstract))
            return false;

        ReferenceAbstract other = (ReferenceAbstract) o;
        return Objects.equals(this.stringValue, other.stringValue);
    }

    @Override
    public String toString() {
        return stringValue;
    }
}
