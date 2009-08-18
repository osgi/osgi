package org.osgi.test.cases.wireadmin.junit;

import org.osgi.service.wireadmin.Producer;
import org.osgi.service.wireadmin.Wire;

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
			WireAdminControl
					.log("**********************************************************************");
			WireAdminControl
					.log("consumersConnected called and will set counter to "
							+
			    		  (wac.synchCounterx + 1));
			WireAdminControl.log("producer is: " + pid + " " + hashCode());
		
			      if (wires != null) {
			        for (int i = 0; i < wires.length; i++) {
			          Wire wire = wires[i];
					WireAdminControl.log("wire is: " + wire);
					WireAdminControl.log("connected: " + wire.isConnected());
					WireAdminControl.log("properties: " + wire.getProperties());
			        }
			      } else {
				WireAdminControl.log("wires are null");
			      }
			WireAdminControl
					.log("**********************************************************************");
//		          new Exception("Stack trace").printStackTrace();
            }
		wac.addInHashtable(pid, wires);
		wac.syncup(pid+ " " + wires); 
	}

	public Object polled(Wire wire) {
		return "";
	}
}