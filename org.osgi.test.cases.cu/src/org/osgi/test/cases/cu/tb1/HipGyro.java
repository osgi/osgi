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

package org.osgi.test.cases.cu.tb1;

import org.osgi.service.cu.ControlUnitException;
import org.osgi.service.cu.admin.spi.CUAdminCallback;
import org.osgi.service.cu.admin.spi.ManagedControlUnit;
import org.osgi.util.measurement.*;

/**
 * It simulates a gyroscope used in a positioning module.
 * 
 * It offers one state variable: 
 * 	short rawOutput
 * 
 * and the following actions:
 * 	void calibrate (Measurement ZRO, Measurement tiltAngle);
 * 	Measurement getZRO();
 *  Measurement getTiltAngle;
 * 	boolean isZROValid();
 * 
 * @version $Revision$
 */
public class HipGyro implements ManagedControlUnit {
	private String id = "hip.gyro.id";
	private String type = "hip.gyro";
	private Measurement ZRO;
	private Measurement tiltAngle;
	private boolean ZROValid;
	private short rawOutput;
	private CUAdminCallback adminCallback;
	
	/**
	 * Create a new HipGyro object.
	 */
	public HipGyro() {
		ZRO = new Measurement(1, Unit.V);
		tiltAngle = new Measurement(1, Unit.rad);
	}

	/**
	 * @param adminCallback
	 * @see org.osgi.service.cu.admin.spi.ManagedControlUnit#setControlUnitCallback(org.osgi.service.cu.admin.spi.CUAdminCallback)
	 */
	public void setControlUnitCallback(CUAdminCallback adminCallback) {
		this.adminCallback = adminCallback;
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
		// short rawOutput
		if (varId == "hip.gyro.rawOutput") {
			rawOutput++;
			return new Short(rawOutput);
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
	public Object invokeAction(String actionId, Object arguments)
			throws ControlUnitException {
		
		// void calibrate (Measurement ZRO, Measurement tiltAngle);
		if (actionId == "hip.gyro.calibrate") {
			if (arguments == null)
				throw (new ControlUnitException(ControlUnitException.ILLEGAL_ACTION_ARGUMENTS_ERROR));
			Object[] args = (Object[])arguments;
			if (	(args[0] instanceof Measurement) &&
					(args[1] instanceof Measurement)){
				ZRO = (Measurement)args[0];
				tiltAngle = (Measurement)args[1];
				ZROValid = true;
				return null;
			}
			else throw (new ControlUnitException(ControlUnitException.ILLEGAL_ACTION_ARGUMENTS_ERROR));
		}
		
		// Measurement getZRO();
		else if (actionId == "hip.gyro.getZRO") {
			if (arguments != null)
				throw (new ControlUnitException(ControlUnitException.ILLEGAL_ACTION_ARGUMENTS_ERROR));
			return ZRO;
		}
		
		// Measurement getTiltAngle;
		else if (actionId == "hip.gyro.getTitleAngle") {
			if (arguments != null)
				throw (new ControlUnitException(ControlUnitException.ILLEGAL_ACTION_ARGUMENTS_ERROR));	
			return tiltAngle;
		}

		// boolean isZROValid();
		else if (actionId == "hip.gyro.isZROValid") {
			if (arguments != null)
				throw (new ControlUnitException(ControlUnitException.ILLEGAL_ACTION_ARGUMENTS_ERROR));	
			return (new Boolean(ZROValid));
		}
		else throw (new ControlUnitException(ControlUnitException.NO_SUCH_ACTION_ERROR));
	}
}
