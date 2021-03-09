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

import java.util.Map;

import org.osgi.dto.DTO;

/**
 * DTO expresses Resource.
 * <p>
 * Universal attributes are expressed in field of the class. Common attributes
 * and other attributes are stored in attribute field.
 * 
 * @see <a href=
 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0001-Functional_Architecture-V3_15_1.pdf">oneM2M
 *      TS-0001 9.6.1.3.1</a>
 * @NotThreadSafe
 */
public class ResourceDTO extends DTO {

	// Universal Attribute, which can be held by all resources.
	/**
	 * Resource Type
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0001-Functional_Architecture-V3_15_1.pdf">oneM2M
	 *      TS-0001 9.6.1.3.1</a>
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oenM2M
	 *      TS-0004 6.3.4.2.1</a>
	 */
	public Integer				resourceType;

	/**
	 * Resource ID
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0001-Functional_Architecture-V3_15_1.pdf">oneM2M
	 *      TS-0001 9.6.1.3.1</a>
	 */
	public String				resourceID;

	/**
	 * Parent ID Resource ID of parent resource.
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0001-Functional_Architecture-V3_15_1.pdf">oneM2M
	 *      TS-0001 9.6.1.3.1</a>
	 */
	public String				parentID;

	/**
	 * Creation time
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0001-Functional_Architecture-V3_15_1.pdf">oneM2M
	 *      TS-0001 9.6.1.3.1</a>
	 */
	public String				creationTime;

	/**
	 * last modified time
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0001-Functional_Architecture-V3_15_1.pdf">oneM2M
	 *      TS-0001 9.6.1.3.1</a>
	 */
	public String				lastModifiedTime;

	/**
	 * Resource name
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0001-Functional_Architecture-V3_15_1.pdf">oneM2M
	 *      TS-0001 9.6.1.3.1</a>
	 */
	public String				resourceName;

	/**
	 * Non Universal Attribute. Value Part must be the types that are allowed
	 * for OSGi DTO. In case of value part can be expressed DTO in this package,
	 * the DTO must be used. In case of value part have sub-elements, GenericDTO
	 * must be used.
	 */
	public Map<String,Object>	attribute;

}
