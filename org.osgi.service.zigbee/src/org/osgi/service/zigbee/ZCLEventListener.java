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

/**
 * This interface represents a listener to events from ZigBee Device nodes
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
	 * Callback method that is invoked for received events. This method must be
	 * called asynchronously.
	 * 
	 * @param event a set of events
	 */
	public void notifyEvent(ZigBeeEvent event);

	/**
	 * Notifies a failure, i.e. when either a
	 * ZCLException.UNSUPPORTED_ATTRIBUTE, or a
	 * ZCLException.UNREPORTABLE_ATTRIBUTE, or ZCLException.INVALID_VALUE, or
	 * ZCLException.INVALID_DATA_TYPE status occurs.
	 * 
	 * @param e the ZCLException.
	 */
	public void onFailure(ZCLException e);

	/**
	 * TIMEOUT_PERIOD is sent from the attribute owner to the listening client
	 * to say that the interval between reports may exceed MAX_INTERVAL.
	 * 
	 * @param timeout in seconds
	 */
	public void notifyTimeOut(int timeout);

}
