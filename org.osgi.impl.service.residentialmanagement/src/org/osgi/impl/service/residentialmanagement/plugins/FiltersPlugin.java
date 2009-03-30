package org.osgi.impl.service.residentialmanagement.plugins;

import org.osgi.framework.BundleContext;

import info.dmtree.DmtException;
import info.dmtree.DmtSession;
import info.dmtree.spi.DataPlugin;
import info.dmtree.spi.ReadWriteDataSession;
import info.dmtree.spi.ReadableDataSession;
import info.dmtree.spi.TransactionalDataSession;

public class FiltersPlugin implements DataPlugin {
	private FiltersReadOnlySession readonly;
	private FiltersReadWriteSession readwrite;

	FiltersPlugin(BundleContext context) {
		readonly = new FiltersReadOnlySession(this, context);
		readwrite = new FiltersReadWriteSession(this, context);
	}

	public TransactionalDataSession openAtomicSession(String[] sessionRoot,
			DmtSession session) throws DmtException {
		// TODO Auto-generated method stub
		return null;
	}

	public ReadableDataSession openReadOnlySession(String[] sessionRoot,
			DmtSession session) throws DmtException {
		return readonly;
	}

	public ReadWriteDataSession openReadWriteSession(String[] sessionRoot,
			DmtSession session) throws DmtException {
		return readwrite;
	}

}
