package org.osgi.impl.service.wireadmin;

import java.util.*;
import org.osgi.service.log.LogService;
import org.osgi.service.wireadmin.*;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Bundle;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.InvalidSyntaxException;

public class WireImpl implements Wire, WireConstants {
	private Object				lastValue			= null;
	private Class[]				flavors				= null;
	private Dictionary			properties;
	private ServiceReference	producerReference	= null;
	private ServiceReference	consumerReference	= null;
	private WireAdminImpl		parent				= null;
	private String				producerPID			= null;
	private String				consumerPID			= null;
	private boolean				connected			= false;
	private String				PID					= null;
	private BundleContext		context				= null;
	private Filter				filter				= null;
	private long				timeStamp			= -1;
	Vector						scope;
	private boolean				filteredByProducer	= false;

	public WireImpl() {
	}

	/**
	 * Return the list of data types understood by the <tt>Consumer</tt>
	 * service connected to this <tt>Wire</tt> object. Note that subclasses of
	 * the classes in this list are acceptable data types as well.
	 * 
	 * <p>
	 * The list is the value of the
	 * {@link WireConstants#WIREADMIN_CONSUMER_FLAVORS}service property of the
	 * <tt>Consumer</tt> service object connected to this object. If no such
	 * property was registered or the type of the property value is not
	 * <tt>Class[]</tt>, this method must return <tt>null</tt>.
	 * 
	 * @return An array containing the list of classes understood by the
	 *         <tt>Consumer</tt> service or <tt>null</tt> if the
	 *         <tt>Wire</tt> is not connected, or the consumer did not
	 *         register a {@link WireConstants#WIREADMIN_CONSUMER_FLAVORS}
	 *         property or the value of the property is not of type
	 *         <tt>Class[]</tt>.
	 */
	public Class[] getFlavors() {
		return flavors;
	}

	/**
	 * Return the last value sent through this <tt>Wire</tt> object.
	 * 
	 * <p>
	 * The returned value is the most recent, valid value passed to the
	 * {@link #update}method or returned by the {@link #poll}method of this
	 * object. If filtering is performed by this <tt>Wire</tt> object, this
	 * methods returns the last value provided by the <tt>Producer</tt>
	 * service.
	 * 
	 * @return The last value passed though this <tt>Wire</tt> object or
	 *         <tt>null</tt> if no valid values have been passed.
	 */
	public Object getLastValue() {
		return lastValue;
	}

	/**
	 * Return the wire properties for this <tt>Wire</tt> object.
	 * 
	 * @return The properties for this <tt>Wire</tt> object. The returned
	 *         <tt>Dictionary</tt> must be read only.
	 */
	public Dictionary getProperties() {
		Hashtable copy = new Hashtable();
		Object key;
		// TBD: properties could be null here?
		if (properties != null)
			for (Enumeration e = properties.keys(); e.hasMoreElements();) {
				key = e.nextElement();
				copy.put(key, properties.get(key));
			}
		return copy;
	}

	/**
	 * Return the connection state of this <tt>Wire</tt> object.
	 * 
	 * <p>
	 * A <tt>Wire</tt> is connected after the Wire Admin service receives
	 * notification that the <tt>Producer</tt> service and the
	 * <tt>Consumer</tt> service for this <tt>Wire</tt> object are both
	 * registered. This method will return <tt>true</tt> prior to notifying
	 * the <tt>Producer</tt> and <tt>Consumer</tt> services via calls to
	 * their respective <tt>consumersConnected</tt> and
	 * <tt>producersConnected</tt> methods.
	 * <p>
	 * A <tt>WireAdminEvent</tt> of type {@link WireAdminEvent#WIRE_CONNECTED}
	 * must be broadcast by the Wire Admin service when the <tt>Wire</tt>
	 * becomes connected.
	 * 
	 * <p>
	 * A <tt>Wire</tt> object is disconnected when either the
	 * <tt>Consumer</tt> or <tt>Producer</tt> service is unregistered or the
	 * <tt>Wire</tt> object is deleted.
	 * <p>
	 * A <tt>WireAdminEvent</tt> of type
	 * {@link WireAdminEvent#WIRE_DISCONNECTED}must be broadcast by the Wire
	 * Admin service when the <tt>Wire</tt> becomes disconnected.
	 * 
	 * @return <tt>true</tt> if both the <tt>Producer</tt> and
	 *         <tt>Consumer</tt> for this <tt>Wire</tt> object are connected
	 *         to the <tt>Wire</tt> object; <tt>false</tt> otherwise.
	 */
	public boolean isConnected() {
		return connected;
	}

