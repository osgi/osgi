/*
 * (C) Copyright 1996-2001 Sun Microsystems, Inc. 
 * 
 * This source code is licensed to OSGi as MEMBER LICENSED MATERIALS 
 * under the terms of Section 3.2 of the OSGi MEMBER AGREEMENT.
 */
package org.osgi.impl.service.prefs;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.prefs.PreferencesService;

/**
 * @author $Id$
 */
public class Activator
		implements BundleActivator, ServiceFactory<PreferencesService> {
	private static final String	PREFERENCES_SERVICE	= "org.osgi.service.prefs.PreferencesService";
	private BundleContext		bundleContext		= null;

	@Override
	public synchronized void start(
			@SuppressWarnings("hiding") BundleContext bundleContext) {
		this.bundleContext = bundleContext;
		Dictionary<String,Object> properties = new Hashtable<>(4);
		properties.put("Description", "The OSGi Preferences Service");
		properties.put("BackingStore", "file");
		bundleContext.registerService(PREFERENCES_SERVICE, this, properties);
	}

	@Override
	public synchronized void stop(
			@SuppressWarnings("hiding") BundleContext bundleContext) {
		// empty
	}

	@Override
	public synchronized PreferencesService getService(Bundle bundle,
			ServiceRegistration<PreferencesService> reg) {
		return new PreferencesServiceImpl(bundleContext, bundle.getBundleId());
	}

	@Override
	public synchronized void ungetService(Bundle bundle,
			ServiceRegistration<PreferencesService> reg,
			PreferencesService service) {
		((PreferencesServiceImpl) service).flushAll();
	}
}
