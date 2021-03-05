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

package org.osgi.util.function;

import static java.util.Objects.requireNonNull;

import org.osgi.annotation.versioning.ConsumerType;

/**
 * A function that accepts a single argument and produces a result.
 * <p>
 * This is a functional interface and can be used as the assignment target for a
 * lambda expression or method reference.
 * 
 * @param <T> The type of the function input.
 * @param <R> The type of the function output.
 * @ThreadSafe
 * @author $Id$
 */
@ConsumerType
@FunctionalInterface
public interface Function<T, R> {
	/**
	 * Applies this function to the specified argument.
	 * 
	 * @param t The input to this function.
	 * @return The output of this function.
	 * @throws Exception An exception thrown by the method.
	 */
	R apply(T t) throws Exception;

	/**
	 * Compose the specified {@code Function} to be called on the value returned
	 * by this {@code Function}.
	 * 
	 * @param <S> The type of the value supplied by the specified
	 *            {@code Function}.
	 * @param after The {@code Function} to be called on the value returned by
	 *            this {@code Function}. Must not be {@code null}.
	 * @return A {@code Function} composed of this {@code Function} and the
	 *         specified {@code Function}.
	 */
	default <S> Function<T,S> andThen(Function< ? super R, ? extends S> after) {
		requireNonNull(after);
		return t -> after.apply(apply(t));
	}

	/**
	 * Compose the specified {@code Function} to be called to supply a value to
	 * be consumed by this {@code Function}.
	 * 
	 * @param <S> The type of the value consumed the specified {@code Function}.
	 * @param before The {@code Function} to be called to supply a value to be
	 *            consumed by this {@code Function}. Must not be {@code null}.
	 * @return A {@code Function} composed of this {@code Function} and the
	 *         specified {@code Function}.
	 */
	default <S> Function<S,R> compose(
			Function< ? super S, ? extends T> before) {
		requireNonNull(before);
		return s -> apply(before.apply(s));
	}

	/**
	 * Returns a {@code java.util.function.Function} which wraps the specified
	 * {@code Function} and throws any thrown exceptions.
	 * <p>
	 * The returned {@code java.util.function.Function} will throw any exception
	 * thrown by the wrapped {@code Function}.
	 * 
	 * @param <T> The type of the function input.
	 * @param <R> The type of the function output.
	 * @param wrapped The {@code Function} to wrap. Must not be {@code null}.
	 * @return A {@code java.util.function.Function} which wraps the specified
	 *         {@code Function}.
	 */
	static <T, R> java.util.function.Function<T,R> asJavaFunction(
			Function<T,R> wrapped) {
		requireNonNull(wrapped);
		return t -> {
			try {
				return wrapped.apply(t);
			} catch (Exception e) {
				throw Exceptions.throwUnchecked(e);
			}
		};
	}

	/**
	 * Returns a {@code java.util.function.Function} which wraps the specified
	 * {@code Function} and the specified value.
	 * <p>
	 * If the the specified {@code Function} throws an {@code Exception}, the
	 * the specified value is returned.
	 * 
	 * @param <T> The type of the function input.
	 * @param <R> The type of the function output.
	 * @param wrapped The {@code Function} to wrap. Must not be {@code null}.
	 * @param orElse The value to return if the specified {@code Function}
	 *            throws an {@code Exception}.
	 * @return A {@code java.util.function.Function} which wraps the specified
	 *         {@code Function} and the specified value.
	 */
	static <T, R> java.util.function.Function<T,R> asJavaFunctionOrElse(
			Function<T,R> wrapped, R orElse) {
		requireNonNull(wrapped);
		return t -> {
			try {
				return wrapped.apply(t);
			} catch (Exception e) {
				return orElse;
			}
		};
	}

	/**
	 * Returns a {@code java.util.function.Function} which wraps the specified
	 * {@code Function} and the specified {@code java.util.function.Supplier}.
	 * <p>
	 * If the the specified {@code Function} throws an {@code Exception}, the
	 * value returned by the specified {@code java.util.function.Supplier} is
	 * returned.
	 * 
	 * @param <T> The type of the function input.
	 * @param <R> The type of the function output.
	 * @param wrapped The {@code Function} to wrap. Must not be {@code null}.
	 * @param orElseGet The {@code java.util.function.Supplier} to call for a
	 *            return value if the specified {@code Function} throws an
	 *            {@code Exception}.
	 * @return A {@code java.util.function.Function} which wraps the specified
	 *         {@code Function} and the specified
	 *         {@code java.util.function.Supplier}.
	 */
	static <T, R> java.util.function.Function<T,R> asJavaFunctionOrElseGet(
			Function<T,R> wrapped,
			java.util.function.Supplier< ? extends R> orElseGet) {
		requireNonNull(wrapped);
		return t -> {
			try {
				return wrapped.apply(t);
			} catch (Exception e) {
				return orElseGet.get();
			}
		};
	}

	/**
	 * Returns a {@code Function} which wraps the specified
	 * {@code java.util.function.Function}.
	 * 
	 * @param <T> The type of the function input.
	 * @param <R> The type of the function output.
	 * @param wrapped The {@code java.util.function.Function} to wrap. Must not
	 *            be {@code null}.
	 * @return A {@code Function} which wraps the specified
	 *         {@code java.util.function.Function}.
	 */
	static <T, R> Function<T,R> asFunction(
			java.util.function.Function<T,R> wrapped) {
		requireNonNull(wrapped);
		return wrapped::apply;
	}
}
