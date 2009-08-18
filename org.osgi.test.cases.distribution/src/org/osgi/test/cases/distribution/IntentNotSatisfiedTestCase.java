/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 */
package org.osgi.test.cases.distribution;

import java.io.FileInputStream;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.discovery.ServicePublication;
import org.osgi.service.distribution.DistributionConstants;
import org.osgi.service.distribution.DistributionProvider;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.test.cases.distribution.internal.DistributedService;
import org.osgi.test.cases.distribution.internal.DistributedServiceImpl;
import org.osgi.test.cases.distribution.internal.DoNotPublishInterface;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.test.support.compatibility.Semaphore;

/**
 * RFC 119 Distributed OSGi TCK
 * 
 * These test cases focus on the DSW only. Discovery is covered by a separate TCK.
 * 
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 * @since 1.0.0
 */
public class IntentNotSatisfiedTestCase extends DefaultTestBundleControl {
	private static final String ALL_INTERFACES = "*";
	
	Properties             tckProperties;
	DistributionProvider   provider;
	ServiceReference       distributionReference;
	EventHandler           eventHandler;
	Object                 proxy;
	
	Semaphore              semExposed;
	Semaphore              semUnsatisfied;
	
	final String           ExposedTopic = DistributionProvider.class.getName().replace('.', '/') + "/service/exposed";
	final String           UnsatisfiedTopic = DistributionProvider.class.getName().replace('.', '/') + "/service/unsatisfied";

	long                   exposeTimeout;
	long                   proxyTimeout;
	long                   publishTimeout;
	
	protected void setUp() throws Exception {
		tckProperties = new Properties();
		tckProperties.put("distribution.tck.expose.timeout", "20000");
		tckProperties.put("distribution.tck.proxy.timeout", "20000");
		tckProperties.put("distribution.tck.publish.timeout", "4000");
		tckProperties.put("distribution.tck.cleanup.delay", "5000");
		
		try {
			tckProperties.load(new FileInputStream("tck.properties"));
		} catch (Exception e) {
			System.err.println("Failed to read TCK properties from tck.properties file");
		}
		
		exposeTimeout = Long.parseLong(tckProperties.getProperty("distribution.tck.expose.timeout"));
		proxyTimeout = Long.parseLong(tckProperties.getProperty("distribution.tck.proxy.timeout"));
		publishTimeout = Long.parseLong(tckProperties.getProperty("distribution.tck.publish.timeout"));
		
		distributionReference = getContext().getServiceReference(DistributionProvider.class.getName());
		assertNotNull(distributionReference);
		provider = (DistributionProvider) getContext().getService(distributionReference);
		assertNotNull(provider);
		
		semExposed = new Semaphore();
		semUnsatisfied = new Semaphore();

		eventHandler = new EventHandler() {
			public void handleEvent(Event event) {
				Semaphore semaphore = null;
				
				if (event.getTopic().equals(ExposedTopic)) {
					semaphore = semExposed;
				} else if (event.getTopic().equals(UnsatisfiedTopic)) {
					semaphore = semUnsatisfied;
				} else {
					return;
				}
				
				semaphore.signal();
			}
		};
	}

	protected void tearDown() throws Exception {
		if (distributionReference != null) {
			getContext().ungetService(distributionReference);
			distributionReference = null;
		}

		unregisterAllServices();
	}

	/**
	 * Registers a DistributedService service with a required intent that is not listed as
	 * supported intent by the DSW implementation.
	 * 
	 * Make sure that the service does not get published by the DSW.
	 * 
	 * @throws Exception
	 */
	public void testRequiredIntentNotSatisfied() throws Exception {
		// make up an intent
		String myFancyIntent = "OSGi_TCK" + System.currentTimeMillis();
		
		// make sure DSW does not support this intent
		String supported = (String) distributionReference.getProperty(DistributionProvider.SUPPORTED_INTENTS);
		if (supported != null) {
			String[] supportedIntents = supported.split(" ");
			for (int i = 0; i < supportedIntents.length; i++) {
				assertFalse("Somebody cheated the implementation to satisfy every intent!", supportedIntents[i].equalsIgnoreCase(myFancyIntent));
			}
		}
		
		// register an event handler for the unsatisfied event emitted by the DSW
		Properties props = new Properties();
		props.put(EventConstants.EVENT_TOPIC, new String[]{EventConstants.EVENT_TOPIC, UnsatisfiedTopic});
		registerService(EventHandler.class.getName(), eventHandler, props);
		
		// register the test service
		Hashtable properties = new Hashtable();
		properties.put(DistributionConstants.REMOTE_INTERFACES, ALL_INTERFACES);
		properties.put(DistributionConstants.REMOTE_REQUIRES_INTENTS, myFancyIntent);
		properties.put("mykey", "myvalue");
		
		DistributedServiceImpl service = new DistributedServiceImpl();

		ServiceRegistration serviceRegistration = getContext().registerService(
				new String[]{DistributedService.class.getName(), DoNotPublishInterface.class.getName()}, service, properties);
		ServiceReference sref = serviceRegistration.getReference();
		assertNotNull(sref);
		
		try {
			// first register service and make sure it is registered
			assertTrue("Timeout for service unsatisfied event emitted by DSW!", semUnsatisfied.waitForSignal(exposeTimeout));
			
			Collection refs = provider.getExposedServices();
			assertNotNull(refs);
		
			boolean found = false;
			for (Iterator i = refs.iterator(); !found && i.hasNext();) {
				ServiceReference r = (ServiceReference) i.next();
				found = r.equals(sref);
			}
			assertFalse("service should not have been exposed as the required intent is not satisified", found);
			
			// check for unwanted ServicePublication registration
			ServiceReference spsref = getContext().getServiceReference(ServicePublication.class.getName());
			try {
				assertTrue("service should not have been published as the required intent is not satisified", spsref == null);
			} finally {
				if (spsref != null) {
					getContext().ungetService(spsref);
				};
			}
		} finally {
			serviceRegistration.unregister();
		}
	}
	
}
