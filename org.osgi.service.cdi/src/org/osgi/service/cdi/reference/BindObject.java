/*
 * Copyright (c) OSGi Alliance (2017, 2018). All Rights Reserved.
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

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.service.cdi.annotations.Reference;

/**
 * A bean provided by CCR for binding actions to life cycle events of matching
 * services.
 *
 * @see Reference
 * @param <S> the service argument type.
 * @author $Id$
 */
@ProviderType
public interface BindObject<S> {

	/**
	 * Subscribe an action to the <em>adding</em> service event.
	 * <p>
	 * Only the last <em>adding</em> action is used.
	 *
	 * @param action the action, whose argument is the service object, to
	 *                   subscribe to the <em>adding</em> service event
	 * @return self
	 * @throws IllegalStateException when called after {@link #bind}
	 */
	BindObject<S> adding(Consumer<S> action);

	/**
	 * Subscribe an action to the <em>adding</em> service event.
	 * <p>
	 * Only the last <em>adding</em> action is used.
	 *
	 * @param action the action, whose arguments are the service object and the
	 *                   {@code Map<String, Object>} of service properties, to
	 *                   subscribe to the <em>adding</em> service event
	 * @return self
	 * @throws IllegalStateException when called after {@link #bind}
	 */
	BindObject<S> adding(BiConsumer<S,Map<String,Object>> action);

	/**
	 * Subscribe an action to the <em>modified</em> service event.
	 * <p>
	 * Only the last <em>modified</em> action is used.
	 *
	 * @param action the action, whose argument is the service object, to
	 *                   subscribe to the <em>modified</em> service event
	 * @return self
	 * @throws IllegalStateException when called after {@link #bind}
	 */
	BindObject<S> modified(Consumer<S> action);

	/**
	 * Subscribe an action to the <em>modified</em> service event.
	 * <p>
	 * Only the last <em>modified</em> action is used.
	 *
	 * @param action the action, whose arguments are the service object and the
	 *                   {@code Map<String, Object>} of service properties, to
	 *                   subscribe to the <em>modified</em> service event
	 * @return self
	 * @throws IllegalStateException when called after {@link #bind}
	 */
	BindObject<S> modified(BiConsumer<S,Map<String,Object>> action);

	/**
	 * Subscribe an action to the <em>removed</em> service event.
	 * <p>
	 * Only the last <em>removed</em> action is used.
	 *
	 * @param action the action, whose argument is the service object, to
	 *                   subscribe to the <em>removed</em> service event
	 * @return self
	 * @throws IllegalStateException when called after {@link #bind}
	 */
	BindObject<S> removed(Consumer<S> action);

	/**
	 * Subscribe an action to the <em>removed</em> service event.
	 * <p>
	 * Only the last <em>removed</em> action is used.
	 *
	 * @param action the action, whose arguments are the service object and the
	 *                   {@code Map<String, Object>} of service properties, to
	 *                   subscribe to the <em>removed</em> service event
	 * @return self
	 * @throws IllegalStateException when called after {@link #bind}
	 */
	BindObject<S> removed(BiConsumer<S,Map<String,Object>> action);

	/**
	 * The bind terminal operation is required to instruct CCR that all the bind
	 * actions have been specified, otherwise bind actions will never be called
	 * by CCR.
	 * <p>
	 * Calling {@link #bind} again has no effect.
	 */
	void bind();

}
