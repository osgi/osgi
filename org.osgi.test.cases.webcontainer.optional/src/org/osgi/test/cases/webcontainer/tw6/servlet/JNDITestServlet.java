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
package org.osgi.test.cases.webcontainer.tw6.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.log.LogService;
import org.osgi.test.cases.webcontainer.util.ConstantsUtil;

/**
 * @version $Rev$ $Date$
 * 
 *          Servlet implementation class JNDITestServlet
 */
public class JNDITestServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public JNDITestServlet() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        // let's try get the osgi log service from JNDI lookup
        InitialContext context;
        LogService logService = null;
        try {
            context = new InitialContext();
            logService = (LogService)context.lookup("osgi:service/org.osgi.service.log.LogService");
        } catch (NamingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }  

        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>JNDITestServlet</title>");
        out.println("</head>");
        out.println("<body>");
        String p = request.getParameter("log");
        if (logService != null) {
            if (p == null || p.equals("1")) {
                logService.log(logService.LOG_ERROR, ConstantsUtil.TESTLOGMSG);
                out.println(ConstantsUtil.TESTLOGMSG + "<br/>");
            } else if (p.equals("2")) {
                logService.log(logService.LOG_WARNING, ConstantsUtil.TESTLOGMSG2);
                out.println(ConstantsUtil.TESTLOGMSG2 + "<br/>");
            } else if (p.equals("3")) {
                logService.log(logService.LOG_INFO, ConstantsUtil.TESTLOGMSG3);
                out.println(ConstantsUtil.TESTLOGMSG3 + "<br/>");
            } else if (p.equals("4")) {
                logService.log(logService.LOG_DEBUG, ConstantsUtil.TESTLOGMSG4,
                        new RuntimeException());
                out.println(ConstantsUtil.TESTLOGMSG4 + "<br/>");
            }
        } else {
            out.println("unable to lookup logService via JNDI");
        }
        out.println("</body>");
        out.println("</html>");
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
    }
}
