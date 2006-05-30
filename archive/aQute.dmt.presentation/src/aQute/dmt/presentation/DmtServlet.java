/*
 * $Header$
 * 
 * Copyright (c) The OSGi Alliance (2005). All Rights Reserved.
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

package aQute.dmt.presentation;

import info.dmtree.*;

import java.io.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.*;

public class DmtServlet extends HttpServlet {
	ComponentContext	context;

	public void doGet(HttpServletRequest rq, HttpServletResponse rsp)
			throws IOException {
		PrintWriter pw = rsp.getWriter();
		try {
			DmtAdmin dmt = (DmtAdmin) context.locateService("DMT");
			String root = rq.getParameter("root");
			DmtSession session = dmt.getSession(root,
					DmtSession.LOCK_TYPE_SHARED);
			
			rsp.setContentType("text/xml");
			pw.println("<?xml encoding='UTF-8' version='1.0'?>");
			pw.println("<dmt>");
			printRecursive( pw, session, "");
			pw.println("</dmt>");
			pw.flush();
		}
		catch (Exception e) {
			pw.println("Could not handle request: ");
			e.printStackTrace(pw);
			rsp.setContentType("text/plain");
		}
	}

	private void printRecursive(PrintWriter pw, DmtSession session, String rover) throws DmtException {
		String [] children = session.getChildNodeNames(rover);
		for ( int i = 0; i< children.length; i++ ) {
			String child = children[i];
			
		}
		
	}

	protected void activate(ComponentContext context) throws ServletException,
			NamespaceException {
		HttpService http = (HttpService) context.locateService("HTTP");
		http.registerServlet("/dmt", this, null, null);
	}
}
