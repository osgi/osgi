package org.osgi.impl.service.upnp.cp.util;

import java.text.*;
import java.util.*;

public abstract class TypeConverter {
	private String	typeName_;

	public TypeConverter(String typeName) {
		typeName_ = typeName;
	}

	protected String getType() {
		return typeName_;
	}

	public Object convertToJavaType(String value)
			throws WrongSoapValueException, IllegalArgumentException, Exception {
		if (value == null || value.equals("")) // sanity check
			throw new IllegalArgumentException();
		Object o = null;
		try {
			o = convertToJavaTypeBySubchild(value);
		}
		catch (NumberFormatException e) {
			throw new WrongSoapValueException("Original message: "
					+ e.getMessage() + "\n" + "Details: Cannot convert '"
					+ value + "' into type '" + getType() + "' ");
		}
		// other exception e.g. IndexOutOfRangeException will be directly thrown
		// to a callee
		return o;
	}

	abstract public String convertToString(Object value)
			throws IllegalArgumentException;

	abstract protected Object convertToJavaTypeBySubchild(String value)
			throws Exception;
}

class IntegerConverter extends TypeConverter {
	public IntegerConverter(String typeName) {
		super(typeName);
	}

	public Object convertToJavaTypeBySubchild(String value) throws Exception {
		return new Integer(value);
	}

	public String convertToString(Object value) throws IllegalArgumentException {
		if (value instanceof Integer)
			return value.toString();
		throw new IllegalArgumentException();
	}
}

class LongConverter extends TypeConverter {
	public LongConverter(String typeName) {
		super(typeName);
	}

	public Object convertToJavaTypeBySubchild(String value) throws Exception {
		return new Long(value);
	}

	public String convertToString(Object value) throws IllegalArgumentException {
		if (value instanceof Long)
			return value.toString();
		throw new IllegalArgumentException();
	}
}

class FloatConverter extends TypeConverter {
	public FloatConverter(String typeName) {
		super(typeName);
	}

	public Object convertToJavaTypeBySubchild(String value) throws Exception {
		return new Float(value);
	}

	public String convertToString(Object value) throws IllegalArgumentException {
		if (value instanceof Float)
			return value.toString();
		throw new IllegalArgumentException();
	}
}

class DoubleConverter extends TypeConverter {
	public DoubleConverter(String typeName) {
		super(typeName);
	}

	public Object convertToJavaTypeBySubchild(String value) throws Exception {
		return new Double(value);
	}

	public String convertToString(Object value) throws IllegalArgumentException {
		if (value instanceof Double) {
			return value.toString();
		}
		throw new IllegalArgumentException();
	}
}
/**
 * 
 * Same as DoubleConverter but no more than 14 digits to the left of the decimal
 * point and no more than 4 to the right.
 * 
 * if there are more than 14 digits to the left of point, it throws
 * IllegalArgumentException if there are more than 4 digits to the right, it
 * just throw the rest away
 *  
 */

class Fixed144Converter extends DoubleConverter {
	public Fixed144Converter(String typeName) {
		super(typeName);
	}

	public String convertToString(Object value) throws IllegalArgumentException {
		if (value instanceof Double) {
			double doubleValue = ((Double) value).doubleValue();
			long leftOfPoint = (long) doubleValue;
			int rightOfPoint = (int) ((doubleValue - (double) leftOfPoint) * 10000);
			if (Long.toString(leftOfPoint).length() > 14)
				throw new IllegalArgumentException(
						"There should be no more than 14 digits to the left of point but value was "
								+ value);
			if (rightOfPoint == 0)
				return Long.toString(leftOfPoint);
			// truncate padding 0 if there is any
			for (int i = 0; i < 4; i++) {
				if (rightOfPoint % 10 == 0)
					rightOfPoint = rightOfPoint / 10;
				else
					break;
			}
			return leftOfPoint + "." + rightOfPoint;
		}
		throw new IllegalArgumentException();
	}
}

class CharacterConverter extends TypeConverter {
	public CharacterConverter(String typeName) {
		super(typeName);
	}

	public Object convertToJavaTypeBySubchild(String value) throws Exception {
		return new Character(value.charAt(0));
	}

	public String convertToString(Object value) throws IllegalArgumentException {
		if (value instanceof Character)
			return value.toString();
		throw new IllegalArgumentException();
	}
}

class StringConverter extends TypeConverter {
	public StringConverter(String typeName) {
		super(typeName);
	}

