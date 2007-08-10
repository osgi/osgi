/*
 * $Date$
 * 
 * Copyright (c) OSGi Alliance (2005, 2006). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
