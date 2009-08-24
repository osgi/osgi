package org.osgi.test.cases.wireadmin.junit;

import org.osgi.service.wireadmin.Producer;
import org.osgi.service.wireadmin.Wire;

/**
 * Helps in testing event dispatchment in the wire admin
 * 
 * @author Vasil Panushev
 */
public class EventTestProducer implements Producer {
	private final boolean	crash;
	private boolean			ccCrashed;
	private boolean			polledCrashed;

	public EventTestProducer(boolean crash) {
		ccCrashed = false;
		polledCrashed = false;
		this.crash = crash;
	}

	public synchronized void consumersConnected(Wire[] wires) {
		if (crash & !ccCrashed) {
			ccCrashed = true;
			throw new RuntimeException("testing");
		}
	}

	public synchronized Object polled(Wire wire) {
		if (crash && !polledCrashed) {
			polledCrashed = true;
			throw new RuntimeException("testing");
		}
		return "42";
	}
}