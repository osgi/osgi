package org.osgi.service.onem2m.dto;
import java.util.Map;

import org.osgi.dto.DTO;

/**
 * DTO expressing Notification.
 */
public class NotificationDTO extends DTO{
	public Map<String,Object> notificationEvent;//NotificationEventDTO
//	@javax.xml.bind.annotation.XmlElement( required  = false)
	public Boolean verificationRequest;
//	@javax.xml.bind.annotation.XmlElement( required  = false)
	public Boolean subscriptionDeletion;
	public String subscriptionReference;
	public String creator;
	public String notificationForwardingURI;
//	@javax.xml.bind.annotation.XmlElement( required  = false)
	public Map<String,Object> ipeDiscoveryRequest;//IPEDiscoveryRequestDTO
}
