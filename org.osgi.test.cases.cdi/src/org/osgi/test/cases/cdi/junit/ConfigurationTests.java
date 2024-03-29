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

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.awaitility.Awaitility;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.service.cdi.ConfigurationPolicy;
import org.osgi.service.cdi.runtime.CDIComponentRuntime;
import org.osgi.service.cdi.runtime.dto.ComponentDTO;
import org.osgi.service.cdi.runtime.dto.ContainerDTO;
import org.osgi.service.cdi.runtime.dto.template.ConfigurationTemplateDTO;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.test.cases.cdi.interfaces.BeanService;
import org.osgi.util.tracker.ServiceTracker;

public class ConfigurationTests extends AbstractTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		Awaitility.doNotCatchUncaughtExceptionsByDefault();
		Awaitility.pollInSameThread();
		runtimeTracker = new ServiceTracker<>(
			bundleContext, CDIComponentRuntime.class, null);
		runtimeTracker.open();

		cdiRuntime = runtimeTracker.waitForService(timeout);

		adminTracker = new ServiceTracker<>(bundleContext, ConfigurationAdmin.class, null);
		adminTracker.open();
		configurationAdmin = adminTracker.getService();
	}

	@After
	@Override
	public void tearDown() throws Exception {
		runtimeTracker.close();
		adminTracker.close();
	}

	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testConfiguration() throws Exception {
		Bundle tb3Bundle = installBundle("tb3.jar");

		Configuration configurationA = null, configurationB = null;

		try {
			int attempts = 50;
			ComponentDTO configurationBeanA = null;

			while (--attempts > 0) {
				ContainerDTO containerDTO = getContainerDTO(cdiRuntime,
						tb3Bundle);

				configurationBeanA = containerDTO.components.stream()
						.filter(c -> c.template.name
								.equals("configurationBeanA"))
						.findFirst()
						.orElse(null);

				if (configurationBeanA != null) {
					break;
				}
				Thread.sleep(100);
			}

			List<ConfigurationTemplateDTO> requiredConfigs = configurationBeanA.template.configurations
					.stream()
					.filter(
				tconf -> tconf.policy == ConfigurationPolicy.REQUIRED
			).collect(Collectors.toList());

			assertTrue(
				configurationBeanA.instances.get(0).configurations.stream().noneMatch(
							iconf -> requiredConfigs.stream()
									.anyMatch(rc -> rc == iconf.template)
				)
			);

			configurationA = configurationAdmin
					.getConfiguration("configurationBeanA", "?");

			Dictionary<String, Object> p1 = new Hashtable<>();
			p1.put("ports", new int[] {12, 4567});
			configurationA.update(p1);

			assertTrue(
				configurationBeanA.instances.get(0).configurations.stream().allMatch(
							iconf -> requiredConfigs.stream()
									.anyMatch(rc -> rc == iconf.template)
				)
			);

			configurationB = configurationAdmin
					.getConfiguration("configurationBeanB", "?");

			Dictionary<String, Object> p2 = new Hashtable<>();
			p2.put("color", "green");
			p2.put("ports", new int[] {80});
			configurationB.update(p2);

			try (CloseableTracker<BeanService,BeanService> stA = track(
					"(&(objectClass=%s)(bean=A))",
					BeanService.class.getName())) {
				BeanService<Callable<int[]>> beanService = stA
						.waitForService(timeout);

				assertNotNull(beanService);

				await().untilAsserted(() -> {
					assertThat(beanService.doSomething()).isEqualTo("blue");
					assertThat(beanService.get().call()).containsExactly(12,
							4567);
				});
			}

			try (CloseableTracker<BeanService,BeanService> stB = track(
					"(&(objectClass=%s)(bean=B))",
					BeanService.class.getName())) {
				final BeanService<Callable<int[]>> beanServiceB = stB
						.waitForService(timeout);
				assertNotNull(beanServiceB);

				await().untilAsserted(() -> {
					assertThat(beanServiceB.doSomething()).isEqualTo("green");
					assertThat(beanServiceB.get().call()).containsExactly(80);
				});
			}
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
			tb3Bundle.uninstall();
		}
	}

	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testOptionalConfiguration() throws Exception {
		Bundle tb5Bundle = installBundle("tb5.jar");

		Configuration configurationC = null;
		ServiceTracker<BeanService, BeanService> stC = null;

		try {
			getBeanManager(tb5Bundle);

			stC = new ServiceTracker<BeanService, BeanService>(
				bundleContext, bundleContext.createFilter(
					"(&(objectClass=org.osgi.test.cases.cdi.interfaces.BeanService)(bean=C))"), null);
			stC.open(true);

			BeanService<Callable<int[]>> beanService = stC.waitForService(timeout);

			assertNotNull(beanService);
			assertEquals("blue", beanService.doSomething());
			assertArrayEquals(new int[] {35777}, beanService.get().call());

			configurationC = configurationAdmin.getConfiguration("foo.bar", "?");

			Dictionary<String, Object> properties = new Hashtable<>();
			properties.put("ports", new int[] {12, 4567});
			configurationC.update(properties);

			stC.close();
			stC = new ServiceTracker<BeanService, BeanService>(
				bundleContext, bundleContext.createFilter(
					"(&(objectClass=org.osgi.test.cases.cdi.interfaces.BeanService)(bean=C)(ports=12))"), null);
			stC.open(true);

			beanService = stC.waitForService(timeout);

			assertNotNull(beanService);
			assertEquals("blue", beanService.doSomething());
			assertArrayEquals(new int[] {12, 4567}, beanService.get().call());

			configurationC.delete();

			stC.close();
			stC = new ServiceTracker<BeanService, BeanService>(
				bundleContext, bundleContext.createFilter(
					"(&(objectClass=org.osgi.test.cases.cdi.interfaces.BeanService)(bean=C)(!(ports=*)))"), null);
			stC.open(true);
			beanService = stC.waitForService(timeout);

			assertNotNull(beanService);
			assertEquals("blue", beanService.doSomething());
			assertArrayEquals(new int[] {35777}, beanService.get().call());
		}
		finally {
			if (configurationC != null) {
				try {
					configurationC.delete();
				}
				catch (Exception e) {
					// ignore
				}
			}
			if (stC != null) {
				stC.close();
			}
			tb5Bundle.uninstall();
		}
	}

	private ServiceTracker<ConfigurationAdmin, ConfigurationAdmin> adminTracker;
	private ConfigurationAdmin configurationAdmin;

}
