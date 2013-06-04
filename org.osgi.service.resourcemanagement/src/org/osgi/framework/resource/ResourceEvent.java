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

import org.osgi.framework.Bundle;

/**
 * An event from the Resource Manager describing:
 * <ul>
 * <li>A Resource Threshold state change</li>
 * <li>A resource context lifecycle change</li>
 * <li>A resource context management operation</li>
 * </ul>
 * <p>
 * <code>ResourceEvent</code> objects are delivered synchronously to all
 * matching {@link ResourceListener} services. A type code is used to identify
 * the event.
 * 
 * @Immutable
 * @see ResourceListener
 */
public class ResourceEvent {

	/**
	 * Type of ResourceEvent indicating a {@link ResourceThreshold} instance
	 * goes to the NORMAL state.
	 */
	public static final int		NORMAL						= 0;

	/**
	 * Type of ResourceEvent indicating a {@link ResourceThreshold} instance
	 * goes to the WARNING state.
	 */
	public static final int		WARNING						= 0;

	/**
	 * Type of ResourceEvent indicating a {@link ResourceThreshold} instance
	 * goes to the ERROR state.
	 */
	public static final int		ERROR						= 1;

	/**
	 * A new {@link ResourceContext} has been created.
	 * <p>
	 * The {@link ResourceManager#createContext(String, ResourceContext)} method
	 * has been invoked.
	 */
	public static final int		RESOURCE_CONTEXT_CREATED	= 2;

	/**
	 * A {@link ResourceContext} has been removed
	 * <p>
	 * The {@link ResourceContext#removeContext(ResourceContext)} method has
	 * beem invoked
	 */
	public static final int		RESOURCE_CONTEXT_REMOVED	= 3;

	/**
	 * A bundle has been added to e {@link ResourceContext}
	 * <p>
	 * The {@link ResourceContext#addBundle(Bundle)} method has been invoked
	 */
	public static final int		BUNDLE_ADDED				= 4;

	/**
	 * A bundle has been removed from a {@link ResourceContext}
	 * <p>
	 * The {@link ResourceContext#removeBundle(Bundle, ResourceContext)} method
	 * has been invoked, or the bundle has been uninstalled
	 */
	public static final int		BUNDLE_REMOVED				= 5;

	private int					type;
	private ResourceMonitor		monitor;
	private ResourceContext		context;
	private ResourceThreshold	threshold;
	private String				previousState;
	private Bundle				bundle;

	/**
	 * Creates a resource threshold event
	 * 
	 * @param type The type of the event. Can be {@link #WARNING} or
	 *        {@link #ERROR}
	 * @param monitor The monitor that caused the event
	 */
	public ResourceEvent(int type, ResourceMonitor monitor) {
		this.type = type;
		this.monitor = monitor;
	}

	/**
	 * Creates a resource context lifecycle event.
	 * 
	 * @param type The type fo the event. Can be
	 *        {@link #RESOURCE_CONTEXT_CREATED} or
	 *        {@link #RESOURCE_CONTEXT_REMOVED}
	 * @param context The resource context which had a lifecycle change
	 */
	public ResourceEvent(int type, ResourceContext context) {
		this.type = type;
		this.context = context;
	}

	/**
	 * Creates a resource context management event
	 * 
	 * @param type The type of the event. Can be {@link #BUNDLE_ADDED} or
	 *        {@link #BUNDLE_REMOVED}
	 * @param context The context which had management operation
	 * @param bundle The bundle that was added to or removed from the context
	 */
	public ResourceEvent(int type, ResourceContext context, Bundle bundle) {
		this.type = type;
		this.context = context;
		this.bundle = bundle;
	}

	/**
	 * Returns the event type. The type values are:
	 * <ul>
	 * <li>{@link #NORMAL}</li>
	 * <li>{@link #WARNING}</li>
	 * <li>{@link #ERROR}</li>
	 * <li>{@link #RESOURCE_CONTEXT_CREATED}</li>
	 * <li>{@link #RESOURCE_CONTEXT_REMOVED}</li>
	 * <li>{@link #BUNDLE_ADDED}</li>
	 * <li>{@link #BUNDLE_REMOVED}</li>
	 * </ul>
	 * 
	 * @return The event type
	 */
	public int getType() {
		return type;
	}

	/**
	 * Returns the monitor that caused the event. Relevant only for event types
	 * {@link #WARNING} and {@link #ERROR}
	 * 
	 * @return The monitor that cased the event, or null if a resource monitor
	 *         is not relevant
	 */
	public ResourceMonitor getMonitor() {
		return monitor;
	}

	/**
	 * Returns the resource context that caused the event.
	 * 
	 * @return The resource context that cased the event.
	 */
	public ResourceContext getContext() {
		return context;
	}

	/**
	 * Returns the bundle that caused the event. Relevant only for event types
	 * {@link #BUNDLE_ADDED} and {@link #BUNDLE_REMOVED}
	 * 
	 * @return The bundle that cased the event, or null if a bundle is not
	 *         relevant
	 */
	public Bundle getBundle() {
		return bundle;
	}

	/**
	 * Returns the previous state of the {@link ResourceThreshold} generating
	 * this event. This method should be called on when {@link #getType()}
	 * returns either {@link #NORMAL}, or {@link #WARNING} or {@link #ERROR}.
	 * 
	 * @return previous state or null
	 * @see #getResourceThreshold()
	 */
	public String getPreviousState() {
		return previousState;
	}

	/**
	 * Returns the {@link ResourceThreshold} instance which generates this
	 * event.This method should be called on when {@link #getType()} returns
	 * either {@link #NORMAL}, or {@link #WARNING} or {@link #ERROR}. The
	 * returned value is a snapshot of the ResourceThreshold instance at the
	 * moment where the event was generated.
	 * 
	 * @return a snapshot of the {@link ResourceThreshold} instance or null.
	 * @see #getPreviousState()
	 */
	public ResourceThreshold getResourceThreshold() {
		return threshold;
	}
}
