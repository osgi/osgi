/*
 * Copyright (c) 2014 ProSyst Software GmbH. All Rights Reserved.
 *
 * This CODE is owned by ProSyst Software GmbH,
 * and is being distributed to OSGi PARTICIPANTS as MATERIALS
 * under the terms of section 1 of the OSGi Alliance Inc. Intellectual Property Rights Policy,
 * Amended and Restated as of May 23, 2011.
 */

package org.osgi.test.cases.dal;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.service.dal.Device;
import org.osgi.service.dal.DeviceException;
import org.osgi.service.dal.Function;
import org.osgi.service.dal.FunctionData;
import org.osgi.service.dal.PropertyMetadata;
import org.osgi.test.cases.step.TestStep;

/**
 * Test class validates the function.
 */
public class FunctionTest extends AbstractDeviceTest {

	/**
	 * The function must be registered under only one interface, the function
	 * interface. The test method checks that rule.
	 * 
	 * @throws ClassNotFoundException If the function class cannot be loaded.
	 */
	public void testRegistrationClasses() throws ClassNotFoundException {
		Function[] functions = null;
		try {
			functions = super.getFunctions(Function.SERVICE_UID, null);
		} catch (InvalidSyntaxException e) {
			// not possible
			fail(null, e);
		}
		for (int i = 0; i < functions.length; i++) {
			String[] regClasses = (String[]) functions[i].getServiceProperty(Constants.OBJECTCLASS);
			assertTrue("At least one registration class is expected!", regClasses.length >= 1);
			assertEquals("The last registration class must be: " + Function.class.getName(),
					Function.class.getName(), regClasses[regClasses.length - 1]);
			Class[] classes = loadClasses(regClasses);
			for (int ii = 0, lastIndex = classes.length - 1; ii < lastIndex; ii++) {
				assertTrue("The child in the class hierarchy must be before the parent", classes[ii + 1].isAssignableFrom(classes[ii]));
			}
		}
	}

	/**
	 * Initially, the function services are registered first. The test method
	 * checks that rule.
	 * 
	 * @throws InvalidSyntaxException If the registered device UID breaks the
	 *         LDAP filter.
	 */
	public void testRegistrationOrder() throws InvalidSyntaxException {
		TestStep testStep = super.getTestStep();
		String deviceID = testStep.execute(Commands.REGISTER_DEVICE_SINGLE_FUNCTION, null)[0];
		long deviceServiceID = ((Long) super.getDevice(deviceID).getServiceProperty(Constants.SERVICE_ID)).longValue();
		Function[] functions = getFunctions(Function.SERVICE_DEVICE_UID, deviceID);
		assertEquals("Only one function must be supported!", 1, functions.length);
		long functionServiceID = ((Long) functions[0].getServiceProperty(Constants.SERVICE_ID)).longValue();
		assertTrue("The function must be registered before the device!", functionServiceID < deviceServiceID);
	}

	/**
	 * The device service is unregistered first. The test method checks that
	 * rule.
	 * 
	 * @throws InvalidSyntaxException If the registered device UID can break the
	 *         LDAP filter.
	 * @throws DeviceException An error while removing the device.
	 * @throws UnsupportedOperationException If remove operation is not
	 *         supported.
	 * @throws IllegalStateException If the test device is already removed.
	 */
	public void testUnregistrationOrder() throws InvalidSyntaxException, DeviceException, UnsupportedOperationException, IllegalStateException {
		TestStep testStep = super.getTestStep();
		String deviceID = testStep.execute(Commands.REGISTER_DEVICE_SINGLE_FUNCTION, null)[0];
		Device device = super.getDevice(deviceID);
		Function[] functions = getFunctions(Function.SERVICE_DEVICE_UID, deviceID);
		assertEquals("Only one function must be supported!", 1, functions.length);
		super.deviceServiceListener.clear();
		device.remove();
		ServiceEvent[] deviceServiceEvents = super.deviceServiceListener.getEvents();
		assertTrue("There are no service event on device remove.", deviceServiceEvents.length > 0);
		boolean isDeviceUnregistered = false;
		boolean isFunctionUnregistered = false;
		for (int i = 0; i < deviceServiceEvents.length; i++) {
			if (ServiceEvent.UNREGISTERING != deviceServiceEvents[i].getType()) {
				continue;
			}
			if (deviceID.equals(deviceServiceEvents[i].getServiceReference().getProperty(Device.SERVICE_UID))) {
				assertFalse("The is already unregistered!", isDeviceUnregistered);
				isDeviceUnregistered = true;
			} else
				if (functions[0].getServiceProperty(Function.SERVICE_UID).equals(
						deviceServiceEvents[i].getServiceReference().getProperty(Function.SERVICE_UID))) {
					assertTrue("The device must be unregistered first!", isDeviceUnregistered);
					assertFalse("The function is already unregistered!", isFunctionUnregistered);
					isFunctionUnregistered = true;
				}
		}
	}

