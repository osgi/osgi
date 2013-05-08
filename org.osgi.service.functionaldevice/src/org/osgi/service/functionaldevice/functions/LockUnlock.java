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
 * Lock/Unlock device function represents lock and unlock functionality. The
 * function doesn't provide an access to properties, there are only operations.
 */
public interface LockUnlock extends DeviceFunction {

	/**
	 * Specifies the lock operation name. The operation can be executed with
	 * {@link #lock()} method.
	 */
	public static final String	OPERATION_LOCK		= "lock";

	/**
	 * Specifies the unlock operation name. The operation can be executed with
	 * {@link #unlock()} method.
	 */
	public static final String	OPERATION_UNLOCK	= "unlock";

	/**
	 * Lock device function operation. The operation name is
	 * {@link #OPERATION_LOCK}.
	 */
	public void lock();

	/**
	 * Unlock device function operation. The operation name is
	 * {@link #OPERATION_UNLOCK}.
	 */
	public void unlock();

}
