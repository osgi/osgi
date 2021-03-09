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

package org.osgi.impl.service.dal;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.dal.Device;
import org.osgi.service.dal.DevicePermission;

final class SimulatedDevice extends SimulatedService implements Device {

	private final SimulatedFunction[]	functions;

	public SimulatedDevice(Dictionary<String,Object> deviceProps,
			BundleContext bc, SimulatedFunction[] functions) {
		this.functions = functions;
		super.register(new String[] {Device.class.getName()}, deviceProps, bc);
	}

	@Override
	public void remove() {
		SecurityManager securityManager = System.getSecurityManager();
		if (null != securityManager) {
			securityManager.checkPermission(
					new DevicePermission(this, DevicePermission.REMOVE));
		}
		super.serviceProps.put(Device.SERVICE_STATUS, Device.STATUS_REMOVED);
		super.serviceProps.remove(Device.SERVICE_STATUS_DETAIL);
		super.serviceReg.setProperties(super.serviceProps);
		super.serviceReg.unregister();
		if (null != this.functions) {
			for (int i = 0; i < this.functions.length; i++) {
				this.functions[i].remove();
			}
		}
	}

	@Override
	public Object getServiceProperty(String propName) {
		Object value = super.serviceRef.getProperty(propName);
		if (null == value) {
			throw new IllegalArgumentException("The property name is missing: " + propName);
		}
		return value;
	}

	@Override
	public String[] getServicePropertyKeys() {
		return super.serviceRef.getPropertyKeys();
	}

	public SimulatedFunction[] getFunctions(String functionClassName) {
		if ((null == this.functions) || (null == functionClassName)) {
			return null;
		}
		List<SimulatedFunction> result = new ArrayList<>();
		for (int i = 0; i < this.functions.length; i++) {
			if (contains(
					(String[]) this.functions[i].getServiceProperty(Constants.OBJECTCLASS),
					functionClassName)) {
				result.add(this.functions[i]);
			}
		}
		return result.isEmpty() ? null :
				result.toArray(new SimulatedFunction[0]);
	}

	private static boolean contains(String[] array, String element) {
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals(element)) {
				return true;
			}
		}
		return false;
	}
}
