/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2004). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
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