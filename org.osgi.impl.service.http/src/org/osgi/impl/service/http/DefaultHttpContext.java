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

import java.net.URL;
import javax.servlet.http.*;
import org.osgi.framework.Bundle;
import org.osgi.service.http.HttpContext;

//  ******************** DefaultHttpContext ********************
/**
 * * Default implementation of the HttpContext interface. An instance of * this
 * class will be returned by the createDefaultHttpContext method * in the
 * HttpService interface. * *
 * 
 * @author Gatespace AB (osgiref@gatespace.com) *
 * @version $Revision$
 */
public final class DefaultHttpContext implements HttpContext {
	private Log		log	= new Log("DefaultHttpContext");
	private Bundle	bundle;

	public DefaultHttpContext(Bundle bundle) {
		this.bundle = bundle;
	}

	//----------------------------------------------------------------------------
	//			IMPLEMENTS - HttpContext
	//----------------------------------------------------------------------------
	//  ==================== getMimeType ====================
	/**
	 * * Implements getMimeType() *
	 * 
	 * @see HttpContext#getMimeType
	 */
	public String getMimeType(String uri) {
		return null;
	}

	//  ==================== getResource ====================
	/**
	 * * Implements getResource() *
	 * 
	 * @see HttpContext#getResource
	 */
	public URL getResource(String name) {
		return bundle.getResource(name);
	}

	//  ==================== handleSecurity ====================
	/**
	 * * Implements handleSecurity() *
	 * 
	 * @see HttpContext#handleSecurity
	 */
	public boolean handleSecurity(HttpServletRequest request,
			HttpServletResponse response) {
		return true;
	}
} // DefaultHttpContext
