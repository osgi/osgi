package org.osgi.impl.service.wireadmin;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.wireadmin.Consumer;
import org.osgi.service.wireadmin.Producer;
import org.osgi.service.wireadmin.Wire;
import org.osgi.service.wireadmin.WireAdminEvent;

public class UpdateDispatcher extends Thread {
	private final List			updates;
	private volatile boolean	active	= true;
	private final WireAdminImpl	parent;

	public UpdateDispatcher(WireAdminImpl parent) {
		super("wireadmin update dispatcher");
		updates = new ArrayList();
		this.parent = parent;
		setDaemon(true);
	}

	public void run() {
		Object[] update = null;
		while (active) {
			synchronized (this) {
				while (updates.isEmpty() && active) {
					try {
						this.wait();
						//System.out.println("updates = " + updates);
					}
					catch (InterruptedException irx) {
						irx.printStackTrace();
					}
				}
				if (!active) {
					return;
				}
				update = (Object[]) updates.remove(0);
			}
			if (((Boolean) update[2]).booleanValue()) {
				try {
					((Producer) update[0])
							.consumersConnected((Wire[]) update[1]);
				}
				catch (Throwable t) {
					parent.bCast(WireAdminEvent.PRODUCER_EXCEPTION,
							(Wire) update[3], t);
				}
			}
			else {
				try {
					((Consumer) update[0])
							.producersConnected((Wire[]) update[1]);
				}
				catch (Throwable t) {
					parent.bCast(WireAdminEvent.CONSUMER_EXCEPTION,
							(Wire) update[3], t);
				}
			}
		}
	}

	public void addProducerUpdate(Producer receiver, Wire source, Wire[] wires) {
		if (receiver != null) {
			addUpdate(receiver, source, wires, true);
		}
	}

	public void addConsumerUpdate(Consumer receiver, Wire source, Wire[] wires) {
		if (receiver != null) {
			addUpdate(receiver, source, wires, false);
		}
	}

	private void addUpdate(Object receiver, Wire source, Wire[] wires,
			boolean producer) {
		if (!isAlive()) {
			start();
		}
		synchronized (this) {
			updates.add(new Object[] {receiver, wires,
					Boolean.valueOf(producer),
					source});
			this.notifyAll();
		}
	}

	public void stopDispatching() {
		this.active = false;
		synchronized (this) {
			this.notifyAll();
		}
	}
}
