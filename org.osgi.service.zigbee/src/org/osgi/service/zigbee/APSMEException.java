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
 * This exception class is specialized for the APSME errors.
 * 
 * @version 1.0
 * 
 * @author see RFC 192 authors: Andre Bottaro, Arnaud Rinquin, Jean-Pierre
 *         Poutcheu, Fabrice Blache, Christophe Demottie, Antonin Chazalet,
 *         Evgeni Grigorov, Nicola Portinaro, Stefano Lenzi.
 */
public class APSMEException extends RuntimeException {

	/** generated */
	private static final long	serialVersionUID						= 5649912932079902597L;

	/**
	 * As described in "Table 2.7 APSME-BIND.confirm Parameters" of the ZigBee
	 * specification 1_053474r17ZB_TSC-ZigBee-Specification.pdf, a binding
	 * request can have the following results: SUCCESS, ILLEGAL_REQUEST,
	 * TABLE_FULL, NOT_SUPPORTED.
	 */
	public static final int		APSME_BIND_SUCCESS						= 0;
	public static final int		APSME_BIND_ILLEGAL_REQUEST				= 1;
	public static final int		APSME_BIND_TABLE_FULL					= 2;
	public static final int		APSME_BIND_NOT_SUPPORTED				= 3;

	/**
	 * As described in "Table 2.9 APSME-UNBIND.confirm Parameters" of the ZigBee
	 * specification 1_053474r17ZB_TSC-ZigBee-Specification.pdf, an unbind
	 * request can have the following results: SUCCESS, ILLEGAL_REQUEST,
	 * INVALID_BINDING.
	 */
	public static final int		APSME_UNBIND_SUCCESS					= 10;
	public static final int		APSME_UNBIND_ILLEGAL_REQUEST			= 11;
	public static final int		APSME_UNBIND_INVALID_BINDING			= 12;

	/**
	 * <i>APSME-ADD-GROUP</i> API defined by the ZigBee Specification, in the
	 * former case it will use the proper commands of the <i>Groups</i> cluster
	 * of the ZigBee Specification Library.
	 * 
	 * As described in "Table 2.15 APSME-ADD-GROUP.confirm Parameters" of the
	 * ZigBee specification 1_053474r17ZB_TSC-ZigBee-Specification.pdf, a
	 * add_group request can have the following status: SUCCESS,
	 * INVALID_PARAMETER or TABLE_FULL.
	 */
	public static final int		APSME_ADD_GROUP_SUCCESS					= 20;
	public static final int		APSME_ADD_GROUP_INVALID_PARAMETER		= 21;
	public static final int		APSME_ADD_GROUP_TABLE_FULL				= 22;

	/**
	 * APSME-REMOVE-GROUP </i> API defined by the ZigBee Specification, in the
	 * former case it will use the proper commands of the <i>Groups</i> cluster
	 * of the ZigBee Specification Library.
	 * 
	 * As described in "Table 2.17 APSME-REMOVE-GROUP.confirm Parameters" of the
	 * ZigBee specification 1_053474r17ZB_TSC-ZigBee-Specification.pdf, a
	 * remove_group request can have the following status: SUCCESS,
	 * INVALID_GROUP or INVALID_PARAMETER.
	 */
	public static final int		APSME_REMOVE_GROUP_SUCCESS				= 30;
	public static final int		APSME_REMOVE_GROUP_INVALID_GROUP		= 31;
	public static final int		APSME_REMOVE_GROUP_INVALID_PARAMETER	= 32;

	private final int			errorCode;

	/**
	 * @param errordesc exception error description
	 */
	public APSMEException(String errordesc) {
		super(errordesc);
		errorCode = 0;
	}

	/**
	 * @param errorCode An error code.
	 * @param errorDesc An error description which explain the type of problem.
	 */
	public APSMEException(int errorCode, String errorDesc) {
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
