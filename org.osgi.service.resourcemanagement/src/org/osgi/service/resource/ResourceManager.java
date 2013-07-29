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

import org.osgi.framework.Bundle;
import org.osgi.service.resource.resourcemonitor.CPUMonitor;
import org.osgi.service.resource.resourcemonitor.DiskStorageMonitor;
import org.osgi.service.resource.resourcemonitor.MemoryMonitor;
import org.osgi.service.resource.resourcemonitor.SocketMonitor;
import org.osgi.service.resource.resourcemonitor.ThreadMonitor;

/**
 * Monitor and manage resource contexts.
 * 
 * <p>
 * The ResourceManager object can be obtained by calling
 * {@link Bundle#adapt(Class) bundle.adapt(ResourceManager.class)} on the system
 * bundle. Only the system bundle can be adapted to a ResourceManager object.
 * 
 * @ThreadSafe
 * @noimplement
 */
public interface ResourceManager {

	/**
	 * The name of the threads resource type, used to monitor and control the
	 * number of threads created by a resource context.
	 * <code>ResourceManager</code> implementations must create
	 * {@link ThreadMonitor} instances for this resource type.
	 */
	public static final String RES_TYPE_THREADS = "resource.type.threads";

	/**
	 * The name of the CPU resource type, used to monitor and control the CPU
	 * time used by a resource context. <code>ResourceManager</code>
	 * implementations must create {@link CPUMonitor} instances for this
	 * resource type.
	 */
	public static final String RES_TYPE_CPU = "resource.type.cpu";

	/**
	 * The name of the disk storage resource type, used to monitor and control
	 * the size of the persistent storage used by a resource context.
	 * <code>ResourceManager</code> implementations must create
	 * {@link DiskStorageMonitor} instances for this resource type.
	 */
	public static final String RES_TYPE_DISK_STORAGE = "resource.type.disk.storage";

	/**
	 * The name of the memory resource type, used to monitor and control the
	 * size of the java heap used by a resource context.
	 * <code>ResourceManager</code> implementations must create
	 * {@link MemoryMonitor} instances for this resource type.
	 */
	public static final String RES_TYPE_MEMORY = "resource.type.memory";

	/**
	 * The name of the socket resource type, used to monitor and control the
	 * number of existing sockets used by a resource context.
	 * <code>ResourceManager</code> implementations must create
	 * {@link SocketMonitor} instances for this resource type.
	 */
	public static final String RES_TYPE_SOCKET = "resource.type.socket";

	/**
	 * The name of the special, optional resource context, representing the
	 * whole OSGi framework.
	 */
	public static final String FRAMEWORK_CONTEXT_NAME = "framework";

	/**
	 * The name of the Resource Context associated with System bundle (bundle
	 * 0).
	 */
	public static final String SYSTEM_CONTEXT_NAME = "system";

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
	 * A {@link ResourceEvent} with type
	 * {@link ResourceEvent#RESOURCE_CONTEXT_CREATED} will be sent.
	 * 
	 * @param name
	 *            The name identifying the context. Names must be unique within
	 *            the framework instance.
	 * @param template
	 *            If a template is provided, the new resource context will
	 *            inherit all resource monitoring settings (enabled monitors,
	 *            thresholds) from the template.
	 * @return A new {@link ResourceContext} instance.
	 */
	public ResourceContext createContext(String name, ResourceContext template);

	/**
	 * Returns the context with the specified name
	 * 
	 * @param name
	 *            The resource context name
	 * @return An existing {@link ResourceContext} with the specified name, or
	 *         null if such a context doesn't exist
	 */
	public ResourceContext getContext(String name);

	/**
	 * Returns the resource context associated with the current thread.
	 * 
	 * @return The {@link ResourceContext} associated with the current thread,
	 *         or null if the thread is not associated with a resource context
	 */
	public ResourceContext getCurrentContext();

	/**
	 * Explicitly associates the current thread with a resource context. All
	 * further resource allocations made by this thread will be assigned to this
	 * resource context.
	 * <p>
	 * To remove the explicit resource context association for the current, call
	 * this method with a <code>null</code> parameter.
	 * 
	 * @param context
	 *            The {@link ResourceContext} to associate the current threat
	 *            with, or null to remove an explicit association.
	 * @return The {@link ResourceContext} that this thread was previously
	 *         explicitly associated with, or <code>null</code> if there was no
	 *         such association.
	 */
	public ResourceContext switchCurrentContext(ResourceContext context);

	/**
	 * Returns the resource context explicitly associated with this thread.
	 * 
	 * @param t
	 *            The thread object for which the associated resource context
	 *            must be returned
	 * @return The {@link ResourceContext} explicitly associated with the
	 *         specified thread, or <code>null</code> if there is no such
	 *         association.
	 */
	public ResourceContext getContext(Thread t);

	/**
	 * Returns a list with the supported resource type names.
	 * 
	 * @return An array containing the names of all resource types that this
	 *         ResourceManager implementation supports.
	 */
	public String[] getSupportedTypes();
}