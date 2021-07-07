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

import org.assertj.core.api.AbstractObjectAssert;
import org.osgi.service.feature.ID;

/**
 * Abstract base class for {@link ID} specific assertions.
 */
public abstract class AbstractFeatureIDAssert<S extends AbstractFeatureIDAssert<S, A>, A extends ID>
	extends AbstractObjectAssert<S, A> {

	/**
	 * Creates a new <code>{@link AbstractFeatureIDAssert}</code> to make assertions on
	 * actual ID.
	 *
	 * @param actual the ID we want to make assertions on.
	 */
	protected AbstractFeatureIDAssert(A actual, Class<S> selfType) {
		super(actual, selfType);
	}

	/**
	 * Verifies that the actual ID's artifactId is equal to the given one.
	 *
	 * @param artifactId the given artifactId to compare the actual ID's
	 *            artifactId to.
	 * @return this assertion object.
	 * @throws AssertionError - if the actual ID's artifactId is not equal to
	 *             the given one.
	 */
	public S hasArtifactId(String artifactId) {
		return isNotNull().has(Conditions.IDConditions.artifactId(artifactId));
	}

	/**
	 * Verifies that the actual ID's classifier is equal to the given one.
	 *
	 * @param classifier the given classifier to compare the actual ID's
	 *            classifier to.
	 * @return this assertion object.
	 * @throws AssertionError - if the actual ID's classifier is not equal to
	 *             the given one.
	 */
	public S hasClassifier(String classifier) {

		return isNotNull().has(Conditions.IDConditions.classifier(classifier));

	}

	/**
	 * Verifies that the actual ID's classifier is empty.
	 *
	 * @return this assertion object.
	 */
	public S hasEmptyClassifier()
	{
		return isNotNull()
				.has(Conditions.IDConditions.classifierEmpty());
	}

	/**
	 * Verifies that the actual ID's groupId is equal to the given one.
	 *
	 * @param groupId the given groupId to compare the actual ID's groupId to.
	 * @return this assertion object.
	 * @throws AssertionError - if the actual ID's groupId is not equal to the
	 *             given one.
	 */
	public S hasGroupId(String groupId) {
		return isNotNull().has(Conditions.IDConditions.groupId(groupId));
	}

	/**
	 * Verifies that the actual ID's type is equal to the given one.
	 *
	 * @param type the given type to compare the actual ID's type to.
	 * @return this assertion object.
	 * @throws AssertionError - if the actual ID's type is not equal to the
	 *             given one.
	 */
	public S hasType(String type) {
		return isNotNull().has(Conditions.IDConditions.type(type));
	}

	/**
	 * Verifies that the actual ID's type is empty.
	 *
	 * @return this assertion object.
	 * @throws AssertionError - if the actual ID's type is not empty
	 */
	public S hasEmptyType() {
		return isNotNull().has(Conditions.IDConditions.typeEmpty());
	}

	/**
	 * Verifies that the actual ID's version is equal to the given one.
	 *
	 * @param version the given version to compare the actual ID's version to.
	 * @return this assertion object.
	 * @throws AssertionError - if the actual ID's version is not equal to the
	 *             given one.
	 */
	public S hasVersion(String version) {
		return isNotNull().has(Conditions.IDConditions.version(version));
	}

}
