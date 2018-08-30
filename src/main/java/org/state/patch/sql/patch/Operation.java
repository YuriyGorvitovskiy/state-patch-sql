package org.state.patch.sql.patch;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(property = "@type", use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @Type(value = CreateTable.class, name = "create-table"),
        @Type(value = DeleteTable.class, name = "delete-table"),
})
public abstract class Operation {
}
