package org.osgi.test.cases.dmt.tc4.ext.util;

import static junit.framework.TestCase.fail;

import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.spi.ExecPlugin;

public class TestExecPlugin implements ExecPlugin {

    private boolean wasExecuteCalled;

    private DmtSession session;

    private String[] nodePath;

    private String correlator;

    private String data;

    private boolean failed = false;

    @Override
	public void execute(DmtSession session, String[] nodePath, String correlator, String data) throws DmtException {
        synchronized (this) {
            if (wasExecuteCalled) {
                failed = true;
            }
        }
        this.wasExecuteCalled = true;
        this.session = session;
        this.nodePath = nodePath;
        this.correlator = correlator;
        this.data = data;
    }

    public boolean wasExecuteCalled() {
        synchronized (this) {
            if (failed) {
				fail();
            }
        }

        try {
            return wasExecuteCalled;
        } finally {
            wasExecuteCalled = false;
        }
    }

    public DmtSession getSession() {
        synchronized (this) {
            if (failed) {
				fail();
            }
        }
        return session;
    }

    public String[] getNodePath() {
        synchronized (this) {
            if (failed) {
				fail();
            }
        }
        return nodePath;
    }

    public String getCorrelator() {
        synchronized (this) {
            if (failed) {
				fail();
            }
        }
        return correlator;
    }

    public String getData() {
        synchronized (this) {
            if (failed) {
				fail();
            }
        }
        return data;
    }
}
