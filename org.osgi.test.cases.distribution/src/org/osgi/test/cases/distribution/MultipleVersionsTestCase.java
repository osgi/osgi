/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 */
package org.osgi.test.cases.distribution;

import java.io.FileInputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.discovery.ServicePublication;
import org.osgi.service.distribution.DistributionConstants;
import org.osgi.service.distribution.DistributionProvider;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.test.cases.distribution.common.A;
import org.osgi.test.cases.distribution.common.B;
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
public class MultipleVersionsTestCase extends DefaultTestBundleControl {
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
		
		// register an event handler waiting for registration events from the DSW
		Properties props = new Properties();
		props.put(EventConstants.EVENT_TOPIC, new String[]{EventConstants.EVENT_TOPIC, ExposedTopic});
		registerService(EventHandler.class.getName(), eventHandler, props);
	}

	protected void tearDown() throws Exception {
		if (distributionReference != null) {
			getContext().ungetService(distributionReference);
			distributionReference = null;
		}
		
		unregisterAllServices();
	}
	
	/**
	 * Register multiple service instances with different versions. Ensure that the exposed service
	 * has the correct version properties.
	 * @throws Exception
	 */
	public void testMultipleVersions() throws Exception {
		final Set serviceRefs = new HashSet();
		final Semaphore sem = new Semaphore();
		
		ServiceListener listener = new ServiceListener(){
		
			public void serviceChanged(ServiceEvent event) {
				if (event.getType() == ServiceEvent.REGISTERED) {
					System.out.println(" received REGISTERED event for remote service");
					
					serviceRefs.add(event.getServiceReference());
					sem.signal();
				}
			}
		};
		
		String filter = "(" + DistributionConstants.REMOTE + "=*)";
		getContext().addServiceListener(listener, filter);
		
		// much more complicated and requires multiple different bundles to be deployed
		Bundle tb1 = getContext().installBundle(getWebServer() + "tb1.jar");
		assertNotNull(tb1);
		
		tb1.start();
		
		// first register service and make sure it is registered
		assertTrue("Timeout for service to be exposed by DSW!", semExposed.waitForSignal(exposeTimeout));

		Bundle tb2 = getContext().installBundle(getWebServer() + "tb2.jar");
		assertNotNull(tb2);
		
		tb2.start();
		
		// first register service and make sure it is registered
		assertTrue("Timeout for service to be exposed by DSW!", semExposed.waitForSignal(exposeTimeout));
		
		// check to see if the DSW has exposed the service
		Collection refs = provider.getExposedServices();
		assertNotNull(refs);
		
		boolean found1 = false;
		boolean found2 = false;
		ServiceReference sref1 = null;
		ServiceReference sref2 = null;
		
		for (Iterator it = refs.iterator(); it.hasNext();) {
			ServiceReference sref = (ServiceReference) it.next();
			
			try {
				String impl = (String) sref.getProperty("implementation");
				if ("1".equals(impl) && !found1) {
					found1 = true;
					sref1 = sref;
				} else if ("2".equals(impl) && !found2) {
					found2 = true;
					sref2 = sref;
				} else {
					fail("Either found dublicate or invalid service");
				}
			} finally {
				getContext().ungetService(sref);
			}
		}
		
		found1 = false;
		found2 = false;
		
		// check for integration with Discovery
		ServiceReference[] sprefs = getContext().getAllServiceReferences(ServicePublication.class.getName(), null);
		if (sprefs != null && sprefs.length > 0) {
			System.out.println("DSW supports Discovery integration since it has registered ServicePublication services");
			
			// validate the ServicePublication
			for (int i = 0; i < sprefs.length; i++) {
				ServiceReference sr = sprefs[i];
				Collection ifnames = (Collection) sr.getProperty(ServicePublication.SERVICE_INTERFACE_NAME);
				assertTrue("ServicePublication did not list interface " + A.class.getName(), ifnames.contains(A.class.getName()));
				assertTrue("ServicePublication did not list interface " + B.class.getName(), ifnames.contains(B.class.getName()));
				
				Map properties = (Map) sr.getProperty(ServicePublication.SERVICE_PROPERTIES);
				assertNotNull(properties);
				assertTrue("ServicePublication does not contain properties from original service registration", properties.containsKey("implementation"));
				
				ServicePublication sp = (ServicePublication) getContext().getService(sr);
				if (sref1.equals(sp.getReference()) && !found1) {
					found1 = true;
					
					assertEquals("1", properties.get("implementation"));
					
					Collection col = (Collection) sr.getProperty(ServicePublication.SERVICE_INTERFACE_VERSION);
					assertNotNull(col);
					assertTrue("ServicePublication did not list interface " + A.class.getName() + " in version 1.0.0",
							col.contains(A.class.getName() + ServicePublication.SEPARATOR + "1.0.0"));
					assertTrue("ServicePublication did not list interface " + B.class.getName() + " in version 1.0.0",
							col.contains(B.class.getName() + ServicePublication.SEPARATOR + "1.0.0"));
				} else if (sref2.equals(sp.getReference()) && !found2) {
					found2 = true;
					
					assertEquals("2", properties.get("implementation"));
					
					Collection col = (Collection) sr.getProperty(ServicePublication.SERVICE_INTERFACE_VERSION);
					assertNotNull(col);
					assertTrue("ServicePublication did not list interface " + A.class.getName() + " in version 2.0.0",
							col.contains(A.class.getName() + ServicePublication.SEPARATOR + "2.0.0"));
					assertTrue("ServicePublication did not list interface " + B.class.getName() + " in version 2.0.0",
							col.contains(B.class.getName() + ServicePublication.SEPARATOR + "2.0.0"));
				} else {
					fail("Either found dublicate or invalid service " + sp.getReference());
				}
			}
		}
		
		// now pretend to be a client and lookup service in local service registry. expect to find the proxy
		try {
			assertTrue("Timeout waiting for remoted service to become available", sem.waitForSignal(proxyTimeout));
			
			for (Iterator i = serviceRefs.iterator(); i.hasNext();) {
				ServiceReference sr = (ServiceReference) i.next();
				
				Object s = getContext().getService(sr);
				assertNotNull(s);
			}
		} finally {
			getContext().removeServiceListener(listener);
		}
	}
	
}
