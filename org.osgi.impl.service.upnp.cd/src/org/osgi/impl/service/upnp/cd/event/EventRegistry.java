package org.osgi.impl.service.upnp.cd.event;

import java.util.*;
import org.osgi.framework.ServiceRegistration;
import org.osgi.impl.service.upnp.cd.ssdp.EventAccessForExporter;

// This class is the key database for eventing. This class holds the subscribers list which
// contains subscription id mapped to subscription object . Also holds the database for
// device udn with serviceid(eventingurl for service) mapped with upnplistener registration object.
public class EventRegistry implements EventAccessForExporter {
	// This table holds the subscriber list. This table maintains all the
	// subscription info against the
	// service unique id given by the device.
	private static Hashtable	subscribersList;
	private static Hashtable	idMapsListener;
	public Subscription			theSubscription;
	private GenaConstants		gc;

	// Constructor for initializing subscriberList table.
	public EventRegistry(String IP) {
		subscribersList = new Hashtable();
		idMapsListener = new Hashtable();
		gc = new GenaConstants(IP);
	}

	// This method inserts the listener mapped with serviceid in idmapslistener
	// table
	public synchronized static void insertListeners(String serviceId,
			ServiceRegistration registration) {
		idMapsListener.put(serviceId, registration);
	}

	// This method checks whether a particular registration object is available
	// against the udn.
	public synchronized static boolean checkIsListenerAvailable(String serviceId) {
		if (idMapsListener.get(serviceId) != null) {
			return true;
		}
		return false;
	}

	// This method inserts the new subscriber in the subscriber list.
	public synchronized static void insertSubscriber(String subscriptionId,
			Subscription subscription) {
		subscribersList.put(subscriptionId, subscription);
	}

	// This method retrieves the subscription object against the given
	// subscription id
	public synchronized static Subscription getSubscriber(String subscriptionId) {
		return (Subscription) subscribersList.get(subscriptionId);
	}

	// This method removes the subscription from the list
	public synchronized static void removeSubscriber(String subscriptionId) {
		subscribersList.remove(subscriptionId);
	}

	// This method removes the udn from the table along with making the listener
	// unregister
	// from the framework. If any subscriber subscribers for this service's
	// eventing url,
	// that subscriber will also be removed from the list.
	public synchronized void removeServiceId(String serviceId) {
		ServiceRegistration registration = (ServiceRegistration) idMapsListener
				.get(serviceId);
		if (registration != null) {
			try {
				registration.unregister();
				registration = null;
			}
			catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		idMapsListener.remove(serviceId);
		for (Enumeration enum = subscribersList.keys(); enum.hasMoreElements();) {
			String subscriptionId = (String) enum.nextElement();
			Subscription subscription = (Subscription) subscribersList
					.get(subscriptionId);
			String serviceIdentifier = subscription.getServiceId();
			if (serviceIdentifier.equals(serviceId)) {
				subscribersList.remove(subscriptionId);
			}
		}
	}

	// This method retrieves all the subscription ids from the list
	public synchronized static Enumeration getSubscriberIds() {
		return subscribersList.keys();
	}

	// This method sets the http gena port got from gena server.
	public void setPort(int port) {
		gc.setPort(port);
	}

	// when a particular subscriber unsubscribes, his subscription for the
	// particular device
	// service event url has also been removed from the idlistenerlist provided
	// no other
	// subscriber is subscribing for that event url. This has to be done because
	// of unncessary
	// keeping of listener with framework.
	public static void checkForServiceId(String serviceId) {
		boolean removeListener = true;
		for (Enumeration enum = subscribersList.elements(); enum
				.hasMoreElements();) {
			if (((Subscription) enum.nextElement()).getServiceId().equals(
					serviceId)) {
				removeListener = false;
				break;
			}
		}
		if (removeListener) {
			ServiceRegistration registration = (ServiceRegistration) idMapsListener
					.get(serviceId);
			try {
				registration.unregister();
				registration = null;
			}
			catch (Exception e) {
				System.out.println(e.getMessage());
			}
			idMapsListener.remove(serviceId);
		}
	}

	public void releaseAllListeners() {
		for (Enumeration enum = idMapsListener.keys(); enum.hasMoreElements();) {
			String eventUrl = (String) enum.nextElement();
			ServiceRegistration registration = (ServiceRegistration) idMapsListener
					.get(eventUrl);
			try {
				registration.unregister();
				registration = null;
				idMapsListener.remove(eventUrl);
			}
			catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		subscribersList = null;
	}
}
