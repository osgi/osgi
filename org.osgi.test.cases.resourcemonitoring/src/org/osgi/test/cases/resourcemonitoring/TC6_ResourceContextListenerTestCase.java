/*
 * Copyright (c) OSGi Alliance (2014, 2015). All Rights Reserved.
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

package org.osgi.test.cases.resourcemonitoring;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.resourcemonitoring.ResourceContext;
import org.osgi.service.resourcemonitoring.ResourceContextEvent;
import org.osgi.service.resourcemonitoring.ResourceContextException;
import org.osgi.service.resourcemonitoring.ResourceContextListener;
import org.osgi.service.resourcemonitoring.ResourceMonitoringService;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * @author $Id$
 */
public class TC6_ResourceContextListenerTestCase extends DefaultTestBundleControl
		implements ResourceContextListener {

	/**
	 * name of the ResourceContext used for the tests
	 */
	private static final String			RESOURCE_CONTEXT_NAME	= "context1";

	/**
	 * name2 of the ResourceContext used for the tests
	 */
	private static final String			RESOURCE_CONTEXT_NAME2	= "context2";

	/**
	 * bundle id 1
	 */
	private static final long			BUNDLE_ID				= 1l;

	/**
	 * bundle id 2
	 */
	private static final long			BUNDLE_ID2				= 2l;

	/**
	 * bundle context
	 */
	private BundleContext				bundleContext;

	/**
	 * ResourceMonitoringService
	 */
	private ResourceMonitoringService	resourceMonitoringService;

	/**
	 * events
	 */
	private List						events;

	/**
	 * service registration of the listener
	 */
	private ServiceRegistration			listenerServiceRegistration;

	/**
	 * 
	 */
	public TC6_ResourceContextListenerTestCase() {
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
	 * Setup test.
	 * <p>
	 * Clears events list.
	 */
	protected void setUp() throws Exception {
		super.setUp();

		events.clear();
	}

	/**
	 * Test case 1 : ResourceContextListener filtering RESOURCE_CONTEXT_CREATED
	 * event.
	 * 
	 * @throws IllegalArgumentException, see
	 *         {@link TC6_ResourceContextListenerTestCase}
	 * @throws ResourceContextException, see
	 *         {@link TC6_ResourceContextListenerTestCase}
	 */
	public void testTC1ResourceContextListenerFiltering_RESOURCE_CONTEXT_CREATED_event() throws IllegalArgumentException, ResourceContextException {

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
		assertEquals("Events list mismatch.", 1, events.size());
		ResourceContextEvent event = (ResourceContextEvent) events.get(0);
		assertEquals("Type mismatch.", ResourceContextEvent.RESOURCE_CONTEXT_CREATED, event.getType());
		assertEquals("Name mismatch.", RESOURCE_CONTEXT_NAME, event.getContext().getName());
	}

	/**
	 * Test case 2: ResourceContextListener filtering RESOURCE_CONTEXT_REMOVED
	 * event.
	 * 
	 * @throws IllegalArgumentException, see
	 *         {@link TC6_ResourceContextListenerTestCase}
	 * @throws ResourceContextException, see
	 *         {@link TC6_ResourceContextListenerTestCase}
	 */
	public void testTC2ResourceContextListenerFiltering_RESOURCE_CONTEXT_REMOVED_event() throws IllegalArgumentException, ResourceContextException {
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
		assertEquals("Events list mismatch.", 1, events.size());
		ResourceContextEvent event = (ResourceContextEvent) events.get(0);
		assertEquals("Type mismatch.", ResourceContextEvent.RESOURCE_CONTEXT_REMOVED, event.getType());
		assertEquals("Name mismatch.", RESOURCE_CONTEXT_NAME, event.getContext().getName());
	}

	/**
	 * Test case 3: ResourceContextListener filtering BUNDLE_ADDED event.
	 * 
	 * @throws IllegalArgumentException, see
	 *         {@link TC6_ResourceContextListenerTestCase}
	 * @throws ResourceContextException, see
	 *         {@link TC6_ResourceContextListenerTestCase}
	 */
	public void testTC3ResourceContextListenerFiltering_BUNDLE_ADDED_event() throws IllegalArgumentException, ResourceContextException {
		// registers the ResourceContextListener with a BUNDLE_ADDED
		// type filter
		registerListener(new int[] {ResourceContextEvent.BUNDLE_ADDED}, null);

		// executes the scenarios
		executeScenario1();

		// unregisters the listener
		unregisterListener();

		// checks that only a ResourceContextEvent.BUNDLE_ADDED has been
		// received
		assertEquals("Events list mismatch.", 1, events.size());
		ResourceContextEvent event = (ResourceContextEvent) events.get(0);
		assertEquals("Type mismatch.", ResourceContextEvent.BUNDLE_ADDED, event.getType());
		assertEquals("Name mismatch.", RESOURCE_CONTEXT_NAME, event.getContext().getName());
		assertEquals("BundleId mismatch.", BUNDLE_ID, event.getBundleId());
	}

	/**
	 * Test case 4: ResourceContextListener filtering BUNDLE_REMOVED event.
	 * 
	 * @throws IllegalArgumentException, see
	 *         {@link TC6_ResourceContextListenerTestCase}
	 * @throws ResourceContextException, see
	 *         {@link TC6_ResourceContextListenerTestCase}
	 */
	public void testTC4ResourceContextListenerFiltering_BUNDLE_REMOVED_event() throws IllegalArgumentException, ResourceContextException {
		// registers the ResourceContextListener with a BUNDLE_REMOVED
		// type filter
		registerListener(new int[] {ResourceContextEvent.BUNDLE_REMOVED},
				null);

		// executes the scenarios
		executeScenario1();

		// unregisters the listener
		unregisterListener();

		// checks that only a ResourceContextEvent.BUNDLE_REMOVED has been
		// received
		assertEquals("Events list mismatch.", 1, events.size());
		ResourceContextEvent event = (ResourceContextEvent) events.get(0);
		assertEquals("Type mismatch.", ResourceContextEvent.BUNDLE_REMOVED, event.getType());
		assertEquals("Name mismatch.", RESOURCE_CONTEXT_NAME, event.getContext().getName());
		assertEquals("BundleId mismatch.", BUNDLE_ID, event.getBundleId());
	}

	/**
	 * Test case 5 : ResourceContextListener filtering events of a specific
	 * ResourceContext.
	 * 
	 * @throws IllegalArgumentException, see
	 *         {@link TC6_ResourceContextListenerTestCase}
	 * @throws ResourceContextException, see
	 *         {@link TC6_ResourceContextListenerTestCase}
	 */
	public void testTC5ResourceContextListenerFilteringEventsOfASpecificResourceContext() throws IllegalArgumentException, ResourceContextException {
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
		assertEquals("Events list mismatch.", 4, events.size());
		// first event is a RESOURCE_CONTEXT_CREATED for context1
		ResourceContextEvent createdEvent = (ResourceContextEvent) events
				.get(0);
		assertEquals("Type mismatch.", ResourceContextEvent.RESOURCE_CONTEXT_CREATED, createdEvent.getType());
		assertEquals("Name mismatch.", RESOURCE_CONTEXT_NAME, createdEvent.getContext().getName());

		// second event is a BUNDLE_ADDED event (bundle1 was added to context1)
		ResourceContextEvent addedEvent = (ResourceContextEvent) events.get(1);
		assertEquals("Type mismatch.", ResourceContextEvent.BUNDLE_ADDED, addedEvent.getType());
		assertEquals("Name mismatch.", RESOURCE_CONTEXT_NAME, addedEvent.getContext().getName());
		assertEquals("BundleId mismatch.", BUNDLE_ID, addedEvent.getBundleId());

		// third event is a BUNDLE_REMOVED event (bundle1 was removed from
		// context1)
		ResourceContextEvent removedEvent = (ResourceContextEvent) events
				.get(2);
		assertEquals("Type mismatch.", ResourceContextEvent.BUNDLE_REMOVED, removedEvent.getType());
		assertEquals("Name mismatch.", RESOURCE_CONTEXT_NAME, removedEvent.getContext().getName());
		assertEquals("BundleId mismatch.", BUNDLE_ID, removedEvent.getBundleId());

		// and finally the fourth event is a RESOURCE_CONTEXT_REMOVED event
		// (context1 was deleted)
		ResourceContextEvent deletedEvent = (ResourceContextEvent) events
				.get(3);
		assertEquals("Type mismatch.", ResourceContextEvent.RESOURCE_CONTEXT_REMOVED, deletedEvent.getType());
		assertEquals("Name mismatch.", RESOURCE_CONTEXT_NAME, deletedEvent.getContext().getName());
	}

	/**
	 * Test case 6 : registering a ResourceContextListener filtering two types
	 * of events on a particular ResourceContext.
	 * 
	 * @throws IllegalArgumentException, see
	 *         {@link TC6_ResourceContextListenerTestCase}
	 * @throws ResourceContextException, see
	 *         {@link TC6_ResourceContextListenerTestCase}
	 */
	public void testTC6RegisteringAResourceContextListenerFilteringTwoTypesOfEventsOnAParticularResourceContext() throws IllegalArgumentException, ResourceContextException {
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
		assertEquals("Events list mismatch.", 2, events.size());
		// first event is a RESOURCE_CONTEXT_CREATED for context1
		ResourceContextEvent createdEvent = (ResourceContextEvent) events
				.get(0);
		assertEquals("Type mismatch.", ResourceContextEvent.RESOURCE_CONTEXT_CREATED, createdEvent.getType());
		assertEquals("Name mismatch.", RESOURCE_CONTEXT_NAME, createdEvent.getContext().getName());
		// and finally the second event is a RESOURCE_CONTEXT_REMOVED event
		// (context1 was deleted)
		ResourceContextEvent deletedEvent = (ResourceContextEvent) events
				.get(1);
		assertEquals("Type mismatch.", ResourceContextEvent.RESOURCE_CONTEXT_REMOVED, deletedEvent.getType());
		assertEquals("Name mismatch.", RESOURCE_CONTEXT_NAME, deletedEvent.getContext().getName());
	}

	/**
	 * Test case 7 : unregistering a ResourceContextListener.
	 * 
	 * @throws IllegalArgumentException, see
	 *         {@link TC6_ResourceContextListenerTestCase}
	 * @throws ResourceContextException, see
	 *         {@link TC6_ResourceContextListenerTestCase}
	 */
	public void testTC7UnregisteringAResourceContextListener() throws IllegalArgumentException, ResourceContextException {
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
		assertEquals("Events list mismatch.", 4, events.size());
		// first event is a RESOURCE_CONTEXT_CREATED for context2
		ResourceContextEvent createdEvent = (ResourceContextEvent) events
				.get(0);
		assertEquals("Type mismatch.", ResourceContextEvent.RESOURCE_CONTEXT_CREATED, createdEvent.getType());
		assertEquals("Name mismatch.", RESOURCE_CONTEXT_NAME2, createdEvent.getContext().getName());

		// second event is a BUNDLE_ADDED event (bundle2 was added to context2)
		ResourceContextEvent addedEvent = (ResourceContextEvent) events.get(1);
		assertEquals("Type mismatch.", ResourceContextEvent.BUNDLE_ADDED, addedEvent.getType());
		assertEquals("Name mismatch.", RESOURCE_CONTEXT_NAME2, addedEvent.getContext().getName());
		assertEquals("BundleId mismatch.", BUNDLE_ID2, addedEvent.getBundleId());

		// third event is a BUNDLE_REMOVED event (bundle2 was removed from
		// context2)
		ResourceContextEvent removedEvent = (ResourceContextEvent) events
				.get(2);
		assertEquals("Type mismatch.", ResourceContextEvent.BUNDLE_REMOVED, removedEvent.getType());
		assertEquals("Name mismatch.", RESOURCE_CONTEXT_NAME2, removedEvent.getContext().getName());
		assertEquals("BundleId mismatch.", BUNDLE_ID2, removedEvent.getBundleId());

		// and finally the fourth event is a RESOURCE_CONTEXT_REMOVED event
		// (context2 was deleted)
		ResourceContextEvent deletedEvent = (ResourceContextEvent) events
				.get(3);
		assertEquals("Type mismatch.", ResourceContextEvent.RESOURCE_CONTEXT_REMOVED, deletedEvent.getType());
		assertEquals("Name mismatch.", RESOURCE_CONTEXT_NAME2, deletedEvent.getContext().getName());
	}

	/**
	 * This method is called to report a ResourceContextEvent. The received
	 * event is added into the events list.
	 */
	public void notify(ResourceContextEvent event) {
		log(TC6_ResourceContextListenerTestCase.class.getName() + " - event.getType(): " + event.getType() + ", event.getBundleId(): " + event.getBundleId());
		// add the event into the list.
		events.add(event);
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
	 * 
	 * @throws IllegalArgumentException, see
	 *         {@link ResourceMonitoringService#createContext(String, ResourceContext)}
	 * @throws ResourceContextException, see
	 *         {@link ResourceContext#addBundle(long)}
	 *         {@link ResourceContext#removeBundle(long)}
	 *         {@link ResourceContext#removeContext(ResourceContext)}
	 */
	private void executeScenario1() throws IllegalArgumentException, ResourceContextException {
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
	 * 
	 * @throws IllegalArgumentException, see
	 *         {@link ResourceMonitoringService#createContext(String, ResourceContext)}
	 * @throws ResourceContextException, see
	 *         {@link ResourceContext#addBundle(long)}
	 *         {@link ResourceContext#removeBundle(long)}
	 *         {@link ResourceContext#removeContext(ResourceContext)}
	 */
	private void executeScenario2() throws IllegalArgumentException, ResourceContextException {
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
