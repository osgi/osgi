/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osgi.test.cases.converter.felix;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.Deque;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.osgi.framework.Version;
import org.osgi.test.cases.converter.felix.MyDTO.Count;
import org.osgi.test.cases.converter.felix.MyEmbeddedDTO.Alpha;
import org.osgi.util.converter.ConversionException;
import org.osgi.util.converter.Converter;
import org.osgi.util.converter.ConverterBuilder;
import org.osgi.util.converter.ConverterFunction;
import org.osgi.util.converter.Converters;
import org.osgi.util.converter.Rule;
import org.osgi.util.converter.TypeReference;

public class ConverterTest {
	private Converter converter;

	@BeforeEach
	public void setUp() {
		converter = Converters.standardConverter();
	}

	@AfterEach
	public void tearDown() {
		converter = null;
	}

	@Test
	public void testVersion() {
		Version v = new Version(1, 2, 3, "qualifier");
		Converter c = Converters.standardConverter();
		String s = c.convert(v).to(String.class);
		Version v2 = c.convert(s).to(Version.class);
		assertEquals(v, v2);
	}

	@Test
	public void testSimpleConversions() {
		// Conversions to String
		assertEquals("abc", converter.convert("abc").to(String.class));
		assertEquals("true", converter.convert(Boolean.TRUE).to(String.class));
		assertEquals("c", converter.convert('c').to(String.class));
		assertEquals("123", converter.convert(123).to(String.class));
		assertEquals("" + Long.MAX_VALUE,
				converter.convert(Long.MAX_VALUE).to(String.class));
		assertEquals("12.3", converter.convert(12.3f).to(String.class));
		assertEquals("12.345", converter.convert(12.345d).to(String.class));
		assertNull(converter.convert(null).to(String.class));
		assertNull(converter.convert(Collections.emptyList()).to(String.class));

		String bistr = "999999999999999999999"; // more than Long.MAX_VALUE
		assertEquals(bistr,
				converter.convert(new BigInteger(bistr)).to(String.class));

		// Conversions to boolean
		assertTrue(converter.convert("true").to(boolean.class));
		assertTrue(converter.convert("TRUE").to(boolean.class));
		assertTrue(converter.convert('x').to(boolean.class));
		assertTrue(converter.convert(Long.MIN_VALUE).to(boolean.class));
		assertTrue(converter.convert(72).to(boolean.class));
		assertFalse(converter.convert("false").to(boolean.class));
		assertFalse(converter.convert("bleh").to(boolean.class));
		assertFalse(converter.convert((char) 0).to(boolean.class));
		assertFalse(converter.convert(null).to(boolean.class));
		assertFalse(
				converter.convert(Collections.emptyList()).to(boolean.class));

		// Conversions to integer
		assertEquals(Integer.valueOf(123),
				converter.convert("123").to(int.class));
		assertEquals(1, (int) converter.convert(true).to(int.class));
		assertEquals(0, (int) converter.convert(false).to(int.class));
		assertEquals(65, (int) converter.convert('A').to(int.class));

		// Conversions to long
		assertEquals(Long.valueOf(65), converter.convert('A').to(Long.class));

		// Conversions to Class
		assertEquals(BigDecimal.class,
				converter.convert("java.math.BigDecimal").to(Class.class));
		assertEquals(BigDecimal.class, converter.convert("java.math.BigDecimal")
				.to(new TypeReference<Class< ? >>() {
				}));
		assertNull(converter.convert(null).to(Class.class));
		assertNull(converter.convert(Collections.emptyList()).to(Class.class));

		assertEquals(Integer.valueOf(123),
				converter.convert("123").to(Integer.class));
		assertEquals(Long.valueOf(123),
				converter.convert("123").to(Long.class));
		assertEquals('1', (char) converter.convert("123").to(Character.class));
		assertEquals('Q',
				(char) converter.convert(null)
						.defaultValue('Q')
						.to(Character.class));
		assertEquals((char) 123,
				(char) converter.convert(123L).to(Character.class));
		assertEquals((char) 123,
				(char) converter.convert(123).to(Character.class));
		assertEquals(Byte.valueOf((byte) 123),
				converter.convert("123").to(Byte.class));
		assertEquals(Float.valueOf("12.3"),
				converter.convert("12.3").to(Float.class));
		assertEquals(Double.valueOf("12.3"),
				converter.convert("12.3").to(Double.class));

		// Conversions to Optional

		Optional<String> s1 = converter.convert("1").to(Optional.class);
		assertEquals("1", s1.get());

		Optional<String> s2 = converter.convert("2")
				.to(new TypeReference<Optional<String>>() {
				});
		assertEquals("2", s2.get());

		Optional<String> n1 = converter.convert(null).to(Optional.class);

		assertThat(n1).isNotNull().isEmpty();

		Optional<String> n2 = converter.convert(null)
				.to(new TypeReference<Optional<String>>() {
				});
		assertThat(n2).isNotNull().isEmpty();

		// OptionalInt
		OptionalInt oi1 = converter.convert("1").to(OptionalInt.class);
		assertThat(oi1).isNotNull().isNotEmpty().hasValue(1);

		OptionalInt oiNull = converter.convert(null).to(OptionalInt.class);
		assertThat(oiNull).isNotNull().isEmpty();

		assertThrows(ConversionException.class,
				() -> converter.convert("badValue").to(OptionalInt.class));

		// OptionalDouble
		OptionalDouble od1 = converter.convert("1").to(OptionalDouble.class);
		assertThat(od1).isNotNull().isNotEmpty().hasValue(1d);

		OptionalDouble odNull = converter.convert(null)
				.to(OptionalDouble.class);
		assertThat(odNull).isNotNull().isEmpty();
		assertThrows(ConversionException.class,
				() -> converter.convert("badValue").to(OptionalDouble.class));

		// OptionalLong
		OptionalLong ol1 = converter.convert("1").to(OptionalLong.class);
		assertThat(ol1).isNotNull().isNotEmpty().hasValue(1l);

		OptionalLong olNull = converter.convert(null).to(OptionalLong.class);
		assertThat(olNull).isNotNull().isEmpty();
		assertThrows(ConversionException.class,
				() -> converter.convert("badValue").to(OptionalLong.class));

	}

	@Test
	public void testOptionalTyped() {
		// String to Double to Optional
		Optional<Double> d = converter.convert("12.3")
				.to(new TypeReference<Optional<Double>>() {
				});
		assertEquals(Double.valueOf("12.3"), d.get());

		Optional<Double> dNull = converter.convert(null)
				.to(new TypeReference<Optional<Double>>() {
				});

		assertThat(dNull).isNotNull().isEmpty();

		assertThrows(ConversionException.class, () -> converter.convert("12.3")
				.to(new TypeReference<Optional<Integer>>() {
				}));
	}

	@Test
	public void testCharAggregateToString() {
		Converter c = Converters.newConverterBuilder()
				.rule(new Rule<List<Character>,String>(
						ConverterTest::characterListToString) {
				})
				.rule(new Rule<String,List<Character>>(
						ConverterTest::stringToCharacterList) {
				})
				.build();

		char[] ca = new char[] {
				'h', 'e', 'l', 'l', 'o'
		};
		assertEquals("hello", c.convert(ca).to(String.class));

		Character[] ca2 = c.convert(ca).to(Character[].class);
		assertEquals("hello", c.convert(ca2).to(String.class));

		List<Character> cl = c.convert(ca)
				.to(new TypeReference<List<Character>>() {
				});
		assertEquals("hello", c.convert(cl).to(String.class));

		// And back
		assertArrayEquals(ca, c.convert("hello").to(char[].class));
		assertArrayEquals(ca2, c.convert("hello").to(Character[].class));
		assertEquals(cl,
				c.convert("hello").to(new TypeReference<List<Character>>() {
				}));
	}

	private static String characterListToString(List<Character> cl) {
		StringBuilder sb = new StringBuilder(cl.size());
		for (char c : cl) {
			sb.append(c);
		}
		return sb.toString();
	}

