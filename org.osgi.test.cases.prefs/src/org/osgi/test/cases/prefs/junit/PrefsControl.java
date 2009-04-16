/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 */

package org.osgi.test.cases.prefs.junit;

import java.util.Arrays;
import java.util.List;

import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;
import org.osgi.service.prefs.PreferencesService;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * This is the bundle initially installed and started by the TestCase when
 * started. It performs the various tests and reports back to the TestCase.
 */
public class PrefsControl extends DefaultTestBundleControl {
	private PreferencesService	prefs;

	protected void setUp() {
		prefs = (PreferencesService) getService(PreferencesService.class);
		assertNotNull(prefs);
	}

	protected void tearDown() {
		ungetService(prefs);
	}

	public void testSystemRoot() {
		Preferences theNode = prefs.getSystemPreferences();
		runAllTests(theNode, "/");
	}

	public void testSystemNode() {
		Preferences theNode = prefs.getSystemPreferences().node(
				"somenode/anothernode");
		runAllTests(theNode, "/somenode/anothernode");
	}

	public void testUserRoot() {
		Preferences theNode = prefs.getUserPreferences("theuser");
		runAllTests(theNode, "/");
	}

	public void testUserNode() {
		Preferences theNode = prefs.getUserPreferences("theuser").node(
				"somenode/anothernode");
		runAllTests(theNode, "/somenode/anothernode");
	}

	public void testRemovedNode() {
		Preferences theNode = prefs.getUserPreferences("theuser").node(
				"removednode");
		testRemoveNode(theNode);
		runRemovedTests(theNode, "/removednode");
	}

	public void testRemovedAncestor() {
		Preferences theNode = prefs.getUserPreferences("theuser").node(
				"removedancestor/child/baby");
		Preferences ancestor = prefs.getUserPreferences("theuser").node(
				"removedancestor");
		testRemoveNode(ancestor);
		runRemovedTests(theNode, "/removedancestor/child/baby");
	}

	private void runAllTests(Preferences theNode, String absPath) {
		/* Test flush and sync */
		flush(theNode);
		sync(theNode);

		/* Test paths, names and if node exists */
		testAbsolutePath(theNode, absPath);
		testParent(theNode, absPath);
		testNodeExists(theNode, "");
		testName(theNode, absPath);
		testUnusualNames(theNode, absPath);
		testIllegalNames(theNode);

		/* Test if properties and node names are case sensitive */
		testCaseSensitivity(theNode);

		/* Test get and put methods */
		testGetMethods(theNode);
		testPutMethods(theNode);

		/* Test reading and writing properties from different types */
		testPropValues(theNode);

		/* Test checking for keys, removing and clearing properties */
		testKeys(theNode);

		/* Test adding children */
		testChildren(theNode);

		/* Test removing the node */
		testRemoveNode(theNode);
	}

	/*
	 * This method runs useful tests when a node is supposed to have been
	 * removed
	 */
	private void runRemovedTests(Preferences theNode, String absPath) {
		try {
			/* Test flush and sync */
			try {
				theNode.flush();
				fail("removed");
			}
			catch (IllegalStateException e) {
				// expected
			}
			try {
				theNode.sync();
				fail("removed");
			}
			catch (IllegalStateException e) {
				// expected
			}

			/* Test paths, names and if node exists */
			testAbsolutePath(theNode, absPath);
			assertFalse(theNode.nodeExists(""));
			testName(theNode, absPath);

			/* Test get and put methods */
			testGetMethodsRemoved(theNode);
			testPutMethodsRemoved(theNode);

			/* Test checking for keys, removing and clearing properties */
			try {
				theNode.put("somekey", "somevalue");
				fail("removed");
			}
			catch (IllegalStateException e) {
				// expected
			}
			try {
				theNode.remove("somekey");
				fail("removed");
			}
			catch (IllegalStateException e) {
				// expected
			}
			try {
				theNode.clear();
				fail("removed");
			}
			catch (IllegalStateException e) {
				// expected
			}

			/* Test adding children */
			try {
				theNode.node("child1");
				fail("removed");
			}
			catch (IllegalStateException e) {
				// expected
			}
			try {
				theNode.nodeExists("child1");
				fail("removed");
			}
			catch (IllegalStateException e) {
				// expected
			}

			/* Test removing the node */
			try {
				theNode.removeNode();
				fail("removed");
			}
			catch (IllegalStateException e) {
				// expected
			}
		}
		catch (Exception e) {
			fail("exception", e);
		}
	}

