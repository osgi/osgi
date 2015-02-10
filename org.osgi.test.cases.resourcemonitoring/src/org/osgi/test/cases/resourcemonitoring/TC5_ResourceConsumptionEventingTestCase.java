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
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import org.osgi.framework.BundleContext;
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
import org.osgi.test.support.compatibility.Semaphore;

/**
 * @author $Id$
 */
public class TC5_ResourceConsumptionEventingTestCase extends DefaultTestBundleControl
		implements ResourceListener {

	private static final String			CONTEXT_NAME	= "context1";

	/**
	 * bundle context
	 */
	private BundleContext				bundleContext;

	/**
	 * resource monitor
	 */
	private ResourceMonitor				resourceMonitor;

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
	private ResourceMonitorFactory		cpuFactory;

	// thresholds
	private Comparable					lowerError;
	private Comparable					lowerWarning;
	private Comparable					upperError;
	private Comparable					upperWarning;

	/**
	 * Service registration of the listener
	 * ServiceRegistration<ResourceListener>
	 */
	private ServiceRegistration			listenerSr;

	/**
	 * list of received events
	 */
	private final List					receivedEvents	= new ArrayList();

	/**
	 * See org.osgi.test.cases.enocean.utils.EventListener class.
	 */
	private final Semaphore				waiter			= new Semaphore();

	/**
	 * last event received.
	 */
	private ResourceEvent				lastEvent;

	public void setBundleContext(BundleContext context) {
		bundleContext = context;

		// retrieve the ResourceMonitoringService
		ServiceReference resourceMonitoringServiceSr = bundleContext
				.getServiceReference(ResourceMonitoringService.class);
		resourceMonitoringService = (ResourceMonitoringService) bundleContext.getService(resourceMonitoringServiceSr);

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
			Collection factorySrs = bundleContext.getServiceReferences(
					ResourceMonitorFactory.class, filter.toString());
			if ((factorySrs != null) && (factorySrs.size() > 0)) {
				cpuFactory = (ResourceMonitorFactory) bundleContext
						.getService((ServiceReference) factorySrs
								.iterator().next());
			}
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
	}

	protected void setUp() throws Exception {
		super.setUp();

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

		waiter.signal();
	}

	/**
	 * Test case 1 : upper warning threshold.
	 * 
	 * This test case checks the receiving of an upper WARNING event type.
	 * 
	 * @throws InterruptedException
	 */
	public void testUpperWarningThreshold() throws InterruptedException {
		// set upper WARNING threshold to 1
		upperWarning = Long.valueOf(Long.toString(1l));

		// set other threshold
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

		// check received events
		checkReceivedEvents();
	}

	/**
	 * Test case 2 : upper error threshold.
	 * 
	 * This test case checks the receiving of an upper ERROR event type.
	 * 
	 * @throws InterruptedException
	 */
	public void testUpperErrorThreshold() throws InterruptedException {
		// set upper ERROR threshold to 2
		upperError = Long.valueOf(Long.toString(2l));

		// set other threshold
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

		// check received events
		checkReceivedEvents();
	}

	/**
	 * Test case 3 : upper warning and error thresholds.
	 * 
	 * This test case checks the receiving of upper WARNING event type and upper
	 * ERROR event type.
	 * 
	 * @throws InterruptedException
	 */
	public void testUpperWarningAndErrorThreshold() throws InterruptedException {
		// set upper WARNING threshold to 1
		upperWarning = Long.valueOf(Long.toString(1l));
		// set upper ERROR threshold to 2
		upperError = Long.valueOf(Long.toString(2l));

		// set other threshold

		lowerWarning = null;
		lowerError = null;

		// register the listener
		registerListener();

		// wait for events
		log("Wait for events (timeout 10000ms).");
		waitForEvent(10000);

		// unregister the listener
		unregisterListener();

		// check received events
		checkReceivedEvents();
	}

	/**
	 * Test case 4 : lower warning threshold.
	 * 
	 * This test case checks the receiving of a lower WARNING event type.
	 * 
	 * @throws InterruptedException
	 */
	public void testLowerWarningThreshold() throws InterruptedException {
		// set lower WARNING threshold to 100
		lowerWarning = Long.valueOf(Long.toString(100l));

		// set other threshold
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

		// check received events
		checkReceivedEvents();
	}

	/**
	 * Test case 5 : lower error threshold.
	 * 
	 * This test case checks the receiving of lower ERROR event type.
	 * 
	 * @throws InterruptedException
	 */
	public void testLowerErrorThreshold() throws InterruptedException {
		// set lower ERROR threshold to 99
		lowerError = Long.valueOf(Long.toString(99l));

		// set other threshold
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

		// check received events
		checkReceivedEvents();
	}

	/**
	 * Test case 6 : lower error and lower warning threshold.
	 * 
	 * This test case checks the receiving of lower ERROR and lower WARNING
	 * event type.
	 * 
	 * @throws InterruptedException
	 */
	public void testLowerWarningAndErrorThreshold() throws InterruptedException {
		// set lower WARNING threshold to 100
		lowerWarning = Long.valueOf(Long.toString(100l));
		// set lower ERROR threshold to 99
		lowerError = Long.valueOf(Long.toString(99l));

		// set other threshold
		upperWarning = null;
		upperError = null;

		// register the listener
		registerListener();

		// wait for events
		log("Wait for events (timeout 10000ms).");
		waitForEvent(10000);

		// unregister the listener
		unregisterListener();

		// check received events
		checkReceivedEvents();
	}

	public void notify(ResourceEvent event) {
		log(TC5_ResourceConsumptionEventingTestCase.class.getName() + " - event.getType(): " + event.getType() + ", value: " + event.getValue());
		receivedEvents.add(event);
		lastEvent = event;
		waiter.signal();
	}

	public Comparable getLowerWarningThreshold() {
		return lowerWarning;
	}

	public Comparable getLowerErrorThreshold() {
		return lowerError;
	}

	public Comparable getUpperWarningThreshold() {
		return upperWarning;
	}

	public Comparable getUpperErrorThreshold() {
		return upperError;
	}

	/**
	 * Waits for a ResourceEvent to occur.
	 * 
	 * @param timeout
	 * @return the ResourceEvent or null.
	 * @throws InterruptedException
	 */
	public ResourceEvent waitForEvent(long timeout) throws InterruptedException {
		if (waiter.waitForSignal(timeout)) {
			return lastEvent;
		}
		return null;
	}

	/**
	 * Waits for a ResourceEvent to occur. Default timeout is:
	 * OSGiTestCaseProperties.getTimeout().
	 * 
	 * @return the ResourceEvent or null.
	 * @throws InterruptedException
	 */
	public ResourceEvent waitForEvent() throws InterruptedException {
		return waitForEvent(OSGiTestCaseProperties.getTimeout());
	}

	/**
	 * Register this instance as a {@link ResourceListener} service.
	 */
	private void registerListener() {
		Dictionary dictionary = new Hashtable();
		if (upperError != null) {
			dictionary.put(ResourceListener.UPPER_ERROR_THRESHOLD, upperError);
		}
		if (upperWarning != null) {
			dictionary.put(ResourceListener.UPPER_WARNING_THRESHOLD,
					upperWarning);
		}
		if (lowerError != null) {
			dictionary.put(ResourceListener.LOWER_ERROR_THRESHOLD, lowerError);
		}
		if (lowerWarning != null) {
			dictionary.put(ResourceListener.LOWER_WARNING_THRESHOLD,
					lowerWarning);
		}
		dictionary.put(ResourceListener.RESOURCE_CONTEXT,
				resourceContext.getName());
		dictionary.put(ResourceListener.RESOURCE_TYPE,
				resourceMonitor.getResourceType());
		listenerSr = bundleContext.registerService(ResourceListener.class,
				this, dictionary);

	}

	/**
	 * Unregister the listener.
	 */
	private void unregisterListener() {
		listenerSr.unregister();
	}

	/**
	 * Check the received events.
	 */
	private void checkReceivedEvents() {
		ResourceEvent previousEvent = null;
		for (Iterator it = receivedEvents.iterator(); it.hasNext();) {
			ResourceEvent currentEvent = (ResourceEvent) it.next();

			if (previousEvent != null) {
				assertTrue("Types must not match.", previousEvent.getType() != currentEvent.getType());

				int eventType = currentEvent.getType();
				Object value = currentEvent.getValue();
				boolean isUpperThreshold = currentEvent.isUpperThreshold();

				log("checked event (type:" + eventType + ", value=" + value
						+ ", isUpper:" + isUpperThreshold);

				Comparable threshold = null;
				if (isUpperThreshold) {
					if (eventType == ResourceEvent.NORMAL) {
						// check either if current value is under ERROR and
						// WARNING threshold
						// if those thresholds are not null
						// at least, one of them is not null

						boolean checked = false;
						threshold = getUpperWarningThreshold();
						if (threshold != null) {
							if (threshold.compareTo(value) < 0) {
								log("=====================ERROR================, warning th:"
										+ threshold + ", value:" + value);
								fail();
							}
							checked = true;
						}

						threshold = getUpperErrorThreshold();
						if (threshold != null) {
							log("upperThreshold:" + threshold + ", value="
									+ value);
							assertTrue("threshold.compareTo(value) > 0 is expected.", threshold.compareTo(value) > 0);
							checked = true;
						}
						assertTrue("Value must be checked.", checked);

					} else
						if (eventType == ResourceEvent.WARNING) {
							// check if current value is higher than WARNING
							// threshold
							threshold = getUpperWarningThreshold();
							assertNotNull("Threshold must no be null.", threshold);
							assertTrue("threshold.compareTo(value) <= 0 is expected.", threshold.compareTo(value) <= 0);

							// check if current value is under ERROR threshold
							threshold = getUpperErrorThreshold();
							if (threshold != null) {
								assertTrue("threshold.compareTo(value) > 0 is expected.", threshold.compareTo(value) > 0);
							}

						} else
							if (eventType == ResourceEvent.ERROR) {
								// check if current value is higher than the
								// ERROR
								// threshold
								threshold = getUpperErrorThreshold();
								assertNotNull("Threshold must no be null.", threshold);
								assertTrue("threshold.compareTo(value) <= 0 is expected.", threshold.compareTo(value) <= 0);

								// check if current value is higher than the
								// WARNING
								// threshold
								threshold = getUpperWarningThreshold();
								if (threshold != null) {
									assertTrue("threshold.compareTo(value) < 0 is expected.", threshold.compareTo(value) < 0);
								}
							}
				} else {
					// lower threshold type
					if (eventType == ResourceEvent.NORMAL) {
						// check if current value is over WARNING threshold
						threshold = getLowerWarningThreshold();
						boolean checked = false;
						if (threshold != null) {
							log("lowerWarningThreshold: " + threshold
									+ ", value=" + value);
							assertTrue("threshold.compareTo(value) < 0 is expected.", threshold.compareTo(value) < 0);
							checked = true;
						}

						// check if current value is over ERROR threholds
						threshold = getLowerErrorThreshold();
						if (threshold != null) {
							assertTrue("threshold.compareTo(value) < 0 is expected.", threshold.compareTo(value) < 0);
							checked = true;
						}

						// at least, one of the two thresholds has to be set
						assertTrue("Value must be checked.", checked);
					} else
						if (eventType == ResourceEvent.WARNING) {
							// check current value is under WARNING threshold
							threshold = getLowerWarningThreshold();
							assertNotNull("Threshold must no be null.", threshold);
							assertTrue("threshold.compareTo(value) >= 0 is expected.", threshold.compareTo(value) >= 0);

							// check current value is higher than ERROR
							// threshold
							threshold = getLowerErrorThreshold();
							if (threshold != null) {
								assertTrue("threshold.compareTo(value) < 0 is expected.", threshold.compareTo(value) < 0);
							}
						} else
							if (eventType == ResourceEvent.ERROR) {
								// check current value is under ERROR threshold
								threshold = getLowerErrorThreshold();
								assertNotNull("Threshold must no be null.", threshold);
								assertTrue("threshold.compareTo(value) >= 0 is expected.", threshold.compareTo(value) >= 0);

								// check current value is under WARNING
								// threshold
								threshold = getLowerWarningThreshold();
								if (threshold != null) {
									assertTrue("threshold.compareTo(value) > 0 is expected.", threshold.compareTo(value) > 0);
								}
							}
				}
			}
			previousEvent = currentEvent;
		}
	}
}
