/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.framework.internal.protocol;

import java.net.ContentHandler;
import java.util.Hashtable;
import java.util.StringTokenizer;
import org.osgi.framework.BundleContext;
import org.osgi.service.url.URLConstants;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The ContentHandlerFactory is registered with the JVM to provide content handlers
 * to requestors.  The ContentHandlerFactory will first look for built-in content handlers.
 * If a built in handler exists, this factory will return null.  Otherwise, this ContentHandlerFactory
 * will search the service registry for a maching Content-Handler and, if found, return a 
 * proxy for that content handler.
 */
public class ContentHandlerFactory implements java.net.ContentHandlerFactory {
	private ServiceTracker contentHandlerTracker;
	private BundleContext context;

	private static final String contentHandlerClazz = "java.net.ContentHandler"; //$NON-NLS-1$
	private static final String CONTENT_HANDLER_PKGS= "java.content.handler.pkgs"; //$NON-NLS-1$
	private static final String DEFAULT_VM_CONTENT_HANDLERS = "sun.net.www.content"; //$NON-NLS-1$

	private Hashtable proxies;

	public ContentHandlerFactory(BundleContext context) {
		this.context = context;

		proxies = new Hashtable(5);

		//We need to track content handler registrations
		contentHandlerTracker = new ServiceTracker(context, contentHandlerClazz, null);
		contentHandlerTracker.open();
	}

	/**
	 * @see java.net.ContentHandlerFactory#createContentHandler(String)
	 */
	//TODO method is too long... consider reducing indentation (returning quickly) and moving complex steps to private methods
	public ContentHandler createContentHandler(String contentType) {
		//first, we check to see if there exists a built in content handler for
		//this content type.  we can not overwrite built in ContentHandlers
		String builtInHandlers = System.getProperty(CONTENT_HANDLER_PKGS);
		builtInHandlers = builtInHandlers == null ? DEFAULT_VM_CONTENT_HANDLERS : DEFAULT_VM_CONTENT_HANDLERS + '|' + builtInHandlers;
		Class clazz = null;
		if (builtInHandlers != null) {
			//replace '/' with a '.' and all characters not allowed in a java class name
			//with a '_'.

			// find all characters not allowed in java names
			String convertedContentType = contentType.replace('.', '_');
			convertedContentType = convertedContentType.replace('/', '.');
			convertedContentType = convertedContentType.replace('-', '_');
			StringTokenizer tok = new StringTokenizer(builtInHandlers, "|"); //$NON-NLS-1$
			while (tok.hasMoreElements()) {
				StringBuffer name = new StringBuffer();
				name.append(tok.nextToken());
				name.append("."); //$NON-NLS-1$
				name.append(convertedContentType);
				try {
					clazz = Class.forName(name.toString());
					if (clazz != null) {
						return (null); //this class exists, it is a built in handler, let the JVM handle it	
					}
				} catch (ClassNotFoundException ex) {
				} //keep looking 
			}
		}

		//first check to see if the handler is in the cache
		ContentHandlerProxy proxy = (ContentHandlerProxy) proxies.get(contentType);
		if (proxy != null) {
			return (proxy);
		}
		org.osgi.framework.ServiceReference[] serviceReferences = contentHandlerTracker.getServiceReferences();
		if (serviceReferences != null) {
			for (int i = 0; i < serviceReferences.length; i++) {
				Object obj = serviceReferences[i].getProperty(URLConstants.URL_CONTENT_MIMETYPE);
				if (obj != null && obj instanceof String[]) {
					String[] contentHandler = (String[]) obj;
					for (int j = 0; j < contentHandler.length; j++) {
						if (contentHandler[j].equals(contentType)) {
							proxy = new ContentHandlerProxy(contentType, serviceReferences[i], context);
							proxies.put(contentType, proxy);
							return (proxy);
						}
					}
				}
			}
		}

		//If we can't find the content handler in the service registry, return Proxy with DefaultContentHandler set.
		//We need to do this because if we return null, we won't get called again for this content type.
		proxy = new ContentHandlerProxy(contentType, null, context);
		proxies.put(contentType, proxy);
		return (proxy);
	}

}