	/* The test methods */
	/* These methods sets the requirement markers and performs tests */

	private void testAbsolutePath(Preferences theNode, String absPath) {
		assertEquals(absPath, theNode.absolutePath());
	}

	private void testChildren(Preferences theNode) {
		clearChildren(theNode);

		createNode(theNode, "child1");
		createNode(theNode, "child2");
		createNode(theNode, "child3");
		testNodeExists(theNode, "child1");
		testNodeExists(theNode, "child2");
		testNodeExists(theNode, "child3");
		try {
			assertEquals(3, theNode.childrenNames().length);
		}
		catch (Exception e) {
			fail("children", e);
		}
		clearChildren(theNode);
	}

	private void testGetMethods(Preferences theNode) {
		clearKeys(theNode);
		assertEquals("(empty)", theNode.get("stringkey", "(empty)"));
		assertFalse(theNode.getBoolean("booleankey", false));
		assertEquals(0,
				theNode.getByteArray("bytearraykey", new byte[] {}).length);
		assertEquals(0.0d, theNode.getDouble("doublekey", 0.0d), 0.0d);
		assertEquals(0.0f, theNode.getFloat("floatkey", 0.0f), 0.0f);
		assertEquals(0, theNode.getInt("integerkey", 0));
		assertEquals(0l, theNode.getLong("longkey", 0l));

		assertNull(theNode.get("stringkey", null));
		assertNull(theNode.getByteArray("bytearraykey", null));

		try {
			try {
				theNode.get(null, "(empty)");
				fail("expected NullPointerException");
			}
			catch (NullPointerException e) {
				// expected
			}
			try {
				theNode.getBoolean(null, false);
				fail("expected NullPointerException");
			}
			catch (NullPointerException e) {
				// expected
			}
			try {
				theNode.getByteArray(null, new byte[] {});
				fail("expected NullPointerException");
			}
			catch (NullPointerException e) {
				// expected
			}
			try {
				theNode.getDouble(null, 0.0d);
				fail("expected NullPointerException");
			}
			catch (NullPointerException e) {
				// expected
			}
			try {
				theNode.getFloat(null, 0.0f);
				fail("expected NullPointerException");
			}
			catch (NullPointerException e) {
				// expected
			}
			try {
				theNode.getInt(null, 0);
				fail("expected NullPointerException");
			}
			catch (NullPointerException e) {
				// expected
			}
			try {
				theNode.getLong(null, 0l);
				fail("expected NullPointerException");
			}
			catch (NullPointerException e) {
				// expected
			}
		}
		catch (Exception e) {
			fail("unexpected", e);
		}
	}

	private void testPutMethods(Preferences theNode) {
		clearKeys(theNode);
		theNode.put("stringkey", "(empty)");
		theNode.putBoolean("booleankey", false);
		theNode.putByteArray("bytearraykey", new byte[] {});
		theNode.putDouble("doublekey", 0.0d);
		theNode.putFloat("floatkey", 0.0f);
		theNode.putInt("integerkey", 0);
		theNode.putLong("longkey", 0l);

		assertEquals("(empty)", theNode.get("stringkey", null));
		assertFalse(theNode.getBoolean("booleankey", true));
		assertEquals(0, theNode.getByteArray("bytearraykey", null).length);
		assertEquals(0.0d, theNode.getDouble("doublekey", 1.0d), 0.0d);
		assertEquals(0.0f, theNode.getFloat("floatkey", 1.0f), 0.0f);
		assertEquals(0, theNode.getInt("integerkey", 1));
		assertEquals(0l, theNode.getLong("longkey", 1l));

		try {
			try {
				theNode.put("stringkey", null);
				fail("expected NullPointerException");
			}
			catch (NullPointerException e) {
				// expected
			}
			try {
				theNode.putByteArray("bytearraykey", null);
				fail("expected NullPointerException");
			}
			catch (NullPointerException e) {
				// expected
			}
			try {
				theNode.put(null, "(empty)");
				fail("expected NullPointerException");
			}
			catch (NullPointerException e) {
				// expected
			}
			try {
				theNode.putBoolean(null, false);
				fail("expected NullPointerException");
			}
			catch (NullPointerException e) {
				// expected
			}
			try {
				theNode.putByteArray(null, new byte[] {});
				fail("expected NullPointerException");
			}
			catch (NullPointerException e) {
				// expected
			}
			try {
				theNode.putDouble(null, 0.0d);
				fail("expected NullPointerException");
			}
			catch (NullPointerException e) {
				// expected
			}
			try {
				theNode.putFloat(null, 0.0f);
				fail("expected NullPointerException");
			}
			catch (NullPointerException e) {
				// expected
			}
			try {
				theNode.putInt(null, 0);
				fail("expected NullPointerException");
			}
			catch (NullPointerException e) {
				// expected
			}
			try {
				theNode.putLong(null, 0l);
				fail("expected NullPointerException");
			}
			catch (NullPointerException e) {
				// expected
			}
		}
		catch (Exception e) {
			fail("unexpected", e);
		}
	}

