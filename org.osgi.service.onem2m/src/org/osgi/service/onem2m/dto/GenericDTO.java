package org.osgi.service.onem2m.dto;

import java.util.Map;

import org.osgi.dto.DTO;


/**
 * GenericDTO expresses miscellaneous data structures of oneM2M.
 *
 */
public class GenericDTO extends DTO{

	/**
	 * Substructure of DTO. Type of the value part should be one of types allowed as OSGi DTO.
	 */
	public Map<String, Object>element;
}
