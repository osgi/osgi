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

/**
 * Representation of the state of a resource for a resource context.
 * <p>
 * ResourceMonitor objects are returned by the
 * {@link ResourceContext#getMonitor(String)} method.
 * <p>
 * The <code>ResourceMonitor</code> object may be used to:
 * <ul>
 * <li>Enable/Disable the monitoring of the corresponding resource type for the
 * corresponding resource context</li>
 * <li>View the current usage of the resource by this resource context</li>
 * </ul>
 * 
 * <p>
 * A resource monitor can have a sampling period, a monitored period, or both.
 * For example, for CPU monitoring, the resource monitor implementation can get
 * the CPU usage of the running threads once per minute, and calculate the CPU
 * usage per context in percentages based on the last ten such measurements.
 * This could make a 60 000 milliseconds sampling period, and a 600 000
 * milliseconds monitored period.
 * 
 * @version 1.0
 * @author $Id$
 * @param <T> The type for the Resource.
 */
public interface ResourceMonitor<T> {

	/**
	 * Returns the resource context that this monitor belongs to
	 * 
	 * @return The associated {@link ResourceContext}
	 */
	public ResourceContext getContext();

	/**
	 * The name of the resource type that this monitor represents
	 * 
	 * @return The name of the monitored resource type
	 */
	public String getResourceType();

	/**
	 * Disable and delete this instance of Resource Monitor. This method MUST
	 * update the list of ResourceMonitor instances hold by the Resource Context
	 * (getContext().removeMonitor(this)).
	 * 
	 * @throws ResourceMonitorException For example, when the monitor can't be
	 *         removed from the ResourceContext.
	 */
	public void delete() throws ResourceMonitorException;

	/**
	 * Checks if the monitoring for this resource type is enabled for this
	 * resource context
	 * 
	 * @return <code>true</code> if monitoring for this resource type is enabled
	 *         for this context, <code>false</code> otherwise
	 */
	public boolean isEnabled();

	/**
	 * Returns true if the ResourceMonitor instance has been deleted, that is the
	 * {@link #delete()} method has been called previously.
	 * 
	 * @return true if deleted.
	 */
	public boolean isDeleted();

	/**
	 * Enable the monitoring of this resource type for the resource context
	 * associated with this monitor instance. This method SHOULD also update the
	 * current resource consumption value (to take into account all previous
	 * resource allocations and releases occurred during the time the monitor
	 * was disabled).
	 * 
	 * @throws ResourceMonitorException if the ResourceMonitor instance can not
	 *         be enabled (for example, some MemoryMonitor implementations
	 *         evaluate the memory consumption by tracking memory allocation
	 *         operation at runtime. This kind of Monitor can not get
	 *         instantaneous memory value. Such Monitor instances need to be
	 *         enabled at starting time.). if the ResourceMonitor instance has
	 *         been previously deleted
	 */
	public void enable() throws ResourceMonitorException;

	/**
	 * Disable the monitoring of this resource type for the resource context
	 * associated with this monitor instance. The resource usage is not
	 * available until it is enabled again.
	 * 
	 * @throws ResourceMonitorException if the ResourceMonitor instance has been
	 *         previously deleted
	 */
	public void disable() throws ResourceMonitorException;

	/**
	 * Returns an object representing the current usage of this resource type by
	 * this resource context.
	 * 
	 * @return The current usage of this resource type.
	 * @throws ResourceMonitorException if the ResourceMonitor instance is not
	 *         enabled.
	 */
	public Comparable<T> getUsage() throws ResourceMonitorException;

	/**
	 * Returns the sampling period for this resource type.
	 * 
	 * @return The sampling period in milliseconds, or <code>-1</code> if a
	 *         sampling period is not relevant for this resource type.
	 */
	public long getSamplingPeriod();

	/**
	 * Returns the time period for which the usage of this resource type is
	 * monitored.
	 * 
	 * @return The monitored period in milliseconds, or <code>-1</code> if a
	 *         monitored period is not relevant for this resource type.
	 */
	public long getMonitoredPeriod();

	/**
	 * Checks if resourceMonitor is equals to the current instance. A
	 * ResourceMonitor rm1 is equals to a ResourceMonitor rm2 if
	 * rm1.getContext().equals(rm2.getContext()) and
	 * r1.getType().equals(rm2.getType()).
	 * 
	 * @param resourceMonitor
	 * @return true if the current instance is equals to the provided
	 *         resourceMonitor
	 */
	@Override
	public boolean equals(Object resourceMonitor);

	/**
	 * Retrieves the hashCode value of this ResourceMonitor. The hashCode value
	 * is based on the hashCode value of the associated ResourceContext and the
	 * hashCode value of the type.
	 * 
	 * @return hashcode
	 */
	@Override
	public int hashCode();
}
