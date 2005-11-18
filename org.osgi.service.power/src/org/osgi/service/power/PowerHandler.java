/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package org.osgi.service.power;

/**
 * <p>
 * A service that has to be registered within the framework to be part of query
 * sequence when the {@link PowerManager} is moving to another system power
 * state.
 * 
 * @version $Revision$
 */
public interface PowerHandler {

	/**
	 * Calls by the {@link PowerManager} to request permission to system power
	 * state change to the given value.
	 * 
	 * @param state requested state.
	 * @return <code>true</code> if the change is accepted, <code>false</code>
	 *         otherwize.
	 */
	boolean handleQuery(int state);

	/**
	 * Called by the {@link PowerManager} to notify that the previous request
	 * change has been denied. Only Power Handlers previouly notified on
	 * {@link #handleQuery(int)} must be called on this method.
	 * 
	 * @param state rejected state.
	 */
	void handleQueryFailed(int state);

}
