/*
 * ============================================================================
 * (c) Copyright 2004 Nokia
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Nokia and its licensors. 
 * All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 * ============================================================================
 */
package org.osgi.impl.service.deploymentadmin;

import java.io.InputStream;
import java.util.*;
import org.osgi.framework.*;
import org.osgi.impl.service.deploymentadmin.api.*;
import org.osgi.service.application.ApplicationContainer;
import org.osgi.util.tracker.ServiceTracker;

public class ApplicationContainerPackageHandler implements BundleActivator,
		PackageHandler {
	private static final String	APPLICATION_TYPE	= "application_type";
	private static final String	PACKAGETYPE_BASIC	= "basic";
	private static final String	CONTAINERTYPE_BASIC	= "BasicContainer";

	static class Tracker extends ServiceTracker {
		public Tracker(BundleContext context) {
			super(context, ApplicationContainer.class.getName(), null);
		}

		private ApplicationContainer getContainer(String type) {
			ApplicationContainer ret = null;
			ServiceReference[] refs = getServiceReferences();
			if (null == refs)
				return null;
			for (int i = 0; i < refs.length; ++i) {
				String prop = (String) refs[i].getProperty(APPLICATION_TYPE);
				if (prop.equals(type)) {
					ret = (ApplicationContainer) getService(refs[i]);
					break;
				}
			}
			return ret;
		}
	}

	private BundleContext	context;
	private Tracker			tracker;

	public void start(BundleContext context) throws Exception {
		this.context = context;
		tracker = new Tracker(context);
		tracker.open();
		Dictionary dict = new Hashtable();
		dict.put(PackageHandler.PACKAGETYPE, PACKAGETYPE_BASIC);
		// unregistered by the OSGi framework
		context.registerService(PackageHandler.class.getName(), this, dict);
	}

	public void stop(BundleContext context) throws Exception {
		tracker.close();
	}

	public void install(InputStream stream, String packageType, Map data)
			throws PackageHandlerException {
		if (PACKAGETYPE_BASIC.equals(packageType)) {
			ApplicationContainer cont = tracker
					.getContainer(CONTAINERTYPE_BASIC);
			try {
				cont.installApplication(stream);
			}
			catch (Exception e) {
				throw new PackageHandlerException(e.getMessage());
			}
		}
		else {
			throw new PackageHandlerException("Package type " + packageType
					+ " is not supported");
		}
	}
}
