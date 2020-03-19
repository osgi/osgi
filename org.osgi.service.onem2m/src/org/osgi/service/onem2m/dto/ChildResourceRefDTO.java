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
 * DTO expresses ChildResourceRef.
 * 
 * @see <a href=
 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
 *      TS-0004 6.3.5.29</a>
 * @see <a href=
 *      "https://git.onem2m.org/PRO/XSD/blob/master/v3_11_0/CDT-commonTypes-v3_11_0.xsd#L885-893">oneM2M
 *      XSD childResourceRef</a>
 * @NotThreadSafe
 */
public class ChildResourceRefDTO extends DTO {
	/**
	 * URI to the child resource.
	 */
	public String	uri;

	/**
	 * name of the child resource pointed to by the URI
	 */
	public String	name;

	/**
	 * resourceType of the child resource pointed to by the URI
	 */
	public Integer	type;				// Resource Type

	/**
	 * resource type specialization of the child resource pointed to by the URI
	 * in case type represents a flexContainer. This is an optional field.
	 */
	public String	specializationID;	// any URI, optional
}
