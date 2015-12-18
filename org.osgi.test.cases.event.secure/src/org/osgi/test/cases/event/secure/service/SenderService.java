/*
 * Copyright (c) OSGi Alliance (2004, 2015). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.test.cases.event.secure.service;

import org.osgi.service.event.Event;

/**
 * Service to send events.
 * 
 * @author $Id$
 */
public interface SenderService {

	/**
	 * Initiate asynchronous, ordered delivery of an event. This method returns
	 * to the caller before delivery of the event is completed. Events are
	 * delivered in the order that they are received by this method.
	 * 
	 * @param event The event to send to all listeners which subscribe to the
	 * topic of the event.
	 * 
	 * @throws SecurityException If the caller does not have {@code
	 * TopicPermission[topic,PUBLISH]} for the topic specified in the event.
	 */
	void postEvent(Event event);

	/**
	 * Initiate synchronous delivery of an event. This method does not return to
	 * the caller until delivery of the event is completed.
	 * 
	 * @param event The event to send to all listeners which subscribe to the
	 * topic of the event.
	 * 
	 * @throws SecurityException If the caller does not have {@code
	 * TopicPermission[topic,PUBLISH]} for the topic specified in the event.
	 */
	void sendEvent(Event event);

}
