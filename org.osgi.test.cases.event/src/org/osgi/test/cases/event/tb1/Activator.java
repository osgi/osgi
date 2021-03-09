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

package org.osgi.test.cases.event.tb1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.test.cases.event.service.TBCService;

/**
 * A bundle that registers a service with the marker interface
 * TBCService so it can be checked the exporter is correct.
 *
 * @author $Id$
 */
public class Activator implements BundleActivator, TBCService, EventHandler {
  
  private BundleContext context;
	private List<Event>			lastEvents	= null;
	private ServiceRegistration< ? >	serviceReg;
  private String[] topics;
  
	/**
   * Register this class as a service. Called when this bundle is started so the 
   * Framework can perform the bundle-specific activities necessary to start this bundle.
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
    this.context = context;
    context.registerService(this.getClass().getName(), this, null);
	}
  
	/**
   * Unregister this class as an event handler service. Called when this bundle is stopped 
   * so the Framework can perform the bundle-specific activities necessary to stop the bundle.
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
    if (serviceReg != null) {
      serviceReg.unregister();
      serviceReg = null;
    }
    this.context = null;
	}
  
  /**
   * Sets the array with event topics in which the event handler is interested.
   * @see org.osgi.test.cases.event.service.TBCService#setTopics(java.lang.String[])
   */
	public void setProperties(String[] topics, String[] delivery) {
		Hashtable<String,Object> ht = new Hashtable<>();
	  if (topics.length == 1) {
		  ht.put(EventConstants.EVENT_TOPIC, topics[0]);
	  }
	  else {
		  ht.put(EventConstants.EVENT_TOPIC, topics);
	  }
		if (delivery != null) {
			if (delivery.length == 1) {
				ht.put(EventConstants.EVENT_DELIVERY, delivery[0]);
			}
			else {
				ht.put(EventConstants.EVENT_DELIVERY, delivery);
			}
		}
		setProperties(ht);
  }
	
	/**
	 * Sets the service properties. Note that if the EventConstants.EVENT_TOPIC 
	 * property is missing or contains a value whose type is other than String, 
	 * String[], or Collection<String>, then the getTopics() method will return
	 * null.
	 * 
	 * @param properties The service properties to register.
	 * @see org.osgi.service.event.EventConstants#EVENT_TOPIC
	 * @see org.osgi.test.cases.event.service.TBCService#setProperties(Dictionary)
	 */
	public void setProperties(Dictionary<String, ? > properties) {
		Object topics = properties.get(EventConstants.EVENT_TOPIC);
		if (topics instanceof String)
			this.topics = new String[]{(String)topics};
		else if (topics instanceof String[])
			this.topics = (String[])topics;
		else if (topics instanceof Collection/*<String>*/) {
			@SuppressWarnings("unchecked")
			Collection<String> collection = (Collection<String>) topics;
			this.topics = collection.toArray(new String[0]);
		}
		else
			this.topics = null;
		if (serviceReg == null) {
			serviceReg = context.registerService(EventHandler.class.getName(), this, properties);
		} else {
			serviceReg.setProperties(properties);
		}
	}
  
  /**
   * Returns the array with all set event topics in which the event handler is interested.
   * @see org.osgi.test.cases.event.service.TBCService#getTopics()
   */
  public String[] getTopics() {
    return topics;
  }
  
  /**
   * Called by the {@link EventAdmin} service to notify the listener of an event.
   * 
   * @param event The event that occurred.
   */
  public synchronized void handleEvent(Event event) {
    if (lastEvents == null) {
			lastEvents = new ArrayList<>();
    }
		lastEvents.add(event);
  }
  
  /**
   * Returns the last received event and then elements in the vector with last events are removed.
   * @see org.osgi.test.cases.event.service.TBCService#getLastReceivedEvent()
   */
  public synchronized Event getLastReceivedEvent() {
    if (lastEvents == null || lastEvents.size() < 1) return null;
		Event event = lastEvents.get(lastEvents.size() - 1);
		lastEvents.clear();
    return event;
  }
  
  /**
   * Returns the last received events and then elements in the vector with last events are removed.
   * @see org.osgi.test.cases.event.service.TBCService#getLastReceivedEvents()
   */
	public synchronized List<Event> getLastReceivedEvents() {
		if (lastEvents == null || lastEvents.size() < 1)
			return null;
		List<Event> events = new ArrayList<>(lastEvents);
		lastEvents.clear();
    return events;
  }
}
