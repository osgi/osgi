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

import org.osgi.service.feature.FeatureArtifact;

/**
 * {@link FeatureArtifact} specific assertions. Although this class is not final to allow Soft
 * assertions proxy, if you wish to extend it, extend
 * {@link AbstractFeatureArtifactAssert} instead.
 */
public class FeatureArtifactAssert extends AbstractFeatureArtifactAssert<FeatureArtifactAssert, FeatureArtifact> {

	/**
	 * Creates a new <code>{@link FeatureArtifactAssert}</code> to make
	 * assertions on actual FeatureArtifact.
	 *
	 * @param actual the FeatureArtifact we want to make assertions on.
	 */
	public FeatureArtifactAssert(FeatureArtifact actual) {
		super(actual, FeatureArtifactAssert.class);
	}

	/**
	 * An entry point for FeatureArtifactAssert to follow AssertJ standard
	 * <code>assertThat()</code> statements.<br>
	 * With a static import, one can write directly:
	 * <code>assertThat(myFeatureArtifact)</code> and get specific assertion
	 * with code completion.
	 *
	 * @param actual the FeatureArtifact we want to make assertions on.
	 * @return a new <code>{@link FeatureArtifactAssert}</code>
	 */
	@org.assertj.core.util.CheckReturnValue
	public static FeatureArtifactAssert assertThat(FeatureArtifact actual) {
		return new FeatureArtifactAssert(actual);
	}
}
