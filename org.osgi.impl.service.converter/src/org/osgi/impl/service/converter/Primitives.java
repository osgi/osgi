package org.osgi.impl.service.converter;

import java.io.*;
import java.lang.reflect.*;
import java.math.*;
import java.util.*;
import java.util.regex.*;

public class Primitives {
	final static IdentityHashMap<Class<?>,Method> map = new IdentityHashMap<Class<?>, Method>();
	
	
	public static Boolean from(Boolean dummy, Byte v) {
		return v != 0;
	}

	public static Boolean from(Boolean dummy, Short v) {
		return v != 0;
	}

	public static Boolean from(Boolean dummy, Character v) {
		return v != 0;
	}

	public static Boolean from(Boolean dummy, Integer v) {
		return v != 0;
	}

	public static Boolean from(Boolean dummy, Long v) {
		return v != 0;
	}

	public static Boolean from(Boolean dummy, Float v) {
		return v != 0;
	}

	public static Boolean from(Boolean dummy, Double v) {
		return v != 0;
	}

	public static Boolean from(Boolean dummy, BigInteger v) {
		return !v.equals(BigInteger.ZERO);
	}

	public static Boolean from(Boolean dummy, BigDecimal v) {
		return !v.equals(BigDecimal.ZERO);
	}

	public static Boolean from(Boolean dummy, String v) {
		v = v.trim();
		return (v.equalsIgnoreCase("TRUE") || v.equalsIgnoreCase("YES") ||v.equalsIgnoreCase("ON"));
	}

	public static Byte from(Byte dummy, Boolean v) {
		return (byte) (v ? 1 : 0);
	}

	public static Byte from(Byte dummy, Short v) {
		check(v, Byte.MIN_VALUE, Byte.MAX_VALUE);
		return v.byteValue();
	}

	public static Byte from(Byte dummy, Character v) {
		check(v, Byte.MIN_VALUE, Byte.MAX_VALUE);
		return (byte) v.charValue();
	}

	public static Byte from(Byte dummy, Integer v) {
		check(v, Byte.MIN_VALUE, Byte.MAX_VALUE);
		return v.byteValue();
	}

	public static Byte from(Byte dummy, Long v) {
		check(v, Byte.MIN_VALUE, Byte.MAX_VALUE);
		return v.byteValue();
	}

	public static Byte from(Byte dummy, Float v) {
		check(v, Byte.MIN_VALUE, Byte.MAX_VALUE);
		return v.byteValue();
	}

	public static Byte from(Byte dummy, Double v) {
		check(v, Byte.MIN_VALUE, Byte.MAX_VALUE);
		return v.byteValue();
	}

	
	public static Byte from(Byte dummy, BigInteger v) {
		return from(dummy,v.toString());
	}

	public static Byte from(Byte dummy, BigDecimal v) {
		return from(dummy,v.toString());
	}

	public static Byte from(Byte dummy, String v) {
		return Byte.parseByte(v.trim());
	}

	// Shorts
	final static Short SHORT_ONE = 1;
	final static Short SHORT_ZERO = 0;
	
	public static Short from(Short dummy, Boolean v) {
		return v ? SHORT_ONE : SHORT_ZERO;
	}

	public static Short from(Short dummy, Byte v) {
		return v.shortValue();
	}

	public static Short from(Short dummy, Character v) {
		check(v, Short.MIN_VALUE, Short.MAX_VALUE);
		return Short.valueOf((short)v.charValue());
	}

	public static Short from(Short dummy, Integer v) {
		check(v, Short.MIN_VALUE, Short.MAX_VALUE);
		return v.shortValue();
	}

	public static Short from(Short dummy, Long v) {
		check(v, Short.MIN_VALUE, Short.MAX_VALUE);
		return v.shortValue();
	}

	public static Short from(Short dummy, Float v) {
		check(v, Short.MIN_VALUE, Short.MAX_VALUE);
		return v.shortValue();
	}

	public static Short from(Short dummy, Double v) {
		check(v, Short.MIN_VALUE, Short.MAX_VALUE);
		return v.shortValue();
	}

	public static Short from(Short dummy, BigInteger v) {
		return from(dummy, v.toString());
	}

	public static Short from(Short dummy, BigDecimal v) {
		return from(dummy, v.toString());
	}
	
	public static Short from(Short dummy, String v) {
		return new Short(v.trim());
	}

	
	// Characters
	final static Character CHAR_ZERO = 0;
	final static Character CHAR_ONE = 1;
	
	public static Character from(Character dummy, Boolean v) {
		return v ? 'T' : 'V';
	}

	public static Character from(Character dummy, Byte v) {
		check(v, Character.MIN_VALUE, Character.MAX_VALUE);
		return (char)v.byteValue();
	}

	public static Character from(Character dummy, Short v) {
		check(v, Character.MIN_VALUE, Character.MAX_VALUE);
		return (char) v.shortValue();
	}

