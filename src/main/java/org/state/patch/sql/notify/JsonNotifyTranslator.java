package org.state.patch.sql.notify;

import org.service.common.translator.JsonTranslator;
import org.state.patch.sql.notify.v1.JsonNotify_v1;

public class JsonNotifyTranslator implements JsonTranslator<Notify, JsonNotify> {

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
        json.processed_at = notify.processedAt;
        json.event_id = notify.eventId;
        json.patch_id = notify.patchId;

        return json;
    }

    @Override
    public Notify fromJson(JsonNotify json) throws Exception {
        if (json instanceof JsonNotify_v1) {
            return fromJson((JsonNotify_v1) json);
        }
        throw new Exception("Unknown notify: " + json);
    }

    public Notify fromJson(JsonNotify_v1 json) {
        return new Notify(json.processed_by,
                          json.processed_at,
                          json.event_id,
                          json.patch_id);
    }

}
