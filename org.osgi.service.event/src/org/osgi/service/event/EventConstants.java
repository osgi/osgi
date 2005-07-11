/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package org.osgi.service.event;

import org.osgi.framework.Constants;

/**
 * 
 * Defines standard names for <code>EventHandler</code> properties.
 * 
 * @version $Revision$
 */
public interface EventConstants {

	/**
	 * Service registration property (named <code>event.topic</code>)
	 * specifying the <code>Event</code> topics of interest to a Event Handler
	 * service.
	 * <p>
	 * Event handlers SHOULD be registered with this property. The value of the
	 * property is an array of strings that describe the topics in which the
	 * handler is interested. An asterisk ('*') may be used as a
	 * trailing wildcard. Event handlers which do not have a value for this
	 * propery are treated as though they had specified this property with the
	 * value { '*' }. More precisely, the value of each entry in the
	 * array must conform to the following grammar:
	 * 
	 * <pre>
	 *          topic-description := '*' | topic ( '/*' )?
	 *          topic := token ( '/' token )*
	 * </pre>
	 * 
	 * @see Event
	 */
	public static final String	EVENT_TOPIC			= "event.topics";

	/**
	 * Service Registration property (named <code>event.filter</code>)
	 * specifying a filter to further select <code>Event</code> s of interest
	 * to a Event Handler service.
	 * <p>
	 * Event handlers MAY be registered with this property. The value of this
	 * property is a string containing an LDAP-style filter specification. Any
	 * of the event's properties may be used in the filter expression. Each
	 * event handler is notified for any event which belongs to the topics in
	 * which the handler has expressed an interest. If the event handler is also
	 * registered with this service property, then the properties of the event
	 * must also match the filter for the event to be delivered to the event
	 * handler.
	 * <p>
	 * If the filter syntax is invalid, then the Event Handler must be ignored and a 
	 * warning should be logged.
	 * 
	 * @see Event
	 * @see org.osgi.framework.Filter
	 */
	public static final String	EVENT_FILTER		= "event.filter";

	/**
	 * The Distinguished Name of the bundle relevant to the event.
	 */
	public static final String	BUNDLE_SIGNER		= "bundle.signer";

	/**
	 * The Bundle Symbolic Name of the bundle relevant to the event.
	 */
	public static final String	BUNDLE_SYMBOLICNAME	= "bundle.symbolicName";

	/**
	 * The actual event object. Used when rebroadcasting an event that was sent
	 * via some other event mechanism.
	 */
	public static final String	EVENT				= "event";

	/**
	 * An exception or error.
	 */
	public static final String	EXCEPTION			= "exception";

	/**
	 * Must be equal to the name of the Exception class.
	 */
	public static final String	EXECPTION_CLASS		= "exception.class";

	/**
	 * Must be equal to exception.getMessage()
	 */
	public static final String	EXCEPTION_MESSAGE	= "exception.message";

	/**
	 * A human-readable message that is usually not localized.
	 */
	public static final String	MESSAGE				= "message";

	/**
	 * A service
	 */

	public static final String	SERVICE				= "service";

	/**
	 * A service’s id.
	 */
	public static final String	SERVICE_ID			= Constants.SERVICE_ID;

	/**
	 * 
	 * A service's objectClass
	 */
	public static final String	SERVICE_OBJECTCLASS	= "service.objectClass";

	/**
	 * 
	 * A service’s persistent identity.
	 */
	public static final String	SERVICE_PID			= Constants.SERVICE_PID;

	/**
	 * 
	 * The time when the event occurred, as reported by
	 * System.currentTimeMillis()
	 */
	public static final String	TIMESTAMP			= "timestamp";

}