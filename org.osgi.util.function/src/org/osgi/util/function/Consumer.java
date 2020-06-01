/*
 * Copyright (c) OSGi Alliance (2017, 2020). All Rights Reserved.
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
 * A function that accepts a single argument and produces no result.
 * <p>
 * This is a functional interface and can be used as the assignment target for a
 * lambda expression or method reference.
 * 
 * @param <T> The type of the function input.
 * @ThreadSafe
 * @since 1.1
 * @author $Id$
 */
@ConsumerType
@FunctionalInterface
public interface Consumer<T> {
	/**
	 * Applies this function to the specified argument.
	 * 
	 * @param t The input to this function.
	 * @throws Exception An exception thrown by the method.
	 */
	void accept(T t) throws Exception;

	/**
	 * Compose the specified {@code Consumer} to be called after this
	 * {@code Consumer}.
	 * 
	 * @param after The {@code Consumer} to be called after this
	 *            {@code Consumer} is called. Must not be {@code null}.
	 * @return A {@code Consumer} composed of this {@code Consumer} and the
	 *         specified {@code Consumer}.
	 */
	default Consumer<T> andThen(Consumer< ? super T> after) {
		requireNonNull(after);
		return t -> {
			accept(t);
			after.accept(t);
		};
	}

	/**
	 * Returns a {@code java.util.function.Consumer} which wraps the specified
	 * {@code Consumer} and throws any thrown exceptions.
	 * <p>
	 * The returned {@code java.util.function.Consumer} will throw any exception
	 * thrown by the wrapped {@code Consumer}.
	 * 
	 * @param <T> The type of the function input.
	 * @param wrapped The {@code Consumer} to wrap. Must not be {@code null}.
	 * @return A {@code java.util.function.Consumer} which wraps the specified
	 *         {@code Consumer}.
	 */
	static <T> java.util.function.Consumer<T> asJavaConsumer(
			Consumer<T> wrapped) {
		requireNonNull(wrapped);
		return t -> {
			try {
				wrapped.accept(t);
			} catch (Exception e) {
				throw Exceptions.throwUnchecked(e);
			}
		};
	}

	/**
	 * Returns a {@code java.util.function.Consumer} which wraps the specified
	 * {@code Consumer} and discards any thrown {@code Exception}s.
	 * <p>
	 * The returned {@code java.util.function.Consumer} will discard any
	 * {@code Exception} thrown by the wrapped {@code Consumer}.
	 * 
	 * @param <T> The type of the function input.
	 * @param wrapped The {@code Consumer} to wrap. Must not be {@code null}.
	 * @return A {@code java.util.function.Consumer} which wraps the specified
	 *         {@code Consumer}.
	 */
	static <T> java.util.function.Consumer<T> asJavaConsumerIgnoreException(
			Consumer<T> wrapped) {
		requireNonNull(wrapped);
		return t -> {
			try {
				wrapped.accept(t);
			} catch (Exception e) {
				// discard
			}
		};
	}

	/**
	 * Returns a {@code Consumer} which wraps a
	 * {@code java.util.function.Consumer}.
	 * 
	 * @param <T> The type of the function input.
	 * @param wrapped The {@code java.util.function.Consumer} to wrap. Must not
	 *            be {@code null}.
	 * @return A {@code Consumer} which wraps the specified
	 *         {@code java.util.function.Consumer}.
	 */
	static <T> Consumer<T> asConsumer(java.util.function.Consumer<T> wrapped) {
		requireNonNull(wrapped);
		return wrapped::accept;
	}
}
