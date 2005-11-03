/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2004, 2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package org.osgi.service.application;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.Map;

import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;

/**
 * An OSGi service that represents an installed application and stores
 * information about it. The application descriptor can be used for instance
 * creation.
 */

public abstract class ApplicationDescriptor {

	/**
	 * The property key for the localized name of the application.
	 */
	public static final String APPLICATION_NAME = "application.name";

	/**
	 * The property key for the localized icon of the application.
	 */
	public static final String APPLICATION_ICON = "application.icon";

	/**
	 * The property key for the unique identifier (PID) of the application.
	 */
	public static final String APPLICATION_PID = Constants.SERVICE_PID;

	/**
	 * The property key for the version of the application.
	 */
	public static final String APPLICATION_VERSION = "application.version";

	/**
	 * The property key for the name of the application vendor.
	 */
	public static final String APPLICATION_VENDOR = Constants.SERVICE_VENDOR;


	/**
	 * The property key for the visibility property of the application.
	 */
	public static final String APPLICATION_VISIBLE = "application.visible";

	/**
	 * The property key for the launchable property of the application.
	 */
	public static final String APPLICATION_LAUNCHABLE = "application.launchable";

	/**
	 * The property key for the locked property of the application.
	 */
	public static final String APPLICATION_LOCKED = "application.locked";

	/**
	 * The property key for the localized description of the application.
	 */
	public static final String APPLICATION_DESCRIPTION = "application.description";

	/**
	 * The property key for the localized documentation of the application.
	 */
	public static final String APPLICATION_DOCUMENTATION = "application.documentation";

	/**
	 * The property key for the localized copyright notice of the application.
	 */
	public static final String APPLICATION_COPYRIGHT = "application.copyright";

	/**
	 * The property key for the localized license of the application.
	 */
	public static final String APPLICATION_LICENSE = "application.license";

	/**
	 * The property key for the application container of the application.
	 */
	public static final String APPLICATION_CONTAINER = "application.container";

	/**
	 * The property key for the location of the application.
	 */
	public static final String APPLICATION_LOCATION = "application.location";

	
	/**
	 * Constructs the <code>ApplicationDescriptor</code>.
	 *
	 * @param applicationId
	 *            The identifier of the application. Its value is also available
	 *            as the <code>service.pid</code> service property of this 
	 *            <code>ApplicationDescriptor</code> service. This parameter must not
	 *            be <code>null</code>.
	 * @throws NullPointerException if the specified <code>applicationId</code> is null.
	 */
	protected  ApplicationDescriptor(String applicationId) {
		if(null == applicationId ) {
			throw new NullPointerException("Application ID must not be null!");
		}
		
		this.pid = applicationId;
		try {
		  AccessController.doPrivileged(new PrivilegedExceptionAction() {
			  public Object run() throws Exception {			
					delegate = (Delegate) implementation.newInstance();
				  return null;
			  }
		  });
	    delegate.setApplicationDescriptor( this, applicationId );
		}
		catch (Exception e) {
			// Too bad ...
			e.printStackTrace();
			System.err
					.println("No implementation available for ApplicationDescriptor, property is: "
							+ cName);
		}
	}

	/**
	 * Returns the identifier of the represented application.
	 * 
	 * @return the identifier of the represented application
	 */
	public final String getApplicationId() {
		return pid;
	}

	/**
	 * This method verifies whether the specified <code>pattern</code>
	 * matches the Distinguished Names of any of the certificate chains
	 * used to authenticate this application.
	 * <P>
	 * The <code>pattern</code> must adhere to the 
	 * syntax defined in {@link org.osgi.service.application.ApplicationAdminPermission}
	 * for signer attributes. 
	 * <p>
	 * This method is used by {@link ApplicationAdminPermission#implies(java.security.Permission)} method
	 * to match target <code>ApplicationDescriptor</code> and filter. 
	 * 
	 * @param pattern a pattern for a chain of Distinguished Names. It must not be null.
	 * @return <code>true</code> if the specified pattern matches at least
	 *   one of the certificate chains used to authenticate this application 
	 * @throws NullPointerException if the specified <code>pattern</code> is null.
	 */	
	public abstract boolean matchDNChain( String pattern );
	
