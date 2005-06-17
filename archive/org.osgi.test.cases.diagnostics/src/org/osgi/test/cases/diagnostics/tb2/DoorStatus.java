/*
 * $Header$
 * 
 * Copyright (c) The OSGi Alliance (2005). All Rights Reserved.
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
package org.osgi.test.cases.diagnostics.tb2;

import org.osgi.service.cu.diag.Status;

/**
*
* @version $Revision$
*/
public class DoorStatus implements Status {
  /**
   * Indicates that there is no error. 
   */
   public static int NO_ERROR = -1;
   
   /**
    * Indicates that the door is open. 
    */
   public static int OPEN_ERROR = 1;
  
  private int errorCode;
  private String errorMessage;

  public DoorStatus(int errorCode) {
    this.errorCode = errorCode;
    setMessage();
  }

  /* 
   * @see org.osgi.service.cu.diag.Status#getStatus()
   */
  public byte getStatus() {
    if (errorCode != -1) {
      return (byte) Status.STATUS_FAILED;
    }
    return (byte) Status.STATUS_OK;
  }

  /* 
   * @see org.osgi.service.cu.diag.Status#getError()
   */
  public int getError() {
    return errorCode;
  }

  private void setMessage() {
    if (errorCode == DoorStatus.NO_ERROR) {
      errorMessage = "";
      return;
    } else if (errorCode == DoorStatus.OPEN_ERROR) {
      errorMessage = "The door is open!";
    }
  }

  /* 
   * @see org.osgi.service.cu.diag.Status#getMessage()
   */
  public String getMessage() {
    return errorMessage;
  }
}
