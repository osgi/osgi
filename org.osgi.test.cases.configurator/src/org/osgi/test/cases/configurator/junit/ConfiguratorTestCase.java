/*
 * Copyright (c) OSGi Alliance (2017). All Rights Reserved.
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

package org.osgi.test.cases.configurator.junit;

import java.io.IOException;
import java.util.Dictionary;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationEvent;
import org.osgi.service.cm.ConfigurationListener;
import org.osgi.test.support.OSGiTestCase;
import org.osgi.util.promise.Deferred;
import org.osgi.util.tracker.ServiceTracker;

public class ConfiguratorTestCase extends OSGiTestCase {
	private ConfigurationAdmin configAdmin;
	private ServiceTracker<ConfigurationAdmin,ConfigurationAdmin> configAdminTracker;

	public void setUp() throws Exception {
		configAdminTracker = new ServiceTracker<>(
				getContext(), ConfigurationAdmin.class, null);
		configAdminTracker.open();
		configAdmin = configAdminTracker.waitForService(5000);
	}

	public void tearDown() {
		configAdminTracker.close();
	}

	public void testBasicConfigurationLifecycle() throws Exception {
		String pid = "org.osgi.test.pid1";
		Deferred<Configuration> deleted = new Deferred<>();
		Deferred<Configuration> updated = new Deferred<>();

		ServiceRegistration<ConfigurationListener> reg = registerConfigListener(
				pid, updated, deleted);
		try {
			assertNull("Precondition, should not yet have the test config",
					readConfig(pid));

			Bundle tb1 = install("tb1.jar");
			assertFalse(updated.getPromise().isDone());
			tb1.start();

			Configuration cfg = updated.getPromise().getValue();
			Dictionary<String,Object> props = cfg.getProperties();
			assertEquals("bar", props.get("foo"));

			assertFalse(deleted.getPromise().isDone());
			tb1.uninstall();
			assertEquals(pid, deleted.getPromise().getValue().getPid());
		} finally {
			reg.unregister();
		}
	}

	public void testImplicitDatatypes() throws Exception {
		Deferred<Configuration> updated = new Deferred<>();

		ServiceRegistration<ConfigurationListener> reg = registerConfigListener(
				"org.osgi.test.pid2", updated, null);
		try {
			Bundle tb2 = install("tb2.jar");
			assertFalse("Precondition", updated.getPromise().isDone());
			tb2.start();

			Configuration cfg = updated.getPromise().getValue();
			Dictionary<String,Object> props = cfg.getProperties();
			assertEquals(6, props.size());
			assertEquals(true, props.get("bval"));
			assertEquals(1234L, props.get("ival"));
			assertEquals(-2.718, props.get("dval"));
			assertEquals("bar", props.get("sval"));
			assertEquals(
					"{\"a\":1,\"b\":\"2\",\"c\":{\"d\":true,\"e\":[999,1000]}}",
					((String) props.get("oval")).replaceAll("\\s", ""));

			assertEquals("org.osgi.test.pid2",
					props.get(Constants.SERVICE_PID));

			tb2.uninstall();
		} finally {
			reg.unregister();
		}
	}

	public void testExplicitDatatypes() throws Exception {
		Deferred<Configuration> updated = new Deferred<>();

		ServiceRegistration<ConfigurationListener> reg = registerConfigListener(
				"org.osgi.test.pid3a", updated, null);
		try {
			Bundle tb2 = install("tb2.jar");
			assertFalse("Precondition", updated.getPromise().isDone());
			tb2.start();

			Configuration cfg = updated.getPromise().getValue();
			Dictionary<String,Object> props = cfg.getProperties();
			assertEquals(Integer.valueOf(1234), props.get("Ival"));
			assertEquals(Boolean.TRUE, props.get("Bval"));
			assertEquals('q', props.get("Cval"));
			assertEquals(Long.MAX_VALUE, props.get("Lval"));
			assertEquals("false", props.get("Sval"));
			assertEquals(-12.34f, props.get("Fval"));
			assertEquals(3.141592653589793, props.get("Dval"));
			assertEquals(Byte.valueOf("-128"), props.get("ByteVal"));
			assertEquals(Short.valueOf("16384"), props.get("ShortVal"));

			tb2.uninstall();
		} finally {
			reg.unregister();
		}
	}

	public void testArraysBasic() throws Exception {
		Deferred<Configuration> updated = new Deferred<>();

		ServiceRegistration<ConfigurationListener> reg = registerConfigListener(
				"org.osgi.test.pid4a", updated, null);
		try {
			Bundle tb2 = install("tb2.jar");
			assertFalse("Precondition", updated.getPromise().isDone());
			tb2.start();

			Configuration cfg = updated.getPromise().getValue();
			Dictionary<String,Object> props = cfg.getProperties();
			assertArrayEquals(new Boolean[] {
					true, true, false, true
			}, props.get("ba"));
			assertArrayEquals(new Long[] {
					Long.MAX_VALUE, Long.MIN_VALUE
			}, props.get("la"));
			assertArrayEquals(new Double[] {
					-999.999
			}, props.get("da"));
			assertArrayEquals(new String[] {
					"one", "two", "three"
			}, props.get("sa"));
			// TODO assertArraysEquals(new String[] {"complex1", "complex2"},
			// props.get("oa"));
			assertArrayEquals(new String[] {}, props.get("xa"));

			tb2.uninstall();
		} finally {
			reg.unregister();
		}
	}

	public void testArraysSpecificBoxed() throws Exception {
		Deferred<Configuration> updated = new Deferred<>();

		ServiceRegistration<ConfigurationListener> reg = registerConfigListener(
				"org.osgi.test.pid4b", updated, null);
		try {
			Bundle tb2 = install("tb2.jar");
			assertFalse("Precondition", updated.getPromise().isDone());
			tb2.start();

			Configuration cfg = updated.getPromise().getValue();
			Dictionary<String,Object> props = cfg.getProperties();
			assertArrayEquals(new Boolean[] {
					true, true, false, true
			}, props.get("ba"));
			assertArrayEquals(new Character[] {
					'h', 'e', 'l', 'l', 'o'
			}, props.get("ca"));
			assertArrayEquals(new Double[] {
					-999.999
			}, props.get("da"));
			assertArrayEquals(new Float[] {
					-0.1f, 0f, 0.1f, 0f, -0.1f
			}, props.get("fa"));
			assertArrayEquals(new Integer[] {
					-1, -2, -3
			}, props.get("ia"));
			assertArrayEquals(new Long[] {
					Long.MAX_VALUE, Long.MIN_VALUE
			}, props.get("la"));
			assertArrayEquals(new String[] {
					"one", "two", "three"
			}, props.get("sa"));
			assertArrayEquals(new Byte[] {
					99
			}, props.get("com.acme.ByteVal"));
			assertArrayEquals(new Short[] {
					32767, 32767
			}, props.get("com.acme.ShortVal"));
			assertArrayEquals(new Integer[] {}, props.get("xa"));

			tb2.uninstall();
		} finally {
			reg.unregister();
		}
	}

	public void testArraysSpecificPrimitive() throws Exception {
		Deferred<Configuration> updated = new Deferred<>();

		ServiceRegistration<ConfigurationListener> reg = registerConfigListener(
				"org.osgi.test.pid4c", updated, null);
		try {
			Bundle tb2 = install("tb2.jar");
			assertFalse("Precondition", updated.getPromise().isDone());
			tb2.start();

			Configuration cfg = updated.getPromise().getValue();
			Dictionary<String,Object> props = cfg.getProperties();
			assertArrayEquals(new boolean[] {
					true, true, false, true
			}, props.get("ba"));
			assertArrayEquals(new char[] {
					'h', 'e', 'l', 'l', 'o'
			}, props.get("ca"));
			assertArrayEquals(new double[] {
					-999.999
			}, props.get("da"));
			assertArrayEquals(new float[] {
					-0.1f, 0f, 0.1f, 0f, -0.1f
			}, props.get("fa"));
			assertArrayEquals(new int[] {
					-1, -2, -3
			}, props.get("ia"));
			assertArrayEquals(new long[] {
					Long.MAX_VALUE, Long.MIN_VALUE
			}, props.get("la"));
			assertArrayEquals(new byte[] {
					99
			}, props.get("com.acme.ByteVal"));
			assertArrayEquals(new short[] {
					32767, 32767
			}, props.get("com.acme.ShortVal"));
			assertArrayEquals(new boolean[] {}, props.get("xa"));

			tb2.uninstall();
		} finally {
			reg.unregister();
		}
	}

	private void assertArrayEquals(Object[] expected, Object actual) {
		assertEquals(expected.getClass(), actual.getClass());
		Object[] ao = (Object[]) actual;
		assertEquals(expected.length, ao.length);

		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], ao[i]);
		}
	}

	private void assertArrayEquals(boolean[] expected, Object actual) {
		assertTrue(actual instanceof boolean[]);
		boolean[] a = (boolean[]) actual;
		assertEquals(expected.length, a.length);

		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], a[i]);
		}
	}

	private void assertArrayEquals(byte[] expected, Object actual) {
		assertTrue(actual instanceof byte[]);
		byte[] a = (byte[]) actual;
		assertEquals(expected.length, a.length);

		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], a[i]);
		}
	}

	private void assertArrayEquals(char[] expected, Object actual) {
		assertTrue(actual instanceof char[]);
		char[] a = (char[]) actual;
		assertEquals(expected.length, a.length);

		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], a[i]);
		}
	}

	private void assertArrayEquals(double[] expected, Object actual) {
		assertTrue(actual instanceof double[]);
		double[] a = (double[]) actual;
		assertEquals(expected.length, a.length);

		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], a[i]);
		}
	}

	private void assertArrayEquals(float[] expected, Object actual) {
		assertTrue(actual instanceof float[]);
		float[] a = (float[]) actual;
		assertEquals(expected.length, a.length);

		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], a[i]);
		}
	}

	private void assertArrayEquals(int[] expected, Object actual) {
		assertTrue(actual instanceof int[]);
		int[] a = (int[]) actual;
		assertEquals(expected.length, a.length);

		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], a[i]);
		}
	}

	private void assertArrayEquals(long[] expected, Object actual) {
		assertTrue(actual instanceof long[]);
		long[] a = (long[]) actual;
		assertEquals(expected.length, a.length);

		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], a[i]);
		}
	}

	private void assertArrayEquals(short[] expected, Object actual) {
		assertTrue(actual instanceof short[]);
		short[] a = (short[]) actual;
		assertEquals(expected.length, a.length);

		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], a[i]);
		}
	}


	private ServiceRegistration<ConfigurationListener> registerConfigListener(
			String pid,
			Deferred<Configuration> updated, Deferred<Configuration> deleted) {
		if (updated == null)
			updated = new Deferred<>();
		if (deleted == null)
			deleted = new Deferred<>();

		Deferred<Configuration> fupdated = updated;
		Deferred<Configuration> fdeleted = deleted;
		ConfigurationListener cl = new ConfigurationListener() {
			@Override
			public void configurationEvent(ConfigurationEvent event) {
				if (!pid.equals(event.getPid()))
					return;

				try {
					ConfigurationAdmin cm = getContext()
							.getService(event.getReference());
					switch (event.getType()) {
						case ConfigurationEvent.CM_UPDATED :
							fupdated.resolve(
									cm.getConfiguration(event.getPid()));
							return;
						case ConfigurationEvent.CM_DELETED :
							fdeleted.resolve(
									cm.getConfiguration(event.getPid()));
							return;
					}
				} catch (IOException e) {
					fail("Unexpected test result", e);
				}
			}
		};
		return getContext().registerService(ConfigurationListener.class, cl,
				null);
	}

	private Configuration readConfig(String pid)
			throws IOException, InvalidSyntaxException {
		Configuration[] configs = configAdmin.listConfigurations(
						"(" + Constants.SERVICE_PID + "=" + pid + ")");
		if (configs == null)
			return null;
		return configs[0];
	}
}


