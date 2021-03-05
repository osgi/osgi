/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.test.cases.framework.junit.version;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;
import org.osgi.framework.VersionRange;

import junit.framework.TestCase;

/**
 * Tests for the VersionRange class.
 */
public class VersionRangeTests extends TestCase {
	public void testConstructors() {
		Version version1 = new Version(1, 2, 3);
		Version version2 = new Version(2, 0, 0);
		new VersionRange('[', version1, version2, ')');
		new VersionRange('[', version1, version2, ']');
		new VersionRange('(', version1, version2, ')');
		new VersionRange('(', version1, version2, ']');

		new VersionRange('[', version1, null, ')');

		new VersionRange("[1,2)");
		new VersionRange("[1.2.3,2.0.0)");
		new VersionRange("(1.2.3,2.0.0]");
		new VersionRange(" [ 1.2.3 , 2.0.0 ) ");
		new VersionRange(" ( 1.2.3 , 2.0.0 ] ");
		new VersionRange("1.2.3");
		new VersionRange(" 1.2.3 ");
		VersionRange.valueOf("[1,2)");
		VersionRange.valueOf("[1.2.3,2.0.0)");
		VersionRange.valueOf("(1.2.3,2.0.0]");
		VersionRange.valueOf(" [ 1.2.3 , 2.0.0 ) ");
		VersionRange.valueOf(" ( 1.2.3 , 2.0.0 ] ");
		VersionRange.valueOf("1.2.3");
		VersionRange.valueOf(" 1.2.3 ");
	}

	public void testConstructorsBadArguments() {
		Version version1 = new Version(1, 2, 3);
		Version version2 = new Version(2, 0, 0);
		try {
			new VersionRange('x', version1, version2, ')');
			fail("VersionRange created with illegal arguments");
		}
		catch (IllegalArgumentException ex) {
			// This is an expected exception and may be ignored
		}

		try {
			new VersionRange('[', version1, version2, 'x');
			fail("VersionRange created with illegal arguments");
		}
		catch (IllegalArgumentException ex) {
			// This is an expected exception and may be ignored
		}

		try {
			new VersionRange('[', null, version2, ')');
			fail("VersionRange created with illegal arguments");
		}
		catch (IllegalArgumentException ex) {
			// This is an expected exception and may be ignored
		}

		try {
			new VersionRange(null);
			fail("VersionRange created with illegal arguments");
		} catch (RuntimeException ex) {
			// This is an expected exception and may be ignored
		}
		try {
			VersionRange.valueOf(null);
			fail("VersionRange created with illegal arguments");
		} catch (RuntimeException ex) {
			// This is an expected exception and may be ignored
		}
		testConstructorsBadArguments("");
		testConstructorsBadArguments("x");
		testConstructorsBadArguments(" 1.2.3 x");
		testConstructorsBadArguments(" 1.2.3 [");
		testConstructorsBadArguments(" 1.2.3 ( ");
		testConstructorsBadArguments(" x 1.2.3 ");
		testConstructorsBadArguments("[");
		testConstructorsBadArguments("(1");
		testConstructorsBadArguments("[1,");
		testConstructorsBadArguments("[1,2");
		testConstructorsBadArguments("[1,2x");
		testConstructorsBadArguments("	[1,2)	x");
		testConstructorsBadArguments("x	[1,2)");
		testConstructorsBadArguments("	[1,2)	)");
		testConstructorsBadArguments("	[1,,2)	");
		testConstructorsBadArguments("	[1,2))	");
		testConstructorsBadArguments("[[1,2)");
		testConstructorsBadArguments("	[,2)	");
		testConstructorsBadArguments("[2)");
	}

	private void testConstructorsBadArguments(String arg) {
		try {
			new VersionRange(arg);
			fail("VersionRange created with illegal arguments");
		} catch (IllegalArgumentException ex) {
			// This is an expected exception and may be ignored
		}
		try {
			VersionRange.valueOf(arg);
			fail("VersionRange created with illegal arguments");
		} catch (IllegalArgumentException ex) {
			// This is an expected exception and may be ignored
		}

	}

