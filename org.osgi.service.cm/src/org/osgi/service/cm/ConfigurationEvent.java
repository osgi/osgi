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

import org.osgi.framework.ServiceReference;

/**
 * A Configuration Event.
 * 
 * <p>
 * <tt>ConfigurationEvent</tt> objects are delivered to all registered
 * <tt>ConfigurationListener</tt> service objects. Events must be delivered in
 * chronological order with respect to each listener.
 * 
 * <p>
 * A type code is used to identify the type of event. The following event types
 * are defined:
 * <ul>
 * <li>{@link #CM_UPDATED}
 * <li>{@link #CM_DELETED}
 * </ul>
 * Additional event types may be defined in the future.
 * 
 * <p>
 * Security Considerations. <tt>ConfigurationEvent</tt> objects do not provide
 * <tt>Configuration</tt> objects, so no sensitive configuration information
 * is available from the event. If the listener wants to locate the
 * <tt>Configuration</tt> object for the specified pid, it must use
 * <tt>ConfigurationAdmin</tt>.
 * 
 * @see ConfigurationListener
 * 
 * @version $Revision$
 * @since 1.2
 */
public class ConfigurationEvent {
	/**
	 * A <tt>Configuration</tt> has been updated.
	 * 
	 * <p>
	 * This <tt>ConfigurationEvent</tt> type that indicates that a
	 * <tt>Configuration</tt> object has been updated with new properties.
	 * 
	 * An event is broadcast when a call to <tt>Configuration.update</tt>
	 * successfully changed a configuration.
	 * 
	 * <p>
	 * The value of <tt>CM_UPDATED</tt> is 1.
	 */
	public static final int			CM_UPDATED	= 1;
	/**
	 * A <tt>Configuration</tt> has been deleted.
	 * 
	 * <p>
	 * This <tt>ConfigurationEvent</tt> type that indicates that a
	 * <tt>Configuration</tt> object has been deleted.
	 * 
	 * An event is broadcast when a call to <tt>Configuration.delete</tt>
	 * successfully deletes a configuration.
	 * 
	 * <p>
	 * The value of <tt>CM_DELETED</tt> is 2.
	 */
	public static final int			CM_DELETED	= 2;
	/**
	 * Type of this event.
	 * 
	 * @see #getType
	 */
	private final int				type;
	/**
	 * The factory pid associated with this event.
	 */
	private final String			factoryPid;
	/**
	 * The pid associated with this event.
	 */
	private final String			pid;
	/**
	 * The ConfigurationAdmin service which created this event.
	 */
	private final ServiceReference	reference;

	/**
	 * Constructs a <tt>ConfigurationEvent</tt> object from the given
	 * <tt>ServiceReference</tt> object, event type, and pids.
	 * 
	 * @param reference The <tt>ServiceReference</tt> object of the
	 *        Configuration Admin service that created this event.
	 * @param type The event type. See {@link #getType}.
	 * @param factoryPid The factory pid of the associated configuration if the
	 *        target of the configuration is a ManagedServiceFactory. Otherwise
	 *        <tt>null</tt> if the target of the configuration is a
	 *        ManagedService.
	 * @param pid The pid of the associated configuration.
	 */
	public ConfigurationEvent(ServiceReference reference, int type,
			String factoryPid, String pid) {
		this.reference = reference;
		this.type = type;
		this.factoryPid = factoryPid;
		this.pid = pid;
	}

	/**
	 * Returns the factory pid of the associated configuration.
	 * 
	 * @return Returns the factory pid of the associated configuration if the
	 *         target of the configuration is a ManagedServiceFactory. Otherwise
	 *         <tt>null</tt> if the target of the configuration is a
	 *         ManagedService.
	 */
	public String getFactoryPid() {
		return factoryPid;
	}

	/**
	 * Returns the pid of the associated configuration.
	 * 
	 * @return Returns the pid of the associated configuration.
	 */
	public String getPid() {
		return pid;
	}

	/**
	 * Return the type of this event.
	 * <p>
	 * The type values are:
	 * <ul>
	 * <li>{@link #CM_UPDATED}
	 * <li>{@link #CM_DELETED}
	 * </ul>
	 * 
	 * @return The type of this event.
	 */
	public int getType() {
		return type;
	}

	/**
	 * Return the <tt>ServiceReference</tt> object of the Configuration Admin
	 * service that created this event.
	 * 
	 * @return The <tt>ServiceReference</tt> object for the Configuration
	 *         Admin service that created this event.
	 */
	public ServiceReference getReference() {
		return reference;
	}
}