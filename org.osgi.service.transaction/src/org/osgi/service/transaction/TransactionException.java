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

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * TransactionException exception is thrown when the transaction has been marked
 * for rollback only or the transaction has been rolled back instead of
 * committed. This is a local exception thrown by methods in the
 * {@link Transaction} interface.
 * 
 * @version $Revision: 1.0 $
 */
public class TransactionException extends Exception {
  
  private static final long serialVersionUID = -640019191675713475L;

/**
   * Undefined error code
   */
  public static final int UNDEFINED = 0;

  /**
   * This error code is assigned, when the {@link Transaction#commit()} method
   * is invoked and Transaction is assigned to more than one thread.
   */
  public static final int THREADS_NOT_FINISHED_ERROR = 1;

  /**
   * This error code is assigned, when the {@link Transaction#commit()} method
   * is invoked and Transaction is not in active state, or was marked as
   * rollback only.
   * 
   * In this case, the application may check
   * {@link TransactionContext#getStatus()} method.
   */
  public static final int STATE_ERROR = 2;

  /**
   * A transaction resource has failed during the 1st, prepare phase of the
   * commit process.
   * 
   * @see TransactionResource#prepare(TransactionContext)
   */
  public static final int RESOURCE_ERROR = 4;

  /**
   * A transaction resource has failed during the 2nd, commit phase, of the
   * commit process. This is typically a {@link RuntimeException}
   * TransactionResource#commit(TransactionContext)
   */
  public static final int HEURISTICS_COMMIT_ERROR = 8;

  /**
   * A transaction resource has failed during rollback. This is typically a
   * {@link RuntimeException}
   * 
   * @see TransactionResource#rollback(TransactionContext)
   */
  public static final int HEURISTICS_ROLLBACK_ERROR = 16;

  /**
   * A transaction cannot be committed because it has expired.
   */
  public static final int TIMEOUT_ERROR = 32;

  private Throwable nested;
  private int errorCode;

  /**
   * Creates new transaction exception
   */
  public TransactionException() {
    this(null, null, UNDEFINED);
  }

  /**
   * Creates a new transaction exception with the specified error code.
   * 
   * @param errorCode the error code
   */
  public TransactionException(int errorCode) {
    this(null, null, errorCode);
  }

  /**
   * Creates new transaction exception, with specified error message
   * 
   * @param message description of the error
   */
  public TransactionException(String message) {
    this(message, null, UNDEFINED);
  }

  /**
   * Creates new transaction exception, with specified error message and nested
   * cause
   * 
   * @param message description of the error
   * @param nested the nested cause
   */
  public TransactionException(String message, Throwable nested) {
    this(message, nested, UNDEFINED);
  }

  /**
   * Creates new transaction exception, with specified error message and nested
   * cause. Also assigns an error code.
   * 
   * @param message description of the error
   * @param nested the nested cause
   * @param errorCode the error code
   */
  public TransactionException(String message, Throwable nested, int errorCode) {
    super(message != null ? message : getString(errorCode));
    this.nested = nested;
    this.errorCode = errorCode;
  }

  /**
   * Used to obtain the nested exception
   * 
   * @return the nested exception
   */
  public Throwable getNestedException() {
    return nested;
  }

  /**
   * Used to obtain the error code
   * 
   * @return the error code
   */
  public int getErrorCode() {
    return errorCode;
  }

  /**
   * @see java.lang.Throwable#printStackTrace(java.io.PrintStream)
   */
  public void printStackTrace(PrintStream out) {
    synchronized (out) {
      super.printStackTrace(out);
      if (nested != null) nested.printStackTrace(out);
    }
  }

  /**
   * @see java.lang.Throwable#printStackTrace(java.io.PrintWriter)
   */
  public void printStackTrace(PrintWriter out) {
    synchronized (out) {
      super.printStackTrace(out);
      if (nested != null) nested.printStackTrace(out);
    }
  }

  private static final String getString(int code) {
    switch (code) {
      case THREADS_NOT_FINISHED_ERROR:
        return "Only the owner thread must be assigned to the transaction before committing or rollback";
      case STATE_ERROR:
        return "The operation cannot be performed at this state of the transaction";
      case RESOURCE_ERROR:
        return "A resource processor failed with error during prepare phase";
      case HEURISTICS_COMMIT_ERROR:
        return "A resource processor failed during commit";
      case HEURISTICS_ROLLBACK_ERROR:
        return "A resource processor failed during rollback";
      case TIMEOUT_ERROR:
        return "The transaction has timed out and is automatically rolled back";
      default:
        return "Transaction error!";
    }
  }

}