	/**
	 * Return the state of this <tt>Wire</tt> object.
	 * 
	 * <p>
	 * A connected <tt>Wire</tt> must always be disconnected before becoming
	 * invalid.
	 * 
	 * @return <tt>false</tt> if this <tt>Wire</tt> is invalid because it
	 *         has been deleted via {@link WireAdmin#deleteWire};<tt>true</tt>
	 *         otherwise.
	 */
	public boolean isValid() {
		return (context != null);
	}

	/**
	 * Poll for an updated value.
	 * 
	 * <p>
	 * This methods is normally called by the <tt>Consumer</tt> service to
	 * request an updated value from the <tt>Producer</tt> service connected
	 * to this <tt>Wire</tt> object. This <tt>Wire</tt> object will call the
	 * {@link Producer#polled}method to obtain an updated value. If this
	 * <tt>Wire</tt> object is not connected, then the <tt>Producer</tt>
	 * service must not be called.
	 * 
	 * <p>
	 * A <tt>WireAdminEvent</tt> of type {@link WireAdminEvent#WIRE_TRACE}
	 * must be broadcast by the Wire Admin service after the <tt>Producer</tt>
	 * service has been successfully called.
	 * 
	 * @return An updated value whose type should be one of the types returned
	 *         by {@link #getFlavors}or <tt>null</tt> if the <tt>Wire</tt>
	 *         object is not connected, the <tt>Producer</tt> service threw an
	 *         exception, or the <tt>Producer</tt> service returned a value
	 *         which is not an instance of one of the types returned by
	 *         {@link #getFlavors}.
	 */
	public Object poll() {
		if (isConnected()) {
			Producer producer = getProducer();
			if (producer == null) {
				// producer was just unregistered
				return null;
			}
			try {
				Object result = producer.polled(this);
				if (isCorrectType(result)) {
					lastValue = result;
					if (result instanceof Envelope[]) {
						Envelope[] envs = (Envelope[]) result;
						ArrayList list = new ArrayList();
						boolean changed = false;
						for (int i = 0; i < envs.length; ++i) {
							if (envs[i].getScope() != null
									&& this.hasScope(envs[i].getScope())) {
								list.add(envs[i]);
							}
							else {
								changed = true;
							}
						}
						if (changed) {
							envs = new Envelope[list.size()];
							result = list.toArray(envs);
						}
					}
					parent.bCast(WireAdminEvent.WIRE_TRACE, this, null);
					return result;
				}
			}
			catch (Throwable th) {
				parent.bCast(WireAdminEvent.PRODUCER_EXCEPTION, this, th);
				return null;
			}
		}
		return null;
	}

	/**
	 * Update the value.
	 * 
	 * <p>
	 * This methods is called by the <tt>Producer</tt> service to notify the
	 * <tt>Consumer</tt> service connected to this <tt>Wire</tt> object of
	 * an updated value.
	 * <p>
	 * If the properties of this <tt>Wire</tt> object contain a
	 * {@link WireConstants#WIREADMIN_FILTER}property, then filtering is
	 * performed on this <tt>Wire</tt>. If the <tt>Producer</tt> service
	 * connected to this <tt>Wire</tt> object was registered with the service
	 * property {@link WireConstants#WIREADMIN_PRODUCER_FILTERS}, the
	 * <tt>Producer</tt> service will perform the filter the value according
	 * to the rules specified for the filter. Otherwise, this <tt>Wire</tt>
	 * object will filter the value according to the rules specified for the
	 * filter.
	 * <p>
	 * If no filtering is done, or the filter indicates the updated value should
	 * be delivered to the <tt>Consumer</tt> service, then this <tt>Wire</tt>
	 * object must call the {@link Consumer#updated}method with the updated
	 * value. If this <tt>Wire</tt> object is not connected, then the
	 * <tt>Consumer</tt> service must not be called.
	 * 
	 * <p>
	 * A <tt>WireAdminEvent</tt> of type {@link WireAdminEvent#WIRE_TRACE}
	 * must be broadcast by the Wire Admin service after the <tt>Consumer</tt>
	 * service has been successfully called.
	 * 
	 * @param value The updated value. The value should be an instance of one of
	 *        the types returned by {@link #getFlavors}.
	 * @see WireConstants#WIREADMIN_FILTER
	 */
	public void update(Object value) {
		//lastValue = null;
		Consumer consumer = getConsumer();
		if (consumer == null) {
			// disconnected
			return;
		}
		// Was 		if (isCorrectType(value.getClass())) {
		if (isCorrectType(value)) {
			if ((filter != null) && (!filteredByProducer)) {
				if (lastValue == null) {
					lastValue = value;
				}
				Hashtable myProps = new Hashtable(value instanceof Number ? 5
						: 3);
				myProps.put(WireConstants.WIREVALUE_CURRENT, value);
				myProps.put(WireConstants.WIREVALUE_PREVIOUS, lastValue);
				myProps.put(WireConstants.WIREVALUE_ELAPSED, new Long(
						timeStamp == -1 ? 0 : System.currentTimeMillis()
								- timeStamp));
				//timeStamp = System.currentTimeMillis();
				if (value instanceof Number && lastValue instanceof Number) {
					double currentNumber = ((Number) value).doubleValue();
					double lastNumber = ((Number) lastValue).doubleValue();
					// Calculates the absolute delta
					Double abDelta = new Double(Math.abs(currentNumber - lastNumber));
					myProps.put(WireConstants.WIREVALUE_DELTA_ABSOLUTE, abDelta);
					// Calculates the relative delta
					Double relDelta = new Double(Math.abs((lastNumber - currentNumber) / currentNumber));
					myProps.put(WireConstants.WIREVALUE_DELTA_RELATIVE, relDelta);
					if (!filter.match(myProps)) {
						parent.getLogService().log(LogService.LOG_DEBUG,
								"filter match on update failed");
						return;
					}
				}
			}
			if (value instanceof Envelope) {
				Envelope env = (Envelope) value;
				if (env.getScope() != null) {
					if (!this.hasScope(env.getScope())) {
						lastValue = null;
						//silently ignore
						return;
					}
				}
			}
			try {
				consumer.updated(this, value);
				timeStamp = System.currentTimeMillis();
				lastValue = value;
				parent.bCast(WireAdminEvent.WIRE_TRACE, this, null);
			}
			catch (Throwable th) {
				parent.bCast(WireAdminEvent.CONSUMER_EXCEPTION, this, th);
			}
		}
	}

