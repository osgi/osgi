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

import static org.junit.Assert.assertEquals;

import java.util.Dictionary;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cdi.runtime.dto.ContainerDTO;
import org.osgi.test.cases.cdi.interfaces.Pojo;

public class EventsTests extends AbstractTestCase {

	@Override
	@Before
	public void setUp() throws Exception {
		cdiRuntime = runtimeTracker.waitForService(timeout);
	}

	@Override
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testContainerComponentReferenceEventHandler() throws Exception {
		Bundle tb = installBundle("tb9.jar");

		try (CloseableTracker<Pojo, Pojo> tracker = track("(objectClass=%s)", Pojo.class.getName());) {
			Pojo pojo = tracker.waitForService(timeout);

			assertEquals(0, pojo.getCount());
			assertEquals("[]", pojo.foo(null));

			ContainerDTO containerDTO = getContainerDTO(cdiRuntime, tb);

			long changeCount = containerDTO.changeCount;

			ServiceRegistration<Integer> int1 = bundleContext.registerService(Integer.class, new Integer(12), null);

			try {
				for (long i = 10; i > 0 && (getContainerDTO(cdiRuntime, tb).changeCount == changeCount); i--) {
					Thread.sleep(20);
				}

				assertEquals(1, pojo.getCount());
				assertEquals("[ADDED]", pojo.foo(null));

				changeCount = containerDTO.changeCount;

				Dictionary<String, Object> properties = int1.getReference().getProperties();
				properties.put("foo", "bar");
				int1.setProperties(properties);

				for (long i = 10; i > 0 && (getContainerDTO(cdiRuntime, tb).changeCount == changeCount); i--) {
					Thread.sleep(20);
				}

				assertEquals("[UPDATED]", pojo.foo(null));
			}
			finally {
				changeCount = containerDTO.changeCount;

				int1.unregister();

				for (long i = 10; i > 0 && (getContainerDTO(cdiRuntime, tb).changeCount == changeCount); i--) {
					Thread.sleep(20);
				}

				assertEquals(0, pojo.getCount());
				assertEquals("[]", pojo.foo(null));
			}
		}
		finally {
			tb.uninstall();
		}
	}

	@Test
	public void testSingleComponentReferenceEventHandler() throws Exception {
		Bundle tb = installBundle("tb10.jar");

		try (CloseableTracker<Pojo, Pojo> tracker = track("(objectClass=%s)", Pojo.class.getName());) {
			Pojo pojo = tracker.waitForService(timeout);

			assertEquals(0, pojo.getCount());
			assertEquals("[]", pojo.foo(null));

			ContainerDTO containerDTO = getContainerDTO(cdiRuntime, tb);

			long changeCount = containerDTO.changeCount;

			ServiceRegistration<Integer> int1 = bundleContext.registerService(Integer.class, new Integer(12), null);

			try {
				for (long i = 10; i > 0 && (getContainerDTO(cdiRuntime, tb).changeCount == changeCount); i--) {
					Thread.sleep(20);
				}

				assertEquals(1, pojo.getCount());
				assertEquals("[ADDED]", pojo.foo(null));

				changeCount = containerDTO.changeCount;

				Dictionary<String, Object> properties = int1.getReference().getProperties();
				properties.put("foo", "bar");
				int1.setProperties(properties);

				for (long i = 10; i > 0 && (getContainerDTO(cdiRuntime, tb).changeCount == changeCount); i--) {
					Thread.sleep(20);
				}

				assertEquals("[UPDATED]", pojo.foo(null));
			}
			finally {
				changeCount = containerDTO.changeCount;

				int1.unregister();

				for (long i = 10; i > 0 && (getContainerDTO(cdiRuntime, tb).changeCount == changeCount); i--) {
					Thread.sleep(20);
				}

				assertEquals(0, pojo.getCount());
				assertEquals("[]", pojo.foo(null));
			}
		}
		finally {
			tb.uninstall();
		}
	}

}
