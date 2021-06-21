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

import static java.util.Collections.singletonMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.osgi.dto.DTO;
import org.osgi.util.converter.Converter;
import org.osgi.util.converter.Converters;
import org.osgi.util.converter.TypeReference;

/**
 * 
 */
public class StandardConverterComplianceTest {

	/**
	 * Section 707.4 : Conversions
	 * <p/>
	 * For scalars, conversions are only performed when the target type is not
	 * compatible with the source type. For example, when requesting to convert
	 * a java.math.BigDecimal to a java.lang.Number the big decimal is simply
	 * used as-is as this type is assignable to the requested target type.
	 * <p/>
	 * In the case of arrays, Collections and Map-like structures a new object
	 * is always returned, even if the target type is compatible with the source
	 * type. This copy can be owned and optionally further modified by the
	 * caller.
	 */
	@Test
	public void testUnecessaryConversion()
	{
		BigDecimal bd = new BigDecimal(5);
		Converter c = Converters.standardConverter();
		Number nb = c.convert(bd).to(Number.class);
		assertSame(bd,nb);

		List<Long> toBeConverted = new ArrayList<Long>();
		toBeConverted.add(5l);
		toBeConverted.add(113l);
		toBeConverted.add(24l);

		Converter converter = Converters.standardConverter();

		Collection<Number> collectionConverted = converter
				.convert(toBeConverted)
				.to(new TypeReference<Collection<Number>>() {});

		Iterator<Number> iterator = collectionConverted.iterator();

		for (int index = 0; index < 3; index++) {
			assertEquals(toBeConverted.get(index).longValue(),
					iterator.next().longValue());
		}
		assertFalse(iterator.hasNext());
		assertNotSame(toBeConverted, collectionConverted);
	}

	/**
	 * Section 707.4 : Conversions
	 * <p/>
	 * 707.4.1 - Generics
	 * <p/>
	 * When converting to a target type with generic type parameters it is
	 * necessary to capture thes to instruct the converter to produce the
	 * correct parameterized type. This can be achieved with the TypeReference
	 * based APIs, for example:
	 * <p/>
	 * Converter c = Converters.standardConverter(); List<Long> list =
	 * c.convert("123").to(new TypeReference<List<Long>>()); // list will
	 * contain the Long value 123L
	 */
	@Test
	public void testGenericConversion() {
		Converter converter = Converters.standardConverter();
		List<String> list = converter.convert(Arrays.<Integer> asList(1, 2, 3))
				.to(new TypeReference<List<String>>() {});

		assertNotNull(list);
		assertEquals(3, list.size());
	}

	/**
	 * Section 707.4 : Conversions
	 * <p/>
	 * 707.4.1 - Generics
	 * <p/>
	 * When converting to a target type with generic fields the converter should
	 * convert the nested data as necessary
	 */
	@Test
	public void testGenericFieldConversion() {
		Converter converter = Converters.standardConverter();
		GenericFieldDto dto = converter
				.convert(singletonMap("values",
						Arrays.<Integer> asList(1, 2, 3)))
				.to(GenericFieldDto.class);

		assertNotNull(dto);
		assertEquals(Arrays.asList("1", "2", "3"), dto.values);
	}

	public static class GenericFieldDto extends DTO {
		public List<String> values;
	}

	/**
	 * Section 707.4 : Conversions
	 * <p/>
	 * 707.4.1 - Generics
	 * <p/>
	 * When converting to a target type with parameterized fields the converter
	 * should convert the nested data as defined by the {@link TypeReference}
	 */
	@Test
	public void testParameterizedGenericFieldConversion() {
		Converter converter = Converters.standardConverter();
		ParameterizedFieldDto<String> dto = converter
				.convert(singletonMap("values",
						Arrays.<Integer> asList(1, 2, 3)))
				.to(new TypeReference<ParameterizedFieldDto<String>>() {});

		assertNotNull(dto);
		assertEquals(Arrays.asList("1", "2", "3"), dto.values);

		ParameterizedFieldDto<Long> dto2 = converter
				.convert(singletonMap("values",
						Arrays.<Integer> asList(1, 2, 3)))
				.to(new TypeReference<ParameterizedFieldDto<Long>>() {});

		assertNotNull(dto2);
		assertEquals(Arrays.asList(1L, 2L, 3L), dto2.values);
	}

	public static class ParameterizedFieldDto<T> extends DTO {
		public List<T> values;
	}

	/**
	 * Section 707.4 : Conversions
	 * <p/>
	 * 707.4.1 - Generics
	 * <p/>
	 * When converting to a target type which reifies parameterized fields the
	 * converter should convert the nested data as defined by the type variables
	 */
	@Test
	public void testReifiedGenericFieldConversion() {
		Converter converter = Converters.standardConverter();
		ReifiedFieldDto dto = converter
				.convert(singletonMap("values",
						Arrays.<Integer> asList(1, 2, 3)))
				.to(ReifiedFieldDto.class);

		assertNotNull(dto);
		assertEquals(Arrays.asList("1", "2", "3"), dto.values);
	}

	/**
	 * Section 707.4 : Conversions
	 * <p/>
	 * 707.4.1 - Generics
	 * <p/>
	 * When converting to a target type which reifies parameterized fields the
	 * converter should convert the nested data as defined by the type variables
	 * This test looks at a subclass that indirectly binds a type variable
	 */
	@Test
	public void testReifiedGenericFieldConversionSubclass() {
		Converter converter = Converters.standardConverter();
		ReifiedFieldDtoSub dto = converter
				.convert(singletonMap("values",
						Arrays.<Integer> asList(1, 2, 3)))
				.to(ReifiedFieldDtoSub.class);

		assertNotNull(dto);
		assertEquals(Arrays.asList("1", "2", "3"), dto.values);
	}

	public static class ReifiedFieldDto extends ParameterizedFieldDto<String> {}

	public static class ReifiedFieldDtoSub extends ReifiedFieldDto {}

	/**
	 * Section 707.4 : Conversions
	 * <p/>
	 * 707.4.1 - Generics
	 * <p/>
	 * When converting to a target type which reifies parameterized fields the
	 * converter should convert the nested data as defined by the type variables
	 * This test looks at a subclass that indirectly binds a type variable
	 */
	@Test
	public void testWildcardGenerics() {

		HashSet<Character> charSet = new HashSet<>(
				Arrays.asList('f', 'o', 'o'));

		Map<String,Object> map = new HashMap<>();
		map.put("charSet", charSet);

		Converter converter = Converters.standardConverter();

		Map<String, ? > m = converter.convert(map)
				.to(new TypeReference<Map<String, ? >>() {});
		assertEquals(1, m.size());
		assertEquals(charSet, m.get("charSet"));

		m = converter.convert(map)
				.to(new TypeReference<Map<String, ? extends List<String>>>() {});
		assertEquals(1, m.size());

		List<String> list = new ArrayList<>();
		for (Character character : charSet) {
			list.add(String.valueOf(character));
		}

		assertEquals(list, m.get("charSet"));
	}

}
