package org.osgi.test.cases.converter;

import static java.util.Arrays.*;

import java.io.*;
import java.lang.reflect.*;
import java.math.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

import junit.framework.*;

import org.osgi.framework.*;
import org.osgi.service.converter.*;
import org.osgi.service.converter.generic.*;

public class ConverterTest extends TestCase {
	AggregateConverter	converter;
	BundleContext		context	= FrameworkUtil.getBundle(ConverterTest.class)
										.getBundleContext();

	public void setUp() {
		ServiceReference ref = context
				.getServiceReference(AggregateConverter.class.getName());
		converter = (AggregateConverter) context.getService(ref);
	}

	/**
	 * Test the string constructor. One of the last steps in the conversion is
	 * to see if there is public String constructor and use this if the source
	 * is a string.
	 */

	public static class WithStringConstructor {
		String	s;

		public WithStringConstructor(String s) {
			this.s = s;
		}

		public WithStringConstructor(Integer s) {
			this.s = s.toString();
		}
	}

	public static class WithGenericStringConstructor<T extends String> {
		String	s;

		public WithGenericStringConstructor(T s) {
			this.s = s;
		}
	}

	public static class WithPrivateStringConstructor {
		String	s;

		private WithPrivateStringConstructor(String s) {
			this.s = s;
		}
	}

	public static WithGenericStringConstructor<String>	withGenericStringConstructor;

	@SuppressWarnings("unchecked")
	public void testWithStringConstructor() throws Exception {
		{
			URL url = converter.convert("http://www.libsync.com",
					new ReifiedType(URL.class));
			assertNotNull(url);
			assertEquals("http://www.libsync.com", url.toExternalForm());
		}
		{
			File file = converter.convert("file.name", new ReifiedType(
					File.class));
			assertNotNull(file);
			assertEquals("file.name", file.getName());
		}
		{
			BigInteger bigi = converter.convert("12345678901234567890",
					new ReifiedType(BigInteger.class));
			assertNotNull(bigi);
			assertEquals("12345678901234567890", bigi.toString());
		}
		{
			BigDecimal bigi = converter.convert("12345678901234567890",
					new ReifiedType(BigDecimal.class));
			assertNotNull(bigi);
			assertEquals("12345678901234567890", bigi.toString());
		}
		{
			WithStringConstructor ws = converter.convert("abc",
					new ReifiedType(WithStringConstructor.class));
			assertNotNull(ws);
			assertEquals("abc", ws.s);
		}

		// Should only work for String constructors
		{
			WithStringConstructor ws = converter.convert(new Integer(1),
					new ReifiedType(WithStringConstructor.class));
			assertNull(ws);
		}

		// Make sure setAccessible is not used
		{
			WithPrivateStringConstructor ws = converter.convert("abc",
					new ReifiedType(WithPrivateStringConstructor.class));
			assertNull(ws);
		}
		{
			WithGenericStringConstructor ws = (WithGenericStringConstructor) converter
					.convert("abc",
							getFieldType("withGenericStringConstructor"));
			assertNotNull(ws);
			assertEquals("abc", ws.s);
		}
	}

	/**
	 * Test the basic values
	 * 
	 */

