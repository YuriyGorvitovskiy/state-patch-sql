package org.state.patch.sql.patch.v1;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(property = "@op_type", use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
                @Type(value = JsonControlOpSuspend.class, name = "suspend"),
                @Type(value = JsonControlOpBackup.class, name = "backup"),
                @Type(value = JsonControlOpPing.class, name = "ping"),
})
public class JsonControlOp {
}