	@SuppressWarnings("unlikely-arg-type")
	public void testEquals() {
		Version version1 = new Version(1, 2, 3);
		Version version2 = new Version(2, 4, 6);
		VersionRange range1;
		VersionRange range2;

		range1 = new VersionRange('[', version1, version2, ')');
		range2 = new VersionRange("[1.2.3,2.4.6)");
		assertEquals("equal objects", range1, range1);
		assertEquals("equal objects", range2, range2);
		assertEquals("equal objects", range1, range2);
		assertEquals("equal objects", range2, range1);

		range1 = new VersionRange('[', version1, version2, ')');
		range2 = new VersionRange('[', version1, version2, ')');
		assertEquals("equal objects", range1, range2);
		assertEquals("equal objects", range2, range1);

		range1 = new VersionRange('[', version1, version2, ')');
		range2 = new VersionRange('[', version1, version2, ']');
		assertFalse("unequal objects", range1.equals(range2));
		assertFalse("unequal objects", range2.equals(range1));
		range2 = new VersionRange("[1.2.3,2.4.6]");
		assertFalse("unequal objects", range1.equals(range2));
		assertFalse("unequal objects", range2.equals(range1));

		range1 = new VersionRange('(', version1, version2, ']');
		range2 = new VersionRange('[', version1, version2, ']');
		assertFalse("unequal objects", range1.equals(range2));
		assertFalse("unequal objects", range2.equals(range1));
		range2 = new VersionRange("[1.2.3,2.4.6]");
		assertFalse("unequal objects", range1.equals(range2));
		assertFalse("unequal objects", range2.equals(range1));

		range1 = new VersionRange('[', version1, version2, ']');
		range2 = new VersionRange('[', version1, version1, ']');
		assertFalse("unequal objects", range1.equals(range2));
		assertFalse("unequal objects", range2.equals(range1));

		range1 = new VersionRange('[', version1, version1, ')');
		range2 = new VersionRange('[', version1, version1, ']');
		assertFalse("unequal objects", range1.equals(range2));
		assertFalse("unequal objects", range2.equals(range1));

		// both are empty; so equal
		range1 = new VersionRange('[', version2, version1, ')');
		range2 = new VersionRange('[', version1, version1, ')');
		assertEquals("equal objects", range1, range2);
		assertEquals("equal objects", range2, range1);

		range1 = new VersionRange('[', version1, null, ')');
		range2 = new VersionRange('[', version1, null, ']');
		assertEquals("equal objects", range1, range2);
		assertEquals("equal objects", range2, range1);

		range1 = new VersionRange('[', version1, version2, ')');
		range2 = new VersionRange('[', version1, null, ')');
		assertFalse("unequal objects", range1.equals(range2));
		assertFalse("unequal objects", range2.equals(range1));

		range1 = new VersionRange('[', version1, null, ')');
		range2 = new VersionRange('[', version1, version2, ')');
		assertFalse("unequal objects", range1.equals(range2));
		assertFalse("unequal objects", range2.equals(range1));

		range1 = new VersionRange('[', version1, version2, ')');
		assertFalse("unequal objects", range1.equals(null));
		assertFalse("unequal objects", range1.equals(this));

	}

	public void testHashCode() throws Exception {
		Version version1 = new Version(1, 2, 3);
		Version version2 = new Version(2, 4, 6);
		VersionRange range1;
		VersionRange range2;

		range1 = new VersionRange('[', version1, version2, ')');
		range2 = new VersionRange('[', version1, version2, ')');
		assertEquals("equal objects have different hashCode",
				range1.hashCode(), range2.hashCode());

		range1 = new VersionRange('[', version1, version2, ')');
		range2 = new VersionRange('[', version1, version2, ']');
		assertFalse("unequal objects have same hashCode",
				range1.hashCode() == range2.hashCode());

		range1 = new VersionRange('(', version1, version2, ']');
		range2 = new VersionRange('[', version1, version2, ']');
		assertFalse("unequal objects have same hashCode",
				range1.hashCode() == range2.hashCode());

		range1 = new VersionRange('[', version1, version2, ']');
		range2 = new VersionRange('[', version1, version1, ']');
		assertFalse("unequal objects have same hashCode",
				range1.hashCode() == range2.hashCode());

		range1 = new VersionRange('[', version1, version1, ')');
		range2 = new VersionRange('[', version1, version1, ']');
		assertFalse("unequal objects have same hashCode",
				range1.hashCode() == range2.hashCode());

		// both are empty; so same hashCode
		range1 = new VersionRange('[', version2, version1, ')');
		range2 = new VersionRange('[', version1, version1, ')');
		assertEquals("equal objects have different hashCode",
				range1.hashCode(), range2.hashCode());

		range1 = new VersionRange('[', version1, null, ')');
		range2 = new VersionRange('[', version1, null, ']');
		assertEquals("equal objects have different hashCode",
				range1.hashCode(), range2.hashCode());

	}

	public void testGetLeftType() {
		Version version11 = new Version(2, 3, 4);
		Version version21 = new Version(5, 6, 7);
		VersionRange range;

		range = new VersionRange('[', version11, version21, ')');
		assertEquals("Wrong type", VersionRange.LEFT_CLOSED,
				range.getLeftType());

		range = new VersionRange('[', version11, version21, ']');
		assertEquals("Wrong type", VersionRange.LEFT_CLOSED,
				range.getLeftType());

		range = new VersionRange('(', version11, version21, ')');
		assertEquals("Wrong type", VersionRange.LEFT_OPEN, range.getLeftType());

		range = new VersionRange('(', version11, version21, ']');
		assertEquals("Wrong type", VersionRange.LEFT_OPEN, range.getLeftType());

		range = new VersionRange('[', version11, null, ')');
		assertEquals("Wrong type", VersionRange.LEFT_CLOSED,
				range.getLeftType());

		range = new VersionRange("[2.3.4,5.6.7)");
		assertEquals("Wrong type", VersionRange.LEFT_CLOSED,
				range.getLeftType());

		range = new VersionRange("[2.3.4,5.6.7]");
		assertEquals("Wrong type", VersionRange.LEFT_CLOSED,
				range.getLeftType());

		range = new VersionRange("(2.3.4,5.6.7)");
		assertEquals("Wrong type", VersionRange.LEFT_OPEN, range.getLeftType());

		range = new VersionRange("(2.3.4,5.6.7]");
		assertEquals("Wrong type", VersionRange.LEFT_OPEN, range.getLeftType());

		range = new VersionRange("2.3.4");
		assertEquals("Wrong type", VersionRange.LEFT_CLOSED,
				range.getLeftType());

	}

