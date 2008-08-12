package org.osgi.test.cases.wireadmin.tbc;

import org.osgi.service.wireadmin.*;

public class WireAPITestProducerImpl implements Producer {
	protected boolean			throwsException;
	private WireAdminControl	wac	= null;

	public WireAPITestProducerImpl(WireAdminControl wac) {
		this.wac = wac;
	}

	public Object polled(Wire wire) {
		if ("42".equals(wire.getProperties().get(
				"org.osgi.test.wireadmin.property"))) {
			if (throwsException) {
				wac
						.log("wire api test",
								"Producer.polled called for correct wire. Throwing exception ...");
				throw new RuntimeException("test exception");
			}
			else {
				wac
						.log("wire api test",
								"Producer.polled called for correct wire. Returning '42'");
			}
		}
		else {
			wac.log("wire api test",
					"Producer.polled called for incorrect wire " + wire
							+ ". Crashing now!");
			throw new RuntimeException(
					"Wire API test producer polled from incorrect wire!");
		}
		return new Integer("42");
	}

	public void consumersConnected(Wire[] wires) {
	}
}
