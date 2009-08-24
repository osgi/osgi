package org.osgi.test.cases.wireadmin.junit;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

import junit.framework.Assert;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.wireadmin.Consumer;
import org.osgi.service.wireadmin.Producer;
import org.osgi.service.wireadmin.Wire;
import org.osgi.service.wireadmin.WireAdmin;
import org.osgi.service.wireadmin.WireConstants;
import org.osgi.test.support.OSGiTestCase;

/**
 * Contains some helper methods for registering/unregistering producers
 * 
 * $Log$ Revision 1.5 2005/05/30 13:41:07 polivier Add test in case of no wires
 * to delete
 * 
 * Revision 1.4 2004/12/03 09:12:32 pkriens Added service project Revision 1.3
 * 2004/11/03 11:47:09 pkriens Format and clean up of warnings Revision 1.2
 * 2004/11/03 10:55:32 pkriens Format and clean up of warnings Revision 1.1
 * 2004/07/07 13:15:26 pkriens *** empty log message ***
 * 
 * Revision 1.2 2003/11/14 07:18:29 vpanushev resolved issues 243 and 246
 * 
 * 
 * @author Neviana Ducheva
 */
public class Helper {
	final BundleContext						context;
	private final Hashtable					consumers;
	private final Hashtable					producers;
	final WireAdminControl					wac;
	private volatile ServiceRegistration	regConsumerEvents	= null;
	private volatile ServiceRegistration	regProducerEvents	= null;

	public Helper(BundleContext context, WireAdminControl wac) {
		this.context = context;
		this.wac = wac;
		consumers = new Hashtable();
		producers = new Hashtable();
	}

	public void registerConsumer(String consumerPID, Object[] flavors) {
		registerConsumer(new ConsumerImpl(wac, consumerPID), consumerPID,
				flavors, null);
	}

	public void registerConsumer(Consumer consumer, String consumerPID,
			Object[] flavors, Map properties) {
		Hashtable p = new Hashtable();
		if (properties != null) {
			p.putAll(properties);
		}
		p.put(Constants.SERVICE_PID, consumerPID);
		p.put(WireConstants.WIREADMIN_CONSUMER_FLAVORS, flavors);
		ServiceRegistration regConsumer = context.registerService(
				Consumer.class.getName(), consumer, p);
		Assert.assertNull("consumer already registered", consumers.put(
				consumerPID, regConsumer));
	}

	public void registerProducer(String producerPID, Class[] flavors) {
		registerProducer(new ProducerImpl(wac, producerPID), producerPID,
				flavors, null);
	}

	public void registerProducer(Producer producer, String producerPID,
			Class[] flavors, Map properties) {
		Hashtable p = new Hashtable();
		if (properties != null) {
			p.putAll(properties);
		}
		p.put(Constants.SERVICE_PID, producerPID);
		p.put(WireConstants.WIREADMIN_PRODUCER_FLAVORS, flavors);
		ServiceRegistration regProducer = context.registerService(
				Producer.class.getName(), producer, p);
		Assert.assertNull("producer already registered", producers.put(
				producerPID, regProducer));
	}

	public void modifyProducer(String producerPID, Map properties) {
		ServiceRegistration regProducer = (ServiceRegistration) producers
				.get(producerPID);
		Assert.assertNotNull("modifying non-existant producer", regProducer);
		ServiceReference ref = regProducer.getReference();
		Hashtable p = new Hashtable();
		p.put(WireConstants.WIREADMIN_PRODUCER_FLAVORS, ref
				.getProperty(WireConstants.WIREADMIN_PRODUCER_FLAVORS));
		p.putAll(properties);
		p.put(Constants.SERVICE_PID, ref.getProperty(Constants.SERVICE_PID));
		regProducer.setProperties(p);
	}

	public void unregisterConsumer(String consumerPID) {
		ServiceRegistration regConsumer = (ServiceRegistration) consumers
				.remove(consumerPID);
		if (regConsumer != null) {
			regConsumer.unregister();
			regConsumer = null;
		}
	}

	public void unregisterProducer(String producerPID) {
		ServiceRegistration regProducer = (ServiceRegistration) producers
				.remove(producerPID);
		if (regProducer != null) {
			regProducer.unregister();
			regProducer = null;
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

	public static void deleteAllWires(WireAdmin wa) {
		Wire[] wires = null;
		try {
			wires = wa.getWires("(org.osgi.test.wireadmin=*)");
		}
		catch (InvalidSyntaxException e) { /* no way */
			OSGiTestCase.fail("unexpected exception", e);
		}
		if (wires != null) {
			for (int counter = 0; counter < wires.length; counter++) {
				wa.deleteWire(wires[counter]);
			}
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
		h.put(WireConstants.WIREADMIN_CONSUMER_FLAVORS,
				new Class[] {String.class});
		h.put(Constants.SERVICE_PID, "consumer.event.test.pid");
		regConsumerEvents = context.registerService(Consumer.class.getName(),
				new EventTestConsumer(crash), h);
	}

	protected void registerEventProducer(boolean crash) {
		Hashtable h = new Hashtable();
		h.put(WireConstants.WIREADMIN_CONSUMER_FLAVORS,
				new Class[] {String.class});
		h.put(Constants.SERVICE_PID, "producer.event.test.pid");
		regProducerEvents = context.registerService(Producer.class.getName(),
				new EventTestProducer(crash), h);
	}

	protected void unregisterEventConsumer() {
		regConsumerEvents.unregister();
	}

	protected void unregisterEventProducer() {
		regProducerEvents.unregister();
	}

	public Wire createWire(WireAdmin wa, String from, String to, Map map) {
		WireAdminControl.log("Wire from " + from + " to " + to + " : " + map);
		Hashtable props = new Hashtable();
		if (map != null) {
			props.putAll(map);
		}
		props.put("org.osgi.test.wireadmin", wac.getName());
		Wire wire = wa.createWire(from, to, props);
		return wire;
	}

	public Wire updateWire(WireAdmin wa, Wire wire, Map map) {
		Hashtable props = new Hashtable();
		if (map != null) {
			props.putAll(map);
		}
		props.put("org.osgi.test.wireadmin", wac.getName());
		wa.updateWire(wire, props);
		return wire;
	}
}