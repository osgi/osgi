
package org.osgi.test.cases.zigbee.impl;

import org.osgi.service.zigbee.ZCLAttribute;
import org.osgi.service.zigbee.descriptions.ZCLClusterDescription;

/**
 * 
 *
 * TODO Add Javadoc comment for this type.
 * 
 * @author $Id$
 */
public class ZCLClusterConf extends ZCLClusterImpl {

	private int[] commandIds;

	public ZCLClusterConf(int[] commandIds, ZCLAttribute[] attributes,
			ZCLClusterDescription desc) {
		super(commandIds, attributes, desc);
		this.commandIds = commandIds;
	}

	public int[] getCommandIds() {
		return commandIds;
	}

	public ZCLAttribute[] getAttributes() {
		return attributes;
	}

}
