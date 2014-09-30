/*
 * Copyright (c) OSGi Alliance (2014). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.test.cases.resourcemonitoring.junit;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.resourcemonitoring.ResourceContext;
import org.osgi.service.resourcemonitoring.ResourceContextEvent;
import org.osgi.service.resourcemonitoring.ResourceContextListener;
import org.osgi.service.resourcemonitoring.ResourceMonitoringService;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * see Conformance Tests description.odt file.
 * 
 * @author $Id$
 */
public class ResourceContextListenerTestCase extends DefaultTestBundleControl
		implements ResourceContextListener {

	/**
	 * name of the ResourceContext used for the tests
	 */
	private static final String	RESOURCE_CONTEXT_NAME	= "context1";

	/**
	 * name2 of the ResourceContext used for the tests
	 */
	private static final String	RESOURCE_CONTEXT_NAME2	= "context2";

	/**
	 * bundle id 1
	 */
	private static final long	BUNDLE_ID				= 1l;

	/**
	 * bundle id 2
	 */
	private static final long	BUNDLE_ID2				= 2l;

	/**
	 * bundle context
	 */
	private BundleContext		bundleContext;

	/**
	 * ResourceMonitoringService
	 */
	private ResourceMonitoringService	resourceMonitoringService;

	/**
	 * events
	 */
	private List				events;

	/**
	 * service registration of the listener
	 */
	private ServiceRegistration	listenerServiceRegistration;

	/**
	 * 
	 */
	public ResourceContextListenerTestCase() {
		events = new ArrayList();
	}

	/**
	 * set bundle context.
	 */
	public void setBundleContext(BundleContext context) {
		bundleContext = context;

		ServiceReference sr = bundleContext
				.getServiceReference(ResourceMonitoringService.class.getName());
		resourceMonitoringService = (ResourceMonitoringService) bundleContext.getService(sr);
	}

	/**
	 * Test register a ResourceContextCreated listener.
	 */
	public void testRegisterA_RESOURCE_CONTEXT_CREATED_Listener() {

		// registers the ResourceContextListener with a RESOURCE_CONTEXT_CREATED
		// type filter
		registerListener(
				new int[] {ResourceContextEvent.RESOURCE_CONTEXT_CREATED},
				null);

		// executes the scenarios
		executeScenario1();

		// unregisters the listener
		unregisterListener();

		// checks that only a RESOURCE_CONTEXT_CREATED has been received
		assertTrue(events.size() == 1);
		ResourceContextEvent event = (ResourceContextEvent) events.get(0);
		assertTrue(event.getType() == ResourceContextEvent.RESOURCE_CONTEXT_CREATED);
		assertTrue(event.getContext().getName().equals(RESOURCE_CONTEXT_NAME));
	}

	/**
	 * Test register a ResourceContextListener filtering
	 * RESOURCE_CONTEXT_REMOVED.
	 */
	public void testRegisterA_RESOURCE_CONTEXT_REMOVED_Listener() {
		// registers the ResourceContextListener with a RESOURCE_CONTEXT_REMOVED
		// type filter
		registerListener(
				new int[] {ResourceContextEvent.RESOURCE_CONTEXT_REMOVED},
				null);

		// executes the scenarios
		executeScenario1();

		// unregisters the listener
		unregisterListener();

		// checks that only a RESOURCE_CONTEXT_REMOVED has been received
		assertTrue(events.size() == 1);
		ResourceContextEvent event = (ResourceContextEvent) events.get(0);
		assertTrue(event.getType() == ResourceContextEvent.RESOURCE_CONTEXT_REMOVED);
		assertTrue(event.getContext().getName().equals(RESOURCE_CONTEXT_NAME));
	}

	/**
	 * Test register a ResourceContextListener filtering BUNDLE_ADDED events.
	 */
	public void testRegisterA_BUNDLE_ADDED_Listener() {
		// registers the ResourceContextListener with a BUNDLE_ADDED
		// type filter
		registerListener(new int[] {ResourceContextEvent.BUNDLE_ADDED}, null);

		// executes the scenarios
		executeScenario1();

		// unregisters the listener
		unregisterListener();

		// checks that only a RESOURCE_CONTEXT_REMOVED has been received
		assertTrue(events.size() == 1);
		ResourceContextEvent event = (ResourceContextEvent) events.get(0);
		assertTrue(event.getType() == ResourceContextEvent.BUNDLE_ADDED);
		assertTrue(event.getContext().getName().equals(RESOURCE_CONTEXT_NAME));
		assertTrue(event.getBundleId() == BUNDLE_ID);
	}

	/**
	 * Test register a ResourceContextListener filtering BUNDLE_REMOVED events.
	 */
	public void testRegisterA_BUNDLE_REMOVED_Listener() {
		// registers the ResourceContextListener with a BUNDLE_REMOVED
		// type filter
		registerListener(new int[] {ResourceContextEvent.BUNDLE_REMOVED},
				null);

		// executes the scenarios
		executeScenario1();

		// unregisters the listener
		unregisterListener();

		// checks that only a RESOURCE_CONTEXT_REMOVED has been received
		assertTrue(events.size() == 1);
		ResourceContextEvent event = (ResourceContextEvent) events.get(0);
		assertTrue(event.getType() == ResourceContextEvent.BUNDLE_REMOVED);
		assertTrue(event.getContext().getName().equals(RESOURCE_CONTEXT_NAME));
		assertTrue(event.getBundleId() == BUNDLE_ID);
	}

	/**
	 * Test register a ResourceContextListener filtering events emmitted by a
	 * specific ResourceContext.
	 */
	public void testRegisterAListenerFilteringEventRelatedToASpecificResourceContext() {

		// register this instance as a ResourceContextListener filtering events
		// of context1.
		registerListener(null, new String[] {RESOURCE_CONTEXT_NAME});

		// executes scenario1 (related to context1) and scenario2 (related to
		// context2)
		executeScenario1();
		executeScenario2();

		// unregister the listener
		unregisterListener();

		// checks received events => four events MUST have been received.
		assertTrue(events.size() == 4);
		// first event is a RESOURCE_CONTEXT_CREATED for context1
		ResourceContextEvent createdEvent = (ResourceContextEvent) events
				.get(0);
		assertTrue(createdEvent.getType() == ResourceContextEvent.RESOURCE_CONTEXT_CREATED);
		assertTrue(createdEvent.getContext().getName()
				.equals(RESOURCE_CONTEXT_NAME));

		// second event is a BUNDLE_ADDED event (bundle1 was added to context1)
		ResourceContextEvent addedEvent = (ResourceContextEvent) events.get(1);
		assertTrue(addedEvent.getType() == ResourceContextEvent.BUNDLE_ADDED);
		assertTrue(addedEvent.getContext().getName()
				.equals(RESOURCE_CONTEXT_NAME));
		assertTrue(addedEvent.getBundleId() == BUNDLE_ID);

		// third event is a BUNDLE_REMOVED event (bundle1 was removed from
		// context1)
		ResourceContextEvent removedEvent = (ResourceContextEvent) events
				.get(2);
		assertTrue(removedEvent.getType() == ResourceContextEvent.BUNDLE_REMOVED);
		assertTrue(removedEvent.getContext().getName()
				.equals(RESOURCE_CONTEXT_NAME));
		assertTrue(removedEvent.getBundleId() == BUNDLE_ID);

		// and finally the fourth event is a RESOURCE_CONTEXT_REMOVED event
		// (context1 was deleted)
		ResourceContextEvent deletedEvent = (ResourceContextEvent) events
				.get(3);
		assertTrue(deletedEvent.getType() == ResourceContextEvent.RESOURCE_CONTEXT_REMOVED);
		assertTrue(deletedEvent.getContext().getName()
				.equals(RESOURCE_CONTEXT_NAME));

	}

	/**
	 * 
	 */
	public void testUnregisterResourceContextListener() {
		// register this instance as a ResourceContextListener without filters
		registerListener(null, null);

		// execute scenario2 (related to context2)
		executeScenario2();

		// unregister this instace as a ResourceContextListener
		unregisterListener();

		// execute scenario1 (related to context1)
		executeScenario1();

		// check that the Listener receives four events all related to context2
		// checks received events => four events MUST have been received.
		assertTrue(events.size() == 4);
		// first event is a RESOURCE_CONTEXT_CREATED for context2
		ResourceContextEvent createdEvent = (ResourceContextEvent) events
				.get(0);
		assertTrue(createdEvent.getType() == ResourceContextEvent.RESOURCE_CONTEXT_CREATED);
		assertTrue(createdEvent.getContext().getName()
				.equals(RESOURCE_CONTEXT_NAME2));

		// second event is a BUNDLE_ADDED event (bundle2 was added to context2)
		ResourceContextEvent addedEvent = (ResourceContextEvent) events.get(1);
		assertTrue(addedEvent.getType() == ResourceContextEvent.BUNDLE_ADDED);
		assertTrue(addedEvent.getContext().getName()
				.equals(RESOURCE_CONTEXT_NAME2));
		assertTrue(addedEvent.getBundleId() == BUNDLE_ID2);

		// third event is a BUNDLE_REMOVED event (bundle2 was removed from
		// context2)
		ResourceContextEvent removedEvent = (ResourceContextEvent) events
				.get(2);
		assertTrue(removedEvent.getType() == ResourceContextEvent.BUNDLE_REMOVED);
		assertTrue(removedEvent.getContext().getName()
				.equals(RESOURCE_CONTEXT_NAME2));
		assertTrue(removedEvent.getBundleId() == BUNDLE_ID2);

		// and finally the fourth event is a RESOURCE_CONTEXT_REMOVED event
		// (context2 was deleted)
		ResourceContextEvent deletedEvent = (ResourceContextEvent) events
				.get(3);
		assertTrue(deletedEvent.getType() == ResourceContextEvent.RESOURCE_CONTEXT_REMOVED);
		assertTrue(deletedEvent.getContext().getName()
				.equals(RESOURCE_CONTEXT_NAME2));

	}

	/**
	 * 
	 */
	public void testRegisterResourceContextListenerFiltering2TypesOfEvents() {
		// register this instance as a ResourceContextListener filtering on
		// RESOURCE_CONTEXT_CREATED event type and on RESOURCE_CONTEXT_REMOVED
		// event type all related to context1
		registerListener(new int[] {
				ResourceContextEvent.RESOURCE_CONTEXT_CREATED,
				ResourceContextEvent.RESOURCE_CONTEXT_REMOVED},
				new String[] {RESOURCE_CONTEXT_NAME});

		// executes scenario1 (related to context1) and scenario2 (related to
		// context2)
		executeScenario1();
		executeScenario2();

		// unregister the listener
		unregisterListener();

		// checks the listener receives 2 events about the creation and the
		// deletion of context1
		assertTrue(events.size() == 2);
		// first event is a RESOURCE_CONTEXT_CREATED for context1
		ResourceContextEvent createdEvent = (ResourceContextEvent) events
				.get(0);
		assertTrue(createdEvent.getType() == ResourceContextEvent.RESOURCE_CONTEXT_CREATED);
		assertTrue(createdEvent.getContext().getName()
				.equals(RESOURCE_CONTEXT_NAME));
		// and finally the second event is a RESOURCE_CONTEXT_REMOVED event
		// (context1 was deleted)
		ResourceContextEvent deletedEvent = (ResourceContextEvent) events
				.get(1);
		assertTrue(deletedEvent.getType() == ResourceContextEvent.RESOURCE_CONTEXT_REMOVED);
		assertTrue(deletedEvent.getContext().getName()
				.equals(RESOURCE_CONTEXT_NAME));
	}

	/**
	 * This method is called to report a ResourceContextEvent. The received
	 * event is added into the events list.
	 */
	public void notify(ResourceContextEvent event) {
		// add the event into the list.
		events.add(event);
	}

	/**
	 * Setup test.
	 * <p>
	 * Clears events list.
	 * </p>
	 */
	protected void setUp() throws Exception {
		super.setUp();

		events.clear();
	}

	/**
	 * This method executes the following scenario:
	 * <ul>
	 * <li>Creates a ResourceContext named context1</li>
	 * <li>Adds bundle 1 to context1.</li>
	 * <li>Removes bundle 1 from context1.</li>
	 * <li>Deletes context1.</li>
	 * </ul>
	 * This method is called by all tests cases to generate
	 * ResourceContextEvent.
	 */
	private void executeScenario1() {
		// create context1
		ResourceContext resourceContext = resourceMonitoringService.createContext(
				RESOURCE_CONTEXT_NAME, null);

		// add bundle 1 to context1
		resourceContext.addBundle(BUNDLE_ID);

		// remove bundle1
		resourceContext.removeBundle(BUNDLE_ID);

		// delete context1
		resourceContext.removeContext(null);
	}

	/**
	 * This method executes the following scenario:
	 * <ul>
	 * <li>Creates a ResourceContext named context2</li>
	 * <li>Adds bundle 2 to context2.</li>
	 * <li>Removes bundle 2 from context2.</li>
	 * <li>Deletes context2.</li>
	 * </ul>
	 * This method is called by all tests cases to generate
	 * ResourceContextEvent.
	 */
	private void executeScenario2() {
		// create context2
		ResourceContext resourceContext = resourceMonitoringService.createContext(
				RESOURCE_CONTEXT_NAME2, null);

		// add bundle 2 to context2
		resourceContext.addBundle(BUNDLE_ID2);

		// remove bundle2
		resourceContext.removeBundle(BUNDLE_ID2);

		// delete context2
		resourceContext.removeContext(null);
	}

	/**
	 * Registers this instance as a ResourceContextListener.
	 * 
	 * @param filters event filter types.
	 * @param resourceContexts resourceContexts
	 */
	private void registerListener(int[] filters, String[] resourceContexts) {
		Dictionary properties = new Hashtable();
		if (filters != null) {
			if (filters.length > 1) {
				properties.put(ResourceContextListener.EVENT_TYPE, filters);
			} else
				if (filters.length == 1) {
					properties.put(ResourceContextListener.EVENT_TYPE, Integer.valueOf(Integer.toString(filters[0])));
				}
		}
		if (resourceContexts != null) {
			if (resourceContexts.length > 1) {
				properties.put(ResourceContextListener.RESOURCE_CONTEXT,
						resourceContexts);
			} else
				if (resourceContexts.length == 1) {
					properties.put(ResourceContextListener.RESOURCE_CONTEXT,
							resourceContexts[0]);
				}
		}

		listenerServiceRegistration = bundleContext.registerService(
				ResourceContextListener.class.getName(), this, properties);
	}

	/**
	 * Unregisters the listener as a service.
	 */
	private void unregisterListener() {
		listenerServiceRegistration.unregister();
	}

}
