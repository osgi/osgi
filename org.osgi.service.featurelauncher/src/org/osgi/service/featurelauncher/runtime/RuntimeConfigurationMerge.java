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
import org.osgi.service.feature.FeatureConfiguration;

/**
 * Merge operations occur when two or more features reference the same (or
 * similar) items to be installed.
 * <p>
 * The purpose of a {@link RuntimeConfigurationMerge} is to resolve possible
 * conflicts between {@link FeatureConfiguration} entries and determine what
 * configuration should be created as a result.
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
 * {@link Feature} being operated upon, the {@link FeatureConfiguration} which
 * needs to be merged, the {@link InstalledConfiguration} representing the
 * current configuration, and a list of {@link FeatureConfigurationDefinition}s
 * representing the installed features participating in the merge. All Feature
 * Configurations will have the same PID.
 * <p>
 * If an <code>UPDATE</code> or <code>REMOVE</code> operation is underway then
 * the Feature being updated or removed will already have been removed from the
 * Installed Configuration and the list of existing Feature Configuration
 * Definitions. For an <code>UPDATE</code> this may result in the
 * {@link InstalledConfiguration#getOwningFeatures()} being an empty list, and
 * the map of existing installed Feature Configurations being empty.
 * <p>
 * The returned result from the merge function is a map of configuration
 * properties that should be used to complete the operation. This may be null if
 * the configuration should be deleted.
 */
@ConsumerType
public interface RuntimeConfigurationMerge {

	/**
	 * Calculate the configuration that should be used at the end of a given
	 * operation.
	 * <p>
	 * Configuration merge operations occur when two or more features define the
	 * same configuration, where configuration identity is determined by the PID
	 * of the configuration. The purpose of this function is to determine what
	 * configuration properties should be used after the merge has finished.
	 * 
	 * @param operation - the type of the operation triggering the merge.
	 * @param feature The feature being operated upon
	 * @param toMerge The {@link FeatureConfiguration} in <code>feature</code>
	 *            that requires merging
	 * @param configuration The existing configuration that has been installed
	 *            as part of previous installations. This will represent a
	 *            configuration with the same PID as <code>toMerge</code>.
	 *            <p>
	 *            Note that this value will be <code>null</code> if the
	 *            configuration does not exist to differentiate it from an empty
	 *            configuration dictionary
	 * @param existingFeatureConfigurations An immutable list of
	 *            {@link FeatureConfigurationDefinition}s which are part of this
	 *            merge operation. The entries are in the same order as the
	 *            Features were installed.
	 *            <p>
	 *            This list may be empty in the case of an <code>UPDATE</code>
	 *            operation. Note that all Feature Configuration Definitions
	 *            will refer to the same PID, and this will match the PID of
	 *            <code>toMerge</code>. An immutable map of existing Feature
	 *            Configurations which are part of this merge operation. The
	 *            keys in the map are the Feature Configurations involved in the
	 *            merge and the values are the Features which contain the
	 *            Feature Configuration.
	 * @return A map of configuration properties to use. Returning
	 *         <code>null</code> indicates that the configuration should be
	 *         deleted.
	 */
	public Map<String,Object> mergeConfiguration(
			MergeOperationType operation,
			Feature feature,
			FeatureConfiguration toMerge,
			InstalledConfiguration configuration,
			List<FeatureConfigurationDefinition> existingFeatureConfigurations);
	
	/**
	 * A {@link FeatureConfigurationDefinition} is used to show which
	 * {@link FeatureConfiguration}(s) are being merged, and the {@link Feature}
	 * that they relate to.
	 */
	public interface FeatureConfigurationDefinition {
		/**
		 * @return The {@link FeatureBundle} being merged
		 */
		public FeatureConfiguration getFeatureConfiguration();
		
		/**
		 * @return The {@link Feature} containing the
		 *         {@link FeatureConfiguration}
		 */
		public Feature getFeature();
	}
}
