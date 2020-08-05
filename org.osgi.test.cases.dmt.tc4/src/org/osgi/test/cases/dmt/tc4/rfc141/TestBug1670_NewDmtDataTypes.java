package org.osgi.test.cases.dmt.tc4.rfc141;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtIllegalStateException;
import org.osgi.test.support.OSGiTestCase;

public class TestBug1670_NewDmtDataTypes extends OSGiTestCase{
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		System.out.println("setting up");
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		System.out.println( "tearing down");
	}
	
	
	/**
	 * tests existence of specified constants for data formats in org.osgi.service.dmt.DmtData
	 */
	public void testNewFormatConstants() throws Exception {
		checkIntConstant(DmtData.class, "FORMAT_LONG", 0x2000 );
		checkIntConstant(DmtData.class, "FORMAT_DATE_TIME", 0x4000 );
	}
	
	/**
	 * performs several checks with an DmtData instance of FORMAT_LONG
	 */
	public void testFormatLong() throws Exception {
		long l = 2701;
		DmtData data = new DmtData( l );
		try {
			// just testing with the new formats
			data.getDateTime();
			// ...
			fail( "must not be able to retrieve any format other than FORMAT_LONG");
		} catch (DmtIllegalStateException e) {}
		assertEquals(DmtData.FORMAT_LONG, data.getFormat() );
		long l2 = data.getLong();
		assertEquals( "initial and returned value are not the same", l, l2 );
	}
	
//	/**
//	 * performs following checks with an DmtData instance of FORMAT_UNSIGNED_INTEGER
//	 * - minimum and maximum value
//	 * - retrieval with getters of wrong format
//	 * - checks reported format
//	 * - compares retrieved value with initial one
//	 */
//	public void testFormatUnsignedInteger() throws Exception {
//		// test minimum limit
//		String uint = "-3";
//		DmtData data = null;
//		try {
//			data = new DmtData( uint, DmtData.FORMAT_UNSIGNED_INTEGER );
//			fail( "DmtData must not accept negative values for FORMAT_UNSIGNED_INTEGER" );
//		} catch (IllegalArgumentException e) {}
//		
//		// test maximum limit
//		long max = 2l * Integer.MAX_VALUE ;
//		uint = "" + (max + 1);
//		try {
//			data = new DmtData( uint, DmtData.FORMAT_UNSIGNED_INTEGER );
//			fail( "DmtData must not accept values bigger than " + max + " for FORMAT_UNSIGNED_INTEGER" );
//		} catch (IllegalArgumentException e) {}
//		
//		uint = "2701";
//		data = new DmtData( uint, DmtData.FORMAT_UNSIGNED_INTEGER );
//		try {
//			// just testing with the new formats
//			data.getLong();
//			data.getUnsignedLong();
//			data.getDateTime();
//			data.getHexBinary();
//			data.getNodeUri();
//			// ...
//			fail( "must not be able to retrieve any format other than FORMAT_UNSIGNED_INTEGER");
//		} catch (DmtIllegalStateException e) {}
//		assertEquals(DmtData.FORMAT_UNSIGNED_INTEGER, data.getFormat() );
//		String uint2 = data.getUnsignedInteger();
//		assertEquals( "initial and returned value are not the same", uint, uint2 );
//	}

