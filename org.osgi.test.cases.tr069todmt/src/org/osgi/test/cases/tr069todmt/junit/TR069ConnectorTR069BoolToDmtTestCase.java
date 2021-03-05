/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.test.cases.tr069todmt.junit;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.tr069todmt.TR069Connector;
import org.osgi.service.tr069todmt.TR069ConnectorFactory;
import org.osgi.service.tr069todmt.TR069Exception;

/**
 * This TestCase checks the conversions of TR069 / TR106 boolean values to the 
 * applicable Dmt values.
 * The applicable formats are defined in the nodes Metadata. The testcase
 * checks the conversions for different metadata conditions.
 * 
 * @author steffen.druesedow@telekom.de
 *
 */
public class TR069ConnectorTR069BoolToDmtTestCase extends TR069ToDmtTestBase {


	public void setUp() throws Exception {
		super.setUp();
		factory = getService(TR069ConnectorFactory.class);
		assertNotNull("Unable to get the TR069ConnectorFactory.", factory);
	}

	/**
	 * This test checks that TR069 Boolean values are set correctly to Dmt nodes
	 * for which the Metadata has FORMAT_BOOLEAN or FORMAT_STRING.
	 * Different string representations of boolean values are used and must be accepted
	 * by the TR069Connector.
	 * 
	 * Setting a boolean value to a node of other formats must not be possible.
	 * 
	 * This test only uses nodes with exactly one format in their metadata.
	 * Nodes without metadata are not considered in this test.
	 * 
	 * @throws Exception
	 */
	public void testTR069BoolToDmtWithSingleFormat() throws Exception {

		// register a plugin that has nodes for each format
		prepareTestNodesWithSingleFormats();
		
		// get a session on the singleton node
		session = dmtAdmin.getSession(ROOT, DmtSession.LOCK_TYPE_ATOMIC);
		connector = factory.create(session);

		// boolean params can be set to nodes of BOOLEAN or STRING format 
		checkTR069BooleanToDmtBoolean("1", true );
		checkTR069BooleanToDmtBoolean("0", false );
		checkTR069BooleanToDmtBoolean("true", true );
		checkTR069BooleanToDmtBoolean("false", false );
		checkTR069BooleanToDmtBoolean("TRUE", true );
		checkTR069BooleanToDmtBoolean("FALSE", false );
		checkTR069BooleanToDmtBoolean("TruE", true );
		checkTR069BooleanToDmtBoolean("FalsE", false );

		checkTR069BooleanToDmtString("1", "true" );
		checkTR069BooleanToDmtString("0", "false" );
		checkTR069BooleanToDmtString("true", "true" );
		checkTR069BooleanToDmtString("false", "false" );
		checkTR069BooleanToDmtString("TRUE", "true" );
		checkTR069BooleanToDmtString("FALSE", "false" );
		checkTR069BooleanToDmtString("TruE", "true" );
		checkTR069BooleanToDmtString("FalsE", "false" );
		
		// try to set invalid values to a boolean node
		assertInvalidValueNotAccepted( BOOLEAN, "-1", TR069Connector.TR069_BOOLEAN);
		assertInvalidValueNotAccepted( BOOLEAN, "xyz", TR069Connector.TR069_BOOLEAN);
		assertInvalidValueNotAccepted( BOOLEAN, "123", TR069Connector.TR069_BOOLEAN);
		assertInvalidValueNotAccepted( BOOLEAN, "a1b2c3", TR069Connector.TR069_BOOLEAN);
		
		// try to set boolean value to invalid nodes
		assertNonListedFormatsNotAccepted( new String[] {BASE64, BINARY, DATE, DATETIME, FLOAT, INTEGER, LONG, RAWBINARY, TIME, XML});
	}
	
	private void checkTR069BooleanToDmtBoolean( String input, boolean expected ) throws Exception {
		connector.setParameterValue(SINGLETON + "." + BOOLEAN, input, TR069Connector.TR069_BOOLEAN );
		DmtData data = session.getNodeValue(SINGLETON + "/" + BOOLEAN);
		assertEquals( "Wrong boolean value found in Dmt for value: " + input, expected, data.getBoolean());
		assertEquals( DmtData.FORMAT_BOOLEAN, data.getFormat());
	}
	private void checkTR069BooleanToDmtString( String input, String expected ) throws Exception {
		connector.setParameterValue(SINGLETON + "." + STRING, input, TR069Connector.TR069_BOOLEAN );
		DmtData data = session.getNodeValue(SINGLETON + "/" + STRING);
		assertEquals( "Wrong String value found in Dmt for value: " + input, expected, data.getString());
		assertEquals( DmtData.FORMAT_STRING, data.getFormat());
	}
	
