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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.osgi.test.cases.converter.junit.ConversionComplianceTest.ExtObject;
import org.osgi.test.cases.converter.junit.MapInterfaceJavaBeansDTOAndAnnotationConversionComplianceTest.MappingBean;
import org.osgi.util.converter.ConversionException;
import org.osgi.util.converter.Converter;
import org.osgi.util.converter.ConverterBuilder;
import org.osgi.util.converter.ConverterFunction;
import org.osgi.util.converter.Converters;
import org.osgi.util.converter.TypeReference;
import org.osgi.util.converter.TypeRule;
import org.osgi.util.function.Function;


public class CustomizedConversionComplianceTest {

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

	public static class MyErrorBean {
		private String	property;

		// empty constructor
		public MyErrorBean() {}

		public void setProperty(String property) {
			this.property = property;
		}

		public String getProperty() {
			return this.property;
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
	@Test
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
	 * 
	 */
	@Test
	public void testCustomizedConversion()
	{
		final SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

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
							return sdf.parse(t);
							
						} catch (ParseException e) {
							e.printStackTrace();
						}
						return null;
					}
				}) {});
		
		Converter c = cb.build();
		
		String stringToBeConverted = "131124072100";
		@SuppressWarnings("deprecation")
		Date dateToBeConverted = new Date(Date.UTC(113, 10, 24, 7, 21, 0));

		String stringConverted = c.convert(dateToBeConverted).to(String.class);
		assertEquals(stringToBeConverted, stringConverted);
	
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
		
	/**
	 * 707.6 - Customizing converters
	 * <p/>	 
	 * [...] 
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
	 * <p/>
	 * 
	 * 707.6.1 - Catch-all-rules
	 * <p/>
	 * It is also possible to register converter rules which are invoked for 
	 * every conversion with the rule(ConverterFunction) method. When multiple 
	 * rules are registered, they are evaluated in the order of registration, 
	 * until a rule indicates that it can handle a conversion. A rule can indicate
	 * that it cannot handle the conversion by returning the CANNOT_HANDLE constant. 
	 * Rules targeting specific types are evaluated before catch-all rules. 
	 */
	@Test
	public void testCustomizedChainConversion()
	{
		final SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		final String error = "MyErrorBean is not handled";
		ConverterBuilder converterBuilder = Converters.newConverterBuilder();
		Converter converter = converterBuilder.build();
		
		ConverterBuilder parentConverterBuilder = converter.newConverterBuilder();		
		parentConverterBuilder.rule(
				new TypeRule<MappingBean,Map<String,String>>(MappingBean.class,
						Map.class,
						new Function<MappingBean,Map<String,String>>() {
					@Override
							public Map<String,String> apply(MappingBean t) {
						
						Map<String,String> m = new HashMap<String,String>();
						m.put("bean", t.toString());
								m.put("prop1", t.getP());
						m.put("prop2", t.getProp2());
						m.put("prop3", t.getProp3());
						m.put("embbeded", t.getEmbedded().toString());
						return m;
					}
				}) {});
		Converter parentConverter = parentConverterBuilder.build();	
		
		ConverterBuilder chilConverterBuilder = parentConverter.newConverterBuilder();
		chilConverterBuilder.rule(new TypeRule<Date,String>(Date.class, String.class,
				new Function<Date,String>() {
					@Override
					public String apply(Date t) {
						return sdf.format(t);
					}
				}) {});
		chilConverterBuilder.rule(new TypeRule<String,Date>(String.class, Date.class,
				new Function<String,Date>() {
					@Override
					public Date apply(String t) {
						try {
							return sdf.parse(t);
							
						} catch (ParseException e) {
							e.printStackTrace();
						}
						return null;
					}
				}) {});
		chilConverterBuilder.rule(new ConverterFunction() {
			@Override
			public Object apply(Object obj, Type targetType) throws Exception{
				Class< ? > clazz = obj.getClass();
				if(MappingBean.class.isAssignableFrom(clazz)){
					System.out.println("Unhandled MappingBean");
				}
				if(MyErrorBean.class.isAssignableFrom(clazz)){
					System.out.println("MyErrorBean triggers error");
					throw new ConversionException(error);
				}
				return ConverterFunction.CANNOT_HANDLE;
			}
		});
		Converter childConverter = chilConverterBuilder.build();	
		String stringToBeConverted = "131124072100";
		@SuppressWarnings("deprecation")
		Date dateToBeConverted = new Date(Date.UTC(113, 10, 24, 7, 21, 0));

		String stringConverted = childConverter.convert(dateToBeConverted).to(String.class);
		assertEquals(stringConverted,stringToBeConverted);		
	
		Date dateConverted = childConverter.convert(stringToBeConverted).to(Date.class);
		assertEquals(dateToBeConverted,dateConverted);
		
		MappingBean embeddedBean = new MappingBean();
		embeddedBean.setEmbedded(new ExtObject());
		embeddedBean.setP("mappingBean_prop1");
		embeddedBean.setProp2("mappingBean_prop2");
		embeddedBean.setProp3("mappingBean_prop3");

		@SuppressWarnings("unchecked")
		Map<String,String> map = childConverter.convert(embeddedBean)
				.to(Map.class);
		assertEquals(embeddedBean.toString(), map.get("bean"));

		MyBean mb = new MyBean();
		mb.setStartDate(dateToBeConverted);
		mb.setEnabled(true);				
		map = childConverter.convert(mb).sourceAsBean().to(new TypeReference<Map<String,String>>(){});
		
		String booleanConverted = "true";
		assertEquals(stringConverted, map.get("startDate"));
		assertEquals(booleanConverted, map.get("enabled"));
		
		MyErrorBean errorBean = new MyErrorBean();
		errorBean.setProperty("simple_error");
		try{
			map = childConverter.convert(errorBean).sourceAsBean().to(new TypeReference<Map<String,String>>(){});
			fail("ConversionException expected");
		} catch(ConversionException e)
		{}
		
	}
	
	/**
	 * 707.7 - Conversion failures
	 * <p/>
	 * Not all conversions can be performed by the standard converter. It cannot convert 
	 * text such as 'lorem ipsum' cannot into a <code>long</code> value. Or the number pi 
	 * into a map. When a conversion fails, the converter will throw a 
	 * org.osgi.util.converter.ConversionException.
	 * <p/>   
	 * If meaningful conversions exist between types not supported by the standard converter,
	 *  a customized converter can be used. Some applications require different behaviour for 
	 *  error scenarios. For example they can use an empty value such as 0 or "" instead of
	 *   the exception, or they might require a different exception to be thrown. 
	 *   For these scenarios a custom error handler can be registered. The error handler is 
	 *   only invoked in cases where otherwise a <code>ConversionException</code> would be 
	 *   thrown. The error handler can return a different value instead or throw another exception.
	 *   <p/>
	 *   An error handler is registered by creating a custom converter and providing it with an error 
	 *   handler via the org.osgi.util.converter.ConverterBuilder.errorHandler(ConvertFunction) method.
	 *   <p/>
	 *   When multiple error handlers are registered for a given converter they are invoked in the order
	 *   in which they were registered until an error handler either throws an exception or returns a 
	 *   value other than org.osgi.util.converter.ConverterFunction.CANNOT_HANDLE
	 */
	@Test
	public void testErrorHandler()
	{
		final AtomicInteger countError = new AtomicInteger(0);	
		final String error = "handled error";
		final Map<String,String> FAILURE = new HashMap<String,String>();
		
		ConverterBuilder cb = Converters.newConverterBuilder();	
		cb.errorHandler(new ConverterFunction() {
			@Override
			public Object apply(Object obj, Type targetType) throws Exception
			{
				if(countError.get() == 0){
					return ConverterFunction.CANNOT_HANDLE;
				}
				Class< ? > clazz = null;
				try{
					clazz = (Class< ? >) ((ParameterizedType) targetType)
							.getRawType();
				} catch(ClassCastException e) {
					clazz = (Class< ? >) targetType;
				}				
				if(Map.class.isAssignableFrom(clazz))
				{
					FAILURE.put("error", error);
					return FAILURE;
				}
				return null;
			}
		});		
		cb.errorHandler(new ConverterFunction() {
			@Override
			public Object apply(Object obj, Type targetType) throws Exception
			{
				countError.incrementAndGet();
				@SuppressWarnings("unused")
				Class< ? > clazz = null;
				try{
					clazz = (Class< ? >) ((ParameterizedType) targetType)
							.getRawType();
				} catch(ClassCastException e) {
					clazz = (Class< ? >) targetType;
				}				
				throw new RuntimeException(error);
			}
		});		
		Converter c = cb.build();	
		String tobeconverted = "to be converted";
		try{
			@SuppressWarnings({
					"unchecked", "unused"
			})
			Map<Object,Object> m = c.convert(tobeconverted).to(Map.class);
		} catch(RuntimeException e){
			assertEquals(error, e.getMessage());
			@SuppressWarnings("unchecked")
			Map<Object,Object> m = c.convert(tobeconverted).to(Map.class);
			assertNotNull(m);
			assertEquals(error, m.get("error"));
		}
	}
}
