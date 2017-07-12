package org.osgi.test.cases.converter.junit;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Pattern;

import org.osgi.util.converter.ConversionException;
import org.osgi.util.converter.Converter;
import org.osgi.util.converter.Converters;
import org.osgi.util.converter.TypeReference;

import junit.framework.TestCase;


/**
 * Chapter 148  - Converter
 */
public class ConverterScalarComplianceTest extends TestCase {

	/**
	 * Section 148.4  - Conversion
	 * 
	 * For scalars, conversions are only performed when the target type 
	 * is not compatible with the source type. For example, when requesting 
	 * a java.math.BigDecimal to java.lang.Number the big decimal is simply
	 * used as-is as this type is assignable to the requested target type
	 */
	public void testScalarAssignableTypeConversion()
	{
		BigDecimal toConvert = BigDecimal.valueOf(System.currentTimeMillis());
		Converter converter = Converters.standardConverter();
		
		Number converted = converter.convert(toConvert).to(
				Number.class);
		
		assertSame(toConvert, converted);
	}
	
	/**
	 * Section 148.4.2  - Scalars
	 * 
	 * Direct conversion between certain scalar types is supported. For
	 * all other scalar types conversion is done by converting to 
	 * String and then converting into the target type
	 * 
	 * to\from          Boolean 
	 * 
	 * boolean     v.booleanValue
	 * char		   v.booleanValue()?1:0  
	 * number      v.booleanValue()?1:0
	 * 
	 */
	public void testScalarConversionFromBoolean()
	{
		Converter converter = Converters.standardConverter();

		Boolean fstToBoConverted = new Boolean(true);
		
		boolean booleanConverted = converter.convert(fstToBoConverted).to(boolean.class);
		assertTrue(booleanConverted);
		
		char charConverted = converter.convert(fstToBoConverted).to(char.class);
		assertEquals((char)1,charConverted);
		
		int intConverted = converter.convert(fstToBoConverted).to(int.class);
		assertEquals(1,intConverted);
	}
	
	/**
	 * Section 148.4.2  - Scalars
	 * 
	 * Direct conversion between certain scalar types is supported. For
	 * all other scalar types conversion is done by converting to 
	 * String and then converting into the target type
	 * 
	 * to\from         Character
	 * 
	 * boolean     v.charValue()!=0         
	 * char		   v.charValue()   
	 * number      (number) v.charValue()
	 * 
	 */
	public void testScalarConversionFromCharacter()
	{
		Converter converter = Converters.standardConverter();

		Character fstToBoConverted = Character.valueOf('A');
		
		boolean booleanConverted = converter.convert(fstToBoConverted).to(boolean.class);
		assertTrue(booleanConverted);
		
		char charConverted = converter.convert(fstToBoConverted).to(char.class);
		assertEquals('A',charConverted);
		
		int intConverted = converter.convert(fstToBoConverted).to(int.class);
		assertEquals(65,intConverted);
	}

	/**
	 * Section 148.4.2  - Scalars
	 * 
	 * Direct conversion between certain scalar types is supported. For
	 * all other scalar types conversion is done by converting to 
	 * String and then converting into the target type
	 * 
	 * to\from          Number
	 * 
	 * boolean     v.numberValue()!=0    
	 * char		   (char) v.intValue()         
	 * number      v.numberValue()         
	 * 
	 */
	public void testScalarConversionFromNumber()
	{
		Converter converter = Converters.standardConverter();

		Number fstToBoConverted = new Double(65);
		
		boolean booleanConverted = converter.convert(fstToBoConverted).to(boolean.class);
		assertTrue(booleanConverted);
		
		char charConverted = converter.convert(fstToBoConverted).to(char.class);
		assertEquals('A',charConverted);
		
		int intConverted = converter.convert(fstToBoConverted).to(int.class);
		assertEquals(65,intConverted);
	}

	/**
	 * Section 148.4.2  - Scalars
	 * 
	 * Direct conversion between certain scalar types is supported. For
	 * all other scalar types conversion is done by converting to 
	 * String and then converting into the target type
	 * 
	 * to\from          null
	 * 
	 * boolean         false
	 * char		         0   
	 * number            0
	 * 
	 */
	public void testScalarConversionFromNull()
	{
		Converter converter = Converters.standardConverter();
		
		boolean booleanConverted = converter.convert(null).to(boolean.class);
		assertFalse(booleanConverted);
		
		char charConverted = converter.convert(null).to(char.class);
		assertEquals((char) 0,charConverted);
		
		int intConverted = converter.convert(null).to(int.class);
		assertEquals(0,intConverted);
	}
	
