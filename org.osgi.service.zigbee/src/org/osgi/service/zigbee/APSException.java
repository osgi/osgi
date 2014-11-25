/*
 * Copyright (c) OSGi Alliance (2013, 2014). All Rights Reserved.
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
 * @version 1.0
 * 
 * @author see RFC 192 authors: Andre Bottaro, Arnaud Rinquin, Jean-Pierre
 *         Poutcheu, Fabrice Blache, Christophe Demottie, Antonin Chazalet,
 *         Evgeni Grigorov, Nicola Portinaro, Stefano Lenzi.
 */
public class APSException extends RuntimeException {

	/** generated */
	private static final long	serialVersionUID		= 5649912932079902597L;

	/**
	 * A request has been executed successfully.
	 */
	public static final int		SUCCESS					= 0x00;

	/**
	 * A transmit request failed since the ASDU is too large and fragmentation
	 * is not supported.
	 */
	public static final int		ASDU_TOO_LONG			= 0xa0;

	/**
	 * A received fragmented frame could not be defragmented at the current
	 * time.
	 */
	public static final int		DEFRAG_DEFERRED			= 0xa1;

	/**
	 * A received fragmented frame could not be defragmented since the device
	 * does not support fragmentation.
	 */
	public static final int		DEFRAG_UNSUPPORTED		= 0xa2;

	/**
	 * A parameter value was out of range.
	 */
	public static final int		ILLEGAL_REQUEST			= 0xa3;

	/**
	 * An APSME-UNBIND.request failed due to the requested binding link not
	 * existing in the binding table.
	 */
	public static final int		INVALID_BINDING			= 0xa4;

	/**
	 * An APSME-REMOVE-GROUP.request has been issued with a group identifier
	 * that does not appear in the group table.
	 */
	public static final int		INVALID_GROUP			= 0xa5;

	/**
	 * A parameter value was invalid or out of range.
	 * 
	 */
	public static final int		INVALID_PARAMETER		= 0xa6;

	/**
	 * An APSDE-DATA.request requesting acknowledged transmission failed due to
	 * no acknowledgement being received.
	 */
	public static final int		NO_ACK					= 0xa7;

	/**
	 * An APSDE-DATA.request with a destination addressing mode set to 0x00
	 * failed due to there being no devices bound to this device.
	 */
	public static final int		NO_BOUND_DEVICE			= 0xa8;

	/**
	 * An APSDE-DATA.request with a destination addressing mode set to 0x03
	 * failed due to no corresponding short address found in the address map
	 * table.
	 */
	public static final int		NO_SHORT_ADDRESS		= 0xa9;

	/**
	 * An APSDE-DATA.request with a destination addressing mode set to 0x00
	 * failed due to a binding table not being supported on the device.
	 */
	public static final int		NOT_SUPPORTED			= 0xaa;

	/**
	 * An ASDU was received that was secured using a link key.
	 */
	public static final int		SECURED_LINK_KEY		= 0xab;

	/**
	 * An ASDU was received that was secured using a network key.
	 */
	public static final int		SECURED_NWK_KEY			= 0xac;

	/**
	 * An APSDE-DATA.request requesting security has resulted in an error during
	 * the corresponding security processing.
	 */
	public static final int		SECURITY_FAIL			= 0xad;

	/**
	 * An APSME-BIND.request or APSME.ADDGROUP. request issued when the binding
	 * or group tables, respectively, were full.
	 */
	public static final int		TABLE_FULL				= 0xae;

	/**
	 * An ASDU was received without any security.
	 */
	public static final int		UNSECURED				= 0xaf;

	/**
	 * An APSME-GET.request or APSMESET. request has been issued with an unknown
	 * attribute identifier.
	 */
	public static final int		UNSUPPORTED_ATTRIBUTE	= 0xb0;

	private final int			errorCode;

	/**
	 * @param errordesc exception error description
	 */
	public APSException(String errordesc) {
		super(errordesc);
		errorCode = 0;
	}

	/**
	 * @param errorCode An error code.
	 * @param errorDesc An error description which explain the type of problem.
	 */
	public APSException(int errorCode, String errorDesc) {
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
