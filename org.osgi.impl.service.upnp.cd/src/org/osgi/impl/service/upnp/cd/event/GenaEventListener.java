package org.osgi.impl.service.upnp.cd.event;

import java.util.*;
import org.osgi.service.upnp.*;

// This class is a upnp event listener which will be registered with the framework when ever
// a subscription for a particular event url comes and is not available in the event registry
public class GenaEventListener implements UPnPEventListener {
	private String				subscriptionId;
	public static final String	UPNP_FILTER	= "upnp.filter";
	private UPnPService			upnpservice;

	// Constructor for initializing variables.
	public GenaEventListener(String subscriptionId, UPnPService upnpservice) {
		this.subscriptionId = subscriptionId;
		this.upnpservice = upnpservice;
	}

	// when ever a statevariable is changed , this method will be called and
	// inturn will
	// send the new changed evented statevariables list to all the subscribers
	// who are
	// subscribed for this .
	public void notifyUPnPEvent(String deviceId, String serviceId,
			Dictionary events) {
		if (upnpservice != null) {
			Hashtable eventedVariables = checkSendEvents((Hashtable) events,
					upnpservice);
			String eventsuburl = deviceId + serviceId;
			String tmpEventUrl = "";
			eventsuburl = eventsuburl.replace(':', '-');
			for (Enumeration enumeration = EventRegistry.getSubscriberIds(); enumeration
					.hasMoreElements();) {
				String element = (String) enumeration.nextElement();
				Subscription subscription = (Subscription) EventRegistry
						.getSubscriber(element);
				String eventURL = subscription.getPublisherPath();
				tmpEventUrl = eventURL.substring(eventURL.indexOf("uuid"));
				if (tmpEventUrl.equals(eventsuburl)) {
					String callbackURL = subscription.getCallbackURL();
					new SendEvents(eventedVariables, subscription).start();
				}
			}
		}
	}

	// this method checks whether statevariables can be evented or not.
	Hashtable checkSendEvents(Hashtable statevariables, UPnPService upnpservice) {
		Hashtable eventedVariables = new Hashtable();
		UPnPStateVariable[] variables = upnpservice.getStateVariables();
		for (Enumeration enumeration = statevariables.keys(); enumeration.hasMoreElements();) {
			String variablename = (String) enumeration.nextElement();
			for (int i = 0; i < variables.length; i++) {
				if (variables[i].getName().equals(variablename)) {
					if (variables[i].sendsEvents()) {
						eventedVariables.put(variablename, statevariables
								.get(variablename));
					}
				}
			}
		}
		return eventedVariables;
	}
}