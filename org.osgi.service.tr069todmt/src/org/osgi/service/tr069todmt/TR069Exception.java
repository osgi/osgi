/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.service.tr069todmt;

import org.osgi.service.dmt.DmtException;

/**
 * This exception is defined in terms of applicable TR-069 fault codes. The
 * TR-069 specification defines the fault codes that can occur in different
 * situations.
 * 
 */
public class TR069Exception extends RuntimeException {
	private static final long	serialVersionUID		= 1L;
	final int					faultCode;
	final DmtException			dmtException;

	/**
	 * 9000 Method not supported
	 */
	final public static int		METHOD_NOT_SUPPORTED	= 9000;

	/**
	 * 9001 Request denied (no reason specified
	 */
	final public static int		REQUEST_DENIED			= 9001;

	/**
	 * 9002 Internal error
	 */
	final public static int		INTERNAL_ERROR			= 9002;

	/**
	 * 9003 Invalid Arguments
	 */
	final public static int		INVALID_ARGUMENTS		= 9003;

	/**
	 * 9004 Resources exceeded (when used in association with
	 * SetParameterValues, this MUST NOT be used to indicate parameters in
	 * error)
	 */
	final public static int		RESOURCES_EXCEEDED		= 9004;

	/**
	 * 9005 Invalid parameter name (associated with Set/GetParameterValues,
	 * GetParameterNames, Set/GetParameterAttributes, AddObject, and
	 * DeleteObject)
	 */
	final public static int		INVALID_PARAMETER_NAME	= 9005;

	/**
	 * 9006 Invalid parameter type (associated with SetParameterValues)
	 */
	final public static int		INVALID_PARAMETER_TYPE	= 9006;

	/**
	 * 9007 Invalid parameter value (associated with SetParameterValues)
	 */
	final public static int		INVALID_PARAMETER_VALUE	= 9007;

	/**
	 * 9008 Attempt to set a non-writable parameter (associated with
	 * SetParameterValues)
	 */
	final public static int		NON_WRITABLE_PARAMETER	= 9008;

	/**
	 * 9009 Notification request rejected (associated with
	 * SetParameterAttributes method).
	 */
	final public static int		NOTIFICATION_REJECTED	= 9009;

	/**
	 * A default constructor when only a message is known. This will generate a
	 * {@link #INTERNAL_ERROR} fault.
	 * 
	 * @param message The message
	 */
	public TR069Exception(String message) {
		this(message, 9002, null);
	}

	/**
	 * A Constructor with a message and a fault code.
	 * 
	 * @param message The message
	 * @param faultCode The TR-069 defined fault code
	 * @param e
	 */
	public TR069Exception(String message, int faultCode, DmtException e) {
		super(message, e);
		this.faultCode = faultCode;
		this.dmtException = e;
	}

	/**
	 * A Constructor with a message and a fault code.
	 * 
	 * @param message The message
	 * @param faultCode The TR-069 defined fault code
	 */
	public TR069Exception(String message, int faultCode) {
		this(message, faultCode, null);
	}

	/**
	 * Create a TR069Exception from a Dmt Exception.
	 * 
	 * @param e The Dmt Exception
	 */
	public TR069Exception(DmtException e) {
		super(e);
		this.faultCode = getFaultCode(e);
		this.dmtException = e;
	}

	private int getFaultCode(DmtException e) {
		switch (e.getCode()) {
			case DmtException.FEATURE_NOT_SUPPORTED :
			case DmtException.COMMAND_NOT_ALLOWED :
			case DmtException.SESSION_CREATION_TIMEOUT :
			case DmtException.TRANSACTION_ERROR :
			case DmtException.UNAUTHORIZED :
				return REQUEST_DENIED;

			case DmtException.INVALID_URI :
			case DmtException.NODE_NOT_FOUND :
			case DmtException.URI_TOO_LONG :
				return INVALID_PARAMETER_NAME;

			case DmtException.LIMIT_EXCEEDED :
				return RESOURCES_EXCEEDED;

			case DmtException.METADATA_MISMATCH :
				return INVALID_PARAMETER_TYPE;

			case DmtException.PERMISSION_DENIED :
				return NON_WRITABLE_PARAMETER;

			default :
				return INTERNAL_ERROR;
		}
	}

	/**
	 * Answer the associated TR-069 fault code.
	 * 
	 * @return Answer the associated TR-069 fault code.
	 */
	public int getFaultCode() {
		return faultCode;
	}

	/**
	 * @return the corresponding Dmt Exception
	 */
	public DmtException getDmtException() {
		return dmtException;
	}
}
