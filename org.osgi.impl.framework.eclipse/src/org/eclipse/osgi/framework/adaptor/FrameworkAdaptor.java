/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.framework.adaptor;

import java.io.IOException;
import java.net.URLConnection;
import java.util.Properties;
import org.eclipse.osgi.framework.log.FrameworkLog;
import org.eclipse.osgi.service.resolver.PlatformAdmin;
import org.eclipse.osgi.service.resolver.State;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

/**
 * FrameworkAdaptor interface to the osgi framework. This class is used to provide
 * platform specific support for the osgi framework.
 *
 * <p>The OSGi framework will call this class to perform platform specific functions.
 *
 * Classes that implement FrameworkAdaptor MUST provide a constructor that takes as a
 * parameter an array of Strings.  This array will contain arguments to be
 * handled by the FrameworkAdaptor.  The FrameworkAdaptor implementation may define the format
 * and content of its arguments.
 *
 * The constructor should parse the arguments passed to it and remember them.
 * The initialize method should perform the actual processing of the adaptor
 * arguments.
 */

public interface FrameworkAdaptor {

	public static final String FRAMEWORK_SYMBOLICNAME = "org.eclipse.osgi"; //$NON-NLS-1$

	/**
	 * Initialize the FrameworkAdaptor object so that it is ready to be
	 * called by the framework.  Handle the arguments that were
	 * passed to the constructor.
	 * This method must be called before any other FrameworkAdaptor methods.
	 * @param eventPublisher The EventPublisher used to publish any events to 
	 * the framework.
	 */
	public void initialize(EventPublisher eventPublisher);

	/**
	 * Initialize the persistent storage for the adaptor.
	 *
	 * @throws IOException If the adaptor is unable to
	 * initialize the bundle storage.
	 */
	public void initializeStorage() throws IOException;

	/**
	 * Compact/cleanup the persistent storage for the adaptor.
	 * @throws IOException If the adaptor is unable to 
	 * compact the bundle storage.
	 *
	 */
	public void compactStorage() throws IOException;

	/**
	 * Return the properties object for the adaptor.
	 * The properties in the returned object supplement
	 * the System properties.
	 * The framework may modify this object.  The Framework
	 * will use the returned properties to set the System
	 * properties.
	 *
	 * @return The properties object for the adaptor.
	 */
	public Properties getProperties();

	/**
	 * Return a list of the installed bundles.  Each element in the
	 * list must be of type <code>BundleData</code>.  Each <code>BundleData</code>
	 * corresponds to one bundle that is persistently stored.
	 * This method must construct <code>BundleData</code> objects for all 
	 * installed bundles and return an array containing the objects.
	 * The returned array becomes the property of the framework.
	 *
	 * @return Array of installed BundleData objects, or null if none can be found.
	 */
	public BundleData[] getInstalledBundles();

	/**
	 * Map a location to a URLConnection.  This is used by the Framework when installing a bundle
	 * from a spacified location.
	 *
	 * @param location of the bundle.
	 * @return URLConnection that represents the location.
	 * @throws BundleException if the mapping fails.
	 */
	public URLConnection mapLocationToURLConnection(String location) throws BundleException;

	/**
	 * Prepare to install a bundle from a URLConnection.
	 * <p>To complete the install,
	 * begin and then commit
	 * must be called on the returned <code>BundleOperation</code> object.
	 * If either of these methods throw a BundleException
	 * or some other error occurs,
	 * then undo must be called on the <code>BundleOperation</code> object
	 * to undo the change to persistent storage.
	 *
	 * @param location Bundle location.
	 * @param source URLConnection from which the bundle may be read.
	 * Any InputStreams returned from the source
	 * (URLConnections.getInputStream) must be closed by the
	 * <code>BundleOperation</code> object.
	 * @return BundleOperation object to be used to complete the install.
	 */
	public BundleOperation installBundle(String location, URLConnection source);

	/**
	 * Prepare to update a bundle from a URLConnection.
	 * <p>To complete the update
	 * begin and then commit
	 * must be called on the returned <code>BundleOperation</code> object.
	 * If either of these methods throw a BundleException
	 * or some other error occurs,
	 * then undo must be called on the <code>BundleOperation</code> object
	 * to undo the change to persistent storage.
	 *
	 * @param bundledata BundleData to update.
	 * @param source URLConnection from which the updated bundle may be read.
	 * Any InputStreams returned from the source
	 * (URLConnections.getInputStream) must be closed by the
	 * <code>BundleOperation</code> object.
	 * @return BundleOperation object to be used to complete the update.
	 */
	public BundleOperation updateBundle(BundleData bundledata, URLConnection source);

	/**
	 * Prepare to uninstall a bundle.
	 * <p>To complete the uninstall,
	 * begin and then commit
	 * must be called on the returned <code>BundleOperation</code> object.
	 * If either of these methods throw a BundleException
	 * or some other error occurs,
	 * then undo must be called on the <code>BundleOperation</code> object
	 * to undo the change to persistent storage.
	 *
	 * @param bundledata BundleData to uninstall.
	 * @return BundleOperation object to be used to complete the uninstall.
	 */
	public BundleOperation uninstallBundle(BundleData bundledata);

	/**
	 * Returns the total amount of free space available for bundle storage on the device.
	 *
	 * @return Free space available in bytes or -1 if it does not apply to this adaptor
	 * @exception IOException if an I/O error occurs determining the available space
	 */
	public long getTotalFreeSpace() throws IOException;

