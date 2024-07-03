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

import java.util.List;
import java.util.Map;

import org.osgi.annotation.versioning.ConsumerType;
import org.osgi.service.feature.Feature;
import org.osgi.service.feature.FeatureBundle;
import org.osgi.service.feature.ID;

/**
 * Merge operations occur when two or more features reference the same (or
 * similar) items to be installed.
 * <p>
 * The purpose of a {@link RuntimeBundleMerge} is to resolve possible conflicts
 * between {@link FeatureBundle} entries and determine which bundle(s) should be
 * installed as a result.
 * <p>
 * Merge operations happen in one of three scenarios, indicated by the
 * {@link MergeOperationType}:
 * <ul>
 * <li>INSTALL - a feature is being installed</li>
 * <li>UPDATE - a feature is being updated</li>
 * <li>REMOVE - a feature is being removed</li>
 * </ul>
 * <p>
 * When any merge operation occurs the merge function will be provided with the
 * {@link Feature} being operated upon, the {@link FeatureBundle} which needs to
 * be merged, a {@link List} of the {@link InstalledBundle}s representing the
 * currently installed bundles applicable to the merge, and a map of
 * {@link FeatureBundle} keys to {@link Feature} values representing the
 * installed features participating in the merge. All Installed Bundle and
 * Feature Bundle objects will have the same group id and artifact id.
 * <p>
 * If an <code>UPDATE</code> or <code>REMOVE</code> operation is underway then
 * the Feature being updated or removed will already have been removed from any
 * Installed Bundles and from the map of Feature Bundles to Features. For an
 * <code>UPDATE</code> this may result in one or more Installed Bundles having
 * an empty list of owning features, and the map of existing installed Feature
 * Bundles being empty.
 * <p>
 * The returned result from the merge function must be a full mapping of
 * installed Bundle {@link ID}s to Lists of owning Feature ids. The values of
 * the map must contain all of the Feature ids from the map of Feature Bundles,
 * and in the case of an <code>INSTALL</code> or <code>UPDATE</code> operation
 * also the Feature being operated upon. The keys of the returned map must only
 * contain IDs from the list of Installed Bundles, and in the case of an
 * <code>INSTALL</code> or <code>UPDATE</code> operation the Feature Bundle
 * being merged</code>.
 * <p>
 * It is an error for any key in the returned map to map to <code>null</code> or
 * an empty list. In the case of a <code>REMOVE</code> operation it is an error
 * to include the Feature id being operated upon in the returned map.
 */
@ConsumerType
public interface RuntimeBundleMerge {

	/**
	 * Calculate the bundles that should be installed at the end of a given
	 * operation.
	 * <p>
	 * Bundle Merge operations occur when two or more features reference a
	 * bundle with the same group id and artifact id, and the purpose of this
	 * method is to identify which bundles should be/remain installed, and which
	 * features they should be owned by.
	 * <p>
	 * The returned result from the merge function must be a full mapping of
	 * installed Bundle {@link ID}s to Lists of owning Features. It is an error
	 * to return a Map containing a key which is not the {@link ID} of a key in
	 * in the <code>installedBundle</code> list or, in the case of an
	 * <code>INSTALL</code> or <code>UPDATE</code> operation, the {@link ID} of
	 * the <code>toMerge</code> Feature Bundle.
	 * <p>
	 * The values of the map must contain all of the Features from the map of
	 * Feature Bundles, and in the case of an <code>INSTALL</code> or
	 * <code>UPDATE</code> operation also the Feature being operated upon. In
	 * the case of a <code>REMOVE</code> operation it is an error to include the
	 * Feature being operated upon in the returned map
	 * <p>
	 * It is an error for any value in the returned map to be <code>null</code>
	 * or an empty list.
	 * 
	 * @param operation - the type of the operation triggering the merge.
	 * @param feature The feature being operated upon
	 * @param toMerge The {@link FeatureBundle} in <code>feature</code> that
	 *            requires merging
	 * @param installedBundles A read list of bundles that have been installed
	 *            as part of previous installations. This list will always
	 *            contain at least one entry.
	 * @param existingFeatureBundles A read only map of existing Feature Bundles
	 *            which are part of this merge operation. The keys in the map
	 *            are the Feature Bundles involved in the merge and the values
	 *            are the Features which contain the Feature Bundle.
	 *            <p>
	 *            This Map may be empty in the case of an <code>UPDATE</code>
	 *            operation. Note that multiple Feature Bundle keys may refer to
	 *            the same bundle ID, or aliases of a single
	 *            {@link InstalledBundle}.
	 * @return A map of Bundle {@link ID} to List of owning Feature ids that
	 *         should be installed as a result of this operation. Note that
	 *         every Feature id <em>must</em> appear in the map values and that
	 *         the map keys may only contain IDs from <code>toMerge</code> or
	 *         one of the keys from the <code>installedBundles</code> map.
	 */
	public Map<ID,List<ID>> mergeBundle(MergeOperationType operation,
			Feature feature,
			FeatureBundle toMerge,
			List<InstalledBundle> installedBundles,
			Map<FeatureBundle,Feature> existingFeatureBundles);
}
