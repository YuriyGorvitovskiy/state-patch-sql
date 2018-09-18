package org.state.patch.sql.model.notify;

import java.util.Date;

public class Notify {
    public final String processedBy;
    public final Date   processedAt;
    public final long   eventId;
    public final long   patchId;

    public Notify(String processedBy, Date processedAt, long eventId, long patchId) {
        this.processedBy = processedBy;
        this.processedAt = processedAt;
        this.eventId = eventId;
        this.patchId = patchId;
    }
}
