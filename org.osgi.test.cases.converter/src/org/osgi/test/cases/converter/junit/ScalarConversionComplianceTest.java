
package org.osgi.test.cases.converter.junit;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
//import java.time.Duration;
//import java.time.Instant;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.time.MonthDay;
//import java.time.OffsetDateTime;
//import java.time.OffsetTime;
//import java.time.Year;
//import java.time.YearMonth;
//import java.time.ZonedDateTime;
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
 * 707 Converter specification
 */
public class ScalarConversionComplianceTest extends TestCase {

	/**
	 * Section 707.4.2 - Scalars
	 * <p/>
	 * 707.4.2.1 - Direct conversion between scalars
	 * <p/>
	 * Direct conversion between the following scalars types is supported:
	 * <p/>
	 * <table>
	 * <tr>
	 * <th>to\from</th>
	 * <th>Boolean</th>
	 * </tr>
	 * <tr>
	 * <td>boolean</td>
	 * <td>v.booleanValue</td>
	 * </tr>
	 * <tr>
	 * <td>char</td>
	 * <td>v.booleanValue()?1:0</td>
	 * </tr>
	 * <tr>
	 * <td>number</td>
	 * <td>v.booleanValue()?1:0</td>
	 * </tr>
	 * </table>
	 */
	public void testScalarConversionFromBoolean() {
		Converter converter = Converters.standardConverter();

		Boolean fstToBoConverted = Boolean.valueOf(true);

		boolean booleanConverted = converter.convert(fstToBoConverted)
				.to(boolean.class);
		assertTrue(booleanConverted);

		char charConverted = converter.convert(fstToBoConverted).to(char.class);
		assertEquals((char) 1, charConverted);

		int intConverted = converter.convert(fstToBoConverted).to(int.class);
		assertEquals(1, intConverted);

		fstToBoConverted = Boolean.valueOf(false);

		booleanConverted = converter.convert(fstToBoConverted)
				.to(boolean.class);
		assertFalse(booleanConverted);

		charConverted = converter.convert(fstToBoConverted).to(char.class);
		assertEquals((char) 0, charConverted);

		intConverted = converter.convert(fstToBoConverted).to(int.class);
		assertEquals(0, intConverted);
	}

	/**
	 * Section 707.4.2 - Scalars
	 * <p/>
	 * 707.4.2.1 - Direct conversion between scalars
	 * <p/>
	 * Direct conversion between the following scalars types is supported:
	 * <p/>
	 * <table>
	 * <tr>
	 * <th>to\from</th>
	 * <th>Character</th>
	 * </tr>
	 * <tr>
	 * <td>boolean</td>
	 * <td>v.charValue!=0</td>
	 * </tr>
	 * <tr>
	 * <td>char</td>
	 * <td>v.charValue</td>
	 * </tr>
	 * <tr>
	 * <td>number</td>
	 * <td>(number)v.charValue()</td>
	 * </tr>
	 * </table>
	 */
	public void testScalarConversionFromCharacter() {
		Converter converter = Converters.standardConverter();

		Character fstToBoConverted = Character.valueOf('A');

		boolean booleanConverted = converter.convert(fstToBoConverted)
				.to(boolean.class);
		assertTrue(booleanConverted);

		char charConverted = converter.convert(fstToBoConverted).to(char.class);
		assertEquals('A', charConverted);

		int intConverted = converter.convert(fstToBoConverted).to(int.class);
		assertEquals(65, intConverted);

		fstToBoConverted = Character.valueOf((char) 0);

		booleanConverted = converter.convert(fstToBoConverted)
				.to(boolean.class);
		assertFalse(booleanConverted);

		charConverted = converter.convert(fstToBoConverted).to(char.class);
		assertEquals(((char) 0), charConverted);

		intConverted = converter.convert(fstToBoConverted).to(int.class);
		assertEquals(0, intConverted);
	}
	
