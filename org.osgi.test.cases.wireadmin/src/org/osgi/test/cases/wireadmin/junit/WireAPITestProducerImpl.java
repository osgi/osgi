package org.osgi.test.cases.wireadmin.junit;

import org.osgi.service.wireadmin.Producer;
import org.osgi.service.wireadmin.Wire;

public class WireAPITestProducerImpl implements Producer {
	private volatile boolean	throwsException;

	public Object polled(Wire wire) {
		if ("42".equals(wire.getProperties().get(
				"org.osgi.test.wireadmin.property"))) {
			if (throwsException) {
				WireAdminControl
						.log("Producer.polled called for correct wire. Throwing exception ...");
				throw new RuntimeException("test exception");
			}
			WireAdminControl
					.log("Producer.polled called for correct wire. Returning '42'");
			return new Integer("42");
		}
		WireAdminControl.log("Producer.polled called for incorrect wire "
				+ wire + ". Crashing now!");
		throw new RuntimeException(
				"Wire API test producer polled from incorrect wire!");
	}

	public void consumersConnected(Wire[] wires) {
		// empty
	}

	void setThrowsException(boolean state) {
		throwsException = state;
	}
}
