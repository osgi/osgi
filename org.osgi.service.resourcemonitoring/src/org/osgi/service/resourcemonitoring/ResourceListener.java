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

package org.osgi.service.resourcemonitoring;

import org.osgi.framework.ServiceListener;

/**
 * <p>
 * A ResourceListener is an OSGi service which is notified when a Resource
 * Context violates one of the threshold defined by the listener.
 * <p>
 * Every ResourceListener is associated to a specific Resource Context and a
 * specific Resource type. It defines two types of thresholds: a lower and a
 * upper. A lower threshold is reached when the resource usage decreases below
 * the threshold. On the contrary, an upper threshold is reached when the
 * resource usage exceeds the threshold.
 * <p>
 * Both lower or upper threshold are two levels: a warning level and error
 * level. The warning level indicates the resource usage becomes to be critical
 * but are still acceptable. The error level indicates the resource usage is now
 * critical for the overall system and actions should be taken.
 * <p>
 * A Resource Listener is registered with these two mandatory properties:
 * <ul>
 * <li>{@link #RESOURCE_CONTEXT} which defines the ResourceContext associated to
 * this Listener</li>
 * <li>{@link #RESOURCE_TYPE} which the type of resource</li>
 * </ul>
 * The next optional properties are used to specify threshold values. A
 * ResourceListener must at least provides one of them:
 * <ul>
 * <li>{@link ResourceListener#UPPER_WARNING_THRESHOLD}</li>
 * <li>{@link ResourceListener#UPPER_ERROR_THRESHOLD}</li>
 * <li>{@link ResourceListener#LOWER_WARNING_THRESHOLD}</li>
 * <li>{@link ResourceListener#LOWER_ERROR_THRESHOLD}</li>
 * </ul>
 * These threshold values can also be retrieved through methods.
 * <p>
 * Resource Listeners are associated to a Resource Context and a Resource
 * Monitor based on the {@link #RESOURCE_CONTEXT} property and the
 * {@link #RESOURCE_TYPE} property (both of them are mandatory at registration
 * time).
 * <p>
 * Once associated, the ResourceMonitor gets the threshold values through the
 * service properties (i.e {@link #UPPER_WARNING_THRESHOLD},
 * {@link #UPPER_ERROR_THRESHOLD}, {@link #LOWER_WARNING_THRESHOLD} and
 * {@link #LOWER_WARNING_THRESHOLD}) and store them. Once it detects a new
 * resource consumption, it compares the new resource usage value with the
 * thresholds provided by the Resource Listener. If the resource usage violates
 * one of these thresholds, the Resource Monitor notifies the
 * {@link ResourceListener} through a call to {@link #notify(ResourceEvent)}.
 * <p>
 * A ResourceMonitor tracks threshold value modification by using a
 * {@link ServiceListener}.
 * 
 * @version 1.0
 * @author $Id$
 * @param <T> The type for the Resource.
 */
public interface ResourceListener<T> {

	/**
	 * Mandatory property specifying the Resource Context associated with the
	 * listener.
	 */
	public final String	RESOURCE_CONTEXT		= "resource.context";

	/**
	 * Mandatory property defining the type of Resource (i.e the
	 * ResourceMonitor) associated to this Listener.
	 */
	public final String	RESOURCE_TYPE			= "resource.type";

	/**
	 * Optional property defining the value of the upper warning threshold.
	 */
	public final String	UPPER_WARNING_THRESHOLD	= "upper.warning.threshold";

	/**
	 * Optional property defining the value of the upper error threshold.
	 */
	public final String	UPPER_ERROR_THRESHOLD	= "upper.error.threshold";

	/**
	 * Optional property defining the value of the lower warning threshold.
	 */
	public final String	LOWER_WARNING_THRESHOLD	= "lower.warning.threshold";

	/**
	 * Optional property defining the value of the lower error threshold.
	 */
	public final String	LOWER_ERROR_THRESHOLD	= "lower.error.threshold";

	/**
	 * Receives a resource monitoring notification
	 * 
	 * @param event The {@link ResourceEvent} object
	 */
	public void notify(ResourceEvent<T> event);

	/**
	 * Retrieves the lower warning threshold value set by the listener. If the
	 * resource usage decreases under this threshold value, the
	 * {@link #notify(ResourceEvent)} will be called. The provided ResourceEvent
	 * then indicates the WARNING state is reached.
	 * 
	 * @return a comparable object or null if no threshold is set.
	 */
	public Comparable<T> getLowerWarningThreshold();

	/**
	 * Retrieves the lower error threshold value set by the listener. If the
	 * resource usage decreases under this threshold, the
	 * {@link #notify(ResourceEvent)} will be called. The provided
	 * {@link ResourceEvent} then indicates the ERROR state is reached.
	 * 
	 * @return a comparable object or null if no threshold is set.
	 */
	public Comparable<T> getLowerErrorThreshold();

	/**
	 * Retrieves the upper warning threshold value set by this listener. If the
	 * resource usage exceeds this threshold, the {@link #notify(ResourceEvent)}
	 * method will be called. The provided {@link ResourceEvent} then indicates
	 * the WARNING state is reached.
	 * 
	 * @return a comparable object or null if no threshold is reached.
	 */
	public Comparable<T> getUpperWarningThreshold();

	/**
	 * Retrieves the upper error threshold value set by this listener. If the
	 * resource usage exceeds this threshold, the {@link #notify(ResourceEvent)}
	 * will be called. The provided {@link ResourceEvent} then indicates the
	 * ERROR state is reached.
	 * 
	 * @return a comparable object or null if no threshold is reached.
	 */
	public Comparable<T> getUpperErrorThreshold();
}
