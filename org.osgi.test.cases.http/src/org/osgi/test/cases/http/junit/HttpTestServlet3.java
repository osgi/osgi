/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.test.cases.http.junit;

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
