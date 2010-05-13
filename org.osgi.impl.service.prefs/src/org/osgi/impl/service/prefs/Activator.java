/*
 * @(#)Activator.java	1.5 01/07/18
 * $Id$
 *
 
 * 
 * (C) Copyright 1996-2001 Sun Microsystems, Inc. 
 * 
 * This source code is licensed to OSGi as MEMBER LICENSED MATERIALS 
 * under the terms of Section 3.2 of the OSGi MEMBER AGREEMENT.
 * 
 */
package org.osgi.impl.service.prefs;

import java.util.*;
import org.osgi.framework.*;

/**
 * @version $Id$
 */
public class Activator implements BundleActivator, ServiceFactory {
	private static final String	PREFERENCES_SERVICE	= "org.osgi.service.prefs.PreferencesService";
	private BundleContext		bundleContext		= null;

	public synchronized void start(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
		Dictionary properties = new Hashtable(4);
		properties.put("Description", "The OSGi Preferences Service");
		properties.put("BackingStore", "file");
		bundleContext.registerService(PREFERENCES_SERVICE, this, properties);
	}

	public synchronized void stop(BundleContext bundleContext) {
	}

	public synchronized Object getService(Bundle bundle, ServiceRegistration reg) {
		return new PreferencesServiceImpl(bundleContext, bundle.getBundleId());
	}

	public synchronized void ungetService(Bundle bundle,
			ServiceRegistration reg, Object service) {
		((PreferencesServiceImpl) service).flushAll();
	}
}
