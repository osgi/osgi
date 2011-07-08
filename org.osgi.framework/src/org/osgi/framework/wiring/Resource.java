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
   * The identity capability of this resource. All resources have an identity capability
   * defined with the following attributes:
   * 
   * <pre>capability osgi.identity {
   *  osgi.identity=&lt;name&gt;
   *  version=&lt;version&gt;
   *  type=&lt;type&gt;
   *}</pre>
   *
   * <p>
   * This method is a convenience and is equivalent to calling <code>getCapabilities(ResourceConstants.IDENTITY_NAMESPACE).get(0)</code>
   * 
   * @return the Capability of this resource in the <code>osgi.identity</code> namespace 
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
