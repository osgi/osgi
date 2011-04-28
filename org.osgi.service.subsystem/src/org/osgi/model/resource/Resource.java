package org.osgi.model.resource;

import java.util.List;
import java.util.Map;

import org.osgi.framework.Constants;
import org.osgi.framework.Version;


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
	final String BUNDLE_NAMESPACE = "osgi.bundle";

	/**
	 * Attribute of type {@link String} used to specify the resource location.
	 */
	final String LOCATION_ATTRIBUTE = "location";

	/**
	 * Attribute of type {@link String} used to specify the resource symbolic name.
	 */
	final String SYMBOLIC_NAME_ATTRIBUTE = "symbolic-name";
	/**
	 * Attribute of type {@link Version} used to specify the resource version.
	 */
	final String VERSION_ATTRIBUTE = Constants.VERSION_ATTRIBUTE;
	/**
	 * Attribute of type {@link String} used to specify the resource name space.
	 */
	final String NAMESPACE_ATTRIBUTE = "namespace";

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
	 *         s, or an empty list if this resource declares no
	 *         requirements in the specified name space.
	 */
	List<Requirement> getRequirements(String namespace);

	/**
	 * Gets the attributes associated to this resource.
	 * 
	 * @return The attributes associated with the resource.
	 */
	public Map<String, Object> getAttributes();

}
