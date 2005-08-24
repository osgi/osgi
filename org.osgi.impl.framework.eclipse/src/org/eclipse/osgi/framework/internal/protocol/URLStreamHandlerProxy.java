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

import java.io.IOException;
import java.net.*;
import org.osgi.framework.*;
import org.osgi.service.url.URLConstants;
import org.osgi.service.url.URLStreamHandlerService;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * The URLStreamHandlerProxy is a URLStreamHandler that acts as a proxy for registered 
 * URLStreamHandlerServices.  When a URLStreamHandler is requested from the URLStreamHandlerFactory 
 * and it exists in the service registry, a URLStreamHandlerProxy is created which will pass all the 
 * requests from the requestor to the real URLStreamHandlerService.  We can't return the real 
 * URLStreamHandlerService from the URLStreamHandlerFactory because the JVM caches URLStreamHandlers 
 * and therefore would not support a dynamic environment of URLStreamHandlerServices being registered 
 * and unregistered.
 */

public class URLStreamHandlerProxy extends URLStreamHandler implements ServiceTrackerCustomizer {
	// TODO lots of type-based names 
	protected URLStreamHandlerService realHandlerService;

	protected URLStreamHandlerSetter urlSetter;

	protected ServiceTracker urlStreamHandlerServiceTracker;

	protected boolean handlerRegistered;

	protected BundleContext context;
	protected ServiceReference urlStreamServiceReference;

	protected String protocol;

	protected int ranking = -1;

	public URLStreamHandlerProxy(String protocol, ServiceReference reference, BundleContext context) {
		handlerRegistered = true;

		this.context = context;
		this.protocol = protocol;

		urlSetter = new URLStreamHandlerSetter(this);

		//set the handler and ranking
		setNewHandler(reference, getRank(reference));

		urlStreamHandlerServiceTracker = new ServiceTracker(context, StreamHandlerFactory.URLSTREAMHANDLERCLASS, this);
		StreamHandlerFactory.secureAction.open(urlStreamHandlerServiceTracker);
	}

	private void setNewHandler(ServiceReference reference, int rank) {
		this.urlStreamServiceReference = reference;
		this.ranking = rank;
		this.realHandlerService = (URLStreamHandlerService) StreamHandlerFactory.secureAction.getService(reference, context);
	}

	/**
	 * @see java.net.URLStreamHandler#equals(URL, URL)
	 */
	protected boolean equals(URL url1, URL url2) {
		return realHandlerService.equals(url1, url2);
	}

	/**
	 * @see java.net.URLStreamHandler#getDefaultPort()
	 */
	protected int getDefaultPort() {
		return realHandlerService.getDefaultPort();
	}

	/**
	 * @see java.net.URLStreamHandler#getHostAddress(URL)
	 */
	protected InetAddress getHostAddress(URL url) {
		return realHandlerService.getHostAddress(url);
	}

	/**
	 * @see java.net.URLStreamHandler#hashCode(URL)
	 */
	protected int hashCode(URL url) {
		return realHandlerService.hashCode(url);
	}

	/**
	 * @see java.net.URLStreamHandler#hostsEqual(URL, URL)
	 */
	protected boolean hostsEqual(URL url1, URL url2) {
		return realHandlerService.hostsEqual(url1, url2);
	}

	/**
	 * @see java.net.URLStreamHandler#openConnection(URL)
	 */
	protected URLConnection openConnection(URL url) throws IOException {
		return realHandlerService.openConnection(url);
	}

	/**
	 * @see java.net.URLStreamHandler#parseURL(URL, String, int, int)
	 */
	protected void parseURL(URL url, String str, int start, int end) {
		realHandlerService.parseURL(urlSetter, url, str, start, end);
	}

	/**
	 * @see java.net.URLStreamHandler#sameFile(URL, URL)
	 */
	protected boolean sameFile(URL url1, URL url2) {
		return realHandlerService.sameFile(url1, url2);
	}

