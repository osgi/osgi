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

import java.util.Map;
import java.util.function.Consumer;
import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cdi.annotations.Reference;

/**
 * This interface is used in CDI Observer methods to watch OSGi service events.
 *
 * <p>
 * The design follows the callback model. Callbacks are registered for each
 * phase of the service lifecycle that is interesting to the application.
 *
 * <p>
 * {@code ReferenceEvent}s are fired by the CDI Extender on behalf of the CDI
 * Container. The events are fired synchronously.
 *
 * The event instance is only valid during the invocation of the observer
 * method. Calling any of the event methods after the return of the observer
 * method will result in an {@link IllegalStateException IllegalStateException}.
 *
 * @see Reference
 * 
 * @param <S> the service argument type.
 *
 * @author $Id: 0b9171cabee0256d12b4ffa1e7bc3470f423c979 $
 */
@ProviderType
public interface ReferenceEvent<S> {

	/**
	 * Subscribe an action to the "adding" service event.
	 *
	 * @param action to subscribe to the "adding" service event
	 * @throws IllegalStateException when called after completion of the observer
	 *         method
	 */
	void onAdding(Consumer<S> action);

	/**
	 * Subscribe an action to the "adding" service event.
	 *
	 * @param consumer to subscribe to the "adding" service event
	 * @throws IllegalStateException when called after completion of the observer
	 *         method
	 */
	void onAddingServiceReference(Consumer<ServiceReference<S>> consumer);

	/**
	 * Subscribe an action to the "adding" service event.
	 *
	 * @param consumer to subscribe to the "adding" service event
	 * @throws IllegalStateException when called after completion of the observer
	 *         method
	 */
	void onAddingServiceObjects(Consumer<ReferenceServiceObjects<S>> consumer);

	/**
	 * Subscribe an action to the "adding" service event.
	 *
	 * @param consumer to subscribe to the "adding" service event
	 * @throws IllegalStateException when called after completion of the observer
	 *         method
	 */
	void onAddingProperties(Consumer<Map<String, ?>> consumer);

	/**
	 * Subscribe an action to the "adding" service event.
	 *
	 * @param consumer to subscribe to the "adding" service event
	 * @throws IllegalStateException when called after completion of the observer
	 *         method
	 */
	void onAddingTuple(Consumer<Map.Entry<Map<String, ?>, S>> consumer);

	/**
	 * Subscribe an action to the "updated" service event.
	 *
	 * @param action to subscribe to the "updated" service event
	 * @throws IllegalStateException when called after completion of the observer
	 *         method
	 */
	void onUpdate(Consumer<S> action);

	/**
	 * Subscribe an action to the "updated" service event.
	 *
	 * @param consumer to subscribe to the "updated" service event
	 * @throws IllegalStateException when called after completion of the observer
	 *         method
	 */
	void onUpdateServiceReference(Consumer<ServiceReference<S>> consumer);

	/**
	 * Subscribe an action to the "updated" service event.
	 *
	 * @param consumer to subscribe to the "updated" service event
	 * @throws IllegalStateException when called after completion of the observer
	 *         method
	 */
	void onUpdateServiceObjects(Consumer<ReferenceServiceObjects<S>> consumer);

	/**
	 * Subscribe an action to the "updated" service event.
	 *
	 * @param consumer to subscribe to the "updated" service event
	 * @throws IllegalStateException when called after completion of the observer
	 *         method
	 */
	void onUpdateProperties(Consumer<Map<String, ?>> consumer);

	/**
	 * Subscribe an action to the "updated" service event.
	 *
	 * @param consumer to subscribe to the "updated" service event
	 * @throws IllegalStateException when called after completion of the observer
	 *         method
	 */
	void onUpdateTuple(Consumer<Map.Entry<Map<String, ?>, S>> consumer);

	/**
	 * Subscribe an action to the "removed" service event.
	 *
	 * @param consumer to subscribe to the "removed" service event
	 * @throws IllegalStateException when called after completion of the observer
	 *         method
	 */
	void onRemove(Consumer<S> consumer);

	/**
	 * Subscribe an action to the "removed" service event.
	 *
	 * @param consumer to subscribe to the "removed" service event
	 * @throws IllegalStateException when called after completion of the observer
	 *         method
	 */
	void onRemoveServiceReference(Consumer<ServiceReference<S>> consumer);

	/**
	 * Subscribe an action to the "removed" service event.
	 *
	 * @param consumer to subscribe to the "removed" service event
	 * @throws IllegalStateException when called after completion of the observer
	 *         method
	 */
	void onRemoveServiceObjects(Consumer<ReferenceServiceObjects<S>> consumer);

	/**
	 * Subscribe an action to the "removed" service event.
	 *
	 * @param consumer to subscribe to the "removed" service event
	 * @throws IllegalStateException when called after completion of the observer
	 *         method
	 */
	void onRemoveProperties(Consumer<Map<String, ?>> consumer);

	/**
	 * Subscribe an action to the "removed" service event.
	 *
	 * @param consumer to subscribe to the "removed" service event
	 * @throws IllegalStateException when called after completion of the observer
	 *         method
	 */
	void onRemoveTuple(Consumer<Map.Entry<Map<String, ?>, S>> consumer);

}
