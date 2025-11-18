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

package org.osgi.service.resolver;

import org.osgi.annotation.versioning.ProviderType;

/**
 * A factory for creating {@link Resolver} instances.
 * 
 * <p>
 * A resolver implementation jar must contain the following resource:
 * 
 * <pre>
 * /META-INF/services/org.osgi.service.resolver.ResolverFactory
 * </pre>
 * 
 * This UTF-8 encoded resource must contain the name of the resolver
 * implementation's ResolverFactory implementation class. Space and tab
 * characters, including blank lines, in the resource must be ignored. The
 * number sign ({@code '#'} &#92;u0023) and all characters following it on each
 * line are a comment and must be ignored.
 * 
 * <p>
 * Applications can find the name of the ResolverFactory implementation class in
 * the resource and then load and construct a ResolverFactory object for the
 * resolver implementation. The ResolverFactory implementation class must have a
 * public, no-argument constructor. Java&#8482; SE 6 introduced the
 * {@code ServiceLoader} class which can create a ResolverFactory instance from
 * the resource.
 * 
 * <p>
 * This factory mechanism allows applications to obtain resolver instances
 * without running within an OSGi framework. This is particularly useful for:
 * <ul>
 * <li>Build tools that need to resolve dependencies before an OSGi framework
 * starts</li>
 * <li>IDEs that want to analyze bundle dependencies</li>
 * <li>Provisioning tools that operate on repositories without running in
 * OSGi</li>
 * <li>The OSGi framework itself during its initialization</li>
 * </ul>
 * 
 * <p>
 * OSGi framework implementations typically provide their own resolver and
 * should include a ResolverFactory service provider configuration in their
 * implementation jar. The resolver obtained from a framework's ResolverFactory
 * will use the same resolution algorithm as the framework itself, ensuring
 * consistent behavior between tooling and runtime.
 * 
 * @ThreadSafe
 * @author $Id$
 * @since 1.2
 */
@ProviderType
public interface ResolverFactory {

	/**
	 * Create a new {@link Resolver} instance.
	 * 
	 * <p>
	 * The returned resolver is independent of any OSGi framework and can be
	 * used to resolve resource dependencies based on the
	 * Requirement-Capability model. The resolver will implement the OSGi
	 * resolution algorithm, including support for all standard OSGi
	 * namespaces and uses constraints.
	 * 
	 * @return A new {@link Resolver} instance. Must not return {@code null}.
	 * @throws SecurityException If the caller does not have
	 *         {@code AllPermission}, and the Java Runtime Environment supports
	 *         permissions.
	 */
	Resolver getResolver();
}
