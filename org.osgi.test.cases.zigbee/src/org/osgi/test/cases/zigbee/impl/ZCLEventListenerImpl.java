
package org.osgi.test.cases.zigbee.impl;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.zigbee.ZCLEventListener;
import org.osgi.service.zigbee.ZCLException;
import org.osgi.service.zigbee.ZigBeeEvent;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * Mocked test.
 * 
 * @author $Id$
 */
public class ZCLEventListenerImpl implements ZCLEventListener {

	private BundleContext		bc;
	private ServiceRegistration	sr;
	private ZigBeeEvent			lastReceivedZigBeeEvent	= null;

	/**
	 * @param bc
	 */
	public ZCLEventListenerImpl(BundleContext bc) {
		this.bc = bc;
	}

	/**
	 * Register the test event listener in the OSGi Service Registry.
	 */
	public void start() {
		bc.registerService(ZCLEventListener.class.getName(), this, null);
	}

	/**
	 * Unregister the test event listener from the OSGi Service Registry.
	 */
	public void stop() {
		if (sr != null) {
			sr.unregister();
		}
	}

	public void notifyEvent(ZigBeeEvent zigbeeEvent) {
		lastReceivedZigBeeEvent = zigbeeEvent;
		DefaultTestBundleControl.log(ZCLEventListener.class.getName() + " just received the following event: " + lastReceivedZigBeeEvent);
	}

	/**
	 * @return the lastReceivedZigBeeEvent.
	 */
	public ZigBeeEvent getLastReceivedZigBeeEvent() {
		return lastReceivedZigBeeEvent;
	}

	public void onFailure(ZCLException e) {
		// TODO Auto-generated method stub
	}

	public void notifyTimeOut(int timeout) {
		// TODO Auto-generated method stub
	}

}
