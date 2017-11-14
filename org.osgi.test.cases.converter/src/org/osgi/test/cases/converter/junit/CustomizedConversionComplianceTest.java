package org.osgi.test.cases.converter.junit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.osgi.util.converter.Converter;
import org.osgi.util.converter.ConverterBuilder;
import org.osgi.util.converter.Converters;
import org.osgi.util.converter.TypeReference;
import org.osgi.util.converter.TypeRule;
import org.osgi.util.function.Function;

import junit.framework.TestCase;


public class CustomizedConversionComplianceTest extends TestCase {

	public static class MyBean {
		private Date	startDate;
		private boolean	enabled;

		// empty constructor
		public MyBean() {}

		public void setStartDate(Date startDate) {
			this.startDate = startDate;
		}

		public Date getStartDate() {
			return this.startDate;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public boolean getEnabled() {
			return this.enabled;
		}
	}

	/**
	 * 707.5 - Repeated or Deferred Conversions
	 * <p/>
	 * In certain situations the same conversion needs to be performed multiple
	 * times, on different source objects. Or maybe the conversion needs to be
	 * performed asynchronously as part of a async stream processing pipeline.
	 * For such cases the Converter can produce a Function, which will perform
	 * the conversion once applied. The function can be invoked multiple times
	 * with different source objects. The Converter can produce this function
	 * through the function() method, which provides an API similar to the
	 * convert(Object) method, with the difference that instead of returning the
	 * conversion, once to() is called, a Function that can perform the
	 * conversion on apply(T) is returned.
	 * <p/>
	 * The Function returned by the converter is thread safe and can be used
	 * concurrently or asynchronously in other threads.
	 * 
	 * @throws Exception
	 */
	public void testRepeatedOrDeferredConversion() throws Exception {
		Converter c = Converters.standardConverter();
		Function<Object,Integer> cf = c.function().defaultValue(999).to(
				Integer.class);
		int i1 = cf.apply("123");
		assertTrue(123 == i1);
		int i2 = cf.apply("");
		assertTrue(999 == i2);
	}

	/**
	 * 707.6 - Customizing converters
	 * <p/>
	 * The Standard Converter applies the conversion rules described in this
	 * specification. While this is useful for many applications, in some cases
	 * deviations from the specified rules may be necessary. This can be done by
	 * creating a customized converter. Customized converters are created based
	 * on an existing converter with additional rules specified that override
	 * the existing converter's behavior. A customized converter is created
	 * through a ConverterBuilder. Customized converters implement the converter
	 * interface and as such can be used to create further customized
	 * converters. Converters are immutable, once created they cannot be
	 * modified, so they can be freely shared without the risk of modification
	 * to the converter's behavior.
	 * <p/>
	 * For example converting a Date to a String may require a specific
	 * format.The default Date to String conversion produces a String in the
	 * format yyyy-MM-ddTHH:mm:ss.SSSZ. If we want to produce a String in the
	 * format yyMMddHHmmssZ instead a custom converter can be applied:
	 * <p/>
	 * Custom conversions are also applied to embedded conversions that are part
	 * of a map or other enclosing object
	 * <p/>
	 * A converter rule can return CANNOT_HANDLE to indicate that it cannot
	 * handle the conversion, in which case next applicable rule is handed the
	 * conversion. If none of the registered rules for the current converter can
	 * handle the conversion, the parent converter object is asked to convert
	 * the value. Since custom converters can be the basis for further custom
	 * converters, a chain of custom converters can be created where a custom
	 * converter rule can either decide to handle the conversion, or it can
	 * delegate back to the next converter in the chain by returning
	 * CANNOT_HANDLE if it wishes to do so.
	 */
	public void testCustomizedConversion()
	{
		final SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
		final AtomicInteger counter = new AtomicInteger(0);

		ConverterBuilder cb = Converters.standardConverter()
				.newConverterBuilder();

		cb.rule(new TypeRule<Date,String>(Date.class, String.class,
				new Function<Date,String>() {
					@Override
					public String apply(Date t) {
						return sdf.format(t);
					}
				}) {});

		cb.rule(new TypeRule<String,Date>(String.class, Date.class,
				new Function<String,Date>() {
					@Override
					public Date apply(String t) {
						try {
							System.out.println("PARSING "+t);
							return sdf.parse(t);
							
						} catch (ParseException e) {
							e.printStackTrace();
						}
						return null;
					}
				}) {});
		
		Converter c = cb.build();
		
		String stringToBeConverted = "131224072100";
		Date dateToBeConverted = new Date(Date.UTC(113, 11, 24, 6, 21, 0));

		String stringConverted = c.convert(dateToBeConverted).to(String.class);
		assertEquals(stringConverted,stringToBeConverted);		
	
		Date dateConverted = c.convert(stringToBeConverted).to(Date.class);
		assertEquals(dateToBeConverted,dateConverted);

		MyBean mb = new MyBean();
		mb.setStartDate(dateToBeConverted);
		mb.setEnabled(true);

		String booleanConverted = "true";
		
		Map<String, String> map = c.convert(mb).sourceAsBean().to(new TypeReference<Map<String,String>>(){});
		assertEquals(booleanConverted, map.get("enabled"));
		assertEquals(stringConverted, map.get("startDate"));
	}
}
