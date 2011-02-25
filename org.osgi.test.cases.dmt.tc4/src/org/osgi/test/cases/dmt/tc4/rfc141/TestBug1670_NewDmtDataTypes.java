package org.osgi.test.cases.dmt.tc4.rfc141;

import info.dmtree.DmtData;
import info.dmtree.DmtIllegalStateException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigInteger;

import org.osgi.test.support.OSGiTestCase;

public class TestBug1670_NewDmtDataTypes extends OSGiTestCase{
	
	protected void setUp() throws Exception {
		super.setUp();
		System.out.println("setting up");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		System.out.println( "tearing down");
	}
	
	
	/**
	 * tests existence of specified constants for data formats in info.dmtree.DmtData
	 */
	public void testNewFormatConstants() throws Exception {
		checkIntConstant(DmtData.class, "FORMAT_LONG", 0x2000 );
		checkIntConstant(DmtData.class, "FORMAT_UNSIGNED_LONG",0x4000  );
		checkIntConstant(DmtData.class, "FORMAT_DATETIME", 0x8000 );
		checkIntConstant(DmtData.class, "FORMAT_UNSIGNED_INTEGER", 0x10000 );
		checkIntConstant(DmtData.class, "FORMAT_NODE_URI", 0x20000 );
		checkIntConstant(DmtData.class, "FORMAT_HEXBINARY", 0x40000 );
	}
	
	/**
	 * performs several checks with an DmtData instance of FORMAT_LONG
	 */
	public void testFormatLong() throws Exception {
		long l = 2701;
		DmtData data = new DmtData( l );
		try {
			// just testing with the new formats
			data.getUnsignedInteger();
			data.getUnsignedLong();
			data.getDateTime();
			data.getHexBinary();
			data.getNodeUri();
			// ...
			fail( "must not be able to retrieve any format other than FORMAT_LONG");
		} catch (DmtIllegalStateException e) {}
		assertEquals(DmtData.FORMAT_LONG, data.getFormat() );
		long l2 = data.getLong();
		assertEquals( "initial and returned value are not the same", l, l2 );
	}
	
	/**
	 * performs following checks with an DmtData instance of FORMAT_UNSIGNED_INTEGER
	 * - minimum and maximum value
	 * - retrieval with getters of wrong format
	 * - checks reported format
	 * - compares retrieved value with initial one
	 */
	public void testFormatUnsignedInteger() throws Exception {
		// test minimum limit
		String uint = "-3";
		DmtData data = null;
		try {
			data = new DmtData( uint, DmtData.FORMAT_UNSIGNED_INTEGER );
			fail( "DmtData must not accept negative values for FORMAT_UNSIGNED_INTEGER" );
		} catch (IllegalArgumentException e) {}
		
		// test maximum limit
		long max = 2l * Integer.MAX_VALUE ;
		uint = "" + (max + 1);
		try {
			data = new DmtData( uint, DmtData.FORMAT_UNSIGNED_INTEGER );
			fail( "DmtData must not accept values bigger than " + max + " for FORMAT_UNSIGNED_INTEGER" );
		} catch (IllegalArgumentException e) {}
		
		uint = "2701";
		data = new DmtData( uint, DmtData.FORMAT_UNSIGNED_INTEGER );
		try {
			// just testing with the new formats
			data.getLong();
			data.getUnsignedLong();
			data.getDateTime();
			data.getHexBinary();
			data.getNodeUri();
			// ...
			fail( "must not be able to retrieve any format other than FORMAT_UNSIGNED_INTEGER");
		} catch (DmtIllegalStateException e) {}
		assertEquals(DmtData.FORMAT_UNSIGNED_INTEGER, data.getFormat() );
		String uint2 = data.getUnsignedInteger();
		assertEquals( "initial and returned value are not the same", uint, uint2 );
	}

	/**
	 * performs following checks with an DmtData instance of FORMAT_UNSIGNED_LONG
	 * - minimum and maximum value
	 * - retrieval with getters of wrong format
	 * - checks reported format
	 * - compares retrieved value with initial one
	 */
	public void testFormatUnsignedLong() throws Exception {
		// test minimum limit
		String ulong = "-3";
		DmtData data = null;
		try {
			data = new DmtData( ulong, DmtData.FORMAT_UNSIGNED_LONG );
			fail( "DmtData must not accept negative values for FORMAT_UNSIGNED_LONG" );
		} catch (IllegalArgumentException e) {}
		
		// test maximum limit
		BigInteger bdMax = new BigInteger("" + Long.MAX_VALUE).multiply( new BigInteger( "2" ) );
		BigInteger bdMaxPlus1 = bdMax.add(new BigInteger("1"));

		try {
			data = new DmtData( bdMaxPlus1.toString(), DmtData.FORMAT_UNSIGNED_LONG );
			fail( "DmtData must not accept values bigger than " + bdMax + " for FORMAT_UNSIGNED_LONG" );
		} catch (IllegalArgumentException e) {}
		
		ulong = "2701";
		data = new DmtData( ulong, DmtData.FORMAT_UNSIGNED_LONG );
		try {
			// just testing with the new formats
			data.getLong();
			data.getUnsignedInteger();
			data.getDateTime();
			data.getHexBinary();
			data.getNodeUri();
			// ...
			fail( "must not be able to retrieve any format other than FORMAT_UNSIGNED_LONG");
		} catch (DmtIllegalStateException e) {}
		assertEquals(DmtData.FORMAT_UNSIGNED_LONG, data.getFormat() );
		String ulong2 = data.getUnsignedLong();
		assertEquals( "initial and returned value are not the same", ulong, ulong2 );
	}

