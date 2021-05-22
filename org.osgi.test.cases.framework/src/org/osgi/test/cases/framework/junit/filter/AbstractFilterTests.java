/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.test.cases.framework.junit.filter;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.AutoCloseableSoftAssertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.test.support.junit4.AbstractOSGiTestCase;
import org.osgi.test.support.mock.MockFactory;

public abstract class AbstractFilterTests extends AbstractOSGiTestCase {

	private static final Matcher nullMatcher = new Matcher() {
		public void matches(SoftAssertions softly, Filter f, boolean expected) {
			softly.assertThat(f.match((Dictionary<String, ? >) null))
					.as("\"%s\".match(null)", f)
					.isEqualTo(expected);
			softly.assertThat(f.matchCase((Dictionary<String, ? >) null))
					.as("\"%s\".matchCase(null)", f)
					.isEqualTo(expected);
			softly.assertThat(f.matches(null))
					.as("\"%s\".matches(null)", f)
					.isEqualTo(expected);
		}
	};

	public abstract Filter createFilter(String filterString)
			throws InvalidSyntaxException;

	Hashtable<String,Object> getProperties() {
		Hashtable<String,Object> props = new Hashtable<>();
		props.put("room", "bedroom");
		props.put("channel", new Object[] {
				Integer.valueOf(34), "101"
		});
		props.put("status", "(on\\)*");
		List<Object> list = new ArrayList<>(10);
		list.add(Long.valueOf(150));
		list.add("100");
		props.put("max record time", list);
		props.put("canrecord", "true(x)");
		props.put("shortvalue", Short.valueOf((short) 1000));
		props.put("intvalue", Integer.valueOf(100000));
		props.put("longvalue", Long.valueOf(10000000000L));
		props.put("bytevalue", Byte.valueOf((byte) 10));
		props.put("floatvalue", Float.valueOf(1.01f));
		props.put("doublevalue", Double.valueOf(2.01));
		props.put("charvalue", Character.valueOf('A'));
		props.put("booleanvalue", Boolean.valueOf(true));
		props.put("weirdvalue", new Hashtable<String,Object>());
		props.put("primintarrayvalue", new int[] {
				1, 2, 3
		});
		props.put("primlongarrayvalue", new long[] {
				1, 2, 3
		});
		props.put("primbytearrayvalue", new byte[] {
				(byte) 1, (byte) 2, (byte) 3
		});
		props.put("primshortarrayvalue", new short[] {
				(short) 1, (short) 2, (short) 3
		});
		props.put("primfloatarrayvalue", new float[] {
				(float) 1.1, (float) 2.2, (float) 3.3
		});
		props.put("primdoublearrayvalue", new double[] {
				1.1, 2.2, 3.3
		});
		props.put("primchararrayvalue", new char[] {
				'A', 'b', 'C', 'd'
		});
		props.put("primbooleanarrayvalue", new boolean[] {
				false
		});
		props.put("bigintvalue", new BigInteger("4123456"));
		props.put("bigdecvalue", new BigDecimal("4.123456"));
		props.put("*", "foo");
		props.put("!  ab", "b");
		props.put("|   ab", "b");
		props.put("&    ab", "b");
		props.put("!", "c");
		props.put("|", "c");
		props.put("&", "c");
		props.put("empty", "");
		props.put("space", Character.valueOf(' '));
		return props;
	}

