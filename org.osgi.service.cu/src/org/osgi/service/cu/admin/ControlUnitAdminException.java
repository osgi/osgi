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
 * {@link org.osgi.service.cu.ControlUnitException} which
 * provides additional error codes 
 * related with the operating over control units through the 
 * {@link org.osgi.service.cu.admin.ControlUnitAdmin} and 
 * {@link org.osgi.service.cu.admin.spi.ControlUnitFactory ControlUnitFactories}.
 *   
 * @version $Revision$
 */
public class ControlUnitAdminException extends ControlUnitException {

  /**
   * Error code which signals that an attempt to perfom and operation over
   * a non-existing unit was made. 
   */
  public static final int NO_SUCH_CONTROL_UNIT_ERROR = 5;
  
  /**
   * Error code which signals that an attempt to create control unit via 
   * a non-existing constructor was made.
   */
  public static final int NO_SUCH_CONSTUCTOR_ERROR = 7;

  /**
   * Error code which signals that an attempt to perform a search with a 
   * non-existing finder was made.
   */
  public static final int NO_SUCH_FINDER_ERROR = 8;
  
  /**
   * Error code which signals that an attempt to create a control unit of 
   * a type which does not support control unit creation was made.
   */
  public static final int CREATION_NOT_SUPPORTED_ERROR = 9;
  
  /**
   * Error code which signals that an attempt to destoy a control unit of 
   * a type which does not support control unit destruction was made.
   */
  public static final int DESTRUCTION_NOT_SUPPORTED_ERROR = 10;

  /**
   * Error code which signals that  an attempt to perform a search opperation
   * over a control unit type which does not support searching was made.
   */
  public static final int SEARCHING_NOT_SUPPORTED_ERROR = 11;
    
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
   * application exception. <BR>
   * 
   * The error code of the constructed exception will be {@link #UNDETERMINED_APPLICATION_ERROR}.
   * The application exception may be retrieved by the {@link #getApplicationException()} method.
   * 
   * @param exception the actual application exception
   */
  public ControlUnitAdminException(Exception exception) {
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
  public ControlUnitAdminException(String message, Exception exception) {
    super(message, exception);
  }
  
}
