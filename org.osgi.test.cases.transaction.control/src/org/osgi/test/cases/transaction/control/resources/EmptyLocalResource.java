package org.osgi.test.cases.transaction.control.resources;

import org.osgi.service.transaction.control.LocalResource;
import org.osgi.service.transaction.control.TransactionException;

public abstract class EmptyLocalResource implements LocalResource {

	@Override
	public void commit() throws TransactionException {
		// TODO Auto-generated method stub

	}

	@Override
	public void rollback() throws TransactionException {
		// TODO Auto-generated method stub

	}

}