	@Test
	public void testCaseInsensitive() throws InvalidSyntaxException {
		final Matcher matcher = new Matcher() {
			final Dictionary<String,Object>	props	= getProperties();
			final ServiceReference< ? >		ref		= newDictionaryServiceReference(
					props);

			public void matches(SoftAssertions softly, Filter f,
					boolean expected) {
				softly.assertThat(f.match(props))
						.as("\"%s\".match(props)", f)
						.isEqualTo(expected);
				softly.assertThat(f.match(ref))
						.as("\"%s\".match(ref)", f)
						.isEqualTo(expected);
			}
		};
		assertFilterTrue("(room=*)", matcher);
		assertFilterTrue("(room=bedroom)", matcher);
		assertFilterTrue("(room~= B E D R O O M )", matcher);
		assertFilterFalse("(room=abc)", matcher);
		assertFilterTrue(" ( room >=aaaa)", matcher);
		assertFilterFalse("(room <=aaaa)", matcher);
		assertFilterTrue("  ( room =b*) ", matcher);
		assertFilterTrue("  ( room =*m) ", matcher);
		assertFilterTrue("(room=bed*room)", matcher);
		assertFilterTrue("  ( room =b*oo*m) ", matcher);
		assertFilterTrue("  ( room =*b*oo*m*) ", matcher);
		assertFilterFalse("  ( room =b*b*  *m*) ", matcher);
		assertFilterTrue("  (& (room =bedroom) (channel ~= 34))", matcher);
		assertFilterFalse("  (&  (room =b*)  (room =*x) (channel=34))",
				matcher);
		assertFilterTrue("(| (room =bed*)(channel=222)) ", matcher);
		assertFilterTrue("(| (room =boom*)(channel=101)) ", matcher);
		assertFilterTrue("  (! (room =ab*b*oo*m*) ) ", matcher);
		assertFilterTrue("  (status =\\(o*\\\\\\)\\*) ", matcher);
		assertFilterTrue("  (canRecord =true\\(x\\)) ", matcher);
		assertFilterTrue("(max Record Time <=140) ", matcher);
		assertFilterTrue("(shortValue >= 100) ", matcher);
		assertFilterTrue("(intValue <= 100001) ", matcher);
		assertFilterTrue("(longValue >= 10000000000 ) ", matcher);
		assertFilterTrue(
				"  (  &  (  byteValue <= 100  )  (  byteValue >= 10  )  )  ",
				matcher);
		assertFilterFalse("(weirdValue = 100) ", matcher);
		assertFilterTrue("(bigIntValue =4123456) ", matcher);
		assertFilterTrue("(bigDecValue =4.123456) ", matcher);
		assertFilterTrue("(floatValue >= 1.0) ", matcher);
		assertFilterTrue("(doubleValue <= 2.011) ", matcher);
		assertFilterTrue("(charValue ~=a) ", matcher);
		assertFilterTrue("(booleanValue = true) ", matcher);
		assertFilterTrue("(primIntArrayValue = 1) ", matcher);
		assertFilterTrue("(primLongArrayValue = 2) ", matcher);
		assertFilterTrue("(primByteArrayValue = 3) ", matcher);
		assertFilterTrue("(primShortArrayValue = 1) ", matcher);
		assertFilterTrue("(primFloatArrayValue = 1.1) ", matcher);
		assertFilterTrue("(primDoubleArrayValue = 2.2) ", matcher);
		assertFilterTrue("(primCharArrayValue ~=D) ", matcher);
		assertFilterTrue("(primBooleanArrayValue = false ) ", matcher);
		assertFilterTrue(
				"(& (| (room =d*m) (room =bed*) (room=abc)) (! (channel=999)))",
				matcher);
		assertFilterTrue("(*=foo)", matcher);
		assertFilterTrue("(!  ab=b)", matcher);
		assertFilterTrue("(|   ab=b)", matcher);
		assertFilterTrue("(&=c)", matcher);
		assertFilterTrue("(!=c)", matcher);
		assertFilterTrue("(|=c)", matcher);
		assertFilterTrue("(&    ab=b)", matcher);
		assertFilterFalse("(!ab=*)", matcher);
		assertFilterFalse("(|ab=*)", matcher);
		assertFilterFalse("(&ab=*)", matcher);
		assertFilterTrue("(empty=)", matcher);
		assertFilterTrue("(empty=*)", matcher);
		assertFilterTrue("(space= )", matcher);
		assertFilterTrue("(space=*)", matcher);
	}

