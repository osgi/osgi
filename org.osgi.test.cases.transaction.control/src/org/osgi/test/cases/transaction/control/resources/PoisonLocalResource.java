package org.osgi.test.cases.transaction.control.resources;

import org.osgi.service.transaction.control.LocalResource;
import org.osgi.service.transaction.control.TransactionException;

public class PoisonLocalResource implements LocalResource {

	private final TransactionException	onCommit;
	private final TransactionException	onRollback;

	public PoisonLocalResource(TransactionException toThrow) {
		this(toThrow, toThrow);
	}

	public PoisonLocalResource(TransactionException onCommit,
			TransactionException onRollback) {

		if (onCommit == null && onRollback == null) {
			throw new IllegalArgumentException(
					"At least one exception must be defined");
		}

		this.onCommit = onCommit;
		this.onRollback = onRollback;
	}

	@Override
	public void commit() throws TransactionException {
		if (onCommit != null) {
			throw onCommit;
		}
	}

	@Override
	public void rollback() throws TransactionException {
		if (onRollback != null) {
			throw onRollback;
		}
	}

}
