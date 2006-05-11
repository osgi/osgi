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

import org.osgi.test.cases.dmt.tc1.tbc.DmtTestControl;

/**
 * 
 * This class contains all the test methods for all the DmtData constructors.
 */
public class DmtData {
	private DmtTestControl tbc;
	public DmtData(DmtTestControl tbc) {
		this.tbc = tbc;

	}

	public void run() {
		testDmtData001();
		testDmtData002();
		testDmtData003();
		testDmtData004();
		testDmtData005();
		testDmtData006();
		testDmtData007();
		testDmtData008();
		testDmtData009();
		testDmtData010();
		testDmtData011();
		testDmtData012();
		testDmtData013();
        testDmtData014();
        testDmtData015();
        testDmtData016();
        testDmtData017();
        testDmtData018();

	}

	/**
	 * This method asserts that when a DmtData(byte[],true) is created, the format is
	 * DmtData.FORMAT_BASE64, DmtData.getSize() and DmtData.getBase64() return the expected value
	 * 
	 * @spec DmtData.DmtData(byte[],boolean)
	 */
	private void testDmtData001() {
		try {		
			tbc.log("#testDmtData001");
			byte[] value = new byte[] { 1, 10, 127 };
			String expectedValue= "01 0A 7F";
			info.dmtree.DmtData data  = new info.dmtree.DmtData(value,true);
			tbc.assertEquals("Asserting DmtData.FORMAT_BASE64",
					info.dmtree.DmtData.FORMAT_BASE64, data.getFormat());
			
			tbc.assertEquals("Asserting the value", value, data.getBase64());
			
			tbc.assertEquals("Asserting the size", value.length, data.getSize());
			
			tbc.assertEquals("Asserts that two-digit hexadecimal numbers for each byte separated by spaces is " +
					"returned in case of FORMAT_BASE64",
					expectedValue, data.toString());
			
		} catch(Exception e) {
			tbc.failUnexpectedException(e);

		}
	}
		
	
	
	/**
	 * This method asserts that when a DmtData(byte[]) is created, the format is
	 * DmtData.FORMAT_BINARY, DmtData.getSize() and DmtData.getBinary() return the expected value
	 * 
	 * @spec DmtData.DmtData(byte[])
	 */
	private void testDmtData002() {
		try {		
			tbc.log("#testDmtData002");
			byte[] value = new byte[] { 3, 55, 100 };
			String expectedValue= "03 37 64";
			info.dmtree.DmtData data  = new info.dmtree.DmtData(value);
			tbc.assertEquals("Asserting DmtData.FORMAT_BINARY",
					info.dmtree.DmtData.FORMAT_BINARY, data.getFormat());
			
			tbc.assertEquals("Asserting the value", value, data.getBinary());
			
			tbc.assertEquals("Asserting the size", value.length, data.getSize());
			
			tbc.assertEquals("Asserts that two-digit hexadecimal numbers for each byte separated by " +
					"spaces is returned in case of FORMAT_BINARY",
					expectedValue, data.toString());
			
		} catch(Exception e) {
			tbc.failUnexpectedException(e);

		}
	}
		
	
	
	/**
	 * This method asserts that when a DmtData(boolean) is created, the format is
	 * DmtData.FORMAT_BOOLEAN, DmtData.getSize() and DmtData.getBoolean() return the expected value
	 * 
	 * @spec DmtData.DmtData(boolean)
	 */
	private void testDmtData003() {
		try {		
			tbc.log("#testDmtData003");
			boolean value = false;
			info.dmtree.DmtData data  = new info.dmtree.DmtData(value);
			tbc.assertEquals("Asserting DmtData.FORMAT_BOOLEAN",
					info.dmtree.DmtData.FORMAT_BOOLEAN, data.getFormat());
			
			tbc.assertEquals("Asserting the value", value, data.getBoolean());
			tbc.assertEquals("Asserting the size", 1, data.getSize());
			tbc.assertEquals("Asserting the string returned", String.valueOf(value), data.toString());
			
		} catch(Exception e) {
			tbc.failUnexpectedException(e);

		}
	}
		
	
	
	/**
	 * This method asserts that when a DmtData(String,DmtData.FORMAT_DATE) is created, the format is
	 * DmtData.FORMAT_DATE, DmtData.getSize() and DmtData.getDate() return the expected value
	 * 
	 * @spec DmtData.DmtData(String,int)
	 */
	private void testDmtData004() {
		try {		
			tbc.log("#testDmtData004");
			String value = "20051001";
			info.dmtree.DmtData data  = new info.dmtree.DmtData(value,info.dmtree.DmtData.FORMAT_DATE);
			tbc.assertEquals("Asserting DmtData.FORMAT_DATE",
					info.dmtree.DmtData.FORMAT_DATE, data.getFormat());
			
			tbc.assertEquals("Asserting the value", value, data.getDate());
			
			tbc.assertEquals("Asserting the string returned", value, data.toString());
			
			tbc.assertEquals("Asserting the size", data.toString().length(), data.getSize());
			
		} catch(Exception e) {
			tbc.failUnexpectedException(e);

		}
	}
		
	
	
