/*
 * Copyright (c) OSGi Alliance (2008). All Rights Reserved.
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
package org.osgi.service.framework;

import java.util.Map;

import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;

/**
 * Framework service that is used to create link bundles.
 * <p>
 * If present, there will only be a single instance of this service registered
 * with the Framework.
 * 
 * @ThreadSafe
 * @version $Revision: 5897 $
 */
// TODO javadoc needs review
public interface LinkBundleFactory {
	/**
	 * Manifest header (named &quot;LinkServiceFilter-Import;) identifying the
	 * service filters that are used by a child link bundle to select services 
	 * that will be registered into a child framework from a parent link bundle.
	 */
	public static final String LINK_SERVICE_FILTER_IMPORT = "LinkServiceFilter-Import";

	/**
	 * Manifest header (named &quot;LinkServiceFilter-Export;) identifying the
	 * service filters that are used by a parent link bundle to select services 
	 * that will be registered into a parent framework from a child link bundle.
	 */
	public static final String LINK_SERVICE_FILTER_EXPORT = "LinkServiceFilter-Export";

	/**
	 * Creates a new child <code>LinkBundle</code>.  The child link bundle has 
	 * a new child <code>Framework</code> associated with it and a companion link
	 * bundle which is installed in the child framework.  Link bundles
	 * share packages and services between a parent framework and a child framework.
	 * <p>
	 * The following steps are required to create a child link bundle:
	 * <ol>
	 * <li>If a bundle containing the same location string is already installed,
	 * then if the Bundle object is a child <code>LinkBundle</code> then that 
	 * link bundle is returned; otherwise a BundleException is thrown indicating
	 * that an incompatible bundle is already installed at the specified location.</li>
	 * <li>The child link bundle's associated resources are allocated. The 
	 * associated resources minimally consist of a unique identifier and a 
	 * persistent storage area. If this step fails, a BundleException is 
	 * thrown.  The linkManifest map is used to provide the headers for the 
	 * child link bundle and its companion link bundle.
	 * <p>  
	 * If link manifest map does not contain the following header(s) then a 
	 * BundleException is thrown:
	 *   <ul>
	 *     <li> {@link Constants#BUNDLE_SYMBOLICNAME Bundle-SymbolicName} the symbolic name 
	 *     used for the child link bundle and its companion link bundle.
	 *   </ul>
	 * </p>
	 * The link manifest map may optionally contain the following header(s):
	 * 	 <ul>
	 *     <li> {@link Constants#BUNDLE_VERSION Bundle-Version} the bundle version
	 *     used for the child link bundle and its companion link bundle.
	 *     <li> {@link Constants#IMPORT_PACKAGE Import-Package} the packages
	 *     which are imported from the parent framework by the child link bundle
	 *     and are exported to the child framework by the companion link bundle.</li>
	 *     <li>{@link Constants#EXPORT_PACKAGE Export-Package} the packages
	 *     which are imported from the child framework by the companion link bundle
	 *     and are exported to the parent framework by the child link bundle.</li>
	 *     <li>{@link #LINK_SERVICE_FILTER_IMPORT LinkServiceFilter-Import} the service
	 *     filters which are acquired from the parent framework by the child link bundle
	 *     and are registered in the child framework by the companion link bundle.</li>
	 *     <li>{@link #LINK_SERVICE_FILTER_EXPORT LinkServiceFilter-Export} the service
	 *     filters which are acquired from the child framework by the companion link bundle
	 *     and are registered in the parent framework by the child link bundle.</li>
	 *   </ul>
	 * <li>A child framework is create which uses a storage area under the child link 
	 * bundle's associated persistent storage.  Note that if the framework configuration
	 * property {@link Constants#FRAMEWORK_STORAGE org.osgi.framework.storage} is specified
	 * in the framework config then it is ignored and a storage area located under the
	 * child link bundle's associated persistent storage area is used. </li>
	 * <li>The child framework is initialized (see {@link Framework#init()}.
	 * <li>A parent link bundle is installed into the child framework</li>
	 * <li>The child link bundle's state is set to INSTALLED.</li>
	 * <li>A bundle event of type {@link BundleEvent#INSTALLED} is fired for the
	 * child link bundle.
	 * <li>The <code>LinkBundle</code> object for the newly child link bundle
	 * is returned
	 * </ol>
	 * <p>
	 * @param frameworkConfig the child framework configuration.
	 * @param location the bundle location used for the child link bundle and 
	 * its companion bundle.
	 * @param linkManifest the manifest used to create the link bundle
	 * @return A new child link bundle.
	 * @throws SecurityException If the caller does not have
	 *         <code>AllPermission</code>.
	 * @see Framework
	 * @see LinkBundle
	 */
	LinkBundle newChildLinkBundle(Map /*<String, String>*/ frameworkConfig, String location, Map /*<String, String>*/ linkManifest) throws BundleException;

}
