/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.framework.internal.core;

import java.util.Dictionary;
import java.util.Hashtable;
import org.eclipse.osgi.framework.debug.DebugOptions;
import org.eclipse.osgi.framework.util.FrameworkMessageFormat;
import org.osgi.framework.*;

/**
 * This class activates the System Bundle.
 */

public class SystemBundleActivator implements BundleActivator {
	protected BundleContext context;
	protected SystemBundle bundle;
	protected Framework framework;
	protected ServiceRegistration packageAdmin;
	protected ServiceRegistration permissionAdmin;
	protected ServiceRegistration startLevel;
	protected ServiceRegistration debugOptions;

	public SystemBundleActivator() {
	}

	public void start(BundleContext context) throws Exception {
		this.context = context;
		bundle = (SystemBundle) context.getBundle();
		framework = bundle.framework;

		FrameworkMessageFormat.setContext(context);

		if (framework.packageAdmin != null) {
			packageAdmin = register(Constants.OSGI_PACKAGEADMIN_NAME, framework.packageAdmin);
		}

		if (framework.permissionAdmin != null) {
			permissionAdmin = register(Constants.OSGI_PERMISSIONADMIN_NAME, framework.permissionAdmin);
		}

		if (framework.startLevelManager != null) {
			startLevel = register(Constants.OSGI_STARTLEVEL_NAME, framework.startLevelManager);
		}

		DebugOptions dbgOptions = null;
		if ((dbgOptions = DebugOptions.getDefault()) != null) {
			debugOptions = register(org.eclipse.osgi.service.debug.DebugOptions.class.getName(), dbgOptions);
		}

		// Always call the adaptor.frameworkStart() at the end of this method.
		framework.adaptor.frameworkStart(context);
		// attempt to resolve all bundles
		// this is done after the adaptor.frameworkStart has been called
		// this should be the first time the resolver State is accessed
		framework.packageAdmin.setResolvedBundles(bundle);
	}

	public void stop(BundleContext context) throws Exception {
		// Always call the adaptor.frameworkStop() at the begining of this method.
		framework.adaptor.frameworkStop(context);

		if (packageAdmin != null) {
			packageAdmin.unregister();
		}

		if (permissionAdmin != null) {
			permissionAdmin.unregister();
		}

		if (startLevel != null) {
			startLevel.unregister();
		}

		if (debugOptions != null) {
			debugOptions.unregister();
		}

		framework = null;
		bundle = null;
		this.context = null;
		FrameworkMessageFormat.setContext(null);
	}

	/**
	 * Register a service object.
	 *
	 */
	protected ServiceRegistration register(String name, Object service) {
		Hashtable properties = new Hashtable(7);

		Dictionary headers = bundle.getHeaders();

		properties.put(Constants.SERVICE_VENDOR, headers.get(Constants.BUNDLE_VENDOR));

		properties.put(Constants.SERVICE_RANKING, new Integer(Integer.MAX_VALUE));

		properties.put(Constants.SERVICE_PID, bundle.getBundleId() + "." + service.getClass().getName()); //$NON-NLS-1$

		return context.registerService(name, service, properties);
	}

}