/*
 * Copyright (c) OSGi Alliance (2010). All Rights Reserved.
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

package org.osgi.service.resource;

/**
 * Representation of the state of a resource for a resource context.
 * <p>
 * ResourceMonitor objects are returned by the
 * {@link ResourceContext#getMonitor(String)} method.
 * <p>
 * The <code>ResourceMonitor</code> object may be used to:
 * <ul>
 * <li>Enable/Disable the monitoring of the corresponding resource type for the
 * corresponding resource context
 * <li>View/Edit/Create/Remove the thresholds for this resource for this
 * resource context (see {@link ResourceThreshold}).
 * <li>View the current usage of the resource by this resource context
 * </ul>
 * 
 * <p>
 * A resource monitor can have a sampling period, a monitored period, or both.
 * For example, for CPU monitoring, the resource management implementation can
 * get the CPU usage of the running threads once per minute, and calculate the
 * CPU usage per context in percentages based on the last ten such measurements.
 * This will make a 60 000 milliseconds sampling period, and a 600 000
 * milliseconds monitored period.
 * 
 * @ThreadSafe
 * @noimplement
 */
public interface ResourceMonitor {

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
	 */
	public void delete();

	/**
	 * Checks if the monitoring for this resource type is enabled for this
	 * resource context
	 * 
	 * @return <code>true</code> if monitoring for this resource type is enabled
	 *         for this context, <code>false</code> otherwise
	 */
	public boolean isEnabled();

	/**
	 * Returns true if the ResourceMonitor instance has been deleted, i.e. the
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
	 *         be enabled (for example some MemoryMonitor implementations
	 *         evaluate the memory consumption by tracking memory allocation
	 *         operation at runtime. This kind of Monitor can not get
	 *         instantaneous memory value. Such Monitor instances need to be
	 *         enabled at starting time.)
	 * @throws IllegalStateException if the ResourceMonitor instance has been
	 *         previously deleted
	 */
	public void enable() throws ResourceMonitorException, IllegalStateException;

	/**
	 * Disable the monitoring of this resource type for the resource context
	 * associated with this monitor instance. The resource usage is not
	 * available until it is enabled again.
	 * 
	 * @throws IllegalStateException if the ResourceMonitor instance has been
	 *         previously deleted
	 */
	public void disable() throws IllegalStateException;

	/**
	 * Returns an object representing the current usage of this resource type by
	 * this resource context.
	 * 
	 * @return The current usage of this resource type.
	 * @throws IllegalStateException if the ResourceMonitor instance is not
	 *         enabled.
	 */
	public Comparable getUsage() throws IllegalStateException;

	/**
	 * Returns the lower {@link ResourceThreshold} instance.
	 * 
	 * @return the lower threshold if set
	 */
	public ResourceThreshold getLowerThreshold();

	/**
	 * Returns the upper {@link ResourceThreshold} instance.
	 * 
	 * @return the upper threshold if set
	 */
	public ResourceThreshold getUpperThreshold();

	/**
	 * Set the lower {@link ResourceThreshold} instance.
	 * 
	 * @param lowerThreshold new lower threshold. It can be null.
	 * @exception ThresholdException if lowerThreshold.ipUpper() returns true.
	 */
	public void setLowerThreshold(ResourceThreshold lowerThreshold) throws ThresholdException;

	/**
	 * Set the upper {@link ResourceThreshold} instance.
	 * 
	 * @param upperThreshold new upper threshold. It can be null
	 * @exception ThresholdException if upperThreshold.isUpper() returns false.
	 */
	public void setUpperThreshold(ResourceThreshold upperThreshold) throws ThresholdException;

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
}