	/**
	 * This method asserts that when a DmtData(float) is created, the format is
	 * DmtData.FORMAT_FLOAT, DmtData.getSize() and DmtData.getFloat() return the expected value
	 * 
	 * @spec DmtData.DmtData(float)
	 */
	private void testDmtData005() {
		try {		
			tbc.log("#testDmtData005");
			float value = 2.4F;
			info.dmtree.DmtData data  = new info.dmtree.DmtData(value);
			tbc.assertEquals("Asserting DmtData.FORMAT_FLOAT",
					info.dmtree.DmtData.FORMAT_FLOAT, data.getFormat());
			
			tbc.assertTrue("Asserting the value", value == data.getFloat());
			
			tbc.assertEquals("Asserting the size", 4, data.getSize());
			
			tbc.assertEquals("Asserting the string returned", String.valueOf(value), data.toString());
		} catch(Exception e) {
			tbc.failUnexpectedException(e);

		}
	}
		
	
	/**
	 * This method asserts that when a DmtData(int) is created, the format is
	 * DmtData.FORMAT_INTEGER, DmtData.getSize() and DmtData.getInt() return the expected value
	 * 
	 * @spec DmtData.DmtData(int)
	 */
	private void testDmtData006() {
		try {		
			tbc.log("#testDmtData006");
			int value = 2;
			info.dmtree.DmtData data  = new info.dmtree.DmtData(value);
			tbc.assertEquals("Asserting DmtData.FORMAT_INTEGER",
					info.dmtree.DmtData.FORMAT_INTEGER, data.getFormat());
			
			tbc.assertEquals("Asserting the value", value,data.getInt());
			
			tbc.assertEquals("Asserting the size", 4, data.getSize());
			
			tbc.assertEquals("Asserting the string returned", String.valueOf(value), data.toString());
			
		} catch(Exception e) {
			tbc.failUnexpectedException(e);

		}
	}
		
	/**
	 * This method asserts that the format of DmtData.NULL_VALUE is 
	 * DmtData.FORMAT_NULL and DmtData.getSize() return the expected value
	 * 
	 * @spec DmtData.NULL_VALUE
	 */
	private void testDmtData007() {
		try {		
			tbc.log("#testDmtData007");
			info.dmtree.DmtData data  = info.dmtree.DmtData.NULL_VALUE;
			tbc.assertEquals("Asserting DmtData.FORMAT_NULL",
					info.dmtree.DmtData.FORMAT_NULL, data.getFormat());
			
			tbc.assertEquals("Asserting the size", 0, data.getSize());
			
			tbc.assertEquals("Asserting the string returned", "null", data.toString());
			
		} catch(Exception e) {
			tbc.failUnexpectedException(e);

		}
	}
		
	
	/**
	 * This method asserts that when a DmtData(String) is created, the format is
	 * DmtData.FORMAT_STRING, DmtData.getSize() and DmtData.getString() return the expected value
	 * 
	 * @spec DmtData.DmtData(String)
	 */
	private void testDmtData008() {
		try {		
			tbc.log("#testDmtData008");
			String value = "TestString";
			info.dmtree.DmtData data  = new info.dmtree.DmtData(value);
			tbc.assertEquals("Asserting DmtData.FORMAT_STRING",
					info.dmtree.DmtData.FORMAT_STRING, data.getFormat());
			
			tbc.assertEquals("Asserting the value", value, data.getString());
			
			tbc.assertEquals("Asserting the size", value.length(), data.getSize());
			
			tbc.assertEquals("Asserting the string returned", value, data.toString());
			
		} catch(Exception e) {
			tbc.failUnexpectedException(e);

		}
	}
		
	
	/**
	 * This method asserts that when a DmtData(String,DmtData.FORMAT_TIME) is created, the format is
	 * DmtData.FORMAT_TIME, DmtData.getSize() and DmtData.getTime() return the expected value
	 * 
	 * @spec DmtData.DmtData(String,int)
	 */
	private void testDmtData009() {
		try {		
			tbc.log("#testDmtData009");
			String value = "120000";
			info.dmtree.DmtData data  = new info.dmtree.DmtData(value,info.dmtree.DmtData.FORMAT_TIME);
			tbc.assertEquals("Asserting DmtData.FORMAT_TIME",
					info.dmtree.DmtData.FORMAT_TIME, data.getFormat());
			
			tbc.assertEquals("Asserting the value", value, data.getTime());
			
			tbc.assertEquals("Asserting the string returned", value, data.toString());
			
			tbc.assertEquals("Asserting the size", data.toString().length(), data.getSize());
			
		} catch(Exception e) {
			tbc.failUnexpectedException(e);

		}
	}
		
