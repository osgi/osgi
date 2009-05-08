/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 */
package org.osgi.test.cases.distribution;

import java.io.FileInputStream;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
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
public class DistributionTestCase extends DefaultTestBundleControl {
	private static final String ALL_INTERFACES = "*";
	
	Properties             tckProperties;
	DistributionProvider   provider;
	ServiceReference       distributionReference;
	EventHandler           eventHandler;
	Object                 proxy;
	
	boolean                failed = false;

	Semaphore              semExposed;
	Semaphore              semUnsatisfied;
	
	final String           ExposedTopic = DistributionProvider.class.getName().replace('.', '/') + "/service/exposed";
	final String           UnsatisfiedTopic = DistributionProvider.class.getName().replace('.', '/') + "/service/unsatisfied";

	long                   exposeTimeout;
	long                   proxyTimeout;
	long                   publishTimeout;
	
	void setFailed() {
		failed = true;
	}
	
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

		// register a service that should not be published
		// since this not condition is hard to test, I do this for every test case and throughout the
		// life of a test case, since the tests cannot block for ever to find out whether the service
		// was accidentally registered or not.
		DoNotPublishInterface service = new DoNotPublishInterface() {
			private static final long serialVersionUID = 1L;};
		registerService(DoNotPublishInterface.class.getName(), service, new Hashtable());
	}

	protected void tearDown() throws Exception {
		if (distributionReference != null) {
			getContext().ungetService(distributionReference);
			distributionReference = null;
		}
		
		String filter = "(" + DistributionConstants.REMOTE + "=*)";
		if (getContext().getAllServiceReferences(DoNotPublishInterface.class.getName(), filter) != null) {
			fail("Failed: Service was remoted that was not supposed to be remoted: " + DoNotPublishInterface.class.getName());
		}
		
		unregisterAllServices();
		
		long cleanupDelay = Long.parseLong(tckProperties.getProperty("distribution.tck.cleanup.delay"));
		if (cleanupDelay > 0) {
			Thread.sleep(cleanupDelay);
		}
	}
