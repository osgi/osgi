package org.osgi.test.cases.transaction.control.jdbc.control;

import java.util.concurrent.Callable;

import org.osgi.service.transaction.control.ScopedWorkException;
import org.osgi.service.transaction.control.TransactionBuilder;
import org.osgi.service.transaction.control.TransactionContext;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.TransactionException;
import org.osgi.service.transaction.control.TransactionRolledBackException;

public class UnscopedTxControl implements TransactionControl {

	@Override
	public <T> T required(Callable<T> work) throws TransactionException,
			TransactionRolledBackException, ScopedWorkException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T requiresNew(Callable<T> work) throws TransactionException,
			TransactionRolledBackException, ScopedWorkException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T notSupported(Callable<T> work)
			throws TransactionException, ScopedWorkException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T supports(Callable<T> work)
			throws TransactionException, ScopedWorkException {
		throw new UnsupportedOperationException();
	}

	@Override
	public TransactionBuilder build() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean activeTransaction() {
		return false;
	}

	@Override
	public boolean activeScope() {
		return false;
	}

	@Override
	public TransactionContext getCurrentContext() {
		return null;
	}

	@Override
	public boolean getRollbackOnly() throws IllegalStateException {
		throw new IllegalStateException("No Scope!");
	}

	@Override
	public void setRollbackOnly() throws IllegalStateException {
		throw new IllegalStateException("No Scope!");
	}

	@Override
	public void ignoreException(Throwable t) throws IllegalStateException {
		throw new IllegalStateException("No Scope!");
	}
}
