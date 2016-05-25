package org.osgi.test.cases.zigbee.impl;

import junit.framework.Assert;

import org.osgi.service.zigbee.ZCLCluster;
import org.osgi.service.zigbee.ZigBeeException;
import org.osgi.service.zigbee.descriptors.ZigBeeSimpleDescriptor;

/**
 * for test purpose only.
 * 
 */
public class ZigBeeTestOSGiIdEndpointImpl extends ZigBeeEndpointImpl {

	private boolean notExportedHasBeenCalled = false;

	public ZigBeeTestOSGiIdEndpointImpl(short id, ZCLCluster[] inputs,
			ZCLCluster[] ouputs, ZigBeeSimpleDescriptor desc) {
		super(id, inputs, ouputs, desc);

	}

	public void notExported(ZigBeeException e) {
		notExportedHasBeenCalled = true;
		Assert.assertEquals(
				"the Endpoint Not exported is called with the wrong exception",
				ZigBeeException.OSGI_EXISTING_ID, e.getErrorCode());
	}

	public boolean notExportedHasBeenCalled() {
		return notExportedHasBeenCalled;
	}

}
