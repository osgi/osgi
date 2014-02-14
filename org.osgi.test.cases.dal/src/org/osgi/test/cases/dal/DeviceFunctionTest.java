/*
 * Copyright (c) OSGi Alliance (2013). All Rights Reserved.
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
import org.osgi.service.dal.DeviceFunction;
import org.osgi.service.dal.DeviceFunctionData;
import org.osgi.service.dal.DeviceFunctionEvent;
import org.osgi.service.dal.PropertyMetadata;
import org.osgi.service.dal.functions.BooleanControl;
import org.osgi.service.dal.functions.data.BooleanData;
import org.osgi.test.cases.step.TestStep;

/**
 * Test class validates the device function.
 */
public class DeviceFunctionTest extends AbstractDeviceTest {

	/**
	 * The device function must be registered under only one interface, the
	 * function interface. The test method checks that rule.
	 */
	public void testRegistrationClasses() {
		DeviceFunction[] deviceFunctions = null;
		try {
			deviceFunctions = super.getDeviceFunctions(null, DeviceFunction.SERVICE_UID, null);
		} catch (InvalidSyntaxException e) {
			// not possible
			fail(null, e);
		}
		for (int i = 0; i < deviceFunctions.length; i++) {
			String[] regClasses = (String[]) deviceFunctions[i].getServiceProperty(Constants.OBJECTCLASS);
			assertEquals("Only one registration class is allowed!", 1, regClasses.length);
		}
	}

	/**
	 * Initially, the device function services are registered first. The test
	 * method checks that rule.
	 * 
	 * @throws InvalidSyntaxException If the registered device UID breaks the
	 *         LDAP filter.
	 */
	public void testRegistrationOrder() throws InvalidSyntaxException {
		TestStep testStep = super.getTestStep();
		String deviceID = testStep.execute(Commands.REGISTER_DEVICE,
				new String[] {BooleanControl.class.getName()})[0];
		long deviceServiceID = ((Long) super.getDevice(deviceID).getServiceProperty(Constants.SERVICE_ID)).longValue();
		DeviceFunction[] deviceFunctions = getDeviceFunctions(
				null, DeviceFunction.SERVICE_DEVICE_UID, deviceID);
		assertEquals("Only one device function must be supported!", 1, deviceFunctions.length);
		assertTrue("Boolean control must be supported.", deviceFunctions[0] instanceof BooleanControl);
		long functionServiceID = ((Long) deviceFunctions[0].getServiceProperty(Constants.SERVICE_ID)).longValue();
		assertTrue("The device function must be registered before the device!", functionServiceID < deviceServiceID);
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
		String deviceID = testStep.execute(Commands.REGISTER_DEVICE,
				new String[] {BooleanControl.class.getName()})[0];
		Device device = super.getDevice(deviceID);
		DeviceFunction[] deviceFunctions = getDeviceFunctions(
				null, DeviceFunction.SERVICE_DEVICE_UID, deviceID);
		assertEquals("Only one device function must be supported!", 1, deviceFunctions.length);
		super.deviceServiceListener.clear();
		device.remove();
		ServiceEvent[] deviceServiceEvents = super.deviceServiceListener.getEvents();
		assertTrue("There are no service event on device remove.", deviceServiceEvents.length > 0);
		boolean isDeviceUnregistered = false;
		boolean isDeviceFunctionUnregistered = false;
		for (int i = 0; i < deviceServiceEvents.length; i++) {
			if (ServiceEvent.UNREGISTERING != deviceServiceEvents[i].getType()) {
				continue;
			}
			if (deviceID.equals(deviceServiceEvents[i].getServiceReference().getProperty(Device.SERVICE_UID))) {
				assertFalse("The is already unregistered!", isDeviceUnregistered);
				isDeviceUnregistered = true;
			} else
				if (deviceFunctions[0].getServiceProperty(DeviceFunction.SERVICE_UID).equals(
						deviceServiceEvents[i].getServiceReference().getProperty(DeviceFunction.SERVICE_UID))) {
					assertTrue("The device must be unregistered first!", isDeviceUnregistered);
					assertFalse("The device function is already unregistered!", isDeviceFunctionUnregistered);
					isDeviceFunctionUnregistered = true;
			}
		}
	}

