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

import java.io.InputStream;
import java.util.Map;

/**
 * The <code>PackageHandler</code> iterface is used internally in the OSGi MEG
 * reference implementation. MEG implementation support multiple package types.
 * In the OSGi MEG reference implementation each of them may have a separate so
 * called package handler that is able to install the installation entity of the
 * package type.
 * <p>
 * The interface is available as a standard OSGi service under the
 * <code>org.osgi.impl.service.deploymentadmin.api.PackageHandler</code>
 * service name.
 * <p>
 * Bundles implementing and exposing this interface have to provide the
 * <code>packagetype</code> property (see {@link #PACKAGETYPE}) in the
 * dictionary enclosed to the service. An implemantation can support more than
 * one package types. In this case the properti has to contain a comma separated
 * list. <code>bundle</code> means OSGi bundle <code>suite</code> means OSGi
 * MEG bundle suite These package type names are reserved (see
 * {@link #PACKAGETYPE_BUNDLE}and {@link #PACKAGETYPE_SUITE}).
 * <p>
 * The following registration means that the bundle provides the <code>
 * PackageHandler</code>
 * interface and supports the "bundle" and the "suite" package types.
 * <pre>
 *       Dictionary dict = new Hashtable();
 *       dict.put(&lt;b&gt;PackageHandler.PACKAGETYPE&lt;/b&gt;, &quot;&lt;b&gt;bundle, suite&lt;/b&gt;&quot;);
 *       ServiceRegistration sReg = context.registerService(
 *           PackageHandler.class.getName(), sObj, dict);
 *  
 * </pre>
 */
public interface PackageHandler {
	/**
	 * Key to the <code>Dictionary</code> to identify the package type the
	 * download plugin supports.
	 */
	String	PACKAGETYPE			= "application_type";
	/**
	 * Package type for OSGi bundles
	 */
	String	PACKAGETYPE_BUNDLE	= "bundle";
	/**
	 * Package type for OSGi MEG bundle suites
	 */
	String	PACKAGETYPE_SUITE	= "suite";

	/**
	 * Installs an entity.
	 * <p>
	 * 
	 * @param stream InputStream to the installation entity
	 * @param packageType package type
	 * @param data addition data (e.g. locatin string in case of OSGi bundles)
	 */
	void install(InputStream stream, String packageType, Map data)
			throws PackageHandlerException;
}
