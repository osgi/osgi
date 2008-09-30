package com.nokia.test.unittest;

import org.osgi.impl.service.deploymentadmin.BundleEntry;

import junit.framework.TestCase;

public class TestBundleEntry extends TestCase {

	public void testEquals001() {
		StubBundle b = new StubBundle();
		BundleEntry b1 = new BundleEntry(b);
	}
	
}
