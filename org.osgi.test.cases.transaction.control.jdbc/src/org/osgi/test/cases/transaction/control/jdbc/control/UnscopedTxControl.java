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

import java.util.concurrent.Callable;

import org.osgi.service.transaction.control.ScopedWorkException;
import org.osgi.service.transaction.control.TransactionBuilder;
import org.osgi.service.transaction.control.TransactionContext;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.TransactionException;
import org.osgi.service.transaction.control.TransactionRolledBackException;

public class UnscopedTxControl implements TransactionControl {

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
		return false;
	}

	@Override
	public TransactionContext getCurrentContext() {
		return null;
	}

	@Override
	public boolean getRollbackOnly() throws IllegalStateException {
		throw new IllegalStateException("No Scope!");
	}

	@Override
	public void setRollbackOnly() throws IllegalStateException {
		throw new IllegalStateException("No Scope!");
	}

	@Override
	public void ignoreException(Throwable t) throws IllegalStateException {
		throw new IllegalStateException("No Scope!");
	}
}
