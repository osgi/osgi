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

import org.osgi.service.resourcemonitoring.monitor.CPUMonitor;
import org.osgi.service.resourcemonitoring.monitor.DiskStorageMonitor;
import org.osgi.service.resourcemonitoring.monitor.MemoryMonitor;
import org.osgi.service.resourcemonitoring.monitor.SocketMonitor;
import org.osgi.service.resourcemonitoring.monitor.ThreadMonitor;

/**
 * It manages the Resource Context instances. It is available through the OSGi
 * service registry.
 * 
 * This service holds the existing Resource Context instances. Resource Context
 * instances are created by calling the
 * {@link #createContext(String, ResourceContext)} method.
 * 
 * @version 1.0
 * @author $Id$
 */
public interface ResourceMonitoringService {

	/**
	 * The name of the threads resource type, used to monitor and control the
	 * number of threads created by a resource context.
	 * {@link ResourceMonitoringService} implementations must create
	 * {@link ThreadMonitor} instances for this resource type.
	 */
	public static final String	RES_TYPE_THREADS		= "resource.type.threads";

	/**
	 * The name of the CPU resource type, used to monitor and control the CPU
	 * time used by a resource context. {@link ResourceMonitoringService}
	 * implementations must create {@link CPUMonitor} instances for this
	 * resource type.
	 */
	public static final String	RES_TYPE_CPU			= "resource.type.cpu";

	/**
	 * The name of the disk storage resource type, used to monitor and control
	 * the size of the persistent storage used by a resource context.
	 * {@link ResourceMonitoringService} implementations must create
	 * {@link DiskStorageMonitor} instances for this resource type.
	 */
	public static final String	RES_TYPE_DISK_STORAGE	= "resource.type.disk.storage";

	/**
	 * The name of the memory resource type, used to monitor and control the
	 * size of the java heap used by a resource context.
	 * {@link ResourceMonitoringService} implementations must create
	 * {@link MemoryMonitor} instances for this resource type.
	 */
	public static final String	RES_TYPE_MEMORY			= "resource.type.memory";

	/**
	 * The name of the socket resource type, used to monitor and control the
	 * number of existing sockets used by a resource context.
	 * {@link ResourceMonitoringService} implementations must create
	 * {@link SocketMonitor} instances for this resource type.
	 */
	public static final String	RES_TYPE_SOCKET			= "resource.type.socket";

	/**
	 * The name of the special, optional resource context, representing the
	 * whole OSGi framework.
	 */
	public static final String	FRAMEWORK_CONTEXT_NAME	= "framework";

	/**
	 * The name of the Resource Context associated with System bundle (bundle
	 * 0).
	 */
	public static final String	SYSTEM_CONTEXT_NAME		= "system";

	/**
	 * Lists all available {@link ResourceContext resource contexts}. The list
	 * will contain the special {@link #FRAMEWORK_CONTEXT_NAME} context and the
	 * {@link #SYSTEM_CONTEXT_NAME} context, if it is supported.
	 * 
	 * @return An array of {@link ResourceContext} objects, or an empty array,
	 *         if no contexts have been created.
	 */
	public ResourceContext[] listContext();

	/**
	 * Creates a new {@link ResourceContext}.
	 * <p>
	 * A {@link ResourceContextEvent} with type
	 * {@link ResourceContextEvent#RESOURCE_CONTEXT_CREATED} will be sent.
	 * 
	 * @param name The name identifying the context. Names must be unique within
	 *        the framework instance.
	 * @param template If a template is provided, the new resource context will
	 *        inherit all resource monitoring settings (enabled monitors,
	 *        thresholds) from the template.
	 * @return A new {@link ResourceContext} instance.
	 * @throws IllegalArgumentException if a problem occurred, for example if the name
	 *         is already used.
	 */
	public ResourceContext createContext(String name, ResourceContext template);

	/**
	 * Returns the context with the specified resource context name.
	 * 
	 * @param name The resource context name
	 * @return An existing {@link ResourceContext} with the specified name, or
	 *         null if such a context doesn't exist
	 */
	public ResourceContext getContext(String name);

	/**
	 * Returns the {@link ResourceContext} associated to the provided bundle id.
	 * 
	 * @param bundleId bundle identifier
	 * @return the {@link ResourceContext} associated to bundle b or null if the
	 *         bundle b does not belong to a Resource Context.
	 */
	public ResourceContext getContext(long bundleId);

	/**
	 * Returns a list with the supported resource type names.
	 * 
	 * @return An array containing the names of all resource types that this
	 *         {@link ResourceMonitoringService} implementation supports.
	 */
	public String[] getSupportedTypes();
}
