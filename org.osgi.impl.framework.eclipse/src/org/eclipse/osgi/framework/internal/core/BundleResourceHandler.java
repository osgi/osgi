/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.framework.internal.core;

import java.io.IOException;
import java.net.*;
import org.eclipse.osgi.framework.adaptor.BundleClassLoader;
import org.eclipse.osgi.framework.adaptor.core.*;
import org.eclipse.osgi.framework.internal.core.AbstractBundle;
import org.osgi.framework.AdminPermission;
import org.osgi.framework.BundleContext;

/**
 * URLStreamHandler the bundleentry and bundleresource protocols.
 */

public abstract class BundleResourceHandler extends URLStreamHandler {
	public static final String SECURITY_AUTHORIZED = "SECURITY_AUTHORIZED"; //$NON-NLS-1$
	protected static BundleContext context;
	protected BundleEntry bundleEntry;

	/** Single object for permission checks */
	protected AdminPermission adminPermission;

	/**
	 * Constructor for a bundle protocol resource URLStreamHandler.
	 */
	public BundleResourceHandler() {
		this(null);
	}

	public BundleResourceHandler(BundleEntry bundleEntry) {
		this.bundleEntry = bundleEntry;
	}

	/** 
	 * Parse reference URL. 
	 */
	protected void parseURL(URL url, String str, int start, int end) {
		// Check the permission of the caller to see if they
		// are allowed access to the resource.
		checkAdminPermission();
		if (end < start) 
			return;
		if (url.getPath() != null)
			// A call to a URL constructor has been made that uses an authorized URL as its context.
			// Null out bundleEntry because it will not be valid for the new path
			bundleEntry = null;
		String spec = ""; //$NON-NLS-1$
		if (start < end)
			spec = str.substring(start, end);
		end -= start;
		//Default is to use path and bundleId from context
		String path = url.getPath();
		String bundleId = url.getHost();
		int resIndex = 0; // must start at 0 index if using a context
		int pathIdx = 0;
		if (spec.startsWith("//")) { //$NON-NLS-1$
			int bundleIdIdx = 2;
			pathIdx = spec.indexOf('/', bundleIdIdx);
			if (pathIdx == -1) {
				pathIdx = end;
				// Use default
				path = ""; //$NON-NLS-1$
			}
			int bundleIdEnd = spec.indexOf(':', bundleIdIdx);
			if (bundleIdEnd > pathIdx || bundleIdEnd == -1)
				bundleIdEnd = pathIdx;
			if (bundleIdEnd < pathIdx - 1)
				try {
					resIndex = Integer.parseInt(spec.substring(bundleIdEnd + 1, pathIdx));
				} catch (NumberFormatException e) {
					// do nothing; results in resIndex == 0
				}
			bundleId = spec.substring(bundleIdIdx, bundleIdEnd);
		}
		if (pathIdx < end && spec.charAt(pathIdx) == '/')
			path = spec.substring(pathIdx, end);
		else if (end > pathIdx) {
			if (path == null || path.equals("")) //$NON-NLS-1$
				path = "/"; //$NON-NLS-1$
			int last = path.lastIndexOf('/') + 1;
			if (last == 0)
				path = spec.substring(pathIdx, end);
			else
				path = path.substring(0, last) + spec.substring(pathIdx, end);
		}
		if (path == null)
			path = ""; //$NON-NLS-1$
		//modify path if there's any relative references
		int dotIndex;
		while ((dotIndex = path.indexOf("/./")) >= 0) //$NON-NLS-1$
			path = path.substring(0, dotIndex + 1) + path.substring(dotIndex + 3);
		if (path.endsWith("/.")) //$NON-NLS-1$
			path = path.substring(0, path.length() - 1);
		while ((dotIndex = path.indexOf("/../")) >= 0) { //$NON-NLS-1$
			if (dotIndex != 0)
				path = path.substring(0, path.lastIndexOf('/', dotIndex - 1)) + path.substring(dotIndex + 3);
			else
				path = path.substring(dotIndex + 3);
		}
		if (path.endsWith("/..") && path.length() > 3) //$NON-NLS-1$
			path = path.substring(0, path.length() - 2);

		// Setting the authority portion of the URL to SECURITY_ATHORIZED
		// ensures that this URL was created by using this parseURL
		// method.  The openConnection method will only open URLs
		// that have the authority set to this.
		setURL(url, url.getProtocol(), bundleId, resIndex, SECURITY_AUTHORIZED, null, path, null, null);
	}

