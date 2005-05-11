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

import java.util.Map;

/**
 * The <code>ProtocolPlugin</code> iterface is used internally in the OSGi MEG
 * reference implementation. It provides download service for the Deployment
 * Engine and for any other modules (e.g. for containers).
 * <p>
 * The interface is available as a standard OSGi service under the
 * <code>org.osgi.impl.service.deploymentadmin.api.ProtocolPlugin</code>
 * service name.
 * <p>
 * Bundles implementing and exposing this interface have to provide the
 * <code>protocol</code> property (see {@link #PROTOCOL}) in the dictionary
 * enclosed to the service.
 * <p>
 * The following registration means that the bundle provides the <code>
 * ProtocolPlugin</code>
 * interface and is able to download the target of an URL.
 * <p>
 * <pre>
 * 
 *       Dictionary dict = new Hashtable();
 *       dict.put(&lt;b&gt;ProtocolPlugin.PROTOCOL&lt;/b&gt;, &quot;&lt;b&gt;url&lt;/b&gt;&quot;);
 *       ServiceRegistration sReg = context.registerService(
 *           ProtocolPlugin.class.getName(), sObj, dict);
 *  
 * </pre>
 * <p>
 * The client of the service finds it as follows:
 * <p>
 * <pre>
 * 
 *       Filter filter = context.createFilter(&quot;(&quot; + &lt;b&gt;ProtocolPlugin.PROTOCOL&lt;/b&gt; + &quot;=&lt;b&gt;url&lt;/b&gt;)&quot;);
 *       ServiceReference[] sRef = context.getServiceReferences(ProtocolPlugin.class.getName(), 
 *           filter.toString());
 *  
 * </pre>
 */
public interface ProtocolPlugin {
	/**
	 * Key to the <code>Dictionary</code> to identify the protocol the
	 * download plugin supports.
	 */
	String	PROTOCOL	= "protocol";

	/**
	 * Gives back an {@link DownloadInputStream}created according to the got
	 * <code>Map</code>. The exact content of the <code>
	 * Map</code> is
	 * implementation specific.
	 * <p>
	 * 
	 * @param attr attributes needed for the DownloadInputStream creation
	 * @return the requested DownloadInputStream
	 * @throws DownloadException if any error occures
	 */
	DownloadInputStream download(Map attr) throws DownloadException;
}