	public void testGetLeft() {
		Version version11 = new Version(2, 3, 4);
		Version version21 = new Version(5, 6, 7);
		VersionRange range;

		range = new VersionRange('[', version11, version21, ')');
		assertEquals("Wrong version", version11, range.getLeft());

		range = new VersionRange('[', version11, version21, ']');
		assertEquals("Wrong version", version11, range.getLeft());

		range = new VersionRange('(', version11, version21, ')');
		assertEquals("Wrong version", version11, range.getLeft());

		range = new VersionRange('(', version11, version21, ']');
		assertEquals("Wrong version", version11, range.getLeft());

		range = new VersionRange('[', version11, null, ')');
		assertEquals("Wrong version", version11, range.getLeft());

		range = new VersionRange("[2.3.4,5.6.7)");
		assertEquals("Wrong version", version11, range.getLeft());

		range = new VersionRange("[2.3.4,5.6.7]");
		assertEquals("Wrong version", version11, range.getLeft());

		range = new VersionRange("(2.3.4,5.6.7)");
		assertEquals("Wrong version", version11, range.getLeft());

		range = new VersionRange("(2.3.4,5.6.7]");
		assertEquals("Wrong version", version11, range.getLeft());

		range = new VersionRange("2.3.4");
		assertEquals("Wrong version", version11, range.getLeft());

	}

	public void testGetRight() {
		Version version11 = new Version(2, 3, 4);
		Version version21 = new Version(5, 6, 7);
		VersionRange range;

		range = new VersionRange('[', version11, version21, ')');
		assertEquals("Wrong version", version21, range.getRight());

		range = new VersionRange('[', version11, version21, ']');
		assertEquals("Wrong version", version21, range.getRight());

		range = new VersionRange('(', version11, version21, ')');
		assertEquals("Wrong version", version21, range.getRight());

		range = new VersionRange('(', version11, version21, ']');
		assertEquals("Wrong version", version21, range.getRight());

		range = new VersionRange('[', version11, null, ')');
		assertEquals("Wrong version", null, range.getRight());

		range = new VersionRange("[2.3.4,5.6.7)");
		assertEquals("Wrong version", version21, range.getRight());

		range = new VersionRange("[2.3.4,5.6.7]");
		assertEquals("Wrong version", version21, range.getRight());

		range = new VersionRange("(2.3.4,5.6.7)");
		assertEquals("Wrong version", version21, range.getRight());

		range = new VersionRange("(2.3.4,5.6.7]");
		assertEquals("Wrong version", version21, range.getRight());

		range = new VersionRange("2.3.4");
		assertEquals("Wrong version", null, range.getRight());
	}

	public void testGetRightType() throws Exception {
		Version version11 = new Version(2, 3, 4);
		Version version21 = new Version(5, 6, 7);
		VersionRange range;

		range = new VersionRange('[', version11, version21, ')');
		assertEquals("Wrong type", VersionRange.RIGHT_OPEN,
				range.getRightType());

		range = new VersionRange('[', version11, version21, ']');
		assertEquals("Wrong type", VersionRange.RIGHT_CLOSED,
				range.getRightType());

		range = new VersionRange('(', version11, version21, ')');
		assertEquals("Wrong type", VersionRange.RIGHT_OPEN,
				range.getRightType());

		range = new VersionRange('(', version11, version21, ']');
		assertEquals("Wrong type", VersionRange.RIGHT_CLOSED,
				range.getRightType());

		range = new VersionRange('[', version11, null, ')');
		assertEquals("Wrong type", VersionRange.RIGHT_OPEN,
				range.getRightType());

		range = new VersionRange("[2.3.4,5.6.7)");
		assertEquals("Wrong type", VersionRange.RIGHT_OPEN,
				range.getRightType());

		range = new VersionRange("[2.3.4,5.6.7]");
		assertEquals("Wrong type", VersionRange.RIGHT_CLOSED,
				range.getRightType());

		range = new VersionRange("(2.3.4,5.6.7)");
		assertEquals("Wrong type", VersionRange.RIGHT_OPEN,
				range.getRightType());

		range = new VersionRange("(2.3.4,5.6.7]");
		assertEquals("Wrong type", VersionRange.RIGHT_CLOSED,
				range.getRightType());

		range = new VersionRange("2.3.4");
		assertEquals("Wrong type", VersionRange.RIGHT_OPEN,
				range.getRightType());

	}

