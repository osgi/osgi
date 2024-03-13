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

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.launch.Framework;
import org.osgi.service.feature.Feature;

/**
 * The Feature launcher is the primary entry point for launching an OSGi
 * framework and set of bundles. As it is a means for launching a framework it
 * is designed to be used from outside OSGi and therefore exposes\
 * 
 * @ThreadSafe
 */
@ProviderType
public interface FeatureLauncher {
    /**
	 * Launch a framework instance based on the supplied feature
	 * 
	 * @param feature the feature to launch
	 * @return A running framework instance.
	 */
	Framework launch(Feature feature);

	/**
	 * Launch a framework instance based on the supplied feature JSON
	 * 
	 * @param jsonReader a {@link Reader} for the input Feature JSON
	 * @return A running framework instance.
	 */
	Framework launch(Reader jsonReader);

	/**
	 * Launch a framework instance based on the supplied feature and variables
	 * 
	 * @param feature the feature to launch
	 * @param variables key/value pairs to set variables in the feature
	 * @return A running framework instance.
	 */
	Framework launch(Feature feature, Map<String,Object> variables);

	/**
	 * Launch a framework instance based on the supplied feature JSON and
	 * variables
	 * 
	 * @param jsonReader a {@link Reader} for the input Feature JSON
	 * @param variables key/value pairs to set variables in the feature
	 * @return A running framework instance.
	 */
	Framework launch(Reader jsonReader, Map<String,Object> variables);

	/**
	 * Launch a framework instance based on the supplied feature, variables and
	 * framework properties
	 * 
	 * @param feature the feature to launch
	 * @param variables key/value pairs to set variables in the feature
	 * @param frameworkProperties set or override framework properties when
	 *            launching the framework
	 * @return A running framework instance.
	 */
	Framework launch(Feature feature, Map<String,Object> variables,
			Map<String,String> frameworkProperties);

	/**
	 * Launch a framework instance based on the supplied feature JSON
	 * 
	 * @param jsonReader a {@link Reader} for the input Feature JSON
	 * @param variables key/value pairs to set variables in the feature
	 * @param frameworkProperties set or override framework properties when
	 *            launching the framework
	 * @return A running framework instance.
	 */
	Framework launch(Reader jsonReader, Map<String,Object> variables,
			Map<String,String> frameworkProperties);

}
