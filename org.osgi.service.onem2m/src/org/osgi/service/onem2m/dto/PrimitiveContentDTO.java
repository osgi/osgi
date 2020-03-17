package org.osgi.service.onem2m.dto;

import java.util.List;

import org.osgi.dto.DTO;

/**
 * DTO expresses Primitive Content.
 * <p>
 * This Data structure is used as union. Only one field MUST have a value, the
 * others MUST be null.
 * 
 * @see <a href=
 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
 *      TS-0004 6.3.5.5</a>
 * @see <a href=
 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
 *      TS-0004 7.2.1</a>
 * @see <a href=
 *      "https://git.onem2m.org/PRO/XSD/blob/master/v3_11_0/CDT-commonTypes-v3_11_0.xsd#L596-602">oneM2M
 *      XSD primitiveContent</a>
 *
 * 
 */
public class PrimitiveContentDTO extends DTO {
	/**
	 * Resource
	 */
	public ResourceDTO resource;

	/**
	 * Resource Wrapper
	 */
	public ResourceWrapperDTO resourceWrapper;

	/**
	 * Aggregated Notification
	 */
	public List<NotificationDTO> aggregatedNotification;

	/**
	 * Security Info
	 */
	public SecurityInfoDTO securityInfo;

	/**
	 * Response Primitive
	 */
	public ResponsePrimitiveDTO responsePrimitive;

	/**
	 * Debug Info
	 */
	public String debugInfo;

	/**
	 * List Of URIs
	 */
	public List<String> listOfURIs;

	/**
	 * URI
	 */
	public String uri;

	/**
	 * Aggregated Response
	 */
	public List<ResponsePrimitiveDTO> aggregatedResponse;

	/**
	 * Child Resource RefList
	 */
	public List<ChildResourceRefDTO> childResourceRefList;

	/**
	 * Notification
	 */
	public NotificationDTO notification;

	/**
	 * Attribute List
	 */
	public List<String> attributeList;

	/**
	 * Request Primitive
	 */
	public RequestPrimitiveDTO requestPrimitive;

	/**
	 * Query Result
	 */
	public String queryResult;

}
