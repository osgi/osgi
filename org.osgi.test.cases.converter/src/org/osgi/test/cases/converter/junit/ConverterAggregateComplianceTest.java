package org.osgi.test.cases.converter.junit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.osgi.test.cases.converter.junit.ConverterComplianceTest.MyInterfaceProvidingLong;
import org.osgi.util.converter.Converter;
import org.osgi.util.converter.ConverterBuilder;
import org.osgi.util.converter.Rule;
import org.osgi.util.converter.StandardConverter;
import org.osgi.util.converter.TypeReference;
import org.osgi.util.function.Function;

import junit.framework.TestCase;


/**
 * Chapter 148  - Converter
 */
public class ConverterAggregateComplianceTest extends TestCase {

	/**
	 * Section 148.4  - Conversion
	 * 
	 * [...]In the case of Aggregates (Arrays and Collections) and Map-like 
	 * structures a new object is always returned, even if the target type
	 * is compatible with the source type. This copy can be owned and optionally
	 * further modified by the caller
	 */
	public void testAggregateUnecessaryConversion()
	{
		List<Long> toBeConverted = new ArrayList<Long>();
		toBeConverted.add(5l);
		toBeConverted.add(113l);
		toBeConverted.add(24l);
		
		Converter converter = new StandardConverter();
		
		Collection<Number> collectionConverted = converter.convert(
			toBeConverted).to(new TypeReference<Collection<Number>>(){});
		
		Iterator<Number> iterator = collectionConverted.iterator();
		
		for(int index = 0;index <3;index++)
		{
			assertEquals(toBeConverted.get(index).longValue(),
					iterator.next().longValue());
		}
		assertFalse(iterator.hasNext());
		assertNotSame(toBeConverted,collectionConverted);
	}
	
	/**
	 * Section 148.4.1  - Generics
	 * 
	 * When converting to a target type with generic type parameters it is
	 * necessary to capture the generic type parameters to instruct the 
	 * converter to produce the correct parameterized type. This can be 
	 * achieved with the TypeReference based APIs
	 */
	public void testGenericConversion()
	{
		Converter converter = new StandardConverter();
		List<String> list = converter.convert(Arrays.<Integer>asList(1,2,3)).to(
				new TypeReference<List<String>>(){});
		
		assertNotNull(list);
		assertEquals(3, list.size());
	}

	/**
	 * Section 148.4.3.1 - Converting to a scalar
	 * 
	 * If a collection or an array needs to be converted to a scalar, the
	 * first element is taken and converted into the target type.
	 */
	public void testAggregateConversionToScalar()
	{
		Converter converter = new StandardConverter();
		List<Long> longList = Arrays.asList(5l,8l,10l);

		Long converted = converter.convert(longList).to(Long.class);
		assertNotNull(converted);
		assertEquals(5l, converted.longValue());
	}

	/**
	 * Section 148.4.3.1 - Converting to a scalar
	 * 
	 * If the collection or array has no elements, the null value is used
	 * to convert into the target type
	 */
	public void testAggregateConversionToScalarEmpty()
	{
		Converter converter = new StandardConverter();
		List<Long> longList = new ArrayList<Long>();
		
		Long converted = converter.convert(longList).to(Long.class);
		assertNull(converted);
	}
	
	/**
	 * Section 148.4.3.2 - Converting to an Array or Collection 
	 * 
	 * When converting to a java.util.Collection, java.util.List or 
	 * java.util.Set the converter will return a live view over the 
	 * backing object that changes when the backing object changes. 
	 * The live view can be prevented by providing the copy() modifier.
	 * When converting to other collection types or arrays a copy is 
	 * always produced.
	 * In all cases the object returned is separate instance that can
	 * be owned by the client. Once the client modifies the returned object
	 * it will stop reflecting changes to the backing object
	 */
	public void testAggregateConversion()
	{
	}
	
