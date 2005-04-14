/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.framework.internal.protocol;

import java.net.URLStreamHandler;
import java.util.Hashtable;
import java.util.StringTokenizer;
import org.eclipse.osgi.framework.adaptor.FrameworkAdaptor;
import org.eclipse.osgi.framework.util.SecureAction;
import org.osgi.framework.BundleContext;
import org.osgi.service.url.URLConstants;
import org.osgi.util.tracker.ServiceTracker;

/**
 * This class contains the URL stream handler factory for the OSGi framework.
 */
public class StreamHandlerFactory implements java.net.URLStreamHandlerFactory {
	static final SecureAction secureAction = new SecureAction();
	/** BundleContext to system bundle */
	protected BundleContext context;

	/** internal adaptor object */
	protected FrameworkAdaptor adaptor;

	private ServiceTracker handlerTracker;

	protected static final String URLSTREAMHANDLERCLASS = "org.osgi.service.url.URLStreamHandlerService"; //$NON-NLS-1$
	protected static final String PROTOCOL_HANDLER_PKGS= "java.protocol.handler.pkgs"; //$NON-NLS-1$
	protected static final String INTERNAL_PROTOCOL_HANDLER_PKG = "org.eclipse.osgi.framework.internal.protocol."; //$NON-NLS-1$
	private static final String DEFAULT_VM_PROTOCOL_HANDLERS = "sun.net.www.protocol"; //$NON-NLS-1$

	private Hashtable proxies;

	/**
	 * Create the factory.
	 *
	 * @param context BundleContext for the system bundle
	 */
	public StreamHandlerFactory(BundleContext context, FrameworkAdaptor adaptor) {
		this.context = context;
		this.adaptor = adaptor;

		proxies = new Hashtable(15);
		handlerTracker = new ServiceTracker(context, URLSTREAMHANDLERCLASS, null);
		handlerTracker.open();
	}

	/**
	 * Creates a new URLStreamHandler instance for the specified
	 * protocol.
	 *
	 * @param protocol The desired protocol
	 * @return a URLStreamHandler for the specific protocol.
	 */
	//TODO consider refactoring this method - it is too long
	public URLStreamHandler createURLStreamHandler(String protocol) {

		//first check for built in handlers
		String builtInHandlers = System.getProperty(PROTOCOL_HANDLER_PKGS);
		builtInHandlers = builtInHandlers == null ? DEFAULT_VM_PROTOCOL_HANDLERS : DEFAULT_VM_PROTOCOL_HANDLERS + '|' + builtInHandlers;
		Class clazz = null;
		if (builtInHandlers != null) {
			StringTokenizer tok = new StringTokenizer(builtInHandlers, "|"); //$NON-NLS-1$
			while (tok.hasMoreElements()) {
				StringBuffer name = new StringBuffer();
				name.append(tok.nextToken());
				name.append("."); //$NON-NLS-1$
				name.append(protocol);
				name.append(".Handler"); //$NON-NLS-1$
				try {
					clazz = Class.forName(name.toString());
					if (clazz != null) {
						return (null); //this class exists, it is a built in handler, let the JVM handle it	
					}
				} catch (ClassNotFoundException ex) {
				} //keep looking 
			}
		}

		//internal protocol handlers
		String name = INTERNAL_PROTOCOL_HANDLER_PKG + protocol + ".Handler"; //$NON-NLS-1$

		try {
			clazz = Class.forName(name);
		}
		//Now we check the service registry
		catch (Throwable t) {
			//first check to see if the handler is in the cache
			URLStreamHandlerProxy handler = (URLStreamHandlerProxy) proxies.get(protocol);
			if (handler != null) {
				return (handler);
			}
			//TODO avoid deep nesting of control structures - return early
			//look through the service registry for a URLStramHandler registered for this protocol
			org.osgi.framework.ServiceReference[] serviceReferences = handlerTracker.getServiceReferences();
			if (serviceReferences != null) {
				for (int i = 0; i < serviceReferences.length; i++) {
					Object prop = serviceReferences[i].getProperty(URLConstants.URL_HANDLER_PROTOCOL);
					if (prop != null && prop instanceof String[]) {
						String[] protocols = (String[]) prop;
						for (int j = 0; j < protocols.length; j++) {
							if (protocols[j].equals(protocol)) {
								handler = new URLStreamHandlerProxy(protocol, serviceReferences[i], context);
								proxies.put(protocol, handler);
								return (handler);
							}
						}
					}
				}
			}
			return (null);
		}

		if (clazz == null) {
			return null;
		}

		try {
			URLStreamHandler handler = (URLStreamHandler) clazz.newInstance();

			if (handler instanceof ProtocolActivator) {
				((ProtocolActivator) handler).start(context, adaptor);
			}

			return handler;
		} catch (Exception e) {
			return null;
		}
	}
}
