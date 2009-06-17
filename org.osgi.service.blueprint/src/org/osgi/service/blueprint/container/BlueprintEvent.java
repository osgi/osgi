/*
 * Copyright (c) OSGi Alliance (2008, 2009). All Rights Reserved.
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
package org.osgi.service.blueprint.container;

import org.osgi.framework.Bundle;

/**
 * A Blueprint Event.
 *
 * <p>
 * <code>BlueprintEvent</code> objects are delivered to all registered <code>BlueprintListener</code>
 * service objects. BlueprintEvents must be asynchronously delivered in chronological order
 * with respect to each listener.
 *
 * <p>
 * In addition, when a listener is registered, the blueprint extender
 * will synchronously send to this listener the last event for each blueprint bundle managed
 * by this extender.  This replay of events is designed so that the new listener can
 * be informed of the state of each managed bundle.  Events sent during this replay will
 * have the {@link #replay} flag set. The blueprint extender must ensure that this replay phase
 * does not interfere with new events so that the chronological order of all events received
 * by the listener is preserved. If the last event for a given blueprint bundle is DESTROYED,
 * the extender must not send it during this replay phase.
 *
 * <p>
 * A type code is used to identify the type of event. The following event types are defined:
 * <ul>
 * <li>{@link #CREATING}
 * <li>{@link #CREATED}
 * <li>{@link #DESTROYING}
 * <li>{@link #DESTROYED}
 * <li>{@link #FAILURE}
 * <li>{@link #GRACE_PERIOD}
 * <li>{@link #WAITING}
 * </ul>
 *
 * <p>
 * <h2>Blueprint Events and EventAdmin</h2>
 * In addition to calling the registered {@link BlueprintListener}s, the blueprint extender
 * must also send those events to the EventAdmin service, if it is available. <br/>
 * See {@link EventConstants} for more informations.
 *
 * @see BlueprintListener
 * @see EventConstants
 */
public class BlueprintEvent {

	/**
	 * The Blueprint extender has started creating a Blueprint Container for the bundle.
	 */
	public static final int CREATING = 1;
	/**
	 * The Blueprint extender has creating a Blueprint Container for the bundle.
	 * This event is sent after the BlueprintContainer service has been registered.
	 */
	public static final int CREATED = 2;
	/**
	 * The Blueprint extender has started detroying the Blueprint Container for the bundle.
	 */
	public static final int DESTROYING = 3;
	/**
	 * The Blueprint Container for the bundle has been completely destroyed.
	 * This event is sent after the BlueprintContainer service has been unregistered.
	 */
	public static final int DESTROYED = 4;
	/**
	 * The Blueprint Container creation for the bundle has failed.
	 * If this event is sent after a timeout in the Grace Period, the {@link #getDependencies()}
	 * method must return an array of missing mandatory dependencies.  The event must also contain
	 * the cause of the failure as a <code>Throwable</code> through the {@link #getException()} method.
	 */
	public static final int FAILURE = 5;
	/**
	 * The Blueprint Container has entered the Grace Period.
	 * The list of missing dependencies must be made available through the {@link #getDependencies()}
 	 * method.  During the grace period, a GRACE_PERIOD event is sent each time the set of
	 * unsatisfied dependencies changes.
	 */
	public static final int GRACE_PERIOD = 6;
	/**
	 * The Blueprint Extender is waiting on the availability of a service to satisfy an
	 * invocation on a referenced service.
	 * The missing dependency must be made available through the {@link #getDependencies()}
	 * method which will return an array containing one filter object as a String.
	 */
	public static final int WAITING = 7;


	/**
	 * Type of this event.
	 *
	 * @see #getType()
	 */
	private final int type;
	/**
	 * The time when the event occured.
	 *
	 * @see #getTimestamp()
	 */
	private final long timestamp;
	/**
	 * The blueprint bundle.
	 *
	 * @see #getBundle()
	 */
	private final Bundle bundle;
	/**
 	 * The blueprint extender bundle.
 	 *
 	 * @see #getExtenderBundle()
 	 */
	private final Bundle extenderBundle;
	/**
	 * An array containing filters identifying the missing dependencies.
	 *
	 * @see #getDependencies()
	 */
	private final String[] dependencies;
	/**
	 * Cause of the failure.
	 *
	 * @see #getException()
	 */
	private final Throwable exception;
	/**
 	 * Indicate if this event is a replay event or not.
	 *
	 * @see #isReplay()
	 */
	private final boolean replay;