	/**
	 * Section 148.4.2.2  - Conversion to String
	 * 
	 * Conversion to String is done by calling to String() on
	 * the object to be converted. In the case of a primitive type,
	 * the object is boxed first
	 */
	public void testScalarConversionToString()
	{
		boolean fstToBeConverted = true;
		Long sndToBeConverted = 125l;
		
		Converter converter = Converters.standardConverter();
		String stringConverted = converter.convert(fstToBeConverted).to(String.class);
		assertEquals("true", stringConverted);
		
		stringConverted = converter.convert(sndToBeConverted).to(String.class);
		assertEquals("125", stringConverted);
	}

	/**
	 * Section 148.4.2.3 - Conversion from String
	 * 
	 * Conversion from String is done by attempting to invoke the 
	 * following methods, in order : 
	 * 
	 *  1. static valueOf(String s)
	 *  2. public constructor taking a single String argument
	 */
	public void testScalarConversionFromString()
	{
		String fstToBeConverted = "myObject";		
		Converter converter = Converters.standardConverter();
		
		ConverterComplianceTest.MyObject myObjectConverted = 
			converter.convert(fstToBeConverted).to(
				ConverterComplianceTest.MyObject.class);
		assertEquals(fstToBeConverted, myObjectConverted.value);

		ConverterComplianceTest.MyOtherObject myOtherObjectConverted = 
			converter.convert(fstToBeConverted).to(
				ConverterComplianceTest.MyOtherObject.class);
		
		assertEquals(fstToBeConverted, myOtherObjectConverted.getValue());
	}

	/**
	 * Section 148.4.2.4 - Special cases converting from String
	 * 
	 * [...]
	 * Some scalars have special rules for converting from String 
	 * values :
	 * 
	 * Target								Method
	 * ------								--------
	 * boolean/Boolean						Boolean.parseBoolean(v)
	 * char/Character						v.length()>0?v.charAt(0):0
	 * number/Number						Number.parseNumber(v)
	 * java.time.Instant					Instant.parse(v)
	 * java.time.LocalDate					LocalDate.parse(v)
	 * java.time.LocalDateTime				LocalDateTime.parse(v)
	 * java.time.LocalTime					LocalTime.parse(v)
	 * java.time.OffsetTime					OffsetTime.parse(v)
	 * java.time.ZoneDateTime				ZoneDateTime.parse(v)
	 * java.util.Date						Date.from(Instant.parse(v))
	 * java.util.UUID						UUID.fromString(v)
	 * java.util.regex.Pattern				Pattern.compile(v)
	 */
	public void testScalarConversionFromStringSpecial()
	{
		//Are conversion potential exceptions thrown (NumberFormatException 
		//thrown while parsing a string which does not represent a numeric 
		//value for example) if not what is the expected result of the 
		//conversion depending on type ?
		Converter converter = Converters.standardConverter();
		
		String booleanToBeConverted = "true";
		
		boolean booleanConverted = converter.convert(booleanToBeConverted).to(boolean.class);
		assertTrue(booleanConverted);

		String charToBeConverted = "Z";
		
		char charConverted = converter.convert(charToBeConverted).to(char.class);
		assertEquals('Z',charConverted);
		
		String intToBeConverted = "4567";
		int intConverted = converter.convert(intToBeConverted).to(int.class);
		assertEquals(4567,intConverted);
		
		String uuidStr = "0-0-0-0-FF";		
		UUID uuid = UUID.fromString(uuidStr);

		UUID convertedUUID = converter.convert(uuidStr).to(UUID.class);
		assertEquals(0,uuid.compareTo(convertedUUID));
		
		String phoneNumber = "0456789899";
		String pattern = "[0-9]{10}";

		Pattern patternConverted = converter.convert(pattern).to(Pattern.class);
		assertTrue(patternConverted.matcher(phoneNumber).matches());
		
		/* Commented out as this is Java 8 only
		Instant instant = Instant.EPOCH;
		String epochStr = "1970-01-01T00:00:00Z";
		
		Instant instantConverted = converter.convert(epochStr).to(Instant.class);
		assertEquals(0,instant.compareTo(instantConverted));
		
		Date date =  Date.from(instant);
		Date dateConverted = converter.convert(epochStr).to(Date.class);
		assertEquals(0,date.compareTo(dateConverted));
		
		String localDateStr = "2013-11-24";
		LocalDate localDate = LocalDate.parse(localDateStr);
		LocalDate localDateConverted =  converter.convert(localDateStr).to(LocalDate.class);
		assertEquals(0,localDate.compareTo(localDateConverted));
		
		String localDateTimeStr = "2013-11-24T07:21:00";
		LocalDateTime localDateTime = LocalDateTime.parse(localDateTimeStr);
		LocalDateTime localDateTimeConverted =  converter.convert(
				localDateTimeStr).to(LocalDateTime.class);
		assertEquals(0,localDateTime.compareTo(localDateTimeConverted));
		
		String localTimeStr = "07:21:00";
		LocalTime localTime = LocalTime.parse(localTimeStr);
		LocalTime localTimeConverted =  converter.convert(
				localTimeStr).to(LocalTime.class);
		assertEquals(0,localTime.compareTo(localTimeConverted));

		String offsetTimeStr = "07:21:00+01:00";
		OffsetTime offsetTime = OffsetTime.parse(offsetTimeStr);
		OffsetTime offsetTimeConverted =  converter.convert(
				offsetTimeStr).to(OffsetTime.class);
		assertEquals(0,offsetTime.compareTo(offsetTimeConverted));

		String zonedDateTimeStr = "2013-11-24T07:21:00+01:00 Europe/Paris";
		ZonedDateTime zonedDateTime = ZonedDateTime.parse(zonedDateTimeStr);
		ZonedDateTime zonedDateTimeConverted =  converter.convert(
				zonedDateTimeStr).to(ZonedDateTime.class);
		assertEquals(0,zonedDateTime.compareTo(zonedDateTimeConverted));
		*/
	}
	