	private void testGetMethodsRemoved(Preferences theNode) {
		try {
			theNode.get("stringkey", "(empty)");
			fail("missing IllegalStateException");
		}
		catch (IllegalStateException e) {
			// expected
		}
		try {
			theNode.getBoolean("booleankey", false);
			fail("missing IllegalStateException");
		}
		catch (IllegalStateException e) {
			// expected
		}
		try {
			theNode.getByteArray("bytearraykey", new byte[] {});
			fail("missing IllegalStateException");
		}
		catch (IllegalStateException e) {
			// expected
		}
		try {
			theNode.getDouble("doublekey", 0.0d);
			fail("missing IllegalStateException");
		}
		catch (IllegalStateException e) {
			// expected
		}
		try {
			theNode.getFloat("floatkey", 0.0f);
			fail("missing IllegalStateException");
		}
		catch (IllegalStateException e) {
			// expected
		}
		try {
			theNode.getInt("integerkey", 0);
			fail("missing IllegalStateException");
		}
		catch (IllegalStateException e) {
			// expected
		}
		try {
			theNode.getLong("longkey", 0l);
			fail("missing IllegalStateException");
		}
		catch (IllegalStateException e) {
			// expected
		}
	}

	private void testPutMethodsRemoved(Preferences theNode) {
		try {
			theNode.put("stringkey", "(empty)");
			fail("missing IllegalStateException");
		}
		catch (IllegalStateException e) {
			// expected
		}
		try {
			theNode.putBoolean("booleankey", false);
			fail("missing IllegalStateException");
		}
		catch (IllegalStateException e) {
			// expected
		}
		try {
			theNode.putByteArray("bytearraykey", new byte[] {});
			fail("missing IllegalStateException");
		}
		catch (IllegalStateException e) {
			// expected
		}
		try {
			theNode.putDouble("doublekey", 0.0d);
			fail("missing IllegalStateException");
		}
		catch (IllegalStateException e) {
			// expected
		}
		try {
			theNode.putFloat("floatkey", 0.0f);
			fail("missing IllegalStateException");
		}
		catch (IllegalStateException e) {
			// expected
		}
		try {
			theNode.putInt("integerkey", 0);
			fail("missing IllegalStateException");
		}
		catch (IllegalStateException e) {
			// expected
		}
		try {
			theNode.putLong("longkey", 0l);
			fail("missing IllegalStateException");
		}
		catch (IllegalStateException e) {
			// expected
		}
	}

	private void testKeys(Preferences theNode) {
		try {
			clearKeys(theNode);

			theNode.put("somekey", "somevalue");
			theNode.put("someotherkey", "someothervalue");

			List keys = Arrays.asList(theNode.keys());
			assertEquals(2, keys.size());
			assertTrue(keys.contains("somekey"));
			assertTrue(keys.contains("someotherkey"));

			testRemove(theNode, "somekey");

			keys = Arrays.asList(theNode.keys());
			assertEquals(1, keys.size());
			assertFalse(keys.contains("somekey"));
			assertTrue(keys.contains("someotherkey"));

			clearKeys(theNode);

			keys = Arrays.asList(theNode.keys());
			assertEquals(0, keys.size());
			assertFalse(keys.contains("somekey"));
			assertFalse(keys.contains("someotherkey"));
		}
		catch (Exception e) {
			fail("keys error", e);
		}
	}

