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
