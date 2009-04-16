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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @version $Rev$ $Date$
 *
 * Servlet implementation class ResourceServlet4
 */
public class ResourceServlet4 extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String myS1;
	private String myS2;
	private Integer myI1;
	private Integer myI2;
	private Integer myI3;
	private Boolean myB1;
	
	@Resource(name="someString1")
	private void setMyS1(String s1) {
	    this.myS1 = s1;
	}
	
	@Resource(name="someString2")
    private void setMyS2(String s2) {
        this.myS2 = s2;
    }
	
    @Resource(name="someInteger1")
    private void setMyI1(Integer i1) {
        this.myI1 = i1;
    }
    
    @Resource(name="someInteger2")
    private void setMyI2(Integer i2) {
        this.myI2 = i2;
    }
    
    @Resource(name="someInteger3")
    private void setMyI3(Integer i3) {
        this.myI3 = i3;
    }
       
    @Resource(name="someBoolean1")
    private void setMyB1(Boolean b1) {
        this.myB1 = b1;
    }
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ResourceServlet4() {
        super();
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
	    printContext(request, response);
	}
	
    private void printContext(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head>");
        out.println("<title>ResourceServlet4</title>");
        out.println("</head>");
        out.println("<body>");
        out.println(this.myS1 + " " + this.myS2 + "<br/>");
        out.println(this.myI1 + " + " + this.myI2 + " = " + this.myI3 + " that is " + this.myB1);
        out.println("</body>");
        out.println("</html>");
    }

}
