package org.state.patch.sql.old.patch;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(property = "@type", use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @Type(value = OpTableCreate.class, name = "create-table"),
        @Type(value = OpTableDelete.class, name = "delete-table"),
        @Type(value = OpColumnCreate.class, name = "create-column"),
        @Type(value = OpColumnDelete.class, name = "delete-column"),
        @Type(value = OpRecordInsert.class, name = "insert"),
        @Type(value = OpRecordChange.class, name = "change"),
        @Type(value = OpRecordDelete.class, name = "delete"),
})
public abstract class Operation {
}
