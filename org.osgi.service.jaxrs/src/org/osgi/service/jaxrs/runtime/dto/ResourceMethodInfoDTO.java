package org.osgi.service.jaxrs.runtime.dto;

import javax.ws.rs.NameBinding;

import org.osgi.dto.DTO;

/**
 * Represents information about a JAX-RS resource method.
 *
 * @NotThreadSafe
 * @author $Id$
 */
public class ResourceMethodInfoDTO extends DTO {

	/**
	 * The HTTP verb being handled, for example GET, DELETE, PUT, POST, HEAD,
	 * OPTIONS
	 */
	public String	method;

	/**
	 * The mime-type(s) consumed by this resource method, null if not defined
	 */
	public String[]	consumingMimeType;

	/**
	 * The mime-type(s) produced by this resource method, null if not defined
	 */
	public String[]	producingMimeType;

	/**
	 * The {@link NameBinding} annotations that apply to this resource method,
	 * if any
	 */
	public String[]	nameBindings;

	/**
	 * The path of this resource method
	 */
	public String	path;
}
