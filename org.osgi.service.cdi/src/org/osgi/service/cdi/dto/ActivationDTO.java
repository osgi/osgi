
package org.osgi.service.cdi.dto;

import org.osgi.framework.dto.ServiceReferenceDTO;
import org.osgi.service.cdi.dto.model.ActivationModelDTO;

/**
 * A snapshot of the runtime state of a {@link ComponentDTO component}
 * activation
 * 
 * @NotThreadSafe
 * @author $Id$
 */
public class ActivationDTO {
	/**
	 * The statically resolved model of this activation
	 */
	public ActivationModelDTO	model;

	/**
	 * The service this activation may have registered.
	 * <p>
	 * Must not be null if {@link ActivationModelDTO#serviceClasses
	 * model.serviceClasses} is not empty.
	 */
	public ServiceReferenceDTO	service;

	/**
	 * The number of objects this activation has created.
	 * <p>
	 * Each instance is dependency injected.
	 * <p>
	 * Depends on {@link ActivationModelDTO#scope model.scope}
	 */
	public int					instances;
}