	/**
	 * Checks that all functions support all required properties.
	 */
	public void testRequiredFunctionProperties() {
		ServiceReference[] functionSRefs = getFunctionSRefs();
		for (int i = 0; i < functionSRefs.length; i++) {
			super.checkRequiredProperties(
					functionSRefs[i],
					new String[] {Function.SERVICE_UID});
		}
	}

	/**
	 * Checks that {@link Function#getServiceProperty(String)} returns the same
	 * value as {@link ServiceReference#getProperty(String)}.
	 */
	public void testFunctionProperties() {
		ServiceReference[] functionSRefs = getFunctionSRefs();
		boolean compared = false;
		for (int i = 0; i < functionSRefs.length; i++) {
			Function function = (Function) super.getContext().getService(functionSRefs[i]);
			if (null == function) {
				continue;
			}
			try {
				String[] refKeys = functionSRefs[i].getPropertyKeys();
				for (int ii = 0; ii < refKeys.length; ii++) {
					assertTrue(
							"The function property and service property values are different.",
							TestUtil.areEqual(functionSRefs[i].getProperty(refKeys[ii]), function.getServiceProperty(refKeys[ii])));
				}
				compared = true;
			} catch (UnsupportedOperationException uoe) {
				// expected
			}
		}
		assertTrue("No function with with property access.", compared);
	}

	/**
	 * Checks that function property value type is correct.
	 */
	public void testFunctionPropertyTypes() {
		checkFunctionPropertyType(Function.SERVICE_DESCRIPTION, new Class[] {String.class});
		checkFunctionPropertyType(Function.SERVICE_DEVICE_UID, new Class[] {String.class});
		checkFunctionPropertyType(Function.SERVICE_OPERATION_NAMES, new Class[] {String[].class});
		checkFunctionPropertyType(Function.SERVICE_PROPERTY_NAMES, new Class[] {String[].class});
		checkFunctionPropertyType(Function.SERVICE_REFERENCE_UIDS, new Class[] {String[].class});
		checkFunctionPropertyType(Function.SERVICE_TYPE, new Class[] {String.class});
		checkFunctionPropertyType(Function.SERVICE_UID, new Class[] {String.class});
		checkFunctionPropertyType(Function.SERVICE_VERSION, new Class[] {String.class});
	}

	/**
	 * Checks that property getter is available and accessible.
	 * 
	 * @throws NoSuchMethodException If the getter is missing.
	 * @throws ClassNotFoundException If the function class cannot be find.
	 */
	public void testPropertyGetter() throws NoSuchMethodException, ClassNotFoundException {
		Function[] functions = getFunctions(
				null, PropertyMetadata.ACCESS_READABLE);
		for (int i = 0; i < functions.length; i++) {
			String[] propertyNames = (String[]) functions[i].getServiceProperty(Function.SERVICE_PROPERTY_NAMES);
			for (int ii = 0; ii < propertyNames.length; ii++) {
				if (isPropertyAccessValid(functions[i], propertyNames[ii], PropertyMetadata.ACCESS_READABLE)) {
					checkPropertyGetter(functions[i], propertyNames[ii]);
				}
			}
		}
	}

	/**
	 * Checks that property setter is available and accessible.
	 * 
	 * @throws NoSuchMethodException If the getter is missing.
	 * @throws ClassNotFoundException If the function class cannot be find.
	 */
	public void testPropertySetters() throws NoSuchMethodException, ClassNotFoundException {
		Function[] functions = getFunctions(
				null, PropertyMetadata.ACCESS_WRITABLE);
		for (int i = 0; i < functions.length; i++) {
			String[] propertyNames = (String[]) functions[i].getServiceProperty(Function.SERVICE_PROPERTY_NAMES);
			for (int ii = 0; ii < propertyNames.length; ii++) {
				if (isPropertyAccessValid(functions[i], propertyNames[ii], PropertyMetadata.ACCESS_READABLE)) {
					checkPropertySetter(functions[i], propertyNames[ii]);
				}
			}
		}
	}

	/**
	 * Tests that there is no operation overloading.
	 * 
	 * @throws ClassNotFoundException If the function class cannot be loaded.
	 */
	public void testOperations() throws ClassNotFoundException {
		Function[] functions = null;
		try {
			functions = super.getFunctions(Function.SERVICE_OPERATION_NAMES, null);
		} catch (InvalidSyntaxException e) {
			fail(null, e);
		}
		for (int i = 0; i < functions.length; i++) {
			String[] operationNames = (String[]) functions[i].getServiceProperty(Function.SERVICE_OPERATION_NAMES);
			for (int ii = 0; ii < operationNames.length; ii++) {
				Class functionClass = TestUtil.getFunctionClass(
						functions[i], super.getContext());
				Method[] methods = TestUtil.getMethods(functionClass, operationNames[ii]);
				assertNotNull("The is no method for operation: " + operationNames[ii]);
				assertEquals("There is operation overloafing for: " + operationNames[ii], 1, methods.length);
			}
		}
	}

