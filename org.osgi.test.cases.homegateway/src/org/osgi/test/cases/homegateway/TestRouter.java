package org.osgi.test.cases.homegateway;

import junit.framework.*;

import org.osgi.framework.*;

public class TestRouter extends TestCase {
	BundleContext context;
	
	public void setBundleContext(BundleContext context) {
		this.context = context;
	}
	public void testSimple() {
		System.out.println("Helo World");
	}
}
