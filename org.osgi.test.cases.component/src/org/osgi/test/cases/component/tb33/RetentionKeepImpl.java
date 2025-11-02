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
package org.osgi.test.cases.component.tb33;

import java.util.Map;

import org.osgi.test.cases.component.service.BaseService;

/**
 * Component implementation for testing retention policy KEEP
 */
public class RetentionKeepImpl implements BaseService {
	
	private volatile boolean activated = false;
	private volatile boolean deactivated = false;
	private volatile int activationCount = 0;
	private volatile int deactivationCount = 0;
	
	public void activate(Map<String, Object> properties) {
		activated = true;
		activationCount++;
	}
	
	public void deactivate() {
		deactivated = true;
		deactivationCount++;
	}

	@Override
	public Map<String, Object> getProperties() {
		return Map.of(
			"activated", activated,
			"deactivated", deactivated,
			"activationCount", activationCount,
			"deactivationCount", deactivationCount
		);
	}

	public boolean isActivated() {
		return activated;
	}

	public boolean isDeactivated() {
		return deactivated;
	}

	public int getActivationCount() {
		return activationCount;
	}

	public int getDeactivationCount() {
		return deactivationCount;
	}
}
