/*
 * Copyright (c) OSGi Alliance (2013). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.service.repository;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The super interface for all requirement expressions. All requirement
 * expressions must extend this interface.
 * 
 * @ThreadSafe
 * @since 1.1
 */
@ProviderType
public interface RequirementExpression {
	// Marker interface.
}
