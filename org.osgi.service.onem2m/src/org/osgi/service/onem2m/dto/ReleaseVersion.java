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

/**
 * enum expresses oneM2M specification version.
 * <p>
 * This information is introduced after Release 2.0 and oneM2M uses only R2A,
 * R3_0 (as 2a and 3).
 * 
 * @see <a href=
 *      "https://git.onem2m.org/PRO/XSD/blob/master/v3_11_0/CDT-commonTypes-v3_11_0.xsd#L450-455">oneM2M
 *      XSD releaseVersion</a>
 */
public enum ReleaseVersion {
	/**
	 * Release 1
	 */
	R1_0,
	/**
	 * Release 1.1
	 */
	R1_1,
	/**
	 * Release 2
	 */
	R2_0,
	/**
	 * Release 2A
	 */
	R2A,
	/**
	 * Release 3
	 */
	R3_0,
	/**
	 * Release 4 (reserved for future)
	 */
	R4_0,
	/**
	 * Release 5 (reserved for future)
	 */
	R5_0;

}
