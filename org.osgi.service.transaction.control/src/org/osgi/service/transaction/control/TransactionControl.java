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

/**
 * The interface used by clients to control the active transaction context
 */
public interface TransactionControl extends TransactionStarter {

	/**
	 * Build a transaction context to surround a piece of transactional work
	 * 
	 * @param propagation The transaction propagation to use
	 * @return A builder to complete the creation of the transaction
	 */
	TransactionBuilder build();

	/**
	 * @return true if a transaction is currently active
	 */
	boolean activeTransaction();

	/**
	 * @return true if a transaction is currently active, or if there is a
	 *         "no transaction" context active
	 */
	boolean activeScope();

	/**
	 * @return The current transaction context, which may be a "no transaction"
	 *         context, or null if there is no active context
	 */
	TransactionContext getCurrentContext();

	/**
	 * Gets the rollback status of the active transaction
	 * 
	 * @return true if the transaction is marked for rollback
	 * @throws IllegalStateException if no transaction is active
	 */
	boolean getRollbackOnly() throws IllegalStateException;

	/**
	 * Marks the current transaction to be rolled back
	 * 
	 * @throws IllegalStateException if no transaction is active
	 */
	void setRollbackOnly() throws IllegalStateException;

	/**
	 * Marks that the current transaction should not be rolled back if the
	 * supplied Exception is thrown by the current transactional work
	 * 
	 * @param t The exception to ignore
	 * @throws IllegalStateException if no transaction is active
	 */
	void ignoreException(Throwable t) throws IllegalStateException;
}
