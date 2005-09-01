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

import java.security.AccessController;
import java.security.PrivilegedAction;
import org.eclipse.osgi.framework.debug.Debug;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.*;

/**
 * This class represents the use of a service by a bundle. One is created for each
 * service acquired by a bundle. This class manages the calls to ServiceFactory
 * and the bundle's use count.
 */

public class ServiceUse {
	/** ServiceFactory object if the service instance represents a factory,
	 null otherwise */
	protected ServiceFactory factory;
	/** Service object either registered or that returned by
	 ServiceFactory.getService() */
	protected Object service;
	/** BundleContext associated with this service use */
	protected BundleContextImpl context;
	/** ServiceDescription of the registered service */
	protected ServiceRegistrationImpl registration;
	/** bundle's use count for this service */
	protected int useCount;

	/** Internal framework object. */

	/**
	 * Constructs a service use encapsulating the service object.
	 * Objects of this class should be constrcuted while holding the
	 * registrations lock.
	 *
	 * @param   context bundle getting the service
	 * @param   registration ServiceRegistration of the service
	 */
	protected ServiceUse(BundleContextImpl context, ServiceRegistrationImpl registration) {
		this.context = context;
		this.registration = registration;
		this.useCount = 0;

		Object service = registration.service;
		if (service instanceof ServiceFactory) {
			factory = (ServiceFactory) service;
			this.service = null;
		} else {
			this.factory = null;
			this.service = service;
		}
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
	 *
	 * <p>The following steps are followed to get the service object:
	 * <ol>
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
	 * @return A service object for the service associated with this
	 * reference.
	 */
	protected Object getService() {
		if ((useCount == 0) && (factory != null)) {
			AbstractBundle factorybundle = registration.context.bundle;
			Object service;

			try {
				service = AccessController.doPrivileged(new PrivilegedAction() {
					public Object run() {
						return factory.getService(context.bundle, registration);
					}
				});
			} catch (Throwable t) {
				if (Debug.DEBUG && Debug.DEBUG_SERVICES) {
					Debug.println(factory + ".getService() exception: " + t.getMessage()); //$NON-NLS-1$
					Debug.printStackTrace(t);
				}
				// allow the adaptor to handle this unexpected error
				context.framework.adaptor.handleRuntimeError(t);
				BundleException be = new BundleException(NLS.bind(Msg.SERVICE_FACTORY_EXCEPTION, factory.getClass().getName(), "getService"), t); //$NON-NLS-1$ 
				context.framework.publishFrameworkEvent(FrameworkEvent.ERROR, factorybundle, be);
				return (null);
			}

			if (service == null) {
				if (Debug.DEBUG && Debug.DEBUG_SERVICES) {
					Debug.println(factory + ".getService() returned null."); //$NON-NLS-1$
				}

				BundleException be = new BundleException(NLS.bind(Msg.SERVICE_OBJECT_NULL_EXCEPTION, factory.getClass().getName())); 
				context.framework.publishFrameworkEvent(FrameworkEvent.ERROR, factorybundle, be);

				return (null);
			}

			String[] clazzes = registration.clazzes;
			String invalidService = BundleContextImpl.checkServiceClass(clazzes, service);
			if (invalidService != null) {
				if (Debug.DEBUG && Debug.DEBUG_SERVICES) {
					Debug.println("Service object is not an instanceof " + invalidService); //$NON-NLS-1$
				}
				throw new IllegalArgumentException(NLS.bind(Msg.SERVICE_NOT_INSTANCEOF_CLASS_EXCEPTION, invalidService)); 
			}
			this.service = service;
		}

		useCount++;

		return (this.service);
	}

	/**
	 * Unget a service's service object.
	 * Releases the service object for a service.
	 * If the context bundle's use count for the service is zero, this method
	 * returns <code>false</code>. Otherwise, the context bundle's use count for the
	 * service is decremented by one.
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
	 * @return <code>true</code> if the context bundle's use count for the service
	 *         is zero otherwise <code>false</code>.
	 */
	protected boolean ungetService() {
		if (useCount == 0) {
			return (true);
		}

		useCount--;

		if (useCount == 0) {
			if (factory != null) {
				try {
					AccessController.doPrivileged(new PrivilegedAction() {
						public Object run() {
							factory.ungetService(context.bundle, registration, service);

							return null;
						}
					});
				} catch (Throwable t) {
					if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
						Debug.println(factory + ".ungetService() exception"); //$NON-NLS-1$
						Debug.printStackTrace(t);
					}

					AbstractBundle factorybundle = registration.context.bundle;
					BundleException be = new BundleException(NLS.bind(Msg.SERVICE_FACTORY_EXCEPTION, factory.getClass().getName(), "ungetService"), t); //$NON-NLS-1$ 
					context.framework.publishFrameworkEvent(FrameworkEvent.ERROR, factorybundle, be);
				}

				service = null;
			}

			return (true);
		}

		return (false);
	}

	/**
	 * Release a service's service object.
	 * <ol>
	 * <li>The bundle's use count for this service is set to zero.
	 * <li>If the service was registered with a {@link ServiceFactory},
	 * the {@link ServiceFactory#ungetService ServiceFactory.ungetService} method
	 * is called to release the service object for the bundle.
	 * </ol>
	 */
	protected void releaseService() {
		if ((useCount > 0) && (factory != null)) {
			try {
				AccessController.doPrivileged(new PrivilegedAction() {
					public Object run() {
						factory.ungetService(context.bundle, registration, service);

						return null;
					}
				});
			} catch (Throwable t) {
				if (Debug.DEBUG && Debug.DEBUG_SERVICES) {
					Debug.println(factory + ".ungetService() exception"); //$NON-NLS-1$
					Debug.printStackTrace(t);
				}

				AbstractBundle factorybundle = registration.context.bundle;
				BundleException be = new BundleException(NLS.bind(Msg.SERVICE_FACTORY_EXCEPTION, factory.getClass().getName(), "ungetService"), t); //$NON-NLS-1$ 
				context.framework.publishFrameworkEvent(FrameworkEvent.ERROR, factorybundle, be);
			}

			service = null;
		}

		useCount = 0;
	}
}
