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

import java.util.List;

import org.osgi.dto.DTO;

/**
 * DTO expresses Primitive Content.
 * <p>
 * This Data structure is used as union. Only one field MUST have a value, the
 * others MUST be null.
 * 
 * @see <a href=
 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
 *      TS-0004 6.3.5.5</a>
 * @see <a href=
 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
 *      TS-0004 7.2.1</a>
 * @see <a href=
 *      "https://git.onem2m.org/PRO/XSD/blob/master/v3_11_0/CDT-commonTypes-v3_11_0.xsd#L596-602">oneM2M
 *      XSD primitiveContent</a>
 * @NotThreadSafe
 */
public class PrimitiveContentDTO extends DTO {
	/**
	 * Resource
	 */
	public ResourceDTO					resource;

	/**
	 * Resource Wrapper
	 */
	public ResourceWrapperDTO			resourceWrapper;

	/**
	 * Aggregated Notification
	 */
	public List<NotificationDTO>		aggregatedNotification;

	/**
	 * Security Info
	 */
	public SecurityInfoDTO				securityInfo;

	/**
	 * Response Primitive
	 */
	public ResponsePrimitiveDTO			responsePrimitive;

	/**
	 * Debug Info
	 */
	public String						debugInfo;

	/**
	 * List Of URIs
	 */
	public List<String>					listOfURIs;

	/**
	 * URI
	 */
	public String						uri;

	/**
	 * Aggregated Response
	 */
	public List<ResponsePrimitiveDTO>	aggregatedResponse;

	/**
	 * Child Resource RefList
	 */
	public List<ChildResourceRefDTO>	childResourceRefList;

	/**
	 * Notification
	 */
	public NotificationDTO				notification;

	/**
	 * Attribute List
	 */
	public List<String>					attributeList;

	/**
	 * Request Primitive
	 */
	public RequestPrimitiveDTO			requestPrimitive;

	/**
	 * Query Result
	 */
	public String						queryResult;

}
