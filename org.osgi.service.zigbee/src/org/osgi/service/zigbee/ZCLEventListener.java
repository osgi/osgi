/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
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
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.service.zigbee;

/**
 * This interface represents a listener to events from ZigBee Device nodes.
 * 
 * @author $Id$
 */
public interface ZCLEventListener {

	/**
	 * Property key for the optional attribute data type of an attribute
	 * reporting configuration record, cf. ZCL Figure 2.16 Format of the
	 * Attribute Reporting Configuration Record.
	 */
	public final static String	ATTRIBUTE_DATA_TYPE	= "zigbee.attribute.datatype";

	/**
	 * Property key for the optional minimum interval, in seconds between
	 * issuing reports of the attribute. A ZigBee Event Listener service can
	 * declare the minimum frequency at which events it wants notifications.
	 */
	public final static String	MIN_REPORT_INTERVAL	= "zigbee.attribute.min.report.interval";

	/**
	 * Property key for the optional maximum interval, in seconds between
	 * issuing reports of the attribute. A ZigBee Event Listener service can
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
	 * Notifies the reception of an event. This method is called asynchronously.
	 * 
	 * @param event a set of events.
	 */
	public void notifyEvent(ZigBeeEvent event);

	/**
	 * Notifies that a failure has occurred.
	 * <p>
	 * That is, when either a {@link ZCLException#UNSUPPORTED_ATTRIBUTE},
	 * {@link ZCLException#UNREPORTABLE_TYPE},
	 * {@link ZCLException#INVALID_VALUE}, or
	 * {@link ZCLException#INVALID_DATA_TYPE} status occurs.
	 * 
	 * @param e the ZCLException.
	 */
	public void onFailure(ZCLException e);

	/**
	 * Notifies that the timeout is elapsed. No event will be received in the
	 * interval.
	 * 
	 * @param timeout the timeout in seconds.
	 */
	public void notifyTimeOut(int timeout);

}
