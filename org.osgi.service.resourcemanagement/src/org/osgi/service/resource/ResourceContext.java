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

/**
 * Logical entity for resource accounting. A resource context has a group of
 * member bundles, and a bundle can be a member of 1 or 0 resource contexts.
 * 
 * <p>
 * Management agents can use the
 * {@link ResourceManager#createContext(String, ResourceContext)} method to
 * create ResourceContext instances.
 * <p>
 * Management agents can use the {@link #getMonitor(String)} method to get
 * {@link ResourceMonitor} instances for the supported resource types. These
 * instances can then be used to monitor the usage of the resources, or the set
 * usage limits.
 * <p>
 * The ResourceContext object associated with a bundle can be obtained by
 * calling {@link Bundle#adapt(Class) bundle.adapt(ResourceContext.class)} on
 * the bundle.
 * 
 * @ThreadSafe
 * @noimplement
 */
public interface ResourceContext {

	/**
	 * Returns the name of the resource context. Resource context names are
	 * unique within a framework instance.
	 * 
	 * @return The resource context name
	 */
	public String getName();

	/**
	 * Returns the list of bundles that are members of this resource context.
	 * 
	 * @return An array of {@link Bundle} objects, or an empty array if no
	 *         bundles are currently members of this context
	 */
	public Bundle[] getBundles();

	/**
	 * Adds a bundle to the resource context. The bundle will be a member of the
	 * context until it is uninstalled, or explicitly removed from the context
	 * with the {@link #removeBundle(Bundle, ResourceContext)} method.
	 * <p>
	 * Resources previously allocated by this bundle (in another resource
	 * context) will not be moved to this resource context. The change applies
	 * only for future allocations.
	 * <p>
	 * A {@link ResourceEvent} with type {@link ResourceEvent#BUNDLE_ADDED} will
	 * be sent.
	 * 
	 * @param b The bundle to add to this resource context
	 */
	public void addBundle(Bundle b);

	/**
	 * Removes the bundle from this resource context. If a
	 * <code>destination</code> context is specified, the bundle will be added
	 * in it.
	 * <p>
	 * Resources previously allocated by this bundle will not be removed from
	 * the resource context. The change applies only for future allocations.
	 * <p>
	 * A {@link ResourceEvent} with type {@link ResourceEvent#BUNDLE_REMOVED}
	 * will be sent.
	 * 
	 * @param b The bundle to remove from this resource context
	 * @param destination A resource context in which to add the bundle, after
	 *        removing it from this context.
	 */
	public void removeBundle(Bundle b, ResourceContext destination);

	/**
	 * Returns a ResourceMonitor instance for the specified resource type. If
	 * the ResourceManager implementation does not support this resource type,
	 * null is returned
	 * 
	 * @param resourceType The resource type, for which a resource monitor is
	 *        requested
	 * @return A ResourceMonitor instance, or null, if this resource type is not
	 *         supported
	 */
	public ResourceMonitor getMonitor(String resourceType);

	/**
	 * Add a new ResourceMonitor instance monitoring resource for this resource
	 * context. This method should be called only by ResourceMonitorFactory
	 * instance.
	 * 
	 * @param resourceMonitor resourceMonitor instance to be added
	 * @throws ResourceMonitorException if resourceMonitor is associated to
	 *         another context or resourceMonitor has been deleted.
	 */
	public void addResourceMonitor(ResourceMonitor resourceMonitor) throws ResourceMonitorException;

	/**
	 * Remove a ResourceMonitor instance from the context.
	 * 
	 * @param resourceMonitor resource monitor instance to be removed
	 */
	public void removeResourceMonitor(ResourceMonitor resourceMonitor);

	/**
	 * Removes a resource context. All resources allocated in this resource
	 * context will be moved to the <code>destination</code> context. If
	 * <code>destination</code> is <code>null</code>, these resources will no
	 * longer be monitored.
	 * <p>
	 * A {@link ResourceEvent} with type
	 * {@link ResourceEvent#RESOURCE_CONTEXT_REMOVED} will be sent.
	 * 
	 * @param destination The {@link ResourceContext} where the resources
	 *        currently allocated by this resource context will be moved.
	 */
	public void removeContext(ResourceContext destination);
}
