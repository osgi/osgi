
package org.osgi.service.cdi.dto.model;

/**
 * A description of a reference dependency of a component
 * <p>
 * The content of this DTO is resolved form metadata at initialization time and
 * remains the same between the CDI bundle restarts.
 * <p>
 * 
 * @NotThreadSafe
 * @author $Id$
 */
public class ReferenceModelDTO extends DependencyModelDTO {
	/**
	 * The name of the reference.
	 * <p>
	 * The value must not be null.
	 */
	public String	name;

	/**
	 * Indicates a target filter used in addition to the {@link #serviceType} to
	 * match services.
	 * <p>
	 * Contains the target filter resolved from the CDI bundle metadata. The filter
	 * can be replaced by configuration at runtime.
	 */
	public String	target;

	/**
	 * Indicates the type of service matched by the reference.
	 * <p>
	 * The value must not be null.
	 */
	public String	serviceType;
}
