package org.osgi.tools.btool;

import java.io.*;

public class BytesResource extends Resource {
	byte	content[];

	BytesResource(BTool btool, String path, byte[] content) {
		super(btool, null, path);
		this.content = content;
	}

	InputStream getInputStream() throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream(content);
		return in;
	}
}
