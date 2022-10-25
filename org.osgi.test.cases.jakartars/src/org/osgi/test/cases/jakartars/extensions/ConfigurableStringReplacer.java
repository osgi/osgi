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
package org.osgi.test.cases.jakartars.extensions;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.ext.Providers;

public class ConfigurableStringReplacer extends AbstractStringReplacer {

	@Context
	Providers	providers;

	@Context
	HttpHeaders	headers;

	public String getToReplace() {
		return getConfig().getToReplace();
	}

	private ExtensionConfig getConfig() {
		ExtensionConfig config = headers.getAcceptableMediaTypes()
				.stream()
				.map(m -> providers.getContextResolver(ExtensionConfig.class,
						m))
				.filter(cr -> cr != null)
				.map(cr -> cr.getContext(ExtensionConfig.class))
				.findFirst()
				.get();
		return config;
	}

	public String getReplaceWith() {
		return getConfig().getReplaceWith();
	}
}
