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
package org.osgi.impl.bundle.component.annotations;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ComponentRetentionPolicy;

/**
 * Test component with retention policy set to KEEP
 */
@Component(
	name = "testRetentionPolicyKeep",
	service = RetentionPolicyKeep.class,
	immediate = false,
	retentionPolicy = ComponentRetentionPolicy.KEEP
)
public class RetentionPolicyKeep {
	
	public String getMessage() {
		return "Component with KEEP retention policy";
	}
}
