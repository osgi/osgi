/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT.
 */
package org.osgi.test.director;

import java.io.InputStream;
import java.net.*;
import java.util.*;
import org.osgi.framework.*;
import org.osgi.test.script.Tag;
import org.osgi.test.service.TestCase;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Track the testcases in the service registry.
 */
class TestCaseTracker extends ServiceTracker {
	Handler		handler;
	Hashtable	testcases	= new Hashtable();
	Hashtable	references	= new Hashtable();

	TestCaseTracker(Handler handler, BundleContext context) {
		super(context, TestCase.class.getName(), null);
		this.handler = handler;
	}

	/**
	 * A new testcase discovered, add it to the list and update the UI.
	 */
	public Object addingService(ServiceReference reference) {
		TestCase tc = (TestCase) super.addingService(reference);
		testcases.put(tc.getName(), tc);
		references.put(tc, reference);
		handler.applet.casesChanged();
		return tc;
	}

	/**
	 * Testcase gone, update the UI.
	 */
	public void removedService(ServiceReference reference, Object tc) {
		testcases.remove(((TestCase) tc).getName());
		references.remove(tc);
		super.removedService(reference, tc);
		handler.applet.casesChanged();
	}

	/**
	 * Convenience method to create an HTML description of a testcase. Uses the
	 * bundle manifest to find many details of the testcase.
	 */
	String getDescription(ServiceReference ref) {
		Bundle b = ref.getBundle();
		StringBuffer result = new StringBuffer();
		append(result, "", (String) b.getHeaders().get("bundle-description"),
				"");
		result.append("<br><small><pre><br><br>");
		append(result, "<em>Version</em>   		  ", (String) b.getHeaders().get(
				"bundle-version"), "<br>");
		append(result, "<em>Copyright</em> 		  ", (String) b.getHeaders().get(
				"bundle-copyright"), "<br>");
		append(result, "<em>Vendor</em>			  ", (String) b.getHeaders().get(
				"bundle-vendor"), "<br>");
		String docurl = (String) b.getHeaders().get("bundle-docurl");
		append(result, "<em>Documentation</em> 	  <a href='", docurl, "'>"
				+ docurl + "</a><br>");
		append(result, "<em>Contact address</em>   ", (String) b.getHeaders()
				.get("bundle-contactaddress"), "<br>");
		append(result, "<em>Location</em>  		  ", (String) b.getLocation(),
				"<br>");
		result.append("</pre></small>");
		String r = result.toString();
		return r;
	}

	/**
	 * Answer a detailed Tag based description of a testcase.
	 * 
	 * We build an XML structur of all the information we can find about the
	 * testcase's bundle.
	 */
	Tag getDescriptionTag(TestCase tc) {
		ServiceReference ref = (ServiceReference) references.get(tc);
		if (ref == null)
			return new Tag("testbundle");
		Bundle bundle = ref.getBundle();
		Dictionary d = bundle.getHeaders();
		Tag tag = new Tag("testbundle");
		tag.addAttribute("version", "" + d.get(Constants.BUNDLE_VERSION));
		tag.addAttribute("location", bundle.getLocation());
		tag.addAttribute("name", "" + d.get(Constants.BUNDLE_NAME));
		tag.addContent("" + d.get(Constants.BUNDLE_DESCRIPTION));
		try {
			URL url = new URL(bundle.getLocation());
			InputStream in = url.openStream();
			java.security.MessageDigest md = null;
			try {
				md = java.security.MessageDigest.getInstance("MD5");
			}
			catch (Exception e) {
			}
			if (md != null) {
				byte buffer[] = new byte[1024];
				int size = in.read(buffer);
				while (size > 0) {
					md.update(buffer, 0, size);
					size = in.read(buffer);
				}
				in.close();
				byte[] hash = md.digest();
				StringBuffer sb = new StringBuffer();
				String del = "";
				for (int i = 0; i < hash.length; i++) {
					sb.append(del);
					int v = 0x00FF & hash[i];
					if (v < 16)
						sb.append('0');
					sb.append(Integer.toHexString(v).toUpperCase());
					del = "-";
				}
				tag.addAttribute("md5", sb.toString());
			}
		}
		catch (MalformedURLException e) {
			System.out
					.println("Bundle location is not a url, no MD5 digest possible, "
							+ bundle.getLocation());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return tag;
	}

	void append(StringBuffer sb, String before, String middle, String after) {
		if (middle == null)
			return;
		if (middle.trim().length() == 0)
			return;
		sb.append(before);
		sb.append(middle);
		sb.append(after);
	}

	TestCase getTestCase(String name) {
		return (TestCase) testcases.get(name);
	}
}
