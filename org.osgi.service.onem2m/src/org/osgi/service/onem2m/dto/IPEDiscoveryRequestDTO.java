package org.osgi.service.onem2m.dto;

import org.osgi.dto.DTO;

/**
 * IPEDiscoveryRequestDTO is an element of NotificationEventDTO
 * 
 * @see <a href=
 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
 *      TS-0004 6.3.5.13</a>
 */
public class IPEDiscoveryRequestDTO extends DTO {
	/**
	 * originator
	 * 
	 */
	public String originator;

	/**
	 * FilterCriteria
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 6.3.5.8</a>
	 */
	public FilterCriteriaDTO filterCriteria;

}
