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

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.osgi.test.cases.webcontainer.util.Constants;

/**
 * @version $Rev$ $Date$
 *
 * Servlet implementation class BundleContextTestServlet
 */
public class BundleContextTestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public BundleContextTestServlet() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		printContext(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

    private void printContext(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        ServletContext sc = getServletContext();
        BundleContext bc = (BundleContext)sc.getAttribute(Constants.OSGIBUNDLECONTEXT);
        
        // let's try use the osgi log service from the bundle context
        ServiceReference logServiceReference = bc.getServiceReference(LogService.class
                .getName());
        LogService logService = (LogService) bc.getService(logServiceReference);
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>BundleContextTestServlet</title>");
        out.println("</head>");
        out.println("<body>");
        String p = request.getParameter("log");
        if (p == null || p.equals("1")) {
            logService.log(logService.LOG_ERROR, Constants.TESTLOGMSG);
            out.println(Constants.TESTLOGMSG + "<br/>");
        } else if (p.equals("2")) {
            logService.log(logService.LOG_WARNING, Constants.TESTLOGMSG2);
            out.println(Constants.TESTLOGMSG2 + "<br/>");
        } else if (p.equals("3")) {
            logService.log(logService.LOG_INFO, Constants.TESTLOGMSG3);
            out.println(Constants.TESTLOGMSG3 + "<br/>");
        } else if (p.equals("4")) {
            logService.log(logService.LOG_DEBUG, Constants.TESTLOGMSG4, new RuntimeException());
            out.println(Constants.TESTLOGMSG4 + "<br/>");
        }
        out.println("</body>");
        out.println("</html>");
    }
}
