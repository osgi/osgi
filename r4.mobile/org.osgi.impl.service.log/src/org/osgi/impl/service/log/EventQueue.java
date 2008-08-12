/*
 * $Header$
 *
 * OSGi Log Service Reference Implementation.
 *

 *
 * (C) Copyright IBM Corporation 2000-2001.
 *
 * This source code is licensed to OSGi as MEMBER LICENSED MATERIALS
 * under the terms of Section 3.2 of the OSGi MEMBER AGREEMENT.
 */
package org.osgi.impl.service.log;

import java.util.*;
import org.osgi.service.log.*;

/**
 * The class manages the event queue to asynchronously deliver LogEntrys to
 * LogListeners.
 * 
 * @author BJ Hargrave (hargrave@us.ibm.com)
 * @version $Revision$
 */
public class EventQueue extends Thread {
	/** event queue */
	protected Vector				queue;
	/** if false the thread must terminate */
	private volatile boolean		running;
	private static final boolean	ASYNCHRONOUS	= true;

	/**
	 * Constructor for the event queue. The queue is created empty and the
	 * thread is started.
	 *  
	 */
	protected EventQueue() {
		running = true;
		queue = new Vector(10, 10);
		setDaemon(true); /* Mark thread as daemon thread */
		start(); /* Start thread */
	}

	/**
	 * Destroy thread.
	 */
	protected void close() {
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
				deliverEvent((LogReaderServiceImpl) item[0], (Vector) item[1],
						(LogEntry) item[2]);
			}
			catch (Throwable t) {
			}
		}
	}

	/**
	 * This method is called to publish a LogEntry.
	 *  
	 */
	protected synchronized void publishLogEntry(Vector logreaders,
			LogEntry logentry) {
		Enumeration enum = logreaders.elements();
		while (enum.hasMoreElements()) {
			LogReaderServiceImpl logreader = (LogReaderServiceImpl) enum
					.nextElement();
			Vector listeners = logreader.listeners;
			if (listeners != null) {
				if (ASYNCHRONOUS) {
					Object[] item = new Object[] {logreader, listeners.clone(),
							logentry};
					queue.addElement(item);
				}
				else {
					deliverEvent(logreader, (Vector) listeners.clone(),
							logentry);
				}
			}
		}
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
	protected void deliverEvent(LogReaderServiceImpl logreader,
			Vector listeners, LogEntry logentry) {
		Enumeration enum = listeners.elements();
		while (enum.hasMoreElements()) {
			LogListener listener = (LogListener) enum.nextElement();
			try {
				logreader.deliverLogEntry(listener, logentry);
			}
			catch (Throwable t) {
			}
		}
	}
}
