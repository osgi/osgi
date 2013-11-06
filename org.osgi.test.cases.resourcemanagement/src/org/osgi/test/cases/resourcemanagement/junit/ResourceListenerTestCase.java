package org.osgi.test.cases.resourcemanagement.junit;

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
import org.osgi.service.resourcemanagement.ResourceContext;
import org.osgi.service.resourcemanagement.ResourceEvent;
import org.osgi.service.resourcemanagement.ResourceListener;
import org.osgi.service.resourcemanagement.ResourceManager;
import org.osgi.service.resourcemanagement.ResourceMonitor;
import org.osgi.service.resourcemanagement.ResourceMonitorFactory;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public class ResourceListenerTestCase extends DefaultTestBundleControl
		implements ResourceListener {

	private static final String CONTEXT_NAME = "context1";

	/**
	 * bundle context
	 */
	private BundleContext bundleContext;

	/**
	 * resource monitor
	 */
	private ResourceMonitor resourceMonitor;

	/**
	 * resource context
	 */
	private ResourceContext resourceContext;

	/**
	 * resource manager
	 */
	private ResourceManager resourceManager;

	/**
	 * cpu Resource Monitor Factory
	 */
	private ResourceMonitorFactory cpuFactory;

	// thresholds
	private Comparable lowerError;
	private Comparable lowerWarning;
	private Comparable upperError;
	private Comparable upperWarning;

	/**
	 * Service registration of the listener
	 */
	private ServiceRegistration<ResourceListener> listenerSr;

	/**
	 * list of received events
	 */
	private final List receivedEvents;

	public ResourceListenerTestCase() {
		receivedEvents = new ArrayList();
	}

	public void setBundleContext(BundleContext context) {
		bundleContext = context;

		// retrieve the ResourceManager
		ServiceReference<ResourceManager> resourceManagerSr = bundleContext
				.getServiceReference(ResourceManager.class);
		resourceManager = bundleContext.getService(resourceManagerSr);

		// retrieve cpu factory
		StringBuffer filter = new StringBuffer();
		filter.append("(&(");
		filter.append(Constants.OBJECTCLASS);
		filter.append("=");
		filter.append(ResourceMonitorFactory.class.getName());
		filter.append(")(");
		filter.append(ResourceMonitorFactory.RESOURCE_TYPE_PROPERTY);
		filter.append("=");
		filter.append(ResourceManager.RES_TYPE_CPU);
		filter.append("))");
		try {
			Collection factorySrs = bundleContext.getServiceReferences(
					ResourceMonitorFactory.class, filter.toString());
			if ((factorySrs != null) && (factorySrs.size() > 0)) {
				cpuFactory = bundleContext
						.getService((ServiceReference<ResourceMonitorFactory>) factorySrs
								.iterator().next());
			}
		} catch (InvalidSyntaxException e) {
		}

	}

	protected void setUp() throws Exception {
		// create a ResourceContext
		resourceContext = resourceManager.createContext(CONTEXT_NAME, null);

		// create a CPU
		resourceMonitor = cpuFactory.createResourceMonitor(resourceContext);
		resourceMonitor.enable();

		receivedEvents.clear();

	}

	protected void tearDown() throws Exception {
		// remove the context
		resourceContext.removeContext(null);

		upperError = null;
		upperWarning = null;
		lowerWarning = null;
		lowerError = null;
	}

	/**
	 * Test upper threshold.
	 * 
	 * @throws InterruptedException
	 */
	public void testUpperErrorThreshold() throws InterruptedException {

		// set upper ERROR threshold to 50
		upperError = 50l;

		// set other threshold
		upperWarning = null;
		lowerWarning = null;
		lowerError = null;

		// register the listener
		registerListener();

		// wait for events
		Thread.sleep(150000);

		// unregister the listener
		unregisterListener();

		// check received events
		checkReceivedEvents();
	}

	/**
	 * Test upper warning threshold.
	 * 
	 * @throws InterruptedException
	 */
	public void testUpperWarningThreshold() throws InterruptedException {
		// set upper WARNING threshold to 35
		upperWarning = 35l;

		// set other threshold
		upperError = null;
		lowerWarning = null;
		lowerError = null;

		// register the listener
		registerListener();

		// wait for events
		Thread.sleep(150000);

		// unregister the listener
		unregisterListener();

		// check received events
		checkReceivedEvents();
	}

	/**
	 * Upper warning and error threshold.
	 * 
	 * @throws InterruptedException
	 */
	public void testUpperWarningAndErrorThreshold() throws InterruptedException {
		// set upper WARNING threshold to 35
		upperWarning = 35l;
		upperError = 50l;

		// set other threshold

		lowerWarning = null;
		lowerError = null;

		// register the listener
		registerListener();

		// wait for events
		Thread.sleep(150000);

		// unregister the listener
		unregisterListener();

		// check received events
		checkReceivedEvents();
	}

	/**
	 * Test lower warning threshold.
	 * 
	 * @throws InterruptedException
	 */
	public void testLowerWarningThreshold() throws InterruptedException {
		lowerWarning = 70l;

		// set other threshold
		lowerError = null;
		upperWarning = null;
		upperError = null;

		// register the listener
		registerListener();

		// wait for events
		Thread.sleep(150000);

		// unregister the listener
		unregisterListener();

		// check received events
		checkReceivedEvents();
	}

	/**
	 * Test lower error threshold.
	 * 
	 * @throws InterruptedException
	 */
	public void testLowerErrorThreshold() throws InterruptedException {
		lowerError = 70l;

		// set other threshold
		lowerWarning = null;
		upperWarning = null;
		upperError = null;

		// register the listener
		registerListener();

		// wait for events
		Thread.sleep(150000);

		// unregister the listener
		unregisterListener();

		// check received events
		checkReceivedEvents();
	}

	/**
	 * Test lower warning and error threshold.
	 * 
	 * @throws InterruptedException
	 */
	public void testLowerWarningAndErrorThreshold() throws InterruptedException {
		lowerError = 50l;
		lowerWarning = 70l;

		// set other threshold
		upperWarning = null;
		upperError = null;

		// register the listener
		registerListener();

		// wait for events
		Thread.sleep(150000);

		// unregister the listener
		unregisterListener();

		// check received events
		checkReceivedEvents();
	}

	public void notify(ResourceEvent event) {
		log("event : " + event.getType() + ", value:" + event.getValue());
		receivedEvents.add(event);

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
				assertTrue(previousEvent.getType() != currentEvent.getType());

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
							assertTrue(threshold.compareTo(value) > 0);
							checked = true;
						}
						assertTrue(checked);

					} else if (eventType == ResourceEvent.WARNING) {
						// check if current value is higher than WARNING
						// threshold
						threshold = getUpperWarningThreshold();
						assertNotNull(threshold);
						assertTrue(threshold.compareTo(value) <= 0);

						// check if current value is under ERROR threshold
						threshold = getUpperErrorThreshold();
						if (threshold != null) {
							assertTrue(threshold.compareTo(value) > 0);
						}

					} else if (eventType == ResourceEvent.ERROR) {
						// check if current value is higher than the ERROR
						// threshold
						threshold = getUpperErrorThreshold();
						assertNotNull(threshold);
						assertTrue(threshold.compareTo(value) <= 0);

						// check if current value is higher than the WARNING
						// threshold
						threshold = getUpperWarningThreshold();
						if (threshold != null) {
							assertTrue(threshold.compareTo(value) < 0);
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
							assertTrue(threshold.compareTo(value) < 0);
							checked = true;
						}

						// check if current value is over ERROR threholds
						threshold = getLowerErrorThreshold();
						if (threshold != null) {
							assertTrue(threshold.compareTo(value) < 0);
							checked = true;
						}

						// at least, one of the two thresholds has to be set
						assertTrue(checked);
					} else if (eventType == ResourceEvent.WARNING) {
						// check current value is under WARNING threshold
						threshold = getLowerWarningThreshold();
						assertNotNull(threshold);
						assertTrue(threshold.compareTo(value) >= 0);

						// check current value is higher than ERROR threshold
						threshold = getLowerErrorThreshold();
						if (threshold != null) {
							assertTrue(threshold.compareTo(value) < 0);
						}
					} else if (eventType == ResourceEvent.ERROR) {
						// check current value is under ERROR threshold
						threshold = getLowerErrorThreshold();
						assertNotNull(threshold);
						assertTrue(threshold.compareTo(value) >= 0);

						// check current value is under WARNING threshold
						threshold = getLowerWarningThreshold();
						if (threshold != null) {
							assertTrue(threshold.compareTo(value) > 0);
						}
					}
				}

			}
			previousEvent = currentEvent;
		}
	}
}
