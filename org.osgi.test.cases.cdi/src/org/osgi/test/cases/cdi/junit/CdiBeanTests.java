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
import static org.osgi.framework.Constants.SERVICE_RANKING;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;

import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.cases.cdi.interfaces.BeanService;
import org.osgi.test.cases.cdi.interfaces.BundleContextBeanQualifier;
import org.osgi.test.cases.cdi.interfaces.BundleScoped;
import org.osgi.test.cases.cdi.interfaces.FieldInjectedReference;
import org.osgi.test.cases.cdi.interfaces.PrototypeScoped;
import org.osgi.test.cases.cdi.interfaces.SingletonScoped;
import org.osgi.test.cases.cdi.junit.services.ServiceBundleScope;
import org.osgi.test.cases.cdi.junit.services.ServicePrototypeScope;
import org.osgi.test.cases.cdi.junit.services.ServiceSingletonFour;
import org.osgi.test.cases.cdi.junit.services.ServiceSingletonOne;
import org.osgi.test.cases.cdi.junit.services.ServiceSingletonThree;
import org.osgi.test.cases.cdi.junit.services.ServiceSingletonTwo;
import org.osgi.util.tracker.ServiceTracker;

@SuppressWarnings("rawtypes")
public class CdiBeanTests extends AbstractTestCase {

	@Test
    public void testConstructorInjectedService() throws Exception {
		ServiceTracker<BeanService, BeanService> tracker = track(
			"(&(objectClass=%s)(objectClass=*.%s))",
			BeanService.class.getName(),
			"ConstructorInjectedService");

		BeanService beanService = tracker.waitForService(timeout);

		assertNotNull(beanService);
		assertEquals("PREFIXCONSTRUCTOR", beanService.doSomething());
	}

	@Test
	public void testFieldInjectedReference_BundleScoped() throws Exception {
		ServiceTracker<FieldInjectedReference, FieldInjectedReference> tracker = track(
			"(&(objectClass=%s)(objectClass=*.%s))",
			FieldInjectedReference.class.getName(),
			"FieldInjectedBundleScopedImpl");

        ServiceRegistration<BundleScoped> bundleScoped = register(
                BundleScoped.class,
                new ServiceBundleScope(),
                "fee.fi", "fee",
                "fo.fum", 23,
                "complex.enough.key", "fum",
                "key", "value",
                "simple.annotation", "blah");
        try {
            FieldInjectedReference fieldInjectedReference = tracker.waitForService(timeout);

            assertNotNull(fieldInjectedReference);
            assertNotNull(fieldInjectedReference.getProperties());
            assertNotNull(fieldInjectedReference.getGenericReference());
            assertNotNull(fieldInjectedReference.getRawReference());
            assertNotNull(fieldInjectedReference.getService());
            assertEquals("value", fieldInjectedReference.getProperties().get("key"));
            assertEquals("value", fieldInjectedReference.getGenericReference().getProperty("key"));
            assertEquals("value", fieldInjectedReference.getRawReference().getProperty("key"));
        } finally {
            bundleScoped.unregister();
        }
	}

	@Test
	public void testFieldInjectedReference_PrototypeScoped() throws Exception {
		ServiceTracker<FieldInjectedReference, FieldInjectedReference> tracker = track(
			"(&(objectClass=%s)(objectClass=*.%s))",
			FieldInjectedReference.class.getName(),
			"FieldInjectedPrototypeScopedImpl");

        ServiceRegistration<PrototypeScoped> prototypeScoped = register(
                PrototypeScoped.class,
                new ServicePrototypeScope(),
                "key", "value");
        try {
            FieldInjectedReference fieldInjectedReference = tracker.waitForService(timeout);

            assertNotNull(fieldInjectedReference);
            assertNotNull(fieldInjectedReference.getProperties());
            assertNotNull(fieldInjectedReference.getGenericReference());
            assertNotNull(fieldInjectedReference.getRawReference());
            assertNotNull(fieldInjectedReference.getService());
            assertEquals("value", fieldInjectedReference.getProperties().get("key"));
            assertEquals("value", fieldInjectedReference.getGenericReference().getProperty("key"));
            assertEquals("value", fieldInjectedReference.getRawReference().getProperty("key"));
        } finally {
            prototypeScoped.unregister();
        }
	}

	@Test
	public void testFieldInjectedService() throws Exception {
		ServiceTracker<BeanService, BeanService> tracker = track(
			"(&(objectClass=%s)(objectClass=*.%s))",
			BeanService.class.getName(),
			"FieldInjectedService");

		BeanService beanService = tracker.waitForService(timeout);

		assertNotNull(beanService);
		assertEquals("PREFIXFIELD", beanService.doSomething());
	}

	@Test
	public void testMethodInjectedService() throws Exception {
		ServiceTracker<BeanService, BeanService> tracker = track(
			"(&(objectClass=%s)(objectClass=*.%s))",
			BeanService.class.getName(),
			"MethodInjectedService");

		BeanService beanService = tracker.waitForService(timeout);

		assertNotNull(beanService);
		assertEquals("PREFIXMETHOD", beanService.doSomething());
	}

