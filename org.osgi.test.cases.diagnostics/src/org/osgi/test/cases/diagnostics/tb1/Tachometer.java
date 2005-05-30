/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
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

import org.osgi.service.cu.ControlUnitException;
import org.osgi.service.cu.diag.DiagnosableControlUnit;
import org.osgi.service.cu.diag.Status;

/**
 *
 * Represents information given by the tachometer.
 * 
 * The tachometer gives a raw output expressed in pulses/second.
 * It can be calibrated with a specific dpp (distance per pulse).
 * 
 * It offers the following variable: 
 *  byte dpp (distance per pulse)
 * 	byte rawOutput (pulses/second)
 * 
 * and the following actions:
 * 	void calibrate (byte dpp);
 *  boolean isCalibrated();
 * 	byte getDpp();
 * 	boolean isDppValid();
 * 
 * @version $Revision$
 */
public class Tachometer implements DiagnosableControlUnit {
	private String id = "tachometer.id";
	private String type = "tachometer";
	private byte dpp;
	private byte rawOutput;
	private boolean calibrated = false;

	/**
	 * Create a new Tachometer
	 */
	public Tachometer() {
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
    //dpp
    if (varId == "tachometer.dpp") {
      return new Byte(dpp);
    }
    // rawOutput
		else if (varId == "tachometer.rawOutput") {
			rawOutput++;
			return new Byte(rawOutput);
		}
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
		//void calibrate(byte dpp)
		if (actionId == "tachometer.calibrate") {
			if (arguments == null || ! (arguments instanceof Byte)) {
				throw (new ControlUnitException(ControlUnitException.ILLEGAL_ACTION_ARGUMENTS_ERROR));
      }
			dpp = ((Byte)arguments).byteValue();
			calibrated = true;
			return null;			
		}
		
		//byte getDpp()
		else if (actionId == "tachometer.getDpp") {
			if (arguments != null) {
				throw (new ControlUnitException(ControlUnitException.ILLEGAL_ACTION_ARGUMENTS_ERROR));
      }
			return (new Byte(dpp));
		}
    
    //boolean isCalibrated()
    else if (actionId == "tachometer.isCalibrated") {
      if (arguments != null) {
        throw (new ControlUnitException(ControlUnitException.ILLEGAL_ACTION_ARGUMENTS_ERROR));
      }
      return (new Boolean(isCalibrated()));
    }
		
		//boolean isDppValid()
		else if (actionId == "tachometer.isDppValid") {
			if (arguments != null) {
				throw (new ControlUnitException(ControlUnitException.ILLEGAL_ACTION_ARGUMENTS_ERROR));
      }
			return (new Boolean(isDppValid()));
		}
    
		else  {
      throw (new ControlUnitException(ControlUnitException.NO_SUCH_ACTION_ERROR));
    }
	}
  
  private boolean isCalibrated() {
    return calibrated;
  }
  
  private boolean isDppValid() {
    return dpp > 0;
  }
  
  /* 
   * @see org.osgi.service.cu.diag.DiagnosableControlUnit#checkStatus()
   */
  public Status checkStatus() throws ControlUnitException {
    if (!calibrated) {
      return new TachometerStatus(TachometerStatus.CALIBRATE_ERROR, "tachometer.isCalibrated");
    }
    if (!isDppValid()) {
      return new TachometerStatus(TachometerStatus.DPP_ERROR, "tachometer.isDppValid");
    }
    return new TachometerStatus(TachometerStatus.NO_ERROR, "");
  }
}
