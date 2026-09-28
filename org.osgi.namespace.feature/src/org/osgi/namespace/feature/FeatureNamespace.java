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

package org.osgi.namespace.feature;

import org.osgi.resource.Namespace;

/**
 * Feature Capability and Requirement Namespace.
 * 
 * <p>
 * This class defines the names for the attributes and directives for this
 * namespace. All unspecified capability attributes are of type {@code String}
 * and are used as arbitrary matching attributes for the capability. The
 * values associated with the specified directive and attribute keys are of
 * type {@code String}, unless otherwise indicated.
 * 
 * <p>
 * A resource that represents a Feature (see the Feature Service
 * Specification) provides exactly one {@code osgi.feature} capability
 * describing itself, in addition to an {@code osgi.identity} capability
 * whose {@code type} attribute is {@link #CAPABILITY_TYPE_FEATURE
 * osgi.feature}. Other Features, or Bundles, can then depend on it by
 * declaring an {@code osgi.feature} requirement with a filter matching the
 * Feature's id and version range, the same way an {@code osgi.wiring.bundle}
 * requirement is used to depend on a bundle.
 * 
 * @Immutable
 * @author $Id$
 */
public final class FeatureNamespace extends Namespace {

	/**
	 * Namespace name for feature capabilities and requirements.
	 * 
	 * <p>
	 * Also, the capability attribute used to specify the id of the feature.
	 */
	public static final String	FEATURE_NAMESPACE				= "osgi.feature";

	/**
	 * The capability attribute contains the {@code Version} of the feature if
	 * one is specified or {@code 0.0.0} if not specified. The value of this
	 * attribute must be of type {@code Version}.
	 */
	public final static String	CAPABILITY_VERSION_ATTRIBUTE	= "version";

	/**
	 * The value of the {@code type} attribute of the {@code osgi.identity}
	 * Namespace identifying the resource as a Feature.
	 * 
	 * @see org.osgi.framework.namespace.IdentityNamespace#CAPABILITY_TYPE_ATTRIBUTE
	 */
	public final static String	CAPABILITY_TYPE_FEATURE			= "osgi.feature";

	private FeatureNamespace() {
		// empty
	}
}
