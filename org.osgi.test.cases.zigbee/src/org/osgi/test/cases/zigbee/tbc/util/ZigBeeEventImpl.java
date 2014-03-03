
package org.osgi.test.cases.zigbee.tbc.util;

import org.osgi.service.zigbee.ZCLCluster;
import org.osgi.service.zigbee.ZigBeeEvent;

/**
 * Mocked impl of ZigBeeEvent.
 */
public class ZigBeeEventImpl implements ZigBeeEvent {

	private ZCLCluster	cluster;
	private Object		value;

	/**
	 * @param cluster
	 * @param value
	 */
	public ZigBeeEventImpl(ZCLCluster cluster, Object value) {
		this.cluster = cluster;
		this.value = value;
	}

	public ZCLCluster getCluster() {
		return cluster;
	}

	public Object getValue() {
		return value;
	}

	public String toString() {
		return ZigBeeEventImpl.class.getName() + "[cluster: " + cluster + ", value:" + value + "]";
	}

}