	@Test
	public void testCaseSensitive() throws InvalidSyntaxException {
		final Matcher matcher = new Matcher() {
			final Hashtable<String,Object> props = getProperties();

			public void matches(SoftAssertions softly, Filter f,
					boolean expected) {
				softly.assertThat(f.matches(props))
						.as("\"%s\".matches(props)", f)
						.isEqualTo(expected);
				softly.assertThat(f.matchCase(props))
						.as("\"%s\".matchCase(props)", f)
						.isEqualTo(expected);
			}
		};
		assertFilterTrue("(room=*)", matcher);
		assertFilterTrue("(room=bedroom)", matcher);
		assertFilterTrue("(room~= B E D R O O M )", matcher);
		assertFilterFalse("(room=abc)", matcher);
		assertFilterTrue(" ( room >=aaaa)", matcher);
		assertFilterFalse("(room <=aaaa)", matcher);
		assertFilterTrue("  ( room =b*) ", matcher);
		assertFilterTrue("  ( room =*m) ", matcher);
		assertFilterTrue("(room=bed*room)", matcher);
		assertFilterTrue("  ( room =b*oo*m) ", matcher);
		assertFilterTrue("  ( room =*b*oo*m*) ", matcher);
		assertFilterFalse("  ( room =b*b*  *m*) ", matcher);
		assertFilterTrue("  (& (room =bedroom) (channel ~= 34))", matcher);
		assertFilterFalse("  (&  (room =b*)  (room =*x) (channel=34))",
				matcher);
		assertFilterTrue("(| (room =bed*)(channel=222)) ", matcher);
		assertFilterTrue("(| (room =boom*)(channel=101)) ", matcher);
		assertFilterTrue("  (! (room =ab*b*oo*m*) ) ", matcher);
		assertFilterTrue("  (status =\\(o*\\\\\\)\\*) ", matcher);
		assertFilterTrue("  (canrecord =true\\(x\\)) ", matcher);
		assertFilterTrue("(max record time <=140) ", matcher);
		assertFilterTrue("(shortvalue >= 100) ", matcher);
		assertFilterTrue("(intvalue <= 100001) ", matcher);
		assertFilterTrue("(longvalue >= 10000000000 ) ", matcher);
		assertFilterTrue(
				"  (  &  (  bytevalue <= 100  )  (  bytevalue >= 10  )  )  ",
				matcher);
		assertFilterFalse("(weirdvalue = 100) ", matcher);
		assertFilterTrue("(bigintvalue =4123456) ", matcher);
		assertFilterTrue("(bigdecvalue =4.123456) ", matcher);
		assertFilterTrue("(floatvalue >= 1.0) ", matcher);
		assertFilterTrue("(doublevalue <= 2.011) ", matcher);
		assertFilterTrue("(charvalue ~=a) ", matcher);
		assertFilterTrue("(booleanvalue = true ) ", matcher);
		assertFilterTrue("(primintarrayvalue = 1) ", matcher);
		assertFilterTrue("(primlongarrayvalue = 2) ", matcher);
		assertFilterTrue("(primbytearrayvalue = 3) ", matcher);
		assertFilterTrue("(primshortarrayvalue = 1) ", matcher);
		assertFilterTrue("(primfloatarrayvalue = 1.1) ", matcher);
		assertFilterTrue("(primdoublearrayvalue = 2.2) ", matcher);
		assertFilterTrue("(primchararrayvalue ~=D) ", matcher);
		assertFilterTrue("(primbooleanarrayvalue = false) ", matcher);
		assertFilterTrue(
				"(& (| (room =d*m) (room =bed*) (room=abc)) (! (channel=999)))",
				matcher);
		assertFilterTrue("(*=foo)", matcher);
		assertFilterTrue("(!  ab=b)", matcher);
		assertFilterTrue("(|   ab=b)", matcher);
		assertFilterTrue("(&=c)", matcher);
		assertFilterTrue("(!=c)", matcher);
		assertFilterTrue("(|=c)", matcher);
		assertFilterTrue("(&    ab=b)", matcher);
		assertFilterFalse("(!ab=*)", matcher);
		assertFilterFalse("(|ab=*)", matcher);
		assertFilterFalse("(&ab=*)", matcher);
		assertFilterTrue("(empty=)", matcher);
		assertFilterTrue("(empty=*)", matcher);
		assertFilterTrue("(space= )", matcher);
		assertFilterTrue("(space=*)", matcher);
	}

	@Test
	public void testInvalidValues() throws InvalidSyntaxException {
		final Matcher matcher = new Matcher() {
			final Hashtable<String,Object>	props	= getProperties();
			final ServiceReference< ? >		ref		= newDictionaryServiceReference(
					props);

			public void matches(SoftAssertions softly, Filter f,
					boolean expected) {
				softly.assertThat(f.match(props))
						.as("\"%s\".match(props)", f)
						.isEqualTo(expected);
				softly.assertThat(f.matches(props))
						.as("\"%s\".matches(props)", f)
						.isEqualTo(expected);
				softly.assertThat(f.match(ref))
						.as("\"%s\".match(ref)", f)
						.isEqualTo(expected);
			}
		};
		assertFilterTrue("(intvalue=*)", matcher);
		assertFilterFalse("(intvalue=b)", matcher);
		assertFilterFalse("(intvalue=)", matcher);
		assertFilterTrue("(longvalue=*)", matcher);
		assertFilterFalse("(longvalue=b)", matcher);
		assertFilterFalse("(longvalue=)", matcher);
		assertFilterTrue("(shortvalue=*)", matcher);
		assertFilterFalse("(shortvalue=b)", matcher);
		assertFilterFalse("(shortvalue=)", matcher);
		assertFilterTrue("(bytevalue=*)", matcher);
		assertFilterFalse("(bytevalue=b)", matcher);
		assertFilterFalse("(bytevalue=)", matcher);
		assertFilterTrue("(charvalue=*)", matcher);
		assertFilterFalse("(charvalue=)", matcher);
		assertFilterTrue("(floatvalue=*)", matcher);
		assertFilterFalse("(floatvalue=b)", matcher);
		assertFilterFalse("(floatvalue=)", matcher);
		assertFilterTrue("(doublevalue=*)", matcher);
		assertFilterFalse("(doublevalue=b)", matcher);
		assertFilterFalse("(doublevalue=)", matcher);
		assertFilterTrue("(booleanvalue=*)", matcher);
		assertFilterFalse("(booleanvalue=b)", matcher);
		assertFilterFalse("(booleanvalue=)", matcher);

	}

	@Test
	public void testNullProperties() throws InvalidSyntaxException {
		assertFilterFalse("(room=bedroom)", nullMatcher);
	}

