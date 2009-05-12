/*
 * Copyright (c) The OSGi Alliance (2004). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

/* REVISION HISTORY:
 *
 * Date         Author(s)
 * CR           Headline
 * ===========  ==============================================================
 * 26/01/2005   Andre Assad
 * 1            Implement TCK
 * ===========  ==============================================================
 * 14/02/2005   Leonardo Barros
 * 1            Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-002)
 */

package org.osgi.test.cases.dmt.tc1.tbc.DmtData;

import java.util.Arrays;

import org.osgi.test.cases.dmt.tc1.tbc.DmtTestControl;

/**
 * 
 * This class contains all the test methods for all the DmtData constructors.
 */
public class DmtData extends DmtTestControl {
	/**
	 * This method asserts that when a DmtData(byte[],true) is created, the format is
	 * DmtData.FORMAT_BASE64, DmtData.getSize() and DmtData.getBase64() return the expected value
	 * 
	 * @spec DmtData.DmtData(byte[],boolean)
	 */
	public void testDmtData001() {
		try {		
			log("#testDmtData001");
			byte[] value = new byte[] { 1, 10, 127 };
			String expectedValue= "01 0A 7F";
			info.dmtree.DmtData data  = new info.dmtree.DmtData(value,true);
			assertEquals("Asserting DmtData.FORMAT_BASE64",
					info.dmtree.DmtData.FORMAT_BASE64, data.getFormat());
			
			assertTrue("Asserting the value", Arrays.equals(value, data
					.getBase64()));
			
			assertEquals("Asserting the size", value.length, data.getSize());
			
			assertEquals(
					"Asserts that two-digit hexadecimal numbers for each byte separated by spaces is "
							+
					"returned in case of FORMAT_BASE64",
					expectedValue, data.toString());
			
		} catch(Exception e) {
			failUnexpectedException(e);

		}
	}
		
	
	
	/**
	 * This method asserts that when a DmtData(byte[]) is created, the format is
	 * DmtData.FORMAT_BINARY, DmtData.getSize() and DmtData.getBinary() return the expected value
	 * 
	 * @spec DmtData.DmtData(byte[])
	 */
	public void testDmtData002() {
		try {		
			log("#testDmtData002");
			byte[] value = new byte[] { 3, 55, 100 };
			String expectedValue= "03 37 64";
			info.dmtree.DmtData data  = new info.dmtree.DmtData(value);
			assertEquals("Asserting DmtData.FORMAT_BINARY",
					info.dmtree.DmtData.FORMAT_BINARY, data.getFormat());
			
			assertTrue("Asserting the value", Arrays.equals(value, data
					.getBinary()));
			
			assertEquals("Asserting the size", value.length, data.getSize());
			
			assertEquals(
					"Asserts that two-digit hexadecimal numbers for each byte separated by "
							+
					"spaces is returned in case of FORMAT_BINARY",
					expectedValue, data.toString());
			
		} catch(Exception e) {
			failUnexpectedException(e);

		}
	}
		
	
	
	/**
	 * This method asserts that when a DmtData(boolean) is created, the format is
	 * DmtData.FORMAT_BOOLEAN, DmtData.getSize() and DmtData.getBoolean() return the expected value
	 * 
	 * @spec DmtData.DmtData(boolean)
	 */
	public void testDmtData003() {
		try {		
			log("#testDmtData003");
			boolean value = false;
			info.dmtree.DmtData data  = new info.dmtree.DmtData(value);
			assertEquals("Asserting DmtData.FORMAT_BOOLEAN",
					info.dmtree.DmtData.FORMAT_BOOLEAN, data.getFormat());
			
			assertEquals("Asserting the value", value, data.getBoolean());
			assertEquals("Asserting the size", 1, data.getSize());
			assertEquals("Asserting the string returned",
					String.valueOf(value), data.toString());
			
		} catch(Exception e) {
			failUnexpectedException(e);

		}
	}
		
	
	
	/**
	 * This method asserts that when a DmtData(String,DmtData.FORMAT_DATE) is created, the format is
	 * DmtData.FORMAT_DATE, DmtData.getSize() and DmtData.getDate() return the expected value
	 * 
	 * @spec DmtData.DmtData(String,int)
	 */
	public void testDmtData004() {
		try {		
			log("#testDmtData004");
			String value = "20051001";
			info.dmtree.DmtData data  = new info.dmtree.DmtData(value,info.dmtree.DmtData.FORMAT_DATE);
			assertEquals("Asserting DmtData.FORMAT_DATE",
					info.dmtree.DmtData.FORMAT_DATE, data.getFormat());
			
			assertEquals("Asserting the value", value, data.getDate());
			
			assertEquals("Asserting the string returned", value, data
					.toString());
			
			assertEquals("Asserting the size", data.toString().length(), data
					.getSize());
			
		} catch(Exception e) {
			failUnexpectedException(e);

		}
	}
		
	
	
