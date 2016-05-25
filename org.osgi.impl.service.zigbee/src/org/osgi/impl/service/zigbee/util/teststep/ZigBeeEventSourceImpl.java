
package org.osgi.impl.service.zigbee.util.teststep;

import org.osgi.framework.BundleContext;
import org.osgi.service.zigbee.ZCLEventListener;
import org.osgi.service.zigbee.ZigBeeEvent;
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
		System.out.println(this.getClass().getName() + " start");
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
		System.out.println(this.getClass().getName() + " stop.");
		serviceTracker.close();
		thread = null;
	}

	public synchronized void run() {
		System.out.println(this.getClass().getName() + " run");
		Thread current = Thread.currentThread();
		int n = 0;
		while (current == thread) {
			Object[] listeners = serviceTracker.getServices();

			if (listeners != null && listeners.length > 0) {
				if (n >= listeners.length) {
					n = 0;
				}
				ZCLEventListener aZCLEventListener = (ZCLEventListener) listeners[n++];

				System.out.println(this.getClass().getName()
						+ " is sending the following event: " + zigbeeEvent);

				aZCLEventListener.notifyEvent(zigbeeEvent);
			}
			try {
				int waitinms = 1000;
				System.out.println(this.getClass().getName() + "wait("
						+ waitinms + ")");
				wait(waitinms);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
