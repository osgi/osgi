/*
 * Copyright (c) OSGi Alliance (2010, 2020). All Rights Reserved.
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
package org.osgi.test.cases.framework.junit.version;

import org.osgi.framework.Version;

import junit.framework.TestCase;

/**
 * Tests for the Version class.
 */
public class VersionTests extends TestCase {
	/**
	 * Test the Version constructor with legal parameters
	 *
	 * @spec Version.<init>(int,int,int)
	 */
	public void testConstructors() {
		/**
		 * Test the Version constructor with legal parameters
		 *
		 * @spec Version.<init>(int,int,int)
		 */
		new Version(0, 0, 0);

		new Version(0, 0, 0,
				"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_-0123456789");

		new Version("0.0.0");
		Version.parseVersion("0.0.0");
		Version.valueOf("0.0.0");
		new Version(
				"0.0.0.ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_-0123456789");
		Version.parseVersion("0.0.0.ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_-0123456789");
		Version.valueOf("0.0.0.ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_-0123456789");

	}

	public void testConstructorsBadArguments() {
		/**
		 * Test the Version constructor with illegal parameters
		 *
		 * @spec Version.<init>(int,int,int)
		 */
		try {
			new Version(-1, 2, 3);
			fail("Version created with illegal arguments");
		}
		catch (IllegalArgumentException ex) {
			// This is an expected exception and may be ignored
		}
		try {
			new Version(1, -2, 3);
			fail("Version created with illegal arguments");
		}
		catch (IllegalArgumentException ex) {
			// This is an expected exception and may be ignored
		}
		try {
			new Version(1, 2, -3);
			fail("Version created with illegal arguments");
		}
		catch (IllegalArgumentException ex) {
			// This is an expected exception and may be ignored
		}
		try {
			new Version(1, 2, 3, "4.5");
			fail("Version created with illegal arguments");
		}
		catch (IllegalArgumentException ex) {
			// This is an expected exception and may be ignored
		}

		try {
			new Version(null);
			fail("Version created with illegal arguments");
		}
		catch (RuntimeException ex) {
			// This is an expected exception and may be ignored
		}

		try {
			Version.valueOf(null);
			fail("Version created with null argument");
		} catch (RuntimeException ex) {
			// This is an expected exception and may be ignored
		}

		try {
			new Version("");
			fail("Version created with illegal arguments");
		}
		catch (IllegalArgumentException ex) {
			// This is an expected exception and may be ignored
		}

		testConstructorsBadArguments("-1.2.3");
		testConstructorsBadArguments("1.-2.3");
		testConstructorsBadArguments("1.2.-3");
		testConstructorsBadArguments("1.2.3.4.5");

		testConstructorsBadArguments("1.2.3.");
		testConstructorsBadArguments("1.2.3-");
		testConstructorsBadArguments("1.2.3-4.5");
		testConstructorsBadArguments("a.2.3");
		testConstructorsBadArguments("1.b.3");
		testConstructorsBadArguments("1.2.c");
		testConstructorsBadArguments("1.");
		testConstructorsBadArguments("1.2.");
	}

	private void testConstructorsBadArguments(String arg) {
		try {
			new Version(arg);
			fail("Version created with illegal arguments");
		} catch (IllegalArgumentException ex) {
			// This is an expected exception and may be ignored
		}
		try {
			Version.parseVersion(arg);
			fail("Version created with illegal arguments");
		} catch (IllegalArgumentException ex) {
			// This is an expected exception and may be ignored
		}
		try {
			Version.valueOf(arg);
			fail("Version created with illegal arguments");
		} catch (IllegalArgumentException ex) {
			// This is an expected exception and may be ignored
		}
	}