	/**
	 * Section 707.4.2 - Scalars
	 * <p/>
	 * 707.4.2.1 - Direct conversion between scalars
	 * <p/>
	 * Direct conversion between the following scalars types is supported:
	 * <p/>
	 * <table>
	 * <tr>
	 * <th>to\from</th>
	 * <th>Number</th>
	 * </tr>
	 * <tr>
	 * <td>boolean</td>
	 * <td>v.numberValue!=0</td>
	 * </tr>
	 * <tr>
	 * <td>char</td>
	 * <td>(char)v.intValue()</td>
	 * </tr>
	 * <tr>
	 * <td>number</td>
	 * <td>v.numberValue()</td>
	 * </tr>
	 * </table>
	 */
	public void testScalarConversionFromNumber() {
		Converter converter = Converters.standardConverter();

		Number fstToBoConverted = Double.valueOf(65);

		boolean booleanConverted = converter.convert(fstToBoConverted)
				.to(boolean.class);
		assertTrue(booleanConverted);

		char charConverted = converter.convert(fstToBoConverted).to(char.class);
		assertEquals('A', charConverted);

		int intConverted = converter.convert(fstToBoConverted).to(int.class);
		assertEquals(65, intConverted);

		fstToBoConverted = Integer.valueOf(0);

		booleanConverted = converter.convert(fstToBoConverted)
				.to(boolean.class);
		assertFalse(booleanConverted);

		charConverted = converter.convert(fstToBoConverted).to(char.class);
		assertEquals(((char) 0), charConverted);

		intConverted = converter.convert(fstToBoConverted).to(int.class);
		assertEquals(0, intConverted);
	}

	/**
	 * Section 707.4.2 - Scalars
	 * <p/>
	 * 707.4.2.1 - Direct conversion between scalars
	 * <p/>
	 * Direct conversion between the following scalars types is supported:
	 * <p/>
	 * <table>
	 * <tr>
	 * <th>to\from</th>
	 * <th>null</th>
	 * </tr>
	 * <tr>
	 * <td>boolean</td>
	 * <td>false</td>
	 * </tr>
	 * <tr>
	 * <td>char</td>
	 * <td>(char)0</td>
	 * </tr>
	 * <tr>
	 * <td>number</td>
	 * <td>0</td>
	 * </tr>
	 * </table>
	 */
	public void testScalarConversionFromNull() {
		Converter converter = Converters.standardConverter();

		boolean booleanConverted = converter.convert(null).to(boolean.class);
		assertFalse(booleanConverted);

		char charConverted = converter.convert(null).to(char.class);
		assertEquals((char) 0, charConverted);

		int intConverted = converter.convert(null).to(int.class);
		assertEquals(0, intConverted);
	}

	/**
	 * Section 707.4.2 - Scalars
	 * <p/>
	 * 707.4.2.2 - Conversion to String
	 * <p/>
	 * Conversion of scalars to String is done by calling to String() on the
	 * object to be converted. In the case of a primitive type, the object is
	 * boxed first.
	 * <p/>
	 * A null object results in a null String value
	 */
	public void testScalarConversionToString() {

		boolean fstToBeConverted = true;
		Long sndToBeConverted = 125l;
		double thdToBeConverted = 5d;
		Converter converter = Converters.standardConverter();
		String stringConverted = converter.convert(fstToBeConverted)
				.to(String.class);
		assertEquals(((Boolean) fstToBeConverted).toString(), stringConverted);

		stringConverted = converter.convert(sndToBeConverted).to(String.class);
		assertEquals(sndToBeConverted.toString(), stringConverted);

		stringConverted = converter.convert(thdToBeConverted).to(String.class);
		assertEquals(((Double) thdToBeConverted).toString(), stringConverted);

		String nullConverted = converter.convert(null).to(String.class);
		assertTrue(nullConverted == null);
	}

