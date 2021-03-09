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

package org.osgi.service.cdi;

/**
 * Defines the possible values of the policy of a reference towards propagating
 * service changes to the CDI runtime
 *
 * @author $Id$
 */
public enum ReferencePolicy {
	/**
	 * Reboot the CDI component that depends on this reference
	 */
	STATIC,
	/**
	 * Update the CDI reference
	 */
	DYNAMIC
}
