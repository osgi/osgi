package org.osgi.service.onem2m.dto;
import java.util.List;

import org.osgi.dto.DTO;

/**
 * DTO expressing Primitive Content.
 *
 * This Data structure is used as union.
 * Only one field MUST have a value, the others MUST be null.
 *
 */
public class PrimitiveContentDTO extends DTO{
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
