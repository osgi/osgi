package org.osgi.service.onem2m.dto;

import org.osgi.dto.DTO;

/**
 * DTO expresses ChildResourceRef.
 * 
 * @see <a href=
 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
 *      TS-0004 6.3.5.29</a>
 * 
 * @see <a href=
 *      "https://git.onem2m.org/PRO/XSD/blob/master/v3_11_0/CDT-commonTypes-v3_11_0.xsd#L885-893">oneM2M
 *      XSD childResourceRef</a>
 * 
 * 
 */
public class ChildResourceRefDTO extends DTO {
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
	public Integer type;// Resource Type

	/**
	 * resource type specialization of the child resource pointed to by the URI in
	 * case type represents a flexContainer. This is an optional field.
	 */
	public String specializationID;// any URI, optional
}
