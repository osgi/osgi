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

package org.osgi.test.cases.dal;

import java.util.Map;

import org.osgi.service.dal.Device;

final class TestDevice implements Device {

	private final Map<String,Object>	deviceProps;
	private final String	uid;

	public TestDevice(Map<String,Object> deviceProps) {
		this.deviceProps = deviceProps;
		this.uid = (null != deviceProps) ? null : String.valueOf(System.currentTimeMillis());
	}

	public Object getServiceProperty(String propName) {
		return (null != this.deviceProps) ? this.deviceProps.get(propName) :
				Device.SERVICE_UID.equals(propName) ? this.uid : null;
	}

	public String[] getServicePropertyKeys() {
		return (null != this.deviceProps) ?
				(String[]) this.deviceProps.keySet().toArray(new String[0])
				:
				new String[] {Device.SERVICE_UID};
	}

	public void remove() {
		// nothing to do here
	}
}
