/*
 * Copyright (c) OSGi Alliance (2009). All Rights Reserved.
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

package org.osgi.test.cases.event.junit;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.test.support.OSGiTestCase;

public class EventTests extends OSGiTestCase {

	public void testIllegalTopics() {
		String[] illegalTopics = new String[] {"", "*", "/error_topic",
				"//error_topic1", "é/error_topic2", "topic/error_topic3/",
				"error_topic&", "topic//error-topic4", "topic\\error_topic5"};
		Hashtable properties = new Hashtable();
		// illegal topics tested
		for (int i = 0; i < illegalTopics.length; i++) {
			String topic = illegalTopics[i];
			try {
				new Event(topic, (Dictionary) properties);
				fail("Excepted IllegalArgumentException for topic:[" + topic
						+ "]");
			}
			catch (IllegalArgumentException e) {
				// expected
			}
		}
	}

	public void testTopics() throws Exception {
		String[] legalTopics = new String[] {"ACTION0",
				"org/osgi/test/cases/event/ACTION1",
				"org/osgi/test/cases/event/ACTION2", "0", "_/-", "_topic",
				"-topic", "9topic0/-topic_/_topic-"};
		// legal topics tested
		for (int i = 0; i < legalTopics.length; i++) {
			String topic = legalTopics[i];
			Event event = null;
			try {
				event = new Event(topic, (Dictionary) null);
			}
			catch (Throwable e) {
				fail("Exception in event construction with topic:[" + topic
						+ "]", e);
			}
			assertEquals("topic not set", new String(topic), event.getTopic());
			assertEquals("topic not in properties", topic, event
					.getProperty("event.topics"));
			assertEquals("wrong amount of properties", 1, event
					.getPropertyNames().length);
			assertTrue("filter does not match", event.matches(FrameworkUtil
					.createFilter("(event.topics=" + topic + ")")));
		}
	}

	public void testProperties() throws Exception {
		assertEquals("constant incorrect", "event.topics",
				EventConstants.EVENT_TOPIC);
		String t1 = "a/b";
		Map m1 = new HashMap();
		m1.put("foo", "bar");
		m1.put(Boolean.TRUE, "baz"); // non-string key must not cause error
		Hashtable m2 = new Hashtable();
		m2.put("foo", "bar");
		Dictionary m3 = new Hashtable();
		m3.put("foo", "bar");

		Event e1 = new Event(t1, m1);
		Event e2 = new Event(t1, (Dictionary) m2);
		Event e3 = new Event(t1, m3);

		assertEquals("topic not set", new String(t1), e1.getTopic());
		assertEquals("topic not set", new String(t1), e2.getTopic());
		assertEquals("topic not set", new String(t1), e3.getTopic());

		assertEquals("topic not in properties", t1, e1
				.getProperty("event.topics"));
		assertEquals("topic not in properties", t1, e2
				.getProperty("event.topics"));
		assertEquals("topic not in properties", t1, e3
				.getProperty("event.topics"));

		assertEquals("bar", e1.getProperty("foo"));
		assertEquals("bar", e2.getProperty("foo"));
		assertEquals("bar", e3.getProperty("foo"));

		assertEquals("wrong amount of properties", 2,
				e1.getPropertyNames().length);
		assertEquals("wrong amount of properties", 2,
				e2.getPropertyNames().length);
		assertEquals("wrong amount of properties", 2,
				e3.getPropertyNames().length);

		assertTrue("events not equal", e1.equals(e2));
		assertTrue("events not equal", e1.equals(e3));
		assertTrue("events not equal", e2.equals(e1));
		assertTrue("events not equal", e2.equals(e3));
		assertTrue("events not equal", e3.equals(e1));
		assertTrue("events not equal", e3.equals(e2));

		assertEquals("event hashcodes not equal", e1.hashCode(), e2.hashCode());
		assertEquals("event hashcodes not equal", e1.hashCode(), e3.hashCode());
		assertEquals("event hashcodes not equal", e2.hashCode(), e1.hashCode());
		assertEquals("event hashcodes not equal", e2.hashCode(), e3.hashCode());
		assertEquals("event hashcodes not equal", e3.hashCode(), e1.hashCode());
		assertEquals("event hashcodes not equal", e3.hashCode(), e2.hashCode());

		Filter f1 = FrameworkUtil.createFilter("(foo=bar)");
		assertTrue("filter does not match", e1.matches(f1));
		assertTrue("filter does not match", e2.matches(f1));
		assertTrue("filter does not match", e3.matches(f1));

		Filter f2 = FrameworkUtil.createFilter("(event.topics=" + t1 + ")");
		assertTrue("filter does not match", e1.matches(f2));
		assertTrue("filter does not match", e2.matches(f2));
		assertTrue("filter does not match", e3.matches(f2));
	}
}
