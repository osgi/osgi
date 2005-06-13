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

import org.eclipse.osgi.framework.eventmgr.EventListeners.ListElement;

/**
 * This class is the central class for the Event Manager. Each
 * program that wishes to use the Event Manager should construct
 * an EventManager object and use that object to construct
 * ListenerQueue for dispatching events. EventListeners objects
 * should be used to manage listener lists.
 *
 * <p>This example uses the ficticous SomeEvent class and shows how to use this package 
 * to deliver a SomeEvent to a set of SomeEventListeners.  
 * <pre>
 *
 * 		// Create an EventManager with a name for an asynchronous event dispatch thread
 * 		EventManager eventManager = new EventManager("SomeEvent Async Event Dispatcher Thread");
 * 		// Create an EventListeners to hold the list of SomeEventListeners
 *		EventListeners eventListeners = new EventListeners();
 *
 *		// Add a SomeEventListener to the listener list
 *	    eventListeners.addListener(someEventListener, null);
 *
 *		// Asynchronously deliver a SomeEvent to registered SomeEventListeners
 *		// Create the listener queue for this event delivery
 *		ListenerQueue listenerQueue = new ListenerQueue(eventManager);
 *		// Add the listeners to the queue and associate them with the event dispatcher
 *		listenerQueue.queueListeners(eventListeners, new EventDispatcher() {
 *	        public void dispatchEvent(Object eventListener, Object listenerObject, 
 *                                    int eventAction, Object eventObject) {
 * 				try {
 *					(SomeEventListener)eventListener.someEventOccured((SomeEvent)eventObject);
 * 				} catch (Throwable t) {
 * 					// properly log/handle any Throwable thrown by the listener
 * 				}
 *			}
 *		});
 *		// Deliver the event to the listeners. 
 *		listenerQueue.dispatchEventAsynchronous(0, new SomeEvent());
 *		
 *		// Remove the listener from the listener list
 *	    eventListeners.removeListener(someEventListener);
 *
 *		// Close EventManager to clean when done to terminate async event dispatch thread
 *		eventManager.close();
 * </pre>
 * 
 * <p>At first glance, this package may seem more complicated than necessary
 * but it has support for some important features. The listener list supports
 * companion objects for each listener object. This is used by the OSGi framework
 * to create wrapper objects for a listener which are passed to the event dispatcher.
 * The ListenerQueue class is used to build a snap shot of the listeners prior to beginning
 * event dispatch. 
 * 
 * The OSGi framework uses a 2 level listener list (EventListeners) for each listener type (4 types). 
 * Level one is managed by the framework and contains the list of BundleContexts which have 
 * registered a listener. Level 2 is managed by each BundleContext for the listeners in that 
 * context. This allows all the listeners of a bundle to be easily and atomically removed from 
 * the level one list. To use a "flat" list for all bundles would require the list to know which 
 * bundle registered a listener object so that the list could be traversed when stopping a bundle 
 * to remove all the bundle's listeners. 
 * 
 * When an event is fired, a snapshot list (ListenerQueue) must be made of the current listeners before delivery 
 * is attempted. The snapshot list is necessary to allow the listener list to be modified while the 
 * event is being delivered to the snapshot list. The memory cost of the snapshot list is
 * low since the ListenerQueue object shares the array of listeners with the EventListeners object.
 * EventListeners uses copy-on-write semantics for managing the array and will copy the array
 * before changing it IF the array has been shared with a ListenerQueue. This minimizes 
 * object creation while guaranteeing the snapshot list is never modified once created.
 * 
 * The OSGi framework also uses a 2 level dispatch technique (EventDispatcher).
 * Level one dispatch is used by the framework to add the level 2 listener list of each 
 * BundleContext to the snapshot in preparation for delivery of the event.
 * Level 2 dispatch is used as the final event deliverer and must cast the listener 
 * and event objects to the proper type before calling the listener. Level 2 dispatch
 * will cancel delivery of an event 
 * to a bundle that has stopped bewteen the time the snapshot was created and the
 * attempt was made to deliver the event.
 * 
 * <p> The highly dynamic nature of the OSGi framework had necessitated these features for 
 * proper and efficient event delivery.  
 * @since 3.1
 */

public class EventManager {
	static final boolean DEBUG = false;

	/**
	 * EventThread for asynchronous dispatch of events.
     * Access to this field must be protected by a synchronized region.
	 */
	private EventThread thread;

	/**
	 * EventThread Name
	 */
	protected final String threadName;

	/**
	 * EventManager constructor. An EventManager object is responsible for
	 * the delivery of events to listeners via an EventDispatcher.
	 *
	 */
	public EventManager() {
		this(null);
	}

	/**
	 * EventManager constructor. An EventManager object is responsible for
	 * the delivery of events to listeners via an EventDispatcher.
	 *
	 * @param threadName The name to give the event thread associated with
	 * this EventManager.
	 */
	public EventManager(String threadName) {
		thread = null;
		this.threadName = threadName;
	}

	/**
	 * This method can be called to release any resources associated with this
	 * EventManager.
	 *
	 */
	public synchronized void close() {
		if (thread != null) {
			thread.close();
			thread = null;
		}
	}

	/**
	 * Returns the EventThread to use for dispatching events asynchronously for
	 * this EventManager.
	 *
	 * @return EventThread to use for dispatching events asynchronously for
	 * this EventManager.
	 */
	synchronized EventThread getEventThread() {
		if (thread == null) {
			/* if there is no thread, then create a new one */
			if (threadName == null) {
				thread = new EventThread();
			} 
			else {
				thread = new EventThread(threadName);
			}
			thread.start(); /* start the new thread */
		}

		return thread;
	}

	/**
	 * This method calls the EventDispatcher object to complete the dispatch of
	 * the event. If there are more elements in the list, call dispatchEvent
	 * on the next item on the list.
	 * This method is package private.
	 *
	 * @param listeners A null terminated array of ListElements with each element containing the primary and 
	 * companion object for a listener. This array must not be modified.
	 * @param dispatcher Call back object which is called to complete the delivery of
	 * the event.
	 * @param eventAction This value was passed by the event source and
	 * is passed to this method. This is passed on to the call back object.
	 * @param eventObject This object was created by the event source and
	 * is passed to this method. This is passed on to the call back object.
	 */
	static void dispatchEvent(ListElement[] listeners, EventDispatcher dispatcher, int eventAction, Object eventObject) {
		int size = listeners.length;
		for (int i = 0; i < size; i++) { /* iterate over the list of listeners */
			ListElement listener = listeners[i];
			if (listener == null) {		/* a null element terminates the list */
				break;
			}
			try {
				/* Call the EventDispatcher to complete the delivery of the event. */
				dispatcher.dispatchEvent(listener.primary, listener.companion, eventAction, eventObject);
			} 
			catch (Throwable t) {
				/* Consume and ignore any exceptions thrown by the listener */
				if (DEBUG) {
					System.out.println("Exception in " + listener.primary); //$NON-NLS-1$
					t.printStackTrace();
				}
			}
		}
	}

	/**
	 * This package private class is used for asynchronously dispatching events.
	 */

	static class EventThread extends Thread {
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
}
