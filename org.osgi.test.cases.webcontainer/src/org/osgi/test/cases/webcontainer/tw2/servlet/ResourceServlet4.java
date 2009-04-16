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

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
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
