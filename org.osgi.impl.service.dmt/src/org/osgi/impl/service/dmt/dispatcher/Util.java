package org.osgi.impl.service.dmt.dispatcher;

import java.util.Arrays;
import java.util.Collection;

/**
 * simple utility class with some static methods 
 * 
 * @author steffen
 *
 */
public class Util {

	/**
	 * Takes an Object and returns a Collection of Strings if:
	 * - Object is of type String[] or
	 * - Object is of type String
	 * @param property ... given object
	 * @return ... Collection of Strings or null, if types don't fit
	 */
	@SuppressWarnings("unchecked")
	public static Collection<String> toCollection(Object property) {
		if (property instanceof Collection<?>)
			return (Collection<String>) property;

		if (property instanceof String)
			return Arrays.asList((String) property);

		if (property instanceof String[])
			return Arrays.asList((String[]) property);

		return null;
	}
}
