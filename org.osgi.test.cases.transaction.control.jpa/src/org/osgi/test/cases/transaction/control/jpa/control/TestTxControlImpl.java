/*
 * Copyright (c) OSGi Alliance (2018). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osgi.test.cases.transaction.control.jpa.control;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.transaction.xa.XAResource;

import org.osgi.service.transaction.control.LocalResource;
import org.osgi.service.transaction.control.ScopedWorkException;
import org.osgi.service.transaction.control.TransactionBuilder;
import org.osgi.service.transaction.control.TransactionContext;
import org.osgi.service.transaction.control.TransactionException;
import org.osgi.service.transaction.control.TransactionRolledBackException;

public class TestTxControlImpl implements TestTransactionControl {

	TestTransactionControl			current;

	final TestTransactionControl	tx;

	final TestTransactionControl	noTran;

	public TestTxControlImpl(boolean local, boolean xa, boolean transactional) {
		this.tx = new TransactionTxControl(local, xa);
		this.noTran = new NoTransactionTxControl();

		current = transactional ? tx : noTran;
	}

	@Override
	public <T> T required(Callable<T> work) throws TransactionException,
			TransactionRolledBackException, ScopedWorkException {
		TestTransactionControl existing = current;
		current = tx;
		try {
			return work.call();
		} catch (Exception e) {
			throw new ScopedWorkException("Bang", e,
					current.getCurrentContext());
		} finally {
			if (existing != current) {
				current.finish(false);
				current = existing;
			}
		}
	}

	@Override
	public <T> T requiresNew(Callable<T> work) throws TransactionException,
			TransactionRolledBackException, ScopedWorkException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T notSupported(Callable<T> work)
			throws TransactionException, ScopedWorkException {
		TestTransactionControl existing = current;
		current = noTran;
		try {
			return work.call();
		} catch (Exception e) {
			throw new ScopedWorkException("Bang", e,
					current.getCurrentContext());
		} finally {
			if (existing != current) {
				current.finish(false);
				current = existing;
			}
		}
	}

	@Override
	public <T> T supports(Callable<T> work)
			throws TransactionException, ScopedWorkException {
		try {
			return work.call();
		} catch (Exception e) {
			throw new ScopedWorkException("Bang", e,
					current.getCurrentContext());
		}
	}

	@Override
	public TransactionBuilder build() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean activeTransaction() {
		return true;
	}

	@Override
	public boolean activeScope() {
		return true;
	}

	@Override
	public TransactionContext getCurrentContext() {
		return current.getCurrentContext();
	}

	@Override
	public boolean getRollbackOnly() throws IllegalStateException {
		return current.getRollbackOnly();
	}

	@Override
	public void setRollbackOnly() throws IllegalStateException {
		current.setRollbackOnly();
	}

	@Override
	public void ignoreException(Throwable t) throws IllegalStateException {
		current.ignoreException(t);
	}

	@Override
	public List<LocalResource> getEnlistedLocalResources() {
		return current.getEnlistedLocalResources();
	}

	@Override
	public Map<XAResource,String> getEnlistedXAResources() {
		return current.getEnlistedXAResources();
	}

	@Override
	public List<Throwable> finish(boolean rollback) {
		return current.finish(rollback);
	}
}
