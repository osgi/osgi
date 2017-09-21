/*
 * Copyright (c) OSGi Alliance (2017). All Rights Reserved.
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

package org.osgi.service.remoteservices;

/**
 * Defines standard constants for Remote Services.
 * 
 * @author $Id$
 */
public final class RemoteServiceConstants {
	private RemoteServiceConstants() {
		// non-instantiable
	}

	/**
	 * Intent supported by Remote Services implementations that support Basic
	 * Remote Services as defined for the {@code osgi.basic} intent.
	 */
	public static final String	BASIC					= "osgi.basic";

	/**
	 * Intent supported by Remote Service implementations that support
	 * Asynchronous Remote Services as defined for the {@code osgi.async}
	 * intent.
	 */
	public static final String	ASYNC					= "osgi.async";

	/**
	 * Intent supported by Remote Service implementations that provide private
	 * communications support as defined for the {@code osgi.private} intent.
	 */
	public static final String	PRIVATE	= "osgi.private";
}
