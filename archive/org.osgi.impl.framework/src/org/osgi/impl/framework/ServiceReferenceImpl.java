/**
 * Copyright (c) 1999 - 2001 Gatespace AB. All Rights Reserved.
 * 
 * Gatespace grants Open Services Gateway Initiative (OSGi) an irrevocable,
 * perpetual, non-exclusive, worldwide, paid-up right and license to
 * reproduce, display, perform, prepare and have prepared derivative works
 * based upon and distribute and sublicense this material and derivative
 * works thereof as set out in the OSGi MEMBER AGREEMENT as of January 24
 * 2000, for use in accordance with Section 2.2 of the BY-LAWS of the
 * OSGi MEMBER AGREEMENT.
 */

package org.osgi.impl.framework;

import java.lang.reflect.*;
import java.security.*;
import java.util.*;

import org.osgi.framework.*;

/**
 * Implementation of the ServiceReference object.
 *
 * @see org.osgi.framework.ServiceReference
 * @author Gatespace AB (osgiref@gatespace.com)
 * @version $Revision$
 */
public class ServiceReferenceImpl implements ServiceReference
{
    
    /**
     * Link to registration object for this reference.
     */
    ServiceRegistrationImpl registration;
    
    
    /**
     * Construct a ServiceReference based on given ServiceRegistration.
     *
     * @param reg ServiceRegistration pointed to be this reference.
     */
    ServiceReferenceImpl(ServiceRegistrationImpl reg)
    {
	registration = reg;
    }
    
    //
    // ServiceReference interface
    //
    
    /**
     * Get the value of a service's property.
     *
     * @see org.osgi.framework.ServiceReference#getProperty
     */
    public Object getProperty(String key)
    {
	synchronized (registration) {
	    if (registration.properties != null) {
		return cloneObject(registration.properties.get(key));
	    } else {
		return null;
	    }
	}
    }
    
    
    /**
     * Get the list of key names for the service's properties.
     *
     * @see org.osgi.framework.ServiceReference#getPropertyKeys
     */
    public String[] getPropertyKeys()
    {
	synchronized (registration) {
	    if (registration.properties != null) {
		return registration.properties.keyArray();
	    } else {
		return null;
	    }
	}
    }
    
    
    /**
     * Return the bundle which registered the service.
     *
     * @see org.osgi.framework.ServiceReference#getBundle
     */
    public Bundle getBundle()
    {
	return registration.bundle;
    }
    
    
    /**
     * Test if ServiceReferences points to same service.
     *
     * @see org.osgi.framework.ServiceReference
     */
    public boolean equals(Object o)
    {
	if (o instanceof ServiceReferenceImpl) {
	    return registration == ((ServiceReferenceImpl)o).registration;
	}
	return false;
    }
    
    
    /**
     * Return a hashcode for the service.
     *
     * @see org.osgi.framework.ServiceReference
     */
    public int hashCode()
    {
	return registration.hashCode();
    }
    
    
    /**
     * Return the bundles that are using the service wrapped by this
     * ServiceReference, i.e., whose usage count for this service
     * is greater than zero.
     *
     * @return array of bundles whose usage count for the service wrapped by
     * this ServiceReference is greater than zero, or <tt>null</tt> if no
     * bundles currently are using this service
     *
     * @since 1.1
     */
    public Bundle[] getUsingBundles()
    {
	synchronized (registration) {
	    if (registration.reference != null && registration.dependents.size() > 0) {
		Set bs = registration.dependents.keySet();
		Bundle[] res =  new Bundle[bs.size()];
		return (Bundle[])bs.toArray(res);
	    } else {
		return null;
	    }
	}
    }
    
    //
    // Package methods
    //
    
