
package org.osgi.test.cases.zigbee.tbc.util;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.zigbee.ZigBeeEvent;
import org.osgi.service.zigbee.ZigBeeEventListener;
import org.osgi.service.zigbee.ZigBeeException;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * Mocked test event listener.
 */
public class ZigBeeEventListenerImpl implements ZigBeeEventListener {

	private BundleContext		bc;
	private ServiceRegistration	sr;
	private ZigBeeEvent			lastReceivedZigBeeEvent	= null;

	/**
	 * @param bc
	 */
	public ZigBeeEventListenerImpl(BundleContext bc) {
		this.bc = bc;
	}

	/**
	 * Register the test event listener in the OSGi Service Registry.
	 */
	public void start() {
		bc.registerService(ZigBeeEventListener.class.getName(), this, null);
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
		DefaultTestBundleControl.log(ZigBeeEventListenerImpl.class.getName() + " just received the following event: " + lastReceivedZigBeeEvent);
	}

	/**
	 * @return the lastReceivedZigBeeEvent.
	 */
	public ZigBeeEvent getLastReceivedZigBeeEvent() {
		return lastReceivedZigBeeEvent;
	}

	public void onFailure(ZigBeeException e) {
		// TODO Auto-generated method stub
	}

	public void notifyTimeOut(int timeout) {
		// TODO Auto-generated method stub
	}

}
