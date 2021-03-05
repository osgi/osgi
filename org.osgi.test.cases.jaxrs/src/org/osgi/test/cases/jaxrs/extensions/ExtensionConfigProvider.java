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
package org.osgi.test.cases.jaxrs.extensions;

import javax.ws.rs.ext.ContextResolver;

public class ExtensionConfigProvider
		implements ContextResolver<ExtensionConfig> {

	private final String	toReplace;

	private final String	replaceWith;

	public ExtensionConfigProvider(String toReplace, String replaceWith) {
		this.toReplace = toReplace;
		this.replaceWith = replaceWith;
	}

	@Override
	public ExtensionConfig getContext(Class< ? > arg0) {
		if (!ExtensionConfig.class.equals(arg0))
			return null;
		return new ExtensionConfig(toReplace, replaceWith);
	}

}
