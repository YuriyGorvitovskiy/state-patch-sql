package org.state.patch.sql;

import java.util.Date;

import org.service.common.message.MessageConsumer;
import org.service.common.message.MessageProducer;
import org.state.patch.sql.config.ServiceConfig;
import org.state.patch.sql.db.Database;
import org.state.patch.sql.model.Model;
import org.state.patch.sql.model.ModelPersistency;
import org.state.patch.sql.notify.JsonNotify;
import org.state.patch.sql.notify.JsonNotifyTranslator;
import org.state.patch.sql.notify.Notify;
import org.state.patch.sql.patch.JsonPatch;
import org.state.patch.sql.patch.JsonPatchTranslator;
import org.state.patch.sql.patch.Patch;
import org.state.patch.sql.patch.PatchControl;
import org.state.patch.sql.patch.PatchData;
import org.state.patch.sql.patch.PatchModel;

public class PatchProcessor {

    ServiceConfig                       config;
    MessageConsumer<Patch, JsonPatch>   patchConsumer;
    MessageProducer<Notify, JsonNotify> notifyProducer;
    Model                               entityModel;
    ModelPersistency                    modelPersistency;
    Database                            entityDatabase;
    JsonPatchTranslator                 patchTranslator;
    JsonNotifyTranslator                notifyTranslator;

    public PatchProcessor(ServiceConfig config) {
        this.config = config;
        this.patchTranslator = new JsonPatchTranslator(entityModel);
        this.notifyTranslator = new JsonNotifyTranslator();
        this.patchConsumer = MessageConsumer.create(config.patch, patchTranslator);
        this.notifyProducer = MessageProducer.create(config.notify, notifyTranslator);
        this.entityModel = new Model();
        this.modelPersistency = new ModelPersistency(config.model);
        this.entityDatabase = Database.create(entityModel, config.entity.database);
    }

    public void run() {
        try {
            modelPersistency.initialize();
            modelPersistency.load(entityModel);
            patchConsumer.run((p) -> process(p));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void process(Patch patch) {
        try {
            if (patch instanceof PatchData) {
                process((PatchData) patch);
            } else if (patch instanceof PatchModel) {
                process((PatchModel) patch);
            } else if (patch instanceof PatchControl) {
                process((PatchControl) patch);
            }
            notify(patch);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    void process(PatchData patch) throws Exception {
        entityDatabase.apply(patch);
    }

    void process(PatchModel patch) throws Exception {
        entityModel.apply(patch);
        entityDatabase.apply(patch);
        modelPersistency.apply(patch);
    }

    void process(PatchControl patch) throws Exception {
        // TODO:
    }

    void notify(Patch patch) throws Exception {
        Notify notify = new Notify(config.name, new Date(), patch.modifiedEventId, patch.modifiedPatchId);
        notifyProducer.post(notify);
    }
}
