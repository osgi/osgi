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
 * Listener for Configuration changes.
 * 
 * <p>
 * <tt>ConfigurationListener</tt> objects are registered with the Framework
 * service registry and are notified when a <tt>Configuration</tt> object is
 * updated or deleted.
 * <p>
 * <tt>ConfigurationListener</tt> objects are passed the type of configuration
 * change.
 * 
 * <p>
 * One of the change methods will be called with <tt>CM_UPDATED</tt> when
 * <tt>Configuration.update</tt> is called or with <tt>CM_DELETED</tt> when
 * <tt>Configuration.delete</tt> is called. Notification will be asynchronous
 * to the update or delete method call.
 * 
 * The design is very lightweight in that is does not pass <tt>Configuration</tt>
 * objects, the listener is merely advised that the configuration information
 * for a given pid has changed. If the listener wants to locate the <tt>Configuration</tt>
 * object for the specified pid, it must use <tt>ConfigurationAdmin</tt>.
 * 
 * <p>
 * Security Considerations. Bundles wishing to monitor <tt>Configuration</tt>
 * changes will require
 * <tt>ServicePermission[ConfigurationListener,REGISTER]</tt> to register a
 * <tt>ConfigurationListener</tt> service.
 * Since <tt>Configuration</tt> objects are not passed to the listener,
 * no sensitive configuration information is available to the listener.
 * 
 * @version $Revision$
 */
public interface ConfigurationListener {
	/**
	 * Change type that indicates that <tt>Configuration.update</tt> was
	 * called.
	 */
	static final int	CM_UPDATED	= 1;
	/**
	 * Change type that indicates that <tt>Configuration.delete</tt> was
	 * called.
	 */
	static final int	CM_DELETED	= 2;

	/**
	 * Receives notification a configuration has changed.
	 * 
	 * <p>
	 * This method is only called if the target of the configuration is a
	 * ManagedService.
	 * 
	 * @param pid The pid of the configuration which changed.
	 * @param type The type of the configuration change.
	 */
	void configurationChanged(String pid, int type);

	/**
	 * Receives notification a factory configuration has changed.
	 * 
	 * <p>
	 * This method is only called if the target of the configuration is a
	 * ManagedServiceFactory.
	 * 
	 * @param factoryPid The factory pid for the changed configuration.
	 * @param pid The pid of the configuration which changed.
	 * @param type The type of the configuration change.
	 */
	void factoryConfigurationChanged(String factoryPid, String pid, int type);
}