	/**
	 * Section 707.4.2 - Scalars
	 * <p/>
	 * 707.4.2.3 - Conversion from String
	 * <p/>
	 * Conversion from String is done by attempting to invoke the following
	 * methods, in order:
	 * <p/>
	 * 1. public static valueOf(String s)
	 * <p/>
	 * 2. public constructor taking a single String argument
	 * <p/>
	 */
	public void testScalarConversionFromString() {
		String fstToBeConverted = "myObject";
		String sndToBeConverted = "9";
		String thdToBeConverted = "True";
		Converter converter = Converters.standardConverter();
		ConversionComplianceTest.MyObject myObjectConverted = converter
				.convert(fstToBeConverted)
				.to(ConversionComplianceTest.MyObject.class);
		assertEquals(fstToBeConverted, myObjectConverted.value);
		ConversionComplianceTest.MyOtherObject myOtherObjectConverted = converter
				.convert(fstToBeConverted)
				.to(ConversionComplianceTest.MyOtherObject.class);
		assertEquals(fstToBeConverted, myOtherObjectConverted.getValue());

		int integerConverted = converter.convert(sndToBeConverted)
				.to(int.class);
		assertEquals(Integer.valueOf(sndToBeConverted).intValue(),
				integerConverted);

		boolean booleanConverted = converter.convert(thdToBeConverted)
				.to(boolean.class);
		assertEquals(Boolean.valueOf(thdToBeConverted).booleanValue(),
				booleanConverted);
	}

