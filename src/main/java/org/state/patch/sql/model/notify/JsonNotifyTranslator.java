package org.state.patch.sql.model.notify;

import org.state.patch.sql.model.notify.v1.JsonNotify_v1;

import com.fasterxml.jackson.databind.util.StdDateFormat;

public class JsonNotifyTranslator {

    static final StdDateFormat DATE_FORMAT = new StdDateFormat();

    public JsonNotify toJson(Notify notify) {
        JsonNotify_v1 json = new JsonNotify_v1();
        json.processed_by = notify.processedBy;
        json.processed_at = DATE_FORMAT.format(notify.processedAt);
        json.event_id = notify.eventId;
        json.patch_id = notify.patchId;

        return json;
    }

}
