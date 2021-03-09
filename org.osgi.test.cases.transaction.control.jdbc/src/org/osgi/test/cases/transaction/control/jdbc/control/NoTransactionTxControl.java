/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.test.cases.transaction.control.jdbc.control;

import static java.util.Collections.*;
import static org.osgi.service.transaction.control.TransactionStatus.NO_TRANSACTION;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import javax.transaction.xa.XAResource;

import org.osgi.service.transaction.control.LocalResource;
import org.osgi.service.transaction.control.ScopedWorkException;
import org.osgi.service.transaction.control.TransactionBuilder;
import org.osgi.service.transaction.control.TransactionContext;
import org.osgi.service.transaction.control.TransactionException;
import org.osgi.service.transaction.control.TransactionRolledBackException;
import org.osgi.service.transaction.control.TransactionStatus;

public class NoTransactionTxControl implements TestTransactionControl {

	private final TransactionContext	context			= new NoTransactionContext();

	List<Runnable>						preCompletion	= new ArrayList<>();

	List<Consumer<TransactionStatus>>	postCompletion	= new ArrayList<>();

	Map<Object,Object>					variables		= new HashMap<>();

	boolean								allowPre		= true;
	boolean								allowPost		= true;

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
		return true;
	}

	@Override
	public TransactionContext getCurrentContext() {
		return context;
	}

	@Override
	public boolean getRollbackOnly() throws IllegalStateException {
		throw new IllegalStateException("No Transaction!");
	}

	@Override
	public void setRollbackOnly() throws IllegalStateException {
		throw new IllegalStateException("No Transaction!");
	}

	@Override
	public void ignoreException(Throwable t) throws IllegalStateException {
		throw new IllegalStateException("No Transaction!");
	}

	@Override
	public List<LocalResource> getEnlistedLocalResources() {
		return emptyList();
	}

	@Override
	public Map<XAResource,String> getEnlistedXAResources() {
		return emptyMap();
	}

	@Override
	public List<Throwable> finish(boolean rollback) {

		List<Throwable> list = new ArrayList<>();

		allowPre = false;

		for (Runnable pre : preCompletion) {
			try {
				pre.run();
			} catch (Exception e) {
				list.add(e);
			}
		}
		preCompletion.clear();

		allowPost = false;

		for (Consumer<TransactionStatus> post : postCompletion) {
			try {
				post.accept(NO_TRANSACTION);
			} catch (Exception e) {
				list.add(e);
			}
		}
		postCompletion.clear();

		variables.clear();
		allowPre = true;
		allowPost = true;

		return list;
	}

	class NoTransactionContext implements TransactionContext {

		@Override
		public Object getTransactionKey() {
			return null;
		}

		@Override
		public Object getScopedValue(Object key) {
			return variables.get(key);
		}

		@Override
		public void putScopedValue(Object key, Object value) {
			variables.put(key, value);
		}

		@Override
		public boolean getRollbackOnly() throws IllegalStateException {
			throw new IllegalStateException("No Transaction!");
		}

		@Override
		public void setRollbackOnly() throws IllegalStateException {
			throw new IllegalStateException("No Transaction!");
		}

		@Override
		public TransactionStatus getTransactionStatus() {
			return NO_TRANSACTION;
		}

		@Override
		public void preCompletion(Runnable job) throws IllegalStateException {
			if (allowPre) {
				preCompletion.add(job);
			} else {
				throw new IllegalStateException(
						"Called at an inappropriate time");
			}
		}

		@Override
		public void postCompletion(Consumer<TransactionStatus> job)
				throws IllegalStateException {
			if (allowPost) {
				postCompletion.add(job);
			} else {
				throw new IllegalStateException(
						"Called at an inappropriate time");
			}
		}

		@Override
		public boolean supportsXA() {
			return false;
		}

		@Override
		public boolean supportsLocal() {
			return false;
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}

		@Override
		public void registerXAResource(XAResource resource, String recoveryId)
				throws IllegalStateException {
			throw new IllegalStateException("No Transaction!");
		}

		@Override
		public void registerLocalResource(LocalResource resource)
				throws IllegalStateException {
			throw new IllegalStateException("No Transaction!");
		}

	}
}
