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
import org.osgi.test.cases.dal.step.DeviceTestSteps;

/**
 * Test class validates the function.
 */
public class FunctionTest extends AbstractDeviceTest {

	/**
	 * Validates that the {@code Function} class is a part of the function
	 * registration.
	 */
	public void testRegistrationClasses() {
		super.testStepProxy.execute(
				DeviceTestSteps.STEP_ID_AVAILABLE_FUNCTION,
				DeviceTestSteps.STEP_MESSAGE_AVAILABLE_FUNCTION);
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
			String functionClassName = Function.class.getName();
			boolean functionClassInUse = false;
			for (int ii = 0; ii < regClasses.length; ii++) {
				if (functionClassName.equals(regClasses[ii])) {
					functionClassInUse = true;
					break;
				}
			}
			assertTrue("Function class is not used for the function registration.", functionClassInUse);
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
		super.deviceServiceListener.clear();
		super.testStepProxy.execute(
				DeviceTestSteps.STEP_ID_REGISTER_DEVICE_FUNCTION,
				DeviceTestSteps.STEP_MESSAGE_REGISTER_DEVICE_FUNCTION);
		ServiceEvent[] serviceEvents = super.deviceServiceListener.getEvents();
		assertTrue(
				"At least one device should be registered, but there are no events.",
				serviceEvents.length > 0);
		for (int i = 0; i < serviceEvents.length; i++) {
			assertEquals(
					"The event type must be registered.",
					ServiceEvent.REGISTERED, serviceEvents[i].getType());
			String deviceUID = (String) serviceEvents[i].getServiceReference().getProperty(Device.SERVICE_UID);
			assertNotNull("The device unique identifier is missing.", deviceUID);
			Function[] functions = getFunctions(Function.SERVICE_DEVICE_UID, deviceUID);
			long deviceServiceID = ((Long) super.getDevice(deviceUID).getServiceProperty(Constants.SERVICE_ID)).longValue();
			for (int ii = 0; ii < functions.length; ii++) {
				long functionServiceID = ((Long) functions[ii].getServiceProperty(Constants.SERVICE_ID)).longValue();
				assertTrue("The function must be registered before the device!", functionServiceID < deviceServiceID);
			}
		}
	}

	/**
	 * The device service is unregistered first. The test method checks that
	 * rule.
	 * 
	 * @throws InvalidSyntaxException If the registered device UID can break the
	 *         LDAP filter.
	 * @throws DeviceException An error while removing the device.
	 */
	public void testUnregistrationOrder() throws InvalidSyntaxException, DeviceException {
		super.deviceServiceListener.clear();
		super.testStepProxy.execute(
				DeviceTestSteps.STEP_ID_REGISTER_DEVICE_FUNCTION,
				DeviceTestSteps.STEP_MESSAGE_REGISTER_DEVICE_FUNCTION);
		ServiceEvent[] serviceEvents = super.deviceServiceListener.getEvents();
		assertTrue(
				"At least one device should be registered.",
				serviceEvents.length > 0);
		for (int i = 0; i < serviceEvents.length; i++) {
			String deviceUID = (String) serviceEvents[i].getServiceReference().getProperty(Device.SERVICE_UID);
			assertNotNull("The device unique identifier is missing.", deviceUID);
			Device device = super.getDevice(deviceUID);
			Function[] functions = getFunctions(Function.SERVICE_DEVICE_UID, deviceUID);
			super.deviceServiceListener.clear();
			TestServiceListener testServiceListener = new TestServiceListener(
					super.getContext(), TestServiceListener.DEVICE_FUNCTION_FILTER);
			try {
				device.remove();
				ServiceEvent[] deviceServiceEvents = testServiceListener.getEvents();
				assertTrue("At least two service events are expected on device remove.", deviceServiceEvents.length > 1);
				for (int ii = 0; ii < functions.length; ii++) {
					boolean isDeviceUnregistered = false;
					boolean isFunctionUnregistered = false;
					assertEquals(
							"The event type must be service modified.",
							ServiceEvent.MODIFIED,
							deviceServiceEvents[0].getType());
					for (int iii = 1; iii < deviceServiceEvents.length; iii++) {
						assertEquals(
								"The event type must be unregistering.",
								ServiceEvent.UNREGISTERING,
								deviceServiceEvents[iii].getType());
						if (deviceUID.equals(deviceServiceEvents[iii].getServiceReference().getProperty(Device.SERVICE_UID))) {
							assertFalse("The device is already unregistered!", isDeviceUnregistered);
							isDeviceUnregistered = true;
						} else
							if (functions[ii].getServiceProperty(Function.SERVICE_UID).equals(
									deviceServiceEvents[iii].getServiceReference().getProperty(Function.SERVICE_UID))) {
								assertTrue("The device must be unregistered first!", isDeviceUnregistered);
								assertFalse("The function is already unregistered!", isFunctionUnregistered);
								isFunctionUnregistered = true;
							}
					}
				}
			} finally {
				testServiceListener.unregister();
			}
		}
	}

