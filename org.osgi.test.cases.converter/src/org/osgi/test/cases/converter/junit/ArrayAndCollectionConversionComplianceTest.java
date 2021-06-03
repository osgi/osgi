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

package org.osgi.test.cases.converter.junit;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.osgi.util.converter.ConversionException;
import org.osgi.util.converter.Converter;
import org.osgi.util.converter.ConverterBuilder;
import org.osgi.util.converter.Converters;
import org.osgi.util.converter.Rule;
import org.osgi.util.converter.TypeReference;
import org.osgi.util.function.Function;


/**
 * 707 Converter specification
 */
public class ArrayAndCollectionConversionComplianceTest {

	/**
	 * Section 707.4.3 : Arrays and Collections
	 * <p/>
	 * 707.4.3.1- Converting from a scalar
	 * <p/>
	 * Scalars are converted into a Collection or Array by creating an instance
	 * of the target type suitable for holding a single element. The scalar
	 * source object will be converted to target element type if necessary and
	 * then set as the element.
	 * <p/>
	 * A null value will result in an empty Collection or Array.
	 * <p/>
	 * Exceptions: Converting a String to a char[] or Character[] will result in
	 * an array with characters representing the characters in the String.
	 */
	@Test
	public void testConversionFromScalar() {
		Converter converter = Converters.standardConverter();

		String scalar = "225";
		Set<Long> converted = converter.convert(scalar)
				.to(new TypeReference<Set<Long>>() {});
		assertEquals(1, converted.size());
		assertTrue(Long.valueOf(225).equals(converted.iterator().next()));

		char[] scalarCharacters = converter.convert(scalar).to(char[].class);
		assertEquals(3, scalarCharacters.length);
		assertEquals('2', scalarCharacters[0]);
		assertEquals('2', scalarCharacters[1]);
		assertEquals('5', scalarCharacters[2]);

		converted = converter.convert(null)
				.to(new TypeReference<Set<Long>>() {});
		assertTrue(converted.isEmpty());
	}
	
	/**
	 * Section 707.4.3 : Arrays and Collections
	 * <p/>
	 * 707.4.3.2- Converting to a scalar
	 * <p/>
	 * If a Collection or array needs to be converted to a scalar, the first
	 * element is taken and converted into the target type. If the collection or
	 * array has no elements, the <code>null</code> value is used to convert
	 * into the target type.
	 * <p/>
	 * <em>Note: </em>deviations from this mechanism can be achieved by using a
	 * ConverterBuilder. <em>Exceptions: </em>
	 * <p/>
	 * <ul>
	 * <li>Converting a <code>char[]</code> or <code>Character[]</code> into a
	 * <code>String</code> results in a String where each character represents
	 * the elements of the character array.
	 */
	@Test
	public void testConversionToScalar() {
		Converter converter = Converters.standardConverter();
		List<Long> longList = Arrays.asList(5l, 8l, 10l);

		Long converted = converter.convert(longList).to(Long.class);
		assertNotNull(converted);
		assertEquals(5l, converted.longValue());

		longList = new ArrayList<Long>();
		converted = converter.convert(longList).to(Long.class);
		assertNull(converted);
		
		String resultString = "chars array";
		String convertedString = converter.convert(resultString.toCharArray()).to(String.class);
		assertEquals(resultString, convertedString);
		
		ConverterBuilder cb = converter.newConverterBuilder();
		cb.rule(new Rule<int[], String>(new Function<int[],String>(){
			@Override
			public String apply(int[] t)  {
				StringBuilder builder = new StringBuilder();
				for (int i = 0; i < t.length; i++) {
					if (i > 0)
						builder.append(",");
					builder.append(String.valueOf(t[i]));
				}
				return builder.toString();
			}
		}) {});
		cb.rule(new Rule<String,int[]>(new Function<String,int[]>() {
			@Override
			public int[] apply(String t) {
				String[] ts = t.split(",");
				int[] result = new int[ts.length];
				for (int i = 0; i < ts.length; i++) {
					result[i] = Integer.parseInt(ts[i]);
				}
				return result;
			}
		}) {});
		Converter c = cb.build();
		String s2 = c.convert(new int[] {
				1, 2
		}).to(String.class);
		assertEquals("1,2", s2);
		int[] sa = c.convert("1,2").to(int[].class);
		assertEquals(2, sa.length);
		assertEquals(1, sa[0]);
		assertEquals(2, sa[1]);
		
	}

