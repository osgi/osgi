package org.osgi.test.cases.upnp.tbc.device.description;

import java.io.*;
import java.net.*;
import javax.servlet.http.*;
import org.osgi.service.http.*;

/**
 * 
 * 
 */
public class DServletContext implements HttpContext {
	public DServletContext() {
	}

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