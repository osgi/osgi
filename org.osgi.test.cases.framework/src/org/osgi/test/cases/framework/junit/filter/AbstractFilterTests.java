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

package org.osgi.test.cases.framework.junit.filter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.test.support.OSGiTestCase;

public abstract class AbstractFilterTests extends OSGiTestCase {

	static final int ISTRUE = 1;
	static final int ISFALSE = 2;
	static final int ISILLEGAL = 3;

	public abstract Filter createFilter(String filterString)
			throws InvalidSyntaxException;
	
	private Dictionary getProperties() {
		Dictionary props = new Hashtable();
		props.put("room", "bedroom");
		props.put("channel", new Object[] {new Integer(34), "101"});
		props.put("status", "(on\\)*"); 
		List vec = new ArrayList(10);
		vec.add(new Long(150));
		vec.add("100");
		props.put("max record time", vec);
		props.put("canrecord", "true(x)");
		props.put("shortvalue", new Short((short) 1000));
		props.put("intvalue", new Integer(100000));
		props.put("longvalue", new Long(10000000000L));
		props.put("bytevalue", new Byte((byte) 10));
		props.put("floatvalue", new Float(1.01));
		props.put("doublevalue", new Double(2.01));
		props.put("charvalue", new Character('A'));
		props.put("booleanvalue", new Boolean(false));
		props.put("weirdvalue", new Hashtable());
		props.put("primintarrayvalue", new int[] {1, 2, 3});
		props.put("primlongarrayvalue", new long[] {1, 2, 3});
		props.put("primbytearrayvalue", new byte[] {(byte) 1, (byte) 2,
				(byte) 3});
		props.put("primshortarrayvalue", new short[] {(short) 1, (short) 2,
				(short) 3});
		props.put("primfloatarrayvalue", new float[] {(float) 1.1, (float) 2.2,
				(float) 3.3});
		props.put("primdoublearrayvalue", new double[] {1.1, 2.2, 3.3});
		props.put("primchararrayvalue", new char[] {'A', 'b', 'C', 'd'});
		props.put("primbooleanarrayvalue", new boolean[] {false}); 
		props.put("bigintvalue", new BigInteger("4123456"));
		props.put("bigdecvalue", new BigDecimal("4.123456"));   
		props.put("*", "foo");
		props.put("!  ab", "b");
		props.put("|   ab", "b");
		props.put("&    ab", "b");
		return props;
	}

	public void testFilter() {
		Dictionary props = getProperties();
		testFilter("(room=*)", props, ISTRUE);
		testFilter("(room=bedroom)", props, ISTRUE);
		testFilter("(room~= B E D R O O M )", props, ISTRUE);
		testFilter("(room=abc)", props, ISFALSE);
		testFilter(" ( room >=aaaa)", props, ISTRUE);
		testFilter("(room <=aaaa)", props, ISFALSE);
		testFilter("  ( room =b*) ", props, ISTRUE);
		testFilter("  ( room =*m) ", props, ISTRUE);
		testFilter("(room=bed*room)", props, ISTRUE);
		testFilter("  ( room =b*oo*m) ", props, ISTRUE);
		testFilter("  ( room =*b*oo*m*) ", props, ISTRUE);
		testFilter("  ( room =b*b*  *m*) ", props, ISFALSE);
		testFilter("  (& (room =bedroom) (channel ~=34))", props, ISTRUE);
		testFilter("  (&  (room =b*)  (room =*x) (channel=34))", props, ISFALSE);
		testFilter("(| (room =bed*)(channel=222)) ", props, ISTRUE);
		testFilter("(| (room =boom*)(channel=101)) ", props, ISTRUE);
		testFilter("  (! (room =ab*b*oo*m*) ) ", props, ISTRUE);
		testFilter("  (status =\\(o*\\\\\\)\\*) ", props, ISTRUE);
		testFilter("  (canRecord =true\\(x\\)) ", props, ISTRUE);
		testFilter("(max Record Time <=140) ", props, ISTRUE);
		testFilter("(shortValue >=100) ", props, ISTRUE);
		testFilter("(intValue <=100001) ", props, ISTRUE);
		testFilter("(longValue >=10000000000) ", props, ISTRUE);
		testFilter(
				"  (  &  (  byteValue <=100)  (  byteValue >=10)  )  ",
				props, ISTRUE);
		testFilter("(weirdValue =100) ", props, ISFALSE);
		testFilter("(bigIntValue =4123456) ", props, ISTRUE);
		testFilter("(bigDecValue =4.123456) ", props, ISTRUE);
		testFilter("(floatValue >=1.0) ", props, ISTRUE);
		testFilter("(doubleValue <=2.011) ", props, ISTRUE);
		testFilter("(charValue ~=a) ", props, ISTRUE);
		testFilter("(booleanValue =false) ", props, ISTRUE);
		testFilter("(primIntArrayValue =1) ", props, ISTRUE);
		testFilter("(primLongArrayValue =2) ", props, ISTRUE);
		testFilter("(primByteArrayValue =3) ", props, ISTRUE);
		testFilter("(primShortArrayValue =1) ", props, ISTRUE);
		testFilter("(primFloatArrayValue =1.1) ", props, ISTRUE);
		testFilter("(primDoubleArrayValue =2.2) ", props, ISTRUE);
		testFilter("(primCharArrayValue ~=D) ", props, ISTRUE);
		testFilter("(primBooleanArrayValue =false) ", props, ISTRUE);
		testFilter(
				"(& (| (room =d*m) (room =bed*) (room=abc)) (! (channel=999)))",
				props, ISTRUE);
		testFilter("(room=bedroom)", null, ISFALSE); 
		testFilter("(*=foo)", props, ISTRUE);
		testFilter("(!  ab=b)", props, ISTRUE);
		testFilter("(|   ab=b)", props, ISTRUE);
		testFilter("(&    ab=b)", props, ISTRUE);
		testFilter("(!ab=*)", props, ISFALSE);
		testFilter("(|ab=*)", props, ISFALSE);
		testFilter("(&ab=*)", props, ISFALSE);
	}