	private static List<Character> stringToCharacterList(String s) {
		List<Character> lc = new ArrayList<>();

		for (int i = 0; i < s.length(); i++) {
			lc.add(s.charAt(i));
		}
		return lc;
	}

	public enum TestEnum {
		FOO, BAR, BLAH, FALSE, X
	};

	public enum TestEnum2 {
		BLAH
	};

	@Test
	public void testEnums() {
		assertSame(TestEnum.BLAH, converter.convert("BLAH").to(TestEnum.class));
		assertSame(TestEnum.X, converter.convert('X').to(TestEnum.class));
		assertSame(TestEnum.FALSE, converter.convert(false).to(TestEnum.class));
		assertSame(TestEnum.BAR, converter.convert(1).to(TestEnum.class));
		assertSame(TestEnum.BLAH,
				converter.convert(TestEnum2.BLAH).to(TestEnum.class));
		assertNull(converter.convert(null).to(TestEnum.class));
		assertNull(
				converter.convert(Collections.emptySet()).to(TestEnum.class));
	}

	@Test
	public void testToReflectType() {
		Type t = TestEnum.class;
		TestEnum e = converter.convert("X").to(t);
		assertEquals(TestEnum.X, e);
	}

	@Test
	public void testIdentialTarget() {
		Object o = new Object();
		assertSame(o, converter.convert(o).to(Object.class));

		Thread t = new Thread(); // No converter available
		assertSame(t, converter.convert(t).to(Thread.class));
		assertSame(t, converter.convert(t).to(Runnable.class));
		assertSame(t, converter.convert(t).to(Object.class));

		Thread st = new Thread() {
		}; // Subclass of Thread
		assertSame(st, converter.convert(st).to(Thread.class));
	}

	@Test
	public void testFromUnknownDataTypeViaString() {
		class MyClass {
			@Override
			public String toString() {
				return "1234";
			}
		}
		;
		MyClass o = new MyClass();

		assertEquals(1234, (int) converter.convert(o).to(int.class));
		assertEquals("1234", converter.convert(o).to(String.class));
	}

	@Test
	public void testToUnknownViaStringCtor() {
		class MyClass {
			@Override
			public String toString() {
				return "http://127.0.0.1:1234/blah";
			}
		}
		;
		MyClass o = new MyClass();

		URL url = converter.convert(o).to(URL.class);
		assertEquals("http://127.0.0.1:1234/blah", url.toString());
		assertEquals("http", url.getProtocol());
		assertEquals("127.0.0.1", url.getHost());
		assertEquals(1234, url.getPort());
		assertEquals("/blah", url.getPath());

		assertNull(converter.convert(null).to(URL.class));
		assertNull(converter.convert(Collections.emptyList()).to(URL.class));
	}

	@Test
	public void testFromMultiToSingle() {
		assertEquals("abc", converter.convert(Collections.singleton("abc"))
				.to(String.class));
		assertEquals("abc",
				converter.convert(Arrays.asList("abc", "def", "ghi"))
						.to(String.class));
		assertEquals(42, (int) converter.convert(Arrays.asList("42", "17"))
				.to(Integer.class));
		MyClass2 mc = converter.convert(new String[] {
				"xxx", "yyy", "zzz"
		}).to(MyClass2.class);
		assertEquals("xxx", mc.toString());
		MyClass2[] arr = new MyClass2[] {
				new MyClass2("3.1412"), new MyClass2("6.2824")
		};
		assertEquals(Float.valueOf(3.1412f),
				Float.valueOf(converter.convert(arr).to(float.class)));
	}

	@Test
	public void testFromListToSet() {
		List<Object> l = new ArrayList<>(Arrays.asList("A", 'B', 333));

		Set< ? > s = converter.convert(l).to(Set.class);
		assertEquals(3, s.size());

		for (Object o : s) {
			Object expected = l.remove(0);
			assertEquals(expected, o);
		}
	}

	@Test
	public void testFromGenericSetToLinkedList() {
		Set<Integer> s = new LinkedHashSet<>();
		s.add(123);
		s.add(456);

		LinkedList<String> ll = converter.convert(s)
				.to(new TypeReference<LinkedList<String>>() {
				});
		assertEquals(Arrays.asList("123", "456"), ll);
	}

	@Test
	public void testFromArrayToGenericOrderPreservingSet() {
		String[] sa = {
				"567", "-765", "0", "-900"
		};

		// Returned set should be order preserving
		Set<Long> s = converter.convert(sa).to(new TypeReference<Set<Long>>() {
		});

		List<String> sl = new ArrayList<>(Arrays.asList(sa));
		for (long l : s) {
			long expected = Long.parseLong(sl.remove(0));
			assertEquals(expected, l);
		}
	}

	@Test
	public void testFromSetToArray() {
		Set<Integer> s = new LinkedHashSet<>();
		s.add(Integer.MIN_VALUE);

		long[] la = converter.convert(s).to(long[].class);
		assertEquals(1, la.length);
		assertEquals(Integer.MIN_VALUE, la[0]);
	}

	@Test
	public void testStringArrayToIntegerArray() {
		String[] sa = {
				"999", "111", "-909"
		};
		Integer[] ia = converter.convert(sa).to(Integer[].class);
		assertEquals(3, ia.length);
		assertArrayEquals(new Integer[] {
				999, 111, -909
		}, ia);
	}

	@Test
	public void testCharArrayConversion() {
		char[] ca = converter.convert(new int[] {
				9, 8, 7
		}).to(char[].class);
		assertArrayEquals(new char[] {
				9, 8, 7
		}, ca);
		Character[] ca2 = converter.convert((long) 17).to(Character[].class);
		assertArrayEquals(new Character[] {
				(char) 17
		}, ca2);
		char[] ca3 = converter.convert(new short[] {
				257
		}).to(char[].class);
		assertArrayEquals(new char[] {
				257
		}, ca3);
		char c = converter.convert(new char[] {
				'x', 'y'
		}).to(char.class);
		assertEquals('x', c);
		char[] ca4a = {
				'x', 'y'
		};
		char[] ca4b = converter.convert(ca4a).to(char[].class);
		assertArrayEquals(new char[] {
				'x', 'y'
		}, ca4b);
		assertNotSame(ca4a, ca4b, "Should have created a new instance");
	}

	/**
	 * 707.4.3.1 - null becomes an empty array
	 */
	@ParameterizedTest(name = "arrayType=\"{0}\"")
	@ValueSource(classes = {
			String[].class, //
			boolean[].class, //
			byte[].class, //
			short[].class, //
			char[].class, //
			int[].class, //
			float[].class, //
			long[].class, //
			double[].class, //

			String[][].class, //
			boolean[][].class, //
			byte[][].class, //
			short[][].class, //
			char[][].class, //
			int[][].class, //
			float[][].class, //
			long[][].class, //
			double[][].class, //

			String[][][].class, //
			boolean[][][].class, //
			byte[][][].class, //
			short[][][].class, //
			char[][][].class, //
			int[][][].class, //
			float[][][].class, //
			long[][][].class, //
			double[][][].class //
	})
	public void testNullToArrayConversion(Class< ? > arrayType) {
		assertTrue(arrayType.isArray());

		Object array = converter.convert(null).to(arrayType);
		assertEquals(0, Array.getLength(array));
		assertTrue(arrayType.isInstance(array));
	}

	@Test
	public void testPropagatingExceptionInArray() {
		try {
			Set<String> concurrentModificationSet = new HashSet<String>() {
				private static final long serialVersionUID = 1L;

				@Override
				public Iterator<String> iterator() {
					return new Iterator<String>() {

						@Override
						public boolean hasNext() {
							return true;
						}

						@Override
						public String next() {
							throw new ConcurrentModificationException(
									"This iterator deliberately throws CMEs!");
						}
					};
				}

			};
			concurrentModificationSet.add("one");
			concurrentModificationSet.add("two");
			converter.convert(concurrentModificationSet).to(String[].class);
			fail("Should have thrown a Conversion Exception when a collection throwing a CME was used as source");
		} catch (ConversionException e) {
			// good
		}
	}

