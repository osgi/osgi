/*
 * Copyright (c) OSGi Alliance (2012). All Rights Reserved.
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

package org.osgi.framework.namespace;

/**
 * Bundle Capability and Requirement Namespace.
 * 
 * <p>
 * This class defines the names for the attributes and directives for this
 * namespace. All unspecified capability attributes are of type {@code String}
 * and are used as arbitrary matching attributes for the capability. The values
 * associated with the specified directive and attribute keys are of type
 * {@code String}, unless otherwise indicated.
 * 
 * <p>
 * A non-fragment resource with the {@link IdentityNamespace#TYPE_BUNDLE
 * osgi.bundle} type {@link IdentityNamespace#CAPABILITY_TYPE_ATTRIBUTE
 * identity} provides exactly one<sup>&#8224;</sup> bundle capability (that is,
 * the bundle can be required by another bundle). A fragment resource with the
 * {@link IdentityNamespace#TYPE_FRAGMENT osgi.fragment} type
 * {@link IdentityNamespace#CAPABILITY_TYPE_ATTRIBUTE identity} must not declare
 * a bundle capability. A resource requires zero or more bundle requirements
 * (that is, required bundles).
 * <p>
 * &#8224; A resource with no symbolic name must not provide a bundle
 * capability.
 * 
 * @Immutable
 * @version $Id$
 */
public final class BundleNamespace extends AbstractWiringNamespace {

	/**
	 * Namespace name for bundle capabilities and requirements.
	 * 
	 * <p>
	 * Also, a capability attribute used to specify the symbolic name of the
	 * bundle.
	 */
	public static final String	BUNDLE_NAMESPACE					= "osgi.wiring.bundle";

	/**
	 */

	/**
	 * The requirement directive used to specify the visibility type for a
	 * requirement. The default value is {@link #VISIBILITY_PRIVATE private}.
	 * 
	 * @see #VISIBILITY_PRIVATE private
	 * @see #VISIBILITY_REEXPORT reexport
	 */
	public final static String	REQUIREMENT_VISIBILITY_DIRECTIVE	= "visibility";

	/**
	 * The directive value identifying a private
	 * {@link #REQUIREMENT_VISIBILITY_DIRECTIVE visibility} type. A private
	 * visibility type indicates that any {@link PackageNamespace packages} that
	 * are exported by the required bundle are not made visible on the export
	 * signature of the requiring bundle. .
	 * 
	 * @see #REQUIREMENT_VISIBILITY_DIRECTIVE
	 */
	public final static String	VISIBILITY_PRIVATE					= "private";

	/**
	 * The directive value identifying a reexport
	 * {@link #REQUIREMENT_VISIBILITY_DIRECTIVE visibility} type. A reexport
	 * visibility type indicates any {@link PackageNamespace packages} that are
	 * exported by the required bundle are re-exported by the requiring bundle.
	 */
	public final static String	VISIBILITY_REEXPORT					= "reexport";

	private BundleNamespace() {
		// empty
	}
}
