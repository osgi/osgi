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

import java.util.*;
import org.osgi.framework.Filter;



/**
 * Contains information regarding an event.<p>
 *
 * <i>NOTE: Although sub-classes are not precluded, the operations defined by this class MUST NOT be overridden.</i>
 *
 * @version $Revision$
 */
public class ChannelEvent {

	/**
	 * The topic of this event.
	 */
	private final String topic;

	/**
	 * The properties carried by this event. Keys are strings and values are objects
	 */
	private Hashtable properties;



	/**
     * Constructs an event.
     *
     * @param topic the topic of the event
     * @param properties the event's properties (may be null)
     *
     * @throws IllegalArgumentException if topic is not a valid topic name, or if properties contains case-variants of the same key
	 */
	public ChannelEvent(String topic, Dictionary properties) {
        // TODO: Verify that topic is well-formed
		this.topic = topic;

		this.properties = new Hashtable();

		if (properties != null) {
            for (Enumeration e = properties.keys(); e.hasMoreElements(); ) {
                String key   = (String) e.nextElement();
                Object value = properties.get(key);

                if (this.properties.put(key, value) != null) {
                    throw new IllegalArgumentException("Dictionary contains case-variants of the same key; key = " + key);
                }
            }
        }

        this.properties.put("topic", topic);
	}



	/**
     * Retrieves a property.
     *
     * @param name the name of the property to retrieve
     *
     * @return the value of the property, or null if not found
     *
     * @throws NullPointerException if name is null
	 */
	public final Object getProperty(String name) {
		return properties.get(name);
	}



	/**
     * Returns a list of this event's property names
     *
     * @return a non-empty array with one element per property
	 */
	public final String[] getPropertyNames() {
		String[] names = new String[properties.size()];

		Enumeration keys = properties.keys();
        for (int i = 0; keys.hasMoreElements(); i++) {
			names[i] = (String) keys.nextElement();
        }

		return names;
	}



	/**
     * Returns the topic of this event.
	 */
	public final String getTopic() {
		return topic;
	}



	/**
     * Tests this event's properties against the given filter.
     *
     * @param filter the filter to test
     *
     * @return true if this event's properties match the filter, false otherwise
     *
     * @throws NullPointerException if filter is null
	 */
	public final boolean matches(Filter filter) {
        // TODO: Case-insensitivity
		return filter.match(properties);
	}



	/**
     * Tests this object for equality with another object.
     *
	 * @param obj the other object to test against
     *
	 * @return true if this object is equivalent to obj
     *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if ((obj != null) && (obj.getClass() == this.getClass())) {
			ChannelEvent evt = (ChannelEvent) obj;
			return topic.equals(evt.topic) && properties.equals(evt.properties);
		}

		return false;
	}



	/**
	 * Returns a hash code for this object.
     *
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return topic.hashCode() ^ properties.hashCode();
	}



	/**
	 * Returns the string representation of this object.
     *
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getClass().getName() + "[topic=" + topic + "]";
	}
}