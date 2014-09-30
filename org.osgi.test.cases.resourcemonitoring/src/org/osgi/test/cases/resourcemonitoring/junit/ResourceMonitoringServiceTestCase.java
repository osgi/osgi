
package org.osgi.test.cases.resourcemonitoring.junit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.resourcemonitoring.ResourceContext;
import org.osgi.service.resourcemonitoring.ResourceContextEvent;
import org.osgi.service.resourcemonitoring.ResourceMonitor;
import org.osgi.service.resourcemonitoring.ResourceMonitorException;
import org.osgi.service.resourcemonitoring.ResourceMonitorFactory;
import org.osgi.service.resourcemonitoring.ResourceMonitoringService;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * see Conformance Tests description.odt file.
 * 
 * @author $Id$
 */
public class ResourceMonitoringServiceTestCase extends DefaultTestBundleControl {

	/**
	 * bundle context
	 */
	private BundleContext				bundleContext;

	/**
	 * ResourceMonitoringService.
	 */
	private ResourceMonitoringService	resourceMonitoringService;

	/**
	 * resource context listener
	 */
	private TestResourceContextListener	resourceContextListener;

	/**
	 * Set bundle context.
	 */
	public void setBundleContext(BundleContext context) {
		bundleContext = context;

		ServiceReference serviceReference = bundleContext
				.getServiceReference(ResourceMonitoringService.class);
		resourceMonitoringService = (ResourceMonitoringService) bundleContext.getService(serviceReference);

	}

	/**
	 * This method is called after each single test. Remove all existing
	 * ResourceContexts.
	 */
	protected void tearDown() throws Exception {
		resourceContextListener.stop();

		// delete all existing resource contexts.
		ResourceContext[] resourceContexts = resourceMonitoringService.listContext();
		for (int i = 0; i < resourceContexts.length; i++) {
			resourceContexts[i].removeContext(null);
		}
	}

	protected void setUp() throws Exception {
		resourceContextListener = new TestResourceContextListener();
		resourceContextListener.start(bundleContext);

		// delete all existing resource contexts.
		ResourceContext[] resourceContexts = resourceMonitoringService.listContext();
		for (int i = 0; i < resourceContexts.length; i++) {
			resourceContexts[i].removeContext(null);
		}
	}

	/**
	 * Test create a ResourceContext and check the list
	 */
	public void testCreateResourceContext() {
		final String name = "context1";

		// retrieve the list of existing resource contexT.
		ResourceContext[] resourceContexts = resourceMonitoringService.listContext();
		assertNotNull(resourceContexts);
		log("--------------------nb of rc :" + resourceContexts.length);
		assertTrue(resourceContexts.length == 0);

		// create resource context
		ResourceContext resourceContext = resourceMonitoringService.createContext(name,
				null);

		// check the resourceContext object is not null
		assertNotNull(resourceContext);

		// check the name of the resource context
		assertTrue(resourceContext.getName().equals(name));

		// check the new ResourceContext has no associated bundles
		assertNotNull(resourceContext.getBundleIds());
		assertTrue(resourceContext.getBundleIds().length == 0);

		// check the new ResourceContext has no associated monitors
		assertNotNull(resourceContext.getMonitors());
		assertTrue(resourceContext.getMonitors().length == 0);

		// check we receive a ResourceContextEvent
		ResourceContextEvent lastEvent = resourceContextListener.getLastEvent();
		assertNotNull(lastEvent);
		assertTrue(lastEvent.getContext().equals(resourceContext));
		assertTrue(lastEvent.getType() == ResourceContextEvent.RESOURCE_CONTEXT_CREATED);

		// check the resource context has been created
		resourceContexts = resourceMonitoringService.listContext();
		assertNotNull(resourceContexts);
		assertTrue(resourceContexts.length == 1);
		assertTrue(resourceContexts[0].equals(resourceContext));

		// get the context
		ResourceContext retrievedResourceContext = resourceMonitoringService
				.getContext(name);
		assertNotNull(retrievedResourceContext);
		assertTrue(retrievedResourceContext.equals(resourceContext));

	}

	/**
	 * Test create a new ResourceContext with an existing resource context name.
	 */
	public void testCreateResourceContextWithAnExistingResourceContextName() {
		final String name = "context1";

		// create first instance of context1
		ResourceContext resourceContext = resourceMonitoringService.createContext(name,
				null);
		assertNotNull(resourceContext);

		// retrieve event
		ResourceContextEvent event = resourceContextListener.getLastEvent();

		// check there is one ResourceContext
		assertTrue(resourceMonitoringService.listContext().length == 1);

		// try to create a new ResourceContext with the same name
		boolean exception = false;
		try {
			resourceMonitoringService.createContext(name, null);
		} catch (RuntimeException e) {
			exception = true;
		}
		assertTrue(exception);

		// check no event has been sent (i.e the last event is still the same)
		assertTrue(resourceContextListener.getLastEvent() == event);

		// check there is still one ResourceContext
		assertTrue(resourceMonitoringService.listContext().length == 1);
	}