	/**
	 * This method asserts that when a DmtData(float) is created, the format is
	 * DmtData.FORMAT_FLOAT, DmtData.getSize() and DmtData.getFloat() return the expected value
	 * 
	 * @spec DmtData.DmtData(float)
	 */
	public void testDmtData005() {
		try {		
			log("#testDmtData005");
			float value = 2.4F;
			info.dmtree.DmtData data  = new info.dmtree.DmtData(value);
			assertEquals("Asserting DmtData.FORMAT_FLOAT",
					info.dmtree.DmtData.FORMAT_FLOAT, data.getFormat());
			
			assertTrue("Asserting the value", value == data.getFloat());
			
			assertEquals("Asserting the size", 4, data.getSize());
			
			assertEquals("Asserting the string returned",
					String.valueOf(value), data.toString());
		} catch(Exception e) {
			failUnexpectedException(e);

		}
	}
		
	
	/**
	 * This method asserts that when a DmtData(int) is created, the format is
	 * DmtData.FORMAT_INTEGER, DmtData.getSize() and DmtData.getInt() return the expected value
	 * 
	 * @spec DmtData.DmtData(int)
	 */
	public void testDmtData006() {
		try {		
			log("#testDmtData006");
			int value = 2;
			info.dmtree.DmtData data  = new info.dmtree.DmtData(value);
			assertEquals("Asserting DmtData.FORMAT_INTEGER",
					info.dmtree.DmtData.FORMAT_INTEGER, data.getFormat());
			
			assertEquals("Asserting the value", value, data.getInt());
			
			assertEquals("Asserting the size", 4, data.getSize());
			
			assertEquals("Asserting the string returned",
					String.valueOf(value), data.toString());
			
		} catch(Exception e) {
			failUnexpectedException(e);

		}
	}
		
	/**
	 * This method asserts that the format of DmtData.NULL_VALUE is 
	 * DmtData.FORMAT_NULL and DmtData.getSize() return the expected value
	 * 
	 * @spec DmtData.NULL_VALUE
	 */
	public void testDmtData007() {
		try {		
			log("#testDmtData007");
			info.dmtree.DmtData data  = info.dmtree.DmtData.NULL_VALUE;
			assertEquals("Asserting DmtData.FORMAT_NULL",
					info.dmtree.DmtData.FORMAT_NULL, data.getFormat());
			
			assertEquals("Asserting the size", 0, data.getSize());
			
			assertEquals("Asserting the string returned", "null", data
					.toString());
			
		} catch(Exception e) {
			failUnexpectedException(e);

		}
	}
		
	
	/**
	 * This method asserts that when a DmtData(String) is created, the format is
	 * DmtData.FORMAT_STRING, DmtData.getSize() and DmtData.getString() return the expected value
	 * 
	 * @spec DmtData.DmtData(String)
	 */
	public void testDmtData008() {
		try {		
			log("#testDmtData008");
			String value = "TestString";
			info.dmtree.DmtData data  = new info.dmtree.DmtData(value);
			assertEquals("Asserting DmtData.FORMAT_STRING",
					info.dmtree.DmtData.FORMAT_STRING, data.getFormat());
			
			assertEquals("Asserting the value", value, data.getString());
			
			assertEquals("Asserting the size", value.length(), data.getSize());
			
			assertEquals("Asserting the string returned", value, data
					.toString());
			
		} catch(Exception e) {
			failUnexpectedException(e);

		}
	}
		
	
	/**
	 * This method asserts that when a DmtData(String,DmtData.FORMAT_TIME) is created, the format is
	 * DmtData.FORMAT_TIME, DmtData.getSize() and DmtData.getTime() return the expected value
	 * 
	 * @spec DmtData.DmtData(String,int)
	 */
	public void testDmtData009() {
		try {		
			log("#testDmtData009");
			String value = "120000";
			info.dmtree.DmtData data  = new info.dmtree.DmtData(value,info.dmtree.DmtData.FORMAT_TIME);
			assertEquals("Asserting DmtData.FORMAT_TIME",
					info.dmtree.DmtData.FORMAT_TIME, data.getFormat());
			
			assertEquals("Asserting the value", value, data.getTime());
			
			assertEquals("Asserting the string returned", value, data
					.toString());
			
			assertEquals("Asserting the size", data.toString().length(), data
					.getSize());
			
		} catch(Exception e) {
			failUnexpectedException(e);

		}
	}
		
