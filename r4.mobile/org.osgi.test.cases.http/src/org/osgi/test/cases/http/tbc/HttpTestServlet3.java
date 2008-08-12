/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Telia Research AB. 2000.
 * This source code is owned by Telia Research AB,
 * and is being distributed to OSGi MEMBERS as
 * MEMBER LICENSED MATERIALS under the terms of section 3.2 of
 * the OSGi MEMBER AGREEMENT. 
 */
package org.osgi.test.cases.http.tbc;

import java.io.*;
import javax.servlet.*;

public class HttpTestServlet3 implements Servlet {
	private ServletConfig		config;
	private static final String	pagePart1	= "<html><head><title>OSGi - HTTP Service test case</title></head><body><h1>";
	private static final String	pagePart2	= "</h1></body></html>";

	public void init(ServletConfig config) throws ServletException {
		this.config = config;
	}

	public void destroy() {
	} // do nothing

	public ServletConfig getServletConfig() {
		return config;
	}

	public String getServletInfo() {
		return "TestCase 9 Servlet";
	}

	public void service(ServletRequest req, ServletResponse res)
			throws ServletException, IOException {
		res.setContentType("text/html");
		PrintWriter out = res.getWriter();
		out.println(pagePart1 + "9 javax.servlet.Servlet registration"
				+ pagePart2);
		out.close();
	}
}