	@Test
	public void testLongCollectionConversion() {
		long[] l = converter.convert(Long.MAX_VALUE).to(long[].class);
		assertArrayEquals(new long[] {
				Long.MAX_VALUE
		}, l);
		Long[] l2 = converter.convert(Long.MAX_VALUE).to(Long[].class);
		assertArrayEquals(new Long[] {
				Long.MAX_VALUE
		}, l2);
		List<Long> ll = converter.convert(new long[] {
				Long.MIN_VALUE, Long.MAX_VALUE
		}).to(new TypeReference<List<Long>>() {
		});
		assertEquals(Arrays.asList(Long.MIN_VALUE, Long.MAX_VALUE), ll);
		List<Long> ll2 = converter.convert(Arrays.asList(123, 345))
				.to(new TypeReference<List<Long>>() {
				});
		assertEquals(Arrays.asList(123L, 345L), ll2);

	}

	@Test
	public void testExceptionDefaultValue() {
		assertEquals(42,
				(int) converter.convert("haha").defaultValue(42).to(int.class));
		assertEquals(999,
				(int) converter.convert("haha")
						.defaultValue(999)
						.to(int.class));
		try {
			converter.convert("haha").to(int.class);
			fail("Should have thrown an exception");
		} catch (ConversionException ex) {
			// good
		}
	}

	@Test
	public void testStandardStringArrayConversion() {
		String[] sa = {
				"A", "B"
		};
		assertEquals("A", converter.convert(sa).toString());
		assertEquals("A", converter.convert(sa).to(String.class));

		String[] sa2 = {
				"A"
		};
		assertArrayEquals(sa2, converter.convert("A").to(String[].class));
	}

	@Test
	public void testCustomStringArrayConversion() {
		ConverterBuilder cb = converter.newConverterBuilder();
		cb.rule(new Rule<String[],String>(
				v -> Stream.of(v).collect(Collectors.joining(","))) {
		});
		cb.rule(new Rule<String,String[]>(v -> v.split(",")) {
		});

		Converter adapted = cb.build();

		String[] sa = {
				"A", "B"
		};
		assertEquals("A,B", adapted.convert(sa).to(String.class));
		assertArrayEquals(sa, adapted.convert("A,B").to(String[].class));
	}

	@Test
	public void testCustomIntArrayConversion() {
		ConverterBuilder cb = converter.newConverterBuilder();
		cb.rule(String.class,
				(f, t) -> f instanceof int[]
						? Arrays.stream((int[]) f)
								.mapToObj(Integer::toString)
								.collect(Collectors.joining(","))
						: null);
		cb.rule(int[].class,
				(f, t) -> f instanceof String
						? Arrays.stream(((String) f).split(","))
								.mapToInt(Integer::parseInt)
								.toArray()
						: null);
		Converter adapted = cb.build();

		int[] ia = {
				1, 2
		};
		assertEquals("1,2", adapted.convert(ia).to(String.class));
		assertArrayEquals(ia, adapted.convert("1,2").to(int[].class));
	}

	@Test
	public void testCustomConverterChaining() {
		ConverterBuilder cb = converter.newConverterBuilder();
		cb.rule(Date.class, (f, t) -> f instanceof String ? new Date(0)
				: ConverterFunction.CANNOT_HANDLE);
		Converter c1 = cb.build();
		assertEquals(new Date(0), c1.convert("something").to(Date.class));
		assertEquals(new Date(0), c1.convert("foo").to(Date.class));

		ConverterBuilder cb2 = c1.newConverterBuilder();
		cb2.rule(Date.class, (f, t) -> f.equals("foo") ? new Date(100000)
				: ConverterFunction.CANNOT_HANDLE);
		Converter c2 = cb2.build();
		assertEquals(new Date(0), c2.convert("something").to(Date.class));
		assertEquals(new Date(100000), c2.convert("foo").to(Date.class));
	}

	@Test
	public void testCustomErrorHandling() {
		ConverterFunction func = new ConverterFunction() {
			@Override
			public Object apply(Object obj, Type targetType) {
				if ("hello".equals(obj)) {
					return -1;
				}
				if ("goodbye".equals(obj)) {
					return null;
				}
				return ConverterFunction.CANNOT_HANDLE;
			}
		};

		ConverterBuilder cb = converter.newConverterBuilder();
		Converter adapted = cb.errorHandler(func).build();

		assertEquals(Integer.valueOf(12),
				adapted.convert("12").to(Integer.class));
		assertEquals(Integer.valueOf(-1),
				adapted.convert("hello").to(Integer.class));
		assertNull(adapted.convert("goodbye").to(Integer.class));

		try {
			adapted.convert("nothing").to(Integer.class);
			fail("Should have thrown a Conversion Exception when converting 'hello' to a number");
		} catch (ConversionException ce) {
			// good
		}

		// This is with the non-adapted converter
		try {
			converter.convert("hello").to(Integer.class);
			fail("Should have thrown a Conversion Exception when converting 'hello' to a number");
		} catch (ConversionException ce) {
			// good
		}
	}

	@Test
	public void testCustomErrorHandlingProxy() {
		ConverterFunction errHandler = new ConverterFunction() {
			@Override
			public Object apply(Object obj, Type targetType) throws Exception {
				return 123;
			}
		};
		ConverterBuilder cb = converter.newConverterBuilder();
		Converter c = cb.errorHandler(errHandler).build();

		Map< ? , ? > m = new HashMap<>();

		MyIntf i = c.convert(m).to(MyIntf.class);
		assertEquals(123, i.value());
	}

	@Test
	public void testMultipleCustomErrorHandling() {
		ConverterBuilder cb1 = converter.newConverterBuilder();
		ConverterFunction func1 = new ConverterFunction() {
			@Override
			public Object apply(Object obj, Type targetType) {
				return -1;
			}
		};
		cb1.errorHandler(func1);
		Converter c1 = cb1.build();

		ConverterBuilder cb2 = c1.newConverterBuilder();
		ConverterFunction func2 = new ConverterFunction() {
			@Override
			public Object apply(Object obj, Type targetType) {
				if ("hello".equals(obj)) {
					return 0;
				}
				return ConverterFunction.CANNOT_HANDLE;
			}
		};
		cb2.errorHandler(func2);
		Converter adapted = cb2.build();

		assertEquals(Integer.valueOf(0),
				adapted.convert("hello").to(Integer.class));
		assertEquals(Integer.valueOf(-1),
				adapted.convert("bye").to(Integer.class));
	}

	public static class MyConverterFunction implements ConverterFunction {
		@Override
		public Object apply(Object obj, Type targetType) throws Exception {
			if ("hello".equals(obj)) {
				return 0;
			}
			return ConverterFunction.CANNOT_HANDLE;
		}
	}

	@Test
	public void testUUIDConversion() {
		UUID uuid = UUID.randomUUID();
		String s = converter.convert(uuid).to(String.class);
		assertTrue(s.length() > 0, "UUID should be something");
		UUID uuid2 = converter.convert(s).to(UUID.class);
		assertEquals(uuid, uuid2);
	}

	@Test
	public void testPatternConversion() {
		String p = "\\S*";
		Pattern pattern = converter.convert(p).to(Pattern.class);
		Matcher matcher = pattern.matcher("hi");
		assertTrue(matcher.matches());
		String p2 = converter.convert(pattern).to(String.class);
		assertEquals(p, p2);
	}

	@Test
	public void testLocalDateTime() {
		LocalDateTime ldt = LocalDateTime.now();
		String s = converter.convert(ldt).to(String.class);
		assertTrue(s.length() > 0);
		LocalDateTime ldt2 = converter.convert(s).to(LocalDateTime.class);
		assertEquals(ldt, ldt2);
	}

	@Test
	public void testLocalDate() {
		LocalDate ld = LocalDate.now();
		String s = converter.convert(ld).to(String.class);
		assertTrue(s.length() > 0);
		LocalDate ld2 = converter.convert(s).to(LocalDate.class);
		assertEquals(ld, ld2);
	}

