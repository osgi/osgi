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

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

//  ******************** RequestDispatcher ********************
/**
 * * The RequestDispatcher allows for forwarding of requests to other URIs * as
 * well as including responses from other URIs. Another URI is in this * case
 * any other resource or servlet registered in the HttpService. *
 * <p>* This implementation is not a 100% correct and needs further
 * improvements. * The request,response objects needs to be wrapped before
 * sending them onto * the next URI. With the current implementation errors
 * caused by another * resource, e.g faulty output, will appear in the resulting
 * response. * *
 * 
 * @author Gatespace AB (osgiref@gatespace.com) *
 * @version $Revision$
 */
public final class RequestDispatcherImpl implements RequestDispatcher {
	private AliasMatch	match;

	RequestDispatcherImpl(AliasMatch match) {
		this.match = match;
	}

	//----------------------------------------------------------------------------
	//		IMPLEMENTS - RequestDispatcher
	//----------------------------------------------------------------------------
	public void forward(ServletRequest request, ServletResponse response)
			throws ServletException, java.io.IOException {
		if (!(request instanceof HttpRequest)
				|| !(response instanceof HttpResponse))
			throw new ServletException(
					"Unrecognized request/response objects, cannot forward request");
		HttpResponse httpResponse = (HttpResponse) response;
		if (httpResponse.isAnyWriterCalled())
			throw new IllegalStateException(
					"Cannot forward request after getWriter()/getOutputStream() has been called");
		match.aliasReg
				.handleRequest((HttpServletRequest) request, httpResponse);
	}

	public void include(ServletRequest request, ServletResponse response)
			throws ServletException, java.io.IOException {
		if (!(request instanceof HttpServletRequest)
				|| !(response instanceof HttpResponse))
			throw new ServletException("Request and response must be HTTP");
		match.aliasReg.handleRequest((HttpServletRequest) request,
				(HttpResponse) response);
	}
}
