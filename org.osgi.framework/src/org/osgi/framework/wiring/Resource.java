package org.osgi.framework.wiring;

import java.util.List;


/**
 * A resource is the representation of a uniquely identified and typed data.
 * 
 * A resources can be wired together via capabilities and requirements.
 * 
 * @ThreadSafe
 * @Immutable
 *  
 * @version $Id$
 */
public interface Resource {
	/**
	 * The {@link ResourceConstants#IDENTITY_NAMESPACE osgi.identity} capability
	 * of this resource. This method is a convenience and is equivalent to
	 * calling:
	 * 
	 * <pre>
	 * <code>getCapabilities(ResourceConstants.IDENTITY_NAMESPACE).get(0);</code>
	 * </pre>
	 * 
	 * &#8224; A resource with no symbolic name must not provide an identity
	 * capability.
	 * 
	 * @return the Capability of this resource in the
	 *         {@link ResourceConstants#IDENTITY_NAMESPACE osgi.identity} name
	 *         space or <code>null</code> if this resource has no
	 *         identity<sup>&#8224;</sup>.
	 */
  Capability getIdentity();
  
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
}