	private void testName(Preferences theNode, String absPath) {
		assertEquals(getNodeName(absPath), theNode.name());
	}

	private static String getNodeName(String absPath) {
		int i = absPath.lastIndexOf("/");
		return absPath.substring(i + 1);
	}

	private static String getAbsPathName(String absPath, String nodeName) {
		if (nodeName.startsWith("/")) {
			return nodeName;
		}
		if (absPath.equals("/")) {
			return "/" + nodeName;
		}
		return absPath + "/" + nodeName;
	}

	private void createNode(Preferences theNode, String pathName) {
		try {
			Preferences theNewNode = theNode.node(pathName);
			assertEquals(getAbsPathName(theNode.absolutePath(), pathName),
					theNewNode.absolutePath());
		}
		catch (Exception e) {
			fail("create node", e);
		}
	}

	private void testNodeExists(Preferences theNode, String pathName) {
		/* Test the relative path */
		try {
			assertTrue(theNode.nodeExists(pathName));

			/* Test the absolute path */
			String absolutePath = theNode.absolutePath();
			if (pathName != null) {
				if (!"".equals(pathName) && (theNode.parent() != null)) {
					absolutePath = absolutePath + "/" + pathName;
				}
			}
			assertTrue(theNode.nodeExists(absolutePath));
		}
		catch (Exception e) {
			fail("node exists", e);
		}
	}

	private void testParent(Preferences theNode, String absPath) {
		try {
			String parentName = "";
			int i = absPath.lastIndexOf('/');
			if (i > 1) {
				parentName = absPath.substring(0, i);
			}
			Preferences parentNode = theNode.parent();
			if (parentNode != null) {
				assertEquals(parentName, parentNode.absolutePath());
			}
			else {
				assertEquals(0, parentName.length());
			}
		}
		catch (Exception e) {
			fail("parent", e);
		}
	}

	private void testRemove(Preferences theNode, String key) {
		try {
			theNode.remove(key);
			assertFalse(Arrays.asList(theNode.keys()).contains(key));
		}
		catch (Exception e) {
			fail("remove key", e);
		}
	}

	private void testRemoveNode(Preferences theNode) {
		try {
			theNode.removeNode();
			assertFalse(theNode.nodeExists(""));
		}
		catch (Exception e) {
			fail("remove node", e);
		}
	}

