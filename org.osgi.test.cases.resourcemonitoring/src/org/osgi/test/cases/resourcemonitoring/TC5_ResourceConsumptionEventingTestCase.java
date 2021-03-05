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

package org.osgi.test.cases.resourcemonitoring;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.resourcemonitoring.ResourceContext;
import org.osgi.service.resourcemonitoring.ResourceEvent;
import org.osgi.service.resourcemonitoring.ResourceListener;
import org.osgi.service.resourcemonitoring.ResourceMonitor;
import org.osgi.service.resourcemonitoring.ResourceMonitorFactory;
import org.osgi.service.resourcemonitoring.ResourceMonitoringService;
import org.osgi.test.support.OSGiTestCaseProperties;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * @author $Id$
 */
public class TC5_ResourceConsumptionEventingTestCase
		extends DefaultTestBundleControl implements ResourceListener<Long> {

	private static final String			CONTEXT_NAME	= "context1";

	/**
	 * resource monitor
	 */
	private ResourceMonitor< ? >		resourceMonitor;

	/**
	 * resource context
	 */
	private ResourceContext				resourceContext;

	/**
	 * ResourceMonitoringService
	 */
	private ResourceMonitoringService	resourceMonitoringService;

	/**
	 * cpu Resource Monitor Factory
	 */
	private ResourceMonitorFactory< ? >	cpuFactory;

	// thresholds
	private Long										lowerError;
	private Long										lowerWarning;
	private Long										upperError;
	private Long										upperWarning;

	/**
	 * Service registration of the listener ServiceRegistration<ResourceListener>
	 */
	private ServiceRegistration<ResourceListener< ? >>	listenerSr;

	/**
	 * list of received events
	 */
	private final List<ResourceEvent<Long>>				receivedEvents	= new ArrayList<>();

	/**
	 * See org.osgi.test.cases.enocean.utils.EventListener class.
	 */
	private final Semaphore				waiter			= new Semaphore(0);

	/**
	 * last event received.
	 */
	private ResourceEvent<Long>							lastEvent;

	protected void setUp() throws Exception {
		super.setUp();
		// retrieve the ResourceMonitoringService
		ServiceReference<ResourceMonitoringService> resourceMonitoringServiceSr = getContext()
				.getServiceReference(ResourceMonitoringService.class);
		resourceMonitoringService = getContext()
				.getService(resourceMonitoringServiceSr);

		// retrieve cpu factory
		StringBuffer filter = new StringBuffer();
		filter.append("(&(");
		filter.append(Constants.OBJECTCLASS);
		filter.append("=");
		filter.append(ResourceMonitorFactory.class.getName());
		filter.append(")(");
		filter.append(ResourceMonitorFactory.RESOURCE_TYPE_PROPERTY);
		filter.append("=");
		filter.append(ResourceMonitoringService.RES_TYPE_CPU);
		filter.append("))");
		try {
			@SuppressWarnings({
					"rawtypes", "unchecked"
			})
			Collection<ServiceReference<ResourceMonitorFactory< ? >>> factorySrs = (Collection) getContext()
					.getServiceReferences(
					ResourceMonitorFactory.class, filter.toString());
			if ((factorySrs != null) && (factorySrs.size() > 0)) {
				cpuFactory = getContext().getService(
						 factorySrs.iterator().next());
			}
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
			fail("Can not get the already existing OSGi service resource monitor factories.");
		}

		// create a ResourceContext
		resourceContext = resourceMonitoringService.createContext(CONTEXT_NAME, null);

		// create a CPU
		resourceMonitor = cpuFactory.createResourceMonitor(resourceContext);
		resourceMonitor.enable();

		receivedEvents.clear();

	}

	protected void tearDown() throws Exception {
		super.tearDown();

		// remove the context
		resourceContext.removeContext(null);

		upperError = null;
		upperWarning = null;
		lowerWarning = null;
		lowerError = null;

		waiter.release();
	}

	/**
	 * Test case 1: upper warning threshold.
	 * 
	 * This test case checks the receiving of an upper WARNING event type.
	 * 
	 * @throws InterruptedException
	 */
	public void testUpperWarningThreshold() throws InterruptedException {
		// set upper WARNING threshold to 0
		upperWarning = Long.valueOf(0);

		// set the other thresholds to null.
		upperError = null;
		lowerWarning = null;
		lowerError = null;

		// register the listener
		registerListener();

		// wait for events
		log("Wait for events (timeout 10000ms).");
		waitForEvent(10000);

		// unregister the listener
		unregisterListener();

		// check received event
		checkUpperWarningReceivedEvent();
	}

	/**
	 * Test case 2: upper error threshold.
	 * 
	 * This test case checks the receiving of an upper ERROR event type.
	 * 
	 * @throws InterruptedException
	 */
	public void testUpperErrorThreshold() throws InterruptedException {
		// set upper ERROR threshold to A
		upperError = Long.valueOf(1);

		// set the other thresholds to null.
		upperWarning = null;
		lowerWarning = null;
		lowerError = null;

		// register the listener
		registerListener();

		// wait for events
		log("Wait for events (timeout 10000ms).");
		waitForEvent(10000);

		// unregister the listener
		unregisterListener();

		// check received event
		checkUpperErrorReceivedEvent();
	}

	/**
	 * Test case 3: lower warning threshold.
	 * 
	 * This test case checks the receiving of a lower WARNING event type.
	 * 
	 * @throws InterruptedException
	 */
	public void testLowerWarningThreshold() throws InterruptedException {
		// set lower WARNING threshold to 100
		lowerWarning = Long.valueOf(100);

		// set the other thresholds to null.
		lowerError = null;
		upperWarning = null;
		upperError = null;

		// register the listener
		registerListener();

		// wait for events
		log("Wait for events (timeout 10000ms).");
		waitForEvent(10000);

		// unregister the listener
		unregisterListener();

		// check received event
		checkLowerWarningReceivedEvent();
	}

	/**
	 * Test case 4: lower error threshold.
	 * 
	 * This test case checks the receiving of lower ERROR event type.
	 * 
	 * @throws InterruptedException
	 */
	public void testLowerErrorThreshold() throws InterruptedException {
		// set lower ERROR threshold to 99
		lowerError = Long.valueOf(99);

		// set the other thresholds to null.
		lowerWarning = null;
		upperWarning = null;
		upperError = null;

		// register the listener
		registerListener();

		// wait for events
		log("Wait for events (timeout 10000ms).");
		waitForEvent(10000);

		// unregister the listener
		unregisterListener();

		// check received event
		checkLowerErrorReceivedEvent();
	}

	/**
	 * Register this instance as a {@link ResourceListener} service.
	 */
	@SuppressWarnings({
			"rawtypes", "unchecked"
	})
	private void registerListener() {
		Dictionary<String,Object> dictionary = new Hashtable<>();
		if (upperError != null) {
			dictionary.put(ResourceListener.UPPER_ERROR_THRESHOLD, upperError);
		}
		if (upperWarning != null) {
			dictionary.put(ResourceListener.UPPER_WARNING_THRESHOLD, upperWarning);
		}
		if (lowerError != null) {
			dictionary.put(ResourceListener.LOWER_ERROR_THRESHOLD, lowerError);
		}
		if (lowerWarning != null) {
			dictionary.put(ResourceListener.LOWER_WARNING_THRESHOLD, lowerWarning);
		}
		dictionary.put(ResourceListener.RESOURCE_CONTEXT, resourceContext.getName());
		dictionary.put(ResourceListener.RESOURCE_TYPE, resourceMonitor.getResourceType());
		listenerSr = (ServiceRegistration) getContext()
				.registerService(ResourceListener.class, this,
				dictionary);
	}

	/**
	 * Unregister the listener.
	 */
	private void unregisterListener() {
		listenerSr.unregister();
	}

	/**
	 * Check the upper warning received event.
	 */
	private void checkUpperWarningReceivedEvent() {
		assertEquals("One, and only one event is expected to be received. Here, " + receivedEvents.size() + " were received.", 1,
				receivedEvents.size());

		ResourceEvent<Long> currentEvent = receivedEvents.iterator().next();
		int eventType = currentEvent.getType();
		Comparable<Long> eventValue = currentEvent.getValue();
		assertNotNull("The event value must not be null.", eventValue);
		boolean eventIsUpperThreshold = currentEvent.isUpperThreshold();

		log("currentEvent - type: " + eventType + ", value: " + eventValue + ", isUpperThreshold: " + eventIsUpperThreshold);

		if (eventIsUpperThreshold) {
			// Here, the event is an "upper" event.
			if (eventType == ResourceEvent.WARNING) {
				// Check if the current eventValue is greater than, or equal to WARNING threshold.
				Long threshold = upperWarning;
				assertNotNull("Upper warning threshold must no be null.", threshold);
				assertTrue("eventValue.compareTo(threshold) >= 0 is expected. EventValue is: " + eventValue + ", threshold is: "
						+ threshold, eventValue.compareTo(threshold) >= 0);
				// Check if the current eventValue is strictly less than the ERROR threshold.
				threshold = upperError;
				if (threshold != null) {
					assertTrue("eventValue.compareTo(threshold) < 0 is expected. EventValue is: " + eventValue + ", threshold is: "
							+ threshold, eventValue.compareTo(threshold) < 0);
				}
			} else {
				fail("An upper warning event is expected here.");
			}
		} else {
			fail("An upper event is expected here.");
		}
	}

	/**
	 * Check the upper error received event.
	 */
	private void checkUpperErrorReceivedEvent() {
		assertEquals("One, and only one event is expected to be received. Here, " + receivedEvents.size() + " were received.", 1,
				receivedEvents.size());

		ResourceEvent<Long> currentEvent = receivedEvents.iterator().next();
		int eventType = currentEvent.getType();
		Comparable<Long> eventValue = currentEvent.getValue();
		assertNotNull("The event value must not be null.", eventValue);
		boolean eventIsUpperThreshold = currentEvent.isUpperThreshold();

		log("currentEvent - type: " + eventType + ", value: " + eventValue + ", isUpperThreshold: " + eventIsUpperThreshold);

		if (eventIsUpperThreshold) {
			// Here, the event is an "upper" event.
			if (eventType == ResourceEvent.ERROR) {
				// Check if current eventValue is greater than, or equal to ERROR threshold
				Long threshold = upperError;
				assertNotNull("Upper error threshold must no be null.", threshold);
				assertTrue("eventValue.compareTo(threshold) >= 0 is expected. EventValue is: " + eventValue + ", threshold is: "
						+ threshold, eventValue.compareTo(threshold) >= 0);
				// Check if current eventValue is strictly greater than the WARNING threshold
				threshold = upperWarning;
				if (threshold != null) {
					assertTrue("eventValue.compareTo(threshold) < 0 is expected. EventValue is: " + eventValue + ", threshold is: "
							+ threshold, eventValue.compareTo(threshold) > 0);
				}
			} else {
				fail("An upper error event is expected here.");
			}
		} else {
			fail("An upper event is expected here.");
		}
	}

	/**
	 * Check the lower warning received event.
	 */
	private void checkLowerWarningReceivedEvent() {
		assertEquals("One, and only one event is expected to be received. Here, " + receivedEvents.size() + " were received.", 1,
				receivedEvents.size());

		ResourceEvent<Long> currentEvent = receivedEvents.iterator().next();
		int eventType = currentEvent.getType();
		Comparable<Long> eventValue = currentEvent.getValue();
		assertNotNull("The event value must not be null.", eventValue);
		boolean eventIsUpperThreshold = currentEvent.isUpperThreshold();

		log("currentEvent - type: " + eventType + ", value: " + eventValue + ", isUpperThreshold: " + eventIsUpperThreshold);

		if (eventIsUpperThreshold) {
			fail("A lower event is expected here.");
		} else {
			// Here, the event is a "lower" event.
			if (eventType == ResourceEvent.WARNING) {
				// Check if the current eventValue is less than, or equal to lower WARNING threshold.
				Long threshold = lowerWarning;
				assertNotNull("Lower warning threshold must no be null.", threshold);
				assertTrue("eventValue.compareTo(threshold) <= 0 is expected. EventValue is: " + eventValue + ", threshold is: "
						+ threshold, eventValue.compareTo(threshold) <= 0);
				// Check if the current eventValue is strictly greater than the lower ERROR threshold.
				threshold = lowerError;
				if (threshold != null) {
					assertTrue("eventValue.compareTo(threshold) > 0 is expected.", eventValue.compareTo(threshold) > 0);
				}
			} else {
				fail("A lower error event is expected here.");
			}
		}
	}

	/**
	 * Check the lower error received event.
	 */
	private void checkLowerErrorReceivedEvent() {
		assertEquals("One, and only one event is expected to be received. Here, " + receivedEvents.size() + " were received.", 1,
				receivedEvents.size());

		ResourceEvent<Long> currentEvent = receivedEvents.iterator().next();
		int eventType = currentEvent.getType();
		Comparable<Long> eventValue = currentEvent.getValue();
		assertNotNull("The event value must not be null.", eventValue);
		boolean eventIsUpperThreshold = currentEvent.isUpperThreshold();

		log("currentEvent - type: " + eventType + ", value: " + eventValue + ", isUpperThreshold: " + eventIsUpperThreshold);

		if (eventIsUpperThreshold) {
			fail("A lower event is expected here.");
		} else {
			// Here, the event is a "lower" event.
			if (eventType == ResourceEvent.WARNING) {
				fail("A lower error event is expected here.");
			} else if (eventType == ResourceEvent.ERROR) {
				// Check if the current eventValue is less than, or equal to lower ERROR threshold.
				Long threshold = lowerError;
				assertNotNull("Lower error threshold must no be null.", threshold);
				assertTrue("eventValue.compareTo(threshold) <= 0 is expected. EventValue is: " + eventValue + ", threshold is: "
						+ threshold, eventValue.compareTo(threshold) <= 0);
				// Check if the current eventValue is strictly less than the lower WARNING threshold.
				threshold = lowerWarning;
				if (threshold != null) {
					assertTrue(
							"eventValue.compareTo(threshold) > 0 is expected.",
							eventValue.compareTo(threshold) > 0);
				}
			}
		}
	}

	/**
	 * Waits for a ResourceEvent to occur.
	 * 
	 * @param timeout
	 * @return the ResourceEvent or null.
	 * @throws InterruptedException
	 */
	public ResourceEvent<Long> waitForEvent(long timeout)
			throws InterruptedException {
		if (waiter.tryAcquire(timeout, TimeUnit.MILLISECONDS)) {
			return lastEvent;
		}
		return null;
	}

	/**
	 * Waits for a ResourceEvent to occur. Default timeout is: OSGiTestCaseProperties.getTimeout().
	 * 
	 * @return the ResourceEvent or null.
	 * @throws InterruptedException
	 */
	public ResourceEvent<Long> waitForEvent() throws InterruptedException {
		return waitForEvent(OSGiTestCaseProperties.getTimeout());
	}

	public void notify(ResourceEvent<Long> event) {
		log("event.getType(): " + event.getType() + ", event.getValue(): " + event.getValue() + ", event.isUpperThreshold(): "
				+ event.isUpperThreshold());
		receivedEvents.add(event);
		lastEvent = event;
		waiter.release();
	}

	public Comparable<Long> getLowerWarningThreshold() {
		return lowerWarning;
	}

	public Comparable<Long> getLowerErrorThreshold() {
		return lowerError;
	}

	public Comparable<Long> getUpperWarningThreshold() {
		return upperWarning;
	}

	public Comparable<Long> getUpperErrorThreshold() {
		return upperError;
	}

}
