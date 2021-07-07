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

import java.util.List;
import java.util.Map;

import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.assertj.core.api.InstanceOfAssertFactory;
import org.assertj.core.api.ListAssert;
import org.assertj.core.api.MapAssert;
import org.osgi.service.feature.Feature;
import org.osgi.service.feature.FeatureBundle;
import org.osgi.service.feature.FeatureConfiguration;
import org.osgi.service.feature.FeatureExtension;

/**
 * Abstract base class for {@link Feature} specific assertions.
 */
public abstract class AbstractFeatureAssert<S extends AbstractFeatureAssert<S, A>, A extends Feature>
		extends AbstractObjectAssert<S,A> {

	/**
	 * Creates a new <code>{@link AbstractFeatureAssert}</code> to make
	 * assertions on actual Feature.
	 *
	 * @param actual the Feature we want to make assertions on.
	 */
	protected AbstractFeatureAssert(A actual, Class<S> selfType) {
		super(actual, selfType);
	}

	@SuppressWarnings("rawtypes")
	static InstanceOfAssertFactory<List, ListAssert<FeatureBundle>>					BUNDLE_LIST		= InstanceOfAssertFactories
		.list(FeatureBundle.class);

	@SuppressWarnings("rawtypes")
	static InstanceOfAssertFactory<Map, MapAssert<String, FeatureConfiguration>>	CONFIG_MAP		= InstanceOfAssertFactories
		.map(String.class, FeatureConfiguration.class);

	@SuppressWarnings("rawtypes")
	static InstanceOfAssertFactory<Map, MapAssert<String, FeatureExtension>>		EXTENSION_MAP	= InstanceOfAssertFactories
		.map(String.class, FeatureExtension.class);

	@SuppressWarnings("rawtypes")
	static InstanceOfAssertFactory<Map, MapAssert<String, String>>					VARIABLE_MAP	= InstanceOfAssertFactories
		.map(String.class, String.class);

	public ListAssert<FeatureBundle> hasBundlesThat() {
		return isNotNull().extracting(f -> f.getBundles(), BUNDLE_LIST)
			.as(actual + ".bundles");
	}

	public MapAssert<String, FeatureConfiguration> hasConfigurationsThat() {
		return isNotNull().extracting(f -> f.getConfigurations(), CONFIG_MAP)
			.as(actual + ".configurations");
	}

	public MapAssert<String, FeatureExtension> hasExtensionsThat() {
		return isNotNull().extracting(f -> f.getExtensions(), EXTENSION_MAP)
			.as(actual + ".extensions");
	}

	public MapAssert<String, String> hasVariablesThat() {
		return isNotNull().extracting(f -> f.getVariables(), VARIABLE_MAP)
			.as(actual + ".variables");
	}

	/**
	 * Verifies that the actual Feature is complete.
	 *
	 * @return this assertion object.
	 * @throws AssertionError - if the actual Feature is not complete.
	 */
	public S isComplete() {
		return isNotNull().is(Conditions.FeatureConditions.complete());
	}

	/**
	 * Verifies that the actual Feature is not complete.
	 *
	 * @return this assertion object.
	 * @throws AssertionError - if the actual Feature is complete.
	 */
	public S isNotComplete() {
		return isNotNull().isNot(Conditions.FeatureConditions.complete());
	}

	/**
	 * Verifies that the actual Feature's description is equal to the given one.
	 *
	 * @param description the given description to compare the actual Feature's
	 *            description to.
	 * @return this assertion object.
	 * @throws AssertionError - if the actual Feature's description is not equal
	 *             to the given one.
	 */
	public S hasDescription(String description) {
		return isNotNull().has(Conditions.FeatureConditions.description(description));
	}

	public S hasDescriptionMatching(String pattern) {
		return isNotNull().has(Conditions.FeatureConditions.descriptionMatches(pattern));
	}


	/**
	 * Verifies that the actual Feature's license is equal to the given one.
	 *
	 * @param license the given license to compare the actual Feature's license
	 *            to.
	 * @return this assertion object.
	 * @throws AssertionError - if the actual Feature's license is not equal to
	 *             the given one.
	 */
	public S hasLicense(String license) {
		return isNotNull().has(Conditions.FeatureConditions.license(license));
	}

	public S hasLicenseMatching(String pattern) {
		return isNotNull().has(Conditions.FeatureConditions.licenseMatches(pattern));
	}


	/**
	 * Verifies that the actual Feature's name is equal to the given one.
	 *
	 * @param name the given name to compare the actual Feature's name to.
	 * @return this assertion object.
	 * @throws AssertionError - if the actual Feature's name is not equal to the
	 *             given one.
	 */
	public S hasName(String name) {
		return isNotNull().has(Conditions.FeatureConditions.name(name));
	}

	public S hasNameMatching(String pattern) {
		return isNotNull().has(Conditions.FeatureConditions.nameMatches(pattern));
	}

	/**
	 * Verifies that the actual Feature's vendor is equal to the given one.
	 *
	 * @param vendor the given vendor to compare the actual Feature's vendor to.
	 * @return this assertion object.
	 * @throws AssertionError - if the actual Feature's vendor is not equal to
	 *             the given one.
	 */
	public S hasVendor(String vendor) {
		return isNotNull().has(Conditions.FeatureConditions.vendor(vendor));
	}

	public S hasVendorMatching(String pattern) {
		return isNotNull().has(Conditions.FeatureConditions.vendorMatches(pattern));
	}

	public FeatureIDAssert hasIDThat() {
		isNotNull();
		return FeatureIDAssert.assertThat(actual.getID()).as(actual + ".id");
	}

	// TODO: categories, copyright, docURL and SCM.

}
