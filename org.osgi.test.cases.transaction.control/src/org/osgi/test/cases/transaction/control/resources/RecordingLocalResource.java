package org.osgi.test.cases.transaction.control.resources;

import org.osgi.service.transaction.control.LocalResource;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.TransactionException;
import org.osgi.service.transaction.control.TransactionStatus;

public class RecordingLocalResource implements LocalResource {

	private final TransactionControl	txControl;

	private TransactionStatus			commitStatus;

	private TransactionStatus			rollbackStatus;

	public RecordingLocalResource(TransactionControl txControl) {
		this.txControl = txControl;
	}

	@Override
	public void commit() throws TransactionException {
		commitStatus = txControl.getCurrentContext().getTransactionStatus();
	}

	@Override
	public void rollback() throws TransactionException {
		rollbackStatus = txControl.getCurrentContext().getTransactionStatus();
	}

	public TransactionStatus getCommitStatus() {
		return commitStatus;
	}

	public TransactionStatus getRollbackStatus() {
		return rollbackStatus;
	}
}
