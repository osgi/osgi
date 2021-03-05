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
 * {@link ResourceMonitoringService#RES_TYPE_THREADS} resource type. A
 * ThreadMonitor instance monitors and limits the thread created by a
 * {@link ResourceContext} instance.
 * 
 * @version 1.0
 * @author $Id$
 */
public interface ThreadMonitor extends ResourceMonitor<Integer> {

	/**
	 * Returns the number of alive threads created by the bundles in this
	 * resource context. A Thread is considered to be alive when its java state
	 * is one of the following:
	 * <ul>
	 * <li>RUNNABLE</li>
	 * <li>BLOCKED</li>
	 * <li>WAITING</li>
	 * <li>TIMED_WAITING</li>
	 * </ul>
	 * <p>
	 * The {@link #getUsage()} method returns the same value, wrapped in a int.
	 * 
	 * @return the number of alive threads created by this resource context
	 */
	public int getAliveThreads();

}
