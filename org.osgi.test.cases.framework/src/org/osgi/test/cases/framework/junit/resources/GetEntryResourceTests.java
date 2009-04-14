package org.osgi.test.cases.framework.junit.resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;

import org.osgi.framework.Bundle;
import org.osgi.test.support.OSGiTestCase;


public class GetEntryResourceTests extends OSGiTestCase {
	Bundle testBundle;

	protected void setUp() throws Exception {
		super.setUp();
		testBundle = install("resources.tb1.jar");
	}

	public void testGetEntryRoot() {
		URL root = testBundle.getEntry("/");
		assertNotNull(root);
	}

	public void testGetEntry01() {
		URL datatxt = testBundle.getEntry("resources/data.txt");
		assertNotNull(datatxt);
		assertURL(datatxt);
		URL dataxml = testBundle.getEntry("resources/data.xml");
		assertURL(dataxml);

		URL doesNotExist = testBundle.getEntry("DoesNotExist");
		assertNull("Found unexpected resource: " + doesNotExist, doesNotExist);
	}

	public void testGetEntry02() {
		URL base = testBundle.getEntry("resources/data.txt");
		assertNotNull(base);
		// test using a file as a relative URL
		URL dataxml = newURL(base, "data.xml");
		assertURL(dataxml);

		URL dir1 = newURL(base, "dir1/");
		assertEquals("Wrong path for dir1", "/resources/dir1/", dir1.getPath());

		// test using the root URL as a base
		URL root = testBundle.getEntry("/");
		assertNotNull(root);
		URL datatxt = newURL(root, "resources/data.txt");
		assertURL(datatxt);
		dataxml = newURL(root, "resources/data.xml");
		assertURL(dataxml);

		// test directory entries
		datatxt = newURL(dir1, "../data.txt");
		assertURL(datatxt);
		datatxt = newURL(dir1, "../dir1/../data.txt");
		assertURL(datatxt);
	}

	public void testFindEntries01() {
		assertFindEntries(testBundle, "resources", null, false, 4);
		assertFindEntries(testBundle, "resources", "*", false, 4);
		assertFindEntries(testBundle, "resources", null, true, 14);
		assertFindEntries(testBundle, "resources", "*", true, 14);
		assertFindEntries(testBundle, "resources", "*.xml", true, 5);
		assertFindEntries(testBundle, "resources", "data*.xml", true, 5);
		assertFindEntries(testBundle, "resources", "*.xml", false, 1);
		assertFindEntries(testBundle, "resources", "data*.xml", false, 1);
		assertFindEntries(testBundle, "resources", "data.txt", false, 1);
		assertFindEntries(testBundle, "resources", "data*", true, 10);
		assertFindEntries(testBundle, "resources", "*d*ta*.*", true, 10);
		assertFindEntries(testBundle, "resources", "doesNotExist", true, 0);
		assertFindEntries(testBundle, "resources", "doesNotExist", false, 0);
		assertFindEntries(testBundle, "resources", "does*Not*Exist", true, 0);
		assertFindEntries(testBundle, "resources", "dir*", false, 2);
		assertFindEntries(testBundle, "resources", "dir*", true, 4);
	}

	public void testGetEntryPaths01() {
		assertEntryPaths(testBundle, "resources", new String[] {"resources/dir1/", "resources/dir2/", "resources/data.txt", "resources/data.xml"});
		assertEntryPaths(testBundle, "resources/dir1", new String[] {"resources/dir1/dir1a/", "resources/dir1/data1.txt", "resources/dir1/data1.xml"});

	}

	private void assertEntryPaths(Bundle bundle, String path, String[] expected) {
		Enumeration paths = bundle.getEntryPaths(path);
		if (expected != null)
			assertNotNull(paths);
		else {
			assertNull(paths);
			return;
		}

		ArrayList expectedList = new ArrayList(expected.length);
		for (int i = 0; i < expected.length; i++)
			expectedList.add(expected[i]);

		int numFound = 0;
		while (paths.hasMoreElements()) {
			Object element = paths.nextElement();
			if (!expectedList.contains(element))
				fail("Unexpected entry found: " + element);
			numFound++;
		}
		assertEquals("Unexpected number of entries", expected.length, numFound);
	}

	private void assertFindEntries(Bundle bundle, String string, String wildCard, boolean recurse, int expectedNum) {
		Enumeration entries = bundle.findEntries("resources", wildCard, recurse);
		if (expectedNum > 0)
			assertNotNull(entries);
		else {
			assertNull(entries);
			return;
		}

		int numFound = 0;
		while (entries.hasMoreElements()) {
			assertURL((URL) entries.nextElement());
			numFound++;
		}
		assertEquals("Unexpected number of entries", expectedNum, numFound);
	}

	protected void tearDown() throws Exception {
		testBundle.uninstall();
	}

	private URL newURL(URL base, String path) {
		try {
			return new URL(base, path);
		} catch (MalformedURLException e) {
			fail("Failed to create URL: " + base + " " + path, e);
		}
		return null;
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
			BufferedReader bufr = new BufferedReader(new InputStreamReader(ins));
			String line = bufr.readLine();
			assertEquals("Incorrect content in: " + url, expected, line);
		} catch (IOException e) {
			fail("Failed to read resource: " + url, e);
		}
		finally {
			if (ins != null)
				try {
					ins.close();
				} catch (IOException e) {
					// nothing
				}
		}
	}
}
