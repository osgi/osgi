/*
 * Copyright (c) OSGi Alliance (2016). All Rights Reserved.
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

/**
 * A transaction context defines the current transaction, and allows resources
 * to register information and/or synchronisations
 */
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
	 * Register a callback that will be made before a call to commit or rollback
	 * 
	 * @param job
	 * @throws IllegalStateException if no transaction is active or the
	 *             transaction has already passed beyond the
	 *             {@link TransactionStatus#MARKED_ROLLBACK} state
	 */
	void preCompletion(Runnable job) throws IllegalStateException;

	/**
	 * Register a callback that will be made after the decision to commit or
	 * rollback
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
	 * @return true if the TransactionContext supports read-only optimisations
	 * <em>and</em> the transaction was marked read only. In particular it is
	 * legal for this method to return false even if the transaction was marked
	 * read only by the initiating client.
	 */
	boolean isReadOnly();

	/**
	 * Register an XA resource with the current transaction
	 * 
	 * @param resource
	 * @throws IllegalStateException if no transaction is active, or the current
	 *             transaction is not XA capable
	 */
	void registerXAResource(XAResource resource) throws IllegalStateException;

	/**
	 * Register an XA resource with the current transaction
	 * 
	 * @param resource
	 * @throws IllegalStateException if no transaction is active, or the current
	 *             transaction is not XA capable
	 */
	void registerLocalResource(LocalResource resource)
			throws IllegalStateException;
}