	/**
	 * Establishes a connection to the resource specified by <code>URL</code>.
	 * Since different protocols may have unique ways of connecting, it must be
	 * overridden by the subclass.
	 *
	 * @return java.net.URLConnection
	 * @param url java.net.URL
	 *
	 * @exception	IOException 	thrown if an IO error occurs during connection establishment
	 */
	protected URLConnection openConnection(URL url) throws IOException {
		// check to make sure that this URL was created using the
		// parseURL method.  This ensures the security check was done
		// at URL construction.
		if (!url.getAuthority().equals(SECURITY_AUTHORIZED)) {
			// No admin security check was made better check now.
			checkAdminPermission();
		}

		if (bundleEntry != null)
			return (new BundleURLConnection(url, bundleEntry));

		String bidString = url.getHost();
		if (bidString == null) {
			throw new IOException(AdaptorMsg.formatter.getString("URL_NO_BUNDLE_ID", url.toExternalForm())); //$NON-NLS-1$
		}
		AbstractBundle bundle = null;
		try {
			Long bundleID = new Long(bidString);
			bundle = (AbstractBundle) context.getBundle(bundleID.longValue());
		} catch (NumberFormatException nfe) {
			throw new MalformedURLException(AdaptorMsg.formatter.getString("URL_INVALID_BUNDLE_ID", bidString)); //$NON-NLS-1$
		}

		if (bundle == null) {
			throw new IOException(AdaptorMsg.formatter.getString("URL_NO_BUNDLE_FOUND", url.toExternalForm())); //$NON-NLS-1$
		}
		return (new BundleURLConnection(url, findBundleEntry(url, bundle)));
	}

	/**
	 * Finds the bundle entry for this protocal.  This is handled
	 * differently for Bundle.gerResource() and Bundle.getEntry()
	 * because getResource uses the bundle classloader and getEntry
	 * only used the base bundle file.
	 * @param url The URL to find the BundleEntry for.
	 * @return the bundle entry
	 */
	abstract protected BundleEntry findBundleEntry(URL url, AbstractBundle bundle) throws IOException;

	/**
	 * Converts a bundle URL to a String.
	 *
	 * @param   url   the URL.
	 * @return  a string representation of the URL.
	 */
	protected String toExternalForm(URL url) {
		StringBuffer result = new StringBuffer(url.getProtocol());
		result.append("://"); //$NON-NLS-1$

		String bundleId = url.getHost();
		if ((bundleId != null) && (bundleId.length() > 0))
			result.append(bundleId);
		int index = url.getPort();
		if (index > 0)
			result.append(':').append(index);

		String path = url.getPath();
		if (path != null) {
			if ((path.length() > 0) && (path.charAt(0) != '/')) /* if name doesn't have a leading slash */
			{
				result.append("/"); //$NON-NLS-1$
			}

			result.append(path);
		}

		return (result.toString());
	}

	public static void setContext(BundleContext context) {
		BundleResourceHandler.context = context;
	}

	protected int hashCode(URL url) {
		int hash = 0;
		String protocol = url.getProtocol();
		if (protocol != null)
			hash += protocol.hashCode();

		String host = url.getHost();
		if (host != null)
			hash += host.hashCode();

		String path = url.getPath();
		if (path != null)
			hash += path.hashCode();
		return hash;
	}

	protected boolean equals(URL url1, URL url2) {
		return sameFile(url1, url2);
	}

	protected synchronized InetAddress getHostAddress(URL url) {
		return null;
	}

	protected boolean hostsEqual(URL url1, URL url2) {
		String host1 = url1.getHost();
		String host2 = url2.getHost();
		if (host1 != null && host2 != null)
			return host1.equalsIgnoreCase(host2);
		return (host1 == null && host2 == null);
	}

	protected boolean sameFile(URL url1, URL url2) {
		String p1 = url1.getProtocol();
		String p2 = url2.getProtocol();
		if (!((p1 == p2) || (p1 != null && p1.equalsIgnoreCase(p2))))
			return false;

		if (!hostsEqual(url1, url2))
			return false;

		if (url1.getPort() != url2.getPort())
			return false;

		String a1 = url1.getAuthority();
		String a2 = url2.getAuthority();
		if (!((a1 == a2) || (a1 != null && a1.equals(a2))))
			return false;

		String path1 = url1.getPath();
		String path2 = url2.getPath();
		if (!((path1 == path2) || (path1 != null && path1.equals(path2))))
			return false;

		return true;
	}

	protected void checkAdminPermission() {
		SecurityManager sm = System.getSecurityManager();

		if (sm != null) {
			if (adminPermission == null) {
				adminPermission = new AdminPermission();
			}

			sm.checkPermission(adminPermission);
		}
	}

	protected static BundleClassLoader getBundleClassLoader(AbstractBundle bundle) {
		BundleLoader loader = bundle.getBundleLoader();
		if (loader == null)
			return null;
		return loader.createClassLoader();
	}
}