package org.osgi.tools.btool;

import java.io.*;
import java.util.Properties;

public class PropertyResource extends Resource {
	Properties	properties;

	PropertyResource(BTool btool, String path, Properties properties) {
		super(btool, null, path);
		this.properties = properties;
	}

	InputStream getInputStream() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		properties.store(out, "org.osgi.tools.btool property manager");
		byte[] result = out.toByteArray();
		ByteArrayInputStream in = new ByteArrayInputStream(result);
		return in;
	}
}
