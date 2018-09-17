package org.state.patch.sql.control.op;

public class ControlOpSuspend extends ControlOp {
    public final boolean shutdown;

    public ControlOpSuspend(boolean shutdown) {
        this.shutdown = shutdown;
    }

}
