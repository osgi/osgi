/*
 * $Header$
 *
 * Copyright (c) IBM Corporation (2005)
 *
 * These materials have been contributed  to the OSGi Alliance as 
 * "MEMBER LICENSED MATERIALS" as defined in, and subject to the terms of, 
 * the OSGi Member Agreement, specifically including but not limited to, 
 * the license rights and warranty disclaimers as set forth in Sections 3.2 
 * and 12.1 thereof, and the applicable Statement of Work. 
 *
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.
 */

package org.eclipse.osgi.component.workqueue;

import org.eclipse.osgi.component.Log;
import org.eclipse.osgi.component.Main;

/**
 * This class is used for asynchronously dispatching work items.
 */

public class WorkQueue extends Thread {
	public static final boolean	DEBUG	= false;
	public Main					main;

	/**
	 * Queued is a nested class. This class represents the items which are
	 * placed on the asynch dispatch queue. This class is private.
	 */
	private class Queued {
		/** dispatcher of this item */
		final WorkDispatcher	dispatcher;
		/** action for this item */
		final int				action;
		/** object for this item */
		final Object			object;
		/** next item in work queue */
		Queued					next;

		/**
		 * Constructor for work queue item
		 * 
		 * @param d Dispatcher for this item
		 * @param a Action for this item
		 * @param o Object for this item
		 */
		Queued(WorkDispatcher d, int a, Object o) {
			dispatcher = d;
			action = a;
			object = o;
			next = null;
		}

		void dispatch() {
			try {
				/*
				 * Call the WorkDispatcher to dispatch the work.
				 */
				dispatcher.dispatchWork(action, object);
			}
			catch (Throwable t) {
				t.printStackTrace();
				Log.log(1, "[SCR] Error dispatching work ", t);
			}
		}
	}

	/** item at the head of the work queue */
	private Queued				head;
	/** item at the tail of the work queue */
	private Queued				tail;
	/** if true the thread should complete it's work and terminate */
	private volatile boolean	stopping;

	/**
	 * Constructor for the work queue thread.
	 * 
	 * @param threadName Name of the WorkQueue
	 */
	public WorkQueue(Main main, String threadName) {
		super(threadName);
		this.main = main;
		stopping = false;
		head = null;
		tail = null;
	}

	/**
	 * Finish all work and stop thread.
	 */
	public void closeAndJoin() {
		stopping = true;
		interrupt();
		try {
			join(); // wait for work to finish
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method pulls items from the work queue and dispatches them.
	 */
	public void run() {
		try {
			while (true) {
				Queued item = dequeueWork();
				if (item == null) {
					return;
				}

				item.dispatch();
			}
		}
		catch (RuntimeException e) {
			if (DEBUG) {
				e.printStackTrace(System.err);
			}
			throw e;
		}
		catch (Error e) {
			if (DEBUG) {
				e.printStackTrace(System.err);
			}
			throw e;
		}
	}

	/**
	 * This methods takes the input parameters and creates a Queued object and
	 * queues it. The thread is notified.
	 * 
	 * @param d Dispatcher for this item
	 * @param a Action for this item
	 * @param o Object for this item
	 */
	public synchronized void enqueueWork(WorkDispatcher d, int a, Object o) {
		if (!isAlive()) { /* If the thread is not alive, throw an exception */
			throw new IllegalStateException("work thread is not alive");
		}

		Queued item = new Queued(d, a, o);

		if (head == null) /* if the queue was empty */
		{
			head = item;
			tail = item;
		}
		else /* else add to end of queue */
		{
			tail.next = item;
			tail = item;
		}

		notify();
	}

	/**
	 * This method is called by the thread to remove items from the queue so
	 * that they can be dispatched to their listeners. If the queue is empty,
	 * the thread waits.
	 * 
	 * @return The Queued removed from the top of the queue or null if the
	 *         thread has been requested to stop.
	 */
	private synchronized Queued dequeueWork() {
		while ((!stopping) && (head == null)) {
			try {
				wait();
			}
			catch (InterruptedException e) {
			}
		}

		if (stopping && head == null) { /* if we are stopping */
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