	public void testIsEmpty() {
		Version version1 = new Version(1, 0, 0);
		Version version2 = new Version(1, 0, 0, "-");
		VersionRange range;

		range = new VersionRange('[', version1, version2, ')');
		assertFalse("range is not empty", range.isEmpty());

		range = new VersionRange('(', version1, version2, ']');
		assertFalse("range is not empty", range.isEmpty());

		range = new VersionRange('[', version1, version1, ']');
		assertFalse("range is not empty", range.isEmpty());

		range = new VersionRange('[', version1, version1, ')');
		assertTrue("range is empty", range.isEmpty());

		range = new VersionRange('(', version1, version1, ']');
		assertTrue("range is empty", range.isEmpty());

		range = new VersionRange('(', version1, version1, ')');
		assertTrue("range is empty", range.isEmpty());

		range = new VersionRange('[', version2, version1, ']');
		assertTrue("range is empty", range.isEmpty());

		range = new VersionRange('[', version2, null, ')');
		assertFalse("range is not empty", range.isEmpty());
	}

	public void testIsExact() {
		Version version1 = new Version(1, 0, 0);
		Version version2 = new Version(1, 0, 0, "-");
		Version version3 = new Version(1, 0, 0, "--");
		VersionRange range;

		range = new VersionRange('[', version1, version1, ']');
		assertTrue("range is exact", range.isExact());

		range = new VersionRange('[', version1, version1, ')');
		assertFalse("range is not exact", range.isExact());

		range = new VersionRange('(', version1, version1, ']');
		assertFalse("range is not exact", range.isExact());

		range = new VersionRange('(', version1, version1, ')');
		assertFalse("range is not exact", range.isExact());

		range = new VersionRange('[', version1, version2, ']');
		assertFalse("range is not exact", range.isExact());

		range = new VersionRange('[', version2, version1, ']');
		assertFalse("range is not exact", range.isExact());

		range = new VersionRange('[', version2, null, ')');
		assertFalse("range is not exact", range.isExact());

		range = new VersionRange('[', version2, null, ']');
		assertFalse("range is not exact", range.isExact());

		range = new VersionRange('[', version1, version2, ')');
		assertTrue("range is exact", range.isExact());

		range = new VersionRange('(', version1, version2, ']');
		assertTrue("range is exact", range.isExact());

		range = new VersionRange('(', version1, version3, ')');
		assertTrue("range is exact", range.isExact());
	}

	public void testToString() {
		Version version11 = new Version(2, 3, 4, "-");
		Version version12 = new Version(2, 3, 4);
		Version version21 = new Version(5, 6, 7, "-");
		Version version22 = new Version(5, 6, 7);
		VersionRange range;

		range = new VersionRange('[', version11, version21, ')');
		assertEquals("Wrong toString result", "[2.3.4.-,5.6.7.-)",
				range.toString());

		range = new VersionRange('[', version12, version22, ')');
		assertEquals("Wrong toString result", "[2.3.4,5.6.7)",
				range.toString());

		range = new VersionRange('[', version11, version21, ']');
		assertEquals("Wrong toString result", "[2.3.4.-,5.6.7.-]",
				range.toString());

		range = new VersionRange('[', version12, version22, ']');
		assertEquals("Wrong toString result", "[2.3.4,5.6.7]",
				range.toString());

		range = new VersionRange('(', version11, version21, ')');
		assertEquals("Wrong toString result", "(2.3.4.-,5.6.7.-)",
				range.toString());

		range = new VersionRange('(', version12, version22, ')');
		assertEquals("Wrong toString result", "(2.3.4,5.6.7)",
				range.toString());

		range = new VersionRange('(', version11, version21, ']');
		assertEquals("Wrong toString result", "(2.3.4.-,5.6.7.-]",
				range.toString());

		range = new VersionRange('(', version12, version22, ']');
		assertEquals("Wrong toString result", "(2.3.4,5.6.7]",
				range.toString());

		range = new VersionRange('[', version11, null, ')');
		assertEquals("Wrong toString result", "2.3.4.-", range.toString());

		range = new VersionRange('[', version12, null, ')');
		assertEquals("Wrong toString result", "2.3.4", range.toString());

		range = new VersionRange("[2.3.4,5.6.7)");
		assertEquals("Wrong toString result", "[2.3.4,5.6.7)",
				range.toString());

		range = new VersionRange("[2.3.4,5.6.7]");
		assertEquals("Wrong toString result", "[2.3.4,5.6.7]",
				range.toString());

		range = new VersionRange("(2.3.4,5.6.7)");
		assertEquals("Wrong toString result", "(2.3.4,5.6.7)",
				range.toString());

		range = new VersionRange("(2.3.4,5.6.7]");
		assertEquals("Wrong toString result", "(2.3.4,5.6.7]",
				range.toString());

		range = new VersionRange("2.3.4");
		assertEquals("Wrong toString result", "2.3.4", range.toString());

		range = new VersionRange("2.3.4.-");
		assertEquals("Wrong toString result", "2.3.4.-", range.toString());
	}

