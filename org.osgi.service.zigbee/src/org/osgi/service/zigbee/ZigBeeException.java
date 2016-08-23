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
 * This class represents root exception for all the code related to ZigBee. The
 * provided constants names, but not the values
 * 
 * @author $Id$
 */
public class ZigBeeException extends RuntimeException {

	private static final long	serialVersionUID	= -2147129696681024813L;

	/** The error code used when another endpoint exists with the same ID. */
	public static final int		OSGI_EXISTING_ID	= 0x30;

	/**
	 * The error code used when several hosts exist for this PAN ID target or
	 * HOST_PID target.
	 */
	public static final int		OSGI_MULTIPLE_HOSTS	= 0x31;

	/**
	 * The error code used when the timeout of ZigBee asynchrounous exchange is
	 * reached.
	 */
	public static final int TIMEOUT = 0x32;

	/**
	 * This error code is used if the ZigBee error returned is not covered by
	 * this API specification.
	 */
	public static final int		UNKNOWN_ERROR		= -1;

	/**
	 * The error code associated to this exception
	 * 
	 * @see #getErrorCode()
	 */
	protected final int			errorCode;

	/**
	 * The actual error code returned by the ZigBee node.
	 * 
	 * @see ZigBeeException#getZigBeeErrorCode()
	 */
	protected final int			zigBeeErrorCode;

	/**
	 * Create a {@linkplain ZigBeeException} containing only a description, but
	 * no error codes. If issued on this exeption the {@link #getErrorCode()}
	 * and {@link #getZigBeeErrorCode()} methods return the
	 * {@link #UNKNOWN_ERROR} constant.
	 * 
	 * @param errorDesc exception error description
	 */
	public ZigBeeException(String errorDesc) {
		this(UNKNOWN_ERROR, UNKNOWN_ERROR, errorDesc);
	}

	/**
	 * Create a {@linkplain ZigBeeException} containing a specific
	 * {@code errorCode}. Using this constructor with {@code errorCode} set to
	 * {@linkplain #UNKNOWN_ERROR} is equivalent to call
	 * {@link #ZigBeeException(String)}.
	 * 
	 * @param errorCode One of the error codes defined in this interface or
	 *        {@link #UNKNOWN_ERROR} if the actual error is not listed in this
	 *        interface.
	 * 
	 * @param errorDesc An error description which explain the type of problem.
	 */
	public ZigBeeException(int errorCode, String errorDesc) {
		this(errorCode, UNKNOWN_ERROR, errorDesc);
	}

	/**
	 * Create a {@linkplain ZigBeeException} containing a specific
	 * {@code errorCode} or {@code zigBeeErrorCode}. Using this constructor with
	 * both the {@code errorCode} and {@code zigBeeErrorCode} set to
	 * {@linkplain #UNKNOWN_ERROR} is equivalent to call
	 * {@link #ZigBeeException(String)}.
	 * 
	 * @param errorCode One of the error codes defined in this interface or
	 *        {@link #UNKNOWN_ERROR} the actual error is not covered in this
	 *        interface.
	 * 
	 * @param zigBeeErrorCode The actual status code or {@link #UNKNOWN_ERROR}
	 *        if this status is unknown.
	 * 
	 * @param errorDesc An error description which explain the type of problem.
	 */
	public ZigBeeException(int errorCode, int zigBeeErrorCode, String errorDesc) {
		super(errorDesc);
		this.errorCode = errorCode;
		this.zigBeeErrorCode = zigBeeErrorCode;
	}

	/**
	 * @return One of the error codes defined above. If the returned error code
	 *         is {@link #UNKNOWN_ERROR} and the {@link #hasZigbeeErrorCode()}
	 *         returns {@code true} then the {@link #getZigBeeErrorCode()}
	 *         provides the actual ZigBee error code returned by the device.
	 */
	public int getZigBeeErrorCode() {
		return zigBeeErrorCode;
	}

	/**
	 * @return the error code.
	 */
	public int getErrorCode() {
		return errorCode;
	}

	/**
	 * @return true if the {@link ZigBeeException} convey also the actual error
	 *         code returned by the ZigBee stack.
	 */
	public boolean hasZigbeeErrorCode() {
		return (zigBeeErrorCode != UNKNOWN_ERROR);
	}
}
