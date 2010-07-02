/*
 * Copyright (c) OSGi Alliance (2005, 2009). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.application;

import java.util.Dictionary;
import java.util.Map;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

/**
 * {@code ApplicationContext} is the access point for an OSGi-aware
 * application to the features of the OSGi Service Platform. Each application
 * instance will have its own {@code ApplicationContext} instance, which
 * will not be reused after destroying the corresponding application instance.
 * <p>
 * Application instances can obtain their {@code ApplicationContext} using
 * the {@link Framework#getApplicationContext} method.
 * <p>
 * The lifecycle of an {@code ApplicationContext} instance is bound to the
 * lifecycle of the corresponding application instance. The
 * {@code ApplicationContext} becomes available when the application is
 * started and it is invalidated when the application instance is stopped (i.e.
 * the "stop" method of the application activator object returned). All method
 * calls (except {@link #getApplicationId()} and {@link #getInstanceId()}) to an
 * invalidated context object result an {@code IllegalStateException}.
 * 
 * @see org.osgi.application.Framework
 * 
 * @version $Id$
 */
public interface ApplicationContext {

    /**
     * Adds the specified {@link ApplicationServiceListener} object to this context
     * application instance's list of listeners. The specified {@code referenceName} is a 
     * reference name specified in the descriptor of the corresponding application. The registered
     * {@code listener} will only receive the {@link ApplicationServiceEvent}s related to the referred service.
     * <p>
     * If the {@code listener} was already added, calling this method will overwrite the previous
     * registration.
     * <p>
     * 
     * @param listener
     *            The {@link org.osgi.application.ApplicationServiceListener} to be added. It must
     *            not be {@code null}
     * @param referenceName the reference name of a service from the descriptor of the corresponding
     *            application. It must not be {@code null}.
     * @throws java.lang.IllegalStateException
     *             If this context application instance has stopped.
     * @throws java.lang.NullPointerException If {@code listener} or {@code referenceName}
     *             is {@code null}      
     * @throws java.lang.IllegalArgumentException If there is no service in the
     *             application descriptor with the specified {@code referenceName}.
     */
    public void addServiceListener(ApplicationServiceListener listener, String referenceName) throws java.lang.IllegalArgumentException;

    /**
     * Adds the specified {@link ApplicationServiceListener} object to this context
     * application instance's list of listeners. The {@code referenceNames} parameter is an 
     * array of reference name specified in the descriptor of the corresponding application. The registered
     * {@code listener} will only receive the {@link ApplicationServiceEvent}s related to the referred 
     * services.
     * <p>
     * If the {@code listener} was already added, calling this method will overwrite the previous
     * registration.
     * <p>
     * 
     * @param listener
     *            The {@link org.osgi.application.ApplicationServiceListener} to be added. It must not
     *            be {@code null}
     * @param referenceNames and array of service reference names from the descriptor of the corresponding
     *            application. It must not be {@code null} and it must not be empty.
     * @throws java.lang.IllegalStateException
     *             If this context application instance has stopped.
     * @throws java.lang.NullPointerException If {@code listener} or {@code referenceNames}
     *             is {@code null}      
     * @throws java.lang.IllegalArgumentException If {@code referenceNames} array is empty or it 
     *    contains unknown references
     */
    public void addServiceListener(ApplicationServiceListener listener, String[] referenceNames) throws java.lang.IllegalArgumentException;

    /**
     * Removes the specified {@link org.osgi.application.ApplicationServiceListener} object from this
     * context application instances's list of listeners.
     * <p>
     * If {@code listener} is not contained in this context application
     * instance's list of listeners, this method does nothing.
     * 
     * @param listener
     *            The {@link org.osgi.application.ApplicationServiceListener} object to be removed.
     * @throws java.lang.IllegalStateException
     *             If this context application instance has stopped.
     */
    public void removeServiceListener(ApplicationServiceListener listener);

	/**
	 * This method returns the identifier of the corresponding application
	 * instance. This identifier is guaranteed to be unique within the scope of
	 * the device.
	 * 
	 * Note: this method can safely be called on an invalid
	 * {@code ApplicationContext} as well.
	 * 
	 * @see org.osgi.service.application.ApplicationHandle#getInstanceId()
	 * 
	 * @return the unique identifier of the corresponding application instance
	 */
    public String getInstanceId();
    
    /**
     * This method return the identifier of the corresponding application type. This identifier
     * is the same for the different instances of the same application but it is different for
     * different application type.
     * <p>
     * Note: this method can safely be called on an invalid 
     * {@code ApplicationContext} as well.
     * 
     * @see org.osgi.service.application.ApplicationDescriptor#getApplicationId()
     * 
     * @return the identifier of the application type.
     */
    public String getApplicationId();