	/**
	 * Section 707.4.3 : Arrays and Collections
	 * <p/>
	 * 707.4.3.3- Converting to an Array or Collection
	 * <p/>
	 * When converting an Array or Collection to a <code>java.util.Collection</code>,
	 * <code>java.util.List</code> or <code>java.util.Set</code> the converter 
	 * will return a live view over the backing object that changes when the backing 
	 * object changes. The live view can be prevented by providing the copy() modifier.
	 * When converting to other collection types or arrays a copy is always
	 * produced.
	 * <p/>
	 * In all cases the object returned is a separate instance that can be owned
	 * by the client. Once the client modifies the returned object a live view
	 * will stop reflecting changes to the backing object.
	 * <p/>
	 * <table>
	 * <tr>
	 * <th>Target</th>
	 * <th>Method</th>
	 * </tr>
	 * <tr>
	 * <td><code>Collection</code>, <code>List</code> or <code>Set</code></td>
	 * <td>A live view over the backing object is created, changes to the
	 * backing object will be reflected, unless the view object is modified by
	 * the client.</td>
	 * </tr>
	 * <tr>
	 * <td>Other Collection interface</td>
	 * <td>A mutable implementation is created. For example, if the target type
	 * is <code>java.util.Queue</code> then the converter can create a
	 * <code>java.util.LinkedList</code>. When converting to a subinterface of
	 * <code>java.util.Set</code> the converter must choose a set implementation
	 * that preserves iteration order.</td>
	 * </tr>
	 * <tr>
	 * <td>Collection concrete type</td>
	 * <td>A new instance is created by calling <code>Class.newInstance()</code>
	 * on the provided type. For example if the target type is
	 * <code>ArrayDeque</code> then the converter creates a target object by
	 * calling <code>ArrayDeque.class.newInstance()</code>. The converter may
	 * choose to use a call a well-known constructor to optimize the creation of
	 * the collection.</td>
	 * </tr>
	 * <tr>
	 * <td><code>T[]</code></td>
	 * <td>A new array is created via
	 * <code>Array.newInstance(Class&lt;T&gt; cls, int x)</code> where
	 * <code>x</code> is the required size of the target collection.</td>
	 * </tr>
	 * </table>
	 * <p/>
	 * Before inserting values into the resulting collection/array they are
	 * converted to the desired target type. In the case of arrays this is the
	 * type of the array. When inserting into a Collection generic type
	 * information about the target type can be made available by using the
	 * <code>to(TypeReference)</code> or <code>to(Type)</code> methods. If no
	 * type information is available, source elements are inserted into the
	 * target object as-is without further treatment.
	 * <p/>
	 * The following example converts an array of <code>int</code>s into a set
	 * of <code>Double</code>s. Note that the resulting set must preserve the
	 * same iteration order as the original array:
	 * <p/>
	 * Set&lt;Double&gt; result = converter.convert(new int[] {2,3,2,1}). to(new
	 * TypeReference&lt;Set&lt;Double&gt;&gt;() {}) // result is 2.0, 3.0, 1.0
	 * <p/>
	 * Values are inserted in the target Collection/array as follows:
	 * <p/>
	 * <ul>
	 * <li>If the source object is <code>null</code>, an empty collection/array
	 * is produced.</li>
	 * <li>If the source is a Collection or Array, then each of its elements is
	 * converted into desired target type, if known, before inserting. Elements
	 * are inserted into the target collection in their normal iteration order.
	 * </li>
	 * <li>If the source is a Map-like structure (as described in the section
	 * called Maps, Interfaces, Java Beans, DTOs and Annotations) then
	 * <code>Map.Entry</code> elements are obtained from it by converting the
	 * source to a <code>Map</code> (if needed) and then
	 * calling<code>Map.entrySet()</code>. Each <code>Map.Entry</code> element
	 * is then converted into the target type as described in the section called
	 * Map.Entry before inserting in the target.</li>
	 * </ul>
	 */
	@Test
	public void testConversionToArrayOrCollection() {
		int[] backingObject = new int[] {
				1, 2, 3
		};

		Converter converter = Converters.standardConverter();
		List<Long> converted = converter.convert(backingObject).view()
				.to(new TypeReference<List<Long>>() {});

		Iterator<Long> iterator = converted.iterator();
		for (int index = 0; index < 3; index++) {
			assertEquals(backingObject[index], iterator.next().intValue());
		}
		assertFalse(iterator.hasNext());

		backingObject[2] = 5;
		iterator = converted.iterator();

		for (int index = 0; index < 3; index++) {
			assertEquals(backingObject[index], iterator.next().intValue());
		}
		assertFalse(iterator.hasNext());
		converted.set(2, 8l);

		iterator = converted.iterator();
		assertEquals(Long.valueOf(1l), iterator.next());
		assertEquals(Long.valueOf(2l), iterator.next());
		assertEquals(Long.valueOf(8l), iterator.next());
		assertFalse(iterator.hasNext());

		// preserve iteration order
		int[] arr = new int[] {
				2, 3, 2, 1
		};
		Set<Double> result = converter.convert(arr). to(new
			TypeReference<Set<Double>>() {});

		// int n = 0;
		Iterator<Double> resultIterator = result.iterator();
		assertEquals(2.0, resultIterator.next(), 0.00001);
		assertEquals(3.0, resultIterator.next(), 0.00001);
		assertEquals(1.0, resultIterator.next(), 0.00001);
		assertFalse(resultIterator.hasNext());
	}

	/**
	 * Section 707.4.3 : Arrays and Collections
	 * <p/>
	 * 707.4.3.4- Converting to maps
	 * <p/>
	 * Conversion to a map-like structure from an Array or Collection is not
	 * supported by the Standard Converter.
	 */
	@Test
	public void testToMapConversion() {
		Converter converter = Converters.standardConverter();
		List<Integer> list = new ArrayList<Integer>();
		list.add(1);
		list.add(5);
		try {
			converter.convert(list).to(new TypeReference<Map<String,Integer>>() {});
			fail("Conversion to a map-like structure from an Array or Collection is not supported by the Standard Converter");
		} catch (ConversionException e) {}
	}

}
