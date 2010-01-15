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

package org.osgi.test.cases.webcontainer.tw2.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.annotation.Resource;
import javax.annotation.Resources;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * @version $Rev$ $Date$
 */
@Resources( {
        @Resource(name = "someDataSource1", type = javax.sql.DataSource.class),
        @Resource(name = "someDataSource2", type = javax.sql.DataSource.class) })
public class ResourceServlet3 extends HttpServlet implements Servlet {

    private static final long serialVersionUID = 1L;
    @Resource(name = "someDataSource1")
    private DataSource ds1;

    @Resource(name = "someDataSource2")
    private DataSource ds2;

    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = null;
        response.setContentType("text/html");
        out = response.getWriter();
        out.println("<html><head><title>ResourceServlet3</title></head><body>");
        out.println("<p>Printing the injections in this ResourceServlet3 ...</p>");
        if (ds1 != null && ds2 != null) {
            out.println(ds1.toString());
            out.println(ds2.toString());
        } else {
            out.println("Error - unable to find name via @Resource");
        }
        out.println("Done!");
        out.println("</body></html>");
        out.close();
    }
}