	/**
	 * Checks that all device functions support all required properties.
	 */
	public void testRequiredFunctionProperties() {
		ServiceReference[] functionSRefs = getDeviceFunctionSRefs();
		for (int i = 0; i < functionSRefs.length; i++) {
			super.checkRequiredProperties(
					functionSRefs[i],
					new String[] {DeviceFunction.SERVICE_UID});
		}
	}

	/**
	 * Checks that {@link DeviceFunction#getServiceProperty(String)} returns the
	 * same value as {@link ServiceReference#getProperty(String)}.
	 */
	public void testFunctionProperties() {
		ServiceReference[] functionSRefs = getDeviceFunctionSRefs();
		boolean compared = false;
		for (int i = 0; i < functionSRefs.length; i++) {
			DeviceFunction function = (DeviceFunction) super.getContext().getService(functionSRefs[i]);
			if (null == function) {
				continue;
			}
			try {
				String[] refKeys = functionSRefs[i].getPropertyKeys();
				for (int ii = 0; ii < refKeys.length; ii++) {
					assertTrue(
							"The device function property and service property values are different.",
							TestUtil.areEqual(functionSRefs[i].getProperty(refKeys[ii]), function.getServiceProperty(refKeys[ii])));
				}
				compared = true;
			} catch (UnsupportedOperationException uoe) {
				// expected
			}
		}
		assertTrue("No device function with with property access.", compared);
	}

	/**
	 * Checks that device function property value type is correct.
	 */
	public void testFunctionPropertyTypes() {
		checkDeviceFunctionPropertyType(DeviceFunction.SERVICE_DESCRIPTION, new Class[] {String.class});
		checkDeviceFunctionPropertyType(DeviceFunction.SERVICE_DEVICE_UID, new Class[] {String.class});
		checkDeviceFunctionPropertyType(DeviceFunction.SERVICE_OPERATION_NAMES, new Class[] {String[].class});
		checkDeviceFunctionPropertyType(DeviceFunction.SERVICE_PROPERTY_NAMES, new Class[] {String[].class});
		checkDeviceFunctionPropertyType(DeviceFunction.SERVICE_REFERENCE_UIDS, new Class[] {String[].class});
		checkDeviceFunctionPropertyType(DeviceFunction.SERVICE_TYPE, new Class[] {String.class});
		checkDeviceFunctionPropertyType(DeviceFunction.SERVICE_UID, new Class[] {String.class});
		checkDeviceFunctionPropertyType(DeviceFunction.SERVICE_VERSION, new Class[] {String.class});
	}

	/**
	 * Checks that property getter is available and accessible.
	 * 
	 * @throws NoSuchMethodException If the getter is missing.
	 * @throws ClassNotFoundException If the device function class cannot be
	 *         find.
	 */
	public void testPropertyGetter() throws NoSuchMethodException, ClassNotFoundException {
		DeviceFunction[] deviceFunctions = getDeviceFunctions(
				null, PropertyMetadata.PROPERTY_ACCESS_READABLE);
		for (int i = 0; i < deviceFunctions.length; i++) {
			String[] propertyNames = (String[]) deviceFunctions[i].getServiceProperty(DeviceFunction.SERVICE_PROPERTY_NAMES);
			for (int ii = 0; ii < propertyNames.length; ii++) {
				if (isPropertyAccessValid(deviceFunctions[i], propertyNames[ii], PropertyMetadata.PROPERTY_ACCESS_READABLE)) {
					checkPropertyGetter(deviceFunctions[i], propertyNames[ii]);
				}
			}
		}
	}

