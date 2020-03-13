package org.osgi.service.onem2m.dto;
import org.osgi.dto.DTO;

/**
 * DTO expressing Notification.
 */
public class NotificationDTO extends DTO{
	/**
	 * Notification Event
	 */
	public NotificationEventDTO notificationEvent;
//	@javax.xml.bind.annotation.XmlElement( required  = false)
	/**
	 * Flag showing verification request. 
	 * 
	 * This field is optional.
	 */
	public Boolean verificationRequest;
//	@javax.xml.bind.annotation.XmlElement( required  = false)
	/**
	 * Flag showing subscription deletion
	 * 
	 * This field is optional.
	 */
	public Boolean subscriptionDeletion;
	
	/**
	 * URI referring subscription resource.
	 */
	public String subscriptionReference;
	
	/**
	 * creator
	 */
	public String creator;
	
	/**
	 * URI for notification target
	 */
	public String notificationForwardingURI;
//	@javax.xml.bind.annotation.XmlElement( required  = false)
	
	/**
	 * IPE Discovery Request.
	 * 
	 */
	public IPEDiscoveryRequestDTO ipeDiscoveryRequest;
}
