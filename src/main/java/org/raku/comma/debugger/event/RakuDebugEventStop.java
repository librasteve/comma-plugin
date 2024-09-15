package org.raku.comma.debugger.event;

import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.frame.XSuspendContext;
import org.raku.comma.debugger.RakuDebugThread;
import org.raku.comma.debugger.RakuSuspendContext;
import org.raku.comma.debugger.RakuThreadDescriptor;

public class RakuDebugEventStop extends RakuDebugEventBase implements RakuDebugEvent {
    private final RakuThreadDescriptor[] threads;
    private final int activeThreadIndex;

    public RakuDebugEventStop(RakuThreadDescriptor[] threads, int activeThreadIndex, XDebugSession session, RakuDebugThread thread) {
        setDebugSession(session);
        setDebugThread(thread);
        this.threads = threads;
        this.activeThreadIndex = activeThreadIndex;
    }

    XSuspendContext getSuspendContext() {
        return new RakuSuspendContext(threads, activeThreadIndex, getDebugSession(), getDebugThread());
    }

    @Override
    public void run() {
        getDebugSession().positionReached(getSuspendContext());
    }
}