/*
 * Copyright (c) OSGi Alliance (2017). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