	/**
	 * Section 707.4.2 - Scalars
	 * <p/>
	 * 707.4.2.3 - Conversion from String
	 * <p/>
	 * [...] Some scalars have special rules for converting from String values.
	 * See below
	 * <p/>
	 * <table>
	 * <tr>
	 * <th>Target</th>
	 * <th>Method</th>
	 * </tr>
	 * <tr>
	 * <td>char / Character</td>
	 * <td>v.length() &gt; 0 ? v.charAt(0) : 0</td>
	 * </tr>
	 * <tr>
	 * <td>java.time.Duration</td>
	 * <td>Duration.parse(v)</td>
	 * </tr>
	 * <tr>
	 * <td>java.time.Instant</td>
	 * <td>Instant.parse(v)</td>
	 * </tr>
	 * <tr>
	 * <td>java.time.LocalDate</td>
	 * <td>LocalDate.parse(v)</td>
	 * </tr>
	 * <tr>
	 * <td>java.time.LocalDateTime</td>
	 * <td>LocalDateTime.parse(v)</td>
	 * </tr>
	 * <tr>
	 * <td>java.time.LocalTime</td>
	 * <td>LocalTime.parse(v)</td>
	 * </tr>
	 * <tr>
	 * <td>java.time.MonthDay</td>
	 * <td>MonthDay.parse(v)</td>
	 * </tr>
	 * <tr>
	 * <td>java.time.OffsetTime</td>
	 * <td>OffsetTime.parse(v)</td>
	 * </tr>
	 * <tr>
	 * <td>java.time.OffsetDateTime</td>
	 * <td>OffsetDateTime.parse(v)</td>
	 * </tr>
	 * <tr>
	 * <td>java.time.Year</td>
	 * <td>Year.parse(v)</td>
	 * </tr>
	 * <tr>
	 * <td>java.time.YearMonth</td>
	 * <td>YearMonth.parse(v)</td>
	 * </tr>
	 * <tr>
	 * <td>java.time.ZonedDateTime</td>
	 * <td>ZonedDateTime.parse(v)</td>
	 * </tr>
	 * <tr>
	 * <td>java.util.UUID</td>
	 * <td>UUID.fromString(v)</td>
	 * </tr>
	 * <tr>
	 * <td>java.util.regex.Pattern</td>
	 * <td>Pattern.compile(v)</td>
	 * </tr>
	 * </table>
	 */
	public void testScalarConversionFromStringSpecial() {
		Converter converter = Converters.standardConverter();

		String emptyString = "";
		char c = converter.convert(emptyString).to(char.class);
		assertEquals(((char) 0), c);

		String string = "AEERECTF";
		c = converter.convert(string).to(char.class);
		assertEquals('A', c);

		// java 1.8 data structures
//		String duration = "PT10H";
//		Duration d = converter.convert(duration).to(Duration.class);
//		assertEquals(Duration.parse("PT10H"), d);
//
//		String instant = "2013-05-30T23:38:23.085Z";
//		Instant i = converter.convert(instant).to(Instant.class);
//		assertEquals(Instant.parse("2013-05-30T23:38:23.085Z"), i);
//
//		String localDateStr = "2013-11-24";
//		LocalDate localDate = LocalDate.parse(localDateStr);
//		LocalDate localDateConverted = converter.convert(localDateStr)
//				.to(LocalDate.class);
//		assertEquals(0, localDate.compareTo(localDateConverted));
//
//		String localDateTimeStr = "2013-11-24T07:21:00";
//		LocalDateTime localDateTime = converter.convert(localDateTimeStr)
//				.to(LocalDateTime.class);
//		assertEquals(LocalDateTime.parse(localDateTimeStr), localDateTime);
//
//		String localTimeStr = "07:21:00";
//		LocalTime localTime = converter.convert(localTimeStr)
//				.to(LocalTime.class);
//		assertEquals(LocalTime.parse(localTimeStr), localTime);
//
//		String monthDay = "--11-24";
//		MonthDay md = converter.convert(monthDay).to(MonthDay.class);
//		assertEquals(MonthDay.parse(monthDay), md);
//
//		String offsetTimeStr = "07:21:00+01:00";
//		OffsetTime offsetTime = converter.convert(offsetTimeStr)
//				.to(OffsetTime.class);
//		assertEquals(OffsetTime.parse(offsetTimeStr), offsetTime);
//
//		String offsetDateTimeStr = "2007-12-03T10:15:30+01:00";
//		OffsetDateTime offsetDateTime = converter.convert(offsetDateTimeStr)
//				.to(OffsetDateTime.class);
//		assertEquals(OffsetDateTime.parse(offsetTimeStr), offsetDateTime);
//
//		String year = "2017";
//		Year y = converter.convert(year).to(Year.class);
//		assertEquals(Year.parse(year), y);
//
//		String yearMonth = "2017-11";
//		YearMonth ym = converter.convert(yearMonth).to(YearMonth.class);
//		assertEquals(YearMonth.parse(yearMonth), y);
//
//		String zonedDateTimeStr = "2013-11-24T07:21:00+01:00 Europe/Paris";
//		ZonedDateTime zonedDateTime = converter.convert(zonedDateTimeStr)
//				.to(ZonedDateTime.class);
//		assertEquals(ZonedDateTime.parse(zonedDateTimeStr), zonedDateTime);

		String uuidStr = "0-0-0-0-FF";
		UUID convertedUUID = converter.convert(uuidStr).to(UUID.class);
		assertEquals(UUID.fromString(uuidStr), convertedUUID);

		String phoneNumber = "0456789899";
		String pattern = "[0-9]{10}";
		Pattern patternConverted = converter.convert(pattern).to(Pattern.class);
		assertTrue(patternConverted.matcher(phoneNumber).matches());
	}

