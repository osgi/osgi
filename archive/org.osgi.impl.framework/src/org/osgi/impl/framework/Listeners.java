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
 * Here we handle all listeners that bundles have registered.
 *
 * @author Gatespace AB (osgiref@gatespace.com)
 * @version $Revision$
 */
public class Listeners
    implements BundleListener, FrameworkListener, ServiceListener
{
    
    /**
     * All bundle event listeners.
     */
    private Vector bundleListeners = new Vector();
    
    /**
     * All framework event listeners.
     */
    private Vector frameworkListeners = new Vector();
    
    /**
     * All service event listeners.
     */
    private Vector serviceListeners = new Vector();
    
    
    /**
     * Add a bundle listener to current framework.
     *
     * @param bundle Who wants to add listener.
     * @param listener Object to add.
     */
    void addBundleListener(Bundle bundle, BundleListener listener)
    {
	ListenerEntry le = new ListenerEntry(bundle, listener, null);
	if (!bundleListeners.contains(le)) {
	    if (listener instanceof SynchronousBundleListener) {
		bundleListeners.add(0, le);
	    } else {
		bundleListeners.add(le);
	    }
	}
    }
    
    
    /**
     * Remove bundle listener from current framework. Silently ignore
     * if listener doesn't exist. If listener is registered more than
     * once remove one instances.
     *
     * @param bundle Who wants to remove listener.
     * @param listener Object to remove.
     */
    void removeBundleListener(Bundle bundle, BundleListener listener)
    {
	removeListener(bundleListeners, bundle, listener);
    }
    
    
    /**
     * Add a bundle listener to current framework.
     *
     * @param bundle Who wants to add listener.
     * @param listener Object to add.
     */
    void addFrameworkListener(Bundle bundle, FrameworkListener listener)
    {
	ListenerEntry le = new ListenerEntry(bundle, listener, null);
	if (!frameworkListeners.contains(le)) {
	    frameworkListeners.add(le);
	}
    }
    
    
    /**
     * Remove framework listener from current framework. Silently ignore
     * if listener doesn't exist. If listener is registered more than
     * once remove all instances.
     *
     * @param bundle Who wants to remove listener.
     * @param listener Object to remove.
     */
    void removeFrameworkListener(Bundle bundle, FrameworkListener listener)
    {
	removeListener(frameworkListeners, bundle, listener);
    }
    
    
    /**
     * Add a service listener with filter to current framework.
     * If no filter is wanted, call with filter param set to null.
     *
     * @param bundle Who wants to add listener.
     * @param listener Object to add.
     * @param filter LDAP String used for filtering event before calling listener.
     */
    void addServiceListener(Bundle bundle, ServiceListener listener, String filter)
	throws InvalidSyntaxException
    {
	if (filter != null) {
	    filter = LDAPQuery.canonicalize(filter);
	    LDAPQuery.check(filter);
	}
	ListenerEntry le = new ListenerEntry(bundle, listener, filter);
	int i = serviceListeners.indexOf(le);
	if (i == -1) {
	    serviceListeners.add(le);
	} else {
	    serviceListeners.setElementAt(le, i);
	}
    }
    
    
    /**
     * Remove service listener from current framework. Silently ignore
     * if listener doesn't exist. If listener is registered more than
     * once remove all instances.
     *
     * @param bundle Who wants to remove listener.
     * @param listener Object to remove.
     */
    void removeServiceListener(Bundle bundle, ServiceListener listener)
    {
	removeListener(serviceListeners, bundle, listener);
    }
    
    
    /**
     * Remove all listener registered by a bundle in the current framework.
     *
     * @param b Bundle which listeners we want to remove.
     */
    void removeAllListeners(Bundle b)
    {
	removeAllListeners(bundleListeners, b);
	removeAllListeners(frameworkListeners, b);
	removeAllListeners(serviceListeners, b);
    }
    

    /**
     * Convenience method for throwing framework error event.
     *
     * @param b Bundle which caused the error.
     * @param t Throwable generated.
     */
    void frameworkError(Bundle b, Throwable t)
    {
	frameworkEvent(new FrameworkEvent(FrameworkEvent.ERROR, b, t));
    }
    
    //
    // BundleListener interface
    //
    
    /**
     * Receive notification that a bundle has had a change occur in it's lifecycle.
     *
     * @see org.osgi.framework.BundleListener#bundleChanged
     */
    public void bundleChanged(final BundleEvent evt) {
	Vector bl = (Vector)bundleListeners.clone();
	for (Enumeration e = bl.elements(); e.hasMoreElements();) {
	    final ListenerEntry l = (ListenerEntry)e.nextElement();
	    try {
		AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
			    ((BundleListener)l.listener).bundleChanged(evt);
			    return null;
			}
		    });
	    } catch (Exception pe) {
		frameworkError(l.bundle, pe);
	    }
	}
    }

    //
    // FrameworkListener interface
    //
    
    /**
     * Receive notification of a general framework event.
     *
     * @see org.osgi.framework.FrameworkListener#frameworkEvent
     */
    public void frameworkEvent(final FrameworkEvent evt) {
	Vector fl = (Vector)frameworkListeners.clone();
	for (Enumeration e = fl.elements(); e.hasMoreElements();) {
	    final ListenerEntry l = (ListenerEntry)e.nextElement();
	    try {
		AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
			    ((FrameworkListener)l.listener).frameworkEvent(evt);
			    return null;
			}
		    });
	    } catch (Exception pe) {
		// We can't report Error events again, since we probably would
		// go into a infinite loop.
		if (evt.getType() != FrameworkEvent.ERROR) {
		    frameworkError(l.bundle, pe);
		}
	    }
	}
    }
    
    //
    // ServiceListener interface
    //
    
    /**
     * Receive notification that a service has had a change occur in it's lifecycle.
     *
     * @see org.osgi.framework.ServiceListener#serviceChanged
     */
    public void serviceChanged(final ServiceEvent evt) {
	ServiceReferenceImpl sr = (ServiceReferenceImpl)evt.getServiceReference();
	String[] c = (String[])sr.getProperty(Constants.OBJECTCLASS);
	Permission[] perms = new Permission[c.length];
	for (int i = 0; i < c.length; i++) {
	    perms[i] = new ServicePermission(c[i], ServicePermission.GET);
	}
	Vector sl = (Vector)serviceListeners.clone();
	for (Enumeration e = sl.elements(); e.hasMoreElements();) {
	    final ListenerEntry l = (ListenerEntry)e.nextElement();
	    try {
		if (l.filter == null || LDAPQuery.query(l.filter, sr.getProperties())) {
		    for (int i = 0; i < c.length; i++) {
			if (l.bundle.hasPermission(perms[i])) {
			    try {
				AccessController.doPrivileged(new PrivilegedAction() {
					public Object run() {
					    ((ServiceListener)l.listener).serviceChanged(evt);
					    return null;
					}
				    });
			    } catch (Exception pe) {
				frameworkError(l.bundle, pe);
			    }
			    break;
			}
		    }
		}
	    } catch (Exception le) {
		frameworkError(l.bundle, le);
	    }
	}
    }

    //
    // Private methods
    //
    
    /**
     * Remove all listener registered by a bundle in specified list within
     * the current framework. Silently ignore if listener doesn't exist.
     *
     * @param l Which list to remove from bundle, framework or service.
     * @param b Bundle which listeners we want to remove.
     */
    private void removeAllListeners(List l, Bundle b)
    {
	synchronized (l) {
	    for (Iterator i = l.iterator(); i.hasNext();) {
		if (((ListenerEntry)i.next()).bundle == b) {
		    i.remove();
		}
	    }
	}
    }
    
    
    /**
     * Remove a listener from specified list within the current framework.
     * Silently ignore if listener doesn't exist. If listener is registered
     * more than once remove all instances.
     *
     * @param l Which list to remove from bundle, framework or service.
     * @param b Who wants to remove listener.
     * @param listener Object to remove.
     */
    private void removeListener(List l, Bundle b, EventListener listener)
    {
	synchronized (l) {
	    for (Iterator i = l.iterator(); i.hasNext();) {
		ListenerEntry le = (ListenerEntry)i.next();
		if (le.listener == listener && le.bundle == b) {
		    i.remove();
		}
	    }
	}
    }
    
}


/**
 * Data structure for saving listener info. Contains bundle
 * who registered listener, listener object and if a service
 * listener any associated filter.
 */
class ListenerEntry
{
    Bundle bundle;
    EventListener listener;
    String filter;
    
    ListenerEntry(Bundle b, EventListener l, String f) {
	bundle = b;
	listener = l;
	filter = f;
    }

    public boolean equals(Object o) {
	if (o instanceof ListenerEntry) {
	    return bundle == ((ListenerEntry)o).bundle &&
		listener == ((ListenerEntry)o).listener;
	}
	return false;
    }
}
