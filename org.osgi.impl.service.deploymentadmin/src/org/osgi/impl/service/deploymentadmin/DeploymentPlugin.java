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

import java.net.*;
import java.util.*;
import org.osgi.framework.*;
import org.osgi.impl.service.deploymentadmin.api.PackageHandlerException;
import org.osgi.service.dmt.*;

public class DeploymentPlugin implements BundleActivator, DmtExecPlugin {
	// DMT node the Deployment Plugin registers itself to as exec DMT plugin
	private final static String	DMT_NODE	= "./OSGi/deploy/install";
	private DeploymentEngine	deploymentEngine;

	public void start(BundleContext context) throws Exception {
		deploymentEngine = new DeploymentEngine(context);
		// registers the data and exec DMT plugin
		Dictionary dict = new Hashtable();
		dict.put("execRootURIs", new String[] {DMT_NODE});
		String[] ifs = new String[] {DmtExecPlugin.class.getName()};
		// unregistered by the OSGi framework
		context.registerService(ifs, this, dict);
	}

	public void stop(BundleContext context) throws Exception {
		deploymentEngine.destroy();
	}

	public void execute(DmtSession session, String nodeUri, String data)
			throws DmtException {
		try {
			// install
			if (nodeUri.equals(DMT_NODE)) {
				URL url = new URL(data);
				deploymentEngine.install(url);
				// error
			}
			else {
				throw new DmtException(nodeUri, DmtException.NODE_NOT_FOUND,
						"Bad node");
			}
		}
		catch (MalformedURLException e) {
			throw new DmtException(nodeUri, DmtException.OTHER_ERROR,
					"Bad URI: " + data, e);
		}
		catch (PackageHandlerException e) {
			throw new DmtException(nodeUri, DmtException.OTHER_ERROR,
					"Error occured during installation", e);
		}
	}
}
