package org.osgi.test.cases.wireadmin.junit;

import org.osgi.service.wireadmin.*;

/**
 * Helps in testing event dispatchment in the wire admin
 * 
 * @author Vasil Panushev
 */
public class EventTestProducer implements Producer {
	private boolean	crash			= false;
	private boolean	ccCrashed		= false;
	private boolean	polledCrashed	= false;

	public EventTestProducer(boolean cr) {
		this.crash = cr;
	}

	public void consumersConnected(Wire[] wires) {
		if (crash & !ccCrashed) {
			ccCrashed = true;
			throw new RuntimeException("testing");
		}
	}

	public Object polled(Wire wire) {
		if (crash && !polledCrashed) {
			polledCrashed = true;
			throw new RuntimeException("testing");
		}
		return "42";
	}
}