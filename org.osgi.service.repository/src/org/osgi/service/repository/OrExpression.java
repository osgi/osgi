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
package org.osgi.service.repository;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * A {@link RequirementExpression} representing the {@code or} of a number of
 * requirement expressions.
 * 
 * @ThreadSafe
 * @since 1.1
 */
@ProviderType
public interface OrExpression extends RequirementExpression {
	/**
	 * Return the requirement expressions that are combined by this
	 * {@code OrExpression}.
	 * 
	 * @return An unmodifiable list of requirement expressions that are combined
	 *         by this {@code OrExpression}. The list contains the requirement
	 *         expressions in the order they were specified when this
	 *         requirement expression was created.
	 */
	List<RequirementExpression> getRequirementExpressions();
}