	@SuppressWarnings("unlikely-arg-type")
	public void testEquals() {
		Version version1;
		Version version2;

		version1 = new Version(0, 0, 0);
		version2 = new Version(0, 0, 0);

		assertEquals("Testing the method equals() with the same versions",
				version1, version2);

		/**
		 * Test the method equals() with the same versions
		 *
		 * @spec Version.equals(Object)
		 */

		version1 = new Version(0, 0, 0, "a");
		version2 = new Version(0, 0, 0, "a");

		assertEquals("Testing the method equals() with the same versions",
				version1, version1);
		assertEquals("Testing the method equals() with the same versions",
				version2, version2);
		assertEquals("Testing the method equals() with the same versions",
				version1, version2);
		assertEquals("Testing the method equals() with the same versions",
				version2, version1);

		/**
		 * Test the method equals() with different versions
		 *
		 * @spec Version.equals(Object)
		 */

		version1 = new Version(0, 0, 0);
		version2 = new Version(1, 0, 0);

		assertFalse("Testing the method equals() with different versions",
				version1.equals(version2));
		assertFalse("Testing the method equals() with different versions",
				version2.equals(version1));

		/**
		 * Test the method equals() with different versions
		 *
		 * @spec Version.equals(Object)
		 */

		version1 = new Version(0, 0, 0);
		version2 = new Version(0, 1, 0);

		assertFalse("Testing the method equals() with different versions",
				version1.equals(version2));
		assertFalse("Testing the method equals() with different versions",
				version2.equals(version1));

		/**
		 * Test the method equals() with different versions
		 *
		 * @spec Version.equals(Object)
		 */

		version1 = new Version(0, 0, 0);
		version2 = new Version(0, 0, 1);

		assertFalse("Testing the method equals() with different versions",
				version1.equals(version2));
		assertFalse("Testing the method equals() with different versions",
				version2.equals(version1));

		/**
		 * Test the method equals() with different versions
		 *
		 * @spec Version.equals(Object)
		 */

		version1 = new Version(0, 0, 0, "a");
		version2 = new Version(0, 0, 0, "b");

		assertFalse("Testing the method equals() with different versions",
				version1.equals(version2));
		assertFalse("Testing the method equals() with different versions",
				version2.equals(version1));

		/**
		 * Test the method equals() with different versions
		 *
		 * @spec Version.equals(Object)
		 */

		version1 = new Version(0, 0, 0, "a");
		version2 = new Version(1, 1, 1, "b");

		assertFalse("Testing the method equals() with different versions",
				version1.equals(version2));
		assertFalse("Testing the method equals() with different versions",
				version2.equals(version1));

		version1 = new Version("0.0.0.a");
		version2 = Version.valueOf(" 0.0.0.a  ");
		assertEquals("Testing the method equals() with the same versions",
				version1, version2);
		assertEquals("Testing the method equals() with the same versions",
				version2, version1);

		version1 = new Version("0.0.0.a");
		version2 = null;
		assertFalse("Testing the method equals() with null",
				version1.equals(version2));
		assertFalse("Testing the method equals() with null",
				version1.equals(this));

	}

	/**
	 * Test the method hashCode() when the equals() returns true
	 *
	 * @spec Version.hashCode();
	 */
	public void testHashCode() throws Exception {
		Version version1;
		Version version2;

		version1 = new Version(0, 0, 0, "a");
		version2 = new Version(0, 0, 0, "a");

		assertEquals(
				"The method hashCode() has different return values when the method equals() returns true for two Version instances",
				version1.hashCode(), version2.hashCode());

		version1 = new Version(0, 0, 0);
		version2 = new Version(1, 0, 0);

		assertFalse(
				"The method hashCode() has the same return value when the method equals() returns false for two Version instances",
				version1.hashCode() == version2.hashCode());

	}

	public void testGetMajor() {
		Version version;

		version = new Version(1, 2, 3);

		assertEquals(
				"Testing the method getMajor() using the constructor Version(int,int,int)",
				1, version.getMajor());

		version = new Version("1.2.3");

		assertEquals(
				"Testing the method getMajor() using the constructor Version(String)",
				1, version.getMajor());
	}

	public void testGetMinor() {
		Version version;

		version = new Version(1, 2, 3);

		assertEquals(
				"Testing the method getMinor() using the constructor Version(int,int,int)",
				2, version.getMinor());

		version = new Version("1.2.3");

		assertEquals(
				"Testing the method getMinor() using the constructor Version(String)",
				2, version.getMinor());
	}

	public void testGetMicro() {
		Version version;

		version = new Version(1, 2, 3);

		assertEquals(
				"Testing the method getMicro() using the constructor Version(int,int,int)",
				3, version.getMicro());

		version = new Version("1.2.3");

		assertEquals(
				"Testing the method getMicro() using the constructor Version(String)",
				3, version.getMicro());
	}

	/**
	 * Test the method getQualifier() using the constructor
	 * Version(int,int,int,String)
	 *
	 * @spec Version.getQualifier()
	 */
	public void testGetQualifier() throws Exception {
		Version version;

		version = new Version(1, 1, 1, "a");

		assertEquals(
				"Testing the method getQualifier() using the constructor Version(int,int,int,String)",
				"a", version.getQualifier());

		version = new Version(1, 1, 1, null);

		assertEquals(
				"Testing the method getQualifier() using the constructor Version(int,int,int,String)",
				"", version.getQualifier());

		version = new Version("1.1.1.a");

		assertEquals(
				"Testing the method getQualifier() using the constructor Version(String)",
				"a", version.getQualifier());

		version = new Version("1.1.1");

		assertEquals(
				"Testing the method getQualifier() using the constructor Version(String)",
				"", version.getQualifier());
	}

