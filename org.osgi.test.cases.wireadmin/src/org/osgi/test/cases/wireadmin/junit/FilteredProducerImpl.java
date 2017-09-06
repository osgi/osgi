package org.osgi.test.cases.wireadmin.junit;

import org.osgi.service.wireadmin.Producer;
import org.osgi.service.wireadmin.Wire;
import org.osgi.test.support.sleep.Sleep;

public class FilteredProducerImpl implements Producer {
	private volatile Wire	wire	= null;

	/**
	 * This method should not be called in the test.
	 *
	 * @return value out of range to indicate the wrong call
	 */
	public Object polled(Wire w) {
		return Integer.valueOf(100);
	}

	public void consumersConnected(Wire[] wires) {
		if (wires != null)
			this.wire = wires[0];
	}

	void updateWire(int delta) {
		for (int i = 0; i < 10; i += delta) {
			wire.update(Integer.valueOf(i));
		}
	}

	void updateWireDelayed(int delta, long delay) {
		for (int i = 0; i < 10; i += delta) {
			long processTime = System.currentTimeMillis();
			wire.update(Integer.valueOf(i));
			processTime = System.currentTimeMillis() - processTime;
			try {
				Sleep.sleep(delay - processTime);
			}
			catch (InterruptedException e) {
				// ignore
			}
		}
	}

	Wire getWire() {
		return wire;
	}
}
