/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.service.onem2m.dto;

import java.util.List;

import org.osgi.dto.DTO;

/**
 * DTO expresses Response Primitive.
 * 
 * @see <a href=
 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
 *      TS-0004 6.4.2</a>
 * @see <a href=
 *      "https://git.onem2m.org/PRO/XSD/blob/master/v3_11_0/CDT-responsePrimitive-v3_11_0.xsd">oneM2M
 *      XSD responsePrimitive</a>
 * @NotThreadSafe
 */
public class ResponsePrimitiveDTO extends DTO {
	/**
	 * Response Status Code
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 6.3.4.2.9</a>
	 */
	public Integer							responseStatusCode;

	/**
	 * Request Identifier
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 6.3.3</a>
	 */
	public String							requestIdentifier;

	/**
	 * Primitive Content
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 6.3.5.5</a>
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 7.2.1.2</a>
	 */
	public PrimitiveContentDTO				content;

	/**
	 * To Parameter
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 6.3.3</a>
	 */
	public String							to;

	/**
	 * From Parameter
	 */
	public String							from;

	/**
	 * Originating Timestamp To Parameter
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 Table 6.3.3-1</a>
	 */
	public String							originatingTimestamp;

	/**
	 * ResultExpiration Timestamp
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 Table 6.3.3-1</a>
	 */
	public String							resultExpirationTimestamp;

	/**
	 * Event Category
	 * <p>
	 * allowed values are 2(Immediate), 3(BestEffort), 4(Latest), and 100-999 as
	 * user defined range.
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 6.3.3</a>
	 * @see <a href=
	 *      "https://git.onem2m.org/PRO/XSD/blob/master/v3_11_0/CDT-commonTypes-v3_11_0.xsd#L326">oneM2M
	 *      XSD eventCat</a>
	 * @see <a href=
	 *      "https://git.onem2m.org/PRO/XSD/blob/master/v3_11_0/CDT-enumerationTypes-v3_11_0.xsd#L208-221">oneM2M
	 *      XSD stdEventCats</a>
	 */
	public Integer							eventCategory;

	/**
	 * Content Status
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 6.3.4.2.44</a>
	 */
	public ContentStatus					contentStatus;

	/**
	 * Content Offset
	 */
	public Integer							contentOffset;

	/**
	 * Assigned Token Identifiers
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 6.3.5.43</a>
	 */
	public List<LocalTokenIdAssignmentDTO>	assignedTokenIdentifiers;
	/**
	 * Token Request Info
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 6.3.5.45</a>
	 */
	public List<DasInfoDTO>					tokenReqInfo;

	// Added R3.0
	/**
	 * AuthSignatureReqInfo
	 */
	public Boolean							AuthSignatureReqInfo;

	/**
	 * Release Version Indicator
	 */
	public ReleaseVersion					releaseVersionIndicator;

	/**
	 * Vendor Information
	 * <p>
	 * Used for vendor specific information. No procedure is defined for the
	 * parameter.
	 */
	public String							vendorInformation;

	/**
	 * Enum ContentStatus
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 6.3.4.2.44</a>
	 */
	public enum ContentStatus {
		/**
		 * PARTIAL_CONTENT
		 */
		PARTIAL_CONTENT, // 1
		/**
		 * FULL_CONTENT
		 */
		FULL_CONTENT; // 2

	}
}
