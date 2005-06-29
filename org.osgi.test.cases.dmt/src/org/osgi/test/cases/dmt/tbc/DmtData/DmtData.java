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
 * 26/01/2005   André Assad
 * 1            Implement TCK
 * ===========  ==============================================================
 * 14/02/2005   Leonardo Barros
 * 1            Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-002)
 */

package org.osgi.test.cases.dmt.tbc.DmtData;

import org.osgi.service.dmt.DmtException;
import org.osgi.test.cases.dmt.tbc.DmtTestControl;

/**
 * 
 * This class contains all the test methods for all the
 *                     DmtData constructors.
 */
public class DmtData {
    //TODO Review the code for changes.
	private DmtTestControl tbc;

	private org.osgi.service.dmt.DmtData dd;

	/**
	 * @param DefaultMEGTestBundleControl
	 *            Default Constructor. It initializes tbc for accessing services
	 *            intances on the TestControl
	 */
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
	}

	/**
	 * 
	 *  This method asserts that the Data format of String
	 *                  DmtData Constructor is equal to DmtDataType.STRING and
	 *                  that the string value passed is the same as
	 *                  DmtData.getString()
	 *                  
	 *  @spec DmtData.DmtData(String)
	 */
	public void testDmtData001() {
		tbc.log("Test DmtData Constructor with String format");

		// Constructors that instantites a DmtData with a String format
		dd = new org.osgi.service.dmt.DmtData("String");

		// asserts that dd has the String format
		tbc.assertEquals("Asserting String format",
				org.osgi.service.dmt.DmtData.FORMAT_STRING, dd.getFormat());

		try {		
			// asserts that the value passed is the same as the DmtData
			tbc.assertEquals("Asserting same data value", "String", dd.getString());
		}catch(Exception e) {
			tbc.fail("getString incorrectly thrown a Exception");
		}
			
	}

	/**
	 * 
	 *  This method passes a String equals to
	 *                  DmtData.FORMAT_NULL in order to assert that a node with
	 *                  null format is created.
	 *  ### Huh?? This creates an INT DmtData???
	 *  @spec DmtData.DmtData(int)
	 */
	public void testDmtData002() {
		tbc.log("Test DmtData Constructor with null String format");

		// Constructors that instantites a DmtData with a null String
		dd = new org.osgi.service.dmt.DmtData(
				org.osgi.service.dmt.DmtData.FORMAT_NULL);

		// asserts that dd has the null String format
		tbc.assertEquals("Asserting String format",
				org.osgi.service.dmt.DmtData.FORMAT_NULL, dd.getFormat());

		try {
			// asserts that the value passed is the same as the DmtData
			tbc.assertNull("Asserting same data value", dd.getString());
		}catch(Exception e){
			tbc.fail("getString incorrectly thrown a Exception");
		}
	}

	/**
	 * 
	 *  This method asserts that a null String parameter can
	 *                  create a new DmtData.
	 *  @spec DmtData.DmtData(String)
	 */
	public void testDmtData003() {
		tbc.log("Test DmtData Constructor with null String parameter");

		String string = null;

		// Constructors that instantites a DmtData with a null String
		dd = new org.osgi.service.dmt.DmtData(string);

		// asserts that dd has the String format
		tbc.assertEquals("Asserting String format",
				org.osgi.service.dmt.DmtData.FORMAT_STRING, dd.getFormat());

		try {
			// asserts that the value passed is the same as the DmtData
			tbc.assertNull("Asserting same data value", dd.getString());
		}catch(Exception e){
			tbc.fail("getString incorrectly thrown a Exception");
		}
	}

	/**
	 * 
	 *  This method asserts that the Data format of DmtData is
	 *                  equal to DmtDataType.XML and that the string value
	 *                  passed is the same as DmtData.getString()
	 *  @spec DmtData.DmtData(String,boolean)
	 */
	public void testDmtData004() {
		String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<test name=\"tck\" jsr=\"232\"></test>";

		tbc
				.log("Test DmtData Constructor with XML format with true xml parameter");

		// Constructors that instantites a DmtData with a null String
		dd = new org.osgi.service.dmt.DmtData(xmlStr, true);

		// asserts that dd has the XML format
		tbc.assertEquals("Asserting XML format",
				org.osgi.service.dmt.DmtData.FORMAT_XML, dd.getFormat());

		try {
			// asserts that the value passed is the same as the DmtData
			tbc.assertEquals("Asserting same data value", xmlStr, dd.getString());
		}catch(Exception e){
			tbc.fail("getString incorrectly thrown a Exception");
		}
	}

	/**
	 * 
	 *  This method asserts that if a false parameter is passed
	 *                  on the constructor, it behaves the same as DmtData(str)
	 *  @spec DmtData.DmtData(String,boolean)
	 */
	public void testDmtData005() {
		String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<test name=\"tck\" jsr=\"232\"></test>";

		tbc
				.log("Test DmtData Constructor with XML format with false xml parameter");

		// Constructors that instantites a DmtData with a null String
		dd = new org.osgi.service.dmt.DmtData(xmlStr, false);

		// asserts that dd has the XML format
		tbc.assertEquals("Asserting String format",
				org.osgi.service.dmt.DmtData.FORMAT_STRING, dd.getFormat());

		try {
			// asserts that the value passed is the same as the DmtData
			tbc.assertEquals("Asserting same data value", xmlStr, dd.getString());
		}catch(Exception e){
			tbc.fail("getString incorrectly thrown a Exception");
		}
	}

	/**
	 * @throws DmtException
	 * 
	 *  This method asserts that the Data format of DmtData is
	 *                  equal to DmtDataType.INTEGER and that the int value
	 *                  passed is the same as dd.getInt()
	 *  @spec DmtData.DmtData(int)
	 */
	public void testDmtData006() {
		int i = 6;

		tbc.log("Test DmtData Constructor with int format");

		// Constructors that instantites a DmtData with a int format
		dd = new org.osgi.service.dmt.DmtData(i);

		// asserts that dd has the INTEGER format
		tbc.assertEquals("Asserting INTEGER format",
				org.osgi.service.dmt.DmtData.FORMAT_INTEGER, dd.getFormat());

		try {
			// asserts that the value passed is the same as the DmtData
			tbc.assertEquals("Asserting same data value", i, dd.getInt());
		}catch(Exception e){
			tbc.fail("getInt incorrectly thrown a Exception");
		}
	}

	/**
	 * @throws DmtException
	 * 
	 *  This method asserts that the Data format of DmtData is
	 *                  equal to DmtDataType.BOOLEAN and that the boolean value
	 *                  passed is the same as dd.getBoolean()
	 *  @spec DmtData.DmtData(boolean)
	 */
	public void testDmtData007() {
		boolean b = true;

		tbc.log("Test DmtData Constructor with boolean format");

		// Constructors that instantites a DmtData with a boolean format
		dd = new org.osgi.service.dmt.DmtData(b);

		// asserts that dd has the BOOLEAN format
		tbc.assertEquals("Asserting BOOLEAN format",
				org.osgi.service.dmt.DmtData.FORMAT_BOOLEAN, dd.getFormat());

		try {
			// asserts that the value passed is the same as the DmtData
			tbc.assertEquals("Asserting same data value", b, dd.getBoolean());
		}catch(Exception e){
			tbc.fail("getBoolean incorrectly thrown a Exception");
		}
	}

	/**
	 * 
	 *  This method asserts that the Data format of DmtData is
	 *                  equal to DmtDataType.BINARY and that the string value
	 *                  passed is the same as dd.getString()
	 *  @spec DmtData.DmtData(byte[])
	 */
	public void testDmtData008() {
		byte[] b = new byte[1024];

		tbc.log("Test DmtData Constructor with binary format");

		// Constructors that instantites a DmtData with a byte array format
		dd = new org.osgi.service.dmt.DmtData(b);

		// asserts that dd has the binary format
		tbc.assertEquals("Asserting BINARY format",
				org.osgi.service.dmt.DmtData.FORMAT_BINARY, dd.getFormat());

		try {
			// asserts that the value passed is the same as the DmtData
			tbc.assertEquals("Asserting same data value", b, dd.getBinary());
		}catch(Exception e){
			tbc.fail("getBinary incorrectly thrown a Exception");
		}
	}

	/**
	 * 
	 *  This method asserts if getInt method throws a
	 *                  DmtException when the DmtData is not created with an
	 *                  integer
	 *  @spec DmtData.getInt()
	 *  @spec DmtData.DmtData(boolean)
	 */
	public void testDmtData009() {
		dd = new org.osgi.service.dmt.DmtData(true);
		try {
			dd.getInt();
			tbc.fail("getInt did not throw a DmtException");
		}catch(org.osgi.service.dmt.DmtException e) {
			tbc.assertEquals("Asserts DmtException code",org.osgi.service.dmt.DmtException.OTHER_ERROR,e.getCode());
		}catch(Exception e){
			tbc.fail("getInt did not throw a DmtException");
		}
	}
	
	/**
	 * 
	 *  This method asserts if getBoolean method throws a
	 *                  DmtException when the DmtData is not created with an
	 *                  boolean
	 *  @spec DmtData.getBoolean()
	 *  @spec DmtData.DmtData(int)
	 */
	public void testDmtData010() {
		dd = new org.osgi.service.dmt.DmtData(0);
		try {
			dd.getBoolean();
			tbc.fail("getBoolean did not throw a DmtException");
		}catch(org.osgi.service.dmt.DmtException e) {
			tbc.assertEquals("Asserts DmtException code",org.osgi.service.dmt.DmtException.OTHER_ERROR,e.getCode());
		}catch(Exception e){
			tbc.fail("getBoolean did not throw a DmtException");
		}
	}

}