	@Test
	public void testLocalTime() {
		LocalTime lt = LocalTime.now();
		String s = converter.convert(lt).to(String.class);
		assertTrue(s.length() > 0);
		LocalTime lt2 = converter.convert(s).to(LocalTime.class);
		assertEquals(lt, lt2);
	}

	@Test
	public void testOffsetDateTime() {
		OffsetDateTime ot = OffsetDateTime.now();
		String s = converter.convert(ot).to(String.class);
		assertTrue(s.length() > 0);
		OffsetDateTime ot2 = converter.convert(s).to(OffsetDateTime.class);
		assertEquals(ot, ot2);
	}

	@Test
	public void testOffsetTime() {
		OffsetTime ot = OffsetTime.now();
		String s = converter.convert(ot).to(String.class);
		assertTrue(s.length() > 0);
		OffsetTime ot2 = converter.convert(s).to(OffsetTime.class);
		assertEquals(ot, ot2);
	}

	@Test
	public void testZonedDateTime() {
		ZonedDateTime zdt = ZonedDateTime.now();
		String s = converter.convert(zdt).to(String.class);
		assertTrue(s.length() > 0);
		ZonedDateTime zdt2 = converter.convert(s).to(ZonedDateTime.class);
		assertEquals(zdt, zdt2);
	}

	@Test
	public void testInstant() {
		Instant i = Instant.now();
		String s = converter.convert(i).to(String.class);
		assertTrue(s.length() > 0);
		Instant i2 = converter.convert(s).to(Instant.class);
		assertEquals(i, i2);
	}

	@Test
	public void testMonthDay() {
		MonthDay md = MonthDay.of(Month.APRIL, 1);
		String s = converter.convert(md).to(String.class);
		assertTrue(s.length() > 0);
		MonthDay md2 = converter.convert(s).to(MonthDay.class);
		assertEquals(md, md2);
	}

	@Test
	public void testYearMonth() {
		YearMonth ym = YearMonth.of(1999, Month.APRIL);
		String s = converter.convert(ym).to(String.class);
		assertTrue(s.length() > 0);
		YearMonth ym2 = converter.convert(s).to(YearMonth.class);
		assertEquals(ym, ym2);
	}

	@Test
	public void testYear() {
		Year y = Year.of(1999);
		String s = converter.convert(y).to(String.class);
		assertTrue(s.length() > 0);
		Year y2 = converter.convert(s).to(Year.class);
		assertEquals(y, y2);
	}

	@Test
	public void testDuration() {
		Duration d = Duration.ofSeconds(42);
		String s = converter.convert(d).to(String.class);
		assertTrue(s.length() > 0);
		Duration d2 = converter.convert(s).to(Duration.class);
		assertEquals(d, d2);
	}

	@Test
	public void testCalendarDate() {
		Calendar cal = new GregorianCalendar(1971, 1, 13, 12, 37, 41);
		TimeZone tz = TimeZone.getTimeZone("CET");
		cal.setTimeZone(tz);
		Date d = cal.getTime();

		Converter c = converter;

		String s = c.convert(d).toString();
		assertEquals("1971-02-13T11:37:41Z", s);
		assertEquals(d, c.convert(s).to(Date.class));

		String s2 = c.convert(cal).toString();
		assertEquals("1971-02-13T11:37:41Z", s2);
		Calendar cal2 = c.convert(s2).to(Calendar.class);
		assertEquals(cal.getTime(), cal2.getTime());
	}

	@Test
	public void testCalendarLong() {
		Calendar cal = new GregorianCalendar(1971, 1, 13, 12, 37, 41);
		TimeZone tz = TimeZone.getTimeZone("UTC");
		cal.setTimeZone(tz);

		long l = converter.convert(cal).to(Long.class);
		assertEquals(l, cal.getTimeInMillis());

		Calendar cal2 = converter.convert(l).to(Calendar.class);
		assertEquals(cal.getTime(), cal2.getTime());
	}

	@Test
	public void testDefaultValue() {
		long l = converter.convert(null).defaultValue("12").to(Long.class);
		assertEquals(12L, l);
		assertNull(
				converter.convert("haha").defaultValue(null).to(Integer.class));
		assertNull(converter.convert("test")
				.defaultValue(null)
				.to(new TypeReference<List<Long>>() {
				}));
	}

	@Test
	public void testDTO2Map() {
		MyEmbeddedDTO embedded = new MyEmbeddedDTO();
		embedded.marco = "hohoho";
		embedded.polo = Long.MAX_VALUE;
		embedded.alpha = Alpha.A;

		MyDTO dto = new MyDTO();
		dto.ping = "lalala";
		dto.pong = Long.MIN_VALUE;
		dto.count = Count.ONE;
		dto.embedded = embedded;

		@SuppressWarnings("rawtypes")
		Map m = converter.convert(dto).to(Map.class);
		assertEquals(4, m.size());
		assertEquals("lalala", m.get("ping"));
		assertEquals(Long.MIN_VALUE, m.get("pong"));
		assertEquals(Count.ONE, m.get("count"));
		assertNotNull(m.get("embedded"));

		MyEmbeddedDTO e = (MyEmbeddedDTO) m.get("embedded");
		assertEquals("hohoho", e.marco);
		assertEquals(Long.MAX_VALUE, e.polo);
		assertEquals(Alpha.A, e.alpha);
	}

	@Test
	public void testDTO2Map2() {
		MyEmbeddedDTO embedded = new MyEmbeddedDTO();
		embedded.marco = "hohoho";
		embedded.polo = Long.MAX_VALUE;
		embedded.alpha = Alpha.A;

		MyDTO dto = new MyDTO();
		dto.ping = "lalala";
		dto.pong = Long.MIN_VALUE;
		dto.count = Count.ONE;
		dto.embedded = embedded;

		@SuppressWarnings("rawtypes")
		Map m = converter.convert(dto).sourceAsDTO().to(Map.class);
		assertEquals(4, m.size());
		assertEquals("lalala", m.get("ping"));
		assertEquals(Long.MIN_VALUE, m.get("pong"));
		assertEquals(Count.ONE, m.get("count"));
		assertNotNull(m.get("embedded"));

		MyEmbeddedDTO e = (MyEmbeddedDTO) m.get("embedded");
		assertEquals("hohoho", e.marco);
		assertEquals(Long.MAX_VALUE, e.polo);
		assertEquals(Alpha.A, e.alpha);

		/*
		 * TODO this is the way it was, but it does not seem right Map e =
		 * (Map)m.get("embedded"); assertEquals("hohoho", e.get("marco"));
		 * assertEquals(Long.MAX_VALUE, e.get("polo")); assertEquals(Alpha.A,
		 * e.get("alpha"));
		 */
	}

	@Test
	public void testDTO2Map3() {
		MyEmbeddedDTO embedded2 = new MyEmbeddedDTO();
		embedded2.marco = "hohoho";
		embedded2.polo = Long.MAX_VALUE;
		embedded2.alpha = Alpha.A;

		MyDTOWithMethods embedded = new MyDTOWithMethods();
		embedded.ping = "lalala";
		embedded.pong = Long.MIN_VALUE;
		embedded.count = Count.ONE;
		embedded.embedded = embedded2;

		MyDTO8 dto = new MyDTO8();
		dto.ping = "lalala";
		dto.pong = Long.MIN_VALUE;
		dto.count = MyDTO8.Count.ONE;
		dto.embedded = embedded;

		@SuppressWarnings("rawtypes")
		Map m = converter.convert(dto).sourceAsDTO().to(Map.class);
		assertEquals(4, m.size());
		assertEquals("lalala", m.get("ping"));
		assertEquals(Long.MIN_VALUE, m.get("pong"));
		assertEquals(MyDTO8.Count.ONE, m.get("count"));
		assertNotNull(m.get("embedded"));
		assertTrue(m.get("embedded") instanceof MyDTOWithMethods);
		MyDTOWithMethods e = (MyDTOWithMethods) m.get("embedded");
		assertEquals("lalala", e.ping);
		assertEquals(Long.MIN_VALUE, e.pong);
		assertEquals(Count.ONE, e.count);
		assertNotNull(e.embedded);
		assertThat(e.embedded).isInstanceOf(MyEmbeddedDTO.class);
		MyEmbeddedDTO e2 = e.embedded;
		assertEquals("hohoho", e2.marco);
		assertEquals(Long.MAX_VALUE, e2.polo);
		assertEquals(Alpha.A, e2.alpha);
	}