	/**
	 * Section 148.4.2.5 - Date and Calendar
	 * 
	 * A java.util.Date instance is converted to a long value by calling
	 * Date.getTime(). Converting a long into java.util.Date is done by
	 * calling new Date(long).
	 */
	public void testScalarConversionDateAndLong()
	{
		TimeZone tz = TimeZone.getTimeZone("UTC");
		
		Calendar calendar = Calendar.getInstance(tz);		
		Date date = calendar.getTime();
		long millisEpoch = date.getTime();

		Converter converter = Converters.standardConverter();
		Date dateConverted = converter.convert(millisEpoch).to(Date.class);
		assertEquals(0,date.compareTo(dateConverted));
		
		long longConverted = converter.convert(date).to(long.class);
		assertEquals(millisEpoch,longConverted);
	}

	/**
	 * Section 148.4.2.5 - Date and Calendar [...] Converting a Date to a String
	 * is done by converting to a Instant and then calling instant toString().
	 * This will produce a ISO-8601 UTC date/time string in the following
	 * format: 2011-12-03T10:15:30Z. Converting a String to a date is done by
	 * calling Date.from(Instant.parse(v)) which can convert this ISO-8601
	 * format back into a Date
	 */
	public void testScalarConversionDateAndString()
	{
		TimeZone tz = TimeZone.getTimeZone("UTC");
		
		Calendar calendar = Calendar.getInstance(tz);
		calendar.clear(Calendar.MILLISECOND);
		
		Date date = calendar.getTime();
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		df.setTimeZone(tz);
		
		String dateStr = df.format(date);

		Converter converter = Converters.standardConverter();
		Date dateConverted = converter.convert(dateStr).to(Date.class);
		assertEquals(0,date.compareTo(dateConverted));
		
		String stringConverted = converter.convert(date).to(String.class);
		assertEquals(dateStr,stringConverted);
	}

	/**
	 * Section 148.4.2.5 - Date and Calendar
	 * 
	 * [...] Conversions from Calendar objects are done by converting
	 * the Calendar to a Date via getTime() first, and then by converting
	 * the resulting Date to the target type. Conversions to a Calendar 
	 * object are done by converting the source to a Date object with 
	 * the desired time (always UTC) and then setting the time in the 
	 * Calendar object via setTime()
	 */
	public void testScalarConversionDateAndCalendar()
	{
		Calendar calendar = Calendar.getInstance(
				TimeZone.getTimeZone("UTC"));
		
		Date date = calendar.getTime();
		long millisEpoch = date.getTime();
		
		Converter converter = Converters.standardConverter();
		long longConverted = converter.convert(calendar).to(long.class);
		assertEquals(millisEpoch, longConverted);
		
		Calendar calendarConverted = converter.convert(millisEpoch).to(Calendar.class);
		assertEquals(0,calendar.compareTo(calendarConverted));
	}
	
