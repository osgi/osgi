/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.framework.internal.core;

import org.eclipse.osgi.framework.debug.Debug;
import org.osgi.framework.*;

public class FilteredServiceListener implements ServiceListener {
	/** Filter for listener. */
	protected FilterImpl filter;
	/** Real listener. */
	protected ServiceListener listener;
	// The bundle context
	protected BundleContextImpl context;
	// is this an AllServiceListener
	protected boolean allservices = false;

	/**
	 * Constructor.
	 *
	 * @param filterstring filter for this listener.
	 * @param listener real listener.
	 * @exception InvalidSyntaxException if the filter is invalid.
	 */
	protected FilteredServiceListener(String filterstring, ServiceListener listener, BundleContextImpl context) throws InvalidSyntaxException {
		if (filterstring != null)
			filter = new FilterImpl(filterstring);
		this.listener = listener;
		this.context = context;
		this.allservices = (listener instanceof AllServiceListener);
	}

	/**
	 * Receive notification that a service has had a
	 * change occur in it's lifecycle.
	 *
	 * @param event The ServiceEvent.
	 */
	public void serviceChanged(ServiceEvent event) {
		if (!context.hasListenServicePermission(event))
			return;

		//TODO Merge this condition and the one in the bottom
		if (filter == null) {
			if (allservices || context.isAssignableTo((ServiceReferenceImpl) event.getServiceReference()))
				listener.serviceChanged(event);
			return;
		}
		ServiceReferenceImpl reference = (ServiceReferenceImpl) event.getServiceReference();

		if (Debug.DEBUG && Debug.DEBUG_EVENTS) {
			String listenerName = this.getClass().getName() + "@" + Integer.toHexString(this.hashCode()); //$NON-NLS-1$
			Debug.println("filterServiceEvent(" + listenerName + ", \"" + filter + "\", " + reference.registration.properties + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}

		if (filter.match(reference) && (allservices || context.isAssignableTo((ServiceReferenceImpl) event.getServiceReference()))) {
			if (Debug.DEBUG && Debug.DEBUG_EVENTS) {
				String listenerName = listener.getClass().getName() + "@" + Integer.toHexString(listener.hashCode()); //$NON-NLS-1$
				Debug.println("dispatchFilteredServiceEvent(" + listenerName + ")"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			listener.serviceChanged(event);
		}
	}

	/**
	 * Get the filter string used by this Filtered listener.
	 *
	 * @return The filter string used by this listener.
	 */
	public String toString() {
		return filter == null ? listener.toString() : filter.toString();
	}

}
