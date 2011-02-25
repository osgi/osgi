package org.osgi.test.cases.dmt.tc4.tb1;

import info.dmtree.DmtException;
import info.dmtree.DmtSession;
import info.dmtree.spi.DataPlugin;
import info.dmtree.spi.ReadWriteDataSession;
import info.dmtree.spi.ReadableDataSession;
import info.dmtree.spi.TransactionalDataSession;

import org.osgi.framework.BundleContext;
import org.osgi.test.cases.dmt.tc4.tb1.intf.Node;
import org.osgi.test.cases.dmt.tc4.tb1.nodes.Framework;


public class FrameworkDP implements DataPlugin {
	private Node frameworkNode;

	
	public FrameworkDP(BundleContext context) {
		frameworkNode = new Framework(context);
	}
	

	public TransactionalDataSession openAtomicSession(String[] sessionRoot,
			DmtSession session) throws DmtException {
		System.out.println("[Framework Data Plugin] Atomic Sessions are not supported");
		return null;
	}

	public ReadableDataSession openReadOnlySession(String[] sessionRoot,
			DmtSession session) throws DmtException {
		System.out.println("[Framework Data Plugin] Creating a Read-Only Session");
		return new ReadOnlyDS(frameworkNode);
	}

	public ReadWriteDataSession openReadWriteSession(String[] sessionRoot,
			DmtSession session) throws DmtException {
		System.out.println("[Framework Data Plugin] Creating a Read-Write Session");
		return new ReadWriteDS(frameworkNode);
	}
}
