package org.osgi.service.onem2m.dto;
import java.util.List;

import org.osgi.dto.DTO;

/**
 * DTO expressing FilterCriteria.
 *
 * This data structure is used for searching resources.
 */
public class FilterCriteriaDTO extends DTO{
	/**
	 * Created Before
	 */
	public String createdBefore;
	
	/**
	 * Created After
	 */
	public String createdAfter;
	
	/**
	 * Modified Since
	 */
	public String modifiedSince;
	
	/**
	 * Unmodified Since
	 */
	public String unmodifiedSince;
//	@javax.xml.bind.annotation.XmlElement( required  = false)
	
	/**
	 * State Tag Smaller
	 */
	public Integer stateTagSmaller;
//	@javax.xml.bind.annotation.XmlElement( required  = false)
	
	/**
	 * State Tag Bigger
	 */
	public Integer stateTagBigger;
	
	/**
	 * Expire Before
	 */
	public String expireBefore;
	
	/**
	 * Expire After
	 */
	public String expireAfter;
	
	/** 
	 * Labels
	 */
	public List<String> labels;
	
	/**
	 * Resource Type
	 */
	public List<Integer> resourceType;
//	@javax.xml.bind.annotation.XmlElement( required  = false)
	
	/**
	 * Size Above
	 */
	public Integer sizeAbove;
//	@javax.xml.bind.annotation.XmlElement( required  = false)
	
	/**
	 * Size Below
	 */
	public Integer sizeBelow;
	
	/**
	 * Content Type
	 */
	public List<String> contentType;
	
	/**
	 * Attribute
	 */
	public List<AttributeDTO> attribute;
//	@javax.xml.bind.annotation.XmlElement( required  = false)
	
	/**
	 * Filter Usage
	 */
	public FilterUsage filterUsage;
//	@javax.xml.bind.annotation.XmlElement( required  = false)
	
	/**
	 * Limit number of Answers
	 */
	public Integer limit;
	
	/**
	 * Semantic Filter
	 */
	public List<String> semanticsFilter;
//	@javax.xml.bind.annotation.XmlElement( required  = false)
	
	/**
	 * Filter Operation
	 */
	public FilterOperation filterOperation;
//	@javax.xml.bind.annotation.XmlElement( required  = false)
	
	/**
	 * Content Filter Syntax
	 */
	public Integer contentFilterSyntax;
	
	/**
	 * Content Filter Query
	 */
	public String contentFilterQuery;
//	@javax.xml.bind.annotation.XmlElement( required  = false)
	
	/**
	 * Level
	 */
	public Integer level;
//	@javax.xml.bind.annotation.XmlElement( required  = false)
	
	/**
	 * Offset
	 */
	public Integer offset;

	// added in R3
	/**
	 * Child Labels
	 */
	public List<String> childLabels;
	
	/**
	 * Parent Labels
	 */
	public List<String> parentLabels;
	
	/**
	 * Label Query
	 */
	public String labelsQuery;
	
	/**
	 * Child Resource Type
	 */
	public List<Integer> childResourceType;
	
	/**
	 * Parent Resource Type
	 */
	public List<Integer> parentResourceType;
	
	/**
	 * Child Attribute
	 */
	public List<AttributeDTO> childAttribute;
	
	/**
	 * Parent Attribute
	 */
	public List<AttributeDTO> parentAttribute;
	
	/**
	 * Apply Relative Path
	 */
	public String applyRelativePath;

	/** 
	 * Enum FilterOperation
	 *
	 */
	public static enum FilterOperation {
		/**
		 * AND
		 */
		AND(1), 
		/**
		 * OR
		 */
		OR(2);

		private int value;

		private FilterOperation(int i) {
			value = i;
		}
		/**
		 * get assigned value
		 * @return assigned integer value
		 */
		public int getValue() {
			return value;
		}
	}

	/**
	 * Enum FilterUsage
	 */
	public static enum FilterUsage {
		/**
		 * Discovery Criteria
		 */
		DiscoveryCriteria(1), 
		/**
		 * Conditional Retrieve
		 */
		ConditionalRetrival(2), 
		/**
		 * IPE on Demand Discovery
		 */
		IPEOndemandDiscovery(3);

		private int value;

		private FilterUsage(int i) {
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
