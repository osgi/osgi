/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 */
package org.osgi.test.cases.distribution;

import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import junit.framework.TestCase;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.hooks.service.ListenerHook;
import org.osgi.service.discovery.Discovery;
import org.osgi.service.discovery.ServiceEndpointDescription;
import org.osgi.service.discovery.ServicePublication;
import org.osgi.service.distribution.DistributionProvider;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.test.cases.distribution.internal.DistributedService;
import org.osgi.test.cases.distribution.internal.DistributedServiceImpl;
import org.osgi.test.cases.distribution.internal.DoNotPublishInterface;
import org.osgi.test.support.compatibility.Semaphore;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * RFC 119 Distributed OSGi TCK
 * 
 * These test cases focus on the DSW only. Discovery is covered by a separate TCK.
 * 
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 * @since 1.0.0
 */
public class DistributionTestCase extends TestCase {
	/**
	 * 
	 */
	private static final String OSGI_REMOTE = "osgi.remote";

	/**
	 * 
	 */
	private static final String OSGI_INTENTS = "service.intents";

	/**
	 * 
	 */
	private static final String OSGI_REMOTE_REQUIRES_INTENTS = "osgi.remote.requires.intents";

	/**
	 * 
	 */
	private static final String OSGI_REMOTE_INTERFACES = "osgi.remote.interfaces";
	
	DistributionProvider provider;
	EventAdmin eventAdmin;
	ServiceReference distributionReference;
	ServiceReference eventAdminReference;
	BundleContext context;
	EventHandler eventHandler;
	DistributedService distributedService;

	final Object lock = new Object();
	String exposedTopic = DistributionProvider.class.getName().replace('.', '/') + "/service/exposed";
	String unsatisfiedTopic = DistributionProvider.class.getName().replace('.', '/') + "/service/unsatisfied";

	
	public void setBundleContext(BundleContext context) {
		this.context = context;
	}

	protected void setUp() throws Exception {
		distributionReference = context.getServiceReference(DistributionProvider.class.getName());
		assertNotNull(distributionReference);
		provider = (DistributionProvider) context.getService(distributionReference);
		assertNotNull(provider);
		
		eventAdminReference = context.getServiceReference(EventAdmin.class.getName());
		assertNotNull(eventAdminReference);
		eventAdmin = (EventAdmin) context.getService(eventAdminReference);
		assertNotNull(eventAdmin);
		
		eventHandler = new EventHandler() {
			public void handleEvent(Event event) {
				synchronized (lock) {
					lock.notifyAll();
				}
			}
		};

	}

