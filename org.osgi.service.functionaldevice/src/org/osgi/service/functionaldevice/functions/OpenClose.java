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

/**
 * Open/Close device function represents open and close functionality. The
 * function doesn't provide an access to properties, there are only operations.
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
	 * Open device function operation. The operation name is
	 * {@link #OPERATION_OPEN}.
	 */
	public void open();

	/**
	 * Close device function operation. The operation name is
	 * {@link #OPERATION_CLOSE}.
	 */
	public void close();

}