	/**
	 * Section 148.4.2.6 - Enum
	 * 
	 * Conversions to Enum types are supported as follows :
	 * 
	 * Source								Method
	 * ------								--------
	 * Number							EnumType.values()[v.intValue()]
	 * String							EnumType.valueOf(v)
	 * 									If no matching enum value can be 
	 * 									found a case-insensitive lookup is
	 * 									done for a matching enum value
	 * 
	 * Primitives are boxed before conversion is done. Other source types
	 * are converted to String before converting to Enum
	 */
	public void testScalarConversionEnum()
	{
		Converter converter = Converters.standardConverter();
		// number
		Number fstToBeConverted = 4l;
		
		ConverterComplianceTest.Animal converted = 
			converter.convert(fstToBeConverted).to(
				ConverterComplianceTest.Animal.class);
		
		assertEquals(ConverterComplianceTest.Animal.FROG, 
				converted);		
		//string - case-insensitive
		String sndToBeConverted = "eagle";
		
		converted = converter.convert(sndToBeConverted).to(
		ConverterComplianceTest.Animal.class);

		assertEquals(ConverterComplianceTest.Animal.eAGLe, 
				converted);
		
		//primitive
		double thdToBeConverted = 1;
		converted = converter.convert(thdToBeConverted).to(
			ConverterComplianceTest.Animal.class);
		
		assertEquals(ConverterComplianceTest.Animal.CROCODILE, 
				converted);

		char fthToBeConverted = 0;
		converted = converter.convert(fthToBeConverted).to(
		    ConverterComplianceTest.Animal.class);
		
		assertEquals(ConverterComplianceTest.Animal.CAT, 
				converted);
	}
	
	/**
	 * Section 148.4.2.7 - Map.Entry
	 * 
	 * Conversions of Map.Entry<K,V> to a target scalar type is done
	 * by evaluating the compatibility of the target type with both 
	 * the key and the value in the entry and then using the best match.
	 * This is done in this order :
	 * 
	 * 1. if one of the key or value is the same type as the target type,
	 * then this is used. If both match, the key is used
	 * 
	 * [...]
	 */
	public void testScalarConversionMapEntryKeyOrValueSameType()
	{
		Converter converter = Converters.standardConverter();

		Long fstKey = 145l;		
		Character characterValue = Character.valueOf('z');
		
		Map<Long,Character> fstMap = new HashMap<Long,Character>();
		fstMap.put(fstKey, characterValue);
		
		Iterator<Map.Entry<Long,Character>> fstIterator = 
				fstMap.entrySet().iterator();
		
		Map.Entry<Long,Character> fstToBeConverted = fstIterator.next();
		Long longConverted = converter.convert(fstToBeConverted).to(Long.class);
		Character characterConverted = converter.convert(fstToBeConverted).to(Character.class);
		
		assertEquals(fstKey,longConverted);
		//Are they also supposed to be the same reference ?
		//assertSame(fstKey,longConverted);
		assertEquals(characterValue,characterConverted);
		//Are they also supposed to be the same reference ?
		//assertSame(characterValue,characterConverted);

		Long sndKey = 12l;
		Long longValue = 20l;
		
		Map<Long,Long> sndMap = new HashMap<Long,Long>();
		sndMap.put(sndKey, longValue);
		
		Iterator<Map.Entry<Long,Long>> sndIterator = 
				sndMap.entrySet().iterator();
		
		Map.Entry<Long,Long> sndToBeConverted = sndIterator.next();
		longConverted = converter.convert(sndToBeConverted).to(Long.class);
		assertEquals(sndKey,longConverted);
		//Are they also supposed to be the same reference ?
		//assertSame(sndKey , longConverted);
	}

	/**
	 * Section 148.4.2.7 - Map.Entry
	 * 
	 * Conversions of Map.Entry<K,V> to a target scalar type is done
	 * by evaluating the compatibility of the target type with both 
	 * the key and the value in the entry and then using the best match.
	 * This is done in this order :
	 * 
	 * [...]
	 * 
	 * 2. If one of the key or value type is assignable to the target type,
	 * then this is used. If both are assignable the most specific is used.
	 * If the key and value are both assignable and equal the key is used.
	 * 
	 * [...]
	 */
	 //what does 'the most specific' mean ? How is it defined ?