	/**
	 * Section 707.4.2 - Scalars
	 * <p/>
	 * 707.4.2.4 - Date and Calendar
	 * <p/>
	 * A java.util.Date instance is converted to a long value by calling
	 * Date.getTime(). Converting a long into a java.util.Date is done by
	 * calling new Date(long). Converting a Date to a String will produce a
	 * ISO-8601 UTC date/time string in the following format:
	 * 2011-12-03T10:15:30Z. In Java 8 this can be done by calling
	 * Date.toInstant().toString(). Converting a String to a Date is done by
	 * parsing this ISO-8601 format back into a Date. In Java 8 this function is
	 * performed by calling Date.from(Instant.parse(v)).
	 * <p/>
	 * Conversions from Calendar objects are done by converting the Calendar to
	 * a Date via getTime() first, and then converting the resulting Date to the
	 * target type. Convertions to a Calendar object are done by converting the
	 * source to a Date object with the desired time (always in UTC) and then
	 * setting the time in the Calendar object via setTime().
	 * <p/>
	 */
	public void testScalarConversionDateAndCalendar() {
		Converter converter = Converters.standardConverter();
		TimeZone tz = TimeZone.getTimeZone("UTC");

		Calendar calendar = Calendar.getInstance(tz);
		Date date = calendar.getTime();
		long millisEpoch = date.getTime();

		Date dateConverted = converter.convert(millisEpoch).to(Date.class);
		assertEquals(date, dateConverted);

		long longConverted = converter.convert(date).to(long.class);
		assertEquals(millisEpoch, longConverted);

		calendar.clear(Calendar.MILLISECOND);
		date = calendar.getTime();

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		df.setTimeZone(tz);
		String dateStr = df.format(date); // date.toInstant().toString();

		dateConverted = converter.convert(dateStr).to(Date.class);
		assertEquals(date, dateConverted);

		String stringConverted = converter.convert(date).to(String.class);
		assertEquals(dateStr, stringConverted);

		millisEpoch = date.getTime();
		longConverted = converter.convert(calendar).to(long.class);

		assertEquals(millisEpoch, longConverted);
		Calendar calendarConverted = converter.convert(millisEpoch)
				.to(Calendar.class);
		assertEquals(calendar.getTimeInMillis(), calendarConverted.getTimeInMillis());
	}

	/**
	 * Section 707.4.2 - Scalars
	 * <p/>
	 * 707.4.2.5 - Enums
	 * <p/>
	 * Conversions to Enum types are supported as follows.
	 * <p/>
	 * <table>
	 * <tr>
	 * <th>Source</th>
	 * <th>Method</th>
	 * </tr>
	 * <tr>
	 * <td><em>Number</em></td>
	 * <td><em>EnumType</em> .values()[v.intValue()]</td>
	 * </tr>
	 * <tr>
	 * <td>String</td>
	 * <td><em>EnumType</em>.valueOf(v). If this does not produce a result a
	 * case-insensitive lookup is done for a matching enum value.</td>
	 * </tr>
	 * </table>
	 * Primitives are boxed before conversion is done. Other source types are
	 * converted to String before converting to Enum.
	 */
	public void testScalarConversionEnum() {
		Converter converter = Converters.standardConverter();
		Number fstToBeConverted = 4l;

		ConversionComplianceTest.Animal converted = converter
				.convert(fstToBeConverted)
				.to(ConversionComplianceTest.Animal.class);
		assertEquals(ConversionComplianceTest.Animal.FROG, converted);

		String sndToBeConverted = "eagle";
		converted = converter.convert(sndToBeConverted)
				.to(ConversionComplianceTest.Animal.class);
		assertEquals(ConversionComplianceTest.Animal.eAGLe, converted);

		double thdToBeConverted = 1;
		converted = converter.convert(thdToBeConverted)
				.to(ConversionComplianceTest.Animal.class);
		assertEquals(ConversionComplianceTest.Animal.CROCODILE,
				converted);

		ConversionComplianceTest.MyObject obj = new ConversionComplianceTest.MyObject(
				"CAT");
		converted = converter.convert(obj)
				.to(ConversionComplianceTest.Animal.class);
		assertEquals(ConversionComplianceTest.Animal.CAT, converted);
	}


