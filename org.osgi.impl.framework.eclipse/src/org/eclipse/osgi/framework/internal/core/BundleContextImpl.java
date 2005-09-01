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

package org.eclipse.osgi.framework.internal.core;

import java.io.File;
import java.io.InputStream;
import java.security.*;
import java.util.*;
import org.eclipse.osgi.event.BatchBundleListener;
import org.eclipse.osgi.framework.debug.Debug;
import org.eclipse.osgi.framework.eventmgr.EventDispatcher;
import org.eclipse.osgi.framework.eventmgr.EventListeners;
import org.eclipse.osgi.internal.profile.Profile;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.*;

/**
 * Bundle's execution context.
 *
 * This object is given out to bundles and wraps the internal
 * BundleContext object. It is destroyed when a bundle is stopped.
 */

public class BundleContextImpl implements BundleContext, EventDispatcher {
	public static final String PROP_SCOPE_SERVICE_EVENTS = "osgi.scopeServiceEvents"; //$NON-NLS-1$
	public static final boolean scopeEvents = Boolean.valueOf(System.getProperty(PROP_SCOPE_SERVICE_EVENTS, "true")).booleanValue(); //$NON-NLS-1$
	/** true if the bundle context is still valid */
	private boolean valid;

	/** Bundle object this context is associated with. */
	// This slot is accessed directly by the Framework instead of using
	// the getBundle() method because the Framework needs access to the bundle
	// even when the context is invalid while the close method is being called.
	protected BundleHost bundle;

	/** Internal framework object. */
	protected Framework framework;

	/** Services that bundle has used. Key is ServiceReference,
	 Value is ServiceUse */
	protected Hashtable servicesInUse;

	/** Listener list for bundle's BundleListeners */
	protected EventListeners bundleEvent;

	/** Listener list for bundle's SynchronousBundleListeners */
	protected EventListeners bundleEventSync;

	/** Listener list for bundle's ServiceListeners */
	protected EventListeners serviceEvent;

	/** Listener list for bundle's FrameworkListeners */
	protected EventListeners frameworkEvent;

	/** The current instantiation of the activator. */
	protected BundleActivator activator;

	/** private object for locking */
	protected Object contextLock = new Object();

	/**
	 * Construct a BundleContext which wrappers the framework for a
	 * bundle
	 *
	 * @param bundle The bundle we are wrapping.
	 */
	protected BundleContextImpl(BundleHost bundle) {
		this.bundle = bundle;
		valid = true;
		framework = bundle.framework;
		bundleEvent = null;
		bundleEventSync = null;
		serviceEvent = null;
		frameworkEvent = null;
		servicesInUse = null;
		activator = null;
	}

	/**
	 * Destroy the wrapper. This is called when the bundle is stopped.
	 *
	 */
	protected void close() {
		valid = false; /* invalidate context */

		if (serviceEvent != null) {
			framework.serviceEvent.removeListener(this);
			serviceEvent = null;
		}
		if (frameworkEvent != null) {
			framework.frameworkEvent.removeListener(this);
			frameworkEvent = null;
		}
		if (bundleEvent != null) {
			framework.bundleEvent.removeListener(this);
			bundleEvent = null;
		}
		if (bundleEventSync != null) {
			framework.bundleEventSync.removeListener(this);
			bundleEventSync = null;
		}

		/* service's registered by the bundle, if any, are unregistered. */
		ServiceReference[] publishedReferences = null;
		synchronized (framework.serviceRegistry) {
			publishedReferences = framework.serviceRegistry.lookupServiceReferences(this);
		}

		if (publishedReferences != null) {
			for (int i = 0; i < publishedReferences.length; i++) {
				try {
					((ServiceReferenceImpl) publishedReferences[i]).registration.unregister();
				} catch (IllegalStateException e) {
					/* already unregistered */
				}
			}
		}

		/* service's used by the bundle, if any, are released. */
		if (servicesInUse != null) {
			int usedSize;
			ServiceReference[] usedRefs = null;

			synchronized (servicesInUse) {
				usedSize = servicesInUse.size();

				if (usedSize > 0) {
					if (Debug.DEBUG && Debug.DEBUG_SERVICES) {
						Debug.println("Releasing services"); //$NON-NLS-1$
					}

					usedRefs = new ServiceReference[usedSize];

					Enumeration refsEnum = servicesInUse.keys();
					for (int i = 0; i < usedSize; i++) {
						usedRefs[i] = (ServiceReference) refsEnum.nextElement();
					}
				}
			}

			for (int i = 0; i < usedSize; i++) {
				((ServiceReferenceImpl) usedRefs[i]).registration.releaseService(this);
			}

			servicesInUse = null;
		}

		bundle = null;
	}

	/**
	 * Retrieve the value of the named environment property.
	 *
	 * @param key The name of the requested property.
	 * @return The value of the requested property, or <code>null</code> if
	 * the property is undefined.
	 */
	public String getProperty(String key) {
		SecurityManager sm = System.getSecurityManager();

		if (sm != null) {
			sm.checkPropertyAccess(key);
		}

		return (framework.getProperty(key));
	}

	/**
	 * Retrieve the Bundle object for the context bundle.
	 *
	 * @return The context bundle's Bundle object.
	 */
	public org.osgi.framework.Bundle getBundle() {
		checkValid();

		return (bundle);
	}

	/**
	 * Install a bundle from a location.
	 *
	 * The bundle is obtained from the location
	 * parameter as interpreted by the framework
	 * in an implementation dependent way. Typically, location
	 * will most likely be a URL.
	 *
	 * @param location The location identifier of the bundle to install.
	 * @return The Bundle object of the installed bundle.
	 */
	public org.osgi.framework.Bundle installBundle(String location) throws BundleException {
		checkValid();
		//note AdminPermission is checked after bundle is loaded
		return framework.installBundle(location);
	}

