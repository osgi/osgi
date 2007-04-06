/*
 * $Id$
 * 
 * Copyright (c) OSGi Alliance (2000, 2004). All Rights Reserved.
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
package org.osgi.impl.service.cu.admin;

/**
 * A thread which is used to notify listeners which registered for asynchronous
 * delivery of events. <BR>
 * 
 * All three types of events (hierarchy changed, control unit and state variable
 * changed) a kept in a single list so the events are delivered in the same
 * order in which they were received.
 * 
 * @version $Revision$
 */
class NotifyListenersThread extends Thread {
  
  private static final int MAX_QUEUE_SIZE = 500;
  
  private CUListenersTracker cuListenersTracker;
  private SVListenersTracker svListenersTracker;
  private HierarchyListenersTracker hierarchyListenersTracker;
  
  private boolean isRunning;
  
  private EventsList eventsQueue;
  
  /**
   * Constructor.
   * 
   * @param cuListenersTracker control unit listeners tracker
   * @param svListenersTracker state variable listeners tracker
   * @param hierarchyListenersTracker hierarchy changed listeners tracker
   */
  NotifyListenersThread(CUListenersTracker cuListenersTracker, 
    SVListenersTracker svListenersTracker, HierarchyListenersTracker hierarchyListenersTracker) {
      
    this.cuListenersTracker = cuListenersTracker;
    this.svListenersTracker = svListenersTracker;
    this.hierarchyListenersTracker = hierarchyListenersTracker;
    
    eventsQueue = new EventsList();
  }
  
  /**
   * Start the thread.
   */
  void initiate() {
    isRunning = true;
    
    start();
  }
  
  /**
   * Stop the thread.
   */
  void finish() {
    isRunning = false;
    
    flushEvents();
  }
  
  /**
   * Add a control unit event.
   * 
   * @param eventType control unit event type
   * @param cuType type of the control unit for which the event is 
   * @param cuID ID of the control unit for which the event is
   */
  void addCUEvent(int eventType, String cuType, String cuID) {
    addEvent( new EventData(eventType, cuType, cuID) );
  }
  
  /**
   * Add hierarchy changed event.
   * 
   * @param eventType event type
   * @param cuType type of the control unit for which the event is
   * @param cuID ID of the control unit for which the event is
   * @param parentType type of the parent control unit for which the event is
   * @param parentID ID of the parent control unit for which the event is
   */
  void addHierachyEvent(int eventType, String cuType, String cuID, String parentType, String parentID) {
    addEvent( new EventData(eventType, cuType, cuID, parentType, parentID) );
  }
  
  /**
   * Add state variable changed event.
   * 
   * @param cuType type of the control unit for which the event is
   * @param cuID ID of the control unit for which the event is
   * @param varID id of the state variable which has changed
   * @param value new value of the variable
   */
  void addSVEvent(String cuType, String cuID, String varID, Object value) {
    addEvent( new EventData(cuType, cuID, varID, value) );
  }
  
  /* (non-Javadoc)
   * @see java.lang.Runnable#run()
   */
  public void run() {
    while (isRunning) {
      
      synchronized (eventsQueue) {
        if ( eventsQueue.isEmpty() ) {
          eventsQueue.notifyAll(); // space freed in the queue, tell whoever is waiting to add event        
          waitOnQueue(); // all events send, wait for event to be added
        }
      } // synchronized
        
      sendEvent(); 
    } // while
  }
  
  private void waitOnQueue() {
    try {
      eventsQueue.wait();
    } catch (InterruptedException ex) {}
  }
  
  private void addEvent(EventData eventData) {
    synchronized (eventsQueue) {
      if (eventsQueue.getSize() > MAX_QUEUE_SIZE) {
        waitOnQueue(); // wait for some room to be freed in the queue
      }
      
      eventsQueue.addLast(eventData);
      
      eventsQueue.notifyAll(); // notify that new event is available in the queue
    }
  }
  
  private void flushEvents() {
    synchronized (eventsQueue) {
      while ( !eventsQueue.isEmpty() ) {
        sendEvent();
      }
    }
  }
  
  private void sendEvent() {
    EventData eventData = null;
    synchronized (eventsQueue) {
      if ( eventsQueue.isEmpty() ) {
        return;
      }
      
      eventData = eventsQueue.removeFirst();
    }
    
    switch ( eventData.getType() ) {
      case EventData.SV_EVENT : { 
        svListenersTracker.notifyAsyncListeners(eventData);
        break;
      }
      
      case EventData.CU_EVENT : {
        cuListenersTracker.notifyAsyncListeners(eventData);
        break;
      }
      
      case EventData.HIERARCHY_EVENT : {
        hierarchyListenersTracker.notifyAsyncListeners(eventData);
        break;
      }
    }
  }
  
}