    /**
     * This method returns the service object for the specified
     * {@code referenceName}. If the cardinality of the reference is
     * 0..n or 1..n and multiple services are bound to the reference, the
     * service with the highest ranking (as specified in its
     * {@link org.osgi.framework.Constants#SERVICE_RANKING} property) is returned. If there
     * is a tie in ranking, the service with the lowest service ID (as specified
     * in its {@link org.osgi.framework.Constants#SERVICE_ID} property); that is, the
     * service that was registered first is returned.
     * 
     * @param referenceName
     *            The name of a reference as specified in a reference element in
     *            this context applications's description. It must not be {@code null}
     * @return A service object for the referenced service or {@code null}
     *         if the reference cardinality is 0..1 or 0..n and no bound service
     *         is available.
     * @throws java.lang.NullPointerException If {@code referenceName} is {@code null}.
     * @throws java.lang.IllegalArgumentException If there is no service in the
     *             application descriptor with the specified {@code referenceName}.
     * @throws java.lang.IllegalStateException
     *             If this context application instance has stopped.
     */
    public Object locateService(String referenceName);

    /**
     * This method returns the service objects for the specified
     * {@code referenceName}.
     * 
     * @param referenceName
     *            The name of a reference as specified in a reference element in
     *            this context applications's description. It must not be 
     *            {@code null}.
     * @return An array of service object for the referenced service or
     *         {@code null} if the reference cardinality is 0..1 or 0..n
     *         and no bound service is available.
     * @throws java.lang.NullPointerException If {@code referenceName} is {@code null}.
     * @throws java.lang.IllegalArgumentException If there is no service in the
     *             application descriptor with the specified {@code referenceName}.
     * @throws java.lang.IllegalStateException
     *             If this context application instance has stopped.
     */
    public Object[] locateServices(String referenceName);
    
    /**
     * Returns the startup parameters specified when calling the 
     * {@link org.osgi.service.application.ApplicationDescriptor#launch}
     * method.
     * <p>
     * Startup arguments can be specified as name, value pairs. The name
     * must be of type {@link java.lang.String}, which must not be
     * {@code null} or empty {@link java.lang.String} ({@code ""}), 
     * the value can be any object including {@code null}.
     * 
     * @return a {@link java.util.Map} containing the startup arguments. 
     *     It can be {@code null}.
     * @throws java.lang.IllegalStateException
     *             If this context application instance has stopped.
     */
    public Map getStartupParameters();
    
