
package org.osgi.service.cdi.dto;

import org.osgi.dto.DTO;
import org.osgi.service.cdi.dto.model.DependencyModelDTO;
import org.osgi.service.cdi.dto.model.DependencyModelDTO.MaximumCardinality;

/**
 * Base abstraction for the runtime state of a dependency
 * 
 * @NotThreadSafe
 * @author $Id$
 */
public abstract class DependencyDTO extends DTO {
	/**
	 * The runtime minimum cardinality of the dependency.
	 * <p>
	 * <ul>
	 * <li>If {@link DependencyModelDTO#maximumCardinality} is
	 * {@link MaximumCardinality#ONE ONE} the value must be either 0 or 1.</li>
	 * <li>If {@link DependencyModelDTO#maximumCardinality} is
	 * {@link MaximumCardinality#MANY MANY} the value must be from 0 to
	 * {@link Integer#MAX_VALUE}.
	 * </ul>
	 */
	public int	minimumCardinality;
}