	/**
	 * This method asserts that when a DmtData(String,DmtData.FORMAT_TIME) is created, the format is
	 * DmtData.FORMAT_TIME, DmtData.getSize() and DmtData.getTime() return the expected value
	 * 
	 * @spec DmtData.DmtData(String,int)
	 */
	private void testDmtData010() {
		try {		
			tbc.log("#testDmtData010");
			String value = "120000Z";
			info.dmtree.DmtData data  = new info.dmtree.DmtData(value,info.dmtree.DmtData.FORMAT_TIME);
			tbc.assertEquals("Asserting DmtData.FORMAT_TIME",
					info.dmtree.DmtData.FORMAT_TIME, data.getFormat());
			
			tbc.assertEquals("Asserting the value", value, data.getTime());
			
			tbc.assertEquals("Asserting the string returned", value, data.toString());
			
		} catch(Exception e) {
			tbc.failUnexpectedException(e);

		}
	}
	/**
	 * This method asserts that when a DmtData(String,DmtData.FORMAT_XML) is created, the format is
	 * DmtData.FORMAT_XML, DmtData.getSize() and DmtData.getXml() return the expected value.
	 * Is also ensures that the validity of the XML is not checked by this constructor
	 * 
	 * @spec DmtData.DmtData(String,int)
	 */
	private void testDmtData011() {
		try {		
			tbc.log("#testDmtData011");
			String value = "testXml";
			info.dmtree.DmtData data  = new info.dmtree.DmtData(value,info.dmtree.DmtData.FORMAT_XML);
			tbc.assertEquals("Asserting DmtData.FORMAT_XML",
					info.dmtree.DmtData.FORMAT_XML, data.getFormat());
			
			tbc.assertEquals("Asserting the value", value, data.getXml());
			
			tbc.assertEquals("Asserting the size", value.length(), data.getSize());
			
			tbc.assertEquals("Asserting the string returned", value, data.toString());
			
			
		} catch(Exception e) {
			tbc.failUnexpectedException(e);

		}
	}
		
	
	/**
	 * This method asserts that when a DmtData(String,DmtData.FORMAT_STRING) is created, the format is
	 * DmtData.FORMAT_STRING, DmtData.getSize() and DmtData.getString() return the expected value
	 * 
	 * @spec DmtData.DmtData(String,int)
	 */
	private void testDmtData012() {
		try {		
			tbc.log("#testDmtData012");
			String value = "TestString2";
			
			info.dmtree.DmtData data  = new info.dmtree.DmtData(value,info.dmtree.DmtData.FORMAT_STRING);
			tbc.assertEquals("Asserting DmtData.FORMAT_STRING",
					info.dmtree.DmtData.FORMAT_STRING, data.getFormat());
			
			tbc.assertEquals("Asserting the value", value, data.getString());
			
			tbc.assertEquals("Asserting the size", value.length(), data.getSize());
			
			tbc.assertEquals("Asserting the string returned", value, data.toString());
			
		} catch(Exception e) {
			tbc.failUnexpectedException(e);

		}
	}
    
    /**
     * This method asserts that when a DmtData(byte[],false) is created, the format is
     * DmtData.FORMAT_BINARY, DmtData.getSize() and DmtData.getBinary() return the expected value
     * 
     * @spec DmtData.DmtData(byte[],boolean)
     */
    private void testDmtData013() {
        try {       
            tbc.log("#testDmtData013");
            byte[] value = new byte[] { 1, 10, 127 };
            String expectedValue= "01 0A 7F";
            info.dmtree.DmtData data  = new info.dmtree.DmtData(value,false);
            tbc.assertEquals("Asserting DmtData.FORMAT_BINARY",
                    info.dmtree.DmtData.FORMAT_BINARY, data.getFormat());
            
            tbc.assertEquals("Asserting the value", value, data.getBinary());
            
            tbc.assertEquals("Asserting the size", value.length, data.getSize());
            
            tbc.assertEquals("Asserts that two-digit hexadecimal numbers for each byte separated by spaces is " +
                    "returned in case of FORMAT_BINARY",
                    expectedValue, data.toString());
            
        } catch(Exception e) {
        	tbc.failUnexpectedException(e);

        }
    }
	
