/*
 * Copyright (c) OSGi Alliance (2017). All Rights Reserved.
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

package org.osgi.service.cdi.reference;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.osgi.annotation.versioning.ProviderType;
import org.osgi.service.cdi.annotations.Greedy;
import org.osgi.service.cdi.annotations.Reference;

/**
 * This interface is used at injection points marked with {@link Reference} to
 * indicate that a suitable service is optional and that dynamic behavior of
 * services is expected. The result is that the component will be created even
 * when no service is available and will not be destroyed as services come and
 * go even when no suitable service is available. Applying {@link Greedy} will
 * determine if, when a service is available, it is the "best" service
 * (according to natural service ordering) rather than any suitable one.
 *
 * @param <T> the service argument type.
 *
 * @author $Id$
 */
@ProviderType
public interface DynamicOptional<T> {

	/**
	 * Get the currently available service.
	 *
	 * @return the currently available service
	 */
	public T get();

	/**
	 * Check if there is a service present.
	 *
	 * @return if there is a service present
	 */
	public boolean isPresent();

	/**
	 * If a service is present, invoke the specified consumer with the service,
	 * otherwise do nothing.
	 *
	 * @param consumer
	 */
	public void ifPresent(Consumer<? super T> consumer);

	/**
	 * If a service is present, and the service matches the given predicate, return
	 * an {@code Optional} describing the service, otherwise return an empty
	 * {@code Optional}.
	 *
	 * @param predicate
	 * @return an {@code Optional<T>}
	 */
	public Optional<T> filter(Predicate<? super T> predicate);

	/**
	 * If a service is present, apply the provided mapping function to it, and if
	 * the result is non-null, return an {@code Optional} describing the result.
	 * Otherwise return an empty {@code Optional}.
	 *
	 * @param mapper
	 * @return an {@code Optional<U>}
	 * @param <U>
	 */
	public <U> Optional<U> map(Function<? super T, ? extends U> mapper);

	/**
	 * If a service is present, apply the provided {@code Optional}-bearing mapping
	 * function to it, return that result, otherwise return an empty
	 * {@code Optional}. This method is similar to {@link #map(Function)}, but the
	 * provided mapper is one whose result is already an {@code Optional}, and if
	 * invoked, {@code flatMap} does not wrap it with an additional
	 * {@code Optional}.
	 *
	 * @param mapper
	 * @return an {@code Optional<U>}
	 * @param <U>
	 */
	public <U> Optional<U> flatMap(Function<? super T, Optional<U>> mapper);

	/**
	 * Return the service if present, otherwise return {@code other}.
	 *
	 * @param other
	 * @return the service or other
	 */
	public T orElse(T other);

	/**
	 * Return the service if present, otherwise invoke {@code other} and return the
	 * result of that invocation.
	 *
	 * @param other
	 * @return the service or result of other
	 */
	public T orElseGet(Supplier<? extends T> other);

	/**
	 * Return the service, if present, otherwise throw an exception to be created by
	 * the provided supplier.
	 *
	 * @param exceptionSupplier
	 * @return the service
	 * @throws X
	 */
	public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X;

}
