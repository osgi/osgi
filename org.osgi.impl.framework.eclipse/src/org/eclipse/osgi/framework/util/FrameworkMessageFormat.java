/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.osgi.framework.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.ArrayList;
import java.util.Locale;
import org.eclipse.osgi.framework.msg.MessageFormat;
import org.eclipse.osgi.service.systembundle.EntryLocator;
import org.osgi.framework.*;

public class FrameworkMessageFormat extends MessageFormat {
	static private BundleContext context;
	static private ArrayList msgList = new ArrayList();
	static private ServiceListener listener = null;
	static private EntryLocator systemEntryLocator = null;

	private Class clazz;
	private String bundleName;

	private FrameworkMessageFormat(String bundleName, Locale locale, Class clazz) {
		super(bundleName, locale, clazz);
		this.bundleName = bundleName;
		this.clazz = clazz;
	}

	protected void init(final String bundleName, final Locale locale, final Class clazz) {
		if (systemEntryLocator == null) {
			super.init(bundleName, locale, clazz);
			return;
		}

		URL resourceURL = systemEntryLocator.getProperties(bundleName, locale);
		if (resourceURL != null) {
			InputStream resourceStream = null;
			try {
				resourceStream = resourceURL.openStream();
				bundle = new PropertyResourceBundle(resourceStream);
				this.locale = locale;
				return;
			} catch (IOException e) {
				// Do nothing will just call super below
			} finally {
				if (resourceStream != null) {
					try {
						resourceStream.close();
					} catch (IOException e) {
						//Ignore exception
					}
				}
			}
		}
		// if we get here just call super
		super.init(bundleName, locale, clazz);
	}

	private void init() {
		init(bundleName, locale, clazz);
	}

	static public synchronized void setContext(BundleContext context) {
		if (context == null) {
			if (FrameworkMessageFormat.context != null && listener != null)
				FrameworkMessageFormat.context.removeServiceListener(listener);
			FrameworkMessageFormat.context = null;
		} else if (FrameworkMessageFormat.context == null) {
			FrameworkMessageFormat.context = context;
			listener = new ResourceFinderListener();
			try {
				FrameworkMessageFormat.context.addServiceListener(listener, "(objectclass=org.eclipse.osgi.service.systembundle.EntryLocator)"); //$NON-NLS-1$
			} catch (InvalidSyntaxException e) {
				// Do nothing this cannot happen.
			}
		}

	}

	static public synchronized MessageFormat getMessageFormat(String bundleName) {
		FrameworkMessageFormat msgFormat = new FrameworkMessageFormat(bundleName, Locale.getDefault(), FrameworkMessageFormat.class);
		msgList.add(msgFormat);
		return msgFormat;
	}

	static private synchronized void initMessages() {
		int size = msgList.size();
		for (int i = 0; i < size; i++) {
			((FrameworkMessageFormat) msgList.get(i)).init();
		}
	}

	static private class ResourceFinderListener implements ServiceListener {
		private ServiceReference resourceFinderRef = null;

		public void serviceChanged(ServiceEvent event) {
			synchronized (FrameworkMessageFormat.class) {
				int eventType = event.getType();
				ServiceReference sr = event.getServiceReference();
				switch (eventType) {
					case ServiceEvent.REGISTERED :
						if (systemEntryLocator == null) {
							resourceFinderRef = sr;
							systemEntryLocator = (EntryLocator) context.getService(sr);
							initMessages();
						}
						break;
					case ServiceEvent.UNREGISTERING :
						if (sr.equals(resourceFinderRef)) {
							systemEntryLocator = null;
							initMessages();
							context.ungetService(resourceFinderRef);
							resourceFinderRef = null;
						}
						break;
				}
			}
		}
	}

}