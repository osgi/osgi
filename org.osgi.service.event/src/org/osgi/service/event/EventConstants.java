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

package org.osgi.service.event;

/**
 * 
 * Defines standard names for <code>ChannelListener</code> properties.
 * 
 * @version $Revision$
 */
public interface EventConstants {

	/**
	 * Service Registration property (named <code>event.topic</code>) specifying
	 * the <code>ChannelEvent</code> topics of interest to a Channel Listener
	 * service.
	 * <p>
	 * Channel listeners SHOULD be registered with this property. The value of
	 * the property is an array of strings that describe which topics in which
	 * the listener is interested. An asterisk (&quot;*&quot;) may be used as a
	 * trailing wildcard. Channel listeners which do not have a value for this
	 * propery are treated as though they had specified this property with the
	 * value &quot;*&quot;. More precisely, the value of each entry in the
	 * array must conform to the following grammar:
	 * 
	 * <pre>
	 *     topic-description := &quot;*&quot; | topic &quot;.*&quot; | topic
	 *     topic := token ( &quot;.&quot; token )*
	 * </pre>
	 * 
	 * @see ChannelEvent
	 */
	public static final String	EVENT_TOPIC		= "event.topics";

	/**
	 * Service Registration property (named <code>event.filter</code>) specifying
	 * a filter to further select <code>ChannelEvent</code> s of interest to a
	 * Channel Listener service.
	 * <p>
	 * Channel listeners MAY be registered with this property. The value of this
	 * property is a string containing an LDAP-style filter specification. Any
	 * of the channel event's properties may be used in the filter expression.
	 * Each channel listener is notified for any channel event which belongs to
	 * the topics in which the listener has expressed an interest. If the
	 * channel listener is also registered with this service property, then the
	 * properties of the channel event must also match the filter for the
	 * channel event to be delivered to the channel listener.
	 * 
	 * @see ChannelEvent
	 * @see org.osgi.framework.Filter
	 */
	public static final String	EVENT_FILTER	= "event.filter";

}