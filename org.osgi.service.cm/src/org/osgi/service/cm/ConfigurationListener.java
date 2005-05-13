/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2004, 2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package org.osgi.service.cm;

/**
 * Listener for Configuration Events.
 * 
 * <p>
 * <code>ConfigurationListener</code> objects are registered with the Framework
 * service registry and are notified with a <code>ConfigurationEvent</code> object
 * when an event is broadcast.
 * <p>
 * <code>ConfigurationListener</code> objects can inspect the received
 * <code>ConfigurationEvent</code> object to determine its type, the pid of the
 * <code>Configuration</code> object with which it is associated, and the
 * Configuration Admin service that broadcasted the event.
 * 
 * <p>
 * Security Considerations. Bundles wishing to monitor configuration events will
 * require <code>ServicePermission[ConfigurationListener,REGISTER]</code> to
 * register a <code>ConfigurationListener</code> service.
 * 
 * @version $Revision$
 * @since 1.2
 */
public interface ConfigurationListener {
	/**
	 * Receives notification of a broadcast <code>ConfigurationEvent</code>
	 * object.
	 * 
	 * @param event The broadcasted <code>ConfigurationEvent</code> object.
	 */
	void configurationEvent(ConfigurationEvent event);
}