	public void testIncludes() {
		Version version11 = new Version(2, 3, 4, "-");
		Version version12 = new Version(2, 3, 4);
		Version version21 = new Version(5, 6, 7, "-");
		Version version22 = new Version(5, 6, 7);
		Version version3 = new Version(3, 4, 5);
		Version version4 = new Version(Integer.MAX_VALUE, Integer.MAX_VALUE,
				Integer.MAX_VALUE);
		VersionRange range;

		range = new VersionRange('[', version11, version22, ']');
		assertTrue("not included", range.includes(version11));
		assertFalse("included", range.includes(version12));
		assertFalse("included", range.includes(version21));
		assertTrue("not included", range.includes(version22));
		assertTrue("not included", range.includes(version3));
		assertFalse("included", range.includes(version4));

		range = new VersionRange('[', version11, version22, ')');
		assertTrue("not included", range.includes(version11));
		assertFalse("included", range.includes(version12));
		assertFalse("included", range.includes(version21));
		assertFalse("included", range.includes(version22));
		assertTrue("not included", range.includes(version3));
		assertFalse("included", range.includes(version4));

		range = new VersionRange('(', version11, version22, ']');
		assertFalse("included", range.includes(version11));
		assertFalse("included", range.includes(version12));
		assertFalse("included", range.includes(version21));
		assertTrue("not included", range.includes(version22));
		assertTrue("not included", range.includes(version3));
		assertFalse("included", range.includes(version4));

		range = new VersionRange('(', version11, version22, ')');
		assertFalse("included", range.includes(version11));
		assertFalse("included", range.includes(version12));
		assertFalse("included", range.includes(version21));
		assertFalse("included", range.includes(version22));
		assertTrue("not included", range.includes(version3));
		assertFalse("included", range.includes(version4));

		range = new VersionRange("[2.3.4,5.6.7)");
		assertTrue("not included", range.includes(version11));
		assertTrue("not included", range.includes(version12));
		assertFalse("included", range.includes(version21));
		assertFalse("included", range.includes(version22));
		assertTrue("not included", range.includes(version3));
		assertFalse("included", range.includes(version4));

		range = new VersionRange("[2.3.4,5.6.7]");
		assertTrue("not included", range.includes(version11));
		assertTrue("not included", range.includes(version12));
		assertFalse("included", range.includes(version21));
		assertTrue("not included", range.includes(version22));
		assertTrue("not included", range.includes(version3));
		assertFalse("included", range.includes(version4));

		range = new VersionRange("(2.3.4,5.6.7]");
		assertTrue("not included", range.includes(version11));
		assertFalse("included", range.includes(version12));
		assertFalse("included", range.includes(version21));
		assertTrue("not included", range.includes(version22));
		assertTrue("not included", range.includes(version3));
		assertFalse("included", range.includes(version4));

		range = new VersionRange("(2.3.4,5.6.7)");
		assertTrue("not included", range.includes(version11));
		assertFalse("included", range.includes(version12));
		assertFalse("included", range.includes(version21));
		assertFalse("included", range.includes(version22));
		assertTrue("not included", range.includes(version3));
		assertFalse("included", range.includes(version4));

		range = new VersionRange("[2.3.4.-,5.6.7.-)");
		assertTrue("not included", range.includes(version11));
		assertFalse("included", range.includes(version12));
		assertFalse("included", range.includes(version21));
		assertTrue("not included", range.includes(version22));
		assertTrue("not included", range.includes(version3));
		assertFalse("included", range.includes(version4));

		range = new VersionRange("(2.3.4.-,5.6.7.-]");
		assertFalse("included", range.includes(version11));
		assertFalse("included", range.includes(version12));
		assertTrue("not included", range.includes(version21));
		assertTrue("not included", range.includes(version22));
		assertTrue("not included", range.includes(version3));
		assertFalse("included", range.includes(version4));

		range = new VersionRange('[', version11, null, ')');
		assertTrue("not included", range.includes(version11));
		assertFalse("included", range.includes(version12));
		assertTrue("not included", range.includes(version21));
		assertTrue("not included", range.includes(version22));
		assertTrue("not included", range.includes(version3));
		assertTrue("not included", range.includes(version4));

		range = new VersionRange('(', version12, null, ')');
		assertTrue("not included", range.includes(version11));
		assertFalse("included", range.includes(version12));
		assertTrue("not included", range.includes(version21));
		assertTrue("not included", range.includes(version22));
		assertTrue("not included", range.includes(version3));
		assertTrue("not included", range.includes(version4));

		range = new VersionRange('[', version12, null, ')');
		assertTrue("not included", range.includes(version11));
		assertTrue("not included", range.includes(version12));
		assertTrue("not included", range.includes(version21));
		assertTrue("not included", range.includes(version22));
		assertTrue("not included", range.includes(version3));
		assertTrue("not included", range.includes(version4));

		range = new VersionRange("2.3.4");
		assertTrue("not included", range.includes(version11));
		assertTrue("not included", range.includes(version12));
		assertTrue("not included", range.includes(version21));
		assertTrue("not included", range.includes(version22));
		assertTrue("not included", range.includes(version3));
		assertTrue("not included", range.includes(version4));

		range = new VersionRange("2.3.4.-");
		assertTrue("not included", range.includes(version11));
		assertFalse("included", range.includes(version12));
		assertTrue("not included", range.includes(version21));
		assertTrue("not included", range.includes(version22));
		assertTrue("not included", range.includes(version3));
		assertTrue("not included", range.includes(version4));

		range = new VersionRange('[', Version.emptyVersion, null, ')');
		assertTrue("not included", range.includes(version11));
		assertTrue("not included", range.includes(version12));
		assertTrue("not included", range.includes(version21));
		assertTrue("not included", range.includes(version22));
		assertTrue("not included", range.includes(version3));
		assertTrue("not included", range.includes(version4));
		assertTrue("not included", range.includes(Version.emptyVersion));

		try {
			range.includes(null);
			fail("includes called with illegal argument");
		}
		catch (RuntimeException ex) {
			// This is an expected exception and may be ignored
		}
	}

