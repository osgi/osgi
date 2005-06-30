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

package org.osgi.test.cases.dmt.main.tbc.DmtData;

import org.osgi.service.dmt.DmtException;
import org.osgi.test.cases.dmt.main.tbc.DmtTestControl;

/**
 * 
 * @methodUnderTest org.osgi.service.dmt.DmtData#DmtData, getString, getInt, getFormat, getBinary, getBoolean
 * @generalDescription This class contains all the test methods for all the
 *                     DmtData constructors.
 */
public class DmtData {
	private DmtTestControl tbc;

	private org.osgi.service.dmt.DmtData dd;

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
		testDmtData019();
		testDmtData020();
		testDmtData021();
		testDmtData022();
		testDmtData023();
		testDmtData024();
		testDmtData025();
		testDmtData026();
		testDmtData027();
	}

	/**
	 * @testID testDmtData001
	 * @testDescription This method asserts that the Data format of String
	 *                  DmtData Constructor is equal to DmtData.FORMAT_STRING and
	 *                  that the string value passed is the same as
	 *                  DmtData.getString()
	 */
	private void testDmtData001() {
		try {		
			tbc.log("#testDmtData001");
			dd = new org.osgi.service.dmt.DmtData("String");
			tbc.assertEquals("Asserting String format",
					org.osgi.service.dmt.DmtData.FORMAT_STRING, dd.getFormat());
			tbc.assertEquals("Asserting same data value", "String", dd.getString());
		}catch(Exception e) {
			tbc.fail("Unexpected Exception");
		}
			
	}

	/**
	 * @testID testDmtData002
	 * @testDescription This method asserts that a null String parameter can
	 *                  create a new DmtData.
	 */
	private void testDmtData002() {
		try {
			tbc.log("#testDmtData002");
			String string = null;
			dd = new org.osgi.service.dmt.DmtData(string);
			tbc.assertEquals("Asserting String format",
					org.osgi.service.dmt.DmtData.FORMAT_STRING, dd.getFormat());
			tbc.assertNull("Asserting same data value", dd.getString());
		}catch(Exception e){
			tbc.fail("Unexpected Exception");
		}
	}

	/**
	 * @testID testDmtData003
	 * @testDescription This method asserts that the Data format of DmtData is
	 *                  equal to DmtData.FORMAT_XML and that the string value
	 *                  passed is the same as DmtData.getString()
	 */
	private void testDmtData003() {
		try {
			tbc.log("#testDmtData003");
			String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
					+ "<test name=\"tck\" jsr=\"232\"></test>";

			dd = new org.osgi.service.dmt.DmtData(xmlStr, true);
			tbc.assertEquals("Asserting XML format",
					org.osgi.service.dmt.DmtData.FORMAT_XML, dd.getFormat());			

			tbc.assertEquals("Asserting same data value", xmlStr, dd.getXml());
		}catch(Exception e){
			tbc.fail("Unexpected Exception");
		}
	}

	/**
	 * @testID testDmtData004
	 * @testDescription This method asserts that if a false parameter is passed
	 *                  on the constructor, it behaves the same as DmtData(str)
	 */
	private void testDmtData004() {
		try {
			tbc.log("#testDmtData004");
			String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
					+ "<test name=\"tck\" jsr=\"232\"></test>";
			dd = new org.osgi.service.dmt.DmtData(xmlStr, false);

			tbc.assertEquals("Asserting String format",
					org.osgi.service.dmt.DmtData.FORMAT_STRING, dd.getFormat());

			tbc.assertEquals("Asserting same data value", xmlStr, dd.getString());
		}catch(Exception e){
			tbc.fail("Unexpected Exception");
		}
	}

	/**
	 * @testID testDmtData005
	 * @testDescription This method asserts that the Data format of DmtData is
	 *                  equal to DmtData.FORMAT_INTEGER and that the int value
	 *                  passed is the same as dd.getInt()
	 */
	private void testDmtData005() {
		try {
			tbc.log("#testDmtData005");
			int i = 6;
			dd = new org.osgi.service.dmt.DmtData(i);


			tbc.assertEquals("Asserting INTEGER format",
					org.osgi.service.dmt.DmtData.FORMAT_INTEGER, dd.getFormat());
			
			tbc.assertEquals("Asserting same data value", i, dd.getInt());
		}catch(Exception e){
			tbc.fail("Unexpected Exception");
		}
	}

	/**
	 * @testID testDmtData006
	 * @testDescription This method asserts that the Data format of DmtData is
	 *                  equal to DmtData.FORMAT_BOOLEAN and that the boolean value
	 *                  passed is the same as dd.getBoolean()
	 */
	private void testDmtData006() {
		try {
			tbc.log("#testDmtData006");
			boolean b = true;
			tbc.log("#Test DmtData Constructor with boolean format");
			dd = new org.osgi.service.dmt.DmtData(b);

			tbc.assertEquals("Asserting BOOLEAN format",
					org.osgi.service.dmt.DmtData.FORMAT_BOOLEAN, dd.getFormat());
			tbc.assertEquals("Asserting same data value", b, dd.getBoolean());
		}catch(Exception e){
			tbc.fail("Unexpected Exception");
		}
	}

	/**
	 * @testID testDmtData007
	 * @testDescription This method asserts that the Data format of DmtData is
	 *                  equal to DmtData.FORMAT_BINARY and that the string value
	 *                  passed is the same as dd.getString()
	 */
	private void testDmtData007() {
		try {
			tbc.log("#testDmtData007");
			byte[] b = new byte[1024];
			dd = new org.osgi.service.dmt.DmtData(b);

			tbc.assertEquals("Asserting BINARY format",
					org.osgi.service.dmt.DmtData.FORMAT_BINARY, dd.getFormat());

			tbc.assertEquals("Asserting same data value", b, dd.getBinary());
		}catch(Exception e){
			tbc.fail("Unexpected Exception");
		}
	}
	
	/**
	 * @testID testDmtData008
	 * @testDescription This method asserts if getInt method throws a
	 *                  DmtException when the DmtData is created with a boolean
	 */
	private void testDmtData008() {

		try {
			tbc.log("#testDmtData008");
			dd = new org.osgi.service.dmt.DmtData(true);
			dd.getInt();
			tbc.failException("#",DmtException.class);
		}catch(DmtException e) {
			tbc.assertEquals("Asserts DmtException code",DmtException.OTHER_ERROR,e.getCode());
		}catch(Exception e){
			tbc.fail("Unexpected Exception");
		}
	}
	/**
	 * @testID testDmtData009
	 * @testDescription This method asserts if getInt method throws a
	 *                  DmtException when the DmtData is created with a byte
	 */
	private void testDmtData009() {

		try {
			tbc.log("#testDmtData009");
			dd = new org.osgi.service.dmt.DmtData(new byte[1]);
			dd.getInt();
			tbc.failException("#",DmtException.class);
		}catch(DmtException e) {
			tbc.assertEquals("Asserts DmtException code",DmtException.OTHER_ERROR,e.getCode());
		}catch(Exception e){
			tbc.fail("Unexpected Exception");
		}
	}
	/**
	 * @testID testDmtData010
	 * @testDescription This method asserts if getInt method throws a
	 *                  DmtException when the DmtData is created with a string
	 */
	private void testDmtData010() {

		try {
			tbc.log("#testDmtData010");
			dd = new org.osgi.service.dmt.DmtData("a");
			dd.getInt();
			tbc.failException("#",DmtException.class);
		}catch(DmtException e) {
			tbc.assertEquals("Asserts DmtException code",DmtException.OTHER_ERROR,e.getCode());
		}catch(Exception e){
			tbc.fail("Unexpected Exception");
		}
	}
	/**
	 * @testID testDmtData011
	 * @testDescription This method asserts if getInt method throws a
	 *                  DmtException when the DmtData is created with a xml
	 */
	private void testDmtData011() {
		try {
			tbc.log("#testDmtData011");
			dd = new org.osgi.service.dmt.DmtData("<test1></test1>",true);
			dd.getInt();
			tbc.failException("#",DmtException.class);
		}catch(DmtException e) {
			tbc.assertEquals("Asserts DmtException code",DmtException.OTHER_ERROR,e.getCode());
		}catch(Exception e){
			tbc.fail("Unexpected Exception");
		}
	}
	/**
	 * @testID testDmtData012
	 * @testDescription This method asserts if getBinary method throws a
	 *                  DmtException when the DmtData is created with a boolean
	 */
	private void testDmtData012() {
		try {
			tbc.log("#testDmtData012");
			dd = new org.osgi.service.dmt.DmtData(true);
			dd.getBinary();
			tbc.failException("#",DmtException.class);
		}catch(DmtException e) {
			tbc.assertEquals("Asserts DmtException code",DmtException.OTHER_ERROR,e.getCode());
		}catch(Exception e){
			tbc.fail("Unexpected Exception");
		}
	}
	/**
	 * @testID testDmtData013
	 * @testDescription This method asserts if getBinary method throws a
	 *                  DmtException when the DmtData is created with an int
	 */
	private void testDmtData013() {
		try {
			tbc.log("#testDmtData013");
			dd = new org.osgi.service.dmt.DmtData(1);
			dd.getBinary();
			tbc.failException("#",DmtException.class);
		}catch(DmtException e) {
			tbc.assertEquals("Asserts DmtException code",DmtException.OTHER_ERROR,e.getCode());
		}catch(Exception e){
			tbc.fail("Unexpected Exception");
		}
	}
	/**
	 * @testID testDmtData014
	 * @testDescription This method asserts if getBinary method throws a
	 *                  DmtException when the DmtData is created with a string
	 */
	private void testDmtData014() {
		try {
			tbc.log("#testDmtData014");
			dd = new org.osgi.service.dmt.DmtData("a");
			dd.getBinary();
			tbc.failException("#",DmtException.class);
		}catch(DmtException e) {
			tbc.assertEquals("Asserts DmtException code",DmtException.OTHER_ERROR,e.getCode());
		}catch(Exception e){
			tbc.fail("Unexpected Exception");
		}
	}
	/**
	 * @testID testDmtData015
	 * @testDescription This method asserts if getBinary method throws a
	 *                  DmtException when the DmtData is created with a xml
	 */
	private void testDmtData015() {
		try {
			tbc.log("#testDmtData015");
			dd = new org.osgi.service.dmt.DmtData("<test1></test1>",true);
			dd.getBinary();
			tbc.failException("#",DmtException.class);
		}catch(DmtException e) {
			tbc.assertEquals("Asserts DmtException code",DmtException.OTHER_ERROR,e.getCode());
		}catch(Exception e){
			tbc.fail("Unexpected Exception");
		}
	}		
	
	/**
	 * @testID testDmtData016
	 * @testDescription This method asserts if getBoolean method throws a
	 *                  DmtException when the DmtData is created with a byte
	 */
	private void testDmtData016() {
		try {
			tbc.log("#testDmtData016");
			dd = new org.osgi.service.dmt.DmtData(new byte[1]);
			dd.getBoolean();
			tbc.failException("#",DmtException.class);
		}catch(DmtException e) {
			tbc.assertEquals("Asserts DmtException code",DmtException.OTHER_ERROR,e.getCode());
		}catch(Exception e){
			tbc.fail("Unexpected Exception");
		}
	}
	/**
	 * @testID testDmtData017
	 * @testDescription This method asserts if getBoolean method throws a
	 *                  DmtException when the DmtData is created with an int
	 */
	private void testDmtData017() {
		try {
			tbc.log("#testDmtData017");
			dd = new org.osgi.service.dmt.DmtData(1);
			dd.getBoolean();
			tbc.failException("#",DmtException.class);
		}catch(DmtException e) {
			tbc.assertEquals("Asserts DmtException code",DmtException.OTHER_ERROR,e.getCode());
		}catch(Exception e){
			tbc.fail("Unexpected Exception");
		}
	}
	/**
	 * @testID testDmtData018
	 * @testDescription This method asserts if getBoolean method throws a
	 *                  DmtException when the DmtData is created with a string
	 */
	private void testDmtData018() {
		try {
			tbc.log("#testDmtData018");
			dd = new org.osgi.service.dmt.DmtData("a");
			dd.getBoolean();
			tbc.failException("#",DmtException.class);
		}catch(DmtException e) {
			tbc.assertEquals("Asserts DmtException code",DmtException.OTHER_ERROR,e.getCode());
		}catch(Exception e){
			tbc.fail("Unexpected Exception");
		}
	}
	/**
	 * @testID testDmtData019
	 * @testDescription This method asserts if getBoolean method throws a
	 *                  DmtException when the DmtData is created with a xml
	 */
	private void testDmtData019() {
		try {
			tbc.log("#testDmtData019");
			dd = new org.osgi.service.dmt.DmtData("<test1></test1>",true);
			dd.getBoolean();
			tbc.failException("#",DmtException.class);
		}catch(DmtException e) {
			tbc.assertEquals("Asserts DmtException code",DmtException.OTHER_ERROR,e.getCode());
		}catch(Exception e){
			tbc.fail("Unexpected Exception");
		}
	}
	/**
	 * @testID testDmtData020
	 * @testDescription This method asserts if getString method throws a
	 *                  DmtException when the DmtData is created with a byte
	 */
	private void testDmtData020() {
		try {
			tbc.log("#testDmtData020");
			dd = new org.osgi.service.dmt.DmtData(new byte[1]);
			dd.getString();
			tbc.failException("#",DmtException.class);
		}catch(DmtException e) {
			tbc.assertEquals("Asserts DmtException code",DmtException.OTHER_ERROR,e.getCode());
		}catch(Exception e){
			tbc.fail("Unexpected Exception");
		}
	}
	/**
	 * @testID testDmtData021
	 * @testDescription This method asserts if getString method throws a
	 *                  DmtException when the DmtData is created with an int
	 */
	private void testDmtData021() {
		try {
			tbc.log("#testDmtData021");
			dd = new org.osgi.service.dmt.DmtData(1);
			dd.getString();
			tbc.failException("#",DmtException.class);
		}catch(DmtException e) {
			tbc.assertEquals("Asserts DmtException code",DmtException.OTHER_ERROR,e.getCode());
		}catch(Exception e){
			tbc.fail("Unexpected Exception");
		}
	}
	/**
	 * @testID testDmtData022
	 * @testDescription This method asserts if getString method throws a
	 *                  DmtException when the DmtData is created with a boolean
	 */
	private void testDmtData022() {
		try {
			tbc.log("#testDmtData022");
			dd = new org.osgi.service.dmt.DmtData(true);
			dd.getString();
			tbc.failException("#",DmtException.class);
		}catch(DmtException e) {
			tbc.assertEquals("Asserts DmtException code",DmtException.OTHER_ERROR,e.getCode());
		}catch(Exception e){
			tbc.fail("Unexpected Exception");
		}
	}
	/**
	 * @testID testDmtData023
	 * @testDescription This method asserts if getString method throws a
	 *                  DmtException when the DmtData is created with a xml
	 */
	private void testDmtData023() {
		try {
			tbc.log("#testDmtData023");
			dd = new org.osgi.service.dmt.DmtData("<test1></test1>",true);
			dd.getString();
			tbc.failException("#",DmtException.class);
		}catch(DmtException e) {
			tbc.assertEquals("Asserts DmtException code",DmtException.OTHER_ERROR,e.getCode());
		}catch(Exception e){
			tbc.fail("Unexpected Exception");
		}
	}
	/**
	 * @testID testDmtData024
	 * @testDescription This method asserts if getXml method throws a
	 *                  DmtException when the DmtData is created with a byte
	 */
	private void testDmtData024() {
		try {
			tbc.log("#testDmtData024");
			dd = new org.osgi.service.dmt.DmtData(new byte[1]);
			dd.getXml();
			tbc.failException("#",DmtException.class);
		}catch(DmtException e) {
			tbc.assertEquals("Asserts DmtException code",DmtException.OTHER_ERROR,e.getCode());
		}catch(Exception e){
			tbc.fail("Unexpected Exception");
		}
	}
	/**
	 * @testID testDmtData025
	 * @testDescription This method asserts if getXml method throws a
	 *                  DmtException when the DmtData is created with an int
	 */
	private void testDmtData025() {
		try {
			tbc.log("#testDmtData025");
			dd = new org.osgi.service.dmt.DmtData(1);
			dd.getXml();
			tbc.failException("#",DmtException.class);
		}catch(DmtException e) {
			tbc.assertEquals("Asserts DmtException code",DmtException.OTHER_ERROR,e.getCode());
		}catch(Exception e){
			tbc.fail("Unexpected Exception");
		}
	}
	/**
	 * @testID testDmtData026
	 * @testDescription This method asserts if getXml method throws a
	 *                  DmtException when the DmtData is created with a boolean
	 */
	private void testDmtData026() {
		try {
			tbc.log("#testDmtData026");
			dd = new org.osgi.service.dmt.DmtData(true);
			dd.getXml();
			tbc.failException("#",DmtException.class);
		}catch(DmtException e) {
			tbc.assertEquals("Asserts DmtException code",DmtException.OTHER_ERROR,e.getCode());
		}catch(Exception e){
			tbc.fail("Unexpected Exception");
		}
	}
	/**
	 * @testID testDmtData027
	 * @testDescription This method asserts if getXml method throws a
	 *                  DmtException when the DmtData is created with a string
	 */
	private void testDmtData027() {
		try {
			tbc.log("#testDmtData027");
			dd = new org.osgi.service.dmt.DmtData("a");
			dd.getXml();
			tbc.failException("#",DmtException.class);
		}catch(DmtException e) {
			tbc.assertEquals("Asserts DmtException code",DmtException.OTHER_ERROR,e.getCode());
		}catch(Exception e){
			tbc.fail("Unexpected Exception");
		}
	}	
}
