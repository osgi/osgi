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

import org.osgi.service.cu.ControlUnitException;
import org.osgi.service.cu.admin.spi.CUAdminCallback;
import org.osgi.service.cu.diag.DiagnosableControlUnit;
import org.osgi.service.cu.diag.Status;
import org.osgi.test.cases.diagnostics.tb1.TachometerStatus;

/**
* Represents a CU on a door. 
*
* @version $Revision$
*/
class Door implements DiagnosableControlUnit {
  private String type = "door";
  private String id;
  private byte state;
  private CUAdminCallback adminCallback;
  
  public byte CLOSED = 1;
  public byte OPENED = 2;
  
  /**
   * Create a new Door object
   * 
   * @param
   * @param
   * 
   */
  public Door(String id, CUAdminCallback adminCallback) {
    this.adminCallback = adminCallback;
    this.id = id;
  }
  
  public byte getState() {
    return state;
  }
  
  public void open() {
    state = OPENED;
  }
  
  public void close() {
    state = CLOSED;
  }

  /**
   * @return
   * @see org.osgi.service.cu.ControlUnit#getId()
   */
  public String getId() {
    return id;
  }

  /**
   * @return
   * @see org.osgi.service.cu.ControlUnit#getType()
   */
  public String getType() {
    return type;
  }

  /**
   * @param varId
   * @return
   * @throws Exception
   * @see org.osgi.service.cu.ControlUnit#queryStateVariable(java.lang.String)
   */
  public Object queryStateVariable(String varId) throws ControlUnitException {    
    if (varId == "state")
      return new Byte (getState());
    else throw (new ControlUnitException(ControlUnitException.NO_SUCH_STATE_VARIABLE_ERROR));
  }

  /**
   * @param actionId
   * @param arguments
   * @return
   * @throws Exception
   * @see org.osgi.service.cu.ControlUnit#invokeAction(java.lang.String, java.lang.Object)
   */
  public Object invokeAction(String actionId, Object arguments) throws ControlUnitException {
    
    // The following actions can be invoked on a door
    // void open()
    // void close()
    if ((actionId == "door.open") && (arguments == null)) {
      if (state == CLOSED) {
        open();
        adminCallback.stateVariableChanged(type, id, "state", new Byte(state));
      }
      return null;
    }
    else if ((actionId == "door.close") && (arguments == null)){
      if (state == OPENED) {
        close();
        adminCallback.stateVariableChanged(type, id, "state", new Byte(state));
      }
      return null;
    }
    else throw (new ControlUnitException(ControlUnitException.NO_SUCH_ACTION_ERROR));
  }
  
  /* 
   * @see org.osgi.service.cu.diag.DiagnosableControlUnit#checkStatus()
   */
  public Status checkStatus() throws ControlUnitException {
    if (state == OPENED) {
      return new DoorStatus(DoorStatus.OPEN_ERROR);
    }
    return new DoorStatus(DoorStatus.NO_ERROR);
  }
}
