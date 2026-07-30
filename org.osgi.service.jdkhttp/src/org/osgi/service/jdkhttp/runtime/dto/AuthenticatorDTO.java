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
 * Represents the state of an active
 * {@code com.sun.net.httpserver.Authenticator} registration.
 *
 * @NotThreadSafe
 * @author $Id$
 */
public class AuthenticatorDTO extends DTO {

	/**
	 * The service id of the authenticator service. This value is never
	 * negative.
	 */
	public long		serviceId;

	/**
	 * The context path patterns to which the authenticator is applied. This
	 * value is never {@code null} and never empty.
	 */
	public String[]	patterns;

	/**
	 * The authentication realm, or {@code null} if none was specified.
	 */
	public String	realm;
}
