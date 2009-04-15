package org.osgi.test.cases.upnp.tbc.device.description;

import java.io.IOException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.http.HttpContext;

/**
 * 
 * 
 */
public class DServletContext implements HttpContext {
	public String getMimeType(String mime) {
		return null;
	}

	public URL getResource(String str) {
		return null;
	}

	public boolean handleSecurity(HttpServletRequest req,
			HttpServletResponse res) throws IOException {
		return true;
	}
}