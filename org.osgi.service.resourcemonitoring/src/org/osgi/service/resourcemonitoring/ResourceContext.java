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

package org.osgi.service.resourcemonitoring;

import org.osgi.framework.Bundle;

/**
 * Logical entity for resource accounting. A resource context has a group of
 * member bundles, and a bundle can be a member of 0 or 1 resource context.
 * 
 * <p>
 * Management agents can use the
 * {@link ResourceMonitoringService#createContext(String, ResourceContext)} method to
 * create ResourceContext instances.
 * </p>
 * <p>
 * Management agents can use the {@link #getMonitor(String)} method to get
 * {@link ResourceMonitor} instances for the supported resource types. These
 * instances can then be used to monitor the usage of the resources, or the set
 * usage limits.
 * </p>
 * <p>
 * ResourceContexts are retrieved through the {@link ResourceMonitoringService} OSGi
 * service.
 * </p>
 * 
 * @author $Id$
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
	 * Returns the bundle identfiers belonging to this Resource Context.
	 * 
	 * @return An array of {@link Bundle} objects, or an empty array if no
	 *         bundles are currently members of this context
	 */
	public long[] getBundleIds();

	/**
	 * Adds a bundle to the resource context. The bundle will be a member of the
	 * context until it is uninstalled, or explicitly removed from the context
	 * with {@link #removeBundle(long)} method or
	 * {@link #removeBundle(long, ResourceContext)} method.
	 * <p>
	 * Resources previously allocated by this bundle (in another resource
	 * context) will not be moved to this resource context. The change applies
	 * only for future allocations.
	 * <p>
	 * A {@link ResourceContextEvent} with type
	 * {@link ResourceContextEvent#BUNDLE_ADDED} will be sent.
	 * 
	 * @param bundleId The bundle to add to this resource context
	 */
	public void addBundle(long bundleId);

	/**
	 * Removes the bundle identified by bundleId from the Resource Context. The
	 * bundle is no longer to this Resource Context.
	 * 
	 * 
	 * @param bundleId bundle identifier
	 */
	public void removeBundle(long bundleId);

	/**
	 * Removes the bundle from this resource context. If a
	 * <code>destination</code> context is specified, the bundle will be added
	 * in it.
	 * <p>
	 * Resources previously allocated by this bundle will not be removed from
	 * the resource context. The change applies only for future allocations.
	 * <p>
	 * A {@link ResourceContextEvent} with type
	 * {@link ResourceContextEvent#BUNDLE_REMOVED} will be sent.
	 * 
	 * @param bundleId the identifier of the bundle to be removed from the
	 *        Resource Context
	 * @param destination A resource context in which to add the bundle, after
	 *        removing it from this context. If no destination is provided (i.e.
	 *        null), the bundle is not associated to a new Resource Context.
	 */
	public void removeBundle(long bundleId, ResourceContext destination);

	/**
	 * Returns a ResourceMonitor instance for the specified resource type. If
	 * the {@link ResourceMonitoringService} implementation does not support
	 * this resource type, null is returned
	 * 
	 * @param resourceType The resource type, for which a resource monitor is
	 *        requested
	 * @return A ResourceMonitor instance, or null, if this resource type is not
	 *         supported
	 */
	public ResourceMonitor getMonitor(String resourceType);

	/**
	 * Retrieves all the existing ResourceMonitor belonging to this context.
	 * 
	 * @return an array of ResourceMonitor. May be empty if no ResourceMonitor
	 */
	public ResourceMonitor[] getMonitors();

	/**
	 * Adds a new ResourceMonitor instance monitoring resource for this resource
	 * context. This method should be called only by ResourceMonitorFactory
	 * instance.
	 * 
	 * @param resourceMonitor resourceMonitor instance to be added
	 * @throws ResourceMonitorException if resourceMonitor is associated to
	 *         another context or resourceMonitor has been deleted.
	 */
	public void addResourceMonitor(ResourceMonitor resourceMonitor) throws ResourceMonitorException;

	/**
	 * Removes a ResourceMonitor instance from the context.
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
	 * A {@link ResourceContextEvent} with type
	 * {@link ResourceContextEvent#RESOURCE_CONTEXT_REMOVED} will be sent.
	 * 
	 * @param destination The {@link ResourceContext} where the resources
	 *        currently allocated by this resource context will be moved.
	 */
	public void removeContext(ResourceContext destination);

	/**
	 * A ResourceContext rc1 is equals to ResourceContext rc2 if rc1.getName()
	 * is equals to rc2.getName().
	 * 
	 * @param resourceContext resource context
	 * @return true if getName().equals(resourceContext.getName()
	 */
	public boolean equals(Object resourceContext);

	/**
	 * Retrieves the hashCode value of a ResourceContext. The hashCode value of
	 * a ResourceContext is only based on the hashcode value of the name of the
	 * context.
	 * 
	 * @return hashcode
	 */
	public int hashCode();
}
