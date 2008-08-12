package org.osgi.test.cases.wireadmin.tbc;

import java.util.*;
import org.osgi.framework.*;
import org.osgi.service.wireadmin.*;

/**
 * Contains some helper methods for registering/unregistering producers
 * 
 * $Log$
 * Revision 1.5  2005/05/30 13:41:07  polivier
 * Add test in case of no wires to delete
 *
 * Revision 1.4  2004/12/03 09:12:32  pkriens
 * Added service project
 * Revision 1.3 2004/11/03 11:47:09 pkriens Format and
 * clean up of warnings Revision 1.2 2004/11/03 10:55:32 pkriens Format and
 * clean up of warnings Revision 1.1 2004/07/07 13:15:26 pkriens *** empty log
 * message ***
 * 
 * Revision 1.2 2003/11/14 07:18:29 vpanushev resolved issues 243 and 246
 * 
 * 
 * @author Neviana Ducheva
 */
public class Helper {
	BundleContext				context;
	private Hashtable			consumers			= null;
	private Hashtable			producers			= null;
	private ServiceRegistration	regConsumer			= null;
	private ServiceRegistration	regProducer			= null;
	private ServiceRegistration	regConsumerEvents	= null;
	private ServiceRegistration	regProducerEvents	= null;
	WireAdminControl			wac;

	public Helper(BundleContext context, WireAdminControl wac) {
		this.context = context;
		this.wac = wac;
		consumers = new Hashtable();
		producers = new Hashtable();
	}

	public void registerConsumer(String consumerPID, Object[] flavors) {
		Hashtable p = new Hashtable();
		p
				.put(
						org.osgi.service.wireadmin.WireConstants.WIREADMIN_CONSUMER_FLAVORS,
						flavors);
		p.put(org.osgi.framework.Constants.SERVICE_PID, consumerPID);
		regConsumer = context.registerService(Consumer.class.getName(),
				new ConsumerImpl(wac, consumerPID), p);
		consumers.put(consumerPID, regConsumer);
	}

	public void registerProducer(String producerPID, Class[] flavors) {
		Hashtable p = new Hashtable();
		p
				.put(
						org.osgi.service.wireadmin.WireConstants.WIREADMIN_PRODUCER_FLAVORS,
						flavors);
		p.put(org.osgi.framework.Constants.SERVICE_PID, producerPID);
		regProducer = context.registerService(Producer.class.getName(),
				new ProducerImpl(wac, producerPID), p);
		producers.put(producerPID, regProducer);
	}

	public void unregisterConsumer(String consumerPID) {
		regConsumer = (ServiceRegistration) consumers.get(consumerPID);
		try {
			if (regConsumer != null) {
				regConsumer.unregister();
				regConsumer = null;
				consumers.remove(consumerPID);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void unregisterProducer(String producerPID) {
		regProducer = (ServiceRegistration) producers.get(producerPID);
		try {
			if (regProducer != null) {
				regProducer.unregister();
				regProducer = null;
				producers.remove(producerPID);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void unregisterAll() {
		Enumeration services = getConsumers();
		while (services.hasMoreElements()) {
			unregisterConsumer((String) services.nextElement());
		}
		services = getProducers();
		while (services.hasMoreElements()) {
			unregisterProducer((String) services.nextElement());
		}
	}

	public void deleteAllWires(WireAdmin wa) {
		try {
			Wire[] wires = wa.getWires("(org.osgi.test.wireadmin=yes)");
			if (wires != null)
			for (int counter = 0; counter < wires.length; counter++) {
				wa.deleteWire(wires[counter]);
			}
		}
		catch (InvalidSyntaxException e) { /* no way */
		}
	}

	public Enumeration getConsumers() {
		return consumers.keys();
	}

	public Enumeration getProducers() {
		return producers.keys();
	}

	protected void registerEventConsumer(boolean crash) {
		Hashtable h = new Hashtable();
		h
				.put(
						org.osgi.service.wireadmin.WireConstants.WIREADMIN_CONSUMER_FLAVORS,
						new Class[] {String.class});
		h.put(org.osgi.framework.Constants.SERVICE_PID,
				"consumer.event.test.pid");
		regConsumerEvents = context.registerService(Consumer.class.getName(),
				new EventTestConsumer(crash), h);
	}

	protected void registerEventProducer(boolean crash) {
		Hashtable h = new Hashtable();
		h
				.put(
						org.osgi.service.wireadmin.WireConstants.WIREADMIN_CONSUMER_FLAVORS,
						new Class[] {String.class});
		h.put(org.osgi.framework.Constants.SERVICE_PID,
				"producer.event.test.pid");
		regProducerEvents = context.registerService(Producer.class.getName(),
				new EventTestProducer(crash), h);
	}

	protected void unregisterEventConsumer() {
		regConsumerEvents.unregister();
	}

	protected void unregisterEventProducer() {
		regProducerEvents.unregister();
	}

	public Wire createWire(WireAdmin wa, String from, String to, Dictionary dict) {
		System.out.println("Wire from "  + from + " to "  + to + " : " + dict );
		if ( dict == null )
			dict = new Hashtable();
		dict.put("org.osgi.test.wireadmin", "yes");
		Wire wire = wa.createWire( from, to, dict );
		return wire;
	}
}