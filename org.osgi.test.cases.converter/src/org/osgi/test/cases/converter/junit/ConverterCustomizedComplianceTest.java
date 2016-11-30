package org.osgi.test.cases.converter.junit;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import org.osgi.util.converter.Converter;
import org.osgi.util.converter.ConverterBuilder;
import org.osgi.util.converter.StandardConverter;
import org.osgi.util.converter.TypeReference;

import junit.framework.TestCase;


/**
 * Chapter 148.5  - Customizing Converter
 */
public class ConverterCustomizedComplianceTest extends TestCase {

	/**
	 * 148.5  - Customizing Converter
	 * 
	 * [...] Converting a Date to a String may require a 
	 * specific format. The default Date to String conversion 
	 * produces a String in the format yyyy-MM-ddTHH:mm:ss:SSSZ.
	 * if we want to produce a String in the format yyMMddHHmmssZ
	 * instead a custom converter can be applied
	 */
	public void testCustomizedConversion()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmssZ");
		ConverterBuilder cb = new StandardConverter().newConverterBuilder();

		cb.rule(Date.class,String.class, v->sdf.format(v), v -> sdf.parse(v));
		Converter c = cb.build();
		
		String stringToBeConverted = "131124072100+0100";
		Date dateToBeConverted = new Date(Date.UTC(2013, 10, 24, 6, 21, 0));

		Date dateConverted = c.convert(stringToBeConverted).to(Date.class);
		assertEquals(0,dateToBeConverted.compareTo(dateConverted));

		String stringConverted = c.convert(dateToBeConverted).to(String.class);
		assertEquals(stringConverted,stringToBeConverted);		
	}
	
	/**
	 * 148.5  - Customizing Converter
	 * 
	 * Custom conversion are also applied to embedded conversions 
	 * that are part of a map or other enclosing object
	 */
	public void testCustomizedEmbeddedConversion()
	{
		ConverterComplianceTest.MyBean mb = new ConverterComplianceTest.MyBean();
		mb.setStartDate(new Date(Date.UTC(2013, 10, 24, 6, 21, 0)));
		mb.setEnabled(true);

		SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmssZ");
		ConverterBuilder cb = new StandardConverter().newConverterBuilder();
		
		cb.rule(Date.class,String.class, v->sdf.format(v), v -> sdf.parse(v));
		Converter c = cb.build();

		String dateConverted = "131124072100+0100";
		String booleanConverted = "true";
		
		Map<String, String> map = c.convert(mb).to(new TypeReference<Map<String,String>>(){});
		assertEquals(booleanConverted, map.get("enabled"));
		assertEquals(dateConverted, map.get("startDate"));
	}
	
	/**
	 * 148.5  - Customizing Converter
	 * 
	 * Rules will only be invoked when actual object needs to be converted, 
	 * null values are never handed to converter rules. A converter rule can
	 * return null however to indicate that it cannot handle the conversion, 
	 * in which case the parent converter object is asked to convert the value.
	 * Since custom converters can be the basis for further custom converters,
	 * a chain of custom converters can be created where the outermost converter
	 * is given the opportunity to convert an object first, but it can delegate
	 * back to the next converter in the hierarchy by returning null if it wishes
	 * to do so
	 */
	//how does a converter express its wishes ?
	public void testCustomizedRuleChainConversion()
	{
		ConverterBuilder cb = new StandardConverter().newConverterBuilder();
		
		cb.rule(ConverterComplianceTest.MyInterfaceProvidingTwoInts.class, 
		Long.class, v -> {return ((long)v.getFirstInt() << 32)
		| ((long)v.getSecondInt() & 0xFFFFFFFFL);}, v-> { return null;});
		
		//delegate to the MyInterfaceProvidingTwoInts interface
		cb.rule(ConverterComplianceTest.MyAbstractImplementation.class,
		Long.class, v -> {return null;}, v-> { return null;});
		
		//how can I chain rules to specify the fact that a 
		//conversion from MyAbstractImplementation type to a 
		//Date one has to pass by the step of the long conversion
		
		//cb.rule(MyAbstractImplementation.class, Date.class, 
		//		v -> {return null;},
		//		v-> { return null;});
		
		Date date = Date.from(Instant.parse("2013-11-24T07:21:00"));
		
		ConverterComplianceTest.MyImplementation myImplementation = 
			new ConverterComplianceTest.MyImplementation();		
		Date  dateConverted = cb.build().convert(myImplementation).to(Date.class);		
		assertEquals(0,date.compareTo(dateConverted));
	}
	
	/**
	 * 148.5.1  - Abstract converter rule
	 * 
	 * When Specifying the types for the converter rules, 
	 * abstract or supertypes can be used. When the converter
	 * looks for a customized converter rule, it first looks
	 * for a converter rule for the most specific type. If it
	 * cannot find a matching rule, it will look for matching
	 * converters for the supertypes of the provided objects.
	 * It will continue to go up the type hierarchy until it 
	 * reaches java.lang.Object. Therefore converter rules that 
	 * convert from or to Object can be used as catchcall or
	 * default rules that are invoked if no specific rule is 
	 * available
	 */
	//How is the most specific type defined ?
	public void testCustomizedAbstractRuleConversion()
	{
		ConverterBuilder cb = new StandardConverter().newConverterBuilder();

		cb.rule(Object.class, Long.class, v -> {return (long)v.hashCode();}, 
				v-> { return null;});	
		cb.rule(ConverterComplianceTest.MyInterfaceProvidingLong.class, 
		Long.class, v -> {return v.getLong();},  v-> { return null;});	
		
		cb.rule(ConverterComplianceTest.MyInterfaceProvidingTwoInts.class, 
		Long.class, v -> {return ((long)v.getFirstInt() << 32)
		| ((long)v.getSecondInt() & 0xFFFFFFFFL);}, v-> { return null;});
		
		ConverterComplianceTest.MyImplementation myImplementation = 
		    new ConverterComplianceTest.MyImplementation();
		
		Long longConverted = cb.build().convert(myImplementation).to(Long.class);
		
		//which one is expected ?
		assertTrue(((long)myImplementation.hashCode()) == longConverted);
		assertTrue(((long)((myImplementation.getFirstInt() << 16) 
		| myImplementation.getSecondInt())) == longConverted);
		assertTrue(myImplementation.getLong() == longConverted);
	}
}