/*
	public void testNotPublish() throws Exception {
		DistributedServiceImpl service = new DistributedServiceImpl();
		
		ServiceRegistration sr = getContext().registerService(DistributedService.class.getName(), service, null);
		
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
		// register an event handler for the exposed event emitted by the DSW
		Properties props = new Properties();
		props.put(EventConstants.EVENT_TOPIC, new String[]{EventConstants.EVENT_TOPIC, ExposedTopic});
		registerService(EventHandler.class.getName(), eventHandler, props);
		
		// create and register a test service
		DistributedServiceImpl service = new DistributedServiceImpl();
		
		Hashtable properties = new Hashtable();
		properties.put(DistributionConstants.REMOTE_INTERFACES, ALL_INTERFACES);
		properties.put("mykey", "myvalue");
		
		// infrastructure to wait for ServicePublication registration, or not;
		// depends on whether DSW supports Discovery integration or not
		final Semaphore sem = new Semaphore();
		
		ServiceListener listener = new ServiceListener() {
			public void serviceChanged(ServiceEvent event) {
				sem.signal();
			}
		};
		getContext().addServiceListener(listener, "(objectClass="+ServicePublication.class.getName()+")");
		
		// register the services
		ServiceRegistration serviceRegistration = getContext().registerService(
				new String[]{DistributedService.class.getName(), DoNotPublishInterface.class.getName()}, service, properties);
		ServiceReference serviceReference = serviceRegistration.getReference();
		assertNotNull(serviceReference);
		
		try {
			// first register service and make sure it is registered
			assertTrue("Timeout for service to be exposed by DSW!", semExposed.waitForSignal(exposeTimeout));
			
			Collection refs = provider.getExposedServices();
			assertNotNull("no exposed services", refs);
		
			boolean found = false;
			for (Iterator i = refs.iterator(); !found && i.hasNext();) {
				ServiceReference r = (ServiceReference) i.next();
				found = r.equals(serviceReference);
			}
			assertTrue("service not exposed by DSW", found);
			
			System.out.println("service " + serviceReference + " is exposed by DSW.");
			
			sem.waitForSignal(4000); // wait for max 4 sec for the ServicePublication registration
			
			ServiceReference spsref = getContext().getServiceReference(ServicePublication.class.getName());
			if (spsref != null) {
				System.out.println("DSW implementation supports Discovery integration!");
				
				try {
					ServicePublication sp = (ServicePublication) getContext().getService(spsref);
					assertNotNull(sp);
					assertEquals(serviceReference, sp.getReference());
					
					Collection interf = (Collection) spsref.getProperty(ServicePublication.SERVICE_INTERFACE_NAME);
					
					System.out.println("ServicePublication property " + ServicePublication.SERVICE_INTERFACE_NAME + ":");
					for (Iterator it = interf.iterator(); it.hasNext();) {
						System.out.println(" " + it.next());
					}
					
					assertNotNull("no interfaces listed", interf);
					assertTrue("Failed to list interface " + DistributedService.class.getName(), interf.contains(DistributedService.class.getName()));
					assertTrue("Failed to list interface " + DoNotPublishInterface.class.getName(), interf.contains(DoNotPublishInterface.class.getName()));
					
					Map map = (Map) spsref.getProperty(ServicePublication.SERVICE_PROPERTIES);
					assertNotNull("service properties were not copied", map);
					assertEquals("service property was not set", properties.get("mykey"), map.get("mykey"));
				} finally {
					getContext().ungetService(spsref);
				}
			}
		} finally {
			getContext().removeServiceListener(listener);
			serviceRegistration.unregister();
		}
	}

	/**
	 * Take the list of supported intents from the DistributionProvider service and register the
	 * DistributedService with the first intent from the list. Ensure that the service gets remoted.
	 * 
	 * @throws Exception
	 */
	public void testMatchRequiredIntent() throws Exception {
		// get the supported intents of the DSW
		String supported = (String) distributionReference.getProperty(DistributionProvider.SUPPORTED_INTENTS);
		String[] intentstrings = supported.split(" ");
		if (intentstrings.length == 0) {
			pass("provider does not support any intents");
		}

		// register an event handler for the registration events of the DSW
		Properties props = new Properties();
		props.put(EventConstants.EVENT_TOPIC, new String[]{EventConstants.EVENT_TOPIC, ExposedTopic});
		registerService(EventHandler.class.getName(), eventHandler, props);
		
		// create and register a test service
		DistributedServiceImpl service = new DistributedServiceImpl();
		
		Hashtable properties = new Hashtable();
		properties.put(DistributionConstants.REMOTE_INTERFACES, ALL_INTERFACES);
		properties.put(DistributionConstants.REMOTE_REQUIRES_INTENTS, intentstrings[0]);
		properties.put("mykey", "myvalue");
		
		ServiceRegistration serviceRegistration = getContext().registerService(
				new String[]{DistributedService.class.getName(), DoNotPublishInterface.class.getName()}, service, properties);
		ServiceReference sref = serviceRegistration.getReference();
		assertNotNull(sref);
		
		try {
			// first register service and make sure it is registered
			assertTrue("Timeout for service to be exposed by DSW!", semExposed.waitForSignal(exposeTimeout));
			
			// look for the DistributedService in the list of exposed services from the DSW
			Collection refs = provider.getExposedServices();
			assertNotNull(refs);
		
			boolean found = false;
			for (Iterator i = refs.iterator(); !found && i.hasNext();) {
				ServiceReference r = (ServiceReference) i.next();
				found = r.equals(sref);
			}
			assertTrue("timeout for service registration notification", found);

			// check for the osgi.deployment.intents and ensure that the required intent is
			// included
			Map pubprops = provider.getExposedProperties(sref);
			assertNotNull(pubprops);
			
			String intent = (String) pubprops.get(DistributionConstants.DEPLOYMENT_INTENTS);
			assertNotNull("no deployment intents found", intent);
			assertTrue(intentstrings[0] + " intent was not listed", intent.indexOf(intentstrings[0]) != -1);
		} finally {
			serviceRegistration.unregister();
		}
	}
	
	/**
	 * Register a service with no intents. Ensure that the proxy is created with the appropriate
	 * properties, e.g. osgi.remote and supported.intents
	 * @throws Exception
	 */
	public void testFindServiceNoIntents() throws Exception {
		// register an event handler waiting for registration events from the DSW
		Properties props = new Properties();
		props.put(EventConstants.EVENT_TOPIC, new String[]{EventConstants.EVENT_TOPIC, ExposedTopic});
		registerService(EventHandler.class.getName(), eventHandler, props);
		
		// infrastructure to wait for DistributedService proxy registration;
		final Semaphore sem = new Semaphore();
		
		ServiceListener listener = new ServiceListener() {
			public void serviceChanged(ServiceEvent event) {
				// look for registration events for the DistributedService where the osgi.remote
				// property is set
				if (event.getType() == ServiceEvent.REGISTERED) {
					log("received REGISTERED event" + event);
					if (event.getServiceReference().getProperty(DistributionConstants.REMOTE) != null) {
						log("service has property " + DistributionConstants.REMOTE);
						// store the reference for the test case to evaluate
						proxy = event.getServiceReference();

						sem.signal();
					}
				}
			}
		};
		// register for events for the DistributedService interface only
		getContext().addServiceListener(listener);
		
		// my test service implementation
		DistributedServiceImpl service = new DistributedServiceImpl();
		
		// properties for the service to register
		// only the DistributedService interface is supposed to be published
		Hashtable properties = new Hashtable();
		properties.put(DistributionConstants.REMOTE_INTERFACES, DistributedService.class.getName());
		properties.put("mykey", "myvalue");
				
		ServiceRegistration distributedServiceRegistration = getContext().registerService(DistributedService.class.getName(), service, properties);
		assertNotNull(distributedServiceRegistration);
		ServiceReference distributedServiceReference = distributedServiceRegistration.getReference();
		assertNotNull(distributedServiceReference);
		
		try {
			// first register service and make sure it is registered
			assertTrue("Timeout for service to be exposed by DSW!", semExposed.waitForSignal(exposeTimeout));
			
			// check to see if the DSW has exposed the service
			Collection refs = provider.getExposedServices();
			assertNotNull(refs);
		
			// look for the DistributedService in the list of exposed services from the DSW
			boolean found = false;
			for (Iterator i = refs.iterator(); !found && i.hasNext();) {
				ServiceReference r = (ServiceReference) i.next();
				found = r.equals(distributedServiceReference);
			}
			assertTrue("service " + DistributedService.class.getName() + " was not exposed by DSW", found);
			
			// check all registered DistributedService services for their properties
			String filter = "(" + DistributionConstants.REMOTE_INTERFACES + "=" + DistributedService.class.getName() + ")";
			ServiceReference[] srefs = getContext().getAllServiceReferences(DistributedService.class.getName(), filter);
			assertNotNull(srefs);
			
			// if this fails, it means that the registered proxy also has the osgi.remote.interfaces property set
			// and the DSW would run into a endless publishing loop
			assertEquals("There must be only one registered service satisfying the filter " + filter, 1, srefs.length);

			// wait for the proxy to become available
			assertTrue("Timeout for registration of proxy service instance for " + 
					DistributedService.class.getName(), sem.waitForSignal(proxyTimeout));
			
			// evaluate the properties and attributes of the proxy
			assertNotNull(proxy);
			assertTrue(proxy instanceof ServiceReference);
			ServiceReference proxyreference = (ServiceReference) proxy;
			
			// check whether the proxy object actually implements all interfaces
			Object serviceObj = getContext().getService(proxyreference);
			assertNotNull(serviceObj);
			assertTrue(serviceObj instanceof DistributedService);
			assertFalse("The proxy MUST NOT implement the interface that was not in the osgi.remote.interfaces set!",
					serviceObj instanceof DoNotPublishInterface);
			
			// invoke the proxy to ensure it is working
			assertEquals("ollah", ((DistributedService)serviceObj).reverse("hallo"));
			
			// check for the intents on the proxy object
			String dpsupportedintents = (String)distributionReference.getProperty(DistributionProvider.SUPPORTED_INTENTS);
			String supportintents = (String) proxyreference.getProperty(DistributionProvider.SUPPORTED_INTENTS);
			assertEquals(dpsupportedintents, supportintents);
		} finally {
			getContext().removeServiceListener(listener);
			distributedServiceRegistration.unregister();
		}
	}
	
	/**
	 * Register a service with multiple interfaces. Ensure that the proxy is created with the appropriate
	 * properties, e.g. osgi.remote and supported.intents and all interfaces are exposed
	 * @throws Exception
	 */
	public void testExposeAllInterfaces() throws Exception {
		// register an event handler waiting for registration events from the DSW
		Properties props = new Properties();
		props.put(EventConstants.EVENT_TOPIC, new String[]{EventConstants.EVENT_TOPIC, ExposedTopic});
		registerService(EventHandler.class.getName(), eventHandler, props);
				
		// infrastructure to wait for DistributedService proxy registration;
		final Semaphore sem = new Semaphore();
		
		ServiceListener listener = new ServiceListener() {
			public void serviceChanged(ServiceEvent event) {
				// look for registration events for any service where the osgi.remote
				// property is set
				if (event.getType() == ServiceEvent.REGISTERED) {
					log("received REGISTERED event" + event);
					if (event.getServiceReference().getProperty(DistributionConstants.REMOTE) != null) {
						log("service has property " + DistributionConstants.REMOTE);
						// store the reference for the test case to evaluate
						proxy = event.getServiceReference();

						sem.signal();
					}
				}
			}
		};
		// register for service registration events
		getContext().addServiceListener(listener, null);
		
		// my test service implementation
		DistributedServiceImpl service = new DistributedServiceImpl();
		
		// properties for the service to register
		// only the DistributedService interface is supposed to be published
		Hashtable properties = new Hashtable();
		properties.put(DistributionConstants.REMOTE_INTERFACES, ALL_INTERFACES);
		properties.put("mykey", "myvalue");
		
		ServiceRegistration distributedServiceRegistration = getContext().registerService(
				new String[]{DistributedService.class.getName(), DoNotPublishInterface.class.getName()}, service, properties);
		assertNotNull(distributedServiceRegistration);
		ServiceReference distributedServiceReference = distributedServiceRegistration.getReference();
		assertNotNull(distributedServiceReference);
		
		try {
			// first register service and make sure it is registered
			assertTrue("Timeout for service to be exposed by DSW!", semExposed.waitForSignal(exposeTimeout));
			
			// check to see if the DSW has exposed the service
			Collection refs = provider.getExposedServices();
			assertNotNull(refs);
		
			// look for the DistributedService in the list of exposed services from the DSW
			boolean found = false;
			for (Iterator i = refs.iterator(); !found && i.hasNext();) {
				ServiceReference r = (ServiceReference) i.next();
				found = r.equals(distributedServiceReference);
			}
			assertTrue("service " + DistributedService.class.getName() + " was not exposed by DSW", found);
			
			// wait for the proxy to become available
			assertTrue("Timeout for registration of proxy service instance for " + 
					DistributedService.class.getName(), sem.waitForSignal(proxyTimeout));
			
			// evaluate the properties and attributes of the proxy
			assertNotNull(proxy);
			assertTrue(proxy instanceof ServiceReference);
			ServiceReference proxyreference = (ServiceReference) proxy;
			
			// check whether the proxy object actually implements all interfaces
			Object serviceObj = getContext().getService(proxyreference);
			assertNotNull(serviceObj);
			assertTrue("The proxy MUST implement all interfaces!", serviceObj instanceof DistributedService);
			assertTrue("The proxy MUST implement all interfaces!", serviceObj instanceof DoNotPublishInterface);
			
			// invoke the proxy to ensure it is working
			assertEquals("ollah", ((DistributedService)serviceObj).reverse("hallo"));
		} finally {
			getContext().removeServiceListener(listener);
			distributedServiceRegistration.unregister();
		}
	}
}
