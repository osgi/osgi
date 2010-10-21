/*
 * Copyright (c) OSGi Alliance (2000, 2008). All Rights Reserved.
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
 * Thrown when an implementation detects a potential deadlock situation that it
 * cannot solve. The name of the current coordination is given as argument.
 */
public class CoordinationException extends RuntimeException {
	private static final long	serialVersionUID	= 1L;

	
	/**
	 * Unknown reason for this exception.
	 */
	public final static int		UNKNOWN				= 0;
	/**
	 * Adding a participant caused a deadlock.
	 */
	public final static int		DEADLOCK_DETECTED	= 1;

	/**
	 * The Coordination was failed with {@link Coordination#fail(Throwable)}.
	 * When this exception type is used, the {@link #getFailure()} method must
	 * return a non-null value.
	 */
	public final static int		FAILED				= 3;

	/**
	 * The Coordination was partially ended.
	 */
	public final static int		PARTIALLY_ENDED		= 4;

	/**
	 * The Coordination was already ended.
	 */
	public final static int		ALREADY_ENDED		= 5;

	/**
	 * A Coordination was pushed on the stack that was already pushed.
	 */
	public final static int		ALREADY_PUSHED		= 6;

	/**
	 * Interrupted while trying to lock the participant.
	 */
	public final static int		LOCK_INTERRUPTED	= 7;

	
	final String				name;
	final int					reason;
	final Throwable				failure;
	final long					id;

	/**
	 * Create a new Coordination Exception.
	 * 
	 * @param message The message
	 * @param coordination The coordination that failed
	 * @param exception The exception
	 * @param reason The reason for the exception.
	 */
	public CoordinationException(String message, Coordination coordination,
			int reason, Throwable exception ) {
		super(message);
		this.reason = reason;
		this.failure = exception;
		this.id = coordination != null ? coordination.getId() : -1;
		this.name = coordination != null ? coordination.getName() : "<>";		
	}
	
	/**
	 * Create a new Coordination Exception.
	 * 
	 * @param message The message
	 * @param coordination The coordination that failed
	 * @param reason The reason for the exception.
	 */
	public CoordinationException(String message, Coordination coordination,
			int reason) {
		this(message, coordination, reason, null);
	}

	/**
	 * Constructor when the coordination has failed and there is a failure
	 * reason.
	 * 
	 * @param message The message
	 * @param coordination The involved coordination
	 * @param reason The reason of the failure as given in
	 *        {@link Coordination#fail(Throwable)}.
	 */
	public CoordinationException(String message, Coordination coordination, Throwable reason) {
		this(message,coordination, FAILED, reason);
	}

	/**
	 * Answer the name of the Coordination associated with this exception.
	 * 
	 * @return the Coordination name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Answer the reason.
	 * 
	 * @return the reason
	 */
	public int getReason() {
		return reason;
	}

	/**
	 * Must be set of the exception type is {@link #FAILED}.
	 * 
	 * @return If exception type is {@link #FAILED} a Throwable
	 */
	public Throwable getFailure() {
		return failure;
	}

	/**
	 * @return Answer the id
	 */
	
	public long getId() {
		return id;
	}
}