	/**
	 * Checks that property setter is available and accessible.
	 * 
	 * @throws NoSuchMethodException If the getter is missing.
	 * @throws ClassNotFoundException If the device function class cannot be
	 *         find.
	 */
	public void testPropertySetters() throws NoSuchMethodException, ClassNotFoundException {
		DeviceFunction[] deviceFunctions = getDeviceFunctions(
				null, PropertyMetadata.PROPERTY_ACCESS_WRITABLE);
		for (int i = 0; i < deviceFunctions.length; i++) {
			String[] propertyNames = (String[]) deviceFunctions[i].getServiceProperty(DeviceFunction.SERVICE_PROPERTY_NAMES);
			for (int ii = 0; ii < propertyNames.length; ii++) {
				if (isPropertyAccessValid(deviceFunctions[i], propertyNames[ii], PropertyMetadata.PROPERTY_ACCESS_READABLE)) {
					checkPropertySetter(deviceFunctions[i], propertyNames[ii]);
				}
			}
		}
	}

	/**
	 * Check the device function events.
	 * 
	 * @throws InvalidSyntaxException If the registered device UID can break
	 *         LDAP filter.
	 * @throws UnsupportedOperationException If {@link BooleanControl#setTrue()}
	 *         is not supported.
	 * @throws IllegalStateException If the device function service is
	 *         unregistered.
	 * @throws DeviceException If an error is available while executing the
	 *         operation.
	 */
	public void testPropertyEvents() throws InvalidSyntaxException, UnsupportedOperationException, IllegalStateException, DeviceException {
		DeviceFunction[] deviceFunctions = getDeviceFunctions(
				BooleanControl.class.getName(), PropertyMetadata.PROPERTY_ACCESS_EVENTABLE);
		BooleanControl booleanControl = (BooleanControl) deviceFunctions[0];
		final String functionUID = (String) booleanControl.getServiceProperty(DeviceFunction.SERVICE_UID);
		DeviceFunctionEventHandler eventHandler = new DeviceFunctionEventHandler(super.getContext());
		eventHandler.register(functionUID);
		DeviceFunctionEvent functionEvent;
		try {
			booleanControl.setTrue();
			functionEvent = eventHandler.getEvents(1)[0];
		} finally {
			eventHandler.unregister();
		}
		BooleanData propertyData = (BooleanData) functionEvent.getFunctionPropertyValue();
		super.assertEquals(true, propertyData);
		assertEquals(
				"The event function identifier is not correct!",
				functionUID,
				functionEvent.getFunctionUID());
		assertEquals(
				"The property name is not correct!",
				BooleanControl.PROPERTY_DATA,
				functionEvent.getFunctionPropertyName());
	}

	/**
	 * Tests that there is no operation overloading.
	 * 
	 * @throws ClassNotFoundException If the device function class cannot be
	 *         loaded.
	 */
	public void testOperations() throws ClassNotFoundException {
		DeviceFunction[] deviceFunctions = null;
		try {
			deviceFunctions = super.getDeviceFunctions(null, DeviceFunction.SERVICE_OPERATION_NAMES, null);
		} catch (InvalidSyntaxException e) {
			fail(null, e);
		}
		for (int i = 0; i < deviceFunctions.length; i++) {
			String[] operationNames = (String[]) deviceFunctions[i].getServiceProperty(DeviceFunction.SERVICE_OPERATION_NAMES);
			for (int ii = 0; ii < operationNames.length; ii++) {
				Class functionClass = TestUtil.getDeviceFunctionClass(
						deviceFunctions[i], super.getContext());
				Method[] methods = TestUtil.getMethods(functionClass, operationNames[ii]);
				assertNotNull("The is no method for operation: " + operationNames[ii]);
				assertEquals("There is operation overloafing for: " + operationNames[ii], 1, methods.length);
			}
		}
	}

