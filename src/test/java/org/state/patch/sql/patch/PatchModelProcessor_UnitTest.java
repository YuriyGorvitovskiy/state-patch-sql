package org.state.patch.sql.patch;

import java.util.Arrays;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.state.patch.sql.model.op.ModelOp;
import org.state.patch.sql.model.op.ModelOpAppendAttribute;
import org.state.patch.sql.model.op.ModelOpCreateType;
import org.state.patch.sql.model.op.ModelOpDeleteAttribute;
import org.state.patch.sql.model.op.ModelOpDeleteType;

public class PatchModelProcessor_UnitTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    PatchModelProcessor subject = new PatchModelProcessor() {

        @Override
        public void createType(ModelOpCreateType op) throws Exception {
        }

        @Override
        public void deleteType(ModelOpDeleteType op) throws Exception {
        }

        @Override
        public void appendAttribute(ModelOpAppendAttribute op) throws Exception {
        }

        @Override
        public void deleteAttribute(ModelOpDeleteAttribute op) throws Exception {
        }
    };

    @Test
    public void loadFromResource() throws Exception {
        // Setup
        final PatchModelProcessor spy = Mockito.spy(subject);

        // Execute
        spy.loadFromResource(PatchModelProcessor_UnitTest.class,
                             "PatchModelProcessor_UnitTest.loadFromResource.model.json",
                             new JsonPatchTranslator(null));

        //Validate
        InOrder inOrder = Mockito.inOrder(spy);
        inOrder.verify(spy).createType(Mockito.any(ModelOpCreateType.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void apply_patch() throws Exception {
        // Setup
        final ModelOpCreateType createTypeOp = new ModelOpCreateType(null, null, null);
        final ModelOpDeleteType deleteTypeOp = new ModelOpDeleteType(null);
        final ModelOpAppendAttribute appendAttributeOp = new ModelOpAppendAttribute(null, null);
        final ModelOpDeleteAttribute deleteAttributeOp = new ModelOpDeleteAttribute(null, null);

        final PatchModel patch = new PatchModel(Arrays.asList(createTypeOp,
                                                              deleteTypeOp,
                                                              appendAttributeOp,
                                                              deleteAttributeOp),
                                                null,
                                                null,
                                                null,
                                                0L,
                                                0L);

        final PatchModelProcessor spy = Mockito.spy(subject);

        // Execute
        spy.apply(patch);

        //Validate
        InOrder inOrder = Mockito.inOrder(spy);
        inOrder.verify(spy).createType(createTypeOp);
        inOrder.verify(spy).deleteType(deleteTypeOp);
        inOrder.verify(spy).appendAttribute(appendAttributeOp);
        inOrder.verify(spy).deleteAttribute(deleteAttributeOp);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void apply_unknown_operation() throws Exception {
        // Setup
        final ModelOp unknownOp = new ModelOp(null) {
        };

        exception.expect(RuntimeException.class);
        exception.expectMessage("Unsupported Model Operation: ");

        // Execute
        subject.apply(unknownOp);
    }

}
