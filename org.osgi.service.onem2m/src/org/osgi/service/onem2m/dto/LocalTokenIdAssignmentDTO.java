package org.osgi.service.onem2m.dto;

import org.osgi.dto.DTO;

/**
 * DTO expresses LocalTokenIdAssignment.
 * 
 * @see <a href=
 *      "https://git.onem2m.org/PRO/XSD/blob/master/v3_11_0/CDT-commonTypes-v3_11_0.xsd#L1112-1123">oneM2M
 *      XSD dynAuthLocalTokenIdAssignments and localTokenIdAssignment</a>
 * 
 */
public class LocalTokenIdAssignmentDTO extends DTO {
	/**
	 * local token ID
	 */
	public java.lang.String localTokenID;
	/**
	 * token ID
	 */
	public java.lang.String tokenID;
}
