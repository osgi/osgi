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

package org.osgi.service.resourcemonitoring.monitor;

import org.osgi.service.resourcemonitoring.ResourceContext;
import org.osgi.service.resourcemonitoring.ResourceMonitor;
import org.osgi.service.resourcemonitoring.ResourceMonitoringService;

/**
 * A {@link ResourceMonitor} for the
 * {@link ResourceMonitoringService#RES_TYPE_DISK_STORAGE} resource type. A
 * DiskStorageMonitor instance monitors and limits the persistent storage of the
 * bundle belonging to the {@link ResourceContext}
 * 
 * @version 1.0
 * @author $Id$
 */
public interface DiskStorageMonitor extends ResourceMonitor<Long> {

	/**
	 * Returns the sum of the size of the persistent storage areas of the
	 * bundles in this resource context.
	 * <p>
	 * The {@link #getUsage()} method returns the same value, wrapped in a long.
	 * 
	 * @return the sum of the sizes of the persistent storage areas in bytes
	 */
	public long getUsedDiskStorage();

}
