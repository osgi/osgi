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
import org.osgi.resource.Requirement;

/**
 * An {@code ExpressionCombiner} can be used to combine requirement expressions
 * into a single complex requirement expression using the {@code and},
 * {@code or} and {@code not} operators.
 * 
 * @ThreadSafe
 * @since 1.1
 */
@ProviderType
public interface ExpressionCombiner {
	/**
	 * Combine two {@link RequirementExpression}s into a requirement expression
	 * using the {@code and} operator.
	 * 
	 * @param expr1 The first requirement expression to combine into the
	 *        returned requirement expression.
	 * @param expr2 The second requirement expression to combine into the
	 *        returned requirement expression
	 * @return An {@link AndExpression} representing an {@code and} of the
	 *         specified requirement expressions.
	 */
	AndExpression and(RequirementExpression expr1, RequirementExpression expr2);

	/**
	 * Combine multiple {@link RequirementExpression}s into a requirement
	 * expression using the {@code and} operator.
	 * 
	 * @param expr1 The first requirement expression to combine into the
	 *        returned requirement expression.
	 * @param expr2 The second requirement expression to combine into the
	 *        returned requirement expression
	 * @param moreExprs Optional, additional requirement expressions to combine
	 *        into the returned requirement expression.
	 * @return An {@link AndExpression} representing an {@code and} of the
	 *         specified requirement expressions.
	 */
	AndExpression and(RequirementExpression expr1, RequirementExpression expr2, RequirementExpression... moreExprs);

	/**
	 * Wrap a {@link Requirement} in an {@link IdentityExpression}. This can be
	 * useful when working with a combination of {@code Requirement}s and
	 * {@code RequirementExpresion}s.
	 * 
	 * @param req The requirement to wrap in a requirement expression.
	 * @return An {@link IdentityExpression} representing the specified
	 *         requirement.
	 */
	IdentityExpression identity(Requirement req);

	/**
	 * Return the negation of a {@link RequirementExpression}.
	 * 
	 * @param expr The requirement expression to negate.
	 * @return A {@link NotExpression} representing the {@code not} of the
	 *         specified requirement expression.
	 */
	NotExpression not(RequirementExpression expr);

	/**
	 * Combine two {@link RequirementExpression}s into a requirement expression
	 * using the {@code or} operator.
	 * 
	 * @param expr1 The first requirement expression to combine into the
	 *        returned requirement expression.
	 * @param expr2 The second requirement expression to combine into the
	 *        returned requirement expression
	 * @return An {@link OrExpression} representing an {@code or} of the
	 *         specified requirement expressions.
	 */
	OrExpression or(RequirementExpression expr1, RequirementExpression expr2);

	/**
	 * Combine multiple {@link RequirementExpression}s into a requirement
	 * expression using the {@code or} operator.
	 * 
	 * @param expr1 The first requirement expression to combine into the
	 *        returned requirement expression.
	 * @param expr2 The second requirement expression to combine into the
	 *        returned requirement expression
	 * @param moreExprs Optional, additional requirement expressions to combine
	 *        into the returned requirement expression.
	 * @return An {@link OrExpression} representing an {@code or} of the
	 *         specified requirement expressions.
	 */
	OrExpression or(RequirementExpression expr1, RequirementExpression expr2, RequirementExpression... moreExprs);
}