	@SuppressWarnings("unchecked")
	public void testBasic() throws Exception {
		// From byte to anywhere
		assertEquals(true, converter.convert((byte) 12, new ReifiedType(
				Boolean.class)));
		assertEquals((byte) 12, converter.convert((byte) 12, new ReifiedType(
				Byte.class)));
		assertEquals((short) 12, converter.convert((byte) 12, new ReifiedType(
				Short.class)));
		assertEquals((char) 12, converter.convert((byte) 12, new ReifiedType(
				Character.class)));
		assertEquals((int) 12, converter.convert((byte) 12, new ReifiedType(
				Integer.class)));
		assertEquals((long) 12, converter.convert((byte) 12, new ReifiedType(
				Long.class)));
		assertEquals((float) 12, converter.convert((byte) 12, new ReifiedType(
				Float.class)));
		assertEquals((double) 12, converter.convert((byte) 12, new ReifiedType(
				Double.class)));
		assertEquals(new BigInteger("12"), converter.convert((byte) 12,
				new ReifiedType(BigInteger.class)));
		assertEquals(new BigDecimal("12"), converter.convert((byte) 12,
				new ReifiedType(BigDecimal.class)));
		assertEquals("12", converter.convert((byte) 12, new ReifiedType(
				String.class)));

		// From short to anywhere
		assertEquals(true, converter.convert((short) 12, new ReifiedType(
				Boolean.class)));
		assertEquals((byte) 12, converter.convert((short) 12, new ReifiedType(
				Byte.class)));
		assertEquals((short) 12, converter.convert((short) 12, new ReifiedType(
				Short.class)));
		assertEquals((char) 12, converter.convert((short) 12, new ReifiedType(
				Character.class)));
		assertEquals((int) 12, converter.convert((short) 12, new ReifiedType(
				Integer.class)));
		assertEquals((long) 12, converter.convert((short) 12, new ReifiedType(
				Long.class)));
		assertEquals((float) 12, converter.convert((short) 12, new ReifiedType(
				Float.class)));
		assertEquals((double) 12, converter.convert((short) 12,
				new ReifiedType(Double.class)));
		assertEquals(new BigInteger("12"), converter.convert((short) 12,
				new ReifiedType(BigInteger.class)));
		assertEquals(new BigDecimal("12"), converter.convert((short) 12,
				new ReifiedType(BigDecimal.class)));
		assertEquals("12", converter.convert((short) 12, new ReifiedType(
				String.class)));

		// From char to anywhere
		assertEquals(true, converter.convert((char) 12, new ReifiedType(
				Boolean.class)));
		assertEquals((byte) 12, converter.convert((char) 12, new ReifiedType(
				Byte.class)));
		assertEquals((short) 12, converter.convert((char) 12, new ReifiedType(
				Short.class)));
		assertEquals((char) 12, converter.convert((char) 12, new ReifiedType(
				Character.class)));
		assertEquals((int) 12, converter.convert((char) 12, new ReifiedType(
				Integer.class)));
		assertEquals((long) 12, converter.convert((char) 12, new ReifiedType(
				Long.class)));
		assertEquals((float) 12, converter.convert((char) 12, new ReifiedType(
				Float.class)));
		assertEquals((double) 12, converter.convert((char) 12, new ReifiedType(
				Double.class)));
		assertEquals(new BigInteger("12"), converter.convert((char) 12,
				new ReifiedType(BigInteger.class)));
		assertEquals(new BigDecimal("12"), converter.convert((char) 12,
				new ReifiedType(BigDecimal.class)));
		assertEquals("\f", converter.convert((char) 12, new ReifiedType(
				String.class)));

		// From int to anywhere
		assertEquals(false, converter.convert((int) 0, new ReifiedType(
				Boolean.class)));
		assertEquals(true, converter.convert((int) -12, new ReifiedType(
				Boolean.class)));
		assertEquals((byte) -12, converter.convert((int) -12, new ReifiedType(
				Byte.class)));
		assertEquals((short) -12, converter.convert((int) -12, new ReifiedType(
				Short.class)));
		assertEquals((char) 12, converter.convert((int) 12, new ReifiedType(
				Character.class)));
		assertEquals((int) -12, converter.convert((int) -12, new ReifiedType(
				Integer.class)));
		assertEquals((long) -12, converter.convert((int) -12, new ReifiedType(
				Long.class)));
		assertEquals((float) -12, converter.convert((int) -12, new ReifiedType(
				Float.class)));
		assertEquals((double) -12, converter.convert((int) -12,
				new ReifiedType(Double.class)));
		assertEquals(new BigInteger("-12"), converter.convert((int) -12,
				new ReifiedType(BigInteger.class)));
		assertEquals(new BigDecimal("-12"), converter.convert((int) -12,
				new ReifiedType(BigDecimal.class)));
		assertEquals("-12", converter.convert((int) -12, new ReifiedType(
				String.class)));

		// From long to anywhere
		assertEquals(false, converter.convert((long) 0, new ReifiedType(
				Boolean.class)));
		assertEquals(true, converter.convert((long) -12, new ReifiedType(
				Boolean.class)));
		assertEquals((byte) -12, converter.convert((long) -12, new ReifiedType(
				Byte.class)));
		assertEquals((short) -12, converter.convert((long) -12,
				new ReifiedType(Short.class)));
		assertEquals((char) 12, converter.convert((long) 12, new ReifiedType(
				Character.class)));
		assertEquals((int) -12, converter.convert((long) -12, new ReifiedType(
				Integer.class)));
		assertEquals((long) -12, converter.convert((long) -12, new ReifiedType(
				Long.class)));
		assertEquals((float) -12, converter.convert((long) -12,
				new ReifiedType(Float.class)));
		assertEquals((double) -12, converter.convert((long) -12,
				new ReifiedType(Double.class)));
		assertEquals(new BigInteger("-12"), converter.convert((long) -12,
				new ReifiedType(BigInteger.class)));
		assertEquals(new BigDecimal("-12"), converter.convert((long) -12,
				new ReifiedType(BigDecimal.class)));
		assertEquals("-12", converter.convert((long) -12, new ReifiedType(
				String.class)));

		// From float to anywhere
		assertEquals(false, converter.convert((float) 0, new ReifiedType(
				Boolean.class)));
		assertEquals(true, converter.convert((float) -12, new ReifiedType(
				Boolean.class)));
		assertEquals((byte) -12, converter.convert((float) -12,
				new ReifiedType(Byte.class)));
		assertEquals((short) -12, converter.convert((float) -12,
				new ReifiedType(Short.class)));
		assertEquals((char) 12, converter.convert((float) 12, new ReifiedType(
				Character.class)));
		assertEquals((int) -12, converter.convert((float) -12, new ReifiedType(
				Integer.class)));
		assertEquals((long) -12, converter.convert((float) -12,
				new ReifiedType(Long.class)));
		assertEquals((float) -12, converter.convert((float) -12,
				new ReifiedType(Float.class)));
		assertEquals((double) -12, converter.convert((float) -12,
				new ReifiedType(Double.class)));
		assertEquals(new BigInteger("-12"), converter.convert((float) -12,
				new ReifiedType(BigInteger.class)));
		assertEquals(new BigDecimal("-12"), converter.convert((float) -12,
				new ReifiedType(BigDecimal.class)));
		assertEquals("-12.0", converter.convert((float) -12, new ReifiedType(
				String.class)));

		// From double to anywhere
		assertEquals(false, converter.convert((double) 0, new ReifiedType(
				Boolean.class)));
		assertEquals(true, converter.convert((double) -12, new ReifiedType(
				Boolean.class)));
		assertEquals((byte) -12, converter.convert((double) -12,
				new ReifiedType(Byte.class)));
		assertEquals((short) -12, converter.convert((double) -12,
				new ReifiedType(Short.class)));
		assertEquals((char) 12, converter.convert((double) 12, new ReifiedType(
				Character.class)));
		assertEquals((int) -12, converter.convert((double) -12,
				new ReifiedType(Integer.class)));
		assertEquals((long) -12, converter.convert((double) -12,
				new ReifiedType(Long.class)));
		assertEquals((float) -12, converter.convert((double) -12,
				new ReifiedType(Float.class)));
		assertEquals((double) -12, converter.convert((double) -12,
				new ReifiedType(Double.class)));
		assertEquals(new BigInteger("-12"), converter.convert((double) -12,
				new ReifiedType(BigInteger.class)));
		assertEquals(new BigDecimal("-12"), converter.convert((double) -12,
				new ReifiedType(BigDecimal.class)));
		assertEquals("-12.0", converter.convert((double) -12, new ReifiedType(
				String.class)));

		// From BigInteger to anywhere
		assertEquals(false, converter.convert(new BigInteger("0"),
				new ReifiedType(Boolean.class)));
		assertEquals(true, converter.convert(new BigInteger("-10"),
				new ReifiedType(Boolean.class)));
		assertEquals((byte) -12, converter.convert(new BigInteger("-12"),
				new ReifiedType(Byte.class)));
		assertEquals((short) -12, converter.convert(new BigInteger("-12"),
				new ReifiedType(Short.class)));
		assertEquals((char) 12, converter.convert(new BigInteger("12"),
				new ReifiedType(Character.class)));
		assertEquals((int) -12, converter.convert(new BigInteger("-12"),
				new ReifiedType(Integer.class)));
		assertEquals((long) -12, converter.convert(new BigInteger("-12"),
				new ReifiedType(Long.class)));
		assertEquals((float) -12, converter.convert(new BigInteger("-12"),
				new ReifiedType(Float.class)));
		assertEquals((double) -12, converter.convert(new BigInteger("-12"),
				new ReifiedType(Double.class)));
		assertEquals(new BigInteger("-12"), converter.convert(new BigInteger(
				"-12"), new ReifiedType(BigInteger.class)));
		assertEquals(new BigDecimal("-12"), converter.convert(new BigInteger(
				"-12"), new ReifiedType(BigDecimal.class)));
		assertEquals("-12", converter.convert(new BigInteger("-12"),
				new ReifiedType(String.class)));

		// From BigInteger to anywhere
		assertEquals(false, converter.convert(new BigDecimal(0),
				new ReifiedType(Boolean.class)));
		assertEquals(true, converter.convert(new BigDecimal(-10),
				new ReifiedType(Boolean.class)));
		assertEquals((byte) -12, converter.convert(new BigDecimal(-12),
				new ReifiedType(Byte.class)));
		assertEquals((short) -12, converter.convert(new BigDecimal(-12),
				new ReifiedType(Short.class)));
		assertEquals((char) 12, converter.convert(new BigDecimal(12),
				new ReifiedType(Character.class)));
		assertEquals((int) -12, converter.convert(new BigDecimal(-12),
				new ReifiedType(Integer.class)));
		assertEquals((long) -12, converter.convert(new BigDecimal(-12),
				new ReifiedType(Long.class)));
		assertEquals((float) -12, converter.convert(new BigDecimal(-12),
				new ReifiedType(Float.class)));
		assertEquals((double) -12, converter.convert(new BigDecimal(-12),
				new ReifiedType(Double.class)));
		assertEquals(new BigInteger("-12"), converter.convert(new BigDecimal(
				-12), new ReifiedType(BigInteger.class)));
		assertEquals(new BigDecimal("-12"), converter.convert(new BigDecimal(
				-12), new ReifiedType(BigDecimal.class)));
		assertEquals("-12", converter.convert(new BigDecimal(-12),
				new ReifiedType(String.class)));
	}

