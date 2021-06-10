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

package org.osgi.test.cases.component.tb30;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Optional;

import org.osgi.test.cases.component.service.BaseService;

public class NoTargetReferenceImpl implements BaseService {

	private final Object			serviceParam;
	private volatile List<Object>	serviceField;

	public NoTargetReferenceImpl(Object serviceParam) {
		this.serviceParam = serviceParam;
	}

	public Dictionary<String,Object> getProperties() {
		final Dictionary<String,Object> props = new Hashtable<>();
		Optional.ofNullable(serviceParam)
				.ifPresent(local -> props.put("serviceParam", local));
		Optional.ofNullable(serviceField)
				.ifPresent(local -> props.put("serviceField", local));

		return props;
	}

}
