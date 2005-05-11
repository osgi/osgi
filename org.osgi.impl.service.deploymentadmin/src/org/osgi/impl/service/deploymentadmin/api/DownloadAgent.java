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
package org.osgi.impl.service.deploymentadmin.api;

import java.net.URL;

/**
 * The <code>DownloadAgent</code> iterface is used internally in the OSGi MEG
 * reference implementation. It provides download service for an arbitrary
 * module of the MEG reference implementation (e.g. for containers).
 * <p>
 * The interface is available as a standard OSGi service under the
 * <code>org.osgi.impl.service.deploymentadmin.api.DownloadAgent</code>
 * service name.
 * <p>
 * Usage example:
 * <p>
 * 
 * <pre>
 * 
 *       ServiceReference sRef = context.getServiceReference(
 *           DownloadAgent.class.getName());
 *       DownloadAgent admin = (DownloadAgent) 
 *           context.getService(sRef);
 *       DownloadInputStream dwnldIs = admin.download(url);
 *       String mimeType = dwnldIs.getDescriptor(
 *           DownloadInputStream.KEY_MIMETYPE);
 *       int ch = dwnldIs.read();
 *       while (-1 != ch) {
 *           ...
 *           ch = dwnldIs.read();
 *       }
 *  
 * </pre>
 * 
 */
public interface DownloadAgent {
	/**
	 * Opens an input stream to the received URL.
	 * <p>
	 * 
	 * @param url the URL of the requested resource
	 * @return the {@link DownloadInputStream}to the URL
	 * @throws DownloadException if any error occures
	 */
	DownloadInputStream download(URL url) throws DownloadException;
}