	/**
	 * Returns the properties of the application descriptor as key-value pairs.
	 * The return value contains the locale aware and unaware properties as
	 * well. The returned <code>Map</code> will include the service
	 * properties of this <code>ApplicationDescriptor</code> as well.
	 * <p>
	 * This method will call the <code>getPropertiesSpecific</code> method
	 * to enable the container implementation to insert application model and/or
	 * container implementation specific properties.
	 * <P>
	 * The returned {@link java.util.Map} will contain the standard OSGi service 
	 * properties as well
	 * (e.g. service.id, service.vendor etc.) and specialized application
	 * descriptors may offer further service properties. The returned Map contains
	 * a snapshot of the properties. It will not reflect further changes in the
	 * property values nor will the update of the Map change the corresponding
	 * service property.
	 *   
	 * @param locale
	 *            the locale string, it may be null, the value null means the
	 *            default locale. If the provided locale is the empty String 
	 *            (<code>""</code>)then raw (non-localized) values are returned.
	 * 
	 * @return copy of the service properties of this application descriptor service,
	 *         according to the specified locale. If locale is null then the
	 *         default locale's properties will be returned. (Since service
	 *         properties are always exist it cannot return null.)
	 * 
	 * @throws IllegalStateException
	 *             if the application descriptor is unregistered
	 */
	public final Map getProperties(String locale) {
		Map props = getPropertiesSpecific( locale );
		
		/* currently the ApplicationDescriptor manages the load/save of locking */
		boolean isLocked = delegate.isLocked(); // the real locking state
		Boolean containerLocked = (Boolean)props.remove( APPLICATION_LOCKED );
		if( containerLocked != null && containerLocked.booleanValue() != isLocked ) {
			if( isLocked )      /* if the container's information is not correct */
				lockSpecific();   /* about the locking state (after loading the lock states) */
			else
				unlockSpecific();
		}			
		/* replace the container's lock with the application model's lock, that's the correct */
		props.put( APPLICATION_LOCKED, new Boolean( isLocked ) ); 		
		return props;
	}
	
	/**
	 * Container implementations can provide application model specific
	 * and/or container implementation specific properties via this 
	 * method. 
	 * 
	 * Localizable properties must be returned localized if the provided
	 * <code>locale</code> argument is not the empty String. The value
	 * <code>null</code> indicates to use the default locale, for other
	 * values the specified locale should be used.
	 *  
	 * The returned {@link java.util.Map} must contain the standard OSGi service 
	 * properties as well
	 * (e.g. service.id, service.vendor etc.) and specialized application
	 * descriptors may offer further service properties. 
	 * The returned <code>Map</code>
	 * contains a snapshot of the properties. It will not reflect further changes in the
	 * property values nor will the update of the Map change the corresponding
	 * service property.

	 * @param locale the locale to be used for localizing the properties.
	 * If <code>null</code> the default locale should be used. If it is
	 * the empty String (<code>""</code>) then raw (non-localized) values
	 * should be returned.
	 * 
	 * @return the application model specific and/or container implementation
	 * specific properties of this application descriptor.
	 *  
	 * @throws IllegalStateException
	 *             if the application descriptor is unregistered
	 */
	protected abstract Map getPropertiesSpecific(String locale);

	/**
	 * Launches a new instance of an application. The <code>args</code> parameter specifies
	 * the startup parameters for the instance to be launched, it may be null.
	 * <p>
	 * The following steps are made:
	 * <UL>
	 * <LI>Check for the appropriate permission.
	 * <LI>Check the locking state of the application. If locked then return
	 * null otherwise continue.
	 * <LI>Calls the <code>launchSpecific()</code> method to create and start an application
	 * instance.
	 * <LI>Returns the <code>ApplicationHandle</code> returned by the 
	 * launchSpecific()
	 * </UL>
	 * The caller has to have ApplicationAdminPermission(applicationPID,
	 * "launch") in order to be able to perform this operation.
	 * <P>
	 * The <code>Map</code> argument of the launch method contains startup 
	 * arguments for the
	 * application. The keys used in the Map can be standard or application
	 * specific. OSGi defines the <code>org.osgi.triggeringevent</code>
	 * key to be used to
	 * pass the triggering event to a scheduled application, however
	 * in the future it is possible that other well-known keys will be defined.
	 * To avoid unwanted clashes of keys, the following rules should be applied:
	 * <ul>
	 *   <li>The keys starting with the dash (-) character are application
	 *       specific, no well-known meaning should be associated with them.</li>
	 *   <li>Well-known keys should follow the reverse domain name based naming.
	 *       In particular, the keys standardized in OSGi should start with
	 *       <code>org.osgi.</code>.</li>
	 * </ul>
	 * <P>
	 * The method is synchonous, it return only when the application instance was
	 * successfully started or the attempt to start it failed.
	 * <P>
	 * This method never returns <code>null</code>. If launching an application fails,
	 * the appropriate exception is thrown.
	 * 
	 * @param arguments
	 *            Arguments for the newly launched application, may be null
	 * 
	 * @return the registered ApplicationHandle, which represents the newly 
	 *         launched application instance. Never returns <code>null</code>.
	 * 
	 * @throws SecurityException
	 *             if the caller doesn't have "launch"
	 *             ApplicationAdminPermission for the application.
	 * @throws Exception
	 *             if starting the application failed
	 * @throws IllegalStateException
	 *             if the application descriptor is unregistered
	 */
	public final ApplicationHandle launch(Map arguments)
			throws Exception {
		delegate.launch(arguments);
		if( !isLaunchableSpecific() )
			throw new Exception("Cannot launch the application!");
	  return launchSpecific(arguments);
	}

