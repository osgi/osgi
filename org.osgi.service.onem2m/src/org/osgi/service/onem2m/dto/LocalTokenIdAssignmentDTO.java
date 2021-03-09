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

import org.osgi.dto.DTO;

/**
 * DTO expresses LocalTokenIdAssignment.
 * 
 * @see <a href=
 *      "https://git.onem2m.org/PRO/XSD/blob/master/v3_11_0/CDT-commonTypes-v3_11_0.xsd#L1112-1123">oneM2M
 *      XSD dynAuthLocalTokenIdAssignments and localTokenIdAssignment</a>
 * @NotThreadSafe
 */
public class LocalTokenIdAssignmentDTO extends DTO {
	/**
	 * local token ID
	 */
	public String	localTokenID;
	/**
	 * token ID
	 */
	public String	tokenID;
}
