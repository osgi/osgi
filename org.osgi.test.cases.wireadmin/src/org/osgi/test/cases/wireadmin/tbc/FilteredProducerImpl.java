package org.osgi.test.cases.wireadmin.tbc;

import org.osgi.service.wireadmin.Producer;
import org.osgi.service.wireadmin.Wire;

public class FilteredProducerImpl implements Producer {

	protected Wire wire = null;

	/**
	 * This method should not be called in the test. 
	 * 
	 * @return value out of range to indicate the wrong call
	 */
	public Object polled(Wire wire) {
		return new Integer(100);
	}

	public void consumersConnected(Wire[] wires) {
		if (wires != null)
			this.wire = wires[0];
	}

	public void updateWire(int delta, boolean wait) {
		for (int i = 0; i < 10; i += delta) {
			if (wait) {
				try {
					Thread.sleep(100 * i);
				} catch (Exception e) {
				}
			}
			wire.update(new Integer(i));
		}
	}
}