	/**
	 * This test checks that TR069 Boolean values are set correctly to Dmt nodes
	 * for which no Metadata is available.
	 * This means that the node must generally accept all formats (see residential spec. 117.15.12.14),
	 * but must try to apply the "most applicable" format first. 
	 * The applicable formats and their precedence are specified in section 131.3.11.
	 * 
	 * If the value string for a TR069 Boolean is parsable as boolean value then the expected DMT data 
	 * format is FORMAT_BOOLEAN - all invalid (non-parsable) values must cause a TR069Exception.
	 *  
	 * Different string representations of boolean values are used and must be accepted
	 * by the TR069Connector.
	 * 
	 * @throws Exception
	 */
	public void testTR069BoolToDmtWithoutFormats() throws Exception {
		// register a plugin that has nodes without associated metadata, i.e. no declared formats
		prepareTestNodeWithoutFormats();
		
		// get a session on the singleton node
		session = dmtAdmin.getSession(ROOT, DmtSession.LOCK_TYPE_ATOMIC);
		connector = factory.create(session);
		
		checkTR069BooleanToDmtUnknown("1", true );
		checkTR069BooleanToDmtUnknown("0", false );
		checkTR069BooleanToDmtUnknown("true", true );
		checkTR069BooleanToDmtUnknown("false", false );
		checkTR069BooleanToDmtUnknown("TRUE", true );
		checkTR069BooleanToDmtUnknown("FALSE", false );
		checkTR069BooleanToDmtUnknown("TruE", true );
		checkTR069BooleanToDmtUnknown("FalsE", false );

		// try to set invalid values
		assertInvalidValueNotAccepted( UNKNOWN, "-1", TR069Connector.TR069_BOOLEAN);
		assertInvalidValueNotAccepted( UNKNOWN, "xyz", TR069Connector.TR069_BOOLEAN);
		assertInvalidValueNotAccepted( UNKNOWN, "123", TR069Connector.TR069_BOOLEAN);
		assertInvalidValueNotAccepted( UNKNOWN, "a1b2c3", TR069Connector.TR069_BOOLEAN);
	}
	
	private void checkTR069BooleanToDmtUnknown( String input, boolean expected ) throws Exception {
		connector.setParameterValue(SINGLETON + "." + UNKNOWN, input, TR069Connector.TR069_BOOLEAN );
		DmtData data = session.getNodeValue(SINGLETON + "/" + UNKNOWN);
		assertEquals( "Wrong boolean value found in Dmt for value: " + input, expected, data.getBoolean());
		assertEquals( DmtData.FORMAT_BOOLEAN, data.getFormat());
	}

	/**
	 * This test checks that TR069 Boolean values are set correctly to Dmt nodes
	 * for which the Metadata reports multiple formats.
	 * 
	 * If the value string for a TR069 Boolean is parsable as boolean value then the expected DMT data 
	 * format is FORMAT_BOOLEAN - all invalid (non-parsable) values must cause a TR069Exception.
	 *  
	 * Different string representations of boolean values are used and must be accepted
	 * by the TR069Connector.
	 * 
	 * @throws Exception
	 */
	public void testTR069BoolToDmtWithMultipleFormats() throws Exception {
		// register a plugin that has nodes with the given OR-ed formats
		prepareTestNodeWithFormats(ALL_DMT_FORMATS);
		
		// get a session on the singleton node
		session = dmtAdmin.getSession(ROOT, DmtSession.LOCK_TYPE_ATOMIC);
		connector = factory.create(session);
		
		// all parsable boolean values must result in DmtData.FORMAT_BOOLEAN values
		checkTR069BooleanToDmtUnknown("1", true );
		checkTR069BooleanToDmtUnknown("0", false );
		checkTR069BooleanToDmtUnknown("true", true );
		checkTR069BooleanToDmtUnknown("false", false );
		checkTR069BooleanToDmtUnknown("TRUE", true );
		checkTR069BooleanToDmtUnknown("FALSE", false );
		checkTR069BooleanToDmtUnknown("TruE", true );
		checkTR069BooleanToDmtUnknown("FalsE", false );

		// all invalid values must cause a TR069Exception
		assertInvalidValueNotAccepted( UNKNOWN, "-1", TR069Connector.TR069_BOOLEAN);
		assertInvalidValueNotAccepted( UNKNOWN, "xyz", TR069Connector.TR069_BOOLEAN);
		assertInvalidValueNotAccepted( UNKNOWN, "123", TR069Connector.TR069_BOOLEAN);
		assertInvalidValueNotAccepted( UNKNOWN, "a1b2c3", TR069Connector.TR069_BOOLEAN);
	}

	private void assertInvalidValueNotAccepted(String node, String value, int type) throws Exception {
		try {
			connector.setParameterValue(SINGLETON + "." + node, value, type );
			fail("This conversion must not be possible because it can not be parsed as Boolean");
		} catch (TR069Exception e) {
			assertEquals( "An invalid value in setParameterValue must cause a TR069Exception.INVALID_PARAMETER_VALUE", TR069Exception.INVALID_PARAMETER_VALUE, e.getFaultCode() );
		}
	}

	private void assertNonListedFormatsNotAccepted(String[] nodes) throws Exception {
		for (String node : nodes) {
			try {
				connector.setParameterValue(SINGLETON + "." + node, "true", TR069Connector.TR069_BOOLEAN);
				fail("Conversions to formats that are not listed in the nodes MetaData must not be possible.");
			} catch (TR069Exception e) {
				assertTrue( "Conversions to formats that are not listed in the nodes MetaData must not be possible.", e.getFaultCode() >= 9001 && e.getFaultCode() <= 9007 );
			}
		}
	}
}
