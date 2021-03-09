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

package org.osgi.impl.service.dal;

import java.util.Map;

import org.osgi.service.dal.OperationMetadata;
import org.osgi.service.dal.PropertyMetadata;

/**
 * Basic implementation of the operation metadata.
 */
public final class OperationMetadataImpl implements OperationMetadata {

	private final Map<String, ? >		metadata;
	private final PropertyMetadata		returnValueMetadata;
	private final PropertyMetadata[]	parametersMetadata;

	/**
	 * Constructs the operation metadata.
	 * 
	 * @param metadata Additional metadata.
	 * @param returnValueMetadata The return value metadata.
	 * @param parametersMetadata The parameters metadata.
	 */
	public OperationMetadataImpl(Map<String, ? > metadata,
			PropertyMetadata returnValueMetadata,
			PropertyMetadata[] parametersMetadata) {
		this.metadata = metadata;
		this.returnValueMetadata = returnValueMetadata;
		this.parametersMetadata = parametersMetadata;
	}

	@Override
	public Map<String, ? > getMetadata() {
		return this.metadata;
	}

	@Override
	public PropertyMetadata getReturnValueMetadata() {
		return this.returnValueMetadata;
	}

	@Override
	public PropertyMetadata[] getParametersMetadata() {
		return this.parametersMetadata;
	}
}