	public void testIllegal() {
		Dictionary props = getProperties();
		testFilter("", props, ISILLEGAL);
		testFilter("()", props, ISILLEGAL);
		testFilter("(=foo)", props, ISILLEGAL);
		testFilter("(", props, ISILLEGAL);
		testFilter("(abc = ))", props, ISILLEGAL);
		testFilter("(& (abc = xyz) (& (345))", props, ISILLEGAL);
		testFilter("  (room = b**oo!*m*) ) ", props, ISILLEGAL);
		testFilter("  (room = b**oo)*m*) ) ", props, ISILLEGAL);
		testFilter("  (room = *=b**oo*m*) ) ", props, ISILLEGAL);
		testFilter("  (room = =b**oo*m*) ) ", props, ISILLEGAL); 
	}

	public void testScalarSubstring() {
		Dictionary props = getProperties();
		testFilter("(shortValue =100*) ", props, ISFALSE);
		testFilter("(intValue =100*) ", props, ISFALSE);
		testFilter("(longValue =100*) ", props, ISFALSE);
		testFilter("(  byteValue =1*00  )", props, ISFALSE);
		testFilter("(bigIntValue =4*23456) ", props, ISFALSE);
		testFilter("(bigDecValue =4*123456) ", props, ISFALSE);
		testFilter("(floatValue =1*0) ", props, ISFALSE);
		testFilter("(doubleValue =2*011) ", props, ISFALSE);
		testFilter("(charValue =a*) ", props, ISFALSE);
		testFilter("(booleanValue =f*lse) ", props, ISFALSE);
	}

	public void testNormalization() {
		try {
			Filter f1 = createFilter("( a = bedroom  )");
			Filter f2 = createFilter(" (a= bedroom  ) ");
			assertEquals("not equal", "(a= bedroom  )", f1.toString());
			assertEquals("not equal", "(a= bedroom  )", f2.toString());
			assertEquals("not equal", f1, f2);
			assertEquals("not equal", f2, f1);
			assertEquals("not equal", f1.hashCode(), f2.hashCode()); 
		} catch (InvalidSyntaxException e) {
			fail("unexpected invalid syntax", e); 
		}
		
	}

	private void testFilter(String query, Dictionary props, int expect) {
		final ServiceReference ref = new DictionaryServiceReference(props);
		Filter f1;
		try {
			f1 = createFilter(query);

			if (expect == ISILLEGAL) {
				fail("expected exception"); 
			}
		} catch (InvalidSyntaxException e) {
			if (expect != ISILLEGAL) {
				fail("exception", e); 
			}
			return;
		}

		boolean val = f1.match(props);
		assertEquals("wrong result", expect == ISTRUE, val); 

		val = f1.match(ref);
		assertEquals("wrong result", expect == ISTRUE, val); 
		
		String normalized = f1.toString();
		Filter f2;
		try {
			f2 = createFilter(normalized);
		}
		catch (InvalidSyntaxException e) {
			fail("exception", e); 
			return;
		}

		val = f2.match(props);
		assertEquals("wrong result", expect == ISTRUE, val); 

		val = f2.match(ref);
		assertEquals("wrong result", expect == ISTRUE, val); 
		
		assertEquals("normalized not equal", normalized, f2.toString());
		
	}

