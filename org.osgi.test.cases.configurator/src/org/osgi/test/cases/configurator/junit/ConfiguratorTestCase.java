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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Iterator;

import org.junit.Assert;
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
			tb2.start();

			Configuration cfg = updated.getPromise().getValue();
			Dictionary<String,Object> props = cfg.getProperties();
			Assert.assertArrayEquals(new Boolean[] {
					true, true, false, true
			}, (Object[]) props.get("ba"));
			Assert.assertArrayEquals(new Long[] {
					Long.MAX_VALUE, Long.MIN_VALUE
			}, (Object[]) props.get("la"));
			Assert.assertArrayEquals(new Double[] {
					-999.999
			}, (Object[]) props.get("da"));
			Assert.assertArrayEquals(new String[] {
					"one", "two", "three"
			}, (Object[]) props.get("sa"));
			Assert.assertArrayEquals(new String[] {
					"{\"foo\":{\"yo\":\"ya\"}}", "{\"bar\":{\"to\":9182}}"
			}, (Object[]) props.get("oa"));
			Assert.assertArrayEquals(new String[] {},
					(Object[]) props.get("xa"));

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
			tb2.start();

			Configuration cfg = updated.getPromise().getValue();
			Dictionary<String,Object> props = cfg.getProperties();
			Assert.assertArrayEquals(new Boolean[] {
					true, true, false, true
			}, (Object[]) props.get("ba"));
			Assert.assertArrayEquals(new Character[] {
					'h', 'e', 'l', 'l', 'o'
			}, (Object[]) props.get("ca"));
			Assert.assertArrayEquals(new Double[] {
					-999.999
			}, (Object[]) props.get("da"));
			Assert.assertArrayEquals(new Float[] {
					-0.1f, 0f, 0.1f, 0f, -0.1f
			}, (Object[]) props.get("fa"));
			Assert.assertArrayEquals(new Integer[] {
					-1, -2, -3
			}, (Object[]) props.get("ia"));
			Assert.assertArrayEquals(new Long[] {
					Long.MAX_VALUE, Long.MIN_VALUE
			}, (Object[]) props.get("la"));
			Assert.assertArrayEquals(new String[] {
					"one", "two", "three"
			}, (Object[]) props.get("sa"));
			Assert.assertArrayEquals(new Byte[] {
					99
			}, (Object[]) props.get("com.acme.ByteVal"));
			Assert.assertArrayEquals(new Short[] {
					32767, 32767
			}, (Object[]) props.get("com.acme.ShortVal"));
			Assert.assertArrayEquals(new Integer[] {},
					(Object[]) props.get("xa"));

			tb2.uninstall();
		} finally {
			reg.unregister();
		}
	}

	public void testCollectionsImplicit() throws Exception {
		Deferred<Configuration> updated = new Deferred<>();

		ServiceRegistration<ConfigurationListener> reg = registerConfigListener(
				"org.osgi.test.pid4d", updated, null);
		try {
			Bundle tb2 = install("tb2.jar");
			tb2.start();

			Configuration cfg = updated.getPromise().getValue();
			Dictionary<String,Object> props = cfg.getProperties();
			assertCollectionEquals(Arrays.asList(true, true, false, true),
					props.get("bcg"));
			assertCollectionEquals(Arrays.asList(-0.1, 0.0, 0.1, 0.0, -0.1),
					props.get("dcg"));
			assertEquals(0, ((Collection< ? >) props.get("ecg")).size());
			assertCollectionEquals(
					Arrays.asList(Long.MAX_VALUE, Long.MIN_VALUE),
					props.get("lcg"));
			assertCollectionEquals(Arrays.asList("one", "two", "three"),
					props.get("scg"));

			tb2.uninstall();
		} finally {
			reg.unregister();
		}
	}

	public void testCollectionsSpecific() throws Exception {
		Deferred<Configuration> updated = new Deferred<>();

		ServiceRegistration<ConfigurationListener> reg = registerConfigListener(
				"org.osgi.test.pid4e", updated, null);
		try {
			Bundle tb2 = install("tb2.jar");
			tb2.start();

			Configuration cfg = updated.getPromise().getValue();
			Dictionary<String,Object> props = cfg.getProperties();
			assertCollectionEquals(Arrays.asList(true, true, false, true),
					props.get("bc"));
			assertCollectionEquals(Arrays.asList('h', 'e', 'l', 'l', 'o'),
					props.get("cc"));
			assertCollectionEquals(Arrays.asList(-999.999), props.get("dc"));
			assertCollectionEquals(Arrays.asList(-0.1f, 0f, 0.1f, 0f, -0.1f),
					props.get("fc"));
			assertCollectionEquals(Arrays.asList(-1, -2, -3), props.get("ic"));
			assertCollectionEquals(
					Arrays.asList(Long.MAX_VALUE, Long.MIN_VALUE),
					props.get("lc"));
			assertCollectionEquals(Arrays.asList("one", "two", "three"),
					props.get("sc"));
			assertCollectionEquals(Arrays.asList((byte) 99),
					props.get("com.acme.ByteVal"));
			assertCollectionEquals(Arrays.asList((short) 32766, (short) 32766),
					props.get("com.acme.ShortVal"));
			assertCollectionEquals(Collections.emptyList(), props.get("ec"));

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
			tb2.start();

			Configuration cfg = updated.getPromise().getValue();
			Dictionary<String,Object> props = cfg.getProperties();
			assertArrayEquals(new boolean[] {
					true, true, false, true
			}, props.get("ba"));
			Assert.assertArrayEquals(new char[] {
					'h', 'e', 'l', 'l', 'o'
			}, (char[]) props.get("ca"));
			Assert.assertArrayEquals(new double[] {
					-999.999
			}, (double[]) props.get("da"), 0.0000001);
			Assert.assertArrayEquals(new float[] {
					-0.1f, 0f, 0.1f, 0f, -0.1f
			}, (float[]) props.get("fa"), 0.0001f);
			Assert.assertArrayEquals(new int[] {
					-1, -2, -3
			}, (int[]) props.get("ia"));
			Assert.assertArrayEquals(new long[] {
					Long.MAX_VALUE, Long.MIN_VALUE
			}, (long[]) props.get("la"));
			Assert.assertArrayEquals(new byte[] {
					99
			}, (byte[]) props.get("com.acme.ByteVal"));
			Assert.assertArrayEquals(new short[] {
					32767, 32767
			}, (short[]) props.get("com.acme.ShortVal"));
			assertArrayEquals(new boolean[] {}, props.get("xa"));

			tb2.uninstall();
		} finally {
			reg.unregister();
		}
	}

	public void testBinaries() throws Exception {
		Deferred<Configuration> updated = new Deferred<>();

		ServiceRegistration<ConfigurationListener> reg = registerConfigListener(
				"binarytest", updated, null);
		try {
			Bundle tb2 = install("tb2.jar");
			tb2.start();

			Configuration cfg = updated.getPromise().getValue();
			Dictionary<String,Object> props = cfg.getProperties();
			Path path = Paths.get(props.get("binaryval").toString());
			assertTrue(path.toFile().isFile());
			assertEquals("Binary file1", new String(Files.readAllBytes(path)));

			String[] paths = (String[]) props.get("binaryarr");
			assertEquals(2, paths.length);
			Path path2 = Paths.get(paths[0]);
			assertEquals("Binary file2", new String(Files.readAllBytes(path2)));

			Path path3 = Paths.get(paths[1]);
			assertEquals("Binary file3", new String(Files.readAllBytes(path3)));

			tb2.uninstall();
		} finally {
			reg.unregister();
		}
	}

	public void testFactoryConfigurations() throws Exception {
		Deferred<Configuration> updated1 = new Deferred<>();
		Deferred<Configuration> updated2 = new Deferred<>();

		ServiceRegistration<ConfigurationListener> reg1 = registerConfigListener(
				"org.acme.factory~instance1", updated1, null);

		ServiceRegistration<ConfigurationListener> reg2 = registerConfigListener(
				"org.acme.factory~instance2", updated2, null);
		try {
			Bundle tb3 = install("tb3.jar");
			tb3.start();

			Configuration cfg1 = updated1.getPromise().getValue();
			Dictionary<String,Object> props1 = cfg1.getProperties();
			assertEquals("org.acme.factory~instance1",
					props1.get(Constants.SERVICE_PID));
			assertEquals("org.acme.factory",
					props1.get(ConfigurationAdmin.SERVICE_FACTORYPID));
			assertEquals("someval", props1.get("somekey"));

			Configuration cfg2 = updated2.getPromise().getValue();
			Dictionary<String,Object> props2 = cfg2.getProperties();
			assertEquals("org.acme.factory~instance2",
					props2.get(Constants.SERVICE_PID));
			assertEquals("org.acme.factory",
					props2.get(ConfigurationAdmin.SERVICE_FACTORYPID));
			assertEquals("someval2", props2.get("somekey"));

			tb3.uninstall();
		} finally {
			reg1.unregister();
			reg2.unregister();
		}
	}

	public void testRanking() throws Exception {
		Deferred<Configuration> updated1 = new Deferred<>();
		Deferred<Configuration> updated2 = new Deferred<>();

		ServiceRegistration<ConfigurationListener> reg1 = registerConfigListener(
				"pid1", updated1, null);

		ServiceRegistration<ConfigurationListener> reg2 = registerConfigListener(
				"pid2", updated2, null);
		try {
			Bundle tb4 = install("tb4.jar");
			tb4.start();

			Configuration cfg1 = updated1.getPromise().getValue();
			Dictionary<String,Object> props1 = cfg1.getProperties();
			assertEquals("winning", props1.get("akey"));

			Configuration cfg2 = updated2.getPromise().getValue();
			Dictionary<String,Object> props2 = cfg2.getProperties();
			assertEquals("winning", props2.get("akey"));

			tb4.uninstall();
		} finally {
			reg1.unregister();
			reg2.unregister();
		}
	}

	// For some reason Assert doesn't provide this one...
	private void assertArrayEquals(boolean[] expected, Object actual) {
		assertTrue(actual instanceof boolean[]);
		boolean[] a = (boolean[]) actual;
		assertEquals(expected.length, a.length);
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], a[i]);
		}
	}

	private void assertCollectionEquals(Collection< ? > expected,
			Object actual) {
		assertTrue(actual instanceof Collection);
		Collection< ? > ac = (Collection< ? >) actual;
		assertEquals(expected.size(), ac.size());

		for (Iterator< ? > ei = expected.iterator(), ea = ac.iterator(); ei
				.hasNext();) {
			assertEquals(ei.next(), ea.next());
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


