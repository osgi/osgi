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

package org.osgi.service.zigbee;

import org.osgi.service.zigbee.descriptions.ZigBeeDataTypeDescription;

/**
 * This interface represents a ZigBee Attribute
 * 
 * @version 1.0
 */
public interface ZigBeeAttribute {

	/**
	 * Property key for the optional attribute id. A ZigBee Event Listener
	 * service can announce for what ZigBee attributes it wants notifications.
	 */
	public final static String	ID					= "zigbee.attribute.id";

	/**
	 * Property key for the optional minimum interval, in seconds between
	 * issuing reports of the attribute A ZigBee Event Listener service can
	 * declare the minimum frequency at which events it wants notifications.
	 */
	public final static String	MIN_REPORT_INTERVAL	= "zigbee.attribute.min.report.interval";

	/**
	 * Property key for the optional maximum interval, in seconds between
	 * issuing reports of the attribute A ZigBee Event Listener service can
	 * declare the maximum frequency at which events it wants notifications.
	 */
	public final static String	MAX_REPORT_INTERVAL	= "zigbee.attribute.max.report.interval";

	/**
	 * Property key for the optional maximum change to the attribute that will
	 * result in a report being issued. A ZigBee Event Listener service can
	 * declare the maximum frequency at which events it wants notifications.
	 */
	public final static String	REPORTABLE_CHANGE	= "zigbee.attribute.reportable.change";

	/**
	 * Property key for the optional maximum expected time, in seconds, between
	 * received reports for the attribute. A ZigBee Event Listener service can
	 * declare the maximum frequency at which events it wants notifications.
	 */
	public final static String	TIMEOUT_PERIOD		= "zigbee.attribute.timeout.period";

	/**
	 * @return the attribute identifier
	 */
	public int getId();

	/**
	 * Gets the current value of the attribute. This method is used when the
	 * attribute data type is not provided.
	 * 
	 * @param handler the handler
	 * @throws ZigBeeException
	 */
	public void getValue(ZigBeeAttributesHandler handler) throws ZigBeeException;

	/**
	 * Sets the current value of the attribute.
	 * 
	 * @param value the value to set
	 * @param handler the handler
	 * @throws ZigBeeException
	 */
	public void setValue(Object value, ZigBeeAttributesHandler handler) throws ZigBeeException;

	/**
	 * @return the Attribute data type
	 */
	public ZigBeeDataTypeDescription getDataType();

}
