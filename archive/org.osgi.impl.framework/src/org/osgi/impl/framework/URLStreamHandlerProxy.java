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
import org.osgi.framework.*;
import org.osgi.service.url.*;

/**
 * Proxies URLStreamHandler by tracking services for a specific protocol. There
 * will be a proxy for each protocol.
 */
public class URLStreamHandlerProxy extends URLStreamHandler {
	final Framework				framework;
	/** The protocol being proxied. */
	String						protocol;
	/** The filter matching the URLStreamHandlerServices beeing proxied */
	String						filter	= null;
	/** The setter that will forward setURL request back to this object. */
	MyURLStreamHandlerSetter	setter	= new MyURLStreamHandlerSetter(this);

	/** returns the current URLStream handler or tries to get one if null. */
	URLStreamHandlerService getHandler() {
		ServiceReference[] srs = getServiceReferences();
		return selectHighestRankingService(srs);
	}

	private int rankingOf(ServiceReference s) {
		Object v = s.getProperty(Constants.SERVICE_RANKING);
		if (v != null && v instanceof Integer) {
			return ((Integer) v).intValue();
		}
		else {
			return 0;
		}
	}

	private int serviceIdOf(ServiceReference s) {
		Object v = s.getProperty(Constants.SERVICE_ID);
		if (v != null && v instanceof Integer) {
			return ((Integer) v).intValue();
		}
		else {
			throw new RuntimeException("Missing service.id on a service.");
		}
	}

	URLStreamHandlerService selectHighestRankingService(ServiceReference[] srs) {
		if (srs == null || srs.length < 1) {
			return null;
		}
		ServiceReference bestSofar = srs[0];
		for (int i = 1; i < srs.length; ++i) {
			if (rankingOf(srs[i]) > rankingOf(bestSofar)) {
				bestSofar = srs[i];
			}
			else
				if (rankingOf(srs[i]) == rankingOf(bestSofar)
						&& serviceIdOf(srs[i]) < serviceIdOf(bestSofar)) {
					bestSofar = srs[i];
				}
		}
		return (URLStreamHandlerService) (((ServiceReferenceImpl) bestSofar).registration.service);
	}

	/**
	 * Instantiates a proxy for a specific protocol.
	 * 
	 * @param bc The BundleContext from the URLHandlerBundle. It will be used to
	 *        access the service registry.
	 * @param protocol The protocol being watched.
	 */
	public URLStreamHandlerProxy(Framework framework, String protocol) {
		this.framework = framework;
		this.protocol = protocol;
	}

	private String getFilter() {
		if (filter == null) {
			filter = "(&(" + Constants.OBJECTCLASS
					+ "=org.osgi.service.url.URLStreamHandlerService)" + "("
					+ URLConstants.URL_HANDLER_PROTOCOL + "=" + protocol + "))";
		}
		return filter;
	}

	/**
	 * returns true if there is a handler for this protocol in the service
	 * registry.
	 */
	boolean exists() {
		ServiceReference[] srs = getServiceReferences();
		return srs != null && srs.length > 0;
	}

	ServiceReference[] getServiceReferences() {
		try {
			return framework.services.get(null, getFilter());
		}
		catch (InvalidSyntaxException e) {
			throw new RuntimeException("unexpected InvalidSyntaxException: "
					+ e.getMessage());
		}
	}

	/**
	 * Simply forward the request to the handler or throw MalformedURLException
	 * if handler has unregistered.
	 */
	protected URLConnection openConnection(URL u) throws java.io.IOException {
		URLStreamHandlerService handler = getHandler();
		if (handler == null)
			throw new MalformedURLException(protocol + " handler gone");
		return handler.openConnection(u);
	}

	/**
	 * Simply forward the request to the handler or throw IllegalStateException
	 * if handler has unregistered.
	 */
	protected void parseURL(URL u, String spec, int start, int limit) {
		URLStreamHandlerService handler = getHandler();
		if (handler == null)
			throw new IllegalStateException(protocol + " handler gone");
		handler.parseURL(setter, u, spec, start, limit);
	}

