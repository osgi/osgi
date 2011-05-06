/*
 * Copyright (c) OSGi Alliance (2011). All Rights Reserved.
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
package org.osgi.model.resource;

import org.osgi.framework.Version;

/**
 * Defines standard names for the attributes, directives and name spaces for
 * resources, capabilities and requirements.
 * 
 * <p>
 * The values associated with these keys are of type {@code String}, unless
 * otherwise indicated.
 * 
 * @version $Id$
 */
public final class ResourceConstants {

	/**
	 * Name space for package capabilities and requirements.
	 * 
	 * For capability attributes the following applies:
	 * <ul>
	 * <li>The
	 * <q>osgi.wiring.package</q> attribute contains the name of the package.
	 * <li>The {@link #VERSION_ATTRIBUTE version} attribute contains the the
	 * {@link Version} of the package if one is specified or
	 * {@link Version#emptyVersion} if not specified.
	 * <li>The {@link #BUNDLE_SYMBOLICNAME_ATTRIBUTE bundle-symbolic-name}
	 * attribute contains the symbolic name of the resource providing the
	 * package if one is specified.
	 * <li>The {@link #BUNDLE_VERSION_ATTRIBUTE bundle-version} attribute
	 * contains the {@link Version} of resource providing the package if one is
	 * specified or {@link Version#emptyVersion} if not specified.
	 * <li>All other attributes are of type {@link String} and are used as
	 * arbitrary matching attributes for the capability.
	 * </ul>
	 * <p>
	 * A resource provides zero or more package
	 * {@link Resource#getCapabilities(String) capabilities} (this is, exported
	 * packages) and requires zero or more package
	 * {@link Resource#getRequirements(String) requirements} (that is, imported
	 * packages).
	 */
	public static final String WIRING_PACKAGE_NAMESPACE = "osgi.wiring.package";

	/**
	 * Name space for bundle capabilities and requirements.
	 * 
	 * For capability attributes the following applies:
	 * <ul>
	 * <li>The
	 * <q>osgi.wiring.bundle</q> attribute contains the symbolic name of the
	 * bundle.
	 * <li>The {@link #BUNDLE_VERSION_ATTRIBUTE bundle-version} attribute
	 * contains the {@link Version} of the bundle if one is specified or
	 * {@link Version#emptyVersion} if not specified.
	 * <li>All other attributes are of type {@link String} and are used as
	 * arbitrary matching attributes for the capability.
	 * </ul>
	 * <p>
	 * A non-fragment resource with the {@link #RESOURCE_BUNDLE_NAMESPACE
	 * osgi.bundle} name space provides exactly one <sup>&#8224;</sup> bundle
	 * {@link Resource#getCapabilities(String) capability} (that is, the bundle
	 * can be required by another bundle). A fragment resource must not declare
	 * a bundle capability. A resource requires zero or more bundle
	 * {@link Resource#getRequirements(String) requirements} (that is, required
	 * bundles).
	 * <p>
	 * &#8224; A resource with no symbolic name must not provide a bundle
	 * capability.
	 */
	public static final String WIRING_BUNDLE_NAMESPACE = "osgi.wiring.bundle";

	/**
	 * Name space for host capabilities and requirements.
	 * 
	 * For capability attributes the following applies:
	 * <ul>
	 * <li>The
	 * <q>osgi.wiring.host</q> attribute contains the symbolic name of the
	 * bundle.
	 * <li>The {@link #BUNDLE_VERSION_ATTRIBUTE bundle-version} attribute
	 * contains the {@link Version} of the bundle if one is specified or
	 * {@link Version#emptyVersion} if not specified.
	 * <li>All other attributes are of type {@link String} and are used as
	 * arbitrary matching attributes for the capability.
	 * </ul>
	 * <p>
	 * <p>
	 * A non-fragment resource with the {@link #RESOURCE_BUNDLE_NAMESPACE
	 * osgi.bundle} name space provides zero or one <sup>&#8224;</sup> host
	 * {@link Resource#getCapabilities(String) capabilities} zero or
	 * one<sup>&#8224;</sup> host capability. A fragment resource must
	 * {@link Resource#getRequirements(String) declare} exactly one host
	 * requirement.
	 * <p>
	 * &#8224; A resource with no bundle symbolic name must not provide a host
	 * capability.
	 */
	public static final String WIRING_HOST_NAMESPACE = "osgi.wiring.host";

	public static final String VERSION_ATTRIBUTE = "version";
	public static final String BUNDLE_SYMBOLICNAME_ATTRIBUTE = "bundle-symbolic-name";
	public static final String BUNDLE_VERSION_ATTRIBUTE = "bundle-version";

	/**
	 * The {@link #RESOURCE_NAMESPACE_ATTRIBUTE name space} for OSGi bundle
	 * resources
	 */
	public final static String RESOURCE_BUNDLE_NAMESPACE = "osgi.bundle";

	/**
	 * A resource {@link Resource#getAttributes() attribute} used to specify the
	 * content of a resource. Typically this specifies a URI which can be used
	 * to locate the content of the resource.
	 */
	public final static String RESOURCE_CONTENT_ATTRIBUTE = "content";

