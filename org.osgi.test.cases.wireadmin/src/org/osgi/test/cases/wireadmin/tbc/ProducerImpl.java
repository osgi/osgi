package org.osgi.test.cases.wireadmin.tbc;

import org.osgi.service.wireadmin.*;

/**
 * A simple producer for test purposes
 * 
 * @author Neviana Ducheva
 */
public class ProducerImpl implements Producer {
	WireAdminControl	wac;
	String				pid;

	public ProducerImpl(WireAdminControl wac, String pid) {
		this.wac = wac;
		this.pid = pid;
	}

	public void consumersConnected(Wire[] wires) {
		//    if (System.getProperty("dump.now") != null) {
		//      System.out.println("**********************************************************************");
		//      System.out.println("cc called and will set counter to " +
		// (wac.synchCounter + 1));
		//      System.out.println("producer is: " + pid + " " + hashCode());
		//      System.out.println("**********************************************************************");
		//    }
		wac.addInHashtable(pid, wires);
		wac.synchCounter++;
	}

	public Object polled(Wire wire) {
		return "";
	}
}