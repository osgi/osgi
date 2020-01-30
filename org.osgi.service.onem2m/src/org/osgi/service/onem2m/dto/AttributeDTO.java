package org.osgi.service.onem2m.dto;

import org.osgi.dto.DTO;

/**
 * DTO expresses Attribute.
 *
 * This is typically used in FilterCriteriaDTO for expressing matching condition.
 */
public class AttributeDTO extends DTO{
	/**
	 * Attribute name
	 */
	public String name;

	/**
	 * Supposed value of the attribute
	 */
	public Object value;
}