	/**
	 * Install a bundle from an InputStream.
	 *
	 * <p>This method performs all the steps listed in
	 * {@link #installBundle(java.lang.String)}, except the
	 * bundle's content will be read from the InputStream.
	 * The location identifier specified will be used
	 * as the identity of the bundle.
	 *
	 * @param location The location identifier of the bundle to install.
	 * @param in The InputStream from which the bundle will be read.
	 * @return The Bundle of the installed bundle.
	 */
	public org.osgi.framework.Bundle installBundle(String location, InputStream in) throws BundleException {
		checkValid();
		//note AdminPermission is checked after bundle is loaded
		return framework.installBundle(location, in);
	}

	/**
	 * Retrieve the bundle that has the given unique identifier.
	 *
	 * @param id The identifier of the bundle to retrieve.
	 * @return A Bundle object, or <code>null</code>
	 * if the identifier doesn't match any installed bundle.
	 */
	public org.osgi.framework.Bundle getBundle(long id) {
		return (framework.getBundle(id));
	}

	/**
	 * Retrieve the bundle that has the given location.
	 *
	 * @param location The location string of the bundle to retrieve.
	 * @return A Bundle object, or <code>null</code>
	 * if the location doesn't match any installed bundle.
	 */
	public AbstractBundle getBundleByLocation(String location) {
		return (framework.getBundleByLocation(location));
	}

	/**
	 * Retrieve a list of all installed bundles.
	 * The list is valid at the time
	 * of the call to getBundles, but the framework is a very dynamic
	 * environment and bundles can be installed or uninstalled at anytime.
	 *
	 * @return An array of {@link AbstractBundle} objects, one
	 * object per installed bundle.
	 */
	public org.osgi.framework.Bundle[] getBundles() {
		return framework.getAllBundles();
	}

