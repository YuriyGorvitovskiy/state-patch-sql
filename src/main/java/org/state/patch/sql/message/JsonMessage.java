package org.state.patch.sql.message;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Abstract class for JSON describing message body.
 *
 * @author Yuriy Gorvitovskiy
 */
public abstract class JsonMessage {
    /**
     * id should uniquely identify the message in the Topic.<br/>
     * In case of:
     * <ul>
     * <li>Kafka it is a message offset</li>
     * </ul>
     * Id is not a part of the message body and should be set by the:
     * <ul>
     * <li>producer after submitting the message to the topic,</li>
     * <li>consumer after reading the message from the topic.</li>
     * </ul>
     */
    @JsonIgnore
    public long message_id;

}
