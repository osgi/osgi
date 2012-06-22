package org.osgi.test.cases.dmt.tc4.ext.util;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

public class ArrayAssert {

	public static void assertEquivalenceArrays(Object[] expected, Object[] actual) {
		assertEquivalenceArrays(null, expected, actual);
	}

	public static void assertEquivalenceArrays(String message, Object[] expected, Object[] actual) {
		if ((expected == null) || (actual == null)) {
			Assert.assertTrue(expected == actual);
			return;
		}
		List actualList = Arrays.asList(actual);
		for (int i = 0; i < expected.length; i++) {
			if (message != null) {
				Assert.assertTrue(message, actualList.contains(expected[i]));
			} else {
				Assert.assertTrue(actualList.contains(expected[i]));
			}
		}
	}
}
