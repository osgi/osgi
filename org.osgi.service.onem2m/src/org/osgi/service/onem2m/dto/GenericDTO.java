package org.osgi.service.onem2m.dto;

import java.util.HashMap;

import org.osgi.dto.DTO;


/**
 * GenericDTO expresses miscellaneous data structures.
 *
 */
public class GenericDTO extends DTO{
	/**
	 * type of data structure, which is represented by this DTO.
	 * 
	 * This is optional field. The creator of the DTO may use the field for clarification purpose.
	 * Receiver should not rely on this information to analyze data structure, since this information may not provided.
	*/
	public String type;

	/**
	 * Substructure of DTO. Type of the value part should be one of types allowed as OSGi DTO.
	 */
	public HashMap<String, Object>element;
}
