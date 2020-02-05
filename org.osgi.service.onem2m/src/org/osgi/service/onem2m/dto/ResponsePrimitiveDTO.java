package org.osgi.service.onem2m.dto;
import java.util.List;

import org.osgi.dto.DTO;

/**
 * DTO expressing Response Primitive.
 */
public class ResponsePrimitiveDTO extends DTO{
//	@javax.xml.bind.annotation.XmlElement( required  = true)
	/**
	 * Response Status Code
	 */
	public Integer responseStatusCode;
//	@javax.xml.bind.annotation.XmlElement( required  = true)
	/**
	 * Request Identifier
	 */
	public String requestIdentifier;
	/**
	 * Primitive Content
	 */
	public PrimitiveContentDTO content;
	/**
	 * To Parameter
	 */
	public String to;
	/**
	 * From Parameter
	 */
	public String from;
	/**
	 * Originating Timestamp
	 */
	public String originatingTimestamp;
	
	/**
	 * ResultExpiration Timestamp
	 */
	public String resultExpirationTimestamp;
	/**
	 * Event Category
	 */
	public String eventCategory;// TODO: Check type
//	@javax.xml.bind.annotation.XmlElement( required  = false)
	/**
	 * Content Status
	 */
	public ContentStatus contentStatus;
//	@javax.xml.bind.annotation.XmlElement( required  = false)
	/**
	 * Content Offset
	 */
	public Integer contentOffset;
	/**
	 * Assigned Token Identifiers
	 */
	public List<LocalTokenIdAssignmentDTO> assignedTokenIdentifiers;
	/**
	 * Token Request Info
	 */
	public List<DasInfoDTO> tokenReqInfo;

	// Added R3.0
	/**
	 * AuthSignatureReqInfo
	 */
	public Boolean AuthSignatureReqInfo;
	/**
	 * Release Version Indicator
	 */
	public ReleaseVersion releaseVersionIndicator;
	/**
	 * Vendor Information
	 */
	public String vendorInformation;

	/**
	 * Enum ContentStatus
	 */
	public static enum ContentStatus{
		/**
		 * PARTIAL_CONTENT
		 */
		PARTIAL_CONTENT, // 1
		/**
		 * FULL_CONTENT
		 */
		FULL_CONTENT;  //2

	}
}
