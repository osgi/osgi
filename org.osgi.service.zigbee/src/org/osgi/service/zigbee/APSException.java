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
 * This exception class is specialized for the APS errors. See
 * "Table 2.26 APS Sub-layer Status Values" of the ZigBee specification
 * 1_053474r17ZB_TSC-ZigBee-Specification.pdf.
 * 
 * @author $Id$
 */
public class APSException extends ZigBeeException {

	private static final long	serialVersionUID		= 5649912932079902597L;

	/**
	 * A request has been executed successfully.
	 */
	public static final int		SUCCESS					= 0x00;

	/**
	 * A transmit request failed since the ASDU is too large and fragmentation
	 * is not supported.
	 */
	public static final int		ASDU_TOO_LONG			= 0x41;

	/**
	 * A received fragmented frame could not be defragmented at the current
	 * time.
	 */
	public static final int		DEFRAG_DEFERRED			= 0x42;

	/**
	 * A received fragmented frame could not be defragmented since the device
	 * does not support fragmentation.
	 */
	public static final int		DEFRAG_UNSUPPORTED		= 0x43;

	/**
	 * A parameter value was out of range.
	 */
	public static final int		ILLEGAL_REQUEST			= 0x44;

	/**
	 * An APSME-UNBIND.request failed due to the requested binding link not
	 * existing in the binding table.
	 */
	public static final int		INVALID_BINDING			= 0x45;

	/**
	 * An APSME-REMOVE-GROUP.request has been issued with a group identifier
	 * that does not appear in the group table.
	 */
	public static final int		INVALID_GROUP			= 0x46;

	/**
	 * A parameter value was invalid or out of range.
	 * 
	 */
	public static final int		INVALID_PARAMETER		= 0x47;

	/**
	 * An APSDE-DATA.request requesting acknowledged transmission failed due to
	 * no acknowledgement being received.
	 */
	public static final int		NO_ACK					= 0x48;

	/**
	 * An APSDE-DATA.request with a destination addressing mode set to 0x00
	 * failed due to there being no devices bound to this device.
	 */
	public static final int		NO_BOUND_DEVICE			= 0x49;

	/**
	 * An APSDE-DATA.request with a destination addressing mode set to 0x03
	 * failed due to no corresponding short address found in the address map
	 * table.
	 */
	public static final int		NO_SHORT_ADDRESS		= 0x4a;

	/**
	 * An APSDE-DATA.request with a destination addressing mode set to 0x00
	 * failed due to a binding table not being supported on the device.
	 */
	public static final int		NOT_SUPPORTED			= 0x4b;

	/**
	 * An ASDU was received that was secured using a link key.
	 */
	public static final int		SECURED_LINK_KEY		= 0x4c;

	/**
	 * An ASDU was received that was secured using a network key.
	 */
	public static final int		SECURED_NWK_KEY			= 0x4d;

	/**
	 * An APSDE-DATA.request requesting security has resulted in an error during
	 * the corresponding security processing.
	 */
	public static final int		SECURITY_FAIL			= 0x4e;

	/**
	 * An APSME-BIND.request or APSME.ADDGROUP. request issued when the binding
	 * or group tables, respectively, were full.
	 */
	public static final int		TABLE_FULL				= 0x4f;

	/**
	 * An ASDU was received without any security.
	 */
	public static final int		UNSECURED				= 0x50;

	/**
	 * An APSME-GET.request or APSMESET. request has been issued with an unknown
	 * attribute identifier.
	 */
	public static final int		UNSUPPORTED_ATTRIBUTE	= 0x51;

	/**
	 * Create a {@linkplain ZCLException} containing only a description, but no
	 * error codes. If issued on this exeption the {@link #getErrorCode()} and
	 * {@link #getZigBeeErrorCode()} methods return the {@link #UNKNOWN_ERROR}
	 * constant.
	 * 
	 * @param errorDesc exception error description
	 */
	public APSException(String errorDesc) {
		super(errorDesc);
	}

	/**
	 * Create a {@linkplain ZCLException} containing a specific
	 * {@code errorCode}. Using this constructor with {@code errorCode} set to
	 * {@linkplain #UNKNOWN_ERROR} is equivalent to call
	 * {@link #APSException(String)}.
	 * 
	 * @param errorCode One of the error codes defined in this interface or
	 *        {@link #UNKNOWN_ERROR} if the actual error is not listed in this
	 *        interface. In this case if the native ZigBee error code is known,
	 *        it is preferred to use the {@link #APSException(int, int, String)}
	 *        constructor, passing {@link #UNKNOWN_ERROR} as first parameter and
	 *        the native ZigBee error as the second.
	 * 
	 * @param errorDesc An error description which explain the type of problem.
	 */
	public APSException(int errorCode, String errorDesc) {
		super(errorCode, errorDesc);
	}

	/**
	 * Create a {@linkplain ZCLException} containing a specific
	 * {@code errorCode} or {@code zigBeeErrorCode}. Using this constructor with
	 * both the {@code errorCode} and {@code zigBeeErrorCode} set to
	 * {@linkplain #UNKNOWN_ERROR} is equivalent to call
	 * {@link #APSException(String)}.
	 * 
	 * @param errorCode One of the error codes defined in this interface or
	 *        {@link #UNKNOWN_ERROR} the actual error is not covered in this
	 *        interface. In this case the {@code zigBeeErrorCode} parameter must
	 *        be the actual status code returned by the ZigBe stack.
	 * 
	 * @param zigBeeErrorCode The actual APS status code or
	 *        {@link #UNKNOWN_ERROR} if this status is unknown.
	 * 
	 * @param errorDesc An error description which explain the type of problem.
	 */
	public APSException(int errorCode, int zigBeeErrorCode, String errorDesc) {
		super(errorCode, zigBeeErrorCode, errorDesc);
	}

}
