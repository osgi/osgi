package org.osgi.impl.service.residentialmanagement;

import junit.framework.TestCase;

import org.osgi.framework.BundleContext;
import org.osgi.framework.PackagePermission;

public class FilteredTestCase extends TestCase {
	BundleContext context;
	
	public void setBundleContext(BundleContext context) {
		this.context = context;
	}
	
	public void testFirst() {
		System.out.println(PackagePermission.class);
	}
}
