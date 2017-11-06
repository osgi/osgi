
package org.osgi.service.cdi.dto;

import org.osgi.framework.dto.ServiceReferenceDTO;
import org.osgi.service.cdi.dto.model.ExtensionModelDTO;

/**
 * A snapshot of the runtime state of an extension dependency of a
 * {@link ContainerDTO container}
 * 
 * @NotThreadSafe
 * @author $Id: $
 */
public class ExtensionDTO extends DependencyDTO {
	/**
	 * The model of this extension dependency
	 * <p>
	 * Must not be null
	 */
	public ExtensionModelDTO	model;

	/**
	 * The service to which the extension dependency is resolved.
	 * <p>
	 * This extension dependency is satisfied when <code>match != null</code>.
	 */
	public ServiceReferenceDTO	match;
}
