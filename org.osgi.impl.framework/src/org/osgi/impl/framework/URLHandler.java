/*
 * $Header$
 *
 * OSGi URL Handler Bundle Reference Implementation
 *

 *
 * (C) Copyright IBM Corporation 2000-2001.
 *
 * This source code is licensed to OSGi as MEMBER LICENSED MATERIALS
 * under the terms of Section 3.2 of the OSGi MEMBER AGREEMENT.
 */
package org.osgi.impl.framework;

import java.net.*;
import java.util.Hashtable;

/**
 * This class implements RFC 44 as a stand alone bundle. Note, that this bundle
 * cannot be stopped once started since it registers content handlers with the
 * JVM!
 * <p>
 * This bundle bridges the factories with the service registry.
 */
public class URLHandler implements URLStreamHandlerFactory,
		ContentHandlerFactory {
	final static String	SPECIFICATION_VERSION	= "1.0";
	Framework			framework;
	/** Package prefixes for built-in content handlers. */
	String				contentPrefixes[]		= null;
	/** Package prefixes for built-in url stream handlers. */
	String				handlerPrefixes[]		= null;

	/* Count the number of pipes in s. For internal use only. */
	private int countPipes(String s) {
		int i = 0, start = 0;
		while (start < s.length()) {
			start = s.indexOf('|', start);
			if (start == -1)
				break;
			i++;
			start++; // Pass the pipe
		}
		return i;
	}

	/* Fill a with pipe separated strings of s. For internal use only. */
	private void fillin(String a[], String s) {
		int i = 0, start = 0;
		int end;
		while (start < s.length()) {
			end = s.indexOf('|', start);
			if (end == -1) {
				a[i] = s.substring(start);
				return;
			}
			a[i++] = s.substring(start, end);
			start = end + 1; // Pass the comma
		}
	}

	/**
	 * Registers the factories. Also sets up prefix arrays for finding built-in
	 * handlers.
	 */
	URLHandler(Framework framework) {
		this.framework = framework;
		String contentProp = System.getProperty("java.content.handler.pkgs");
		String handlerProp = System.getProperty("java.protocol.handler.pkgs");
		if (contentProp != null) {
			int i = countPipes(contentProp);
			contentPrefixes = new String[i + 1];
			fillin(contentPrefixes, contentProp);
		}
		if (handlerProp != null) {
			int i = countPipes(handlerProp);
			handlerPrefixes = new String[i + 1];
			fillin(handlerPrefixes, handlerProp);
		}
		URL.setURLStreamHandlerFactory(this);
		URLConnection.setContentHandlerFactory(this);
	}

	/**
	 * Fix mimetype to correspond to the class name lookup rules of rfc 44
	 * section 3.3.
	 */
	private String fixMIME(String mimetype) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < mimetype.length(); i++) {
			char c = mimetype.charAt(i);
			if (Character.isLetterOrDigit(c)) {
				sb.append(c);
			}
			else
				if (c == '/') {
					sb.append('.');
				}
				else {
					sb.append('_');
				}
		}
		return sb.toString();
	}

	/** Cache of the created content handlers */
	Hashtable	contentHandlerCache	= new Hashtable();

	/**
	 * Constructs an OSGIContentHandler for the given MIME type.
	 * 
	 * @return An OSGIContentHandler or null.
	 */
	public ContentHandler createContentHandler(String mimetype) {
		/* java.net.URLConnection doesn't cache what we return, so we must */
		mimetype = mimetype.toLowerCase();
		ContentHandler handler = (ContentHandler) contentHandlerCache
				.get(mimetype);
		if (handler != null)
			return handler;
		if (contentPrefixes != null) {
			for (int i = 0; i < contentPrefixes.length; i++) {
				try {
					Class clazz = Class.forName(contentPrefixes[i] + '.'
							+ fixMIME(mimetype));
					if (clazz != null) {
						handler = (ContentHandler) clazz.newInstance();
						break;
					}
				}
				catch (Exception e) {
				}
			}
		}
		if (handler == null) {
			handler = new ContentHandlerProxy(framework, mimetype);
		}
		contentHandlerCache.put(mimetype, handler);
		return handler;
	}

	/**
	 * Constructs an OSGIURLHandler for the given MIME type.
	 * 
	 * @return An OSGIURLHandler or null.
	 */
	public URLStreamHandler createURLStreamHandler(String protocol) {
		protocol = protocol.toLowerCase();
		if (handlerPrefixes != null) {
			for (int i = 0; i < handlerPrefixes.length; i++) {
				try {
					Class clazz = Class.forName(handlerPrefixes[i] + '.'
							+ protocol + ".Handler");
					if (clazz != null) {
						return (URLStreamHandler) clazz.newInstance();
					}
				}
				catch (Exception e) {
				}
			}
		}
		URLStreamHandlerProxy proxy = new URLStreamHandlerProxy(framework,
				protocol);
		// Unlike content handlers, we want to return null if a handler
		// doesn't exist for the protocol.
		if (proxy.exists())
			return proxy;
		proxy = null;
		return null;
	}
}