	/**
	 * Returns the PermissionStorage object which will be used to
	 * to manage the permission data.
	 *
	 * @return The PermissionStorage object for the adaptor.
	 * @see "org.osgi.service.permissionadmin.PermissionAdmin"
	 */
	public PermissionStorage getPermissionStorage() throws IOException;

	/**
	 * Returns the <code>ServiceRegistry</code> object which will be used
	 * to manage ServiceReference bindings.
	 * @return The ServiceRegistry object for the adaptor.
	 */
	public ServiceRegistry getServiceRegistry();

	/**
	 * The framework will call this method after the 
	 * System BundleActivator.start(BundleContext) has been called.  The context is 
	 * the System Bundle's BundleContext.  This method allows FrameworkAdaptors to 
	 * have access to the OSGi framework to get services, register services and 
	 * perform other OSGi operations. 
	 * @param context The System Bundle's BundleContext.
	 * @exception BundleException on any error that may occur.
	 */
	public void frameworkStart(BundleContext context) throws BundleException;

	/**
	 * The framework will call this method before the 
	 * System BundleActivator.stop(BundleContext) has been called.  The context is 
	 * the System Bundle's BundleContext.  This method allows FrameworkAdaptors to 
	 * have access to the OSGi framework to get services, register services and 
	 * perform other OSGi operations. 
	 * @param context The System Bundle's BundleContext.
	 * @exception BundleException on any error that may occur.
	 */
	public void frameworkStop(BundleContext context) throws BundleException;

	/**
	 * The framework will call this method before the process of framework
	 * shutdown is started.  This gives FrameworkAdaptors a chance to
	 * perform actions before the framework start level is decremented and
	 * all the bundles are stopped.  This method will get called before the
	 * {@link #frameworkStop(BundleContext)} method.
	 * @param context The System Bundle's BundleContext.
	 */
	public void frameworkStopping(BundleContext context);

	/**
	 * Gets the value for Export-Package for packages that a FrameworkAdaptor is exporting
	 * to the framework.  The String returned will be parsed by the framework
	 * and the packages specified will be exported by the System Bundle.
	 * @return The value for Export-Package that the System Bundle will export or
	 * null if none exist.
	 */
	public String getExportPackages();

	/**
	 * Gets the value for Provide-Package for packages that a FrameworkAdaptor is exporting
	 * to the framework.  The String returned will be parsed by the framework
	 * and the packages specified will be exported by the System Bundle.
	 * @return The value for Provide-Package that the System Bundle will export or
	 * null if none exist.
	 */
	public String getProvidePackages();

	/**
	 * Gets any Service class names that a FrameworkAdaptor is exporting to the
	 * framework.  The class names returned will be exported by the System Bundle.
	 * @return The value of Export-Service that the System Bundle will export or
	 * null if none exist
	 */
	public String getExportServices();

	/**
	 * Returns the initial bundle start level as maintained by this adaptor
	 * @return the initial bundle start level
	 */
	public int getInitialBundleStartLevel();

	/**
	 * Sets the initial bundle start level 
	 * @param value the initial bundle start level
	 */
	public void setInitialBundleStartLevel(int value);

	/**
	 * Returns the FrameworkLog for the FrameworkAdaptor.  The FrameworkLog
	 * is used by the Framework and FrameworkAdaptor to log any error messages 
	 * and FramworkEvents of type ERROR.  
	 * @return The FrameworkLog to be used by the Framework.
	 */
	public FrameworkLog getFrameworkLog();

	/**
	 * Creates a BundleData object for the System Bundle.  The BundleData
	 * returned will be used to define the System Bundle for the Framework.
	 * @return the BundleData for the System Bundle.
	 * @throws BundleException if any error occurs while creating the 
	 * System BundleData.
	 */
	public BundleData createSystemBundleData() throws BundleException;

	/**
	 * Returns the bundle watcher for this FrameworkAdaptor.
	 * @return the bundle watcher for this FrameworkAdaptor.
	 */
	public BundleWatcher getBundleWatcher();

	/**
	 * Returns the PlatformAdmin for this FrameworkAdaptor.
	 * <p>
	 * This method will not be called until after 
	 * {@link FrameworkAdaptor#frameworkStart(BundleContext)} is called.
	 * @return the PlatformAdmin for this FrameworkAdaptor.
	 */
	public PlatformAdmin getPlatformAdmin();

	/**
	 * Returns the State for this FrameworkAdaptor.
	 * <p>
	 * This method will not be called until after 
	 * {@link FrameworkAdaptor#frameworkStart(BundleContext)} is called.
	 * The State returned is used by the framework to resolve bundle
	 * dependencies.
	 * @return the State for this FrameworkAdaptor.
	 */
	public State getState();

	/**
	 * Returns the parent ClassLoader all BundleClassLoaders created.  All
	 * BundleClassLoaders that are created must use the ClassLoader returned
	 * by this method as a parent ClassLoader.  Each call to this method must 
	 * return the same ClassLoader object.
	 * @return the parent ClassLoader for all BundleClassLoaders created.
	 */
	public ClassLoader getBundleClassLoaderParent();

	/**
	 * Handles a RuntimeException or Error that was caught by the Framework and
	 * there is not a suitable caller to propagate the Throwable to.  This gives
	 * the FrameworkAdaptor the ablity to handle such cases.  For example, a 
	 * FrameworkAdaptor may decide that such unexpected errors should cause an error
	 * message to be logged, or that the Framework should be terminated immediately.
	 * @param error The Throwable for the runtime error that is to be handled.
	 */
	public void handleRuntimeError(Throwable error);
}