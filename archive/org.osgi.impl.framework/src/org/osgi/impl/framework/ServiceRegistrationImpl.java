/**
 * Copyright (c) 1999 - 2001 Gatespace AB. All Rights Reserved.
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

import java.security.*;
import java.util.*;
import org.osgi.framework.*;

/**
 * Implementation of the ServiceRegistration object.
 * 
 * @see org.osgi.framework.ServiceRegistration
 * @author Gatespace AB (osgiref@gatespace.com)
 * @version $Revision$
 */
public class ServiceRegistrationImpl implements ServiceRegistration {
	/**
	 * Bundle implementing this services.
	 */
	BundleImpl				bundle;
	/**
	 * Service or ServiceFactory object.
	 */
	Object					service;
	/**
	 * Reference object to this service registration.
	 */
	ServiceReferenceImpl	reference;
	/**
	 * Service propeties.
	 */
	PropertiesDictionary	properties;
	/**
	 * Bundles dependent on this service. Integer is used as reference counter,
	 * counting number of unbalanced getService().
	 */
	Hashtable				/* Bundle->Integer */dependents		= new Hashtable();
	/**
	 * Object instances that factory has produced.
	 */
	Hashtable				/* Bundle->Object */serviceInstances	= new Hashtable();
	/**
	 * Avoid recursive unregistrations.
	 */
	private boolean			unregistering						= false;

	/**
	 * Construct a ServiceRegistration for a registered service.
	 * 
	 * @param b Bundle providing service.
	 * @param s Service object.
	 * @param props Properties describing service.
	 */
	ServiceRegistrationImpl(BundleImpl b, Object s, PropertiesDictionary props) {
		bundle = b;
		service = s;
		properties = props;
		reference = new ServiceReferenceImpl(this);
	}

	//
	// ServiceRegistration interface
	//
	/**
	 * Returns a ServiceReference object for this registration.
	 * 
	 * @see org.osgi.framework.ServiceRegistration#getReference
	 */
	public ServiceReference getReference() {
		if (reference != null) {
			return reference;
		}
		else {
			throw new IllegalStateException("Service is unregistered");
		}
	}

	/**
	 * Update the properties associated with this service.
	 * 
	 * @see org.osgi.framework.ServiceRegistration#setProperties
	 */
	public synchronized void setProperties(Dictionary props) {
		if (reference != null) {
			String[] classes = (String[]) properties.get(Constants.OBJECTCLASS);
			Long sid = (Long) properties.get(Constants.SERVICE_ID);
			properties = new PropertiesDictionary(props, classes, sid);
			bundle.framework.listeners.serviceChanged(new ServiceEvent(
					ServiceEvent.MODIFIED, reference));
		}
		else {
			throw new IllegalStateException("Service is unregistered");
		}
	}

	/**
	 * Unregister the service.
	 * 
	 * @see org.osgi.framework.ServiceRegistration#unregister
	 */
	public synchronized void unregister() {
		if (reference != null) {
			if (unregistering) {
				throw new IllegalStateException(
						"Unregister called inside service UNREGISTER listener or inside ungetService.");
			}
			unregistering = true;
			bundle.framework.services.removeServiceRegistration(this);
			bundle.framework.listeners.serviceChanged(new ServiceEvent(
					ServiceEvent.UNREGISTERING, reference));
			for (Iterator i = serviceInstances.entrySet().iterator(); i
					.hasNext();) {
				final Map.Entry e = (Map.Entry) i.next();
				try {
					final ServiceRegistration sr = this;
					AccessController
							.doPrivileged(new PrivilegedExceptionAction() {
								public Object run() {
									((ServiceFactory) service).ungetService(
											(Bundle) e.getKey(), sr, e
													.getValue());
									return null;
								}
							});
				}
				catch (PrivilegedActionException pe) {
					bundle.framework.listeners
							.frameworkEvent(new FrameworkEvent(
									FrameworkEvent.ERROR, bundle, pe
											.getException()));
				}
			}
			unregistering = false;
			bundle = null;
			reference = null;
			service = null;
			dependents = null;
			serviceInstances = null;
		}
		else {
			throw new IllegalStateException("Service is unregistered");
		}
	}

	//
	// Package methods
	//
	/**
	 * Check if a bundle uses this service
	 * 
	 * @param b Bundle to check
	 * @return true if bundle uses this service
	 */
	synchronized boolean isUsedByBundle(Bundle b) {
		if (dependents != null) {
			return dependents.containsKey(b);
		}
		else {
			return false;
		}
	}
}
