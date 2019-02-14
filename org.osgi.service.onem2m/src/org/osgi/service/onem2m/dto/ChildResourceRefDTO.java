package org.osgi.service.onem2m.dto;

import org.osgi.dto.DTO;

/**
 * DTO expressing ChildResourceRef.
 *
 */
public class ChildResourceRefDTO extends DTO{
	public String uri;
	public String name;
	public Integer type;//Resource Type
	public String specializationID;// any URI, optional
}