	@Test
	public void testInvalidFilter() {
		assertFilterInvalid("");
		assertFilterInvalid("()");
		assertFilterInvalid("(=foo)");
		assertFilterInvalid("(");
		assertFilterInvalid("(abc = ))");
		assertFilterInvalid("(& (abc = xyz) (& (345))");
		assertFilterInvalid("  (room = b**oo!*m*) ) ");
		assertFilterInvalid("  (room = b**oo)*m*) ) ");
		assertFilterInvalid("  (room = *=b**oo*m*) ) ");
		assertFilterInvalid("  (room = =b**oo*m*) ) ");
	}

	@Test
	public void testScalarSubstring() throws InvalidSyntaxException {
		final Matcher matcher = new Matcher() {
			final Hashtable<String,Object>	props	= getProperties();
			final ServiceReference< ? >		ref		= newDictionaryServiceReference(
					props);

			public void matches(SoftAssertions softly, Filter f,
					boolean expected) {
				softly.assertThat(f.match(props))
						.as("\"%s\".match(props)", f)
						.isEqualTo(expected);
				softly.assertThat(f.matchCase(props))
						.as("\"%s\".matchCase(props)", f)
						.isEqualTo(expected);
				softly.assertThat(f.matches(props))
						.as("\"%s\".matches(props)", f)
						.isEqualTo(expected);
				softly.assertThat(f.match(ref))
						.as("\"%s\".match(ref)", f)
						.isEqualTo(expected);
			}
		};
		assertFilterFalse("(shortvalue =100*) ", matcher);
		assertFilterFalse("(intvalue =100*) ", matcher);
		assertFilterFalse("(longvalue =100*) ", matcher);
		assertFilterFalse("(  bytevalue =1*00  )", matcher);
		assertFilterFalse("(bigintvalue =4*23456) ", matcher);
		assertFilterFalse("(bigdecvalue =4*123456) ", matcher);
		assertFilterFalse("(floatvalue =1*0) ", matcher);
		assertFilterFalse("(doublevalue =2*011) ", matcher);
		assertFilterFalse("(charvalue =a*) ", matcher);
		assertFilterFalse("(booleanvalue =t*ue) ", matcher);
	}

	@Test
	public void testNormalization() throws InvalidSyntaxException {
		Filter f1 = createFilter("( a = bedroom  )");
		Filter f2 = createFilter(" (a= bedroom  ) ");
		assertEquals("not equal", "(a= bedroom  )", f1.toString());
		assertEquals("not equal", "(a= bedroom  )", f2.toString());
		assertEquals("not equal", f1, f2);
		assertEquals("not equal", f2, f1);
		assertEquals("not equal", f1.hashCode(), f2.hashCode());
	}

	private void assertFilterInvalid(String query) {
		assertThatExceptionOfType(InvalidSyntaxException.class)
				.isThrownBy(() -> createFilter(query));
	}

	private void assertFilterTrue(String query, Matcher matcher)
			throws InvalidSyntaxException {
		Filter f1 = createFilter(query);

		try (AutoCloseableSoftAssertions softly = new AutoCloseableSoftAssertions()) {
			matcher.matches(softly, f1, true);
		}

		String normalized = f1.toString();
		Filter f2 = createFilter(normalized);

		try (AutoCloseableSoftAssertions softly = new AutoCloseableSoftAssertions()) {
			matcher.matches(softly, f2, true);
		}

		assertEquals("normalized not equal", normalized, f2.toString());
	}

	private void assertFilterFalse(String query, Matcher matcher)
			throws InvalidSyntaxException {
		Filter f1 = createFilter(query);

		try (AutoCloseableSoftAssertions softly = new AutoCloseableSoftAssertions()) {
			matcher.matches(softly, f1, false);
		}

		String normalized = f1.toString();
		Filter f2 = createFilter(normalized);

		try (AutoCloseableSoftAssertions softly = new AutoCloseableSoftAssertions()) {
			matcher.matches(softly, f2, false);
		}

		assertEquals("normalized not equal", normalized, f2.toString());
	}

