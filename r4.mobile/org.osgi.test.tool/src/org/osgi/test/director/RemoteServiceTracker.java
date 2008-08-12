/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi 
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT. 
 */
package org.osgi.test.director;

import java.util.Vector;
import org.osgi.framework.*;
import org.osgi.test.service.RemoteService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Track the RemoteService services in the registry. When such a service is
 * discovered, add it to the list and notify the UI.
 */
class RemoteServiceTracker extends ServiceTracker {
	Handler	handler;
	Vector	targets	= new Vector();

	RemoteServiceTracker(Handler handler, BundleContext context)
			throws InvalidSyntaxException {
		super(context, context
				.createFilter("(&(application=org.osgi.test)(objectClass="
						+ RemoteService.class.getName() + "))"), null);
		this.handler = handler;
	}

	/**
	 * New Remote Service. Add it to the list and notify the UI.
	 */
	public Object addingService(ServiceReference reference) {
		RemoteService rs = (RemoteService) super.addingService(reference);
		targets.addElement(rs);
		handler.applet.targetsChanged();
		return rs;
	}

	/**
	 * Remote Service gone, remove.
	 */
	public void removedService(ServiceReference reference, Object target) {
		targets.removeElement(target);
		handler.applet.targetsChanged();
	}

	/**
	 * We maintain a list of targets here.
	 */
	Vector getTargets() {
		return targets;
	}
}