	protected void tearDown() throws Exception {
		if (distributionReference != null) {
			context.ungetService(distributionReference);
			distributionReference = null;
		}
		if (eventAdminReference != null) {
			context.ungetService(eventAdminReference);
			eventAdminReference = null;
		}
	}
/*
	public void testNotPublish() throws Exception {
		DistributedServiceImpl service = new DistributedServiceImpl();
		
		ServiceRegistration sr = context.registerService(DistributedService.class.getName(), service, null);
		
		Thread.sleep(6000);
		
		ServiceReference[] refs = provider.getExposedServices();
		assertNotNull(refs);
		assertTrue(refs.length == 0);
		
		sr.unregister();
	}
*/
	/**
	 * Register a service with the service property osgi.remote.interfaces set to '*'.
	 * 
	 * Make sure that the service gets exposed by the DSW after a reasonable amount of time.
	 * 
	 * In case DSW supports Discovery check for the registration of a ServicePublication service.
	 * 
	 * @throws Exception
	 */
	public void testPublishExposed() throws Exception {
		DistributedServiceImpl service = new DistributedServiceImpl();
		
		Hashtable properties = new Hashtable();
		properties.put(OSGI_REMOTE_INTERFACES, "*");
		properties.put("mykey", "myvalue");
		
		Properties props = new Properties();
		props.put(EventConstants.EVENT_TOPIC, new String[]{EventConstants.EVENT_TOPIC, exposedTopic});
		ServiceRegistration ehsr = context.registerService(EventHandler.class.getName(), eventHandler, props);
		
		ServiceRegistration sr = context.registerService(DistributedService.class.getName(), service, properties);
		
		try {
			synchronized (lock) {
				try {
					lock.wait(/*5 **/ 60000); // wait 5 min
				} catch (InterruptedException ie) {
					fail("timeout for service registration notification");
				}
			}
			
			ServiceReference sref = context.getServiceReference(DistributedService.class.getName());
			assertNotNull(sref);
			
			Collection refs = provider.getExposedServices();
			assertNotNull(refs);
		
			boolean found = false;
			for (Iterator i = refs.iterator(); !found && i.hasNext();) {
				ServiceReference r = (ServiceReference) i.next();
				found = r.equals(sref);
			}
			assertTrue("timeout for service registration notification", found);
			
			assertEquals("myvalue", sref.getProperty("mykey"));
			
			ServiceReference spsref = context.getServiceReference(ServicePublication.class.getName());
			if (sref != null) {
				System.out.println("DSW implementation supports Discovery integration!");
				
				ServicePublication sp = (ServicePublication) context.getService(spsref);
				try {
					assertEquals(sref, sp.getReference());
				} finally {
					context.ungetService(spsref);
				}
			}
		} finally {
			ehsr.unregister();
			sr.unregister();
		}
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
		DistributedServiceImpl service = new DistributedServiceImpl();

		String myFancyIntent = "OSGi_TCK" + System.currentTimeMillis();
		
		// make sure DSW does not support this intent
		String supported = (String) distributionReference.getProperty(DistributionProvider.PROP_KEY_SUPPORTED_INTENTS);
		if (supported != null) {
			String[] supportedIntents = supported.split(" ");
			for (int i = 0; i < supportedIntents.length; i++) {
				assertFalse("Somebody cheated the implementation to satisfy every intent!", supportedIntents[i].equalsIgnoreCase(myFancyIntent));
			}
		}
		
		Hashtable properties = new Hashtable();
		properties.put(OSGI_REMOTE_INTERFACES, "*");
		properties.put(OSGI_REMOTE_REQUIRES_INTENTS, myFancyIntent);
		properties.put("mykey", "myvalue");
		
		Properties props = new Properties();
		props.put(EventConstants.EVENT_TOPIC, new String[]{EventConstants.EVENT_TOPIC, unsatisfiedTopic});
		ServiceRegistration ehsr = context.registerService(EventHandler.class.getName(), eventHandler, props);
		
		ServiceRegistration sr = context.registerService(DistributedService.class.getName(), service, properties);
		
		try {
			synchronized (lock) {
				try {
					lock.wait(/*5 **/ 60000); // wait 5 min
				} catch (InterruptedException ie) {
					fail("timeout for service exposure failed notification");
				}
			}
			
			ServiceReference sref = context.getServiceReference(DistributedService.class.getName());
			assertNotNull(sref);
			
			Collection refs = provider.getExposedServices();
			assertNotNull(refs);
		
			boolean found = false;
			for (Iterator i = refs.iterator(); !found && i.hasNext();) {
				ServiceReference r = (ServiceReference) i.next();
				found = r.equals(sref);
			}
			
			assertFalse("service should not have been exposed as the required intents is not satisified", found);
		} finally {
			ehsr.unregister();
			sr.unregister();
		}
	}
	
	/**
	 * Take the list of supported intents from the DistributionProvider service and register the
	 * DistributedService with the first intent from the list. Ensure that the service gets remoted.
	 * 
	 * @throws Exception
	 */
	public void testMatchRequiredIntent() throws Exception {
		String supported = (String) distributionReference.getProperty(DistributionProvider.PROP_KEY_SUPPORTED_INTENTS);
		String[] intentstrings = supported.split(" ");
		if (intentstrings.length == 0) {
			System.out.println("provider does not support any intents");
			return;
		}
		
		DistributedServiceImpl service = new DistributedServiceImpl();
		
		Hashtable properties = new Hashtable();
		properties.put(OSGI_REMOTE_INTERFACES, "*");
		properties.put(OSGI_REMOTE_REQUIRES_INTENTS, intentstrings[0]);
		properties.put("mykey", "myvalue");
		
		Properties props = new Properties();
		props.put(EventConstants.EVENT_TOPIC, new String[]{EventConstants.EVENT_TOPIC, exposedTopic});
		ServiceRegistration ehsr = context.registerService(EventHandler.class.getName(), eventHandler, props);
		
		ServiceRegistration sr = context.registerService(DistributedService.class.getName(), service, properties);
		
		try {
			synchronized (lock) {
				try {
					lock.wait(/*5 **/ 60000); // wait 5 min
				} catch (InterruptedException ie) {
					fail("timeout for service registration notification");
				}
			}
			
			Collection refs = provider.getExposedServices();
			assertNotNull(refs);
		
			ServiceReference sref = context.getServiceReference(DistributedService.class.getName());
			boolean found = false;
			for (Iterator i = refs.iterator(); !found && i.hasNext();) {
				ServiceReference r = (ServiceReference) i.next();
				found = sref.equals(r);
			}
			assertTrue("timeout for service registration notification", found);

			Map pubprops = provider.getExposedProperties(sref);
			String intent = (String) pubprops.get(OSGI_INTENTS);
			assertNotNull(intent);
			assertTrue(intent.indexOf(intentstrings[0]) != -1);
		} finally {
			ehsr.unregister();
			sr.unregister();
		}
	}
	
