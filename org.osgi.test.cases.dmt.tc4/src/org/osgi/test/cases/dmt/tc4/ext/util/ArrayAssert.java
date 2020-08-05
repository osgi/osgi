package org.osgi.test.cases.dmt.tc4.ext.util;

import static junit.framework.TestCase.assertTrue;

import java.util.Arrays;
import java.util.List;
public class ArrayAssert {

	public static void assertEquivalenceArrays(Object[] expected, Object[] actual) {
		assertEquivalenceArrays(null, expected, actual);
	}

	public static void assertEquivalenceArrays(String message, Object[] expected, Object[] actual) {
		if ((expected == null) || (actual == null)) {
			assertTrue(expected == actual);
			return;
		}
		List<Object> actualList = Arrays.asList(actual);
		for (int i = 0; i < expected.length; i++) {
			if (message != null) {
				assertTrue(message, actualList.contains(expected[i]));
			} else {
				assertTrue(actualList.contains(expected[i]));
			}
		}
	}
}
