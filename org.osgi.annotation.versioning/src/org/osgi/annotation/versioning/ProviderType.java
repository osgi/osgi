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

package org.osgi.annotation.versioning;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A type implemented by, or extended by, the Provider Role.
 * <p>
 * A non-binary-compatible change to a provider type normally requires
 * incrementing the major version of the type's package. This change will
 * require all providers and all consumers to be updated to handle the change.
 * However, a non-binary-compatible change affecting a {@code protected} access
 * member only requires incrementing the minor version of the type's package.
 * This change will require all providers to be updated to handle the change,
 * but consumers will not require changes since they only use, and do not
 * extend, the provider type and thus could not access {@code protected} access
 * members of the provider type.
 * <p>
 * A binary-compatible change to a provider type normally requires incrementing
 * the minor version of the type's package. This change will require all
 * providers to be updated to handle the change, but consumers will not require
 * changes since they only use, and do not implement or extend, the provider
 * type.
 * <p>
 * A type can be marked {@link ConsumerType} or {@link ProviderType} but not
 * both. A type is assumed to be {@link ConsumerType} if it is not marked either
 * {@link ConsumerType} or {@link ProviderType}.
 * <p>
 * A package can be marked {@link ProviderType}. In this case, all types in the
 * package are considered to be a provider type regardless of whether they are
 * marked {@link ConsumerType} or {@link ProviderType}.
 * <p>
 * This annotation is not retained at runtime. It is for use by tools to
 * understand the semantic version of a package. When a bundle implements a
 * provider type from an imported package, then the bundle's import range for
 * that package must require the package's exact major and minor version.
 * 
 * @see <a href= "https://docs.osgi.org/whitepaper/semantic-versioning/" >
 *      Semantic Versioning</a>
 * @author $Id$
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({
		ElementType.TYPE, ElementType.PACKAGE
})
public @interface ProviderType {
	// marker annotation
}
