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
 * Abstract <code>DeviceFunction</code> data wrapper. A subclass must be used
 * for an access to the property values by all Device Functions. It takes care
 * about the timestamp and additional metadata. The subclasses are responsible
 * to provide concrete value and unit if required.
 * <p>
 * The subclass is responsible to provide correct implementation of
 * {@link Comparable#compareTo(Object)} method.
 */
public abstract class DeviceFunctionData implements Comparable {

	/**
	 * Metadata key, which value represents the data description. The property
	 * value type is <code>java.lang.String</code>.
	 */
	public static final String	META_INFO_DESCRIPTION	= "description";

	/**
	 * Contains <code>DeviceFunctionData</code> timestamp. The timestamp is the
	 * difference between the value collecting time and midnight, January 1,
	 * 1970 UTC. It's measured in milliseconds. The device driver is responsible
	 * to generate that value when the value is received from the device.
	 * {@link java.lang.Long#MIN_VALUE} value means no timestamp.
	 */
	public final long			timestamp;

	/**
	 * Contains <code>DeviceFunctionData</code> metadata. It's dynamic metadata
	 * related only to this specific value. Possible keys:
	 * <ul>
	 * <li>{@link #META_INFO_DESCRIPTION}</li>
	 * <li>custom key</li>
	 */
	public final Map			metadata;

	/**
	 * Constructs new <code>DeviceFunctionData</code> instance with the
	 * specified field values. The map keys must match to the field names. The
	 * map values will be assigned to the appropriate class fields. For example,
	 * the maps can be: {"timestamp"=Long(1384440775495)}. That map will
	 * initialize the "timestamp" field with 1384440775495.
	 * 
	 * @param fields Contains the new <code>DeviceFunctionData</code> instance
	 *        field values.
	 */
	public DeviceFunctionData(final Map fields) {
		final Long timestamp = (Long) fields.get("timestamp");
		this.timestamp = (null != timestamp) ? timestamp.longValue() : Long.MIN_VALUE;
		this.metadata = (Map) fields.get("metadata");
	}

	/**
	 * Constructs new <code>DeviceFunctionData</code> instance with the
	 * specified arguments.
	 * 
	 * @param timestamp The data timestamp.
	 * @param metadata The data metadata.
	 */
	public DeviceFunctionData(final long timestamp, final Map metadata) {
		this.timestamp = timestamp;
		this.metadata = metadata;
	}

	/**
	 * Returns <code>DeviceFunctionData</code> timestamp. The timestamp is the
	 * difference between the value collecting time and midnight, January 1,
	 * 1970 UTC. It's measured in milliseconds. The device driver is responsible
	 * to generate that value when the value is received from the device.
	 * {@link java.lang.Long#MIN_VALUE} value means no timestamp.
	 * 
	 * @return <code>DeviceFunctionData</code> timestamp.
	 */
	public long getTimestamp() {
		return this.timestamp;
	}

	/**
	 * Returns <code>DeviceFunctionData</code> metadata. It's dynamic metadata
	 * related only to this specific value. Possible keys:
	 * <ul>
	 * <li>{@link #META_INFO_DESCRIPTION}</li>
	 * <li>custom key</li>
	 * 
	 * @return <code>DeviceFunctionData</code> metadata or <code>null</code> is
	 *         there is no metadata.
	 */
	public Map getMetadata() {
		return this.metadata;
	}

}