	/**
	 * This method asserts that when a DmtData(String,DmtData.FORMAT_TIME) is created, the format is
	 * DmtData.FORMAT_TIME, DmtData.getSize() and DmtData.getTime() return the expected value
	 * 
	 * @spec DmtData.DmtData(String,int)
	 */
	public void testDmtData010() {
		try {		
			log("#testDmtData010");
			String value = "120000Z";
			info.dmtree.DmtData data  = new info.dmtree.DmtData(value,info.dmtree.DmtData.FORMAT_TIME);
			assertEquals("Asserting DmtData.FORMAT_TIME",
					info.dmtree.DmtData.FORMAT_TIME, data.getFormat());
			
			assertEquals("Asserting the value", value, data.getTime());
			
			assertEquals("Asserting the string returned", value, data
					.toString());
			
		} catch(Exception e) {
			failUnexpectedException(e);

		}
	}
	/**
	 * This method asserts that when a DmtData(String,DmtData.FORMAT_XML) is created, the format is
	 * DmtData.FORMAT_XML, DmtData.getSize() and DmtData.getXml() return the expected value.
	 * Is also ensures that the validity of the XML is not checked by this constructor
	 * 
	 * @spec DmtData.DmtData(String,int)
	 */
	public void testDmtData011() {
		try {		
			log("#testDmtData011");
			String value = "testXml";
			info.dmtree.DmtData data  = new info.dmtree.DmtData(value,info.dmtree.DmtData.FORMAT_XML);
			assertEquals("Asserting DmtData.FORMAT_XML",
					info.dmtree.DmtData.FORMAT_XML, data.getFormat());
			
			assertEquals("Asserting the value", value, data.getXml());
			
			assertEquals("Asserting the size", value.length(), data.getSize());
			
			assertEquals("Asserting the string returned", value, data
					.toString());
			
			
		} catch(Exception e) {
			failUnexpectedException(e);

		}
	}
		
	
	/**
	 * This method asserts that when a DmtData(String,DmtData.FORMAT_STRING) is created, the format is
	 * DmtData.FORMAT_STRING, DmtData.getSize() and DmtData.getString() return the expected value
	 * 
	 * @spec DmtData.DmtData(String,int)
	 */
	public void testDmtData012() {
		try {		
			log("#testDmtData012");
			String value = "TestString2";
			
			info.dmtree.DmtData data  = new info.dmtree.DmtData(value,info.dmtree.DmtData.FORMAT_STRING);
			assertEquals("Asserting DmtData.FORMAT_STRING",
					info.dmtree.DmtData.FORMAT_STRING, data.getFormat());
			
			assertEquals("Asserting the value", value, data.getString());
			
			assertEquals("Asserting the size", value.length(), data.getSize());
			
			assertEquals("Asserting the string returned", value, data
					.toString());
			
		} catch(Exception e) {
			failUnexpectedException(e);

		}
	}
    
    /**
     * This method asserts that when a DmtData(byte[],false) is created, the format is
     * DmtData.FORMAT_BINARY, DmtData.getSize() and DmtData.getBinary() return the expected value
     * 
     * @spec DmtData.DmtData(byte[],boolean)
     */
    public void testDmtData013() {
        try {       
            log("#testDmtData013");
            byte[] value = new byte[] { 1, 10, 127 };
            String expectedValue= "01 0A 7F";
            info.dmtree.DmtData data  = new info.dmtree.DmtData(value,false);
            assertEquals("Asserting DmtData.FORMAT_BINARY",
                    info.dmtree.DmtData.FORMAT_BINARY, data.getFormat());
            
            assertTrue("Asserting the value", Arrays.equals(value, data
					.getBinary()));
            
            assertEquals("Asserting the size", value.length, data.getSize());
            
            assertEquals(
					"Asserts that two-digit hexadecimal numbers for each byte separated by spaces is "
							+
                    "returned in case of FORMAT_BINARY",
                    expectedValue, data.toString());
            
        } catch(Exception e) {
        	failUnexpectedException(e);

        }
    }
	