	/**
	 * A resource {@link Resource#getAttributes() attribute} used to specify the
	 * resource symbolic name.
	 */
	public final static String RESOURCE_SYMBOLIC_NAME_ATTRIBUTE = "symbolic-name";

	/**
	 * A resource {@link Resource#getAttributes() attribute} used to specify the
	 * resource version.
	 */
	public final static String RESOURCE_VERSION_ATTRIBUTE = VERSION_ATTRIBUTE;

	/**
	 * A resource {@link Resource#getAttributes() attribute} used to specify the
	 * resource name space.
	 */
	public final static String RESOURCE_NAMESPACE_ATTRIBUTE = "namespace";

	/**
	 * A requirement {@link Requirement#getDirectives() directive} used to
	 * specify a capability filter. This filter is used to match against a
	 * capability's {@link Capability#getAttributes() attributes}.
	 */
	public final static String REQUIREMENT_FILTER_DIRECTIVE = "filter";

	/**
	 * A requirement {@link Requirement#getDirectives() directive} used to
	 * specify the resolution type for a requirement. The default value is
	 * {@link #REQUIREMENT_RESOLUTION_MANDATORY mandatory}.
	 * 
	 * @see #REQUIREMENT_RESOLUTION_MANDATORY mandatory
	 * @see #REQUIREMENT_RESOLUTION_OPTIONAL optional
	 */
	public final static String REQUIREMENT_RESOLUTION_DIRECTIVE = "resolution";
	public final static String REQUIREMENT_RESOLUTION_MANDATORY = "mandatory";
	public final static String REQUIREMENT_RESOLUTION_OPTIONAL = "optional";

	/**
	 * A requirement {@link Requirement#getDirectives() directive} used to
	 * specify the effective time for the requirement. The default value is
	 * {@link #EFFECTIVE_RESOLVE resolve}.
	 * 
	 * @see #EFFECTIVE_RESOLVE resolve
	 * @see #EFFECTIVE_ACTIVE active
	 */
	public final static String REQUIREMENT_EFFECTIVE_DIRECTIVE = "effective";
	public final static String EFFECTIVE_RESOLVE = "resolve";
	public final static String EFFECTIVE_ACTIVE = "active";

	/**
	 * A requirement {@link Requirement#getDirectives() directive} used to
	 * specify the visibility type for a requirement. The default value is
	 * {@link #REQUIREMENT_VISIBILITY_PRIVATE private}. This directive must only
	 * be used for requirements with the require
	 * {@link #WIRING_BUNDLE_NAMESPACE bundle} name space.
	 * 
	 * @see #REQUIREMENT_VISIBILITY_PRIVATE private
	 * @see #REQUIREMENT_VISIBILITY_REEXPORT reexport
	 */
	public final static String REQUIREMENT_VISIBILITY_DIRECTIVE = "visibility";
	public final static String REQUIREMENT_VISIBILITY_PRIVATE = "private";
	public final static String REQUIREMENT_VISIBILITY_REEXPORT = "reexport";

	/**
	 * A capability {@link Capability#getDirectives() directive} used to specify
	 * the comma separated list of packages a capability uses.
	 */
	public final static String CAPABILITY_USES_DIRECTIVE = "uses";

	/**
	 * A capability {@link Capability#getDirectives() directive} used to specify
	 * the effective time for the capability. The default value is
	 * {@link #EFFECTIVE_RESOLVE resolve}.
	 * 
	 * @see #EFFECTIVE_RESOLVE resolve
	 * @see #EFFECTIVE_ACTIVE active
	 */
	public final static String CAPABILITY_EFFECTIVE_DIRECTIVE = REQUIREMENT_EFFECTIVE_DIRECTIVE;

	/**
	 * A capability {@link Capability#getDirectives() directive} used to specify
	 * the comma separated list of mandatory attributes which must be specified
	 * in the {@link #REQUIREMENT_FILTER_DIRECTIVE filter} of a requirement in
	 * order for the capability to match the requirement. This directive must
	 * only be used for capabilities with the {@link #WIRING_PACKAGE_NAMESPACE
	 * package} name space.
	 */
	public final static String CAPABILITY_MANDATORY_DIRECTIVE = "mandatory";

	/**
	 * A capability {@link Capability#getDirectives() directive} used to specify
	 * the comma separated list of classes which must be allowed to be exported.
	 * This directive must only be used for capabilities with the
	 * {@link #WIRING_PACKAGE_NAMESPACE package} name space.
	 */
	public final static String CAPABILITY_INCLUDE_DIRECTIVE = "include";

	/**
	 * A capability {@link Capability#getDirectives() directive} used to specify
	 * the comma separated list of classes which must not be allowed to be
	 * exported. This directive must only be used for capabilities with the
	 * {@link #WIRING_PACKAGE_NAMESPACE package} name space.
	 */
	public final static String CAPABILITY_EXCLUDE_DIRECTIVE = "exclude";
}
