package org.osgi.test.cases.dmt.tc4.tb1;

import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.service.dmt.spi.ReadWriteDataSession;
import org.osgi.service.dmt.spi.ReadableDataSession;
import org.osgi.service.dmt.spi.TransactionalDataSession;

import org.osgi.framework.BundleContext;
import org.osgi.test.cases.dmt.tc4.tb1.intf.Node;
import org.osgi.test.cases.dmt.tc4.tb1.nodes.Framework;


public class FrameworkDP implements DataPlugin {
	private Node frameworkNode;

	
	public FrameworkDP(BundleContext context) {
		frameworkNode = new Framework(context);
	}
	

	@Override
	public TransactionalDataSession openAtomicSession(String[] sessionRoot,
			DmtSession session) throws DmtException {
		System.out.println("[Framework Data Plugin] Atomic Sessions are not supported");
		return null;
	}

	@Override
	public ReadableDataSession openReadOnlySession(String[] sessionRoot,
			DmtSession session) throws DmtException {
		System.out.println("[Framework Data Plugin] Creating a Read-Only Session");
		return new ReadOnlyDS(frameworkNode);
	}

	@Override
	public ReadWriteDataSession openReadWriteSession(String[] sessionRoot,
			DmtSession session) throws DmtException {
		System.out.println("[Framework Data Plugin] Creating a Read-Write Session");
		return new ReadWriteDS(frameworkNode);
	}
}