	/**
	 * This method asserts that when a DmtData(String) is created with null, the format is
	 * DmtData.FORMAT_STRING, DmtData.getSize() and DmtData.getString() return the expected value
	 * 
	 * @spec DmtData.DmtData(String)
	 */
	public void testDmtData014() {
		try {		
			log("#testDmtData014");
			info.dmtree.DmtData data  = new info.dmtree.DmtData((String)null);
			assertEquals("Asserting DmtData.FORMAT_STRING",
					info.dmtree.DmtData.FORMAT_STRING, data.getFormat());
			
			assertNull("Asserting the value", data.getString());

			assertEquals("Asserting the size", 0, data.getSize());
			
			assertEquals("Asserting the string returned", "", data.toString());
			
		} catch(Exception e) {
			failUnexpectedException(e);

		}
	}
	
	/**
	 * This method asserts that when a DmtData(String,FORMAT_STRING) is created with null, the format is
	 * DmtData.FORMAT_STRING, DmtData.getSize() and DmtData.getString() return the expected value
	 * 
	 * @spec DmtData.DmtData(String,int)
	 */
	public void testDmtData015() {
		try {		
			log("#testDmtData015");
			info.dmtree.DmtData data  = new info.dmtree.DmtData(null,info.dmtree.DmtData.FORMAT_STRING);
			assertEquals("Asserting DmtData.FORMAT_STRING",
					info.dmtree.DmtData.FORMAT_STRING, data.getFormat());
			
			assertNull("Asserting the value", data.getString());

			assertEquals("Asserting the size", 0, data.getSize());
			
			assertEquals("Asserting the string returned", "", data.toString());
			
		} catch(Exception e) {
			failUnexpectedException(e);

		}
	}
	
	/**
	 * This method asserts that when a DmtData(String,FORMAT_XML) is created with null, the format is
	 * DmtData.FORMAT_XML, DmtData.getSize() and DmtData.getString() return the expected value
	 * 
	 * @spec DmtData.DmtData(String,int)
	 */
	public void testDmtData016() {
		try {		
			log("#testDmtData016");
			info.dmtree.DmtData data  = new info.dmtree.DmtData(null,info.dmtree.DmtData.FORMAT_XML);
			assertEquals("Asserting DmtData.FORMAT_XML",
					info.dmtree.DmtData.FORMAT_XML, data.getFormat());
			
			assertNull("Asserting the value", data.getXml());

			assertEquals("Asserting the size", 0, data.getSize());
			
			assertEquals("Asserting the string returned", "", data.toString());
			
		} catch(Exception e) {
			failUnexpectedException(e);

		}
	}
	
	/**
	 * This method asserts that when a DmtData(String,String) is created the format is
	 * DmtData.FORMAT_RAW_STRING, DmtData.getSize() and DmtData.getRawString() return the expected value
	 * 
	 * @spec DmtData.DmtData(String,String)
	 */
	public void testDmtData017() {
		try {		
			log("#testDmtData017");
			String format = "format";
			String value = "data";
			info.dmtree.DmtData data  = new info.dmtree.DmtData(format,value);
			assertEquals("Asserting DmtData.FORMAT_RAW_STRING",
					info.dmtree.DmtData.FORMAT_RAW_STRING, data.getFormat());
			
			assertEquals("Asserting the value", value, data.getRawString());
			
			assertEquals("Asserting the size", value.length(), data.getSize());
			
		} catch(Exception e) {
			failUnexpectedException(e);

		}
	}
	
	/**
	 * This method asserts that when a DmtData(String,String) is created the format is
	 * DmtData.FORMAT_RAW_BINARY, DmtData.getSize() and DmtData.getRawBinary() return the expected value
	 * 
	 * @spec DmtData.DmtData(String,byte[])
	 */
	public void testDmtData018() {
		try {		
			log("#testDmtData018");
			String format = "format";
			byte[] value = new byte[] {1};
			info.dmtree.DmtData data  = new info.dmtree.DmtData(format,value);
			assertEquals("Asserting DmtData.FORMAT_RAW_BINARY",
					info.dmtree.DmtData.FORMAT_RAW_BINARY, data.getFormat());
			
			assertTrue("Asserting the value", Arrays.equals(value, data
					.getRawBinary()));
			
			assertEquals("Asserting the size", value.length, data.getSize());
			
		} catch(Exception e) {
			failUnexpectedException(e);

		}
	}
	
}
