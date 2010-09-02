/*
 * Copyright (c) OSGi Alliance (2010). All Rights Reserved.
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

import junit.framework.TestCase;

import org.osgi.framework.Version;

/**
 * Test for the Version class.
 */
public class VersionTests extends TestCase {
	/**
	 * Test the Version constructor with legal parameters
	 * 
	 * @spec Version.<init>(int,int,int)
	 */
	public void testVersionConstructors() {
		new Version(0, 0, 0);

		new Version(0, 0, 0, "a");

		/**
		 * Test the Version constructor with legal parameters
		 * 
		 * @spec Version.<init>(int,int,int)
		 */

		new Version("0.0.0");

		/**
		 * Test the Version constructor with illegal parameters
		 * 
		 * @spec Version.<init>(int,int,int)
		 */
		try {
			new Version(-1, -1, -1);
			fail("Version created with illegal constructors");
		}
		catch (IllegalArgumentException ex) {
			// This is an expected exception and may be ignored
		}

		/**
		 * Test the Version constructor with legal parameters
		 * 
		 * @spec Version.<init>(int,int,int)
		 */
		try {
			new Version(null);
			fail("Version created with illegal constructors");
		}
		catch (Exception ex) {
			// This is an expected exception and may be ignored
		}

		/**
		 * Test the Version constructor with legal parameters
		 * 
		 * @spec Version.<init>(int,int,int)
		 */
		try {
			new Version("");
			fail("Version created with illegal constructors");
		}
		catch (IllegalArgumentException ex) {
			// This is an expected exception and may be ignored
		}

		assertSame("not emptyVersion", Version.parseVersion(null),
				Version.emptyVersion);
		assertSame("not emptyVersion", Version.parseVersion(""),
				Version.emptyVersion);
	}

	public void testVersionEquals() {
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
				version1, version2);

		/**
		 * Test the method equals() with different versions
		 * 
		 * @spec Version.equals(Object)
		 */

		version1 = new Version(0, 0, 0);
		version2 = new Version(1, 0, 0);

		if (version1.equals(version2)) {
			fail("Testing the method equals() with different versions");
		}

		/**
		 * Test the method equals() with different versions
		 * 
		 * @spec Version.equals(Object)
		 */

		version1 = new Version(0, 0, 0);
		version2 = new Version(0, 1, 0);

		if (version1.equals(version2)) {
			fail("Testing the method equals() with different versions");
		}

		/**
		 * Test the method equals() with different versions
		 * 
		 * @spec Version.equals(Object)
		 */

		version1 = new Version(0, 0, 0);
		version2 = new Version(0, 0, 1);

		if (version1.equals(version2)) {
			fail("Testing the method equals() with different versions");
		}

		/**
		 * Test the method equals() with different versions
		 * 
		 * @spec Version.equals(Object)
		 */

		version1 = new Version(0, 0, 0, "a");
		version2 = new Version(0, 0, 0, "b");

		if (version1.equals(version2)) {
			fail("Testing the method equals() with different versions");
		}

		/**
		 * Test the method equals() with different versions
		 * 
		 * @spec Version.equals(Object)
		 */

		version1 = new Version(0, 0, 0, "a");
		version2 = new Version(1, 1, 1, "b");

		if (version1.equals(version2)) {
			fail("Testing the method equals() with different versions");
		}
	}

	/**
	 * Test the method hashCode() when the equals() returns true
	 * 
	 * @spec Version.hashCode();
	 */
	public void testVersionHashCode() throws Exception {
		Version version1;
		Version version2;

		version1 = new Version(0, 0, 0);
		version2 = new Version(0, 0, 0);

		assertEquals(
				"The method hashCode() has different return values when the method equals() returns true for two Version instances",
				version1.hashCode(), version2.hashCode());
		version1 = new Version(0, 0, 0);
		version2 = new Version(1, 0, 0);

		if (version1.hashCode() == version2.hashCode()) {
			fail("The method hashCode() has the same return value when the method equals() returns false for two Version instances");
		}
	}

	public void testVersionGetMajor() {
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

	public void testVersionGetMinor() {
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

	public void testVersionGetMicro() {
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
	public void testVersionGetQualifier() throws Exception {
		Version version;

		version = new Version(1, 1, 1, "a");

		assertEquals(
				"Testing the method getQualifier() using the constructor Version(int,int,int,String)",
				"a", version.getQualifier());

		version = new Version("1.1.1.a");

		assertEquals(
				"Testing the method getQualifier using the constructor Version(String)",
				"a", version.getQualifier());
	}

	public void testVersionCompareTo() {
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

		if (version1.compareTo(version2) >= 0) {
			fail("Testing the method compareTo() with first version number less than second version number");
		}

		/**
		 * Test the method compareTo() with first version number greater than
		 * second version number
		 * 
		 * @spec Version.compareTo(Version);
		 */

		version1 = new Version(2, 1, 1);
		version2 = new Version(1, 1, 1);

		if (version1.compareTo(version2) <= 0) {
			fail("Testing the method compareTo() with first version number greater than second version number");
		}

		/**
		 * Test the method compareTo() with same version numbers
		 * 
		 * @spec Version.compareTo(Version);
		 */

		version1 = new Version(1, 1, 1);
		version2 = new Version(1, 1, 1);

		if (version1.compareTo(version2) != 0) {
			fail("Testing the method compareTo() with same version numbers");
		}

		/**
		 * Test the method compareTo() with an incorrect object
		 * 
		 * @spec Version.compareTo(Version);
		 */
		String incorrect;
		Version version;

		incorrect = "";
		version = new Version(1, 1, 1);

		try {
			version.compareTo(incorrect);
			fail("Testing the method compareTo() with an incorrect object");
		}
		catch (ClassCastException ex) {
			// This is an expected exception and can be ignored
		}
	}

	public void testVersionConstantsValues() {
		assertEquals("emptyVersion not equal to 0.0.0", new Version(0, 0, 0),
				Version.emptyVersion);
	}
}