/*
 * $Date: 2007-08-10 04:17:58 +0300 $
 *
 * Copyright (c) OSGi Alliance (2000, 2007). All Rights Reserved.
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
package org.osgi.service.transaction;

/**
 * The Transaction Manager service acts as a factory for Transaction objects. It
 * will associate these objects with the current thread.
 * 
 * @version $Revision: 1.0 $
 */
public interface TransactionManager {

  /**
   * Create a new open transaction.
   * 
   * The transaction object is created and its status is set to STATUS_ACTIVE.
   * The current thread will be associated with the transaction so that the
   * current transaction can be retrieved by any resource managers.
   * 
   * @return a new Transaction object in the active state
   * @throws IllegalStateException When there already is an open transaction
   *             associated with the current thread.
   * @throws SecurityException if the called doesn't have the required
   *             permissions
   */
  public Transaction begin();

  /**
   * Answer the transaction associated with the current thread.
   * 
   * @return The currently associated transaction or null.
   * @throws SecurityException if the called doesn't have the required
   *             permissions
   */
  public TransactionContext getTransaction();

  /**
   * Return a list of transactions that were restored after fail-back and
   * associated with the specified resource name. These are not yet committed or
   * rolled-back transactions, since the Transaction Manager deletes them.
   * However, their status could be different, compared to the state just before
   * the power fail occurred.
   * 
   * @param resourceName the <b>class name</b> of the Resource. If this
   *            parameter is <code>null</code>, then all transactions are
   *            listed. This is particularly useful for debugging purpose.
   * @return A list of transactions that were restored or <code>null</code> if
   *         there are no transaction, that are associated with the specified
   *         resource.
   * @throws SecurityException if the called doesn't have the required
   *             permissions
   */
  public TransactionContext[] listTransactions(String resourceName);

  /**
   * This method should be called by a watch-dog to ensure, that the transaction
   * manager will clean up the blocked transactions.
   * 
   * Note, that even if transaction is delayed, the transaction manager may not
   * roll-back it immediately. However, it is certain that timed out transaction
   * is marked as roll-back only.
   * 
   * @return the delay in milliseconds, after which, the timer thread should
   *         check again to timed out transactions. Notice, that this method may
   *         return <code>0</code>, which usually means, that there are no
   *         active transactions. However, if you sleep with 'zero' this means
   *         'sleep forever'. So take care!
   */
  public long checkTimedoutTransactions();

}