	/**
	 * Create a simple BlueprintEvent object.
	 *
	 * @param type   The type of the event.
	 * @param bundle The managed bundle this event is originating from.
	 * @param extenderBundle
	 *               The bundle of the blueprint extender that is generating
	 *               the event.
	 */
	public BlueprintEvent(int type, Bundle bundle, Bundle extenderBundle) {
		this(type, bundle, extenderBundle, null, null);
	}


	/**
	 * Create a BlueprintEvent object associated with a
	 * set of dependencies.
	 *
	 * @param type   The type of the event.
	 * @param bundle The managed bundle this event is originating from.
	 * @param extenderBundle
	 *               The bundle of the blueprint extender that is generating
	 *               the event.
	 * @param dependencies
	 *               An array of String filters for each dependency associated
	 *               with this event.
	 */
	public BlueprintEvent(int type, Bundle bundle, Bundle extenderBundle, String[] dependencies) {
		this(type, bundle, extenderBundle, dependencies, null);
	}


	/**
	 * Create a BlueprintEvent object associated with a
	 * failure cause.
	 *
	 * @param type      The type of the event.
	 * @param bundle    The managed bundle this event is originating from.
	 * @param extenderBundle
	 *                  The bundle of the blueprint extender that is generating
	 *                  the event.
	 * @param exception A Throwable object describing the root cause of the
	 *                  event.
	 */
	public BlueprintEvent(int type, Bundle bundle, Bundle extenderBundle, Throwable exception) {
		this(type, bundle, extenderBundle, null, exception);
	}


	/**
	 * Create a BlueprintEvent object associated with a
	 * failure cause and related to a set of dependencies.
	 *
	 * @param type      The type of the event.
	 * @param bundle    The managed bundle this event is originating from.
	 * @param extenderBundle
	 *                  The bundle of the blueprint extender that is generating
	 *                  the event.
	 * @param dependencies
	 *               An array of String filters for each dependency associated
	 *               with this event.
	 * @param exception A Throwable object describing the root cause of the
	 *                  event.
	 */
	public BlueprintEvent(int type, Bundle bundle, Bundle extenderBundle, String[] dependencies, Throwable exception) {
		this.type = type;
		this.timestamp = System.currentTimeMillis();
		this.bundle = bundle;
		this.extenderBundle = extenderBundle;
		this.dependencies = dependencies;
		this.exception = exception;
		this.replay = false;
	}

	/**
 	 * Create a new blueprint event from the given blueprint event.
 	 * The timestamp property will be copied from the original event and only the
 	 * replay property will be overriden with the given value.
 	 *
	 * @param event the original event to copy
	 * @param replay if the copied event should be used as a replay event
	 */
	public BlueprintEvent(BlueprintEvent event, boolean replay) {
		this.type = event.type;
		this.timestamp = event.timestamp;
		this.bundle = event.bundle;
		this.extenderBundle = event.extenderBundle;
		this.dependencies = event.dependencies;
		this.exception = event.exception;
		this.replay = replay;
	}

	/**
	 * Return the type of this event.
	 * <p>
	 * The type values are:
	 * <ul>
	 * <li>{@link #CREATING}
	 * <li>{@link #CREATED}
	 * <li>{@link #DESTROYING}
	 * <li>{@link #DESTROYED}
	 * <li>{@link #FAILURE}
	 * <li>{@link #GRACE_PERIOD}
	 * <li>{@link #WAITING}
	 * </ul>
	 *
	 * @return The type of this event.
	 */
	public int getType() {
		return type;
	}

	/**
	 * Return the time at which this event occured.
	 *
	 * @return The time at which this event occured.
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * Return the blueprint bundle.
	 *
	 * @return The blueprint bundle.  Never <code>null</code>.
	 */
	public Bundle getBundle() {
		return bundle;
	}

	/**
	 * Return the Bundle of the blueprint extender.
	 *
	 * @return The Bundle of the blueprint extender.  Never <code>null</code>.
	 */
	public Bundle getExtenderBundle() {
		return extenderBundle;
	}

	/**
	 * Return the filters identifying the missing dependencies that caused this event.
	 * <p>
	 * This field is only valid for {@link #WAITING},
	 * {@link #GRACE_PERIOD} and {@link #FAILURE}
	 * events.
	 *
	 * @return The missing dependencies informations.  May be <code>null</code>.
	 */
	public String[] getDependencies() {
		return dependencies;
	}

	/**
	 * Return the cause for a {@link #FAILURE} event.
	 *
	 * @return The cause of the failure.  May be <code>null</code>.
	 */
	public Throwable getException() {
		return exception;
	}

	/**
 	 * Return the fact that this event is a replay event or not.
	 *
	 * @return a boolean indicating if this event is a replay event.
	 */
	public boolean isReplay() {
		return replay;
	}

}
