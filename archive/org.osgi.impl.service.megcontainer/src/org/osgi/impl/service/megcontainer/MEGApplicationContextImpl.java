/*
 * ============================================================================
 * (c) Copyright 2004 Nokia
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Nokia and its licensors. 
 * All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 * ============================================================================
 */
package org.osgi.impl.service.megcontainer;

import java.util.*;
import org.osgi.framework.*;
import org.osgi.service.application.*;
import org.osgi.service.event.EventChannel;

/**
 * The application context class for MEG apps
 */
public class MEGApplicationContextImpl implements MEGApplicationContext,
		Runnable {
	private Map				args;
	private BundleContext	bc;
	private Hashtable		serviceHash;
	private Thread			requestThread;
	private LinkedList		serviceRequests;

	class ServiceRequest {
		private String	className;
		private String	filter;
		private boolean	rejected;
		private Object	resultObject	= null;

		public ServiceRequest(String className, String filter) {
			this.className = className;
			this.filter = filter;
			rejected = false;
		}

		public String getFilter() {
			return filter;
		}

		public String getClassName() {
			return className;
		}

		public synchronized Object waitToComplete(long millisecs) {
			try {
				wait(millisecs);
			}
			catch (InterruptedException e) {
			};
			rejected = true;
			return resultObject;
		}

		public synchronized boolean completed(Object result) {
			boolean wasRejected = rejected;
			resultObject = result;
			notify();
			return wasRejected;
		}
	}

	public MEGApplicationContextImpl(BundleContext bc, Map args) {
		this.args = args;
		this.bc = bc;
		serviceHash = new Hashtable();
		serviceRequests = new LinkedList();
		requestThread = new Thread(this);
		requestThread.start();
	}

	public Object getServiceObject(String className, String filter)
			throws Exception {
		ServiceReference references[] = bc.getServiceReferences(className,
				filter);
		if (references == null || references.length == 0)
			return null;
		Object object = bc.getService(references[0]);
		serviceHash.put(object, references[0]);
		return object;
	}

	public Object getServiceObject(String className, String filter,
			long millisecs) throws Exception {
		ServiceRequest servReq = new ServiceRequest(className, filter);
		addServiceRequest(servReq);
		return servReq.waitToComplete(millisecs);
	}

	public boolean ungetServiceObject(Object serviceObject) {
		ServiceReference reference = (ServiceReference) serviceHash
				.remove(serviceObject);
		if (reference == null)
			return false;
		try {
			return bc.ungetService(reference);
		}
		catch (Exception e) {
		}
		return false;
	}

	public EventChannel getEventChannel() {
		/*
		 * TODO TODO TODO TODO TODO --- WAIT A CONSISTENT API --- TODO TODO TODO
		 * TODO TODO
		 */
		return null;
	}

	public ApplicationManager getApplicationManager() {
		/*
		 * TODO TODO TODO TODO TODO --- WAIT A CONSISTENT API --- TODO TODO TODO
		 * TODO TODO
		 */
		return null;
	}

	public Map getLaunchArgs() {
		return args;
	}

	private synchronized void addServiceRequest(ServiceRequest req) {
		serviceRequests.add(req);
		notify();
	}

	private synchronized Object getNextServiceRequest() {
		while (serviceRequests.isEmpty()) {
			try {
				wait();
			}
			catch (InterruptedException e) {
			}
		}
		return serviceRequests.removeFirst();
	}

	public void run() {
		while (true) {
			ServiceRequest servReq = (ServiceRequest) getNextServiceRequest();
			Object result = null;
			try {
				if (servReq.completed(result = getServiceObject(servReq
						.getClassName(), servReq.getFilter())))
					ungetServiceObject(result); // if the user rejected
			}
			catch (Exception e) {
				if (result != null)
					ungetServiceObject(result);
			}
		}
	}
}
