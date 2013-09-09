/*
 * Copyright (c) OSGi Alliance (2013). All Rights Reserved.
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

package org.osgi.service.functionaldevice.functions;

import java.util.Collections;
import java.util.Map;

/**
 * Device Function alarm data. It cares about the alarm type, severity and
 * additional metadata.
 * 
 * @see Alarm
 */
public class AlarmData {

	/**
	 * Represents the alarm type. The immutable field is accessible with
	 * {@link #getType()} getter.
	 */
	public final int type;

	/**
	 * Represents the alarm severity. The immutable field is accessible with
	 * {@link #getSeverity()} getter.
	 */
	public final int severity;

	/**
	 * Represents <code>AlarmData</code> timestamp. The timestamp is the
	 * difference between the value collecting time and midnight, January 1,
	 * 1970 UTC. It's measured in milliseconds. If possible, the value should be
	 * provided by the device, otherwise the device driver can generate that
	 * info. {@link java.lang.Long#MIN_VALUE} value means no timestamp. The
	 * immutable field is accessible with {@link #getTimestamp()} getter.
	 */
	public final long timestamp;

	/**
	 * Represents <code>AlarmData</code> metadata in an unmodifiable
	 * <code>Map</code>. The immutable field is accessible with
	 * {@link #getMetadata()} getter.
	 */
	public final Map metadata;

	/**
	 * Constructs new <code>AlarmData</code> instance with the specified
	 * arguments.
	 * 
	 * @param type
	 *            The alarm type.
	 * @param severity
	 *            The alarm severity.
	 * @param metadata
	 *            The alarm metadata.
	 */
	public AlarmData(final int type, final int severity, final long timestamp,
			final Map metadata) {
		this.type = type;
		this.severity = severity;
		this.timestamp = timestamp;
		this.metadata = (null == metadata) ? null : Collections
				.unmodifiableMap(metadata);
	}

	/**
	 * Returns the alarm type.
	 * 
	 * @return The alarm type.
	 */
	public int getType() {
		return this.type;
	}

	/**
	 * Returns the alarm severity.
	 * 
	 * @return The alarm severity.
	 */
	public int getSeverity() {
		return this.severity;
	}

	/**
	 * Returns <code>AlarmData</code> timestamp. The timestamp is the difference
	 * between the value collecting time and midnight, January 1, 1970 UTC. It's
	 * measured in milliseconds. If possible, the value should be provided by
	 * the device, otherwise the device driver can generate that info.
	 * {@link java.lang.Long#MIN_VALUE} value means no timestamp.
	 * 
	 * @return <code>AlarmData</code> timestamp.
	 */
	public long getTimestamp() {
		return this.timestamp;
	}

	/**
	 * Returns <code>AlarmData</code> metadata.
	 * 
	 * @return <code>AlarmData</code> metadata.
	 */
	public Map getMetadata() {
		return this.metadata;
	}

}
