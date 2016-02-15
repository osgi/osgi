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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A builder for a piece of transactional work
 */
public abstract class TransactionBuilder implements TransactionStarter {

	/**
	 * The list of {@link Throwable} types that must trigger rollback
	 */
	protected final List<Class< ? extends Throwable>>	rollbackFor		= new ArrayList<Class< ? extends Throwable>>();
	/**
	 * The list of {@link Throwable} types that must not trigger rollback
	 */
	protected final List<Class< ? extends Throwable>>	noRollbackFor	= new ArrayList<Class< ? extends Throwable>>();

	/**
	 * Declare a list of Exception types (and their subtypes) that <em>must</em>
	 * trigger a rollback. By default the transaction will rollback for all
	 * {@link Exception}s. If a more specific type is registered using
	 * {@link #noRollbackFor(Class, Class...)} then that type will not trigger
	 * rollback. If the same type is registered using both
	 * {@link #rollbackFor(Class, Class...)} and
	 * {@link #noRollbackFor(Class, Class...)} then the transaction
	 * <em>will not</em> begin and will instead throw a
	 * {@link TransactionException}
	 * <p>
	 * Note that the behaviour of this method differs from Java EE and Spring in
	 * two ways:
	 * <ul>
	 * <li>In Java EE and Spring transaction management checked exceptions are
	 * considered "normal returns" and do not trigger rollback. Using an
	 * Exception as a normal return value is considered a <em>bad</em> design
	 * practice. In addition this means that checked Exceptions such as
	 * java.sql.SQLException do not trigger rollback by default. This, in turn,
	 * leads to implementation mistakes that break the transactional behaviour
	 * of applications.</li>
	 * <li>In Java EE it is legal to specify the same Exception type in
	 * {@link #rollbackFor} and {@link #noRollbackFor}. Stating that the same
	 * Exception should both trigger <em>and</em> not trigger rollback is a
	 * logical impossibility, and clearly indicates an API usage error. This API
	 * therefore enforces usage by triggering an exception in this invalid case.
	 * </li>
	 * </ul>
	 * 
	 * @param t
	 * @param throwables The Exception types that should trigger rollback
	 * @return this builder
	 */
	@SafeVarargs
	public final TransactionBuilder rollbackFor(Class< ? extends Throwable> t,
			Class< ? extends Throwable>... throwables) {
		Objects.requireNonNull(t,
				"The supplied exception types must be non Null");
		for (Class< ? extends Throwable> t2 : throwables) {
			Objects.requireNonNull(t2,
					"The supplied exception types must be non-null");
		}
		rollbackFor.clear();
		rollbackFor.add(t);
		rollbackFor.addAll(Arrays.asList(throwables));
		return this;
	}

	/**
	 * Declare a list of Exception types (and their subtypes) that
	 * <em>must not</em> trigger a rollback. By default the transaction will
	 * rollback for all {@link Exception}s. If an Exception type is registered
	 * using this method then that type and its subtypes will <em>not</em>
	 * trigger rollback. If the same type is registered using both
	 * {@link #rollbackFor(Class, Class...)} and
	 * {@link #noRollbackFor(Class, Class...)} then the transaction
	 * <em>will not</em> begin and will instead throw a
	 * {@link TransactionException}
	 * <p>
	 * Note that the behaviour of this method differs from Java EE and Spring in
	 * two ways:
	 * <ul>
	 * <li>In Java EE and Spring transaction management checked exceptions are
	 * considered "normal returns" and do not trigger rollback. Using an
	 * Exception as a normal return value is considered a <em>bad</em> design
	 * practice. In addition this means that checked Exceptions such as
	 * java.sql.SQLException do not trigger rollback by default. This, in turn,
	 * leads to implementation mistakes that break the transactional behaviour
	 * of applications.</li>
	 * <li>In Java EE it is legal to specify the same Exception type in
	 * {@link #rollbackFor} and {@link #noRollbackFor}. Stating that the same
	 * Exception should both trigger <em>and</em> not trigger rollback is a
	 * logical impossibility, and clearly indicates an API usage error. This API
	 * therefore enforces usage by triggering an exception in this invalid case.
	 * </li>
	 * </ul>
	 * 
	 * @param t An exception type that should not trigger rollback
	 * @param throwables further exception types that should not trigger
	 *            rollback
	 * @return this builder
	 */
	@SafeVarargs
	public final TransactionBuilder noRollbackFor(Class< ? extends Throwable> t,
			Class< ? extends Throwable>... throwables) {

		Objects.requireNonNull(t,
				"The supplied exception types must be non Null");
		for (Class< ? extends Throwable> t2 : throwables) {
			Objects.requireNonNull(t2,
					"The supplied exception types must be non-null");
		}
		noRollbackFor.clear();
		noRollbackFor.add(t);
		noRollbackFor.addAll(Arrays.asList(throwables));
		return this;
	}
	
	/**
	 * Indicate to the Transaction Control service that this transaction
	 * will be read-only. This hint may be used by the Transaction Control
	 * service and associated resources to optimise the transaction.
	 * 
	 * <p>
	 * Note that this method is for optimisation purposes only. The TransactionControl
	 * service is free to ignore the call if it does not offer read-only optimisation. 
	 * 
	 * <p>
	 * If a transaction is marked read-only and then the scoped work performs a write
	 * operation on a resource then this is a programming error. The resource is
	 * free to raise an exception when the write is attempted, or to permit the write 
	 * operation. As a result the transaction may commit successfully, or may rollback.
	 * 
	 * @return this builder
	 */
	public abstract TransactionBuilder readOnly();
}