	/**
	 * @see java.net.URLStreamHandler#toExternalForm(URL)
	 */
	protected String toExternalForm(URL url) {
		return realHandlerService.toExternalForm(url);
	}

	/**
	 * @see java.net.URLStreamHandler#setURL(URL, String, String, int, String, String, String, String, String)
	 */
	public void setURL(URL u, String protocol, String host, int port, String authority, String userInfo, String file, String query, String ref) {
		super.setURL(u, protocol, host, port, authority, userInfo, file, query, ref);
	}


	public void setURL(URL url, String protocol, String host, int port, String file, String ref) {

		//using non-deprecated URLStreamHandler.setURL method. 
		//setURL(URL u, String protocol, String host, int port, String authority, String userInfo, String file, String query, String ref) 
		super.setURL(url, protocol, host, port, null, null, file, null, ref);
	}

	/**
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#addingService(ServiceReference)
	 */
	public Object addingService(ServiceReference reference) {
		//check to see if our protocol is being registered by another service
		Object prop = reference.getProperty(URLConstants.URL_HANDLER_PROTOCOL);
		if (!(prop instanceof String[]))
			return null;
		String[] protocols = (String[]) prop;
		for (int i = 0; i < protocols.length; i++) {
			if (protocols[i].equals(protocol)) {
				//If our protocol is registered by another service, check the service ranking and switch 
				//URLStreamHandlers if nessecary.

				Object property = reference.getProperty(Constants.SERVICE_RANKING);

				int newServiceRanking = (property instanceof Integer) ? ((Integer) property).intValue() : 0;

				if (!handlerRegistered) {
					setNewHandler(reference, newServiceRanking);
					handlerRegistered = true;
				}

				if (newServiceRanking > ranking) {
					setNewHandler(reference, newServiceRanking);
				}
				return (reference);
			}
		}

		//we don't want to continue hearing events about a URLStreamHandlerService not registered under our protocol
		return (null);
	}

	/**
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#modifiedService(ServiceReference, Object)
	 */
	//TODO return earlier, avoid nesting of ifs
	// check to see if the ranking has changed.  If so, re-select a new URLHandler
	public void modifiedService(ServiceReference reference, Object service) {
		int newRank = getRank(reference);

		if (reference == urlStreamServiceReference) {
			if (newRank < ranking) //The URLHandler we are currently
			//using has dropped it's ranking below a URLHandler registered for the same protocol.  
			//We need to swap out URLHandlers.
			//this should get us the highest ranked service, if available
			{
				ServiceReference newReference = urlStreamHandlerServiceTracker.getServiceReference();
				if (newReference != urlStreamServiceReference && newReference != null) {
					setNewHandler(newReference, ((Integer) newReference.getProperty(Constants.SERVICE_RANKING)).intValue());
				}
			}
		} else if (newRank > ranking) //the service changed is another URLHandler that we are not currently using
		//If it's ranking is higher, we must swap it in.
		{
			setNewHandler(reference, newRank);
		}
	}

	/**
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#removedService(ServiceReference, Object)
	 */
	public void removedService(ServiceReference reference, Object service) {
		//check to see if our URLStreamHandler was unregistered.  If so, look 
		//for a lower ranking URLHandler
		if (reference == urlStreamServiceReference) {
			//this should get us the highest ranking service left, if available
			ServiceReference newReference = urlStreamHandlerServiceTracker.getServiceReference();
			if (newReference != null) {
				setNewHandler(newReference, getRank(newReference));
			} else {
				handlerRegistered = false;
				realHandlerService = new NullURLStreamHandlerService();
				ranking = -1;
			}

		}

	}

	private int getRank(ServiceReference reference) {
		Object property = reference.getProperty(Constants.SERVICE_RANKING);
		return (property instanceof Integer) ? ((Integer) property).intValue() : 0;
	}

}
