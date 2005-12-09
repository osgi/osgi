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
import javax.xml.transform.*;
import javax.xml.transform.stream.*;

import nursery.obr.resource.*;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.*;
import org.osgi.service.obr.*;

import aQute.lib.tag.Tag;

public class ObrServlet extends HttpServlet implements HttpContext, URIResolver {
	ComponentContext	context;
	RepositoryAdmin		admin;

	public void doGet(HttpServletRequest rq, HttpServletResponse rsp)
			throws IOException {
		boolean xml = rq.getParameter("xml") != null;
		PrintWriter pw;
		ByteArrayOutputStream bout = null;
		if (xml) {
			pw = rsp.getWriter();
		}
		else {
			bout = new ByteArrayOutputStream();
			pw = new PrintWriter(new OutputStreamWriter(bout, "utf-8"));
		}

		try {
			String cmd = rq.getParameter("cmd");
			rsp.setContentType(xml ? "text/xml" : "text/html");
			if (cmd == null || cmd.equals(""))
				cmd = "repository";

			pw.println("<?xml version='1.0'?>");
			pw.println("<?xml-stylesheet type='text/xsl' title='Compact' "
					+ " href='" + cmd + ".xsl'?>");
			Tag tag = new Tag("result");
			tag.addAttribute("start", new Date());
			if ("repository".equals(cmd)) {
				doRepository(tag, rq.getParameter("add"));
			}
			else if ("search".equals(cmd)) {
				doSearch(tag, rq.getParameter("keywords"));
			}
			else if ("resolve".equals(cmd)) {
				doResolve(tag, rq.getParameterValues("in"));
			}
			else if ("resource".equals(cmd)) {
				doResource(tag, rq.getParameter("id"));
			}
			tag.addAttribute("end", new Date());
			tag.print(0, pw);
			pw.flush();

			if (!xml) {
				// TODO use templates and cache these, this
				// is horribly inefficient.
				ByteArrayInputStream bin = new ByteArrayInputStream(bout
						.toByteArray());
				Source data = new StreamSource(bin);
				Source style = new StreamSource(getClass().getResource(
						"www/" + cmd + ".xsl").openStream());
				Result output = new StreamResult(rsp.getOutputStream());

				// create Transformer and perform the tranfomation
				TransformerFactory fact = TransformerFactory.newInstance();
				fact.setURIResolver(this);
				Transformer xslt = fact.newTransformer(style);
				xslt.transform(data, output);
			}
		}
		catch (Throwable e) {
			pw.println("Could not handle request: ");
			e.printStackTrace(pw);
			rsp.setContentType("text/plain");
		}

	}

	private void doResource(Tag tag, String id) {
		Resource resource = admin.getResourceById(id);
		tag.addContent(ResourceImpl.toXML(resource));
	}

	private void doResolve(Tag tag, String[] in) {
		Resolver resolver = admin.resolver();
		for (int i = 0; in != null && i < in.length; i++) {
			Resource resource = admin.getResourceById(in[i]);
			resolver.add(resource);
		}

		if (!resolver.resolve()) {
			tag.addAttribute("error", "Can not resolve");
		}

		tag.addContent(toXML(resolver));
	}

	static Tag toXML(Resolver resolver) {
		Tag top = new Tag("resolver");
		Resource addedResource[] = resolver.getAddedResources();
		Tag added = new Tag("added");
		top.addContent(added);
		for (int i = 0; i < addedResource.length; i++) {
			Tag resource = ResourceImpl.toXML(addedResource[i]);
			added.addContent(resource);
		}
		Resource optionalResources[] = resolver.getOptionalResources();
		Tag optional = new Tag("optional");
		top.addContent(optional);
		for (int i = 0; i < optionalResources.length; i++)
			optional.addContent(ResourceImpl.toXML(optionalResources[i]));

		Resource requiredResources[] = resolver.getRequiredResources();
		Tag required = new Tag("required");
		top.addContent(required);
		for (int i = 0; i < requiredResources.length; i++) {
			Tag resource = ResourceImpl.toXML(requiredResources[i]);
			Requirement[] reason = resolver.getReason(requiredResources[i]);
			for (int r = 0; r < reason.length; r++) {
				Tag rq = RequirementImpl.toXML(reason[r]);
				rq.rename("reason");
				resource.addContent(rq);
			}
			required.addContent(resource);
		}

		Requirement unsatisfiedRequirements[] = resolver
				.getUnsatisfiedRequirements();
		Tag unsatisfied = new Tag("unsatisfied");
		top.addContent(unsatisfied);
		for (int i = 0; i < unsatisfiedRequirements.length; i++)
			unsatisfied.addContent(RequirementImpl
					.toXML(unsatisfiedRequirements[i]));

		return top;
	}

	private void doSearch(Tag tag, String parameter)
			throws InvalidSyntaxException {
		tag.addAttribute("keywords", parameter);
		String filter = null;
		if (parameter != null) {
			String keywords[] = parameter.split("[^\\w]");
			StringBuffer sb = new StringBuffer();
			sb.append("(&");
			for (int i = 0; i < keywords.length; i++) {
				sb.append("(|");
				sb.append("(description~=*");
				sb.append(keywords[i]);
				sb.append("*)");
				sb.append("(name~=*");
				sb.append(keywords[i]);
				sb.append("*)");
				sb.append("(copyright~=*");
				sb.append(keywords[i]);
				sb.append("*)");
				sb.append("(category~=*");
				sb.append(keywords[i]);
				sb.append("*)");
				sb.append(")");
			}
			sb.append(")");
			tag.addAttribute("filter", sb.toString());
			filter = sb.toString();
		}
		showResources(tag, filter);
	}

	private void showResources(Tag tag, String filter) {
		Resource[] resources = admin.discoverResources(filter);
		for (int i = 0; resources != null && i < resources.length; i++) {
			showResource(tag, resources[i]);
		}
	}

	private void showResource(Tag tag, Resource resource) {
		tag.addContent(ResourceImpl.toXML(resource));
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

	public Source resolve(String href, String base) throws TransformerException {
		try {
			URL url = getClass().getResource("www/" + href);
			if (url == null)
				return null;
			return new StreamSource(url.openStream());
		}
		catch( Exception e ) {
			throw new TransformerException(e); // SILLY!
		}
	}
}
