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
 * Listener for Channel Events.
 * 
 * <p>
 * <code>ChannelListener</code> objects are registered with the Framework service
 * registry and are notified with a <code>ChannelEvent</code> object when a
 * channel event is broadcast.
 * <p>
 * <code>ChannelListener</code> objects can inspect the received
 * <code>ChannelEvent</code> object to determine its topic and properties.
 * 
 * <p>
 * <code>ChannelListener</code> objects should be registered with a service
 * property {@link EventConstants#EVENT_TOPIC} whose value is the list of
 * topics in which the channel listener is interesed.
 * <p>
 * For example:
 * 
 * <pre>
 * String[] topics = new String[] {&quot;org.osgi.topic&quot;, &quot;com.isv.*&quot;};
 * Hashtable ht = new Hashtable();
 * ht.put(EVENT_TOPIC, topics);
 * context.registerService(ChannelListener.class.getName(), this, ht);
 * </pre>
 * 
 * If a <code>ChannelListener</code> object is registered without a service
 * property {@link EventConstants#EVENT_TOPIC}, then the
 * <code>ChannelListener</code> will receive channel events of all topics.
 * 
 * <p>
 * Security Considerations. Bundles wishing to monitor <code>ChannelEvent</code>
 * objects will require <code>ServicePermission[ChannelListener,REGISTER]</code>
 * to register a <code>ChannelListener</code> service. The bundle must also have
 * <code>TopicPermission[topic,SUBSCRIBE]</code> for the topic specified in the
 * channel event in order to receive the event.
 * 
 * @see ChannelEvent
 * 
 * @version $Revision$
 */
public interface ChannelListener {
	/**
	 * Called by the {@link EventChannel} to notify the listener of an event.
	 * 
	 * @param event The event that occurred.
	 */
	void channelEvent(ChannelEvent event);
}