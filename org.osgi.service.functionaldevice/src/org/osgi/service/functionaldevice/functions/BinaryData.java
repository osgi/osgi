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
 * Device Function binary data wrapper. It can contain a boolean value,
 * timestamp and additional metadata.
 * 
 * @see BinaryControl
 * @see BinarySensor
 */
public class BinaryData {

	/** <code>BinaryData</code> instance represents <code>true</code> value. */
	public static final BinaryData TRUE = new BinaryData(true, -1, null);

	/** <code>BinaryData</code> instance represents <code>false</code> value. */
	public static final BinaryData FALSE = new BinaryData(false, -1, null);

	/**
	 * Represents <code>BinaryData</code> value. The immutable field is
	 * accessible with {@link #getValue()} getter.
	 */
	public final boolean value;

	/**
	 * Represents <code>BinaryData</code> timestamp. The timestamp is the
	 * difference between the value collecting time and midnight, January 1,
	 * 1970 UTC. It's measured in milliseconds. If possible, the value should be
	 * provided by the device, otherwise the device driver can generate that
	 * info. {@link java.lang.Long#MIN_VALUE} value means no timestamp. The
	 * immutable field is accessible with {@link #getTimestamp()} getter.
	 */
	public final long timestamp;

	/**
	 * Represents <code>BinaryData</code> metadata in an unmodifiable
	 * <code>Map</code>. The immutable field is accessible with
	 * {@link #getMetadata()} getter.
	 */
	public final Map metadata;

	/**
	 * Constructs new <code>BinaryData</code> instance with the specified
	 * arguments.
	 * 
	 * @param value
	 *            The binary value.
	 * @param timestamp
	 *            The value timestamp.
	 * @param metadata
	 *            The value metadata.
	 */
	public BinaryData(final boolean value, final long timestamp,
			final Map metadata) {
		this.value = value;
		this.timestamp = timestamp;
		this.metadata = (null == metadata) ? null : Collections
				.unmodifiableMap(metadata);
	}

	/**
	 * Returns <code>BinaryData</code> value.
	 * 
	 * @return <code>BinaryData</code> value.
	 */
	public final boolean getValue() {
		return this.value;
	}

	/**
	 * Returns <code>BinaryData</code> timestamp. The timestamp is the
	 * difference between the value collecting time and midnight, January 1,
	 * 1970 UTC. It's measured in milliseconds. If possible, the value should be
	 * provided by the device, otherwise the device driver can generate that
	 * info. {@link java.lang.Long#MIN_VALUE} value means no timestamp.
	 * 
	 * @return <code>BinaryData</code> timestamp.
	 */
	public final long getTimestamp() {
		return this.timestamp;
	}

	/**
	 * Returns <code>BinaryData</code> metadata.
	 * 
	 * @return <code>BinaryData</code> metadata.
	 */
	public final Map getMetadata() {
		return this.metadata;
	}

}
