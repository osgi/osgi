package org.osgi.service.onem2m.dto;

import org.osgi.dto.DTO;

/**
 * DTO expressing ChildResourceRef.
 *
 */
public class ChildResourceRefDTO extends DTO{
	/**
	 * URI to the child resource.
	 */
	public String uri;
	
	/**
	 * name of the child resource pointed to by the URI
	 */
	public String name;
	
	/**
	 * resourceType of the child resource pointed to by the URI
	 */
	public Integer type;//Resource Type
	
	/**
	 * resource type specialization of the child resource pointed to 
	 * by the URI in case @type represents a flexContainer.
	 * This is an optional field.
	 */
	public String specializationID;// any URI, optional
}
