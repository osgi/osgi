package org.osgi.test.cases.wireadmin.tbc;

import org.osgi.service.wireadmin.*;

public class FilteredConsumerImpl implements Consumer {
	private WireAdminControl	wac				= null;
	public static int			valuesReceived	= 0;

	public FilteredConsumerImpl(WireAdminControl wac) {
		this.wac = wac;
	}

	public void updated(Wire wire, Object value) {
		if ("42".equals(wire.getProperties().get(
				"org.osgi.test.wireadmin.property"))) {
			wac.log("value filtering test", "consumer received value " + value);
			valuesReceived++;
		}
		else {
			wac.log("value filtering test",
					"filter test consumer received update from unkown wire "
							+ wire + " value is " + value);
		}
	}

	public void producersConnected(Wire[] wires) {
	}
}