    /**
     * Get the service object.
     *
     * @param bundle requester of service.
     * @return Service requested or null in case of failure.
     */
    Object getService(final BundleImpl bundle)
    {
	Object s = null;
	synchronized (registration) {
	    if (registration.bundle != null) {
		Integer ref = (Integer)registration.dependents.get(bundle);
		if (ref == null) {
		    String[] classes = (String[])registration.properties.get(Constants.OBJECTCLASS);
		    if (bundle.framework.checkPermissions) {
			boolean perm = false;
			AccessControlContext acc = AccessController.getContext();
			for (int i = 0; i < classes.length; i++) {
			    try { 
				acc.checkPermission(new ServicePermission(classes[i], ServicePermission.GET));
				perm = true;
				break;
			    } catch (AccessControlException ignore) { }
			}
			if (!perm) {
			    throw new SecurityException("Bundle has not permission to get service.");
			}
		    }
		    if (registration.service instanceof ServiceFactory) {
			try {
			    s = AccessController.doPrivileged(new PrivilegedExceptionAction() {
				    public Object run() {
				       return ((ServiceFactory)registration.service).getService(bundle, registration);
				    }
				});
			} catch (PrivilegedActionException pe) {
			    bundle.framework.listeners.frameworkError(registration.bundle, pe.getException());
			    return null;
			}
			if (s == null) {
			    return null;
			}
			ClassLoader cl = registration.bundle.getClassLoader();
			for (int i = 0; i < classes.length; i++) {
			    Class c = null;
			    try {
				c = Class.forName(classes[i], true, cl);
			    } catch (ClassNotFoundException ignore) { } // Already checked
			    if (!c.isInstance(s)) {
				bundle.framework.listeners.frameworkError(registration.bundle, new BundleException("ServiceFactory produced an object that did not implement: " + classes[i]));
				return null;
			    }
			}
			registration.serviceInstances.put(bundle, s);
		    } else {
			s = registration.service;
		    }
		    registration.dependents.put(bundle, new Integer(1));
		} else {
		    int count = ref.intValue();
		    registration.dependents.put(bundle, new Integer(count + 1));
		    if (registration.service instanceof ServiceFactory) {
			s = registration.serviceInstances.get(bundle);
		    } else {
			s = registration.service;
		    }
		}
	    }
	}
	return s;
    }
    
    
    /**
     * Unget the service object.
     *
     * @param bundle Bundle who wants remove service.
     * @param checkRefCounter If true decrement refence counter and remove service
     *                        if we reach zero. If false remove service without
     *                        checking refence counter.
     * @return True if service was remove or false if only refence counter was
     *         decremented.
     */
    boolean ungetService(BundleImpl bundle, boolean checkRefCounter)
    {
	synchronized (registration) {
	    if (registration.reference != null) {
		Object countInteger = registration.dependents.remove(bundle);
		if (countInteger != null) {
		    int count = ((Integer) countInteger).intValue();
		    if (checkRefCounter && count > 1) {
			registration.dependents.put(bundle, new Integer(count - 1));
			return true;
		    } else {
			Object sfi = registration.serviceInstances.remove(bundle);
			if (sfi != null) {
			    try {
				((ServiceFactory)registration.service).ungetService(bundle, registration, sfi);
			    } catch (Exception e) {
				bundle.framework.listeners.frameworkError(registration.bundle, e);
			    }
			}
		    }
		}
	    }
	}
	return false;
    }
    
    
    /**
     * Get all properties registered with this service.
     *
     * @return Dictionary containing properties or null
     *         if service has been removed. 
     */
    Dictionary getProperties()
    {
	return (Dictionary)registration.properties;
    }
    
    //
    // Private methods
    //    
    
    /**
     * Clone object. Handles all service property types
     * and does this recursivly.
     *
     * @param bundle Object to clone.
     * @return Cloned object.
     */
    Object cloneObject(Object val)
    {
	if (val instanceof Object []) {
	    val = ((Object [])val).clone();
	    int len = Array.getLength(val);
	    if (len > 0 && Array.get(val, 0).getClass().isArray()) {
		for (int i = 0; i < len; i++) {
		    Array.set(val, i, cloneObject(Array.get(val, i)));
		}
	    }
	} else if (val instanceof boolean []) {
	    val = ((boolean [])val).clone();
	} else if (val instanceof byte []) {
	    val = ((byte [])val).clone();
	} else if (val instanceof char []) {
	    val = ((char [])val).clone();
	} else if (val instanceof double []) {
	    val = ((double [])val).clone();
	} else if (val instanceof float []) {
	    val = ((float [])val).clone();
	} else if (val instanceof int []) {
	    val = ((int [])val).clone();
	} else if (val instanceof long []) {
	    val = ((long [])val).clone();
	} else if (val instanceof short []) {
	    val = ((short [])val).clone();
	} else if (val instanceof Vector) {
	    Vector c = (Vector)((Vector)val).clone();
	    for (int i = 0; i < c.size(); i++) {
		c.set(i, cloneObject(c.get(i)));
	    }
	    val = c;
	}
	return val;
    }
}
