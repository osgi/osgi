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
package org.osgi.test.cases.jmx.framework.junit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import javax.management.ObjectName;
import javax.management.openmbean.ArrayType;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularType;

import org.osgi.framework.Constants;
import org.osgi.jmx.Item;
import org.osgi.jmx.framework.BundleStateMBean;
import org.osgi.jmx.framework.FrameworkMBean;

public class FrameworkMBeanTestCase extends MBeanGeneralTestCase {

	private FrameworkMBean frameworkMBean;
	private BundleStateMBean bundleStateMBean;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		super.waitForRegistering(createObjectName(FrameworkMBean.OBJECTNAME));
		frameworkMBean = getMBeanFromServer(FrameworkMBean.OBJECTNAME,
											FrameworkMBean.class);
		frameworkMBean.refreshBundlesAndWait(null);
		super.waitForRegistering(createObjectName(BundleStateMBean.OBJECTNAME));
		bundleStateMBean = getMBeanFromServer(BundleStateMBean.OBJECTNAME,
											BundleStateMBean.class);

	}

    public void testObjectNameStructure() throws Exception {
        ObjectName queryName = new ObjectName(FrameworkMBean.OBJECTNAME + ",*");
        Set<ObjectName> names = getMBeanServer().queryNames(queryName, null);
        assertEquals(1, names.size());

        ObjectName name = names.iterator().next();
        Hashtable<String, String> props = name.getKeyPropertyList();

        String type = props.get("type");
        assertEquals("framework", type);
        String version = props.get("version");
        assertEquals("1.7", version);
        String framework = props.get("framework");
        assertEquals(getContext().getBundle(0).getSymbolicName(), framework);
        String uuid = props.get("uuid");
        assertEquals(getContext().getProperty(Constants.FRAMEWORK_UUID), uuid);

        assertTrue(name.getKeyPropertyListString().startsWith(
                "type=" + type + ",version=" + version + ",framework=" + framework + ",uuid=" + uuid));

        assertEquals(uuid, frameworkMBean.getProperty(Constants.FRAMEWORK_UUID));
    }

	/* Test scenario: Install bundle. Start bundle. Set bundle start level bigger than the framework one. Check that bundle is stopped.
	 * Change framework start level to be bigger than the bundle one. Check that bundle is started. Change framework start level to be
	 * smaller than the bundle one. Check that bundle is stopped. Un-install bundle.
	 */
	public void testFrameworkStartLevel() {
		long testBundle = -1;
		try {
			//install bundle tb2
			URL entry = getContext().getBundle().getEntry("tb2.jar");
			testBundle = frameworkMBean.installBundleFromURL("tb2.jar", entry.toString());

			//get framework start level
			int frameworkStartLevel = frameworkMBean.getFrameworkStartLevel();

			//start bundle; assure bundle is started
			frameworkMBean.startBundle(testBundle);
			assertTrue("bundle tb2 could not be started for " +  waitTime + " seconds ", waitBundleStateChange(testBundle, "ACTIVE"));

			//set bundle start level bigger than the framework one; bundle should be stopped automatically
			frameworkMBean.setBundleStartLevel(testBundle, frameworkStartLevel + 2);
			assertTrue("bundle tb2 is not stopped for " +  waitTime + " seconds after setting its bundle start level bigger than framework start level", waitBundleStateChange(testBundle, "RESOLVED"));

			//set framework start level bigger than the bundle start level; bundle should be started
			int bundleStartLevel = bundleStateMBean.getStartLevel(testBundle);
			frameworkMBean.setFrameworkStartLevel(bundleStartLevel + 2);
			assertTrue("bundle tb2 is not started for " +  waitTime + " seconds after setting framework start level bigger than its start level", waitBundleStateChange(testBundle, "ACTIVE"));

			//set framework start level less than the bundle start level; bundle should be stopped
			frameworkMBean.setFrameworkStartLevel(bundleStartLevel - 2);
			assertTrue("bundle tb2 is not stopped for " +  waitTime + " seconds after setting framework start level smaller than its start level", waitBundleStateChange(testBundle, "RESOLVED"));
		} catch(Exception io) {
			io.printStackTrace();
			fail("Exception occured", io);
		} finally {
			if (testBundle >= 0) {
				try {
					frameworkMBean.uninstallBundle(testBundle);
				} catch (IOException io) {}
			}
		}
	}

	/*
	 * Test scenario: Install one bundle. Check that bundle initial start level is equal to the one returned from framework mBean.
	 * Change initial bundle start level. Install second bundle. Check that second installed bundle initial start level is the new one.
	 * Check that the initial bundle start level of the first bundle is not changed. Start bundles. Set bundles initial start levels bigger
	 * than the framework one. Check that bundles are stopped. Change the bundles initial start level to be smaller than the framework one.
	 * Check that bundles are started. Un-install bundles.
	 */
	public void testBundleStartLevel() {
		long testBundle1 = -1;
		long testBundle2 = -1;
		try {
			//install bundle tb2
			URL entry = getContext().getBundle().getEntry("tb2.jar");
			testBundle2 = frameworkMBean.installBundleFromURL("tb2.jar", entry.toString());

			//check bundle start level is equal to initial one
			int initialBundleStartLevel = frameworkMBean.getInitialBundleStartLevel();
			int testBundle1StartLeve2 = bundleStateMBean.getStartLevel(testBundle2);
			assertTrue("bundle start level is different from the initial bundle start level returned from the framework MBean", initialBundleStartLevel == testBundle1StartLeve2);

			//change initial bundle start level
			frameworkMBean.setInitialBundleStartLevel(initialBundleStartLevel + 1);

			//install bundle tb1
			entry = getContext().getBundle().getEntry("tb1.jar");
			testBundle1 = frameworkMBean.installBundleFromURL("tb1.jar", entry.toString());

			//check bundle start level is equal to changed one
			initialBundleStartLevel = frameworkMBean.getInitialBundleStartLevel();
			int testBundle1StartLeve1 = bundleStateMBean.getStartLevel(testBundle1);
			assertTrue("bundle start level is different from the initial bundle start level returned from the framework MBean", initialBundleStartLevel == testBundle1StartLeve1);

			//check already installed bundles start level is not changed
			assertTrue("installed bundle start level is changed after changing inital bundle start level via the framework MBean", testBundle1StartLeve2 == bundleStateMBean.getStartLevel(testBundle2));

			//set framework start level bigger than installed bundles' one
			frameworkMBean.setFrameworkStartLevel(testBundle1StartLeve1 + 2);

			//start bundles; assure bundle are started
			frameworkMBean.startBundle(testBundle2);
			frameworkMBean.startBundle(testBundle1);
			assertTrue("bundle tb2 could not be started for " +  waitTime + " seconds", waitBundleStateChange(testBundle2, "ACTIVE"));
			assertTrue("bundle tb1 could not be started for " +  waitTime + " seconds", waitBundleStateChange(testBundle1, "ACTIVE"));

			//set bundles start levels bigger than the framework one; bundles should be stopped automatically
			CompositeData result = frameworkMBean.setBundleStartLevels(new long[] {testBundle1, testBundle2}, new int[] {testBundle1StartLeve1 + 4, testBundle1StartLeve1 + 4});
			assertCompositeDataKeys(result, "BATCH_ACTION_RESULT_TYPE", new String[] { "BundleInError", "Completed", "Error", "Remaining", "Success" });
			assertTrue("setting of bundles start levels doesn't succeed", ((Boolean) result.get("Success")).booleanValue());
			assertTrue("bundle tb1 is not stopped for " +  waitTime + " seconds after setting its bundle start level bigger than framework start level", waitBundleStateChange(testBundle1, "RESOLVED"));
			assertTrue("bundle tb2 is not stopped for " +  waitTime + " seconds after setting its bundle start level bigger than framework start level", waitBundleStateChange(testBundle2, "RESOLVED"));

			//set bundles start levels smaller than the framework one; bundles should be started automatically
			result = frameworkMBean.setBundleStartLevels(new long[] {testBundle2, testBundle1}, new int[] {testBundle1StartLeve1, testBundle1StartLeve1});
			assertCompositeDataKeys(result, "BATCH_ACTION_RESULT_TYPE", new String[] { "BundleInError", "Completed", "Error", "Remaining", "Success" });
			assertTrue("setting of bundles start levels doesn't succeed", ((Boolean) result.get("Success")).booleanValue());
			assertTrue("bundle tb2 is not started for " +  waitTime + " seconds after setting its bundle start level smaller than framework start level", waitBundleStateChange(testBundle2, "ACTIVE"));
			assertTrue("bundle tb1 is not started for " +  waitTime + " seconds after setting its bundle start level smaller than framework start level", waitBundleStateChange(testBundle1, "ACTIVE"));

			//set bundle start level bigger than the framework one; bundle should be stopped automatically
			frameworkMBean.setBundleStartLevel(testBundle1, testBundle1StartLeve1 + 4);
			assertTrue("bundle tb1 is not stopped for " +  waitTime + " seconds after setting its bundle start level bigger than framework start level", waitBundleStateChange(testBundle1, "RESOLVED"));

			//set bundles start level smaller than the framework one; bundle should be started automatically
			frameworkMBean.setBundleStartLevel(testBundle1, testBundle1StartLeve1);
			assertTrue("bundle tb1 is not started for " +  waitTime + " seconds after setting its bundle start level smaller than framework start level", waitBundleStateChange(testBundle1, "ACTIVE"));
		} catch(Exception io) {
			io.printStackTrace();
			fail("Exception occured", io);
		} finally {
			if (testBundle1 >= 0) {
				try {
					frameworkMBean.uninstallBundle(testBundle1);
				} catch (IOException io) {}
			}
			if (testBundle2 >= 0) {
				try {
					frameworkMBean.uninstallBundle(testBundle2);
				} catch (IOException io) {}
			}
		}
	}

	/* Test scenario: Install bundle from URL and check it is installed and the returned bundle id is useful for operations (check done via starting it). Un-install the bundle.
	/* Install two bundles from URL and check they are installed and the returned bundle ids are useful	(check done via starting them). Un-install bundles. */
	public void testBundleInstallFromURL() {
		long testBundle = -1;
		try {
			//install single bundle
			URL entry2 = getContext().getBundle().getEntry("tb2.jar");
			testBundle = frameworkMBean.installBundleFromURL("tb2.jar", entry2.toString());
			//check bundle id is the right one
			frameworkMBean.startBundle(testBundle);
			assertTrue("bundle tb2 is not started for " +  waitTime + " seconds", waitBundleStateChange(testBundle, "ACTIVE"));
		} catch(Exception io) {
			io.printStackTrace();
			fail("Exception occured", io);
		} finally {
			if (testBundle >= 0) {
				try {
					frameworkMBean.uninstallBundle(testBundle);
				} catch (IOException io) {
					fail("Exception occured", io);
				}
			}
		}

		Long[] bundleIds = null;
		try {
			//install several bundles
			URL entry1 = getContext().getBundle().getEntry("tb1.jar");
			URL entry2 = getContext().getBundle().getEntry("tb2.jar");
			CompositeData result = frameworkMBean.installBundlesFromURL(new String[] {"tb2.jar", "tb1.jar"}, new String[] {entry2.toString(), entry1.toString()});
			assertCompositeDataKeys(result, "BATCH_ACTION_RESULT_TYPE", new String[] { "BundleInError", "Completed", "Error", "Remaining", "Success" });
			assertTrue("installing bundles from URL doesn't succeed", ((Boolean) result.get("Success")).booleanValue());
			bundleIds = (Long[]) result.get("Completed");
			assertTrue("installing bundles from URL doesn't return right bundle ids info", (bundleIds != null) && (bundleIds.length == 2));
			frameworkMBean.startBundle(bundleIds[0].longValue());
			frameworkMBean.startBundle(bundleIds[1].longValue());
			assertTrue("bundle tb2 is not started for " +  waitTime + " seconds", waitBundleStateChange(bundleIds[0], "ACTIVE"));
			assertTrue("bundle tb1 is not started for " +  waitTime + " seconds", waitBundleStateChange(bundleIds[1], "ACTIVE"));

			//if mentioned as bundleIds[0] and than bundleIds[1] -> there is a problem with bundleIds[0] to be un-installed
			result = frameworkMBean.uninstallBundles(new long[] { bundleIds[1].longValue(), bundleIds[0].longValue() });
			assertCompositeDataKeys(result, "BATCH_ACTION_RESULT_TYPE", new String[] { "BundleInError", "Completed", "Error", "Remaining", "Success" });
			assertTrue("un-installing bundles from URL doesn't succeed", ((Boolean) result.get("Success")).booleanValue());
		} catch(Exception io) {
			io.printStackTrace();
			fail("Exception occured", io);
		}
	}

	/* Test scenario: Install bundle and get it last modified time. Update the bundle. Check that last modified time is bigger. Un-install the bundle.
	/* Install bundles and get their last modified time. Update bundles. Check that new last modified time is bigger for each of them. Un-install bundles.
	*/
	public void testBundleUpdateFromURL() {
		long testBundle = -1;
		try {
			//install single bundle
			URL entry2 = getContext().getBundle().getEntry("tb2.jar");
			testBundle = frameworkMBean.installBundleFromURL("tb2.jar", entry2.toString());
			//get last modification time
			long lastModifiedTime = bundleStateMBean.getLastModified(testBundle);
			//wait some time
			Thread.sleep(10);
			//update bundle
			frameworkMBean.updateBundleFromURL(testBundle, entry2.toString());
			//get new last modification time
			long newLastModifiedTime = bundleStateMBean.getLastModified(testBundle);
			//check that newest last modification time is bigger than the previous one
			assertTrue("after update bundle from url the bundle's last modified time is not changed", newLastModifiedTime > lastModifiedTime);
		} catch(Exception io) {
			io.printStackTrace();
			fail("Exception occured", io);
		} finally {
			if (testBundle >= 0) {
				try {
					frameworkMBean.uninstallBundle(testBundle);
				} catch (IOException io) {
					fail("Exception occured", io);
				}
			}
		}

		long testBundle1 = -1;
		long testBundle2 = -1;
		try {
			//install two bundles
			URL entry2 = getContext().getBundle().getEntry("tb2.jar");
			testBundle2 = frameworkMBean.installBundleFromURL("tb2.jar", entry2.toString());
			URL entry1 = getContext().getBundle().getEntry("tb1.jar");
			testBundle1 = frameworkMBean.installBundleFromURL("tb1.jar", entry1.toString());
			//get last modification time
			long lastModifiedTime1 = bundleStateMBean.getLastModified(testBundle1);
			long lastModifiedTime2 = bundleStateMBean.getLastModified(testBundle2);
			//wait some time
			Thread.sleep(10);
			//update bundle
			CompositeData result = frameworkMBean.updateBundlesFromURL(new long[] { testBundle1, testBundle2 }, new String[] {entry1.toString(), entry2.toString() });
			assertCompositeDataKeys(result, "BATCH_ACTION_RESULT_TYPE", new String[] { "BundleInError", "Completed", "Error", "Remaining", "Success" });
			assertTrue("update of bundles from url doesn't succeed", ((Boolean) result.get("Success")).booleanValue());
			//get new last modification times
			long newLastModifiedTime1 = bundleStateMBean.getLastModified(testBundle1);
			long newLastModifiedTime2 = bundleStateMBean.getLastModified(testBundle2);
			//check that newest last modification time is bigger than the previous one
			assertTrue("after update bundles from url the bundles' last modified time is not changed", (newLastModifiedTime1 > lastModifiedTime1) && (newLastModifiedTime2 > lastModifiedTime2));
		} catch(Exception io) {
			io.printStackTrace();
			fail("Exception occured", io);
		} finally {
			if (testBundle1 >= 0) {
				try {
					frameworkMBean.uninstallBundle(testBundle1);
				} catch (IOException io) {
					io.printStackTrace();
				}
			}
			if (testBundle2 >= 0) {
				try {
					frameworkMBean.uninstallBundle(testBundle2);
				} catch (IOException io) {
					io.printStackTrace();
				}
			}
		}
	}

	/* Test scenario: Install bundle and resolve it. Check their state is RESOLVED. Un-install the bundle.
	 * Install two bundles and resolve them. Check their state is RESOLVED. Check operation is successful. Un-install bundles.
	*/
	public void testBundleResolve() {
		long testBundle = -1;
		try {
			//install single bundle
			URL entry2 = getContext().getBundle().getEntry("tb2.jar");
			testBundle = frameworkMBean.installBundleFromURL("tb2.jar", entry2.toString());
			//resolve bundle2
			assertTrue("bundle tb2 could not be resolved", frameworkMBean.resolveBundle(testBundle));
		} catch(Exception io) {
			io.printStackTrace();
			fail("Exception occured", io);
		} finally {
			if (testBundle >= 0) {
				try {
					frameworkMBean.uninstallBundle(testBundle);
				} catch (IOException io) {
					fail("Exception occured", io);
				}
			}
		}

		long testBundle1 = -1;
		long testBundle2 = -1;
		try {
			//install bundle2
			URL entry2 = getContext().getBundle().getEntry("tb2.jar");
			testBundle2 = frameworkMBean.installBundleFromURL("tb2.jar", entry2.toString());

			//install bundle1
			URL entry1 = getContext().getBundle().getEntry("tb1.jar");
			testBundle1 = frameworkMBean.installBundleFromURL("tb1.jar", entry1.toString());

			//resolve bundles
			assertTrue("bundle tb1 and tb2 could not be resolved", frameworkMBean.resolveBundles(new long[] {testBundle1, testBundle2}));
		} catch(Exception io) {
			io.printStackTrace();
			fail("Exception occured", io);
		} finally {
			if (testBundle1 >= 0) {
				try {
					frameworkMBean.uninstallBundle(testBundle1);
				} catch (IOException io) {}
			}
			if (testBundle2 >= 0) {
				try {
					frameworkMBean.uninstallBundle(testBundle2);
				} catch (IOException io) {}
			}
		}
	}

	/* Test scenario: Install bundles tb1 and tb2. Start  each of them. Stop both. Start both. Stop each of them.
	 * After execution of each operation check their state was changed correctly.
	*/
	public void testBundleStartStop() {
		long testBundle1 = -1;
		long testBundle2 = -1;
		try {
			//install bundle2
			URL entry2 = getContext().getBundle().getEntry("tb2.jar");
			testBundle2 = frameworkMBean.installBundleFromURL("tb2.jar", entry2.toString());

			//install bundle1
			URL entry1 = getContext().getBundle().getEntry("tb1.jar");
			testBundle1 = frameworkMBean.installBundleFromURL("tb1.jar", entry1.toString());

			frameworkMBean.startBundle(testBundle2);
			assertTrue("bundle tb2 is not started for " +  waitTime + " seconds", waitBundleStateChange(testBundle2, "ACTIVE"));

			frameworkMBean.startBundle(testBundle1);
			assertTrue("bundle tb1 is not started for " +  waitTime + " seconds", waitBundleStateChange(testBundle1, "ACTIVE"));

			CompositeData result = frameworkMBean.stopBundles(new long[] {testBundle2, testBundle1});
			assertCompositeDataKeys(result, "BATCH_ACTION_RESULT_TYPE", new String[] { "BundleInError", "Completed", "Error", "Remaining", "Success" });
			assertTrue("stop of bundles doesn't succeed", ((Boolean) result.get("Success")).booleanValue());
			assertTrue("bundle tb2 is not stopped for " +  waitTime + " seconds", waitBundleStateChange(testBundle2, "RESOLVED"));
			assertTrue("bundle tb1 is not stopped for " +  waitTime + " seconds", waitBundleStateChange(testBundle1, "RESOLVED"));

			result = frameworkMBean.startBundles(new long[] {testBundle2, testBundle1});
			assertCompositeDataKeys(result, "BATCH_ACTION_RESULT_TYPE", new String[] { "BundleInError", "Completed", "Error", "Remaining", "Success" });
			assertTrue("start of bundles doesn't succeed", ((Boolean) result.get("Success")).booleanValue());
			assertTrue("bundle tb2 is not started for " +  waitTime + " seconds", waitBundleStateChange(testBundle2, "ACTIVE"));
			assertTrue("bundle tb1 is not started for " +  waitTime + " seconds", waitBundleStateChange(testBundle1, "ACTIVE"));

			frameworkMBean.stopBundle(testBundle2);
			assertTrue("bundle tb2 is not stopped for " +  waitTime + " seconds", waitBundleStateChange(testBundle2, "RESOLVED"));

			frameworkMBean.stopBundle(testBundle1);
			assertTrue("bundle tb1 is not stopped for " +  waitTime + " seconds", waitBundleStateChange(testBundle1, "RESOLVED"));
		} catch(Exception io) {
			io.printStackTrace();
			fail("Exception occured", io);
		} finally {
			if (testBundle1 >= 0) {
				try {
					frameworkMBean.uninstallBundle(testBundle1);
				} catch (IOException io) {}
			}
			if (testBundle2 >= 0) {
				try {
					frameworkMBean.uninstallBundle(testBundle2);
				} catch (IOException io) {}
			}
		}
	}

	/* Test scenario: Install 2 bundles; first one export second one import packages; Un-install the first one, than install it.
	*  Check that the second one is in INSTALLED state. Call refresh bundle. Check that second one moved to RESOLVED state.
	*  Repeat the same test using the refresh bundles method.
	*/
	public void testBundleRefresh() {
		long testBundle1 = -1;
		long testBundle2 = -1;
		try {
			//install bundle2
			URL entry2 = getContext().getBundle().getEntry("tb2.jar");
			testBundle2 = frameworkMBean.installBundleFromURL("tb2.jar", entry2.toString());

			//install bundle1
			URL entry1 = getContext().getBundle().getEntry("tb1.jar");
			testBundle1 = frameworkMBean.installBundleFromURL("tb1.jar", entry1.toString());

			//test refresh bundle
			frameworkMBean.uninstallBundle(testBundle2);
			testBundle2 = frameworkMBean.installBundleFromURL("tb2.jar", entry2.toString());
			assertTrue("bundle tb1 is not moved to installed state for " +  waitTime + " seconds", waitBundleStateChange(testBundle1, "INSTALLED"));
			frameworkMBean.refreshBundle(testBundle2);
			assertTrue("resolve of bundle tb1 doesn't succeed for " + waitTime + " seconds", waitBundleStateChange(testBundle1, "RESOLVED"));

			//test refresh bundles
			frameworkMBean.uninstallBundle(testBundle2);
			frameworkMBean.refreshBundle(testBundle1);
			assertTrue("bundle tb1 is not moved to installed state for " +  waitTime + " seconds", waitBundleStateChange(testBundle1, "INSTALLED"));
			testBundle2 = frameworkMBean.installBundleFromURL("tb2.jar", entry2.toString());
			frameworkMBean.refreshBundles(new long[] {testBundle1, testBundle2});
			assertTrue("resolve of bundle tb1 doesn't succeed for " + waitTime + " seconds", waitBundleStateChange(testBundle1, "RESOLVED"));
		} catch(Exception io) {
			io.printStackTrace();
			fail("Exception occured", io);
		} finally {
			if (testBundle1 >= 0) {
				try {
					frameworkMBean.uninstallBundle(testBundle1);
				} catch (Exception io) {}
			}
			if (testBundle2 >= 0) {
				try {
					frameworkMBean.uninstallBundle(testBundle2);
				} catch (Exception io) {}
			}
		}
	}

	public void testItem() {
		Item item = new Item("Key", "The key of the property",SimpleType.STRING);
		CompositeType type = Item.compositeType("Test type", "Test description", item);
		assertTrue("item was not created with the right key", type.containsKey("Key"));
	}

	public void testItemArrayType() {
		ArrayType<String[]> type = Item.arrayType(1, SimpleType.STRING);
		assertTrue("type was not created with right dimentsion", type.getDimension() == 1);
		assertTrue("type was not created as array", type.isArray());
	}

	public void testItemCompositeType() {
		Item item1 = new Item("Key", "The key of the property",SimpleType.STRING);
		Item item2 = new Item("Value", "The value of the property",SimpleType.STRING);
		CompositeType type = Item.compositeType("Test type", "Test description", item1, item2);
		assertTrue("composite type doesn't contain key item", type.containsKey("Key"));
		assertTrue("composite type doesn't contain value item", type.containsKey("Value"));
	}

	public void testItemExtend() {
		Item item1 = new Item("Key", "The key of the property",SimpleType.STRING);
		Item item2 = new Item("Value", "The value of the property",SimpleType.STRING);
		CompositeType type1 = Item.compositeType("Test type", "Test description", item1, item2);
		Item item3 = new Item("Type", "The type of the property",SimpleType.STRING);
		CompositeType type2 = Item.extend(type1, "Type2", "Type2 description", item3);
		assertTrue("composite type doesn't contain key item", type2.containsKey("Key"));
		assertTrue("composite type doesn't contain value item", type2.containsKey("Value"));
		assertTrue("composite type doesn't contain type item", type2.containsKey("Type"));
	}

	public void testItemTabularType() {
		Item item1 = new Item("Key", "The key of the property",SimpleType.STRING);
		Item item2 = new Item("Value", "The value of the property",SimpleType.STRING);
		CompositeType type1 = Item.compositeType("Test type", "Test description", item1, item2);
		TabularType tabularType = Item.tabularType("TypeName", "TypeDescription", type1, "Key");
		assertTrue("tabular type doesn't contain key item", tabularType.getRowType().containsKey("Key"));
		assertTrue("composite type doesn't contain value item", tabularType.getRowType().containsKey("Value"));
		assertTrue("composite type doesn't have as a key the Key attribute", tabularType.getIndexNames().contains("Key"));
	}

	public void testItemComplex() {
		ArrayType<String[][]> arrayType = Item.arrayType(2, SimpleType.STRING);
		Item item1 = new Item("arr", "The array property",arrayType);
		Item item2 = new Item("key", "The key property",SimpleType.STRING);
		CompositeType type1 = Item.compositeType("Test type", "Test description", item1);
		CompositeType type2 = Item.extend(type1, "Type2", "Type2 description", item2);
		assertTrue("composite type doesn't contain key item", type2.containsKey("arr"));
		assertTrue("composite type doesn't contain value item", type2.containsKey("key"));
		TabularType tabularType = Item.tabularType("TypeName", "TypeDescription", type2, "key");
		assertTrue("tabular type doesn't contain key item", tabularType.getRowType().containsKey("key"));
		assertTrue("composite type doesn't contain arr item", tabularType.getRowType().containsKey("arr"));
		assertTrue("composite type doesn't have as a key the key attribute", tabularType.getIndexNames().contains("key"));
	}

	public void testBundleInstallAndUpdate() {
		long testBundle = -1;
		try {
			File destFile = getContext().getDataFile("/tb2.jar");
			if (destFile == null) return;
			createFile(destFile, getContext().getBundle().getEntry("tb2.jar").openStream());
			testBundle = frameworkMBean.installBundle("file:" + destFile.getAbsolutePath());
			long lastModifiedTime = bundleStateMBean.getLastModified(testBundle);
			//wait some time
			Thread.sleep(10);
			//update bundle
			frameworkMBean.updateBundle(testBundle);
			//get new last modification time
			long newLastModifiedTime = bundleStateMBean.getLastModified(testBundle);
			//check that newest last modification time is bigger than the previous one
			assertTrue("after update bundle from url the bundle's last modified time is not changed", newLastModifiedTime > lastModifiedTime);
		} catch(Exception io) {
			io.printStackTrace();
			fail("Exception occured", io);
		} finally {
			if (testBundle >= 0) {
				try {
					frameworkMBean.uninstallBundle(testBundle);
				} catch (IOException io) {
					fail("Exception occured", io);
				}
			}
			File destFile = getContext().getDataFile("/tb2.jar");
			if (destFile.exists()) {
				destFile.delete();
			}
		}
	}

	public void testBundlesInstallAndUpdate() {
		long[] testBundles = new long[] {-1, -1};
		try {
			File destFile2 = getContext().getDataFile("/tb2.jar");
			File destFile1 = getContext().getDataFile("/tb1.jar");
			if ((destFile2 == null) || (destFile1 == null)) return;
			createFile(destFile2, getContext().getBundle().getEntry("tb2.jar").openStream());
			createFile(destFile1, getContext().getBundle().getEntry("tb1.jar").openStream());

			CompositeData result = frameworkMBean.installBundles(new String[] {"file:" + destFile2.getAbsolutePath(), "file:" + destFile1.getAbsolutePath()} );
			assertCompositeDataKeys(result, "BATCH_ACTION_RESULT_TYPE", new String[] { "BundleInError", "Completed", "Error", "Remaining", "Success" });
			assertTrue("installing bundles from URL doesn't succeed", ((Boolean) result.get("Success")).booleanValue());
			Long[] bundleIds = (Long[]) result.get("Completed");
			assertTrue("installing bundles from URL doesn't return right bundle ids info", (bundleIds != null) && (bundleIds.length == 2));

			testBundles[0] = bundleIds[0].longValue();
			testBundles[1] = bundleIds[1].longValue();
			long lastModifiedTime2 = bundleStateMBean.getLastModified(testBundles[0]);
			long lastModifiedTime1 = bundleStateMBean.getLastModified(testBundles[1]);
			//wait some time
			Thread.sleep(10);
			//update bundle
			frameworkMBean.updateBundles(testBundles);
			//get new last modification time
			long newLastModifiedTime2 = bundleStateMBean.getLastModified(testBundles[0]);
			long newLastModifiedTime1 = bundleStateMBean.getLastModified(testBundles[1]);
			//check that newest last modification time is bigger than the previous one
			assertTrue("after update bundles from url the bundles' last modified time is not changed", (newLastModifiedTime2 > lastModifiedTime2) && (newLastModifiedTime1 > lastModifiedTime1));
		} catch(Exception io) {
			io.printStackTrace();
			fail("Exception occured", io);
		} finally {
			if (testBundles[1] >= 0) {
				try {
					frameworkMBean.uninstallBundle(testBundles[1]);
				} catch (IOException io) {
					fail("Exception occured", io);
				}
			}
			if (testBundles[0] >= 0) {
				try {
					frameworkMBean.uninstallBundle(testBundles[0]);
				} catch (IOException io) {
					fail("Exception occured", io);
				}
			}
			File destFile = getContext().getDataFile("/tb2.jar");
			if (destFile.exists()) {
				destFile.delete();
			}
			destFile = getContext().getDataFile("/tb1.jar");
			if (destFile.exists()) {
				destFile.delete();
			}
		}
	}

	public void testDependencyClosure() throws Exception {
        long testBundle1ID = -1;
        long testBundle2ID = -1;

        try {
            //install and start bundle2
            URL entry2 = getContext().getBundle().getEntry("tb2.jar");
            testBundle2ID = frameworkMBean.installBundleFromURL("tb2.jar", entry2.toString());
            frameworkMBean.startBundles(new long[] {testBundle2ID});

            long[] closure1 = frameworkMBean.getDependencyClosure(new long [] {testBundle2ID});
            assertTrue(Arrays.equals(new long [] {testBundle2ID}, closure1));

            //install and start bundle1
            URL entry1 = getContext().getBundle().getEntry("tb1.jar");
            testBundle1ID = frameworkMBean.installBundleFromURL("tb1.jar", entry1.toString());
            frameworkMBean.startBundles(new long[] {testBundle1ID});

            long[] closure2 = frameworkMBean.getDependencyClosure(new long [] {testBundle2ID});
            Set<Long> actualBundles = longArrayToSet(closure2);
            Set<Long> bothBundles = new HashSet<Long>(Arrays.asList(testBundle1ID, testBundle2ID));
            assertEquals(bothBundles, actualBundles);

            long[] closure3 = frameworkMBean.getDependencyClosure(new long [] {testBundle1ID, testBundle2ID});
            Set<Long> actualBundles3 = longArrayToSet(closure3);
            assertEquals(bothBundles, actualBundles3);
        } finally {
            if (testBundle1ID >= 0)
                frameworkMBean.uninstallBundle(testBundle1ID);

            if (testBundle2ID >= 0)
                frameworkMBean.uninstallBundle(testBundle2ID);
        }
	}

	public void testGetRemovalPendingBundles() throws Exception {
        long testBundle1ID = -1;

        try {
            //install and start bundle1 and bundle2
            URL entry2 = getContext().getBundle().getEntry("tb2.jar");
            long testBundle2ID = frameworkMBean.installBundleFromURL("tb2.jar", entry2.toString());
            URL entry1 = getContext().getBundle().getEntry("tb1.jar");
            testBundle1ID = frameworkMBean.installBundleFromURL("tb1.jar", entry1.toString());
            frameworkMBean.startBundles(new long[] {testBundle1ID, testBundle2ID});

            assertEquals("Precondition failed", 0, frameworkMBean.getRemovalPendingBundles().length);

            frameworkMBean.uninstallBundle(testBundle2ID);
            assertTrue(Arrays.equals(new long [] {testBundle2ID}, frameworkMBean.getRemovalPendingBundles()));
        } finally {
            if (testBundle1ID >= 0)
                frameworkMBean.uninstallBundle(testBundle1ID);

            // bundle2 is uninstalled as part of the test
        }
	}

    public void testRefreshBundleAndWait() throws Exception {
        long testBundle1ID = -1;
        long testBundle2ID = -1;
        try {
            //install bundle2
            URL entry2 = getContext().getBundle().getEntry("tb2.jar");
            testBundle2ID = frameworkMBean.installBundleFromURL("tb2.jar", entry2.toString());
            frameworkMBean.startBundle(testBundle2ID);

            //install bundle1
            URL entry1 = getContext().getBundle().getEntry("tb1.jar");
            testBundle1ID = frameworkMBean.installBundleFromURL("tb1.jar", entry1.toString());
            frameworkMBean.startBundle(testBundle1ID);

            //test refresh bundle
            frameworkMBean.uninstallBundle(testBundle2ID);
            testBundle2ID = -1;

            assertEquals("ACTIVE", bundleStateMBean.getState(testBundle1ID));
            assertFalse(frameworkMBean.refreshBundleAndWait(testBundle1ID));
            assertEquals("INSTALLED", bundleStateMBean.getState(testBundle1ID));

            // install bundle 2 again
            testBundle2ID = frameworkMBean.installBundleFromURL("tb2.jar", entry2.toString());

            assertEquals("INSTALLED", bundleStateMBean.getState(testBundle1ID));
            assertTrue(frameworkMBean.refreshBundleAndWait(testBundle1ID));
            assertEquals("RESOLVED", bundleStateMBean.getState(testBundle1ID));
        } finally {
            if (testBundle1ID >= 0)
                frameworkMBean.uninstallBundle(testBundle1ID);

            if (testBundle2ID >= 0)
                frameworkMBean.uninstallBundle(testBundle2ID);
        }
    }

    public void testRefreshBundlesAndWait() throws Exception {
        long testBundle1ID = -1;
        long testBundle2ID = -1;
        try {
            //install bundle2
            URL entry2 = getContext().getBundle().getEntry("tb2.jar");
            testBundle2ID = frameworkMBean.installBundleFromURL("tb2.jar", entry2.toString());
            frameworkMBean.startBundle(testBundle2ID);

            //install bundle1
            URL entry1 = getContext().getBundle().getEntry("tb1.jar");
            testBundle1ID = frameworkMBean.installBundleFromURL("tb1.jar", entry1.toString());
            frameworkMBean.startBundle(testBundle1ID);

            Set<Long> bothBundles = new HashSet<Long>(Arrays.asList(testBundle1ID, testBundle2ID));

            assertEquals("ACTIVE", bundleStateMBean.getState(testBundle1ID));
            assertEquals("ACTIVE", bundleStateMBean.getState(testBundle2ID));
            CompositeData res0 = frameworkMBean.refreshBundlesAndWait(new long [] {testBundle1ID, testBundle2ID});
            assertTrue((Boolean) res0.get("Success"));
            assertEquals(bothBundles, new HashSet<Long>(Arrays.asList((Long []) res0.get("Completed"))));
            assertEquals("ACTIVE", bundleStateMBean.getState(testBundle1ID));
            assertEquals("ACTIVE", bundleStateMBean.getState(testBundle2ID));

            //test refresh bundle
            frameworkMBean.uninstallBundle(testBundle2ID);
            testBundle2ID = -1;

            assertEquals("ACTIVE", bundleStateMBean.getState(testBundle1ID));
            CompositeData res1 = frameworkMBean.refreshBundlesAndWait(new long [] {testBundle1ID});
            assertCompositeDataKeys(res1, "BATCH_RESOLVE_RESULT_TYPE", new String [] {"Completed", "Success"});
            assertFalse((Boolean) res1.get("Success"));
            assertEquals(0, ((Long []) res1.get("Completed")).length);
            assertEquals("INSTALLED", bundleStateMBean.getState(testBundle1ID));

            // install bundle 2 again
            testBundle2ID = frameworkMBean.installBundleFromURL("tb2.jar", entry2.toString());
            bothBundles = new HashSet<Long>(Arrays.asList(testBundle1ID, testBundle2ID));

            assertEquals("INSTALLED", bundleStateMBean.getState(testBundle1ID));
            assertEquals("INSTALLED", bundleStateMBean.getState(testBundle2ID));
            CompositeData res2 = frameworkMBean.refreshBundlesAndWait(new long [] {testBundle1ID, testBundle2ID});
            assertCompositeDataKeys(res2, "BATCH_RESOLVE_RESULT_TYPE", new String [] {"Completed", "Success"});
            assertTrue((Boolean) res2.get("Success"));
            assertEquals(bothBundles, new HashSet<Long>(Arrays.asList((Long []) res2.get("Completed"))));
            assertEquals("RESOLVED", bundleStateMBean.getState(testBundle1ID));
            assertEquals("RESOLVED", bundleStateMBean.getState(testBundle2ID));
        } finally {
            if (testBundle1ID >= 0)
                frameworkMBean.uninstallBundle(testBundle1ID);

            if (testBundle2ID >= 0)
                frameworkMBean.uninstallBundle(testBundle2ID);
        }
	}

    public void testRefreshBundlesAndWaitNull() throws Exception {
        long testBundle1ID = -1;
        long testBundle2ID = -1;
        try {
            //install bundle2
            URL entry2 = getContext().getBundle().getEntry("tb2.jar");
            testBundle2ID = frameworkMBean.installBundleFromURL("tb2.jar", entry2.toString());
            frameworkMBean.startBundle(testBundle2ID);

            //install bundle1
            URL entry1 = getContext().getBundle().getEntry("tb1.jar");
            testBundle1ID = frameworkMBean.installBundleFromURL("tb1.jar", entry1.toString());
            frameworkMBean.startBundle(testBundle1ID);

            Set<Long> bothBundles = new HashSet<Long>(Arrays.asList(testBundle1ID, testBundle2ID));

            assertEquals("ACTIVE", bundleStateMBean.getState(testBundle1ID));
            assertEquals("ACTIVE", bundleStateMBean.getState(testBundle2ID));
            CompositeData res0 = frameworkMBean.refreshBundlesAndWait(null);
            HashSet<Long> completed0 = new HashSet<Long>(Arrays.asList((Long []) res0.get("Completed")));
            assertTrue(completed0.containsAll(bothBundles));
            assertEquals("ACTIVE", bundleStateMBean.getState(testBundle1ID));
            assertEquals("ACTIVE", bundleStateMBean.getState(testBundle2ID));

            //test refresh bundle
            frameworkMBean.uninstallBundle(testBundle2ID);
            testBundle2ID = -1;

            assertEquals("ACTIVE", bundleStateMBean.getState(testBundle1ID));
            CompositeData res1 = frameworkMBean.refreshBundlesAndWait(null);
            assertCompositeDataKeys(res1, "BATCH_RESOLVE_RESULT_TYPE", new String [] {"Completed", "Success"});
            assertFalse((Boolean) res1.get("Success"));
            HashSet<Long> completed1 = new HashSet<Long>(Arrays.asList((Long []) res1.get("Completed")));
            assertFalse(completed1.contains(testBundle1ID));
            assertEquals("INSTALLED", bundleStateMBean.getState(testBundle1ID));

            // install bundle 2 again
            testBundle2ID = frameworkMBean.installBundleFromURL("tb2.jar", entry2.toString());

            assertEquals("INSTALLED", bundleStateMBean.getState(testBundle1ID));
            assertEquals("INSTALLED", bundleStateMBean.getState(testBundle2ID));
            CompositeData res2 = frameworkMBean.refreshBundlesAndWait(new long [] {testBundle1ID});
            assertCompositeDataKeys(res2, "BATCH_RESOLVE_RESULT_TYPE", new String [] {"Completed", "Success"});
            assertTrue((Boolean) res2.get("Success"));
            assertTrue(Arrays.equals(new Long [] {testBundle1ID}, (Long []) res2.get("Completed")));
            assertEquals("RESOLVED", bundleStateMBean.getState(testBundle1ID));
            assertEquals("RESOLVED", bundleStateMBean.getState(testBundle2ID));
        } finally {
            if (testBundle1ID >= 0)
                frameworkMBean.uninstallBundle(testBundle1ID);

            if (testBundle2ID >= 0)
                frameworkMBean.uninstallBundle(testBundle2ID);
        }
    }

    public void testResolve() throws Exception {
        long testBundle1ID = -1;
        long testBundle2ID = -1;
        long testBundle3ID = -1;
        try {
            //install bundle1, it should not resolve because it has a dependency on bundle2
            URL entry1 = getContext().getBundle().getEntry("tb1.jar");
            testBundle1ID = frameworkMBean.installBundleFromURL("tb1.jar", entry1.toString());

            URL entry3 = getContext().getBundle().getEntry("tb3.jar");
            testBundle3ID = frameworkMBean.installBundleFromURL("tb3.jar", entry3.toString());

            CompositeData res1 = frameworkMBean.resolve(new long [] {testBundle1ID, testBundle3ID});
            assertCompositeDataKeys(res1, "BATCH_RESOLVE_RESULT_TYPE", new String [] {"Completed", "Success"});

            assertFalse((Boolean) res1.get("Success"));
            assertTrue(Arrays.equals(new Long [] {testBundle3ID}, (Long []) res1.get("Completed")));

            URL entry2 = getContext().getBundle().getEntry("tb2.jar");
            testBundle2ID = frameworkMBean.installBundleFromURL("tb2.jar", entry2.toString());

            CompositeData res2 = frameworkMBean.resolve(new long [] {testBundle1ID, testBundle2ID});
            assertCompositeDataKeys(res2, "BATCH_RESOLVE_RESULT_TYPE", new String [] {"Completed", "Success"});
            assertTrue((Boolean) res2.get("Success"));
            Set<Long> expectedCompleted = new HashSet<Long>(Arrays.asList(testBundle1ID, testBundle2ID));
            Set<Long> actualCompleted = new HashSet<Long>(Arrays.asList((Long []) res2.get("Completed")));
            assertEquals(expectedCompleted, actualCompleted);
        } finally {
            if (testBundle1ID >= 0)
                frameworkMBean.uninstallBundle(testBundle1ID);

            if (testBundle2ID >= 0)
                frameworkMBean.uninstallBundle(testBundle2ID);

            if (testBundle3ID >= 0)
                frameworkMBean.uninstallBundle(testBundle3ID);
        }
	}

    public void testResolveNull() throws Exception {
        long testBundle1ID = -1;
        long testBundle2ID = -1;
        long testBundle3ID = -1;
        try {
            //install bundle1, it should not resolve because it has a dependency on bundle2
            URL entry1 = getContext().getBundle().getEntry("tb1.jar");
            testBundle1ID = frameworkMBean.installBundleFromURL("tb1.jar", entry1.toString());

            URL entry3 = getContext().getBundle().getEntry("tb3.jar");
            testBundle3ID = frameworkMBean.installBundleFromURL("tb3.jar", entry3.toString());

            CompositeData res1 = frameworkMBean.resolve(null);
            assertCompositeDataKeys(res1, "BATCH_RESOLVE_RESULT_TYPE", new String [] {"Completed", "Success"});
            assertFalse((Boolean) res1.get("Success"));
            HashSet<Long> completed1 = new HashSet<Long>(Arrays.asList((Long []) res1.get("Completed")));
            assertFalse(completed1.contains(testBundle1ID));
            assertTrue(completed1.contains(testBundle3ID));

            URL entry2 = getContext().getBundle().getEntry("tb2.jar");
            testBundle2ID = frameworkMBean.installBundleFromURL("tb2.jar", entry2.toString());

            CompositeData res2 = frameworkMBean.resolve(null);
            assertCompositeDataKeys(res2, "BATCH_RESOLVE_RESULT_TYPE", new String [] {"Completed", "Success"});
            assertTrue((Boolean) res2.get("Success"));
            Set<Long> expectedCompleted = new HashSet<Long>(Arrays.asList(testBundle1ID, testBundle2ID));
            Set<Long> actualCompleted = new HashSet<Long>(Arrays.asList((Long []) res2.get("Completed")));
            assertTrue(actualCompleted.containsAll(expectedCompleted));
        } finally {
            if (testBundle1ID >= 0)
                frameworkMBean.uninstallBundle(testBundle1ID);

            if (testBundle2ID >= 0)
                frameworkMBean.uninstallBundle(testBundle2ID);

            if (testBundle3ID >= 0)
                frameworkMBean.uninstallBundle(testBundle3ID);
        }
    }

    public void testExceptions() {
		assertNotNull(frameworkMBean);

		//test getFrameworkStartLevel method
		try {
			frameworkMBean.getFrameworkStartLevel();
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}

		//test getInitialBundleStartLevel method
		try {
			frameworkMBean.getInitialBundleStartLevel();
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}

		//test installBundle method
		try {
			frameworkMBean.installBundle(STRING_NULL);
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		try {
			frameworkMBean.installBundle(STRING_EMPTY);
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		try {
			frameworkMBean.installBundle(STRING_SPECIAL_SYMBOLS);
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}

		//test installBundleFromURL method
		try {
			frameworkMBean.installBundleFromURL(STRING_NULL, STRING_NULL);
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		try {
			frameworkMBean.installBundleFromURL(STRING_EMPTY, STRING_EMPTY);
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		try {
			frameworkMBean.installBundleFromURL(STRING_SPECIAL_SYMBOLS, STRING_SPECIAL_SYMBOLS);
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		try {
			frameworkMBean.installBundleFromURL(STRING_NULL, STRING_URL);
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		try {
			frameworkMBean.installBundleFromURL(STRING_EMPTY, STRING_URL);
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		try {
			frameworkMBean.installBundleFromURL(STRING_SPECIAL_SYMBOLS, STRING_URL);
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}

		//test installBundles method
		try {
			frameworkMBean.installBundles(null);
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}
		try {
			frameworkMBean.installBundles(new String[] {});
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}
		try {
			frameworkMBean.installBundles(new String[] { STRING_NULL });
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}
		try {
			frameworkMBean.installBundles(new String[] { STRING_EMPTY });
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}
		try {
			frameworkMBean.installBundles(new String[] { STRING_SPECIAL_SYMBOLS });
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}
		try {
			frameworkMBean.installBundles(new String[] { STRING_URL });
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}

		//test installBundlesFromURL method
		try {
			frameworkMBean.installBundlesFromURL(null, null);
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}
		try {
			frameworkMBean.installBundlesFromURL(new String[] {} , new String[] {});
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}
		try {
			frameworkMBean.installBundlesFromURL(new String[] { STRING_NULL } , new String[] {});
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}
		try {
			frameworkMBean.installBundlesFromURL(new String[] { STRING_URL } , new String[] { STRING_URL });
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}
		try {
			frameworkMBean.installBundlesFromURL(new String[] { STRING_EMPTY } , new String[] {});
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}
		try {
			frameworkMBean.installBundlesFromURL(new String[] { STRING_EMPTY } , new String[] { STRING_URL, STRING_NULL });
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}

		//test refreshBundle method
		try {
			frameworkMBean.refreshBundle(LONG_NEGATIVE);
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		try {
			frameworkMBean.refreshBundle(LONG_BIG);
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}

		//test refreshBundles method
		try {
			frameworkMBean.refreshBundles(null);
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}
		try {
			frameworkMBean.refreshBundles(new long[] { LONG_NEGATIVE });
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		try {
			frameworkMBean.refreshBundles(new long[] { LONG_BIG });
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}

		//test resolveBundle method
		try {
			frameworkMBean.resolveBundle( LONG_NEGATIVE );
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}
		try {
			frameworkMBean.resolveBundle( LONG_BIG );
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		//test resolveBundles method
		try {
			frameworkMBean.resolveBundles( null );
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}
		try {
			frameworkMBean.resolveBundles( new long[] { LONG_NEGATIVE });
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		try {
			frameworkMBean.resolveBundles( new long[] { LONG_BIG } );
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}

		//test setBundleStartLevel method
		try {
			frameworkMBean.setBundleStartLevel(LONG_NEGATIVE, INT_BIG);
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		try {
			frameworkMBean.setBundleStartLevel(LONG_BIG, INT_BIG);
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		try {
			frameworkMBean.setBundleStartLevel(1, INT_NEGATIVE);
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}

		//test setBundleStartLevels method
		try {
			frameworkMBean.setBundleStartLevels(null, null);
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}
		try {
			frameworkMBean.setBundleStartLevels(new long[] {}, new int[] {});
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}
		try {
			frameworkMBean.setBundleStartLevels(new long[] { LONG_BIG }, new int[] {});
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}
		try {
			frameworkMBean.setBundleStartLevels(new long[] { 1 }, new int[] { INT_NEGATIVE });
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}
		try {
			frameworkMBean.setBundleStartLevels(new long[] { 1 }, null);
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}

		//test setFrameworkStartLevel method
		try {
			frameworkMBean.setFrameworkStartLevel(INT_NEGATIVE);
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}

		//test setInitialBundleStartLevel method
		try {
			frameworkMBean.setInitialBundleStartLevel(INT_NEGATIVE);
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}

		//test startBundle method
		try {
			frameworkMBean.startBundle(LONG_NEGATIVE);
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}
		try {
			frameworkMBean.startBundle(LONG_BIG);
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		//test startBundles method
		try {
			frameworkMBean.startBundles(null);
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}
		try {
			frameworkMBean.startBundles(new long[] { LONG_NEGATIVE });
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}
		try {
			frameworkMBean.startBundles(new long[] { LONG_BIG });
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}

		//test stopBundle method
		try {
			frameworkMBean.stopBundle(LONG_NEGATIVE);
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}
		try {
			frameworkMBean.stopBundle(LONG_BIG);
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		//test stopBundles method
		try {
			frameworkMBean.stopBundles(null);
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}
		try {
			frameworkMBean.stopBundles(new long[] { LONG_NEGATIVE });
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}
		try {
			frameworkMBean.stopBundles(new long[] { LONG_BIG });
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}

		//test uninstallBundle method
		try {
			frameworkMBean.uninstallBundle(LONG_NEGATIVE);
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}
		try {
			frameworkMBean.uninstallBundle(LONG_BIG);
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		//test uninstallBundles method
		try {
			frameworkMBean.uninstallBundles(null);
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}
		try {
			frameworkMBean.uninstallBundles(new long[] { LONG_NEGATIVE });
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}
		try {
			frameworkMBean.uninstallBundles(new long[] { LONG_BIG });
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}

		//test updateBundle method
		try {
			frameworkMBean.updateBundle(LONG_NEGATIVE);
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}
		try {
			frameworkMBean.updateBundle(LONG_BIG);
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		//test updateBundles method
		try {
			frameworkMBean.updateBundles(null);
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}
		try {
			frameworkMBean.updateBundles(new long[] { LONG_NEGATIVE });
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}
		try {
			frameworkMBean.updateBundles(new long[] { LONG_BIG });
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}

		//test updateBundleFromURL method
		try {
			frameworkMBean.updateBundleFromURL(LONG_NEGATIVE, null);
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}
		try {
			frameworkMBean.updateBundleFromURL(1, STRING_URL);
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		try {
			frameworkMBean.updateBundleFromURL(LONG_BIG, STRING_EMPTY);
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		//test updateBundlesFromURL method
		try {
			frameworkMBean.updateBundlesFromURL(null, null);
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}
		catch (IllegalArgumentException e) {
			fail("unexpected exception", e);
		}
		try {
			frameworkMBean.updateBundlesFromURL(new long[] { 1, LONG_NEGATIVE, LONG_BIG }, new String[] { STRING_NULL, STRING_SPECIAL_SYMBOLS });
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}
		catch (IllegalArgumentException e) {
			fail("unexpected exception", e);
		}
	}



	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		super.waitForUnRegistering(createObjectName(FrameworkMBean.OBJECTNAME));
	}

	private boolean waitBundleStateChange(long bundleId, String state) {
		boolean equal = false;
		int count = waitTime*10;
		try {
			while (count-- > 0) {
				if (bundleStateMBean.getState(bundleId).equals(state)) {
					equal = true;
					break;
				} else {
					synchronized (this) {
						this.wait(100);					}
				}
			}
		} catch(InterruptedException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
		return equal;
	}

	private void createFile(File destFile, InputStream in) throws IOException {
		FileOutputStream fOut = new FileOutputStream(destFile);
		byte[] array = new byte[1024];
		int read = in.read(array);
		while (read > 0) {
			fOut.write(array, 0, read);
			read = in.read(array);
		}
		in.close();
		fOut.close();
	}

    private Set<Long> longArrayToSet(long[] array) {
        Set<Long> set = new HashSet<Long>();
        for (long l : array) {
            set.add(l);
        }
        return set;
    }

	private final int waitTime = 20;
}