	/**
	 * Section 707.4.2 - Scalars
	 * <p/>
	 * 707.4.2.6 - Map.Entry
	 * <p/>
	 * Conversion of Map.Entry&lt;K,V&gt; to a target scalar type is done by
	 * evaluating the compatibility of the target type with both the key and the
	 * value in the entry and then using the best match. This is done in the
	 * following order:
	 * <p/>
	 * <ol>
	 * <li>If one of the key or value is the same as the target type, then this
	 * is used. If both are the same, the key is used.</li>
	 * <li>If one of the key or value type is assignable to the target type,
	 * then this is used. If both are assignable the key is used.</li>
	 * <li>If one of the key or value is of type String, this is used and
	 * converted to the target type. If both are of type String the key is
	 * used.</li>
	 * <li>If none of the above matches the key is converted into a String and
	 * this value is then converted to the target type.</li>
	 * </ol>
	 * <p/>
	 * Conversion to Map.Entry from a scalar is not supported.
	 */
	public void testScalarConversionMapEntryKeyOrValueSameType() {
		Converter converter = Converters.standardConverter();

		Long fstKey = 145l;
		Character characterValue = Character.valueOf('z');

		Map<Long,Character> fstMap = new HashMap<Long,Character>();
		fstMap.put(fstKey, characterValue);

		Iterator<Map.Entry<Long,Character>> fstIterator = fstMap.entrySet()
				.iterator();

		Map.Entry<Long,Character> fstToBeConverted = fstIterator.next();
		Long longConverted = converter.convert(fstToBeConverted).to(Long.class);
		Character characterConverted = converter.convert(fstToBeConverted)
				.to(Character.class);

		assertEquals(fstKey, longConverted);
		assertEquals(characterValue, characterConverted);

		Long sndKey = 12l;
		Long longValue = 20l;

		Map<Long,Long> sndMap = new HashMap<Long,Long>();
		sndMap.put(sndKey, longValue);
		Iterator<Map.Entry<Long,Long>> sndIterator = sndMap.entrySet()
				.iterator();

		Map.Entry<Long,Long> sndToBeConverted = sndIterator.next();
		longConverted = converter.convert(sndToBeConverted).to(Long.class);
		assertEquals(sndKey, longConverted);

		fstKey = 0l;
		String stringValue = "true";

		Map<Number,String> fvMap = new HashMap<Number,String>();
		fvMap.put(fstKey, stringValue);

		Iterator<Map.Entry<Number,String>> fvIterator = fvMap.entrySet()
				.iterator();

		Map.Entry<Number,String> fvToBeConverted = fvIterator.next();
		Boolean booleanConverted = converter.convert(fvToBeConverted)
				.to(Boolean.class);

		assertTrue(booleanConverted.booleanValue());

		String sthKey = "true";
		stringValue = "false";

		Map<String,String> sthMap = new HashMap<String,String>();
		sthMap.put(sthKey, stringValue);

		Iterator<Map.Entry<String,String>> sthIterator = sthMap.entrySet()
				.iterator();

		Map.Entry<String,String> sthToBeConverted = sthIterator.next();
		booleanConverted = converter.convert(sthToBeConverted)
				.to(Boolean.class);

		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		df.setTimeZone(tz);	
		
		Calendar calendar = Calendar.getInstance(tz);
		String dateStr = df.format(calendar.getTime());
		
		sthMap = new HashMap<String,String>();
		sthMap.put(dateStr, "date");

		sthIterator = sthMap.entrySet().iterator();
		sthToBeConverted = sthIterator.next();
		Date date = converter.convert(sthToBeConverted).to(Date.class);
		assertEquals((date.getTime()/1000), (calendar.getTime().getTime()/1000));
		
		Number snKey = 1l;
		Number numberValue = 0l;

		Map<Number,Number> snMap = new HashMap<Number,Number>();
		snMap.put(snKey, numberValue);
		Iterator<Map.Entry<Number,Number>> snIterator = snMap.entrySet().iterator();
		Map.Entry<Number,Number> snToBeConverted = snIterator.next();
		booleanConverted = converter.convert(snToBeConverted).to(boolean.class);
		//number to String = "1", then parsed to identify boolean value = false
		assertFalse(booleanConverted.booleanValue());

		String tobeConverted = "map entry to be converted";

		try {
			converter.convert(tobeConverted)
					.to(new TypeReference<Map.Entry<String,String>>() {});
			fail("ConversionException expected");
		} catch (ConversionException e) {}
	}
}
