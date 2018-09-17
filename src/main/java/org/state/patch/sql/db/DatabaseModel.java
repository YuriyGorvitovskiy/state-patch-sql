package org.state.patch.sql.db;

import org.state.patch.sql.patch.PatchModelProcessor;

public interface DatabaseModel extends PatchModelProcessor {

    public boolean isTypeExists(String type) throws Exception;

}
