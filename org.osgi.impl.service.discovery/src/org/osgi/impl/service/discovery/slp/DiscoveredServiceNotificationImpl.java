/**
 * 
 */
package org.osgi.impl.service.discovery.slp;

import org.osgi.service.discovery.DiscoveredServiceNotification;
import org.osgi.service.discovery.ServiceEndpointDescription;

/**
 * @author kt32483
 * 
 */
public class DiscoveredServiceNotificationImpl implements
		DiscoveredServiceNotification {

	private ServiceEndpointDescription descr;
	private int type;

	public DiscoveredServiceNotificationImpl(ServiceEndpointDescription sed,
			int t) {
		descr = sed;
		type = t;
	}

	/**
	 * @see org.osgi.service.discovery.DiscoveredServiceNotification#getServiceEndpointDescription()
	 */
	public ServiceEndpointDescription getServiceEndpointDescription() {
		return descr;
	}

	/**
	 * @see org.osgi.service.discovery.DiscoveredServiceNotification#getType()
	 */
	public int getType() {
		return type;
	}

}
