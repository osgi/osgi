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

/**
 * A user supplied customizer used to handle the event lifecycles.
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
 * @param <S> the service argument type.
 * @param <T> the tracked type.
 *
 * @author $Id$
 */
public interface ReferenceEventCustomizer<S, T> {

	/**
	 * Called when a service matching the reference is added.
	 * 
	 * @param s the service instance
	 * @return the tracked instance
	 */
	T adding(S s);

	/**
	 * Called when a service matching the reference is modified.
	 * 
	 * @param s the service instance
	 * @param t the tracked instance
	 */
	void modified(S s, T t);

	/**
	 * Called when a service matching the reference is removed.
	 * 
	 * @param s the service instance
	 * @param t the tracked instance
	 */
	void removed(S s, T t);

}
