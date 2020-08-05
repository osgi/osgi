/*
 * Copyright (c) 2014 ProSyst Software GmbH. All Rights Reserved.
 *
 * This CODE is owned by ProSyst Software GmbH,
 * and is being distributed to OSGi PARTICIPANTS as MATERIALS
 * under the terms of section 1 of the OSGi Alliance Inc. Intellectual Property Rights Policy,
 * Amended and Restated as of May 23, 2011.
 */

package org.osgi.test.cases.dal;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.service.dal.FunctionEvent;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * Validates the {@link FunctionEvent}.
 */
public final class FunctionEventTest extends DefaultTestBundleControl {

	private static final String				PROP_NAME		= "test-prop";
	private static final String				FUNCTION_UID	= "test-function-uid";
	private static final TestFunctionData	PROP_VALUE		= new TestFunctionData(System.currentTimeMillis(), null);

	/**
	 * Test the function event properties.
	 */
	public void testEventProperties() {
		Dictionary<String,Object> eventProps = new Hashtable<>();
		eventProps.put(FunctionEvent.PROPERTY_NAME, PROP_NAME);
		eventProps.put(FunctionEvent.PROPERTY_VALUE, PROP_VALUE);
		eventProps.put(FunctionEvent.FUNCTION_UID, FUNCTION_UID);
		checkEventProps(new FunctionEvent(
				FunctionEvent.TOPIC_PROPERTY_CHANGED, eventProps));

	}

	private void checkEventProps(FunctionEvent event) {
		assertEquals("The event function UID is not correct!", FUNCTION_UID, event.getFunctionUID());
		assertEquals("The event function property name is not correct!", PROP_NAME, event.getFunctionPropertyName());
		assertEquals("The event function property value is not correct!", PROP_VALUE, event.getFunctionPropertyValue());
	}
}
