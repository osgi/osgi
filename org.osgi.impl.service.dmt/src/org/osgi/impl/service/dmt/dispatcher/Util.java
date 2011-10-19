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

	static Collection<String> toCollection(Object property) {
		if (property instanceof String)
			return Arrays.asList((String) property);

		if (property instanceof String[])
			return Arrays.asList((String[]) property);

		return null;
	}
}
