/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 */
package org.osgi.test.cases.distribution;

import java.io.FileInputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.Filter;
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
import org.osgi.test.cases.distribution.common.A;
import org.osgi.test.cases.distribution.common.B;
import org.osgi.test.cases.distribution.common.TestServiceImpl;
import org.osgi.test.cases.distribution.internal.DistributedService;
import org.osgi.test.cases.distribution.internal.DistributedServiceImpl;
import org.osgi.test.cases.distribution.internal.DoNotPublishInterface;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
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
public class ServiceProxyTestCase extends DefaultTestBundleControl {
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
	}

	protected void tearDown() throws Exception {
		if (distributionReference != null) {
			getContext().ungetService(distributionReference);
			distributionReference = null;
		}
		
		unregisterAllServices();
	}
	
	/**
	 * Register a service with multiple interfaces. Ensure that the proxy is created with the appropriate
	 * properties, e.g. osgi.remote and supported.intents and all interfaces are exposed
	 * @throws Exception
	 */
	public void testExposeAllInterfaces() throws Exception {
		
		// my test service implementation
		TestServiceImpl service = new TestServiceImpl();
		
		// properties for the service to register
		// only the DistributedService interface is supposed to be published
		Hashtable properties = new Hashtable();
		properties.put(DistributionConstants.REMOTE_INTERFACES, ALL_INTERFACES);
		properties.put("mykey", "myvalue");
		
		ServiceRegistration serviceRegistration = getContext().registerService(
				new String[]{A.class.getName(), B.class.getName()}, service, properties);
		assertNotNull(serviceRegistration);
		
		// infrastructure to wait for DistributedService proxy registration;
		final Semaphore semA = new Semaphore();
		final Semaphore semB = new Semaphore();
		final Map referenceMap = new HashMap();
		
		ServiceTracker trackerA = new ServiceTracker(getContext(), A.class.getName(), new ServiceTrackerCustomizer(){
		
			public void removedService(ServiceReference reference, Object service) {
			}
		
			public void modifiedService(ServiceReference reference, Object service) {
			}
		
			public Object addingService(ServiceReference reference) {
				if (reference.getProperty(DistributionConstants.REMOTE) != null) {
					referenceMap.put("A",reference);
					semA.signal();
					
					return getContext().getService(reference);
				}
				return null;
			}
		});
		trackerA.open();
		
		ServiceTracker trackerB = new ServiceTracker(getContext(), B.class.getName(), new ServiceTrackerCustomizer(){
		
			public void removedService(ServiceReference reference, Object service) {
			}
		
			public void modifiedService(ServiceReference reference, Object service) {
			}
		
			public Object addingService(ServiceReference reference) {
				if (reference.getProperty(DistributionConstants.REMOTE) != null) {
					referenceMap.put("B",reference);
					semA.signal();
					
					return getContext().getService(reference);
				}
				return null;
			}
		});
		trackerB.open();
		
		try {
			// wait for the proxies to become available
			
			// A
			
			assertTrue("Timeout for registration of proxy service instance for " + 
					A.class.getName(), semA.waitForSignal(proxyTimeout));
			ServiceReference sr = (ServiceReference) referenceMap.get("A");
			assertNotNull(sr);
			
			Collection col = provider.getRemoteServices();
			assertNotNull(col);
			assertTrue(col.contains(sr));
			
			assertNotNull("Service proxy is required to have " + DistributionConstants.REMOTE +
					" property set",sr.getProperty(DistributionConstants.REMOTE));
			assertEquals("Service proxy is required to have all servic properties set", "myvalue", sr.getProperty("mykey"));

			// B
			
			assertTrue("Timeout for registration of proxy service instance for " + 
					B.class.getName(), semB.waitForSignal(proxyTimeout));
			sr = (ServiceReference) referenceMap.get("B");
			assertNotNull(sr);
			
			col = provider.getRemoteServices();
			assertNotNull(col);
			assertTrue(col.contains(sr));
			
			assertNotNull("Service proxy is required to have " + DistributionConstants.REMOTE +
					" property set",sr.getProperty(DistributionConstants.REMOTE));
			assertEquals("Service proxy is required to have all servic properties set", "myvalue", sr.getProperty("mykey"));
		} finally {
			trackerA.close();
			trackerB.close();
			serviceRegistration.unregister();
		}
	}
}
