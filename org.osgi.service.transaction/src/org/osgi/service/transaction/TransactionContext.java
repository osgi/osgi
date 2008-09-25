/*
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
 * The TransactionContext provides methods, that allows the Transaction
 * Resources to join and participate in the current transaction. It also
 * prevents them from performing full commit or rollback.
 * 
 * @version $Revision: 1.0 $
 */
public interface TransactionContext {

  /**
   * A transaction is associated with the target object but its current status
   * cannot be determined. This is a transient condition and a subsequent
   * invocation will ultimately return a different status.
   */
  public final static int STATUS_UNKNOWN = 0;

  /**
   * Directly after it is created the status is ACTIVE.
   */
  public final static int STATUS_ACTIVE = 1;

  /**
   * During the second phase
   */
  public final static int STATUS_COMMITTING = 2;

  /**
   * After the transaction is ended and has ended successfully.
   */
  public final static int STATUS_COMMITTED = 4;

  /**
   * Any participant of the transaction can mark the transaction
   * setRollbackOnly. Once this is set, the transaction may continue but a final
   * commit will become a roll-back. The setRollbackOnly method will set the
   * status to STATUS_MARKED_ROLLBACK.
   */
  public final static int STATUS_MARKED_ROLLBACK = 8;

  /**
   * No transaction is currently associated with the target object. This will
   * occur after a transaction has completed.
   */
  public final static int STATUS_NO_TRANSACTION = 16;

  /**
   * After the first phase of 2-phase commit, just before STATUS_COMMITTING
   */
  public final static int STATUS_PREPARED = 32;

  /**
   * During the first phase of the 2-phase commit.
   */
  public final static int STATUS_PREPARING = 64;

  /**
   * After the transaction is rolled back. This is a final state, no more
   * changes are possible.
   */
  public final static int STATUS_ROLLEDBACK = 128;

  /**
   * During the roll back phase.
   */
  public final static int STATUS_ROLLING_BACK = 256;

  /**
   * Obtain the status of the transaction associated with the current thread.
   * 
   * @return the status of the transaction
   */
  public int getStatus();

  /**
   * Modify the transaction associated with the current thread such that the
   * only possible outcome of the transaction is to roll back the transaction.
   */
  public void setRollbackOnly();

  /**
   * A resource manager that wants to participate in the transaction should
   * register with this transaction through this method.
   * 
   * @param tr The associated transaction resource manager
   * @return true if this is the first time, or false if it is already
   *         registered
   */
  public boolean join(TransactionResource tr);

  /**
   * This method will reset the timeout. This is a little bit like a watch-dog,
   * that will prevent transactions from being locked. The creator of the
   * transaction sets the maximum timeout, allowed for a step to execute. When
   * step is executed, it resets the timeout.
   */
  public void resetTimeout();

  /**
   * This method is used to query the PID of the transaction.
   * 
   * @return the transaction PID.
   */
  public long getPid();

  /**
   * This method allows a resource to associate the current transaction to
   * another thread. Notice, that this will not unbind the transaction from the
   * current tread.
   * 
   * @param thread the thread, to which the transaction should be associated.
   * @return <code>true</code> if association is succesfull or
   *         <code>false</code> if the target thread, already has an
   *         associated transaction.
   * @throws TransactionException If the transaction is not in the active state
   */
  public boolean associateThread(Thread thread) throws TransactionException;

  /**
   * This method disassociates the current thread from the transaction.
   * 
   * @return <code>true</code> if the thread was associated to the
   *         transaction, <code>false</code> - if not.
   */
  public boolean disassociateThread();
  
  /**
   * This method waits all threads except one to disassociate from the transaction.
   * 
   * @param timeout the timeout within the threads must disassociate in milliseconds.
   * @return <code>true</code> if the threads disassociated within the given
   * 				 timeout, <code>false</code> - if not.
   */
  public boolean waitThreadsToFinish(int timeout);

}
