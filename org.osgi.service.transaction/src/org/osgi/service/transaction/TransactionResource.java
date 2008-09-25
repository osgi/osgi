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
 * A TransactionResource represents a participant in a transaction.
 * 
 * When a subsystem is used and it can participate in a transaction, it should
 * register itself with the current by calling the join method.
 * 
 * The resource manager should carefully lock its objects to provide the
 * appropriate guarantees.
 * 
 * At the end of a transaction, the transaction manager will call-back the
 * resource manager and ask for the outcome of this transaction with the
 * {@link #prepare(TransactionContext)} method.
 * 
 * If the resource manager has not experienced any problems for this
 * transaction, it should silently return. If the transaction could not be
 * persisted, it should throw an appropriate exception.
 * 
 * After all the participating resource managers have been consulted, the
 * transaction manager will call {@link #commit(TransactionContext)} to make the
 * changes persistent or {@link #rollback(TransactionContext)} to undo all the
 * changes that were made since the transaction started.
 * 
 * @version $Revision: 1.0 $
 */
public interface TransactionResource {

  /**
   * Prepare for the changes to be made persistent, no further changes in this
   * transaction will be made. This method should verify any constraints that
   * could not have been checked before and throw an exception if anything is
   * not correct.
   * 
   * This method may be called on another thread then the thread associated with
   * the transaction to speed up the 2-phase commit. I.e. the getCurrentThread()
   * may return null.
   * 
   * @param transaction The associated transaction.
   * @throws Exception When there is an inconsistency or other error, the
   *             exception will reflect this. An exception indicates that the
   *             transaction will be rolled back. The
   *             {@link #rollback(TransactionContext)} method will still be
   *             called.
   */
  public void prepare(TransactionContext transaction) throws Exception;

  /**
   * Undo all the changes made to this subsystem that are associated with the
   * given transaction. The method may be called on another thread than the one
   * associated with the transaction object.
   * 
   * @param transaction The associated transaction.
   */
  public void rollback(TransactionContext transaction);

  /**
   * Commit all the changes associated with the given transaction.
   * 
   * @param transaction
   */
  public void commit(TransactionContext transaction);

}
