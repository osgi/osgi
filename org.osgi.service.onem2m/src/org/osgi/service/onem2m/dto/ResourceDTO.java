package org.osgi.service.onem2m.dto;

import java.util.Map;

import org.osgi.dto.DTO;

/**
 * DTO expresses Resource.
 * <p>
 * Universal attributes are expressed in field of the class. Common attributes
 * and other attributes are stored in attribute field.
 * 
 * @see <a href=
 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0001-Functional_Architecture-V3_15_1.pdf">oneM2M
 *      TS-0001 9.6.1.3.1</a>
 *
 */
public class ResourceDTO extends DTO {

	// Universal Attribute, which can be held by all resources.
	/**
	 * Resource Type
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0001-Functional_Architecture-V3_15_1.pdf">oneM2M
	 *      TS-0001 9.6.1.3.1</a>
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oenM2M
	 *      TS-0004 6.3.4.2.1</a>
	 */
	public Integer resourceType;

	/**
	 * Resource ID
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0001-Functional_Architecture-V3_15_1.pdf">oneM2M
	 *      TS-0001 9.6.1.3.1</a>
	 */
	public String resourceID;

	/**
	 * Parent ID
	 * 
	 * Resource ID of parent resource.
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0001-Functional_Architecture-V3_15_1.pdf">oneM2M
	 *      TS-0001 9.6.1.3.1</a>
	 */
	public String parentID;

	/**
	 * Creation time
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0001-Functional_Architecture-V3_15_1.pdf">oneM2M
	 *      TS-0001 9.6.1.3.1</a>
	 */
	public String creationTime;

	/**
	 * last modified time
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0001-Functional_Architecture-V3_15_1.pdf">oneM2M
	 *      TS-0001 9.6.1.3.1</a>
	 */
	public String lastModifiedTime;

	/**
	 * Resource name
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0001-Functional_Architecture-V3_15_1.pdf">oneM2M
	 *      TS-0001 9.6.1.3.1</a>
	 */
	public String resourceName;

	/**
	 * Non Universal Attribute.
	 * 
	 * Value Part must be the types that are allowed for OSGi DTO. In case of value
	 * part can be expressed DTO in this package, the DTO must be used. In case of
	 * value part have sub-elements, GenericDTO must be used.
	 */
	public Map<String, Object> attribute;

}
