package org.osgi.service.onem2m.dto;
import java.util.List;

import org.osgi.dto.DTO;

/**
 * DTO expressing Primitive Content.
 *
 * This Data structure is like union.
 * Only one field MUST have a value, other field MUST be null.
 *
 */
public class PrimitiveContentDTO extends DTO{
	public ResourceDTO resource;
	public ResourceWrapperDTO resourceWrapper;
	public List<NotificationDTO> aggregatedNotification;
	public SecurityInfoDTO securityInfo;
	public ResponsePrimitiveDTO responsePrimitive;

	public String debugInfo;

	public List<String> listOfURIs;
	public String uri;

	public List<ResponsePrimitiveDTO> aggregatedResponse;
	public List<ChildResourceRefDTO> childResourceRefList;
}
