package org.state.patch.sql.notify;

import org.state.patch.sql.notify.v1.JsonNotify_v1;
import org.state.patch.sql.translator.JsonTranslator;

import com.fasterxml.jackson.databind.util.StdDateFormat;

public class JsonNotifyTranslator implements JsonTranslator<Notify, JsonNotify> {

    static final StdDateFormat DATE_FORMAT = new StdDateFormat();

    @Override
    public Class<Notify> getEntityClass() {
        return Notify.class;
    }

    @Override
    public Class<JsonNotify> getJsonClass() {
        return JsonNotify.class;
    }

    @Override
    public JsonNotify toJson(Notify notify) {
        JsonNotify_v1 json = new JsonNotify_v1();
        json.processed_by = notify.processedBy;
        json.processed_at = DATE_FORMAT.format(notify.processedAt);
        json.event_id = notify.eventId;
        json.patch_id = notify.patchId;

        return json;
    }

    @Override
    public Notify fromJson(JsonNotify json) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

}
