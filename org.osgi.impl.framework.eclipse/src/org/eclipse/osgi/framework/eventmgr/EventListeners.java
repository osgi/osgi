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
 * This class manages a list of listeners.
 * Listeners may be added or removed as necessary.
 */

public class EventListeners {
	/**
	 * The empty array singleton instance, returned by getListeners()
	 * when size == 0.
	 */
	private static final ListElement[] emptyArray = new ListElement[0];

	/**
	 * The initial capacity of the list. Always >= 1.
	 */
	private final int initialCapacity;

	/**
	 * The list of elements.  Initially <code>null</code> but initialized
	 * to an array of size initialCapacity the first time an element is added.
	 * Maintains invariants: 
	 * 	list != null IFF size != 0
	 * 	list[size] == null
	 *  for all i < size: list[i] != null
     * Access to this field must be protected by a synchronized region.
	 */
	private ListElement[] list = null;

	/**
	 * The current number of elements.
	 * Maintains invariant: 0 <= size <= list.length.
     * Access to this field must be protected by a synchronized region.
	 */
	private int size = 0;

	/**
	 * If true and about to modify the list,
	 * then the list must be copied first.
     * Access to this field must be protected by a synchronized region.
	 */
	private boolean copyOnWrite = false;

	/**
	 * Creates a listener list with an initial capacity of 10.
	 *
	 */
	public EventListeners() {
		this(10);
	}

	/**
	 * Creates a listener list with the given initial capacity.
	 *
	 * @param capacity The number of listeners which this list can initially 
	 *    accept without growing its internal representation; must be at
	 *    least 1
	 * @throws IllegalArgumentException If capacity is less than 1.
	 */
	public EventListeners(int capacity) {
		if (capacity < 1)
			throw new IllegalArgumentException();
		this.initialCapacity = capacity;
	}

	/**
	 * Add a listener to the list.
	 * If a listener object is already in the list, then it is replaced.
	 *
	 * @param listener This is the listener object to be added to the list.
	 * @param listenerObject This is an optional listener-specific object.
	 * This object will be passed to the EventDispatcher along with the listener
	 * when the listener is to be called. This may be null
	 * @throws IllegalArgumentException If listener is null.
	 */
	public synchronized void addListener(Object listener, Object listenerObject) { 
		if (listener == null) {
			throw new IllegalArgumentException();
		}

		if (size == 0) {
			list = new ListElement[initialCapacity];
		} 
		else {
			// copy array if necessary
			if (copyOnWrite) {
				copyList(size);
				copyOnWrite = false;
			}

			// check for duplicates using identity
			for (int i = 0; i < size; i++) {
				if (list[i].primary == listener) {
					list[i] = new ListElement(listener, listenerObject); /* use the most recent companion */
					return;
				}
			}

			// grow array if necessary
			// This wont recopy list if copy on write occured above since that
			// would have grown the list.
			if (size == list.length) {
				copyList(size);
			}
		}

		list[size] = new ListElement(listener, listenerObject);
		size++;
	}

	/**
	 * Remove a listener from the list.
	 *
	 * @param listener This is the listener object to be removed from the list.
	 * @throws IllegalArgumentException If listener is null.
	 */
	public synchronized void removeListener(Object listener) {
		if (listener == null) {
			throw new IllegalArgumentException();
		}

		for (int i = 0; i < size; i++) {
			if (list[i].primary == listener) {
				size--;
				if (size == 0) {
					list = null; /* invariant: list must be null iff size is zero */
					return;
				}
				if (copyOnWrite) {
					copyList(i);
					copyOnWrite = false;
				}
				else {
					System.arraycopy(list, i + 1, list, i, size - i);
					list[size] = null; /* invariant: end of list must be null */
				}
				return;
			}
		}
	}

	/**
	 * Remove all listeners from the list.
	 */
	public synchronized void removeAllListeners() {
		/* invariant: list must be null iff size is zero */
		list = null;
		size = 0;
	}

	/**
	 * Return the list of (listener, listenerObject) pairs.
	 * Package private method.
	 * The array may be longer than the number of pairs in the array.
	 * The end of the pairs is signalled by a null element or
	 * end of array. 
	 * This array must not be modified by anyone and should not be 
	 * exposed outside of this package.
	 * To reduce memory allocations, the internal array is shared
	 * with the rest of this package. However an array returned by this method
	 * must not be modified in anyway.
	 * 
	 * @return A shared array that must not be modified by anyone. 
	 */
	synchronized ListElement[] getListeners() {
		if (size == 0) {
			return emptyArray;
		}
		copyOnWrite = true;
		return list;
	}
	
	/**
	 * Copy the array.
	 * @param i Index of element to remove from array. Must be equal to size to 
	 * copy entire array.
	 * @throws IndexOutOfBoundsException If i < 0 or i > size.
	 */
	private void copyList(int i) {
		if (i > size) {
			throw new IndexOutOfBoundsException();
		}
		int capacity = (size * 3) / 2 + 1;
		if (capacity < initialCapacity) {
			capacity = initialCapacity;
		}
		ListElement[] newList = new ListElement[capacity];
		System.arraycopy(list, 0, newList, 0, i);
		if (i < size) {
			System.arraycopy(list, i + 1, newList, i, size - i);
		}
		list = newList;
	}
}
