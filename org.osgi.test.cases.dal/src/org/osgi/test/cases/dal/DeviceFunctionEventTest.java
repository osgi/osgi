/*
 * Copyright (c) OSGi Alliance (2014). All Rights Reserved.
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

import java.math.BigDecimal;
import java.util.Dictionary;
import java.util.Hashtable;
import org.osgi.service.dal.DeviceFunctionEvent;
import org.osgi.service.dal.functions.data.LevelData;


/**
 * Validates the {@link DeviceFunctionEvent}.
 */
public final class DeviceFunctionEventTest extends AbstractDeviceTest {

	private static final String	PROP_NAME	= "test-prop";
	private static final String		FUNCTION_UID	= "test-function-uid";
	private static final LevelData	PROP_VALUE		= new LevelData(
															System.currentTimeMillis(), null, null, new BigDecimal(1));

	/**
	 * Test the device function event properties.
	 */
	public void testEventProperties() {
		Hashtable eventProps = new Hashtable();
		eventProps.put(DeviceFunctionEvent.PROPERTY_FUNCTION_PROPERTY_NAME, PROP_NAME);
		eventProps.put(DeviceFunctionEvent.PROPERTY_FUNCTION_PROPERTY_VALUE, PROP_VALUE);
		eventProps.put(DeviceFunctionEvent.PROPERTY_FUNCTION_UID, FUNCTION_UID);
		checkEventProps(new DeviceFunctionEvent(
				DeviceFunctionEvent.TOPIC_PROPERTY_CHANGED, (Dictionary) eventProps));

	}

	private void checkEventProps(DeviceFunctionEvent event) {
		assertEquals("The event function UID is not correct!", FUNCTION_UID, event.getFunctionUID());
		assertEquals("The event function property name is not correct!", PROP_NAME, event.getFunctionPropertyName());
		assertEquals("The event function property value is not correct!", PROP_VALUE, event.getFunctionPropertyValue());
	}

}
