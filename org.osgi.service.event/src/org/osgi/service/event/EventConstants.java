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

/**
 * 
 * Defines standard names for <code>EventHandler</code> properties.
 * 
 * @version $Revision$
 */
public interface EventConstants {

	/**
	 * Service registration property (named <code>event.topic</code>) specifying
	 * the <code>Event</code> topics of interest to a Event Handler
	 * service.
	 * <p>
	 * Event handlers SHOULD be registered with this property. The value of
	 * the property is an array of strings that describe the topics in which
	 * the handler is interested. An asterisk (&quot;*&quot;) may be used as a
	 * trailing wildcard. Event handlers which do not have a value for this
	 * propery are treated as though they had specified this property with the
	 * value { &quot;*&quot; }. More precisely, the value of each entry in the
	 * array must conform to the following grammar:
	 * 
	 * <pre>
	 *     topic-description := &quot;*&quot; | topic ( &quot;/*&quot; )?
	 *     topic := token ( &quot;/&quot; token )*
	 * </pre>
	 * 
	 * @see Event
	 */
	public static final String	EVENT_TOPIC		= "event.topics";

	/**
	 * Service Registration property (named <code>event.filter</code>) specifying
	 * a filter to further select <code>Event</code> s of interest to a
	 * Event Handler service.
	 * <p>
	 * Event handlers MAY be registered with this property. The value of this
	 * property is a string containing an LDAP-style filter specification. Any
	 * of the event's properties may be used in the filter expression.
	 * Each event handler is notified for any event which belongs to
	 * the topics in which the handler has expressed an interest. If the
	 * event handler is also registered with this service property, then the
	 * properties of the event must also match the filter for the
	 * event to be delivered to the event handler.
	 * 
	 * @see Event
	 * @see org.osgi.framework.Filter
	 */
	public static final String	EVENT_FILTER	= "event.filter";

}