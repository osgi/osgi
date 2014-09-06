/*
 * Copyright (c) 2014 ProSyst Software GmbH. All Rights Reserved.
 *
 * This CODE is owned by ProSyst Software GmbH,
 * and is being distributed to OSGi PARTICIPANTS as MATERIALS
 * under the terms of section 1 of the OSGi Alliance Inc. Intellectual Property Rights Policy,
 * Amended and Restated as of May 23, 2011.
 */

package org.osgi.test.cases.dal.functions;

import org.osgi.service.dal.DeviceException;
import org.osgi.service.dal.Function;
import org.osgi.service.dal.FunctionEvent;
import org.osgi.service.dal.PropertyMetadata;
import org.osgi.service.dal.functions.BooleanControl;
import org.osgi.service.dal.functions.data.BooleanData;

/**
 * Validates the {@code BooleanControl} functions.
 */
public final class BooleanControlTest extends AbstractFunctionTest {

	/**
	 * Checks {@link BooleanControl#setTrue()} operation functionality.
	 * 
	 * @throws IllegalStateException If the function is removed.
	 * @throws DeviceException If operation error is available.
	 */
	public void testSetTrue() throws IllegalStateException, DeviceException {
		Function[] booleanControls = super.getFunctions(BooleanControl.class.getName());
		boolean check = false;
		for (int i = 0; i < booleanControls.length; i++) {
			final BooleanControl currentBooleanControl = (BooleanControl) booleanControls[i];
			try {
				currentBooleanControl.setTrue();
				check = true;
				super.assertEquals(true, currentBooleanControl.getData());
			} catch (UnsupportedOperationException uoe) {
				// expected, go ahead
			}
		}
		assertTrue("At least one Boolean Control must support setTrue operation.", check);
	}

	/**
	 * Checks {@link BooleanControl#setFalse()} operation functionality.
	 * 
	 * @throws IllegalStateException If the function is removed.
	 * @throws DeviceException If operation error is available.
	 */
	public void testSetFalse() throws IllegalStateException, DeviceException {
		Function[] booleanControls = super.getFunctions(BooleanControl.class.getName());
		boolean check = false;
		for (int i = 0; i < booleanControls.length; i++) {
			final BooleanControl currentBooleanControl = (BooleanControl) booleanControls[i];
			try {
				currentBooleanControl.setFalse();
				check = true;
				super.assertEquals(false, currentBooleanControl.getData());
			} catch (UnsupportedOperationException uoe) {
				//expected, go ahead
			}
		}
		assertTrue("At least one Boolean Control must support setFalse operation.", check);
	}

	/**
	 * Checks {@link BooleanControl#reverse()} operation functionality.
	 * 
	 * @throws IllegalStateException If the function is removed.
	 * @throws DeviceException If operation error is available.
	 */
	public void testReverse() throws IllegalStateException, DeviceException {
		Function[] booleanControls = super.getFunctions(BooleanControl.class.getName());
		boolean check = false;
		for (int i = 0; i < booleanControls.length; i++) {
			final BooleanControl currentBooleanControl = (BooleanControl) booleanControls[i];
			BooleanData currentData = currentBooleanControl.getData();
			try {
				currentBooleanControl.reverse();
				check = true;
				super.assertEquals(currentData.getValue() ? false : true, currentBooleanControl.getData());
			} catch (UnsupportedOperationException uoe) {
				// expected, go ahead
			}
		}
		assertTrue("At least one Boolean Control must support reverse operation.", check);
	}

	/**
	 * Checks {@code BooleanControl} function events.
	 * 
	 * @throws UnsupportedOperationException If {@link BooleanControl#setTrue()}
	 *         is not supported.
	 * @throws IllegalStateException If the function service is unregistered.
	 * @throws DeviceException If an error is available while executing the
	 *         operation.
	 */
	public void testPropertyEvent() throws UnsupportedOperationException, IllegalStateException, DeviceException {
		final Function[] functions = getFunctions(
				BooleanControl.class.getName(), PropertyMetadata.ACCESS_EVENTABLE);
		final BooleanControl booleanControl = (BooleanControl) functions[0];
		final String functionUID = (String) booleanControl.getServiceProperty(Function.SERVICE_UID);
		final FunctionEventHandler eventHandler = new FunctionEventHandler(super.getContext());
		eventHandler.register(functionUID, BooleanControl.PROPERTY_DATA);
		final FunctionEvent functionEvent;
		final BooleanData currentData = booleanControl.getData();
		try {
			booleanControl.reverse();
			functionEvent = eventHandler.getEvents(1)[0];
		} finally {
			eventHandler.unregister();
		}
		BooleanData propertyData = (BooleanData) functionEvent.getFunctionPropertyValue();
		super.assertEquals(!currentData.getValue(), propertyData);
		assertEquals(
				"The event function identifier is not correct!",
				functionUID,
				functionEvent.getFunctionUID());
		assertEquals(
				"The property name is not correct!",
				BooleanControl.PROPERTY_DATA,
				functionEvent.getFunctionPropertyName());
	}
}
