/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2000, 2001).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi (OSGI)
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

import org.osgi.framework.*;
import org.osgi.service.cm.*;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Manager of update and delete events, forwarded by ConfigurationImpl to the
 * corresponding ManagedService(Factories). As those events are asynchronous, a
 * separate thread is engaged for their execution.
 * 
 * @author OSGi Alliance
 * @version $Revision$
 */
public class CMEventManager extends Thread {
	private static Vector	eventQueue;
	private static Object	synch;
	private boolean			running;
	private static boolean	isWaiting;
	private BundleContext	bc;
	private static ServiceTracker listeners;

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
		listeners = new ServiceTracker(bc, ConfigurationListener.class.getName(), null);
		listeners.open();
	}

	/**
	 * While the event queue has elements - they are processed, i.e.
	 * ManagedService(Factories) are informed for the event.
	 * 
	 * When queue empties monitoring object is put in wait state.
	 */
	public void run() {
		while (running) {
			synchronized (synch) {
				if (eventQueue.size() == 0 && running) {
					/*
					 * if there are no events at the moment - wait until
					 * notified
					 */
					try {
						isWaiting = true;
						synch.wait();
						isWaiting = false;
					}
					catch (InterruptedException e) {
						Log.log(1, "[CM]InterruptedException in EventManager.",
								e);
					}
				}
			}
			while (eventQueue.size() > 0) {
				Object object = eventQueue.elementAt(0);
				if (object instanceof ConfigEventSnapshot) {
					ConfigEventSnapshot snapshot = (ConfigEventSnapshot) object;
					ConfigurationEvent event = snapshot.event;
					ServiceReference[] references = snapshot.references;
					for (int i = 0; references!=null && i < references.length; i++) {
						ConfigurationListener listener = (ConfigurationListener) listeners.getService(references[i]);
						if (listener != null) {
							try {
								listener.configurationEvent(event);
							}
							catch (Throwable e) {
								Log.log(1,
										"[CM]Error while calling ConfigurationListener.", e);
							}
						}
					}
				}
				else {
					CMEvent event = (CMEvent) object;
					try {
						if (event.config != null && event.config.fPid != null) {
							ManagedServiceFactory msf = (ManagedServiceFactory) bc
									.getService(event.sRef);
							if (msf != null) {
								if (event.event == CMEvent.UPDATED) {
									msf.updated(event.config.pid, event.props);
								}
								else {
									msf.deleted(event.config.pid);
								}
							}
						}
						else {
							ManagedService ms = (ManagedService) bc
									.getService(event.sRef);
							if (ms != null) {
								ms.updated(event.props);
							}
						}
						bc.ungetService(event.sRef);
					}
					catch (Throwable e) {
						if (event.config != null) {
							Log.log(1,
									"[CM]Error while calling back ManagedService[Factory]. Configuration's pid is "
											+ event.config.pid, e);
						}
					}
				}
				eventQueue.removeElementAt(0);
			}
		}
	}

	/**
	 * Add an event to the queue. The event will be forwarded to target service
	 * as soon as possible.
	 * 
	 * @param event Event holding info for update/deletion of a configuration.
	 */
	protected static void addEvent(CMEvent event) {
		eventQueue.addElement(event);
		synchronized (synch) {
			if (isWaiting) {
				synch.notify();
			}
		}
	}

	/**
	 * Add an event to the queue. The event will be forwarded to target service
	 * as soon as possible.
	 * 
	 * @param event event, holding info for update/deletion of a configuration.
	 */
	protected static void addEvent(ConfigurationEvent event) {
		eventQueue.addElement(new ConfigEventSnapshot(event, listeners.getServiceReferences()));
		synchronized (synch) {
			if (isWaiting) {
				synch.notify();
			}
		}
	}
	
	private static class ConfigEventSnapshot {
		ServiceReference[] references;
		ConfigurationEvent event;
		ConfigEventSnapshot(ConfigurationEvent event, ServiceReference[] references) {
			this.event = event;
			this.references = references;
		}
	}

	/**
	 * Stops this thread, making it getting out of method run.
	 */
	public void stopIt() {
		listeners.close();
		listeners = null;
		synchronized (synch) {
			running = false;
			synch.notify();
		}
	}
}