package org.osgi.service.application;

import java.io.IOException;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * An OSGi service that represents an installed application and stores
 * information about it. The application descriptor can be used for instance
 * creation.
 * 
 * @modelguid {8679CDDA-3F17-4CE1-B0B2-2371B5876B1D}
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
	public static final String APPLICATION_PID = "service.pid";

	/**
	 * The property key for the version of the application.
	 */
	public static final String APPLICATION_VERSION = "application.version";

	/**
	 * The property key for the name of the application vendor.
	 */
	public static final String APPLICATION_VENDOR = "application.vendor";

	/**
	 * The property key for the singleton property of the application.
	 */
	public static final String APPLICATION_SINGLETON = "application.singleton";

	/**
	 * The property key for the autostart property of the application.
	 */
	public static final String APPLICATION_AUTOSTART = "application.autostart";

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
	 * Gets the identifier of the represented application. This identifier (PID)
	 * must be unique on the device, and must not change when the application is
	 * updated.
	 * 
	 * This value is also available as a service property "service.pid" of this
	 * application descriptor.
	 * 
	 * @throws IllegalStateException
	 *             if the application descriptor is unregistered
	 * 
	 * @modelguid {4A78F411-6CF9-45C6-8F6A-44ED8C8F8B7E}
	 */
	public abstract String getPID();

	/**
	 * Returns the properties of the application descriptor as key-value pairs.
	 * The return value contains the locale aware and unaware properties as
	 * well. Some of the properties can be retrieved directly with methods in
	 * this interface.
	 * 
	 * @param locale
	 *            the locale string, it may be null, the value null means the
	 *            default locale.
	 * 
	 * @return service properties of this application descriptor service,
	 *         according to the specified locale. If locale is null then the
	 *         default locale's properties will be returned. (Since service
	 *         properties are always exist it cannot return null.)
	 * 
	 * @throws IllegalStateException
	 *             if the application descriptor is unregistered
	 * 
	 * @modelguid {DA01FC37-C5F5-45F6-ADDA-AA07AD31CDCA}
	 */
	public Map getProperties(String locale) {
		return null;
	}

	/**
	 * Launches a new instance of an application. The args parameter specifies
	 * the startup parameters for the instance to be launched, it may be null.
	 * <p>
	 * The following steps are made:
	 * <UL>
	 * <LI>Check for the appropriate permission.
	 * <LI>Check the locking state of the application. If locked then return
	 * null otherwise continue.
	 * <LI>If the application is a singleton and already has a running instance
	 * then throw SingletonException.
	 * <LI>Calls the launchSpecific() method to create and start an application
	 * instance.
	 * <LI>Returns the ServiceReference returned by the launchSpecific()
	 * </UL>
	 * The caller has to have ApplicationAdminPermission(applicationPID,
	 * "launch") in order to be able to perform this operation.
	 * 
	 * @param arguments
	 *            Arguments for the newly launched application, may be null
	 * 
	 * @return the ServiceReference to registered ApplicationHandle which
	 *         represents the newly launched application instance
	 * 
	 * @throws SingletonException
	 *             if the call attempts to launch a second instance of a
	 *             singleton application
	 * @throws SecurityException
	 *             if the caller doesn't have "launch"
	 *             ApplicationAdminPermission for the application.
	 * @throws Exception
	 *             if starting the application(s) failed
	 * @throws IllegalStateException
	 *             if the application descriptor is unregistered
	 * 
	 * @modelguid {1E455A11-4289-4E0E-A3D3-B65B291E6FE3}
	 */
	public final ServiceReference launch(Map arguments)
			throws SingletonException, Exception {
		return null;
	}

	/**
	 * Called by launch() to create and start a new instance in an application
	 * model specific way. It also creates and registeres the application handle
	 * to represent the newly created and started instance and registeres it.
	 * 
	 * @param arguments
	 *            the startup parameters of the new application instance, may be
	 *            null
	 * 
	 * @return the service reference of the registered application model
	 *         specific application handle for the newly created and started
	 *         instance.
	 * 
	 * @throws Exception
	 *             if any problem occures.
	 */
	protected abstract ServiceReference launchSpecific(Map arguments)
			throws Exception;

	/**
	 * Schedules the application at a specified event. Schedule information
	 * should not get lost even if the framework or the device restarts so it
	 * should be stored in a persistent storage. It has to register the returned
	 * service.
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
	 * @return the service reference of the registered scheduled application
	 *         service
	 * 
	 * @throws NullPointerException
	 *             if the topic is null
	 * @throws IOException
	 *             may be thrown if writing the information about the scheduled
	 *             application requires operation on the permanent storage and
	 *             I/O problem occurred.
	 * @throws SecurityException
	 *             if the caller doesn't have "schedule"
	 *             ApplicationAdminPermission for the application.
	 * @throws IllegalStateException
	 *             if the application descriptor is unregistered
	 * 
	 * @modelguid {F91D977E-C639-4802-BA36-2C000BF0DBD9}
	 */
	public final ServiceReference schedule(Map arguments, String topic,
			String eventFilter, boolean recurring) throws IOException {
		return null;
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
	 * 
	 * @modelguid {A627693E-D67C-45EE-B683-4EA2B93AF982}
	 */
	public final void lock() {
	}

	/**
	 * Unsets the lock state of the application.
	 * 
	 * @throws SecurityException
	 *             if the caller doesn't have "lock" ApplicationAdminPermission
	 *             for the application.
	 * @throws IllegalStateException
	 *             if the application descriptor is unregistered
	 * 
	 * @modelguid {739EAD89-AF58-4C38-9833-B8E86E56C44E}
	 */
	public final void unlock() {
	}

	/**
	 * Returns a boolean indicating whether the application is locked.
	 * 
	 * @throws IllegalStateException
	 *             if the application descriptor is unregistered
	 * 
	 * @modelguid {54F610F8-B6E8-4B44-A973-A39A177452D4}
	 */
	public final boolean isLocked() {
		return false;
	}

	/**
	 * Retrieves the bundle context of the container to which the specialization
	 * of the application descriptor belongs
	 * 
	 * @throws IllegalStateException
	 *             if the application descriptor is unregistered
	 * 
	 * @return the bundle context of the container
	 */
	protected abstract BundleContext getBundleContext();

}