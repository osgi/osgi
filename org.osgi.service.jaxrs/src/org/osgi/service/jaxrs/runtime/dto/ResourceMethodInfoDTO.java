/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.service.jaxrs.runtime.dto;

import javax.ws.rs.Consumes;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.NameBinding;
import javax.ws.rs.Produces;

import org.osgi.dto.DTO;

/**
 * Represents information about a JAX-RS resource method. All information is
 * determined by reading the relevant annotations, from the JAX-RS type and not
 * interpreted further. Dynamic information, or information provided in other
 * ways may not be represented in this DTO.
 *
 * @NotThreadSafe
 * @author $Id$
 */
public class ResourceMethodInfoDTO extends DTO {

	/**
	 * The HTTP verb being handled, for example GET, DELETE, PUT, POST, HEAD,
	 * OPTIONS, null if no {@link HttpMethod} is defined
	 */
	public String	method;

	/**
	 * The mime-type(s) consumed by this resource method, null if
	 * {@link Consumes} is not defined
	 */
	public String[]	consumingMimeType;

	/**
	 * The mime-type(s) produced by this resource method, null if
	 * {@link Produces} is not defined
	 */
	public String[]	producingMimeType;

	/**
	 * The {@link NameBinding} annotations that apply to this resource method,
	 * if any
	 */
	public String[]	nameBindings;

	/**
	 * The path of this resource method. Placeholder information present in the
	 * URI pattern will not be interpreted and simply returned as defined.
	 */
	public String	path;
}
