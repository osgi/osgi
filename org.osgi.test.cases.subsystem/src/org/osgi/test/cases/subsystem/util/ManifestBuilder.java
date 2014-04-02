package org.osgi.test.cases.subsystem.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class ManifestBuilder {
	private final Manifest manifest;
	
	public ManifestBuilder() {
		manifest = new Manifest();
		manifest.getMainAttributes().putValue(Attributes.Name.MANIFEST_VERSION.toString(), "1.0");
	}
	
	public byte[] build() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			manifest.write(baos);
			return baos.toByteArray();
		}
		finally {
			baos.close();
		}
	}
	
	public ManifestBuilder header(String name, String value) {
		manifest.getMainAttributes().putValue(name, value);
		return this;
	}
}