	/**
	 * Test create a ResourceContext based on a template.
	 * 
	 * @throws ResourceMonitorException not expected
	 */
	public void testCreateResourceContextWithTemplate()
			throws ResourceMonitorException {
		final String name1 = "context1";
		final String name2 = "context2";

		// create a Context context1
		ResourceContext resourceContext1 = resourceMonitoringService.createContext(name1,
				null);

		// add a FakeMonitor simulating a CPU ResourceMonitor
		resourceContext1.addResourceMonitor(new FakeResourceMonitor(
				ResourceMonitoringService.RES_TYPE_CPU, resourceContext1));

		// create the ResourceContext2 based on ResourceContext1
		ResourceContext resourceContext2 = resourceMonitoringService.createContext(name2,
				resourceContext1);

		assertNotNull(resourceContext2);
		assertTrue(resourceContext2.getName().equals(name2));
		assertTrue(resourceContext2.getBundleIds().length == 0);
		ResourceMonitor[] rc2Monitors = resourceContext2.getMonitors();
		assertTrue(rc2Monitors.length == 1);
		assertTrue(rc2Monitors[0].getResourceType().equals(
				ResourceMonitoringService.RES_TYPE_CPU));

	}

	/**
	 * @throws ResourceMonitorException
	 */
	public void testCreateResourceContextWithADeletedTemplate()
			throws ResourceMonitorException {
		final String name1 = "context1";
		final String name2 = "context2";

		// create a Context context1
		ResourceContext resourceContext1 = resourceMonitoringService.createContext(name1,
				null);

		// add a FakeMonitor simulating a CPU ResourceMonitor
		resourceContext1.addResourceMonitor(new FakeResourceMonitor(
				ResourceMonitoringService.RES_TYPE_CPU, resourceContext1));

		// remove context1
		resourceContext1.removeContext(null);

		// create the ResourceContext2 based on ResourceContext1
		ResourceContext resourceContext2 = null;
		resourceContext2 = resourceMonitoringService.createContext(name2,
				resourceContext1);

		assertNotNull(resourceContext2);
		assertTrue(resourceContext2.getName().equals(name2));
		assertTrue(resourceContext2.getBundleIds().length == 0);
		ResourceMonitor[] rc2Monitors = resourceContext2.getMonitors();
		assertTrue(rc2Monitors.length == 0);
	}

	/**
	 * Test retrieve the context based on a bundle id.
	 */
	public void testGetContextByBundle() {
		final String name = "context1";
		final long bundleId = 1;

		// get ResourceContext associated with bundleId and check bundleId is
		// not associated with a context
		ResourceContext resourceContext = resourceMonitoringService.getContext(bundleId);
		assertNull(resourceContext);

		// create a new context
		ResourceContext resourceContext1 = resourceMonitoringService.createContext(name,
				null);

		// associated bundleId with resourceContext1
		resourceContext1.addBundle(bundleId);

		// get ResourceContext by bundle id
		resourceContext = resourceMonitoringService.getContext(bundleId);
		assertNotNull(resourceContext);
		assertTrue(resourceContext.equals(resourceContext1));

		// try to retrieve the ResourceContext with an unexisting bundle
		resourceContext = resourceMonitoringService.getContext(-1);
		assertNull(resourceContext);

	}

	/**
	 * Test supported types
	 * 
	 * @throws InvalidSyntaxException
	 */
	public void testSupportedTypes() throws InvalidSyntaxException {
		String[] supportedTypes = resourceMonitoringService.getSupportedTypes();
		assertNotNull(supportedTypes);

		// retrieves all ServiceReference of ResourceMonitorFactory services
		Collection factorySrs = getContext().getServiceReferences(
				ResourceMonitorFactory.class, null);
		assertNotNull(factorySrs);

		// check the number of ServiceReference is the same as the number of
		// supported types
		assertTrue("factorySrs.size(): " + factorySrs.size() + " must be equal to supportedTypes.length: " + supportedTypes.length, factorySrs.size() == supportedTypes.length);

		// iterate over the ServiceReference collection and retrieves each
		// ResourceMonitorFactory service.
		List computedSupportedTypes = new ArrayList();
		for (Iterator it = factorySrs.iterator(); it.hasNext();) {
			ResourceMonitorFactory rmf = (ResourceMonitorFactory) getContext().getService(
					(ServiceReference) it.next());
			// add the type in the list
			computedSupportedTypes.add(rmf.getType());
		}

		assertTrue(computedSupportedTypes.size() == supportedTypes.length);

		// check that each supported types is in the list
		for (int i = 0; i < supportedTypes.length; i++) {
			assertTrue(computedSupportedTypes.contains(supportedTypes[i]));
		}
	}

}
