package org.osgi.test.cases.jmx.junit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

import javax.management.RuntimeMBeanException;
import javax.management.openmbean.ArrayType;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularType;

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
		super.waitForRegistering(createObjectName(BundleStateMBean.OBJECTNAME));		
		bundleStateMBean = getMBeanFromServer(BundleStateMBean.OBJECTNAME,
											BundleStateMBean.class);
		
	}

	public void testFrameworkMBeanExists() {
		assertNotNull(frameworkMBean);
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
			assertTrue("Exception occured: " + io.toString(), false);
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
			assertTrue("Exception ocurred: " + io.toString(), false);
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
			assertTrue("Exception ocurred: " + io.toString(), false);
		} finally {
			if (testBundle >= 0) {
				try {
					frameworkMBean.uninstallBundle(testBundle);
				} catch (IOException io) {
					assertTrue("Exception ocurred: " + io.toString(), false);				
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
			assertTrue("Exception ocurred: " + io.toString(), false);
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
			Thread.currentThread().sleep(10);
			//update bundle
			frameworkMBean.updateBundleFromURL(testBundle, entry2.toString());
			//get new last modification time
			long newLastModifiedTime = bundleStateMBean.getLastModified(testBundle);
			//check that newest last modification time is bigger than the previous one
			assertTrue("after update bundle from url the bundle's last modified time is not changed", newLastModifiedTime > lastModifiedTime);
		} catch(Exception io) {
			io.printStackTrace();
			assertTrue("Exception ocurred: " + io.toString(), false);
		} finally {
			if (testBundle >= 0) {
				try {
					frameworkMBean.uninstallBundle(testBundle);
				} catch (IOException io) {
					assertTrue("Exception ocurred: " + io.toString(), false);				
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
			Thread.currentThread().sleep(10);
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
			assertTrue("Exception ocurred: " + io.toString(), false);
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
			assertTrue("Exception ocurred: " + io.toString(), false);
		} finally {
			if (testBundle >= 0) {
				try {
					frameworkMBean.uninstallBundle(testBundle);
				} catch (IOException io) {
					assertTrue("Exception ocurred: " + io.toString(), false);				
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
			assertTrue("Exception ocurred: " + io.toString(), false);
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
			assertTrue("Exception ocurred: " + io.toString(), false);
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
			assertTrue("Exception ocurred: " + io.toString(), false);
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
		ArrayType type = Item.arrayType(1, SimpleType.STRING);
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
	
	public void testBundleInstallAndUpdate() {
		long testBundle = -1;
		try {
			File destFile = getContext().getDataFile("/tb2.jar");
			if (destFile == null) return;
			createFile(destFile, getContext().getBundle().getEntry("tb2.jar").openStream());
			testBundle = frameworkMBean.installBundle("file:" + destFile.getAbsolutePath());
			long lastModifiedTime = bundleStateMBean.getLastModified(testBundle);
			//wait some time
			Thread.currentThread().sleep(10);
			//update bundle
			frameworkMBean.updateBundle(testBundle);
			//get new last modification time
			long newLastModifiedTime = bundleStateMBean.getLastModified(testBundle);
			//check that newest last modification time is bigger than the previous one
			assertTrue("after update bundle from url the bundle's last modified time is not changed", newLastModifiedTime > lastModifiedTime);
		} catch(Exception io) {
			io.printStackTrace();
			assertTrue("Exception ocurred: " + io.toString(), false);
		} finally {
			if (testBundle >= 0) {
				try {
					frameworkMBean.uninstallBundle(testBundle);
				} catch (IOException io) {
					assertTrue("Exception ocurred: " + io.toString(), false);				
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
			Thread.currentThread().sleep(10);
			//update bundle
			frameworkMBean.updateBundles(testBundles);
			//get new last modification time
			long newLastModifiedTime2 = bundleStateMBean.getLastModified(testBundles[0]);
			long newLastModifiedTime1 = bundleStateMBean.getLastModified(testBundles[1]);
			//check that newest last modification time is bigger than the previous one
			assertTrue("after update bundles from url the bundles' last modified time is not changed", (newLastModifiedTime2 > lastModifiedTime2) && (newLastModifiedTime1 > lastModifiedTime1));
		} catch(Exception io) {
			io.printStackTrace();
			assertTrue("Exception ocurred: " + io.toString(), false);
		} finally {
			if (testBundles[1] >= 0) {
				try {
					frameworkMBean.uninstallBundle(testBundles[1]);
				} catch (IOException io) {
					assertTrue("Exception ocurred: " + io.toString(), false);				
				}
			}
			if (testBundles[0] >= 0) {
				try {
					frameworkMBean.uninstallBundle(testBundles[0]);
				} catch (IOException io) {
					assertTrue("Exception ocurred: " + io.toString(), false);				
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
	
	public void testExceptions() {
		/*
		 * Bug report for this method is https://www.osgi.org/members/bugzilla/show_bug.cgi?id=1605
		 */
		assertNotNull(frameworkMBean);

		//test getFrameworkStartLevel method
		try {
			frameworkMBean.getFrameworkStartLevel();			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method getFrameworkStartLevel throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}
		
		//test getInitialBundleStartLevel method		
		try {
			frameworkMBean.getInitialBundleStartLevel();
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method getInitialBundleStartLevel throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}
		
		//test installBundle method
		try {
			frameworkMBean.installBundle(STRING_NULL);
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method installBundle throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}
		try {
			frameworkMBean.installBundle(STRING_EMPTY);
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method installBundle throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}
		try {
			frameworkMBean.installBundle(STRING_SPECIAL_SYMBOLS);
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method installBundle throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}

		//test installBundleFromURL method
		try {
			frameworkMBean.installBundleFromURL(STRING_NULL, STRING_NULL);
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method installBundleFromURL throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}
		try {
			frameworkMBean.installBundleFromURL(STRING_EMPTY, STRING_EMPTY);
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method installBundleFromURL throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}
		try {
			frameworkMBean.installBundleFromURL(STRING_SPECIAL_SYMBOLS, STRING_SPECIAL_SYMBOLS);
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method installBundleFromURL throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}
		try {
			frameworkMBean.installBundleFromURL(STRING_NULL, STRING_URL);
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method installBundleFromURL throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}
		try {
			frameworkMBean.installBundleFromURL(STRING_EMPTY, STRING_URL);
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method installBundleFromURL throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		
		try {
			frameworkMBean.installBundleFromURL(STRING_SPECIAL_SYMBOLS, STRING_URL);
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method installBundleFromURL throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		
		
		//test installBundles method
		try {
			frameworkMBean.installBundles(null);
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method installBundles throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		
		try {
			frameworkMBean.installBundles(new String[] {});
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method installBundles throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		
		try {
			frameworkMBean.installBundles(new String[] { STRING_NULL });
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method installBundles throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		
		try {
			frameworkMBean.installBundles(new String[] { STRING_EMPTY });
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method installBundles throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		
		try {
			frameworkMBean.installBundles(new String[] { STRING_SPECIAL_SYMBOLS });
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method installBundles throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		
		try {
			frameworkMBean.installBundles(new String[] { STRING_URL });
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method installBundles throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		
	
		//test installBundlesFromURL method		
		try {
			frameworkMBean.installBundlesFromURL(null, null);
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method installBundlesFromURL throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}	
		try {
			frameworkMBean.installBundlesFromURL(new String[] {} , new String[] {});
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method installBundlesFromURL throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		
		try {
			frameworkMBean.installBundlesFromURL(new String[] { STRING_NULL } , new String[] {});
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method installBundlesFromURL throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		
		try {
			frameworkMBean.installBundlesFromURL(new String[] { STRING_URL } , new String[] { STRING_URL });
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method installBundlesFromURL throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		
		try {
			frameworkMBean.installBundlesFromURL(new String[] { STRING_EMPTY } , new String[] {});
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method installBundlesFromURL throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}	
		try {
			frameworkMBean.installBundlesFromURL(new String[] { STRING_EMPTY } , new String[] { STRING_URL, STRING_NULL });
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method installBundlesFromURL throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}			
		
		//test refreshBundle method		
		try {
			frameworkMBean.refreshBundle(LONG_NEGATIVE);
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method refreshBundle throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		
		try {
			frameworkMBean.refreshBundle(LONG_BIG);
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method refreshBundle throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}	
		
		//test refreshBundles method		
		try {
			frameworkMBean.refreshBundles(null);
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method refreshBundles throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}
		try {
			frameworkMBean.refreshBundles(new long[] { LONG_NEGATIVE });
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method refreshBundles throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		
		try {
			frameworkMBean.refreshBundles(new long[] { LONG_BIG });
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method refreshBundles throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		

		//test resolveBundle method		
		try {
			frameworkMBean.resolveBundle( LONG_NEGATIVE );
		} catch(IOException ioException) {
		} catch(RuntimeMBeanException e) {
			//spec describes this method could throw IllegalArgumentException; let's check
			assertRootCauseIllegalArgumentException(e);
		}
		try {
			frameworkMBean.resolveBundle( LONG_BIG );
		} catch(IOException ioException) {
		} catch(RuntimeMBeanException e) {
			//spec describes this method could throw IllegalArgumentException; let's check
			assertRootCauseIllegalArgumentException(e);
		} 
		
		//test resolveBundles method		
		try {
			frameworkMBean.resolveBundles( null );
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method resolveBundles throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		
		try {
			frameworkMBean.resolveBundles( new long[] { LONG_NEGATIVE });
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method resolveBundles throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		
		try {
			frameworkMBean.resolveBundles( new long[] { LONG_BIG } );
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method resolveBundles throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		

		//test setBundleStartLevel method		
		try {
			frameworkMBean.setBundleStartLevel(LONG_NEGATIVE, INT_BIG);
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method setBundleStartLevel throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		
		try {
			frameworkMBean.setBundleStartLevel(LONG_BIG, INT_BIG);
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method setBundleStartLevel throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		
		try {
			frameworkMBean.setBundleStartLevel(1, INT_NEGATIVE);
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method setBundleStartLevel throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}
		
		//test setBundleStartLevels method		
		try {
			frameworkMBean.setBundleStartLevels(null, null);
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method setBundleStartLevels throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}	
		try {
			frameworkMBean.setBundleStartLevels(new long[] {}, new int[] {});
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method setBundleStartLevels throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		
		try {
			frameworkMBean.setBundleStartLevels(new long[] { LONG_BIG }, new int[] {});
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method setBundleStartLevels throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		
		try {
			frameworkMBean.setBundleStartLevels(new long[] { 1 }, new int[] { INT_NEGATIVE });
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method setBundleStartLevels throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		
		try {
			frameworkMBean.setBundleStartLevels(new long[] { 1 }, null);
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method setBundleStartLevels throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		

		//test setFrameworkStartLevel method		
		try {
			frameworkMBean.setFrameworkStartLevel(INT_NEGATIVE);
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method setFrameworkStartLevel throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}
		
		//test setInitialBundleStartLevel method		
		try {
			frameworkMBean.setInitialBundleStartLevel(INT_NEGATIVE);
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method setInitialBundleStartLevel throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}
		
		//test startBundle method		
		try {
			frameworkMBean.startBundle(LONG_NEGATIVE);
		} catch(IOException ioException) {
		}  catch(RuntimeMBeanException e) {
			//spec describes this method could throw IllegalArgumentException; let's check
			assertRootCauseIllegalArgumentException(e);
		}
		try {
			frameworkMBean.startBundle(LONG_BIG);
		} catch(IOException ioException) {
		} catch(RuntimeMBeanException e) {
			//spec describes this method could throw IllegalArgumentException; let's check
			assertRootCauseIllegalArgumentException(e);
		}	

		//test startBundles method		
		try {
			frameworkMBean.startBundles(null);
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method startBundles throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}
		try {
			frameworkMBean.startBundles(new long[] { LONG_NEGATIVE });
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method startBundles throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}
		try {
			frameworkMBean.startBundles(new long[] { LONG_BIG });
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method startBundles throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}
		
		//test stopBundle method		
		try {
			frameworkMBean.stopBundle(LONG_NEGATIVE);
		} catch(IOException ioException) {
		}  catch(RuntimeMBeanException e) {
			//spec describes this method could throw IllegalArgumentException; let's check
			assertRootCauseIllegalArgumentException(e);
		}
		try {
			frameworkMBean.stopBundle(LONG_BIG);
		} catch(IOException ioException) {
		} catch(RuntimeMBeanException e) {
			//spec describes this method could throw IllegalArgumentException; let's check
			assertRootCauseIllegalArgumentException(e);
		}	
		
		//test stopBundles method
		try {
			frameworkMBean.stopBundles(null);
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method stopBundles throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}
		try {
			frameworkMBean.stopBundles(new long[] { LONG_NEGATIVE });
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method stopBundles throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}
		try {
			frameworkMBean.stopBundles(new long[] { LONG_BIG });
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method stopBundles throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}
		
		//test uninstallBundle method		
		try {
			frameworkMBean.uninstallBundle(LONG_NEGATIVE);
		} catch(IOException ioException) {
		}  catch(RuntimeMBeanException e) {
			//spec describes this method could throw IllegalArgumentException; let's check
			assertRootCauseIllegalArgumentException(e);
		}
		try {
			frameworkMBean.uninstallBundle(LONG_BIG);
		} catch(IOException ioException) {
		} catch(RuntimeMBeanException e) {
			//spec describes this method could throw IllegalArgumentException; let's check
			assertRootCauseIllegalArgumentException(e);
		}	
		
		//test uninstallBundles method		
		try {
			frameworkMBean.uninstallBundles(null);
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method uninstallBundles throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}
		try {
			frameworkMBean.uninstallBundles(new long[] { LONG_NEGATIVE });
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method uninstallBundles throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}
		try {
			frameworkMBean.uninstallBundles(new long[] { LONG_BIG });
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method uninstallBundles throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}	
		
		//test updateBundle method		
		try {
			frameworkMBean.updateBundle(LONG_NEGATIVE);
		} catch(IOException ioException) {
		}  catch(RuntimeMBeanException e) {
			//spec describes this method could throw IllegalArgumentException; let's check
			assertRootCauseIllegalArgumentException(e);
		}
		try {
			frameworkMBean.updateBundle(LONG_BIG);
		} catch(IOException ioException) {
		} catch(RuntimeMBeanException e) {
			//spec describes this method could throw IllegalArgumentException; let's check
			assertRootCauseIllegalArgumentException(e);
		}	
		
		//test updateBundles method		
		try {
			frameworkMBean.updateBundles(null);
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method updateBundles throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}
		try {
			frameworkMBean.updateBundles(new long[] { LONG_NEGATIVE });
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method updateBundles throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}
		try {
			frameworkMBean.updateBundles(new long[] { LONG_BIG });
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();			
			assertTrue("method updateBundles throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		
		
		//test updateBundleFromURL method		
		try {
			frameworkMBean.updateBundleFromURL(LONG_NEGATIVE, null);
		} catch(IOException ioException) {
		}  catch(RuntimeMBeanException e) {
			//spec describes this method could throw IllegalArgumentException; let's check
			assertRootCauseIllegalArgumentException(e);
		}
		try {
			frameworkMBean.updateBundleFromURL(1, STRING_URL);
		} catch(IOException ioException) {
		}  catch(RuntimeMBeanException e) {
			//spec describes this method could throw IllegalArgumentException; let's check
			assertRootCauseIllegalArgumentException(e);
		}
		
		try {
			frameworkMBean.updateBundleFromURL(LONG_BIG, STRING_EMPTY);
		} catch(IOException ioException) {
		} catch(RuntimeMBeanException e) {
			//spec describes this method could throw IllegalArgumentException; let's check
			assertRootCauseIllegalArgumentException(e);
		}	
		
		//test updateBundlesFromURL method		
		try {
			frameworkMBean.updateBundlesFromURL(null, null);
		} catch(IOException ioException) {
		}  catch(RuntimeMBeanException e) {
			//spec describes this method could throw IllegalArgumentException; let's check
			assertRootCauseIllegalArgumentException(e);
		}
		try {
			frameworkMBean.updateBundlesFromURL(new long[] { 1, LONG_NEGATIVE, LONG_BIG }, new String[] { STRING_NULL, STRING_SPECIAL_SYMBOLS });
		} catch(IOException ioException) {
		}  catch(RuntimeMBeanException e) {
			//spec describes this method could throw IllegalArgumentException; let's check
			assertRootCauseIllegalArgumentException(e);
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
	
	private final int waitTime = 20;
}