	@Test
	public void testComparable() throws InvalidSyntaxException {
		Object comp42 = new SampleComparable("42");
		Object comp43 = new SampleComparable("43");
		Hashtable<String,Object> hash = new Hashtable<>();

		Filter f1 = createFilter("(comparable=42)");

		hash.put("comparable", comp42);
		assertTrue("does not match filter", f1.match(hash));
		assertTrue("does not match filter", f1.matchCase(hash));
		assertTrue("does not match filter", f1.matches(hash));
		assertTrue("does not match filter",
				f1.match(newDictionaryServiceReference(hash)));

		hash.put("comparable", comp43);
		assertFalse("does match filter", f1.match(hash));
		assertFalse("does match filter", f1.matchCase(hash));
		assertFalse("does match filter", f1.matches(hash));
		assertFalse("does match filter",
				f1.match(newDictionaryServiceReference(hash)));

		f1 = createFilter("(comparable<=42)");

		hash.put("comparable", comp42);
		assertTrue("does not match filter", f1.match(hash));
		assertTrue("does not match filter", f1.matchCase(hash));
		assertTrue("does not match filter", f1.matches(hash));
		assertTrue("does not match filter",
				f1.match(newDictionaryServiceReference(hash)));

		hash.put("comparable", comp43);
		assertFalse("does match filter", f1.match(hash));
		assertFalse("does match filter", f1.matchCase(hash));
		assertFalse("does match filter", f1.matches(hash));
		assertFalse("does match filter",
				f1.match(newDictionaryServiceReference(hash)));

		f1 = createFilter("(comparable>=42)");

		hash.put("comparable", comp42);
		assertTrue("does not match filter", f1.match(hash));
		assertTrue("does not match filter", f1.matchCase(hash));
		assertTrue("does not match filter", f1.matches(hash));
		assertTrue("does not match filter",
				f1.match(newDictionaryServiceReference(hash)));

		hash.put("comparable", comp43);
		assertTrue("does not match filter", f1.match(hash));
		assertTrue("does not match filter", f1.matchCase(hash));
		assertTrue("does not match filter", f1.matches(hash));
		assertTrue("does not match filter",
				f1.match(newDictionaryServiceReference(hash)));

		f1 = createFilter("(comparable=4*2)");

		hash.put("comparable", comp42);
		assertFalse("does match filter", f1.match(hash));
		assertFalse("does match filter", f1.matchCase(hash));
		assertFalse("does match filter", f1.matches(hash));
		assertFalse("does match filter",
				f1.match(newDictionaryServiceReference(hash)));
	}

	@Test
	public void testComparableException() throws InvalidSyntaxException {
		Object compbad = new SampleComparable("exception");
		Hashtable<String,Object> hash = new Hashtable<>();

		Filter f1 = createFilter("(comparable=exception)");

		hash.put("comparable", compbad);
		assertFalse("does match filter", f1.match(hash));
		assertFalse("does match filter", f1.matchCase(hash));
		assertFalse("does match filter", f1.matches(hash));
		assertFalse("does match filter",
				f1.match(newDictionaryServiceReference(hash)));
	}

	@Test
	public void testVersion() throws InvalidSyntaxException {
		Version v42 = new Version("4.2");
		Version v43 = new Version("4.3");
		Hashtable<String,Object> hash = new Hashtable<>();

		Filter f1 = createFilter("(version=4.2)");

		hash.put("version", v42);
		assertTrue("does not match filter", f1.match(hash));
		assertTrue("does not match filter", f1.matchCase(hash));
		assertTrue("does not match filter", f1.matches(hash));
		assertTrue("does not match filter",
				f1.match(newDictionaryServiceReference(hash)));

		hash.put("version", v43);
		assertFalse("does match filter", f1.match(hash));
		assertFalse("does match filter", f1.matchCase(hash));
		assertFalse("does match filter", f1.matches(hash));
		assertFalse("does match filter",
				f1.match(newDictionaryServiceReference(hash)));

		f1 = createFilter("(version<=4.2)");

		hash.put("version", v42);
		assertTrue("does not match filter", f1.match(hash));
		assertTrue("does not match filter", f1.matchCase(hash));
		assertTrue("does not match filter", f1.matches(hash));
		assertTrue("does not match filter",
				f1.match(newDictionaryServiceReference(hash)));

		hash.put("version", v43);
		assertFalse("does match filter", f1.match(hash));
		assertFalse("does match filter", f1.matchCase(hash));
		assertFalse("does match filter", f1.matches(hash));
		assertFalse("does match filter",
				f1.match(newDictionaryServiceReference(hash)));

		f1 = createFilter("(version>=4.2)");

		hash.put("version", v42);
		assertTrue("does not match filter", f1.match(hash));
		assertTrue("does not match filter", f1.matchCase(hash));
		assertTrue("does not match filter", f1.matches(hash));
		assertTrue("does not match filter",
				f1.match(newDictionaryServiceReference(hash)));

		hash.put("version", v43);
		assertTrue("does not match filter", f1.match(hash));
		assertTrue("does not match filter", f1.matchCase(hash));
		assertTrue("does not match filter", f1.matches(hash));
		assertTrue("does not match filter",
				f1.match(newDictionaryServiceReference(hash)));

		f1 = createFilter("(version=4*2)");

		hash.put("version", v42);
		assertFalse("does match filter", f1.match(hash));
		assertFalse("does match filter", f1.matchCase(hash));
		assertFalse("does match filter", f1.matches(hash));
		assertFalse("does match filter",
				f1.match(newDictionaryServiceReference(hash)));
	}