	/**
	 * We just pass this up to URLConnection. URLStreamHandlerServices will use
	 * this method to call setURL since only the URLStreamHandler for the
	 * protocol (which is this proxy) will be able to call setURL.
	 */
	public void setURL(URL u, String protocol, String host, int port,
			String authority, String userInfo, String path, String query,
			String ref) {
		super.setURL(u, protocol, host, port, authority, userInfo, path, query,
				ref);
	}

	/**
	 * We just pass this up to URLConnection. URLStreamHandlerServices will use
	 * this method to call setURL since only the URLStreamHandler for the
	 * protocol (which is this proxy) will be able to call setURL.
	 */
	public void setURL(URL u, String protocol, String host, int port,
			String file, String ref) {
		super.setURL(u, protocol, host, port, file, ref);
	}

	/**
	 * Simply forward the request to the handler or throw IllegalStateException
	 * if handler has unregistered.
	 */
	protected String toExternalForm(URL u) {
		URLStreamHandlerService handler = getHandler();
		if (handler == null)
			throw new IllegalStateException(protocol + " handler gone");
		return handler.toExternalForm(u);
	}

	/**
	 * Simply forward the request to the handler or throw IllegalStateException
	 * if handler has unregistered.
	 */
	protected boolean equals(URL u1, URL u2) {
		URLStreamHandlerService handler = getHandler();
		if (handler == null)
			throw new IllegalStateException(protocol + " handler gone");
		return handler.equals(u1, u2);
	}

	/**
	 * Simply forward the request to the handler or throw IllegalStateException
	 * if handler has unregistered.
	 */
	protected int getDefaultPort() {
		URLStreamHandlerService handler = getHandler();
		if (handler == null)
			throw new IllegalStateException(protocol + " handler gone");
		return handler.getDefaultPort();
	}

	/**
	 * Simply forward the request to the handler or throw IllegalStateException
	 * if handler has unregistered.
	 */
	protected InetAddress getHostAddress(URL u) {
		URLStreamHandlerService handler = getHandler();
		if (handler == null)
			throw new IllegalStateException(protocol + " handler gone");
		return handler.getHostAddress(u);
	}

	/**
	 * Simply forward the request to the handler or throw IllegalStateException
	 * if handler has unregistered.
	 */
	protected int hashCode(URL u) {
		URLStreamHandlerService handler = getHandler();
		if (handler == null)
			throw new IllegalStateException(protocol + " handler gone");
		return handler.hashCode(u);
	}

	/**
	 * Simply forward the request to the handler or throw IllegalStateException
	 * if handler has unregistered.
	 */
	protected boolean hostsEqual(URL u1, URL u2) {
		URLStreamHandlerService handler = getHandler();
		if (handler == null)
			throw new IllegalStateException(protocol + " handler gone");
		return handler.hostsEqual(u1, u2);
	}

	/**
	 * Simply forward the request to the handler or throw IllegalStateException
	 * if handler has unregistered.
	 */
	protected boolean sameFile(URL u1, URL u2) {
		URLStreamHandlerService handler = getHandler();
		if (handler == null)
			throw new IllegalStateException(protocol + " handler gone");
		return handler.sameFile(u1, u2);
	}
}

class MyURLStreamHandlerSetter implements URLStreamHandlerSetter {
	URLStreamHandlerProxy	proxy;

	MyURLStreamHandlerSetter(URLStreamHandlerProxy proxy) {
		this.proxy = proxy;
	}

	public void setURL(URL u, String proto, String host, int port, String auth,
			String user, String path, String query, String ref) {
		proxy.setURL(u, proto, host, port, auth, user, path, query, ref);
	}

	public void setURL(URL u, String proto, String host, int port, String file,
			String ref) {
		proxy.setURL(u, proto, host, port, file, ref);
	}
}
