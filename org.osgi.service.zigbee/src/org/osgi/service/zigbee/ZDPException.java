/*
 * Copyright (c) OSGi Alliance (2016). All Rights Reserved.
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

package org.osgi.service.zigbee;

/**
 * This class represents root exception for all the code related to ZDP (see
 * Table 2.137 ZDP Enumerations Description in ZIGBEE SPECIFICATION:
 * 1_053474r17ZB_TSC-ZigBee-Specification.pdf)
 * 
 * @author $Id$
 */
public class ZDPException extends ZigBeeException {

	private static final long	serialVersionUID	= 2909437185484211441L;

	/**
	 * The requested operation or transmission was completed successfully.
	 */
	public static final int		SUCCESS				= 0x00;

	/**
	 * Note that: 0x01-0x7f Reserved.
	 */

	/**
	 * The supplied request type was invalid.
	 */
	public static final int		INV_REQUESTTYPE		= 0x21;

	/**
	 * The requested device did not exist on a device following a child
	 * descriptor request to a parent.
	 */
	public static final int		DEVICE_NOT_FOUND	= 0x22;

	/**
	 * The supplied endpoint was equal to 0x00 or between 0xf1 and 0xff.
	 */
	public static final int		INVALID_EP			= 0x23;

	/**
	 * The requested endpoint is not described by a simple descriptor.
	 */
	public static final int		NOT_ACTIVE			= 0x24;

	/**
	 * The requested optional feature is not supported on the target device.
	 */
	public static final int		NOT_SUPPORTED		= 0x25;

	/**
	 * A timeout has occurred with the requested operation.
	 */
	public static final int		TIMEOUT				= 0x26;

	/**
	 * The end device bind request was unsuccessful due to a failure to match
	 * any suitable clusters.
	 */
	public static final int		NO_MATCH			= 0x27;

	/**
	 * The unbind request was unsuccessful due to the coordinator or source
	 * device not having an entry in its binding table to unbind.
	 */
	public static final int		NO_ENTRY			= 0x28;

	/**
	 * A child descriptor was not available following a discovery request to a
	 * parent.
	 */
	public static final int		NO_DESCRIPTOR		= 0x29;

	/**
	 * The device does not have storage space to support the requested
	 * operation.
	 */
	public static final int		INSUFFICIENT_SPACE	= 0x2a;

	/**
	 * The device is not in the proper state to support the requested operation.
	 */
	public static final int		NOT_PERMITTED		= 0x2b;

	/**
	 * The device does not have table space to support the operation.
	 */
	public static final int		TABLE_FULL			= 0x2c;

	/**
	 * The permissions configuration table on the target indicates that the
	 * request is not authorized from this device.
	 */
	public static final int		NOT_AUTHORIZED		= 0x2d;

	/**
	 * Create a {@linkplain ZCLException} containing only a description, but no
	 * error codes. If issued on this exception the {@link #getErrorCode()} and
	 * {@link #getZigBeeErrorCode()} methods return the {@link #UNKNOWN_ERROR}
	 * constant.
	 * 
	 * @param errorDesc exception error description
	 */
	public ZDPException(String errorDesc) {
		super(errorDesc);
	}

	/**
	 * Create a {@linkplain ZCLException} containing a specific
	 * {@code errorCode}. Using this constructor with {@code errorCode} set to
	 * {@linkplain #UNKNOWN_ERROR} is equivalent to call
	 * {@link #ZDPException(String)}.
	 * 
	 * @param errorCode One of the error codes defined in this interface or
	 *        {@link #UNKNOWN_ERROR} if the actual error is not listed in this
	 *        interface. In this case if the native ZigBee error code is known,
	 *        it is preferred to use the {@link #ZDPException(int, int, String)}
	 *        constructor, passing {@link #UNKNOWN_ERROR} as first parameter and
	 *        the native ZigBee error as the second.
	 * 
	 * @param errorDesc An error description which explain the type of problem.
	 */
	public ZDPException(int errorCode, String errorDesc) {
		super(errorCode, errorDesc);
	}

	/**
	 * Create a {@linkplain ZCLException} containing a specific
	 * {@code errorCode} or {@code zigBeeErrorCode}. Using this constructor with
	 * both the {@code errorCode} and {@code zigBeeErrorCode} set to
	 * {@linkplain #UNKNOWN_ERROR} is equivalent to call
	 * {@link #ZDPException(String)}.
	 * 
	 * @param errorCode One of the error codes defined in this interface or
	 *        {@link #UNKNOWN_ERROR} the actual error is not covered in this
	 *        interface. In this case the {@code zigBeeErrorCode} parameter must
	 *        be the actual status code returned by the ZigBe stack.
	 * 
	 * @param zigBeeErrorCode The actual ZDP status code or
	 *        {@link #UNKNOWN_ERROR} if this status is unknown.
	 * 
	 * @param errorDesc An error description which explain the type of problem.
	 */
	public ZDPException(int errorCode, int zigBeeErrorCode, String errorDesc) {
		super(errorCode, zigBeeErrorCode, errorDesc);
	}

}
