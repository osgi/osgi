/*
 * Project Title : Wire Admin Test Case
 * Author        : Neviana Ducheva
 * Company       : ProSyst
 */
package org.osgi.test.cases.wireadmin.tb4;

import java.util.Hashtable;
import org.osgi.framework.*;
import org.osgi.service.wireadmin.*;

/**
 * 
 * 
 * @author Neviana Ducheva
 * @version
 * @since
 */
public class ProducerImpl implements BundleActivator, Producer {
	ServiceRegistration	regProducer;
	BundleContext		context;

	public ProducerImpl() {
	}

	public void start(BundleContext context) {
		this.context = context;
		Hashtable p = new Hashtable();
		p
				.put(
						org.osgi.service.wireadmin.WireConstants.WIREADMIN_PRODUCER_FLAVORS,
						new Class[] {String.class, Integer.class,
								Envelope.class});
		p.put(org.osgi.framework.Constants.SERVICE_PID,
				"producer.ProducerImplA");
		regProducer = context.registerService(Producer.class.getName(),
				new ProducerImpl(), p);
	}

	public void stop(BundleContext context) {
		try {
			if (regProducer != null) {
				regProducer.unregister();
				regProducer = null;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * 
	 * @param wires
	 */
	public void consumersConnected(Wire[] wires) {
	}

	/**
	 * 
	 * 
	 * @param wire
	 * @return
	 */
	public Object polled(Wire wire) {
		return "";
	}
}