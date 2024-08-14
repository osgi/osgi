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
package org.osgi.service.featurelauncher;

import java.io.Reader;
import java.util.Map;
import java.util.ServiceLoader;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.launch.Framework;
import org.osgi.service.feature.Feature;
import org.osgi.service.feature.FeatureExtension;
import org.osgi.service.featurelauncher.decorator.FeatureDecorator;
import org.osgi.service.featurelauncher.decorator.FeatureExtensionHandler;
import org.osgi.service.featurelauncher.repository.ArtifactRepository;
import org.osgi.service.featurelauncher.repository.ArtifactRepositoryFactory;

/**
 * The Feature launcher is the primary entry point for launching an OSGi
 * framework and set of bundles. As it is a means for launching a framework it
 * is designed to be used from outside OSGi and therefore should be obtained
 * using the {@link ServiceLoader}.
 */
@ProviderType
public interface FeatureLauncher extends ArtifactRepositoryFactory {

    /**
	 * Begin launching a framework instance based on the supplied feature
	 * 
	 * @param feature the feature to launch
	 * @return A running framework instance.
	 * @throws LaunchException
	 */
	LaunchBuilder launch(Feature feature);

	/**
	 * Begin launching a framework instance based on the supplied feature JSON
	 * 
	 * @param jsonReader a {@link Reader} for the input Feature JSON
	 * @return A running framework instance.
	 * @throws LaunchException
	 */
	LaunchBuilder launch(Reader jsonReader);

	/**
	 * A builder for configuring and triggering the launch of an OSGi framework
	 * containing the supplied feature
	 * <p>
	 * {@link LaunchBuilder} instances are single use. Once they have been used
	 * to launch a framework instance they become invalid and all methods will
	 * throw {@link IllegalStateException}
	 */
	public interface LaunchBuilder {

		/**
		 * Add a repository to this {@link LaunchBuilder} that will be used to
		 * locate installable artifact data.
		 * 
		 * @param repository the repository to add
		 * @return <code>this</code>
		 * @throws NullPointerException if the repository is null
		 * @throws IllegalStateException if the builder has been launched
		 */
		LaunchBuilder withRepository(ArtifactRepository repository);

		/**
		 * Configure this {@link LaunchBuilder} with the supplied properties.
		 * 
		 * @param configuration the configuration for this implementation
		 * @return <code>this</code>
		 * @throws IllegalStateException if the builder has been launched
		 */
		LaunchBuilder withConfiguration(Map<String,Object> configuration);

		/**
		 * Configure this {@link LaunchBuilder} with the supplied variables.
		 * 
		 * @param variables the variable placeholder overrides for this launch
		 * @return <code>this</code>
		 * @throws IllegalStateException if the builder has been launched
		 */
		LaunchBuilder withVariables(Map<String,Object> variables);

		/**
		 * Configure this {@link LaunchBuilder} with the supplied Framework
		 * Launch Properties.
		 * 
		 * @param frameworkProps the launch properties to use when starting the
		 *            framework
		 * @return <code>this</code>
		 * @throws IllegalStateException if the builder has been launched
		 */
		LaunchBuilder withFrameworkProperties(
				Map<String,Object> frameworkProps);

		/**
		 * Add a {@link FeatureDecorator} to this {@link LaunchBuilder} that
		 * will be used to decorate the feature being launched. If called
		 * multiple times then the supplied decorators will be called in the
		 * same order that they were added to this builder.
		 * 
		 * @param decorator the decorator to add
		 * @return <code>this</code>
		 * @throws NullPointerException if the decorator is <code>null</code>
		 * @throws IllegalStateException if the builder has been launched
		 */
		LaunchBuilder withDecorator(FeatureDecorator decorator);

		/**
		 * Add a {@link FeatureExtensionHandler} to this {@link LaunchBuilder}
		 * that will be used to process the named {@link FeatureExtension} if it
		 * is found in the {@link Feature} being launched. If called multiple
		 * times for the same <code>extensionName</code> then later calls will
		 * replace the <code>extensionHandler</code> to be used.
		 * 
		 * @param extensionName the name of the extension to handle
		 * @param extensionHandler the extensionHandler to add
		 * @return <code>this</code>
		 * @throws NullPointerException if the extension name or decorator is
		 *             <code>null</code>
		 * @throws IllegalStateException if the builder has been launched
		 */
		LaunchBuilder withExtensionHandler(String extensionName,
				FeatureExtensionHandler extensionHandler);

		/**
		 * Launch a framework instance based on the configured builder
		 * 
		 * @return A running framework instance.
		 * @throws LaunchException
		 * @throws IllegalStateException if the builder has been launched
		 */
		Framework launchFramework();
	}

}
