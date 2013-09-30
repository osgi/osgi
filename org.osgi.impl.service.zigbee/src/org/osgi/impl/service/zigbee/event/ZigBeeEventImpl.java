
package org.osgi.impl.service.zigbee.event;

import org.osgi.service.zigbee.ZigBeeCluster;
import org.osgi.service.zigbee.ZigBeeEvent;

/**
 * Mocked impl of ZigBeeEvent.
 */
public class ZigBeeEventImpl implements ZigBeeEvent {

	private ZigBeeCluster	cluster;
	private Object			value;

	/**
	 * @param cluster
	 * @param value
	 */
	public ZigBeeEventImpl(ZigBeeCluster cluster, Object value) {
		this.cluster = cluster;
		this.value = value;
	}

	public ZigBeeCluster getCluster() {
		return cluster;
	}

	public Object getValue() {
		return value;
	}

}
