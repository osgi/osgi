/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2000, 2004). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.service.cu;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Custom exception thrown from some control unit related methods.<BR>
 * 
 * It has an error code, defining the type of error, which occurred, and an
 * optional nested exception.
 * 
 * @version $Revision$
 */
public class ControlUnitException extends RuntimeException {

  /**
   * Error code which signals that an undetermined error has occurred. The
   * nested exception should be checked for more information about the
   * actual error.
   */
  public static final int UNDETERMINED_ERROR = 0;

  /**
   * This error code means that the user has tried to invoke non-existent
   * action of the unit.
   */
  public static final int NO_SUCH_ACTION_ERROR = 1;

  /**
   * This error code means, that the user has tried to read the value
   * of non-existent state variable.
   */
  public static final int NO_SUCH_STATE_VARIABLE_ERROR = 2;

  /**
   * This error code means, that the user has supplied an invalid argument
   * for action invocation.
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
   * Creates a new exception with assigned nested error.<br>
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
   * error.<br>
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

    /* (non-Javadoc)
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
