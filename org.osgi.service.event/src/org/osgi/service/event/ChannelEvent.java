/*
 * Copyright (c) The Open Services Gateway Initiative (2004).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the Open Services Gateway Initiative
 * (OSGI) Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of OSGi). OSGi is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and OSGI DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL OSGI BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.service.event;

import java.util.*;
import org.osgi.framework.Filter;

public class ChannelEvent {
	private String		topic;
	private Hashtable	properties;

	public ChannelEvent(String topic, Map properties) {
		this.topic = topic;
		this.properties = new Hashtable();
		this.properties.put("topic", topic);
		if (properties != null)
			this.properties.putAll(properties);
	}

	public final Object getProperty(String name) {
		return properties.get(name);
	}

	public final String[] getPropertyNames() {
		String[] names = new String[properties.size()];
		int i = 0;
		Enumeration keys = properties.keys();
		for (i = 0; keys.hasMoreElements(); i++)
			names[i] = keys.nextElement().toString();
		return names;
	}

	public final String getTopic() {
		return topic;
	}

	public final boolean matches(Filter filter) {
		return filter.match(properties);
	}

	public boolean equals(Object obj) {
		if (obj instanceof ChannelEvent) {
			ChannelEvent evt = (ChannelEvent) obj;
			return topic.equals(evt.topic) && properties.equals(evt.properties);
		}
		return false;
	}

	public int hashCode() {
		return topic.hashCode() ^ properties.hashCode();
	}

	public String toString() {
		return topic.toString();
	}
}
