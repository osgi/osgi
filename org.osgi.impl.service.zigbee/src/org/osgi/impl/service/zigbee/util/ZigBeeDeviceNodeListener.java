
package org.osgi.impl.service.zigbee.util;

import org.osgi.service.zigbee.ZigBeeEndpoint;
import org.osgi.service.zigbee.ZigBeeNode;

/**
 * @author $Id$
 */
public interface ZigBeeDeviceNodeListener {

	/**
	 * @param node
	 */
	public void addNode(ZigBeeNode node);

	/**
	 * @param node
	 */
	public void removeNode(ZigBeeNode node);

	public void addEndpoint(ZigBeeEndpoint ep);

	public void removeEndpoint(ZigBeeEndpoint ep);
}
