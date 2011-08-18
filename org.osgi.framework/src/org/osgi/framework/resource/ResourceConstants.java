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

package org.osgi.framework.resource;

import org.osgi.framework.Constants;
import org.osgi.framework.Version;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWiring;

/**
 * Defines standard names for the attributes, directives and name spaces for
 * resources, capabilities and requirements.
 * 
 * <p>
 * The values associated with these keys are of type {@code String}, unless
 * otherwise indicated.
 * 
 * @Immutable
 * @version $Id$
 */
public final class ResourceConstants {

	private ResourceConstants() {
		// keep others from creating objects of this type.
	}

	/**
	 * Name space for the identity capability.  Each {@link Resource resource} 
	 * provides exactly one<sup>&#8224;</sup> identity capability that can be 
	 * used to identify the resource.
	 * 
	 * For identity capability attributes the following applies:
	 * <ul>
	 * <li>The
	 * <q>osgi.identity</q> attribute contains the symbolic name of the
	 * resource.
	 * <li>The {@link #IDENTITY_VERSION_ATTRIBUTE version} attribute contains
	 * the {@link Version} of the resource.
	 * <li>The {@link #IDENTITY_TYPE_ATTRIBUTE type} attribute contains the
	 * resource type.
	 * </ul>
	 * <p>
	 * A resource with a symbolic name 
	 * {@link Resource#getCapabilities(String) provides} exactly one 
	 * <sup>&#8224;</sup> identity
	 * {@link Resource#getCapabilities(String) capability}.
	 * <p>
	 * For a {@link BundleRevision revision} with a symbolic name the 
	 * {@link BundleWiring wiring} for the revision
	 * {@link BundleWiring#getCapabilities(String) provides} exactly
	 * one<sup>&#8224;</sup> identity capability. 
	 * <p>
	 * &#8224; A resource with no symbolic name must not provide an identity
	 * capability.
	 */
	public static final String IDENTITY_NAMESPACE = "osgi.identity";

	/**
	 * An {@link #IDENTITY_NAMESPACE identity} capability attribute identifying the
	 * {@link Version version} of the resource.  This attribute must be set to a value of
	 * type {@link Version}.  If the resource has no version then the value 
	 * {@link Version#emptyVersion 0.0.0} must be used for the attribute.
	 */
	public static final String IDENTITY_VERSION_ATTRIBUTE = Constants.VERSION_ATTRIBUTE;

	/**
	 * An {@link #IDENTITY_NAMESPACE identity} capability attribute identifying the
	 * resource type.  This attribute must be set to a value of type {@link String}.
	 * if the resource has no type then the value 
	 * {@link ResourceConstants#IDENTITY_TYPE_UNKNOWN unknown} must be used for the
	 * attribute.
	 */
	public static final String IDENTITY_TYPE_ATTRIBUTE = "type";

	/**
	 * An {@link #IDENTITY_NAMESPACE identity} capability {@link #IDENTITY_TYPE_ATTRIBUTE type}
	 * attribute value identifying the resource type as an OSGi bundle.
	 */
	public static final String IDENTITY_TYPE_BUNDLE = "osgi.bundle";

	/**
	 * An {@link #IDENTITY_NAMESPACE identity} capability {@link #IDENTITY_TYPE_ATTRIBUTE type}
	 * attribute value identifying the resource type as an OSGi fragment.
	 */
	public static final String IDENTITY_TYPE_FRAGMENT = "osgi.fragment";

	/**
	 * An {@link #IDENTITY_NAMESPACE identity} capability {@link #IDENTITY_TYPE_ATTRIBUTE type}
	 * attribute value identifying the resource type as unknown.
	 */
	public static final String IDENTITY_TYPE_UNKNOWN = "unknown";

	/**
	 * An {@link #IDENTITY_NAMESPACE identity} capability {@link Requirement#getDirectives() directive}
	 * identifying if the resource is a singleton.  A {@link String} value of &quot;true&quot; indicates
	 * the resource is a singleton; any other value or <code>null</code> indicates the resource is not a 
	 * singleton.
	 */
	public static final String IDENTITY_SINGLETON_DIRECTIVE = Constants.SINGLETON_DIRECTIVE;

