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

package org.osgi.test.cases.feature.assertj;

import java.util.Map;

import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.assertj.core.api.InstanceOfAssertFactory;
import org.assertj.core.api.MapAssert;
import org.osgi.service.feature.FeatureBundle;

/**
 * Abstract base class for {@link FeatureBundle} specific assertions - Generated
 * by CustomAssertionGenerator.
 */
public abstract class AbstractFeatureBundleAssert<S extends AbstractFeatureBundleAssert<S, A>, A extends FeatureBundle>
		extends AbstractObjectAssert<S,A> {

	/**
	 * Creates a new <code>{@link AbstractFeatureBundleAssert}</code> to make
	 * assertions on actual FeatureBundle.
	 *
	 * @param actual the FeatureBundle we want to make assertions on.
	 */
	protected AbstractFeatureBundleAssert(A actual, Class<S> selfType) {
		super(actual, selfType);
	}

	@SuppressWarnings("rawtypes")
	static InstanceOfAssertFactory<Map, MapAssert<String, Object>> METADATA_MAP = InstanceOfAssertFactories
		.map(String.class, Object.class);

	public MapAssert<String, Object> hasMetadataThat() {
		return isNotNull().extracting(f -> f.getMetadata(), METADATA_MAP)
			.as(actual + ".metadata");
	}

}