	static class A {
	}

	static class B extends A {
	}

	static class C extends B {
	}

	static A	a	= new A();
	static B	b	= new B();
	static C	c	= new C();

	/**
	 * Check if we can use step 1: T is assignable for the class of s
	 */
	public void testAssignable() {
		// Test the simple ones
		assertEquals(new Byte((byte) 42), converter.convert(
				new Byte((byte) 42), new GenericType<Byte>(Byte.class)));
		assertEquals(new Short((short) 42), converter.convert(new Short(
				(short) 42), new GenericType<Short>(Short.class)));
		assertEquals(new Long(42), converter.convert(new Long(42),
				new GenericType<Long>(Long.class)));
		assertEquals(new Float(42), converter.convert(new Float(42),
				new GenericType<Float>(Float.class)));
		assertEquals(new Double(42), converter.convert(new Double(42),
				new GenericType<Double>(Double.class)));

		assertTrue("b should be assignable to an A because B extends A",
				b == converter.convert(b, new GenericType<A>(A.class)));
		assertTrue("b should be assignable to a B", b == converter.convert(b,
				new GenericType<A>(B.class)));
	}

	/**
	 * Check if we can us step 2: Try all converters
	 */

	public void testConverters() {

	}

	/**
	 * Step 3: Conversion to an array
	 */