	public Object convertToJavaTypeBySubchild(String value) throws Exception {
		return value;
	}

	public String convertToString(Object value) throws IllegalArgumentException {
		if (value instanceof String)
			return (String) value;
		throw new IllegalArgumentException();
	}
}
/**
 * REDTAG: does not work perfectly (written on 2003-04-02)
 */

class ISO8601Converter extends TypeConverter {
	private String				formatStrings[]	= {
												// see
												// http://www.cl.cam.ac.uk/~mgk25/iso-time.html
												// for ISO 8601
												"yyyy-MM-dd", "yyyyMMdd",
			"yy-MM-dd", "yyMMdd", "yyyy-MM", "yyyyMM", "yyyy-'W'ww",
			"yyyy'W'ww",
			// REDTAG: don't know how to solve this... 1997-W01-2 or 1997W012
			// yyyy-'W'ww-? or yyyy'W'ww? ??
			"yy'W'ww", "yyyy-DDD", "yyyyDDD",	};
	private SimpleDateFormat[]	dateFormatters	= new SimpleDateFormat[formatStrings.length];

	public ISO8601Converter(String typeName) {
		super(typeName);
		for (int i = 0; i < formatStrings.length; i++) {
			dateFormatters[i] = (SimpleDateFormat) DateFormat
					.getDateTimeInstance();
			dateFormatters[i].applyPattern(formatStrings[i]);
		}
	}

	public Object convertToJavaTypeBySubchild(String value) throws Exception {
		for (int i = 0; i < formatStrings.length; i++) {
			Date date = dateFormatters[i].parse(value);
			return date;
		}
		throw new WrongSoapValueException();
	}

	public String convertToString(Object value) throws IllegalArgumentException {
		if (value instanceof Date) {
			Date date = (Date) value;
			int year = date.getYear() + 1900;
			int month = date.getMonth() + 1;
			int day = date.getDay();
			return Integer.toString(year) + "-"
					+ ConverterUtil.get00to99(month) + "-"
					+ ConverterUtil.get00to99(day);
		}
		throw new IllegalArgumentException();
	}
}
/**
 * @author hsyoon
 * 
 * Allocates a Boolean object representing the value true if the string argument
 * is not null and is equal, ignoring case, to the string "true". Otherwise,
 * allocate a Boolean object representing the value false.
 */

class BooleanConverter extends TypeConverter {
	public BooleanConverter(String typeName) {
		super(typeName);
	}

	public Object convertToJavaTypeBySubchild(String value) throws Exception {
		return new Boolean(value);
	}

	public String convertToString(Object value) throws IllegalArgumentException {
		if (value instanceof Boolean)
			return value.toString();
		throw new IllegalArgumentException();
	}
}
/**
 * if value is "" then it returns byte[] with its length = 0 if value has type
 * of "33-3a-5a", it return byte[] containing {33, 3a, 5a}
 */

class BinHexConverter extends TypeConverter {
	public BinHexConverter(String typeName) {
		super(typeName);
	}

	public Object convertToJavaTypeBySubchild(String value) throws Exception {
		StringTokenizer tokenizer = new StringTokenizer(value, "-");
		int count = tokenizer.countTokens();
		byte[] returnBytes = new byte[count];
		for (int i = 0; tokenizer.hasMoreTokens(); i++) {
			String token = (String) tokenizer.nextElement();
			// the length of token should be 2
			if (token.length() != 2)
				throw new WrongSoapValueException("the length of token '"
						+ token + "' should be 2 or more");
			returnBytes[i] = Byte.parseByte(token, 16);
		}
		return returnBytes;
	}

	public String convertToString(Object value) throws IllegalArgumentException {
		if (value instanceof byte[]) {
			byte[] bytes = (byte[]) value;
			StringBuffer strBuf = new StringBuffer(ConverterUtil
					.get00toFF(bytes[0]));
			for (int i = 1; i < bytes.length; i++) {
				strBuf.append("-");
				strBuf.append(ConverterUtil.get00toFF(bytes[i]));
			}
			return strBuf.toString();
		}
		throw new IllegalArgumentException();
	}
}

class ConverterUtil {
	public static String get00to99(int value) {
		return value < 10 ? "0" + Integer.toString(value) : Integer
				.toString(value);
	}

	public static String get00toFF(int value) {
		return value < 0x10 ? "0" + Integer.toString(value, 16) : Integer
				.toString(value, 16);
	}
}