	public void testFindServiceNoIntents() throws Exception {
		DistributedServiceImpl service = new DistributedServiceImpl();
		
		Hashtable properties = new Hashtable();
		properties.put(OSGI_REMOTE_INTERFACES, "*");
		properties.put("mykey", "myvalue");
		
		Properties props = new Properties();
		props.put(EventConstants.EVENT_TOPIC, new String[]{EventConstants.EVENT_TOPIC, exposedTopic});
		ServiceRegistration ehsr = context.registerService(EventHandler.class.getName(), eventHandler, props);
		assertNotNull(ehsr);
		
		ServiceRegistration sr = context.registerService(DistributedService.class.getName(), service, properties);
		assertNotNull(sr);
				
		ServiceTracker serviceTracker = null;
		
		final Set serviceReferences = new HashSet();
		
		try {
			// first register service and make sure it is registered
			synchronized (lock) {
				try {
					lock.wait(/*5 **/ 60000); // wait 5 min
				} catch (InterruptedException ie) {
					fail("timeout for service registration notification");
				}
			}
			
			Collection refs = provider.getExposedServices();
			assertNotNull(refs);
		
			ServiceReference sref = context.getServiceReference(DistributedService.class.getName());
			boolean found = false;
			for (Iterator i = refs.iterator(); !found && i.hasNext();) {
				ServiceReference r = (ServiceReference) i.next();
				found = sref.equals(r);
			}
			assertTrue("timeout for service registration notification", found);
			
			// now wait for the service proxy to be registered
			final Semaphore semaphore = new Semaphore();
			
			// then look up service from client side
			serviceTracker = new ServiceTracker(context, DistributedService.class.getName(), new ServiceTrackerCustomizer() {

				public Object addingService(ServiceReference reference) {
					Object remote = reference.getProperty(OSGI_REMOTE);
					if (remote != null) {
						distributedService = (DistributedService) context.getService(reference);
						serviceReferences.add(reference);
						semaphore.signal();
					}
					return distributedService;
				}

				public void modifiedService(ServiceReference reference,
						Object service) {
					distributedService = (DistributedService) service;
				}

				public void removedService(ServiceReference reference,
						Object service) {
					distributedService = null;
				}
				
			});
			serviceTracker.open();
			
			semaphore.waitForSignal(10000);
			
			assertNotNull(distributedService);
			assertTrue(distributedService instanceof DistributedService);
			assertTrue(distributedService instanceof DoNotPublishInterface);
			
			assertFalse(serviceReferences.isEmpty());
			
			assertEquals("ollah", distributedService.reverse("hallo"));
			
			ServiceReference reference = (ServiceReference) serviceReferences.iterator().next();
			
			String dpsupportedintents = (String)distributionReference.getProperty(DistributionProvider.PROP_KEY_SUPPORTED_INTENTS);
			String supportintents = (String) reference.getProperty(DistributionProvider.PROP_KEY_SUPPORTED_INTENTS);
			assertEquals(dpsupportedintents, supportintents);
		} finally {
			ehsr.unregister();
			sr.unregister();
			
			if (serviceTracker != null) {
				serviceTracker.close();
			}
			if (!serviceReferences.isEmpty()) {
				context.ungetService((ServiceReference) serviceReferences.iterator().next());
				
				serviceReferences.clear();
			}
		}
	}
	
}
