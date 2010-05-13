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
import java.net.URL;
import java.security.*;
import javax.servlet.http.*;
import org.osgi.service.http.*;

//  ******************** ResourceRegistration ********************
/**
 * * A registration for a resource. The resource is retrived through the *
 * HttpContect, which is supplied by the bundle making the registration. * *
 * 
 * @author Gatespace AB (osgiref@gatespace.com) *
 * @version $Id$
 */
public final class ResourceRegistration extends AliasRegistration {
	private String	name;

	public ResourceRegistration(HttpServiceImpl serviceImpl, String alias,
			String name, HttpContext httpContext) throws NamespaceException {
		super(serviceImpl, alias, httpContext);
		// name must not be null or end with '/'
		if (name == null)
			throw new IllegalArgumentException("name can not be null");
		if (name.endsWith("/"))
			throw new IllegalArgumentException(
					"The name must not end with slash ('/') - " + name);
		this.name = name;
	}

	public AliasMatch getMatch(String uri) {
		log.debug("Checking if resource exist:" + uri);
		URL url = getResource(uri);
		return (url != null) ? new AliasMatch(this, url) : null;
	}

	public void handleRequest(HttpServletRequest request, HttpResponse response)
			throws IOException {
		InputStream is;
		URL url;
		String reqURI = request.getRequestURI();
		if (response.aliasMatch.url == null
				|| (is = response.aliasMatch.url.openStream()) == null) {
			log.error("Strange, cannot get bundle's resource : " + reqURI);
			response
					.sendFancyError(
							HttpServletResponse.SC_NOT_FOUND,
							"Failed to get bundle resource: " + reqURI,
							"The bundle which registered this URL unexpectedly failed to deliver the resouce contents "
									+ "A probable cause is that the bundle is about to stop or simple cannot deliver any contents due "
									+ "to some internal problems.", true);
			return;
		}
		String mimeType = getMimeType(reqURI);
		response.setContentType(mimeType != null ? mimeType : serviceImpl
				.getHttpServer().getMimeType(reqURI));
		response.setBody(is);
		is.close();
	}

	public InputStream getResourceAsStream(String res) {
		try {
			URL url = getResource(res);
			return (url != null) ? url.openStream() : null;
		}
		catch (Exception e) {
			log.error("getResouceAsStream() failed: ", e);
			return null;
		}
	}

	public URL getResource(final String resource) {
		if (closed)
			return null;
		try {
			final String name = this.name;
			return (URL) AccessController.doPrivileged(
					new PrivilegedExceptionAction() {
						public Object run() throws Exception {
							return httpContext.getResource(name
									+ resource.substring(alias.length()));
						}
					}, acc);
		}
		catch (PrivilegedActionException pae) {
			log.error("getResouce() failed: ", pae.getException());
			return null;
		}
		catch (Exception e) {
			log.error("getResouce() failed: ", e);
			return null;
		}
	}
}
