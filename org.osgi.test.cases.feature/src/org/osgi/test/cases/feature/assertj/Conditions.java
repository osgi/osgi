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

import static org.assertj.core.api.Assertions.not;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.assertj.core.api.Condition;
import org.osgi.service.feature.Feature;
import org.osgi.service.feature.FeatureArtifact;
import org.osgi.service.feature.FeatureBundle;
import org.osgi.service.feature.FeatureConfiguration;
import org.osgi.service.feature.FeatureExtension;
import org.osgi.service.feature.ID;


public interface Conditions {

	interface FeatureArtifactConditions {

		static Condition<FeatureArtifact> iDNull() {
			return new Condition<>(f -> f.getID() == null, "ID <null>");
		}

	}

	interface FeatureBundleConditions {

		static Condition<FeatureBundle> metadataEmpty() {
			return new Condition<>(f -> f.getMetadata()
				.isEmpty(), "metadata empty");
		}

		static Condition<FeatureBundle> metadataNull() {
			return new Condition<>(f -> f.getMetadata() == null, "metadata <null>");
		}

	}

	interface FeatureConditions extends FeatureArtifactConditions {

		static Condition<Feature> complete() {
			return new Condition<Feature>(Feature::isComplete, "complete");
		}

		static Condition<Feature> description(String description) {
			return new Condition<Feature>(
					Predicates.description(description),
					"description <%s>",
				description);
		}

		static Condition<Feature> descriptionMatches(String pattern) {
			return new Condition<Feature>(
					Predicates.descriptionMatches(pattern),
					"description match <%s>", pattern);
		}

		static Condition<Feature> descriptionNull() {
			return new Condition<>(f -> !f.getDescription().isPresent(),
					"description <null>");
		}

		static Condition<Feature> license(String license) {
			return new Condition<Feature>(Predicates.license(license),
					"license <%s>", license);
		}

		static Condition<Feature> licenseMatches(String pattern) {
			return new Condition<Feature>(
					Predicates.licenseMatches(pattern),
					"license match <%s>", pattern);
		}

		static Condition<Feature> licenseNull() {
			return new Condition<>(f -> !f.getLicense().isPresent(),
					"license <null>");
		}

		static Condition<Feature> name(String name) {
			return new Condition<Feature>(Predicates.name(name),
					"name <%s>", name);
		}

		static Condition<Feature> nameMatches(String pattern) {
			return new Condition<Feature>(
					Predicates.nameMatches(pattern), "name match <%s>",
					pattern);
		}

		static Condition<Feature> nameNull() {
			return new Condition<>(f -> f.getName() == null, "name <null>");
		}

		static Condition<Feature> notComplete() {
			return not(complete()).describedAs("not complete");
		}

		static Condition<Feature> vendor(String vendor) {
			return new Condition<Feature>(Predicates.vendor(vendor),
					"vendor <%s>", vendor);
		}

		static Condition<Feature> vendorMatches(String pattern) {
			return new Condition<Feature>(
					Predicates.vendorMatches(pattern),
					"vendor match <%s>", pattern);
		}

		static Condition<Feature> vendorNull() {
			return new Condition<>(f -> !f.getVendor().isPresent(),
					"vendor <null>");
		}

		static Condition<Feature> categoriesEmpty() {
			return new Condition<>(
					f -> f.getCategories() != null
							&& f.getCategories().isEmpty(),
					"categories must be null");
		}

		static Condition<Feature> categoryContains(String category) {
			return new Condition<Feature>(
					f -> f.getCategories() != null
							&& f.getCategories().contains(category),
					"categories must be %s", category);
		}

		static Condition<Feature> categoryMatches(String pattern) {
			return new Condition<Feature>(
					f -> f.getCategories() != null && f.getCategories()
							.stream()
							.filter(c -> c.matches(pattern))
							.findAny()
							.isPresent(),
					"categories must match %s", pattern);
		}

		static Condition<Feature> copyrightNull() {
			return new Condition<>(f -> !f.getCopyright().isPresent(),
					"copyright must be null");
		}

		static Condition<Feature> copyright(String copyright) {
			return new Condition<Feature>(
					Predicates.copyright(copyright),
					"copyright must be %s", copyright);
		}

		static Condition<Feature> copyrightMatches(String pattern) {
			return new Condition<Feature>(
					Predicates.copyrightMatches(pattern),
					"copyright must match %s", pattern);
		}

		static Condition<Feature> docURLNull() {
			return new Condition<>(f -> f.getDocURL() == null,
					"docURL must be null");
		}

		static Condition<Feature> docURL(String docURL) {
			return new Condition<Feature>(
					Predicates.docURL(docURL),
					"docURL must be %s", docURL);
		}

		static Condition<Feature> docURLMatches(String pattern) {
			return new Condition<Feature>(
					Predicates.docURLMatches(pattern),
					"docURL must match %s", pattern);
		}

		static Condition<Feature> scmNull() {
			return new Condition<>(f -> !f.getSCM().isPresent(),
					"scm must be empty");
		}

		static Condition<Feature> scm(String scm) {
			return new Condition<Feature>(Predicates.scm(scm),
					"scm must be %s", scm);
		}

		static Condition<Feature> scmMatches(String pattern) {
			return new Condition<Feature>(Predicates.scmMatches(pattern),
					"scm must match %s", pattern);
		}
	}

	interface FeatureConfigurationConditions {

