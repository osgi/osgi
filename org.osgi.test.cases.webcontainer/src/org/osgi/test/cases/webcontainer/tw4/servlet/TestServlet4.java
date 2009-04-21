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

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.test.cases.webcontainer.util.Constants;

/**
 * @version $Rev$ $Date$
 *
 * Servlet implementation class TestServlet4
 * This servlet is used to test different response content types
 */
public class TestServlet4 extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public TestServlet4() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     *      This method is based on the mime type test cases from org.osgi.test.cases.http
     */
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        String content = "";
        String type = request.getParameter("type");

        if (type == null) {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println(Constants.TW4BASIC);
            out.println("<body>");
            out.println("no type is specified");
            out.println("</body>");
            out.println("</html>");
        } else if (type.equals("plain")) {
            response.setContentType("text/plain");
            PrintWriter out = response.getWriter();
            out.println(Constants.PLAINRESPONSE);
        } else if (type.equals("html")) {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println(Constants.HTMLRESPONSE);
        } else if (type.equals("jpg")) {
            response.setContentType("image/jpeg");
            ServletOutputStream out = response.getOutputStream(); // binary
            // output
            InputStream in = getServletContext().getResourceAsStream("images/osgi_mobile.jpg");
            byte buffer[] = new byte[4096];
            int read;
            while ((read = in.read(buffer, 0, buffer.length)) != -1) {
                out.write(buffer, 0, read);
            }

        }
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
