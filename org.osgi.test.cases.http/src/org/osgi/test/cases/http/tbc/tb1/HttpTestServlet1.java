/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Telia Research AB. 2000.
 * This source code is owned by Telia Research AB,
 * and is being distributed to OSGi MEMBERS as
 * MEMBER LICENSED MATERIALS under the terms of section 3.2 of
 * the OSGi MEMBER AGREEMENT. 
 */
package org.osgi.test.cases.http.tbc.tb1;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class HttpTestServlet1 extends javax.servlet.http.HttpServlet {
	private static final String	pagePart1	= "<html><head><title>OSGi - HTTP Service test case</title></head>";
	private static final String	pagePart2	= "<body><h1>5 Uninstallation of source bundle</h1></body></html>";

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println(pagePart1 + pagePart2);
	}
}
