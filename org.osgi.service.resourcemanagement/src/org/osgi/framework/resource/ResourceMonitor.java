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
package org.osgi.framework.resource;

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
	 * Checks if the monitoring for this resource type is enabled for this
	 * resource context
	 * 
	 * @return <code>true</code> if monitoring for this resource type is enabled
	 *         for this context, <code>false</code> otherwise
	 */
	public boolean isMonitored();

	/**
	 * Enable/Disable the monitoring of this resource type for this resource
	 * context.
	 * <p>
	 * When monitoring of a resource type is enabled, the resources of this type
	 * that were allocated by the bundles in this resource context while
	 * monitoring was disabled, will not be added to the resource context. The
	 * action applies only for future allocations.
	 * <p>
	 * When monitoring of a resource type is disabled, the resources of this
	 * type that were previously allocated by the bundles in this resource
	 * context while monitoring was enabled, will not be removed from the
	 * resource context. The action applies only for future allocations.
	 * 
	 * @param monitor
	 *            <code>true</code> to enable monitoring, <code>false</code> to
	 *            disable it.
	 */
	public void setMonitored(boolean monitor);

	/**
	 * Returns an object representing the current usage of this resource type by
	 * this resource context.
	 * 
	 * @return The current usage of this resource type.
	 */
	public Object getUsage();

	/**
	 * Returns an array of existing {@link ResourceThreshold} instances.
	 * 
	 * @return
	 */
	public ResourceThreshold[] getThresholds();

	/**
	 * Add a new Resource Threshold.
	 * 
	 * @param resourceThreshold
	 *            the new {@link ResourceThreshold} instance to be added to the
	 *            array.
	 */
	public void addResourceThreshold(ResourceThreshold resourceThreshold);

	/**
	 * Remove a Resource Threshold.
	 * 
	 * @param resourceThreshold
	 *            to be removed.
	 */
	public void removeResourceThreshold(ResourceThreshold resourceThreshold);


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