	public static Character from(Character dummy, Integer v) {
		check(v, Character.MIN_VALUE, Character.MAX_VALUE);
		return(char) v.shortValue();
	}

	public static Character from(Character dummy, Long v) {
		check(v, Character.MIN_VALUE, Character.MAX_VALUE);
		return (char) v.shortValue();
	}

	public static Character from(Character dummy, Float v) {
		check(v, Character.MIN_VALUE, Character.MAX_VALUE);
		return (char) v.shortValue();
	}

	public static Character from(Character dummy, Double v) {
		check(v, Character.MIN_VALUE, Character.MAX_VALUE);
		return (char) v.shortValue();
	}

	public static Character from(Character dummy, BigInteger v) {
		return from(dummy,v.toString());
	}

	public static Character from(Character dummy, BigDecimal v) {
		return from(dummy,v.toString());
	}
	
	public static Character from(Character dummy, String s) {
		int n = Integer.parseInt( s.trim());
		check(n, Character.MIN_VALUE, Character.MAX_VALUE);
		return new Character((char)n);
	}

	
	// ints
	final static Integer INT_ONE = 1;
	final static Integer INT_ZERO = 0;
	
	public static Integer from(Integer dummy, Boolean v) {
		return v ? INT_ONE : INT_ZERO;
	}

	public static Integer from(Integer dummy, Byte v) {
		return v.intValue();
	}

	public static Integer from(Integer dummy, Short v) {
		return v.intValue();
	}

	public static Integer from(Integer dummy, Character v) {
		return (int) v.charValue();
	}

	public static Integer from(Integer dummy, Integer v) {
		return v.intValue();
	}

	public static Integer from(Integer dummy, Long v) {
		check(v, Integer.MIN_VALUE, Integer.MAX_VALUE);
		return v.intValue();
	}

	public static Integer from(Integer dummy, Float v) {
		check(v, Integer.MIN_VALUE, Integer.MAX_VALUE);
		return v.intValue();
	}

	public static Integer from(Integer dummy, Double v) {
		check(v, Integer.MIN_VALUE, Integer.MAX_VALUE);
		return v.intValue();
	}

	public static Integer from(Integer dummy, BigInteger v) {
		return from(dummy, v.toString());
	}
	
	public static Integer from(Integer dummy, BigDecimal v) {
		return from(dummy, v.toString());
	}
	
	public static Integer from(Integer dummy, String v) {
		return new Integer(v.trim());
	}

	// longs
	final static Long LONG_ONE = 1L;
	final static Long LONG_ZERO = 0L;
	public static Long from(Long dummy, Boolean v) {
		return v ? LONG_ONE : LONG_ZERO;
	}

	public static Long from(Long dummy, Byte v) {
		return v.longValue();
	}

	public static Long from(Long dummy, Short v) {
		return v.longValue();
	}

	public static Long from(Long dummy, Character v) {
		return (long) v.charValue();
	}

	public static Long from(Long dummy, Integer v) {
		return v.longValue();
	}

	public static Long from(Long dummy, Long v) {
		return v.longValue();
	}

	public static Long from(Long dummy, Float v) {
		check(v, Long.MIN_VALUE, Long.MAX_VALUE);
		return v.longValue();
	}

	public static Long from(Long dummy, Double v) {
		check(v, Long.MIN_VALUE, Long.MAX_VALUE);
		return v.longValue();
	}

	public static Long from(Long dummy, BigInteger v) {
		return from(dummy,v.toString());
	}

	public static Long from(Long dummy, BigDecimal v) {
		return from(dummy,v.toString());
	}

	public static Long from(Long dummy, String v) {
		return new Long(v.trim());
	}

	
	// Floats
	final static Float FLOAT_ONE = (float) 1;
	final static Float FLOAT_ZERO = (float) 0;
	
	public static Float from(Float dummy, Boolean v) {
		return v ? FLOAT_ONE : FLOAT_ZERO;
	}

	public static Float from(Float dummy, Byte v) {
		return v.floatValue();
	}

	public static Float from(Float dummy, Short v) {
		return v.floatValue();
	}

	public static Float from(Float dummy, Character v) {
		return (float) v.charValue();
	}

	public static Float from(Float dummy, Integer v) {
		return v.floatValue();
	}

	public static Float from(Float dummy, Long v) {
		return v.floatValue();
	}

	public static Float from(Float dummy, Float v) {
		return v;
	}

	public static Float from(Float dummy, Double v) {
		check(v, Float.MIN_VALUE, Float.MAX_VALUE);
		return v.floatValue();
	}

	public static Float from(Float dummy, BigInteger v) {
		return from(dummy,v.toString());
	}

	public static Float from(Float dummy, BigDecimal v) {
		return from(dummy,v.toString());
	}

	public static Float from(Float dummy, String v) {
		return new Float(v.trim());
	}

