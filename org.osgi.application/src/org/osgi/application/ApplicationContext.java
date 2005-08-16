package org.osgi.application;

import java.util.*;

import org.osgi.framework.*;

/**
 * <code>ApplicationContext</code> is the access point for an OSGi-aware
 * application to the features of the OSGi Service Platform. Each application
 * instance will have its own <code>ApplicationContext</code> instance, which
 * will not be reused after destorying the corresponding application instace.
 * <P>
 * Application instances can obtain their <code>ApplicationContext</code>
 * using the {@link Framework#getMyApplicationContext} method.
 * 
 * @see org.osgi.application.Framework
 */
public interface ApplicationContext {

    /**
     * Adds the specified {@link org.osgi.framework.BundleListener} object to this context
     * application instance's list of listeners if not already present.
     * {@link org.osgi.framework.BundleListener} objects are notified when a bundle has a
     * lifecycle state change.
     * <P>
     * If this context application's list of listeners already contains a
     * listener <code>l</code> such that <code>(l==listener)</code>,this
     * method does nothing.
     * 
     * @param listener
     *            The {@link org.osgi.framework.BundleListener} to be added.
     * @throws java.lang.IllegalStateException
     *             If this context application instance has stopped.
     */
    public void addBundleListener(BundleListener listener);

    /**
     * Adds the specified {@link org.osgi.framework.FrameworkListener} object to this
     * context application instance's list of listeners if not already present.
     * {@link org.osgi.framework.FrameworkListener} objects are notified of general
     * Framework events.
     * <P>
     * If this context application's list of listeners already contains a
     * listener <code>l</code> such that <code>(l==listener)</code>,this
     * method does nothing.
     * 
     * @param listener
     *            The {@link org.osgi.framework.FrameworkListener} to be added.
     * @throws java.lang.IllegalStateException
     *             If this context application instance has stopped.
     */
    public void addFrameworkListener(FrameworkListener listener);

    /**
     * Adds the specified {@link org.osgi.framework.ServiceListener} object to this context
     * application instance's list of listeners.
     * <P>
     * This method is the same as calling
     * {@link ApplicationContext#addServiceListener(ServiceListener listener, String filter)}
     * with <code>filter</code> set to <code>null</code>.
     * <P>
     * 
     * @param listener
     *            The {@link org.osgi.framework.ServiceListener} to be added.
     * @throws java.lang.IllegalStateException
     *             If this context application instance has stopped.
     */
    public void addServiceListener(ServiceListener listener);

    /**
     * Adds the specified {@link org.osgi.framework.ServiceListener} object with the
     * specified filter to this context application instance's list of
     * listeners. ServiceListener objects are notified when a service has a
     * lifecycle state change.
     * <P>
     * If this context bundle's list of listeners already contains a listener
     * <code>l</code> such that <code>(l==listener)</code>, this method
     * replaces that listener's filter (which may be <code>null</code>) with
     * the specified one (which may be </code>null</code>).
     * <P>
     * The <code>listener</code> is called if the <code>filter</code>
     * criteria is met. To filter based upon the class of the service, the
     * filter should reference the 
     * {@link org.osgi.framework.Constants#OBJECTCLASS Constants.OBJECTCLASS} 
     * property.
     * If filter is <code>null</code>, all services are considered to match
     * the filter.
     * <P>
     * When using a filter, it is possible that the <code>ServiceEvents</code>
     * for the complete life cycle of a service will not be delivered to the
     * listener. For example, if the <code>filter</code> only matches when the
     * property <code>x</code> has the value 1, the <code>listener</code>
     * will not be called if the service is registered with the property </code>x</code>
     * not set to the value 1. Subsequently, when the service is modified
     * setting property <code>x</code> to the value 1, the </code>filter</code>
     * will match and the <code>listener</code> will be called with a 
     * {@link org.osgi.framework.ServiceEvent ServiceEvent}
     * of type <code>MODIFIED</code>. Thus, the <code>listener</code> will
     * not be called with a {@link org.osgi.framework.ServiceEvent} of type <code>REGISTERED</code>.
     * <P>
     * If the Java Runtime Environment supports permissions, the {@link org.osgi.framework.ServiceListener}
     * object will be notified of a service event only if the application that
     * is registering it has the {@link org.osgi.framework.ServicePermission} to get the
     * service using at least one of the named classes the service was
     * registered under.
     * 
     * @param listener
     *            The {@link org.osgi.framework.ServiceListener} object to be added.
     * @param filter
     *            The filter criteria.
     * @throws org.osgi.framework.InvalidSyntaxException
     *             If filter contains an invalid filter string which cannot be
     *             parsed.
     * @throws java.lang.IllegalStateException
     *             If this context application instace has stopped.
     */
    public void addServiceListener(ServiceListener listener,
            java.lang.String filter) throws InvalidSyntaxException;