	private boolean isCorrectType(Object object) {
		Class objectClass = object.getClass();
		if (flavors == null) {
			// every consumer should specify flavors, the error has been logged,
			// but should be corrected
			return true;
		}
		for (int x = 0; x < flavors.length; x++) {
			if (flavors[x].isAssignableFrom(objectClass)) {
				return true;
			}
		}
		// Envelope?
		if (object instanceof Envelope[]) {
			Envelope[] env = (Envelope[]) object;
			for (int x = 0; x < flavors.length; x++) {
				for (int j = 0; j < env.length; j++) {
					if (flavors[x].isAssignableFrom(env[j].getClass())) {
						return true;
					}
				}
			}
		}
		return false;
	}
	static Wire[] EMPTY = new Wire[0];
	public void delete() {	
		parent.removeWire(this);
		parent.updateProducer(getProducer(), this, producerPID);
		parent.updateConsumer(getConsumer(), this, consumerPID);

		if (producerReference != null) {
			context.ungetService(producerReference);
			producerReference = null;
		}
		if (consumerReference != null) {
			context.ungetService(consumerReference);
			consumerReference = null;
		}
		flavors = null;
		lastValue = null;
		context = null;
		connected = false;
		parent.getLogService().log(LogService.LOG_DEBUG,
				"wire with PID " + getPID() + " deleted");
	}

	public void setProperties(Dictionary properties) {
		this.properties = properties;
		properties.put(WireConstants.WIREADMIN_PRODUCER_PID, producerPID);
		properties.put(WireConstants.WIREADMIN_CONSUMER_PID, consumerPID);
		properties.put(WireConstants.WIREADMIN_PID, String.valueOf(PID));
		String filterString = (String) properties
				.get(WireConstants.WIREADMIN_FILTER);
		if (filterString != null) {
			try {
				filter = context.createFilter(filterString);
			}
			catch (InvalidSyntaxException ivsx) {
				filter = null;
				parent.getLogService().log(LogService.LOG_WARNING,
						"invalid filter in wire properties", ivsx);
			}
		}
		else {
			filter = null;
		}
		
		if ((getProducer()) != null &&  (getConsumer() != null)) {
			parent.getUpdateDispatcher().addProducerUpdate(getProducer(), this,
					parent.getProducerWires(producerPID));
			parent.getUpdateDispatcher().addConsumerUpdate(getConsumer(), this,
					parent.getConsumerWires(consumerPID));
		}
	}

	public void setProducerPID(String PID) {
		this.producerPID = PID;
		if (properties != null) {
			properties.put(WireConstants.WIREADMIN_PRODUCER_PID, PID);
		}
		checkConnection();
	}

	public void setConsumerPID(String PID) {
		this.consumerPID = PID;
		if (properties != null) {
			properties.put(WireConstants.WIREADMIN_CONSUMER_PID, PID);
		}
		checkConnection();
	}

