/**
 * Copyright (c) 1999, 2000 Gatespace AB. All Rights Reserved.
 *
 * Gatespace grants the OSGi Alliance an irrevocable,
 * perpetual, non-exclusive, worldwide, paid-up right and license to
 * reproduce, display, perform, prepare and have prepared derivative works
 * based upon and distribute and sublicense this material and derivative
 * works thereof as set out in the OSGi MEMBER AGREEMENT as of January 24
 * 2000, for use in accordance with Section 2.2 of the BY-LAWS of the
 * OSGi MEMBER AGREEMENT.
 */
package org.osgi.impl.service.http;

import java.io.*;
import java.security.*;
import javax.servlet.http.HttpServletRequest;
import org.osgi.framework.Bundle;
import org.osgi.service.http.*;

//  ******************** AliasRegistration ********************
/**
 * * Base class for all registrations. * *
 * 
 * @author Gatespace AB (osgiref@gatespace.com) *
 * @version $Revision$
 */
public abstract class AliasRegistration {
	String					alias;				// Alias as registered
	HttpContext				httpContext;
	HttpServiceImpl			serviceImpl;
	AccessControlContext	acc;
	protected Log			log;
	boolean					closed	= false;

	public AliasRegistration(HttpServiceImpl serviceImpl, String alias,
			HttpContext httpContext) throws NamespaceException,
			IllegalArgumentException {
		if (alias == null || alias.length() == 0)
			throw new NamespaceException("Alias can not be empty");
		// Alias must start with '/'
		if (alias.charAt(0) != '/')
			throw new IllegalArgumentException(
					"Alias must begin with slash ('/') - " + alias);
		// and not end with '/'
		if (alias.equals("/"))
			alias = "";
		else
			if (alias.endsWith("/"))
				throw new IllegalArgumentException(
						"Alias must not end with slash ('/') - " + alias);
		this.alias = alias;
		this.serviceImpl = serviceImpl;
		this.httpContext = httpContext;
		this.acc = AccessController.getContext();
		this.log = new Log("AliasRegistration_bid#"
				+ serviceImpl.getBundle().getBundleId() + " " + alias);
	}

	public String getAlias() {
		return alias;
	}

	public void destroy() {
		closed = true;
		alias = null;
		httpContext = null;
		serviceImpl = null;
		log = null;
	}

	public abstract AliasMatch getMatch(String uri);

	public abstract void handleRequest(HttpServletRequest request,
			HttpResponse response) throws IOException;

	public abstract InputStream getResourceAsStream(String path);

	public HttpContext getHttpContext() {
		return httpContext;
	}

	public Bundle getBundle() {
		return serviceImpl.getBundle();
	}

	public String getMimeType(final String name) {
		try {
			return (String) AccessController.doPrivileged(
					new PrivilegedExceptionAction() {
						public Object run() throws Exception {
							return httpContext.getMimeType(name);
						}
					}, acc);
		}
		catch (PrivilegedActionException pae) {
			log.error("getMimeType() failed: ", pae.getException());
			return null;
		}
		catch (Exception e) {
			log.error("getMimeType() failed: ", e);
			return null;
		}
	}
}
