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
 * A Resource Context Event instance is an event sent to Resource Context
 * Listener instances through a call to
 * {@link ResourceContextListener#notify(ResourceContextEvent)} method.
 * 
 * A Resource Context Event has a type among the four following ones:
 * <ul>
 * <li>{@link #RESOURCE_CONTEXT_CREATED} – A new Resource Context instance has
 * been created.</li>
 * <li>{@link #RESOURCE_CONTEXT_REMOVED} – A Resource Context instance has been
 * deleted.</li>
 * <li>{@link #BUNDLE_ADDED} – A bundle has been added in the scope of a
 * Resource Context instance.</li>
 * <li>{@link #BUNDLE_REMOVED} – A bundle has been removed from the scope of a
 * Resource Context instance.</li>
 * </ul>
 * 
 * @version 1.0
 * @author $Id$
 */
public class ResourceContextEvent {

	/**
	 * A new {@link ResourceContext} has been created.
	 * <p>
	 * The
	 * {@link ResourceMonitoringService#createContext(String, ResourceContext)}
	 * method has been invoked.
	 */
	public static final int			RESOURCE_CONTEXT_CREATED	= 0;

	/**
	 * A {@link ResourceContext} has been removed
	 * <p>
	 * The {@link ResourceContext#removeContext(ResourceContext)} method has
	 * been invoked
	 */
	public static final int			RESOURCE_CONTEXT_REMOVED	= 1;

	/**
	 * A bundle has been added to e {@link ResourceContext}
	 * <p>
	 * The {@link ResourceContext#addBundle(long)} method has been invoked
	 */
	public static final int			BUNDLE_ADDED				= 2;

	/**
	 * A bundle has been removed from a {@link ResourceContext}
	 * <p>
	 * {@link ResourceContext#removeBundle(long)} method or
	 * {@link ResourceContext#removeBundle(long, ResourceContext)} method have
	 * been invoked, or the bundle has been uninstalled
	 */
	public static final int			BUNDLE_REMOVED				= 3;

	/**
	 * event type.
	 */
	private final int				type;

	/**
	 * bundle.
	 */
	private final long				bundleId;

	/**
	 * context.
	 */
	private final ResourceContext	context;

	/**
	 * Create a new ResourceContextEvent. This constructor should be used when
	 * the type of the event is either {@link #RESOURCE_CONTEXT_CREATED} or
	 * {@link #RESOURCE_CONTEXT_REMOVED}.
	 * 
	 * @param pType event type
	 * @param pResourceContext context
	 */
	public ResourceContextEvent(final int pType, final ResourceContext pResourceContext) {
		type = pType;
		context = pResourceContext;
		bundleId = -1;
	}

	/**
	 * Create a new ResourceContextEvent. This constructor should be used when
	 * the type of the event is either {@link #BUNDLE_ADDED} or
	 * {@link #BUNDLE_REMOVED}.
	 * 
	 * @param pType event type
	 * @param pResourceContext context
	 * @param pBundleId bundle
	 */
	public ResourceContextEvent(final int pType, final ResourceContext pResourceContext, final long pBundleId) {
		type = pType;
		context = pResourceContext;
		bundleId = pBundleId;
	}

	/**
	 * Retrieves the type of this Resource Context Event.
	 * 
	 * @return the type of the event. One of:
	 *         <ul>
	 *         <li>{@link #RESOURCE_CONTEXT_CREATED}</li>
	 *         <li>{@link #RESOURCE_CONTEXT_REMOVED}</li>
	 *         <li>{@link #BUNDLE_ADDED}</li>
	 *         <li>{@link #BUNDLE_REMOVED}</li>
	 *         </ul>
	 * 
	 */
	public int getType() {
		return type;
	}

	/**
	 * Retrieves the Resource Context associated to this event
	 * 
	 * @return Resource Context.
	 */
	public ResourceContext getContext() {
		return context;
	}

	/**
	 * <p>
	 * Retrieves the identifier of the bundle being added to or removed from the
	 * Resource Context.
	 * 
	 * <p>
	 * This method returns a valid value only when {@link #getType()} returns:
	 * <ul>
	 * <li>{@link #BUNDLE_ADDED}</li>
	 * <li>{@link #BUNDLE_REMOVED}</li>
	 * </ul>
	 * 
	 * @return the bundle id or -1 (invalid value).
	 */
	public long getBundleId() {
		return bundleId;
	}

	@Override
	public String toString() {
		return super.toString();
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object var0) {
		if (var0 == null) {
			return false;
		}
		if (!(var0 instanceof ResourceContextEvent)) {
			return false;
		}
		ResourceContextEvent event = (ResourceContextEvent) var0;
		if (event.getBundleId() != getBundleId()) {
			return false;
		}
		if (event.getContext() == null) {
			return false;
		}
		if (!event.getContext().equals(getContext())) {
			return false;
		}
		return true;
	}

}
