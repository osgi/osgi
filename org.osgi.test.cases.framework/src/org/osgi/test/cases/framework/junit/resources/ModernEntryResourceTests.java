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
package org.osgi.test.cases.framework.junit.resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.framework.Bundle;
import org.osgi.test.support.OSGiTestCase;

/**
 * Tests for the modern stream-based Bundle entry methods added in OSGi Core
 * 1.11.
 */
public class ModernEntryResourceTests extends OSGiTestCase {
	Bundle	testBundle;
	Bundle	testFragment;

	protected void setUp() throws Exception {
		super.setUp();
		if (getName().startsWith("testFragment"))
			testFragment = install("resources.tb2.jar");
		testBundle = install("resources.tb1.jar");
	}

	/**
	 * Test that entry() returns Optional with URL when entry exists
	 */
	public void testEntryPresent() {
		Optional<URL> result = testBundle.entry("resources/data.txt");
		assertTrue("entry() should return present Optional for existing entry",
				result.isPresent());
		assertURL(result.get());
	}

	/**
	 * Test that entry() returns empty Optional when entry does not exist
	 */
	public void testEntryAbsent() {
		Optional<URL> result = testBundle.entry("DoesNotExist");
		assertFalse(
				"entry() should return empty Optional for non-existent entry",
				result.isPresent());
	}

	/**
	 * Test that entry() returns present Optional for root
	 */
	public void testEntryRoot() {
		Optional<URL> root = testBundle.entry("/");
		assertTrue("entry() should return present Optional for root",
				root.isPresent());
	}

	/**
	 * Test that entry() never returns null
	 */
	public void testEntryNeverNull() {
		assertNotNull("entry() should never return null",
				testBundle.entry("DoesNotExist"));
		assertNotNull("entry() should never return null for existing entry",
				testBundle.entry("resources/data.txt"));
	}

	/**
	 * Test that entry() handles IllegalStateException by returning empty
	 * Optional
	 */
	public void testEntryUninstalledBundle() throws Exception {
		Bundle tempBundle = install("resources.tb1.jar");
		tempBundle.uninstall();
		Optional<URL> result = tempBundle.entry("resources/data.txt");
		assertNotNull("entry() should return non-null Optional", result);
		assertFalse(
				"entry() should return empty Optional for uninstalled bundle",
				result.isPresent());
	}

	/**
	 * Test that entryPaths() returns stream with paths when entries exist
	 */
	public void testEntryPathsPresent() {
		Stream<String> paths = testBundle.entryPaths("resources");
		assertNotNull("entryPaths() should never return null", paths);
		List<String> pathList = paths.collect(Collectors.toList());
		assertTrue("entryPaths() should return paths for resources directory",
				pathList.size() > 0);
		assertTrue("Should contain resources/data.txt",
				pathList.contains("resources/data.txt"));
		assertTrue("Should contain resources/data.xml",
				pathList.contains("resources/data.xml"));
	}

	/**
	 * Test that entryPaths() returns empty stream when no entries exist
	 */
	public void testEntryPathsAbsent() {
		Stream<String> paths = testBundle.entryPaths("doesNotExist");
		assertNotNull("entryPaths() should never return null", paths);
		List<String> pathList = paths.collect(Collectors.toList());
		assertEquals("entryPaths() should return empty stream for non-existent path", 0, pathList.size());
	}

	/**
	 * Test that entryPaths() never returns null
	 */
	public void testEntryPathsNeverNull() {
		assertNotNull("entryPaths() should never return null",
				testBundle.entryPaths("doesNotExist"));
		assertNotNull("entryPaths() should never return null for existing path",
				testBundle.entryPaths("resources"));
	}

	/**
	 * Test that entryPaths() handles IllegalStateException by returning empty
	 * stream
	 */
	public void testEntryPathsUninstalledBundle() throws Exception {
		Bundle tempBundle = install("resources.tb1.jar");
		tempBundle.uninstall();
		Stream<String> paths = tempBundle.entryPaths("resources");
		assertNotNull("entryPaths() should return non-null Stream", paths);
		List<String> pathList = paths.collect(Collectors.toList());
		assertEquals(
				"entryPaths() should return empty stream for uninstalled bundle",
				0, pathList.size());
	}