	private void checkPropertySetter(DeviceFunction deviceFunction, String propertyName) throws NoSuchMethodException, ClassNotFoundException {
		final String setterName = TestUtil.getBeanAccessor(propertyName, "set");
		final Class functionClass = TestUtil.getDeviceFunctionClass(deviceFunction, super.getContext());
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

	private void checkPropertyGetter(DeviceFunction deviceFunction, String propertyName) throws NoSuchMethodException, ClassNotFoundException {
		final String getterName = TestUtil.getBeanAccessor(propertyName, "get");
		final Class functionClass = TestUtil.getDeviceFunctionClass(deviceFunction, super.getContext());
		Method getter = functionClass.getMethod(getterName, null);
		Class returnType = getter.getReturnType();
		assertNotNull("The device function getter must have return type: " + getterName, returnType);
		assertTrue(
				"The device function getter must return a subclass of " + DeviceFunctionData.class.getName(),
				DeviceFunctionData.class.isAssignableFrom(returnType));
	}

	private ServiceReference[] getDeviceFunctionSRefs() {
		try {
			ServiceReference[] functionSRefs = super.getContext().getServiceReferences(
					null, '(' + DeviceFunction.SERVICE_UID + "=*)");
			assertNotNull("There are no device functions.", functionSRefs);
			return functionSRefs;
		} catch (InvalidSyntaxException e) {
			// null is valid filter
		}
		return null;
	}

	private DeviceFunction[] getDeviceFunctions(String functionClass, int propertyAccess) {
		try {
			ServiceReference[] functionSRefs = super.getContext().getServiceReferences(
					functionClass, '(' + DeviceFunction.SERVICE_PROPERTY_NAMES + "=*)");
			assertNotNull("There are no device functions.", functionSRefs);
			List result = new ArrayList(functionSRefs.length);
			for (int i = 0; i < functionSRefs.length; i++) {
				final DeviceFunction deviceFunction = (DeviceFunction) super.getContext().getService(functionSRefs[i]);
				if (null == deviceFunction) {
					continue;
				}
				String[] propertyNames = (String[]) deviceFunction.getServiceProperty(
						DeviceFunction.SERVICE_PROPERTY_NAMES);
				for (int ii = 0; ii < propertyNames.length; ii++) {
					if (isPropertyAccessValid(deviceFunction, propertyNames[ii], propertyAccess)) {
						result.add(deviceFunction);
						break;
					}
				}
			}
			assertFalse(
					"There is no device function, which contains a proeprty with an access: " + propertyAccess,
					result.isEmpty());
			return (DeviceFunction[]) result.toArray(new DeviceFunction[result.size()]);
		} catch (InvalidSyntaxException e) {
			// the filter is valid
		}
		return null;
	}

	private static boolean isPropertyAccessValid(
			DeviceFunction deviceFunction, String propertyName, int propertyAccess) {
		final PropertyMetadata propertyMetadata = deviceFunction.getPropertyMetadata(propertyName);
		if (null == propertyMetadata) {
			return false;
		}
		final Map metadata = propertyMetadata.getMetadata(null);
		if (null == metadata) {
			return false;
		}
		Integer accessType = (Integer) metadata.get(PropertyMetadata.PROPERTY_ACCESS);
		return (null != accessType) && (propertyAccess == (accessType.intValue() & propertyAccess));
	}

	private void checkDeviceFunctionPropertyType(String propertyName, Class[] expectedTypes) {
		DeviceFunction[] functions = null;
		try {
			functions = super.getDeviceFunctions(null, propertyName, null);
		} catch (InvalidSyntaxException e) {
			fail(null, e);
		}
		for (int i = 0; i < functions.length; i++) {
			Class propertyType = functions[i].getServiceProperty(propertyName).getClass();
			assertTrue("The device function proeprty type is not correct: " + propertyName + ", type: " + propertyType,
					TestUtil.contains(expectedTypes, propertyType));
		}
	}

}
