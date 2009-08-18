package org.osgi.test.cases.wireadmin.junit;

import org.osgi.service.wireadmin.Producer;
import org.osgi.service.wireadmin.Wire;

public class FilteredProducerImpl implements Producer {
	private volatile Wire	wire	= null;

	/**
	 * This method should not be called in the test.
	 * 
	 * @return value out of range to indicate the wrong call
	 */
	public Object polled(Wire w) {
		return new Integer(100);
	}

	public void consumersConnected(Wire[] wires) {
		if (wires != null)
			this.wire = wires[0];
	}

	void updateWire(int delta, boolean wait) {
		long processTime = 0;
		for (int i = 0; i < 10; i += delta) {
			if (wait) {
				try {
					if (100 * i - processTime > 0) {
						Thread.sleep((100 * i) - processTime);
					}
				}
				catch (Exception e) {
					// ignored
				}
			}
			processTime = System.currentTimeMillis();
			wire.update(new Integer(i));
			processTime = System.currentTimeMillis() - processTime;
		}
	}

	Wire getWire() {
		return wire;
	}
}