	@SuppressWarnings("rawtypes")
	public void testCompareTo() {
		/**
		 * Test the method compareTo() with first version number less than
		 * second version number
		 *
		 * @spec Version.compareTo(Version);
		 */
		Version version1;
		Version version2;

		version1 = new Version(1, 1, 1);
		version2 = new Version(2, 1, 1);

		assertTrue(
				"Testing the method compareTo() with first version number less than second version number",
				version1.compareTo(version2) < 0);
		assertTrue(
				"Testing the method compareTo() with first version number less than second version number",
				version2.compareTo(version1) > 0);

		/**
		 * Test the method compareTo() with first version number greater than
		 * second version number
		 *
		 * @spec Version.compareTo(Version);
		 */

		version1 = new Version(2, 1, 1);
		version2 = new Version(1, 1, 1);

		assertTrue(
				"Testing the method compareTo() with first version number greater than second version number",
				version1.compareTo(version2) > 0);
		assertTrue(
				"Testing the method compareTo() with first version number greater than second version number",
				version2.compareTo(version1) < 0);

		/**
		 * Test the method compareTo() with same version numbers
		 *
		 * @spec Version.compareTo(Version);
		 */

		version1 = new Version(1, 1, 1);
		version2 = version1;

		assertTrue("Testing the method compareTo() with same version numbers",
				version1.compareTo(version2) == 0);
		assertTrue("Testing the method compareTo() with same version numbers",
				version2.compareTo(version1) == 0);

		version1 = new Version(1, 1, 1);
		version2 = new Version(1, 1, 1);

		assertTrue("Testing the method compareTo() with same version numbers",
				version1.compareTo(version2) == 0);
		assertTrue("Testing the method compareTo() with same version numbers",
				version2.compareTo(version1) == 0);

		version1 = new Version(1, 1, 1, "a");
		version2 = new Version(1, 1, 1, "a");

		assertTrue("Testing the method compareTo() with same version numbers",
				version1.compareTo(version2) == 0);
		assertTrue("Testing the method compareTo() with same version numbers",
				version2.compareTo(version1) == 0);

		version1 = new Version(1, 1, 1, "");
		version2 = new Version(1, 1, 1, "b");

		assertTrue("Testing the method compareTo() with different qualifiers",
				version1.compareTo(version2) < 0);
		assertTrue("Testing the method compareTo() with different qualifiers",
				version2.compareTo(version1) > 0);

		version1 = new Version(1, 1, 1, null);
		version2 = new Version(1, 1, 1, "a");

		assertTrue("Testing the method compareTo() with different qualifiers",
				version1.compareTo(version2) < 0);
		assertTrue("Testing the method compareTo() with different qualifiers",
				version2.compareTo(version1) > 0);

		/**
		 * Test the method compareTo() with an incorrect object
		 *
		 * @spec Version.compareTo(Version);
		 */
		Object incorrect = "";
		version1 = new Version(1, 1, 1);
		try {
			@SuppressWarnings({
					"unused", "unchecked"
			})
			int result = ((Comparable) version1).compareTo(incorrect);
			fail("Testing the method compareTo() with an incorrect object");
		}
		catch (ClassCastException ex) {
			// This is an expected exception and can be ignored
		}
	}

	public void testConstantsValues() {
		assertEquals("emptyVersion not equal to 0.0.0", new Version(0, 0, 0),
				Version.emptyVersion);
		assertEquals("emptyVersion has different hashCode than 0.0.0",
				new Version(0, 0, 0).hashCode(),
				Version.emptyVersion.hashCode());
		assertSame("not emptyVersion", Version.parseVersion(null),
				Version.emptyVersion);
		assertSame("not emptyVersion", Version.parseVersion(""),
				Version.emptyVersion);
		assertSame("not emptyVersion", Version.parseVersion("	"),
				Version.emptyVersion);
		assertSame("not emptyVersion", Version.valueOf(""),
				Version.emptyVersion);
		assertSame("not emptyVersion", Version.valueOf("	"),
				Version.emptyVersion);
	}

	public void testToString() {
		Version version;

		version = new Version(1, 2, 3);
		assertEquals("Wrong toString result", "1.2.3", version.toString());

		version = new Version(1, 2, 3, "a");
		assertEquals("Wrong toString result", "1.2.3.a", version.toString());

		version = new Version(1, 2, 3, null);
		assertEquals("Wrong toString result", "1.2.3", version.toString());

		version = new Version("1.2.3");
		assertEquals("Wrong toString result", "1.2.3", version.toString());

		version = new Version("1.2.3.a");
		assertEquals("Wrong toString result", "1.2.3.a", version.toString());
	}
}
