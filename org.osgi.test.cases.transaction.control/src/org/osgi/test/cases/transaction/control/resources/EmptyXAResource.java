package org.osgi.test.cases.transaction.control.resources;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

public abstract class EmptyXAResource implements XAResource {

	@Override
	public void commit(Xid xid, boolean onePhase) throws XAException {

	}

	@Override
	public void end(Xid xid, int flags) throws XAException {

	}

	@Override
	public void forget(Xid xid) throws XAException {

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
		return XA_OK;
	}

	@Override
	public Xid[] recover(int flag) throws XAException {
		return new Xid[0];
	}

	@Override
	public void rollback(Xid xid) throws XAException {

	}

	@Override
	public boolean setTransactionTimeout(int seconds) throws XAException {
		return false;
	}

	@Override
	public void start(Xid xid, int flags) throws XAException {

	}

}
