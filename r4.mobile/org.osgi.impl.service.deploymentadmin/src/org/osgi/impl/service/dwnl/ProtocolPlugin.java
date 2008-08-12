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
package org.osgi.impl.service.dwnl;

import java.io.InputStream;
import java.util.Map;

/**
 * The interface is available as a standard OSGi service under the
 * <code>org.osgi.impl.service.deploymentadmin.api.ProtocolPlugin</code>
 * service name. {@link org.osgi.impl.service.dwnl.DownloadAgent} 
 * implementations use (tracks) these services to provide dowload functionality.<p>
 */
public interface ProtocolPlugin {

    /**
	 * Key to the service registration <code>Dictionary</code> to identify 
	 * the protocol the download plugin supports.
	 */
	String	PROTOCOL	= "protocol";
	
    /**
	 * Gives back an {@link DownloadInputStream}created according to the got
	 * <code>Map</code>. The exact content of the <code>
	 * Map</code> is
	 * implementation specific.
	 * <p>
	 * 
	 * @param attr attributes needed for the InputStream creation
	 * @return the InputStream to the requested resource 
	 * @throws Exception if any error occures
	 */
	InputStream download(Map attr) throws Exception;
	
}