	/**
	 * Name space for package capabilities and requirements.
	 * 
	 * For capability attributes the following applies:
	 * <ul>
	 * <li>The
	 * <q>osgi.wiring.package</q> attribute contains the name of the package.
	 * <li>The {@link Constants#VERSION_ATTRIBUTE version} attribute contains
	 * the the {@link Version} of the package if one is specified or
	 * {@link Version#emptyVersion} if not specified.
	 * <li>The {@link Constants#BUNDLE_SYMBOLICNAME_ATTRIBUTE
	 * bundle-symbolic-name} attribute contains the symbolic name of the
	 * resource providing the package if one is specified.
	 * <li>The {@link Constants#BUNDLE_VERSION_ATTRIBUTE bundle-version}
	 * attribute contains the {@link Version} of resource providing the package
	 * if one is specified or {@link Version#emptyVersion} if not specified.
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
	 * <li>The {@link Constants#BUNDLE_VERSION_ATTRIBUTE bundle-version}
	 * attribute contains the {@link Version} of the bundle if one is specified
	 * or {@link Version#emptyVersion} if not specified.
	 * <li>All other attributes are of type {@link String} and are used as
	 * arbitrary matching attributes for the capability.
	 * </ul>
	 * <p>
	 * A non-fragment resource with the {@link #IDENTITY_TYPE_BUNDLE
	 * osgi.bundle} type {@link #IDENTITY_TYPE_ATTRIBUTE identity} provides 
	 * exactly one <sup>&#8224;</sup> bundle
	 * {@link Resource#getCapabilities(String) capability} (that is, the bundle
	 * can be required by another bundle). A fragment resource with the 
	 * {@link #IDENTITY_TYPE_FRAGMENT osgi.fragment} type 
	 * {@link #IDENTITY_TYPE_ATTRIBUTE identity} must not declare
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
	 * <li>The {@link Constants#BUNDLE_VERSION_ATTRIBUTE bundle-version}
	 * attribute contains the {@link Version} of the bundle if one is specified
	 * or {@link Version#emptyVersion} if not specified.
	 * <li>All other attributes are of type {@link String} and are used as
	 * arbitrary matching attributes for the capability.
	 * </ul>
	 * <p>
	 * <p>
	 * A non-fragment resource with the with the {@link #IDENTITY_TYPE_BUNDLE
	 * osgi.bundle} type {@link #IDENTITY_TYPE_ATTRIBUTE identity} provides 
	 * zero or one <sup>&#8224;</sup> host
	 * {@link Resource#getCapabilities(String) capabilities}. 
	 * A fragment resource with the 
	 * {@link #IDENTITY_TYPE_FRAGMENT osgi.fragment} type 
	 * {@link #IDENTITY_TYPE_ATTRIBUTE identity} must not declare
	 * a host capability and must 
	 * {@link Resource#getRequirements(String) declare} exactly one host
	 * requirement.
	 * <p>
	 * &#8224; A resource with no bundle symbolic name must not provide a host
	 * capability.
	 */
	public static final String WIRING_HOST_NAMESPACE = "osgi.wiring.host";

	/**
	 * A requirement {@link Requirement#getDirectives() directive} used to
	 * specify a capability filter. This filter is used to match against a
	 * capability's {@link Capability#getAttributes() attributes}.
	 */
	public final static String REQUIREMENT_FILTER_DIRECTIVE = Constants.FILTER_DIRECTIVE;

	/**
	 * A requirement {@link Requirement#getDirectives() directive} used to
	 * specify the resolution type for a requirement. The default value is
	 * {@link #REQUIREMENT_RESOLUTION_MANDATORY mandatory}.
	 * 
	 * @see #REQUIREMENT_RESOLUTION_MANDATORY mandatory
	 * @see #REQUIREMENT_RESOLUTION_OPTIONAL optional
	 */
	public final static String REQUIREMENT_RESOLUTION_DIRECTIVE = Constants.RESOLUTION_DIRECTIVE;
	/**
	 * A directive value identifying a mandatory
	 * {@link Resource#getRequirements(String) requirement} resolution type. A
	 * mandatory resolution type indicates that the requirement must be resolved
	 * when the {@link Resource resource} is resolved. If such requirement
	 * cannot be resolved, the resource fails to resolve.
	 * 
	 * @see #REQUIREMENT_RESOLUTION_DIRECTIVE
	 */
	public final static String REQUIREMENT_RESOLUTION_MANDATORY = Constants.RESOLUTION_MANDATORY;

	/**
	 * A directive value identifying an optional
	 * {@link Resource#getRequirements(String) requirement} resolution type. An
	 * optional resolution type indicates that the requirement is optional and
	 * the {@link Resource resource} may be resolved without requirement being
	 * resolved.
	 * 
	 * @see #REQUIREMENT_RESOLUTION_DIRECTIVE
	 */
	public final static String REQUIREMENT_RESOLUTION_OPTIONAL = Constants.RESOLUTION_OPTIONAL;

	/**
	 * A requirement {@link Requirement#getDirectives() directive} used to
	 * specify the effective time for the requirement. The default value is
	 * {@link #EFFECTIVE_RESOLVE resolve}.
	 * 
	 * @see #EFFECTIVE_RESOLVE resolve
	 * @see #EFFECTIVE_ACTIVE active
	 */
	public final static String REQUIREMENT_EFFECTIVE_DIRECTIVE = Constants.EFFECTIVE_DIRECTIVE;