	@Test
	public void testBeanAsServiceWithProperties() throws Exception {
		ServiceTracker<BeanService, BeanService> tracker = track(
			"(&(objectClass=%s)(objectClass=*.%s))",
			BeanService.class.getName(),
			"ServiceWithProperties");

		BeanService beanService = tracker.waitForService(timeout);

		assertNotNull(beanService);

		ServiceReference<BeanService> serviceReference = tracker.getServiceReference();

		assertEquals("test.value.b2", serviceReference.getProperty("test.key.b2"));

		assertTrue(serviceReference.getProperty("p.Boolean") instanceof Boolean);
		assertTrue(serviceReference.getProperty("p.Boolean.array") instanceof boolean[]);
		assertEquals(2, ((boolean[])serviceReference.getProperty("p.Boolean.array")).length);

		assertTrue(serviceReference.getProperty("p.Byte") instanceof Byte);
		assertTrue(serviceReference.getProperty("p.Byte.array") instanceof byte[]);
		assertEquals(2, ((byte[])serviceReference.getProperty("p.Byte.array")).length);

		assertTrue(serviceReference.getProperty("p.Character") instanceof Character);
		assertTrue(serviceReference.getProperty("p.Character.array") instanceof char[]);
		assertEquals(2, ((char[])serviceReference.getProperty("p.Character.array")).length);

		assertTrue(serviceReference.getProperty("p.Double") instanceof Double);
		assertTrue(serviceReference.getProperty("p.Double.array") instanceof double[]);
		assertEquals(2, ((double[])serviceReference.getProperty("p.Double.array")).length);

		assertTrue(serviceReference.getProperty("p.Float") instanceof Float);
		assertTrue(serviceReference.getProperty("p.Float.array") instanceof float[]);
		assertEquals(2, ((float[])serviceReference.getProperty("p.Float.array")).length);

		assertTrue(serviceReference.getProperty("p.Integer") instanceof Integer);
		assertTrue(serviceReference.getProperty("p.Integer.array") instanceof int[]);
		assertEquals(2, ((int[])serviceReference.getProperty("p.Integer.array")).length);

		assertTrue(serviceReference.getProperty("p.Long") instanceof Long);
		assertTrue(serviceReference.getProperty("p.Long.array") instanceof long[]);
		assertEquals(2, ((long[])serviceReference.getProperty("p.Long.array")).length);

		assertTrue(serviceReference.getProperty("p.Short") instanceof Short);
		assertTrue(serviceReference.getProperty("p.Short.array") instanceof short[]);
		assertEquals(2, ((short[])serviceReference.getProperty("p.Short.array")).length);

		assertTrue(serviceReference.getProperty("p.String") instanceof String);
		assertTrue(serviceReference.getProperty("p.String.array") instanceof String[]);
		assertEquals(2, ((String[])serviceReference.getProperty("p.String.array")).length);

		// glubInteger = 45, gooString = "green"
		assertTrue(serviceReference.getProperty("glub.integer") instanceof Integer);
		assertEquals(45, ((Integer)serviceReference.getProperty("glub.integer")).intValue());
		assertTrue(serviceReference.getProperty("goo.string") instanceof String);
		assertEquals("green", (serviceReference.getProperty("goo.string")));
	}

	@Test
	public void testBundleContextInjection() throws Exception {
		BeanManager beanManager = getBeanManager(cdiBundle);

		assertNotNull(beanManager);

		@SuppressWarnings("serial")
		Set<Bean<?>> beans = beanManager.getBeans(Object.class, new AnnotationLiteral<BundleContextBeanQualifier>() {});
		Bean<?> bean = beanManager.resolve(beans);
		CreationalContext<?> ctx = beanManager.createCreationalContext(bean);
		Object bcb = beanManager.getReference(bean, Object.class, ctx);
		assertNotNull(bcb);
		@SuppressWarnings("unchecked")
		BeanService<BundleContext> bti = (BeanService<BundleContext>)bcb;
		assertNotNull(bti.get());
		assertTrue(bti.get() instanceof BundleContext);
	}

	@Test
	public void testInstanceProperties() throws Exception {
		ServiceTracker<BeanService, BeanService> tracker = track(
			"(&(objectClass=%s)(objectClass=*.%s))",
			BeanService.class.getName(),
			"Instance_ServiceProperties");

		BeanService beanService = tracker.waitForService(timeout);

		assertNotNull(beanService);
		assertEquals(4, Integer.decode(beanService.doSomething()).intValue());
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>)beanService.get();
		assertNotNull(map);
		assertEquals(100000, (int)map.get("service.ranking"));
	}

	@Test
	public void testInstanceServiceReference() throws Exception {
		ServiceTracker<BeanService, BeanService> tracker = track(
			"(&(objectClass=%s)(objectClass=*.%s))",
			BeanService.class.getName(),
			"Instance_ServiceReference");
		
        ServiceRegistration<?> regOne = register(
                SingletonScoped.class, new ServiceSingletonOne(), SERVICE_RANKING, 1);
        ServiceRegistration<?> regTwo = register(
                SingletonScoped.class, new ServiceSingletonTwo(), SERVICE_RANKING, 2);
        ServiceRegistration<?> regThree = register(
                SingletonScoped.class, new ServiceSingletonThree(), SERVICE_RANKING, 3);
        ServiceRegistration<?> regFour = register(
                SingletonScoped.class, new ServiceSingletonFour(), SERVICE_RANKING, 4);
        try {
            BeanService beanService = tracker.waitForService(timeout);

            assertNotNull(beanService);
            assertEquals(4, Integer.decode(beanService.doSomething()).intValue());
            ServiceReference<?> sr = (ServiceReference<?>) beanService.get();
            assertNotNull(sr);
            assertEquals(4, (int) sr.getProperty("service.ranking"));
        } finally {
            Stream.of(regOne, regTwo, regThree, regFour).forEach(ServiceRegistration::unregister);
        }
	}

	@Test
	public void testInstance_Optional() throws Exception {
		ServiceTracker<BeanService, BeanService> tracker = track(
			"(&(objectClass=%s)(objectClass=*.%s))",
			BeanService.class.getName(),
			"Instance_Optional");

		BeanService beanService = tracker.waitForService(timeout);

		assertNotNull(beanService);
		assertEquals(0, Integer.decode(beanService.doSomething()).intValue());
		assertNull(beanService.get());
	}

}