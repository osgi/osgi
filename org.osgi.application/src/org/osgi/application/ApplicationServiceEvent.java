/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package org.osgi.application;

import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;

/**
 *
 * TODO Add Javadoc comment for this type.
 * 
 * @version $Revision$
 */
public class ApplicationServiceEvent extends ServiceEvent {

	final Object serviceObject;

	/**
	 * Creates a new applicat service event object.
	 * 
	 * @param type The event type.
	 * @param reference A <code>ServiceReference</code> object to the service
	 *        that had a lifecycle change.
	 * @param serviceObject The service object bound to this application instance. It can
	 *    be <code>null</code> if this application is not bound to this service yet.
	 */
	public ApplicationServiceEvent(int type, ServiceReference reference, Object serviceObject) {
		super(type, reference);
		this.serviceObject = serviceObject;
	}
	
	/**
	 * This method returns the service object of this service bound to the listener
	 * application instace. A service object becomes bound to the application when it
	 * first obtains a service object reference to that service by calling the
	 * <code>ApplicationContext.locateService</code> or <code>locateServices</code>
	 * methods. If the application is not bound to the service yet, this method returns
	 * <code>null</code>.
	 * 
	 * @return the service object bound to the listener application or <code>null</code>
	 *   if it isn't bound to this service yet.
	 */
	public Object getServiceObject() {
		return this.serviceObject;
	}

}
