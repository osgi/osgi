/**
 * Copyright (c) 2001 Gatespace AB. All Rights Reserved.
 * 
 * Gatespace grants the OSGi Alliance an irrevocable,
 * perpetual, non-exclusive, worldwide, paid-up right and license to
 * reproduce, display, perform, prepare and have prepared derivative works
 * based upon and distribute and sublicense this material and derivative
 * works thereof as set out in the OSGi MEMBER AGREEMENT as of January 24
 * 2000, for use in accordance with Section 2.2 of the BY-LAWS of the
 * OSGi MEMBER AGREEMENT.
 */
package org.osgi.impl.framework;

import java.io.*;
import java.net.*;
import java.security.*;

/**
 * Bundle URL handling.
 * 
 * @author Jan Stein
 * @version $Revision$
 */
public class BundleURLStreamHandler extends URLStreamHandler {
	final public static String	PROTOCOL	= "bundle";
	private BundleImpl			bundle;

	BundleURLStreamHandler(BundleImpl b) {
		bundle = b;
	}

	public URLConnection openConnection(URL u) {
		return new BundleURLConnection(u);
	}

	class BundleURLConnection extends URLConnection {
		private InputStream	is	= null;

		BundleURLConnection(URL u) {
			super(u);
		}

		public void connect() throws IOException {
			if (!connected) {
				try {
					final URL u = url;
					is = (InputStream) AccessController
							.doPrivileged(new PrivilegedExceptionAction() {
								public Object run() throws IOException {
									String pkg = u.getFile();
									int pos = pkg.lastIndexOf('/');
									if (pos != -1) {
										pkg = pkg.substring(0, pos).replace(
												'/', '.');
									}
									else {
										pkg = null;
									}
									BundleClassLoader cl = bundle
											.getExporterClassLoader(pkg);
									if (cl != null) {
										return cl.getInputStream(u);
									}
									else {
										throw new IOException(
												"No classloader available");
									}
								}
							});
				}
				catch (PrivilegedActionException e) {
					throw (IOException) e.getException();
				}
				connected = true;
			}
		}

		public InputStream getInputStream() {
			try {
				connect();
			}
			catch (IOException ignore) {
			}
			return is;
		}
	}
}
