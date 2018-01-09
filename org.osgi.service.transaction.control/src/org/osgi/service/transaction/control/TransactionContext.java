/*
 * Copyright (c) OSGi Alliance (2016, 2018). All Rights Reserved.
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
package org.osgi.service.transaction.control;

import java.util.function.Consumer;

import javax.transaction.xa.XAResource;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.service.transaction.control.recovery.RecoverableXAResource;

/**
 * A transaction context defines the current transaction, and allows resources
 * to register information and/or synchronizations
 */
@ProviderType
public interface TransactionContext {

	/**
	 * Get the key associated with the current transaction
	 * 
	 * @return the transaction key, or null if there is no transaction
	 */
	Object getTransactionKey();

	/**
	 * Get a value scoped to this transaction
	 * 
	 * @param key
	 * @return The resource, or <code>null</code>
	 */
	Object getScopedValue(Object key);

	/**
	 * Associate a value with this transaction
	 * 
	 * @param key
	 * @param value
	 */
	void putScopedValue(Object key, Object value);

	/**
	 * Is this transaction marked for rollback only
	 * 
	 * @return true if this transaction is rollback only
	 * @throws IllegalStateException if no transaction is active
	 */
	boolean getRollbackOnly() throws IllegalStateException;

	/**
	 * Mark this transaction for rollback
	 * 
	 * @throws IllegalStateException if no transaction is active
	 */
	void setRollbackOnly() throws IllegalStateException;

	/**
	 * @return The current transaction status
	 */
	TransactionStatus getTransactionStatus();

	/**
	 * Register a callback that will be made before a scope completes.
	 * <p>
	 * For transactional scopes the state of the scope will be either
	 * {@link TransactionStatus#ACTIVE} or
	 * {@link TransactionStatus#MARKED_ROLLBACK}. Pre-completion callbacks may
	 * call {@link #setRollbackOnly()} to prevent a commit from proceeding.
	 * <p>
	 * For no-transaction scopes the state of the scope will always be
	 * {@link TransactionStatus#NO_TRANSACTION}.
	 * <p>
	 * Exceptions thrown by pre-completion callbacks are treated as if they were
	 * thrown by the scoped work, including any configured commit or rollback
	 * behaviors for transactional scopes.
	 * 
	 * @param job The action to perform before completing the scope
	 * @throws IllegalStateException if the transaction has already passed
	 *             beyond the {@link TransactionStatus#MARKED_ROLLBACK} state
	 */
	void preCompletion(Runnable job) throws IllegalStateException;

	/**
	 * Register a callback that will be made after the scope completes
	 * <p>
	 * For transactional scopes the state of the scope will be either
	 * {@link TransactionStatus#COMMITTED} or
	 * {@link TransactionStatus#ROLLED_BACK}.
	 * <p>
	 * For no-transaction scopes the state of the scope will always be
	 * {@link TransactionStatus#NO_TRANSACTION}.
	 * <p>
	 * Post-completion callbacks should not throw {@link Exception}s and cannot
	 * affect the outcome of a piece of scoped work
	 * 
	 * @param job
	 * @throws IllegalStateException if no transaction is active
	 */
	void postCompletion(Consumer<TransactionStatus> job)
			throws IllegalStateException;

	/**
	 * @return true if the current transaction supports XA resources
	 */
	boolean supportsXA();

	/**
	 * @return true if the current transaction supports Local resources
	 */
	boolean supportsLocal();

	/**
	 * @return true if the TransactionContext supports read-only optimizations
	 *         <em>and</em> the transaction was marked read only. In particular
	 *         it is legal for this method to return false even if the
	 *         transaction was marked read only by the initiating client.
	 */
	boolean isReadOnly();

	/**
	 * Register an XA resource with the current transaction
	 * 
	 * @param resource
	 * @param recoveryId The resource id to be used for recovery, the id may be
	 *            <code>null</code> if this resource is not recoverable.
	 *            <p>
	 *            If an id is passed then a {@link RecoverableXAResource} with
	 *            the same id must be registered in the service registry for
	 *            recovery to occur.
	 *            <p>
	 *            If the underlying {@link TransactionControl} service does not
	 *            support recovery then it must treat the resource as if it is
	 *            not recoverable.
	 * @throws IllegalStateException if no transaction is active, or the current
	 *             transaction is not XA capable
	 */
	void registerXAResource(XAResource resource, String recoveryId)
			throws IllegalStateException;

	/**
	 * Register a Local resource with the current transaction
	 * 
	 * @param resource
	 * @throws IllegalStateException if no transaction is active, or the current
	 *             transaction does not support local resources.
	 */
	void registerLocalResource(LocalResource resource)
			throws IllegalStateException;
}
