/*
 * Copyright (c) OSGi Alliance (2015). All Rights Reserved.
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


package org.osgi.test.cases.component.annotations.junit;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.ComponentException;
import org.osgi.test.cases.component.annotations.service.ObjectProvider1;
import org.osgi.test.cases.component.annotations.types.AnnotationMember;
import org.osgi.test.cases.component.annotations.types.ClassMember;
import org.osgi.test.cases.component.annotations.types.EnumMember;
import org.osgi.test.cases.component.annotations.types.NameMapping;
import org.osgi.test.cases.component.annotations.types.TestEnum;
import org.osgi.test.support.OSGiTestCase;
import org.osgi.test.support.OSGiTestCaseProperties;
import org.osgi.util.tracker.ServiceTracker;

public class ComponentPropertyTypeTestCase extends OSGiTestCase {
	private static long SLEEP = OSGiTestCaseProperties.getTimeout() * OSGiTestCaseProperties.getScaling();

	public ComponentPropertyTypeTestCase() {
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testNameMapping130() throws Exception {
		Bundle tb1 = install("tb1.jar");
		assertNotNull("tb1 failed to install", tb1);
		try {
			tb1.start();

			Filter providerFilter = getContext().createFilter("(&(" + Constants.OBJECTCLASS + "="
					+ ObjectProvider1.class.getName() + ")(" + ComponentConstants.COMPONENT_NAME
					+ "=org.osgi.test.cases.component.annotations.tb1.NameMappingComponent))");
			ServiceTracker<ObjectProvider1<NameMapping>, ObjectProvider1<NameMapping>> providerTracker = new ServiceTracker<ObjectProvider1<NameMapping>, ObjectProvider1<NameMapping>>(
					getContext(), providerFilter, null);
			try {
				providerTracker.open();
				ObjectProvider1<NameMapping> p = providerTracker.waitForService(SLEEP * 3);
				assertNotNull("missing provider", p);
				NameMapping config = p.get1();
				assertNotNull("missing config", config);

				assertEquals("property has wrong value", "xml/myProperty143", config.myProperty143());
				assertEquals("property has wrong value", "xml/new", config.$new());
				assertEquals("property has wrong value", "xml/my$prop", config.my$$prop());
				assertEquals("property has wrong value", "xml/dot.prop", config.dot_prop());
				assertEquals("property has wrong value", "xml/.secret", config._secret());
				assertEquals("property has wrong value", "xml/another_prop", config.another__prop());
				assertEquals("property has wrong value", "xml/three_.prop", config.three___prop());
				assertEquals("property has wrong value", "xml/four._prop", config.four_$__prop());
				assertEquals("property has wrong value", "xml/five..prop", config.five_$_prop());

			} finally {
				providerTracker.close();
			}
		} finally {
			tb1.uninstall();
		}
	}

	public void testAnnotationMember130() throws Exception {
		Bundle tb1 = install("tb1.jar");
		assertNotNull("tb1 failed to install", tb1);
		try {
			tb1.start();

			Filter providerFilter = getContext().createFilter("(&(" + Constants.OBJECTCLASS + "="
					+ ObjectProvider1.class.getName() + ")(" + ComponentConstants.COMPONENT_NAME
					+ "=org.osgi.test.cases.component.annotations.tb1.AnnotationMemberComponent))");
			ServiceTracker<ObjectProvider1<AnnotationMember>, ObjectProvider1<AnnotationMember>> providerTracker = new ServiceTracker<ObjectProvider1<AnnotationMember>, ObjectProvider1<AnnotationMember>>(
					getContext(), providerFilter, null);
			try {
				providerTracker.open();
				ObjectProvider1<AnnotationMember> p = providerTracker.waitForService(SLEEP * 3);
				assertNotNull("missing provider", p);
				AnnotationMember config = p.get1();
				assertNotNull("missing config", config);

				try {
					config.error();
					fail("annotation return type did not throw expected ComponentException");
				} catch (ComponentException e) {
					// expected
				}
				try {
					config.errors();
					fail("annotation return type did not throw expected ComponentException");
				} catch (ComponentException e) {
					// expected
				}

			} finally {
				providerTracker.close();
			}
		} finally {
			tb1.uninstall();
		}
	}

	public void testClassMember130() throws Exception {
		Bundle tb1 = install("tb1.jar");
		assertNotNull("tb1 failed to install", tb1);
		try {
			tb1.start();
			
			Filter providerFilter = getContext().createFilter("(&(" + Constants.OBJECTCLASS + "="
					+ ObjectProvider1.class.getName() + ")(" + ComponentConstants.COMPONENT_NAME
					+ "=org.osgi.test.cases.component.annotations.tb1.ClassMemberComponent))");
			ServiceTracker<ObjectProvider1<ClassMember>, ObjectProvider1<ClassMember>> providerTracker = new ServiceTracker<ObjectProvider1<ClassMember>, ObjectProvider1<ClassMember>>(
					getContext(), providerFilter, null);
			try {
				providerTracker.open();
				ObjectProvider1<ClassMember> p = providerTracker.waitForService(SLEEP * 3);
				assertNotNull("missing provider", p);
				ClassMember config = p.get1();
				assertNotNull("missing config", config);
				
				Class<?> single = config.single();
				assertSame("property has wrong value", ClassMember.class, single);
				Class<?>[] multiple = config.multiple();
				assertNotNull("array null", multiple);
				assertEquals("array wrong length", 2, multiple.length);
				assertSame("property has wrong value", p.getClass(), multiple[0]);
				assertSame("property has wrong value", ClassMember.class, multiple[1]);
			} finally {
				providerTracker.close();
			}
		} finally {
			tb1.uninstall();
		}
	}

	public void testEnumMember130() throws Exception {
		Bundle tb1 = install("tb1.jar");
		assertNotNull("tb1 failed to install", tb1);
		try {
			tb1.start();

			Filter providerFilter = getContext().createFilter("(&(" + Constants.OBJECTCLASS + "="
					+ ObjectProvider1.class.getName() + ")(" + ComponentConstants.COMPONENT_NAME
					+ "=org.osgi.test.cases.component.annotations.tb1.EnumMemberComponent))");
			ServiceTracker<ObjectProvider1<EnumMember>, ObjectProvider1<EnumMember>> providerTracker = new ServiceTracker<ObjectProvider1<EnumMember>, ObjectProvider1<EnumMember>>(
					getContext(), providerFilter, null);
			try {
				providerTracker.open();
				ObjectProvider1<EnumMember> p = providerTracker.waitForService(SLEEP * 3);
				assertNotNull("missing provider", p);
				EnumMember config = p.get1();
				assertNotNull("missing config", config);

				TestEnum single = config.single();
				assertSame("property has wrong value", TestEnum.ITEM1, single);
				TestEnum[] multiple = config.multiple();
				assertNotNull("array null", multiple);
				assertEquals("array wrong length", 2, multiple.length);
				assertSame("property has wrong value", TestEnum.AnotherItem, multiple[0]);
				assertSame("property has wrong value", TestEnum.ITEM1, multiple[1]);
			} finally {
				providerTracker.close();
			}
		} finally {
			tb1.uninstall();
		}
	}

}
