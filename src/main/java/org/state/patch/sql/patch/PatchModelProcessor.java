package org.state.patch.sql.patch;

import org.state.patch.sql.model.op.ModelOp;
import org.state.patch.sql.model.op.ModelOpAppendAttribute;
import org.state.patch.sql.model.op.ModelOpCreateType;
import org.state.patch.sql.model.op.ModelOpDeleteAttribute;
import org.state.patch.sql.model.op.ModelOpDeleteType;
import org.state.patch.sql.util.ResourceString;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface PatchModelProcessor {

    public default void loadFromResource(Class<?> resourceClass,
                                         String resourceName,
                                         JsonPatchTranslator jsonTranslator) throws Exception {
        ResourceString json = new ResourceString(resourceClass, resourceName);
        JsonPatch patch = new ObjectMapper().readValue(json.toString(), JsonPatch.class);
        PatchModel modelPatch = (PatchModel) jsonTranslator.fromJson(patch);
        apply(modelPatch);
    }

    public default void apply(PatchModel modelPatch) throws Exception {
        for (ModelOp operation : modelPatch.operations) {
            this.apply(operation);
        }
    }

    public default void apply(ModelOp operation) throws Exception {
        if (operation instanceof ModelOpCreateType) {
            createType((ModelOpCreateType) operation);
        } else if (operation instanceof ModelOpDeleteType) {
            deleteType((ModelOpDeleteType) operation);
        } else if (operation instanceof ModelOpAppendAttribute) {
            appendAttribute((ModelOpAppendAttribute) operation);
        } else if (operation instanceof ModelOpDeleteAttribute) {
            deleteAttribute((ModelOpDeleteAttribute) operation);
        } else {
            throw new RuntimeException("Unsupported Model Operation: " + operation);
        }
    }

    public void createType(ModelOpCreateType op) throws Exception;

    public void deleteType(ModelOpDeleteType op) throws Exception;

    public void appendAttribute(ModelOpAppendAttribute op) throws Exception;

    public void deleteAttribute(ModelOpDeleteAttribute op) throws Exception;

}