	/**
	 * performs following checks with an DmtData instance of FORMAT_DATE_TIME
	 * - wrong time format in given string
	 * - retrieval with getters of wrong format
	 * - checks reported format
	 * - compares retrieved value with initial one
	 */
	public void testFormatDateTime() throws Exception {
		DmtData data = null;
		String dt = "20100101";
		try {
			data = new DmtData( dt, DmtData.FORMAT_DATETIME );
			fail( "DmtData must not accept date-only values for FORMAT_DATETIME" );
		} catch (IllegalArgumentException e) {}

		dt = "161601";
		try {
			data = new DmtData( dt, DmtData.FORMAT_DATETIME );
			fail( "DmtData must not accept time-only values for FORMAT_DATETIME" );
		} catch (IllegalArgumentException e) {}
		
		dt = "any string";
		try {
			data = new DmtData( dt, DmtData.FORMAT_DATETIME );
			fail( "DmtData must not accept invalid formatted values for FORMAT_DATETIME" );
		} catch (IllegalArgumentException e) {}
		
		dt = "20100101T102030";
		try {
			data = new DmtData( dt, DmtData.FORMAT_DATETIME );
		} catch (IllegalArgumentException e) {
			fail( "DmtData must not accept valid values like '" + dt + "' for FORMAT_DATETIME" );
		}
		dt = "20100101T102030Z";
		try {
			data = new DmtData( dt, DmtData.FORMAT_DATETIME );
		} catch (IllegalArgumentException e) {
			fail( "DmtData must not accept valid values like '" + dt + "' for FORMAT_DATETIME" );
		}
		try {
			// just testing with the new formats
			data.getLong();
			data.getUnsignedInteger();
			data.getUnsignedLong();
			data.getHexBinary();
			data.getNodeUri();
			// ...
			fail( "must not be able to retrieve any format other than FORMAT_DATETIME");
		} catch (DmtIllegalStateException e) {}
		assertEquals(DmtData.FORMAT_DATETIME, data.getFormat() );
		String dt2 = data.getDateTime();
		assertEquals( "initial and returned value are not the same", dt, dt2 );
	}
	
	
	/**
	 * performs several checks with an DmtData instance of FORMAT_HEXBINARY
	 */
	public void testFormatHexBinary() throws Exception {
		byte[] bytes = "a hexbinary test string".getBytes();
		DmtData data = new DmtData( bytes, DmtData.FORMAT_HEXBINARY );
		try {
			// just testing with the new formats
			data.getLong();
			data.getUnsignedInteger();
			data.getUnsignedLong();
			data.getDateTime();
			data.getNodeUri();
			// ...
			fail( "must not be able to retrieve any format other than FORMAT_HEXBINARY");
		} catch (DmtIllegalStateException e) {}
		assertEquals(DmtData.FORMAT_HEXBINARY, data.getFormat() );
		byte[] bytes2 = data.getHexBinary();
		for (int i = 0; i < bytes2.length; i++) {
			assertEquals( "initial and returned bytes are not the same", bytes[i], bytes2[i] );
		}
	}

	/**
	 * performs several checks with an DmtData instance of FORMAT_NODE_URI
	 */
	public void testFormatNodeUri() throws Exception {
		String uri = "protocol://host:port/path/file";
		DmtData data = new DmtData( uri, DmtData.FORMAT_NODE_URI );
		try {
			// just testing with the new formats
			data.getLong();
			data.getUnsignedInteger();
			data.getUnsignedLong();
			data.getDateTime();
			data.getHexBinary();
			// ...
			fail( "must not be able to retrieve any format other than FORMAT_NODE_URI");
		} catch (DmtIllegalStateException e) {}
		assertEquals(DmtData.FORMAT_NODE_URI, data.getFormat() );
		String uri2 = data.getNodeUri();
		assertEquals( "initial and returned uri values are not the same", uri, uri2 );
	}
	
	
	/**
	 * helper method to check a given class for existence and correct type, modifiers and value of a given field name
	 * @param fieldClass the class to be checked
	 * @param fieldName the name of the field
	 * @param value the expected value of the field
	 */
	private void checkIntConstant( Class fieldClass, String fieldName, int value ) {
		
		try {
			Field f = fieldClass.getField(fieldName);
			assertTrue(Modifier.isPublic(f.getModifiers()));
			assertTrue(Modifier.isStatic(f.getModifiers()));
			assertTrue(Modifier.isFinal(f.getModifiers()));
			assertEquals(fieldName, value, f.getInt(null));
		}
		catch (NoSuchFieldException e) {
			fail("missing field: " + fieldName, e);
		}
		catch (IllegalAccessException e) {
			fail("bad field: " + fieldName, e);
		}
	}
}
