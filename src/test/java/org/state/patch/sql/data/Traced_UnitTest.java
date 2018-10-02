package org.state.patch.sql.data;

import static org.junit.Assert.assertSame;

import java.util.Date;

import org.junit.Test;

public class Traced_UnitTest {

    @Test
    public void constructor() {
        // Setup,
        ReferenceExternal modifiedBy = new ReferenceExternal("user:3");
        Date modifiedAt = new Date();
        long modifiedEventId = 45;
        long modifiedPatchId = 67;

        // Execute
        Traced traced = new Traced(modifiedBy, modifiedAt, modifiedEventId, modifiedPatchId) {
        };

        //Verify
        assertSame(modifiedBy, traced.modifiedBy);
        assertSame(modifiedAt, traced.modifiedAt);
        assertSame(modifiedEventId, traced.modifiedEventId);
        assertSame(modifiedPatchId, traced.modifiedPatchId);
    }

}
