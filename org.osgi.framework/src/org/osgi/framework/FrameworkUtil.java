/*
 * Copyright (c) OSGi Alliance (2005, 2008). All Rights Reserved.
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

package org.osgi.framework;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

/**
 * Framework Utility class.
 * 
 * <p>
 * This class contains utility methods which access Framework functions that may
 * be useful to bundles.
 * 
 * @since 1.3
 * @ThreadSafe
 * @version $Revision$
 */
public class FrameworkUtil {
	/**
	 * FrameworkUtil objects may not be constructed.
	 */
	private FrameworkUtil() {
	}

	/**
	 * Creates a <code>Filter</code> object. This <code>Filter</code> object may
	 * be used to match a <code>ServiceReference</code> object or a
	 * <code>Dictionary</code> object.
	 * 
	 * <p>
	 * If the filter cannot be parsed, an {@link InvalidSyntaxException} will be
	 * thrown with a human readable message where the filter became unparsable.
	 * 
	 * <p>
	 * This method returns a Filter implementation which may not perform as well
	 * as the framework implementation-specific Filter implementation returned
	 * by {@link BundleContext#createFilter(String)}.
	 * 
	 * @param filter The filter string.
	 * @return A <code>Filter</code> object encapsulating the filter string.
	 * @throws InvalidSyntaxException If <code>filter</code> contains an invalid
	 *         filter string that cannot be parsed.
	 * @throws NullPointerException If <code>filter</code> is null.
	 * 
	 * @see Filter
	 */
	public static Filter createFilter(String filter)
			throws InvalidSyntaxException {
		return FilterImpl.newInstance(filter);
	}

