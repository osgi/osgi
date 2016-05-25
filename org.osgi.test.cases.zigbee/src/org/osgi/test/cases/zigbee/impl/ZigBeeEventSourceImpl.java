
package org.osgi.test.cases.zigbee.impl;

import org.osgi.framework.BundleContext;
import org.osgi.service.zigbee.ZCLEventListener;
import org.osgi.service.zigbee.ZigBeeEvent;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Mocked test event source.
 * 
 * @author $Id$
 */
public class ZigBeeEventSourceImpl implements Runnable {

	private BundleContext	bc;
	private ZigBeeEvent		zigbeeEvent;
	private Thread			thread;
	private ServiceTracker	serviceTracker;

	/**
	 * @param bc
	 * @param zigbeeEvent
	 */
	public ZigBeeEventSourceImpl(BundleContext bc, ZigBeeEvent zigbeeEvent) {
		this.bc = bc;
		this.zigbeeEvent = zigbeeEvent;
	}

	/**
	 * Launch this testEventSource.
	 */
	public void start() {
		DefaultTestBundleControl.log(ZigBeeEventSourceImpl.class.getName()
				+ " start.");
		serviceTracker = new ServiceTracker(bc,
				ZCLEventListener.class.getName(),
				null);
		serviceTracker.open();
		thread = new Thread(this, ZigBeeEventSourceImpl.class.getName()
				+ " - Whiteboard");
		thread.start();
	}

	/**
	 * Terminate this testEventSource.
	 */
	public void stop() {
		DefaultTestBundleControl.log(ZigBeeEventSourceImpl.class.getName()
				+ " stop.");
		serviceTracker.close();
		thread = null;
	}

	public synchronized void run() {
		DefaultTestBundleControl.log(ZigBeeEventSourceImpl.class.getName()
				+ " run.");
		Thread current = Thread.currentThread();
		int n = 0;
		while (current == thread) {
			Object[] listeners = serviceTracker.getServices();
			// log("listeners: " + listeners);

			// try {
			// ServiceReference[] srs = bc.getAllServiceReferences(null,
			// null);
			// log("srs: " + srs);
			// if (srs != null) {
			// for (ServiceReference sr : srs) {
			// log("sr: " + sr);
			// }
			// }
			// } catch (InvalidSyntaxException e1) {
			// e1.printStackTrace();
			// }

			if (listeners != null && listeners.length > 0) {
				if (n >= listeners.length) {
					n = 0;
				}
				ZCLEventListener aZCLEventListener = (ZCLEventListener) listeners[n++];

				DefaultTestBundleControl.log(ZigBeeEventSourceImpl.class
						.getName()
						+ " is sending the following event: "
						+ zigbeeEvent);

				aZCLEventListener.notifyEvent(zigbeeEvent);
			}
			try {
				int waitinms = 1000;
				// DefaultTestBundleControl.log("wait(" + waitinms + ")");
				wait(waitinms);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
