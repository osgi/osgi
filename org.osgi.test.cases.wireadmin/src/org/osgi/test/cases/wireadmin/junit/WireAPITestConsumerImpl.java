package org.osgi.test.cases.wireadmin.junit;

import org.osgi.service.wireadmin.Consumer;
import org.osgi.service.wireadmin.Wire;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public class WireAPITestConsumerImpl implements Consumer {
	private volatile Object	value	= null;

	@Override
	public void updated(Wire wire, Object v) {
		if ("42".equals(wire.getProperties().get(
				"org.osgi.test.wireadmin.property"))) {
			DefaultTestBundleControl.log("Consumer.updated received value " + v
					+ " from correct wire");
			this.value = v;
			return;
		}
		DefaultTestBundleControl.log("Consumer.updated received value " + v
				+ " from incorrect wire " + wire);
		this.value = null;
	}

	@Override
	public void producersConnected(Wire[] wires) {
		// empty
	}

	Object getValue() {
		return value;
	}
}
