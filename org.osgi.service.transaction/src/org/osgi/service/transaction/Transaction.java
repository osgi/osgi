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
 * A Transaction object can be obtained from the TransactionManager with the
 * createTransaction() method. The returned transaction is open and associated
 * with the current thread. (getCurrentTransaction() will return it). However,
 * the object's methods may be called in other threads as well.
 * 
 * The transaction object is created by client code. When the client code calls
 * other subsystems, the subsystems can join the transaction. joining the
 * transaction means they can affect the outcome of the transaction.
 * 
 * Once the client is finished, it should call commit if all the changes were
 * successful, or call roll-back if there were problems. The commit will persist
 * the changes while roll-back will undo all the changes since the beginning of
 * the transaction.
 * 
 * A transaction will pass through a well defined set of states.
 * 
 * @version $Revision: 1.0 $
 */
public interface Transaction extends TransactionContext {

  /**
   * Complete the transaction associated with the current thread.
   * 
   * If the status is {@link #STATUS_MARKED_ROLLBACK}, this transaction must be
   * rolled back.
   * 
   * If the status is anything else but {@link #STATUS_ACTIVE}, an
   * IllegalStateException is thrown.
   * 
   * The status is set to {@link #STATUS_PREPARING}
   * 
   * Call all participating Resource Managers with the prepare method in no
   * particular order. The transaction manager may use multiple threads to call
   * the different resource managers. If any of them throw an exception, perform
   * a roll-back and return false. In that case, the remaining prepare methods
   * should not be called.
   * 
   * The status is set to {@link #STATUS_PREPARED} The status is set to
   * {@link #STATUS_COMMITTING} Call all participating Resource Managers with
   * the commit method. If a resource manager fails the commit (exception,
   * timeout), continue with the remaining resource managers and log the
   * offending resource managers.
   * 
   * The status is set to {@link #STATUS_COMMITTED} if all commits are
   * successful.
   * 
   * Before returning, the transaction will release all the locks held by this
   * transaction.
   * 
   * @throws SecurityException Thrown to indicate that the thread is not allowed
   *             to commit the transaction.
   * @throws TransactionException if the transaction was rolled back, instead of
   *             committed
   */
  public void commit() throws TransactionException;

  /**
   * Roll back the transaction associated with the current thread.
   * 
   * 
   * The status is set to {@link #STATUS_ROLLING_BACK}
   * 
   * Call the {@link TransactionResource#rollback(TransactionContext)} method on
   * all participating resource managers. Exceptions should be logged but may
   * not block calling the other resource managers.
   * 
   * The status is set to {@link #STATUS_ROLLEDBACK}
   * 
   * @throws SecurityException Thrown to indicate that the thread is not allowed
   *             to commit the transaction.
   */
  public void rollback();

  /**
   * Modify the timeout value that is associated with transactions started by
   * subsequent invocations of the begin method.
   * 
   * If an application has not called this method, the transaction service uses
   * some default value for the transaction timeout.
   * 
   * @param seconds - The value of the timeout in seconds. If the value is zero,
   *            the transaction service restores the default value.
   * @throws IllegalArgumentException if the seconds parameter has negative
   *             value
   */
  public void setTransactionTimeout(int seconds);

}