	@Test
	public void testDTO2Map4() {
		MyDefaultCtorDTOAlike dto = new MyDefaultCtorDTOAlike();
		dto.myProp = "myValue";

		@SuppressWarnings("rawtypes")
		Map m = converter.convert(dto).to(Map.class);
		assertEquals(1, m.size());
		assertEquals("myValue", m.get("myProp"));
	}

	@Test
	public void testDTO2Map5() {
		MyDTO3 dto = new MyDTO3();
		dto.charSet = new HashSet<>(Arrays.asList('f', 'o', 'o'));

		@SuppressWarnings("rawtypes")
		Map m = converter.convert(dto).to(new TypeReference<Map<String, ? >>() {
		});
		assertEquals(1, m.size());
		assertEquals(dto.charSet, m.get("charSet"));

		m = converter.convert(dto)
				.to(new TypeReference<Map<String, ? extends List<String>>>() {
				});
		assertEquals(1, m.size());

		List<String> list = new ArrayList<>();
		for (Character character : dto.charSet) {
			list.add(String.valueOf(character));
		}

		assertEquals(list, m.get("charSet"));
	}

	@Test
	@SuppressWarnings({
			"rawtypes", "unchecked"
	})
	public void testDTOFieldShadowing() {
		MySubDTO dto = new MySubDTO();
		dto.ping = "test";
		dto.count = Count.THREE;

		Map m = converter.convert(dto)
				.to(new TypeReference<Map<String,String>>() {
				});

		Map<String,String> expected = new HashMap<>();
		expected.put("ping", "test");
		expected.put("count", "THREE");
		expected.put("pong", "0");
		expected.put("embedded", null);
		assertEquals(expected, new HashMap<String,String>(m));

		MySubDTO dto2 = converter.convert(m).to(MySubDTO.class);
		assertEquals("test", dto2.ping);
		assertEquals(Count.THREE, dto2.count);
		assertEquals(0L, dto2.pong);
		assertNull(dto2.embedded);
	}

	@Test
	public void testMap2DTO() {
		Map<String,Object> m = new HashMap<>();
		m.put("ping", "abc xyz");
		m.put("pong", 42L);
		m.put("count", Count.ONE);
		Map<String,Object> e = new HashMap<>();
		e.put("marco", "ichi ni san");
		e.put("polo", 64L);
		e.put("alpha", Alpha.A);
		m.put("embedded", e);

		MyDTO dto = converter.convert(m).to(MyDTO.class);
		assertEquals("abc xyz", dto.ping);
		assertEquals(42L, dto.pong);
		assertEquals(Count.ONE, dto.count);
		assertNotNull(dto.embedded);
		assertEquals(dto.embedded.marco, "ichi ni san");
		assertEquals(dto.embedded.polo, 64L);
		assertEquals(dto.embedded.alpha, Alpha.A);
	}

	@Test
	public void testMap2DTOView() {
		Map<String,Object> src = Collections.singletonMap("pong", 42);
		MyDTOWithMethods dto = converter.convert(src)
				.targetAs(MyDTO.class)
				.to(MyDTOWithMethods.class);
		assertEquals(42, dto.pong);
	}

	@Test
	@SuppressWarnings({
			"rawtypes", "unchecked"
	})
	public void testDTOWithGenerics() {
		MyDTO2 dto = new MyDTO2();
		dto.longList = Arrays.asList(999L, 1000L);
		dto.dtoMap = new LinkedHashMap<>();

		MyDTO3 subDTO1 = new MyDTO3();
		subDTO1.charSet = new HashSet<>(Arrays.asList('f', 'o', 'o'));
		dto.dtoMap.put("zzz", subDTO1);

		MyDTO3 subDTO2 = new MyDTO3();
		subDTO2.charSet = new HashSet<>(Arrays.asList('b', 'a', 'r'));
		dto.dtoMap.put("aaa", subDTO2);

		Map m = converter.convert(dto).to(Map.class);
		assertEquals(2, m.size());

		assertEquals(Arrays.asList(999L, 1000L), m.get("longList"));
		Map nestedMap = (Map) m.get("dtoMap");

		// Check iteration order is preserved by iterating
		int i = 0;
		for (Iterator<Map.Entry> it = nestedMap.entrySet().iterator(); it
				.hasNext(); i++) {
			Map.Entry entry = it.next();
			switch (i) {
				case 0 :
					assertEquals("zzz", entry.getKey());
					MyDTO3 dto1 = (MyDTO3) entry.getValue();
					assertNotSame(subDTO1, dto1, "Should have created a copy");
					assertEquals(
							new HashSet<Character>(Arrays.asList('f', 'o')),
							dto1.charSet);
					break;
				case 1 :
					assertEquals("aaa", entry.getKey());
					MyDTO3 dto2 = (MyDTO3) entry.getValue();
					assertNotSame(subDTO2, dto2, "Should have created a copy");
					assertEquals(
							new HashSet<Character>(
									Arrays.asList('b', 'a', 'r')),
							dto2.charSet);
					break;
				default :
					fail("Unexpected number of elements on map");
			}
		}

		// convert back
		MyDTO2 dto2 = converter.convert(m).to(MyDTO2.class);
		assertEquals(dto.longList, dto2.longList);

		// Cannot simply do dto.equals() as the DTOs don't implement that
		assertEquals(dto.dtoMap.size(), dto2.dtoMap.size());
		MyDTO3 dto2SubZZZ = dto2.dtoMap.get("zzz");
		assertEquals(dto2SubZZZ.charSet,
				new HashSet<Character>(Arrays.asList('f', 'o')));
		MyDTO3 dto2SubAAA = dto2.dtoMap.get("aaa");
		assertEquals(dto2SubAAA.charSet,
				new HashSet<Character>(Arrays.asList('b', 'a', 'r')));
	}

	@Test
	public void testMapToDTOWithGenerics() {
		Map<String,Object> dto = new HashMap<>();

		dto.put("longList", Arrays.asList((short) 999, "1000"));

		Map<String,Object> dtoMap = new LinkedHashMap<>();
		dto.put("dtoMap", dtoMap);

		Map<String,Object> subDTO1 = new HashMap<>();
		subDTO1.put("charSet",
				new HashSet<>(Arrays.asList("foo", (int) 'o', 'o')));
		dtoMap.put("zzz", subDTO1);

		Map<String,Object> subDTO2 = new HashMap<>();
		subDTO2.put("charSet", new HashSet<>(Arrays.asList('b', 'a', 'r')));
		dtoMap.put("aaa", subDTO2);

		MyDTO2 converted = converter.convert(dto).to(MyDTO2.class);

		assertEquals(Arrays.asList(999L, 1000L), converted.longList);
		Map<String,MyDTO3> nestedMap = converted.dtoMap;

		// Check iteration order is preserved by iterating
		int i = 0;
		for (Iterator<Map.Entry<String,MyDTO3>> it = nestedMap.entrySet()
				.iterator(); it.hasNext(); i++) {
			Map.Entry<String,MyDTO3> entry = it.next();
			switch (i) {
				case 0 :
					assertEquals("zzz", entry.getKey());
					MyDTO3 dto1 = entry.getValue();
					assertEquals(
							new HashSet<Character>(Arrays.asList('f', 'o')),
							dto1.charSet);
					break;
				case 1 :
					assertEquals("aaa", entry.getKey());
					MyDTO3 dto2 = entry.getValue();
					assertEquals(
							new HashSet<Character>(
									Arrays.asList('b', 'a', 'r')),
							dto2.charSet);
					break;
				default :
					fail("Unexpected number of elements on map");
			}
		}
	}

