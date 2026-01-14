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

package org.osgi.test.cases.component.tb33.impl;

import org.osgi.test.cases.component.service.DataService;

/**
 * Final class implementation of DataService that requires constructor arguments.
 * This class cannot be directly instantiated by DS, demonstrating the need for
 * ServiceFactory support.
 * 
 * @author $Id$
 */
public final class DataServiceImpl implements DataService {
	private final String instanceId;
	private final long bundleId;
	
	/**
	 * Constructor requires parameters, which DS cannot provide directly
	 */
	public DataServiceImpl(String instanceId, long bundleId) {
		this.instanceId = instanceId;
		this.bundleId = bundleId;
	}
	
	@Override
	public String getInstanceId() {
		return instanceId;
	}
	
	@Override
	public long getBundleId() {
		return bundleId;
	}
	
	@Override
	public String getData() {
		return "Data from instance " + instanceId + " for bundle " + bundleId;
	}
}
