/*
 * Copyright (c) OSGi Alliance (2019, 2020). All Rights Reserved.
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

package org.osgi.service.onem2m.dto;

import org.osgi.dto.DTO;

/**
 * DTO expresses DasInfo. DAS is short for Dynamic Authorization Server.
 * 
 * @see <a href=
 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
 *      TS-0004 6.3.5.45</a>
 * @see <a href=
 *      "https://git.onem2m.org/PRO/XSD/blob/master/v3_11_0/CDT-commonTypes-v3_11_0.xsd#L1135-1147">oneM2M
 *      XSD dynAuthTokenReqInfo and dasInfo</a>
 * @NotThreadSafe
 */
public class DasInfoDTO extends DTO {
	/**
	 * Dynamic Authorization Server URI
	 */
	public String		uri;

	/**
	 * Information to send to the Dynamic Authorization Server
	 * 
	 * @see <a href=
	 *      "https://git.onem2m.org/PRO/XSD/blob/master/v3_11_0/CDT-commonTypes-v3_11_0.xsd#L1149-1198">oneM2M
	 *      XSD dynAuthDasRequest</a>
	 */
	public GenericDTO	dasRequest;

	/**
	 * Secured Information to send to the Dynamic Authorization Server. JWS or
	 * JWE is assigned to this field.
	 */
	public String		securedDasRequest;
}