	private void testPropValues(Preferences theNode) {
		clearKeys(theNode);
		final byte[] empty = new byte[] {};
		final byte[] trueResult = new byte[] {(byte) 0xb6, (byte) 0xbb,
				(byte) 0x9e};
		final byte[] sometextResult = new byte[] {(byte) 0xb2, (byte) 0x89,
				(byte) 0x9e, (byte) 0xb5, (byte) 0xec, (byte) 0x6d};
		assertEquals("(empty)", theNode.get("thekey", "(empty)"));
		assertFalse(theNode.getBoolean("thekey", false));
		assertTrue(Arrays.equals(empty, theNode.getByteArray("thekey", empty)));
		assertEquals(0.0d, theNode.getDouble("thekey", 0.0d), 0.0d);
		assertEquals(0.0f, theNode.getFloat("thekey", 0.0f), 0.0f);
		assertEquals(0, theNode.getInt("thekey", 0));
		assertEquals(0l, theNode.getLong("thekey", 0l));

		/* Test putting in a string */
		theNode.put("thekey", "astring");
		assertEquals("astring", theNode.get("thekey", "(empty)"));
		assertFalse(theNode.getBoolean("thekey", false));
		assertTrue(Arrays.equals(empty, theNode.getByteArray("thekey", empty)));
		assertEquals(0.0d, theNode.getDouble("thekey", 0.0d), 0.0d);
		assertEquals(0.0f, theNode.getFloat("thekey", 0.0f), 0.0f);
		assertEquals(0, theNode.getInt("thekey", 0));
		assertEquals(0l, theNode.getLong("thekey", 0l));

		/* Test putting in a boolean string */
		theNode.put("thekey", "true");
		assertEquals("true", theNode.get("thekey", "(empty)"));
		assertTrue(theNode.getBoolean("thekey", false));
		assertTrue(Arrays.equals(trueResult, theNode.getByteArray("thekey",
				empty)));
		assertEquals(0.0d, theNode.getDouble("thekey", 0.0d), 0.0d);
		assertEquals(0.0f, theNode.getFloat("thekey", 0.0f), 0.0f);
		assertEquals(0, theNode.getInt("thekey", 0));
		assertEquals(0l, theNode.getLong("thekey", 0l));

		/* Test putting in a boolean */
		theNode.putBoolean("thekey", true);
		assertEquals("true", theNode.get("thekey", "(empty)"));
		assertTrue(theNode.getBoolean("thekey", false));
		assertTrue(Arrays.equals(trueResult, theNode.getByteArray("thekey",
				empty)));
		assertEquals(0.0d, theNode.getDouble("thekey", 0.0d), 0.0d);
		assertEquals(0.0f, theNode.getFloat("thekey", 0.0f), 0.0f);
		assertEquals(0, theNode.getInt("thekey", 0));
		assertEquals(0l, theNode.getLong("thekey", 0l));

		/* Test putting in a string that works as a byte array too */
		theNode.put("thekey", "sometext");
		assertEquals("sometext", theNode.get("thekey", "(empty)"));
		assertFalse(theNode.getBoolean("thekey", false));
		assertTrue(Arrays.equals(sometextResult, theNode.getByteArray("thekey",
				empty)));
		assertEquals(0.0d, theNode.getDouble("thekey", 0.0d), 0.0d);
		assertEquals(0.0f, theNode.getFloat("thekey", 0.0f), 0.0f);
		assertEquals(0, theNode.getInt("thekey", 0));
		assertEquals(0l, theNode.getLong("thekey", 0l));

		/* Test putting in a double/float string */
		theNode.put("thekey", "1.0");
		assertEquals("1.0", theNode.get("thekey", "(empty)"));
		assertFalse(theNode.getBoolean("thekey", false));
		assertTrue(Arrays.equals(empty, theNode.getByteArray("thekey", empty)));
		assertEquals(1.0d, theNode.getDouble("thekey", 0.0d), 0.0d);
		assertEquals(1.0f, theNode.getFloat("thekey", 0.0f), 0.0f);
		assertEquals(0, theNode.getInt("thekey", 0));
		assertEquals(0l, theNode.getLong("thekey", 0l));

		/* Test putting in the Double.MIN_VALUE */
		theNode.putDouble("thekey", Double.MIN_VALUE);
		assertEquals(Double.toString(Double.MIN_VALUE), theNode.get("thekey",
				"(empty)"));
		assertFalse(theNode.getBoolean("thekey", false));
		assertTrue(Arrays.equals(empty, theNode.getByteArray("thekey", empty)));
		assertEquals(Double.MIN_VALUE, theNode.getDouble("thekey", 0.0d), 0.0d);
		assertEquals(0.0f, theNode.getFloat("thekey", 0.0f), 0.0f);
		assertEquals(0, theNode.getInt("thekey", 0));
		assertEquals(0l, theNode.getLong("thekey", 0l));

		/* Test putting in the Float.MIN_VALUE */
		theNode.putFloat("thekey", Float.MIN_VALUE);
		assertEquals(Float.toString(Float.MIN_VALUE), theNode.get("thekey",
				"(empty)"));
		assertFalse(theNode.getBoolean("thekey", false));
		assertTrue(Arrays.equals(empty, theNode.getByteArray("thekey", empty)));
		assertEquals(Float.MIN_VALUE, theNode.getDouble("thekey", 0.0d), 0.002d);
		assertEquals(Float.MIN_VALUE, theNode.getFloat("thekey", 0.0f), 0.0f);
		assertEquals(0, theNode.getInt("thekey", 0));
		assertEquals(0l, theNode.getLong("thekey", 0l));

		/* Test putting in an Integer/Long string */
		theNode.put("thekey", "1");
		assertEquals("1", theNode.get("thekey", "(empty)"));
		assertFalse(theNode.getBoolean("thekey", false));
		assertTrue(Arrays.equals(empty, theNode.getByteArray("thekey", empty)));
		assertEquals(1.0d, theNode.getDouble("thekey", 0.0d), 0.0d);
		assertEquals(1.0f, theNode.getFloat("thekey", 0.0f), 0.0f);
		assertEquals(1, theNode.getInt("thekey", 0));
		assertEquals(1l, theNode.getLong("thekey", 0l));

		/* Test putting in the Integer.MIN_VALUE Integer */
		theNode.putInt("thekey", Integer.MIN_VALUE);
		assertEquals(Integer.toString(Integer.MIN_VALUE), theNode.get("thekey",
				"(empty)"));
		assertFalse(theNode.getBoolean("thekey", false));
		assertTrue(Arrays.equals(empty, theNode.getByteArray("thekey", empty)));
		assertEquals(Integer.MIN_VALUE, theNode.getDouble("thekey", 0.0d), 0.0d);
		assertEquals(Integer.MIN_VALUE, theNode.getFloat("thekey", 0.0f), 0.0f);
		assertEquals(Integer.MIN_VALUE, theNode.getInt("thekey", 0));
		assertEquals(Integer.MIN_VALUE, theNode.getLong("thekey", 0l));

		/* Test putting in the Long.MIN_VALUE */
		theNode.putLong("thekey", Long.MIN_VALUE);
		assertEquals(Long.toString(Long.MIN_VALUE), theNode.get("thekey",
				"(empty)"));
		assertFalse(theNode.getBoolean("thekey", false));
		assertTrue(Arrays.equals(empty, theNode.getByteArray("thekey", empty)));
		assertEquals(Long.MIN_VALUE, theNode.getDouble("thekey", 0.0d), 0.0d);
		assertEquals(Long.MIN_VALUE, theNode.getFloat("thekey", 0.0f), 0.0f);
		assertEquals(0, theNode.getInt("thekey", 0));
		assertEquals(Long.MIN_VALUE, theNode.getLong("thekey", 0l));
	}