    /**
     * Application can query the service properties of a service object
     * it is bound to. Application gets bound to a service object when
     * it first obtains a reference to the service by calling 
     * {@code locateService} or {@code locateServices} methods.
     * 
     * @param serviceObject A service object the application is bound to.
     *    It must not be null.
     * @return The service properties associated with the specified service
     *    object.
     * @throws NullPointerException if the specified {@code serviceObject}
     *    is {@code null}
     * @throws IllegalArgumentException if the application is not
     *    bound to the specified service object or it is not a service
     *    object at all.
     * @throws java.lang.IllegalStateException
     *             If this context application instance has stopped.
     */
    public Map getServiceProperties(Object serviceObject);

    
    /**
	 * Registers the specified service object with the specified properties
	 * under the specified class names into the Framework. A
	 * {@link org.osgi.framework.ServiceRegistration} object is returned. The
	 * {@link org.osgi.framework.ServiceRegistration} object is for the private use of the
	 * application registering the service and should not be shared with other
	 * applications. The registering application is defined to be the context application.
	 * Bundles can locate the service by using either the
	 * {@link org.osgi.framework.BundleContext#getServiceReferences} or 
	 * {@link org.osgi.framework.BundleContext#getServiceReference} method. Other applications
	 * can locate this service by using {@link #locateService(String)} or {@link #locateServices(String)}
	 * method, if they declared their dependence on the registered service.
	 * 
	 * <p>
	 * An application can register a service object that implements the
	 * {@link org.osgi.framework.ServiceFactory} interface to have more flexibility in providing
	 * service objects to other applications or bundles.
	 * 
	 * <p>
	 * The following steps are required to register a service:
	 * <ol>
	 * <li>If {@code service} is not a {@code ServiceFactory},
	 * an {@code IllegalArgumentException} is thrown if
	 * {@code service} is not an {@code instanceof} all the
	 * classes named.
	 * <li>The Framework adds these service properties to the specified
	 * {@code Dictionary} (which may be {@code null}): a property
	 * named {@link org.osgi.framework.Constants#SERVICE_ID} identifying the registration number of
	 * the service and a property named {@link org.osgi.framework.Constants#OBJECTCLASS} containing
	 * all the specified classes. If any of these properties have already been
	 * specified by the registering bundle, their values will be overwritten by
	 * the Framework.
	 * <li>The service is added to the Framework service registry and may now
	 * be used by others.
	 * <li>A service event of type {@link org.osgi.framework.ServiceEvent#REGISTERED} is
	 * fired. This event triggers the corresponding {@link ApplicationServiceEvent} to be 
	 * delivered to the applications that registered the appropriate listener.
	 * <li>A {@code ServiceRegistration} object for this registration is
	 * returned.
	 * </ol>
	 * 
	 * @param clazzes The class names under which the service can be located.
	 *        The class names in this array will be stored in the service's
	 *        properties under the key {@link org.osgi.framework.Constants#OBJECTCLASS}.
     *        This parameter must not be {@code null}.
	 * @param service The service object or a {@code ServiceFactory}
	 *        object.
	 * @param properties The properties for this service. The keys in the
	 *        properties object must all be {@code String} objects. See
	 *        {@link org.osgi.framework.Constants} for a list of standard service property keys.
	 *        Changes should not be made to this object after calling this
	 *        method. To update the service's properties the
	 *        {@link org.osgi.framework.ServiceRegistration#setProperties} method must be called.
	 *        The set of properties may be {@code null} if the service
	 *        has no properties.
	 * 
	 * @return A {@link org.osgi.framework.ServiceRegistration} object for use by the application
	 *         registering the service to update the service's properties or to
	 *         unregister the service.
	 * 
	 * @throws java.lang.IllegalArgumentException If one of the following is
	 *         true:
	 *         <ul>
	 *         <li>{@code service} is {@code null}.
	 *         <li>{@code service} is not a {@code ServiceFactory}
	 *         object and is not an instance of all the named classes in
	 *         {@code clazzes}.
	 *         <li>{@code properties} contains case variants of the same
	 *         key name.
	 *         </ul>
	 * @throws NullPointerException if {@code clazzes} is {@code null}
     * 
	 * @throws java.lang.SecurityException If the caller does not have the
	 *         {@code ServicePermission} to register the service for all
	 *         the named classes and the Java Runtime Environment supports
	 *         permissions.
	 * 
	 * @throws java.lang.IllegalStateException If this ApplicationContext is no
	 *         longer valid.
	 * 
	 * @see org.osgi.framework.BundleContext#registerService(java.lang.String[], java.lang.Object, java.util.Dictionary)
	 * @see org.osgi.framework.ServiceRegistration
	 * @see org.osgi.framework.ServiceFactory
	 */
    public ServiceRegistration registerService(String[] clazzes,
            Object service, Dictionary properties);

    /**
	 * Registers the specified service object with the specified properties
	 * under the specified class name with the Framework.
	 * 
	 * <p>
	 * This method is otherwise identical to
	 * {@link #registerService(java.lang.String[], java.lang.Object,
	 * java.util.Dictionary)} and is provided as a convenience when
	 * {@code service} will only be registered under a single class name.
	 * Note that even in this case the value of the service's
	 * {@link Constants#OBJECTCLASS} property will be an array of strings,
	 * rather than just a single string.
	 * 
	 * @param clazz The class name under which the service can be located. It
     *        must not be {@code null}
	 * @param service The service object or a {@code ServiceFactory}
	 *        object.
	 * @param properties The properties for this service. 
	 * 
	 * @return A {@code ServiceRegistration} object for use by the application
	 *         registering the service to update the service's properties or to
	 *         unregister the service.
	 *         
     * @throws java.lang.IllegalArgumentException If one of the following is
     *         true:
     *         <ul>
     *         <li>{@code service} is {@code null}.
     *         <li>{@code service} is not a {@code ServiceFactory}
     *         object and is not an instance of the named class in
     *         {@code clazz}.
     *         <li>{@code properties} contains case variants of the same
     *         key name.
     *         </ul>
     * @throws NullPointerException if {@code clazz} is {@code null}
     * 
     * @throws java.lang.SecurityException If the caller does not have the
     *         {@code ServicePermission} to register the service 
     *         the named class and the Java Runtime Environment supports
     *         permissions.
     *
	 * @throws java.lang.IllegalStateException If this ApplicationContext is no
	 *         longer valid.
	 * @see #registerService(java.lang.String[], java.lang.Object,
	 *      java.util.Dictionary)
	 */
    public ServiceRegistration registerService(String clazz, Object service,
            Dictionary properties);
}
