/*
 * $Id$
 *
 * OSGi EventQueue for UserAdmin adapted from LogService.
 *

 *
 * (C) Copyright IBM Corporation 2000-2001.
 *
 * This source code is licensed to OSGi as MEMBER LICENSED MATERIALS
 * under the terms of Section 3.2 of the OSGi MEMBER AGREEMENT.
 */
package org.osgi.impl.service.useradmin;

import java.util.Vector;
import org.osgi.service.useradmin.*;

/**
 * The class manages the event queue to asynchronously deliver UserAdminEvents
 * to UserAdminListeners.
 * 
 * @author BJ Hargrave (hargrave@us.ibm.com)
 * @author Benjamin Reed (breed@almaden.ibm.com)
 * @version $Id$
 */
public class EventQueue extends Thread {
	/** event queue */
	protected Vector			queue;
	/** if false the thread must terminate */
	private volatile boolean	running;

	/**
	 * Constructor for the event queue. The queue is created empty and the
	 * thread is started.
	 *  
	 */
	EventQueue() {
		running = true;
		queue = new Vector(10, 10);
		setDaemon(true); /* Mark thread as daemon thread */
		start(); /* Start thread */
	}

	/**
	 * Destroy thread.
	 */
	void close() {
		running = false;
		interrupt();
	}

	/**
	 * 
	 * This method is the event queue dispatcher thread. It pulls events from
	 * the queue and dispatches them.
	 *  
	 */
	public void run() {
		while (running) {
			try {
				Object[] item = getNextEvent();
				deliverEvent((Object[]) item[0], (UserAdminEvent) item[1]);
			}
			catch (Throwable t) {
			}
		}
	}

	/**
	 * This method is called to publish an Event
	 *  
	 */
	synchronized void publishEvent(Object listeners, UserAdminEvent event) {
		Object[] item = new Object[] {listeners, event};
		queue.addElement(item);
		notify();
	}

	/**
	 * This method waits until an item is in the queue and then removes it from
	 * the queue and returns it.
	 */
	protected synchronized Object[] getNextEvent() throws InterruptedException {
		while (running && queue.isEmpty()) {
			try {
				wait();
			}
			catch (InterruptedException e) {
			}
		}
		if (!running) /* if we are stopping */
		{
			throw new InterruptedException(); /* throw an exception */
		}
		Object[] item = (Object[]) queue.elementAt(0);
		queue.removeElementAt(0);
		return (item);
	}

	/**
	 * Deliver the logentry to the list of listeners.
	 */
	private void deliverEvent(Object listeners[], UserAdminEvent event) {
		if (listeners == null)
			return;
		for (int i = 0; i < listeners.length; i++) {
			try {
				((UserAdminListener) listeners[i]).roleChanged(event);
			}
			catch (Throwable t) { // Bad Bad Listener }
			}
		}
	}
}
