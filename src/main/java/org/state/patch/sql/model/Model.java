package org.state.patch.sql.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.state.patch.sql.model.op.ModelOpAppendAttribute;
import org.state.patch.sql.model.op.ModelOpCreateType;
import org.state.patch.sql.model.op.ModelOpDeleteAttribute;
import org.state.patch.sql.model.op.ModelOpDeleteType;
import org.state.patch.sql.patch.PatchModelProcessor;

public class Model implements PatchModelProcessor {

    Map<String, EntityType> types;

    public Model() {
        this.types = Collections.synchronizedMap(new HashMap<>());
    }

    @Override
    public String toString() {
        return "Model";
    }

    public EntityType getEntityType(String type) {
        return types.get(type);
    }

    @Override
    public void createType(ModelOpCreateType op) {
        Attribute identity = new Attribute(op.identity.name,
                                           op.identity.type,
                                           op.identity.initial);

        List<Attribute> typeAttrs = new ArrayList<>(op.attrs.size());
        for (ModelOpCreateType.Attribute opAttr : op.attrs) {
            typeAttrs.add(new Attribute(opAttr.name,
                                        opAttr.type,
                                        opAttr.initial));
        }
        types.put(op.type, new EntityType(op.type,
                                          identity,
                                          typeAttrs));
    }

    @Override
    public void deleteType(ModelOpDeleteType op) {
        types.remove(op.type);
    }

    @Override
    public void appendAttribute(ModelOpAppendAttribute op) {
        EntityType oldType = types.get(op.type);
        Collection<Attribute> oldAttrs = oldType.attrs.values();

        List<Attribute> newAttrs = new ArrayList<>(oldAttrs.size() + 1);
        newAttrs.addAll(oldType.attrs.values());
        newAttrs.add(new Attribute(op.attr.name,
                                   op.attr.type,
                                   op.attr.initial));

        types.put(oldType.name, new EntityType(oldType.name,
                                               oldType.identity,
                                               newAttrs));

    }

    @Override
    public void deleteAttribute(ModelOpDeleteAttribute op) {
        EntityType oldType = types.get(op.type);

        List<Attribute> newAttrs = new ArrayList<>(oldType.attrs.values());
        newAttrs.removeIf(a -> Objects.equals(a.name, op.attribName));

        types.put(oldType.name, new EntityType(oldType.name,
                                               oldType.identity,
                                               newAttrs));
    }

    public void add(EntityType entityType) {
    }
}
