
package org.osgi.test.cases.resourcemanagement.junit;

import org.osgi.service.resourcemanagement.ResourceContext;
import org.osgi.service.resourcemanagement.ResourceMonitor;
import org.osgi.service.resourcemanagement.ResourceMonitorException;

/**
 *
 */
public class FakeResourceMonitor implements ResourceMonitor {

	private String			resourceType;
	private ResourceContext	resourceContext;

	/**
	 * 
	 */
	public FakeResourceMonitor() {
		// nothing to do.
	}

	/**
	 * @param pResourceType
	 * @param pResourceContext
	 */
	public FakeResourceMonitor(String pResourceType,
			ResourceContext pResourceContext) {
		resourceType = pResourceType;
		resourceContext = pResourceContext;
	}

	public ResourceContext getContext() {
		return resourceContext;
	}

	public String getResourceType() {
		return resourceType;
	}

	public void delete() {
		// TODO Auto-generated method stub

	}

	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isDeleted() {
		// TODO Auto-generated method stub
		return false;
	}

	public void enable() throws ResourceMonitorException, IllegalStateException {
		// TODO Auto-generated method stub

	}

	public void disable() throws IllegalStateException {
		// TODO Auto-generated method stub

	}

	public Comparable getUsage() throws IllegalStateException {
		// TODO Auto-generated method stub
		return null;
	}

	public long getSamplingPeriod() {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getMonitoredPeriod() {
		// TODO Auto-generated method stub
		return 0;
	}

}