	public static Collection<Integer>[]	intCollectionArray;
	public static Integer[][]			intArrayArray;
	public static int[][]				primitiveIntArrayArray;

	@SuppressWarnings("unchecked")
	public void testArrayTarget() throws NoSuchFieldException {
		List<List<String>> list = asList(asList("1"), asList("2"), asList("3"));

		// [["1"],["2"],["3"]] -> Integer[][]
		{
			intArrayArray = (Integer[][]) converter.convert(list,
					getFieldType("intArrayArray"));
			assertNotNull(intArrayArray);
			assertEquals(asList(asList(1), asList(2), asList(3)), asList(
					asList(intArrayArray[0]), asList(intArrayArray[1]),
					asList(intArrayArray[2])));
		}
		// [["1"],["2"],["3"]] -> Collection<Integer>[]
		{
			intCollectionArray = (Collection<Integer>[]) converter.convert(
					list, getFieldType("intCollectionArray"));
			assertNotNull(intCollectionArray);
			assertEquals(asList(asList(1), asList(2), asList(3)),
					asList(intCollectionArray));
		}
		{
			Integer[] array = converter.convert(asList(1, 2, 3),
					new GenericType(Integer[].class));
			assertNotNull(array);
			assertEquals(asList(1, 2, 3), asList(array));
		}
		{
			String[] array = converter.convert(asList("A", "B", "C"),
					new GenericType(String[].class));
			assertNotNull(array);
			assertEquals(asList("A", "B", "C"), asList(array));
		}

		{
			Integer[] array = converter.convert(asList("1", "2", "3"),
					new GenericType(Integer[].class));
			assertNotNull(array);
			assertEquals(asList(1, 2, 3), asList(array));
		}
		// [["1"],["2"],["3"]] -> int[][]
		{
			primitiveIntArrayArray = (int[][]) converter.convert(list,
					getFieldType("primitiveIntArrayArray"));
			assertNotNull(primitiveIntArrayArray);
			assertEquals(asList(asList(1), asList(2), asList(3)),// 
					asList(
							//
							asList(primitiveIntArrayArray[0][0]),
							asList(primitiveIntArrayArray[1][0]),
							asList(primitiveIntArrayArray[2][0]) //
					)//
			);
		}

		short[] shorts = {1, 2, 3};

		// short[] = -> long[]
		{
			long[] longs = converter.convert(shorts, new GenericType(
					long[].class));
			assertNotNull(longs);
			assertTrue(Arrays.equals(new long[] {1, 2, 3}, longs));
		}

		// short[] = -> int[]
		{
			int[] ints = converter
					.convert(shorts, new GenericType(int[].class));
			assertNotNull(ints);
			assertTrue(Arrays.equals(new int[] {1, 2, 3}, ints));
		}

		// short[] = -> byte[]
		{
			byte[] bytes = converter.convert(shorts, new GenericType(
					byte[].class));
			assertNotNull(bytes);
			assertTrue(Arrays.equals(new byte[] {1, 2, 3}, bytes));
		}

		// short[] = -> double[]
		{
			double[] doubles = converter.convert(shorts, new GenericType(
					double[].class));
			assertNotNull(doubles);
			assertTrue(Arrays.equals(new double[] {1, 2, 3}, doubles));
		}

		// short[] = -> float[]
		{
			float[] floats = converter.convert(shorts, new GenericType(
					float[].class));
			assertNotNull(floats);
			assertTrue(Arrays.equals(new float[] {1, 2, 3}, floats));
		}

		// short[] = -> byte[]
		{
			byte[] bytes = converter.convert(new short[] {1000},
					new GenericType(byte[].class));
			assertNull(bytes);
		}

	}

