package testbp;

import static java.util.Arrays.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.regex.*;

import junit.framework.*;

public class BasicConverterTest<A extends Integer, T extends LinkedList<Long>, B extends A>
		extends TestCase {
	BasicConverter converter = new BasicConverter();

	/**
	 * Convert some basic types.
	 */
	public int primitiveInteger;
	public long primitiveLong;

	public void testNumbers() throws Exception {
		primitiveInteger = (Integer) converter.convert("42",
				getType("primitiveInteger"));
		primitiveLong = (Long) converter
				.convert("42", getType("primitiveLong"));
	}

	/**
	 * Convert generic lists in different type forms.
	 */
	public List<String> listString; // Simple Parameterized Type, no conversion
	public List<Integer> listIntegers; // Parametrized Type with raw class
	// argument based conv.
	public List<? extends Integer> listWildcardIntegers; // Wildcard type
	// member conversion
	public List<? super Integer> listWildcardSuperIntegers; // Wildcard super
	// type member
	// conversion
	public LinkedList<Integer> linkedIntegers; // Other target class
	public List<A> listA; // Use a type variable constrained to Integer
	public T listT; // Type Variable, Must be a LinkedList<Long>
	public List<T> listT2; // Parameterized Type with a Type Variable as
	// argument (list of list)
	public LinkedList<B> listB; // Other target list, member is B is a
	// constrained version of A, which extends
	// Integer
	public Vector<B> vectorB; // See if Vctor works

	public void testCollections() throws Exception {
		assertList("listString", Arrays.asList("a", "b"), Arrays.asList("a",
				"b"), ArrayList.class);

		List<String> numstrings = Arrays.asList("1", "15");
		List<Integer> ints = Arrays.asList(1, 15);
		List<Long> longs = Arrays.asList(new Long(1), new Long(15));
		List<Byte> bytes = Arrays.asList(new Byte((byte) 1),
				new Byte((byte) 15));

		assertList("listIntegers", numstrings, ints, ArrayList.class);
		assertList("listIntegers", longs, ints, ArrayList.class);
		assertList("listIntegers", ints, ints, ArrayList.class);
		assertList("listIntegers", bytes, ints, ArrayList.class);

		assertList("listWildcardIntegers", numstrings, ints, ArrayList.class);
		assertList("listWildcardIntegers", longs, ints, ArrayList.class);

		assertList("listWildcardSuperIntegers", numstrings, ints,
				ArrayList.class);
		assertList("listWildcardSuperIntegers", longs, ints, ArrayList.class);

		assertList("linkedIntegers", numstrings, ints, LinkedList.class);

		assertList("listA", numstrings, ints, ArrayList.class);

		assertList("listT", numstrings, longs, LinkedList.class);

		List<List<String>> aa = asList(numstrings, numstrings);
		List<List<Long>> ii = asList(longs, longs);
		assertList("listT2", aa, ii, ArrayList.class);

		assertList("listB", numstrings, ints, LinkedList.class);
		assertList("vectorB", numstrings, ints, Vector.class);
	}

	/**
	 * Helper to help with lists assertions
	 * 
	 * @param field
	 * @param source
	 * @param expected
	 * @param impl
	 * @throws Exception
	 */
	private void assertList(String field, List<?> source, List<?> expected,
			Class<?> impl) throws Exception {
		Collection<?> result = (Collection<?>) converter.convert(source,
				getType(field));
		String del = "[";
		for (Object o : result) {
			System.out.print(del + "(" + o.getClass().getSimpleName() + ") "
					+ o);
			del = ", ";
		}
		System.out.println("]");
		ArrayList<?> a = new ArrayList<Object>(expected);
		ArrayList<?> b = new ArrayList<Object>(result);
		assertEquals(a, b);
		assertEquals(impl, result.getClass());
	}

	/**
	 * Test pattern
	 */
	public Pattern pattern;

	public void testPattern() throws Exception {
		pattern = (Pattern) converter.convert("[a-z]+", getType("pattern"));
		assertEquals("[a-z]+", pattern.pattern());
	}

	/**
	 * Get the type of a field in this class
	 * 
	 * @param field
	 *            name of the field
	 * @return the Type associated with te field
	 * @throws Exception
	 */

	Type getType(String field) throws Exception {
		return getClass().getField(field).getGenericType();
	}
}
