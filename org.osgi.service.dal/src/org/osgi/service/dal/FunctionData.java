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

package org.osgi.service.dal;

import java.util.Map;

/**
 * Abstract {@code Function} data wrapper. A subclass must be used for an access
 * to the property values by all functions. It takes care about the timestamp
 * and additional metadata. The subclasses are responsible to provide concrete
 * value and unit if required.
 * <p>
 * The subclass is responsible to provide correct implementation of
 * {@link Comparable#compareTo(Object)} method.
 */
public abstract class FunctionData implements Comparable {

	/**
	 * Represents the timestamp field name. The field value is available with
	 * {@link #timestamp} and {@link #getTimestamp()}. The field type is
	 * {@code long}. The constant can be used as a key to
	 * {@link #FunctionData(Map)}.
	 */
	public static final String	FIELD_TIMESTAMP			= "timestamp";

	/**
	 * Represents the metadata field name. The field value is available with
	 * {@link #metadata} and {@link #getMetadata()}. The field type is
	 * {@code Map}. The constant can be used as a key to
	 * {@link #FunctionData(Map)}.
	 */
	public static final String	FIELD_METADATA			= "metadata";

	/**
	 * Metadata key, which value represents the data description. The property
	 * value type is {@code java.lang.String}.
	 */
	public static final String	META_INFO_DESCRIPTION	= "description";

	/**
	 * Contains {@code FunctionData} timestamp. The timestamp is the difference
	 * between the value collecting time and midnight, January 1, 1970 UTC. It's
	 * measured in milliseconds. The device driver is responsible to generate
	 * that value when the value is received from the device.
	 * {@link java.lang.Long#MIN_VALUE} value means no timestamp.
	 */
	public final long			timestamp;

	/**
	 * Contains {@code FunctionData} metadata. It's dynamic metadata related
	 * only to this specific value. Possible keys:
	 * <ul>
	 * <li>{@link #META_INFO_DESCRIPTION}</li>
	 * <li>custom key</li>
	 */
	public final Map			metadata;

	/**
	 * Constructs new {@code FunctionData} instance with the specified field
	 * values. The map keys must match to the field names. The map values will
	 * be assigned to the appropriate class fields. For example, the maps can
	 * be: {"timestamp"=Long(1384440775495)}. That map will initialize the
	 * {@link #FIELD_TIMESTAMP} field with 1384440775495. If timestamp is
	 * missing, {@link Long#MIN_VALUE} is used.
	 * <p>
	 * {@link #FIELD_TIMESTAMP} field value type must be {@code Long}.
	 * {@link #FIELD_METADATA} field value type must be {@code Map}.
	 * 
	 * @param fields Contains the new {@code FunctionData} instance field
	 *        values.
	 * 
	 * @throws ClassCastException If the field value types are not expected.
	 * @throws NullPointerException If the fields map is {@code null}.
	 */
	public FunctionData(final Map fields) {
		final Long timestampLocal = (Long) fields.get("timestamp");
		this.timestamp = (null != timestampLocal) ? timestampLocal.longValue() : Long.MIN_VALUE;
		this.metadata = (Map) fields.get("metadata");
	}

	/**
	 * Constructs new {@code FunctionData} instance with the specified
	 * arguments.
	 * 
	 * @param timestamp The data timestamp.
	 * @param metadata The data metadata.
	 */
	public FunctionData(final long timestamp, final Map metadata) {
		this.timestamp = timestamp;
		this.metadata = metadata;
	}

	/**
	 * Returns {@code FunctionData} timestamp. The timestamp is the difference
	 * between the value collecting time and midnight, January 1, 1970 UTC. It's
	 * measured in milliseconds. The device driver is responsible to generate
	 * that value when the value is received from the device.
	 * {@link java.lang.Long#MIN_VALUE} value means no timestamp.
	 * 
	 * @return {@code FunctionData} timestamp.
	 */
	public long getTimestamp() {
		return this.timestamp;
	}

	/**
	 * Returns {@code FunctionData} metadata. It's dynamic metadata related only
	 * to this specific value. Possible keys:
	 * <ul>
	 * <li>{@link #META_INFO_DESCRIPTION}</li>
	 * <li>custom key</li>
	 * 
	 * @return {@code FunctionData} metadata or {@code null} is there is no
	 *         metadata.
	 */
	public Map getMetadata() {
		return this.metadata;
	}

	/**
	 * Two {@code FunctionData} instances are equal if their metadata and
	 * timestamp are equivalent.
	 * 
	 * @param other The other instance to compare. It must be of
	 *        {@code FunctionData} type.
	 * 
	 * @return {@code true} if this instance and argument have equivalent
	 *         metadata and timestamp, {@code false} otherwise.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		if (!(other instanceof FunctionData)) {
			return false;
		}
		FunctionData otherData = (FunctionData) other;
		if (null != this.metadata) {
			if ((null == otherData.metadata) || (!this.metadata.equals(otherData.metadata))) {
				return false;
			}
		} else if (null != otherData.metadata) {
			return false;
		}
		return this.timestamp == otherData.timestamp;
	}

	/**
	 * Returns the hash code of this {@code FunctionData}.
	 * 
	 * @return {@code FunctionData} hash code.
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		Long timestampLocal = new Long(this.timestamp);
		return (null == this.metadata) ? timestampLocal.hashCode() :
				this.metadata.hashCode() + timestampLocal.hashCode();
	}

}