	/**
	 * RFC 1960-based Filter. Filter objects can be created by calling the
	 * constructor with the desired filter string. A Filter object can be called
	 * numerous times to determine if the match argument matches the filter
	 * string that was used to create the Filter object.
	 * 
	 * <p>
	 * The syntax of a filter string is the string representation of LDAP search
	 * filters as defined in RFC 1960: <i>A String Representation of LDAP Search
	 * Filters</i> (available at http://www.ietf.org/rfc/rfc1960.txt). It should
	 * be noted that RFC 2254: <i>A String Representation of LDAP Search
	 * Filters</i> (available at http://www.ietf.org/rfc/rfc2254.txt) supersedes
	 * RFC 1960 but only adds extensible matching and is not applicable for this
	 * API.
	 * 
	 * <p>
	 * The string representation of an LDAP search filter is defined by the
	 * following grammar. It uses a prefix format.
	 * 
	 * <pre>
	 *   &lt;filter&gt; ::= '(' &lt;filtercomp&gt; ')'
	 *   &lt;filtercomp&gt; ::= &lt;and&gt; | &lt;or&gt; | &lt;not&gt; | &lt;item&gt;
	 *   &lt;and&gt; ::= '&amp;' &lt;filterlist&gt;
	 *   &lt;or&gt; ::= '|' &lt;filterlist&gt;
	 *   &lt;not&gt; ::= '!' &lt;filter&gt;
	 *   &lt;filterlist&gt; ::= &lt;filter&gt; | &lt;filter&gt; &lt;filterlist&gt;
	 *   &lt;item&gt; ::= &lt;simple&gt; | &lt;present&gt; | &lt;substring&gt;
	 *   &lt;simple&gt; ::= &lt;attr&gt; &lt;filtertype&gt; &lt;value&gt;
	 *   &lt;filtertype&gt; ::= &lt;equal&gt; | &lt;approx&gt; | &lt;greater&gt; | &lt;less&gt;
	 *   &lt;equal&gt; ::= '='
	 *   &lt;approx&gt; ::= '&tilde;='
	 *   &lt;greater&gt; ::= '&gt;='
	 *   &lt;less&gt; ::= '&lt;='
	 *   &lt;present&gt; ::= &lt;attr&gt; '=*'
	 *   &lt;substring&gt; ::= &lt;attr&gt; '=' &lt;initial&gt; &lt;any&gt; &lt;final&gt;
	 *   &lt;initial&gt; ::= NULL | &lt;value&gt;
	 *   &lt;any&gt; ::= '*' &lt;starval&gt;
	 *   &lt;starval&gt; ::= NULL | &lt;value&gt; '*' &lt;starval&gt;
	 *   &lt;final&gt; ::= NULL | &lt;value&gt;
	 * </pre>
	 * 
	 * <code>&lt;attr&gt;</code> is a string representing an attribute, or key,
	 * in the properties objects of the registered services. Attribute names are
	 * not case sensitive; that is cn and CN both refer to the same attribute.
	 * <code>&lt;value&gt;</code> is a string representing the value, or part of
	 * one, of a key in the properties objects of the registered services. If a
	 * <code>&lt;value&gt;</code> must contain one of the characters '
	 * <code>*</code>' or '<code>(</code>' or '<code>)</code>', these characters
	 * should be escaped by preceding them with the backslash '<code>\</code>'
	 * character. Note that although both the <code>&lt;substring&gt;</code> and
	 * <code>&lt;present&gt;</code> productions can produce the <code>'attr=*'</code>
	 * construct, this construct is used only to denote a presence filter.
	 * 
	 * <p>
	 * Examples of LDAP filters are:
	 * 
	 * <pre>
	 *   &quot;(cn=Babs Jensen)&quot;
	 *   &quot;(!(cn=Tim Howes))&quot;
	 *   &quot;(&amp;(&quot; + Constants.OBJECTCLASS + &quot;=Person)(|(sn=Jensen)(cn=Babs J*)))&quot;
	 *   &quot;(o=univ*of*mich*)&quot;
	 * </pre>
	 * 
	 * <p>
	 * The approximate match (<code>~=</code>) is implementation specific but
	 * should at least ignore case and white space differences. Optional are
	 * codes like soundex or other smart "closeness" comparisons.
	 * 
	 * <p>
	 * Comparison of values is not straightforward. Strings are compared
	 * differently than numbers and it is possible for a key to have multiple
	 * values. Note that that keys in the match argument must always be strings.
	 * The comparison is defined by the object type of the key's value. The
	 * following rules apply for comparison:
	 * 
	 * <blockquote>
	 * <TABLE BORDER=0>
	 * <TR>
	 * <TD><b>Property Value Type </b></TD>
	 * <TD><b>Comparison Type</b></TD>
	 * </TR>
	 * <TR>
	 * <TD>String</TD>
	 * <TD>String comparison</TD>
	 * </TR>
	 * <TR valign=top>
	 * <TD>Integer, Long, Float, Double, Byte, Short, BigInteger, BigDecimal</TD>
	 * <TD>numerical comparison</TD>
	 * </TR>
	 * <TR>
	 * <TD>Character</TD>
	 * <TD>character comparison</TD>
	 * </TR>
	 * <TR>
	 * <TD>Boolean</TD>
	 * <TD>equality comparisons only</TD>
	 * </TR>
	 * <TR>
	 * <TD>[] (array)</TD>
	 * <TD>recursively applied to values</TD>
	 * </TR>
	 * <TR>
	 * <TD>Collection</TD>
	 * <TD>recursively applied to values</TD>
	 * </TR>
	 * </TABLE>
	 * Note: arrays of primitives are also supported. </blockquote>
	 * 
	 * A filter matches a key that has multiple values if it matches at least
	 * one of those values. For example,
	 * 
	 * <pre>
	 * Dictionary d = new Hashtable();
	 * d.put(&quot;cn&quot;, new String[] {&quot;a&quot;, &quot;b&quot;, &quot;c&quot;});
	 * </pre>
	 * 
	 * d will match <code>(cn=a)</code> and also <code>(cn=b)</code>
	 * 
	 * <p>
	 * A filter component that references a key having an unrecognizable data
	 * type will evaluate to <code>false</code> .
	 */
	private static class FilterImpl implements Filter {
		/**
		 * Constructs a {@link FilterImpl} object. This filter object may be
		 * used to match a {@link ServiceReference} or a Dictionary.
		 * 
		 * <p>
		 * If the filter cannot be parsed, an {@link InvalidSyntaxException}
		 * will be thrown with a human readable message where the filter became
		 * unparsable.
		 * 
		 * @param filterString the filter string.
		 * @exception InvalidSyntaxException If the filter parameter contains an
		 *            invalid filter string that cannot be parsed.
		 */
		static FilterImpl newInstance(String filterString)
				throws InvalidSyntaxException {
			return new Parser(filterString).parse();
		}

		/**
		 * Filter using a service's properties.
		 * <p>
		 * This <code>Filter</code> is executed using the keys and values of the
		 * referenced service's properties. The keys are case insensitively
		 * matched with this <code>Filter</code>.
		 * 
		 * @param reference The reference to the service whose properties are
		 *        used in the match.
		 * @return <code>true</code> if the service's properties match this
		 *         <code>Filter</code>; <code>false</code> otherwise.
		 */
		public boolean match(ServiceReference reference) {
			return match0(new ServiceReferenceDictionary(reference));
		}

		/**
		 * Filter using a <code>Dictionary</code>. This <code>Filter</code> is
		 * executed using the specified <code>Dictionary</code>'s keys and
		 * values. The keys are case insensitively matched with this
		 * <code>Filter</code>.
		 * 
		 * @param dictionary The <code>Dictionary</code> whose keys are used in
		 *        the match.
		 * @return <code>true</code> if the <code>Dictionary</code>'s keys and
		 *         values match this filter; <code>false</code> otherwise.
		 * @throws IllegalArgumentException If <code>dictionary</code> contains
		 *         case variants of the same key name.
		 */
		public boolean match(Dictionary dictionary) {
			return match0(new CaseInsensitiveDictionary(dictionary));
		}

		/**
		 * Filter with case sensitivity using a <code>Dictionary</code>. This
		 * <code>Filter</code> is executed using the specified
		 * <code>Dictionary</code>'s keys and values. The keys are case
		 * sensitively matched with this <code>Filter</code>.
		 * 
		 * @param dictionary The <code>Dictionary</code> whose keys are used in
		 *        the match.
		 * @return <code>true</code> if the <code>Dictionary</code>'s keys and
		 *         values match this filter; <code>false</code> otherwise.
		 * @since 1.3
		 */
		public boolean matchCase(Dictionary dictionary) {
			return match0(dictionary);
		}

