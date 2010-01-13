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

import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;


/**
 * Defines standard names for composite constants.
 * 
 * @ThreadSafe
 * @version $Revision$
 */
public class CompositeConstants {
	/**
	 * Private constructor to prevent objects of this type.
	 */
	private CompositeConstants() {
		// non-instantiable
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
	 * <p>
	 * A valid manifest for a composite bundle must have this directive set
	 * to <code>true</code>.  Any attempt to install a composite which does 
	 * not have this directive set to <code>true</code> must result in a
	 * {@linkplain BundleException}.
	 * 
	 * @see Constants#BUNDLE_SYMBOLICNAME
	 */
	public final static String	COMPOSITE_DIRECTIVE						= "composite";

	/**
	 * Composite manifest header (named &quot;Composite-PackageImportPolicy&quot;)
	 * identifying a list of package constraints to import into the composite.  
	 * Any exported package from a bundle installed in the parent framework which 
	 * satisfies one of the specified package constraints is available to satisfy 
	 * Import-Package constraints from constituent bundles.
	 * <p>
	 * This header uses the same syntax as the {@linkplain Constants#IMPORT_PACKAGE
	 * Import-Package} header.
	 */
	public static final String COMPOSITE_PACKAGE_IMPORT_POLICY = "Composite-PackageImportPolicy";

	/**
	 * Composite manifest header (named &quot;Composite-PackageExportPolicy&quot;)
	 * identifying a list of package constraints to export out of a composite.
	 * Any exported package from a constituent bundle in the composite which 
	 * satisfies one of the specified package constraints is available to satisfy 
	 * Import-Package constraints from bundles installed in the parent framework.
	 * <p>
	 * This header uses the same syntax as the {@linkplain Constants#IMPORT_PACKAGE 
	 * Import-Package} header.
	 */
	public static final String COMPOSITE_PACKAGE_EXPORT_POLICY = "Composite-PackageExportPolicy";

	/**
	 * Composite manifest header (named &quot;Composite-BundleRequirePolicy&quot;)
	 * identifying a list of require bundle constraints to import into the composite.
	 * Any bundle installed in the parent framework which satisfies one of the 
	 * specified require bundle constraints is available to satisfy Require-Bundle
	 * constraints from constituent bundles.
	 * <p>
	 * This header uses the same syntax as the {@linkplain Constants#REQUIRE_BUNDLE
	 * Require-Bundle} header.
	 */
	public static final String COMPOSITE_BUNDLE_REQUIRE_POLICY = "Composite-BundleRequirePolicy";

	/**
	 * Composite manifest header (named &quot;Composite-ServiceImportPolicy&quot;)
	 * identifying a service filter that controls the services to import into the
	 * composite.  See {@link Filter} for a description of the filter syntax.  Any 
	 * services registered by bundles installed in the parent framework that match
	 * the specified service filter is available to constituent bundles.
	 */
	public static final String COMPOSITE_SERVICE_IMPORT_POLICY = "Composite-ServiceImportPolicy";

	/**
	 * Composite manifest header (named &quot;Composite-ServiceExportPolicy&quot;)
	 * identifying a service filter that controls the services to export out of the
	 * composite.  See {@link Filter} for a description of the filter syntax.  Any 
	 * services registered by constituent bundles that match the specified service 
	 * filter is available to bundles installed in the parent framework.
	 */
	public static final String COMPOSITE_SERVICE_EXPORT_POLICY = "Composite-ServiceExportPolicy";

	/**
	 * Manifest header directive identifying the symbolic name a sibling composite 
	 * must have that allows capabilities exported from that composite to match 
	 * an import sharing policy specified by {@link #COMPOSITE_PACKAGE_IMPORT_POLICY 
	 * Composite-PackageImportPolicy} or {@link #COMPOSITE_SERVICE_IMPORT_POLICY 
	 * Composite-ServiceImportPolicy}.
	 * 
	 * <p>
	 * The directive value is encoded in the a manifest header like:
	 * 
	 * <pre>
	 *     Composite-PackageImportPolicy: org.example.pkg; composite-symbolic-name=&quot;org.example.composite&quot;
	 *     Composite-ServiceImportPolicy: &quot;(attr=somevalue)&quot;; composite-symbolic-name=&quot;org.example.composite&quot;
	 * </pre>
	 * 
	 * @see #COMPOSITE_PACKAGE_IMPORT_POLICY
	 * @see #COMPOSITE_SERVICE_IMPORT_POLICY
	 */
	public static final String COMPOSITE_SYMBOLICNAME_DIRECTIVE = "composite-symbolic-name";

	/**
	 * Manifest header directive identifying the version a sibling composite 
	 * must have that allows capabilities exported from that composite to match 
	 * an import sharing policy specified by {@link #COMPOSITE_PACKAGE_IMPORT_POLICY 
	 * Composite-PackageImportPolicy} or {@link #COMPOSITE_SERVICE_IMPORT_POLICY 
	 * Composite-ServiceImportPolicy}.
	 * 
	 * <p>
	 * The directive value is encoded in the a manifest header like:
	 * 
	 * <pre>
	 *     Composite-PackageImportPolicy: org.example.pkg; composite-version=&quot;[1.0,1.1)&quot;
	 *     Composite-ServiceImportPolicy: &quot;(attr=somevalue)&quot;; composite-version=&quot;[1.0,1.1)&quot;
	 * </pre>
	 * 
	 * <p>
	 * In most cases a {@link #COMPOSITE_SYMBOLICNAME_DIRECTIVE composite-symbolic-name} 
	 * directive should be specified along with the composite-version directive like:
	 *
	 * <pre>
	 *     Composite-PackageImportPolicy: org.example.pkg;
	 *      composite-symbolic-name=&quot;org.example.composite&quot;; composite-version=&quot;[1.0,1.1)&quot;
	 *     Composite-ServiceImportPolicy: &quot;(attr=somevalue)&quot;;
	 *      composite-symbolic-name=&quot;org.example.composite&quot;; composite-version=&quot;[1.0,1.1)&quot;
	 * </pre>
	 * 
	 * @see #COMPOSITE_PACKAGE_IMPORT_POLICY
	 * @see #COMPOSITE_SERVICE_IMPORT_POLICY
	 */
	public static final String COMPOSITE_VERSION_DIRECTIVE = "composite-version";

	/**
	 * A supported configuration parameter for a composite framework.
	 * @see Constants#FRAMEWORK_BEGINNING_STARTLEVEL
	 */
	public final static String COMPOSITE_FRAMEWORK_BEGINNING_STARTLEVEL = Constants.FRAMEWORK_BEGINNING_STARTLEVEL;
}
