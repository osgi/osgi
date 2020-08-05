package org.osgi.test.cases.wireadmin.junit;

import org.osgi.service.wireadmin.Producer;
import org.osgi.service.wireadmin.Wire;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public class WireAPITestProducerImpl implements Producer {
	private volatile boolean	throwsException;

	@Override
	public Object polled(Wire wire) {
		if ("42".equals(wire.getProperties().get(
				"org.osgi.test.wireadmin.property"))) {
			if (throwsException) {
				DefaultTestBundleControl
						.log("Producer.polled called for correct wire. Throwing exception ...");
				throw new RuntimeException("test exception");
			}
			DefaultTestBundleControl
					.log("Producer.polled called for correct wire. Returning '42'");
			return Integer.valueOf("42");
		}
		DefaultTestBundleControl.log("Producer.polled called for incorrect wire "
				+ wire + ". Crashing now!");
		throw new RuntimeException(
				"Wire API test producer polled from incorrect wire!");
	}

	@Override
	public void consumersConnected(Wire[] wires) {
		// empty
	}

	void setThrowsException(boolean state) {
		throwsException = state;
	}
}
