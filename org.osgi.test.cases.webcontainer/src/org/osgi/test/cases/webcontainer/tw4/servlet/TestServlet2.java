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
package org.osgi.test.cases.webcontainer.tw4.servlet;

import static org.osgi.test.cases.webcontainer.util.ConstantsUtil.TW4BASIC;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @version $Rev$ $Date$
 * 
 *          Servlet implementation class TestServlet2
 */
public class TestServlet2 extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public TestServlet2() {
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

    private void printContext(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
		out.println(TW4BASIC);
        out.println("<body>");
        out.println(createContent(request, response));
        out.println("</body>");
        out.println("</html>");
    }

    private String createContent(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        String content = "";
        String tc = request.getParameter("tc");
        if (tc == null) {
            return "no test case is specified";
        } else if (tc.equals("1")) {
            return "param1: " + getParameter(request, "param1")
                    + "<br/> param2: " + getParameter(request, "param2")
                    + "<br/>";
        } else if (tc.equals("2")) {
            return "param1: " + getParameter(request, "param1") + "<br/>"
                    + "param2: " + getParameter(request, "param2") + "<br/>"
                    + "param3: " + getParameter(request, "param3") + "<br/>"
                    + "param4: " + getParameter(request, "param4") + "<br/>"
                    + "param5: " + getParameter(request, "param5") + "<br/>";
        }
        return content;
    }

    private String getParameter(HttpServletRequest request, String p) {
        return request.getParameter(p) == null ? "" : request.getParameter(p);

    }

}