		/**
		 * Returns this <code>Filter</code>'s filter string.
		 * <p>
		 * The filter string is normalized by removing whitespace which does not
		 * affect the meaning of the filter.
		 * 
		 * @return This <code>Filter</code>'s filter string.
		 */
		public String toString() {
			String result = this.filterString;
			if (result == null) {
				StringBuffer sb = new StringBuffer();
				sb.append('(');

				switch (op) {
					case AND : {
						sb.append('&');

						FilterImpl[] filters = (FilterImpl[]) value;
						for (int i = 0, size = filters.length; i < size; i++) {
							sb.append(filters[i].toString());
						}

						break;
					}

					case OR : {
						sb.append('|');

						FilterImpl[] filters = (FilterImpl[]) value;
						for (int i = 0, size = filters.length; i < size; i++) {
							sb.append(filters[i].toString());
						}

						break;
					}

					case NOT : {
						sb.append('!');
						sb.append(value.toString());

						break;
					}

					case SUBSTRING : {
						sb.append(attr);
						sb.append('=');

						String[] substrings = (String[]) value;

						for (int i = 0, size = substrings.length; i < size; i++) {
							String substr = substrings[i];

							if (substr == null) /* * */{
								sb.append('*');
							}
							else /* xxx */{
								sb.append(encodeValue(substr));
							}
						}

						break;
					}
					case EQUAL : {
						sb.append(attr);
						sb.append('=');
						sb.append(encodeValue(value.toString()));

						break;
					}
					case GREATER : {
						sb.append(attr);
						sb.append(">="); //$NON-NLS-1$
						sb.append(encodeValue(value.toString()));

						break;
					}
					case LESS : {
						sb.append(attr);
						sb.append("<="); //$NON-NLS-1$
						sb.append(encodeValue(value.toString()));

						break;
					}
					case APPROX : {
						sb.append(attr);
						sb.append("~="); //$NON-NLS-1$
						sb.append(encodeValue(approxString(value.toString())));

						break;
					}

					case PRESENT : {
						sb.append(attr);
						sb.append("=*"); //$NON-NLS-1$

						break;
					}
				}

				sb.append(')');

				result = sb.toString();
				if (topLevel) /* only hold onto String object at toplevel */{
					this.filterString = result;
				}
			}

			return result;
		}

		/**
		 * Compares this <code>Filter</code> to another <code>Filter</code>.
		 * 
		 * <p>
		 * This implementation returns the result of calling
		 * <code>this.toString().equals(obj.toString()</code>.
		 * 
		 * @param obj The object to compare against this <code>Filter</code>.
		 * @return If the other object is a <code>Filter</code> object, then
		 *         returns the result of calling
		 *         <code>this.toString().equals(obj.toString()</code>;
		 *         <code>false</code> otherwise.
		 */
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}

			if (!(obj instanceof Filter)) {
				return false;
			}

