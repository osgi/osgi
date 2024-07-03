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
package org.osgi.service.featurelauncher.runtime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.osgi.annotation.versioning.ConsumerType;
import org.osgi.framework.Version;
import org.osgi.service.feature.Feature;
import org.osgi.service.feature.FeatureBundle;
import org.osgi.service.feature.FeatureConfiguration;
import org.osgi.service.feature.ID;

/**
 * Merge operations occur when two or more features reference the same (or
 * similar) items to be installed.
 * <p>
 * The purpose of a {@link RuntimeMerges} is to provide common merge strategies
 * in an easy to construct way.
 */
@ConsumerType
public final class RuntimeMerges {

	private static final String SNAPSHOT = "-SNAPSHOT";

	/**
	 * The {@link #preferExistingBundles()} merge strategy tries to reduce the
	 * number of new installations by applying semantic versioning rules. The
	 * new bundle is only installed if it has:
	 * <ul>
	 * <li>A different major version from all installed bundles</li>
	 * <li>A higher minor version than all other installed bundles with the same
	 * major version</li>
	 * </ul>
	 * 
	 * @return the prefer existing merge strategy
	 */
	public static RuntimeBundleMerge preferExistingBundles() {
		return (operation, feature, toMerge, installedBundles,
				existingFeatures) -> {
			// Removal is non-invasive
			if (operation == MergeOperationType.REMOVE)
				return unchangedWithoutEmpty(installedBundles);

			Version v;
			try {
				v = getOSGiVersion(toMerge.getID());
			} catch (IllegalArgumentException iae) {
				// Unable to process the version
				return unchangedAddNew(feature, toMerge, installedBundles);
			}
			
			if(installedBundles.stream().noneMatch(i -> v.getMajor() == i.bundle.getVersion().getMajor())) {
				// No existing bundles with the right major version
				return unchangedAddNew(feature, toMerge, installedBundles);
			}
			
			if(installedBundles.stream().noneMatch(i -> v.getMajor() == i.bundle.getVersion().getMajor() && v.getMinor() <= i.bundle.getVersion().getMinor())) {
				// No existing bundles with the same major version and equal or
				// higher minor version
				return unchangedAddNew(feature, toMerge, installedBundles);
			}
			
			// Find the highest version bundle with the right major version
			Optional<InstalledBundle> first = installedBundles.stream()
					.filter(i -> v.getMajor() == i.bundle.getVersion()
							.getMajor())
					.sorted((a, b) -> a.bundle.getVersion()
							.compareTo(b.bundle.getVersion()))
					.findFirst();

			if (first.isPresent()) {
				// Create an identical map but add feature as an owner of first
				// Note that we cannot use #unchangedWithoutEmpty() as that
				// might remove the bundle we want during an update
				Map<ID,List<ID>> unchanged = installedBundles.stream()
						.collect(Collectors.toMap(i -> i.bundleId,
								i -> new ArrayList<>(i.owningFeatures)));
				unchanged.get(first.get().bundleId).add(feature.getID());
				unchanged.values().removeIf(Collection::isEmpty);
				return unchanged;
			} else {
				// This should be impossible, fall back to installing toMerge
				return unchangedAddNew(feature, toMerge, installedBundles);
			}
		};
	}

	private static Map<ID,List<ID>> unchangedAddNew(Feature feature,
			FeatureBundle toMerge, List<InstalledBundle> installedBundles) {
		Map<ID,List<ID>> unchanged = unchangedWithoutEmpty(installedBundles);
		unchanged.put(toMerge.getID(), Arrays.asList(feature.getID()));
		return unchanged;
	}

	/**
	 * Attempts to turn the version String from an ID into an OSGi version
	 * <p>
	 * Note that this parsing is more lenient than
	 * {@link Version#parseVersion(String)}. It treats the first three segments
	 * separated by <code>.</code> characters as possible integers. If they are
	 * integers then they represent the major, minor and micro segments of an
	 * OSGi version. If any non-numeric segments are encountered, or the end of
	 * the string, then the remaining version segments are <code>0</code>. Any
	 * remaining content from the input version string is used as the qualifier.
	 * 
	 * @param id
	 * @return An OSGi version which attempts to replicate the version from the
	 *         ID
	 */
	public static Version getOSGiVersion(ID id) {
		String version = id.getVersion();
		if (version.endsWith(SNAPSHOT)) {
			version = version.substring(0,
					version.length() - SNAPSHOT.length());
		}

		int from = 0;
		int[] versions = {
				0, 0, 0
		};
		for (int i = 0; i < 3; i++) {
			int idx = version.indexOf('.', from);
			if (idx > from) {
				try {
					versions[i] = Integer
							.parseInt(version.substring(from, idx));
				} catch (NumberFormatException nfe) {
					// Not a number, skip to the qualifier
					break;
				}
				from = idx + 1;
			} else {
				// This is the final part of the version
				try {
					versions[i] = Integer.parseInt(version.substring(from));
				} catch (NumberFormatException nfe) {
					// Not a number, leave it for the qualifier
				}
				break;
			}
		}
		
		return new Version(versions[0], versions[1], versions[2],
				version.substring(from));
	}

	private static Map<ID,List<ID>> unchangedWithoutEmpty(
			List<InstalledBundle> installedBundles) {
		return installedBundles.stream()
				.filter(i -> !i.owningFeatures.isEmpty())
				.collect(Collectors.toMap(i -> i.bundleId,
						i -> new ArrayList<>(i.owningFeatures)));
	}

	/**
	 * The {@link #replaceExistingProperties()} merge strategy simply replaces
	 * any existing configuration values with the new values from the new
	 * {@link FeatureConfiguration}.
	 * <p>
	 * Removal is more complex and relies on the fact that the
	 * {@link InstalledConfiguration#owningFeatures} are in installation order.
	 * This means that we can descend the list looking for the previous
	 * configuration properties and apply them
	 * 
	 * @return the replace existing merge strategy
	 */
	public static RuntimeConfigurationMerge replaceExistingProperties() {
		return (operation, feature, toMerge, configuration,
				existingFeatureConfigurations) -> {

			if (operation == MergeOperationType.REMOVE) {
				// Find the latest Feature and use those properties
				ListIterator<ID> it = configuration.owningFeatures
						.listIterator(configuration.owningFeatures.size() - 1);
				while (it.hasPrevious()) {
					Optional<FeatureConfiguration> first = findFeatureConfigurationForFeatureWithId(
							existingFeatureConfigurations, it.previous());
					if (first.isPresent()) {
						return first.get().getValues();
					}
				}
				// Unable to find any FeatureConfiguration so it must be null
				return null;
			} else {
				// The new value just replaces what was there before
				return toMerge.getValues();
			}
		};
	}

	private static <T> Optional<T> findFeatureConfigurationForFeatureWithId(
			Map<T,Feature> existingFeatureConfigurations, ID id) {
		return existingFeatureConfigurations.entrySet()
				.stream()
				.filter(e -> e.getValue()
						.getID()
						.toString()
						.equals(id.toString()))
				.map(Entry::getKey)
				.findFirst();
	}

}
