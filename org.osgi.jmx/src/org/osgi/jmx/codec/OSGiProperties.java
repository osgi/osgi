/*
 * Copyright (c) OSGi Alliance (2009). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.jmx.codec;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;

import org.osgi.framework.ServiceReference;

/**
 * <p>
 * This class serves as both the documentation of the type structure and as the
 * codification of the mechanism to convert to/from the TabularData.
 * <p>
 * This class represents the CODEC for property dictionaries. As JMX is a rather
 * primitive system and is not intended to be a generic RMI type system, the set
 * of types that can be transfered between the management agent and the managed
 * OSGi container is limited to simple types, arrays of simple types and vectors
 * of simple types. This enforcement is strict and no attempt is made to create
 * a yet another generic serialization mechanism for transferring property
 * values outside of these types.
 * <p>
 * The syntax for the type indicator
 * 
 * <pre>
 * type   ::=    scalar | vector | array 
 * scalar ::=    String | Integer | Long | Float | 
 *               Double | Byte | Short | Character |
 *               Boolean | BigDecimal | BigInteger
 * primitive ::= int | long | float | double | byte | short | 
 *               char | boolean 
 * array ::=     &lt;Array of primitive&gt; | &lt;Array of scalar&gt;
 * vector ::=    Vector of scalar
 * </pre>
 * 
 * The values for Arrays and Vectors are separated by ",".
 * <p>
 * The structure of the composite data for a row in the table is:
 * <table border="1">
 * <tr>
 * <td>Key</td>
 * <td>String</td>
 * </tr>
 * <tr>
 * <td>Value</td>
 * <td>String</td>
 * </tr>
 * <tr>
 * <td>Type</td>
 * <td>String</td>
 * </tr>
 * </table>
 * <p>
 * The
 */
@SuppressWarnings("unchecked")
public class OSGiProperties {

	/**
	 * Answer the tabular data representation of the properties dictionary
	 * 
	 * @param properties
	 * @return the tabular data representation of the properties
	 */ 
	public static TabularData tableFrom(Dictionary properties) {
		TabularDataSupport table = new TabularDataSupport(PROPERTY_TABLE);
		for (Enumeration keys = properties.keys(); keys.hasMoreElements();) {
			String key = (String) keys.nextElement();
			table.put(encode(key, properties.get(key)));
		}
		return table;
	}

	/**
	 * Answer the tabular data representation of the service references
	 * properties
	 * 
	 * @param ref
	 * @return the tabular data representing the service reference properties
	 */
	public static TabularData tableFrom(ServiceReference ref) {
		Hashtable<String, Object> props = new Hashtable<String, Object>();
		for (String key : ref.getPropertyKeys()) {
			props.put(key, ref.getProperty(key));
		}
		return tableFrom(props);
	}

	/**
	 * Encode the key and value as composite data
	 * 
	 * @param key
	 * @param value
	 * @return the encoded composite data of the key and value
	 */ 
	public static CompositeData encode(String key, Object value) {
		Class<?> clazz = value.getClass();

		if (clazz.isArray()) {
			return encodeArray(key, value, clazz.getComponentType());
		} else if (clazz.equals(Vector.class)) {
			return encodeVector(key, (Vector) value);
		}
		return propertyData(key, value.toString(), typeOf(clazz));
	}

	/**
	 * Answer the hashtable converted from the supplied tabular data
	 * 
	 * @param table
	 * @return the hashtable represented by the tabular data
	 */ 
	public static Hashtable<String, Object> propertiesFrom(TabularData table) {
		Hashtable props = new Hashtable();
		if (table == null) {
			return props;
		}
		for (CompositeData data : (Collection<CompositeData>) table.values()) {
			props.put(data.get(KEY), parse((String) data.get(VALUE),
					(String) data.get(TYPE)));
		}

		return props;
	}

