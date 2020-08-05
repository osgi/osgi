/*
 * Copyright (c) OSGi Alliance (2013, 2020). All Rights Reserved.
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

import java.lang.reflect.Array;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Abstract {@code Function} data wrapper. A subclass must be used for an access
 * to the property values by all functions. It takes care about the timestamp
 * and additional metadata. The subclasses are responsible to provide concrete
 * value and unit if required.
 */
public abstract class FunctionData implements Comparable<Object> {

	/**
	 * Represents the timestamp field name. The field value is available with
	 * {@link #getTimestamp()}. The field type is {@code long}. The constant can
	 * be used as a key to {@link #FunctionData(Map)}.
	 */
	public static final String	FIELD_TIMESTAMP	= "timestamp";

	/**
	 * Represents the metadata field name. The field value is available with
	 * {@link #getMetadata()}. The field type is {@code Map}. The constant can
	 * be used as a key to {@link #FunctionData(Map)}.
	 */
	public static final String	FIELD_METADATA	= "metadata";

	/**
	 * Metadata key, which value represents the data description. The property
	 * value type is {@code java.lang.String}.
	 */
	public static final String	DESCRIPTION		= "description";

	private final long			timestamp;
	private final Map<String, ? >	metadata;

	/**
	 * Constructs new {@code FunctionData} instance with the specified field
	 * values. The map keys must match to the field names. The map values will
	 * be assigned to the appropriate class fields. For example, the maps can
	 * be: {"timestamp"=Long(1384440775495)}. That map will initialize the
	 * {@link #FIELD_TIMESTAMP} field with 1384440775495. If timestamp is
	 * missing, {@link Long#MIN_VALUE} is used.
	 * <ul>
	 * <li>{@link #FIELD_TIMESTAMP} - optional field. The value type must be
	 * {@code Long}.</li>
	 * <li>{@link #FIELD_METADATA} - optional field. The value type must be
	 * {@code Map}.</li>
	 * </ul>
	 * 
	 * @param fields Contains the new {@code FunctionData} instance field
	 *        values.
	 * 
	 * @throws ClassCastException If the field value types are not expected.
	 * @throws NullPointerException If the fields map is {@code null}.
	 */
	@SuppressWarnings("unchecked")
	public FunctionData(Map<String, ? > fields) {
		Long timestampLocal = (Long) fields.get(FIELD_TIMESTAMP);
		this.timestamp = (null != timestampLocal) ? timestampLocal.longValue() : Long.MIN_VALUE;
		this.metadata = (Map<String, ? >) fields.get(FIELD_METADATA);
	}

	/**
	 * Constructs new {@code FunctionData} instance with the specified
	 * arguments.
	 * 
	 * @param timestamp The data timestamp optional field.
	 * @param metadata The data metadata optional field.
	 */
	public FunctionData(long timestamp, Map<String, ? > metadata) {
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
	 * <li>{@link #DESCRIPTION}</li>
	 * <li>custom key</li>
	 * </ul>
	 * 
	 * @return {@code FunctionData} metadata or {@code null} is there is no
	 *         metadata.
	 */
	public Map<String, ? > getMetadata() {
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
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FunctionData)) {
			return false;
		}
		try {
			return 0 == compareTo(other);
		} catch (ClassCastException cce) {
			return false;
		}
	}

