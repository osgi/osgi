/*
 * Copyright (c) OSGi Alliance (2016, 2017). All Rights Reserved.
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

package org.osgi.impl.service.zigbee.basedriver.configuration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.osgi.service.zigbee.descriptions.ZCLDataTypeDescription;
import org.w3c.dom.Element;

public class ParserUtils {

	/**
	 * Mark an element attribute as MANDATORY
	 */
	public final static boolean	MANDATORY	= true;

	/**
	 * Mark an element attribute as OPTIONAL
	 */
	public final static boolean	OPTIONAL	= false;

	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();

		if ((len % 2) != 0) {
			throw new IllegalArgumentException("The passed argument must contain an even number of hex digits.");
		}
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
					.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	/**
	 * Converts a list like "1, 2, 3" into an array of int [ 1, 2, 3]
	 * 
	 * @param listString
	 * @return the array.
	 */

	public static int[] toArray(String listString) {
		if (listString == null) {
			return new int[0];
		}

		StringTokenizer tokenizer = new StringTokenizer(listString, ",");
		List l = new ArrayList();
		while (tokenizer.hasMoreElements()) {
			l.add(Integer.valueOf(tokenizer.nextToken()));
		}

		int[] array = new int[l.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = ((Integer) l.get(i)).intValue();
		}
		return array;
	}

	public static ZCLDataTypeDescription dataTypeName2dataType(String dataTypeName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
		Class cls = Class.forName("org.osgi.service.zigbee.types." + dataTypeName);

		Class[] classes = {};
		Method method = cls.getMethod("getInstance", classes);
		ZCLDataTypeDescription dataType = (ZCLDataTypeDescription) method
				.invoke(null);

		return dataType;
	}

	public static String getAttribute(Element element, String attributeName, boolean required, final String defaultValue) {
		if (element == null) {
			throw new NullPointerException("element argument cannot be null");
		}

		String attributeValue = element.getAttribute(attributeName);
		if (attributeValue == null || (attributeValue != null && attributeValue.length() == 0)) {
			if (!required) {
				return defaultValue;
			} else {
				throw new RuntimeException("parse error: missing attribute '" + attributeName + "'");
			}
		}

		return attributeValue;
	}

	public static int getAttribute(Element element, String attributeName, boolean required, final int defaultValue) {
		if (element == null) {
			throw new NullPointerException("element argument cannot be null");
		}

		String attributeValue = element.getAttribute(attributeName);
		if (attributeValue == null || (attributeValue != null && attributeValue.length() == 0)) {
			if (!required) {
				return defaultValue;
			} else {
				throw new RuntimeException("parse error: missing attribute " + attributeName);
			}
		}
		if (attributeValue.startsWith("0x")) {
			return Integer.parseInt(attributeValue.substring(2), 16);
		} else {
			return Integer.parseInt(attributeValue);
		}
	}

	public static short getAttribute(Element element, String attributeName, boolean required, final short defaultValue) {
		if (element == null) {
			throw new NullPointerException("element argument cannot be null");
		}

		String attributeValue = element.getAttribute(attributeName);
		if (attributeValue == null || (attributeValue != null && attributeValue.length() == 0)) {
			if (!required) {
				return defaultValue;
			} else {
				throw new RuntimeException("parse error: missing attribute " + attributeName);
			}
		}

		return Short.parseShort(attributeValue);
	}

	public static byte getAttribute(Element element, String attributeName, boolean required, final byte defaultValue) {
		if (element == null) {
			throw new NullPointerException("element argument cannot be null");
		}

		String attributeValue = element.getAttribute(attributeName);
		if (attributeValue == null || (attributeValue != null && attributeValue.length() == 0)) {
			if (!required) {
				return defaultValue;
			} else {
				throw new RuntimeException("parse error: missing attribute " + attributeName);
			}
		}

		return Byte.parseByte(attributeValue);
	}

	public static BigInteger getAttribute(Element element, String attributeName, boolean required, final BigInteger defaultValue) {
		if (element == null) {
			throw new NullPointerException("element argument cannot be null");
		}

		String attributeValue = element.getAttribute(attributeName);
		if (attributeValue == null || (attributeValue != null && attributeValue.length() == 0)) {
			if (!required) {
				return defaultValue;
			} else {
				throw new RuntimeException("parse error: missing attribute " + attributeName);
			}
		}

		return new BigInteger(attributeValue, 16);

	}

	public static boolean getAttribute(Element element, String attributeName, boolean required, boolean defaultValue) {
		if (element == null) {
			throw new NullPointerException("element argument cannot be null");
		}

		String attributeValue = element.getAttribute(attributeName);
		if (attributeValue == null || (attributeValue != null && attributeValue.length() == 0)) {
			if (!required) {
				return defaultValue;
			} else {
				throw new RuntimeException("parse error: missing attribute " + attributeName);
			}
		}

		return Boolean.valueOf(attributeValue).booleanValue();
	}

	public static String getAttribute(Map element, String attributeName, boolean required, final String defaultValue) {
		if (element == null) {
			throw new NullPointerException("element argument cannot be null");
		}

		String attributeValue = (String) element.get(attributeName);
		if (attributeValue == null) {
			if (!required) {
				return defaultValue;
			} else {
				throw new RuntimeException("parse error: missing attribute " + attributeName);
			}
		}

		return attributeValue;
	}

	public static int getAttribute(Map element, String attributeName, boolean required, final int defaultValue) {
		if (element == null) {
			throw new NullPointerException("element argument cannot be null");
		}

		String attributeValue = (String) element.get(attributeName);
		if (attributeValue == null || (attributeValue != null && attributeValue.length() == 0)) {
			if (!required) {
				return defaultValue;
			} else {
				throw new RuntimeException("parse error: missing attribute " + attributeName);
			}
		}

		if (attributeValue.startsWith("0x")) {
			return Integer.parseInt(attributeValue.substring(2), 16);
		} else {
			return Integer.parseInt(attributeValue);
		}
	}

	public static short getAttribute(Map element, String attributeName, boolean required, final short defaultValue) {
		if (element == null) {
			throw new NullPointerException("element argument cannot be null");
		}

		String attributeValue = (String) element.get(attributeName);
		if (attributeValue == null || (attributeValue != null && attributeValue.length() == 0)) {
			if (!required) {
				return defaultValue;
			} else {
				throw new RuntimeException("parse error: missing attribute " + attributeName);
			}
		}
		if (attributeValue.startsWith("0x")) {
			return (short) Integer.parseInt(attributeValue.substring(2), 16);
		} else {
			return Short.parseShort(attributeValue);
		}
	}

	public static byte getAttribute(Map element, String attributeName, boolean required, final byte defaultValue) {
		if (element == null) {
			throw new NullPointerException("element argument cannot be null");
		}

		String attributeValue = (String) element.get(attributeName);
		if (attributeValue == null || (attributeValue != null && attributeValue.length() == 0)) {
			if (!required) {
				return defaultValue;
			} else {
				throw new RuntimeException("parse error: missing attribute " + attributeName);
			}
		}
		if (attributeValue.startsWith("0x")) {
			return (byte) Integer.parseInt(attributeValue.substring(2), 16);
		} else {
			return Byte.parseByte(attributeValue);
		}
	}

	public static BigInteger getAttribute(Map element, String attributeName, boolean required, final BigInteger defaultValue) {
		if (element == null) {
			throw new NullPointerException("element argument cannot be null");
		}

		String attributeValue = (String) element.get(attributeName);
		if (attributeValue == null || (attributeValue != null && attributeValue.length() == 0)) {
			if (!required) {
				return defaultValue;
			} else {
				throw new RuntimeException("parse error: missing attribute " + attributeName);
			}
		}

		return new BigInteger(attributeValue);
	}

	public static boolean getAttribute(Map element, String attributeName, boolean required, boolean defaultValue) {
		if (element == null) {
			throw new NullPointerException("element argument cannot be null");
		}

		String attributeValue = (String) element.get(attributeName);
		if (attributeValue == null || (attributeValue != null && attributeValue.length() == 0)) {
			if (!required) {
				return defaultValue;
			} else {
				throw new RuntimeException("parse error: missing attribute " + attributeName);
			}
		}
		return Boolean.valueOf(attributeValue).booleanValue();
	}

	public static BigInteger getParameter(Map properties, String parameterName, boolean required, BigInteger defaultValue) {
		if (properties == null) {
			throw new NullPointerException("properties argument cannot be null");
		}

		Object value = properties.get(parameterName);

		if (value == null) {
			if (!required) {
				return defaultValue;
			} else {
				throw new RuntimeException("missing required property: " + parameterName);
			}
		} else if (value instanceof BigInteger) {
			return (BigInteger) value;
		} else {
			throw new RuntimeException("expected a BigInteger, got " + value.getClass().getName());
		}
	}

	public static boolean getParameter(Map properties, String parameterName, boolean required, boolean defaultValue) {
		if (properties == null) {
			throw new NullPointerException("properties argument cannot be null");
		}

		Object value = properties.get(parameterName);

		if (value == null) {
			if (!required) {
				return defaultValue;
			} else {
				throw new RuntimeException("missing required property: " + parameterName);
			}
		} else if (value instanceof Boolean) {
			return ((Boolean) value).booleanValue();
		} else {
			throw new RuntimeException("expected a Boolean parameter, got " + value.getClass().getName());
		}
	}

	public static int getParameter(Map properties, String parameterName, boolean required, int defaultValue) {
		if (properties == null) {
			throw new NullPointerException("properties argument cannot be null");
		}

		Object value = properties.get(parameterName);

		if (value == null) {
			if (!required) {
				return defaultValue;
			} else {
				throw new RuntimeException("missing required property: " + parameterName);
			}
		} else if (value instanceof Integer) {
			return ((Integer) value).intValue();
		} else {
			throw new RuntimeException("expected a Integer parameter, got " + value.getClass().getName());
		}
	}

	public static short getParameter(Map properties, String parameterName, boolean required, short defaultValue) {
		if (properties == null) {
			throw new NullPointerException("properties argument cannot be null");
		}

		Object value = properties.get(parameterName);

		if (value == null) {
			if (!required) {
				return defaultValue;
			} else {
				throw new RuntimeException("missing required property: " + parameterName);
			}
		} else if (value instanceof Short) {
			return ((Short) value).shortValue();
		} else {
			throw new RuntimeException("expected a Short parameter, got " + value.getClass().getName());
		}
	}
}
