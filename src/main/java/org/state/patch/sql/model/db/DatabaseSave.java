package org.state.patch.sql.model.db;

import org.state.patch.sql.data.op.DataOpDelete;
import org.state.patch.sql.data.op.DataOpInsert;
import org.state.patch.sql.data.op.DataOpUpdate;

public interface DatabaseSave {

    public void insert(DataOpInsert op) throws Exception;

    public void delete(DataOpDelete op) throws Exception;

    public void update(DataOpUpdate op) throws Exception;

}
