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
 * This class represents root exception for all the code related to ZigBee/ZDP
 * (see Table 2.137 ZDP Enumerations Description in ZIGBEE SPECIFICATION:
 * 1_053474r17ZB_TSC-ZigBee-Specification.pdf)
 * 
 * @version 1.0
 * 
 * @author see RFC 192 authors: Andre Bottaro, Arnaud Rinquin, Jean-Pierre
 *         Poutcheu, Fabrice Blache, Christophe Demottie, Antonin Chazalet,
 *         Evgeni Grigorov, Nicola Portinaro, Stefano Lenzi.
 * 
 */
public class ZDPException extends RuntimeException {

	/** generated */
	private static final long	serialVersionUID	= 2909437185484211441L;

	/**
	 * The requested operation or transmission was completed successfully.
	 */
	public static final short	SUCCESS				= 0x00;

	/**
	 * Note that: 0x01-0x7f Reserved.
	 */

	/**
	 * The supplied request type was invalid.
	 */
	public static final short	INV_REQUESTTYPE		= 0x80;

	/**
	 * The requested device did not exist on a device following a child
	 * descriptor request to a parent.
	 */
	public static final short	DEVICE_NOT_FOUND	= 0x81;

	/**
	 * The supplied endpoint was equal to 0x00 or between 0xf1 and 0xff.
	 */
	public static final short	INVALID_EP			= 0x82;

	/**
	 * The requested endpoint is not described by a simple descriptor.
	 */
	public static final short	NOT_ACTIVE			= 0x83;

	/**
	 * The requested optional feature is not supported on the target device.
	 */
	public static final short	NOT_SUPPORTED		= 0x84;

	/**
	 * A timeout has occurred with the requested operation.
	 */
	public static final short	TIMEOUT				= 0x85;

	/**
	 * The end device bind request was unsuccessful due to a failure to match
	 * any suitable clusters.
	 */
	public static final short	NO_MATCH			= 0x86;

	/**
	 * Note that: 0x87 Reserved.
	 */

	/**
	 * The unbind request was unsuccessful due to the coordinator or source
	 * device not having an entry in its binding table to unbind.
	 */
	public static final short	NO_ENTRY			= 0x88;

	/**
	 * A child descriptor was not available following a discovery request to a
	 * parent.
	 */
	public static final short	NO_DESCRIPTOR		= 0x89;

	/**
	 * The device does not have storage space to support the requested
	 * operation.
	 */
	public static final short	INSUFFICIENT_SPACE	= 0x8a;

	/**
	 * The device is not in the proper state to support the requested operation.
	 */
	public static final short	NOT_PERMITTED		= 0x8b;

	/**
	 * The device does not have table space to support the operation.
	 */
	public static final short	TABLE_FULL			= 0x8c;

	/**
	 * The permissions configuration table on the target indicates that the
	 * request is not authorized from this device.
	 */
	public static final short	NOT_AUTHORIZED		= 0x8d;

	/**
	 * Note that: 0x8e-0xff Reserved.
	 */

	private final int			errorCode;

	/**
	 * @param errordesc exception error description
	 */
	public ZDPException(String errordesc) {
		super(errordesc);
		errorCode = 0;
	}

	/**
	 * @param errorCode An error code.
	 * @param errorDesc An error description which explain the type of problem.
	 */
	public ZDPException(int errorCode, String errorDesc) {
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