	/**
	 * Encode the array as composite data
	 * 
	 * @param key
	 * @param value
	 * @param componentClazz
	 * @return the composite data representation
	 */
	protected static CompositeData encodeArray(String key, Object value,
			Class<?> componentClazz) {
		String type = typeOf(componentClazz);
		StringBuffer buf = new StringBuffer();
		if (Integer.TYPE.equals(componentClazz)) {
			int[] array = (int[]) value;
			for (int i = 0; i < array.length; i++) {
				buf.append(array[i]);
				if (i < array.length - 1) {
					buf.append(',');
				}
			}
		} else if (Long.TYPE.equals(componentClazz)) {
			long[] array = (long[]) value;
			for (int i = 0; i < array.length; i++) {
				buf.append(array[i]);
				if (i < array.length - 1) {
					buf.append(',');
				}
			}
		} else if (Double.TYPE.equals(componentClazz)) {
			double[] array = (double[]) value;
			for (int i = 0; i < array.length; i++) {
				buf.append(array[i]);
				if (i < array.length - 1) {
					buf.append(',');
				}
			}
		} else if (Byte.TYPE.equals(componentClazz)) {
			byte[] array = (byte[]) value;
			for (int i = 0; i < array.length; i++) {
				buf.append(array[i]);
				if (i < array.length - 1) {
					buf.append(',');
				}
			}
		} else if (Short.TYPE.equals(componentClazz)) {
			short[] array = (short[]) value;
			for (int i = 0; i < array.length; i++) {
				buf.append(array[i]);
				if (i < array.length - 1) {
					buf.append(',');
				}
			}
		} else if (Character.TYPE.equals(componentClazz)) {
			char[] array = (char[]) value;
			for (int i = 0; i < array.length; i++) {
				buf.append(array[i]);
				if (i < array.length - 1) {
					buf.append(',');
				}
			}
		} else if (Boolean.TYPE.equals(componentClazz)) {
			boolean[] array = (boolean[]) value;
			for (int i = 0; i < array.length; i++) {
				buf.append(array[i]);
				if (i < array.length - 1) {
					buf.append(',');
				}
			}
		} else {
			Object[] array = (Object[]) value;
			for (int i = 0; i < array.length; i++) {
				buf.append(array[i]);
				if (i < array.length - 1) {
					buf.append(',');
				}
			}
		}
		return propertyData(key, buf.toString(), "Array of " + type);
	}

