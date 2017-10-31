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

package org.osgi.service.cdi;

import java.util.function.Consumer;
import java.util.function.Function;
import org.osgi.annotation.versioning.ProviderType;

/**
 * This interface is used in CDI Observer methods to watch OSGi service events.
 *
 * @author $Id$
 */
@ProviderType
public interface ReferenceEvent {

	/**
	 * Declare a function to call during "adding" events.
	 * <p>
	 * The type parameter S is the service argument type and can be one of the
	 * following:
	 * </p>
	 * <ul>
	 * <li>service type</li>
	 * <li>{@link org.osgi.framework.ServiceReference ServiceReference}</li>
	 * <li>{@link org.osgi.service.cdi.ReferenceServiceObjects
	 * ReferenceServiceObjects}</li>
	 * <li>properties ({@link java.util.Map Map})</li>
	 * <li>tuple of properties ({@link java.util.Map Map}) as key, service type as
	 * value ({@link java.util.Map.Entry Map.Entry})</li>
	 * </ul>
	 * <p>
	 * The type parameter R is an arbitrary type provided by the user that
	 * implements {@link AutoCloseable} to handle the remove event.
	 *
	 * @param adding a function called when the event type is "adding"
	 * @return event
	 *
	 * @param <S> the service argument type.
	 * @param <R> the result of the adding function
	 */
	<S, R extends AutoCloseable> ReferenceEvent adding(Function<S, R> adding);

	/**
	 * Declare a consumer to call during "modified" events.
	 * <p>
	 * The type parameter S is the service argument type and can be one of the
	 * following:
	 * </p>
	 * <ul>
	 * <li>service type</li>
	 * <li>{@link org.osgi.framework.ServiceReference ServiceReference}</li>
	 * <li>{@link org.osgi.service.cdi.ReferenceServiceObjects
	 * ReferenceServiceObjects}</li>
	 * <li>properties ({@link java.util.Map Map})</li>
	 * <li>tuple of properties ({@link java.util.Map Map}) as key, service type as
	 * value ({@link java.util.Map.Entry Map.Entry})</li>
	 * </ul>
	 *
	 * @param modified a consumer called when the event type is "modified"
	 * @return event
	 *
	 * @param <S> the service argument type.
	 */
	<S> ReferenceEvent modified(Consumer<S> modified);

}