	private static Class[] loadClasses(String[] classNames) throws ClassNotFoundException {
		if (null == classNames) {
			return null;
		}
		Class[] classes = new Class[classNames.length];
		for (int i = 0; i < classNames.length; i++) {
			classes[i] = Class.forName(classNames[i]);
		}
		return classes;
	}

	private void checkPropertySetter(Function function, String propertyName) throws NoSuchMethodException, ClassNotFoundException {
		final String setterName = TestUtil.getBeanAccessor(propertyName, "set");
		final Class functionClass = TestUtil.getFunctionClass(function, super.getContext());
		Method[] setters = TestUtil.getMethods(functionClass, setterName);
		assertNotNull("There are no setters for property: " + propertyName, setters);
		assertTrue("There must be one or two setters.",
				(1 == setters.length) || (2 == setters.length));
		boolean setterWithOneParam = false;
		boolean setterWithTwoParam = false;
		for (int i = 0; i < setters.length; i++) {
			assertEquals(
					"No return type is expected for the property setter: " + propertyName,
					Void.TYPE,
					setters[i].getReturnType());
			Class[] paramTypes = setters[i].getParameterTypes();
			switch (paramTypes.length) {
				case 1 :
					assertFalse("There must be only one setter with one parameters: " + setters[i], setterWithOneParam);
					setterWithOneParam = true;
					break;
				case 2 :
					assertFalse("There must be only one setter with two parameters: " + setters[i], setterWithTwoParam);
					setterWithTwoParam = true;
					break;
				default :
					fail("The setter must have one or two parameters.");
			}
		}
	}

	private void checkPropertyGetter(Function function, String propertyName) throws NoSuchMethodException, ClassNotFoundException {
		final String getterName = TestUtil.getBeanAccessor(propertyName, "get");
		final Class functionClass = TestUtil.getFunctionClass(function, super.getContext());
		Method getter = functionClass.getMethod(getterName, null);
		Class returnType = getter.getReturnType();
		assertNotNull("The function getter must have return type: " + getterName, returnType);
		assertTrue(
				"The function getter must return a subclass of " + FunctionData.class.getName(),
				FunctionData.class.isAssignableFrom(returnType));
	}

	private ServiceReference[] getFunctionSRefs() {
		try {
			ServiceReference[] functionSRefs = super.getContext().getServiceReferences(
					null, '(' + Function.SERVICE_UID + "=*)");
			assertNotNull("There are no functions.", functionSRefs);
			return functionSRefs;
		} catch (InvalidSyntaxException e) {
			// null is valid filter
		}
		return null;
	}

	private Function[] getFunctions(String functionClass, int propertyAccess) {
		try {
			ServiceReference[] functionSRefs = super.getContext().getServiceReferences(
					functionClass, '(' + Function.SERVICE_PROPERTY_NAMES + "=*)");
			assertNotNull("There are no functions.", functionSRefs);
			List result = new ArrayList(functionSRefs.length);
			for (int i = 0; i < functionSRefs.length; i++) {
				final Function function = (Function) super.getContext().getService(functionSRefs[i]);
				if (null == function) {
					continue;
				}
				String[] propertyNames = (String[]) function.getServiceProperty(
						Function.SERVICE_PROPERTY_NAMES);
				for (int ii = 0; ii < propertyNames.length; ii++) {
					if (isPropertyAccessValid(function, propertyNames[ii], propertyAccess)) {
						result.add(function);
						break;
					}
				}
			}
			assertFalse(
					"There is no function, which contains a proeprty with an access: " + propertyAccess,
					result.isEmpty());
			return (Function[]) result.toArray(new Function[result.size()]);
		} catch (InvalidSyntaxException e) {
			// the filter is valid
		}
		return null;
	}

	private static boolean isPropertyAccessValid(
			Function function, String propertyName, int propertyAccess) {
		final PropertyMetadata propertyMetadata = function.getPropertyMetadata(propertyName);
		if (null == propertyMetadata) {
			return false;
		}
		final Map metadata = propertyMetadata.getMetadata(null);
		if (null == metadata) {
			return false;
		}
		Integer accessType = (Integer) metadata.get(PropertyMetadata.ACCESS);
		return (null != accessType) && (propertyAccess == (accessType.intValue() & propertyAccess));
	}

	private void checkFunctionPropertyType(String propertyName, Class[] expectedTypes) {
		Function[] functions = null;
		try {
			functions = super.getFunctions(propertyName, null);
		} catch (InvalidSyntaxException e) {
			fail(null, e);
		}
		for (int i = 0; i < functions.length; i++) {
			Class propertyType = functions[i].getServiceProperty(propertyName).getClass();
			assertTrue("The function proeprty type is not correct: " + propertyName + ", type: " + propertyType,
					TestUtil.contains(expectedTypes, propertyType));
		}
	}

}
