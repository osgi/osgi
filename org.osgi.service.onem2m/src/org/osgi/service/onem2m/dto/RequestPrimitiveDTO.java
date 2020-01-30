package org.osgi.service.onem2m.dto;

import java.util.List;

import org.osgi.dto.DTO;

/**
 * DTO expresses Request Primitive.
 */
public class RequestPrimitiveDTO extends DTO {
//	@javax.xml.bind.annotation.XmlElement(required = true)
	/**
	 * Operation
	 */
	public Operation operation;
//	@javax.xml.bind.annotation.XmlElement(required = true)
	
	/**
	 * To Parameter
	 */
	public String to;
	
	/**
	 * From Parameter.
	 * 
	 * In other word, originator of request is stored.
	 */
	public String from;
//	@javax.xml.bind.annotation.XmlElement(required = true)

	/**
	 * Request Identifier
	 */
	public String requestIdentifier;
//	@javax.xml.bind.annotation.XmlElement(required = false)

	/**
	 * Resource Type
	 */
	public Integer resourceType;
	
	/**
	 * Primitive Content
	 */
	public PrimitiveContentDTO content;
	
	/**
	 * Role IDs
	 */
	public List<String> roleIDs;

	/**
	 * Originating Timestamp
	 */
	public String originatingTimestamp;
	
	/**
	 * Request Expiration Timestamp
	 */
	public String requestExpirationTimestamp;
	/**
	 * Result Expiration Timestamp
	 */
	public String resultExpirationTimestamp;
	
	/**
	 * Operation Execution Time
	 */
	public String operationExecutionTime;

	/**
	 * Response Type Info
	 */
	public ResponseTypeInfoDTO responseType;
	
	/**
	 * Result Persistence
	 */
	public String resultPersistence;
//	@javax.xml.bind.annotation.XmlElement(required = false)
	/**
	 * Result Content
	 */
	public ResultContent resultContent;
	
	/**
	 * Event Category
	 */
	public String eventCategory;
//	@javax.xml.bind.annotation.XmlElement(required = false)
	
	/**
	 * Delivery Aggregation
	 */
	public Boolean deliveryAggregation;
	
	/**
	 * Group Request Identifier
	 */
	public String groupRequestIdentifier;
	
	/**
	 * Filter Criteria
	 */
	public FilterCriteriaDTO filterCriteria;
//	@javax.xml.bind.annotation.XmlElement(required = false)
	
	/**
	 * Discovery Result Type
	 */
	public DiscoveryResultType discoveryResultType;
	
	/**
	 * tokens
	 */
	public String tokens;
	
	/**
	 * Token Identifiers
	 */
	public List<String> tokenIDs;
	
	/**
	 * Local Token Identifiers
	 */
	public List<String> localTokenIDs;
//	@javax.xml.bind.annotation.XmlElement(required = false)
	
	/**
	 * Token Request Indicator
	 */
	public Boolean tokenRequestIndicator;

	// Added at R3.0
	/**
	 * Group Request Target Members
	 */
	public List<String> groupRequestTargetMembers;
	
	/**
	 * Author Sign Indicator
	 */
	public Boolean authorSignIndicator;
	
	/**
	 * Author Signs
	 */
	public List<String> authorSigns;
	
	/**
	 * Author Relation Indicator
	 */
	public Boolean authorRelIndicator;// Rel = Relation
	
	/**
	 * Semantic Query Indicator
	 */
	public Boolean semanticQueryIndicator;
	
	/**
	 * Release Version
	 */
	public ReleaseVersion releaseVersionIndicator;
	
	/**
	 * Vendor Information
	 */
	public String vendorInformation;

	public static enum DiscoveryResultType {
		/**
		 * structured
		 */
		structured(1), 
		/**
		 * unstructured
		 */
		unstructured(2);

		int value;

		private DiscoveryResultType(int i) {
			value = i;
		}

		public int getValue() {
			return value;
		}
	}
	/**
	 * enum type for Result Content
	 * 
	 *
	 */
	public static enum ResultContent {
		/**
		 * nothing
		 */
		nothing(1), 
		/**
		 * attributes
		 */
		attributes(2),
		/**
		 * hierarchicalAddress
		 */
		hierarchicalAddress(3), 
		/**
		 * hierarchicalAddressAndAttributes
		 */
		hierarchicalAddressAndAttributes(4), 
		/**
		 * attributesAndChildResources
		 */
		attributesAndChildResources(5),
		/**
		 * attributesAndChildResourceReferences
		 */
		attributesAndChildResourceReferences(6), 
		/**
		 * childResourceReferences
		 */
		childResourceReferences(7), 
		/**
		 * originalResource
		 */
		originalResource(8),
		/**
		 * childResources
		 */
		childResources(9);

		int value;

		private ResultContent(int i) {
			value = i;
		}

		/**
		 * get assigned integer value
		 * @return assigned integer value
		 */
		public int getValue() {
			return value;
		}
	}

	/**
	 * enum type for Operation
	 * 
	 *
	 */
	public static enum Operation {
		/**
		 * Create
		 */
		Create(1),
		/**
		 * Retrieve
		 */
		Retrieve(2), 
		/**
		 * Update
		 */
		Update(3),
		/**
		 * Delete
		 */
		Delete(4), 
		/**
		 * Notify
		 */
		Notify(5);

		int value;

		private Operation(int i) {
			value = i;
		}

		/**
		 * get assigned integer value
		 * @return assigned integer value
		 */
		public int getValue() {
			return value;
		}
	}

}
