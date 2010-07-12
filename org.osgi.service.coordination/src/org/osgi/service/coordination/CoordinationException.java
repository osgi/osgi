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
package org.osgi.service.coordination;

/**
 * Thrown when an implementation detects a potential deadlock situation that it
 * cannot solve. The name of the current coordination is given as argument.
 */
public class CoordinationException extends RuntimeException {
	private static final long	serialVersionUID			= 1L;

	public final static int		UNKNOWN						= 0;
	public final static int		DEADLOCK_DETECTED			= 1;
	public final static int		TIMEOUT						= 2;
	public final static int		NOT_ON_INITIATING_THREAD	= 3;

	final String				name;
	final int					reason;

	public CoordinationException(String message, String name, int reason) {
		super(message);
		this.name = name;
		this.reason = reason;
	}

	public String getName() {
		return name;
	}

	public int getReason() {
		return reason;
	}

	public static String translateReason(int n) {
		switch (n) {
			case DEADLOCK_DETECTED :
				return "deadlock detected";
			case TIMEOUT :
				return "lock timed out";
			case UNKNOWN :
				return "unknown";
			case NOT_ON_INITIATING_THREAD :
				return "Must be called on the same thread as the initiator";
			default :
				return "unknown";
		}
	}
}