		static Condition<FeatureConfiguration> factoryConfiguration() {

			return new Condition<FeatureConfiguration>(
					f -> !f.getFactoryPid().isPresent(),
					"factoryConfiguration");
		}

		static Condition<FeatureConfiguration> factoryPid(String text) {
			return new Condition<FeatureConfiguration>(
					Predicates.ConfigurationsPredicates.factoryPid(text),
					"factoryPid <%s>", text);
		}

		static Condition<FeatureConfiguration> factoryPidMatches(
				String pattern) {
			return new Condition<FeatureConfiguration>(
					Predicates.ConfigurationsPredicates
							.factoryPidMatches(pattern),
					"factoryPid matches <%s>", pattern);
		}

		static Condition<FeatureConfiguration> pid(String text) {

			return new Condition<FeatureConfiguration>(f -> Objects.equals(f.getPid(), text), "pid <%s>", text);
		}
	}

	interface FeatureExtensionConditions {

		static Condition<FeatureExtension> json(String json) {
			return new Condition<FeatureExtension>(f -> Objects.equals(f.getJSON(), json), "json <%s>", json);
		}

		static Condition<FeatureExtension> jsonMatches(String pattern) {
			return new Condition<FeatureExtension>(f -> Objects.nonNull(f.getJSON()) && f.getJSON()
				.matches(pattern), "json match <%s>", pattern);
		}

		static Condition<FeatureExtension> jsonNull() {
			return new Condition<>(f -> f.getJSON() == null, "json <null>");
		}

		static Condition<FeatureExtension> kind(final FeatureExtension.Kind kind) {
			return new Condition<>(f -> Objects.equals(f.getKind(), kind), "kind <%s>", kind);
		}

		static Condition<FeatureExtension> kindMandantory() {
			return kind(FeatureExtension.Kind.MANDATORY);
		}

		static Condition<FeatureExtension> kindNull() {
			return kind(null);
		}

		static Condition<FeatureExtension> kindOptional() {
			return kind(FeatureExtension.Kind.OPTIONAL);
		}

		static Condition<FeatureExtension> kindTransient() {
			return kind(FeatureExtension.Kind.TRANSIENT);
		}

		static Condition<FeatureExtension> name(String name) {
			return new Condition<FeatureExtension>(f -> Objects.equals(f.getName(), name), "name <%s>", name);
		}

		static Condition<FeatureExtension> nameMatches(String pattern) {
			return new Condition<FeatureExtension>(f -> Objects.nonNull(f.getName()) && f.getName()
				.matches(pattern), "name match <%s>", pattern);
		}

		static Condition<FeatureExtension> nameNull() {
			return new Condition<>(f -> f.getName() == null, "name <null>");

		}

		static Condition<FeatureExtension> text(List<String> lines) {
			return new Condition<FeatureExtension>(
					f -> Objects.nonNull(f.getText())
							&& Objects.equals(f.getText(), lines),
					"text <%s>", lines);
		}

		static Condition<FeatureExtension> text(String text) {
			return new Condition<FeatureExtension>(
					f -> Objects.nonNull(f.getText()) && Objects.equals(
							f.getText()
									.stream()
									.collect(Collectors
											.joining(System.lineSeparator())),
							text),
					"text <%s>", text);
		}

		static Condition<FeatureExtension> textMatches(String pattern) {
			return new Condition<FeatureExtension>(f -> Objects.nonNull(f.getText()) && f.getText()
					.stream()
					.collect(Collectors.joining(System.lineSeparator()))
				.matches(pattern), "text match <%s>", pattern);
		}

		static Condition<FeatureExtension> textNull() {
			return new Condition<>(f -> f.getText() == null, "test <null>");
		}

		static Condition<FeatureExtension> type(final FeatureExtension.Type type) {
			return kind(null);
		}

		static Condition<FeatureExtension> typeArtifacts() {
			return type(FeatureExtension.Type.ARTIFACTS);
		}

		static Condition<FeatureExtension> typeJson() {
			return type(FeatureExtension.Type.JSON);
		}

		static Condition<FeatureExtension> typeNull() {
			return type(null);
		}

		static Condition<FeatureExtension> typeText() {
			return type(FeatureExtension.Type.TEXT);
		}

	}

	interface IDConditions {
		static Condition<ID> artifactId(String artifactId) {
			return new Condition<ID>(f -> Objects.equals(f.getArtifactId(), artifactId), "artifactId <%s>", artifactId);
		}

		static Condition<ID> classifier(String classifier) {
			return new Condition<ID>(
					Predicates.IDPredicates.classifier(classifier),
					"classifier <%s>", classifier);
		}

		static Condition<ID> classifierEmpty() {
			return new Condition<ID>(f -> !f.getClassifier().isPresent(),
					"classifier is empty");
		}

		static Condition<ID> groupId(String groupId) {
			return new Condition<ID>(f -> Objects.equals(f.getGroupId(), groupId), "groupId <%s>", groupId);
		}
		;
		static Condition<ID> type(String type) {
			return new Condition<ID>(Predicates.IDPredicates.type(type),
					"type <%s>", type);
		}

		static Condition<ID> typeEmpty() {
			return new Condition<ID>(f -> !f.getType().isPresent(),
					"type is empty");
		}

		static Condition<ID> version(String version) {
			return new Condition<ID>(f -> Objects.equals(f.getVersion(), version), "version <%s>", version);
		}
	}

}
