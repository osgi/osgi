/*
 * Project Title : Wire Admin Test Case
 * Author        : Neviana Ducheva
 * Company       : ProSyst
 */
package org.osgi.test.cases.wireadmin.tb1;

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
public class ConsumerImpl implements BundleActivator, Consumer {
	ServiceRegistration	regConsumer;
	BundleContext		context;

	public ConsumerImpl() {
	}

	public void start(BundleContext context) {
		this.context = context;
		Hashtable p = new Hashtable();
		p
				.put(
						org.osgi.service.wireadmin.WireConstants.WIREADMIN_CONSUMER_FLAVORS,
						new Class[] {String.class, Integer.class,
								Envelope.class});
		p.put(org.osgi.framework.Constants.SERVICE_PID,
				"consumer.ConsumerImplA");
		regConsumer = context.registerService(Consumer.class.getName(),
				new ConsumerImpl(), p);
	}

	public void stop(BundleContext context) {
		try {
			if (regConsumer != null) {
				regConsumer.unregister();
				regConsumer = null;
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
	public void producersConnected(Wire[] wires) {
	}

	/**
	 * 
	 * 
	 * @param wire
	 * @param value
	 */
	public void updated(Wire wire, Object value) {
	}
}