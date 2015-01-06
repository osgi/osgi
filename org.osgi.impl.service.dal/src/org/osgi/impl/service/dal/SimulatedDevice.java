/*
 * Copyright (c) 2014 ProSyst Software GmbH. All Rights Reserved.
 *
 * This CODE is owned by ProSyst Software GmbH,
 * and is being distributed to OSGi PARTICIPANTS as MATERIALS
 * under the terms of section 1 of the OSGi Alliance Inc. Intellectual Property Rights Policy,
 * Amended and Restated as of May 23, 2011.
 */

package org.osgi.impl.service.dal;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceFactory;
import org.osgi.service.dal.Device;
import org.osgi.service.dal.DevicePermission;

final class SimulatedDevice extends SimulatedService implements Device, ServiceFactory {

	private final SimulatedFunction[]	functions;

	public SimulatedDevice(Dictionary deviceProps, BundleContext bc, SimulatedFunction[] functions) {
		this.functions = functions;
		super.register(new String[] {Device.class.getName()}, deviceProps, bc);
	}

	public void remove() {
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

	public Object getServiceProperty(String propName) {
		Object value = super.serviceRef.getProperty(propName);
		if (null == value) {
			throw new IllegalArgumentException("The property name is missing: " + propName);
		}
		return value;
	}

	public String[] getServicePropertyKeys() {
		return super.serviceRef.getPropertyKeys();
	}

	public SimulatedFunction[] getFunctions(String functionClassName) {
		if ((null == this.functions) || (null == functionClassName)) {
			return null;
		}
		List result = new ArrayList();
		for (int i = 0; i < this.functions.length; i++) {
			if (contains(
					(String[]) this.functions[i].getServiceProperty(Constants.OBJECTCLASS),
					functionClassName)) {
				result.add(this.functions[i]);
			}
		}
		return result.isEmpty() ? null :
				(SimulatedFunction[]) result.toArray(new SimulatedFunction[result.size()]);
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
