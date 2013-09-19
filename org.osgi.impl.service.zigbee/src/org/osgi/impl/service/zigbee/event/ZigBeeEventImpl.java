package org.osgi.impl.service.zigbee.event;

import java.util.Dictionary;
import org.osgi.service.zigbee.ZigBeeCluster;
import org.osgi.service.zigbee.ZigBeeEvent;

/**
 * Mocked impl of ZigBeeEvent.
 */
public class ZigBeeEventImpl implements ZigBeeEvent {

	private ZigBeeCluster	cluster;
	private Dictionary		events;

	/**
	 * @param cluster
	 * @param events
	 */
	public ZigBeeEventImpl(ZigBeeCluster cluster, Dictionary events) {
		this.cluster = cluster;
		this.events = events;
	}

	public ZigBeeCluster getCluster() {
		return cluster;
	}

	public Dictionary getAttributesEvents() {
		return events;
	}

}
