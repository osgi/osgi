/*
 * Copyright (c) OSGi Alliance (2014, 2020). All Rights Reserved.
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

package org.osgi.util.function;

import static java.util.Objects.requireNonNull;

import org.osgi.annotation.versioning.ConsumerType;

/**
 * A predicate that accepts a single argument and produces a boolean result.
 * <p>
 * This is a functional interface and can be used as the assignment target for a
 * lambda expression or method reference.
 * 
 * @param <T> The type of the predicate input.
 * @ThreadSafe
 * @author $Id$
 */
@ConsumerType
@FunctionalInterface
public interface Predicate<T> {
	/**
	 * Evaluates this predicate on the specified argument.
	 * 
	 * @param t The input to this predicate.
	 * @return {@code true} if the specified argument is accepted by this
	 *         predicate; {@code false} otherwise.
	 * @throws Exception An exception thrown by the method.
	 */
	boolean test(T t) throws Exception;

	/**
	 * Return a {@code Predicate} which is the negation of this
	 * {@code Predicate}.
	 * 
	 * @return A {@code Predicate} which is the negation of this
	 *         {@code Predicate}.
	 */
	default Predicate<T> negate() {
		return t -> !test(t);
	}

	/**
	 * Compose this {@code Predicate} logical-AND the specified
	 * {@code Predicate}.
	 * <p>
	 * Short-circuiting is used, so the specified {@code Predicate} is not
	 * called if this {@code Predicate} returns {@code false}.
	 * 
	 * @param and The {@code Predicate} to be called after this
	 *            {@code Predicate} is called. Must not be {@code null}.
	 * @return A {@code Predicate} composed of this {@code Predicate} and the
	 *         specified {@code Predicate} using logical-AND.
	 */
	default Predicate<T> and(Predicate< ? super T> and) {
		requireNonNull(and);
		return t -> test(t) && and.test(t);
	}

	/**
	 * Compose this {@code Predicate} logical-OR the specified
	 * {@code Predicate}.
	 * <p>
	 * Short-circuiting is used, so the specified {@code Predicate} is not
	 * called if this {@code Predicate} returns {@code true}.
	 * 
	 * @param or The {@code Predicate} to be called after this {@code Predicate}
	 *            is called. Must not be {@code null}.
	 * @return A {@code Predicate} composed of this {@code Predicate} and the
	 *         specified {@code Predicate} using logical-OR.
	 */
	default Predicate<T> or(Predicate< ? super T> or) {
		requireNonNull(or);
		return t -> test(t) || or.test(t);
	}

	/**
	 * Returns a {@code java.util.function.Predicate} which wraps the specified
	 * {@code Predicate} and throws any thrown exceptions.
	 * <p>
	 * The returned {@code java.util.function.Predicate} will throw any
	 * exception thrown by the wrapped {@code Predicate}.
	 * 
	 * @param <T> The type of the predicate input.
	 * @param wrapped The {@code Predicate} to wrap. Must not be {@code null}.
	 * @return A {@code java.util.function.Predicate} which wraps the specified
	 *         {@code Predicate}.
	 */
	static <T> java.util.function.Predicate<T> asJavaPredicate(
			Predicate<T> wrapped) {
		requireNonNull(wrapped);
		return t -> {
			try {
				return wrapped.test(t);
			} catch (Exception e) {
				throw Exceptions.throwUnchecked(e);
			}
		};
	}

	/**
	 * Returns a {@code java.util.function.Predicate} which wraps the specified
	 * {@code Predicate} and the specified value.
	 * <p>
	 * If the the specified {@code Predicate} throws an {@code Exception}, the
	 * the specified value is returned.
	 * 
	 * @param <T> The type of the predicate input.
	 * @param wrapped The {@code Predicate} to wrap. Must not be {@code null}.
	 * @param orElse The value to return if the specified {@code Predicate}
	 *            throws an {@code Exception}.
	 * @return A {@code java.util.function.Predicate} which wraps the specified
	 *         {@code Predicate} and the specified value.
	 */
	static <T> java.util.function.Predicate<T> asJavaPredicateOrElse(
			Predicate<T> wrapped, boolean orElse) {
		requireNonNull(wrapped);
		return t -> {
			try {
				return wrapped.test(t);
			} catch (Exception e) {
				return orElse;
			}
		};
	}

	/**
	 * Returns a {@code java.util.function.Predicate} which wraps the specified
	 * {@code Predicate} and the specified
	 * {@code java.util.function.BooleanSupplier}.
	 * <p>
	 * If the the specified {@code Predicate} throws an {@code Exception}, the
	 * value returned by the specified
	 * {@code java.util.function.BooleanSupplier} is returned.
	 * 
	 * @param <T> The type of the predicate input.
	 * @param wrapped The {@code Predicate} to wrap. Must not be {@code null}.
	 * @param orElseGet The {@code java.util.function.BooleanSupplier} to call
	 *            for a return value if the specified {@code Predicate} throws
	 *            an {@code Exception}.
	 * @return A {@code java.util.function.Predicate} which wraps the specified
	 *         {@code Predicate} and the specified
	 *         {@code java.util.function.BooleanSupplier}.
	 */
	static <T> java.util.function.Predicate<T> asJavaPredicateOrElseGet(
			Predicate<T> wrapped,
			java.util.function.BooleanSupplier orElseGet) {
		requireNonNull(wrapped);
		return t -> {
			try {
				return wrapped.test(t);
			} catch (Exception e) {
				return orElseGet.getAsBoolean();
			}
		};
	}

	/**
	 * Returns a {@code Predicate} which wraps the specified
	 * {@code java.util.function.Predicate}.
	 * 
	 * @param <T> The type of the predicate input.
	 * @param wrapped The {@code java.util.function.Predicate} to wrap. Must not
	 *            be {@code null}.
	 * @return A {@code Predicate} which wraps the specified
	 *         {@code java.util.function.Predicate}.
	 */
	static <T> Predicate<T> asPredicate(
			java.util.function.Predicate<T> wrapped) {
		requireNonNull(wrapped);
		return wrapped::test;
	}
}