	/**
	 * Called by launch() to create and start a new instance in an application
	 * model specific way. It also creates and registeres the application handle
	 * to represent the newly created and started instance and registeres it.
	 * The method is synchonous, it return only when the application instance was
	 * successfully started or the attempt to start it failed.
	 * <P>
	 * This method must not return <code>null</code>. If launching the application
	 * failed, and exception must be thrown.
	 * 
	 * @param arguments
	 *            the startup parameters of the new application instance, may be
	 *            null
	 * 
	 * @return the registered application model
	 *         specific application handle for the newly created and started
	 *         instance.
	 * 
	 * @throws IllegalStateException
	 *             if the application descriptor is unregistered
	 * @throws Exception
	 *             if any problem occures.
	 */
	protected abstract ApplicationHandle launchSpecific(Map arguments)
			throws Exception;
	
	/**
	 * This method is called by launch() to verify that according to the
	 * container, the application is launchable.
	 * 
	 * @return true, if the application is launchable according to the 
	 *  container, false otherwise.
	 *  
	 * @throws IllegalStateException
	 *             if the application descriptor is unregistered
	 */
	protected abstract boolean isLaunchableSpecific();

	/**
	 * Schedules the application at a specified event. Schedule information
	 * should not get lost even if the framework or the device restarts so it
	 * should be stored in a persistent storage. The method registers a
	 * {@link ScheduledApplication} service in Service Registry, representing
	 * the created scheduling.
	 * 
	 * @param arguments
	 *            the startup arguments for the scheduled application, may be
	 *            null
	 * @param topic
	 *            specifies the topic of the triggering event, it may contain a
	 *            trailing asterisk as wildcard, the empty string is treated as
	 *            "*", must not be null
	 * @param eventFilter
	 *            specifies and LDAP filter to filter on the properties of the
	 *            triggering event, may be null
	 * @param recurring
	 *            if the recurring parameter is false then the application will
	 *            be launched only once, when the event firstly occurs. If the
	 *            parameter is true then scheduling will take place for every
	 *            event occurrence; i.e. it is a recurring schedule
	 * 
	 * @return the registered scheduled application service
	 * 
	 * @throws NullPointerException
	 *             if the topic is <code>null</code>
	 * @throws IOException
	 *             may be thrown if writing the information about the scheduled
	 *             application requires operation on the permanent storage and
	 *             I/O problem occurred.
	 * @throws InvalidSyntaxException 
	 * 			   if the specified <code>eventFilter</code> is not syntactically correct
	 * @throws SecurityException
	 *             if the caller doesn't have "schedule"
	 *             ApplicationAdminPermission for the application.
	 * @throws IllegalStateException
	 *             if the application descriptor is unregistered
	 */
	public final ScheduledApplication schedule(Map arguments, String topic,
			String eventFilter, boolean recurring) throws IOException,  InvalidSyntaxException {
		return delegate.schedule(arguments, topic, eventFilter, recurring);
	}

	/**
	 * Sets the lock state of the application. If an application is locked then
	 * launching a new instance is not possible. It does not affect the already
	 * launched instances.
	 * 
	 * @throws SecurityException
	 *             if the caller doesn't have "lock" ApplicationAdminPermission
	 *             for the application.
	 * @throws IllegalStateException
	 *             if the application descriptor is unregistered
	 */
	public final void lock() {
		delegate.lock();
		lockSpecific();
	}
	
	/**
	 * This method is used to notify the container implementation that the
	 * corresponding application has been locked and it should update the
	 * <code>application.locked</code> service property accordingly.
	 */
	protected abstract void lockSpecific();

	/**
	 * Unsets the lock state of the application.
	 * 
	 * @throws SecurityException
	 *             if the caller doesn't have "lock" ApplicationAdminPermission
	 *             for the application.
	 * @throws IllegalStateException
	 *             if the application descriptor is unregistered
	 */
	public final void unlock() {
		delegate.unlock();
		unlockSpecific();
	}
	
	/**
	 * This method is used to notify the container implementation that the
	 * corresponding application has been unlocked and it should update the
	 * <code>application.locked</code> service property accordingly.

	 * @throws IllegalStateException
	 *             if the application descriptor is unregistered
	 */
	protected abstract void unlockSpecific();

		/**
		 * @skip
		 */
	public interface Delegate {
		void setApplicationDescriptor(ApplicationDescriptor d, String pid );

		boolean isLocked();

		void lock();

		void unlock();

		ScheduledApplication schedule(Map args, String topic, String filter,
				boolean recurs);

		void launch(Map arguments) throws Exception;
	}

	Delegate	delegate;
	String							pid;

	static Class					implementation;
	static String					cName;

	{
		try {
		  AccessController.doPrivileged(new PrivilegedExceptionAction() {
			  public Object run() throws Exception {			
					cName = System.getProperty("org.osgi.vendor.application.ApplicationDescriptor");
    			implementation = Class.forName(cName);
				  return null;
			  }
		  });
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}
}