	/**
	 * Checks that all functions support all required properties.
	 */
	public void testRequiredFunctionProperties() {
		super.testStepProxy.execute(
				DeviceTestSteps.STEP_ID_AVAILABLE_FUNCTION,
				DeviceTestSteps.STEP_MESSAGE_AVAILABLE_FUNCTION);
		ServiceReference< ? >[] functionSRefs = getFunctionSRefs();
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
		super.testStepProxy.execute(
				DeviceTestSteps.STEP_ID_AVAILABLE_FUNCTION,
				DeviceTestSteps.STEP_MESSAGE_AVAILABLE_FUNCTION);
		ServiceReference< ? >[] functionSRefs = getFunctionSRefs();
		boolean compared = false;
		for (int i = 0; i < functionSRefs.length; i++) {
			Function function = (Function) super.getContext().getService(functionSRefs[i]);
			if (null == function) {
				continue;
			}
			String[] refKeys = functionSRefs[i].getPropertyKeys();
			for (int ii = 0; ii < refKeys.length; ii++) {
				assertTrue(
						"The function property and service property values are different.",
						TestUtil.areEqual(functionSRefs[i].getProperty(refKeys[ii]), function.getServiceProperty(refKeys[ii])));
			}
			compared = true;
		}
		assertTrue("No function properties have been compared.", compared);
	}

	/**
	 * Checks that function property value type is correct.
	 */
	public void testFunctionPropertyTypes() {
		super.testStepProxy.execute(
				DeviceTestSteps.STEP_ID_FUNCTIONS_ALL_PROPS,
				DeviceTestSteps.STEP_MESSAGE_FUNCTIONS_ALL_PROPS);
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
		super.testStepProxy.execute(
				DeviceTestSteps.STEP_ID_PROPERTY_READABLE,
				DeviceTestSteps.STEP_MESSAGE_PROPERTY_READABLE);
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
		super.testStepProxy.execute(
				DeviceTestSteps.STEP_ID_PROPERTY_WRITABLE,
				DeviceTestSteps.STEP_MESSAGE_PROPERTY_WRITABLE);
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
		super.testStepProxy.execute(
				DeviceTestSteps.STEP_ID_AVAILABLE_OPERATION,
				DeviceTestSteps.STEP_MESSAGE_AVAILABLE_OPERATION);
		Function[] functions = null;
		try {
			functions = super.getFunctions(Function.SERVICE_OPERATION_NAMES, null);
		} catch (InvalidSyntaxException e) {
			fail(null, e);
		}
		for (int i = 0; i < functions.length; i++) {
			String[] operationNames = (String[]) functions[i].getServiceProperty(Function.SERVICE_OPERATION_NAMES);
			for (int ii = 0; ii < operationNames.length; ii++) {
				Method[] methods = TestUtil.getFunctionMethods(functions[i], operationNames[ii], super.getContext());
				assertNotNull("There is no method for operation: " + operationNames[ii], methods);
				assertEquals("There is operation overloading for: " + operationNames[ii], 1, methods.length);
			}
		}
	}

	private void checkPropertySetter(Function function, String propertyName) throws ClassNotFoundException {
		String setterName = TestUtil.getBeanAccessor(propertyName, "set");
		Method[] setters = TestUtil.getFunctionMethods(function, setterName, super.getContext());
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
			Class< ? >[] paramTypes = setters[i].getParameterTypes();
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
		String getterName = TestUtil.getBeanAccessor(propertyName, "get");
		Method[] getters = TestUtil.getFunctionMethods(function, getterName, super.getContext());
		assertEquals("Only one getter is expected: " + getterName, 1, getters.length);
		Method getter = getters[0];
		assertEquals("The getter must not accept parameters.", 0, getter.getParameterTypes().length);
		Class< ? > returnType = getter.getReturnType();
		assertNotNull("The function getter must have return type: " + getterName, returnType);
		assertTrue(
				"The function getter must return a subclass of " + FunctionData.class.getName(),
				FunctionData.class.isAssignableFrom(returnType));
	}

	private ServiceReference< ? >[] getFunctionSRefs() {
		try {
			ServiceReference< ? >[] functionSRefs = super.getContext()
					.getServiceReferences(
					(String)null, '(' + Function.SERVICE_UID + "=*)");
			assertNotNull("There are no functions.", functionSRefs);
			return functionSRefs;
		} catch (InvalidSyntaxException e) {
			// null is valid filter
		}
		return null;
	}

	private Function[] getFunctions(String functionClass, int propertyAccess) {
		try {
			ServiceReference< ? >[] functionSRefs = super.getContext()
					.getServiceReferences(
					functionClass, '(' + Function.SERVICE_PROPERTY_NAMES + "=*)");
			assertNotNull("There are no functions.", functionSRefs);
			List<Function> result = new ArrayList<>(functionSRefs.length);
			for (int i = 0; i < functionSRefs.length; i++) {
				Function function = (Function) super.getContext().getService(functionSRefs[i]);
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
			return result.toArray(new Function[0]);
		} catch (InvalidSyntaxException e) {
			// the filter is valid
		}
		return null;
	}

	private static boolean isPropertyAccessValid(
			Function function, String propertyName, int propertyAccess) {
		PropertyMetadata propertyMetadata = function.getPropertyMetadata(propertyName);
		if (null == propertyMetadata) {
			return false;
		}
		Map<String, ? > metadata = propertyMetadata.getMetadata(null);
		if (null == metadata) {
			return false;
		}
		Integer accessType = (Integer) metadata.get(PropertyMetadata.ACCESS);
		return (null != accessType) && (propertyAccess == (accessType.intValue() & propertyAccess));
	}

	private void checkFunctionPropertyType(String propertyName,
			Class< ? >[] expectedTypes) {
		Function[] functions = null;
		try {
			functions = super.getFunctions(propertyName, null);
		} catch (InvalidSyntaxException e) {
			fail(null, e);
		}
		for (int i = 0; i < functions.length; i++) {
			Class< ? > propertyType = functions[i]
					.getServiceProperty(propertyName)
					.getClass();
			assertTrue("The function proeprty type is not correct: " + propertyName + ", type: " + propertyType,
					TestUtil.contains(expectedTypes, propertyType));
		}
	}
}
