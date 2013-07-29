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
	public static final int	NORMAL						= 0;

	/**
	 * Type of ResourceEvent indicating a {@link ResourceThreshold} instance
	 * goes to the WARNING state.
	 */
	public static final int	WARNING						= 1;

	/**
	 * Type of ResourceEvent indicating a {@link ResourceThreshold} instance
	 * goes to the ERROR state.
	 */
	public static final int	ERROR						= 2;

	/**
	 * A new {@link ResourceContext} has been created.
	 * <p>
	 * The {@link ResourceManager#createContext(String, ResourceContext)} method
	 * has been invoked.
	 */
	public static final int	RESOURCE_CONTEXT_CREATED	= 3;

	/**
	 * A {@link ResourceContext} has been removed
	 * <p>
	 * The {@link ResourceContext#removeContext(ResourceContext)} method has
	 * beem invoked
	 */
	public static final int	RESOURCE_CONTEXT_REMOVED	= 4;

	/**
	 * A bundle has been added to e {@link ResourceContext}
	 * <p>
	 * The {@link ResourceContext#addBundle(Bundle)} method has been invoked
	 */
	public static final int	BUNDLE_ADDED				= 5;

	/**
	 * A bundle has been removed from a {@link ResourceContext}
	 * <p>
	 * The {@link ResourceContext#removeBundle(Bundle, ResourceContext)} method
	 * has been invoked, or the bundle has been uninstalled
	 */
	public static final int	BUNDLE_REMOVED				= 6;

	private int				type;
	private ResourceContext	context;
	private String			resourceType;
	private Object			thresholdValue;
	private boolean			isUpperThreshold;
	private Object			value;
	private int				previousState;
	private Bundle			bundle;

	/**
	 * Creates a resource threshold event
	 * 
	 * @param type The type of the event. Can be {@link #NORMAL},
	 *        {@link #WARNING} or {@link #ERROR}
	 * @param resourceType the type of the Resource Monitor triggering this
	 *        event.
	 * @param value the resource consumption value at the moment when the event
	 *        was generated
	 * @param thresholdValue the threshold value (given by the
	 *        {@link ResourceThreshold} instance triggering this event)
	 * @param isUpperThreshold true if the {@link ResourceThreshold} instance
	 *        (triggering this event) is a upper threshold.
	 */
	public ResourceEvent(int type, String resourceType, Object value, Object thresholdValue, boolean isUpperThreshold) {
		this.type = type;
		this.resourceType = resourceType;
		this.value = value;
		this.thresholdValue = thresholdValue;
		this.isUpperThreshold = isUpperThreshold;
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
	 * Returns the resource consumption value. Relevant only for event types
	 * {@link #NORMAL}, {@link #WARNING} and {@link #ERROR}.
	 * 
	 * @return the resource consumption value, or null if a resource monitor is
	 *         not relevant
	 */
	public Object getValue() {
		return value;
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
	public int getPreviousState() {
		return previousState;
	}

	/**
	 * Returns the threshold value. This threshold value is the same at the one
	 * at the moment when this event was generated. This method is only used
	 * when {@link #getType()} returns {@link #NORMAL}, {@link #WARNING} or
	 * {@link #ERROR}.
	 * 
	 * @return a snapshot of the {@link ResourceThreshold} instance or null.
	 * @see #getPreviousState()
	 */
	public Object getThresholdValue() {
		return thresholdValue;
	}

	/**
	 * Returns true if the {@link ResourceThreshold} triggering this event is an
	 * upper threshold. This method is only used when {@link #getType()} returns
	 * {@link #NORMAL}, {@link #WARNING} or {@link #ERROR}.
	 * 
	 * 
	 * @return true if it is an upper threshold.
	 */
	public boolean isUpperThreshold() {
		return isUpperThreshold;
	}

	/**
	 * Get the type of resource (i.e the type of the {@link ResourceMonitor}
	 * instance triggering this event).This method is only used when
	 * {@link #getType()} returns {@link #NORMAL}, {@link #WARNING} or
	 * {@link #ERROR}. The listener can use this value to retrieve the related
	 * {@link ResourceMonitor} instance by calling {@link #getContext()}.
	 * {@link ResourceContext#getMonitor(String) getMonitor(getResourceType())}
	 * 
	 * @return resource type
	 */
	public String getResourceType() {
		return resourceType;
	}
}
