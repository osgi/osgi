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
package org.osgi.impl.service.dwnlimpl;

import java.io.InputStream;
import java.util.*;

import org.osgi.framework.*;
import org.osgi.impl.service.dwnl.*;
import org.osgi.util.tracker.ServiceTracker;

public class DownloadAgentImpl implements DownloadAgent, BundleActivator {
    
    private BundleContext         context;
    private ServiceRegistration   reg;
    private TrackerProtocolPlugin trackPP;
   
    /*
     * Class to track the Protocol Plugins
     */
    private class TrackerProtocolPlugin extends ServiceTracker {
        public TrackerProtocolPlugin() {
            super(DownloadAgentImpl.this.context, 
                    ProtocolPlugin.class.getName(), null);
        }
    }

    public void start(BundleContext context) throws Exception {
        this.context = context;
        
		reg = context.registerService(DownloadAgent.class.getName(), this, null);
		
		trackPP = new TrackerProtocolPlugin();
		trackPP.open();
    }

    public void stop(BundleContext context) throws Exception {
        trackPP.close();        
        reg.unregister();
    }

	private ProtocolPlugin getPlugin(String protocol) {
		ProtocolPlugin ret = null;
		ServiceReference[] refs = trackPP.getServiceReferences();
		if (null == refs)
			return null;
		for (int i = 0; i < refs.length; ++i) {
			String prop = (String) refs[i].getProperty(ProtocolPlugin.PROTOCOL);
			if (protocol.equals(prop)) {
				ret = (ProtocolPlugin) trackPP.getService(refs[i]);
				break;
			}
		}
		return ret;
	}

	public InputStream download(String protocol, Map attrs) throws Exception {
		ProtocolPlugin plugin = getPlugin(protocol);
		if (null == plugin)
			throw new Exception("There is no appropriate protocol plugin " + 
			    "for " + protocol);
		return plugin.download(attrs);
	}
	
}
