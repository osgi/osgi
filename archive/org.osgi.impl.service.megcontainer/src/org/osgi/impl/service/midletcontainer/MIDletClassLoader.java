package org.osgi.impl.service.midletcontainer;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.ProtectionDomain;
import org.osgi.framework.Bundle;

class MIDletClassLoader extends ClassLoader {
	private Bundle				bundle;
	private ProtectionDomain	protectionDomain;

	public MIDletClassLoader(ClassLoader parent, Bundle bundle,
			ProtectionDomain protectionDomain) {
		super(parent);
		this.bundle = bundle;
		this.protectionDomain = protectionDomain;
	}

	protected Class findClass(String name) throws ClassNotFoundException {
		return bundle.loadClass( name );
//		byte b[] = loadClassData(name);
//		return defineClass(name, b, 0, b.length, protectionDomain);
	}

	private byte[] loadClassData(String name) throws ClassNotFoundException {
		try {
			byte data[];
			String classFile = name.replace('.', '/') + ".class";
			URL url = bundle.getResource(classFile);
			if (url == null)
				throw new ClassNotFoundException();
			URLConnection connection = url.openConnection();
			int length = connection.getContentLength();
			data = new byte[length];
			InputStream input = connection.getInputStream();
			try {
				input.read(data);
			}
			finally {
				input.close();
			}
			return data;
		}
		catch (Exception e) {
			throw new ClassNotFoundException("Cannot load the required class!",
					e);
		}
	}
}