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
package org.osgi.impl.service.policy;

import java.util.Hashtable;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.impl.service.policy.permadmin.PermissionAdminPlugin;
import org.osgi.service.dmt.DmtDataPlugIn;
import org.osgi.service.permissionadmin.PermissionAdmin;


/**
 * @author Peter Nagy <peter.1.nagy@nokia.com>
 */
public final class JavaPolicyPlugin implements BundleActivator {

	public void start(BundleContext context) throws Exception {
		ServiceReference permissionAdminReference = context.getServiceReference(PermissionAdmin.class.getName());
		if (permissionAdminReference!=null) {
			PermissionAdminPlugin permissionAdminPlugin = new PermissionAdminPlugin((PermissionAdmin) context.getService(permissionAdminReference));
			Hashtable props = new Hashtable();
			props.put("dataRootURIs",PermissionAdminPlugin.dataRootURI);
			context.registerService(DmtDataPlugIn.class.getName(),permissionAdminPlugin,props);
		}
	}

	public void stop(BundleContext context) throws Exception {
		// framework will take care of removing our registered services
	}

}
