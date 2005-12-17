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
import java.net.ContentHandler;
import java.net.URLConnection;
import org.osgi.framework.*;
import org.osgi.service.url.URLConstants;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * The ContentHandlerProxy is a ContentHandler that acts as a proxy for registered ContentHandlers.
 * When a ContentHandler is requested from the ContentHandlerFactory and it exists in the service
 * registry, a ContentHandlerProxy is created which will pass all the requests from the requestor to 
 * the real ContentHandler.  We can't return the real ContentHandler from the ContentHandlerFactory
 * because the JVM caches ContentHandlers and therefore would not support a dynamic environment of
 * ContentHandlers being registered and unregistered.
 */
public class ContentHandlerProxy extends ContentHandler implements ServiceTrackerCustomizer {
	protected ContentHandler realHandler;

	//TODO avoid type-based names
	protected ServiceTracker contentHandlerServiceTracker;

	protected BundleContext context;
	protected ServiceReference contentHandlerServiceReference;

	protected String contentType;

	protected int ranking = -1;

	public ContentHandlerProxy(String contentType, ServiceReference reference, BundleContext context) {
		this.context = context;
		this.contentType = contentType;

		if (reference != null) {
			setNewHandler(reference, getRank(reference));
		}
		//In this case, the proxy is constructed for a Content Handler that is not currently registered. 
		//Use the DefaultContentHandler until a ContentHandler for this mime-type is registered
		else {
			realHandler = new DefaultContentHandler();
		}
		contentHandlerServiceTracker = new ServiceTracker(context, ContentHandler.class.getName(), this);
		StreamHandlerFactory.secureAction.open(contentHandlerServiceTracker);
	}

	private void setNewHandler(ServiceReference reference, int rank) {
		this.contentHandlerServiceReference = reference;
		this.ranking = rank;
		this.realHandler = (ContentHandler) StreamHandlerFactory.secureAction.getService(reference, context);
	}

	/**
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#addingService(ServiceReference)
	 */
	public Object addingService(ServiceReference reference) {
		//check to see if our contentType is being registered by another service
		Object prop = reference.getProperty(URLConstants.URL_CONTENT_MIMETYPE);
		if (!(prop instanceof String[]))
			return null;
		String[] contentTypes = (String[]) prop;
		for (int i = 0; i < contentTypes.length; i++) {
			if (contentTypes[i].equals(contentType)) {
				//If our contentType is registered by another service, check the service ranking and switch 
				//URLStreamHandlers if nessecary.

				int newServiceRanking = getRank(reference);

				//int newServiceRanking = ((Integer)reference.getProperty(Constants.SERVICE_RANKING)).intValue();
				if (newServiceRanking > ranking) {
					setNewHandler(reference, newServiceRanking);
				}
				return (reference);
			}
		}

		//we don't want to continue hearing events about a ContentHandler service not registered under our contentType
		return (null);
	}

	/**
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#modifiedService(ServiceReference, Object)
	 */

	public void modifiedService(ServiceReference reference, Object service) {
		int newrank = getRank(reference);

		if (reference == contentHandlerServiceReference) {
			if (newrank < ranking) //The ContentHandler we are currently
			//using has dropped it's ranking below a ContentHandler registered for the same protocol.  
			//We need to swap out ContentHandlers.
			//this should get us the highest ranked service, if available
			{
				ServiceReference newReference = contentHandlerServiceTracker.getServiceReference();
				if (newReference != contentHandlerServiceReference && newReference != null) {
					setNewHandler(newReference, ((Integer) newReference.getProperty(Constants.SERVICE_RANKING)).intValue());
				}
			}
		} else if (newrank > ranking) //the service changed is another URLHandler that we are not currently using
		//If it's ranking is higher, we must swap it in.
		{
			setNewHandler(reference, newrank);
		}

	}

	/**
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#removedService(ServiceReference, Object)
	 */
	public void removedService(ServiceReference reference, Object service) {
		//check to see if our URLStreamHandler was unregistered.  If so, look 
		//for a lower ranking URLHandler
		//TODO invert test and return quickly, to reduce indentation
		if (reference == contentHandlerServiceReference) {
			//this should get us the highest ranking service left, if available
			ServiceReference newReference = contentHandlerServiceTracker.getServiceReference();
			if (newReference != null) {
				setNewHandler(newReference, getRank(newReference));
			} else {
				ranking = -1;
				realHandler = new DefaultContentHandler();
			}

		}

	}

	/**
	 * @see java.net.ContentHandler#getContent(URLConnection)
	 */

	public Object getContent(URLConnection uConn) throws IOException {
		return realHandler.getContent(uConn);
	}

	private int getRank(ServiceReference reference) {
		Object property = reference.getProperty(Constants.SERVICE_RANKING);
		return (property instanceof Integer) ? ((Integer) property).intValue() : 0;
	}

	class DefaultContentHandler extends ContentHandler {

		/**
		 * @see java.net.ContentHandler#getContent(URLConnection)
		 */
		public Object getContent(URLConnection uConn) throws IOException {
			return uConn.getInputStream();
		}
	}
}
