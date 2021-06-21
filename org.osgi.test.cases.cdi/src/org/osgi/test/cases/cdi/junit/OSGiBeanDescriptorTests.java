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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.TypeLiteral;

import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.test.cases.cdi.interfaces.BeanService;
import org.osgi.test.cases.cdi.interfaces.Pojo;
import org.osgi.util.tracker.ServiceTracker;

public class OSGiBeanDescriptorTests extends AbstractTestCase {

	@Test
	public void testServices() throws Exception {
		Bundle tb2Bundle = installBundle("tb2.jar");

		ServiceTracker<Pojo, Pojo> st = new ServiceTracker<Pojo, Pojo>(
			bundleContext, Pojo.class, null);
		st.open(true);

		try {
			Pojo pojo = st.waitForService(timeout);
			assertNotNull(pojo);
		}
		finally {
			tb2Bundle.uninstall();
			st.close();
		}
	}

	@SuppressWarnings("serial")
	@Test
	public void testReferences() throws Exception {
		Bundle tb1Bundle = installBundle("tb1.jar");
		Bundle tb2Bundle = installBundle("tb2.jar");

		try {
			timeout = 2000;
			BeanManager beanManager = getBeanManager(tb1Bundle);
			assertNotNull(beanManager);
			Set<Bean<?>> beans = beanManager.getBeans("beanimpl");
			Bean<?> bean = beanManager.resolve(beans);
			CreationalContext<?> ctx = beanManager.createCreationalContext(bean);
			BeanService<?> beanService = (BeanService<?>)beanManager.getReference(
				bean, new TypeLiteral<BeanService<?>>() {}.getType(), ctx);

			assertNotNull(beanService);
			assertEquals("POJO-IMPLBEAN-IMPL", beanService.doSomething());
		}
		finally {
			tb2Bundle.uninstall();
			tb1Bundle.uninstall();
		}
	}

}