	/**
	 * A directive value identifying a {@link #CAPABILITY_EFFECTIVE_DIRECTIVE
	 * capability} or {@link #REQUIREMENT_EFFECTIVE_DIRECTIVE requirement} that
	 * is effective at resolve time. Capabilities and requirements with an
	 * effective time of resolve are the only capabilities which are processed
	 * while resolving a resource.
	 * 
	 * @see #REQUIREMENT_EFFECTIVE_DIRECTIVE
	 * @see #CAPABILITY_EFFECTIVE_DIRECTIVE
	 */
	public final static String EFFECTIVE_RESOLVE = Constants.EFFECTIVE_RESOLVE;

	/**
	 * A directive value identifying a {@link #CAPABILITY_EFFECTIVE_DIRECTIVE
	 * capability} or {@link #REQUIREMENT_EFFECTIVE_DIRECTIVE requirement} that
	 * is effective at active time. Capabilities and requirements with an
	 * effective time of active are ignored while resolving a resource.
	 * 
	 * @see #REQUIREMENT_EFFECTIVE_DIRECTIVE
	 * @see #CAPABILITY_EFFECTIVE_DIRECTIVE
	 */
	public final static String EFFECTIVE_ACTIVE = Constants.EFFECTIVE_ACTIVE;

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
	public final static String REQUIREMENT_VISIBILITY_DIRECTIVE = Constants.VISIBILITY_DIRECTIVE;

	/**
	 * A directive value identifying a private
	 * {@link #REQUIREMENT_VISIBILITY_DIRECTIVE visibility} type. A private
	 * visibility type indicates that any {@link #WIRING_PACKAGE_NAMESPACE
	 * packages} that are exported by the required
	 * {@link #WIRING_BUNDLE_NAMESPACE bundle} are not made visible on the
	 * export signature of the requiring {@link #WIRING_BUNDLE_NAMESPACE bundle}
	 * .
	 * 
	 * @see #REQUIREMENT_VISIBILITY_DIRECTIVE
	 */
	public final static String REQUIREMENT_VISIBILITY_PRIVATE = Constants.VISIBILITY_PRIVATE;

	/**
	 * A directive value identifying a reexport
	 * {@link #REQUIREMENT_VISIBILITY_DIRECTIVE visibility} type. A reexport
	 * visibility type indicates any {@link #WIRING_PACKAGE_NAMESPACE packages}
	 * that are exported by the required {@link #WIRING_BUNDLE_NAMESPACE bundle}
	 * are re-exported by the requiring {@link #WIRING_BUNDLE_NAMESPACE bundle}.
	 */
	public final static String REQUIREMENT_VISIBILITY_REEXPORT = Constants.VISIBILITY_REEXPORT;
	
	/**
	 * A requirement {@link Requirement#getDirectives() directive} used to
	 * specify the cardinality for a requirement. The default value is
	 * {@link #REQUIREMENT_CARDINALITY_SINGULAR singular}.
	 * 
	 * @see #REQUIREMENT_CARDINALITY_MULTIPLE multiple
	 * @see #REQUIREMENT_CARDINALITY_SINGULAR singular
	 */
	public final static String REQUIREMENT_CARDINALITY_DIRECTIVE = "cardinality";
	  
	/**
	 * A directive value identifying a multiple
	 * {@link #REQUIREMENT_CARDINALITY_DIRECTIVE cardinality} type.
	 */
	public final static String REQUIREMENT_CARDINALITY_MULTIPLE = "multiple";
	  
	/**
	 * A directive value identifying a singular
	 * {@link #REQUIREMENT_CARDINALITY_DIRECTIVE cardinality} type.
	 */
	public final static String REQUIREMENT_CARDINALITY_SINGULAR = "singular";


	/**
	 * A capability {@link Capability#getDirectives() directive} used to specify
	 * the comma separated list of {@link #WIRING_PACKAGE_NAMESPACE package}
	 * names a capability uses.
	 */
	public final static String CAPABILITY_USES_DIRECTIVE = Constants.USES_DIRECTIVE;

	/**
	 * A capability {@link Capability#getDirectives() directive} used to specify
	 * the effective time for the capability. The default value is
	 * {@link #EFFECTIVE_RESOLVE resolve}.
	 * 
	 * @see #EFFECTIVE_RESOLVE resolve
	 * @see #EFFECTIVE_ACTIVE active
	 */
	public final static String CAPABILITY_EFFECTIVE_DIRECTIVE = Constants.EFFECTIVE_DIRECTIVE;

	/**
	 * A capability {@link Capability#getDirectives() directive} used to specify
	 * the comma separated list of mandatory attributes which must be specified
	 * in the {@link #REQUIREMENT_FILTER_DIRECTIVE filter} of a requirement in
	 * order for the capability to match the requirement. This directive must
	 * only be used for capabilities with the {@link #WIRING_PACKAGE_NAMESPACE
	 * package}, {@link #WIRING_BUNDLE_NAMESPACE bundle}, or
	 * {@link #WIRING_HOST_NAMESPACE host} name space.
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
