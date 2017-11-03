package org.osgi.service.cdi.dto;

import org.osgi.framework.dto.ServiceReferenceDTO;
import org.osgi.service.cdi.dto.model.ActivationModelDTO;

/**
 * Runtime representation of a component activation
 */
public class ActivationDTO {
    /**
     *
     */
    public ActivationModelDTO model;

    /**
     * Must not be null if {@link ActivationModelDTO#serviceClasses
     * model.serviceClasses} is not empty.
     */
    public ServiceReferenceDTO service;
}
