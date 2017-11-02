/*
 * Copyright (c) OSGi Alliance (2015, 2016). All Rights Reserved.
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

package org.osgi.test.cases.component.junit;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.ComponentException;
import org.osgi.service.component.ComponentFactory;
import org.osgi.test.cases.component.service.ObjectProvider1;
import org.osgi.test.cases.component.types.AnnotationMember;
import org.osgi.test.cases.component.types.ClassMember;
import org.osgi.test.cases.component.types.Coercion;
import org.osgi.test.cases.component.types.EnumMember;
import org.osgi.test.cases.component.types.NameMapping;
import org.osgi.test.cases.component.types.TestEnum;
import org.osgi.test.support.OSGiTestCase;
import org.osgi.util.tracker.ServiceTracker;

public class ComponentPropertyTypeTestCase extends OSGiTestCase {
	private static int SLEEP = 1000;

	public ComponentPropertyTypeTestCase() {
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		String sleepTimeString = getProperty("osgi.tc.component.sleeptime");
		int sleepTime = SLEEP;
		if (sleepTimeString != null) {
			try {
				sleepTime = Integer.parseInt(sleepTimeString);
			} catch (Exception e) {
				e.printStackTrace();
				System.out
						.println("Error while parsing sleep value! The default one will be used : "
								+ SLEEP);
			}
			if (sleepTime < 100) {
				System.out.println("The sleep value is too low : " + sleepTime
						+ " ! The default one will be used : " + SLEEP);
			} else {
				SLEEP = sleepTime;
			}
		}
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testNameMapping130() throws Exception {
		Bundle tb26 = install("tb26.jar");
		assertNotNull("tb26 failed to install", tb26);
		try {
			tb26.start();

			Filter providerFilter = getContext().createFilter("(&(" + Constants.OBJECTCLASS + "="
					+ ObjectProvider1.class.getName() + ")(" + ComponentConstants.COMPONENT_NAME
					+ "=org.osgi.test.cases.component.tb26.NameMappingComponent))");
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
			tb26.uninstall();
		}
	}

	public void testAnnotationMember130() throws Exception {
		Bundle tb26 = install("tb26.jar");
		assertNotNull("tb26 failed to install", tb26);
		try {
			tb26.start();

			Filter providerFilter = getContext().createFilter("(&(" + Constants.OBJECTCLASS + "="
					+ ObjectProvider1.class.getName() + ")(" + ComponentConstants.COMPONENT_NAME
					+ "=org.osgi.test.cases.component.tb26.AnnotationMemberComponent))");
			ServiceTracker<ObjectProvider1<AnnotationMember>, ObjectProvider1<AnnotationMember>> providerTracker = new ServiceTracker<ObjectProvider1<AnnotationMember>, ObjectProvider1<AnnotationMember>>(
					getContext(), providerFilter, null);
			try {
				providerTracker.open();
				ObjectProvider1<AnnotationMember> p = providerTracker.waitForService(SLEEP * 3);
				assertNotNull("missing provider", p);
				AnnotationMember config = p.get1();
				assertNotNull("missing config", config);

				try {
					assertEquals("did not throw Exception", ComponentException.class, config.error());
				} catch (ComponentException e) {
					// expected
				}
				try {
					assertEquals("did not throw Exception", ComponentException.class, config.errors());
				} catch (ComponentException e) {
					// expected
				}

			} finally {
				providerTracker.close();
			}
		} finally {
			tb26.uninstall();
		}
	}

	public void testClassMember130() throws Exception {
		Bundle tb26 = install("tb26.jar");
		assertNotNull("tb26 failed to install", tb26);
		try {
			tb26.start();

			Filter providerFilter = getContext().createFilter("(&(" + Constants.OBJECTCLASS + "="
					+ ObjectProvider1.class.getName() + ")(" + ComponentConstants.COMPONENT_NAME
					+ "=org.osgi.test.cases.component.tb26.ClassMemberComponent))");
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
			tb26.uninstall();
		}
	}

	public void testEnumMember130() throws Exception {
		Bundle tb26 = install("tb26.jar");
		assertNotNull("tb26 failed to install", tb26);
		try {
			tb26.start();

			Filter providerFilter = getContext().createFilter("(&(" + Constants.OBJECTCLASS + "="
					+ ObjectProvider1.class.getName() + ")(" + ComponentConstants.COMPONENT_NAME
					+ "=org.osgi.test.cases.component.tb26.EnumMemberComponent))");
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
			tb26.uninstall();
		}
	}

	public void testCoercion130() throws Exception {
		Bundle tb26 = install("tb26.jar");
		assertNotNull("tb26 failed to install", tb26);
		try {
			tb26.start();

			Filter providerFilter = getContext().createFilter("(&(" + Constants.OBJECTCLASS + "="
					+ ComponentFactory.class.getName() + ")(" + ComponentConstants.COMPONENT_FACTORY
					+ "=org.osgi.test.cases.component.tb26.CoercionComponent))");
			ServiceTracker<ComponentFactory<ObjectProvider1<Coercion>>,ComponentFactory<ObjectProvider1<Coercion>>> providerTracker = new ServiceTracker<>(
					getContext(), providerFilter, null);
			try {
				providerTracker.open();
				ComponentFactory<ObjectProvider1<Coercion>> f = providerTracker
						.waitForService(SLEEP * 3);
				assertNotNull("missing factory", f);
				Dictionary<String, Object> props = new Hashtable<String, Object>();
				props.put("stringCollection", Arrays.asList("list", "second"));
				props.put("booleanCollection", Arrays.asList(Boolean.TRUE, Boolean.TRUE));
				props.put("charCollection", Arrays.asList(Character.valueOf('@'), Character.valueOf('\u0000')));
				props.put("byteCollection", Arrays.asList(Byte.valueOf((byte) 64), Byte.valueOf((byte) 0)));
				props.put("shortCollection", Arrays.asList(Short.valueOf((short) 64), Short.valueOf((short) 0)));
				props.put("intCollection", Arrays.asList(Integer.valueOf(64), Integer.valueOf(0)));
				props.put("longCollection", Arrays.asList(Long.valueOf(64), Long.valueOf(0)));
				props.put("floatCollection", Arrays.asList(Float.valueOf(64.3F), Float.valueOf(0.3F)));
				props.put("doubleCollection", Arrays.asList(Double.valueOf(64.3D), Double.valueOf(0.3D)));
				props.put("classCollection",
						Arrays.asList("org.osgi.test.cases.component.types.Coercion",
								"org.osgi.test.cases.component.tb26.CoercionComponent"));
				props.put("enumCollection", Arrays.asList(TestEnum.ITEM1.name(),
						TestEnum.AnotherItem.name()));
				props.put("int$Collection", Arrays.asList(Integer.valueOf(64), Integer.valueOf(0)));
				ObjectProvider1<Coercion> p = f.newInstance(props).getInstance();
				assertNotNull("missing provider", p);
				Coercion config = p.get1();
				assertNotNull("missing config", config);

				assertEquals("property has wrong value", "xml/stringString", config.stringString());
				assertEquals("property has wrong value", "true", config.stringBoolean());
				assertEquals("property has wrong value", "@", config.stringCharacter());
				assertEquals("property has wrong value", "2", config.stringByte());
				assertEquals("property has wrong value", "1024", config.stringShort());
				assertEquals("property has wrong value", "123456", config.stringInteger());
				assertEquals("property has wrong value", "9876543210", config.stringLong());
				assertEquals("property has wrong value", "3.14", config.stringFloat());
				assertEquals("property has wrong value", "2.1", config.stringDouble());
				assertEquals("property has wrong value", "list", config.stringCollection());
				assertEquals("property has wrong value", "true", config.stringArray());
				assertNull("property has wrong value", config.stringNone());

				assertTrue("property has wrong value", config.booleanString());
				assertTrue("property has wrong value", config.booleanBoolean());
				assertFalse("property has wrong value", config.booleanCharacter());
				assertTrue("property has wrong value", config.booleanByte());
				assertTrue("property has wrong value", config.booleanShort());
				assertTrue("property has wrong value", config.booleanInteger());
				assertTrue("property has wrong value", config.booleanLong());
				assertTrue("property has wrong value", config.booleanFloat());
				assertFalse("property has wrong value", config.booleanDouble());
				assertTrue("property has wrong value", config.booleanCollection());
				assertTrue("property has wrong value", config.booleanArray());
				assertFalse("property has wrong value", config.booleanNone());

				assertEquals("property has wrong value", '6', config.charString());
				assertEquals("property has wrong value", (char) 0, config.charStringEmpty());
				assertEquals("property has wrong value", (char) 1, config.charBooleanTrue());
				assertEquals("property has wrong value", (char) 0, config.charBooleanFalse());
				assertEquals("property has wrong value", '@', config.charCharacter());
				assertEquals("property has wrong value", (char) 2, config.charByte());
				assertEquals("property has wrong value", (char) 1034, config.charShort());
				assertEquals("property has wrong value", (char) 123456, config.charInteger());
				assertEquals("property has wrong value", (char) 9876543210L, config.charLong());
				assertEquals("property has wrong value", (char) 3.14F, config.charFloat());
				assertEquals("property has wrong value", (char) 2.1D, config.charDouble());
				assertEquals("property has wrong value", '@', config.charCollection());
				assertEquals("property has wrong value", (char) 1, config.charArray());
				assertEquals("property has wrong value", (char) 0, config.charNone());

				assertEquals("property has wrong value", (byte) 64, config.byteString());
				assertEquals("property has wrong value", (byte) 1, config.byteBooleanTrue());
				assertEquals("property has wrong value", (byte) 0, config.byteBooleanFalse());
				assertEquals("property has wrong value", (byte) 64, config.byteCharacter());
				assertEquals("property has wrong value", (byte) 2, config.byteByte());
				assertEquals("property has wrong value", (byte) 1034, config.byteShort());
				assertEquals("property has wrong value", (byte) 123456, config.byteInteger());
				assertEquals("property has wrong value", (byte) 9876543210L, config.byteLong());
				assertEquals("property has wrong value", (byte) 3.14F, config.byteFloat());
				assertEquals("property has wrong value", (byte) 2.1D, config.byteDouble());
				assertEquals("property has wrong value", (byte) 64, config.byteCollection());
				assertEquals("property has wrong value", (byte) 1, config.byteArray());
				assertEquals("property has wrong value", (byte) 0, config.byteNone());

				assertEquals("property has wrong value", (short) 64, config.shortString());
				assertEquals("property has wrong value", (short) 1, config.shortBooleanTrue());
				assertEquals("property has wrong value", (short) 0, config.shortBooleanFalse());
				assertEquals("property has wrong value", (short) 64, config.shortCharacter());
				assertEquals("property has wrong value", (short) 2, config.shortByte());
				assertEquals("property has wrong value", (short) 1034, config.shortShort());
				assertEquals("property has wrong value", (short) 123456, config.shortInteger());
				assertEquals("property has wrong value", (short) 9876543210L, config.shortLong());
				assertEquals("property has wrong value", (short) 3.14F, config.shortFloat());
				assertEquals("property has wrong value", (short) 2.1D, config.shortDouble());
				assertEquals("property has wrong value", (short) 64, config.shortCollection());
				assertEquals("property has wrong value", (short) 1, config.shortArray());
				assertEquals("property has wrong value", (short) 0, config.shortNone());

				assertEquals("property has wrong value", 64, config.intString());
				assertEquals("property has wrong value", 1, config.intBooleanTrue());
				assertEquals("property has wrong value", 0, config.intBooleanFalse());
				assertEquals("property has wrong value", 64, config.intCharacter());
				assertEquals("property has wrong value", 2, config.intByte());
				assertEquals("property has wrong value", 1034, config.intShort());
				assertEquals("property has wrong value", 123456, config.intInteger());
				assertEquals("property has wrong value", (int) 9876543210L, config.intLong());
				assertEquals("property has wrong value", (int) 3.14F, config.intFloat());
				assertEquals("property has wrong value", (int) 2.1D, config.intDouble());
				assertEquals("property has wrong value", 64, config.intCollection());
				assertEquals("property has wrong value", 1, config.intArray());
				assertEquals("property has wrong value", 0, config.intNone());

				assertEquals("property has wrong value", 64, config.longString());
				assertEquals("property has wrong value", 1, config.longBooleanTrue());
				assertEquals("property has wrong value", 0, config.longBooleanFalse());
				assertEquals("property has wrong value", 64, config.longCharacter());
				assertEquals("property has wrong value", 2, config.longByte());
				assertEquals("property has wrong value", 1034, config.longShort());
				assertEquals("property has wrong value", 123456, config.longInteger());
				assertEquals("property has wrong value", 9876543210L, config.longLong());
				assertEquals("property has wrong value", (long) 3.14F, config.longFloat());
				assertEquals("property has wrong value", (long) 2.1D, config.longDouble());
				assertEquals("property has wrong value", 64, config.longCollection());
				assertEquals("property has wrong value", 1, config.longArray());
				assertEquals("property has wrong value", 0, config.longNone());

				final float deltaFloat = 0x1.0p-126f;
				assertEquals("property has wrong value", 64.3F, config.floatString(), deltaFloat);
				assertEquals("property has wrong value", 1, config.floatBooleanTrue(), deltaFloat);
				assertEquals("property has wrong value", 0, config.floatBooleanFalse(), deltaFloat);
				assertEquals("property has wrong value", 64, config.floatCharacter(), deltaFloat);
				assertEquals("property has wrong value", 2, config.floatByte(), deltaFloat);
				assertEquals("property has wrong value", 1034, config.floatShort(), deltaFloat);
				assertEquals("property has wrong value", 123456, config.floatInteger(), deltaFloat);
				assertEquals("property has wrong value", 9876543210L, config.floatLong(), deltaFloat);
				assertEquals("property has wrong value", 3.14F, config.floatFloat(), deltaFloat);
				assertEquals("property has wrong value", (float) 2.1D, config.floatDouble(), deltaFloat);
				assertEquals("property has wrong value", 64.3F, config.floatCollection(), deltaFloat);
				assertEquals("property has wrong value", 1F, config.floatArray(), deltaFloat);
				assertEquals("property has wrong value", 0F, config.floatNone(), deltaFloat);

				final double deltaDouble = 0x1.0p-1022;
				assertEquals("property has wrong value", 64.3D, config.doubleString(), deltaDouble);
				assertEquals("property has wrong value", 1, config.doubleBooleanTrue(), deltaDouble);
				assertEquals("property has wrong value", 0, config.doubleBooleanFalse(), deltaDouble);
				assertEquals("property has wrong value", 64, config.doubleCharacter(), deltaDouble);
				assertEquals("property has wrong value", 2, config.doubleByte(), deltaDouble);
				assertEquals("property has wrong value", 1034, config.doubleShort(), deltaDouble);
				assertEquals("property has wrong value", 123456, config.doubleInteger(), deltaDouble);
				assertEquals("property has wrong value", 9876543210L, config.doubleLong(), deltaDouble);
				assertEquals("property has wrong value", 3.14F, config.doubleFloat(), deltaDouble);
				assertEquals("property has wrong value", 2.1D, config.doubleDouble(), deltaDouble);
				assertEquals("property has wrong value", 64.3D, config.doubleCollection(), deltaDouble);
				assertEquals("property has wrong value", 1D, config.doubleArray(), deltaDouble);
				assertEquals("property has wrong value", 0D, config.doubleNone(), deltaDouble);

				try {
					assertEquals("did not throw Exception", ComponentException.class, config.classBoolean());
				} catch (ComponentException e) {
					// expected
				}
				try {
					assertEquals("did not throw Exception", ComponentException.class, config.classCharacter());
				} catch (ComponentException e) {
					// expected
				}
				try {
					assertEquals("did not throw Exception", ComponentException.class, config.classByte());
				} catch (ComponentException e) {
					// expected
				}
				try {
					assertEquals("did not throw Exception", ComponentException.class, config.classShort());
				} catch (ComponentException e) {
					// expected
				}
				try {
					assertEquals("did not throw Exception", ComponentException.class, config.classInteger());
				} catch (ComponentException e) {
					// expected
				}
				try {
					assertEquals("did not throw Exception", ComponentException.class, config.classLong());
				} catch (ComponentException e) {
					// expected
				}
				try {
					assertEquals("did not throw Exception", ComponentException.class, config.classFloat());
				} catch (ComponentException e) {
					// expected
				}
				try {
					assertEquals("did not throw Exception", ComponentException.class, config.classDouble());
				} catch (ComponentException e) {
					// expected
				}
				assertSame("property has wrong value", Coercion.class, config.classCollection());
				assertSame("property has wrong value", p.getClass(), config.classArray());
				assertNull("property has wrong value", config.classNone());

				try {
					assertEquals("did not throw Exception", ComponentException.class, config.enumBoolean());
				} catch (ComponentException e) {
					// expected
				}
				try {
					assertEquals("did not throw Exception", ComponentException.class, config.enumCharacter());
				} catch (ComponentException e) {
					// expected
				}
				try {
					assertEquals("did not throw Exception", ComponentException.class, config.enumByte());
				} catch (ComponentException e) {
					// expected
				}
				try {
					assertEquals("did not throw Exception", ComponentException.class, config.enumShort());
				} catch (ComponentException e) {
					// expected
				}
				try {
					assertEquals("did not throw Exception", ComponentException.class, config.enumInteger());
				} catch (ComponentException e) {
					// expected
				}
				try {
					assertEquals("did not throw Exception", ComponentException.class, config.enumLong());
				} catch (ComponentException e) {
					// expected
				}
				try {
					assertEquals("did not throw Exception", ComponentException.class, config.enumFloat());
				} catch (ComponentException e) {
					// expected
				}
				try {
					assertEquals("did not throw Exception", ComponentException.class, config.enumDouble());
				} catch (ComponentException e) {
					// expected
				}
				assertSame("property has wrong value", TestEnum.ITEM1, config.enumCollection());
				assertSame("property has wrong value", TestEnum.AnotherItem, config.enumArray());
				assertNull("property has wrong value", config.enumNone());

				assertNotNull("array null", config.string$$String());
				assertEquals("array wrong length", 1, config.string$$String().length);
				assertEquals("property has wrong value", "xml/stringString", config.string$$String()[0]);
				assertNotNull("array null", config.boolean$$Boolean());
				assertEquals("array wrong length", 1, config.boolean$$Boolean().length);
				assertTrue("property has wrong value", config.boolean$$Boolean()[0]);
				assertNotNull("array null", config.char$$Character());
				assertEquals("array wrong length", 1, config.char$$Character().length);
				assertEquals("property has wrong value", '@', config.char$$Character()[0]);
				assertNotNull("array null", config.byte$$Byte());
				assertEquals("array wrong length", 1, config.byte$$Byte().length);
				assertEquals("property has wrong value", (byte) 2, config.byte$$Byte()[0]);
				assertNotNull("array null", config.short$$Short());
				assertEquals("array wrong length", 1, config.short$$Short().length);
				assertEquals("property has wrong value", (short) 1034, config.short$$Short()[0]);
				assertNotNull("array null", config.int$$Integer());
				assertEquals("array wrong length", 1, config.int$$Integer().length);
				assertEquals("property has wrong value", 123456, config.int$$Integer()[0]);
				assertNotNull("array null", config.long$$Long());
				assertEquals("array wrong length", 1, config.long$$Long().length);
				assertEquals("property has wrong value", 9876543210L, config.long$$Long()[0]);
				assertNotNull("array null", config.float$$Float());
				assertEquals("array wrong length", 1, config.float$$Float().length);
				assertEquals("property has wrong value", 3.14F, config.float$$Float()[0], deltaFloat);
				assertNotNull("array null", config.double$$Double());
				assertEquals("array wrong length", 1, config.double$$Double().length);
				assertEquals("property has wrong value", 2.1D, config.double$$Double()[0], deltaDouble);
				assertNotNull("array null", config.int$$Collection());
				assertEquals("array wrong length", 2, config.int$$Collection().length);
				assertEquals("property has wrong value", 64, config.int$$Collection()[0]);
				assertEquals("property has wrong value", 0, config.int$$Collection()[1]);
				assertNotNull("array null", config.long$$Array());
				assertEquals("array wrong length", 2, config.long$$Array().length);
				assertEquals("property has wrong value", 9876543210L, config.long$$Array()[0]);
				assertEquals("property has wrong value", 123456L, config.long$$Array()[1]);
				// this changed in 1.4 (R7) - while it has to be null in 1.3 it must be an empty array in 1.4:
				assertNotNull("array null", config.string$$None());
				assertEquals("array wrong length", 0, config.string$$None().length);

			} finally {
				providerTracker.close();
			}
		} finally {
			tb26.uninstall();
		}
	}

}
