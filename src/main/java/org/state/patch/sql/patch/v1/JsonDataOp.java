package org.state.patch.sql.patch.v1;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(property = "@op_type", use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
                @Type(value = JsonDataOpInsert.class, name = "insert"),
                @Type(value = JsonDataOpUpdate.class, name = "update"),
                @Type(value = JsonDataOpDelete.class, name = "delete"),
})
public class JsonDataOp {
    public String entity_id;
}
