package org.osgi.model.resource;

import java.util.List;
import java.util.Map;

import org.osgi.framework.Constants;
import org.osgi.framework.Version;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWiring;

/**
 * A resource is the representation of a uniquely identified and typed data.
 * 
 * A resources can be wired together via capabilities and requirements.
 * 
 * TODO decide on identity characteristics of a revision. Given in OSGi multiple
 * bundles can be installed with same bsn/version this cannot be used as a key.
 * 
 * What then is identity of a resource? Object identity? URI (needs getter
 * method?)
 * 
 * @ThreadSafe
 * @version $Id$
 */
public interface Resource {
	/**
	 * Name space for OSGi bundle resources
	 */
	final String RESOURCE_BUNDLE_NAMESPACE = "osgi.bundle";

	/**
	 * Attribute of type {@link String} used to specify the content of a
	 * resource. Typically this specifies a URI which can be used to locate the
	 * content of the resource.
	 */
	final String RESOURCE_CONTENT_ATTRIBUTE = "content";

	/**
	 * Attribute of type {@link String} used to specify the resource symbolic
	 * name.
	 */
	final String RESOURCE_SYMBOLIC_NAME_ATTRIBUTE = "symbolic-name";
	/**
	 * Attribute of type {@link Version} used to specify the resource version.
	 */
	final String RESOURCE_VERSION_ATTRIBUTE = Constants.VERSION_ATTRIBUTE;
	/**
	 * Attribute of type {@link String} used to specify the resource name space.
	 */
	final String RESOURCE_NAMESPACE_ATTRIBUTE = "namespace";

	/**
	 * A requirement {@link Requirement#getDirectives() directive} used to
	 * specify a capability filter. This filter is used to match against a
	 * capability's {@link Capability#getAttributes() attributes}.
	 */
	final String REQUIREMENT_FILTER_DIRECTIVE = Constants.FILTER_DIRECTIVE;

	/**
	 * A requirement {@link Requirement#getDirectives() directive} used to
	 * specify the resolution type for a requirement. The default value is
	 * {@link Constants#RESOLUTION_MANDATORY mandatory}.
	 * 
	 * @see Constants#RESOLUTION_MANDATORY mandatory
	 * @see Constants#RESOLUTION_OPTIONAL optional
	 */
	final String REQUIREMENT_RESOLUTION_DIRECTIVE = Constants.RESOLUTION_DIRECTIVE;

	/**
	 * A requirement {@link Requirement#getDirectives() directive} used to
	 * specify the effective time for the requirement. The default value is
	 * {@link Constants#EFFECTIVE_RESOLVE resolve}.
	 * 
	 * @see Constants#EFFECTIVE_RESOLVE resolve
	 * @see Constants#EFFECTIVE_ACTIVE active
	 */
	final String REQUIREMENT_EFFECTIVE_DIRECTIVE = Constants.EFFECTIVE_DIRECTIVE;

	/**
	 * A requirement {@link Requirement#getDirectives() directive} used to
	 * specify the visibility type for a requirement. The default value is
	 * {@link Constants#VISIBILITY_PRIVATE private}. This directive must only be
	 * used for requirements with the require
	 * {@link BundleRevision#BUNDLE_NAMESPACE bundle} name space.
	 * 
	 * @see Constants#VISIBILITY_PRIVATE private
	 * @see Constants#VISIBILITY_REEXPORT reexport
	 */
	final String REQUIREMENT_VISIBILITY_DIRECTIVE = Constants.VISIBILITY_DIRECTIVE;

	/**
	 * A capability {@link Capability#getDirectives() directive} used to specify
	 * the comma separated list of packages a capability uses.
	 */
	final String CAPABILITY_USES_DIRECTIVE = Constants.USES_DIRECTIVE;

	/**
	 * A capability {@link Capability#getDirectives() directive} used to specify
	 * the effective time for the capability. The default value is
	 * {@link Constants#EFFECTIVE_RESOLVE resolve}.
	 * 
	 * @see Constants#EFFECTIVE_RESOLVE resolve
	 * @see Constants#EFFECTIVE_ACTIVE active
	 */
	final String CAPABILITY_EFFECTIVE_DIRECTIVE = Constants.EFFECTIVE_DIRECTIVE;

	/**
	 * A capability {@link Capability#getDirectives() directive} used to specify
	 * the comma separated list of mandatory attributes which must be specified
	 * in the {@link #REQUIREMENT_FILTER_DIRECTIVE filter} of a requirement in
	 * order for the capability to match the requirement. This directive must
	 * only be used for capabilities with the
	 * {@link BundleRevision#PACKAGE_NAMESPACE package} name space.
	 */
	final String CAPABILITY_MANDATORY_DIRECTIVE = Constants.MANDATORY_DIRECTIVE;

	/**
	 * A capability {@link Capability#getDirectives() directive} used to specify
	 * the comma separated list of classes which must be allowed to be exported.
	 * This directive must only be used for capabilities with the
	 * {@link BundleRevision#PACKAGE_NAMESPACE package} name space.
	 */
	final String CAPABILITY_INCLUDE_DIRECTIVE = Constants.INCLUDE_DIRECTIVE;

	/**
	 * A capability {@link Capability#getDirectives() directive} used to specify
	 * the comma separated list of classes which must not be allowed to be exported.
	 * This directive must only be used for capabilities with the
	 * {@link BundleRevision#PACKAGE_NAMESPACE package} name space.
	 */
	final String CAPABILITY_EXCLUDE_DIRECTIVE = Constants.EXCLUDE_DIRECTIVE;

	/**
	 * Returns the capabilities declared by this resource.
	 * 
	 * @param namespace
	 *            The name space of the declared capabilities to return or
	 *            {@code null} to return the declared capabilities from all name
	 *            spaces.
	 * @return A list containing a snapshot of the declared {@link Capability}s,
	 *         or an empty list if this resource declares no capabilities in the
	 *         specified name space.
	 */
	List<Capability> getCapabilities(String namespace);

	/**
	 * Returns the requirements declared by this bundle resource.
	 * 
	 * @param namespace
	 *            The name space of the declared requirements to return or
	 *            {@code null} to return the declared requirements from all name
	 *            spaces.
	 * @return A list containing a snapshot of the declared {@link Requirement}
	 *         s, or an empty list if this resource declares no requirements in
	 *         the specified name space.
	 */
	List<Requirement> getRequirements(String namespace);

	/**
	 * Gets the attributes associated to this resource.
	 * 
	 * @return The attributes associated with the resource.
	 */
	public Map<String, Object> getAttributes();

}
