package org.state.patch.sql.model.db;

import org.state.patch.sql.model.op.ModelOpAppendAttribute;
import org.state.patch.sql.model.op.ModelOpCreateType;
import org.state.patch.sql.model.op.ModelOpDeleteAttribute;
import org.state.patch.sql.model.op.ModelOpDeleteType;

public interface DatabaseModel {

    public boolean isTypeExists(String type) throws Exception;

    public void createType(ModelOpCreateType op) throws Exception;

    public void deleteType(ModelOpDeleteType op) throws Exception;

    public void appendAttribute(ModelOpAppendAttribute op) throws Exception;

    public void deleteAttribute(ModelOpDeleteAttribute op) throws Exception;

}
