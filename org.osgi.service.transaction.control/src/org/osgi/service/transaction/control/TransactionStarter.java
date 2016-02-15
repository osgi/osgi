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

import java.util.concurrent.Callable;

/**
 * Implementations of this interface are able to run a piece of work within a
 * transaction
 */
public interface TransactionStarter {
	/**
	 * A transaction is required to run the supplied piece of work. If no
	 * transaction is active then it must be started and associated with the
	 * work and then completed after the transactional work has finished.
	 * 
	 * @param work
	 * @return The value returned by the work
	 * @throws TransactionException if there is an error starting or completing
	 *             the transaction
	 * @throws TransactionRolledBackException if the transaction rolled back due
	 *             to a failure in one of the resources or an internal error in
	 *             the TransactionControl service
	 * @throws ScopedWorkException if the supplied work throws an
	 *             {@link Exception}
	 */
	<T> T required(Callable<T> work) throws TransactionException,
			TransactionRolledBackException, ScopedWorkException;

	/**
	 * A new transaction is required to run the supplied piece of work. If an
	 * existing transaction is active then it must suspended and a new
	 * transaction started and associated with the work. After the work has
	 * completed the new transaction must also complete and any suspended
	 * transaction be resumed.
	 * 
	 * @param work
	 * @return The value returned by the work
	 * @throws TransactionException if there is an error starting or completing
	 *             the transaction
	 * @throws TransactionRolledBackException if the transaction rolled back due
	 *             to a failure
	 * @throws ScopedWorkException if the supplied work throws an
	 *             {@link Exception}
	 */
	<T> T requiresNew(Callable<T> work) throws TransactionException,
			TransactionRolledBackException, ScopedWorkException;

	/**
	 * The supplied piece of work must be run outside the context of a
	 * transaction. If an existing transaction is active then it must be
	 * suspended and a "no transaction" context associated with the work. After
	 * the work has completed any suspended transaction must be resumed.
	 * <p>
	 * The "no transaction" context does not support resource enlistment, and
	 * will not commit or rollback any changes, however it does provide a post
	 * completion callback to any registered functions. This function is
	 * suitable for final cleanup, such as closing a connection
	 * 
	 * @param work
	 * @return The value returned by the work
	 * @throws TransactionException if there is an error starting or completing
	 *             the transaction
	 * @throws ScopedWorkException if the supplied work throws an
	 *             {@link Exception}
	 */
	<T> T notSupported(Callable<T> work)
			throws TransactionException, ScopedWorkException;

	/**
	 * The supplied piece of work may run inside or outside the context of a
	 * transaction. If an existing transaction or "no transaction" context is
	 * active then it will continue, otherwise a new "no transaction" context is
	 * associated with the work. After the work has completed any created
	 * transaction context must be completed.
	 * <p>
	 * The "no transaction" context does not support resource enlistment, and
	 * will not commit or rollback any changes, however it does provide a post
	 * completion callback to any registered functions. This function is
	 * suitable for final cleanup, such as closing a connection
	 * 
	 * @param work
	 * @return The value returned by the work
	 * @throws TransactionException if there is an error starting or completing
	 *             the transaction
	 * @throws ScopedWorkException if the supplied work throws an
	 *             {@link Exception}
	 */
	<T> T supports(Callable<T> work)
			throws TransactionException, ScopedWorkException;


}