	/**
	 * Step 4 Collections
	 * 
	 */

	public static Collection<List<Set<Queue<SortedSet<Integer>>>>>	deepCollection;

	@SuppressWarnings("unchecked")
	public void testCollectionsDeep() throws Exception {
		{
			ReifiedType< ? > type = getFieldType("deepCollection");
			List<List<List<List<String[]>>>> source = asList(asList(asList(asList(
					new String[] {"1", "2"}, new String[] {"4", "3"}))));

			Collection<List<Set<Queue<SortedSet<Integer>>>>> convert = (Collection<List<Set<Queue<SortedSet<Integer>>>>>) converter
					.convert(source, type);

			List<Set<Queue<SortedSet<Integer>>>> c1 = convert.iterator().next();
			assertEquals(1, c1.size());

			Set<Queue<SortedSet<Integer>>> c2 = c1.iterator().next();
			assertEquals(1, c2.size());

			Queue<SortedSet<Integer>> c3 = c2.iterator().next();
			assertEquals(2, c3.size());

			Iterator<SortedSet<Integer>> i = c3.iterator();
			SortedSet<Integer> c4_1 = i.next();
			SortedSet<Integer> c4_2 = i.next();

			assertEquals(asList(1, 2), new ArrayList<Integer>(c4_1));
			assertEquals(asList(3, 4), new ArrayList<Integer>(c4_2));
		}
	}

	public static SortedSet<Integer>	sortedIntegers;

	@SuppressWarnings("unchecked")
	public void testCollectionsInterfaceToImplementation() throws Exception {
		Collection<Object> list = new Collection<Object>() {
			List<Object>	org	= new ArrayList();

			public boolean add(Object v) {
				return org.add(v);
			}

			public boolean addAll(Collection< ? extends Object> v) {
				return org.addAll(v);
			}

			public void clear() {
				org.clear();
			}

			public boolean contains(Object v) {
				return org.contains(v);
			}

			public boolean containsAll(Collection< ? > v) {
				return org.containsAll(v);
			}

			public boolean isEmpty() {
				return org.isEmpty();
			}

			public Iterator<Object> iterator() {
				return org.iterator();
			}

			public boolean remove(Object v) {
				return org.remove(v);
			}

			public boolean removeAll(Collection< ? > v) {
				return org.removeAll(v);
			}

			public boolean retainAll(Collection< ? > v) {
				return org.retainAll(v);
			}

			public int size() {
				return org.size();
			}

			public Object[] toArray() {
				return org.toArray();
			}

			public <T> T[] toArray(T[] v) {
				return org.toArray(v);
			}
		};

		assertTrue(converter.convert(list, new ReifiedType<Collection>(
				Collection.class)) instanceof ArrayList);
		assertTrue(converter.convert(list, new ReifiedType<List>(List.class)) instanceof ArrayList);
		assertTrue(converter.convert(list, new ReifiedType<Queue>(Queue.class)) instanceof LinkedList);
		assertTrue(converter.convert(list, new ReifiedType<Set>(Set.class)) instanceof HashSet);
		assertTrue(converter.convert(list, new ReifiedType<SortedSet>(
				SortedSet.class)) instanceof TreeSet);
	}

