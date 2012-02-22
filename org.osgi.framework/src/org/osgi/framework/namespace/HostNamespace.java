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

import org.osgi.resource.Namespace;

/**
 * Host Capability and Requirement Namespace.
 * 
 * <p>
 * This class defines the names for the attributes and directives for this
 * namespace. All unspecified capability attributes are of type {@code String}
 * and are used as arbitrary matching attributes for the capability. The values
 * associated with the specified directive and attribute keys are of type
 * {@code String}, unless otherwise indicated.
 * <p>
 * For compatibility with previous versions of the specification all directives
 * specified on the {@code Bundle-SymbolicName} header are available to this
 * namespace. The directive {@link #REQUIREMENT_VISIBILITY_DIRECTIVE visibility}
 * should be looked up using the {@link BundleNamespace#BUNDLE_NAMESPACE bundle}
 * namespace. The directive {@link #CAPABILITY_SINGLETON_DIRECTIVE singleton}
 * should be looked up using the {@link IdentityNamespace#IDENTITY_NAMESPACE
 * identity} namespace. Also, the {@link Namespace#CAPABILITY_USES_DIRECTIVE uses}
 * directive has no meaning to this namespace.
 * <p>
 * A non-fragment resource with the with the
 * {@link IdentityNamespace#TYPE_BUNDLE osgi.bundle} type
 * {@link IdentityNamespace#CAPABILITY_TYPE_ATTRIBUTE identity} provides zero or
 * one<sup>&#8224;</sup> host capabilities. A fragment resource with the
 * {@link IdentityNamespace#TYPE_FRAGMENT osgi.fragment} type
 * {@link IdentityNamespace#CAPABILITY_TYPE_ATTRIBUTE identity} must not declare
 * a host capability and must declare exactly one host requirement.
 * <p>
 * &#8224; A resource with no bundle symbolic name must not provide a host
 * capability.
 * 
 * @Immutable
 * @version $Id$
 */
public final class HostNamespace extends AbstractWiringNamespace {

	/**
	 * Namespace name for host capabilities and requirements.
	 * 
	 * <p>
	 * Also, the capability attribute used to specify the symbolic name of the
	 * host.
	 * 
	 */
	public static final String	HOST_NAMESPACE								= "osgi.wiring.host";

	/**
	 * The capability directive identifying if the resource is a singleton. A
	 * {@code String} value of &quot;true&quot; indicates the resource is a
	 * singleton; any other value or <code>null</code> indicates the resource is
	 * not a singleton.
	 */
	public static final String	CAPABILITY_SINGLETON_DIRECTIVE	= "singleton";

	/**
	 * The capability directive identifying if and when a fragment may attach to
	 * a host bundle. The default value is {@link #FRAGMENT_ATTACHMENT_ALWAYS
	 * always}.
	 * 
	 * @see #FRAGMENT_ATTACHMENT_ALWAYS
	 * @see #FRAGMENT_ATTACHMENT_RESOLVETIME
	 * @see #FRAGMENT_ATTACHMENT_NEVER
	 */
	public static final String	CAPABILITY_FRAGMENT_ATTACHMENT_DIRECTIVE	= "fragment-attachment";

	/**
	 * The directive value indicating that fragments are allowed to attach to
	 * the host bundle at any time (while the host is resolved or during the
	 * process of resolving the host bundle).
	 * 
	 * @see #CAPABILITY_FRAGMENT_ATTACHMENT_DIRECTIVE
	 */
	public static final String	FRAGMENT_ATTACHMENT_ALWAYS					= "always";

	/**
	 * The directive value indicating that fragments are allowed to attach to
	 * the host bundle only during the process of resolving the host bundle.
	 * 
	 * @see #CAPABILITY_FRAGMENT_ATTACHMENT_DIRECTIVE
	 */
	public static final String	FRAGMENT_ATTACHMENT_RESOLVETIME				= "resolve-time";

	/**
	 * The directive value indicating that no fragments are allowed to attach to
	 * the host bundle at any time.
	 * 
	 * @see #CAPABILITY_FRAGMENT_ATTACHMENT_DIRECTIVE
	 */
	public static final String	FRAGMENT_ATTACHMENT_NEVER					= "never";

	/**
	 * The requirement directive used to specify the type of the extension
	 * fragment.
	 * 
	 * @see #EXTENSION_FRAMEWORK
	 * @see #EXTENSION_BOOTCLASSPATH
	 */
	public final static String	REQUIREMENT_EXTENSION_DIRECTIVE				= "extension";

	/**
	 * The directive value indicating that the extension fragment is to be
	 * loaded by the framework's class loader.
	 * 
	 * 
	 * @see #REQUIREMENT_EXTENSION_DIRECTIVE
	 */
	public final static String	EXTENSION_FRAMEWORK							= "framework";

	/**
	 * The directive value indicating that the extension fragment is to be
	 * loaded by the boot class loader.
	 * 
	 * @see #REQUIREMENT_EXTENSION_DIRECTIVE
	 */
	public final static String	EXTENSION_BOOTCLASSPATH						= "bootclasspath";

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
	 * 
	 * @see #REQUIREMENT_VISIBILITY_DIRECTIVE
	 */
	public final static String	VISIBILITY_REEXPORT					= "reexport";

	private HostNamespace() {
		// empty
	}
}
