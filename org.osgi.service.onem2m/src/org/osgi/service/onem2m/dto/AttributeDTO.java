package org.osgi.service.onem2m.dto;

import org.osgi.dto.DTO;

/**
 * DTO expresses Attribute.
 * <p>
 * This class is typically used in FilterCriteriaDTO for expressing matching
 * condition.
 * 
 * @see <a href=
 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
 *      TS-0004 6.3.5.9</a>
 * 
 */
public class AttributeDTO extends DTO {
	/**
	 * Attribute name
	 */
	public String name;

	/**
	 * Supposed value of the attribute
	 */
	public Object value;
}