//	/**
//	 * performs following checks with an DmtData instance of FORMAT_UNSIGNED_LONG
//	 * - minimum and maximum value
//	 * - retrieval with getters of wrong format
//	 * - checks reported format
//	 * - compares retrieved value with initial one
//	 */
//	public void testFormatUnsignedLong() throws Exception {
//		// test minimum limit
//		String ulong = "-3";
//		DmtData data = null;
//		try {
//			data = new DmtData( ulong, DmtData.FORMAT_UNSIGNED_LONG );
//			fail( "DmtData must not accept negative values for FORMAT_UNSIGNED_LONG" );
//		} catch (IllegalArgumentException e) {}
//		
//		// test maximum limit
//		BigInteger bdMax = new BigInteger("" + Long.MAX_VALUE).multiply( new BigInteger( "2" ) );
//		BigInteger bdMaxPlus1 = bdMax.add(new BigInteger("1"));
//
//		try {
//			data = new DmtData( bdMaxPlus1.toString(), DmtData.FORMAT_UNSIGNED_LONG );
//			fail( "DmtData must not accept values bigger than " + bdMax + " for FORMAT_UNSIGNED_LONG" );
//		} catch (IllegalArgumentException e) {}
//		
//		ulong = "2701";
//		data = new DmtData( ulong, DmtData.FORMAT_UNSIGNED_LONG );
//		try {
//			// just testing with the new formats
//			data.getLong();
//			data.getUnsignedInteger();
//			data.getDateTime();
//			data.getHexBinary();
//			data.getNodeUri();
//			// ...
//			fail( "must not be able to retrieve any format other than FORMAT_UNSIGNED_LONG");
//		} catch (DmtIllegalStateException e) {}
//		assertEquals(DmtData.FORMAT_UNSIGNED_LONG, data.getFormat() );
//		String ulong2 = data.getUnsignedLong();
//		assertEquals( "initial and returned value are not the same", ulong, ulong2 );
//	}

	/**
	 * performs following checks with an DmtData instance of FORMAT_DATE_TIME
	 * - wrong time format in given string
	 * - retrieval with getters of wrong format
	 * - checks reported format
	 * - compares retrieved value with initial one
	 * 
	 * TODO test is against string, mst be against Date object
	 */
	public void testFormatDateTime() throws Exception {
		DmtData data = null;
		String dt = "20100101T102030";
		try {
			data = new DmtData( dt, DmtData.FORMAT_DATE_TIME );
			fail( "DmtData(String,int) must not accept FORMAT_DATE_TIME format" );
		} catch (IllegalArgumentException e) {
		}
		dt = "20100101T102030Z";
		try {
			data = new DmtData( dt, DmtData.FORMAT_DATE_TIME );
			fail( "DmtData(String,int) must not accept FORMAT_DATE_TIME format" );
		} catch (IllegalArgumentException e) {
		}

		Date dt1 = new Date();
		try {
			data = new DmtData(dt1);
		} catch (IllegalArgumentException e) {
			fail( "DmtData(Date) must not throw IllegalArgumentException" );
		}
		try {
			// just testing with the new formats
			data.getLong();
			// ...
			fail( "must not be able to retrieve any format other than FORMAT_DATE_TIME");
		} catch (DmtIllegalStateException e) {}
		assertEquals(DmtData.FORMAT_DATE_TIME, data.getFormat() );
		Date dt2 = data.getDateTime();
		assertEquals( "initial and returned value are not the same", dt1, dt2 );
	}
	
	
//	/**
//	 * performs several checks with an DmtData instance of FORMAT_HEX_BINARY
//	 */
//	public void testFormatHexBinary() throws Exception {
//		byte[] bytes = "a hexbinary test string".getBytes();
//		DmtData data = new DmtData( bytes, DmtData.FORMAT_HEX_BINARY );
//		try {
//			// just testing with the new formats
//			data.getLong();
//			data.getUnsignedInteger();
//			data.getUnsignedLong();
//			data.getDateTime();
//			data.getNodeUri();
//			// ...
//			fail( "must not be able to retrieve any format other than FORMAT_HEX_BINARY");
//		} catch (DmtIllegalStateException e) {}
//		assertEquals(DmtData.FORMAT_HEX_BINARY, data.getFormat() );
//		byte[] bytes2 = data.getHexBinary();
//		for (int i = 0; i < bytes2.length; i++) {
//			assertEquals( "initial and returned bytes are not the same", bytes[i], bytes2[i] );
//		}
//	}
//
//	/**
//	 * performs several checks with an DmtData instance of FORMAT_NODE_URI
//	 */
//	public void testFormatNodeUri() throws Exception {
//		String uri = "protocol://host:port/path/file";
//		DmtData data = new DmtData( uri, DmtData.FORMAT_NODE_URI );
//		try {
//			// just testing with the new formats
//			data.getLong();
//			data.getUnsignedInteger();
//			data.getUnsignedLong();
//			data.getDateTime();
//			data.getHexBinary();
//			// ...
//			fail( "must not be able to retrieve any format other than FORMAT_NODE_URI");
//		} catch (DmtIllegalStateException e) {}
//		assertEquals(DmtData.FORMAT_NODE_URI, data.getFormat() );
//		String uri2 = data.getNodeUri();
//		assertEquals( "initial and returned uri values are not the same", uri, uri2 );
//	}
	
	
	/**
	 * helper method to check a given class for existence and correct type, modifiers and value of a given field name
	 * @param fieldClass the class to be checked
	 * @param fieldName the name of the field
	 * @param value the expected value of the field
	 */
	private void checkIntConstant(Class< ? > fieldClass, String fieldName,
			int value) {
		
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
