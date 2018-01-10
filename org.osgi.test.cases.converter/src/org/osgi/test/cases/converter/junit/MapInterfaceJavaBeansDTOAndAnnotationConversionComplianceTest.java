
package org.osgi.test.cases.converter.junit;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;

import org.osgi.dto.DTO;
import org.osgi.test.cases.converter.junit.ConversionComplianceTest.ExtObject;
import org.osgi.util.converter.ConversionException;
import org.osgi.util.converter.Converter;
import org.osgi.util.converter.Converters;
import org.osgi.util.converter.TypeReference;

import junit.framework.TestCase;


/**
 * 707 Converter specification
 */
public class MapInterfaceJavaBeansDTOAndAnnotationConversionComplianceTest
		extends TestCase {

	@Retention(RetentionPolicy.RUNTIME)
	public static @interface AnnotationInterface {
		String prop1();

		String prop2() default "value2";

		String prop3() default "value3";

		String prop4();
	}

	@AnnotationInterface(prop1 = "definedValue1", prop4 = "definedValue4")
	public static class AnnotatedMappingClass {
		public AnnotatedMappingClass() {}
	}

	public static interface MappingInterface {

		String prop1();

		String prop2();

		String prop2(String prop2);

		String prop3();

		String prop3(String prop3);

		String prop4();
	}

	public static interface OtherInterface {

		String prop5();
	}

	public static class MultiInterfaces
			implements OtherInterface, MappingInterface {

		private String	prop1	= "value1";
		private String	prop2	= "value2";
		private String	prop3	= "value3";
		private String	prop4	= "value4";
		private String	prop5	= "value5";

		// no empty constructor
		public MultiInterfaces(boolean b) {}

		@Override
		public String prop1() {
			return this.prop1;
		}

		@Override
		public String prop2() {
			return this.prop2;
		}

		@Override
		public String prop2(String prop2) {
			String old = this.prop2;
			this.prop2 = prop2;
			return old;
		}

		@Override
		public String prop3() {
			return this.prop3;
		}

		@Override
		public String prop3(String prop3) {
			String old = this.prop3;
			this.prop3 = prop3;
			return old;
		}

		@Override
		public String prop4() {
			return this.prop4;
		}

		@Override
		public String prop5() {
			return this.prop5;
		}
	}

	public static class TypeWithoutGetProperties extends MultiInterfaces {

		public TypeWithoutGetProperties(boolean b) {
			super(b);
		}
	}

	public static class TypeWithGetProperties extends MultiInterfaces {

		public TypeWithGetProperties(boolean b) {
			super(b);
		}

		public Dictionary getProperties() {
			Hashtable table = new Hashtable();
			table.put("prop1", prop1());
			table.put("prop2", prop2());
			table.put("prop3", prop3());
			table.put("prop4", prop4());
			table.put("prop5", prop5());
			return table;
		}
	}
	
	public static class MappingBean {
		private String			prop1;
		private String			prop2;
		private String			prop3;
		private ExtObject	embedded;

		// empty constructor
		public MappingBean() {}

		public void setProp1(String prop1) {
			this.prop1 = prop1;
		}

		public String getProp1() {
			return this.prop1;
		}

		public void setProp2(String prop2) {
			this.prop2 = prop2;
		}

		public String getProp2() {
			return this.prop2;
		}

		public void setProp3(String prop3) {
			this.prop3 = prop3;
		}

		public String getProp3() {
			return this.prop3;
		}

		public void setEmbedded(ExtObject embedded) {
			this.embedded = embedded;
		}

		public ExtObject getEmbedded() {
			return this.embedded;
		}
	}
	
	public static class DTOLike {

		public String	prop1;
		public String	prop2;

		DTOLike() {}
	}

	public static class NotDTOLike {

		public String	prop1;
		public String	prop2;
		public String	prop3;

		public NotDTOLike() {}

		public void generateProp3() {
			if (prop1 == null || prop2 == null) {
				return;
			}
			prop3 = prop1.concat(prop2);
		}
	}

	public static class WithStaticAndPrivateFieldsDTOLike {
		public static final String	STATIC_FIELD	= "STATIC_FIELD";

		private String				prop0			= "private";
		public String				prop1;
		public String				prop2;

		WithStaticAndPrivateFieldsDTOLike() {}
	}

	public static class KeyMappingDTOLike {
		public static final String	PREFIX_	= "org.osgi.util.converter.test.";

		public String				special$prop;								// "org.osgi.util.converter.test.specialprop";
		public String				special$$prop;								// "org.osgi.util.converter.test.special$prop";
		public String				special_prop;								// "org.osgi.util.converter.test.special.prop";
		public String				_specialprop;								// "org.osgi.util.converter.test..specialprop";
		public String				special__prop;								// "org.osgi.util.converter.test.special_prop";
		public String				special___prop;								// "org.osgi.util.converter.test.special_.prop";
		public String				special_$__prop;							// "org.osgi.util.converter.test.special._prop";
		public String				special_$_prop;								// "org.osgi.util.converter.test.special..prop";
		public String				special$_$prop;								// "org.osgi.util.converter.test.special-prop";
		public String				special$$_$prop;							// "org.osgi.util.converter.test.special$.prop";

		public KeyMappingDTOLike() {}
	}

	public static class KeyMappingBean {
		public static final String	PREFIX_	= "org.osgi.util.converter.test.";

		private String				special$prop;								// "org.osgi.util.converter.test.specialprop";
		private String				special$$prop;								// "org.osgi.util.converter.test.special$prop";
		private String				special_prop;								// "org.osgi.util.converter.test.special.prop";
		private String				_specialprop;								// "org.osgi.util.converter.test..specialprop";
		private String				special__prop;								// "org.osgi.util.converter.test.special_prop";
		private String				special___prop;								// "org.osgi.util.converter.test.special_.prop";
		private String				special_$__prop;							// "org.osgi.util.converter.test.special._prop";
		private String				special_$_prop;								// "org.osgi.util.converter.test.special..prop";
		private String				special$_$prop;								// "org.osgi.util.converter.test.special-prop";
		private String				special$$_$prop;							// "org.osgi.util.converter.test.special$.prop";

		public KeyMappingBean() {}

		public String getSpecial$prop() {
			return special$prop;
		}

		public void setSpecial$prop(String special$prop) {
			this.special$prop = special$prop;
		}

		public String getSpecial$$prop() {
			return special$$prop;
		}

		public void setSpecial$$prop(String special$$prop) {
			this.special$$prop = special$$prop;
		}

		public String getSpecial_prop() {
			return special_prop;
		}

		public void setSpecial_prop(String special_prop) {
			this.special_prop = special_prop;
		}

		public String get_specialprop() {
			return _specialprop;
		}

		public void set_specialprop(String _specialprop) {
			this._specialprop = _specialprop;
		}

		public String getSpecial__prop() {
			return special__prop;
		}

		public void setSpecial__prop(String special__prop) {
			this.special__prop = special__prop;
		}

		public String getSpecial___prop() {
			return special___prop;
		}

		public void setSpecial___prop(String special___prop) {
			this.special___prop = special___prop;
		}

		public String getSpecial_$__prop() {
			return special_$__prop;
		}

		public void setSpecial_$__prop(String special_$__prop) {
			this.special_$__prop = special_$__prop;
		}

		public String getSpecial_$_prop() {
			return special_$_prop;
		}

		public void setSpecial_$_prop(String special_$_prop) {
			this.special_$_prop = special_$_prop;
		}

		public String getSpecial$_$prop() {
			return special$_$prop;
		}

		public void setSpecial$_$prop(String special$_$prop) {
			this.special$_$prop = special$_$prop;
		}

		public String getSpecial$$_$prop() {
			return special$$_$prop;
		}

		public void setSpecial$$_$prop(String special$$_$prop) {
			this.special$$_$prop = special$$_$prop;
		}
	}

	@Retention(RetentionPolicy.RUNTIME)
	public static @interface KeyMappingAnnotation {
		public static final String PREFIX_ = "org.osgi.util.converter.test.";

		public String special$prop(); // "org.osgi.util.converter.test.specialprop";

		public String special$$prop();// "org.osgi.util.converter.test.special$prop";

		public String special_prop();// "org.osgi.util.converter.test.special.prop";

		public String _specialprop();// "org.osgi.util.converter.test..specialprop";

		public String special__prop();// "org.osgi.util.converter.test.special_prop";

		public String special___prop();// "org.osgi.util.converter.test.special_.prop";

		public String special_$__prop();// "org.osgi.util.converter.test.special._prop";

		public String special_$_prop();// "org.osgi.util.converter.test.special..prop";

		public String special$_$prop();// "org.osgi.util.converter.test.special-prop";

		public String special$$_$prop();// "org.osgi.util.converter.test.special$.prop";
	}

	@KeyMappingAnnotation(_specialprop = "org.osgi.util.converter.test..specialprop", special$$_$prop = "org.osgi.util.converter.test.special$.prop", special$$prop = "org.osgi.util.converter.test.special$prop", special$_$prop = "org.osgi.util.converter.test.special-prop", special$prop = "org.osgi.util.converter.test.specialprop", special_$__prop = "org.osgi.util.converter.test.special._prop", special_$_prop = "org.osgi.util.converter.test.special..prop", special___prop = "org.osgi.util.converter.test.special_.prop", special__prop = "org.osgi.util.converter.test.special_prop", special_prop = "org.osgi.util.converter.test.special.prop")
	public static class KeyMappingAnnotatedClass {
		public KeyMappingAnnotatedClass() {}
	}

	public static interface KeyMappingInterface {
		public static final String PREFIX_ = "org.osgi.util.converter.test.";

		public String special$prop(); // "org.osgi.util.converter.test.specialprop";

		public String special$$prop();// "org.osgi.util.converter.test.special$prop";

		public String special_prop();// "org.osgi.util.converter.test.special.prop";

		public String _specialprop();// "org.osgi.util.converter.test..specialprop";

		public String special__prop();// "org.osgi.util.converter.test.special_prop";

		public String special___prop();// "org.osgi.util.converter.test.special_.prop";

		public String special_$__prop();// "org.osgi.util.converter.test.special._prop";

		public String special_$_prop();// "org.osgi.util.converter.test.special..prop";

		public String special$_$prop();// "org.osgi.util.converter.test.special-prop";

		public String special$$_$prop();// "org.osgi.util.converter.test.special$.prop";
	}

	public static @interface SingleElementAnnotation {
		KeyMappingAnnotation value();
	}

	@SingleElementAnnotation(@KeyMappingAnnotation(_specialprop = "org.osgi.util.converter.test..specialprop", special$$_$prop = "org.osgi.util.converter.test.special$.prop", special$$prop = "org.osgi.util.converter.test.special$prop", special$_$prop = "org.osgi.util.converter.test.special-prop", special$prop = "org.osgi.util.converter.test.specialprop", special_$__prop = "org.osgi.util.converter.test.special._prop", special_$_prop = "org.osgi.util.converter.test.special..prop", special___prop = "org.osgi.util.converter.test.special_.prop", special__prop = "org.osgi.util.converter.test.special_prop", special_prop = "org.osgi.util.converter.test.special.prop"))
	public static class SingleElementAnnotatedClass {
		public SingleElementAnnotatedClass() {}
	}

	public static class MyDTO2 extends DTO {
		public static String		shouldBeIgnored	= "ignoreme";

		public List<Long>			longList;

		public Map<String,MyDTO3>	dtoMap;
	}

	public static class MyDTO3 extends DTO {
		public Set<Character> charSet;
	}

	public static class MyGenericDTOWithVariables<T> extends DTO {
		public Set<T>	set;
		public T		raw;
		public T[]		array;
	}

	public interface MyGenericInterface {
		public Set<Character> charSet();
	}

	public interface MyGenericInterfaceWithVariables<T> {
		public Set<T> set();
		public T raw();
		public T[] array();
	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface MyMarkerAnnotation {}

	@MyMarkerAnnotation
	public interface MarkedInterface {
		String foo();
	}

	/**
	 * Section 707.4.4 : Maps, Interfaces, Java Beans, DTOs and Annotations
	 * <p/>
	 * 707.4.4.1 - Converting from scalar
	 * <p/>
	 * Conversions from a scalar to a map-like type are not supported by the
	 * standard converter.
	 */
	public void testFromScalarConversion() {
		Converter converter = Converters.standardConverter();
		try {
			converter.convert("scalar").to(Map.class);
			fail("Scalar to map-like structure not supported");
		} catch (ConversionException e) {}
	}

	/**
	 * Section 707.4.4 : Maps, Interfaces, Java Beans, DTOs and Annotations
	 * <p/>
	 * 707.4.4.2- Converting to a scalar
	 * <p/>
	 * Conversions of a map-like structure to a scalar are done by iterating
	 * through the entries of the map and taking the first Map.Entry instance.
	 * Then this instance is converted into the target scalar type as described
	 * in the section called Map.Entry.
	 * <p/>
	 * An empty map results in a null scalar value.
	 */
	public void testToScalarConversion() {
		Converter converter = Converters.standardConverter();
		
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		df.setTimeZone(tz);	
		
		Calendar calendar = Calendar.getInstance(tz);	
		Date date = calendar.getTime();
		String dateStr = df.format(date);

		Map<String,String> map = new HashMap<String,String>();
		map.put(dateStr, "epoch");				
		
		date = converter.convert(map).to(Date.class);
		assertEquals((calendar.getTime().getTime()/1000), (date.getTime()/1000));
	}

	/**
	 * Section 707.4.4 : Maps, Interfaces, Java Beans, DTOs and Annotations
	 * <p/>
	 * 707.4.4.3- Converting to an Array or Collection
	 * <p/>
	 * A map-like structure is converted to an Array or Collection target type
	 * by creating an ordered collection of Map.Entry objects. Then this
	 * collection is converted to the target type as described in the section
	 * called Arrays and Collections and the section called Map.Entry.
	 * @throws ParseException 
	 */
	public void testToArrayOrCollectionConversion() throws ParseException {
				
		TimeZone tz = TimeZone.getTimeZone("UTC");			
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		df.setTimeZone(tz);
		
		Calendar calendar = Calendar.getInstance(tz);			
		long day = calendar.getTimeInMillis();
		
		long millisDay = 1000 * 3600 * 24;
		
		Map<String,String> map = new HashMap<String,String>();
		for (int i = 0; i < 4; i++) {
			Date d = new Date((day + (i * millisDay)));
			map.put(df.format(d), "day".concat(String.valueOf(i)));
		}
		Converter converter = Converters.standardConverter();
		List<Date> dates = converter.convert(map).to(new TypeReference<List<Date>>() {});
		
		assertEquals(map.size(), dates.size());

		Iterator<String> keys = map.keySet().iterator();
		while (keys.hasNext()) {
			if (!dates.contains(df.parse(keys.next()))) {
				fail();
			}
		}
	}

	/**
	 * Section 707.4.4 : Maps, Interfaces, Java Beans, DTOs and Annotations
	 * <p/>
	 * 707.4.4.4 - Converting to a map-like structure
	 * <p/>
	 * 707.4.4.4.1 - Converting to a Map
	 * <p/>
	 * When converting a map-like structure to a java.util.Map the converter
	 * will return a live view over the backing object that changes when the
	 * backing object changes. The live view can be prevented by providing the
	 * copy() modifier. When converting to other map types a copy is always
	 * produced. In all cases the object returned is a separate instance that
	 * can be owned by the client. When the client modifies the returned object
	 * a live view will stop reflecting changes to the backing object.
	 * <p/>
	 * </p>
	 * <table>
	 * <tr>
	 * <th>Target</th>
	 * <th>Method</th>
	 * </tr>
	 * <tr>
	 * <td><code>java.util.Map</code></td>
	 * <td>A map view over the backing object is created, changes to the backing
	 * object will be reflected in the map, unless the map is modified by the
	 * client.</td>
	 * </tr>
	 * <tr>
	 * <td>Other Map interface</td>
	 * <td>A mutable implementation is created. For example, if the target type
	 * is <code>ConcurrentNavigableMap</code> then the implementation can create
	 * a <code>ConcurrentSkipListMap</code>.</td>
	 * </tr>
	 * <tr>
	 * <td>Map concrete type</td>
	 * <td>A new instance is created by calling <code>Class.newInstance()</code>
	 * on the provided type. For example if the target type is
	 * <code>HashMap</code> then the converter creates a target object by
	 * calling <code>HashMap.class.newInstance()</code>. The converter may
	 * choose to use a call a well-known constructor to optimize the creation of
	 * the map.</td>
	 * </tr>
	 * </table>
	 * <p/>
	 * When converting from a map-like object to a Map or sub-type, each
	 * key-value pair in the source map is converted to desired types of the
	 * target map using the generic information if available. Map type
	 * information for the target type can be made available by using the
	 * to(TypeReference) or to(Type) methods. If no type information is
	 * available, key-value pairs are used in the map as-is
	 */
	public void testMapConversion() {
		Converter converter = Converters.standardConverter();
		DTOLike dtolike = new DTOLike();
		dtolike.prop1 = "value1";
		dtolike.prop2 = "value2";

		// with live view
		Map converted = converter.convert(dtolike).view().to(Map.class);
		assertNotNull(converted);
		assertEquals(2, converted.size());
		assertEquals(dtolike.prop1, converted.get("prop1"));
		assertEquals(dtolike.prop2, converted.get("prop2"));

		// reflect backing object changes
		dtolike.prop1 = "value1bis";
		assertEquals(dtolike.prop1, converted.get("prop1"));

		// do not reflect backing object changes
		converted.put("prop1", "value1ter");
		dtolike.prop1 = "value1frth";
		assertFalse(dtolike.prop1.equals(converted.get("prop1")));

		// without live view
		converted = converter.convert(dtolike).to(Map.class);
		assertNotNull(converted);
		assertEquals(2, converted.size());
		assertEquals(dtolike.prop1, converted.get("prop1"));
		assertEquals(dtolike.prop2, converted.get("prop2"));

		// do not reflect backing object changes
		dtolike.prop1 = "value1fve";
		assertFalse(dtolike.prop1.equals(converted.get("prop1")));

		// Other Map interface
		converted = converter.convert(dtolike).to(NavigableMap.class);
		assertNotNull(converted);
		assertTrue(NavigableMap.class.isAssignableFrom(converted.getClass()));

		// concrete type
		converted = converter.convert(dtolike).to(TreeMap.class);
		assertNotNull(converted);
		assertEquals(converted.getClass(), TreeMap.class);

		// handle generic type
		converted = converter.convert(dtolike)
				.to(new TypeReference<Map<Character,String>>() {});
		assertNotNull(converted);
		assertEquals(1, converted.size());
		assertTrue(converted.get('p').equals(dtolike.prop1)
				|| converted.get('p').equals(dtolike.prop2));
	}

	/**
	 * Section 707.4.4 : Maps, Interfaces, Java Beans, DTOs and Annotations
	 * <p/>
	 * 707.4.4.4 - Converting to a map-like structure
	 * <p/>
	 * 707.4.4.4.2 - Dictionary
	 * <p/>
	 * Converting between a map and a Dictionary is done by iterating over the
	 * source and inserting the key value pairs in the target, converting them
	 * to the requested target type, if known. As with other generic types,
	 * target type information for Dictionaries can be provided via a
	 * TypeReference.
	 */
	public void testDictionaryConversion() {
		Converter converter = Converters.standardConverter();
		DTOLike dtolike = new DTOLike();
		dtolike.prop1 = "value1";
		dtolike.prop2 = "value2";

		Dictionary converted = converter.convert(dtolike).to(Dictionary.class);
		assertNotNull(converted);
		assertEquals(2, converted.size());
		assertEquals(dtolike.prop1, converted.get("prop1"));
		assertEquals(dtolike.prop2, converted.get("prop2"));

		converted = converter.convert(dtolike)
				.to(new TypeReference<Dictionary<Character,Character>>() {});
		assertNotNull(converted);
		assertEquals(1, converted.size());
		assertEquals('v', converted.get('p'));
	}

	/**
	 * Section 707.4.4 : Maps, Interfaces, Java Beans, DTOs and Annotations
	 * <p/>
	 * 707.4.4.4 - Converting to a map-like structure
	 * <p/>
	 * 707.4.4.4.3 - Interface
	 * <p/>
	 * Converting a map-like structure into an interface can be a useful way to
	 * give a map of untyped data a typed API. The converter synthesizes an
	 * interface instance to represent the conversion.
	 * <p/>
	 * Note that converting to annotations provides similar functionality with
	 * the added benefit of being able to specify default values in the
	 * annotation code.
	 * <p/>
	 * <b>Converting to an Interface</b>
	 * <p/>
	 * When converting into an interface the converter will create a dynamic
	 * proxy to implement the interface. The name of the method returning the
	 * value should match the key of the map entry, taking into account the
	 * mapping rules specified in the section called Key Mapping. The key of the
	 * map may need to be converted into a String first.
	 * <p/>
	 * Conversion is done on demand: only when the method on the interface is
	 * actually invoked. This avoids conversion errors on methods for which the
	 * information is missing or cannot be converted, but which the caller does
	 * not require. Note that the converter will not copy the source map when
	 * converting to an interface allowing changes to the source map to be
	 * reflected live to the proxy. The proxy cannot cache the conversions.
	 * Interfaces can provide methods for default values by providing a
	 * single-argument method override in addition to the no-parameter method
	 * matching the key name. If the type of the default does not match the
	 * target type it is converted first.
	 * <p/>
	 * Default values are used when the key is not present in the map for the
	 * method.
	 * <p/>
	 * If a key is present with a null value, then null is taken as the value
	 * and converted to the target type. If no default is specified and a
	 * requested value is not present in the map, a ConversionException is
	 * thrown.
	 * <p/>
	 * <b>Converting from an Interface</b>
	 * <p/>
	 * An interface can also be the source of a conversion to another map-like
	 * type. The name of each method without parameters is taken as key, taking
	 * into account the section called Key Mapping. The method is invoked using
	 * reflection to produce the associated value.
	 * <p/>
	 * Whether a conversion source object is an interface is determined
	 * dynamically. When an object implements multiple interfaces by default the
	 * first interface from these that has no-parameter methods is taken as the
	 * source type. To select a different interface use the sourceAs(Class)
	 * modifier:
	 * <p/>
	 * <code>
	 * Map m = converter.convert(myMultiInterface).
	 * sourceAs(MyInterfaceB.class).to(Map.class);</code>
	 * <p/>
	 * If the source object also has a getProperties() [...] this method is used
	 * to obtain the map view by default. This behaviour can be overridden by
	 * using the sourceAs(Class) modifier.
	 */
	public void testInterfaceConversion() {

		DTOLike dtolike = new DTOLike();
		dtolike.prop1 = "value1";
		dtolike.prop2 = "value2";

		Converter converter = Converters.standardConverter();
		MappingInterface mappingInterface = converter.convert(dtolike)
				.to(MappingInterface.class);
		assertNotNull(mappingInterface);

		assertEquals(dtolike.prop1, mappingInterface.prop1());
		assertEquals(dtolike.prop2, mappingInterface.prop2());

		// report changes
		dtolike.prop2 = "value3";
		assertEquals(dtolike.prop2, mappingInterface.prop2());
		assertEquals(dtolike.prop2, mappingInterface.prop2("defaultValue"));

		// use default value
		assertEquals("defaultValue", mappingInterface.prop3("defaultValue"));

		// do not use default
		dtolike.prop2 = null;
		assertNull(mappingInterface.prop2(null));

		try {
			mappingInterface.prop4();
			fail("ConversionException expected : undefined in DTOLike and no default ");
		} catch (ConversionException e) {}

		TypeWithoutGetProperties multiInterface = new TypeWithoutGetProperties(
				false);

		// select the appropriate interface
		Map converted = converter.convert(multiInterface)
				.sourceAs(MappingInterface.class)
				.view()
				.to(Map.class);

		multiInterface.prop2("newValue2");

		assertNotNull(converted);
		assertEquals(4, converted.size());

		assertNull(converted.get("prop5"));

		assertEquals(multiInterface.prop1(), converted.get("prop1"));
		assertEquals(multiInterface.prop2(), converted.get("prop2"));
		assertEquals(multiInterface.prop3(), converted.get("prop3"));
		assertEquals(multiInterface.prop4(), converted.get("prop4"));

		converted.put("prop4", "newValue4");
		assertFalse(multiInterface.prop4().equals(converted.get("prop4")));

		// use the first implemented interface
		converted = converter.convert(multiInterface).view().to(Map.class);

		assertNotNull(converted);
		assertEquals(1, converted.size());
		assertNull(converted.get("prop1"));
		assertNull(converted.get("prop2"));
		assertNull(converted.get("prop3"));
		assertNull(converted.get("prop4"));
		assertEquals(multiInterface.prop5(), converted.get("prop5"));
	}

	/**
	 * Section 707.4.4 : Maps, Interfaces, Java Beans, DTOs and Annotations
	 * <p/>
	 * 707.4.4.4 - Converting to a map-like structure
	 * <p/>
	 * 707.4.4.4.4 - Annotation
	 * <p/>
	 * Conversion to and from annotations behaves similar to interface
	 * conversion with the added capability of specifying a default in the
	 * annotation definition.
	 * <p/>
	 * When converting to an annotation type, the converter will return an
	 * instance of the requested annotation class. As with interfaces, values
	 * are only obtained from the conversion source when the annotation method
	 * is actually called. If the requested value is not available, the default
	 * as specified in the annotation class is used. If no default is specified
	 * a ConversionException is thrown.
	 * <p/>
	 * Similar to interfaces, conversions to and from annotations also follow
	 * the Key Mapping section for annotation element names.
	 */
	public void testAnnotationConversion() {

		DTOLike dtolike = new DTOLike();
		dtolike.prop1 = "value1";
		dtolike.prop2 = "value2";

		Converter converter = Converters.standardConverter();
		AnnotationInterface annotationInterface = converter.convert(dtolike)
				.to(AnnotationInterface.class);
		assertNotNull(annotationInterface);

		assertEquals(dtolike.prop1, annotationInterface.prop1());
		assertEquals(dtolike.prop2, annotationInterface.prop2());

		// report changes
		dtolike.prop2 = "newValue2";
		assertEquals(dtolike.prop2, annotationInterface.prop2());

		// use default value
		assertEquals("value3", annotationInterface.prop3());

		// still use default
		dtolike.prop2 = null;
		assertEquals("value2", annotationInterface.prop2());

		try {
			annotationInterface.prop4();
			fail("ConversionException expected : undefined in DTOLike and no default ");
		} catch (ConversionException e) {}

		AnnotationInterface annotation = AnnotatedMappingClass.class
				.getAnnotation(AnnotationInterface.class);

		Map convertedMap = converter.convert(annotation).to(Map.class);
		assertNotNull(convertedMap);
		// detach from live view
		convertedMap.put("prop5", "value5");
		assertEquals(annotation.prop1(), convertedMap.get("prop1"));
		assertEquals(annotation.prop2(), convertedMap.get("prop2"));
		assertEquals(annotation.prop3(), convertedMap.get("prop3"));
		assertEquals(annotation.prop4(), convertedMap.get("prop4"));
	}

	/**
	 * Section 707.4.4 : Maps, Interfaces, Java Beans, DTOs and Annotations
	 * <p/>
	 * 707.4.4.4 - Converting to a map-like structure
	 * <p/>
	 * 707.4.4.4.5 - Java Beans
	 * <p/>
	 * Java Beans are concrete (non-abstract) classes that follow the Java Bean
	 * naming convention. They provide public getters and setters to access
	 * their properties and have a public no-parameter constructor. When
	 * converting from a Java Bean introspection is used to find the read
	 * accessors. A read accessor must have no arguments and a non-void return
	 * value. The method name must start with get followed by a capitalized
	 * property name, for example getSize() provides access to the property
	 * size. For boolean/Boolean properties a prefix of <code><i>is<i></code> is
	 * also permitted. Properties names follow the the section called Key
	 * Mapping.
	 * <p/>
	 * For the converter to consider an object as a Java Bean the sourceAsBean()
	 * or targetAsBean() modifier needs to be invoked, for example:
	 * <p/>
	 * <code>  Map m = converter.convert(myBean).sourceAsBean().to(Map.class);</code>
	 * <p/>
	 * When converting to a Java Bean, the bean is constructed eagerly. All
	 * available properties are set in the bean using the bean's write
	 * accessors, that is, public setters methods with a single argument. All
	 * methods of the bean class itself and its super classes are considered.
	 * When a property cannot be converted this will cause a
	 * <code>ConversionException</code>. If a property is missing in the source,
	 * the property will not be set in the bean.
	 * <p/>
	 * Note: access via indexed bean properties is not supported.
	 * <p/>
	 * Note: the getClass() method of the java.lang.Object class is not
	 * considered an accessor.
	 */
	public void testJavaBeanConversion() {

		ConversionComplianceTest.ExtObject embedded = new ConversionComplianceTest.ExtObject();

		MappingBean bean = new MappingBean();
		bean.setProp1("value1");
		bean.setProp2("value2");
		bean.setProp3("value3");
		bean.setEmbedded(embedded);

		Converter converter = Converters.standardConverter();
		try {
			converter.convert(bean)
					.to(new TypeReference<Map<String,String>>() {});
			fail("No rule if not declared as JavaBean");
		} catch (ConversionException e) {}

		Map converted = converter.convert(bean).sourceAsBean().to(
				new TypeReference<Map<String,String>>() {});
		assertNotNull(converted);
		assertEquals(4, converted.size());
		assertEquals("extended", converted.get("embedded"));

		try {
			converter.convert(converted).targetAsBean().to(MappingBean.class);
			fail("No way to create ExtObject");
		} catch (ConversionException e) {}

		converted.remove("embedded");
		MappingBean convertedBean = converter.convert(converted)
				.targetAsBean()
				.to(MappingBean.class);
		assertNotNull(convertedBean);
		assertEquals(bean.getProp1(), convertedBean.getProp1());
		assertEquals(bean.getProp2(), convertedBean.getProp2());
		assertEquals(bean.getProp3(), convertedBean.getProp3());
		assertNull(convertedBean.getEmbedded());
	}

	/**
	 * Section 707.4.4 : Maps, Interfaces, Java Beans, DTOs and Annotations
	 * <p/>
	 * 707.4.4.4 - Converting to a map-like structure
	 * <p/>
	 * 707.4.4.4.6 - DTOs
	 * <p/>
	 * DTOs are classes with public non-static fields and no methods other than
	 * the ones provided by the java.lang.Object class. OSGi DTOs extend the
	 * org.osgi.dto.DTO class, however objects following the DTO rules that do
	 * not extend the DTO class are also treated as DTOs by the converter. DTOs
	 * may have static fields, or non-public instance fields. These are ignored
	 * by the converter.
	 * <p/>
	 * When converting from a DTO to another map-like structure each public
	 * instance field is considered. The field name is taken as the key for the
	 * map entry, taking into account the section called
	 * <code><I>Key Mapping<I></code>, the field value is taken as the value for
	 * the map entry.
	 * <p/>
	 * When converting to a DTO, the converter attempts to find fields that
	 * match the key of each entry in the source map and then converts the value
	 * to the field type before assigning it. The key of the map entries may
	 * need to be converted into a String first. Keys are mapped according to
	 * the section called <code><I>Key Mapping<I></code>. The DTO is constructed
	 * using its no-parameter constructor and each public field is filled with
	 * data from the source eagerly. Fields present in the DTO but missing in
	 * the source object not be set.
	 * <p/>
	 * The converter only considers a type to be a DTO type if it declares no
	 * methods. However, if a type needs to be treated as a DTO that has
	 * methods, the converter can be instructed to do this using the
	 * sourceAsDTO() and targetAsDTO() modifiers.
	 */
	public void testDTOConversion() {
		Converter converter = Converters.standardConverter();

		WithStaticAndPrivateFieldsDTOLike withStaticAndPrivateFieldsDTOLike = new WithStaticAndPrivateFieldsDTOLike();
		withStaticAndPrivateFieldsDTOLike.prop1 = "value1";
		withStaticAndPrivateFieldsDTOLike.prop2 = "value2";
		Map map = converter.convert(withStaticAndPrivateFieldsDTOLike).sourceAsDTO()
				.to(Map.class);
		assertNotNull(map);
		assertNull(map.get("prop0"));
		assertNull(map.get("STATIC_FIELD"));
		assertEquals(withStaticAndPrivateFieldsDTOLike.prop1, map.get("prop1"));
		assertEquals(withStaticAndPrivateFieldsDTOLike.prop2, map.get("prop2"));

		NotDTOLike notDtoLike = new NotDTOLike();
		notDtoLike.prop1 = "value1_";
		notDtoLike.prop2 = "value2";
		notDtoLike.prop3 = "value3";
		AnnotationInterface annotationConverted = converter.convert(notDtoLike)
				.sourceAsDTO()
				.to(AnnotationInterface.class);
		assertNotNull(annotationConverted);
		assertEquals(notDtoLike.prop1, annotationConverted.prop1());
		assertEquals(notDtoLike.prop2, annotationConverted.prop2());
		assertEquals(notDtoLike.prop3, annotationConverted.prop3());
		notDtoLike.generateProp3();
		assertEquals(notDtoLike.prop3, annotationConverted.prop3());
		try {
			annotationConverted.prop4();
			fail("ConversionException expected for undefined field");
		} catch (ConversionException e) {}

		map = new HashMap();
		map.put("prop1", "mapValue1");
		map.put("prop3", "mapValue3");
		map.put("prop4", "mapValue4");
		NotDTOLike notDtoLikeConverted = converter.convert(map)
				.targetAsDTO()
				.to(NotDTOLike.class);
		assertNotNull(notDtoLikeConverted);
		assertNull(notDtoLikeConverted.prop2);
		assertEquals(map.get("prop1"), notDtoLikeConverted.prop1);
		assertEquals(map.get("prop3"), notDtoLikeConverted.prop3);
	}

	/**
	 * Section 707.4.4 : Maps, Interfaces, Java Beans, DTOs and Annotations
	 * <p/>
	 * 707.4.4.4 - Converting to a map-like structure
	 * <p/>
	 * 707.4.4.4.7 - Types with getProperties()
	 * <p/>
	 * The converter uses reflection to find a public java.util.Map
	 * getProperties() or java.util.Dictionary getProperties() method on the
	 * source type to obtain a map view over the source object. This map view is
	 * used to convert the source object to a map-like structure.
	 * <p/>
	 * If the source object both implements an interface and also has a public
	 * getProperties() method, the converter uses the getProperties() method to
	 * obtain the map view. This getProperties() may or may not be part of an
	 * implemented interface.
	 * <p/>
	 * Note: this mechanism can only be used to convert to another type. The
	 * reverse is not supported
	 */
	public void testTypesWithGetPropertiesConversion() {

		Converter converter = Converters.standardConverter();
		TypeWithGetProperties typeWithGetProperties = new TypeWithGetProperties(
				true);
		Map map = converter.convert(typeWithGetProperties).to(Map.class);

		assertNotNull(map);
		assertEquals(map.get("prop1"), typeWithGetProperties.prop1());
		assertEquals(map.get("prop2"), typeWithGetProperties.prop2());
		assertEquals(map.get("prop3"), typeWithGetProperties.prop3());
		assertEquals(map.get("prop4"), typeWithGetProperties.prop4());
		assertEquals(map.get("prop5"), typeWithGetProperties.prop5());

		try {
			TypeWithGetProperties convertedTypeWithGetProperties = converter
					.convert(map)
					.to(TypeWithGetProperties.class);
			fail("conversion to Type with getProperties() not supported");
		} catch (ConversionException e) {}

	}
	
	/**
	 * Section 707.4.4 : Maps, Interfaces, Java Beans, DTOs and Annotations
	 * <p/>
	 * 707.4.4.4 - Converting to a map-like structure
	 * <p/>
	 * 707.4.4.4.8 -Key Mapping
	 * <p/>
	 * When converting to or from a Java type, the key is derived from the
	 * method or field name. Certain common property name characters, such as
	 * full stop ('.' \u002E) and hyphen-minus ('-' \u002D) are not valid in
	 * Java identifiers. So the name of a method must be converted to its
	 * corresponding key name as follows:
	 * <p/>
	 * <ul>
	 * <li>A single dollar sign (<code class="code">'$' \u0024</code>) is
	 * removed unless it is followed by:
	 * <ul>
	 * <li>A low line (<code class="code">'_' \u005F</code>) and a dollar sign
	 * in which case the three consecutive characters (<code>"$_$"</code>) are
	 * converted to a single hyphen-minus (<code>'-' \u002D).</li>
	 <li>Another dollar sign in which case the two
	 consecutive dollar signs (<code>"$$"</code>) are converted to a single
	 * dollar sign.</li>
	 * </ul>
	 * </li>
	 * <li>A single low line (<code>'_' \u005F</code>) is converted into a full
	 * stop (<code>'.' \u002E</code>) unless is it followed by another low line
	 * in which case the two consecutive low lines (<code>"__"</code>) are
	 * converted to a single low line.</li>
	 * <li>All other characters are unchanged.</li>
	 * <li>If the type that declares the method also declares a static final
	 * <code>PREFIX_</code> field whose value is a compile-time constant
	 * <code>String</code>, then the key name is prefixed with the value of the
	 * <code>PREFIX_</code> field. <code>PREFIX_</code> fields in super-classes
	 * or super-interfaces are ignored.</li>
	 * </ul>
	 * <p/>
	 * However, if the type is a <em>single-element annotation</em>, then the
	 * key name for the <code>value</code> method is derived from the name of
	 * the component property type rather than the name of the method. In this
	 * case, the simple name of the component property type, that is, the name
	 * of the class without any package name or outer class name, if the
	 * component property type is an inner class, must be converted to the
	 * <code>value</code> method's property name as follows:
	 * <p/>
	 * <ul>
	 * <li>When a lower case character is followed by an upper case character, a
	 * full stop (<code>'.' \u002E</code>) is inserted between them.</li>
	 * <li>Each uppercase character is converted to lower case.</li>
	 * <li>All other characters are unchanged.</li>
	 * <li>If the annotation type declares a <code>PREFIX_</code> field whose
	 * value is a compile-time constant <code>String</code>, then the id is
	 * prefixed with the value of the <code>PREFIX_</code> field.</li>
	 * </ul>
	 */
	public void testKeyMapping() {

		Map<String,String> resultmap = new HashMap<String,String>();
		resultmap.put("org.osgi.util.converter.test.specialprop",
				"org.osgi.util.converter.test.specialprop");
		resultmap.put("org.osgi.util.converter.test.special$prop",
				"org.osgi.util.converter.test.special$prop");
		resultmap.put("org.osgi.util.converter.test.special.prop",
				"org.osgi.util.converter.test.special.prop");
		resultmap.put("org.osgi.util.converter.test..specialprop",
				"org.osgi.util.converter.test..specialprop");
		resultmap.put("org.osgi.util.converter.test.special_prop",
				"org.osgi.util.converter.test.special_prop");
		resultmap.put("org.osgi.util.converter.test.special_.prop",
				"org.osgi.util.converter.test.special_.prop");
		resultmap.put("org.osgi.util.converter.test.special._prop",
				"org.osgi.util.converter.test.special._prop");
		resultmap.put("org.osgi.util.converter.test.special..prop",
				"org.osgi.util.converter.test.special..prop");
		resultmap.put("org.osgi.util.converter.test.special-prop",
				"org.osgi.util.converter.test.special-prop");
		resultmap.put("org.osgi.util.converter.test.special$.prop",
				"org.osgi.util.converter.test.special$.prop");

		Converter converter = Converters.standardConverter();

		KeyMappingDTOLike dto = new KeyMappingDTOLike();
		dto.special$prop = "org.osgi.util.converter.test.specialprop";
		dto.special$$prop = "org.osgi.util.converter.test.special$prop";
		dto.special_prop = "org.osgi.util.converter.test.special.prop";
		dto._specialprop = "org.osgi.util.converter.test..specialprop";
		dto.special__prop = "org.osgi.util.converter.test.special_prop";
		dto.special___prop = "org.osgi.util.converter.test.special_.prop";
		dto.special_$__prop = "org.osgi.util.converter.test.special._prop";
		dto.special_$_prop = "org.osgi.util.converter.test.special..prop";
		dto.special$_$prop = "org.osgi.util.converter.test.special-prop";
		dto.special$$_$prop = "org.osgi.util.converter.test.special$.prop";

		Map<String,String> map = converter.convert(dto)
				.to(new TypeReference<Map<String,String>>() {});
		assertEquals(resultmap, map);

		KeyMappingDTOLike resultdto = converter.convert(resultmap)
				.targetAsDTO()
				.to(KeyMappingDTOLike.class);

		assertEquals(dto.special$prop, resultdto.special$prop);
		assertEquals(dto.special$$prop, resultdto.special$$prop);
		assertEquals(dto.special_prop, resultdto.special_prop);
		assertEquals(dto._specialprop, resultdto._specialprop);
		assertEquals(dto.special__prop, resultdto.special__prop);
		assertEquals(dto.special___prop, resultdto.special___prop);
		assertEquals(dto.special_$__prop, resultdto.special_$__prop);
		assertEquals(dto.special_$_prop, resultdto.special_$_prop);
		assertEquals(dto.special$_$prop, resultdto.special$_$prop);
		assertEquals(dto.special$$_$prop, resultdto.special$$_$prop);

		KeyMappingAnnotation keyMappingAnnotation = KeyMappingAnnotatedClass.class
				.getAnnotation(KeyMappingAnnotation.class);

		map = converter.convert(keyMappingAnnotation)
				.to(new TypeReference<Map<String,String>>() {});
		assertEquals(resultmap, map);

		KeyMappingAnnotation resultkeyMappingAnnotation = converter
				.convert(resultmap)
				.to(KeyMappingAnnotation.class);

		assertEquals(keyMappingAnnotation.special$prop(),
				resultkeyMappingAnnotation.special$prop());
		assertEquals(keyMappingAnnotation.special$$prop(),
				resultkeyMappingAnnotation.special$$prop());
		assertEquals(keyMappingAnnotation.special_prop(),
				resultkeyMappingAnnotation.special_prop());
		assertEquals(keyMappingAnnotation._specialprop(),
				resultkeyMappingAnnotation._specialprop());
		assertEquals(keyMappingAnnotation.special__prop(),
				resultkeyMappingAnnotation.special__prop());
		assertEquals(keyMappingAnnotation.special___prop(),
				resultkeyMappingAnnotation.special___prop());
		assertEquals(keyMappingAnnotation.special_$__prop(),
				resultkeyMappingAnnotation.special_$__prop());
		assertEquals(keyMappingAnnotation.special_$_prop(),
				resultkeyMappingAnnotation.special_$_prop());
		assertEquals(keyMappingAnnotation.special$_$prop(),
				resultkeyMappingAnnotation.special$_$prop());
		assertEquals(keyMappingAnnotation.special$$_$prop(),
				resultkeyMappingAnnotation.special$$_$prop());

		KeyMappingBean bean = new KeyMappingBean();
		bean.setSpecial$prop("org.osgi.util.converter.test.specialprop");
		bean.setSpecial$$prop("org.osgi.util.converter.test.special$prop");
		bean.setSpecial_prop("org.osgi.util.converter.test.special.prop");
		bean.set_specialprop("org.osgi.util.converter.test..specialprop");
		bean.setSpecial__prop("org.osgi.util.converter.test.special_prop");
		bean.setSpecial___prop("org.osgi.util.converter.test.special_.prop");
		bean.setSpecial_$__prop("org.osgi.util.converter.test.special._prop");
		bean.setSpecial_$_prop("org.osgi.util.converter.test.special..prop");
		bean.setSpecial$_$prop("org.osgi.util.converter.test.special-prop");
		bean.setSpecial$$_$prop("org.osgi.util.converter.test.special$.prop");

		map = converter.convert(bean)
				.sourceAsBean()
				.to(new TypeReference<Map<String,String>>() {});
		Map<String,String> resultmap2 = new HashMap<>(resultmap);
		resultmap2.remove("org.osgi.util.converter.test..specialprop");
		assertEquals(resultmap2, map);

		KeyMappingBean resultbean = converter.convert(resultmap)
				.targetAsBean()
				.to(KeyMappingBean.class);
		assertEquals(bean.getSpecial$prop(), resultbean.getSpecial$prop());
		assertEquals(bean.getSpecial$$prop(), resultbean.getSpecial$$prop());
		assertEquals(bean.getSpecial_prop(), resultbean.getSpecial_prop());
		// assertEquals(bean.get_specialprop(), resultbean.get_specialprop());
		assertEquals(bean.getSpecial__prop(), resultbean.getSpecial__prop());
		assertEquals(bean.getSpecial___prop(), resultbean.getSpecial___prop());
		assertEquals(bean.getSpecial_$__prop(),
				resultbean.getSpecial_$__prop());
		assertEquals(bean.getSpecial_$_prop(), resultbean.getSpecial_$_prop());
		assertEquals(bean.getSpecial$_$prop(), resultbean.getSpecial$_$prop());
		assertEquals(bean.getSpecial$$_$prop(),
				resultbean.getSpecial$$_$prop());

		KeyMappingInterface inter = new KeyMappingInterface() {
			String	_specialprop	= "org.osgi.util.converter.test..specialprop";
			String	special$$_$prop	= "org.osgi.util.converter.test.special$.prop";
			String	special$$prop	= "org.osgi.util.converter.test.special$prop";
			String	special$_$prop	= "org.osgi.util.converter.test.special-prop";
			String	special$prop	= "org.osgi.util.converter.test.specialprop";
			String	special_$__prop	= "org.osgi.util.converter.test.special._prop";
			String	special_$_prop	= "org.osgi.util.converter.test.special..prop";
			String	special___prop	= "org.osgi.util.converter.test.special_.prop";
			String	special__prop	= "org.osgi.util.converter.test.special_prop";
			String	special_prop	= "org.osgi.util.converter.test.special.prop";

			@Override
			public String special$prop() {
				return special$prop;
			}

			@Override
			public String special$$prop() {
				return special$$prop;
			}

			@Override
			public String special_prop() {
				return special_prop;
			}

			@Override
			public String _specialprop() {
				return _specialprop;
			}

			@Override
			public String special__prop() {
				return special__prop;
			}

			@Override
			public String special___prop() {
				return special___prop;
			}

			@Override
			public String special_$__prop() {
				return special_$__prop;
			}

			@Override
			public String special_$_prop() {
				return special_$_prop;
			}

			@Override
			public String special$_$prop() {
				return special$_$prop;
			}

			@Override
			public String special$$_$prop() {
				return special$$_$prop;
			}
		};

		map = converter.convert(inter)
				.sourceAs(KeyMappingInterface.class)
				.to(
				new TypeReference<Map<String,String>>() {});
		assertEquals(resultmap, map);

		KeyMappingInterface resultinter = converter.convert(resultmap)
				.to(KeyMappingInterface.class);

		assertEquals(inter.special$prop(), resultinter.special$prop());
		assertEquals(inter.special$$prop(), resultinter.special$$prop());
		assertEquals(inter.special_prop(), resultinter.special_prop());
		assertEquals(inter._specialprop(), resultinter._specialprop());
		assertEquals(inter.special__prop(), resultinter.special__prop());
		assertEquals(inter.special___prop(), resultinter.special___prop());
		assertEquals(inter.special_$__prop(), resultinter.special_$__prop());
		assertEquals(inter.special_$_prop(), resultinter.special_$_prop());
		assertEquals(inter.special$_$prop(), resultinter.special$_$prop());
		assertEquals(inter.special$$_$prop(), resultinter.special$$_$prop());
	}

	public void testMapToDTOWithGenerics() {
		Map<String,Object> dto = new HashMap<>();

		dto.put("longList", Arrays.asList((short) 999, "1000"));

		Map<String,Object> dtoMap = new LinkedHashMap<>();
		dto.put("dtoMap", dtoMap);

		Map<String,Object> subDTO1 = new HashMap<>();
		subDTO1.put("charSet",
				new HashSet<>(Arrays.asList("foo", (int) 'o', 'o')));
		dtoMap.put("zzz", subDTO1);

		Map<String,Object> subDTO2 = new HashMap<>();
		subDTO2.put("charSet", new HashSet<>(Arrays.asList('b', 'a', 'r')));
		dtoMap.put("aaa", subDTO2);

		Converter converter = Converters.standardConverter();
		MyDTO2 converted = converter.convert(dto).to(MyDTO2.class);

		assertEquals(Arrays.asList(999L, 1000L), converted.longList);
		Map<String,MyDTO3> nestedMap = converted.dtoMap;

		// Check iteration order is preserved by iterating
		int i = 0;
		for (Iterator<Map.Entry<String,MyDTO3>> it = nestedMap.entrySet()
				.iterator(); it.hasNext(); i++) {
			Map.Entry<String,MyDTO3> entry = it.next();
			switch (i) {
				case 0 :
					assertEquals("zzz", entry.getKey());
					MyDTO3 dto1 = entry.getValue();
					assertEquals(
							new HashSet<Character>(Arrays.asList('f', 'o')),
							dto1.charSet);
					break;
				case 1 :
					assertEquals("aaa", entry.getKey());
					MyDTO3 dto2 = entry.getValue();
					assertEquals(
							new HashSet<Character>(
									Arrays.asList('b', 'a', 'r')),
							dto2.charSet);
					break;
				default :
					fail("Unexpected number of elements on map");
			}
		}
	}

	public void testMapToDTOWithGenericVariables() {
		Map<String,Object> dto = new HashMap<>();
		dto.put("set", new HashSet<>(Arrays.asList("foo", (int) 'o', 'o')));
		dto.put("raw", "1234");
		dto.put("array", Arrays.asList("foo", (int) 'o', 'o'));

		Converter converter = Converters.standardConverter();
		MyGenericDTOWithVariables<Character> converted = converter.convert(dto)
				.to(new TypeReference<MyGenericDTOWithVariables<Character>>() {});
		assertEquals(Character.valueOf('1'), converted.raw);
		assertTrue(Arrays.equals(new Character[] {
				'f', 'o', 'o'
		}, converted.array));
		assertEquals(new HashSet<Character>(Arrays.asList('f', 'o')),
				converted.set);
	}

	public void testMapToInterfaceWithGenerics() {
		Map<String,Object> dto = new HashMap<>();
		dto.put("charSet", new HashSet<>(Arrays.asList("foo", (int) 'o', 'o')));

		Converter converter = Converters.standardConverter();
		MyGenericInterface converted = converter.convert(dto)
				.to(MyGenericInterface.class);
		assertEquals(new HashSet<Character>(Arrays.asList('f', 'o')),
				converted.charSet());
	}

	public void testMapToInterfaceWithGenericVariables() {
		Map<String,Object> dto = new HashMap<>();
		dto.put("set", new HashSet<>(Arrays.asList("foo", (int) 'o', 'o')));
		dto.put("raw", "1234");
		dto.put("array", Arrays.asList("foo", (int) 'o', 'o'));

		Converter converter = Converters.standardConverter();
		MyGenericInterfaceWithVariables<Character> converted = converter
				.convert(dto)
				.to(new TypeReference<MyGenericInterfaceWithVariables<Character>>() {});
		assertEquals(Character.valueOf('1'), converted.raw());
		assertTrue(Arrays.equals(new Character[] {
				'f', 'o', 'o'
		}, converted.array()));
		assertEquals(new HashSet<Character>(Arrays.asList('f', 'o')),
				converted.set());
	}

	public void testConvertMarkerAnnotation() {
		Converter converter = Converters.standardConverter();

		MyMarkerAnnotation ann = MarkedInterface.class
				.getAnnotation(MyMarkerAnnotation.class);
		Map< ? , ? > m = converter.convert(ann).to(Map.class);
		assertEquals(1, m.size());
		assertEquals(Boolean.TRUE, m.get("my.marker.annotation"));

		try {
			converter.convert(m).to(MyMarkerAnnotation.class);
			fail("Should have thrown a Conversion Exception");
		} catch (ConversionException ce) {
			// good
		}

		Map<String,String> m2 = converter.convert(ann)
				.to(new TypeReference<Map<String,String>>() {});
		assertEquals("true", m2.get("my.marker.annotation"));
	}
}
