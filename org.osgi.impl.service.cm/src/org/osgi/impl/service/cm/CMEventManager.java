/*
 * $Header$
 *
 * Copyright (c) The Open Services Gateway Initiative (2000, 2001).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the Open Services Gateway Initiative (OSGI)
 * Specification may be subject to third party intellectual property rights,
 * including without limitation, patent rights (such a third party may or may
 * not be a member of OSGi). OSGi is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS IS"
 * basis and OSGI DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL NOT
 * INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR FITNESS
 * FOR A PARTICULAR PURPOSE. IN NO EVENT WILL OSGI BE LIABLE FOR ANY LOSS OF
 * PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF BUSINESS,
 * OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL, PUNITIVE OR
 * CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS DOCUMENT OR THE
 * INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.impl.service.cm;

import java.util.Vector;
import java.util.Dictionary;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import org.osgi.service.cm.ManagedService;
import org.osgi.service.cm.ManagedServiceFactory;


/**
 * Manager of update and delete events, forwarded by ConfigurationImpl 
 * to the corresponding ManagedService(Factories). As those 
 * events are asynchronous, a separate thread is engaged for their 
 * execution.
 *
 * @author Open Services Gateway Initiative
 * @version $Revision$
 */
public class CMEventManager extends Thread {
  private static Vector eventQueue;
  private static Object synch;
  private boolean running;
  private static boolean isWaiting;
  private BundleContext bc;

  /**
   * Constructs the CMEventManager.
   */
  public CMEventManager(BundleContext bc) {
    super("CMEvent Manager");
    this.bc = bc;
    eventQueue = new Vector();
    synch = new Object();
    running = true;
    isWaiting = false;
  }
  

  /**
   * While the event queue has elements - they are processed, i.e. 
   * ManagedService(Factories) are informed for the event.
   * 
   * When queue empties monitoring object is put in wait state.
   */
  public void run() {
    CMEvent event;
    while (running) {
      synchronized (synch) {
        if (eventQueue.size() == 0 && running) {
          /* 
            if there are no events at the moment - wait until notified 
          */
          try {
            isWaiting = true;
            synch.wait();
            isWaiting = false;
          } catch (InterruptedException e) {
            Log.log(1, "[CM]InterruptedException in EventManager.", e);
          }
        }
      }      
      while (eventQueue.size() > 0) {
        event = (CMEvent) eventQueue.elementAt(0);
        try {
          if (event.config != null && event.config.fPid != null) {
            ManagedServiceFactory msf = (ManagedServiceFactory) 
              bc.getService(event.sRef);
            if (msf != null) {
              if (event.event == event.UPDATED) {                
                msf.updated(event.config.pid, event.props);
              } else {
                msf.deleted(event.config.pid);
              }
            }
          } else {
            ManagedService ms = (ManagedService) bc.getService(event.sRef);
            if (ms != null) {
              ms.updated(event.props);
            }
          }
          bc.ungetService(event.sRef);
        } catch (Throwable e) {
          if (event.config != null) {
            Log.log(1,"[CM]Error while calling back ManagedService[Factory]. Configuration's pid is " + event.config.pid, e);
          }
        }
        eventQueue.removeElementAt(0);
      }
    }
  }
  

  /**
   * Add an event to the queue. The event will be forwarded
   * to target service as soon as possible.
   *
   * @param   upEv  event, holding info for update/deletion of a configuration.
   */
  protected static void addEvent(CMEvent upEv) {
    eventQueue.addElement(upEv);
    synchronized (synch) {
      if (isWaiting) {
        synch.notify();
      }
    }
  }
  

  /**
   * Stops this thread, making it getting
   * out of method run.
   */
  public void stopIt() {
    synchronized (synch) {
      running = false;
      synch.notify();
    }
  }
}