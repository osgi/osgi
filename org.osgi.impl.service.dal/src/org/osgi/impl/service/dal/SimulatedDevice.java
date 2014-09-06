/*
 * Copyright (c) 2014 ProSyst Software GmbH. All Rights Reserved.
 *
 * This CODE is owned by ProSyst Software GmbH,
 * and is being distributed to OSGi PARTICIPANTS as MATERIALS
 * under the terms of section 1 of the OSGi Alliance Inc. Intellectual Property Rights Policy,
 * Amended and Restated as of May 23, 2011.
 */

package org.osgi.impl.service.dal;

import java.util.Dictionary;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceFactory;
import org.osgi.service.dal.Device;
import org.osgi.service.dal.DeviceException;
import org.osgi.service.dal.DevicePermission;
import org.osgi.service.dal.Function;

final class SimulatedDevice extends SimulatedService implements Device, ServiceFactory {

	private final SimulatedFunction[]	functions;

	public SimulatedDevice(Dictionary deviceProps, BundleContext bc, SimulatedFunction[] functions) {
		this.functions = functions;
		super.register(new String[] {Device.class.getName()}, deviceProps, bc);
	}

	public void remove() throws DeviceException, UnsupportedOperationException, SecurityException, IllegalStateException {
		SecurityManager securityManager = System.getSecurityManager();
		if (null != securityManager) {
			securityManager.checkPermission(
					new DevicePermission(this, DevicePermission.REMOVE));
		}
		super.serviceReg.unregister();
		if (null != this.functions) {
			for (int i = 0; i < this.functions.length; i++) {
				this.functions[i].remove();
			}
		}
	}

	public Object getServiceProperty(String propName) throws IllegalArgumentException {
		Object value = super.serviceRef.getProperty(propName);
		if (null == value) {
			throw new IllegalArgumentException("The property name is missing: " + propName);
		}
		return value;
	}

	public String[] getServicePropertyKeys() {
		return super.serviceRef.getPropertyKeys();
	}

	public SimulatedFunction getFunction(String functionUID) {
		if ((null == this.functions) || (null == functionUID)) {
			return null;
		}
		for (int i = 0; i < this.functions.length; i++) {
			if (functionUID.equals(this.functions[i].getServiceProperty(Function.SERVICE_UID))) {
				return this.functions[i];
			}
		}
		return null;
	}

}
