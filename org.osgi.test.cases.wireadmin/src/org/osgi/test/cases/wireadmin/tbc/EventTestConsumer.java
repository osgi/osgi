package org.osgi.test.cases.wireadmin.tbc;

import org.osgi.service.wireadmin.*;

/**
 * Helps in testing event dispatchment in the wire admin
 * 
 * @author Vasil Panushev
 */
public class EventTestConsumer implements Consumer {
	private boolean	crash			= false;
	private boolean	pcCrashed		= false;
	private boolean	updatedCrashed	= false;

	public EventTestConsumer(boolean cr) {
		this.crash = cr;
	}

	public void producersConnected(Wire[] wires) {
		if (crash && !pcCrashed) {
			pcCrashed = true;
			throw new RuntimeException("testing");
		}
	}

	public void updated(Wire wire, Object data) {
		if (crash && !updatedCrashed) {
			updatedCrashed = true;
			throw new RuntimeException("testing");
		}
	}
}