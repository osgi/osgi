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

/**
 * Custom exception throw from some control unit related methods.<BR>
 * 
 * It has an error code, defining the type of error which occured, and an
 * optional application exception.
 *  
 * @version $Revision$
 */
public class ControlUnitException extends Exception {
  
  /**
   * Error code which signals that an undetermined error has occured. 
   * The application exception should be checked for more information about 
   * the actual error.
   */
  public static final int UNDETERMINED_APPLICATION_ERROR = 0;
  
  /**
   * Error code which signals that the invocation of an attempt to invoke an 
   * non-existing method was made.
   */
  public static final int NO_SUCH_ACTION_ERROR = 1;
  
  /**
   * Error code which signals that an attempt to query the value of an 
   * non-existing state variable was made.
   */
  public static final int NO_SUCH_STATE_VARIABLE_ERROR = 2;
  
  /**
   * Error code which signals that an attempt to invoke action with illegal
   * arguments was made.
   */
  public static final int ILLEGAL_ACTION_ARGUMENTS_ERROR = 3;
  
  private int errorCode;
  private Exception applicationException;
  
  /**
   * Constructs a new control unit exception with the given error code.
   * 
   * @param errorCode the error code
   */
  public ControlUnitException(int errorCode) {
    this.errorCode = errorCode;
  }
  
  /**
   * Constructs a new control unit exception with the given message and error code.
   * 
   * @param message the detail message
   * @param errorCode the error code
   */
  public ControlUnitException(String message, int errorCode) {
    super(message);
    
    this.errorCode = errorCode;
  }
  
  /**
   * Constructs a new undetermined error control unit exception with the given 
   * application exception. <BR>
   * 
   * The error code of the constructed exception will be {@link #UNDETERMINED_APPLICATION_ERROR}.
   * The application exception may be retrieved by the {@link #getApplicationException()} method.
   * 
   * @param exception the actual application exception
   */
  public ControlUnitException(Exception exception) {
    this(null, exception);
  }
  
  /**
   * Constructs a new undetermined application error exception with the given message 
   * and exception. <BR>
   * 
   * The error code of the constructed exception will be {@link #UNDETERMINED_APPLICATION_ERROR}.
   * The application exception may be retrieved by the {@link #getApplicationException()} method. 
   * 
   * @param message detail message
   * @param exception the actual application exception
   */
  public ControlUnitException(String message, Exception exception) {
    this(message, UNDETERMINED_APPLICATION_ERROR);
    
    this.applicationException = exception;
  }
  
  /**
   * Returns the error code for this control unit exception.
   * 
   * @return error code
   */
  public int getErrorCode() {
    return errorCode;
  }
  
  /**
   * Returns the application exception, if any, for this control unit exception.
   *  
   * @return application exception or <code>null</code> if there is no application exception
   */
  public Exception getApplicationException() {
    return applicationException;
  }

}
