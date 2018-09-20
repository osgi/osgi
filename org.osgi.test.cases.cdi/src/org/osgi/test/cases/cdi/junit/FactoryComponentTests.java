/**
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
 */

package org.osgi.test.cases.cdi.junit;

import static org.junit.Assert.*;

import java.util.Dictionary;
import java.util.Hashtable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.test.cases.cdi.interfaces.BeanService;
import org.osgi.util.tracker.ServiceTracker;

public class FactoryComponentTests extends AbstractTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		adminTracker = new ServiceTracker<>(bundleContext, ConfigurationAdmin.class, null);
		adminTracker.open();
		configurationAdmin = adminTracker.getService();
	}

	@After
	@Override
	public void tearDown() throws Exception {
		adminTracker.close();
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testFactoryComponent() throws Exception {
		Bundle tb7Bundle = installBundle("tb7.jar");

		ServiceTracker<BeanService, BeanService> tracker = track(
			"(&(objectClass=%s)(objectClass=*.%s))",
			BeanService.class.getName(),
			"ConfigurationBeanF");

		BeanService beanService = tracker.waitForService(timeout);

		assertNull(beanService);

		Configuration configurationA = null, configurationB = null;

		try {
			configurationA = configurationAdmin.getFactoryConfiguration("configurationBeanF", "one");

			Dictionary<String, Object> p1 = new Hashtable<>();
			p1.put("ports", new int[] {12, 4567});
			p1.put("instance", "A");
			configurationA.update(p1);

			ServiceTracker<BeanService, BeanService> trackerA = track(
				"(&(objectClass=%s)(objectClass=*.%s)(instance=A))",
				BeanService.class.getName(),
				"ConfigurationBeanF");

			BeanService beanServiceA = trackerA.waitForService(timeout);

			assertNotNull(beanServiceA);

			configurationB = configurationAdmin.getFactoryConfiguration("configurationBeanF", "two");

			p1 = new Hashtable<>();
			p1.put("ports", new int[] {45689, 1065});
			p1.put("instance", "B");
			configurationB.update(p1);

			ServiceTracker<BeanService, BeanService> trackerB = track(
				"(&(objectClass=%s)(objectClass=*.%s)(instance=B))",
				BeanService.class.getName(),
				"ConfigurationBeanF");

			BeanService beanServiceB = trackerB.waitForService(timeout);

			assertNotNull(beanServiceB);

			assertFalse(beanServiceA == beanServiceB);

			int trackingCount = trackerA.getTrackingCount();

			configurationA.delete();

			for (int i = 10; (i > 0) && (trackerA.getTrackingCount() == trackingCount); i--) {
				Thread.sleep(20);
			}

			beanServiceA = trackerA.getService();

			assertNull(beanServiceA);

			trackingCount = trackerB.getTrackingCount();

			configurationB.delete();

			for (int i = 10; (i > 0) && (trackerB.getTrackingCount() == trackingCount); i--) {
				Thread.sleep(20);
			}

			beanServiceB = trackerB.getService();

			assertNull(beanServiceB);
		}
		finally {
			if (configurationA != null) {
				try {
					configurationA.delete();
				}
				catch (Exception e) {
					// ignore
				}
			}
			if (configurationB != null) {
				try {
					configurationB.delete();
				}
				catch (Exception e) {
					// ignore
				}
			}
			tb7Bundle.uninstall();
		}
	}

	private ServiceTracker<ConfigurationAdmin, ConfigurationAdmin> adminTracker;
	private ConfigurationAdmin configurationAdmin;

}