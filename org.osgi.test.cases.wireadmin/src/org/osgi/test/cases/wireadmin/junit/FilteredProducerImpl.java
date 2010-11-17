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

	void updateWire(int delta) {
		for (int i = 0; i < 10; i += delta) {
			wire.update(new Integer(i));
		}
	}

	void updateWireDelayed(int delta, long delay) {
		for (int i = 0; i < 10; i += delta) {
			long processTime = System.currentTimeMillis();
			wire.update(new Integer(i));
			processTime = System.currentTimeMillis() - processTime;
			try {
				Thread.sleep(delay - processTime);
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
