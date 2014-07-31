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

package org.osgi.service.resourcemanagement.monitor;

import org.osgi.service.resourcemanagement.ResourceContext;
import org.osgi.service.resourcemanagement.ResourceManager;
import org.osgi.service.resourcemanagement.ResourceMonitor;

/**
 * A {@link ResourceMonitor} for the {@link ResourceManager#RES_TYPE_CPU}
 * resource type. CPUMonitor instance monitors the CPU consumed by a
 * {@link ResourceContext} instance.
 * 
 * @author see RFC 200 authors: Andre Bottaro, Gregory Bonnardel, Svetozar
 *         Dimov, Evgeni Grigorov, Arnaud Rinquin, Antonin Chazalet.
 */
public interface CPUMonitor extends ResourceMonitor {

	/**
	 * Returns the CPU usage as a cumulative number of nanoseconds
	 * <p>
	 * The {@link #getUsage()} method returns the same value, wrapped in a
	 * {@link Long}
	 * 
	 * @return the CPU usage in nanoseconds
	 */
	public int getCPUUsage();

}