	@Test
	public void testVersionException() throws InvalidSyntaxException {
		Version v = Version.emptyVersion;
		Hashtable<String,Object> hash = new Hashtable<>();

		Filter f1 = createFilter("(version=exception)");

		hash.put("version", v);
		assertFalse("does match filter", f1.match(hash));
		assertFalse("does match filter", f1.matchCase(hash));
		assertFalse("does match filter", f1.matches(hash));
		assertFalse("does match filter",
				f1.match(newDictionaryServiceReference(hash)));
	}

	@Test
	public void testObject() throws InvalidSyntaxException {
		Object obj42 = new SampleObject("42");
		Object obj43 = new SampleObject("43");
		Hashtable<String,Object> hash = new Hashtable<>();

		Filter f1 = createFilter("(object=42)");

		hash.put("object", obj42);
		assertTrue("does not match filter", f1.match(hash));
		assertTrue("does not match filter", f1.matchCase(hash));
		assertTrue("does not match filter", f1.matches(hash));
		assertTrue("does not match filter",
				f1.match(newDictionaryServiceReference(hash)));

		hash.put("object", obj43);
		assertFalse("does match filter", f1.match(hash));
		assertFalse("does match filter", f1.matchCase(hash));
		assertFalse("does match filter", f1.matches(hash));
		assertFalse("does match filter",
				f1.match(newDictionaryServiceReference(hash)));

		f1 = createFilter("(object=4*2)");

		hash.put("object", obj42);
		assertFalse("does match filter", f1.match(hash));
		assertFalse("does match filter", f1.matchCase(hash));
		assertFalse("does match filter", f1.matches(hash));
		assertFalse("does match filter",
				f1.match(newDictionaryServiceReference(hash)));
	}

	@Test
	public void testObjectException() throws InvalidSyntaxException {
		Object objbad = new SampleObject("exception");
		Hashtable<String,Object> hash = new Hashtable<>();

		Filter f1 = createFilter("(object=exception)");

		hash.put("object", objbad);
		assertFalse("does match filter", f1.match(hash));
		assertFalse("does match filter", f1.matchCase(hash));
		assertFalse("does match filter", f1.matches(hash));
		assertFalse("does match filter",
				f1.match(newDictionaryServiceReference(hash)));
	}

	@Test
	public void testValueOf() throws InvalidSyntaxException {
		Object obj42 = new SampleValueOf("42", null);
		Object obj43 = new SampleValueOf("43", null);
		Hashtable<String,Object> hash = new Hashtable<>();

		Filter f1 = createFilter("(object=42)");

		hash.put("object", obj42);
		assertTrue("does not match filter", f1.match(hash));
		assertTrue("does not match filter", f1.matchCase(hash));
		assertTrue("does not match filter", f1.matches(hash));
		assertTrue("does not match filter",
				f1.match(newDictionaryServiceReference(hash)));

		hash.put("object", obj43);
		assertFalse("does match filter", f1.match(hash));
		assertFalse("does match filter", f1.matchCase(hash));
		assertFalse("does match filter", f1.matches(hash));
		assertFalse("does match filter",
				f1.match(newDictionaryServiceReference(hash)));

		f1 = createFilter("(object=4*2)");

		hash.put("object", obj42);
		assertFalse("does match filter", f1.match(hash));
		assertFalse("does match filter", f1.matchCase(hash));
		assertFalse("does match filter", f1.matches(hash));
		assertFalse("does match filter",
				f1.match(newDictionaryServiceReference(hash)));
	}

	@Test
	public void testValueOfException() throws InvalidSyntaxException {
		Object objbad = new SampleValueOf("exception", null);
		Hashtable<String,Object> hash = new Hashtable<>();

		Filter f1 = createFilter("(object=exception)");

		hash.put("object", objbad);
		assertFalse("does match filter", f1.match(hash));
		assertFalse("does match filter", f1.matchCase(hash));
		assertFalse("does match filter", f1.matches(hash));
		assertFalse("does match filter",
				f1.match(newDictionaryServiceReference(hash)));
	}

	@Test
	public void testValueOfWithUnassignableReturnType()
			throws InvalidSyntaxException {
		Object obj42 = new SampleValueOfWithUnassignableReturnType("42");
		Object obj43 = new SampleValueOfWithUnassignableReturnType("43");
		Hashtable<String,Object> hash = new Hashtable<>();

		Filter f1 = createFilter("(object=42)");

		hash.put("object", obj42);
		assertTrue("does not match filter", f1.match(hash));
		assertTrue("does not match filter", f1.matchCase(hash));
		assertTrue("does not match filter", f1.matches(hash));
		assertTrue("does not match filter",
				f1.match(newDictionaryServiceReference(hash)));

		hash.put("object", obj43);
		assertFalse("does match filter", f1.match(hash));
		assertFalse("does match filter", f1.matchCase(hash));
		assertFalse("does match filter", f1.matches(hash));
		assertFalse("does match filter",
				f1.match(newDictionaryServiceReference(hash)));

		f1 = createFilter("(object=4*2)");

		hash.put("object", obj42);
		assertFalse("does match filter", f1.match(hash));
		assertFalse("does match filter", f1.matchCase(hash));
		assertFalse("does match filter", f1.matches(hash));
		assertFalse("does match filter",
				f1.match(newDictionaryServiceReference(hash)));
	}

