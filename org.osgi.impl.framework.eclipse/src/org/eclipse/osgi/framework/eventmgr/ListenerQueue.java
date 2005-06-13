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

import java.util.ArrayList;
import org.eclipse.osgi.framework.eventmgr.EventListeners.ListElement;
import org.eclipse.osgi.framework.eventmgr.EventManager.EventThread;

/**
 * The ListenerQueue is used to snapshot the set of listeners at the time the event
 * is fired. The snapshot list is then used to dispatch
 * events to those listeners. A ListenerQueue object is associated with a
 * specific EventManager object. ListenerQueue objects constructed with the same
 * EventManager object will get in-order delivery of events
 * using asynchronous delivery. No delivery order is guaranteed for synchronous
 * delivery to avoid any potential deadly embraces.
 *
 * <p>ListenerQueue objects are created as necesssary to build a set of listeners
 * that should receive a specific event or events. Once the set is created, the event
 * can then be synchronously or asynchronously delivered to the set of
 * listeners. After the event has been dispatched for delivery, the
 * ListenerQueue object should be discarded as it is likely the list of listeners is stale.
 * A new ListenerQueue object should be created when it is time to deliver 
 * another event.  The memory cost of a ListenerQueue object is
 * low since the ListenerQueue object shares the array of listeners with the EventListeners 
 * object which are queued.
 * EventListeners uses copy-on-write semantics for managing the array and will copy the array
 * before changing it once the array has been shared with a ListenerQueue. This minimizes 
 * object creation while guaranteeing the snapshot list is never modified once created.
 * @since 3.1
 */
public class ListenerQueue {
	/**
	 * EventManager with which this queue is associated.
	 */
	protected final EventManager manager;
	/**
	 * A list of listener lists.
	 */
	private final ArrayList queue;

	/**
	 * Once the listener queue has been used to dispatch an event, 
	 * you cannot add modify the queue.
     * Access to this field must be protected by a synchronized region.
	 */
	private boolean readOnly;

	/**
	 * ListenerQueue constructor. This method creates an empty snapshop list.
	 *
	 * @param manager The EventManager this queue is associated with.
	 * @throws IllegalArgumentException If manager is null.
	 */
	public ListenerQueue(EventManager manager) {
		if (manager == null) {
			throw new IllegalArgumentException();
		}

		this.manager = manager;
		queue = new ArrayList();
		readOnly = false;
	}

	/**
	 * Add a listener list to the snapshot list. This method can be called multiple times, prior to
	 * calling one of the dispatchEvent methods, to build the set of listeners for the
	 * delivery of a specific event. The current list of listeners in the specified EventListeners
	 * object is added to the snapshot list.
	 *
	 * @param listeners An EventListeners object to add to the queue. The current listeners
	 * in the EventListeners object will be called when an event is dispatched.
	 * @param dispatcher An EventDispatcher object to use when dispatching an event
	 * to the listeners on the specified EventListeners.
	 * @throws IllegalStateException If called after one of the dispatch methods has been called.
	 */
	public synchronized void queueListeners(EventListeners listeners, EventDispatcher dispatcher) {
		if (readOnly) {
			throw new IllegalStateException();
		}

		if (listeners != null) {
			ListElement[] list = listeners.getListeners();

			if (list.length > 0) {
				queue.add(new EventListeners.ListElement(list, dispatcher));
			}
		}
	}

	/**
	 * Asynchronously dispatch an event to the snapshot list. An event dispatch thread
	 * maintained by the associated EventManager is used to deliver the events.
	 * This method may return immediately to the caller.
	 *
	 * @param eventAction This value is passed to the EventDispatcher.
	 * @param eventObject This object is passed to the EventDispatcher.
	 */
	public void dispatchEventAsynchronous(int eventAction, Object eventObject) {
		synchronized (this) {
			readOnly = true;
		}
		EventThread eventThread = manager.getEventThread();
		synchronized (eventThread) { /* synchronize on the EventThread to ensure no interleaving of posting to the event thread */
			int size = queue.size();
			for (int i = 0; i < size; i++) { /* iterate over the list of listener lists */
				ListElement list = (ListElement)queue.get(i);
				eventThread.postEvent((ListElement[]) list.primary, (EventDispatcher) list.companion, eventAction, eventObject);
			}
		}
	}

	/**
	 * Synchronously dispatch an event to the snapshot list. The event may
	 * be dispatched on the current thread or an event dispatch thread
	 * maintained by the associated EventManager.
	 * This method will not return to the caller until the EventDispatcher
	 * has been called (and has returned) for each listener on the queue.
	 *
	 * @param eventAction This value is passed to the EventDispatcher.
	 * @param eventObject This object is passed to the EventDispatcher.
	 */
	public void dispatchEventSynchronous(int eventAction, Object eventObject) {
		synchronized (this) {
			readOnly = true;
		}
		// We can't guarantee any delivery order for synchronous events.
		// Attempts to do so result in deadly embraces.
		int size = queue.size();
		for (int i = 0; i < size; i++) { /* iterate over the list of listener lists */
			ListElement list = (ListElement)queue.get(i);
			EventManager.dispatchEvent((ListElement[]) list.primary, (EventDispatcher) list.companion, eventAction, eventObject);
		}
	}
}
