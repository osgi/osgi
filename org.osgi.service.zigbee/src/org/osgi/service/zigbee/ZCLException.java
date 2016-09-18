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
 * This class represents root exception for all the code related to ZigBee/ZCL.
 * The provided constants names, but not the values, maps to the ZCL error codes
 * defined in the ZCL specification.
 * 
 * @author $Id$
 */
public class ZCLException extends ZigBeeException {

	private static final long	serialVersionUID					= -7330626950388193679L;

	/** ZCL Success error code. */
	public static final int		SUCCESS								= 0x00;

	/** ZCL Failure error code. */
	public static final int		FAILURE								= 0x01;

	/** ZCL Malformed Command error code. */
	public static final int		MALFORMED_COMMAND					= 0x02;

	/** ZCL Cluster Command Not Supported error code. */
	public static final int		CLUSTER_COMMAND_NOT_SUPPORTED		= 0x03;

	/** ZCL General Command Not Supported error code. */
	public static final int		GENERAL_COMMAND_NOT_SUPPORTED		= 0x04;

	/** ZCL Manuf Cluster Command Not Supported error code. */
	public static final int		MANUF_CLUSTER_COMMAND_NOT_SUPPORTED	= 0x05;

	/** ZCL Manuf General Command Not Supported error code. */
	public static final int		MANUF_GENERAL_COMMAND_NOT_SUPPORTED	= 0x06;

	/** ZCL Invalid Field error code. */
	public static final int		INVALID_FIELD						= 0x07;

	/** ZCL Unsupported Attribute error code. */
	public static final int		UNSUPPORTED_ATTRIBUTE				= 0x08;

	/** ZCL Invalid Value error code. */
	public static final int		INVALID_VALUE						= 0x09;

	/** ZCL Read Only error code. */
	public static final int		READ_ONLY							= 0x0a;

	/** ZCL Insufficient Space error code. */
	public static final int		INSUFFICIENT_SPACE					= 0x0b;

	/** ZCL Duplicate Exists error code. */
	public static final int		DUPLICATE_EXISTS					= 0x0c;

	/** ZCL Not Found error code. */
	public static final int		NOT_FOUND							= 0x0d;

	/** Unreportable Type error code. */
	public static final int		UNREPORTABLE_TYPE					= 0x0e;

	/** ZCL Invalid Data Type error code. */
	public static final int		INVALID_DATA_TYPE					= 0x0f;

	/**
	 * HARDWARE_FAILURE - in this case, an additional exception describing the
	 * problem can be nested.
	 */
	public static final int		HARDWARE_FAILURE					= 0x10;

	/**
	 * Software Failure error code - in this case, an additional exception
	 * describing the problem can be nested.
	 */
	public static final int		SOFTWARE_FAILURE					= 0x11;

	/** ZCL Calibration Error error code. */
	public static final int		CALIBRATION_ERROR					= 0x12;

	/**
	 * Creates a {@linkplain ZCLException} containing only a description, but no
	 * error codes. If issued on this exeption the {@link #getErrorCode()} and
	 * {@link #getZigBeeErrorCode()} methods return the {@link #UNKNOWN_ERROR}
	 * constant.
	 * 
	 * @param errorDesc exception error description.
	 */
	public ZCLException(String errorDesc) {
		this(UNKNOWN_ERROR, UNKNOWN_ERROR, errorDesc);
	}

	/**
	 * Creates a {@linkplain ZCLException} containing a specific
	 * {@code errorCode}. Using this constructor with {@code errorCode} set to
	 * {@linkplain #UNKNOWN_ERROR} is equivalent to call
	 * {@link #ZCLException(String)}.
	 * 
	 * @param errorCode One of the error codes defined in this interface or
	 *        {@link #UNKNOWN_ERROR} if the actual error is not listed in this
	 *        interface. In this case if the native ZigBee error code is known,
	 *        it is preferred to use the {@link #ZCLException(int, int, String)}
	 *        constructor, passing {@link #UNKNOWN_ERROR} as first parameter and
	 *        the native ZigBee error as the second.
	 * 
	 * @param errorDesc An error description which explain the type of problem.
	 */
	public ZCLException(int errorCode, String errorDesc) {
		super(errorCode, errorDesc);
	}

	/**
	 * Creates a {@linkplain ZCLException} containing a specific
	 * {@code errorCode} or {@code zigBeeErrorCode}. Using this constructor with
	 * both the {@code errorCode} and {@code zigBeeErrorCode} set to
	 * {@linkplain #UNKNOWN_ERROR} is equivalent to call
	 * {@link #ZCLException(String)}.
	 * 
	 * @param errorCode One of the error codes defined in this interface or
	 *        {@link #UNKNOWN_ERROR} the actual error is not covered in this
	 *        interface. In this case the {@code zigBeeErrorCode} parameter must
	 *        be the actual status code returned by the ZigBe stack.
	 * 
	 * @param zigBeeErrorCode The actual ZCL status code or
	 *        {@link #UNKNOWN_ERROR} if this status is unknown.
	 * 
	 * @param errorDesc An error description which explain the type of problem.
	 */
	public ZCLException(int errorCode, int zigBeeErrorCode, String errorDesc) {
		super(errorCode, zigBeeErrorCode, errorDesc);
	}

}