	/**
	 * Returns the hash code of this {@code FunctionData}.
	 * 
	 * @return {@code FunctionData} hash code.
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return ((int) (this.timestamp ^ (this.timestamp >>> 32))) +
				calculateMapHashCode(this.metadata);
	}

	/**
	 * Compares this {@code FunctionData} instance with the given argument. If
	 * the argument is not {@code FunctionData}, it throws
	 * {@code ClassCastException}. Otherwise, this method returns:
	 * <ul>
	 * <li>{@code -1} if this instance timestamp is less than the argument
	 * timestamp. If they are equivalent, it can be the result of the metadata
	 * map deep comparison.</li>
	 * <li>{@code 0} if all fields are equivalent.</li>
	 * <li>{@code 1} if this instance timestamp is greater than the argument
	 * timestamp. If they are equivalent, it can be the result of the metadata
	 * map deep comparison.</li>
	 * </ul>
	 * Metadata map deep comparison compares the elements of all nested
	 * {@code java.util.Map} and array instances. {@code null} is less than any
	 * other non-null instance.
	 *
	 * @param o {@code FunctionData} to be compared.
	 *
	 * @return {@code -1}, {@code 0} or {@code 1} depending on the comparison
	 *         rules.
	 *
	 * @throws ClassCastException If the method argument is not of type
	 *         {@code FunctionData} or metadata maps contain values of different
	 *         types for the same key.
	 * @throws NullPointerException If the method argument is {@code null}.
	 *
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Object o) {
		FunctionData other = (FunctionData) o;
		if (this.timestamp == other.timestamp) {
			return compareMaps(this.metadata, other.metadata);
		}
		return (this.timestamp < other.timestamp) ? -1 : 1;
	}

	private static int calculateMapHashCode(Map< ? , ? > map) {
		if (null == map) {
			return 0;
		}
		return calculateMapHashCodeDeep(map, null);
	}

	private static int calculateMapHashCodeDeep(Map< ? , ? > map,
			IdentityHashMap<Object,Object> usedContainers) {
		int result = 0;
		for (Map.Entry< ? , ? > currentEntry : map.entrySet()) {
			result += currentEntry.getKey().hashCode();
			result += calculateValueHashCodeDeep(currentEntry.getValue(), usedContainers);
		}
		return result;
	}

	private static int calculateValueHashCodeDeep(Object value,
			IdentityHashMap<Object,Object> usedContainers) {
		if (null == value) {
			return 0;
		}
		if (value.getClass().isArray()) {
			if (null == usedContainers) {
				usedContainers = new IdentityHashMap<>();
			}
			if (null != usedContainers.put(value, value)) {
				return 0;
			}
			try {
				return calculateArrayHashCodeDeep(value, usedContainers);
			} finally {
				usedContainers.remove(value);
			}
		}
		if (value instanceof Map) {
			if (null == usedContainers) {
				usedContainers = new IdentityHashMap<>();
			}
			if (null != usedContainers.put(value, value)) {
				return 0;
			}
			try {
				return calculateMapHashCodeDeep((Map< ? , ? >) value,
						usedContainers);
			} finally {
				usedContainers.remove(value);
			}
		}
		return value.hashCode();
	}

	private static int calculateArrayHashCodeDeep(Object array,
			IdentityHashMap<Object,Object> usedContainers) {
		int arrayLength = Array.getLength(array);
		int result = 0;
		for (int i = 0; i < arrayLength; i++) {
			result += calculateValueHashCodeDeep(Array.get(array, i), usedContainers);
		}
		return result;
	}

	private static int compareMaps(Map< ? , ? > thisMap,
			Map< ? , ? > otherMap) {
		if (null == thisMap) {
			return (null != otherMap) ? -1 : 0;
		}
		if (null == otherMap) {
			return 1;
		}
		return compareMapsDeep(thisMap, otherMap, null);
	}

	private static int compareMapsDeep(Map< ? , ? > thisMap,
			Map< ? , ? > otherMap,
			IdentityHashMap<Object,Object> thisUsedContainers) {
		int result = compare(thisMap.size(), otherMap.size());
		if (0 != result) {
			return result;
		}

		for (Map.Entry< ? , ? > thisEntry : thisMap.entrySet()) {
			if (otherMap.containsKey(thisEntry.getKey())) {
				result = compareValuesDeep(thisEntry.getValue(), otherMap.get(thisEntry.getKey()), thisUsedContainers);
				if (0 != result) {
					return result;
				}
			} else {
				return 1;
			}
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	private static int compareValuesDeep(Object thisValue, Object otherValue,
			IdentityHashMap<Object,Object> thisUsedContainers) {
		if (null == thisValue) {
			return (null == otherValue) ? 0 : -1;
		}
		if (null == otherValue) {
			return 1;
		}
		if (thisValue.getClass().isArray()) {
			if (otherValue.getClass().isArray()) {
				if (null == thisUsedContainers) {
					thisUsedContainers = new IdentityHashMap<>();
				}
				if (null != thisUsedContainers.put(thisValue, thisValue)) {
					return -1;
				}
				try {
					return compareArraysDeep(thisValue, otherValue, thisUsedContainers);
				} finally {
					thisUsedContainers.remove(thisValue);
				}
			}
			return 1;
		}
		if (thisValue instanceof Map) {
			if (otherValue instanceof Map) {
				if (null == thisUsedContainers) {
					thisUsedContainers = new IdentityHashMap<>();
				}
				if (null != thisUsedContainers.put(thisValue, thisValue)) {
					return -1;
				}
				try {
					return compareMapsDeep((Map< ? , ? >) thisValue,
							(Map< ? , ? >) otherValue, thisUsedContainers);
				} finally {
					thisUsedContainers.remove(thisValue);
				}
			}
			return 1;
		}
		if (thisValue instanceof Comparable) {
			return (((Comparable<Object>) thisValue)).compareTo(otherValue);
		}
		return thisValue.equals(otherValue) ? 0 : 1;
	}

	private static int compareArraysDeep(Object thisArray, Object otherArray,
			IdentityHashMap<Object,Object> thisUsedContainers) {
		int thisArrayLength = Array.getLength(thisArray);
		int otherArrayLength = Array.getLength(otherArray);
		int result = compare(thisArrayLength, otherArrayLength);
		if (0 != result) {
			return result;
		}
		for (int i = 0; i < thisArrayLength; i++) {
			result = compareValuesDeep(Array.get(thisArray, i), Array.get(otherArray, i), thisUsedContainers);
			if (0 != result) {
				return result;
			}
		}
		return 0;
	}

	private static int compare(int thisValue, int otherValue) {
		return (thisValue < otherValue) ? -1 :
				((thisValue > otherValue) ? 1 : 0);
	}
}
