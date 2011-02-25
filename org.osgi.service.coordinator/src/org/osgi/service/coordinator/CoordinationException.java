/*
 * Copyright (c) OSGi Alliance (2010, 2011). All Rights Reserved.
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
package org.osgi.service.coordinator;

/**
 * Unchecked exception which may be thrown by a Coordinator implementation.
 * 
 * @version $Id$
 */
public class CoordinationException extends RuntimeException {
	private static final long	serialVersionUID	= 1L;

	/**
	 * Unknown reason for this exception.
	 */
	public final static int		UNKNOWN				= 0;

	/**
	 * Registering a Participant with a Coordination would have resulted in a
	 * deadlock.
	 */
	public final static int		DEADLOCK_DETECTED	= 1;

	/**
	 * The Coordination has terminated as a failure with
	 * {@link Coordination#fail(Throwable)}. When this exception type is used,
	 * the {@link #getCause()} method must return a non-null value.
	 */
	public final static int		FAILED				= 2;

	/**
	 * The Coordination has partially ended.
	 */
	public final static int		PARTIALLY_ENDED		= 3;

	/**
	 * The Coordination has already terminated normally.
	 */
	public final static int		ALREADY_ENDED		= 4;

	/**
	 * The Coordination was already on a thread's thread local Coordination
	 * stack.
	 */
	public final static int		ALREADY_PUSHED		= 5;

	/**
	 * The current thread was interrupted while waiting to register a
	 * Participant with a Coordination.
	 */
	public final static int		LOCK_INTERRUPTED	= 6;

	/**
	 * The Coordination cannot be ended by the calling thread since the
	 * Coordination is on the thread local Coordination stack of another thread.
	 */
	public final static int		WRONG_THREAD		= 7;

	private final String		name;
	private final int			type;
	private final long			id;

	/**
	 * Create a new Coordination Exception with a cause.
	 * 
	 * @param message The detail message for this exception.
	 * @param coordination The Coordination associated with this exception.
	 * @param cause The cause associated with this exception.
	 * @param type The type of this exception.
	 */
	public CoordinationException(String message, Coordination coordination,
			int type, Throwable cause) {
		super(message, cause);
		this.type = type;
		if (coordination == null) {
			this.id = -1L;
			this.name = "<>";
		}
		else {
			this.id = coordination.getId();
			this.name = coordination.getName();
		}
		if ((type == FAILED) && (cause == null)) {
			throw new IllegalArgumentException(
					"A cause must be specified for type FAILED");
		}
	}

	/**
	 * Create a new Coordination Exception.
	 * 
	 * @param message The detail message for this exception.
	 * @param coordination The Coordination associated with this exception.
	 * @param type The type of this exception.
	 */
	public CoordinationException(String message, Coordination coordination,
			int type) {
		super(message);
		this.type = type;
		if (coordination == null) {
			this.id = -1L;
			this.name = "<>";
		}
		else {
			this.id = coordination.getId();
			this.name = coordination.getName();
		}
		if (type == FAILED) {
			throw new IllegalArgumentException(
					"A cause must be specified for type FAILED");
		}
	}

	/**
	 * Returns the name of the {@link Coordination} associated with this
	 * exception.
	 * 
	 * @return The name of the Coordination associated with this exception or
	 *         {@code "<>"} if no Coordination is associated with this
	 *         exception.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the type for this exception.
	 * 
	 * @return The type of this exception.
	 */
	public int getType() {
		return type;
	}

	/**
	 * Returns the id of the {@link Coordination} associated with this
	 * exception.
	 * 
	 * @return The id of the Coordination associated with this exception or
	 *         {@code -1} if no Coordination is associated with this exception.
	 */

	public long getId() {
		return id;
	}
}
