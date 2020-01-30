package org.osgi.service.onem2m.dto;
import java.util.List;
import java.util.Map;

import org.osgi.dto.DTO;

/**
 * DTO expressing Resource.
 *
 */
public class ResourceDTO extends DTO{

	// Universal Attribute, which can be held by all resources.
//	@javax.xml.bind.annotation.XmlElement( required  = true)
	/**
	 * Resource Type
	 */
	public Integer resourceType;
//	@javax.xml.bind.annotation.XmlElement( required  = true)
	
	/**
	 * Resource ID
	 */
	public String resourceID;
//	@javax.xml.bind.annotation.XmlElement( required  = true)
	
	/**
	 * Parent ID
	 * 
	 * Resource ID of parent resource.
	 */
	public String parentID;
//	@javax.xml.bind.annotation.XmlElement( required  = true)
	
	/**
	 * Creation time
	 */
	public String creationTime;
//	@javax.xml.bind.annotation.XmlElement( required  = true)
	
	/**
	 * last modified time
	 */
	public String lastModifiedTime;

	/**
	 * Resource name
	 */
	public String resourceName;

	// optional, Universal Attributes
	/**
	 * Labels
	 * 
	 * This field is optional.
	 */
	public List<String> labels;

	/**
	 * Non Universal Attribute.
	 * Value Part must be the types that are allowed for OSGi DTO.
	 */
	public Map<String, Object> attribute;

	/**
	 * Setter method for attribute.
	 * 
	 * @param key key of attribute
	 * @param value value of attribute
	 */
    public void setAttribute(String key, Object value) {
        this.attribute.put(key, value);
    }

    /**
	 * Getter method for attribute.
	 * 
	 * @return attributes
	 * 
	 * TODO: Funny.
	 */
    public Map<String, Object> getAttribute() {
        return this.attribute;
    }

}
