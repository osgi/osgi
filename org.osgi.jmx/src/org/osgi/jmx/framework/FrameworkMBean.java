/*
 * Copyright 2008 Oracle Corporation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.osgi.jmx.framework;

import java.io.IOException;

import javax.management.openmbean.CompositeData;

/**
 * 
 * The FrameworkMbean provides mechanisms to exert control over the framework.
 * For many operations, it provides a batch mechanism to avoid excessive message
 * passing when interacting remotely.
 */
public interface FrameworkMBean {

	/**
	 * Retrieve the framework start level
	 * 
	 * @return the framework start level
	 * @throws IOException
	 *             if the operation failed
	 */
	int getFrameworkStartLevel() throws IOException;

	/**
	 * Answer the initial start level assigned to a bundle when it is first
	 * started
	 * 
	 * @return the start level
	 * @throws IOException
	 *             if the operation failed
	 */
	int getInitialBundleStartLevel() throws IOException;

	/**
	 * Install the bundle indicated by the bundleLocations
	 * 
	 * @param location
	 *            - the location of the bundle to install
	 * @return the bundle id the installed bundle
	 * @throws IOException
	 *             if the operation does not succeed
	 */
	long installBundle(String location) throws IOException;

	/**
	 * Install the bundle indicated by the bundleLocations
	 * 
	 * @param location
	 *            - the location to assign to the bundle
	 * @param url
	 *            - the URL which will supply the bytes for the bundle
	 * @return the bundle id the installed bundle
	 * @throws IOException
	 *             if the operation does not succeed
	 */
	long installBundle(String location, String url) throws IOException;

	/**
	 * Batch install the bundles indicated by the list of bundleLocationUrls
	 * <p>
	 * 
	 * @see org.osgi.jmx.codec.BundleBatchInstallResult BatchBundleResult for
	 *      the precise specification of the CompositeData type representing the
	 *      returned result.
	 *      <p>
	 * @param locations
	 *            - the array of locations of the bundles to install
	 * @return the resulting state from executing the operation
	 * @throws IOException
	 *             if the operation does not succeed
	 */
	CompositeData installBundles(String[] locations) throws IOException;

	/**
	 * Batch install the bundles indicated by the list of bundleLocationUrls
	 * <p>
	 * 
	 * @see org.osgi.jmx.codec.BundleBatchInstallResult BatchBundleResult for
	 *      the precise specification of the CompositeData type representing the
	 *      returned result.
	 *      <p>
	 * @param locations
	 *            - the array of locations to assign to the installed bundles
	 * @param urls
	 *            - the array of urls which supply the bundle bytes
	 * @return the resulting state from executing the operation
	 * @throws IOException
	 *             if the operation does not succeed
	 */
	CompositeData installBundles(String[] locations, String[] urls)
			throws IOException;

	/**
	 * Force the update, replacement or removal of the pacakges identified by
	 * the list of bundles
	 * <p>
	 * 
	 * @see org.osgi.jmx.codec.BundleBatchActionResult BundleBatchActionResult
	 *      for the precise specification of the CompositeData type representing
	 *      the returned result.
	 *      <p>
	 * @param bundleIdentifier
	 *            - the bundle identifier
	 * @throws IOException
	 *             if the operation failed
	 */
	void refreshPackages(long bundleIdentifier) throws IOException;

	/**
	 * Force the update, replacement or removal of the pacakges identified by
	 * the list of bundles
	 * <p>
	 * 
	 * @see org.osgi.jmx.codec.BundleBatchActionResult BundleBatchActionResult
	 *      for the precise specification of the CompositeData type representing
	 *      the returned result.
	 *      <p>
	 * @param bundleIdentifiers
	 *            - the array of bundle identifiers
	 * @return the resulting state from executing the operation
	 * @throws IOException
	 *             if the operation failed
	 */
	CompositeData refreshPackages(long[] bundleIdentifiers) throws IOException;

	/**
	 * Resolve the bundle indicated by the unique symbolic name and version
	 * 
	 * @param bundleIdentifier
	 *            - the bundle identifier
	 * @return true if the bundle was resolved, false otherwise
	 * @throws IOException
	 *             if the operation does not succeed
	 * @throws IllegalArgumentException
	 *             if the bundle indicated does not exist
	 */
	boolean resolveBundle(long bundleIdentifier) throws IOException;

