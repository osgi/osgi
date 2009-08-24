package org.osgi.test.cases.wireadmin.junit;

import org.osgi.service.wireadmin.Consumer;
import org.osgi.service.wireadmin.Wire;

/**
 * Helps in testing event dispatchment in the wire admin
 * 
 * @author Vasil Panushev
 */
public class EventTestConsumer implements Consumer {
	private final boolean	crash;
	private boolean			pcCrashed;
	private boolean			updatedCrashed;

	public EventTestConsumer(boolean crash) {
		pcCrashed = false;
		updatedCrashed = false;
		this.crash = crash;
	}

	public synchronized void producersConnected(Wire[] wires) {
		if (crash && !pcCrashed) {
			pcCrashed = true;
			throw new RuntimeException("testing");
		}
	}

	public synchronized void updated(Wire wire, Object data) {
		if (crash && !updatedCrashed) {
			updatedCrashed = true;
			throw new RuntimeException("testing");
		}
	}
}