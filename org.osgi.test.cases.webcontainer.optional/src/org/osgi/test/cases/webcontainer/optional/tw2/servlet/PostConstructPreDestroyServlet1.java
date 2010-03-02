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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.test.cases.webcontainer.util.ConstantsUtil;
import org.osgi.test.cases.webcontainer.optional.util.Event;
import org.osgi.test.cases.webcontainer.optional.util.EventLogger;

/**
 * @version $Rev$ $Date$
 * 
 *          Servlet implementation class BasicAnnotationServlet
 */
public class PostConstructPreDestroyServlet1 extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public PostConstructPreDestroyServlet1() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        printContext(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        printContext(request, response);
    }

    @PostConstruct
    public void postConstruct() {
        EventLogger.logEvent(new Event(this.getClass().getName(),
                ConstantsUtil.POSTCONSTRUCT, ConstantsUtil.POSTCONSTRUCTDESP));
    }

    @PreDestroy
    public void cleanup() {
        EventLogger.logEvent(new Event(this.getClass().getName(), "cleanup",
                ConstantsUtil.CLEANUPDESP));
    }

    private void printContext(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head>");
        out.println("<title>PostConstructPreDestroyServlet1</title>");
        out.println("</head>");
        out.println("<body>");
        out.println(EventLogger.printEvent(new Event(this.getClass().getName(),
                "printContext", ConstantsUtil.PRINTCONTEXT)));
        out.println("</body>");
        out.println("</html>");
    }

}
