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

	public static final short	SUCCESS								= 0x00;

	public static final short	FAILURE								= 0x01;

	public static final short	MALFORMED_COMMAND					= 0x80;

	public static final short	CLUSTER_COMMAND_NOT_SUPPORTED		= 0x81;

	public static final short	GENERAL_COMMAND_NOT_SUPPORTED		= 0x82;

	public static final short	MANUF_CLUSTER_COMMAND_NOT_SUPPORTED	= 0x83;

	public static final short	MANUF_GENERAL_COMMAND_NOT_SUPPORTED	= 0x84;

	public static final short	INVALID_FIELD						= 0x85;

	public static final short	ATTRIBUTE_NOT_SUPPORTED				= 0x86;

	public static final short	INVALID_VALUE						= 0x87;

	public static final short	READ_ONLY							= 0x88;

	public static final short	INSUFFICIENT_SPACE					= 0x89;

	public static final short	DUPLICATE_EXISTS					= 0x8a;

	public static final short	NOT_FOUND							= 0x8b;

	public static final short	UNREPORTABLE_TYPE					= 0x8c;

	public static final short	INVALID_DATA_TYPE					= 0x8d;

	public static final short	HARDWARE_FAILURE					= 0xc0;

	public static final short	SOFTWARE_FAILURE					= 0xc1;

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
	 * @param errordesc An error description which explain the type of problem.
	 */
	public ZigBeeException(int errorCode, String errordesc) {
		super(errordesc);
		this.errorCode = errorCode;
	}

	/**
	 * @return A ZigBee error code defined a ZigBee Forum working committee or
	 *         specified by a ZigBee vendor.
	 */
	public int getZigBeeError_Code() {
		return errorCode;
	}
}