    /**
     * Removes the specified {@link org.osgi.framework.BundleListener} object from this
     * context application instances's list of listeners.
     * <P>
     * If <code>listener</code> is not contained in this context application
     * instance's list of listeners, this method does nothing.
     * 
     * @param listener
     *            The {@link org.osgi.framework.BundleListener} object to be removed.
      * @throws java.lang.IllegalStateException
     *             If this context application instance has stopped.
    */
    public void removeBundleListener(BundleListener listener);

    /**
     * Removes the specified {@link org.osgi.framework.FrameworkListener} object from this
     * context application instances's list of listeners.
     * <P>
     * If <code>listener</code> is not contained in this context application
     * instance's list of listeners, this method does nothing.
     * 
     * @throws java.lang.IllegalStateException
     *             If this context application instance has stopped.
     * @param listener
     *            The {@link org.osgi.framework.FrameworkListener} object to be removed.
     */
    public void removeFrameworkListener(FrameworkListener listener);

    /**
     * Removes the specified {@link org.osgi.framework.ServiceListener} object from this
     * context application instances's list of listeners.
     * <P>
     * If <code>listener</code> is not contained in this context application
     * instance's list of listeners, this method does nothing.
     * 
     * @throws java.lang.IllegalStateException
     *             If this context application instance has stopped.
     * @param listener
     *            The {@link org.osgi.framework.ServiceListener} object to be removed.
     */
    public void removeServiceListener(ServiceListener listener);

    /**
     * This method returns the service object for the specified
     * <code>referenceName</code>. If the cardinality of the reference is
     * 0..n or 1..n and multiple services are bound to the reference, the
     * service with the highest ranking (as specified in its
     * {@link org.osgi.framework.Constants#SERVICE_RANKING} property) is returned. If there
     * is a tie in ranking, the service with the lowest service ID (as specified
     * in its {@link org.osgi.framework.Constants#SERVICE_ID} property); that is, the
     * service that was registered first is returned.
     * 
     * @param referenceName
     *            The name of a reference as specified in a reference element in
     *            this context applications�s description.
     * @return A service object for the referenced service or <code>null</code>
     *         if the reference cardinality is 0..1 or 0..n and no bound service
     *         is available.
     */
    public Object locateService(String referenceName);

    /**
     * This method returns the service objects for the specified
     * <code>referenceName</code>.
     * 
     * @param referenceName
     *            The name of a reference as specified in a reference element in
     *            this context applications�s description.
     * @return An array of service object for the referenced service or
     *         <code>null</code> if the reference cardinality is 0..1 or 0..n
     *         and no bound service is available.
     */
    public Object[] locateServices(String referenceName);
    
    /**
     * Returns the startup parameters specified when calling the 
     * {@link org.osgi.service.application.ApplicationDescriptor#launch}
     * method.
     * <p>
     * Startup arguments can be specified as name, value pairs. The name
     * must be of type {@link java.lang.String}, which must not be
     * <code>null</code> or empty {@link java.lang.String} (<code>""</code>), 
     * the value can be any object including <code>null</code>.
     * 
     * @return a {@link java.util.Map} containing the startup arguments.
     */
    public Map getStartupParameters();

    public ServiceRegistration registerService(String[] clazzes,
            Object service, Dictionary properties);

    public ServiceRegistration registerService(String clazz, Object service,
            Dictionary properties);
}
