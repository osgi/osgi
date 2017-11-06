
package org.osgi.service.cdi.dto;

import org.osgi.framework.dto.ServiceReferenceDTO;
import org.osgi.service.cdi.dto.model.DependencyModelDTO.MaximumCardinality;
import org.osgi.service.cdi.dto.model.ReferenceModelDTO;

/**
 * A snapshot of the runtime state of a {@link ComponentDTO component} reference
 * dependency
 * 
 * @NotThreadSafe
 * @author $Id: $
 */
public class ReferenceDTO extends DependencyDTO {
	/**
	 * The static model of this reference dependency as resolved at initialization
	 * time.
	 */
	public ReferenceModelDTO		model;

	/**
	 * Indicates the runtime target filter used in addition to the
	 * {@link ReferenceModelDTO#serviceType model.serviceType} to match services.
	 */
	public String					target;

	/**
	 * The set of services that match this reference.
	 * <p>
	 * The value must not be null. An empty array indicates no matching services.
	 * <p>
	 * This dependency is satisfied when.
	 * <p>
	 * <pre>
	 * {@link DependencyDTO#minimumCardinality minimumCardinality} <= matches.size <= {@link MaximumCardinality#toInt() model.maximumCardinality.toInt()}
	 * </pre>
	 */
	public ServiceReferenceDTO[]	matches;
}
