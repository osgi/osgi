package org.osgi.test.cases.dmt.tc4.ext.util;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

public class ArrayAssert {

	public static void assertEquivalenceArrays(Object[] expected, Object[] actual) {
		if ((expected == null) || (actual == null)) {
			Assert.assertTrue(expected == actual);
			return;
		}
		List actualList = Arrays.asList(actual);
		for (int i = 0; i < expected.length; i++) {
			Assert.assertTrue(actualList.contains(expected[i]));
		}
	}
}
