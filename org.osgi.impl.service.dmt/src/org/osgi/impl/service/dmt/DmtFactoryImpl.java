package org.osgi.impl.service.dmt;

import org.osgi.service.dmt.*;

public class DmtFactoryImpl implements DmtFactory {
	private DmtPlugInDispatcher	dispatcher;

	public DmtFactoryImpl(DmtPlugInDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	public DmtSession getTree(DmtPrincipal principal) throws DmtException {
		return getTree(principal, ".", DmtSession.LOCK_TYPE_AUTOMATIC);
	}

	public DmtSession getTree(DmtPrincipal principal, String subtreeUri)
			throws DmtException {
		return getTree(principal, subtreeUri, DmtSession.LOCK_TYPE_AUTOMATIC);
	}

	public DmtSession getTree(DmtPrincipal principal, String subtreeUri,
			int lockMode) throws DmtException {
		return new DmtSessionImpl(principal, subtreeUri, lockMode, dispatcher);
	}
}
