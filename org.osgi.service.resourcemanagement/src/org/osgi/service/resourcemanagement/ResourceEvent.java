/*
 * Copyright (c) OSGi Alliance (2010, 2013). All Rights Reserved.
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

package org.osgi.service.resourcemanagement;


/**
 * <p>
 * An event sent to {@link ResourceListener} when resource usage violates one of
 * their thresholds.
 * </p>
 * <p>
 * <code>ResourceEvent</code> objects are delivered synchronously to all
 * matching {@link ResourceListener} services. A type code is used to identify
 * the event.
 * </p>
 * 
 * @Immutable
 * @see ResourceListener
 */
public class ResourceEvent {

	/**
	 * Type of ResourceEvent indicating a threshold goes to the NORMAL state.
	 */
	public static final int	NORMAL						= 0;

	/**
	 * Type of ResourceEvent indicating a threshold goes to the WARNING state.
	 */
	public static final int	WARNING						= 1;

	/**
	 * Type of ResourceEvent indicating a threshold goes to the ERROR state.
	 */
	public static final int	ERROR						= 2;

	private final int		type;
	private final boolean			isUpperThrehsold;
	private final ResourceContext	resourceContext;
	private final Object			value;

	/**
	 * Create a new ResourceEvent.
	 * 
	 * @param pType
	 * @param pContext
	 * @param pIsUpperThreshold
	 * @param pValue
	 */
	public ResourceEvent(final int pType, final ResourceContext pContext, final boolean pIsUpperThreshold, final Object pValue) {
		type = pType;
		resourceContext = pContext;
		isUpperThrehsold = pIsUpperThreshold;
		value = pValue;
	}

	/**
	 * Returns the event type. The type values are:
	 * <ul>
	 * <li>{@link #NORMAL}</li>
	 * <li>{@link #WARNING}</li>
	 * <li>{@link #ERROR}</li>
	 * </ul>
	 * 
	 * @return The event type
	 */
	public int getType() {
		return type;
	}

	/**
	 * Returns the resource consumption value. Relevant only for event types
	 * {@link #NORMAL}, {@link #WARNING} and {@link #ERROR}.
	 * 
	 * @return the resource consumption value, or null if a resource monitor is
	 *         not relevant
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Returns the resource context that caused the event.
	 * 
	 * @return The resource context that cased the event.
	 */
	public ResourceContext getContext() {
		return resourceContext;
	}



	/**
	 * Returns true if the threshold triggering this event is an upper
	 * threshold. This method is only used when {@link #getType()} returns
	 * {@link #NORMAL}, {@link #WARNING} or {@link #ERROR}.
	 * 
	 * 
	 * @return true if it is an upper threshold.
	 */
	public boolean isUpperThreshold() {
		return isUpperThrehsold;
	}

	public int hashCode() {
		// TODO
		return super.hashCode();
	}

	public boolean equals(Object var0) {
		// TODO
		return super.equals(var0);
	}

	public String toString() {
		// TODO
		return super.toString();
	}
}