	/**
	 * Test that entries() returns stream with URLs when entries exist
	 */
	public void testEntriesPresent() {
		Stream<URL> entries = testBundle.entries("resources", "*.txt", false);
		assertNotNull("entries() should never return null", entries);
		List<URL> entryList = entries.collect(Collectors.toList());
		assertEquals("entries() should return 1 txt file", 1,
				entryList.size());
		assertURL(entryList.get(0));
	}

	/**
	 * Test that entries() with recursion finds more entries
	 */
	public void testEntriesRecursive() {
		Stream<URL> entries = testBundle.entries("resources", "*.txt", true);
		assertNotNull("entries() should never return null", entries);
		List<URL> entryList = entries.collect(Collectors.toList());
		assertTrue("entries() with recursion should find multiple txt files",
				entryList.size() > 1);
		for (URL url : entryList) {
			assertURL(url);
		}
	}

	/**
	 * Test that entries() returns empty stream when no entries match
	 */
	public void testEntriesAbsent() {
		Stream<URL> entries = testBundle.entries("resources", "doesNotExist",
				false);
		assertNotNull("entries() should never return null", entries);
		List<URL> entryList = entries.collect(Collectors.toList());
		assertEquals(
				"entries() should return empty stream for non-matching pattern",
				0, entryList.size());
	}

	/**
	 * Test that entries() never returns null
	 */
	public void testEntriesNeverNull() {
		assertNotNull("entries() should never return null",
				testBundle.entries("doesNotExist", "*", false));
		assertNotNull("entries() should never return null for existing path",
				testBundle.entries("resources", "*.txt", false));
	}

	/**
	 * Test that entries() handles IllegalStateException by returning empty
	 * stream
	 */
	public void testEntriesUninstalledBundle() throws Exception {
		Bundle tempBundle = install("resources.tb1.jar");
		tempBundle.uninstall();
		Stream<URL> entries = tempBundle.entries("resources", "*.txt", false);
		assertNotNull("entries() should return non-null Stream", entries);
		List<URL> entryList = entries.collect(Collectors.toList());
		assertEquals(
				"entries() should return empty stream for uninstalled bundle",
				0, entryList.size());
	}

	/**
	 * Test that entries() with null pattern works like "*"
	 */
	public void testEntriesNullPattern() {
		Stream<URL> entries1 = testBundle.entries("resources", null, false);
		Stream<URL> entries2 = testBundle.entries("resources", "*", false);
		List<URL> list1 = entries1.collect(Collectors.toList());
		List<URL> list2 = entries2.collect(Collectors.toList());
		assertEquals(
				"entries() with null pattern should behave like '*' pattern",
				list2.size(), list1.size());
	}

	/**
	 * Test entryPaths() with fragments attached
	 */
	public void testFragmentEntryPaths() {
		Stream<String> paths = testBundle.entryPaths("resources");
		assertNotNull("entryPaths() should never return null", paths);
		List<String> pathList = paths.collect(Collectors.toList());
		assertTrue("entryPaths() should return paths", pathList.size() > 0);
	}

	/**
	 * Test entries() with fragments attached
	 */
	public void testFragmentEntries() {
		Stream<URL> entries = testBundle.entries("resources", null, false);
		assertNotNull("entries() should never return null", entries);
		List<URL> entryList = entries.collect(Collectors.toList());
		// With fragment attached, should have more entries
		assertTrue("entries() should return entries with fragment",
				entryList.size() > 0);
	}

	protected void tearDown() throws Exception {
		testBundle.uninstall();
		if (testFragment != null)
			testFragment.uninstall();
	}

	private void assertURL(URL url) {
		assertNotNull("Unexpected null url", url);
		String expected = url.getPath();
		int lastSlash = expected.lastIndexOf('/');
		assertTrue(lastSlash >= 0);
		if (lastSlash == expected.length() - 1)
			return;
		expected = expected.substring(lastSlash + 1);
		InputStream ins = null;
		try {
			ins = url.openStream();
			assertNotNull("Expecting to open stream to resource", ins);
			BufferedReader bufr = new BufferedReader(
					new InputStreamReader(ins));
			String line = bufr.readLine();
			assertEquals("Incorrect content in: " + url, expected, line);
		} catch (IOException e) {
			fail("Failed to read resource: " + url, e);
		} finally {
			if (ins != null)
				try {
					ins.close();
				} catch (IOException e) {
					// nothing
				}
		}
	}
}
