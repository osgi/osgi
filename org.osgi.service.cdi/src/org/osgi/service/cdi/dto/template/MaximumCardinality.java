package org.osgi.service.cdi.dto.template;

import java.util.Arrays;

/**
 * Defines the possible values for maximum cardinality of dependencies
 */
public enum MaximumCardinality {
	/**
	 * Defines a unary reference.
	 */
	ONE(1),
	/**
	 * Defines a plural reference.
	 */
	MANY(Integer.MAX_VALUE);

	private int value;

	private MaximumCardinality(int value) {
		this.value = value;
	}

	/**
	 * Convert this upper cardinality boundary to an integer
	 *
	 * @return The integer representation of this upper cardinality boundary
	 */
	public int toInt() {
		return value;
	}

	/**
	 * Resolve an integer to an upper cardinality boundary.
	 *
	 * @param value The integer representation of an upper cardinality boundary
	 * @return The enum representation of the upper cardinality boundary descrubed
	 *         by <code>value</code>
	 */
	public static MaximumCardinality fromInt(int value) {
		return Arrays.stream(values())
				.filter(it -> it.value == value)
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Illegal maximum cardinality value: " + value));
	}
}