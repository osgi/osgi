/*
 * $Id$
 *
 * Copyright (c) The OSGi Alliance (2009). All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.test.cases.webcontainer.tw2.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.test.cases.webcontainer.util.Constants;
import org.osgi.test.cases.webcontainer.util.Event;
import org.osgi.test.cases.webcontainer.util.EventLogger;

/**
 * Servlet implementation class ServletContextListenerServlet
 */
public class ServletContextListenerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Resource(name = "someString1")
    private String someString1;

    @Resource(name = "someString2")
    private String someString2;

    @Resource(name = "someInteger1")
    private Integer someInteger1;

    @Resource(name = "someInteger2")
    private Integer someInteger2;

    @Resource(name = "someInteger3")
    private Integer someInteger3;

    @Resource(name = "someBoolean1")
    private Boolean someBoolean1;
    
    @Resource(name = "someBoolean2")
    private Boolean someBoolean2;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletContextListenerServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    @PostConstruct
    public void postConstruct() {
        EventLogger.logEvent(new Event(this.getClass().getName(),
                Constants.POSTCONSTRUCT, ""));
    }

    @PreDestroy
    public void preDestroy() {
        EventLogger.logEvent(new Event(this.getClass().getName(),
                Constants.PREDESTROY, ""));
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        printContext(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
    }

    private void printContext(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        String modify = request.getParameter("modifyAttribute");
        if (modify != null && modify.equalsIgnoreCase("true")) {
            // let's modify the attributes
            getServletContext().removeAttribute(Constants.WELCOMESTRING);
            getServletContext().setAttribute(
                    Constants.WELCOMESTATEMENT,
                    someInteger1 + "+" + someInteger2 + "=" + someInteger3
                            + " is not " + someBoolean2);

        } else if (modify != null && modify.equalsIgnoreCase("reset")) {
            // let's set the attributes to their original value
            getServletContext().setAttribute(Constants.WELCOMESTRING,
                    someString1 + " " + someString2);
            getServletContext().setAttribute(
                    Constants.WELCOMESTATEMENT,
                    someInteger1 + "+" + someInteger2 + "=" + someInteger3
                            + " is " + someBoolean1);

        }
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>ServletContextListenerServlet</title>");
        out.println("</head>");
        out.println("<body>");
        out.println(Constants.EMAIL + "-"
                + getServletContext().getAttribute(Constants.EMAIL) + "<br/>");
        out.println(Constants.WELCOMESTRING + "-"
                + getServletContext().getAttribute(Constants.WELCOMESTRING)
                + "<br/>");
        out.println(Constants.WELCOMESTATEMENT + "-"
                + getServletContext().getAttribute(Constants.WELCOMESTATEMENT)
                + "<br/>");
        out.println("</body>");
        out.println("</html>");
    }
}