	/**
	 * This method asserts that when a DmtData(String) is created with null, the format is
	 * DmtData.FORMAT_STRING, DmtData.getSize() and DmtData.getString() return the expected value
	 * 
	 * @spec DmtData.DmtData(String)
	 */
	private void testDmtData014() {
		try {		
			tbc.log("#testDmtData014");
			info.dmtree.DmtData data  = new info.dmtree.DmtData((String)null);
			tbc.assertEquals("Asserting DmtData.FORMAT_STRING",
					info.dmtree.DmtData.FORMAT_STRING, data.getFormat());
			
			tbc.assertNull("Asserting the value", data.getString());

			tbc.assertEquals("Asserting the size", 0, data.getSize());
			
			tbc.assertEquals("Asserting the string returned", "",data.toString());
			
		} catch(Exception e) {
			tbc.failUnexpectedException(e);

		}
	}
	
	/**
	 * This method asserts that when a DmtData(String,FORMAT_STRING) is created with null, the format is
	 * DmtData.FORMAT_STRING, DmtData.getSize() and DmtData.getString() return the expected value
	 * 
	 * @spec DmtData.DmtData(String,int)
	 */
	private void testDmtData015() {
		try {		
			tbc.log("#testDmtData015");
			info.dmtree.DmtData data  = new info.dmtree.DmtData(null,info.dmtree.DmtData.FORMAT_STRING);
			tbc.assertEquals("Asserting DmtData.FORMAT_STRING",
					info.dmtree.DmtData.FORMAT_STRING, data.getFormat());
			
			tbc.assertNull("Asserting the value", data.getString());

			tbc.assertEquals("Asserting the size", 0, data.getSize());
			
			tbc.assertEquals("Asserting the string returned", "",data.toString());
			
		} catch(Exception e) {
			tbc.failUnexpectedException(e);

		}
	}
	
	/**
	 * This method asserts that when a DmtData(String,FORMAT_XML) is created with null, the format is
	 * DmtData.FORMAT_XML, DmtData.getSize() and DmtData.getString() return the expected value
	 * 
	 * @spec DmtData.DmtData(String,int)
	 */
	private void testDmtData016() {
		try {		
			tbc.log("#testDmtData016");
			info.dmtree.DmtData data  = new info.dmtree.DmtData(null,info.dmtree.DmtData.FORMAT_XML);
			tbc.assertEquals("Asserting DmtData.FORMAT_XML",
					info.dmtree.DmtData.FORMAT_XML, data.getFormat());
			
			tbc.assertNull("Asserting the value", data.getXml());

			tbc.assertEquals("Asserting the size", 0, data.getSize());
			
			tbc.assertEquals("Asserting the string returned", "",data.toString());
			
		} catch(Exception e) {
			tbc.failUnexpectedException(e);

		}
	}
	
	/**
	 * This method asserts that when a DmtData(String,String) is created the format is
	 * DmtData.FORMAT_RAW_STRING, DmtData.getSize() and DmtData.getRawString() return the expected value
	 * 
	 * @spec DmtData.DmtData(String,String)
	 */
	private void testDmtData017() {
		try {		
			tbc.log("#testDmtData017");
			String format = "format";
			String value = "data";
			info.dmtree.DmtData data  = new info.dmtree.DmtData(format,value);
			tbc.assertEquals("Asserting DmtData.FORMAT_RAW_STRING",
					info.dmtree.DmtData.FORMAT_RAW_STRING, data.getFormat());
			
			tbc.assertEquals("Asserting the value", value, data.getRawString());
			
			tbc.assertEquals("Asserting the size", value.length(), data.getSize());
			
		} catch(Exception e) {
			tbc.failUnexpectedException(e);

		}
	}
	
	/**
	 * This method asserts that when a DmtData(String,String) is created the format is
	 * DmtData.FORMAT_RAW_BINARY, DmtData.getSize() and DmtData.getRawBinary() return the expected value
	 * 
	 * @spec DmtData.DmtData(String,byte[])
	 */
	private void testDmtData018() {
		try {		
			tbc.log("#testDmtData018");
			String format = "format";
			byte[] value = new byte[] {1};
			info.dmtree.DmtData data  = new info.dmtree.DmtData(format,value);
			tbc.assertEquals("Asserting DmtData.FORMAT_RAW_BINARY",
					info.dmtree.DmtData.FORMAT_RAW_BINARY, data.getFormat());
			
			tbc.assertEquals("Asserting the value", value, data.getRawBinary());
			
			tbc.assertEquals("Asserting the size",value.length, data.getSize());
			
		} catch(Exception e) {
			tbc.failUnexpectedException(e);

		}
	}
	
}
