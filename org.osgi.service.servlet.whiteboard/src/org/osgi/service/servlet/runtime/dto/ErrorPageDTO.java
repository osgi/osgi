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

package org.osgi.service.servlet.runtime.dto;

/**
 * Represents a {@code jakarta.servlet.Servlet} for handling errors and currently
 * being used by a servlet context.
 *
 * @NotThreadSafe
 * @author $Id$
 */
public class ErrorPageDTO extends BaseServletDTO {
	/**
	 * The exceptions the error page is used for. This array might be
	 * empty.
	 */
	public String[]	exceptions;

	/**
	 * The error codes the error page is used for. This array might be
	 * empty.
	 */
	public long[]	errorCodes;
}
