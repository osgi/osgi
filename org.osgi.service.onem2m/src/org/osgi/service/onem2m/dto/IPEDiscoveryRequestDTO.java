package org.osgi.service.onem2m.dto;

import org.osgi.dto.DTO;

/**
 * IPEDiscoveryRequestDTO is an element of NotificationEventDTO
 * 
 * @see oneM2M TS-0004 6.3.5.13
 */
public class IPEDiscoveryRequestDTO extends DTO {
	/**
	 * originator
	 * 
	 * 
	 */
	public String originator;
	
	/**
	 * FilterCriteria
	 * 
	 * @see oneM2M TS-0004 6.3.5.8
	 */
	public FilterCriteriaDTO filterCriteria;
	
}
