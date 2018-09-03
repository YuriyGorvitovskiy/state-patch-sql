package org.state.patch.sql.patch;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(property = "@type", use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @Type(value = CreateTable.class, name = "create-table"),
        @Type(value = DeleteTable.class, name = "delete-table"),
        @Type(value = CreateColumn.class, name = "create-column"),
        @Type(value = DeleteColumn.class, name = "delete-column"),
        @Type(value = InsertRecord.class, name = "insert"),
        @Type(value = ChangeRecord.class, name = "change"),
        @Type(value = DeleteRecord.class, name = "delete"),
})
public abstract class Operation {
}
