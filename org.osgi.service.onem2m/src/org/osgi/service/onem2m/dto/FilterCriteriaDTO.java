package org.osgi.service.onem2m.dto;

import java.util.List;

import org.osgi.dto.DTO;

/**
 * DTO expresses FilterCriteria.
 *
 * This data structure is used for searching resources.
 * 
 * @see <a href=
 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
 *      TS-0004 6.3.5.8</a>
 * @see <a href=
 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oenM2M
 *      TS-0004 7.3.3.17.17</a>
 * 
 */
public class FilterCriteriaDTO extends DTO {
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

	/**
	 * State Tag Smaller
	 */
	public Integer stateTagSmaller;

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

	/**
	 * Size Above
	 */
	public Integer sizeAbove;

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

	/**
	 * Filter Usage
	 */
	public FilterUsage filterUsage;

	/**
	 * Limit number of Answers
	 */
	public Integer limit;

	/**
	 * Semantic Filter
	 */
	public List<String> semanticsFilter;

	/**
	 * Filter Operation
	 */
	public FilterOperation filterOperation;

	/**
	 * Content Filter Syntax
	 */
	public Integer contentFilterSyntax;

	/**
	 * Content Filter Query
	 */
	public String contentFilterQuery;

	/**
	 * Level
	 */
	public Integer level;

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
	 * 
	 */
	public String applyRelativePath;

	/**
	 * Enum FilterOperation
	 *
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 6.3.4.2.34</a>
	 */
	public static enum FilterOperation {
		/**
		 * Logical AND
		 */
		AND(1),
		/**
		 * Logical OR
		 */
		OR(2);

		private int value;

		private FilterOperation(int i) {
			value = i;
		}

		/**
		 * get assigned value
		 * 
		 * @return assigned integer value
		 */
		public int getValue() {
			return value;
		}
	}

	/**
	 * Enum FilterUsage
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 6.3.4.2.31</a>
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
		 * 
		 * @return assigned integer value
		 */
		public int getValue() {
			return value;
		}

	}
}