	// Doubles
	final static Double DOUBLE_ZERO = Double.valueOf(0);
	final static Double DOUBLE_ONE = Double.valueOf(1);
	public static Double from(Double dumy, Boolean v) {
		return v ? DOUBLE_ONE : DOUBLE_ZERO;
	}

	public static Double from(Double dummy, Byte v) {
		return v.doubleValue();
	}

	public static Double from(Double dummy, Short v) {
		return v.doubleValue();
	}

	public static Double from(Double dummy, Character v) {
		return Double.valueOf( v.charValue());
	}

	public static Double from(Double dummy, Integer v) {
		return v.doubleValue();
	}

	public static Double from(Double dummy, Long v) {
		return v.doubleValue();
	}

	public static Double from(Double dummy, Float v) {
		return v.doubleValue();
	}

	public static Double from(Double dummy, BigInteger v) {
		return from(dummy, v.toString());
	}

	public static Double from(Double dummy, BigDecimal v) {
		return from(dummy, v.toString());
	}

	public static Double from(Double dummy, String s ) {
		return new Double(s.trim());
	}
	
	public static BigInteger from(BigInteger dummy, Byte v) {
		BigDecimal bd = new BigDecimal(v);
		return bd.toBigInteger();
	}

	public static BigInteger from(BigInteger dummy, Short v) {
		BigDecimal bd = new BigDecimal(v);
		return bd.toBigInteger();
	}

	public static BigInteger from(BigInteger dummy, Character v) {
		BigDecimal bd = new BigDecimal((int)v);
		return bd.toBigInteger();
	}

	public static BigInteger from(BigInteger dummy, Integer v) {
		BigDecimal bd = new BigDecimal(v);
		return bd.toBigInteger();
	}

	public static BigInteger from(BigInteger dummy, Long v) {
		BigDecimal bd = new BigDecimal(v);
		return bd.toBigInteger();
	}

	public static BigInteger from(BigInteger dummy, Float v) {
		BigDecimal bd = new BigDecimal(v);
		return bd.toBigInteger();
	}

	public static BigInteger from(BigInteger dummy, Double v) {
		BigDecimal bd = new BigDecimal(v);
		return bd.toBigInteger();
	}

	public static BigInteger from(BigInteger dummy, BigDecimal v) {
		return v.toBigInteger();
	}

	public static BigInteger from(BigInteger dummy, String v) {
		return new BigInteger( v.trim());
	}

	
	public static BigDecimal from(BigDecimal dummy, Byte v) {
		return new BigDecimal(v);
	}

	public static BigDecimal from(BigDecimal dummy, Short v) {
		return new BigDecimal(v);
	}

	public static BigDecimal from(BigDecimal dummy, Character v) {
		return new BigDecimal((int)v);
	}

	public static BigDecimal from(BigDecimal dummy, Integer v) {
		return new BigDecimal(v);
	}

	public static BigDecimal from(BigDecimal dummy, Long v) {
		return new BigDecimal(v);
	}

	public static BigDecimal from(BigDecimal dummy, Float v) {
		return new BigDecimal(v);
	}

	public static BigDecimal from(BigDecimal dummy, Double v) {
		return new BigDecimal(v);
	}

	public static BigDecimal from(BigDecimal dummy, BigInteger v) {
		return new BigDecimal(v);
	}


	public static BigDecimal from(BigDecimal dummy, String v) {
		return new BigDecimal( v.trim());
	}


	public static Locale from(Locale dummy, String v ) {
		String parts[] = v.split("_");
		switch (parts.length) {
			case 1 :
				return new Locale(parts[0]);
			case 2 :
				return new Locale(parts[0], parts[1]);
			case 3 :
				return new Locale(parts[0], parts[1], parts[2]);
		}
		return null;
	}
	
	public static Pattern from(Pattern dummy, String v ) {
		return Pattern.compile(v);
	}

	public static Properties from(Properties dummy, String v ) throws Exception {
		Properties p = new Properties();
		byte[] encoded = v.getBytes("ISO-8859-1");
		ByteArrayInputStream in = new ByteArrayInputStream(encoded);
		p.load(in);
		in.close();
		return p;
	}

	@SuppressWarnings("unchecked")
	public static <S,T> T convert( Class<T> target, S source) throws Exception {		
		Method m = Primitives.class.getMethod("from", target, source.getClass());
		return (T) m.invoke(null, null, source);
	}
	
	
	
	static void check(long v, long min, long max) {
		if (v < min)
			throw new IllegalArgumentException("Value " + v
					+ "is too small for conversion, needs to be >= " + min);
		if (v > max)
			throw new IllegalArgumentException("Value " + v
					+ "is too big for conversion, needs to be <= " + max);
	}
	static void check(double v, double min, double max) {
		if (v <= -max)
			throw new IllegalArgumentException("Value " + v
					+ "is too small for conversion, needs to be >= " + min);
		if (v > max)
			throw new IllegalArgumentException("Value " + v
					+ "is too big for conversion, needs to be <= " + max);
	}
}
