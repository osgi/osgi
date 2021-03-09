/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.impl.service.upnp.cd.event;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import org.osgi.service.upnp.UPnPEventListener;
import org.osgi.service.upnp.UPnPService;
import org.osgi.service.upnp.UPnPStateVariable;

// This class is a upnp event listener which will be registered with the framework when ever
// a subscription for a particular event url comes and is not available in the event registry
public class GenaEventListener implements UPnPEventListener {
	@SuppressWarnings("unused")
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
	@Override
	public void notifyUPnPEvent(String deviceId, String serviceId,
			Dictionary<String,Object> events) {
		if (upnpservice != null) {
			Hashtable<String,Object> eventedVariables = checkSendEvents(
					events,
					upnpservice);
			String eventsuburl = deviceId + serviceId;
			String tmpEventUrl = "";
			eventsuburl = eventsuburl.replace(':', '-');
			for (Enumeration<String> enumeration = EventRegistry
					.getSubscriberIds(); enumeration
					.hasMoreElements();) {
				String element = enumeration.nextElement();
				Subscription subscription = EventRegistry
						.getSubscriber(element);
				String eventURL = subscription.getPublisherPath();
				tmpEventUrl = eventURL.substring(eventURL.indexOf("uuid"));
				if (tmpEventUrl.equals(eventsuburl)) {
					@SuppressWarnings("unused")
					String callbackURL = subscription.getCallbackURL();
					new SendEvents(eventedVariables, subscription).start();
				}
			}
		}
	}

	// this method checks whether statevariables can be evented or not.
	Hashtable<String,Object> checkSendEvents(
			Dictionary<String,Object> statevariables,
			@SuppressWarnings("hiding") UPnPService upnpservice) {
		Hashtable<String,Object> eventedVariables = new Hashtable<>();
		UPnPStateVariable[] variables = upnpservice.getStateVariables();
		for (Enumeration<String> enumeration = statevariables
				.keys(); enumeration.hasMoreElements();) {
			String variablename = enumeration.nextElement();
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