	public void testScalarConversionMapEntryKeyOrValueAssignableType()
	{
		Converter converter = Converters.standardConverter();

		Number fstKey = 145l;		
		Comparable<?> characterValue = Character.valueOf('z');
		
		Map<Number,Comparable<?>> fstMap = 
				new HashMap<Number,Comparable<?>>();
		
		fstMap.put(fstKey , characterValue);
		
		Iterator<Map.Entry<Number,Comparable<?>>> fstIterator = 
				fstMap.entrySet().iterator();
		
		Map.Entry<Number,Comparable<?>> fstToBeConverted = fstIterator.next();
		Integer intConverted = converter.convert(fstToBeConverted).to(Integer.class);
		Character characterConverted = converter.convert(fstToBeConverted).to(Character.class);
		
		//Number and Comparable are both assignable from Integer
		//they are not equal
		//how is defined the most specific ?
		//is the key expected here ?
		assertEquals(intConverted.intValue(), fstKey.intValue());
		
		assertEquals(characterValue, characterConverted);
		//Are they also supposed to be the same reference ?
		//assertSame(characterValue , characterConverted);

		Number sndKey = 20l;
		Number longValue = 8l;
		
		Map<Number,Number> sndMap = 
				new HashMap<Number,Number>();
		
		sndMap.put(sndKey , longValue);
		
		Iterator<Map.Entry<Number,Number>> sndIterator = 
				sndMap.entrySet().iterator();
		
		Map.Entry<Number,Number> sndToBeConverted = sndIterator.next();
		intConverted = converter.convert(sndToBeConverted).to(Integer.class);
		
		assertEquals(intConverted.intValue(), sndKey.intValue());		
	}

	/**
	 * Section 148.4.2.7 - Map.Entry
	 * 
	 * Conversions of Map.Entry<K,V> to a target scalar type is done
	 * by evaluating the compatibility of the target type with both 
	 * the key and the value in the entry and then using the best match.
	 * This is done in this order :
	 * 
	 * [...]
	 * 
	 * 3. If one of the key or value is of type String, this is used and
	 * converted to the target type. If both are of type String the key is
	 * used
	 * 
	 * [...]
	 * 
	 */
	public void testScalarConversionMapEntryKeyOrValueStringType()
	{
		Converter converter = Converters.standardConverter();

		Number fstKey = 0l;		
		String stringValue = "true";
		
		Map<Number,String> fstMap = 
				new HashMap<Number,String>();
		
		fstMap.put(fstKey , stringValue);
		
		Iterator<Map.Entry<Number,String>> fstIterator = 
				fstMap.entrySet().iterator();
		
		Map.Entry<Number,String> fstToBeConverted = fstIterator.next();		
		Boolean booleanConverted = converter.convert(fstToBeConverted).to(Boolean.class);

		assertEquals(true, booleanConverted.booleanValue());
		
		String sndKey = "true";
		stringValue = "false";
		
		Map<String,String> sndMap = 
				new HashMap<String,String>();
		
		sndMap.put(sndKey , stringValue);
		
		Iterator<Map.Entry<String,String>> sndIterator = 
				sndMap.entrySet().iterator();
		
		Map.Entry<String,String> sndToBeConverted = sndIterator.next();
		booleanConverted = converter.convert(sndToBeConverted).to(Boolean.class);

		assertEquals(true, booleanConverted.booleanValue());
	}

	/**
	 * Section 148.4.2.7 - Map.Entry
	 * 
	 * Conversions of Map.Entry<K,V> to a target scalar type is done
	 * by evaluating the compatibility of the target type with both 
	 * the key and the value in the entry and then using the best match.
	 * This is done in this order :
	 * 
	 * [...]
	 * 
	 * 4. If none of the above matches the key is converted into String and 
	 * this value is then converted to the target type.
	 * 
	 */
	public void testScalarConversionMapEntryKeyToString()
	{
		Converter converter = Converters.standardConverter();

		Number fstKey = 1l;		
		Number numberValue = 0l;
		
		Map<Number,Number> fstMap = 
				new HashMap<Number,Number>();
		
		fstMap.put(fstKey , numberValue);
		
		Iterator<Map.Entry<Number,Number>> fstIterator = 
				fstMap.entrySet().iterator();
		
		Map.Entry<Number,Number> fstToBeConverted = fstIterator.next();		
		Boolean booleanConverted = converter.convert(fstToBeConverted).to(Boolean.class);

		assertEquals(true, booleanConverted.booleanValue());
	}
	
	/**
	 * Section 148.4.2.7 - Map.Entry
	 * 
	 * [...]
	 * Conversion to Map.Entry from a scalar is not supported
	 */
	public void testScalarConversionMapEntryUnsuported()
	{
		Converter converter = Converters.standardConverter();
		String tobeConverted = "map entry to be converted";
		
		try{
			Map.Entry<String,String> converted = 
				converter.convert(tobeConverted).to(
						new TypeReference<Map.Entry<String,String>>(){});
		
			fail("ConversionException expected");
		} catch (ConversionException e)
		{}
	}
}
