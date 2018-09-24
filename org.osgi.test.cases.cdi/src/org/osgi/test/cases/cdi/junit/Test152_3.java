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

import javax.enterprise.context.spi.Context;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.test.cases.cdi.interfaces.BeanService;
import org.osgi.util.tracker.ServiceTracker;

public class Test152_3 extends AbstractTestCase {

	@BeforeClass
	public static void beforeClass() throws Exception {
	}

	@AfterClass
	public static void afterClass() throws Exception {
	}

	@Override
	public void setUp() throws Exception {
	}

	@After
	@Override
	public void tearDown() throws Exception {
	}

	@Test
	public void componentScopeContext() throws Exception {
		Bundle tbBundle = installBundle("tb152_3.jar");

		ServiceTracker<Object, Object> oneTracker = null, twoTracker = null;
		try {
			getBeanManager(tbBundle);

			oneTracker = track("(&(objectClass=%s)(%s=%s))", BeanService.class.getName(), Constants.SERVICE_DESCRIPTION,
					"one");
			oneTracker.open();
			Object service = oneTracker.waitForService(timeout);

			twoTracker = track("(&(objectClass=%s)(%s=%s))", BeanService.class.getName(), Constants.SERVICE_DESCRIPTION,
					"two");
			twoTracker.open();
			twoTracker.waitForService(timeout);

			assertThat(service).isNotNull();
			@SuppressWarnings("unchecked")
			BeanService<Context> bs = (BeanService<Context>)service;
			Context context = bs.get();
			assertThat(context).isNotNull();
		}
		finally {
			if (oneTracker != null) {
				oneTracker.close();
			}
			if (twoTracker != null) {
				twoTracker.close();
			}
			tbBundle.uninstall();
		}
	}

}