	/**
	 * Encode the vector as composite data
	 * 
	 * @param key
	 * @param value
	 * @return the composite data representation
	 */ 
	protected static CompositeData encodeVector(String key, Vector value) {
		String type = "String";
		if (value.size() > 0) {
			type = typeOf(value.get(0).getClass());
		}
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < value.size(); i++) {
			buf.append(value.get(i));
			if (i < value.size() - 1) {
				buf.append(',');
			}
		}
		return propertyData(key, buf.toString(), "Vector of " + type);
	}

	/**
	 * Answer the string type of the class
	 * 
	 * @param clazz
	 * @return the string type of the class
	 */
	protected static String typeOf(Class<?> clazz) {

		if (clazz.equals(String.class)) {
			return "String";
		}
		if (clazz.equals(Integer.class)) {
			return "Integer";
		}
		if (clazz.equals(Long.class)) {
			return "Long";
		}
		if (clazz.equals(Double.class)) {
			return "Double";
		}
		if (clazz.equals(Byte.class)) {
			return "Byte";
		}
		if (clazz.equals(Short.class)) {
			return "Short";
		}
		if (clazz.equals(Character.class)) {
			return "Character";
		}
		if (clazz.equals(Boolean.class)) {
			return "Boolean";
		}
		if (clazz.equals(BigDecimal.class)) {
			return "BigDecimal";
		}
		if (clazz.equals(BigInteger.class)) {
			return "BigInteger";
		}
		if (clazz.equals(Integer.TYPE)) {
			return "int";
		}
		if (clazz.equals(Long.TYPE)) {
			return "long";
		}
		if (clazz.equals(Double.TYPE)) {
			return "double";
		}
		if (clazz.equals(Byte.TYPE)) {
			return "byte";
		}
		if (clazz.equals(Short.TYPE)) {
			return "short";
		}
		if (clazz.equals(Character.TYPE)) {
			return "char";
		}
		if (clazz.equals(Boolean.TYPE)) {
			return "boolean";
		}
		throw new IllegalArgumentException("Illegal type: " + clazz);
	}

	/**
	 * Answer the composite data representation of the key/value pair
	 * 
	 * @param key
	 * @param value
	 * @param type
	 * @return the composite data representation of the key/value pair
	 */
	protected static CompositeData propertyData(String key, String value,
			String type) {
		Object[] itemValues = new Object[PROPERTIES.length];
		itemValues[0] = key;
		itemValues[1] = value;
		itemValues[2] = type;

		try {
			return new CompositeDataSupport(PROPERTY, PROPERTIES, itemValues);
		} catch (OpenDataException e) {
			throw new IllegalStateException("Cannot form property open data", e);
		}
	}

	/**
	 * Parse the string value into an Object
	 * 
	 * @param value
	 * @param type
	 * @return the object represented by the String
	 */
	public static Object parse(String value, String type) {
		StringTokenizer tokens = new StringTokenizer(type);
		if (!tokens.hasMoreElements()) {
			throw new IllegalArgumentException("Type is empty");
		}
		String token = tokens.nextToken();
		if ("Array".equals(token)) {
			return parseArray(value, tokens);
		}
		if ("Vector".equals(token)) {
			return parseVector(value, tokens);
		}
		if (SCALAR_TYPES.contains(token)) {
			return parseScalar(value, token);
		}
		throw new IllegalArgumentException("Unknown type: " + type);
	}

	/**
	 * Parse the array represented by the string value
	 * 
	 * @param value
	 * @param tokens
	 * @return the array represented by the string value
	 */
	protected static Object parseArray(String value, StringTokenizer tokens) {
		if (!tokens.hasMoreTokens()) {
			throw new IllegalArgumentException(
					"Expecting <of> token in Array type");
		}
		if (!"of".equals(tokens.nextToken())) {
			throw new IllegalArgumentException(
					"Expecting <of> token in Array type");
		}
		if (!tokens.hasMoreTokens()) {
			throw new IllegalArgumentException(
					"Expecting <primitive>|<scalar> token in Array type");
		}
		String type = tokens.nextToken();
		if (SCALAR_TYPES.contains(type)) {
			return parseScalarArray(value, type);
		} else if (PRIMITIVE_TYPES.contains(type)) {
			return parsePrimitiveArray(value, type);
		} else {
			throw new IllegalArgumentException(
					"Expecting <scalar>|<primitive> type token in Array type: "
							+ type);
		}
	}

	/**
	 * Parse the array represented by the string value
	 * 
	 * @param value
	 * @param type
	 * @return the array represented by the string value
	 */
	protected static Object parseScalarArray(String value, String type) {
		ArrayList<Object> array = new ArrayList<Object>();
		StringTokenizer values = new StringTokenizer(value, ",");
		while (values.hasMoreTokens()) {
			array.add(parseScalar(values.nextToken().trim(), type));
		}
		return array.toArray(createScalarArray(type, array.size()));
	}

	/**
	 * Create the scalar array from the supplied type
	 * 
	 * @param type
	 * @param size
	 * @return the scalar array from the supplied type
	 */
	protected static Object[] createScalarArray(String type, int size) {
		if ("String".equals(type)) {
			return new String[size];
		}
		if ("Integer".equals(type)) {
			return new Integer[size];
		}
		if ("Long".equals(type)) {
			return new Long[size];
		}
		if ("Double".equals(type)) {
			return new Double[size];
		}
		if ("Byte".equals(type)) {
			return new Byte[size];
		}
		if ("Short".equals(type)) {
			return new Short[size];
		}
		if ("Character".equals(type)) {
			return new Character[size];
		}
		if ("Boolean".equals(type)) {
			return new Boolean[size];
		}
		if ("BigDecimal".equals(type)) {
			return new BigDecimal[size];
		}
		if ("BigInteger".equals(type)) {
			return new BigInteger[size];
		}
		throw new IllegalArgumentException("Unknown scalar type: " + type);
	}

	/**
	 * Parse the array from the supplied values
	 * 
	 * @param value
	 * @param type
	 * @return the array from the supplied values
	 */
	protected static Object parsePrimitiveArray(String value, String type) {
		StringTokenizer values = new StringTokenizer(value, ",");
		if ("int".equals(type)) {
			int[] array = new int[values.countTokens()];
			int i = 0;
			while (values.hasMoreTokens()) {
				array[i++] = Integer.parseInt(values.nextToken().trim());
			}
			return array;
		}
		if ("long".equals(type)) {
			long[] array = new long[values.countTokens()];
			int i = 0;
			while (values.hasMoreTokens()) {
				array[i++] = Long.parseLong(values.nextToken().trim());
			}
			return array;
		}
		if ("double".equals(type)) {
			double[] array = new double[values.countTokens()];
			int i = 0;
			while (values.hasMoreTokens()) {
				array[i++] = Double.parseDouble(values.nextToken().trim());
			}
			return array;
		}
		if ("byte".equals(type)) {
			byte[] array = new byte[values.countTokens()];
			int i = 0;
			while (values.hasMoreTokens()) {
				array[i++] = Byte.parseByte(values.nextToken().trim());
			}
			return array;
		}
		if ("short".equals(type)) {
			short[] array = new short[values.countTokens()];
			int i = 0;
			while (values.hasMoreTokens()) {
				array[i++] = Short.parseShort(values.nextToken().trim());
			}
			return array;
		}
		if ("char".equals(type)) {
			char[] array = new char[values.countTokens()];
			int i = 0;
			while (values.hasMoreTokens()) {
				array[i++] = values.nextToken().trim().charAt(0);
			}
			return array;
		}
		if ("boolean".equals(type)) {
			boolean[] array = new boolean[values.countTokens()];
			int i = 0;
			while (values.hasMoreTokens()) {
				array[i++] = Boolean.parseBoolean(values.nextToken().trim());
			}
			return array;
		}
		throw new IllegalArgumentException("Unknown primitive type: " + type);
	}

	/**
	 * Parse the vector represented by teh supplied string value
	 * 
	 * @param value
	 * @param tokens
	 * @return the vector represented by teh supplied string value
	 */
	protected static Object parseVector(String value, StringTokenizer tokens) {
		if (!tokens.hasMoreTokens()) {
			throw new IllegalArgumentException(
					"Expecting <of> token in Vector type");
		}
		if (!tokens.nextElement().equals("of")) {
			throw new IllegalArgumentException(
					"Expecting <of> token in Vector type");
		}
		if (!tokens.hasMoreTokens()) {
			throw new IllegalArgumentException(
					"Expecting <scalar> token in Vector type");
		}
		String type = tokens.nextToken();
		StringTokenizer values = new StringTokenizer(value, ",");
		Vector<Object> vector = new Vector<Object>();
		if (!SCALAR_TYPES.contains(type)) {
			throw new IllegalArgumentException(
					"Expecting <scalar> type token in Vector type: " + type);
		}
		while (values.hasMoreTokens()) {
			vector.add(parseScalar(values.nextToken().trim(), type));
		}
		return vector;
	}

	/**
	 * Construct the scalar valre represented by the string
	 * 
	 * @param value
	 * @param type
	 * @return the scalar valre represented by the string
	 */
	protected static Object parseScalar(String value, String type) {
		if ("String".equals(type)) {
			return value;
		}
		if ("Integer".equals(type)) {
			return Integer.parseInt(value);
		}
		if ("Long".equals(type)) {
			return Long.parseLong(value);
		}
		if ("Double".equals(type)) {
			return Double.parseDouble(value);
		}
		if ("Byte".equals(type)) {
			return Byte.parseByte(value);
		}
		if ("Short".equals(type)) {
			return Short.parseShort(value);
		}
		if ("Character".equals(type)) {
			return value.charAt(0);
		}
		if ("Boolean".equals(type)) {
			return Boolean.parseBoolean(value);
		}
		if ("BigDecimal".equals(type)) {
			return new BigDecimal(value);
		}
		if ("BigInteger".equals(type)) {
			return new BigInteger(value);
		}
		throw new IllegalArgumentException("Unknown scalar type: " + type);
	}

	private static TabularType createPropertyTableType() {
		try {
			return new TabularType("Properties", "The table of credentials",
					PROPERTY, new String[] { KEY });
		} catch (OpenDataException e) {
			throw new IllegalStateException(
					"Cannot form services table open data", e);
		}
	}

	private static CompositeType createPropertyType() {
		String description = "This type encapsulates a key/value pair";
		String[] itemNames = PROPERTIES;
		OpenType[] itemTypes = new OpenType[itemNames.length];
		String[] itemDescriptions = new String[itemNames.length];
		itemTypes[0] = SimpleType.STRING;
		itemTypes[1] = SimpleType.STRING;
		itemTypes[2] = SimpleType.STRING;

		itemDescriptions[0] = "The key of the property";
		itemDescriptions[1] = "The value of the property";
		itemDescriptions[2] = "The type of the value";

		try {
			return new CompositeType("Property", description, itemNames,
					itemDescriptions, itemTypes);
		} catch (OpenDataException e) {
			throw new IllegalStateException("Cannot form property open data", e);
		}

	}

	/**
	 * The key of the entry
	 */
	public static final String KEY = "Key";
	/**
	 * The value of the entry
	 */
	public static final String VALUE = "Value";
	/**
	 * The type of the entry
	 */
	public static final String TYPE = "Type";
	/**
	 * The composite entries for the row
	 */
	public static final String[] PROPERTIES = new String[] { KEY, VALUE, TYPE };

	/**
	 * The property type
	 */
	public static final CompositeType PROPERTY = createPropertyType();
	/**
	 * The table type
	 */
	public static final TabularType PROPERTY_TABLE = createPropertyTableType();
	/**
	 * The scalar type
	 */
	protected static final Set<String> SCALAR_TYPES = new HashSet<String>();
	/**
	 * The primitive types
	 */
	protected static final Set<String> PRIMITIVE_TYPES = new HashSet<String>();

	static {
		SCALAR_TYPES.add("String");
		SCALAR_TYPES.add("Integer");
		SCALAR_TYPES.add("Long");
		SCALAR_TYPES.add("Float");
		SCALAR_TYPES.add("Double");
		SCALAR_TYPES.add("Byte");
		SCALAR_TYPES.add("Short");
		SCALAR_TYPES.add("Character");
		SCALAR_TYPES.add("Boolean");
		SCALAR_TYPES.add("BigDecimal");
		SCALAR_TYPES.add("BigInteger");

		PRIMITIVE_TYPES.add("int");
		PRIMITIVE_TYPES.add("long");
		PRIMITIVE_TYPES.add("float");
		PRIMITIVE_TYPES.add("double");
		PRIMITIVE_TYPES.add("byte");
		PRIMITIVE_TYPES.add("short");
		PRIMITIVE_TYPES.add("char");
		PRIMITIVE_TYPES.add("boolean");
	}
}
