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

import java.io.*;
import java.security.*;
import java.util.*;

import org.osgi.framework.*;

/**
 * Implementation of the BundleContext object.
 *
 * @see org.osgi.framework.BundleContext
 * @author Gatespace AB (osgiref@gatespace.com)
 * @version $Revision$
 */
public class BundleContextImpl implements BundleContext
{
    
    /**
     * Reference to current framework object.
     */
    private final Framework framework;
    
    /**
     * Reference to bundleImpl for this context.
     */
    private BundleImpl bundle;
    
    
    /**
     * Create a BundleContext for specified bundle.
     */
    public BundleContextImpl(BundleImpl bundle)
    {
	this.bundle = bundle;
	framework = bundle.framework;
    }


    /**
     * Invalidate this BundleContext.
     */
    void invalidate()
    {
	bundle = null;
    }

    //
    // BundleContext interface
    //
    
    /**
     * Retrieve the value of the named environment property.
     *
     * @see org.osgi.framework.BundleContext#getProperty
     */
    public String getProperty(String key)
    {
	isBCvalid();
	return Framework.getProperty(key);
    }
    
    
    /**
     * Install a bundle from location.
     *
     * @see org.osgi.framework.BundleContext#installBundle
     */
    public Bundle installBundle(String location) throws BundleException
    {
	isBCvalid();
	framework.checkAdminPermission();
	return framework.bundles.install(location, (InputStream)null);
    }
    
    
    /**
     * Install a bundle from an InputStream.
     *
     * @see org.osgi.framework.BundleContext#installBundle
     */
    public Bundle installBundle(String location, InputStream in)
	throws BundleException
    {
	try {
	    isBCvalid();
	    framework.checkAdminPermission();
	    return framework.bundles.install(location, in);
	} finally {
	    if (in != null) {
		try {
		    in.close();
		} catch (IOException ignore) {}
	    }
	}
    }
    
    
    /**
     * Retrieve the Bundle object for the calling bundle.
     *
     * @see org.osgi.framework.BundleContext#getBundle
     */
    public Bundle getBundle()
    {
	isBCvalid();
	return bundle;
    }
    
    
    /**
     * Retrieve the bundle that has the given unique identifier.
     *
     * @see org.osgi.framework.BundleContext#getBundle
     */
    public Bundle getBundle(long id)
    {
	isBCvalid();
	return framework.bundles.getBundle(id);
    }
    
    
    /**
     * Retrieve a list of all installed bundles.
     *
     * @see org.osgi.framework.BundleContext#getBundles
     */
    public Bundle[] getBundles()
    {
	isBCvalid();
	return framework.bundles.getBundles();
    }
    
    
    /**
     * Add a service listener with a filter.
     *
     * @see org.osgi.framework.BundleContext#addServiceListener
     */
    public void addServiceListener(ServiceListener listener, String filter)
	throws InvalidSyntaxException {
	isBCvalid();
	framework.listeners.addServiceListener(bundle, listener, filter);
    }
    
    
    /**
     * Add a service listener.
     *
     * @see org.osgi.framework.BundleContext#addServiceListener
     */
    public void addServiceListener(ServiceListener listener)
    {
	isBCvalid();
	try {
	    framework.listeners.addServiceListener(bundle, listener, null);
	} catch (InvalidSyntaxException neverHappens) { }
    }
    
    
    /**
     * Remove a service listener.
     *
     * @see org.osgi.framework.BundleContext#removeServiceListener
     */
    public void removeServiceListener(ServiceListener listener)
    {
	isBCvalid();
	framework.listeners.removeServiceListener(bundle, listener);
    }
    
    
    /**
     * Add a bundle listener.
     *
     * @see org.osgi.framework.BundleContext#addBundleListener
     */
    public void addBundleListener(BundleListener listener)
    {
	isBCvalid();
	if (listener instanceof SynchronousBundleListener) {
	    framework.checkAdminPermission();
	}
	framework.listeners.addBundleListener(bundle, listener);
    }
    
    
    /**
     * Remove a bundle listener.
     *
     * @see org.osgi.framework.BundleContext#removeBundleListener
     */
    public void removeBundleListener(BundleListener listener)
    {
	isBCvalid();
	if (listener instanceof SynchronousBundleListener) {
	    framework.checkAdminPermission();
	}
	framework.listeners.removeBundleListener(bundle, listener);
    }
    
    
    /**
     * Add a framework listener.
     *
     * @see org.osgi.framework.BundleContext#addFrameworkListener
     */
    public void addFrameworkListener(FrameworkListener listener)
    {
	isBCvalid();
	framework.listeners.addFrameworkListener(bundle, listener);
    }
    
    
    /**
     * Remove a framework listener.
     *
     * @see org.osgi.framework.BundleContext#removeFrameworkListener
     */
    public void removeFrameworkListener(FrameworkListener listener)
    {
	isBCvalid();
	framework.listeners.removeFrameworkListener(bundle, listener);
    }
    
    
    /**
     * Register a service with multiple names.
     *
     * @see org.osgi.framework.BundleContext#registerService
     */
    public ServiceRegistration registerService(String[] clazzes,
					       Object service,
					       Dictionary properties)
    {
	isBCvalid();
	String [] classes = (String[]) clazzes.clone();
	return framework.services.register(bundle, classes, service, properties);
    }
    
    
    /**
     * Register a service with a single name.
     *
     * @see org.osgi.framework.BundleContext#registerService
     */
    public ServiceRegistration registerService(String clazz,
					       Object service,
					       Dictionary properties)
    {
	isBCvalid();
	String [] classes =  new String [] { clazz };
	return framework.services.register(bundle, classes, service, properties);
    }
    
    
    /**
     * Get a list of service references.
     *
     * @see org.osgi.framework.BundleContext#getServiceReferences
     */
    public ServiceReference[] getServiceReferences(String clazz, String filter)
	throws InvalidSyntaxException
    {
	isBCvalid();
	if (framework.checkPermissions) {
	    try {
		String c = (clazz != null) ? clazz : "*";
		AccessController.checkPermission(new ServicePermission(c, ServicePermission.GET));
	    } catch (AccessControlException ignore) {
		return null;
	    }
	}
	return framework.services.get(clazz, filter);
    }
    
    
    /**
     * Get a service reference.
     *
     * @see org.osgi.framework.BundleContext#getServiceReference
     */
    public ServiceReference getServiceReference(String clazz)
    {
	isBCvalid();
	if (framework.checkPermissions) {
	    try { 
		String c = (clazz != null) ? clazz : "*";
		AccessController.checkPermission(new ServicePermission(c, ServicePermission.GET));
	    } catch (AccessControlException ignore) {
		return null;
	    }
	}
	return framework.services.get(clazz);
    }
    
    
    /**
     * Get the service object.
     *
     * @see org.osgi.framework.BundleContext#getService
     */
    public Object getService(ServiceReference reference)
    {
	isBCvalid();
	return ((ServiceReferenceImpl)reference).getService(bundle);
    }
    
    
    /**
     * Unget the service object.
     *
     * @see org.osgi.framework.BundleContext#ungetService
     */
    public boolean ungetService(ServiceReference reference)
    {
	isBCvalid();
	return ((ServiceReferenceImpl)reference).ungetService(bundle, true);
    }
    
    
    /**
     * Creates a File object for a file in the persistent storage
     * area provided for the bundle.
     *
     * @see org.osgi.framework.BundleContext#getDataFile
     */
    public File getDataFile(String filename)
    {
	isBCvalid();
	File dataRoot = bundle.getDataRoot();
	if (!dataRoot.exists()) {
	    dataRoot.mkdirs();
	}
	return new File(dataRoot, filename);
    }
    
    /**
     * Constructs a Filter object. This filter object may be used
     * to match a {@link ServiceReference} or a Dictionary.
     *
     * @param filter the filter string.
     * @return the Filter object encapsulating the filter string.
     * @exception InvalidSyntaxException If the filter parameter contains
     * an invalid filter string which cannot be parsed.
     *
     * @since 1.1
     */
    public Filter createFilter(String filter) throws InvalidSyntaxException
    {
	isBCvalid();
	return new FilterImpl(filter);
    }
    
    //
    // Package methods
    //
    
    /**
     * Check that the bundle is still valid.
     *
     * @return true if valid.
     * @exception IllegalStateException, if bundle isn't active.
     */
    void isBCvalid()
    {
	if (bundle == null) {
	    throw new IllegalStateException("This bundle context is no longer valid");
	}
    }
    
}