	/**
	 * Add a service listener with a filter.
	 * {@link ServiceListener}s are notified when a service has a lifecycle
	 * state change.
	 * See {@link #getServiceReferences(String, String) getServiceReferences}
	 * for a description of the filter syntax.
	 * The listener is added to the context bundle's list of listeners.
	 * See {@link #getBundle() getBundle()}
	 * for a definition of context bundle.
	 *
	 * <p>The listener is called if the filter criteria is met.
	 * To filter based upon the class of the service, the filter
	 * should reference the "objectClass" property.
	 * If the filter paramater is <code>null</code>, all services
	 * are considered to match the filter.
	 * <p>If the Java runtime environment supports permissions, then additional
	 * filtering is done.
	 * {@link AbstractBundle#hasPermission(Object) Bundle.hasPermission} is called for the
	 * bundle which defines the listener to validate that the listener has the
	 * {@link ServicePermission} permission to <code>"get"</code> the service
	 * using at least one of the named classes the service was registered under.
	 *
	 * @param listener The service listener to add.
	 * @param filter The filter criteria.
	 * @exception InvalidSyntaxException If the filter parameter contains
	 * an invalid filter string which cannot be parsed.
	 * @see ServiceEvent
	 * @see ServiceListener
	 * @exception java.lang.IllegalStateException
	 * If the bundle context has stopped.
	 */
	public void addServiceListener(ServiceListener listener, String filter) throws InvalidSyntaxException {
		checkValid();

		if (Debug.DEBUG && Debug.DEBUG_EVENTS) {
			String listenerName = listener.getClass().getName() + "@" + Integer.toHexString(listener.hashCode()); //$NON-NLS-1$
			Debug.println("addServiceListener[" + bundle + "](" + listenerName + ", \"" + filter + "\")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}

		ServiceListener filteredListener = new FilteredServiceListener(filter, listener, this);

		synchronized (framework.serviceEvent) {
			if (serviceEvent == null) {
				serviceEvent = new EventListeners();
				framework.serviceEvent.addListener(this, this);
			}

			serviceEvent.addListener(listener, filteredListener);
		}
	}

	/**
	 * Add a service listener.
	 *
	 * <p>This method is the same as calling
	 * {@link #addServiceListener(ServiceListener, String)}
	 * with filter set to <code>null</code>.
	 *
	 * @see #addServiceListener(ServiceListener, String)
	 */
	public void addServiceListener(ServiceListener listener) {
		try {
			addServiceListener(listener, null);
		} catch (InvalidSyntaxException e) {
			if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
				Debug.println("InvalidSyntaxException w/ null filter" + e.getMessage()); //$NON-NLS-1$
				Debug.printStackTrace(e);
			}
		}
	}

	/**
	 * Remove a service listener.
	 * The listener is removed from the context bundle's list of listeners.
	 * See {@link #getBundle() getBundle()}
	 * for a definition of context bundle.
	 *
	 * <p>If this method is called with a listener which is not registered,
	 * then this method does nothing.
	 *
	 * @param listener The service listener to remove.
	 * @exception java.lang.IllegalStateException
	 * If the bundle context has stopped.
	 */
	public void removeServiceListener(ServiceListener listener) {
		checkValid();

		if (Debug.DEBUG && Debug.DEBUG_SERVICES) {
			String listenerName = listener.getClass().getName() + "@" + Integer.toHexString(listener.hashCode()); //$NON-NLS-1$
			Debug.println("removeServiceListener[" + bundle + "](" + listenerName + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		if (serviceEvent != null) {
			synchronized (framework.serviceEvent) {
				serviceEvent.removeListener(listener);
			}
		}
	}

	/**
	 * Add a bundle listener.
	 * {@link BundleListener}s are notified when a bundle has a lifecycle
	 * state change.
	 * The listener is added to the context bundle's list of listeners.
	 * See {@link #getBundle() getBundle()}
	 * for a definition of context bundle.
	 *
	 * @param listener The bundle listener to add.
	 * @exception java.lang.IllegalStateException
	 * If the bundle context has stopped.
	 * @see BundleEvent
	 * @see BundleListener
	 */
	public void addBundleListener(BundleListener listener) {
		checkValid();

		if (Debug.DEBUG && Debug.DEBUG_EVENTS) {
			String listenerName = listener.getClass().getName() + "@" + Integer.toHexString(listener.hashCode()); //$NON-NLS-1$
			Debug.println("addBundleListener[" + bundle + "](" + listenerName + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		if (listener instanceof SynchronousBundleListener) {
			framework.checkAdminPermission(getBundle(), AdminPermission.LISTENER);
			synchronized (framework.bundleEventSync) {
				if (bundleEventSync == null) {
					bundleEventSync = new EventListeners();
					framework.bundleEventSync.addListener(this, this);
				}

				bundleEventSync.addListener(listener, listener);
			}
		} else {
			synchronized (framework.bundleEvent) {
				if (bundleEvent == null) {
					bundleEvent = new EventListeners();
					framework.bundleEvent.addListener(this, this);
				}

				bundleEvent.addListener(listener, listener);
			}
		}
	}

	/**
	 * Remove a bundle listener.
	 * The listener is removed from the context bundle's list of listeners.
	 * See {@link #getBundle() getBundle()}
	 * for a definition of context bundle.
	 *
	 * <p>If this method is called with a listener which is not registered,
	 * then this method does nothing.
	 *
	 * @param listener The bundle listener to remove.
	 * @exception java.lang.IllegalStateException
	 * If the bundle context has stopped.
	 */
	public void removeBundleListener(BundleListener listener) {
		checkValid();

		if (Debug.DEBUG && Debug.DEBUG_EVENTS) {
			String listenerName = listener.getClass().getName() + "@" + Integer.toHexString(listener.hashCode()); //$NON-NLS-1$
			Debug.println("removeBundleListener[" + bundle + "](" + listenerName + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		if (listener instanceof SynchronousBundleListener) {
			framework.checkAdminPermission(getBundle(), AdminPermission.LISTENER);

			if (bundleEventSync != null) {
				synchronized (framework.bundleEventSync) {
					bundleEventSync.removeListener(listener);
				}
			}
		} else {
			if (bundleEvent != null) {
				synchronized (framework.bundleEvent) {
					bundleEvent.removeListener(listener);
				}
			}
		}
	}

	/**
	 * Add a general framework listener.
	 * {@link FrameworkListener}s are notified of general framework events.
	 * The listener is added to the context bundle's list of listeners.
	 * See {@link #getBundle() getBundle()}
	 * for a definition of context bundle.
	 *
	 * @param listener The framework listener to add.
	 * @exception java.lang.IllegalStateException
	 * If the bundle context has stopped.
	 * @see FrameworkEvent
	 * @see FrameworkListener
	 */
	public void addFrameworkListener(FrameworkListener listener) {
		checkValid();

		if (Debug.DEBUG && Debug.DEBUG_EVENTS) {
			String listenerName = listener.getClass().getName() + "@" + Integer.toHexString(listener.hashCode()); //$NON-NLS-1$
			Debug.println("addFrameworkListener[" + bundle + "](" + listenerName + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		synchronized (framework.frameworkEvent) {
			if (frameworkEvent == null) {
				frameworkEvent = new EventListeners();
				framework.frameworkEvent.addListener(this, this);
			}

			frameworkEvent.addListener(listener, listener);
		}
	}

	/**
	 * Remove a framework listener.
	 * The listener is removed from the context bundle's list of listeners.
	 * See {@link #getBundle() getBundle()}
	 * for a definition of context bundle.
	 *
	 * <p>If this method is called with a listener which is not registered,
	 * then this method does nothing.
	 *
	 * @param listener The framework listener to remove.
	 * @exception java.lang.IllegalStateException
	 * If the bundle context has stopped.
	 */
	public void removeFrameworkListener(FrameworkListener listener) {
		checkValid();

		if (Debug.DEBUG && Debug.DEBUG_EVENTS) {
			String listenerName = listener.getClass().getName() + "@" + Integer.toHexString(listener.hashCode()); //$NON-NLS-1$
			Debug.println("removeFrameworkListener[" + bundle + "](" + listenerName + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		if (frameworkEvent != null) {
			synchronized (framework.frameworkEvent) {
				frameworkEvent.removeListener(listener);
			}
		}
	}

	/**
	 * Register a service with multiple names.
	 * This method registers the given service object with the given properties
	 * under the given class names.
	 * A {@link ServiceRegistrationImpl} object is returned.
	 * The {@link ServiceRegistrationImpl} object is for the private use of the bundle
	 * registering the service and should not be shared with other bundles.
	 * The registering bundle is defined to be the context bundle.
	 * See {@link #getBundle()} for a definition of context bundle.
	 * Other bundles can locate the service by using either the
	 * {@link #getServiceReferences getServiceReferences} or
	 * {@link #getServiceReference getServiceReference} method.
	 *
	 * <p>A bundle can register a service object that implements the
	 * {@link ServiceFactory} interface to
	 * have more flexiblity in providing service objects to different
	 * bundles.
	 *
	 * <p>The following steps are followed to register a service:
	 * <ol>
	 * <li>If the service parameter is not a {@link ServiceFactory},
	 * an <code>IllegalArgumentException</code> is thrown if the
	 * service parameter is not an <code>instanceof</code>
	 * all the classes named.
	 * <li>The service is added to the framework's service registry
	 * and may now be used by other bundles.
	 * <li>A {@link ServiceEvent} of type {@link ServiceEvent#REGISTERED}
	 * is synchronously sent.
	 * <li>A {@link ServiceRegistrationImpl} object for this registration
	 * is returned.
	 * </ol>
	 *
	 * @param clazzes The class names under which the service can be located.
	 *                The class names in this array will be stored in the service's
	 *                properties under the key "objectClass".
	 * @param service The service object or a {@link ServiceFactory} object.
	 * @param properties The properties for this service.
	 *        The keys in the properties object must all be Strings.
	 *        Changes should not be made to this object after calling this method.
	 *        To update the service's properties call the
	 *        {@link ServiceRegistrationImpl#setProperties ServiceRegistration.setProperties}
	 *        method.
	 *        This parameter may be <code>null</code> if the service has no properties.
	 * @return A {@link ServiceRegistrationImpl} object for use by the bundle
	 *        registering the service to update the
	 *        service's properties or to unregister the service.
	 * @exception java.lang.IllegalArgumentException If one of the following is true:
	 * <ul>
	 * <li>The service parameter is null.
	 * <li>The service parameter is not a {@link ServiceFactory} and is not an
	 * <code>instanceof</code> all the named classes in the clazzes parameter.
	 * </ul>
	 * @exception java.lang.SecurityException If the caller does not have
	 * {@link ServicePermission} permission to "register" the service for
	 * all the named classes
	 * and the Java runtime environment supports permissions.
	 * @exception java.lang.IllegalStateException
	 * If the bundle context has stopped.
	 * @see ServiceRegistrationImpl
	 * @see ServiceFactory
	 */
	public org.osgi.framework.ServiceRegistration registerService(String[] clazzes, Object service, Dictionary properties) {
		checkValid();

		if (service == null) {
			if (Debug.DEBUG && Debug.DEBUG_SERVICES) {
				Debug.println("Service object is null"); //$NON-NLS-1$
			}

			throw new NullPointerException(Msg.SERVICE_ARGUMENT_NULL_EXCEPTION); 
		}

		int size = clazzes.length;

		if (size == 0) {
			if (Debug.DEBUG && Debug.DEBUG_SERVICES) {
				Debug.println("Classes array is empty"); //$NON-NLS-1$
			}

			throw new IllegalArgumentException(Msg.SERVICE_EMPTY_CLASS_LIST_EXCEPTION); 
		}

		/* copy the array so that changes to the original will not affect us. */
		String[] copy = new String[clazzes.length];
		for (int i = 0; i < clazzes.length; i++) {
			copy[i] = new String(clazzes[i].getBytes());
		}
		clazzes = copy;

		/* check for ServicePermissions. */
		framework.checkRegisterServicePermission(clazzes);

		if (!(service instanceof ServiceFactory)) {
			String invalidService = checkServiceClass(clazzes, service);
			if (invalidService != null) {
				if (Debug.DEBUG && Debug.DEBUG_SERVICES) {
					Debug.println("Service object is not an instanceof " + invalidService); //$NON-NLS-1$
				}
				throw new IllegalArgumentException(NLS.bind(Msg.SERVICE_NOT_INSTANCEOF_CLASS_EXCEPTION, invalidService)); 
			}
		}

		return (createServiceRegistration(clazzes, service, properties));
	}

	//Return the name of the class that is not satisfied by the service object 
	static String checkServiceClass(final String[] clazzes, final Object serviceObject) {
		ClassLoader cl = (ClassLoader) AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				return serviceObject.getClass().getClassLoader();
			}
		});
		for (int i = 0; i < clazzes.length; i++) {
			try {
				Class serviceClazz = cl == null ? Class.forName(clazzes[i]) : cl.loadClass(clazzes[i]);
				if (!serviceClazz.isInstance(serviceObject))
					return clazzes[i];
			} catch (ClassNotFoundException e) {
				//This check is rarely done
				if (extensiveCheckServiceClass(clazzes[i], serviceObject.getClass()))
					return clazzes[i];
			}
		}
		return null;
	}

	private static boolean extensiveCheckServiceClass(String clazz, Class serviceClazz) {
		if (clazz.equals(serviceClazz.getName()))
			return false;
		Class[] interfaces = serviceClazz.getInterfaces();
		for (int i = 0; i < interfaces.length; i++)
			if (!extensiveCheckServiceClass(clazz, interfaces[i]))
				return false;
		Class superClazz = serviceClazz.getSuperclass();
		if (superClazz != null)
			if (!extensiveCheckServiceClass(clazz, superClazz))
				return false;
		return true;
	}

	/**
	 * Create a new ServiceRegistration object. This method is used so that it may be overridden
	 * by a secure implementation.
	 *
	 * @param clazzes The class names under which the service can be located.
	 * @param service The service object or a {@link ServiceFactory} object.
	 * @param properties The properties for this service.
	 * @return A {@link ServiceRegistrationImpl} object for use by the bundle.
	 */
	protected ServiceRegistrationImpl createServiceRegistration(String[] clazzes, Object service, Dictionary properties) {
		return (new ServiceRegistrationImpl(this, clazzes, service, properties));
	}

	/**
	 * Register a service with a single name.
	 * This method registers the given service object with the given properties
	 * under the given class name.
	 *
	 * <p>This method is otherwise identical to
	 * {@link #registerService(java.lang.String[], java.lang.Object, java.util.Dictionary)}
	 * and is provided as a convenience when the service parameter will only be registered
	 * under a single class name.
	 *
	 * @see #registerService(java.lang.String[], java.lang.Object, java.util.Dictionary)
	 */
	public org.osgi.framework.ServiceRegistration registerService(String clazz, Object service, Dictionary properties) {
		String[] clazzes = new String[] {clazz};

		return (registerService(clazzes, service, properties));
	}

	/**
	 * Returns a list of <tt>ServiceReference</tt> objects. This method returns a list of
	 * <tt>ServiceReference</tt> objects for services which implement and were registered under
	 * the specified class and match the specified filter criteria.
	 *
	 * <p>The list is valid at the time of the call to this method, however as the Framework is
	 * a very dynamic environment, services can be modified or unregistered at anytime.
	 *
	 * <p><tt>filter</tt> is used to select the registered service whose
	 * properties objects contain keys and values which satisfy the filter.
	 * See {@link FilterImpl}for a description of the filter string syntax.
	 *
	 * <p>If <tt>filter</tt> is <tt>null</tt>, all registered services
	 * are considered to match the filter.
	 * <p>If <tt>filter</tt> cannot be parsed, an {@link InvalidSyntaxException}will
	 * be thrown with a human readable message where the filter became unparsable.
	 *
	 * <p>The following steps are required to select a service:
	 * <ol>
	 * <li>If the Java Runtime Environment supports permissions, the caller is checked for the
	 * <tt>ServicePermission</tt> to get the service with the specified class.
	 * If the caller does not have the correct permission, <tt>null</tt> is returned.
	 * <li>If the filter string is not <tt>null</tt>, the filter string is
	 * parsed and the set of registered services which satisfy the filter is
	 * produced.
	 * If the filter string is <tt>null</tt>, then all registered services
	 * are considered to satisfy the filter.
	 * <li>If <code>clazz</code> is not <tt>null</tt>, the set is further reduced to
	 * those services which are an <tt>instanceof</tt> and were registered under the specified class.
	 * The complete list of classes of which a service is an instance and which
	 * were specified when the service was registered is available from the
	 * service's {@link Constants#OBJECTCLASS}property.
	 * <li>An array of <tt>ServiceReference</tt> to the selected services is returned.
	 * </ol>
	 *
	 * @param clazz The class name with which the service was registered, or
	 * <tt>null</tt> for all services.
	 * @param filter The filter criteria.
	 * @return An array of <tt>ServiceReference</tt> objects, or
	 * <tt>null</tt> if no services are registered which satisfy the search.
	 * @exception InvalidSyntaxException If <tt>filter</tt> contains
	 * an invalid filter string which cannot be parsed.
	 */
	public org.osgi.framework.ServiceReference[] getServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
		checkValid();
		if (Debug.DEBUG && Debug.DEBUG_SERVICES) {
			Debug.println("getServiceReferences(" + clazz + ", \"" + filter + "\")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		return (framework.getServiceReferences(clazz, filter, this, false));
	}

	public ServiceReference[] getAllServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
		checkValid();
		if (Debug.DEBUG && Debug.DEBUG_SERVICES) {
			Debug.println("getAllServiceReferences(" + clazz + ", \"" + filter + "\")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		return (framework.getServiceReferences(clazz, filter, this, true));
	}

	/**
	 * Get a service reference.
	 * Retrieves a {@link ServiceReferenceImpl} for a service
	 * which implements the named class.
	 *
	 * <p>This reference is valid at the time
	 * of the call to this method, but since the framework is a very dynamic
	 * environment, services can be modified or unregistered at anytime.
	 *
	 * <p>This method is provided as a convenience for when the caller is
	 * interested in any service which implements a named class. This method is
	 * the same as calling {@link #getServiceReferences getServiceReferences}
	 * with a <code>null</code> filter string but only a single {@link ServiceReferenceImpl}
	 * is returned.
	 *
	 * @param clazz The class name which the service must implement.
	 * @return A {@link ServiceReferenceImpl} object, or <code>null</code>
	 * if no services are registered which implement the named class.
	 * @see #getServiceReferences
	 */
	public org.osgi.framework.ServiceReference getServiceReference(String clazz) {
		checkValid();

		if (Debug.DEBUG && Debug.DEBUG_SERVICES) {
			Debug.println("getServiceReference(" + clazz + ")"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		try {
			ServiceReference[] references = framework.getServiceReferences(clazz, null, this, false);

			if (references != null) {
				int index = 0;

				int length = references.length;

				if (length > 1) /* if more than one service, select highest ranking */{
					int rankings[] = new int[length];
					int count = 0;
					int maxRanking = Integer.MIN_VALUE;

					for (int i = 0; i < length; i++) {
						int ranking = ((ServiceReferenceImpl) references[i]).getRanking();

						rankings[i] = ranking;

						if (ranking > maxRanking) {
							index = i;
							maxRanking = ranking;
							count = 1;
						} else {
							if (ranking == maxRanking) {
								count++;
							}
						}
					}

					if (count > 1) /* if still more than one service, select lowest id */{
						long minId = Long.MAX_VALUE;

						for (int i = 0; i < length; i++) {
							if (rankings[i] == maxRanking) {
								long id = ((ServiceReferenceImpl) references[i]).getId();

								if (id < minId) {
									index = i;
									minId = id;
								}
							}
						}
					}
				}

				return (references[index]);
			}
		} catch (InvalidSyntaxException e) {
			if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
				Debug.println("InvalidSyntaxException w/ null filter" + e.getMessage()); //$NON-NLS-1$
				Debug.printStackTrace(e);
			}
		}

		return (null);
	}

	/**
	 * Get a service's service object.
	 * Retrieves the service object for a service.
	 * A bundle's use of a service is tracked by a
	 * use count. Each time a service's service object is returned by
	 * {@link #getService}, the context bundle's use count for the service
	 * is incremented by one. Each time the service is release by
	 * {@link #ungetService}, the context bundle's use count
	 * for the service is decremented by one.
	 * When a bundle's use count for a service
	 * drops to zero, the bundle should no longer use the service.
	 * See {@link #getBundle()} for a definition of context bundle.
	 *
	 * <p>This method will always return <code>null</code> when the
	 * service associated with this reference has been unregistered.
	 *
	 * <p>The following steps are followed to get the service object:
	 * <ol>
	 * <li>If the service has been unregistered,
	 * <code>null</code> is returned.
	 * <li>The context bundle's use count for this service is incremented by one.
	 * <li>If the context bundle's use count for the service is now one and
	 * the service was registered with a {@link ServiceFactory},
	 * the {@link ServiceFactory#getService ServiceFactory.getService} method
	 * is called to create a service object for the context bundle.
	 * This service object is cached by the framework.
	 * While the context bundle's use count for the service is greater than zero,
	 * subsequent calls to get the services's service object for the context bundle
	 * will return the cached service object.
	 * <br>If the service object returned by the {@link ServiceFactory}
	 * is not an <code>instanceof</code>
	 * all the classes named when the service was registered or
	 * the {@link ServiceFactory} throws an exception,
	 * <code>null</code> is returned and a
	 * {@link FrameworkEvent} of type {@link FrameworkEvent#ERROR} is broadcast.
	 * <li>The service object for the service is returned.
	 * </ol>
	 *
	 * @param reference A reference to the service whose service object is desired.
	 * @return A service object for the service associated with this
	 * reference, or <code>null</code> if the service is not registered.
	 * @exception java.lang.SecurityException If the caller does not have
	 * {@link ServicePermission} permission to "get" the service
	 * using at least one of the named classes the service was registered under
	 * and the Java runtime environment supports permissions.
	 * @exception java.lang.IllegalStateException
	 * If the bundle context has stopped.
	 * @see #ungetService
	 * @see ServiceFactory
	 */
	public Object getService(org.osgi.framework.ServiceReference reference) {
		checkValid();

		if (servicesInUse == null) {
			synchronized (contextLock) {
				if (servicesInUse == null) {
					// Cannot predict how many services a bundle will use, start with a small table.
					servicesInUse = new Hashtable(10);
				}
			}
		}

		ServiceRegistrationImpl registration = ((ServiceReferenceImpl) reference).registration;

		framework.checkGetServicePermission(registration.clazzes);

		return registration.getService(BundleContextImpl.this);
	}

	/**
	 * Unget a service's service object.
	 * Releases the service object for a service.
	 * If the context bundle's use count for the service is zero, this method
	 * returns <code>false</code>. Otherwise, the context bundle's use count for the
	 * service is decremented by one.
	 * See {@link #getBundle()} for a definition of context bundle.
	 *
	 * <p>The service's service object
	 * should no longer be used and all references to it should be destroyed
	 * when a bundle's use count for the service
	 * drops to zero.
	 *
	 * <p>The following steps are followed to unget the service object:
	 * <ol>
	 * <li>If the context bundle's use count for the service is zero or
	 * the service has been unregistered,
	 * <code>false</code> is returned.
	 * <li>The context bundle's use count for this service is decremented by one.
	 * <li>If the context bundle's use count for the service is now zero and
	 * the service was registered with a {@link ServiceFactory},
	 * the {@link ServiceFactory#ungetService ServiceFactory.ungetService} method
	 * is called to release the service object for the context bundle.
	 * <li><code>true</code> is returned.
	 * </ol>
	 *
	 * @param reference A reference to the service to be released.
	 * @return <code>false</code> if the context bundle's use count for the service
	 *         is zero or if the service has been unregistered,
	 *         otherwise <code>true</code>.
	 * @exception java.lang.IllegalStateException
	 * If the bundle context has stopped.
	 * @see #getService
	 * @see ServiceFactory
	 */
	public boolean ungetService(org.osgi.framework.ServiceReference reference) {
		checkValid();

		ServiceRegistrationImpl registration = ((ServiceReferenceImpl) reference).registration;

		return registration.ungetService(BundleContextImpl.this);
	}

	/**
	 * Creates a <code>File</code> object for a file in the
	 * persistent storage area provided for the bundle by the framework.
	 * If the adaptor does not have file system support, this method will
	 * return <code>null</code>.
	 *
	 * <p>A <code>File</code> object for the base directory of the
	 * persistent storage area provided for the context bundle by the framework
	 * can be obtained by calling this method with the empty string ("")
	 * as the parameter.
	 * See {@link #getBundle()} for a definition of context bundle.
	 *
	 * <p>If the Java runtime environment supports permissions,
	 * the framework the will ensure that the bundle has
	 * <code>java.io.FilePermission</code> with actions
	 * "read","write","execute","delete" for all files (recursively) in the
	 * persistent storage area provided for the context bundle by the framework.
	 *
	 * @param filename A relative name to the file to be accessed.
	 * @return A <code>File</code> object that represents the requested file or
	 * <code>null</code> if the adaptor does not have file system support.
	 * @exception java.lang.IllegalStateException
	 * If the bundle context has stopped.
	 */
	public File getDataFile(String filename) {
		checkValid();

		return (framework.getDataFile(bundle, filename));
	}

	/**
	 * Call bundle's BundleActivator.start()
	 * This method is called by Bundle.startWorker to start the bundle.
	 *
	 * @exception org.osgi.framework.BundleException if
	 *            the bundle has a class that implements the BundleActivator interface,
	 *            but Framework couldn't instantiate it, or the BundleActivator.start()
	 *            method failed
	 */
	protected void start() throws BundleException {
		activator = bundle.loadBundleActivator();

		if (activator != null) {
			try {
				startActivator(activator);
			} catch (BundleException be) {
				activator = null;
				throw be;
			}
		}

		/* activator completed successfully. We must use this
		 same activator object when we stop this bundle. */
	}

	/**
	 * Calls the start method of a BundleActivator.
	 * @param bundleActivator that activator to start
	 */
	protected void startActivator(final BundleActivator bundleActivator) throws BundleException {
		if (Profile.PROFILE && Profile.STARTUP)
			Profile.logEnter("BundleContextImpl.startActivator()", null); //$NON-NLS-1$
		try {
			AccessController.doPrivileged(new PrivilegedExceptionAction() {
				public Object run() throws Exception {
					if (bundleActivator != null) {
						if (Profile.PROFILE && Profile.STARTUP)
							Profile.logTime("BundleContextImpl.startActivator()", "calling " + bundle.getLocation() + " bundle activator"); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
						/* Start the bundle synchronously */
						bundleActivator.start(BundleContextImpl.this);
						if (Profile.PROFILE && Profile.STARTUP)
							Profile.logTime("BundleContextImpl.startActivator()", "returned from " + bundle.getLocation() + " bundle activator"); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
					}
					return null;
				}
			});
		} catch (Throwable t) {
			if (t instanceof PrivilegedActionException) {
				t = ((PrivilegedActionException) t).getException();
			}

			if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
				Debug.printStackTrace(t);
			}

			String clazz = null;
			clazz = bundleActivator.getClass().getName();

			throw new BundleException(NLS.bind(Msg.BUNDLE_ACTIVATOR_EXCEPTION, new Object[] {clazz, "start", bundle.getSymbolicName() == null ? "" + bundle.getBundleId() : bundle.getSymbolicName()}), t); //$NON-NLS-1$ //$NON-NLS-2$ 
		} finally {
			if (Profile.PROFILE && Profile.STARTUP)
				Profile.logExit("BundleContextImpl.startActivator()"); //$NON-NLS-1$
		}

	}

	/**
	 * Call bundle's BundleActivator.stop()
	 * This method is called by Bundle.stopWorker to stop the bundle.
	 *
	 * @exception org.osgi.framework.BundleException if
	 *            the bundle has a class that implements the BundleActivator interface,
	 *            and the BundleActivator.stop() method failed
	 */
	protected void stop() throws BundleException {
		try {
			AccessController.doPrivileged(new PrivilegedExceptionAction() {
				public Object run() throws Exception {
					if (activator != null) {
						/* Stop the bundle synchronously */
						activator.stop(BundleContextImpl.this);
					}
					return null;
				}
			});
		} catch (Throwable t) {
			if (t instanceof PrivilegedActionException) {
				t = ((PrivilegedActionException) t).getException();
			}

			if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
				Debug.printStackTrace(t);
			}

			String clazz = (activator == null) ? "" : activator.getClass().getName(); //$NON-NLS-1$

			throw new BundleException(NLS.bind(Msg.BUNDLE_ACTIVATOR_EXCEPTION, new Object[] {clazz, "stop", bundle.getSymbolicName() == null ? "" + bundle.getBundleId() : bundle.getSymbolicName()}), t); //$NON-NLS-1$ //$NON-NLS-2$ 
		} finally {
			activator = null;
		}
	}

	/**
	 * Provides a list of {@link ServiceReference}s for the services
	 * registered by this bundle
	 * or <code>null</code> if the bundle has no registered
	 * services.
	 *
	 * <p>The list is valid at the time
	 * of the call to this method, but the framework is a very dynamic
	 * environment and services can be modified or unregistered at anytime.
	 *
	 * @return An array of {@link ServiceReference} or <code>null</code>.
	 * @exception java.lang.IllegalStateException If the
	 * bundle has been uninstalled.
	 * @see ServiceRegistrationImpl
	 * @see ServiceReferenceImpl
	 */
	protected ServiceReference[] getRegisteredServices() {
		ServiceReference[] services = null;

		synchronized (framework.serviceRegistry) {
			services = framework.serviceRegistry.lookupServiceReferences(this);
			if (services == null) {
				return null;
			}
			int removed = 0;
			for (int i = services.length - 1; i >= 0; i--) {
				ServiceReferenceImpl ref = (ServiceReferenceImpl) services[i];
				String[] classes = ref.getClasses();
				try { /* test for permission to the classes */
					framework.checkGetServicePermission(classes);
				} catch (SecurityException se) {
					services[i] = null;
					removed++;
				}
			}
			if (removed > 0) {
				ServiceReference[] temp = services;
				services = new ServiceReference[temp.length - removed];
				for (int i = temp.length - 1; i >= 0; i--) {
					if (temp[i] == null)
						removed--;
					else
						services[i - removed] = temp[i];
				}
			}
		}
		return (services);

	}

	/**
	 * Provides a list of {@link ServiceReferenceImpl}s for the
	 * services this bundle is using,
	 * or <code>null</code> if the bundle is not using any services.
	 * A bundle is considered to be using a service if the bundle's
	 * use count for the service is greater than zero.
	 *
	 * <p>The list is valid at the time
	 * of the call to this method, but the framework is a very dynamic
	 * environment and services can be modified or unregistered at anytime.
	 *
	 * @return An array of {@link ServiceReferenceImpl} or <code>null</code>.
	 * @exception java.lang.IllegalStateException If the
	 * bundle has been uninstalled.
	 * @see ServiceReferenceImpl
	 */
	protected ServiceReferenceImpl[] getServicesInUse() {
		if (servicesInUse == null) {
			return (null);
		}

		synchronized (servicesInUse) {
			int size = servicesInUse.size();

			if (size == 0) {
				return (null);
			}

			ServiceReferenceImpl[] references = new ServiceReferenceImpl[size];
			int refcount = 0;

			Enumeration refsEnum = servicesInUse.keys();

			for (int i = 0; i < size; i++) {
				ServiceReferenceImpl reference = (ServiceReferenceImpl) refsEnum.nextElement();

				try {
					framework.checkGetServicePermission(reference.registration.clazzes);
				} catch (SecurityException se) {
					continue;
				}

				references[refcount] = reference;
				refcount++;
			}

			if (refcount < size) {
				if (refcount == 0) {
					return (null);
				}

				ServiceReferenceImpl[] refs = references;
				references = new ServiceReferenceImpl[refcount];

				System.arraycopy(refs, 0, references, 0, refcount);
			}

			return (references);
		}
	}

	/**
	 * Bottom level event dispatcher for the BundleContext.
	 *
	 * @param originalListener listener object registered under.
	 * @param l listener to call (may be filtered).
	 * @param action Event class type
	 * @param object Event object
	 */
	public void dispatchEvent(Object originalListener, Object l, int action, Object object) {
		// save the bundle ref to a local variable 
		// to avoid interference from another thread closing this context
		AbstractBundle tmpBundle = bundle;
		try {
			if (isValid()) /* if context still valid */{
				switch (action) {
					case Framework.BUNDLEEVENT :
					case Framework.BUNDLEEVENTSYNC : {
						BundleListener listener = (BundleListener) l;

						if (Debug.DEBUG && Debug.DEBUG_EVENTS) {
							String listenerName = listener.getClass().getName() + "@" + Integer.toHexString(listener.hashCode()); //$NON-NLS-1$
							Debug.println("dispatchBundleEvent[" + tmpBundle + "](" + listenerName + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						}

						BundleEvent event = (BundleEvent) object;
						switch (event.getType()) {
							case Framework.BATCHEVENT_BEGIN : {
								if (listener instanceof BatchBundleListener)
									((BatchBundleListener) listener).batchBegin();
								break;
							}
							case Framework.BATCHEVENT_END : {
								if (listener instanceof BatchBundleListener)
									((BatchBundleListener) listener).batchEnd();
								break;
							}
							default : {
								listener.bundleChanged((BundleEvent) object);
							}
						}
						break;
					}

					case Framework.SERVICEEVENT : {
						ServiceEvent event = (ServiceEvent) object;

						ServiceListener listener = (ServiceListener) l;
						if (Debug.DEBUG && Debug.DEBUG_EVENTS) {
							String listenerName = listener.getClass().getName() + "@" + Integer.toHexString(listener.hashCode()); //$NON-NLS-1$
							Debug.println("dispatchServiceEvent[" + tmpBundle + "](" + listenerName + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						}
						listener.serviceChanged(event);

						break;
					}

					case Framework.FRAMEWORKEVENT : {
						FrameworkListener listener = (FrameworkListener) l;

						if (Debug.DEBUG && Debug.DEBUG_EVENTS) {
							String listenerName = listener.getClass().getName() + "@" + Integer.toHexString(listener.hashCode()); //$NON-NLS-1$
							Debug.println("dispatchFrameworkEvent[" + tmpBundle + "](" + listenerName + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						}

						listener.frameworkEvent((FrameworkEvent) object);
						break;
					}
				}
			}
		} catch (Throwable t) {
			if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
				Debug.println("Exception in bottom level event dispatcher: " + t.getMessage()); //$NON-NLS-1$
				Debug.printStackTrace(t);
			}
			// allow the adaptor to handle this unexpected error
			framework.adaptor.handleRuntimeError(t);
			publisherror: {
				if (action == Framework.FRAMEWORKEVENT) {
					FrameworkEvent event = (FrameworkEvent) object;
					if (event.getType() == FrameworkEvent.ERROR) {
						break publisherror; // avoid infinite loop
					}
				}

				framework.publishFrameworkEvent(FrameworkEvent.ERROR, tmpBundle, t);
			}
		}
	}

	/**
	 * Check for permission to listen to a service.
	 */
	protected boolean hasListenServicePermission(ServiceEvent event) {
		ProtectionDomain domain = bundle.getProtectionDomain();

		if (domain != null) {
			ServiceReferenceImpl reference = (ServiceReferenceImpl) event.getServiceReference();

			String[] names = reference.registration.clazzes;

			int len = names.length;

			for (int i = 0; i < len; i++) {
				if (domain.implies(new ServicePermission(names[i], ServicePermission.GET))) {
					return true;
				}
			}

			return false;
		}

		return (true);
	}

	/**
	 * Construct a Filter object. This filter object may be used
	 * to match a ServiceReference or a Dictionary.
	 * See Filter
	 * for a description of the filter string syntax.
	 *
	 * @param filter The filter string.
	 * @return A Filter object encapsulating the filter string.
	 * @exception InvalidSyntaxException If the filter parameter contains
	 * an invalid filter string which cannot be parsed.
	 */
	public org.osgi.framework.Filter createFilter(String filter) throws InvalidSyntaxException {
		checkValid();

		return (new FilterImpl(filter));
	}

	/**
	 * This method checks that the context is still valid. If the context is
	 * no longer valid, an IllegalStateException is thrown.
	 *
	 * @exception java.lang.IllegalStateException
	 * If the context bundle has stopped.
	 */
	protected void checkValid() {
		if (!isValid()) {
			throw new IllegalStateException(Msg.BUNDLE_CONTEXT_INVALID_EXCEPTION); 
		}
	}

	/**
	 * This method checks that the context is still valid. 
	 *
	 * @return true if the context is still valid; false otherwise
	 */
	protected boolean isValid() {
		return valid;
	}

	boolean isAssignableTo(ServiceReferenceImpl reference) {
		if (!scopeEvents)
			return true;
		String[] clazzes = reference.getClasses();
		for (int i = 0; i < clazzes.length; i++)
			if (!reference.isAssignableTo(bundle, clazzes[i]))
				return false;
		return true;
	}
}
