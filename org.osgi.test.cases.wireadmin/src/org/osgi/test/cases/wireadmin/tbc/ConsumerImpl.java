/*
 * Project Title : Wire Admin Test Case
 * Author        : Neviana Ducheva
 * Company       : ProSyst
 */
package org.osgi.test.cases.wireadmin.tbc;

import org.osgi.service.wireadmin.*;

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
				      System.out.println("**********************************************************************");
				      System.out.println("pc called and will set counter to " +
				 (wac.synchCounterx+1));
				      System.out.println("consumer is: " + pid + " " + hashCode());
				      if (wires != null) {
				        for (int i = 0; i < wires.length; i++) {
				          Wire wire = wires[i];
				          System.out.println("wire is: " + wire);
				          System.out.println("connected: " + wire.isConnected());
				          System.out.println("properties: " + wire.getProperties());
				        }
				        System.out.println("**********************************************************************");
				      } else {
				        System.out.println("wires are null");
				      }
//			        new Exception("Stack trace").printStackTrace();
		    }
		wac.addInHashtable(pid, wires);
		wac.syncup(pid + " " + wires);
	}

	public void updated(Wire wire, Object value) {
	}
}