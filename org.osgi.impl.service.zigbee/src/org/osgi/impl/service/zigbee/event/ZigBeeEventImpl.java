
package org.osgi.impl.service.zigbee.event;

import java.util.Dictionary;
import org.osgi.service.zigbee.ZigBeeCluster;
import org.osgi.service.zigbee.ZigBeeEvent;

public class ZigBeeEventImpl implements ZigBeeEvent {

	private ZigBeeCluster	cluster;
	private Dictionary		events;

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
