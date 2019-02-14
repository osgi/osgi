package org.osgi.service.onem2m.dto;
import java.util.List;

import org.osgi.dto.DTO;

/**
 * DTO expressing Response Primitive.
 */
public class ResponsePrimitiveDTO extends DTO{
//	@javax.xml.bind.annotation.XmlElement( required  = true)
	public Integer responseStatusCode;
//	@javax.xml.bind.annotation.XmlElement( required  = true)
	public String requestIdentifier;
	public PrimitiveContentDTO content;
	public String to;
	public String from;
	public String originatingTimestamp;
	public String resultExpirationTimestamp;
	public String eventCategory;
//	@javax.xml.bind.annotation.XmlElement( required  = false)
	public ContentStatus contentStatus;
//	@javax.xml.bind.annotation.XmlElement( required  = false)
	public Integer contentOffset;
	public List<LocalTokenIdAssignmentDTO> assignedTokenIdentifiers;//Map<String,Object>
	public List<DasInfoDTO> tokenReqInfo;//DynAuthTokenReqInfoDTO

	// Added R3.0
	public Boolean AuthSignatureReqInfo;
	public ReleaseVersion releaseVersionIndicator;
	public String vendorInformation;

	public static enum ContentStatus{
		PARTIAL_CONTENT, // 1
		FULL_CONTENT;  //2

	}
}
