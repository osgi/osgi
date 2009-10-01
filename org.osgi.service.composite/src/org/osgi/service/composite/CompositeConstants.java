/*
 * Copyright (c) OSGi Alliance (2009). All Rights Reserved.
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

package org.osgi.service.composite;

import org.osgi.framework.Constants;


/**
 * Defines standard names for composite constants.
 * 
 * @ThreadSafe
 * @version $Revision: 5654 $
 * @deprecated This is proposed API. As a result, this API may never be
 *             published or the final API may change substantially by the time
 *             of final publication. You are cautioned against relying upon this
 *             API.
 */
public class CompositeConstants {
	/**
	 * Private constructor to prevent objects of this type.
	 */
	private CompositeConstants() {
	}
	/**
	 * Manifest header directive identifying whether a bundle is a composite.
	 * The default value is <code>false</code>.
	 * 
	 * <p>
	 * The directive value is encoded in the Bundle-SymbolicName manifest header
	 * like:
	 * 
	 * <pre>
	 *     Bundle-SymbolicName: com.acme.composite.test; composite:=true
	 * </pre>
	 * 
	 * <p>
	 * The attribute value may be retrieved from the <code>Dictionary</code>
	 * object returned by the <code>Bundle.getHeaders</code> method.
	 * 
	 * @see Constants#BUNDLE_SYMBOLICNAME
	 */
	public final static String	COMPOSITE_DIRECTIVE						= "composite";

	/**
	 * Manifest header (named &quot;Composite-PackageImportPolicy&quot;)
	 * identifying a list of package constraints to import into the composite.  
	 * Any exported package from a bundle installed in the parent framework which 
	 * satisfies one of the specified package constraints is available to satisfy 
	 * Import-Package constraints from constituent bundles.
	 */
	public static final String COMPOSITE_PACKAGE_IMPORT_POLICY = "Composite-PackageImportPolicy";
	/**
	 * Manifest header (named &quot;Composite-PackageExportPolicy&quot;)
	 * identifying...
	 */
	public static final String COMPOSITE_PACKAGE_EXPORT_POLICY = "Composite-PackageExportPolicy";
	/**
	 * Manifest header (named &quot;Composite-BundleRequirePolicy&quot;)
	 * identifying...
	 */
	public static final String COMPOSITE_BUNDLE_REQUIRE_POLICY = "Composite-BundleRequirePolicy";
	/**
	 * Manifest header (named &quot;Composite-ServiceImportPolicy&quot;)
	 * identifying...
	 */
	public static final String COMPOSITE_SERVICE_IMPORT_POLICY = "Composite-ServiceImportPolicy";
	/**
	 * Manifest header (named &quot;Composite-ServiceExportPolicy&quot;)
	 * identifying...
	 */
	public static final String COMPOSITE_SERVICE_EXPORT_POLICY = "Composite-ServiceExportPolicy";

	/**
	 * A supported configuration parameter for a composite framework.
	 * @see Constants#FRAMEWORK_BEGINNING_STARTLEVEL
	 */
	public final static String COMPOSITE_FRAMEWORK_BEGINNING_STARTLEVEL = Constants.FRAMEWORK_BEGINNING_STARTLEVEL;
}
