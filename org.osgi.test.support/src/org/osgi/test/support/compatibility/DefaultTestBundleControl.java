package org.osgi.test.support.compatibility;

import java.net.URL;

import junit.framework.TestCase;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;

public abstract class DefaultTestBundleControl extends TestCase {
	
	/* @GuardedBy("this") */
	private BundleContext	context;

	/**
	 * THis method is called by the JUnit runner for OSGi, and gives us a Bundle
	 * Context.
	 */
	public synchronized void setBundleContext(BundleContext context) {
		this.context = context;
	}

	/**
	 * Returns the current Bundle Context
	 */

	public synchronized BundleContext getContext() {
		if (context == null)
			fail("No valid Bundle context said, are you running in OSGi Test mode?");

		return context;
	}

	/**
	 * This returned a web server but we will just now, it is mostly used in
	 * installBundle and there we get the bundle from our resources.
	 */
	public String getWebServer() {
		return "/www/";
	}
	
	/**
	 * Uninstall a bundle.
	 * 
	 * @param bundle
	 * @throws BundleException
	 */
	public void uninstallBundle(Bundle bundle) throws BundleException {
		bundle.uninstall();
	}

	/**
	 * Install a bundle.
	 * 
	 * @param bundle
	 * @throws BundleException
	 */
	public Bundle installBundle(String url) throws BundleException {
		URL resource = getClass().getResource(url);
		if ( resource == null )
			fail("Can not load bundle " + url);
		return getContext().installBundle(resource.toString());
	}

	/**
	 * Convenience getService method
	 */	
	public Object getService(Class clazz ) {
		BundleContext c = getContext();
		ServiceReference ref = c.getServiceReference(clazz.getName());
		if ( ref == null )
			return null;
		
		return c.getService(ref);
	}
}
