package org.osgi.test.cases.tr069todmt.util;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.service.dmt.spi.ReadWriteDataSession;
import org.osgi.service.dmt.spi.ReadableDataSession;
import org.osgi.service.dmt.spi.TransactionalDataSession;

public abstract class GenericDataPlugin<T> implements DataPlugin {
	final ReadWriteLock	rwl	= new ReentrantReadWriteLock();
	final String		base[];

	protected GenericDataPlugin(String[] base) {
		this.base = base;
	}

	public ReadableDataSession openReadOnlySession(String[] sessionRoot,
			DmtSession session) throws DmtException {

		return new GDSession<T>(this, rwl.readLock(), false, base,
				getRoot(false));
	}

	public ReadWriteDataSession openReadWriteSession(String[] sessionRoot,
			DmtSession session) throws DmtException {

		return new GDSession<T>(this, rwl.writeLock(), true, base,
				getRoot(true));
	}

	public TransactionalDataSession openAtomicSession(String[] sessionRoot,
			DmtSession session) throws DmtException {

		return new GDSession<T>(this, rwl.writeLock(), true, base,
				getRoot(true));
	}

	abstract protected T getRoot(boolean writeable);

}