	public void testFilterString() throws Exception {
		Version version11 = new Version(2, 3, 4, "-");
		Version version12 = new Version(2, 3, 4);
		Version version21 = new Version(5, 6, 7, "-");
		Version version22 = new Version(5, 6, 7);
		Version version3 = new Version(3, 4, 5);
		Version version4 = new Version(Integer.MAX_VALUE, Integer.MAX_VALUE,
				Integer.MAX_VALUE);
		VersionRange range;

		range = new VersionRange('[', version11, version22, ']');
		assertTrue("not included", includesByFilter(range, version11));
		assertFalse("included", includesByFilter(range, version12));
		assertFalse("included", includesByFilter(range, version21));
		assertTrue("not included", includesByFilter(range, version22));
		assertTrue("not included", includesByFilter(range, version3));
		assertFalse("included", includesByFilter(range, version4));
		assertFalse("included", includesByFilter(range, null));

		range = new VersionRange('[', version11, version22, ')');
		assertTrue("not included", includesByFilter(range, version11));
		assertFalse("included", includesByFilter(range, version12));
		assertFalse("included", includesByFilter(range, version21));
		assertFalse("included", includesByFilter(range, version22));
		assertTrue("not included", includesByFilter(range, version3));
		assertFalse("included", includesByFilter(range, version4));
		assertFalse("included", includesByFilter(range, null));

		range = new VersionRange('(', version11, version22, ']');
		assertFalse("included", includesByFilter(range, version11));
		assertFalse("included", includesByFilter(range, version12));
		assertFalse("included", includesByFilter(range, version21));
		assertTrue("not included", includesByFilter(range, version22));
		assertTrue("not included", includesByFilter(range, version3));
		assertFalse("included", includesByFilter(range, version4));
		assertFalse("included", includesByFilter(range, null));

		range = new VersionRange('(', version11, version22, ')');
		assertFalse("included", includesByFilter(range, version11));
		assertFalse("included", includesByFilter(range, version12));
		assertFalse("included", includesByFilter(range, version21));
		assertFalse("included", includesByFilter(range, version22));
		assertTrue("not included", includesByFilter(range, version3));
		assertFalse("included", includesByFilter(range, version4));
		assertFalse("included", includesByFilter(range, null));

		range = new VersionRange("[2.3.4,5.6.7)");
		assertTrue("not included", includesByFilter(range, version11));
		assertTrue("not included", includesByFilter(range, version12));
		assertFalse("included", includesByFilter(range, version21));
		assertFalse("included", includesByFilter(range, version22));
		assertTrue("not included", includesByFilter(range, version3));
		assertFalse("included", includesByFilter(range, version4));
		assertFalse("included", includesByFilter(range, null));

		range = new VersionRange("[2.3.4,5.6.7]");
		assertTrue("not included", includesByFilter(range, version11));
		assertTrue("not included", includesByFilter(range, version12));
		assertFalse("included", includesByFilter(range, version21));
		assertTrue("not included", includesByFilter(range, version22));
		assertTrue("not included", includesByFilter(range, version3));
		assertFalse("included", includesByFilter(range, version4));
		assertFalse("included", includesByFilter(range, null));

		range = new VersionRange("(2.3.4,5.6.7]");
		assertTrue("not included", includesByFilter(range, version11));
		assertFalse("included", includesByFilter(range, version12));
		assertFalse("included", includesByFilter(range, version21));
		assertTrue("not included", includesByFilter(range, version22));
		assertTrue("not included", includesByFilter(range, version3));
		assertFalse("included", includesByFilter(range, version4));
		assertFalse("included", includesByFilter(range, null));

		range = new VersionRange("(2.3.4,5.6.7)");
		assertTrue("not included", includesByFilter(range, version11));
		assertFalse("included", includesByFilter(range, version12));
		assertFalse("included", includesByFilter(range, version21));
		assertFalse("included", includesByFilter(range, version22));
		assertTrue("not included", includesByFilter(range, version3));
		assertFalse("included", includesByFilter(range, version4));
		assertFalse("included", includesByFilter(range, null));

		range = new VersionRange("[2.3.4.-,5.6.7.-)");
		assertTrue("not included", includesByFilter(range, version11));
		assertFalse("included", includesByFilter(range, version12));
		assertFalse("included", includesByFilter(range, version21));
		assertTrue("not included", includesByFilter(range, version22));
		assertTrue("not included", includesByFilter(range, version3));
		assertFalse("included", includesByFilter(range, version4));
		assertFalse("included", includesByFilter(range, null));

		range = new VersionRange("(2.3.4.-,5.6.7.-]");
		assertFalse("included", includesByFilter(range, version11));
		assertFalse("included", includesByFilter(range, version12));
		assertTrue("not included", includesByFilter(range, version21));
		assertTrue("not included", includesByFilter(range, version22));
		assertTrue("not included", includesByFilter(range, version3));
		assertFalse("included", includesByFilter(range, version4));
		assertFalse("included", includesByFilter(range, null));

		range = new VersionRange('[', version11, null, ')');
		assertTrue("not included", includesByFilter(range, version11));
		assertFalse("included", includesByFilter(range, version12));
		assertTrue("not included", includesByFilter(range, version21));
		assertTrue("not included", includesByFilter(range, version22));
		assertTrue("not included", includesByFilter(range, version3));
		assertTrue("not included", includesByFilter(range, version4));
		assertFalse("included", includesByFilter(range, null));

		range = new VersionRange('(', version12, null, ')');
		assertTrue("not included", includesByFilter(range, version11));
		assertFalse("included", includesByFilter(range, version12));
		assertTrue("not included", includesByFilter(range, version21));
		assertTrue("not included", includesByFilter(range, version22));
		assertTrue("not included", includesByFilter(range, version3));
		assertTrue("not included", includesByFilter(range, version4));
		assertFalse("included", includesByFilter(range, null));

		range = new VersionRange('[', version12, null, ')');
		assertTrue("not included", includesByFilter(range, version11));
		assertTrue("not included", includesByFilter(range, version12));
		assertTrue("not included", includesByFilter(range, version21));
		assertTrue("not included", includesByFilter(range, version22));
		assertTrue("not included", includesByFilter(range, version3));
		assertTrue("not included", includesByFilter(range, version4));
		assertFalse("included", includesByFilter(range, null));

		range = new VersionRange("2.3.4");
		assertTrue("not included", includesByFilter(range, version11));
		assertTrue("not included", includesByFilter(range, version12));
		assertTrue("not included", includesByFilter(range, version21));
		assertTrue("not included", includesByFilter(range, version22));
		assertTrue("not included", includesByFilter(range, version3));
		assertTrue("not included", includesByFilter(range, version4));
		assertFalse("included", includesByFilter(range, null));

		range = new VersionRange("2.3.4.-");
		assertTrue("not included", includesByFilter(range, version11));
		assertFalse("included", includesByFilter(range, version12));
		assertTrue("not included", includesByFilter(range, version21));
		assertTrue("not included", includesByFilter(range, version22));
		assertTrue("not included", includesByFilter(range, version3));
		assertTrue("not included", includesByFilter(range, version4));
		assertFalse("included", includesByFilter(range, null));

		range = new VersionRange('[', Version.emptyVersion, null, ')');
		assertTrue("not included", includesByFilter(range, version11));
		assertTrue("not included", includesByFilter(range, version12));
		assertTrue("not included", includesByFilter(range, version21));
		assertTrue("not included", includesByFilter(range, version22));
		assertTrue("not included", includesByFilter(range, version3));
		assertTrue("not included", includesByFilter(range, version4));
		assertTrue("not included",
				includesByFilter(range, Version.emptyVersion));
		assertFalse("included", includesByFilter(range, null));

	}

