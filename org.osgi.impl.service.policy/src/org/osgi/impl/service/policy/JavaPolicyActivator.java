/*
 * ============================================================================
 * (c) Copyright 2005 Nokia
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
package org.osgi.impl.service.policy;

import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.impl.service.dmt.api.DmtPrincipalPermissionAdmin;
import org.osgi.impl.service.policy.condpermadmin.ConditionalPermissionAdminPlugin;
import org.osgi.impl.service.policy.dmtprincipal.DmtPrincipalPlugin;
import org.osgi.impl.service.policy.permadmin.PermissionAdminPlugin;
import org.osgi.service.condpermadmin.ConditionalPermissionAdmin;
import org.osgi.service.dmt.DmtDataPlugin;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;


/**
 * Activator for the policy subtree. All the policy subtrees are wrappers around services
 * that are in the service registry. So this activator creates a tracker to look for those,
 * and for every exported service of PermissionAdmin,ConditionalPermissionAdmin,
 * DmtPrincipalPermissionAdmin, it creates and exports a DmtData service for the DMT admin
 * to pick up.
 * @author Peter Nagy <peter.1.nagy@nokia.com>
 */
public final class JavaPolicyActivator implements BundleActivator,ServiceTrackerCustomizer {
	ServiceTracker tracker = null;
	BundleContext context = null;

	public void start(BundleContext context) throws Exception {
		this.context = context;
		Filter filter = context.createFilter(
				"(|"+
				"(objectClass="+PermissionAdmin.class.getName()+")"+
				"(objectClass="+ConditionalPermissionAdmin.class.getName()+")"+
				"(objectClass="+DmtPrincipalPermissionAdmin.class.getName()+")"+
				")");
		tracker = new ServiceTracker(context,filter,this);
		tracker.open();
	}

	public void stop(BundleContext context) throws Exception {
		tracker.close();
	}
	
	public Object addingService(ServiceReference reference) {
		//System.out.println("adding: "+reference);
		String[] objectClass = (String[]) reference.getProperty("objectClass");

		if (PermissionAdmin.class.getName().equals(objectClass[0])) {
			PermissionAdmin pa = (PermissionAdmin) context.getService(reference);
			PermissionAdminPlugin permissionAdminPlugin;
			try {
				permissionAdminPlugin = new PermissionAdminPlugin(pa);
			}
			catch (NoSuchAlgorithmException e) {
				e.printStackTrace(); // TODO
				return null;
			}
			Hashtable props = new Hashtable();
			props.put("dataRootURIs",PermissionAdminPlugin.dataRootURI);
			ServiceRegistration sr = context.registerService(DmtDataPlugin.class.getName(),permissionAdminPlugin,props);
			return sr;
		} else if (ConditionalPermissionAdmin.class.getName().equals(objectClass[0])) {
			ConditionalPermissionAdmin pa = (ConditionalPermissionAdmin) context.getService(reference);
			ConditionalPermissionAdminPlugin conditionalPermissionAdminPlugin;
			try {
				conditionalPermissionAdminPlugin = new ConditionalPermissionAdminPlugin(pa);
			}
			catch (NoSuchAlgorithmException e) {
				e.printStackTrace(); // TODO
				return null;
			}
			Hashtable props = new Hashtable();
			props.put("dataRootURIs",ConditionalPermissionAdminPlugin.dataRootURI);
			ServiceRegistration sr = context.registerService(DmtDataPlugin.class.getName(),conditionalPermissionAdminPlugin,props);
			return sr;
		} else if (DmtPrincipalPermissionAdmin.class.getName().equals(objectClass[0])) {
			DmtPrincipalPermissionAdmin pa = (DmtPrincipalPermissionAdmin) context.getService(reference);
			DmtPrincipalPlugin dmtPrincipalPlugin;
			try {
				dmtPrincipalPlugin = new DmtPrincipalPlugin(pa);
			}
			catch (NoSuchAlgorithmException e) {
				e.printStackTrace(); // TODO
				return null;
			}
			Hashtable props = new Hashtable();
			props.put("dataRootURIs",DmtPrincipalPlugin.dataRootURI);
			ServiceRegistration sr = context.registerService(DmtDataPlugin.class.getName(),dmtPrincipalPlugin,props);
			return sr;
		}
		return null;
	}

	public void modifiedService(ServiceReference reference, Object service) {
		//System.out.println("modified: "+reference);
	}

	public void removedService(ServiceReference reference, Object service) {
		//System.out.println("removed: "+reference);
		
		// in addingService, we gave back the ServiceRegistration for our plugin.
		// Now, when the wrapped service is removed, we remove our plugin, too.
		ServiceRegistration sr = (ServiceRegistration) service;
		sr.unregister();
	}
}
