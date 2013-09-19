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

package org.osgi.service.zigbee;

/**
 * This class represents root exception for all the code related to ZigBee
 * 
 * @version 1.0
 */
public class ZigBeeException extends Exception {

	/** generated */
	private static final long	serialVersionUID					= -7330626950388193679L;

	/** SUCCESS */
	public static final short	SUCCESS								= 0x00;

	/** FAILURE */
	public static final short	FAILURE								= 0x01;

	/** MALFORMED_COMMAND */
	public static final short	MALFORMED_COMMAND					= 0x80;

	/** CLUSTER_COMMAND_NOT_SUPPORTED */
	public static final short	CLUSTER_COMMAND_NOT_SUPPORTED		= 0x81;

	/** GENERAL_COMMAND_NOT_SUPPORTED */
	public static final short	GENERAL_COMMAND_NOT_SUPPORTED		= 0x82;

	/** MANUF_CLUSTER_COMMAND_NOT_SUPPORTED */
	public static final short	MANUF_CLUSTER_COMMAND_NOT_SUPPORTED	= 0x83;

	/** MANUF_GENERAL_COMMAND_NOT_SUPPORTED */
	public static final short	MANUF_GENERAL_COMMAND_NOT_SUPPORTED	= 0x84;

	/** INVALID_FIELD */
	public static final short	INVALID_FIELD						= 0x85;

	/** ATTRIBUTE_NOT_SUPPORTED */
	public static final short	ATTRIBUTE_NOT_SUPPORTED				= 0x86;

	/** INVALID_VALUE */
	public static final short	INVALID_VALUE						= 0x87;

	/** READ_ONLY */
	public static final short	READ_ONLY							= 0x88;

	/** INSUFFICIENT_SPACE */
	public static final short	INSUFFICIENT_SPACE					= 0x89;

	/** DUPLICATE_EXISTS */
	public static final short	DUPLICATE_EXISTS					= 0x8a;

	/** NOT_FOUND */
	public static final short	NOT_FOUND							= 0x8b;

	/** UNREPORTABLE_TYPE */
	public static final short	UNREPORTABLE_TYPE					= 0x8c;

	/** INVALID_DATA_TYPE */
	public static final short	INVALID_DATA_TYPE					= 0x8d;

	/** HARDWARE_FAILURE */
	public static final short	HARDWARE_FAILURE					= 0xc0;

	/** SOFTWARE_FAILURE */
	public static final short	SOFTWARE_FAILURE					= 0xc1;

	/** CALIBRATION_ERROR */
	public static final short	CALIBRATION_ERROR					= 0xc2;

	private final int			errorCode;

	/**
	 * @param errordesc exception error description
	 */
	public ZigBeeException(String errordesc) {
		super(errordesc);
		errorCode = 0;
	}

	/**
	 * @param errorCode An error code.
	 * @param errorDesc An error description which explain the type of problem.
	 */
	public ZigBeeException(int errorCode, String errorDesc) {
		super(errorDesc);
		this.errorCode = errorCode;
	}

	/**
	 * @return A ZigBee error code defined a ZigBee Forum working committee or
	 *         specified by a ZigBee vendor.
	 */
	public int getZigBeeErrorCode() {
		return errorCode;
	}
}
