/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi 
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT. 
 */
package org.osgi.test.shared;

import java.util.*;
import org.osgi.framework.*;

/**
 * The Shadow class is a helper for making a shadow registry of the Framework
 * registry. It maintains a list of services from the framework but it allows
 * the user to wrap an object around it. This class handles (or at least is
 * supposed to handle) the tricky multithreading issues of coming and going
 * services.
 * <p>
 * Normally this class is extended with a domain specific class.
 */
public abstract class Shadow implements ServiceListener {
	BundleContext	_context;						// Framework context
	Hashtable		_wrappers	= new Hashtable();	// ServiceReference ->
	// Object wrapper
	String			_filter;						// Filter for this shadow
	Class			_clazz;						// Clazz for this shadow

	/**
	 * Open a shadown on the registry.
	 * 
	 * @param context framework context
	 * @param clazz clazz to watch
	 * @param filter filter for watched objects
	 */
	public void open(BundleContext context, Class clazz, String filter)
			throws InvalidSyntaxException {
		if (filter == null)
			_filter = "(objectClass=" + clazz.getName() + ")";
		else
			_filter = "(&(objectClass=" + clazz.getName() + ")" + filter + ")";
		_clazz = clazz;
		_context = context;
		synchronized (this) {
			_context.addServiceListener(this, _filter);
			ServiceReference ref[] = _context.getServiceReferences(_clazz
					.getName(), _filter);
			for (int i = 0; ref != null && i < ref.length; i++)
				add(ref[i]);
		}
	}

	/**
	 * Close the current shadow. Release all services.
	 */
	public synchronized void close() {
		_context.removeServiceListener(this);
		for (Enumeration e = _wrappers.keys(); e.hasMoreElements();) {
			ServiceReference ref = (ServiceReference) e.nextElement();
			_context.ungetService(ref);
		}
	}

	/**
	 * Framework event, handle the semantics locally.
	 */
	public synchronized void serviceChanged(ServiceEvent event) {
		ServiceReference ref = event.getServiceReference();
		switch (event.getType()) {
			case ServiceEvent.REGISTERED :
				add(ref);
				break;
			case ServiceEvent.MODIFIED :
				modified(ref);
				break;
			case ServiceEvent.UNREGISTERING :
				remove(ref);
				break;
		}
	}

	/**
	 * Add a new service to the shadow registry.
	 * 
	 * @param ref Reference to the service
	 */
	protected void add(ServiceReference ref) {
		if (_wrappers.get(ref) == null) {
			Object service = _context.getService(ref);
			Object wrapper = wrapper(ref, service);
			_wrappers.put(ref, wrapper);
			changed();
		}
		else
			System.out.println("Trying to add twice " + ref);
	}

	/**
	 * Remove a service from the shadow registry.
	 * 
	 * @param ref Reference to the service
	 */
	protected void remove(ServiceReference ref) {
		Object association = _wrappers.get(ref);
		if (association != null) {
			_wrappers.remove(ref);
			_context.ungetService(ref);
			changed();
		}
		else
			System.out
					.println("Trying to remove non existent reference " + ref);
	}

	/**
	 * Properties to service were modified.
	 * 
	 * @param ref Reference to the service
	 */
	protected void modified(ServiceReference ref) {
		changed();
	}

	/**
	 * Get the wrapper from the reference, must be implemented by subclass.
	 * 
	 * @param reference Reference to service
	 * @param service Actual service object
	 */
	protected abstract Object wrapper(ServiceReference reference, Object service);

	/**
	 * Release the wrapper. Can be overridden by the subclass to do clean up.
	 * Default is do nothing.
	 * 
	 * @param wrapper wrapped object
	 */
	protected void release(Object wrapper) {
	}

	/**
	 * Indicate changed registry, can be overridden by subclass. Default is do
	 * nothing.
	 */
	protected void changed() {
	}

	/**
	 * Return an enumeration of all wrappers.
	 * 
	 * Notice that this might change on the fly.
	 */
	public Enumeration getWrappers() {
		return _wrappers.elements();
	}

	/**
	 * Answer the size of the list, notice that this size can change any time.
	 */
	public int size() {
		return _wrappers.size();
	}

	/**
	 * Find the object that equals the wrapper.
	 * 
	 * @param search object searched
	 */
	public synchronized Object get(Object search) {
		for (Enumeration e = _wrappers.elements(); e.hasMoreElements();) {
			Object wrapper = e.nextElement();
			if (search.equals(wrapper))
				return wrapper;
		}
		return null;
	}
}