	private boolean includesByFilter(VersionRange range, Version version)
			throws Exception {
		String attributeName = getName();
		String filterString = range.toFilterString(attributeName);
		// System.out.println(range + " => " + filterString);
		Filter filter = FrameworkUtil.createFilter(filterString);
		Map<String, Version> map = new HashMap<String, Version>();
		if (version != null) {
			map.put(attributeName, version);
		}
		return filter.matches(map);
	}

	public void testFilterStringBadArguments() {
		VersionRange range;

		range = new VersionRange('[', Version.emptyVersion,
				Version.emptyVersion, ']');

		// Bad characters '=', '>', '<', '~', '(' or ')'
		try {
			range.toFilterString(null);
			fail("filter string created with illegal arguments");
		}
		catch (RuntimeException ex) {
			// This is an expected exception and may be ignored
		}

		try {
			range.toFilterString("");
			fail("filter string created with illegal arguments");
		}
		catch (IllegalArgumentException ex) {
			// This is an expected exception and may be ignored
		}

		try {
			range.toFilterString("b~d");
			fail("filter string created with illegal arguments");
		}
		catch (IllegalArgumentException ex) {
			// This is an expected exception and may be ignored
		}

		try {
			range.toFilterString("ba)");
			fail("filter string created with illegal arguments");
		}
		catch (IllegalArgumentException ex) {
			// This is an expected exception and may be ignored
		}

		try {
			range.toFilterString("(ad");
			fail("filter string created with illegal arguments");
		}
		catch (IllegalArgumentException ex) {
			// This is an expected exception and may be ignored
		}

		try {
			range.toFilterString("b=d");
			fail("filter string created with illegal arguments");
		}
		catch (IllegalArgumentException ex) {
			// This is an expected exception and may be ignored
		}

		try {
			range.toFilterString("<ad");
			fail("filter string created with illegal arguments");
		}
		catch (IllegalArgumentException ex) {
			// This is an expected exception and may be ignored
		}

		try {
			range.toFilterString("ba>");
			fail("filter string created with illegal arguments");
		}
		catch (IllegalArgumentException ex) {
			// This is an expected exception and may be ignored
		}

	}