	/**
	 * Check if the primitives go well with collections
	 * 
	 */
	static public Collection<Boolean>	booleans;
	static public Collection<Byte>		bytes;
	static public Collection<Short>		shorts;
	static public Collection<Character>	chars;
	static public Collection<Integer>	integers;
	static public Collection<Long>		longs;
	static public Collection<Float>		floats;
	static public Collection<Double>	doubles;
	static public Collection<String>	strings;

	public void testCollectionsPrimitives() throws Exception {
		{
			ReifiedType< ? > type = getFieldType("booleans");
			List<Boolean> sb = asList(true, true, true);
			List<Boolean> sbb = asList(false, true);
			assertEquals(sb, converter.convert(new byte[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new short[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new int[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new long[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new float[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new double[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new String[] {"on", "yes",
					"true"}, type));
			assertEquals(sb, converter.convert(new char[] {1, 2, 3}, type));
			assertEquals(sbb, converter.convert(new boolean[] {false, true},
					type));
		}
		{
			ReifiedType< ? > type = getFieldType("bytes");
			List<Byte> sb = asList((byte) 1, (byte) 2, (byte) 3);
			List<Byte> sbb = asList((byte) 0, (byte) 1);
			assertEquals(sb, converter.convert(new byte[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new short[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new int[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new long[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new float[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new double[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new String[] {"1", "2", "3"},
					type));
			assertEquals(sb, converter.convert(new char[] {1, 2, 3}, type));
			assertEquals(sbb, converter.convert(new boolean[] {false, true},
					type));
		}
		{
			ReifiedType< ? > type = getFieldType("shorts");
			List<Short> sb = asList((short) 1, (short) 2, (short) 3);
			List<Short> sbb = asList((short) 0, (short) 1);
			assertEquals(sb, converter.convert(new byte[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new short[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new int[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new long[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new float[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new double[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new String[] {"1", "2", "3"},
					type));
			assertEquals(sb, converter.convert(new char[] {1, 2, 3}, type));
			assertEquals(sbb, converter.convert(new boolean[] {false, true},
					type));
		}
		{
			ReifiedType< ? > type = getFieldType("chars");
			List<Character> sb = asList((char) 1, (char) 2, (char) 3);
			List<Character> sbb = asList((char) 0, (char) 1);
			assertEquals(sb, converter.convert(new byte[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new short[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new int[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new long[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new float[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new double[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new String[] {"1", "2", "3"},
					type));
			assertEquals(sb, converter.convert(new char[] {1, 2, 3}, type));
			assertEquals(sbb, converter.convert(new boolean[] {false, true},
					type));
		}
		{
			ReifiedType< ? > type = getFieldType("integers");
			List<Integer> sb = asList((int) 1, (int) 2, (int) 3);
			List<Integer> sbb = asList((int) 0, (int) 1);
			assertEquals(sb, converter.convert(new byte[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new short[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new int[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new long[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new float[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new double[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new String[] {"1", "2", "3"},
					type));
			assertEquals(sb, converter.convert(new char[] {1, 2, 3}, type));
			assertEquals(sbb, converter.convert(new boolean[] {false, true},
					type));
		}
		{
			ReifiedType< ? > type = getFieldType("longs");
			List<Long> sb = asList((long) 1, (long) 2, (long) 3);
			List<Long> sbb = asList((long) 0, (long) 1);
			assertEquals(sb, converter.convert(new byte[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new short[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new int[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new long[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new float[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new double[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new String[] {"1", "2", "3"},
					type));
			assertEquals(sb, converter.convert(new char[] {1, 2, 3}, type));
			assertEquals(sbb, converter.convert(new boolean[] {false, true},
					type));
		}
		{
			ReifiedType< ? > type = getFieldType("floats");
			List<Float> sb = asList((float) 1, (float) 2, (float) 3);
			List<Float> sbb = asList((float) 0, (float) 1);
			assertEquals(sb, converter.convert(new byte[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new short[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new int[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new long[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new float[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new double[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new String[] {"1", "2", "3"},
					type));
			assertEquals(sb, converter.convert(new char[] {1, 2, 3}, type));
			assertEquals(sbb, converter.convert(new boolean[] {false, true},
					type));
		}
		{
			ReifiedType< ? > type = getFieldType("doubles");
			List<Double> sb = asList((double) 1, (double) 2, (double) 3);
			List<Double> sbb = asList((double) 0, (double) 1);
			assertEquals(sb, converter.convert(new byte[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new short[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new int[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new long[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new float[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new double[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new String[] {"1", "2", "3"},
					type));
			assertEquals(sb, converter.convert(new char[] {1, 2, 3}, type));
			assertEquals(sbb, converter.convert(new boolean[] {false, true},
					type));
		}
		{
			ReifiedType< ? > type = getFieldType("strings");
			List<String> sb = asList("1", "2", "3");
			List<String> sba = asList("A", "B", "C");
			List<String> sbf = asList("1.0", "2.0", "3.0");
			List<String> sbb = asList("false", "true");
			assertEquals(sb, converter.convert(new byte[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new short[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new int[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new long[] {1, 2, 3}, type));
			assertEquals(sbf, converter.convert(new float[] {1, 2, 3}, type));
			assertEquals(sbf, converter.convert(new double[] {1, 2, 3}, type));
			assertEquals(sb, converter.convert(new String[] {"1", "2", "3"},
					type));
			assertEquals(sba, converter.convert(new char[] {0x41, 0x42, 0x43},
					type));
			assertEquals(sbb, converter.convert(new boolean[] {false, true},
					type));
		}
	}

	ReifiedType< ? > getFieldType(String fieldName) throws NoSuchFieldException {
		Field field = getClass().getField(fieldName);
		GenericType< ? > gt = new GenericType<Object>(field.getGenericType());
		return gt;
	}

	/**
	 * Test map simple
	 */

	public static Map<String, String>			simpletestmap;
	public static Dictionary<String, String>	simpletestdictionary;

	@SuppressWarnings("unchecked")
	public void testMap() throws Exception {

		{
			ReifiedType< ? > type = getFieldType("simpletestmap");
			Dictionary<Integer, Integer> d = new Hashtable<Integer, Integer>();
			d.put(1, 2);
			d.put(2, 4);
			simpletestmap = (Map<String, String>) converter.convert(d, type);
		}
		{
			ReifiedType< ? > type = getFieldType("simpletestmap");
			Dictionary<Integer, Integer> d = new Dictionary<Integer, Integer>() {
				Map<Integer, Integer>	source	= new HashMap<Integer, Integer>();

				@Override
				public Enumeration<Integer> elements() {
					return Collections.enumeration(source.values());
				}

				@Override
				public Integer get(Object key) {
					return source.get(key);
				}

				@Override
				public boolean isEmpty() {
					return source.isEmpty();
				}

				@Override
				public Enumeration<Integer> keys() {
					return Collections.enumeration(source.keySet());
				}

				@Override
				public Integer put(Integer var0, Integer var1) {
					return source.put(var0, var1);
				}

				@Override
				public Integer remove(Object var0) {
					return source.remove(var0);
				}

				@Override
				public int size() {
					return source.size();
				}
			};

			d.put(1, 2);
			d.put(2, 4);
			simpletestmap = (Map<String, String>) converter.convert(d, type);
			assertTrue(simpletestmap instanceof LinkedHashMap);
			assertEquals(2, simpletestmap.size());
			assertEquals("2", simpletestmap.get("1"));
			assertEquals("4", simpletestmap.get("2"));
		}
		{
			ReifiedType< ? > type = getFieldType("simpletestdictionary");
			Map<Integer, Integer> map = new HashMap<Integer, Integer>();
			map.put(42, 84);
			map.put(84, 168);

			simpletestdictionary = (Dictionary<String, String>) converter
					.convert(map, type);
			assertTrue(simpletestdictionary instanceof Dictionary);
			assertEquals(2, simpletestdictionary.size());
			assertEquals("84", simpletestdictionary.get("42"));
			assertEquals("168", simpletestdictionary.get("84"));
		}

		// map to dictionary
	}

	/**
	 * Test map
	 */

	public static Map<String, Collection<Map<Short, Long>>>	testmap;

	@SuppressWarnings("unchecked")
	public void testMapComplex() throws Exception {
		ReifiedType< ? > type = getFieldType("testmap");
		Hashtable<BigInteger, BigInteger> internal1 = new Hashtable<BigInteger, BigInteger>();
		internal1.put(new BigInteger("1"), new BigInteger("2"));
		internal1.put(new BigInteger("2"), new BigInteger("4"));
		internal1.put(new BigInteger("3"), new BigInteger("6"));
		Hashtable<BigInteger, BigInteger> internal2 = new Hashtable<BigInteger, BigInteger>();
		internal2.put(new BigInteger("4"), new BigInteger("8"));
		internal2.put(new BigInteger("5"), new BigInteger("10"));
		internal2.put(new BigInteger("6"), new BigInteger("12"));

		Set<Dictionary<BigInteger, BigInteger>> set = new LinkedHashSet<Dictionary<BigInteger, BigInteger>>();
		set.add(internal1);
		set.add(internal2);

		Hashtable<Integer, Set<Dictionary<BigInteger, BigInteger>>> ht = new Hashtable<Integer, Set<Dictionary<BigInteger, BigInteger>>>();
		ht.put(1, set);
		ht.put(2, set);

		testmap = (Map<String, Collection<Map<Short, Long>>>) converter
				.convert(ht, type);
		assertTrue(testmap.containsKey("1"));
		assertTrue(testmap.containsKey("2"));

		assertEquals(2, testmap.size());
		Collection<Map<Short, Long>> c1 = testmap.get("1");
		Collection<Map<Short, Long>> c2 = testmap.get("2");
		assertTrue(c1 instanceof ArrayList< ? >);
		assertTrue(c2 instanceof ArrayList< ? >);

		assertNotNull(c1);
		assertNotNull(c2);
		assertEquals(c1, c2);
		assertEquals(2, c1.size());
		assertFalse(c1 == c2); // Should have created a new one

		Iterator<Map<Short, Long>> i = c1.iterator();
		Map<Short, Long> map1 = i.next();
		Map<Short, Long> map2 = i.next();
		assertEquals(3, map1.size());
		assertEquals(3, map2.size());

		for (Map.Entry<Short, Long> entry : map1.entrySet()) {
			long l = entry.getValue();
			short s = entry.getKey();
			assertEquals(l, s * 2);
		}
		for (Map.Entry<Short, Long> entry : map2.entrySet()) {
			long l = entry.getValue();
			short s = entry.getKey();
			assertEquals(l, s * 2);
		}
	}

	/**
	 * Pattern
	 */

	@SuppressWarnings("unchecked")
	public void testPattern() throws Exception {
		Pattern p = converter.convert("Hello\\s+World", new ReifiedType(
				Pattern.class));
		assertTrue(p.matcher("Hello      World").matches());
	}

	/**
	 * Pattern
	 */

	@SuppressWarnings("unchecked")
	public void testLocale() throws Exception {
		{
			Locale p = converter.convert("nl_be_vl", new ReifiedType(
					Locale.class));
			assertEquals("nl_BE_vl", p.toString());
			assertEquals("BE", p.getCountry());
			assertEquals("nl", p.getLanguage());
			assertEquals("vl", p.getVariant());
		}
		{
			Locale p = converter
					.convert("nl_be", new ReifiedType(Locale.class));
			assertEquals("nl_BE", p.toString());
			assertEquals("BE", p.getCountry());
			assertEquals("nl", p.getLanguage());
			assertEquals("", p.getVariant());
		}
		{
			Locale p = converter.convert("nl", new ReifiedType(Locale.class));
			assertEquals("nl", p.toString());
			assertEquals("nl", p.getLanguage());
			assertEquals("", p.getCountry());
			assertEquals("", p.getVariant());
		}
	}

	/**
	 * Properties
	 */

	@SuppressWarnings("unchecked")
	public void testProperties() throws Exception {
		Properties p = converter.convert(
				"a=1\nb=2\\\n3\nc   =    5\nd=Loïc Cotonéa", new ReifiedType(
						Properties.class));
		assertEquals("1", p.getProperty("a"));
		assertEquals("23", p.getProperty("b"));
		assertEquals("5", p.getProperty("c"));
		assertEquals("Loïc Cotonéa", p.getProperty("d"));
	}

	/**
	 * Enums
	 */

	public static enum Alpha {
		A, B, C;
	}

	@SuppressWarnings("unchecked")
	public void testEnum() throws Exception {
		{
			Alpha a = converter.convert("B", new ReifiedType(Alpha.class));
			assertEquals(Alpha.B, a);
		}
		{
			Alpha a = converter.convert("c", new ReifiedType(Alpha.class));
			assertNull(a);
		}
	}

	/**
	 * Class load
	 */

	@SuppressWarnings("unchecked")
	public void testClassLoad() throws Exception {
		Class< ? > c = converter.convert(getClass().getName(), new ReifiedType(
				Class.class));
		assertNotNull(c);
		assertEquals(getClass(), c);

		// TODO multiple bundles loading
	}

}