	@Test
	public void testNullMapValue() throws InvalidSyntaxException {
		Map<String,Object> map = new HashMap<>();
		map.put("foo", null);
		Filter f1 = createFilter("(foo=*)");
		assertFalse("does match filter", f1.matches(map));
	}

	@Test
	public void testComparableValueOf() throws InvalidSyntaxException {
		Object comp42 = new SampleComparableValueOf("42", null);
		Object comp43 = new SampleComparableValueOf("43", null);
		Hashtable<String,Object> hash = new Hashtable<>();

		Filter f1 = createFilter("(comparable=42)");

		hash.put("comparable", comp42);
		assertTrue("does not match filter", f1.match(hash));
		assertTrue("does not match filter", f1.matchCase(hash));
		assertTrue("does not match filter", f1.matches(hash));
		assertTrue("does not match filter",
				f1.match(newDictionaryServiceReference(hash)));

		hash.put("comparable", comp43);
		assertFalse("does match filter", f1.match(hash));
		assertFalse("does match filter", f1.matchCase(hash));
		assertFalse("does match filter", f1.matches(hash));
		assertFalse("does match filter",
				f1.match(newDictionaryServiceReference(hash)));

		f1 = createFilter("(comparable<=42)");

		hash.put("comparable", comp42);
		assertTrue("does not match filter", f1.match(hash));
		assertTrue("does not match filter", f1.matchCase(hash));
		assertTrue("does not match filter", f1.matches(hash));
		assertTrue("does not match filter",
				f1.match(newDictionaryServiceReference(hash)));

		hash.put("comparable", comp43);
		assertFalse("does match filter", f1.match(hash));
		assertFalse("does match filter", f1.matchCase(hash));
		assertFalse("does match filter", f1.matches(hash));
		assertFalse("does match filter",
				f1.match(newDictionaryServiceReference(hash)));

		f1 = createFilter("(comparable>=42)");

		hash.put("comparable", comp42);
		assertTrue("does not match filter", f1.match(hash));
		assertTrue("does not match filter", f1.matchCase(hash));
		assertTrue("does not match filter", f1.matches(hash));
		assertTrue("does not match filter",
				f1.match(newDictionaryServiceReference(hash)));

		hash.put("comparable", comp43);
		assertTrue("does not match filter", f1.match(hash));
		assertTrue("does not match filter", f1.matchCase(hash));
		assertTrue("does not match filter", f1.matches(hash));
		assertTrue("does not match filter",
				f1.match(newDictionaryServiceReference(hash)));

		f1 = createFilter("(comparable=4*2)");

		hash.put("comparable", comp42);
		assertFalse("does match filter", f1.match(hash));
		assertFalse("does match filter", f1.matchCase(hash));
		assertFalse("does match filter", f1.matches(hash));
		assertFalse("does match filter",
				f1.match(newDictionaryServiceReference(hash)));
	}

	@Test
	public void testComparableValueOfException() throws InvalidSyntaxException {
		Object compbad = new SampleComparableValueOf("exception", null);
		Hashtable<String,Object> hash = new Hashtable<>();

		Filter f1 = createFilter("(comparable=exception)");

		hash.put("comparable", compbad);
		assertFalse("does match filter", f1.match(hash));
		assertFalse("does match filter", f1.matchCase(hash));
		assertFalse("does match filter", f1.matches(hash));
		assertFalse("does match filter",
				f1.match(newDictionaryServiceReference(hash)));
	}

	public static class SampleComparableValueOf
			implements Comparable<SampleComparableValueOf> {
		private final int				value;
		private final RuntimeException	e;

		public static SampleComparableValueOf valueOf(String value) {
			return new SampleComparableValueOf(value, null);
		}

		public SampleComparableValueOf(String value) {
			this.value = -1;
			this.e = null;
		}

		SampleComparableValueOf(String value, Object whatever) {
			int v = -1;
			RuntimeException r = null;
			try {
				v = Integer.parseInt(value);
			} catch (RuntimeException re) {
				r = re;
			}
			this.value = v;
			this.e = r;
		}

		public int compareTo(SampleComparableValueOf o) {
			if (e != null) {
				e.fillInStackTrace();
				throw e;
			}
			return value - o.value;
		}

		public String toString() {
			if (e != null) {
				e.fillInStackTrace();
				return e.toString();
			}
			return String.valueOf(value);
		}
	}