	/**
	 * Batch resolve the bundles indicated by the list of bundle identifiers
	 * 
	 * @param bundleIdentifiers
	 *            = the identifiers of the bundles to resolve
	 * @return true if the bundles were resolved, false otherwise
	 * @throws IOException
	 *             if the operation does not succeed
	 */
	boolean resolveBundles(long[] bundleIdentifiers) throws IOException;

	/**
	 * Restart the framework by updating the system bundle
	 * 
	 * @throws IOException
	 *             if the operation failed
	 */
	void restartFramework() throws IOException;

	/**
	 * Set the start level for the bundle identifier
	 * 
	 * @param bundleIdentifier
	 *            - the bundle identifier
	 * @param newlevel
	 *            - the new start level for the bundle
	 * @throws IOException
	 *             if the operation failed
	 */
	void setBundleStartLevel(long bundleIdentifier, int newlevel)
			throws IOException;

	/**
	 * Set the start levels for the list of bundles
	 * <p>
	 * 
	 * @see org.osgi.jmx.codec.BundleBatchActionResult BundleBatchActionResult
	 *      for the precise specification of the CompositeData type representing
	 *      the returned result.
	 *      <p>
	 * @param bundleIdentifiers
	 *            - the array of bundle identifiers
	 * @param newlevels
	 *            - the array of new start level for the bundles
	 * @return the resulting state from executing the operation
	 * @throws IOException
	 *             if the operation failed
	 */
	CompositeData setBundleStartLevels(long[] bundleIdentifiers, int[] newlevels)
			throws IOException;

	/**
	 * Set the start level for the framework
	 * 
	 * @param newlevel
	 *            - the new start level
	 * @throws IOException
	 *             if the operation failed
	 */
	void setFrameworkStartLevel(int newlevel) throws IOException;

	/**
	 * Set the initial start level assigned to a bundle when it is first started
	 * 
	 * @param newlevel
	 *            - the new start level
	 * @throws IOException
	 *             if the operation failed
	 */
	void setInitialBundleStartLevel(int newlevel) throws IOException;

	/**
	 * Shutdown the framework by stopping the system bundle
	 * 
	 * @throws IOException
	 *             if the operation failed
	 */
	void shutdownFramework() throws IOException;

	/**
	 * Start the bundle indicated by the bundle identifier
	 * 
	 * @param bundleIdentifier
	 *            - the bundle identifier
	 * @throws IOException
	 *             if the operation does not succeed
	 * @throws IllegalArgumentException
	 *             if the bundle indicated does not exist
	 */
	void startBundle(long bundleIdentifier) throws IOException;

	/**
	 * Batch start the bundles indicated by the list of bundle identifier
	 * <p>
	 * 
	 * @see org.osgi.jmx.codec.BundleBatchActionResult BundleBatchActionResult
	 *      for the precise specification of the CompositeData type representing
	 *      the returned result.
	 *      <p>
	 * @param bundleIdentifiers
	 *            - the array of bundle identifiers
	 * @return the resulting state from executing the operation
	 * @throws IOException
	 *             if the operation does not succeed
	 */
	CompositeData startBundles(long[] bundleIdentifiers) throws IOException;

	/**
	 * Stop the bundle indicated by the bundle identifier
	 * 
	 * @param bundleIdentifier
	 *            - the bundle identifier
	 * @throws IOException
	 *             if the operation does not succeed
	 * @throws IllegalArgumentException
	 *             if the bundle indicated does not exist
	 */
	void stopBundle(long bundleIdentifier) throws IOException;

	/**
	 * Batch stop the bundles indicated by the list of bundle identifier
	 * <p>
	 * 
	 * @see org.osgi.jmx.codec.BundleBatchActionResult BundleBatchActionResult
	 *      for the precise specification of the CompositeData type representing
	 *      the returned result.
	 *      <p>
	 * @param bundleIdentifiers
	 *            - the array of bundle identifiers
	 * @return the resulting state from executing the operation
	 * @throws IOException
	 *             if the operation does not succeed
	 */
	CompositeData stopBundles(long[] bundleIdentifiers) throws IOException;

	/**
	 * Uninstall the bundle indicated by the bundle identifier
	 * 
	 * @param bundleIdentifier
	 *            - the bundle identifier
	 * @throws IOException
	 *             if the operation does not succeed
	 * @throws IllegalArgumentException
	 *             if the bundle indicated does not exist
	 */
	void uninstallBundle(long bundleIdentifier) throws IOException;

