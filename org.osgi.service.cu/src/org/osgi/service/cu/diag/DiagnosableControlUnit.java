/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package org.osgi.service.cu.diag;

import org.osgi.service.cu.ControlUnit;
import org.osgi.service.cu.ControlUnitException;

/**
 * Describes a device in terms of actions that can be invoked on the device and
 * variable that can retrieved from a device.
 * 
 * @version $Revision$
 */
public interface DiagnosableControlUnit extends ControlUnit
{
	   
   /**
    * Performs a complete diagnostic on the device. It is up to the device to decide
    * what are the actions that must be performed to check if the device is correctly running.
    * The result is given by the status.
    * 
    * @return The status of the method
    * @throws ControlUnitException if error prevents the execution of the action.
    * {@link ControlUnitException#getErrorCode()}
    * and {@link ControlUnitException#getNestedException()} methods can be used 
    * to determine the actual cause.
   	*/
   	public Status checkStatus() throws ControlUnitException;
}
