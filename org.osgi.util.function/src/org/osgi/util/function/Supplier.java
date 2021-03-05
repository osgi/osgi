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
 * A function that produces a result.
 * <p>
 * This is a functional interface and can be used as the assignment target for a
 * lambda expression or method reference.
 * 
 * @param <T> The type of the function output.
 * @ThreadSafe
 * @author $Id$
 */
@ConsumerType
@FunctionalInterface
public interface Supplier<T> {
	/**
	 * Returns a value.
	 * 
	 * @return The output of this function.
	 * @throws Exception An exception thrown by the method.
	 */
	T get() throws Exception;

	/**
	 * Returns a {@code java.util.function.Supplier} which wraps the specified
	 * {@code Supplier} and throws any thrown exceptions.
	 * <p>
	 * The returned {@code java.util.function.Supplier} will throw any exception
	 * thrown by the wrapped {@code Supplier}.
	 * 
	 * @param <T> The type of the function output.
	 * @param wrapped The {@code Supplier} to wrap. Must not be {@code null}.
	 * @return A {@code java.util.function.Supplier} which wraps the specified
	 *         {@code Supplier}.
	 */
	static <T> java.util.function.Supplier<T> asJavaSupplier(
			Supplier<T> wrapped) {
		requireNonNull(wrapped);
		return () -> {
			try {
				return wrapped.get();
			} catch (Exception e) {
				throw Exceptions.throwUnchecked(e);
			}
		};
	}

	/**
	 * Returns a {@code java.util.function.Supplier} which wraps the specified
	 * {@code Supplier} and the specified value.
	 * <p>
	 * If the the specified {@code Supplier} throws an {@code Exception}, the
	 * the specified value is returned.
	 * 
	 * @param <T> The type of the function output.
	 * @param wrapped The {@code Supplier} to wrap. Must not be {@code null}.
	 * @param orElse The value to return if the specified {@code Supplier}
	 *            throws an {@code Exception}.
	 * @return A {@code java.util.function.Supplier} which wraps the specified
	 *         {@code Supplier} and the specified value.
	 */
	static <T> java.util.function.Supplier<T> asJavaSupplierOrElse(
			Supplier<T> wrapped, T orElse) {
		requireNonNull(wrapped);
		return () -> {
			try {
				return wrapped.get();
			} catch (Exception e) {
				return orElse;
			}
		};
	}

	/**
	 * Returns a {@code java.util.function.Supplier} which wraps the specified
	 * {@code Supplier} and the specified {@code java.util.function.Supplier}.
	 * <p>
	 * If the the specified {@code Supplier} throws an {@code Exception}, the
	 * value returned by the specified {@code java.util.function.Supplier} is
	 * returned.
	 * 
	 * @param <T> The type of the function output.
	 * @param wrapped The {@code Supplier} to wrap. Must not be {@code null}.
	 * @param orElseGet The {@code java.util.function.Supplier} to call for a
	 *            return value if the specified {@code Supplier} throws an
	 *            {@code Exception}.
	 * @return A {@code java.util.function.Supplier} which wraps the specified
	 *         {@code Supplier} and the specified
	 *         {@code java.util.function.Supplier}.
	 */
	static <T> java.util.function.Supplier<T> asJavaSupplierOrElseGet(
			Supplier<T> wrapped,
			java.util.function.Supplier< ? extends T> orElseGet) {
		requireNonNull(wrapped);
		return () -> {
			try {
				return wrapped.get();
			} catch (Exception e) {
				return orElseGet.get();
			}
		};
	}

	/**
	 * Returns a {@code Supplier} which wraps the specified
	 * {@code java.util.function.Supplier}.
	 * 
	 * @param <T> The type of the function output.
	 * @param wrapped The {@code java.util.function.Supplier} to wrap. Must not
	 *            be {@code null}.
	 * @return A {@code Supplier} which wraps the specified
	 *         {@code java.util.Supplier.Function}.
	 */
	static <T> Supplier<T> asSupplier(java.util.function.Supplier<T> wrapped) {
		requireNonNull(wrapped);
		return wrapped::get;
	}
}
