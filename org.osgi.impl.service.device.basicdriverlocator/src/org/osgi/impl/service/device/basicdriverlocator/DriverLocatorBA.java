/**
 * Copyright (c) 1999, 2000 Gatespace AB. All Rights Reserved.
 * 
 * Gatespace grants the OSGi Alliance an irrevocable,
 * perpetual, non-exclusive, worldwide, paid-up right and license to
 * reproduce, display, perform, prepare and have prepared derivative works
 * based upon and distribute and sublicense this material and derivative
 * works thereof as set out in the OSGi MEMBER AGREEMENT as of January 24
 * 2000, for use in accordance with Section 2.2 of the BY-LAWS of the
 * OSGi MEMBER AGREEMENT.
 */
package org.osgi.impl.service.device.basicdriverlocator;

import java.util.Properties;
import org.osgi.framework.*;

/**
 * Creates and registers a DriverLocator Service with basic file lookup.
 * 
 * @author Gatespace AB (osgiref@gatespace.com)
 * @version $Revision$
 * @see osgi.org.osgi.framework.BundleActivator
 *  
 */
public class DriverLocatorBA implements BundleActivator {
	BasicDriverLocator	basicLocator	= null;
	ServiceRegistration	sr				= null;

	/**
	 * Bundle is started by framework.
	 */
	public void start(BundleContext bc) throws Exception {
		try {
			Log.start(bc);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		basicLocator = new BasicDriverLocator(bc);
		Properties props = new Properties();
		sr = bc.registerService("org.osgi.service.device.DriverLocator",
				basicLocator, props);
	}

	/**
	 * Bundle is stopped by framework.
	 */
	public void stop(BundleContext bc) throws Exception {
		Log.close();
		// Cleanup of registered service is done automatically by framework.
	}
}
