package org.osgi.test.cases.wireadmin.tbc;

import org.osgi.service.wireadmin.Consumer;
import org.osgi.service.wireadmin.Wire;

public class WireAPITestConsumerImpl implements Consumer {

	private WireAdminControl wac = null;

	public WireAPITestConsumerImpl(WireAdminControl wac) {
		this.wac = wac;
	}

	public void updated(Wire wire, Object value) {
		if ("42".equals(wire.getProperties().get("org.osgi.test.wireadmin.property"))) {
			wac.log("wire api test", "Consumer.updated received value " + value + " from correct wire");
		} else {
			wac.log("wire api test", "Consumer.updated received value " + value + " from incorrect wire " + wire);
		}
	}

	public void producersConnected(Wire[] wires) {
	}

}
