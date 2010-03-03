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

package org.osgi.test.cases.webcontainer.optional.tw2.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.annotation.security.DeclareRoles;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.test.cases.webcontainer.optional.util.ConstantsUtil;
import org.osgi.test.cases.webcontainer.optional.util.Event;
import org.osgi.test.cases.webcontainer.optional.util.EventLogger;

/**
 * @version $Rev$ $Date$
 * 
 *          Servlet implementation class SecurityTestServlet
 */
@DeclareRoles("manager")
public class SecurityTestServlet extends HttpServlet {
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
    public SecurityTestServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    @PostConstruct
    public void postConstruct() {
        EventLogger.logEvent(new Event(this.getClass().getName(),
                ConstantsUtil.POSTCONSTRUCT, ""));
    }

    @PreDestroy
    public void preDestroy() {
        EventLogger.logEvent(new Event(this.getClass().getName(),
                ConstantsUtil.PREDESTROY, ""));
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
            getServletContext().removeAttribute(ConstantsUtil.WELCOMESTRING);
            getServletContext().setAttribute(
                    ConstantsUtil.WELCOMESTATEMENT,
                    someInteger1 + "+" + someInteger2 + "=" + someInteger3
                            + " is not " + someBoolean2);

        } else if (modify != null && modify.equalsIgnoreCase("reset")) {
            // let's set the attributes to their original value
            getServletContext().setAttribute(ConstantsUtil.WELCOMESTRING,
                    someString1 + " " + someString2);
            getServletContext().setAttribute(
                    ConstantsUtil.WELCOMESTATEMENT,
                    someInteger1 + "+" + someInteger2 + "=" + someInteger3
                            + " is " + someBoolean1);

        }
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>SecurityTestServlet</title>");
        out.println("</head>");
        out.println("<body>");
        out.println(ConstantsUtil.EMAIL + "-"
                + getServletContext().getAttribute(ConstantsUtil.EMAIL) + "<br/>");
        out.println(ConstantsUtil.WELCOMESTRING + "-"
                + getServletContext().getAttribute(ConstantsUtil.WELCOMESTRING)
                + "<br/>");
        out.println(ConstantsUtil.WELCOMESTATEMENT + "-"
                + getServletContext().getAttribute(ConstantsUtil.WELCOMESTATEMENT)
                + "<br/>");
        out.println("</body>");
        out.println("</html>");
    }
}
