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

package nursery.obr.web;

import java.io.*;
import java.net.URL;
import java.util.*;

import javax.servlet.http.*;

import org.osgi.framework.*;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.*;
import org.osgi.service.obr.*;

import aQute.lib.tag.Tag;

public class ObrServlet extends HttpServlet implements HttpContext {
	ComponentContext	context;
	RepositoryAdmin		admin;

	
	
	public void doGet(HttpServletRequest rq, HttpServletResponse rsp)
			throws IOException {
		PrintWriter pw = rsp.getWriter();
		try {
			String cmd = rq.getParameter("cmd");
			rsp.setContentType("text/xml");
			pw.println("<?xml version='1.0'?>");
			Tag tag = new Tag("result");
			tag.addAttribute("start", new Date());
			if ("repository".equals(cmd)) {
				doRepository(tag, rq.getParameter("add"));
			}
			else if ("search".equals(cmd)) {
				doSearch(tag, rq.getParameter("keywords"));
			}
			tag.addAttribute("end", new Date());
			tag.print(0, pw);
			pw.flush();
		}
		catch (Exception e) {
			pw.println("Could not handle request: ");
			e.printStackTrace(pw);
			rsp.setContentType("text/plain");
		}
	}

	private void doSearch(Tag tag, String parameter) throws InvalidSyntaxException {
		Filter filter = null;
		if (parameter != null) {
			String keywords[] = parameter.split("[^\\w]");
			StringBuffer	sb = new StringBuffer();
			sb.append("(&");
			for ( int i=0; i<keywords.length; i++ ) {
				sb.append("(|");
				sb.append("(description=*");
				sb.append(keywords[i]);
				sb.append("*)");
				sb.append("(name=*");
				sb.append(keywords[i]);
				sb.append("*)");
				sb.append("(copyright=*");
				sb.append(keywords[i]);
				sb.append("*)");
				sb.append(")");
			}
			sb.append(")");
			tag.addAttribute("filter", sb.toString());
			filter= context.getBundleContext().createFilter(sb.toString());
		}
		showResources(tag,filter);
	}

	
	
	
	private void showResources(Tag tag, Filter filter) {
		
	}

	private void doRepository(Tag tag, String add) {
		if (add != null)
			try {
				URL url = new URL(add);
				admin.addRepository(url);
			}
			catch (Exception e) {
				tag.addAttribute("error", "" + e);
			}
		showRepositories(tag);
	}

	/**
	 * 
	 * @param tag
	 */
	private void showRepositories(Tag tag) {
		Repository[] repositories = admin.listRepositories();
		for (int i = 0; i < repositories.length; i++) {
			Tag repo = new Tag("repository");
			repo.addAttribute("url", repositories[i].getURL().toString());
			repo.addAttribute("name", repositories[i].getName());
			repo.addAttribute("size", repositories[i].getResources().length);

			tag.addContent(repo);
		}
	}

	protected void activate(ComponentContext context) throws Exception {
		this.context = context;
		admin = (RepositoryAdmin) context.locateService("RA");
		// For testing
		admin.addRepository(getClass().getResource("/repository.xml"));
		HttpService http = (HttpService) context.locateService("HTTP");
		http.registerResources("/obr", "www", this);
		http.registerServlet("/obr/cgi", this, new Hashtable(), this);
	}

	public boolean handleSecurity(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		return true;
	}

	public URL getResource(String name) {
		return getClass().getResource(name);
	}

	public String getMimeType(String name) {
		return null;
	}
}
