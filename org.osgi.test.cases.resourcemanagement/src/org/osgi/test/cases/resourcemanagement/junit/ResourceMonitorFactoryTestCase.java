
package org.osgi.test.cases.resourcemanagement.junit;

import java.util.Collection;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.resourcemanagement.ResourceContext;
import org.osgi.service.resourcemanagement.ResourceManager;
import org.osgi.service.resourcemanagement.ResourceMonitorFactory;
import org.osgi.test.cases.resourcemanagement.utils.FakeResourceMonitorFactory;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * see Conformance Tests description.odt file.
 * 
 * @author Antonin CHAZALET, Orange, antonin.chazalet@orange.com,
 *         antonin.chazalet@gmail.com
 */
public class ResourceMonitorFactoryTestCase extends DefaultTestBundleControl {

	/**
	 * bundle context.
	 */
	private BundleContext			bundleContext;

	/**
	 * resource manager.
	 */
	private ResourceManager			resourceManager;

	/**
	 * resourceContext
	 */
	private ResourceContext			resourceContext;

	/**
	 * resource context name
	 */
	private final String			resourceContextName	= "context1";

	public void setBundleContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
		ServiceReference serviceReference = this.bundleContext
				.getServiceReference(ResourceManager.class);
		resourceManager = (ResourceManager) this.bundleContext.getService(serviceReference);
	}

	protected void setUp() throws Exception {
		resourceContext = resourceManager.createContext(resourceContextName,
				null);
	}

	protected void tearDown() throws Exception {
		resourceContext.removeContext(null);
	}

	/**
	 * Add another resource monitor factory, and test that everything worked
	 * well.
	 */
	public void testAddingAnotherResourceMonitorFactory() {
		StringBuffer filter = new StringBuffer();
		filter.append("(&(");
		filter.append(Constants.OBJECTCLASS);
		filter.append("=");
		filter.append(ResourceMonitorFactory.class.getName());
		filter.append("))");
		Integer totalNumberOfExistingResourceMonitorFactories = null;
		try {
			Collection factoryReferences = bundleContext.getServiceReferences(
					ResourceMonitorFactory.class, filter.toString());
 
			// Keep the total number of existing resource monitor factory/ies.
			totalNumberOfExistingResourceMonitorFactories = new Integer(factoryReferences.size());
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
			fail("Can not get the already existing OSGi service resource monitor factories.");
		}

		// Create, and add another resource monitor factory in the OSGi services
		// registry.
		FakeResourceMonitorFactory fakeResourceMonitorFactory = new FakeResourceMonitorFactory(bundleContext, ResourceManager.RES_TYPE_DISK_STORAGE);
		
		// Check that the new total number of existing resource monitor
		// factory/ies is equal to the previous one plus one.
		int newTotalNumberOfExistingResourceMonitorFactories;
		try {
			Collection factoryReferences = bundleContext.getServiceReferences(
					ResourceMonitorFactory.class, filter.toString());

			// Keep the new total number of existing resource monitor
			// factory/ies.
			newTotalNumberOfExistingResourceMonitorFactories = factoryReferences.size();
			assertEquals(totalNumberOfExistingResourceMonitorFactories.intValue() + 1, newTotalNumberOfExistingResourceMonitorFactories);
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
			fail("Can not get the already existing OSGi service resource monitor factories.");
		}

		// Remove the just added resource monitor factory from the OSGi services
		// registry.
		fakeResourceMonitorFactory.stop();
	}

}
