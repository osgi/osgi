/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi 
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT. 
 */
package org.osgi.test.shared;

import java.util.Vector;

/**
 * A save multithreaded queue.
 * 
 * This class is used in several places to handle send/receive problems between
 * processes.
 */
public class Queue {
	Vector	_list	= new Vector();
	Thread	_thread	= null;

	/**
	 * Add a new element to the queue, if there are waiters notify them.
	 * 
	 * @param subject object to add
	 */
	synchronized public void push(Object subject) {
		_list.addElement(subject);
		notify();
	}

	/**
	 * Interrupts the waiting thread if any.
	 */
	synchronized public void abort() {
		if (_thread != null)
			_thread.interrupt();
	}

	/**
	 * Wait for an object to be pushed for timeout milliseconds.
	 * 
	 * @param timeout ms to wait
	 * @returns null or object
	 */
	synchronized public Object pop(long timeout) {
		long deadline = System.currentTimeMillis() + timeout;
		long difference = timeout;
		try {
			while (_list != null && _list.isEmpty() && difference > 0) {
				_thread = Thread.currentThread();
				wait(difference);
				_thread = null;
				difference = deadline - System.currentTimeMillis();
			}
			if (_list == null || _list.isEmpty())
				return null;
			Object subject = _list.elementAt(0);
			_list.removeElementAt(0);
			return subject;
		}
		catch (InterruptedException e) {
		}
		return null;
	}

	/**
	 * Release all waiters.
	 */
	public synchronized void releaseAll() {
		_list = null;
		notifyAll();
	}
}
