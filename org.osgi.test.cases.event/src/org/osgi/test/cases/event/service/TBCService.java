/*
 * $Id$
 * 
 * Copyright (c) The OSGi Alliance (2004). All Rights Reserved.
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

package org.osgi.test.cases.event.service;

import java.util.Vector;

import org.osgi.service.event.Event;

/**
 * Dummy service to check exporter
 * 
 * @version $Id$
 */
public interface TBCService {

	/**
	 * Sets the array with event topics in which the event handler is
	 * interested.
	 * 
	 * @param topics the array with event topics
	 * @param intents the array with event intents
	 */
	public void setProperties(String[] topics, String[] intents);
  
  /**
   * Returns the array with all set event topics in which the event handler is interested.
   * 
   * @return the array with all set event topics
   */
  public String[] getTopics();
    
  /**
   * Returns the last received event and then the last event is set to null.
   *
   * @return last received event
   */
  public Event getLastReceivedEvent();
  
  /**
   * Returns the last received events and then elements in the vector with last events are removed.
   * @see org.osgi.test.cases.event.service.TBCService#getLastReceivedEvents()
   */
  public Vector getLastReceivedEvents();
}
