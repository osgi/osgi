package org.osgi.test.cases.transaction.control.resources;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

public class PoisonXAResource implements XAResource {

	private final XAException	onPrepare;
	private final XAException	onCommit;
	private final XAException	onRollback;

	public PoisonXAResource(XAException toThrow) {
		this(toThrow, toThrow);
	}

	public PoisonXAResource(XAException onCommit, XAException onRollback) {
		this(onCommit, onRollback, null);
	}

	public PoisonXAResource(XAException onCommit, XAException onRollback,
			XAException onPrepare) {

		if (onCommit == null && onRollback == null) {
			throw new IllegalArgumentException(
					"At least one exception must be defined");
		}

		this.onCommit = onCommit;
		this.onRollback = onRollback;
		this.onPrepare = onPrepare;
	}

	@Override
	public void commit(Xid xid, boolean onePhase) throws XAException {
		if (onCommit != null) {
			throw onCommit;
		}
	}

	@Override
	public void end(Xid xid, int flags) throws XAException {
		// TODO Auto-generated method stub
	}

	@Override
	public void forget(Xid xid) throws XAException {
		// TODO Auto-generated method stub
	}

	@Override
	public int getTransactionTimeout() throws XAException {
		return 0;
	}

	@Override
	public boolean isSameRM(XAResource xares) throws XAException {
		return xares == this;
	}

	@Override
	public int prepare(Xid xid) throws XAException {
		if (onPrepare != null) {
			throw onPrepare;
		}
		return XA_OK;
	}

	@Override
	public Xid[] recover(int flag) throws XAException {
		return new Xid[0];
	}

	@Override
	public void rollback(Xid xid) throws XAException {
		if (onRollback != null) {
			throw onRollback;
		}
	}

	@Override
	public boolean setTransactionTimeout(int seconds) throws XAException {
		return false;
	}

	@Override
	public void start(Xid xid, int flags) throws XAException {
		// TODO Auto-generated method stub
	}

}