	private void testIllegalNames(Preferences theNode) {
		String[] names = {"/node1/", "/node2//subnode", "node3/",
				"node4//subnode"};

		for (int i = 0; i < names.length; i++) {
			String name = names[i];
			try {
				theNode.node(name);
				fail(name);
			}
			catch (IllegalArgumentException e) {
				// expected
			}
			catch (Exception e) {
				fail(name, e);
			}
		}
	}

	private void testUnusualNames(Preferences theNode, String absPath) {
		String[] names = {"/.././_?/\\", "/ / / / ", ".././_?/\\", " / / / "};

		for (int i = 0; i < names.length; i++) {
			String name = names[i];
			String namePath = getAbsPathName(absPath, name);
			try {
				Preferences newNode = theNode.node(name);
				assertNotNull(newNode);
				assertEquals(namePath, newNode.absolutePath());
			}
			catch (Exception e) {
				fail(name, e);
			}
		}
		clearChildren(theNode);
	}

	private void testCaseSensitivity(Preferences theNode) {
		/* Test if keys are case-sensitive */
		theNode.put("Name", "UPPERCASE");
		assertEquals("lowercase", theNode.get("name", "lowercase"));

		/* Test if node names are case-sensitive */
		theNode.node("NameOfChild");
		try {
			assertFalse(theNode.nodeExists("nameofchild"));
		}
		catch (BackingStoreException e) {
			fail("node exists error", e);
		}
		clearKeys(theNode);
		clearChildren(theNode);
	}

	/* The helper methods */
	/*
	 * These methods performs tests that may be called from multiple places
	 * (while testing different requrements. These methods may very well perform
	 * logs, but should NOT set requirement markers
	 */

	private void clearKeys(Preferences theNode) {
		try {
			theNode.clear();
			assertEquals(0, theNode.keys().length);
		}
		catch (Exception e) {
			fail("clear keys", e);
		}
	}

	private void clearChildren(Preferences theNode) {
		try {
			String[] children = theNode.childrenNames();
			for (int i = 0; i < children.length; i++) {
				Preferences child = theNode.node(children[i]);
				child.removeNode();
			}
			assertEquals(0, theNode.childrenNames().length);
		}
		catch (Exception e) {
			fail("clear children", e);
		}
	}

	private void flush(Preferences theNode) {
		try {
			theNode.flush();
		}
		catch (Exception e) {
			fail(theNode.absolutePath() + " flush failed", e);
		}
	}

	private void sync(Preferences theNode) {
		try {
			theNode.sync();
		}
		catch (Exception e) {
			fail(theNode.absolutePath() + " sync failed", e);
		}
	}
}
