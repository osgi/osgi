package org.osgi.service.onem2m.dto;
import java.util.List;

import org.osgi.dto.DTO;

/**
 * DTO expressing FilterCriteria.
 *
 * This data structure is used for searching resources.
 */
public class FilterCriteriaDTO extends DTO{
	public String createdBefore;
	public String createdAfter;
	public String modifiedSince;
	public String unmodifiedSince;
//	@javax.xml.bind.annotation.XmlElement( required  = false)
	public Integer stateTagSmaller;
//	@javax.xml.bind.annotation.XmlElement( required  = false)
	public Integer stateTagBigger;
	public String expireBefore;
	public String expireAfter;
	public List<String> labels;
	public List<Integer> resourceType;
//	@javax.xml.bind.annotation.XmlElement( required  = false)
	public Integer sizeAbove;
//	@javax.xml.bind.annotation.XmlElement( required  = false)
	public Integer sizeBelow;
	public List<String> contentType;
	public List<AttributeDTO> attribute;
//	@javax.xml.bind.annotation.XmlElement( required  = false)
	public FilterUsage filterUsage;
//	@javax.xml.bind.annotation.XmlElement( required  = false)
	public Integer limit;
	public List<String> semanticsFilter;
//	@javax.xml.bind.annotation.XmlElement( required  = false)
	public FilterOperation filterOperation;
//	@javax.xml.bind.annotation.XmlElement( required  = false)
	public Integer contentFilterSyntax;
	public String contentFilterQuery;
//	@javax.xml.bind.annotation.XmlElement( required  = false)
	public Integer level;
//	@javax.xml.bind.annotation.XmlElement( required  = false)
	public Integer offset;

	// added in R3
	public List<String> childLabels;
	public List<String> parentLabels;
	public String labelsQuery;
	public List<Integer> childResourceType;
	public List<Integer> parentResourceType;
	public List<AttributeDTO> childAttribute;
	public List<AttributeDTO> parentAttribute;
	public String applyRelativePath;

	public static enum FilterOperation {
		AND(1), OR(2);

		private int value;

		private FilterOperation(int i) {
			value = i;
		}

		public int getValue() {
			return value;
		}
	}

	public static enum FilterUsage {
		DiscoveryCriteria(1), ConditionalRetrival(2), IPEOndemandDiscovery(3);

		private int value;

		private FilterUsage(int i) {
			value = i;
		}

		public int getValue() {
			return value;
		}

	}
}
