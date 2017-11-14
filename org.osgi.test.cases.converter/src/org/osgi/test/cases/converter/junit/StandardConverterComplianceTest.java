package org.osgi.test.cases.converter.junit;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.osgi.util.converter.Converter;
import org.osgi.util.converter.Converters;
import org.osgi.util.converter.TypeReference;

import junit.framework.TestCase;

/**
 * 
 */
public class StandardConverterComplianceTest extends TestCase{

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
	public void testGenericConversion() {
		Converter converter = Converters.standardConverter();
		List<String> list = converter.convert(Arrays.<Integer> asList(1, 2, 3))
				.to(new TypeReference<List<String>>() {});

		assertNotNull(list);
		assertEquals(3, list.size());
	}

}
