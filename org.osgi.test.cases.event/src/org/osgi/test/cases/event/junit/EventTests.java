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
import org.osgi.service.event.EventProperties;
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
		assertConstant("event.topics", "EVENT_TOPIC", EventConstants.class);
		String t1 = "a/b";
		Map m1 = new HashMap();
		m1.put("foo", "bar");
		String[] a1 = new String[] {"foo", "bar"};
		m1.put("baz", a1);
		m1.put(Boolean.TRUE, "baz"); // non-string key must not cause error
		m1.put("event.topics", "b/a"); // event.topics in properties is
										// overridden
		Hashtable m2 = new Hashtable();
		m2.put("foo", "bar");
		m2.put("baz", a1);
		m2.put("event.topics", "b/a"); // event.topics in properties is
										// overridden
		Dictionary m3 = new Hashtable();
		m3.put("foo", "bar");
		m3.put("baz", a1);
		m3.put("event.topics", "b/a"); // event.topics in properties is
										// overridden

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

		assertEquals("foo property value != bar", "bar", e1.getProperty("foo"));
		assertEquals("foo property value != bar", "bar", e2.getProperty("foo"));
		assertEquals("foo property value != bar", "bar", e3.getProperty("foo"));

		assertEquals("wrong amount of properties", 3,
				e1.getPropertyNames().length);
		assertEquals("wrong amount of properties", 3,
				e2.getPropertyNames().length);
		assertEquals("wrong amount of properties", 3,
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

	public void testEventsEquals() {
		String t1 = "a/b";
		Map m1 = new HashMap();
		m1.put("foo", "bar");
		m1.put("baz", new String[] {"foo", "bar"});
		m1.put("event.topics", "b/a"); // event.topics in properties is
		// overridden
		Hashtable m2 = new Hashtable();
		m2.put("foo", "bar");
		m2.put("baz", new String[] {"foo", "bar"});
		m2.put("event.topics", "b/a"); // event.topics in properties is
		// overridden
		Dictionary m3 = new Hashtable();
		m3.put("foo", "bar");
		m3.put("baz", new String[] {"foo", "bar"});
		m3.put("event.topics", "b/a"); // event.topics in properties is
		// overridden

		Event e1 = new Event(t1, m1);
		Event e2 = new Event(t1, (Dictionary) m2);
		Event e3 = new Event(t1, m3);

		assertFalse("events equal", e1.equals(e2));
		assertFalse("events equal", e1.equals(e3));
		assertFalse("events equal", e2.equals(e1));
		assertFalse("events equal", e2.equals(e3));
		assertFalse("events equal", e3.equals(e1));
		assertFalse("events equal", e3.equals(e2));

		assertFalse("event hashcodes equal", e1.hashCode() == e2.hashCode());
		assertFalse("event hashcodes equal", e1.hashCode() == e3.hashCode());
		assertFalse("event hashcodes equal", e2.hashCode() == e1.hashCode());
		assertFalse("event hashcodes equal", e2.hashCode() == e3.hashCode());
		assertFalse("event hashcodes equal", e3.hashCode() == e1.hashCode());
		assertFalse("event hashcodes equal", e3.hashCode() == e2.hashCode());

	}

	public void testContainsProperty() {
		String t1 = "a/b";
		Map m1 = new HashMap();
		m1.put("foo", null);
		String[] a1 = new String[] {"foo", "bar"};
		m1.put("baz", a1);
		m1.put(Boolean.TRUE, "baz"); // non-string key must not cause error
		Hashtable m2 = new Hashtable();
		m2.put("foo", "bar");
		m2.put("baz", a1);
		Dictionary m3 = new Hashtable();
		m3.put("foo", "bar");
		m3.put("baz", a1);

		Event e1 = new Event(t1, m1);
		Event e2 = new Event(t1, (Dictionary) m2);
		Event e3 = new Event(t1, m3);
		assertTrue("foo property not in properties", e1.containsProperty("foo"));
		assertTrue("foo property not in properties", e2.containsProperty("foo"));
		assertTrue("foo property not in properties", e3.containsProperty("foo"));

		assertFalse("bar property not in properties", e1
				.containsProperty("bar"));
		assertFalse("bar property not in properties", e2
				.containsProperty("bar"));
		assertFalse("bar property not in properties", e3
				.containsProperty("bar"));
	}

	public void testEventProperties() throws Exception {
		String t1 = "a/b";
		Map m1 = new HashMap();
		m1.put("foo", "bar");
		String[] a1 = new String[] {"foo", "bar"};
		m1.put("baz", a1);
		m1.put(Boolean.TRUE, "baz"); // non-string key must not cause error
		m1.put("event.topics", "b/a"); // event.topics in properties is
		// overridden

		EventProperties p1 = new EventProperties(m1);
		EventProperties p2 = new EventProperties(m1);

		assertTrue("eventproperties not equal", p1.equals(p2));
		assertTrue("eventproperties not equal", p2.equals(p1));

		assertEquals("eventproperties hashcodes not equal", p1.hashCode(), p2
				.hashCode());
		assertEquals("eventproperties hashcodes not equal", p2.hashCode(), p1
				.hashCode());
		assertEquals("wrong amount of properties", 2, p1.size());
		assertEquals("wrong amount of properties", 2, p2.size());
		assertEquals("wrong amount of properties", 2, p1.keySet().size());
		assertEquals("wrong amount of properties", 2, p2.keySet().size());
		assertEquals("wrong amount of properties", 2, p1.values().size());
		assertEquals("wrong amount of properties", 2, p2.values().size());
		assertEquals("wrong amount of properties", 2, p1.entrySet().size());
		assertEquals("wrong amount of properties", 2, p2.entrySet().size());
		assertFalse("empty", p1.isEmpty());
		assertFalse("empty", p2.isEmpty());
		assertTrue("not empty", new EventProperties(null).isEmpty());

		try {
			p1.clear();
			fail("did not throw exception");
		}
		catch (UnsupportedOperationException e) {
			// expected
		}

		try {
			p1.put("xxx", "zzz");
			fail("did not throw exception");
		}
		catch (UnsupportedOperationException e) {
			// expected
		}

		try {
			p1.putAll(m1);
			fail("did not throw exception");
		}
		catch (UnsupportedOperationException e) {
			// expected
		}

		try {
			p1.remove("foo");
			fail("did not throw exception");
		}
		catch (UnsupportedOperationException e) {
			// expected
		}
		try {
			p1.keySet().clear();
			fail("did not throw exception");
		}
		catch (UnsupportedOperationException e) {
			// expected
		}
		try {
			p1.values().clear();
			fail("did not throw exception");
		}
		catch (UnsupportedOperationException e) {
			// expected
		}
		try {
			p1.entrySet().clear();
			fail("did not throw exception");
		}
		catch (UnsupportedOperationException e) {
			// expected
		}

		assertFalse("contains event.topics key", p1.containsKey("event.topics"));
		assertTrue("foo key not present", p1.containsKey("foo"));
		assertFalse("baz value not present", p1.containsValue("baz"));
		assertTrue("bar value not present", p1.containsValue("bar"));

		Event e1 = new Event(t1, p1);
		Event e2 = new Event(t1, p1);
		Event e3 = new Event(t1, p1);

		assertEquals("topic not set", new String(t1), e1.getTopic());
		assertEquals("topic not set", new String(t1), e2.getTopic());
		assertEquals("topic not set", new String(t1), e3.getTopic());

		assertEquals("topic not in properties", t1, e1
				.getProperty("event.topics"));
		assertEquals("topic not in properties", t1, e2
				.getProperty("event.topics"));
		assertEquals("topic not in properties", t1, e3
				.getProperty("event.topics"));

		assertEquals("foo property value != bar", "bar", e1.getProperty("foo"));
		assertEquals("foo property value != bar", "bar", e2.getProperty("foo"));
		assertEquals("foo property value != bar", "bar", e3.getProperty("foo"));

		assertEquals("wrong amount of properties", 3,
				e1.getPropertyNames().length);
		assertEquals("wrong amount of properties", 3,
				e2.getPropertyNames().length);
		assertEquals("wrong amount of properties", 3,
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

		Filter f1 = FrameworkUtil.createFilter("(baz=bar)");
		assertTrue("filter does not match", e1.matches(f1));
		assertTrue("filter does not match", e2.matches(f1));
		assertTrue("filter does not match", e3.matches(f1));

		Filter f2 = FrameworkUtil.createFilter("(event.topics=" + t1 + ")");
		assertTrue("filter does not match", e1.matches(f2));
		assertTrue("filter does not match", e2.matches(f2));
		assertTrue("filter does not match", e3.matches(f2));

		// change the value object to verify all events are sharing the same
		// EventProperties.
		Filter f3 = FrameworkUtil.createFilter("(baz=baz)");
		assertFalse("filter does match", e1.matches(f3));
		assertFalse("filter does match", e2.matches(f3));
		assertFalse("filter does match", e3.matches(f3));
		a1[1] = "baz";
		assertTrue("filter does not match", e1.matches(f3));
		assertTrue("filter does not match", e2.matches(f3));
		assertTrue("filter does not match", e3.matches(f3));

	}

}
