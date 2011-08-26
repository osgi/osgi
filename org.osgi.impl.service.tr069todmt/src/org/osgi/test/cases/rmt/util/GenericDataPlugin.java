package org.osgi.test.cases.rmt.util;

import java.util.concurrent.locks.*;

import org.osgi.service.dmt.*;
import org.osgi.service.dmt.spi.*;

public abstract class GenericDataPlugin<T> implements DataPlugin {
	final ReadWriteLock	rwl	= new ReentrantReadWriteLock();
	final String		base[];

	protected GenericDataPlugin(String[] base) {
		this.base = base;
	}

	@Override
	public ReadableDataSession openReadOnlySession(String[] sessionRoot,
			DmtSession session) throws DmtException {

		return new GDSession<T>(this, rwl.readLock(), false, base,
				getRoot(false));
	}

	@Override
	public ReadWriteDataSession openReadWriteSession(String[] sessionRoot,
			DmtSession session) throws DmtException {

		return new GDSession<T>(this, rwl.writeLock(), true, base,
				getRoot(true));
	}

	@Override
	public TransactionalDataSession openAtomicSession(String[] sessionRoot,
			DmtSession session) throws DmtException {

		return new GDSession<T>(this, rwl.writeLock(), true, base,
				getRoot(true));
	}

	abstract protected T getRoot(boolean writeable);

}