	public void testComparable() {
		Filter f1 = null;
		Object comp42 = new SampleComparable("42");
		Object comp43 = new SampleComparable("43");
		Hashtable hash = new Hashtable();

		try {
			f1 = createFilter("(comparable=42)"); 
		} catch (InvalidSyntaxException e) {
			fail("invalid syntax", e); 
		}

		hash.put("comparable", comp42);
		assertTrue("does not match filter", f1.match(hash));
		assertTrue("does not match filter", f1
				.match(new DictionaryServiceReference(hash))); 

		hash.put("comparable", comp43);
		assertFalse("does match filter", f1.match(hash));
		assertFalse("does match filter", f1
				.match(new DictionaryServiceReference(hash))); 

		try {
			f1 = createFilter("(comparable<=42)");
		}
		catch (InvalidSyntaxException e) {
			fail("invalid syntax", e);
		}

		hash.put("comparable", comp42);
		assertTrue("does not match filter", f1.match(hash));
		assertTrue("does not match filter", f1
				.match(new DictionaryServiceReference(hash)));

		hash.put("comparable", comp43);
		assertFalse("does match filter", f1.match(hash));
		assertFalse("does match filter", f1
				.match(new DictionaryServiceReference(hash))); 

		try {
			f1 = createFilter("(comparable>=42)");
		}
		catch (InvalidSyntaxException e) {
			fail("invalid syntax", e);
		}

		hash.put("comparable", comp42);
		assertTrue("does not match filter", f1.match(hash));
		assertTrue("does not match filter", f1
				.match(new DictionaryServiceReference(hash)));

		hash.put("comparable", comp43);
		assertTrue("does not match filter", f1.match(hash));
		assertTrue("does not match filter", f1
				.match(new DictionaryServiceReference(hash))); 

		try {
			f1 = createFilter("(comparable=4*2)");
		}
		catch (InvalidSyntaxException e) {
			fail("invalid syntax", e);
		}
		
		hash.put("comparable", comp42);
		assertFalse("does match filter", f1.match(hash));
		assertFalse("does match filter", f1
				.match(new DictionaryServiceReference(hash))); 
	}

	public void testObject() {
		Filter f1 = null;
		Object obj42 = new SampleObject("42");
		Object obj43 = new SampleObject("43");
		Hashtable hash = new Hashtable();

		try {
			f1 = createFilter("(object=42)");
		}
		catch (InvalidSyntaxException e) {
			fail("invalid syntax", e);
		}

		hash.put("object", obj42);
		assertTrue("does not match filter", f1.match(hash));
		assertTrue("does not match filter", f1
				.match(new DictionaryServiceReference(hash)));

		hash.put("object", obj43);
		assertFalse("does match filter", f1.match(hash));
		assertFalse("does match filter", f1
				.match(new DictionaryServiceReference(hash)));

		try {
			f1 = createFilter("(object=4*2)");
		}
		catch (InvalidSyntaxException e) {
			fail("invalid syntax", e);
		}

		hash.put("object", obj42);
		assertFalse("does match filter", f1.match(hash));
		assertFalse("does match filter", f1
				.match(new DictionaryServiceReference(hash)));
	}
	
	public static class SampleComparable implements Comparable {
		private int value = -1;

		public SampleComparable(String value) {
			this.value = Integer.parseInt(value);
		}

		public int compareTo(Object o) {
			return value - ((SampleComparable) o).value;
		}

		public String toString() {
			return String.valueOf(value);
		}
	}

	public static class SampleObject {
		private int	value	= -1;

		public SampleObject(String value) {
			this.value = Integer.parseInt(value);
		}

		public boolean equals(Object o) {
			if (o instanceof SampleObject) {
				return value == ((SampleObject) o).value;
			}
			return false;
		}

		public String toString() {
			return String.valueOf(value);
		}
	}
	
	private static class DictionaryServiceReference implements ServiceReference {
		private final Dictionary dictionary;
		private final String[] keys;

		DictionaryServiceReference(Dictionary dictionary) {
			if (dictionary == null) {
				this.dictionary = null;
				this.keys = new String[] {};
				return;
			}
			this.dictionary = dictionary;
			List keyList = new ArrayList(dictionary.size());
			for (Enumeration e = dictionary.keys(); e.hasMoreElements();) {
				Object k = e.nextElement();
				if (k instanceof String) {
					String key = (String) k;
					for (Iterator i = keyList.iterator(); i.hasNext();) {
						if (key.equalsIgnoreCase((String) i.next())) {
							throw new IllegalArgumentException();
						}
					}
					keyList.add(key);
				}
			}
			this.keys = (String[]) keyList.toArray(new String[keyList.size()]);
		}

		public Object getProperty(String k) {
			for (int i = 0, length = keys.length; i < length; i++) {
				String key = keys[i];
				if (key.equalsIgnoreCase(k)) {
					return dictionary.get(key);
				}
			}
			return null;
		}

		public String[] getPropertyKeys() {
			return (String[]) keys.clone();
		}

		public int compareTo(Object reference) {
			throw new UnsupportedOperationException();
		}

		public Bundle getBundle() {
			throw new UnsupportedOperationException();
		}
		
		public Bundle[] getUsingBundles() {
			throw new UnsupportedOperationException();
		}

		public boolean isAssignableTo(Bundle bundle, String className) {
			throw new UnsupportedOperationException();
		}
	}
}
