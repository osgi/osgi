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

import java.util.function.BiConsumer;
import java.util.function.Function;
import org.osgi.annotation.versioning.ProviderType;

/**
 * This interface is used in CDI Observer methods to watch OSGi service events.
 * <p>
 * The type parameter is the service argument type and can be one of the
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
 * @param <S> the service argument type.
 *
 * @author $Id$
 */
@ProviderType
public interface ReferenceEvent<S> {

	/**
	 * Dispatch the handling of the event to operations that will handle the
	 * lifecycles.
	 *
	 * @param <T> the tracked type
	 * @param adding function called on adding service
	 * @param modified consumer called on modified service
	 * @param removed consumer called on removed service
	 */
	<T> void dispatch(Function<S, T> adding, BiConsumer<S, T> modified, BiConsumer<S, T> removed);

	/**
	 * Dispatch the handling of the event to a customizer that will handle the
	 * lifecycles.
	 *
	 * @param <T> the tracked type
	 * @param customizer handles the lifecycles
	 */
	<T> void dispatch(ReferenceEventCustomizer<S, T> customizer);

}
