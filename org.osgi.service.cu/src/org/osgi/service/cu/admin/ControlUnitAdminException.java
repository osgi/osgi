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
package org.osgi.service.cu.admin;

import org.osgi.service.cu.ControlUnitException;

/**
 * <code>ControlUnitAdminException</code> is a subclass of 
 * {@link org.osgi.service.cu.ControlUnitException}, which
 * provides additional error codes 
 * related with the operating over control units through the 
 * {@link org.osgi.service.cu.admin.ControlUnitAdmin} and 
 * {@link org.osgi.service.cu.admin.spi.ControlUnitFactory ControlUnitFactories}.
 *   
 * @version $Revision$
 */
public class ControlUnitAdminException extends ControlUnitException {

  /**
   * This error code means that the user tried to perform an operation
   * over non-existent control unit.
   */
  public static final int NO_SUCH_CONTROL_UNIT_ERROR = 4;
  
  /**
   * This error code means that the user attempted to create a control
   * unit, but its factory doesn't provide the requested constructor
   * method.
   */
  public static final int NO_SUCH_CONSTUCTOR_ERROR = 5;

  /**
   * This error code means that the user tried to perform a search, but
   * the there is not finder method that matches the given finder id.
   */
  public static final int NO_SUCH_FINDER_ERROR = 6;

  /**
   * This error code means that someone has tried to create a control
   * unit dynamically, but it's factory doesn't provide any constructor
   * methods.
   */
  public static final int CREATION_NOT_SUPPORTED_ERROR = 7;

  /**
   * This error code means that the user has tried to destroy a control unit
   * which factory doesn't define a destructor method.
   */
  public static final int DESTRUCTION_NOT_SUPPORTED_ERROR = 8;

  /**
   * This error code means that the user has tried to search for a control unit
   * but a finder method(s) are not defined.
   */
  public static final int SEARCHING_NOT_SUPPORTED_ERROR = 9;
  
  /**
   * This error code means that the user tried to perform an operation
   * over non-existent control unit type.
   */
  public static final int NO_SUCH_CONTROL_UNIT_TYPE_ERROR = 10;
    
  /**
   * Constructs a new control unit exception with the given error code.
   * 
   * @param errorCode the error code
   */
  public ControlUnitAdminException(int errorCode) {
    super(errorCode);
  }
  
  /**
   * Constructs a new control unit exception with the given message and error code.
   * 
   * @param message the detail message
   * @param errorCode the error code
   */
  public ControlUnitAdminException(String message, int errorCode) {
    super(message, errorCode);
  }
  
  /**
   * Constructs a new undetermined error control unit exception with the given 
   * nested exception. <BR>
   * 
   * The error code of the constructed exception will be {@link #UNDETERMINED_ERROR}.
   * The nested exception may be retrieved by the {@link #getNestedException()} method.
   * 
   * @param exception the nested exception
   */
  public ControlUnitAdminException(Throwable exception) {
    this(null, exception);
  }
  
  /**
   * Constructs a new undetermined error exception with the given message 
   * and exception. <BR>
   * 
   * The error code of the constructed exception will be {@link #UNDETERMINED_ERROR}.
   * The nested exception may be retrieved by the {@link #getNestedException()} method. 
   * 
   * @param message detail message
   * @param exception the nested exception
   */
  public ControlUnitAdminException(String message, Throwable exception) {
    super(message, exception);
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  public String toString() {
    int errorCode = getErrorCode();
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
      case NO_SUCH_CONTROL_UNIT_ERROR:
        buffer.append("NO_SUCH_CONTROL_UNIT_ERROR");
        break;
      case  NO_SUCH_CONSTUCTOR_ERROR:
        buffer.append("NO_SUCH_CONSTUCTOR_ERROR");
        break;
      case NO_SUCH_FINDER_ERROR:
        buffer.append("NO_SUCH_FINDER_ERROR");
        break;
      case CREATION_NOT_SUPPORTED_ERROR:
        buffer.append("CREATION_NOT_SUPPORTED_ERROR");
        break;
      case DESTRUCTION_NOT_SUPPORTED_ERROR:
        buffer.append("DESTRUCTION_NOT_SUPPORTED_ERROR");
        break;
      case SEARCHING_NOT_SUPPORTED_ERROR:
        buffer.append("SEARCHING_NOT_SUPPORTED_ERROR");
        break;
      default:
        buffer.append(errorCode);
    }
    String msg = getMessage();
    if( msg != null ) {
      buffer.append(",message = ").append(msg);
    }
    Throwable nestedException = getNestedException();
    if (nestedException != null) {
      buffer.append(",nestedException = ").append(nestedException);
    }
    buffer.append("]");
    return buffer.toString();
  }
  
}
