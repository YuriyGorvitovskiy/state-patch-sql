package org.state.patch.sql.patch;

import java.util.Arrays;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.state.patch.sql.data.op.DataOp;
import org.state.patch.sql.data.op.DataOpDelete;
import org.state.patch.sql.data.op.DataOpInsert;
import org.state.patch.sql.data.op.DataOpUpdate;
import org.state.patch.sql.patch.PatchData;
import org.state.patch.sql.patch.PatchDataProcessor;

public class PatchDataProcessor_UnitTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    PatchDataProcessor subject = new PatchDataProcessor() {

        @Override
        public void insert(DataOpInsert op) throws Exception {
        }

        @Override
        public void update(DataOpUpdate op) throws Exception {
        }

        @Override
        public void delete(DataOpDelete op) throws Exception {
        }
    };

    @Test
    public void apply_patch() throws Exception {
        // Setup
        final DataOpInsert insertOp = new DataOpInsert(null, null);
        final DataOpUpdate updateOp = new DataOpUpdate(null, null);
        final DataOpDelete deleteOp = new DataOpDelete(null);

        final PatchData patch = new PatchData(Arrays.asList(insertOp, updateOp, deleteOp), null, null, null, 0L, 0L);

        final PatchDataProcessor spy = Mockito.spy(subject);

        // Execute
        spy.apply(patch);

        //Validate
        InOrder inOrder = Mockito.inOrder(spy);
        inOrder.verify(spy).insert(insertOp);
        inOrder.verify(spy).update(updateOp);
        inOrder.verify(spy).delete(deleteOp);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void apply_unknown_operation() throws Exception {
        // Setup
        final DataOp unknownOp = new DataOp(null) {
        };

        exception.expect(RuntimeException.class);
        exception.expectMessage("Unsupported Data Operation: ");

        // Execute
        subject.apply(unknownOp);
    }

}