	public void testIntersection() {
		Version version11 = new Version(2, 3, 4, "-");
		Version version12 = new Version(2, 3, 4);
		Version version21 = new Version(5, 6, 7, "-");
		Version version22 = new Version(5, 6, 7);
		Version version3 = new Version(3, 4, 5);
		VersionRange range1;
		VersionRange range2;
		VersionRange range3;
		VersionRange expected;

		range1 = new VersionRange('[', version11, version22, ']');
		expected = range1;
		assertEquals("wrong intersection", expected,
				range1.intersection((VersionRange[]) null));

		range1 = new VersionRange('[', version11, version22, ']');
		expected = range1;
		assertEquals("wrong intersection", expected, range1.intersection());

		range1 = new VersionRange('[', version11, version22, ']');
		range2 = range1;
		expected = range1;
		assertEquals("wrong intersection", expected,
				range1.intersection(range2));
		assertEquals("wrong intersection", expected,
				range2.intersection(range1));

		range1 = new VersionRange('[', version11, version22, ')');
		range2 = range1;
		expected = range1;
		assertEquals("wrong intersection", expected,
				range1.intersection(range2));
		assertEquals("wrong intersection", expected,
				range2.intersection(range1));

		range1 = new VersionRange('(', version11, version22, ']');
		range2 = range1;
		expected = range1;
		assertEquals("wrong intersection", expected,
				range1.intersection(range2));
		assertEquals("wrong intersection", expected,
				range2.intersection(range1));

		range1 = new VersionRange('(', version11, version22, ')');
		range2 = range1;
		expected = range1;
		assertEquals("wrong intersection", expected,
				range1.intersection(range2));
		assertEquals("wrong intersection", expected,
				range2.intersection(range1));

		range1 = new VersionRange('[', version11, version22, ']');
		range2 = new VersionRange('[', version11, version22, ')');
		expected = range2;
		assertEquals("wrong intersection", expected,
				range1.intersection(range2));
		assertEquals("wrong intersection", expected,
				range2.intersection(range1));

		range1 = new VersionRange('[', version11, version22, ']');
		range2 = new VersionRange('(', version11, version22, ']');
		expected = range2;
		assertEquals("wrong intersection", expected,
				range1.intersection(range2));
		assertEquals("wrong intersection", expected,
				range2.intersection(range1));

		range1 = new VersionRange('[', version11, version22, ']');
		range2 = new VersionRange('(', version11, version22, ')');
		expected = range2;
		assertEquals("wrong intersection", expected,
				range1.intersection(range2));
		assertEquals("wrong intersection", expected,
				range2.intersection(range1));

		range1 = new VersionRange('[', version11, version22, ']');
		range2 = new VersionRange('[', version12, version21, ']');
		expected = range1;
		assertEquals("wrong intersection", expected,
				range1.intersection(range2));
		assertEquals("wrong intersection", expected,
				range2.intersection(range1));

		range1 = new VersionRange('[', version12, version22, ']');
		range2 = new VersionRange('[', version11, version21, ']');
		expected = new VersionRange('[', version11, version22, ']');
		assertEquals("wrong intersection", expected,
				range1.intersection(range2));
		assertEquals("wrong intersection", expected,
				range2.intersection(range1));

		range1 = new VersionRange('[', version12, version22, ')');
		range2 = new VersionRange('[', version11, null, ')');
		expected = new VersionRange('[', version11, version22, ')');
		assertEquals("wrong intersection", expected,
				range1.intersection(range2));
		assertEquals("wrong intersection", expected,
				range2.intersection(range1));

		range1 = new VersionRange('[', version22, version12, ')');
		range2 = new VersionRange('[', version11, null, ')');
		assertTrue("range is not empty", range1.isEmpty());
		assertTrue("range is not empty", range1.intersection(range2).isEmpty());
		assertTrue("range is not empty", range2.intersection(range1).isEmpty());

		range1 = new VersionRange('[', version22, version12, ')');
		range2 = new VersionRange('[', version11, version21, ')');
		range3 = new VersionRange('(', version12, version3, ']');
		assertTrue("range is not empty", range1.isEmpty());
		assertTrue("range is not empty", range1.intersection(range2, range3)
				.isEmpty());
		assertTrue("range is not empty", range2.intersection(range1, range3)
				.isEmpty());
		assertTrue("range is not empty", range3.intersection(range1, range2)
				.isEmpty());

		range1 = new VersionRange('[', version12, version22, ')');
		range2 = new VersionRange('[', version11, version21, ')');
		range3 = new VersionRange('(', version12, version3, ']');
		expected = new VersionRange('[', version11, version3, ']');
		assertEquals("wrong intersection", expected,
				range1.intersection(range2, range3));
		assertEquals("wrong intersection", expected,
				range2.intersection(range1, range3));
		assertEquals("wrong intersection", expected,
				range3.intersection(range1, range2));

		range1 = new VersionRange('[', version12, version11, ']');
		range2 = new VersionRange('[', version22, version21, ']');
		assertFalse("range is empty", range1.isEmpty());
		assertFalse("range is empty", range2.isEmpty());
		assertTrue("range is not empty", range1.intersection(range2).isEmpty());
		assertTrue("range is not empty", range2.intersection(range1).isEmpty());

	}
}
