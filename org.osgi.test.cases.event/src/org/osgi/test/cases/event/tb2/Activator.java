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

package org.osgi.test.cases.event.tb2;

import java.util.Hashtable;
import java.util.Vector;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.test.cases.event.service.TBCService;

/**
 * A bundle that registers a service with the marker interface
 * TBCService so it can be checked the exporter is correct.
 *
 * @version $Id$
 */
public class Activator implements BundleActivator, TBCService, EventHandler {
  
  private BundleContext context;
  private Vector lastEvents = null;
  private ServiceRegistration serviceReg;
  private String[] topics;
  
  /**
   * Register this class as a service. Called when this bundle is started so the 
   * Framework can perform the bundle-specific activities necessary to start this bundle.
   * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
   */
  public void start(BundleContext context) throws Exception {
    this.context = context;
    context.registerService(this.getClass().getName(), this, null);
	}
  
	/**
   * Unregister this class as an event handler service. Called when this bundle is stopped 
   * so the Framework can perform the bundle-specific activities necessary to stop the bundle.
   * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
   */
  public void stop(BundleContext context) throws Exception {
    if (serviceReg != null) {
      serviceReg.unregister();
      serviceReg = null;
    }
    this.context = null;
	}
  
  /**
   * Sets the array with event topics in which the event handler is interested.
   * @see org.osgi.test.cases.event.service.TBCService#setTopics(java.lang.String[])
   */
  public void setTopics(String[] topics) {
    this.topics = topics;
    Hashtable ht = new Hashtable();
	  if (topics.length == 1) {
		  ht.put(EventConstants.EVENT_TOPIC, topics[0]);
	  }
	  else {
		  ht.put(EventConstants.EVENT_TOPIC, topics);
	  }
    if (serviceReg == null) {
      serviceReg = context.registerService(EventHandler.class.getName(), this, ht);
    } else {
      serviceReg.setProperties(ht);
    }
  }
  
  /**
   * Returns the array with all set event topics in which the event handler is interested.
   * @see org.osgi.test.cases.event.service.TBCService#getTopics()
   */
  public String[] getTopics() {
    return topics;
  }
  
  /**
   * Called by the {@link EventAdmin} service to notify the listener of an event.
   * 
   * @param event The event that occurred.
   */
  public synchronized void handleEvent(Event event) {
    if (lastEvents == null) {
      lastEvents = new Vector();
    }
    lastEvents.addElement(event);
  }
  
  /**
   * Returns the last received event and then elements in the vector with last events are removed.
   * @see org.osgi.test.cases.event.service.TBCService#getLastReceivedEvent()
   */
  public synchronized Event getLastReceivedEvent() {
    if (lastEvents == null || lastEvents.size() < 1) return null;
    Event event = (Event) lastEvents.lastElement();
    lastEvents.removeAllElements();
    return event;
  }
  
  /**
   * Returns the last received events and then elements in the vector with last events are removed.
   * @see org.osgi.test.cases.event.service.TBCService#getLastReceivedEvents()
   */
  public synchronized Vector getLastReceivedEvents() {
		if (lastEvents == null || lastEvents.size() < 1)
			return null;
    Vector events = (Vector) lastEvents.clone();
    lastEvents.removeAllElements();
    return events;
  }
}
