/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.framework.eventmgr;

/**
 * This package private class is used for asynchronously dispatching events.
 */

class EventThread extends Thread {
	/**
	 * Queued is a nested top-level (non-member) class. This class
	 * represents the items which are placed on the asynch dispatch queue.
	 * This class is private.
	 */
	private static class Queued {
		/** listener list for this event */
		final ListElement[] listeners;
		/** dispatcher of this event */
		final EventDispatcher dispatcher;
		/** action for this event */
		final int action;
		/** object for this event */
		final Object object;
		/** next item in event queue */
		Queued next;

		/**
		 * Constructor for event queue item
		 *
		 * @param l Listener list for this event
		 * @param d Dispatcher for this event
		 * @param a Action for this event
		 * @param o Object for this event
		 */
		Queued(ListElement[] l, EventDispatcher d, int a, Object o) {
			listeners = l;
			dispatcher = d;
			action = a;
			object = o;
			next = null;
		}
	}

	/** item at the head of the event queue */
	private Queued head;
	/** item at the tail of the event queue */
	private Queued tail;
	/** if false the thread must terminate */
	private volatile boolean running;

	/**
	 * Constructor for the event thread. 
	 * @param threadName Name of the EventThread 
	 */
	EventThread(String threadName) {
		super(threadName);
		init();
	}

	/**
	 * Constructor for the event thread.
	 */
	EventThread() {
		super();
		init();
	}

	private void init() {
		running = true;
		head = null;
		tail = null;

		setDaemon(true); /* Mark thread as daemon thread */
	}

	/**
	 * Stop thread.
	 */
	void close() {
		running = false;
		interrupt();
	}

	/**
	 * This method pulls events from
	 * the queue and dispatches them.
	 */
	public void run() {
		try {
			while (true) {
				Queued item = getNextEvent();
				if (item == null) {
					return;
				}
				EventManager.dispatchEvent(item.listeners, item.dispatcher, item.action, item.object);
			}
		}
		catch (RuntimeException e) {
			if (EventManager.DEBUG) {
				e.printStackTrace();
			}
			throw e;
		}
		catch (Error e) {
			if (EventManager.DEBUG) {
				e.printStackTrace();
			}
			throw e;
		}
	}

	/**
	 * This methods takes the input parameters and creates a Queued
	 * object and queues it.
	 * The thread is notified.
	 *
	 * @param l Listener list for this event
	 * @param d Dispatcher for this event
	 * @param a Action for this event
	 * @param o Object for this event
	 */
	synchronized void postEvent(ListElement[] l, EventDispatcher d, int a, Object o) {
		if (!isAlive()) {	/* If the thread is not alive, throw an exception */
			throw new IllegalStateException();
		}
		
		Queued item = new Queued(l, d, a, o);

		if (head == null) /* if the queue was empty */
		{
			head = item;
			tail = item;
		} else /* else add to end of queue */
		{
			tail.next = item;
			tail = item;
		}

		notify();
	}

	/**
	 * This method is called by the thread to remove
	 * items from the queue so that they can be dispatched to their listeners.
	 * If the queue is empty, the thread waits.
	 *
	 * @return The Queued removed from the top of the queue or null
	 * if the thread has been requested to stop.
	 */
	private synchronized Queued getNextEvent() {
		while (running && (head == null)) {
			try {
				wait();
			} 
			catch (InterruptedException e) {
			}
		}

		if (!running) { /* if we are stopping */
			return null;
		}

		Queued item = head;
		head = item.next;
		if (head == null) {
			tail = null;
		}

		return item;
	}
}
