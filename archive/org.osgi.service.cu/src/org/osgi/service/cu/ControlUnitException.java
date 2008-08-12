/*
 * $Date$
 * 
 * Copyright (c) OSGi Alliance (2005, 2007). All Rights Reserved.
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
package org.osgi.service.cu;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Custom exception thrown from Control Unit related methods.
 * 
 * It has an error code, defining the type of the error that occurred and 
 * an optional nested exception.
 * 
 * @version $Revision$
 */
public class ControlUnitException extends Exception {

  /**
   * Error code which signals that an undetermined error has occurred. The
   * nested exception should be checked for more information about the
   * actual error.
   */
  public static final int UNDETERMINED_ERROR = 0;

  /**
   * This error code means that the user has tried to invoke 
   * non-existent action on the control unit.
   */
  public static final int NO_SUCH_ACTION_ERROR = 1;

  /**
   * This error code means that the user has tried to read the value
   * of non-existent state variable.
   */
  public static final int NO_SUCH_STATE_VARIABLE_ERROR = 2;

  /**
   * This error code means that the user has invoked an action with 
   * an invalid argument.
   */
  public static final int ILLEGAL_ACTION_ARGUMENTS_ERROR = 3;

  private int errorCode;
  private Throwable nestedException;

  /**
   * Constructs a new control unit exception with the given error code.
   * 
   * @param errorCode the error code
   */
  public ControlUnitException(int errorCode) {
    super();
    this.errorCode = errorCode;
  }

  /**
   * Constructs a new control unit exception with the given message and error
   * code.
   * 
   * @param message the detail message
   * @param errorCode the error code
   */
  public ControlUnitException(String message, int errorCode) {
    super(message);
    this.errorCode = errorCode;
  }

  /**
   * Creates a new exception with the given nested exception.
   * 
   * The error code of the constructed exception will be
   * {@link #UNDETERMINED_ERROR}. The nested exception may be
   * retrieved by the {@link #getNestedException()} method.
   * 
   * @param exception the actual nested exception
   */
  public ControlUnitException(Throwable exception) {
    this(null, exception);
  }

  /**
   * Constructs a new exception with the specified message and assigned nested
   * error.
   * 
   * The error code of the constructed exception will be
   * {@link #UNDETERMINED_ERROR}. The nested exception may be
   * retrieved by the {@link #getNestedException()} method.
   * 
   * @param message detail message
   * @param exception the nested exception
   */
  public ControlUnitException(String message, Throwable exception) {
    this(message, UNDETERMINED_ERROR);
    this.nestedException = exception;
  }

  /**
   * Returns the error code for this control unit exception.
   * 
   * @return The error code
   */
  public int getErrorCode() {
    return errorCode;
  }

  /**
   * Returns the nested exception, if there is any.
   * 
   * @return The nested exception or <code>null</code> if there is no
   *         nested exception
   */
  public Throwable getNestedException() {
    return nestedException;
  }

  /**
   * Prints the stack trace of the nested exception too (if there is one)
   * 
   * @see java.lang.Throwable#printStackTrace()
   */
  public void printStackTrace() {
    if (nestedException != null) {
      synchronized (System.err) {
        super.printStackTrace();
        nestedException.printStackTrace();
      }
    } else {
      super.printStackTrace();
    }
  }

  /**
   * Prints the stack trace of the nested exception too (if there is one)
   * 
   * @param s PrintWriter to use for output.
   * @see java.lang.Throwable#printStackTrace(java.io.PrintWriter)
   */
  public void printStackTrace(PrintWriter s) {
    if (nestedException != null) {
      synchronized (s) {
        super.printStackTrace(s);
        nestedException.printStackTrace(s);
      }
    } else {
      super.printStackTrace(s);
    }
  }

  /**
   * Prints the stack trace of the nested exception too (if there is one)
   * 
   * @param s PrintStream to use for output
   * @see java.lang.Throwable#printStackTrace(java.io.PrintStream)
   */
  public void printStackTrace(PrintStream s) {
    if (nestedException != null) {
      synchronized (s) {
        super.printStackTrace(s);
        nestedException.printStackTrace(s);
      }
    } else {
      super.printStackTrace(s);
    }
  }

    /**
     * Returns a short description of this exception. The description is 
     * a concatenation of <code>String</code> representation of the error code,
     * the error message (if there is one) and - if there is a nested exception - 
     * the result from its <code>toString()</code> method.
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
    StringBuffer buffer = new StringBuffer();
    buffer.append("ControlUnitException[");
    buffer.append(" error = ").append(errorCode);
    switch (errorCode) {
      case UNDETERMINED_ERROR:
        buffer.append("UNDETERMINED_ERROR");
        break;
      case NO_SUCH_ACTION_ERROR:
        buffer.append("NO_SUCH_ACTION_ERROR");
        break;
      case NO_SUCH_STATE_VARIABLE_ERROR:
        buffer.append("NO_SUCH_STATE_VARIABLE_ERROR");
        break;
      case ILLEGAL_ACTION_ARGUMENTS_ERROR:
        buffer.append("ILLEGAL_ACTION_ARGUMENTS_ERROR");
        break;
      default:
        buffer.append(errorCode);
    }
    String msg = super.getMessage();
    if( msg != null ) {
      buffer.append(",message = ").append(msg);
    }
    if (nestedException != null) {
      buffer.append(",nestedException = ").append(nestedException);
    }
    buffer.append("]");
    return buffer.toString();
  }

}