			return this.toString().equals(obj.toString());
		}

		/**
		 * Returns the hashCode for this <code>Filter</code>.
		 * 
		 * <p>
		 * This implementation returns the result of calling
		 * <code>this.toString().hashCode()</code>.
		 * 
		 * @return The hashCode of this <code>Filter</code>.
		 */
		public int hashCode() {
			return this.toString().hashCode();
		}

		/** filter operation */
		private final int					op;
		private static final int			EQUAL		= 1;
		private static final int			APPROX		= 2;
		private static final int			GREATER		= 3;
		private static final int			LESS		= 4;
		private static final int			PRESENT		= 5;
		private static final int			SUBSTRING	= 6;
		private static final int			AND			= 7;
		private static final int			OR			= 8;
		private static final int			NOT			= 9;

		/** filter attribute or null if operation AND, OR or NOT */
		private final String				attr;
		/** filter operands */
		private final Object				value;

		/* normalized filter string for topLevel Filter object */
		private transient volatile String	filterString;

		/* true if root Filter object */
		private final boolean				topLevel;

		FilterImpl(boolean topLevel, int operation, String attr, Object value) {
			this.topLevel = topLevel;
			this.op = operation;
			this.attr = attr;
			this.value = value;
		}

		/**
		 * Internal match routine. Dictionary parameter must support
		 * case-insensitive get.
		 * 
		 * @param properties A dictionary whose keys are used in the match.
		 * @return If the Dictionary's keys match the filter, return
		 *         <code>true</code>. Otherwise, return <code>false</code>.
		 */
		private boolean match0(Dictionary properties) {
			switch (op) {
				case AND : {
					FilterImpl[] filters = (FilterImpl[]) value;
					for (int i = 0, size = filters.length; i < size; i++) {
						if (!filters[i].match0(properties)) {
							return false;
						}
					}

					return true;
				}

				case OR : {
					FilterImpl[] filters = (FilterImpl[]) value;
					for (int i = 0, size = filters.length; i < size; i++) {
						if (filters[i].match0(properties)) {
							return true;
						}
					}

					return false;
				}

				case NOT : {
					FilterImpl filter = (FilterImpl) value;

					return !filter.match0(properties);
				}

				case SUBSTRING :
				case EQUAL :
				case GREATER :
				case LESS :
				case APPROX : {
					Object prop = (properties == null) ? null : properties
							.get(attr);

					return compare(op, prop, value);
				}

				case PRESENT : {
					Object prop = (properties == null) ? null : properties
							.get(attr);

					return prop != null;
				}
			}

			return false;
		}

		/**
		 * Encode the value string such that '(', '*', ')' and '\' are escaped.
		 * 
		 * @param value unencoded value string.
		 * @return encoded value string.
		 */
		private static String encodeValue(String value) {
			boolean encoded = false;
			int inlen = value.length();
			int outlen = inlen << 1; /* inlen 2 */

			char[] output = new char[outlen];
			value.getChars(0, inlen, output, inlen);

			int cursor = 0;
			for (int i = inlen; i < outlen; i++) {
				char c = output[i];

				switch (c) {
					case '(' :
					case '*' :
					case ')' :
					case '\\' : {
						output[cursor] = '\\';
						cursor++;
						encoded = true;

						break;
					}
				}

				output[cursor] = c;
				cursor++;
			}

			return encoded ? new String(output, 0, cursor) : value;
		}

		private boolean compare(int operation, Object value1, Object value2) {
			if (value1 == null) {
				return false;
			}
			if (value1 instanceof String) {
				return compare_String(operation, (String) value1, value2);
			}

			Class clazz = value1.getClass();
			if (clazz.isArray()) {
				Class type = clazz.getComponentType();
				if (type.isPrimitive()) {
					return compare_PrimitiveArray(operation, type, value1,
							value2);
				}
				return compare_ObjectArray(operation, (Object[]) value1, value2);
			}
			if (value1 instanceof Collection) {
				return compare_Collection(operation, (Collection) value1,
						value2);
			}
			if (value1 instanceof Integer) {
				return compare_Integer(operation,
						((Integer) value1).intValue(), value2);
			}
			if (value1 instanceof Long) {
				return compare_Long(operation, ((Long) value1).longValue(),
						value2);
			}
			if (value1 instanceof Byte) {
				return compare_Byte(operation, ((Byte) value1).byteValue(),
						value2);
			}
			if (value1 instanceof Short) {
				return compare_Short(operation, ((Short) value1).shortValue(),
						value2);
			}
			if (value1 instanceof Character) {
				return compare_Character(operation, ((Character) value1)
						.charValue(), value2);
			}
			if (value1 instanceof Float) {
				return compare_Float(operation, ((Float) value1).floatValue(),
						value2);
			}
			if (value1 instanceof Double) {
				return compare_Double(operation, ((Double) value1)
						.doubleValue(), value2);
			}
			if (value1 instanceof Boolean) {
				return compare_Boolean(operation, ((Boolean) value1)
						.booleanValue(), value2);
			}
			if (value1 instanceof Comparable) {
				return compare_Comparable(operation, (Comparable) value1,
						value2);
			}
			return compare_Unknown(operation, value1, value2); // RFC 59
		}

		private boolean compare_Collection(int operation,
				Collection collection, Object value2) {
			for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
				if (compare(operation, iterator.next(), value2)) {
					return true;
				}
			}
			return false;
		}

		private boolean compare_ObjectArray(int operation, Object[] array,
				Object value2) {
			for (int i = 0, size = array.length; i < size; i++) {
				if (compare(operation, array[i], value2)) {
					return true;
				}
			}
			return false;
		}

		private boolean compare_PrimitiveArray(int operation, Class type,
				Object primarray, Object value2) {
			if (Integer.TYPE.isAssignableFrom(type)) {
				int[] array = (int[]) primarray;
				for (int i = 0, size = array.length; i < size; i++) {
					if (compare_Integer(operation, array[i], value2)) {
						return true;
					}
				}
				return false;
			}
			if (Long.TYPE.isAssignableFrom(type)) {
				long[] array = (long[]) primarray;
				for (int i = 0, size = array.length; i < size; i++) {
					if (compare_Long(operation, array[i], value2)) {
						return true;
					}
				}
				return false;
			}
			if (Byte.TYPE.isAssignableFrom(type)) {
				byte[] array = (byte[]) primarray;
				for (int i = 0, size = array.length; i < size; i++) {
					if (compare_Byte(operation, array[i], value2)) {
						return true;
					}
				}
				return false;
			}
			if (Short.TYPE.isAssignableFrom(type)) {
				short[] array = (short[]) primarray;
				for (int i = 0, size = array.length; i < size; i++) {
					if (compare_Short(operation, array[i], value2)) {
						return true;
					}
				}
				return false;
			}
			if (Character.TYPE.isAssignableFrom(type)) {
				char[] array = (char[]) primarray;
				for (int i = 0, size = array.length; i < size; i++) {
					if (compare_Character(operation, array[i], value2)) {
						return true;
					}
				}
				return false;
			}
			if (Float.TYPE.isAssignableFrom(type)) {
				float[] array = (float[]) primarray;
				for (int i = 0, size = array.length; i < size; i++) {
					if (compare_Float(operation, array[i], value2)) {
						return true;
					}
				}
				return false;
			}
			if (Double.TYPE.isAssignableFrom(type)) {
				double[] array = (double[]) primarray;
				for (int i = 0, size = array.length; i < size; i++) {
					if (compare_Double(operation, array[i], value2)) {
						return true;
					}
				}
				return false;
			}
			if (Boolean.TYPE.isAssignableFrom(type)) {
				boolean[] array = (boolean[]) primarray;
				for (int i = 0, size = array.length; i < size; i++) {
					if (compare_Boolean(operation, array[i], value2)) {
						return true;
					}
				}
				return false;
			}
			return false;
		}

		private boolean compare_String(int operation, String string,
				Object value2) {
			switch (operation) {
				case SUBSTRING : {
					String[] substrings = (String[]) value2;
					int pos = 0;
					for (int i = 0, size = substrings.length; i < size; i++) {
						String substr = substrings[i];

						if (i + 1 < size) /* if this is not that last substr */{
							if (substr == null) /* * */{
								String substr2 = substrings[i + 1];

								if (substr2 == null) /* ** */
									continue; /* ignore first star */
								/* *xxx */
								int index = string.indexOf(substr2, pos);
								if (index == -1) {
									return false;
								}

								pos = index + substr2.length();
								if (i + 2 < size) // if there are more
									// substrings, increment
									// over the string we just
									// matched; otherwise need
									// to do the last substr
									// check
									i++;
							}
							else /* xxx */{
								int len = substr.length();
								if (string.regionMatches(pos, substr, 0, len)) {
									pos += len;
								}
								else {
									return false;
								}
							}
						}
						else /* last substr */{
							if (substr == null) /* * */{
								return true;
							}
							/* xxx */
							return string.endsWith(substr);
						}
					}

					return true;
				}
				case EQUAL : {
					return string.equals(value2);
				}
				case APPROX : {
					string = approxString(string);
					String string2 = approxString((String) value2);

					return string.equalsIgnoreCase(string2);
				}
				case GREATER : {
					return string.compareTo((String) value2) >= 0;
				}
				case LESS : {
					return string.compareTo((String) value2) <= 0;
				}
			}
			return false;
		}

		private boolean compare_Integer(int operation, int intval, Object value2) {
			int intval2 = Integer.parseInt(((String) value2).trim());
			switch (operation) {
				case SUBSTRING : {
					return false;
				}
				case APPROX :
				case EQUAL : {
					return intval == intval2;
				}
				case GREATER : {
					return intval >= intval2;
				}
				case LESS : {
					return intval <= intval2;
				}
			}
			return false;
		}

		private boolean compare_Long(int operation, long longval, Object value2) {
			long longval2 = Long.parseLong(((String) value2).trim());
			switch (operation) {
				case SUBSTRING : {
					return false;
				}
				case APPROX :
				case EQUAL : {
					return longval == longval2;
				}
				case GREATER : {
					return longval >= longval2;
				}
				case LESS : {
					return longval <= longval2;
				}
			}
			return false;
		}

		private boolean compare_Byte(int operation, byte byteval, Object value2) {
			byte byteval2 = Byte.parseByte(((String) value2).trim());
			switch (operation) {
				case SUBSTRING : {
					return false;
				}
				case APPROX :
				case EQUAL : {
					return byteval == byteval2;
				}
				case GREATER : {
					return byteval >= byteval2;
				}
				case LESS : {
					return byteval <= byteval2;
				}
			}
			return false;
		}

		private boolean compare_Short(int operation, short shortval,
				Object value2) {
			short shortval2 = Short.parseShort(((String) value2).trim());
			switch (operation) {
				case SUBSTRING : {
					return false;
				}
				case APPROX :
				case EQUAL : {
					return shortval == shortval2;
				}
				case GREATER : {
					return shortval >= shortval2;
				}
				case LESS : {
					return shortval <= shortval2;
				}
			}
			return false;
		}

		private boolean compare_Character(int operation, char charval,
				Object value2) {
			char charval2 = (((String) value2).trim()).charAt(0);
			switch (operation) {
				case SUBSTRING : {
					return false;
				}
				case EQUAL : {
					return charval == charval2;
				}
				case APPROX : {
					return (charval == charval2)
							|| (Character.toUpperCase(charval) == Character
									.toUpperCase(charval2))
							|| (Character.toLowerCase(charval) == Character
									.toLowerCase(charval2));
				}
				case GREATER : {
					return charval >= charval2;
				}
				case LESS : {
					return charval <= charval2;
				}
			}
			return false;
		}

		private boolean compare_Boolean(int operation, boolean boolval,
				Object value2) {
			boolean boolval2 = new Boolean(((String) value2).trim())
					.booleanValue();
			switch (operation) {
				case SUBSTRING : {
					return false;
				}
				case APPROX :
				case EQUAL : 
				case GREATER : 
				case LESS : {
					return boolval == boolval2;
				}
			}
			return false;
		}

		private boolean compare_Float(int operation, float floatval,
				Object value2) {
			float floatval2 = Float.parseFloat(((String) value2).trim());
			switch (operation) {
				case SUBSTRING : {
					return false;
				}
				case APPROX :
				case EQUAL : {
					return Float.compare(floatval, floatval2) == 0;
				}
				case GREATER : {
					return Float.compare(floatval, floatval2) >= 0;
				}
				case LESS : {
					return Float.compare(floatval, floatval2) <= 0;
				}
			}
			return false;
		}

		private boolean compare_Double(int operation, double doubleval,
				Object value2) {
			double doubleval2 = Double.parseDouble(((String) value2).trim());
			switch (operation) {
				case SUBSTRING : {
					return false;
				}
				case APPROX :
				case EQUAL : {
					return Double.compare(doubleval, doubleval2) == 0;
				}
				case GREATER : {
					return Double.compare(doubleval, doubleval2) >= 0;
				}
				case LESS : {
					return Double.compare(doubleval, doubleval2) <= 0;
				}
			}
			return false;
		}

		private static final Class[]	constructorType	= new Class[] {String.class};

		private boolean compare_Comparable(int operation, Comparable value1,
				Object value2) {
			Constructor constructor;
			try {
				constructor = value1.getClass().getConstructor(constructorType);
			}
			catch (NoSuchMethodException e) {
				return false;
			}
			try {
				if (!constructor.isAccessible())
					AccessController.doPrivileged(new SetAccessibleAction(
							constructor));
				value2 = constructor
						.newInstance(new Object[] {((String) value2).trim()});
			}
			catch (IllegalAccessException e) {
				return false;
			}
			catch (InvocationTargetException e) {
				return false;
			}
			catch (InstantiationException e) {
				return false;
			}

			switch (operation) {
				case SUBSTRING : {
					return false;
				}
				case APPROX : 
				case EQUAL : {
					return value1.compareTo(value2) == 0;
				}
				case GREATER : {
					return value1.compareTo(value2) >= 0;
				}
				case LESS : {
					return value1.compareTo(value2) <= 0;
				}
			}
			return false;
		}

		private boolean compare_Unknown(int operation, Object value1,
				Object value2) {
			Constructor constructor;
			try {
				constructor = value1.getClass().getConstructor(constructorType);
			}
			catch (NoSuchMethodException e) {
				return false;
			}
			try {
				if (!constructor.isAccessible())
					AccessController.doPrivileged(new SetAccessibleAction(
							constructor));
				value2 = constructor
						.newInstance(new Object[] {((String) value2).trim()});
			}
			catch (IllegalAccessException e) {
				return false;
			}
			catch (InvocationTargetException e) {
				return false;
			}
			catch (InstantiationException e) {
				return false;
			}

			switch (operation) {
				case SUBSTRING : {
					return false;
				}
				case APPROX :
				case EQUAL :
				case GREATER : 
				case LESS : {
					return value1.equals(value2);
				}
			}
			return false;
		}

		/**
		 * Map a string for an APPROX (~=) comparison.
		 * 
		 * This implementation removes white spaces. This is the minimum
		 * implementation allowed by the OSGi spec.
		 * 
		 * @param input Input string.
		 * @return String ready for APPROX comparison.
		 */
		private static String approxString(String input) {
			boolean changed = false;
			char[] output = input.toCharArray();
			int cursor = 0;
			for (int i = 0, length = output.length; i < length; i++) {
				char c = output[i];

				if (Character.isWhitespace(c)) {
					changed = true;
					continue;
				}

				output[cursor] = c;
				cursor++;
			}

			return changed ? new String(output, 0, cursor) : input;
		}

		/**
		 * Parser class for OSGi filter strings. This class parses the complete
		 * filter string and builds a tree of Filter objects rooted at the
		 * parent.
		 */
		private static class Parser {
			private final String	filterstring;
			private final char[]	filterChars;
			private int				pos;

			Parser(String filterstring) {
				this.filterstring = filterstring;
				filterChars = filterstring.toCharArray();
				pos = 0;
			}

			FilterImpl parse() throws InvalidSyntaxException {
				FilterImpl filter;
				try {
					filter = parse_filter(true);
				}
				catch (ArrayIndexOutOfBoundsException e) {
					throw new InvalidSyntaxException(
							"Filter ended abruptly", filterstring); //$NON-NLS-1$
				}

				if (pos != filterChars.length) {
					throw new InvalidSyntaxException(
							"Extraneous trailing characters: " + filterstring.substring(pos), filterstring); //$NON-NLS-1$
				}
				return filter;
			}

			private FilterImpl parse_filter(boolean topLevel)
					throws InvalidSyntaxException {
				FilterImpl filter;
				skipWhiteSpace();

				if (filterChars[pos] != '(') {
					throw new InvalidSyntaxException("Missing '(': "
							+ filterstring.substring(pos), filterstring); //$NON-NLS-1$
				}

				pos++;

				filter = parse_filtercomp(topLevel);

				skipWhiteSpace();

				if (filterChars[pos] != ')') {
					throw new InvalidSyntaxException("Missing ')': "
							+ filterstring.substring(pos), filterstring); //$NON-NLS-1$
				}

				pos++;

				skipWhiteSpace();

				return filter;
			}

			private FilterImpl parse_filtercomp(boolean topLevel)
					throws InvalidSyntaxException {
				skipWhiteSpace();

				char c = filterChars[pos];

				switch (c) {
					case '&' : {
						pos++;
						return parse_and(topLevel);
					}
					case '|' : {
						pos++;
						return parse_or(topLevel);
					}
					case '!' : {
						pos++;
						return parse_not(topLevel);
					}
				}
				return parse_item(topLevel);
			}

			private FilterImpl parse_and(boolean topLevel)
					throws InvalidSyntaxException {
				skipWhiteSpace();

				if (filterChars[pos] != '(') {
					throw new InvalidSyntaxException("Missing '(': "
							+ filterstring.substring(pos), filterstring); //$NON-NLS-1$
				}

				List operands = new ArrayList(10);

				while (filterChars[pos] == '(') {
					FilterImpl child = parse_filter(false);
					operands.add(child);
				}

				return new FilterImpl(topLevel, FilterImpl.AND, null, operands
						.toArray(new FilterImpl[operands.size()]));
			}

			private FilterImpl parse_or(boolean topLevel)
					throws InvalidSyntaxException {
				skipWhiteSpace();

				if (filterChars[pos] != '(') {
					throw new InvalidSyntaxException("Missing '(': "
							+ filterstring.substring(pos), filterstring); //$NON-NLS-1$
				}

				List operands = new ArrayList(10);

				while (filterChars[pos] == '(') {
					FilterImpl child = parse_filter(false);
					operands.add(child);
				}

				return new FilterImpl(topLevel, FilterImpl.OR, null, operands
						.toArray(new FilterImpl[operands.size()]));
			}

			private FilterImpl parse_not(boolean topLevel)
					throws InvalidSyntaxException {
				skipWhiteSpace();

				if (filterChars[pos] != '(') {
					throw new InvalidSyntaxException("Missing '(': "
							+ filterstring.substring(pos), filterstring); //$NON-NLS-1$
				}

				FilterImpl child = parse_filter(false);

				return new FilterImpl(topLevel, FilterImpl.NOT, null, child);
			}

			private FilterImpl parse_item(boolean topLevel)
					throws InvalidSyntaxException {
				String attr = parse_attr();

				skipWhiteSpace();

				switch (filterChars[pos]) {
					case '~' : {
						if (filterChars[pos + 1] == '=') {
							pos += 2;
							return new FilterImpl(topLevel, FilterImpl.APPROX,
									attr, parse_value());
						}
						break;
					}
					case '>' : {
						if (filterChars[pos + 1] == '=') {
							pos += 2;
							return new FilterImpl(topLevel, FilterImpl.GREATER,
									attr, parse_value());
						}
						break;
					}
					case '<' : {
						if (filterChars[pos + 1] == '=') {
							pos += 2;
							return new FilterImpl(topLevel, FilterImpl.LESS,
									attr, parse_value());
						}
						break;
					}
					case '=' : {
						if (filterChars[pos + 1] == '*') {
							int oldpos = pos;
							pos += 2;
							skipWhiteSpace();
							if (filterChars[pos] == ')') {
								return new FilterImpl(topLevel,
										FilterImpl.PRESENT, attr, null);
							}
							pos = oldpos;
						}

						pos++;
						Object string = parse_substring();

						if (string instanceof String) {
							return new FilterImpl(topLevel, FilterImpl.EQUAL,
									attr, string);
						}
						return new FilterImpl(topLevel, FilterImpl.SUBSTRING,
								attr, string);
					}
				}

				throw new InvalidSyntaxException("Invalid operator: "
						+ filterstring.substring(pos), filterstring); //$NON-NLS-1$
			}

			private String parse_attr() throws InvalidSyntaxException {
				skipWhiteSpace();

				int begin = pos;
				int end = pos;

				char c = filterChars[pos];

				while (c != '~' && c != '<' && c != '>' && c != '=' && c != '('
						&& c != ')') {
					pos++;

					if (!Character.isWhitespace(c)) {
						end = pos;
					}

					c = filterChars[pos];
				}

				int length = end - begin;

				if (length == 0) {
					throw new InvalidSyntaxException("Missing attr: "
							+ filterstring.substring(pos), filterstring); //$NON-NLS-1$
				}

				return new String(filterChars, begin, length);
			}

			private String parse_value() throws InvalidSyntaxException {
				StringBuffer sb = new StringBuffer(filterChars.length - pos);

				parseloop: while (true) {
					char c = filterChars[pos];

					switch (c) {
						case ')' : {
							break parseloop;
						}

						case '(' : {
							throw new InvalidSyntaxException("Invalid value: "
									+ filterstring.substring(pos), filterstring); //$NON-NLS-1$
						}

						case '\\' : {
							pos++;
							c = filterChars[pos];
							/* fall through into default */
						}

						default : {
							sb.append(c);
							pos++;
							break;
						}
					}
				}

				if (sb.length() == 0) {
					throw new InvalidSyntaxException("Missing value: "
							+ filterstring.substring(pos), filterstring); //$NON-NLS-1$
				}

				return sb.toString();
			}

			private Object parse_substring() throws InvalidSyntaxException {
				StringBuffer sb = new StringBuffer(filterChars.length - pos);

				List operands = new ArrayList(10);

				parseloop: while (true) {
					char c = filterChars[pos];

					switch (c) {
						case ')' : {
							if (sb.length() > 0) {
								operands.add(sb.toString());
							}

							break parseloop;
						}

						case '(' : {
							throw new InvalidSyntaxException("Invalid value: "
									+ filterstring.substring(pos), filterstring); //$NON-NLS-1$
						}

						case '*' : {
							if (sb.length() > 0) {
								operands.add(sb.toString());
							}

							sb.setLength(0);

							operands.add(null);
							pos++;

							break;
						}

						case '\\' : {
							pos++;
							c = filterChars[pos];
							/* fall through into default */
						}

						default : {
							sb.append(c);
							pos++;
							break;
						}
					}
				}

				int size = operands.size();

				if (size == 0) {
					throw new InvalidSyntaxException("Missing value: "
							+ filterstring.substring(pos), filterstring); //$NON-NLS-1$
				}

				if (size == 1) {
					Object single = operands.get(0);

					if (single != null) {
						return single;
					}
				}

				return operands.toArray(new String[size]);
			}

			private void skipWhiteSpace() {
				for (int length = filterChars.length; (pos < length)
						&& Character.isWhitespace(filterChars[pos]);) {
					pos++;
				}
			}
		}
	}

	/**
	 * This Dictionary is used for case-insensitive key lookup during filter
	 * evaluation. This Dictionary implementation only supports the get
	 * operation using a String key as no other operations are used by the
	 * Filter implementation.
	 */
	private static class CaseInsensitiveDictionary extends Dictionary {
		private final Dictionary	dictionary;
		private final String[]		keys;

		/**
		 * Create a case insensitive dictionary from the specified dictionary.
		 * 
		 * @param dictionary
		 * @throws IllegalArgumentException If <code>dictionary</code> contains
		 *         case variants of the same key name.
		 */
		CaseInsensitiveDictionary(Dictionary dictionary) {
			if (dictionary == null) {
				this.dictionary = null;
				this.keys = new String[0];
				return;
			}
			this.dictionary = dictionary;
			List keyList = new ArrayList(dictionary.size());
			for (Enumeration e = dictionary.keys(); e.hasMoreElements();) {
				Object k = e.nextElement();
				if (k instanceof String) {
					String key = (String) k;
					for (Iterator i = keyList.iterator(); i.hasNext();) {
						if (key.equalsIgnoreCase((String) i.next())) {
							throw new IllegalArgumentException();
						}
					}
					keyList.add(key);
				}
			}
			this.keys = (String[]) keyList.toArray(new String[keyList.size()]);
		}

		public Object get(Object o) {
			String k = (String) o;
			for (int i = 0, length = keys.length; i < length; i++) {
				String key = keys[i];
				if (key.equalsIgnoreCase(k)) {
					return dictionary.get(key);
				}
			}
			return null;
		}

		public boolean isEmpty() {
			throw new UnsupportedOperationException();
		}

		public Enumeration keys() {
			throw new UnsupportedOperationException();
		}

		public Enumeration elements() {
			throw new UnsupportedOperationException();
		}

		public Object put(Object key, Object value) {
			throw new UnsupportedOperationException();
		}

		public Object remove(Object key) {
			throw new UnsupportedOperationException();
		}

		public int size() {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * This Dictionary is used for key lookup from a ServiceReference during
	 * filter evaluation. This Dictionary implementation only supports the get
	 * operation using a String key as no other operations are used by the
	 * Filter implementation.
	 */
	private static class ServiceReferenceDictionary extends Dictionary {
		private final ServiceReference	reference;

		ServiceReferenceDictionary(ServiceReference reference) {
			this.reference = reference;
		}

		public Object get(Object key) {
			if (reference == null) {
				return null;
			}
			return reference.getProperty((String) key);
		}

		public boolean isEmpty() {
			throw new UnsupportedOperationException();
		}

		public Enumeration keys() {
			throw new UnsupportedOperationException();
		}

		public Enumeration elements() {
			throw new UnsupportedOperationException();
		}

		public Object put(Object key, Object value) {
			throw new UnsupportedOperationException();
		}

		public Object remove(Object key) {
			throw new UnsupportedOperationException();
		}

		public int size() {
			throw new UnsupportedOperationException();
		}
	}

	private static class SetAccessibleAction implements PrivilegedAction {
		private final AccessibleObject	accessible;

		SetAccessibleAction(AccessibleObject accessible) {
			this.accessible = accessible;
		}

		public Object run() {
			accessible.setAccessible(true);
			return null;
		}
	}
}
