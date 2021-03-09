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

import static org.osgi.service.transaction.control.TransactionStatus.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.osgi.service.transaction.control.LocalResource;
import org.osgi.service.transaction.control.ScopedWorkException;
import org.osgi.service.transaction.control.TransactionBuilder;
import org.osgi.service.transaction.control.TransactionContext;
import org.osgi.service.transaction.control.TransactionException;
import org.osgi.service.transaction.control.TransactionRolledBackException;
import org.osgi.service.transaction.control.TransactionStatus;

public class TransactionTxControl implements TestTransactionControl {

	final boolean						local;

	final boolean						xa;

	private final TransactionContext	context			= new TransactionContextImpl();

	UUID								id				= UUID.randomUUID();

	List<Runnable>						preCompletion	= new ArrayList<>();

	List<Consumer<TransactionStatus>>	postCompletion	= new ArrayList<>();

	Map<Object,Object>					variables		= new HashMap<>();

	Map<XAResource,String>				xaResources		= new HashMap<>();
	Map<XAResource,Xid>					resourceBranches	= new HashMap<>();

	List<LocalResource>					localResources	= new ArrayList<>();

	boolean								allowPre		= true;
	boolean								allowPost		= true;

	TransactionStatus					status				= TransactionStatus.ACTIVE;

	public TransactionTxControl(boolean local, boolean xa) {
		this.local = local;
		this.xa = xa;
	}

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
		return true;
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
		return status == MARKED_ROLLBACK || status == ROLLING_BACK;
	}

	@Override
	public void setRollbackOnly() throws IllegalStateException {
		if (status == ACTIVE || status == MARKED_ROLLBACK) {
			status = MARKED_ROLLBACK;
			return;
		}
		throw new IllegalStateException("Too late to set rollback");
	}

	@Override
	public void ignoreException(Throwable t) throws IllegalStateException {
		throw new UnsupportedOperationException(
				"Not permitted in the tests as it is not used!");
	}

	@Override
	public List<LocalResource> getEnlistedLocalResources() {
		return localResources;
	}

	@Override
	public Map<XAResource,String> getEnlistedXAResources() {
		return xaResources;
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

		rollback = rollback || status == MARKED_ROLLBACK;

		status = rollback ? ROLLING_BACK : COMMITTING;

		if (local) {
			for (LocalResource lr : localResources) {
				try {
					if (rollback) {
						lr.rollback();
					} else {
						lr.commit();
					}
				} catch (Exception e) {
					list.add(e);
				}
			}
		}
		localResources.clear();

		// Not a full XA algorithm as we don't care that much!
		if (xa) {
			for (XAResource xaResource : xaResources.keySet()) {
				try {
					Xid branchId = resourceBranches.get(xaResource);
					xaResource.end(branchId, rollback ? XAResource.TMFAIL
							: XAResource.TMSUCCESS);
					if (rollback) {
						xaResource.rollback(branchId);
					} else {
						xaResource.commit(branchId, true);
					}
				} catch (Exception e) {
					list.add(e);
				}
			}
		}
		
		status = status == ROLLING_BACK ? ROLLED_BACK : COMMITTED;

		xaResources.clear();
		resourceBranches.clear();
		
		allowPost = false;

		for (Consumer<TransactionStatus> post : postCompletion) {
			try {
				post.accept(status);
			} catch (Exception e) {
				list.add(e);
			}
		}
		postCompletion.clear();

		variables.clear();
		allowPre = true;
		allowPost = true;
		id = UUID.randomUUID();
		status = ACTIVE;

		return list;
	}

	class TransactionContextImpl implements TransactionContext {

		@Override
		public Object getTransactionKey() {
			return id;
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
			return TransactionTxControl.this.getRollbackOnly();
		}

		@Override
		public void setRollbackOnly() throws IllegalStateException {
			TransactionTxControl.this.setRollbackOnly();
		}

		@Override
		public TransactionStatus getTransactionStatus() {
			return status;
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
			return xa;
		}

		@Override
		public boolean supportsLocal() {
			return local;
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}

		@Override
		public void registerXAResource(XAResource resource, String recoveryId)
				throws IllegalStateException {
			if (xa) {
				Xid id = new XidImpl();
				try {
					resource.start(id, XAResource.TMNOFLAGS);
				} catch (XAException e) {
					throw new TransactionException("Unable to add XA resource",
							e);
				}

				xaResources.put(resource, recoveryId);
				resourceBranches.put(resource, id);
				return;
			}
			throw new IllegalStateException("XA not supported");
		}

		@Override
		public void registerLocalResource(LocalResource resource)
				throws IllegalStateException {
			if (local) {
				localResources.add(resource);
				return;
			}

			throw new IllegalStateException("Local not supported");
		}

	}

	class XidImpl implements Xid {

		UUID branchId = UUID.randomUUID();

		@Override
		public byte[] getBranchQualifier() {
			return getUUIDBytes(branchId);
		}

		@Override
		public int getFormatId() {
			return 0;
		}

		@Override
		public byte[] getGlobalTransactionId() {
			return getUUIDBytes(id);
		}

		private byte[] getUUIDBytes(UUID uuid) {
			return (Long.toHexString(uuid.getMostSignificantBits())
					+ Long.toHexString(uuid.getLeastSignificantBits()))
							.getBytes(StandardCharsets.UTF_8);
		}

	}
}