	private void checkConnection() {
		if (connected) {
			if (producerReference == null || consumerReference == null) {
				connected = false;
				scope = null;
				flavors = null;
				parent.wireDisconnected(this);
				Producer producer = getProducer();
				if (producer != null) {
					parent.getUpdateDispatcher().addProducerUpdate(producer,
							this, parent.getProducerWires(producerPID));
				}
				else {
					parent.getUpdateDispatcher().addProducerUpdate(producer,
							this, null);
					Consumer consumer = getConsumer();
					if (consumer != null) {
						parent.getUpdateDispatcher().addConsumerUpdate(
								consumer, this,
								parent.getConsumerWires(consumerPID));
					}
				}
			}
		}
		else {
			if ((producerReference != null && consumerReference != null)) {
				connected = true;
				setScope();
				parent.wireConnected(this);
				parent.getUpdateDispatcher().addProducerUpdate(getProducer(),
						this, parent.getProducerWires(producerPID));
				parent.getUpdateDispatcher().addConsumerUpdate(getConsumer(),
						this, parent.getConsumerWires(consumerPID));
			}
		}
	}

	private void setFlavors(ServiceReference consumerReference) {
		if (consumerReference == null) {
			this.flavors = null;
			return;
		}
		try {
			Class[] newFlavors = (Class[]) consumerReference
					.getProperty(WireConstants.WIREADMIN_CONSUMER_FLAVORS);
			if (newFlavors != null) {
				flavors = newFlavors;
				return;
			}
		}
		catch (ClassCastException ccx) {
		}
		flavors = null;
		parent.getLogService().log(LogService.LOG_ERROR,
				"consumer registered w/o flavors");
	}

	public void setParent(WireAdminImpl parent) {
		this.parent = parent;
		this.context = parent.getBundleContext();
	}

	public void setPID(String PID) {
		this.PID = PID;
		if (properties != null) {
			properties.put(WireConstants.WIREADMIN_PID, PID);
		}
	}

	public String getProducerPID() {
		return producerPID;
	}

	public String getConsumerPID() {
		return consumerPID;
	}

	/**
	 * Get the producer using the producerReference.
	 * 
	 * @return
	 */
	public Producer getProducer() {
		if (producerReference == null) {
			return null;
		}
		return (Producer) context.getService(producerReference);
	}

	/**
	 * Get the consumer using the consumerReference.
	 * 
	 * @return
	 */
	public Consumer getConsumer() {
		if (consumerReference == null) {
			return null;
		}
		return (Consumer) context.getService(consumerReference);
	}

	public void setProducer(ServiceReference producer) {
		this.producerReference = producer;
		filteredByProducer = false;
		if (producerReference != null
				&& producerReference
						.getProperty(WireConstants.WIREADMIN_PRODUCER_FILTERS) != null) {
			// producer will do the filtering
			filteredByProducer = true;
		}
		checkConnection();
	}

	public void setConsumer(ServiceReference consumer) {
		this.consumerReference = consumer;
		setFlavors(consumerReference);
		checkConnection();
	}

	public String getPID() {
		return PID;
	}

	void setScope() {
		scope = null;
		ServiceReference pr = producerReference;
		ServiceReference cr = consumerReference;
		if (pr == null || cr == null)
			return;
		Vector a = filterOnPermission((String[]) pr
				.getProperty(WIREADMIN_PRODUCER_SCOPE), WirePermission.PRODUCE,
				pr.getBundle());
		Vector b = filterOnPermission((String[]) cr
				.getProperty(WIREADMIN_CONSUMER_SCOPE), WirePermission.CONSUME,
				cr.getBundle());
		if (a == null || b == null)
			return;
		scope = a;
		if (b.size() == 1 && (b.elementAt(0)).equals("*"))
			return;
		scope.retainAll(b);
	}

	Vector filterOnPermission(String[] names, String action, Bundle b) {
		if (names == null)
			return null;
		Vector result = new Vector();
		for (int i = 0; i < names.length; i++) {
			WirePermission wp = new WirePermission(names[i], action);
			if (b.hasPermission(wp))
				result.addElement(names[i]);
		}
		return result;
	}

	public String[] getScope() {
		if (scope != null)
			return (String[]) scope.toArray(new String[scope.size()]);
		return null;
	}

	public boolean hasScope(String s) {
		return scope == null || (scope.contains(s) || scope.contains("*"));
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("a Wire ");
		sb.append(" from ");
		sb.append(producerPID);
		sb.append(" to ");
		sb.append(consumerPID);
		sb.append(" scope ");
		sb.append(scope);
		if (isConnected())
			sb.append(" is connected");
		return sb.toString();
	}
}
