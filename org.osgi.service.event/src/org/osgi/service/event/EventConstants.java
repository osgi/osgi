/*
 * Copyright (c) OSGi Alliance (2005, 2018). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.service.event;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;

/**
 * Defines standard names for {@code EventHandler} properties.
 * 
 * @author $Id$
 */
@ProviderType
public interface EventConstants {

	/**
	 * Service registration property specifying the {@code Event} topics of
	 * interest to an Event Handler service.
	 * <p>
	 * Event handlers SHOULD be registered with this property. Each value of
	 * this property is a string that describe the topics in which the handler
	 * is interested. An asterisk ('*') may be used as a trailing wildcard.
	 * Event Handlers which do not have a value for this property must not
	 * receive events. More precisely, the value of each string must conform to
	 * the following grammar:
	 * 
	 * <pre>
	 *  topic-description := '*' | topic ( '/*' )?
	 *  topic := token ( '/' token )*
	 * </pre>
	 * 
	 * <p>
	 * The value of this property must be of type {@code String},
	 * {@code String[]}, or {@code Collection<String>}.
	 * 
	 * @see Event
	 */
	public static final String	EVENT_TOPIC					= "event.topics";

	/**
	 * Service Registration property specifying a filter to further select
	 * {@code Event} s of interest to an Event Handler service.
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
	 * If the filter syntax is invalid, then the Event Handler must be ignored
	 * and a warning should be logged.
	 * 
	 * <p>
	 * The value of this property must be of type {@code String}.
	 * 
	 * @see Event
	 * @see Filter
	 */
	public static final String	EVENT_FILTER				= "event.filter";

	/**
	 * Service Registration property specifying the delivery qualities requested
	 * by an Event Handler service.
	 * <p>
	 * Event handlers MAY be registered with this property. Each value of this
	 * property is a string specifying a delivery quality for the Event handler.
	 * 
	 * <p>
	 * The value of this property must be of type {@code String},
	 * {@code String[]}, or {@code Collection<String>}.
	 * 
	 * @see #DELIVERY_ASYNC_ORDERED
	 * @see #DELIVERY_ASYNC_UNORDERED
	 * @since 1.3
	 */
	public static final String	EVENT_DELIVERY				= "event.delivery";

	/**
	 * Event Handler delivery quality value specifying the Event Handler
	 * requires asynchronously delivered events be delivered in order. Ordered
	 * delivery is the default for asynchronously delivered events.
	 * 
	 * <p>
	 * This delivery quality value is mutually exclusive with
	 * {@link #DELIVERY_ASYNC_UNORDERED}. However, if both this value and
	 * {@link #DELIVERY_ASYNC_UNORDERED} are specified for an event handler,
	 * this value takes precedence.
	 * 
	 * @see #EVENT_DELIVERY
	 * @since 1.3
	 */
	public static final String	DELIVERY_ASYNC_ORDERED		= "async.ordered";

	/**
	 * Event Handler delivery quality value specifying the Event Handler does
	 * not require asynchronously delivered events be delivered in order. This
	 * may allow an Event Admin implementation to optimize asynchronous event
	 * delivery by relaxing ordering requirements.
	 * 
	 * <p>
	 * This delivery quality value is mutually exclusive with
	 * {@link #DELIVERY_ASYNC_ORDERED}. However, if both this value and
	 * {@link #DELIVERY_ASYNC_ORDERED} are specified for an event handler,
	 * {@link #DELIVERY_ASYNC_ORDERED} takes precedence.
	 * 
	 * @see #EVENT_DELIVERY
	 * @since 1.3
	 */
	public static final String	DELIVERY_ASYNC_UNORDERED	= "async.unordered";

	/**
	 * The Distinguished Names of the signers of the bundle relevant to the
	 * event. The type of the value for this event property is {@code String} or
	 * {@code Collection} of {@code String}.
	 */
	public static final String	BUNDLE_SIGNER				= "bundle.signer";

	/**
	 * The Bundle Symbolic Name of the bundle relevant to the event. The type of
	 * the value for this event property is {@code String}.
	 */
	public static final String	BUNDLE_SYMBOLICNAME			= "bundle.symbolicName";

	/**
	 * The Bundle id of the bundle relevant to the event. The type of the value
	 * for this event property is {@code Long}.
	 * 
	 * @since 1.1
	 */
	public static final String	BUNDLE_ID					= "bundle.id";

	/**
	 * The Bundle object of the bundle relevant to the event. The type of the
	 * value for this event property is {@link Bundle}.
	 * 
	 * @since 1.1
	 */
	public static final String	BUNDLE						= "bundle";

	/**
	 * The version of the bundle relevant to the event. The type of the value
	 * for this event property is {@link Version}.
	 * 
	 * @since 1.2
	 */
	public static final String	BUNDLE_VERSION				= "bundle.version";

	/**
	 * The forwarded event object. Used when rebroadcasting an event that was
	 * sent via some other event mechanism. The type of the value for this event
	 * property is {@code Object}.
	 */
	public static final String	EVENT						= "event";

	/**
	 * An exception or error. The type of the value for this event property is
	 * {@code Throwable}.
	 */
	public static final String	EXCEPTION					= "exception";

	/**
	 * The name of the exception type. Must be equal to the name of the class of
	 * the exception in the event property {@link #EXCEPTION}. The type of the
	 * value for this event property is {@code String}.
	 * 
	 * @since 1.1
	 */
	public static final String	EXCEPTION_CLASS				= "exception.class";

	/**
	 * The exception message. Must be equal to the result of calling
	 * {@code getMessage()} on the exception in the event property
	 * {@link #EXCEPTION}. The type of the value for this event property is
	 * {@code String}.
	 */
	public static final String	EXCEPTION_MESSAGE			= "exception.message";

	/**
	 * A human-readable message that is usually not localized. The type of the
	 * value for this event property is {@code String}.
	 */
	public static final String	MESSAGE						= "message";

	/**
	 * A service reference. The type of the value for this event property is
	 * {@link ServiceReference}.
	 */
	public static final String	SERVICE						= "service";

	/**
	 * A service's id. The type of the value for this event property is
	 * {@code Long}.
	 */
	public static final String	SERVICE_ID					= Constants.SERVICE_ID;

	/**
	 * A service's objectClass. The type of the value for this event property is
	 * {@code String[]}.
	 */
	public static final String	SERVICE_OBJECTCLASS			= "service.objectClass";

	/**
	 * A service's persistent identity. The type of the value for this event
	 * property is {@code String} or {@code Collection} of {@code String}.
	 */
	public static final String	SERVICE_PID					= Constants.SERVICE_PID;

	/**
	 * The time when the event occurred, as reported by
	 * {@code System.currentTimeMillis()}. The type of the value for this event
	 * property is {@code Long}.
	 */
	public static final String	TIMESTAMP					= "timestamp";

	/**
	 * The name of the implementation capability for the Event Admin
	 * specification
	 * 
	 * @since 1.4
	 */
	public static final String	EVENT_ADMIN_IMPLEMENTATION			= "osgi.event";
	/**
	 * The version of the implementation capability for the Event Admin
	 * specification
	 * 
	 * @since 1.4
	 */
	public static final String	EVENT_ADMIN_SPECIFICATION_VERSION	= "1.4.0";

	/**
	 * This constant was released with an incorrectly spelled name. It has been
	 * replaced by {@link #EXCEPTION_CLASS}
	 * 
	 * @deprecated As of 1.1. Replaced by {@link #EXCEPTION_CLASS}.
	 */
	public static final String	EXECPTION_CLASS				= "exception.class";
}
