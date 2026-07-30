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
 * Represents the full runtime state of a JDK HttpServer Whiteboard
 * implementation.
 * <p>
 * Obtained from
 * {@link org.osgi.service.jdkhttp.runtime.JdkHttpServerRuntime#getRuntimeDTO()}.
 *
 * @NotThreadSafe
 * @author $Id$
 */
public class JdkHttpServerRuntimeDTO extends DTO {

	/**
	 * The {@code service.id} of the
	 * {@code org.osgi.service.jdkhttp.runtime.JdkHttpServerRuntime} service.
	 */
	public long						serviceId;

	/**
	 * The endpoints where the HTTP server is listening, e.g.
	 * {@code "http://localhost:8080"}. This value is never {@code null} but
	 * may be empty.
	 */
	public String[]					endpoints;

	/**
	 * The currently active handler registrations. This value is never
	 * {@code null} but may be empty.
	 */
	public HandlerDTO[]				handlers;

	/**
	 * The currently active filter registrations. This value is never
	 * {@code null} but may be empty.
	 */
	public FilterDTO[]				filters;

	/**
	 * The currently active authenticator registrations. This value is never
	 * {@code null} but may be empty.
	 */
	public AuthenticatorDTO[]			authenticators;

	/**
	 * The currently active resource registrations. This value is never
	 * {@code null} but may be empty.
	 */
	public ResourceDTO[]				resources;

	/**
	 * The handlers that failed to register. This value is never
	 * {@code null} but may be empty.
	 */
	public FailedHandlerDTO[]			failedHandlers;

	/**
	 * The filters that failed to register. This value is never {@code null}
	 * but may be empty.
	 */
	public FailedFilterDTO[]			failedFilters;

	/**
	 * The authenticators that failed to register. This value is never
	 * {@code null} but may be empty.
	 */
	public FailedAuthenticatorDTO[]	failedAuthenticators;

	/**
	 * The resources that failed to register. This value is never
	 * {@code null} but may be empty.
	 */
	public FailedResourceDTO[]			failedResources;
}
