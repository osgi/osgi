/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2008). All Rights Reserved.
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

package org.osgi.test.cases.jmx.junit;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.management.MBeanServer;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.RuntimeMBeanException;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.TabularData;

import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public abstract class MBeanGeneralTestCase extends DefaultTestBundleControl {

	//private BundleContext context;
	private MBeanServer mBeanServer = null;
	private ServiceReference reference = null;
	private ServiceRegistration registration;

	protected final static String STRING_NULL = null;
	protected final static String STRING_EMPTY = "";
	protected final static String STRING_SPECIAL_SYMBOLS = "+})-=:;\"\\/?{(";
	protected final static String STRING_URL = "file:.";	
	protected final static long LONG_NEGATIVE = -2;
	protected final static long LONG_BIG = 1000000;
	protected final static int INT_NEGATIVE = -2;
	protected final static int INT_BIG = 1000000;
	
//	public void setBundleContext(BundleContext context) {
//		this.context = context;
//	}
	
	public final MBeanServer getMBeanServer() {
		return mBeanServer;
	}

	protected void setUp() throws Exception {
		super.setUp();
		MBeanServer hack = ManagementFactory.getPlatformMBeanServer();

		registration = getContext().registerService(MBeanServer.class
				.getCanonicalName(), hack, null);
			
		String key = MBeanServer.class.getCanonicalName();
		System.out.println(key);

		reference = getContext().getServiceReference(key);
		assertNotNull(reference);
		mBeanServer = (MBeanServer) getContext().getService(reference);
		assertNotNull(mBeanServer);

	}
	
	/*
	 * The current registering / unregistering of the mbeans happens in 
	 * a dedicated thread. So it's possible that even after setUp / tearDown
	 * are called the effects are not immediate after the return of the thread
	 * In order to avoid race conditions, we would implement artifical loop
	 * sleeping and checking for the effect of regierting \ unregister of the 
	 * MBeans
	 */
	protected  void waitForRegistering(ObjectName objectName) throws InterruptedException {
		
		//fix that ugly code
		for (boolean registered = false;!registered; registered = mBeanServer.isRegistered(objectName)) {
			Thread.sleep(100);
		}
	}
	
	
	
	protected void waitForUnRegistering(ObjectName objectName) throws InterruptedException {
		
		//fix that ugly code
		for (boolean registered = true;registered; registered = mBeanServer.isRegistered(objectName)) {
			Thread.sleep(100);
		}
	}	
	
	
	


	protected void tearDown() throws Exception {
		
		getContext().ungetService(reference);
		registration.unregister();
	}

	// public void testManagedBundle2() throws Exception {}

	@SuppressWarnings("unchecked")
	protected <T> T getMBeanFromServer(String objectName, Class<T> type) {
		ObjectName objectName0;
		try {
			objectName0 = new ObjectName(objectName);
		} catch (MalformedObjectNameException e) {
			throw new RuntimeException(e);
		} catch (NullPointerException e) {
			throw new RuntimeException(e);
		}
		assertNotNull(mBeanServer);
		assertNotNull(objectName0);
		T mbean = (T) MBeanServerInvocationHandler.newProxyInstance(mBeanServer, objectName0,
				type, false);
		return mbean;
	}
	
	protected ObjectName createObjectName(String name) {
		ObjectName objectName;
		try {
			objectName = new ObjectName(name);
		} catch (MalformedObjectNameException e) {
			throw new RuntimeException(e);
		} catch (NullPointerException e) {
			throw new RuntimeException(e);
		}
		return objectName;
	}
	
	protected void assertCompositeDataKeys(CompositeData cd, String type, String[] keys) {
		for (int i = 0; i < keys.length; i++) {
			assertTrue("composite data from type " + type + " doesn't contain key " + keys[i], cd.containsKey(keys[i]));
		}
	}	
	
	protected void assertTabularDataStructure(TabularData td, String type, String key, String[] compositeDataKeys) {
		List<String> indexNames = td.getTabularType().getIndexNames();
		assertNotNull(indexNames);
		if (key != null) {
			assertTrue("tabular data " + type + " has wrong key set", indexNames.size() == 1);
			assertTrue("tabular data " + type + " doesn't contain key " + key, indexNames.iterator().next().equals(key));
		}
		CompositeType ct = td.getTabularType().getRowType();
		for (int i = 0; i < compositeDataKeys.length; i++) {
			assertTrue("tabular data row type " + type + " doesn't contain key " + compositeDataKeys[i], ct.containsKey(compositeDataKeys[i]));
		}
	}	
	
	protected void assertTabularDataStructure(TabularData td, String type, String[] keys, String[] compositeDataKeys) {
		List<String> indexNames = td.getTabularType().getIndexNames();
		assertNotNull(indexNames);
		if (keys != null) {
			HashSet<String> keySet = new HashSet<String>();
			for (int i = 0; i < keys.length; i++) {
				keySet.add(keys[i]);
			}			
			assertTrue("tabular data " + type + " has wrong key set size of " + indexNames.size(), indexNames.size() == keys.length);
			Iterator<String> iter = indexNames.iterator();
			while (iter.hasNext()) {
				String indexName = iter.next();
				assertTrue("tabular data " + type + " contains wrong key " + indexName, keySet.contains(indexName));				
			}
		}
		CompositeType ct = td.getTabularType().getRowType();
		for (int i = 0; i < compositeDataKeys.length; i++) {
			assertTrue("tabular data row type " + type + " doesn't contain key " + compositeDataKeys[i], ct.containsKey(compositeDataKeys[i]));
		}
	}
	
	protected void assertIOException(Exception e) {
		assertTrue("", e instanceof IOException);
	}

	protected void assertIllegalArgumentException(RuntimeException e) {
		assertTrue("exception " + e + " is not IllegalArgumentException", e instanceof IllegalArgumentException);
	}
	
	protected void assertRootCauseIllegalArgumentException(RuntimeMBeanException mbeanException) {
		RuntimeException re = mbeanException.getTargetException();
		assertIllegalArgumentException(re);
	}
	
}
