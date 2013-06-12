/*
 * Copyright (c) OSGi Alliance (2013). All Rights Reserved.
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

package org.osgi.service.functionaldevice.functions;

import org.osgi.service.functionaldevice.DeviceFunction;
import org.osgi.service.functionaldevice.FunctionalDeviceException;

/**
 * <code>OpenClose</code> Device Function represents open and close
 * functionality. The function doesn't provide an access to properties, there
 * are only operations. The Device Function name is
 * <code>org.osgi.service.functionaldevice.functions.OpenClose</code>.
 */
public interface OpenClose extends DeviceFunction {

	/**
	 * Specifies the open operation name. The operation can be executed with
	 * {@link #open()} method.
	 */
	public static final String	OPERATION_OPEN	= "open";

	/**
	 * Specifies the close operation name. The operation can be executed with
	 * {@link #close()} method.
	 */
	public static final String	OPERATION_CLOSE	= "close";

	/**
	 * Open Device Function operation. The operation name is
	 * {@link #OPERATION_OPEN}.
	 * 
	 * @throws UnsupportedOperationException If the operation is not supported.
	 * @throws IllegalStateException If this device service object has already
	 *         been unregistered.
	 * @throws FunctionalDeviceException If an operation error is available.
	 */
	public void open() throws UnsupportedOperationException, IllegalStateException, FunctionalDeviceException;

	/**
	 * Close Device Function operation. The operation name is
	 * {@link #OPERATION_CLOSE}.
	 * 
	 * @throws UnsupportedOperationException If the operation is not supported.
	 * @throws IllegalStateException If this device service object has already
	 *         been unregistered.
	 * @throws FunctionalDeviceException If an operation error is available.
	 */
	public void close() throws UnsupportedOperationException, IllegalStateException, FunctionalDeviceException;

}