	/**
	 * Batch uninstall the bundles indicated by the list of bundle identifiers
	 * <p>
	 * 
	 * @see org.osgi.jmx.codec.BundleBatchActionResult BundleBatchActionResult
	 *      for the precise specification of the CompositeData type representing
	 *      the returned result.
	 *      <p>
	 * @param bundleIdentifiers
	 *            - the array of bundle identifiers
	 * @return the resulting state from executing the operation
	 * @throws IOException
	 *             if the operation does not succeed
	 */
	CompositeData uninstallBundles(long[] bundleIdentifiers) throws IOException;

	/**
	 * Update the bundle indicated by the bundle identifier
	 * 
	 * @param bundleIdentifier
	 *            - the bundle identifier
	 * @throws IOException
	 *             if the operation does not succeed
	 * @throws IllegalArgumentException
	 *             if the bundle indicated does not exist
	 */
	void updateBundle(long bundleIdentifier) throws IOException;

	/**
	 * Update the bundle identified by the bundle identifier
	 * 
	 * @param bundleIdentifier
	 *            - the bundle identifier
	 * @param url
	 *            - the URL to use to update the bundle
	 * @throws IOException
	 *             if the operation does not succeed
	 * @throws IllegalArgumentException
	 *             if the bundle indicated does not exist
	 */
	void updateBundle(long bundleIdentifier, String url) throws IOException;

	/**
	 * Batch update the bundles indicated by the list of bundle identifier
	 * <p>
	 * 
	 * @see org.osgi.jmx.codec.BundleBatchActionResult BundleBatchActionResult
	 *      for the precise specification of the CompositeData type representing
	 *      the returned result.
	 *      <p>
	 * @param bundleIdentifiers
	 *            - the array of bundle identifiers
	 * @return the resulting state from executing the operation
	 * @throws IOException
	 *             if the operation does not succeed
	 */
	CompositeData updateBundles(long[] bundleIdentifiers) throws IOException;

	/**
	 * Update the bundle uniquely identified by the bundle symbolic name and
	 * version using the contents of the supplied urls
	 * <p>
	 * 
	 * @see org.osgi.jmx.codec.BundleBatchActionResult BundleBatchActionResult
	 *      for the precise specification of the CompositeData type representing
	 *      the returned result.
	 *      <p>
	 * @param bundleIdentifiers
	 *            - the array of bundle identifiers
	 * @param urls
	 *            - the array of URLs to use to update the bundles
	 * @return the resulting state from executing the operation
	 * @throws IOException
	 *             if the operation does not succeed
	 * @throws IllegalArgumentException
	 *             if the bundle indicated does not exist
	 */
	CompositeData updateBundles(long[] bundleIdentifiers, String[] urls)
			throws IOException;

	/**
	 * Update the framework by updating the system bundle
	 * 
	 * @throws IOException
	 *             if the operation failed
	 */
	void updateFramework() throws IOException;

	/**
	 * The name of the item containing the list of bundles completing the batch
	 * operation in the CompositeData
	 */
	String BUNDLE_COMPLETED = "Completed";

	/**
	 * The name of the item containing the error message of the batch operation
	 * in the CompositeData
	 */
	String BUNDLE_ERROR_MESSAGE = "Error";

	/**
	 * The name of the item containing the bundle which caused the error during
	 * the batch operation in the CompositeData
	 */
	String BUNDLE_IN_ERROR = "BundleInError";

	/**
	 * The name of the item containing the list of remaing bundles unproccessed
	 * by the failing batch operation in the CompositeData
	 */
	String BUNDLE_REMAINING = "Remaining";

	/**
	 * The name of the item containing the success status of the batch operation
	 * in the CompositeData
	 */
	String BUNDLE_SUCCESS = "Success";

	/**
	 * The item names in the CompositeData representing the result of a batch
	 * operation
	 */
	String[] BUNDLE_ACTION_RESULT = { BUNDLE_SUCCESS, BUNDLE_ERROR_MESSAGE,
			BUNDLE_COMPLETED, BUNDLE_IN_ERROR, BUNDLE_REMAINING };

	/**
	 * The name of the CompositeType which represents the result of a batch
	 * operation
	 */
	public static final String BUNDLE_BATCH_ACTION_RESULT = "BundleBatchActionResult";

	/**
	 * The name of the CompositeType which represents the result of a batch
	 * install operation
	 */
	public static final String BUNDLE_BATCH_INSTALL_RESULT = "BundleBatchInstallResult";

}