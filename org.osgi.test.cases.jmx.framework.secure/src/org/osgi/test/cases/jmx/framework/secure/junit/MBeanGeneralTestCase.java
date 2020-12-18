/*
 * Copyright (c) OSGi Alliance (2008, 2020). All Rights Reserved.
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

package org.osgi.test.cases.jmx.framework.secure.junit;

import java.lang.management.ManagementFactory;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.TabularData;

import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public abstract class MBeanGeneralTestCase extends DefaultTestBundleControl {

	//private BundleContext context;
	private MBeanServer mBeanServer = null;
	private ServiceReference< ? >		reference				= null;
	private ServiceRegistration< ? >	registration;

	protected final static String STRING_NULL = null;
	protected final static String STRING_EMPTY = "";
	protected final static String STRING_SPECIAL_SYMBOLS = "+})-=:;\"\\/?{(";
	protected final static String STRING_URL = "file:.";
	protected final static long LONG_NEGATIVE = -2;
	protected final static long LONG_BIG = 1000000;
	protected final static int INT_NEGATIVE = -2;
	protected final static int INT_BIG = 1000000;

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

		// The following delay makes the test case run consistently.
		// I expect that the MBean server was not happy being deleted
		// and created all the time. This gives it time to settle.
		// pkriens.
		Thread.sleep(2000);
	}

	/*
	 * The current registering / unregistering of the mbeans happens in
	 * a dedicated thread. So it's possible that even after setUp / tearDown
	 * are called the effects are not immediate after the return of the thread
	 * In order to avoid race conditions, this loop checks for the existence of
	 * the MBean with the provided prefix and returns as soon as its found.
	 */
	protected ObjectName waitForRegistering(ObjectName objectNamePrefix) throws Exception {
	    return waitForRegistering(mBeanServer, objectNamePrefix);
	}

    protected ObjectName waitForRegistering(MBeanServer server, ObjectName objectNamePrefix) throws Exception {
	    ObjectName objectPattern = new ObjectName(objectNamePrefix.toString() + ",*");

	    int i = 15; // timeout after 15 seconds
		while (i > 0) {
		    Set<ObjectName> result = server.queryNames(objectPattern, null);
		    if (result.size() == 1)
		        return result.iterator().next(); // found it
		    else if (result.size() > 1)
		        throw new IllegalStateException("More than one matching MBean was found. " +
		        		"This is an incorrect precondition for the test. Prefix: " + objectNamePrefix);

		    Thread.sleep(1000);
		    i--;
		}
		throw new IllegalStateException("Querying for MBean timed out. Prefix:" + objectNamePrefix);
	}

	protected void waitForUnRegistering(ObjectName objectNamePrefix) throws Exception {
        ObjectName objectPattern = new ObjectName(objectNamePrefix.toString() + ",*");

        int i = 15; // timeout after 15 seconds
        while (i > 0) {
            Set<ObjectName> result = mBeanServer.queryNames(objectPattern, null);
            if (result.size() == 0)
                return; // gone!

            Thread.sleep(1000);
            i--;
        }
        throw new IllegalStateException("Waiting for MBean unregistrationtimed out. Prefix:" + objectNamePrefix);
	}


	protected void tearDown() throws Exception {

		getContext().ungetService(reference);
		registration.unregister();
	}

	protected <T> T getMBeanFromServer(String objectName, Class<T> type) {
		ObjectName objectName0;
		try {
			objectName0 = new ObjectName(objectName + ",*");
		} catch (MalformedObjectNameException e) {
			throw new RuntimeException(e);
		} catch (NullPointerException e) {
			throw new RuntimeException(e);
		}
		assertNotNull(mBeanServer);
		assertNotNull(objectName0);

		Set<ObjectName> names = mBeanServer.queryNames(objectName0, null);
		if (names.size() == 0) {
		    throw new IllegalStateException("MBean not found. ObjectName: " + objectName0);
		}

		T mbean = MBeanServerInvocationHandler.newProxyInstance(mBeanServer, names.iterator().next(),
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

    protected void assertCompositeDataKeys(CompositeData cd, String type, Collection<?> keys) {
        String [] arr = new String[keys.size()];
        int i=0;
        for (Object key : keys) {
            arr[i++] = (String) key;
        }
        assertCompositeDataKeys(cd, type, arr);
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

    protected void assertTabularDataStructure(TabularData td, String type, String[] keys, Collection<?> compositeDataKeys) {
        String [] arr = new String[compositeDataKeys.size()];
        int i=0;
        for (Object key : compositeDataKeys) {
            arr[i++] = (String) key;
        }
        assertTabularDataStructure(td, type, keys, arr);
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
}
