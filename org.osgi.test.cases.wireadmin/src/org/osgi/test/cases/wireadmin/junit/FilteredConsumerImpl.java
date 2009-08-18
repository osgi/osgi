package org.osgi.test.cases.wireadmin.junit;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.wireadmin.Consumer;
import org.osgi.service.wireadmin.Wire;

public class FilteredConsumerImpl implements Consumer {
	private List	valuesReceived	= new ArrayList();

	public void updated(Wire wire, Object value) {
		if ("42".equals(wire.getProperties().get(
				"org.osgi.test.wireadmin.property"))) {
			WireAdminControl.log("consumer received value " + value);
			synchronized (this) {
				valuesReceived.add(value);
			}
			return;
		}
		WireAdminControl
				.log("filter test consumer received update from unkown wire "
						+ wire + " value is " + value);
	}

	public void producersConnected(Wire[] wires) {
		// empty
	}

	synchronized int numberValuesReceived() {
		return valuesReceived.size();
	}

	synchronized List resetValuesReceived() {
		List result = valuesReceived;
		valuesReceived = new ArrayList();
		return result;
	}
}
