
package org.osgi.test.cases.zigbee.impl;

import org.osgi.service.zigbee.ZCLCluster;
import org.osgi.service.zigbee.descriptors.ZigBeeSimpleDescriptor;

/**
 * 
 *
 * TODO Add Javadoc comment for this type.
 * 
 * @author $Id$
 */
public class ZigBeeEndpointConf extends ZigBeeEndpointImpl {

	private ZigBeeSimpleDescriptor desc;

	public ZigBeeEndpointConf(short id, ZCLCluster[] inputs,
			ZCLCluster[] ouputs, ZigBeeSimpleDescriptor desc) {
		super(id, inputs, ouputs, desc);
		this.desc = desc;

		// TODO Auto-generated constructor stub
	}

	public ZigBeeSimpleDescriptor getSimpleDescriptor() {
		// TODO Auto-generated method stub
		return desc;
	}

}
