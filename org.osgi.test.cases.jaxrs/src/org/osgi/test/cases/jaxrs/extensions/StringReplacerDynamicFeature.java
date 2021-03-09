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

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.WriterInterceptor;

import org.osgi.test.cases.jaxrs.resources.WhiteboardResource;

public class StringReplacerDynamicFeature implements DynamicFeature {

	private final StringReplacer replacer;

	public StringReplacerDynamicFeature(String from, String to) {
		replacer = new StringReplacer(from, to);
	}

	@Override
	public void configure(ResourceInfo info, FeatureContext fc) {
		if (info.getResourceClass() == WhiteboardResource.class) {
			fc.register(replacer, WriterInterceptor.class);
		}
	}

}
