/*
 * Project Title : Wire Admin Test Case
 * Author        : Neviana Ducheva
 * Company       : ProSyst
 */
package org.osgi.test.cases.wireadmin.junit;

import org.osgi.service.wireadmin.Consumer;
import org.osgi.service.wireadmin.Wire;

/**
 * A simple consumer implementation for test purposes
 * 
 * @author Neviana Ducheva
 */
public class ConsumerImpl implements Consumer {
	WireAdminControl	wac;
	String				pid;

	public ConsumerImpl(WireAdminControl wac, String pid) {
		this.wac = wac;
		this.pid = pid;
	}

	public void producersConnected(Wire[] wires) {
		if (System.getProperty("dump.now") != null) {
			WireAdminControl
					.log("**********************************************************************");
			WireAdminControl
					.log("producersConnected called and will set counter to "
							+ (wac.synchCounterx + 1));
			WireAdminControl.log("consumer is: " + pid + " " + hashCode());
			if (wires != null) {
				for (int i = 0; i < wires.length; i++) {
					Wire wire = wires[i];
					WireAdminControl.log("wire is: " + wire);
					WireAdminControl.log("connected: " + wire.isConnected());
					WireAdminControl.log("properties: " + wire.getProperties());
				}
			}
			else {
				WireAdminControl.log("wires are null");
			}
			WireAdminControl
					.log("**********************************************************************");
			// new Exception("Stack trace").printStackTrace();
		}
		wac.addInHashtable(pid, wires);
		wac.syncup(pid + " " + wires);
	}

	public void updated(Wire wire, Object value) {
		// empty
	}
}