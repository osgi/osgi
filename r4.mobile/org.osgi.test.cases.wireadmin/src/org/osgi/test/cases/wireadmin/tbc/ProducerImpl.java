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
		    if (System.getProperty("dump.now") != null) {
			      System.out.println("**********************************************************************");
			      System.out.println("cc called and will set counter to " +
			    		  (wac.synchCounterx + 1));
			      System.out.println("producer is: " + pid + " " + hashCode());
		
			      if (wires != null) {
			        for (int i = 0; i < wires.length; i++) {
			          Wire wire = wires[i];
			          System.out.println("wire is: " + wire);
			          System.out.println("connected: " + wire.isConnected());
			          System.out.println("properties: " + wire.getProperties());
			        }
			      } else {
			        System.out.println("wires are null");
			      }
		          System.out.println("**********************************************************************");
//		          new Exception("Stack trace").printStackTrace();
            }
		wac.addInHashtable(pid, wires);
		wac.syncup(pid+ " " + wires); 
	}

	public Object polled(Wire wire) {
		return "";
	}
}