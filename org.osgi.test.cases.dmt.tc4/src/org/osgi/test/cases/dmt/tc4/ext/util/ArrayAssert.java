package org.osgi.test.cases.dmt.tc4.ext.util;

import java.util.Arrays;

import junit.framework.Assert;

public class ArrayAssert {

	public static void assertEquivalenceArrays(Object[] expected, Object[] actual) {
		if ((expected == null) || (actual == null)) {
			Assert.assertTrue(expected == actual);
			return;
		}
		expected = (Object[]) expected.clone();
		actual = (Object[]) actual.clone();
		Arrays.sort(expected);
		Arrays.sort(actual);
		Assert.assertTrue(Arrays.equals(expected, actual));
	}
}
