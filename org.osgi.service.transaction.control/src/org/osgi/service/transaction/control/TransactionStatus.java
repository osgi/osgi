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
 * The status of the transaction
 * 
 * A transaction may not enter all of the states in this enum, however it will always
 * traverse the enum in ascending order. In particular if the TransactionStatus is 
 * reported as X then it will never proceed into a state Y where X.compareTo(Y) >= 0;
 *
 */
public enum TransactionStatus {

	/**
	 * No transaction is currently active
	 */
	NO_TRANSACTION,
	/**
	 * A transaction is currently in progress
	 */
	ACTIVE,
	/**
	 * A transaction is currently in progress and has been marked for rollback
	 */
	MARKED_ROLLBACK,
	/**
	 * A two phase commit is occurring and the transaction is being prepared
	 */
	PREPARING,
	/**
	 * A two phase commit is occurring and the transaction has been prepared
	 */
	PREPARED,
	/**
	 * The transaction is in the process of being committed
	 */
	COMMITTING,
	/**
	 * The transaction has committed
	 */
	COMMITTED,
	/**
	 * The transaction is in the process of rolling back
	 */
	ROLLING_BACK,
	/**
	 * The transaction has been rolled back
	 */
	ROLLED_BACK;
}
