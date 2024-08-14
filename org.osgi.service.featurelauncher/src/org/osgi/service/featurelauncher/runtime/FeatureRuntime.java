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

import java.io.Reader;
import java.util.List;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.service.feature.Feature;
import org.osgi.service.feature.FeatureExtension;
import org.osgi.service.feature.ID;
import org.osgi.service.featurelauncher.LaunchException;
import org.osgi.service.featurelauncher.decorator.FeatureDecorator;
import org.osgi.service.featurelauncher.decorator.FeatureExtensionHandler;
import org.osgi.service.featurelauncher.repository.ArtifactRepository;
import org.osgi.service.featurelauncher.repository.ArtifactRepositoryFactory;

/**
 * The Feature runtime service allows features to be installed and removed
 * dynamically at runtime.
 * 
 * @ThreadSafe
 */
@ProviderType
public interface FeatureRuntime extends ArtifactRepositoryFactory {

	/**
	 * Get the default repositories for the {@link FeatureRuntime} service.
	 * These are the repositories which are used by default when installing or
	 * updating features.
	 * <p>
	 * This method can be used to select a subset of the default repositories
	 * when using an {@link OperationBuilder}, or to query for instances
	 * manually.
	 * 
	 * @return the default repositories
	 */
	Map<String,ArtifactRepository> getDefaultRepositories();

	/**
	 * Install a feature into the runtime
	 * 
	 * @param feature the feature to launch
	 * @return An {@link OperationBuilder} that can be used to set up the
	 *         installation of this feature
	 * @throws LaunchException if installation fails
	 */
	InstallOperationBuilder install(Feature feature);

	/**
	 * Install a feature into the runtime based on the supplied feature JSON
	 * 
	 * @param jsonReader a {@link Reader} for the input Feature JSON
	 * @return An installedFeature representing the results of installing this
	 *         feature
	 * @throws LaunchException if installation fails
	 */
	InstallOperationBuilder install(Reader jsonReader);

	/**
	 * Get the features that have been installed by the {@link FeatureRuntime}
	 * service
	 * 
	 * @return a list of installed features
	 */
	List<InstalledFeature> getInstalledFeatures();

	/**
	 * Remove an installed feature
	 * 
	 * @param featureId the feature id
	 */
	void remove(ID featureId);

	/**
	 * Update a feature in the runtime
	 * 
	 * @param featureId the id of the feature to update
	 * @param feature the feature to launch
	 * @return An installedFeature representing the results of updating this
	 *         feature
	 */
	UpdateOperationBuilder update(ID featureId, Feature feature);

	/**
	 * Update a feature in the runtime based on the supplied feature JSON
	 * 
	 * @param featureId the id of the feature to update
	 * @param jsonReader a {@link Reader} for the input Feature JSON
	 * @return An installedFeature representing the results of updating this
	 *         feature
	 */
	UpdateOperationBuilder update(ID featureId, Reader jsonReader);

	/**
	 * An {@link OperationBuilder} is used to configure the installation or
	 * update of a {@link Feature} by the {@link FeatureRuntime}. Instances are
	 * not thread safe and must not be shared.
	 * <p>
	 * Once the {@link #complete()} method is called the operation will be run
	 * by the feature runtime and the operation builder will be invalidated,
	 * with all methods throwing {@link IllegalStateException}.
	 * 
	 * @param <T>
	 */
	public interface OperationBuilder<T extends OperationBuilder<T>> {

		/**
		 * Add an {@link ArtifactRepository} for use by this
		 * {@link OperationBuilder} instance. If an {@link ArtifactRepository}
		 * is already set for the given name then it will be replaced. Passing a
		 * <code>null</code> {@link ArtifactRepository} will remove the
		 * repository from this operation.
		 * 
		 * @param name the name to use for this repository
		 * @param repository the repository
		 * @return <code>this</code>
		 * @throws IllegalStateException if the builder has been completed
		 */
		T addRepository(String name,
				ArtifactRepository repository);

		/**
		 * Include the default repositories when completing this operation. This
		 * value defaults to <code>true</code>. If any
		 * {@link ArtifactRepository} added using
		 * {@link #addRepository(String, ArtifactRepository)} has the same name
		 * as a default repository then the added repository will override the
		 * default repository.
		 * 
		 * @param include
		 * @return <code>this</code>
		 * @throws IllegalStateException if the builder has been completed
		 */
		T useDefaultRepositories(boolean include);

		/**
		 * Use The supplied {@link RuntimeBundleMerge} to resolve any bundle
		 * merge operations that are required to complete the operation
		 * 
		 * @param merge
		 * @return <code>this</code>
		 */
		T withBundleMerge(RuntimeBundleMerge merge);

		/**
		 * Use The supplied {@link RuntimeConfigurationMerge} to resolve any
		 * configuration merge operations that are required to complete the
		 * operation
		 * 
		 * @param merge
		 * @return <code>this</code>
		 */
		T withConfigurationMerge(RuntimeConfigurationMerge merge);

		/**
		 * Configure this {@link OperationBuilder} with the supplied variables.
		 * 
		 * @param variables the variable placeholder overrides for this launch
		 * @return <code>this</code>
		 * @throws IllegalStateException if the builder has been completed
		 */
		T withVariables(Map<String,Object> variables);

		/**
		 * Add a {@link FeatureDecorator} to this {@link OperationBuilder} that
		 * will be used to decorate the feature. If called multiple times then
		 * the supplied decorators will be called in the same order that they
		 * were added to this builder.
		 * 
		 * @param decorator the decorator to add
		 * @return <code>this</code>
		 * @throws NullPointerException if the decorator is <code>null</code>
		 * @throws IllegalStateException if the builder has been launched
		 */
		T withDecorator(FeatureDecorator decorator);

		/**
		 * Add a {@link FeatureExtensionHandler} to this
		 * {@link OperationBuilder} that will be used to process the named
		 * {@link FeatureExtension} if it is found in the {@link Feature}. If
		 * called multiple times for the same <code>extensionName</code> then
		 * later calls will replace the <code>extensionHandler</code> to be
		 * used.
		 * 
		 * @param extensionName the name of the extension to handle
		 * @param extensionHandler the extensionHandler to add
		 * @return <code>this</code>
		 * @throws NullPointerException if the extension name or decorator is
		 *             <code>null</code>
		 * @throws IllegalStateException if the builder has been launched
		 */
		T withExtensionHandler(String extensionName,
				FeatureExtensionHandler extensionHandler);

		/**
		 * Complete the operation by installing or updating the feature
		 * 
		 * @return An {@link InstalledFeature} representing the result of the
		 *         operation
		 * @throws FeatureRuntimeException if an error occurs
		 * @throws IllegalStateException if the builder has been completed
		 *             already
		 */
		public InstalledFeature complete() throws FeatureRuntimeException;
	}

	/**
	 * The {@link OperationBuilder} for a
	 * {@link FeatureRuntime#install(Feature)} operation. Instances are not
	 * thread safe and must not be shared.
	 */
	public interface InstallOperationBuilder
			extends OperationBuilder<InstallOperationBuilder> {
		/**
		 * An alias for the {@link #complete()} method
		 * 
		 * @return the installed feature
		 */
		InstalledFeature install();
	}

	/**
	 * The {@link OperationBuilder} for a
	 * {@link FeatureRuntime#install(Feature)} operation. Instances are not
	 * thread safe and must not be shared.
	 */
	public interface UpdateOperationBuilder
			extends OperationBuilder<UpdateOperationBuilder> {
		/**
		 * An alias for the {@link #complete()} method
		 * 
		 * @return the updated feature
		 */
		InstalledFeature update();
	}

}