	/**
	 * Section 148.4.3.2 - Converting to an Array or Collection 
	 * 
	 * [...]
	 * 
	 * When converting to other collection types or arrays a copy is 
	 * always produced. In all cases the object returned is separate 
	 * instance that can be owned by the client.
	 * 
	 * [...]
	 */
	public void testAggregateConversionToOtherArrayOrCollection()
	{
		int[] backingObject = new int[]{1,2,3};
		
		Converter converter = new StandardConverter();
		List<Long> converted = converter.convert(backingObject).to(
				new TypeReference<List<Long>>(){});
		
		Iterator<Long> iterator = converted.iterator();
		for(int index = 0; index< 3; index++)
		{
			assertEquals(backingObject[index],iterator.next().intValue());
		}
		assertFalse(iterator.hasNext());
		
		backingObject[2] = 5;
		iterator = converted.iterator();
		for(int index = 0; index< 3; index++)
		{
			switch(index)
			{
			case 0:
			case 1:
				assertTrue(backingObject[index] == iterator.next().intValue());
				break;
			case 2:
				assertFalse(backingObject[index] == iterator.next().intValue());
				break;
			}
		}		
	}
	
	/**
	 * Section 148.4.3.2 - Converting to an Array or Collection 
	 * 
	 * [...]
	 * 
	 * If the source is null an empty collection/array is produced
	 * 
	 * [...]
	 */
	public void testAggregateConversionFromNull()
	{		
		Converter converter = new StandardConverter();
		Set<Long> converted = converter.convert(null).to(
				new TypeReference<Set<Long>>(){});
		assertTrue(converted.isEmpty());
	}
	
	/**
	 * Section 148.4.3.2 - Converting to an Array or Collection 
	 * 
	 * [...]
	 * 
	 * If value is not a Collection, Array or Map-like structure
	 * (as described in Maps, Interfaces, Java Beans, DTOs and 
	 * Annotations [...]) a result with a single element is produced.
	 * The result is first converted into the desired target type, if
	 * the target type is known
	 * 
	 * [...]
	 */
	public void testAggregateConversionFromNotSpecial()
	{		
		ConverterBuilder cb = new StandardConverter().newConverterBuilder();

		cb.rule(new Rule<ConverterComplianceTest.MyInterfaceProvidingLong, Long>( 
		new Function<ConverterComplianceTest.MyInterfaceProvidingLong,Long>(){
			@Override
			public Long apply(MyInterfaceProvidingLong t) {
				
				return t.getLong();
			}
				}) {});
//		new Function<Long,ConverterComplianceTest.MyInterfaceProvidingLong>(){
//
//			@Override
//			public MyInterfaceProvidingLong apply(Long t) {
//				return null;
//			}
//			
//		});	
		
		ConverterComplianceTest.MyImplementation myImplementation = 
		    new ConverterComplianceTest.MyImplementation();
		
		List<Long> longListConverted = cb.build().convert(
			myImplementation).to(new TypeReference<ArrayList<Long>>(){});
		
		assertEquals(1,longListConverted.size());
		assertEquals(myImplementation.getLong(),
				longListConverted.get(0).longValue());
	}
	
	/**
	 * Section 148.4.5 - Priority 
	 * 
	 * In case multiple conversion rules match, the rule with the highest
	 * priority is used, where 1 is the highest overall priority
	 * 
	 * Priority							Rule
	 *    1							  annotation
	 *    2							     DTO
	 *    3							   entity.getProperties()
	 *    4							   interface (including collections and maps)
	 *    5							    scalar
	 */
	public void testAggregateConversionPriority()
	{

	}
	
	/**
	 * Section 148.4.5 - Priority 
	 * 
	 * [...]
	 * The priority rules defined in the above table can be overridden
	 * by using as(Class) to indicate as what class the source target object 
	 * should be treated.
	 */
	public void testAggregateConversionPriorityOverridden()
	{
	}
}
