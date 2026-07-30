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

package org.osgi.service.jdkhttp.runtime.dto;

import org.osgi.dto.DTO;

/**
 * Represents how a request path would be processed by a JDK HttpServer
 * Whiteboard implementation.
 * <p>
 * Obtained from
 * {@link org.osgi.service.jdkhttp.runtime.JdkHttpServerRuntime#calculateRequestInfoDTO(String)}.
 *
 * @NotThreadSafe
 * @author $Id$
 */
public class RequestInfoDTO extends DTO {

	/**
	 * The path of the request relative to the server root. This value is
	 * never {@code null}.
	 */
	public String				path;

	/**
	 * The handler processing this request, or {@code null} if no handler
	 * context matches the path, or a resource registration matches the path
	 * with a longer prefix.
	 */
	public HandlerDTO			handlerDTO;

	/**
	 * The resource registration processing this request, or {@code null} if
	 * no resource pattern matches the path, or a handler context matches the
	 * path with a longer prefix.
	 */
	public ResourceDTO			resourceDTO;

	/**
	 * The filters applied to the matched handler or resource context. This
	 * value is never {@code null} but may be empty.
	 */
	public FilterDTO[]			filterDTOs;

	/**
	 * The authenticator guarding the matched handler or resource context, or
	 * {@code null} if none applies.
	 */
	public AuthenticatorDTO		authenticatorDTO;
}
