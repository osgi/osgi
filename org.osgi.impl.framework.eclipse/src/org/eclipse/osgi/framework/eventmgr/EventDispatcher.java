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
 * The EventDispatcher interface contains the method that is called by the
 * Event Manager to complete the event delivery to the event listener.
 * <p>
 * Clients may implement this interface.
 * </p>
 * @since 3.1
 */
public interface EventDispatcher {
	/**
	 * This method is called once for each listener.
	 * This method must cast the event listener object to the appropriate listener
	 * class for the event type and call the appropriate listener method.
	 * 
	 * <p>The method should properly log/handle any exceptions thrown by the called
	 * listener. The EventManager will ignore any Throwable thrown by this method
	 * in order to continue delivery of the event to the next listener.
	 *
	 * @param eventListener This listener must be cast to the appropriate listener
	 * class for the event to be delivered and the appropriate listener method
	 * must then be called.
	 * @param listenerObject This is the optional companion object that was 
	 * specified when the listener was added to the EventListeners object.
	 * @param eventAction This value was passed to the ListenerQueue object via one of its
	 * dispatchEvent* method calls. It can provide information (such
	 * as which listener method to call) so that the EventDispatcher
	 * can complete the delivery of the event to the listener.
	 * @param eventObject This object was passed to the ListenerQueue object via one of its
	 * dispatchEvent* method calls. This object was created by the event source and
	 * is passed to this method. It should contain all the necessary information (such
	 * as what event object to pass) so that this method
	 * can complete the delivery of the event to the listener.
	 * This is typically the actual event object.
	 */
	public void dispatchEvent(Object eventListener, Object listenerObject, int eventAction, Object eventObject);
}
