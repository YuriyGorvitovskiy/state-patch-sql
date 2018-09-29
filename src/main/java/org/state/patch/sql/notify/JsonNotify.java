package org.state.patch.sql.notify;

import org.state.patch.sql.message.JsonMessage;
import org.state.patch.sql.notify.v1.JsonNotify_v1;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(property = "@notify_type", use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
                @Type(value = JsonNotify_v1.class, name = "patch.processed.v1"),
})
public class JsonNotify extends JsonMessage {
}