	public static class SampleComparable
			implements Comparable<SampleComparable> {
		private final int				value;
		private final RuntimeException	e;

		public SampleComparable(String value) {
			int v = -1;
			RuntimeException r = null;
			try {
				v = Integer.parseInt(value);
			} catch (RuntimeException re) {
				r = re;
			}
			this.value = v;
			this.e = r;
		}

		public int compareTo(SampleComparable o) {
			if (e != null) {
				e.fillInStackTrace();
				throw e;
			}
			return value - o.value;
		}

		public String toString() {
			if (e != null) {
				e.fillInStackTrace();
				return e.toString();
			}
			return String.valueOf(value);
		}
	}

	public static class SampleObject {
		private final int				value;
		private final RuntimeException	e;

		public SampleObject(String value) {
			int v = -1;
			RuntimeException r = null;
			try {
				v = Integer.parseInt(value);
			} catch (RuntimeException re) {
				r = re;
			}
			this.value = v;
			this.e = r;
		}

		public boolean equals(Object o) {
			if (e != null) {
				e.fillInStackTrace();
				throw e;
			}
			if (o instanceof SampleObject) {
				return value == ((SampleObject) o).value;
			}
			return false;
		}

		public String toString() {
			if (e != null) {
				e.fillInStackTrace();
				return e.toString();
			}
			return String.valueOf(value);
		}
	}

	public static class SampleValueOf {
		private final int				value;
		private final RuntimeException	e;

		public static SampleValueOf valueOf(String value) {
			return new SampleValueOf(value, null);
		}

		public SampleValueOf(String value) {
			this.value = -1;
			this.e = null;
		}

		SampleValueOf(String value, Object whatever) {
			int v = -1;
			RuntimeException r = null;
			try {
				v = Integer.parseInt(value);
			} catch (RuntimeException re) {
				r = re;
			}
			this.value = v;
			this.e = r;
		}

		public boolean equals(Object o) {
			if (e != null) {
				e.fillInStackTrace();
				throw e;
			}
			if (o instanceof SampleValueOf) {
				return value == ((SampleValueOf) o).value;
			}
			return false;
		}

		public String toString() {
			if (e != null) {
				e.fillInStackTrace();
				return e.toString();
			}
			return String.valueOf(value);
		}
	}

	public static class SampleValueOfWithUnassignableReturnType {
		private final int				value;
		private final RuntimeException	e;

		public static Integer valueOf(String value) {
			return Integer.parseInt(value);
		}

		public SampleValueOfWithUnassignableReturnType(String value) {
			int v = -1;
			RuntimeException r = null;
			try {
				v = Integer.parseInt(value);
			} catch (RuntimeException re) {
				r = re;
			}
			this.value = v;
			this.e = r;
		}

		public boolean equals(Object o) {
			if (e != null) {
				e.fillInStackTrace();
				throw e;
			}
			if (o instanceof SampleValueOfWithUnassignableReturnType) {
				return value == ((SampleValueOfWithUnassignableReturnType) o).value;
			}
			return false;
		}

		public String toString() {
			if (e != null) {
				e.fillInStackTrace();
				return e.toString();
			}
			return String.valueOf(value);
		}
	}

	public static ServiceReference< ? > newDictionaryServiceReference(
			Dictionary<String,Object> dictionary) {
		return MockFactory.newMock(ServiceReference.class,
				new DictionaryServiceReference(dictionary));
	}

	private static class DictionaryServiceReference {
		private final Dictionary< ? , ? >	dictionary;
		private final String[]				keys;

		DictionaryServiceReference(Dictionary< ? , ? > dictionary) {
			if (dictionary == null) {
				this.dictionary = null;
				this.keys = new String[] {};
				return;
			}
			this.dictionary = dictionary;
			List<String> keyList = new ArrayList<>(dictionary.size());
			for (Enumeration< ? > e = dictionary.keys(); e.hasMoreElements();) {
				Object k = e.nextElement();
				if (k instanceof String) {
					String key = (String) k;
					for (Iterator<String> i = keyList.iterator(); i
							.hasNext();) {
						if (key.equalsIgnoreCase(i.next())) {
							throw new IllegalArgumentException();
						}
					}
					keyList.add(key);
				}
			}
			this.keys = keyList.toArray(new String[keyList.size()]);
		}

		@SuppressWarnings("unused")
		public Object getProperty(String k) {
			for (int i = 0, length = keys.length; i < length; i++) {
				String key = keys[i];
				if (key.equalsIgnoreCase(k)) {
					return dictionary.get(key);
				}
			}
			return null;
		}

		@SuppressWarnings("unused")
		public String[] getPropertyKeys() {
			return keys.clone();
		}
	}

	static interface Matcher {
		public void matches(SoftAssertions softly, Filter f, boolean expected);
	}
}
