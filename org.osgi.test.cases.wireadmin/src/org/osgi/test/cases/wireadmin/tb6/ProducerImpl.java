/*
 * Project Title : Wire Admin Test Case
 * Author        : Neviana Ducheva
 * Company       : ProSyst
 */
package org.osgi.test.cases.wireadmin.tb6;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.wireadmin.Envelope;
import org.osgi.service.wireadmin.Producer;
import org.osgi.service.wireadmin.Wire;
import org.osgi.service.wireadmin.WireConstants;

/**
 * 
 * 
 * @author Neviana Ducheva
 * @version
 * @since
 */
public class ProducerImpl implements BundleActivator, Producer {
	public void start(BundleContext context) {
		Hashtable p = new Hashtable();
		p.put(WireConstants.WIREADMIN_PRODUCER_FLAVORS, new Class[] {
				String.class, Integer.class, Envelope.class});
		p.put(Constants.SERVICE_PID, "producer.ProducerImplC");
		p.put(WireConstants.WIREADMIN_PRODUCER_SCOPE, new String[] {"*"});
		context.registerService(Producer.class.getName(), this, p);
	}

	public void stop(BundleContext context) {
		// service unregistered by framework
	}

	/**
	 * 
	 * 
	 * @param wires
	 */
	public void consumersConnected(Wire[] wires) {
		// empty
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