/*
 * Copyright (c) IBM Corporation (2009). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osgi.test.cases.webcontainer.tw5.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.test.cases.webcontainer.simple.SimpleHello;
import org.osgi.test.cases.webcontainer.util.ConstantsUtil;

/**
 * @version $Rev$ $Date$
 * 
 *          Servlet implementation class ClasspathTestServlet
 */
public class ClasspathTestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(ClasspathTestServlet.class);
	private static final SimpleHello simple = new SimpleHello();

    /**
     * Default constructor. 
     */
    public ClasspathTestServlet() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        log.info("processing http servlet request");
        response.setContentType("text/html");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>ClasspathTestServlet</title>");
        out.println("</head>");
        out.println("<body>");
        if (log != null) {
            out.println(ConstantsUtil.ABLEGETLOG + "<br/>");
        }
        if (simple != null) {
            out.println(ConstantsUtil.ABLEGETSIMPLEHELLO + "<br/>");
        }
        out.println("</body>");
        out.println("</html>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