	@Test
	public void testMapToDTOWithGenericVariables() {
		Map<String,Object> dto = new HashMap<>();
		dto.put("set", new HashSet<>(Arrays.asList("foo", (int) 'o', 'o')));
		dto.put("raw", "1234");
		dto.put("array", Arrays.asList("foo", (int) 'o', 'o'));

		MyGenericDTOWithVariables<Character> converted = converter.convert(dto)
				.to(new TypeReference<MyGenericDTOWithVariables<Character>>() {
				});
		assertEquals(Character.valueOf('1'), converted.raw);
		assertArrayEquals(new Character[] {
				'f', 'o', 'o'
		}, converted.array);
		assertEquals(new HashSet<Character>(Arrays.asList('f', 'o')),
				converted.set);
	}

	@Test
	public void testMapToDTOWithSurplusMapFiels() {
		Map<String,String> m = new HashMap<>();
		m.put("foo", "bar");
		MyDTO3 dtoDoesNotMap = converter.convert(m).to(MyDTO3.class);
		assertNull(dtoDoesNotMap.charSet);
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void testCopyMap() {
		Map m = new HashMap();
		Map m2 = converter.convert(m).to(Map.class);
		assertEquals(m, m2);
		assertNotSame(m, m2);
	}

	@Test
	@SuppressWarnings({
			"rawtypes", "unchecked"
	})
	public void testCopyMap2() {
		Map m = new HashMap();
		m.put("key", Arrays.asList("a", "b", "c"));
		Map m2 = converter.convert(m).to(Map.class);
		assertEquals(m, m2);
		assertNotSame(m, m2);
	}

	@Test
	public void testConversionPriority() {
		MyBean mb = new MyBean();
		mb.intfVal = 17;
		mb.beanVal = "Hello";

		assertEquals(Collections.singletonMap("value", "Hello"),
				converter.convert(mb).sourceAsBean().to(Map.class));
	}

	@Test
	public void testConvertAsInterface() {
		MyBean mb = new MyBean();
		mb.intfVal = 17;
		mb.beanVal = "Hello";

		assertEquals(17,
				converter.convert(mb)
						.sourceAs(MyIntf.class)
						.to(Map.class)
						.get("value"));
	}

	@Test
	public void testConvertAsBean() {
		MyBean mb = new MyBean();
		mb.intfVal = 17;
		mb.beanVal = "Hello";

		assertEquals(Collections.singletonMap("value", "Hello"),
				converter.convert(mb).sourceAsBean().to(Map.class));
	}

	@Test
	public void testConvertAsDTO() {
		MyClass3 mc3 = new MyClass3(17);

		assertEquals(17,
				converter.convert(mc3)
						.sourceAsDTO()
						.to(Map.class)
						.get("value"));
	}

	@Test
	public void testDTONameMangling() {
		Map<String,String> m = new HashMap<>();
		m.put("org.osgi.framework.uuid", "test123");
		m.put("myProperty143", "true");
		m.put("my$prop", "42");
		m.put("dot.prop", "456");
		m.put(".secret", " ");
		m.put("another_prop", "lalala");
		m.put("three_.prop", "hi ha ho");
		m.put("four._prop", "");
		m.put("five..prop", "test");
		m.put("six-prop", "987");
		m.put("seven$.prop", "3.141");

		MyDTO7 dto = converter.convert(m).to(MyDTO7.class);
		assertEquals("test123", dto.org_osgi_framework_uuid);
		assertTrue(dto.myProperty143);
		assertEquals(42, dto.my$$prop);
		assertEquals(Long.valueOf(456L), dto.dot_prop);
		assertEquals(' ', dto._secret);
		assertEquals("lalala", dto.another__prop);
		assertEquals("hi ha ho", dto.three___prop);
		assertEquals("", dto.four_$__prop);
		assertEquals("test", dto.five_$_prop);
		assertEquals((short) 987, dto.six$_$prop);
		dto.seven$$_$prop = 3.141;

		// And convert back
		Map<String,String> m2 = converter.convert(dto)
				.to(new TypeReference<Map<String,String>>() {
				});
		assertEquals(new HashMap<String,String>(m),
				new HashMap<String,String>(m2));
	}

	@Test
	public void testCollectionInterfaceMapping() {
		Collection< ? > coll = converter.convert("test").to(Collection.class);
		assertEquals("test", coll.iterator().next());

		List< ? > list = converter.convert("test").to(List.class);
		assertEquals("test", list.iterator().next());

		Set< ? > set = converter.convert("test").to(Set.class);
		assertEquals("test", set.iterator().next());

		NavigableSet< ? > ns = converter.convert("test").to(NavigableSet.class);
		assertEquals("test", ns.iterator().next());

		SortedSet< ? > ss = converter.convert("test").to(SortedSet.class);
		assertEquals("test", ss.iterator().next());

		Queue< ? > q = converter.convert("test").to(Queue.class);
		assertEquals("test", q.iterator().next());

		Deque< ? > dq = converter.convert("test").to(Deque.class);
		assertEquals("test", dq.iterator().next());

		Map< ? , ? > m = converter.convert(Collections.singletonMap("x", "y"))
				.to(Map.class);
		assertEquals("y", m.get("x"));

		ConcurrentMap< ? , ? > cm = converter
				.convert(Collections.singletonMap("x", "y"))
				.to(ConcurrentMap.class);
		assertEquals("y", cm.get("x"));

		ConcurrentNavigableMap< ? , ? > cnm = converter
				.convert(Collections.singletonMap("x", "y"))
				.to(ConcurrentNavigableMap.class);
		assertEquals("y", cnm.get("x"));

		NavigableMap< ? , ? > nm = converter
				.convert(Collections.singletonMap("x", "y"))
				.to(NavigableMap.class);
		assertEquals("y", nm.get("x"));

		SortedMap< ? , ? > sm = converter
				.convert(Collections.singletonMap("x", "y"))
				.to(SortedMap.class);
		assertEquals("y", sm.get("x"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testLiveMapFromInterface() {
		int[] val = new int[1];
		val[0] = 51;

		MyIntf intf = new MyIntf() {
			@Override
			public int value() {
				return val[0];
			}
		};

		@SuppressWarnings("rawtypes")
		Map m = converter.convert(intf).view().to(Map.class);
		assertEquals(51, m.get("value"));

		val[0] = 52;
		assertEquals(52, m.get("value"),
				"Changes to the backing map should be reflected");

		m.put("value", 53);
		assertEquals(53, m.get("value"));

		val[0] = 54;
		assertEquals(53, m.get("value"),
				"Changes to the backing map should not be reflected any more");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testLiveMapFromDTO() {
		MyDTO8 myDTO = new MyDTO8();

		myDTO.count = MyDTO8.Count.TWO;
		myDTO.pong = 42L;

		@SuppressWarnings("rawtypes")
		Map m = converter.convert(myDTO).view().to(Map.class);
		assertEquals(42L, m.get("pong"));

		myDTO.ping = "Ping!";
		assertEquals("Ping!", m.get("ping"));
		myDTO.pong = 52L;
		assertEquals(52L, m.get("pong"));
		myDTO.ping = "Pong!";
		assertEquals("Pong!", m.get("ping"));
		assertNull(m.get("nonexistant"));

		m.put("pong", 62L);
		myDTO.ping = "Poing!";
		myDTO.pong = 72L;
		assertEquals("Pong!", m.get("ping"));
		assertEquals(62L, m.get("pong"));
		assertNull(m.get("nonexistant"));
	}

	@Test
	public void testMapFromDTO() {
		MyDTO9 dto = new MyDTO9();
		dto.key1 = "value1";
		dto.key2 = "value2";

		Map<Character,Character> m = converter.convert(dto)
				.to(new TypeReference<Map<Character,Character>>() {
				});
		assertEquals(1, m.size());
		assertEquals('v', (char) m.get('k'));

		assertThat(m).asInstanceOf(InstanceOfAssertFactories.MAP)
				.containsKey('k')
				.containsValue('v')
				.doesNotContainKey("key1")
				.doesNotContainValue("value1");
	}

	@Test
	public void testLiveMapFromDictionary() throws URISyntaxException {
		URI testURI = new URI("http://foo");
		Hashtable<String,Object> d = new Hashtable<>();
		d.put("test", testURI);

		Map<String,Object> m = converter.convert(d)
				.view()
				.to(new TypeReference<Map<String,Object>>() {
				});
		assertEquals(testURI, m.get("test"));

		URI testURI2 = new URI("http://bar");
		d.put("test2", testURI2);
		assertEquals(testURI2, m.get("test2"));
		assertEquals(testURI, m.get("test"));
	}

	@Test
	public void testLiveMapFromMap() {
		Map<String,String> s = new HashMap<>();

		s.put("true", "123");
		s.put("false", "456");

		Map<Boolean,Short> m = converter.convert(s)
				.view()
				.to(new TypeReference<Map<Boolean,Short>>() {
				});
		assertEquals(Short.valueOf("123"), m.get(Boolean.TRUE));
		assertEquals(Short.valueOf("456"), m.get(Boolean.FALSE));

		s.remove("true");
		assertNull(m.get(Boolean.TRUE));

		s.put("TRUE", "999");
		assertEquals(Short.valueOf("999"), m.get(Boolean.TRUE));
	}

	@Test
	public void testLiveMapFromBean() {
		MyBean mb = new MyBean();
		mb.beanVal = "" + Long.MAX_VALUE;

		Map<SomeEnum,Long> m = converter.convert(mb)
				.sourceAsBean()
				.view()
				.to(new TypeReference<Map<SomeEnum,Long>>() {
				});
		assertEquals(1, m.size());
		assertEquals(Long.valueOf(Long.MAX_VALUE), m.get(SomeEnum.VALUE));

		mb.beanVal = "" + Long.MIN_VALUE;
		assertEquals(Long.valueOf(Long.MIN_VALUE), m.get(SomeEnum.VALUE));

		m.put(SomeEnum.GETVALUE, 123L);
		mb.beanVal = "12";
		assertEquals(Long.valueOf(Long.MIN_VALUE), m.get(SomeEnum.VALUE));
	}

	@Test
	public void testPrefixDTO() {
		Map<String,String> m = new HashMap<>();
		m.put("org.foo.bar.width", "327");
		m.put("org.foo.bar.warp", "eeej");
		m.put("length", "12");

		PrefixDTO dto = converter.convert(m).to(PrefixDTO.class);
		assertEquals(327L, dto.width);
		assertEquals(0, dto.length, "This one should not be set");

		Map<String,String> m2 = converter.convert(dto)
				.to(new TypeReference<HashMap<String,String>>() {
				});
		Map<String,String> expected = new HashMap<>();
		expected.put("org.foo.bar.width", "327");
		expected.put("org.foo.bar.length", "0");
		assertEquals(expected, m2);
	}

	@Test
	public void testPrefixInterface() {
		Map<String,String> m = new HashMap<>();
		m.put("org.foo.bar.width", "327");
		m.put("org.foo.bar.warp", "eeej");
		m.put("length", "12");

		PrefixInterface i = converter.convert(m).to(PrefixInterface.class);
		assertEquals(327L, i.width());
		try {
			i.length();
			fail("Should have thrown an exception");
		} catch (ConversionException ce) {
			// good
		}

		PrefixInterface i2 = new PrefixInterface() {
			@Override
			public long width() {
				return Long.MAX_VALUE;
			}

			@Override
			public int length() {
				return Integer.MIN_VALUE;
			}
		};

		Map<String,String> m2 = converter.convert(i2)
				.to(new TypeReference<Map<String,String>>() {
				});
		Map<String,String> expected = new HashMap<>();
		expected.put("org.foo.bar.width", "" + Long.MAX_VALUE);
		expected.put("org.foo.bar.length", "" + Integer.MIN_VALUE);
		assertEquals(expected, m2);
	}

	@Test
	public void testAnnotationInterface() {
		Map<String,String> m = new HashMap<>();
		m.put("org.foo.bar.width", "327");
		m.put("org.foo.bar.warp", "eeej");
		m.put("length", "12");

		PrefixAnnotation pa = converter.convert(m).to(PrefixAnnotation.class);
		assertEquals(327L, pa.width());
		assertEquals(51, pa.length());

		Map<String,String> m2 = converter.convert(pa)
				.to(new TypeReference<Map<String,String>>() {
				});
		Map<String,String> expected = new HashMap<>();
		expected.put("org.foo.bar.width", "327");
		expected.put("org.foo.bar.length", "51");
		assertEquals(expected, m2);
	}

	@Test
	public void testPrefixEnumAnnotation() {
		PrefixEnumAnnotation pea = converter.convert(Collections.emptyMap())
				.to(PrefixEnumAnnotation.class);

		assertEquals(1000, pea.timeout());
		assertEquals(PrefixEnumAnnotation.Type.SINGLE, pea.type());

		@SuppressWarnings("rawtypes")
		Map m = converter.convert(pea).to(Map.class);
		assertEquals(1000L, m.get("com.acme.config.timeout"));
		assertEquals(PrefixEnumAnnotation.Type.SINGLE,
				m.get("com.acme.config.type"));
	}

	@Test
	public void testTargetAsString() {
		Map<String,String> m = new HashMap<>();
		CharSequence cs = converter.convert(m)
				.targetAs(String.class)
				.to(CharSequence.class);
		assertNull(cs);

		Map<String,String> m2 = new HashMap<>();
		m2.put("Hi", "there");
		CharSequence cs2 = converter.convert(m2)
				.targetAs(String.class)
				.to(CharSequence.class);
		assertEquals("Hi", cs2);
	}

	@SuppressWarnings({
			"rawtypes", "unchecked"
	})
	@Test
	public void testTargetAsDTO() {
		MyDTOWithMethods expected = new MyDTOWithMethods();
		expected.count = Count.ONE;
		expected.ping = "pong";
		expected.pong = 42;
		Map m = new HashMap<>();
		m.put("count", Count.ONE);
		m.put("ping", "pong");
		m.put("pong", 42);
		MyDTOWithMethods actual = converter.convert(m)
				.targetAsDTO()
				.to(MyDTOWithMethods.class);
		assertEquals(expected.count, actual.count);
		assertEquals(expected.ping, actual.ping);
		assertEquals(expected.pong, actual.pong);
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testLongArrayToLongCollection() {
		Long[] la = new Long[] {
				Long.MIN_VALUE, Long.MAX_VALUE
		};

		List lc = converter.convert(la).to(List.class);

		assertEquals(la.length, lc.size());

		int i = 0;
		for (Iterator it = lc.iterator(); it.hasNext(); i++) {
			assertEquals(la[i], it.next());
		}
	}

	@Test
	public void testMapToInterfaceWithGenerics() {
		Map<String,Object> dto = new HashMap<>();
		dto.put("charSet", new HashSet<>(Arrays.asList("foo", (int) 'o', 'o')));

		MyGenericInterface converted = converter.convert(dto)
				.to(MyGenericInterface.class);
		assertEquals(new HashSet<Character>(Arrays.asList('f', 'o')),
				converted.charSet());

	}

	@Test
	public void testMapToInterfaceWithOptional() {
		Map<String,Object> dto = new HashMap<>();
		dto.put("text", "foo");
		dto.put("textOptional", Optional.of("fooOptional"));
		dto.put("textOptionalOptional",
				Optional.of(Optional.of("fooOptionalOptional")));

		dto.put("textSetDefaultEmptyOptional", "bar");
		dto.put("textNullSet", null);
		dto.put("textNullSetDefaultEmptyOptional", null);

		dto.put("oInt", 1);
		dto.put("oIntNullSet", null);
		dto.put("oIntBadValue", "badValue");


		dto.put("oDouble", 2D);
		dto.put("oDoubleNullSet", null);
		dto.put("oDoubleBadValue", "badValue");


		dto.put("oLong", 3L);
		dto.put("oLongNullSet", null);
		dto.put("oLongBadValue", "badValue");

		MyGenericInterfaceOptional converted = converter.convert(dto)
				.to(MyGenericInterfaceOptional.class);

		assertThat(converted.text()).isNotNull().isPresent();
		assertThat(converted.text()).get().isSameAs("foo");
		assertThat(converted.oInt()).hasValue(1);
		assertThat(converted.oDouble()).hasValue(2D);
		assertThat(converted.oLong()).hasValue(3L);

		assertThat(converted.textOptional()).isNotNull().isPresent();
		assertThat(converted.textOptional().get()).isNotNull()
				.isSameAs("fooOptional");

		assertThat(converted.textOptionalOptional()).isNotNull().isPresent();
		assertThat(converted.textOptionalOptional().get()).isNotNull()
				.isInstanceOf(Optional.class);
		assertThat(converted.textOptionalOptional().get().get()).isNotNull()
				.isNotEmpty()
				.isSameAs("fooOptionalOptional");

		assertThat(converted.textSetDefaultEmptyOptional()).isNotNull()
				.isPresent();
		assertThat(converted.textSetDefaultEmptyOptional().get())
				.isSameAs("bar");

		assertThat(converted.textNotSetDefaultEmptyOptional()).isNotNull()
				.isNotPresent();

		assertThat(converted.textNullSetDefaultEmptyOptional()).isNotNull()
				.isNotPresent();

		assertThat(converted.optionalIntNotSetDefaultEmptyOptional())
				.isNotNull()
				.isNotPresent();
		assertThat(converted.optionalDoubleNotSetDefaultEmptyOptional())
				.isNotNull()
				.isNotPresent();
		assertThat(converted.optionalLongNotSetDefaultEmptyOptional())
				.isNotNull()
				.isNotPresent();

		assertThat(converted.textNullSet()).isNotNull().isEmpty();
		assertThat(converted.oDoubleNullSet()).isNotNull().isEmpty();
		assertThat(converted.oLongNullSet()).isNotNull().isEmpty();
		assertThat(converted.oIntNullSet()).isNotNull().isEmpty();

		assertThrows(ConversionException.class, () -> converted.textNotSet());

		assertThrows(ConversionException.class, () -> converted.oIntNotSet());

		assertThrows(ConversionException.class,
				() -> converted.oDoubleNotSet());

		assertThrows(ConversionException.class, () -> converted.oLongNotSet());

		// Conversion failures should be deferred when using interfaces
		assertThrows(ConversionException.class, () -> converted.oIntBadValue());
		assertThrows(ConversionException.class,
				() -> converted.oDoubleBadValue());
		assertThrows(ConversionException.class,
				() -> converted.oLongBadValue());

	}

	@Test
	public void testMapToInterfaceWithGenericVariables() {
		Map<String,Object> dto = new HashMap<>();
		dto.put("set", new HashSet<>(Arrays.asList("foo", (int) 'o', 'o')));
		dto.put("raw", "1234");
		dto.put("array", Arrays.asList("foo", (int) 'o', 'o'));

		MyGenericInterfaceWithVariables<Character> converted = converter
				.convert(dto)
				.to(new TypeReference<MyGenericInterfaceWithVariables<Character>>() {
				});
		assertEquals(Character.valueOf('1'), converted.raw());
		assertArrayEquals(new Character[] {
				'f', 'o', 'o'
		}, converted.array());
		assertEquals(new HashSet<Character>(Arrays.asList('f', 'o')),
				converted.set());
	}

	@Test
	public void testMapToInterfaceWithOptionalValue() throws Exception {
		ConverterBuilder cb = Converters.newConverterBuilder();
		cb.errorHandler(new ConverterFunction() {
			@Override
			public Object apply(Object pObj, Type pTargetType)
					throws Exception {
				if ("java.lang.Integer".equals(pTargetType.getTypeName())) {
					return 0;
				}
				return ConverterFunction.CANNOT_HANDLE;
			}
		});
		Converter convWithErrorHandler = cb.build();

		Map<String,Object> map = new HashMap<String,Object>();
		map.put("code", "harley");
		MyIntf2 inter = convWithErrorHandler.convert(map).to(MyIntf2.class);
		assertEquals("harley", inter.code());
		assertEquals(Integer.valueOf(0), inter.value());
	}

	@Test
	public void testMapToEmptyInterface() throws Exception {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("a", "b");
		EmptyInterface i = Converters.standardConverter()
				.convert(map)
				.to(EmptyInterface.class);
		assertNotNull(i);

		EmptyInterface2 j = Converters.standardConverter()
				.convert(map)
				.to(EmptyInterface2.class);
		assertNotNull(j);

		EmptyInterface3 k = Converters.standardConverter()
				.convert(map)
				.to(EmptyInterface3.class);
		assertNotNull(k);
	}

	@interface AnnType {
		boolean a();

		String b();
	}

	interface Interf {
		default Boolean a() {
			return null;
		}

		default Boolean b() {
			return null;
		}
	}

	@Test
	public void testDefaultInterfaceToAnnotationType() throws Throwable {
		AnnType a = Converters.standardConverter().convert(new Interf() {
		}).to(AnnType.class);
		assertThat(a.a()).isFalse();
		assertThat(a.b()).isNull();

	}

	@Test
	public void testDefaultInterfaceMethod() throws Throwable {
		Class< ? > clazz = InterfaceWithDefaultMethod.class;
		InterfaceWithDefaultMethod i = (InterfaceWithDefaultMethod) Converters
				.standardConverter()
				.convert(new HashMap<String,Object>())
				.to(clazz);
		assertEquals(InterfaceWithDefaultMethod.RESULT, i.defaultMethod());
		assertNull(i.defaultMethodNull());
		Assertions.assertThatExceptionOfType(ConversionException.class)
				.isThrownBy(() -> i.defaultMethodException());

		ConverterFunction errHandler = new ConverterFunction() {
			@Override
			public Object apply(Object obj, Type targetType) throws Exception {
				return "ok";
			}
		};
		ConverterBuilder cb = converter.newConverterBuilder();
		Converter c = cb.errorHandler(errHandler).build();

		Map< ? , ? > m = new HashMap<>();

		InterfaceWithDefaultMethod ie = c.convert(m)
				.to(InterfaceWithDefaultMethod.class);
		assertEquals("ok", ie.defaultMethodException());

		Assertions.assertThatExceptionOfType(ConversionException.class)
				.isThrownBy(() -> i.nonDefault());

	}

	@Test
	public void testConvertBooleanToNumber() {
		assertEquals(Byte.valueOf((byte) 1),
				converter.convert(Boolean.TRUE).to(Byte.class));
		assertEquals(Short.valueOf((short) 1),
				converter.convert(Boolean.TRUE).to(Short.class));
		assertEquals(Integer.valueOf(1),
				converter.convert(Boolean.TRUE).to(Integer.class));
		assertEquals(Long.valueOf(1),
				converter.convert(Boolean.TRUE).to(Long.class));
		assertEquals(Float.valueOf(1.0f),
				converter.convert(Boolean.TRUE).to(Float.class));
		assertEquals(Double.valueOf(1.0),
				converter.convert(Boolean.TRUE).to(Double.class));
	}

	public static interface MyIntf2 {
		String code();

		Integer value();
	}

	public static interface EmptyInterface {
	}

	public static interface EmptyInterface2 extends EmptyInterface {
	}

	public static interface NonEmptyInterface {
		int a();
	}

	public static interface EmptyInterface3 extends NonEmptyInterface {
	}

	public static class MyClass2 {
		private final String value;

		public MyClass2(String v) {
			value = v;
		}

		@Override
		public String toString() {
			return value;
		}
	}

	public static interface MyIntf {
		int value();
	}

	public static class MyBean implements MyIntf {
		int		intfVal;
		String	beanVal;

		@Override
		public int value() {
			return intfVal;
		}

		public String getValue() {
			return beanVal;
		}
	}

	public static class MyClass3 {
		public int		value;
		public String	string	= "String";

		public MyClass3(int value) {
			this.value = value;
		}

		public int value() {
			return value;
		}
	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface MyAnnotation {
		int value() default 17;
	}

	public enum SomeEnum {
		VALUE, GETVALUE
	};
}
