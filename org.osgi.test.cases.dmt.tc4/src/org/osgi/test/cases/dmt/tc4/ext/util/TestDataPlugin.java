package org.osgi.test.cases.dmt.tc4.ext.util;

import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.service.dmt.spi.ReadWriteDataSession;
import org.osgi.service.dmt.spi.ReadableDataSession;
import org.osgi.service.dmt.spi.TransactionalDataSession;

import java.util.HashMap;
import java.util.Map;

public class TestDataPlugin implements DataPlugin {

    protected final Map nodeMap = new HashMap();

    public ReadableDataSession openReadOnlySession(String[] sessionRoot, DmtSession session) throws DmtException {
        return new TestPluginReadableDataSession(nodeMap);
    }

    public ReadWriteDataSession openReadWriteSession(String[] sessionRoot, DmtSession session) throws DmtException {
        return new TestPluginReadWriteDataSession(nodeMap);
    }

    public TransactionalDataSession openAtomicSession(String[] sessionRoot, DmtSession session) throws DmtException {
        return null;
    }

    public void setNode(String nodeUri, TestNode node) {
        nodeMap.put(nodeUri, node);
    }
}
