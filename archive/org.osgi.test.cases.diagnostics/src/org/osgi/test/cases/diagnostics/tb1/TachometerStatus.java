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
package org.osgi.test.cases.diagnostics.tb1;

import org.osgi.service.cu.diag.Status;

/**
 * 
 * @version 1.0
 * @created May 26, 2005
 */
public class TachometerStatus implements Status {
  /**
  * Indicates that there is no error. 
  */
  public static int NO_ERROR = -1;
  
  /**
   * Indicates that the tachometer is not calibrated. 
   */
  public static int CALIBRATE_ERROR = 1;
   
  /**
   * Indicates that the dpp value is not valid. 
   */
  public static int DPP_ERROR = 2;
  
  
  private int errorCode = -1;
  private String errorMessage;
  private String actionId;

  public TachometerStatus(int errorCode, String actionId) {
    this.errorCode = errorCode;
    this.actionId = actionId;
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
    if (errorCode == TachometerStatus.NO_ERROR) {
      errorMessage = "";
      return;
    } else if (errorCode == TachometerStatus.CALIBRATE_ERROR) {
      errorMessage = "The tachometer is not calibrated!";
    } else if (errorCode == TachometerStatus.DPP_ERROR) {
      errorMessage = "The Distance Per Pulse value is not correct!";
    }
    if (actionId != null && !"".equals(actionId)) {
      errorMessage += " Error found by action " + actionId;
    }
  }

  /* 
   * @see org.osgi.service.cu.diag.Status#getMessage()
   */
  public String getMessage() {
    return errorMessage;
  }
}
