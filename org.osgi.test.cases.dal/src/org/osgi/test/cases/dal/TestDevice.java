/*
 * Copyright (c) 2014 ProSyst Software GmbH. All Rights Reserved.
 *
 * This CODE is owned by ProSyst Software GmbH,
 * and is being distributed to OSGi PARTICIPANTS as MATERIALS
 * under the terms of section 1 of the OSGi Alliance Inc. Intellectual Property Rights Policy,
 * Amended and Restated as of May 23, 2011.
 */

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
