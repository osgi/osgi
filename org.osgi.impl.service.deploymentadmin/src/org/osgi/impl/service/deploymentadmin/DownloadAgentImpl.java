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

import java.net.URL;
import java.util.*;
import org.osgi.framework.*;
import org.osgi.impl.service.deploymentadmin.api.*;
import org.osgi.util.tracker.ServiceTracker;

public class DownloadAgentImpl extends ServiceTracker implements DownloadAgent {
	public DownloadAgentImpl(BundleContext context) {
		super(context, ProtocolPlugin.class.getName(), null);
		open();
		context.registerService(DownloadAgent.class.getName(), this, null);
	}

	void destroy() {
		close();
	}

	private ProtocolPlugin getPlugin(String protocol) {
		ProtocolPlugin ret = null;
		ServiceReference[] refs = getServiceReferences();
		if (null == refs)
			return null;
		for (int i = 0; i < refs.length; ++i) {
			String prop = (String) refs[i].getProperty(ProtocolPlugin.PROTOCOL);
			if (protocol.equals(prop)) {
				ret = (ProtocolPlugin) getService(refs[i]);
				break;
			}
		}
		return ret;
	}

	public DownloadInputStream download(URL url) throws DownloadException {
		ProtocolPlugin plugin = getPlugin("url");
		if (null == plugin)
			throw new DownloadException(
					"There is no appropriate protocol plugin " + "for " + url);
		// only "url" is supported
		Map map = new HashMap();
		map.put("url", url);
		return plugin.download(map);
	}
}
