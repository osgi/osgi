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
 * The <code>DownloadAgent</code> iterface is used internally in the OSGi MEG
 * reference implementation. It provides download service for an arbitrary
 * module of the MEG reference implementation (e.g. for Deployment DMT plugin).
 */
public interface DownloadAgent {

    /**
	 * Opens an input stream to the received URL.
	 * @param attr attributes needed for the InputStream creation
	 * @return the InputStream to the resource
	 * @throws Exception if any error occures
	 */
	InputStream download(String protocol, Map attr) throws Exception;
	
}
