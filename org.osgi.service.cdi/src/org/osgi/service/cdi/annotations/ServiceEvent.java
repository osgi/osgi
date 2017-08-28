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

package org.osgi.service.cdi.annotations;

/**
 * This interface is used in CDI Observer methods to watch OSGi service events.
 * <p>
 * The type parameter is the service argument type and can be one of the
 * following:
 * </p>
 * <ul>
 * <li>service type</li>
 * <li>{@link org.osgi.framework.ServiceReference ServiceReference}</li>
 * <li>{@link org.osgi.framework.ServiceObjects ServiceObjects}</li>
 * <li>properties ({@link java.util.Map Map})</li>
 * <li>tuple of properties ({@link java.util.Map Map}) as key, service type as
 * value ({@link java.util.Map.Entry Map.Entry})</li>
 * </ul>
 *
 * @param <T> the service argument type.
 */
public interface ServiceEvent<T> {

	/**
	 * A functional interface who's method is {@link #accept(Object)} describing an
	 * operation to be executed during a service event.
	 *
	 * @param <T> the service argument type
	 */
	public interface Consumer<T> {

		/**
		 * Performs this operation with the given service argument.
		 *
		 * @param t the service argument
		 */
		public void accept(T t);
	}

	/**
	 * An operation to be invoked upon adding a service to be tracked.
	 *
	 * @param consumer an operation to be invoked upon adding a service to be
	 *        tracked
	 * @return the service event
	 */
	ServiceEvent<T> adding(Consumer<T> consumer);

	/**
	 * An operation to be invoked upon modification of a tracked service's
	 * properties.
	 *
	 * @param consumer an operation to be invoked upon modification of a tracked
	 *        service's properties
	 * @return the service event
	 */
	ServiceEvent<T> modified(Consumer<T> consumer);

	/**
	 * An operation to be invoked upon the removal of a tracked service.
	 *
	 * @param consumer an operation to be invoked upon the removal of a tracked
	 *        service
	 * @return the service event
	 */
	ServiceEvent<T> removed(Consumer<T> consumer);

}
