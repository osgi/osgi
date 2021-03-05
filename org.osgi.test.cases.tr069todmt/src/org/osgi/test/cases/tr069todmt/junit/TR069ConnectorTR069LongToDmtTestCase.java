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
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtIllegalStateException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.tr069todmt.TR069Connector;
import org.osgi.service.tr069todmt.TR069ConnectorFactory;
import org.osgi.service.tr069todmt.TR069Exception;

/**
 * This TestCase checks the conversions of TR069 / TR106 Long values to the 
 * applicable Dmt values.
 * The applicable formats are defined in the nodes Metadata. The testcase
 * checks the conversions for different metadata conditions.
 * 
 * @author steffen.druesedow@telekom.de
 *
 */
public class TR069ConnectorTR069LongToDmtTestCase extends TR069ToDmtTestBase {

	public void setUp() throws Exception {
		super.setUp();
		factory = getService(TR069ConnectorFactory.class);
		assertNotNull("Unable to get the TR069ConnectorFactory.", factory);
	}

	/**
	 * This test checks the conversion of TR069 Long values to Dmt.
	 * 
	 * The applicable Dmt formats are specified in the target nodes metadata.
	 * For long values these are in order of applicability:
	 * FORMAT_LONG, FORMAT_FLOAT, FORMAT_INTEGER, FORMAT_STRING
	 * 
	 * This test only uses exactly one target format per node. 
	 * It is tested that the Connector only performs conversions to the applicable format.
	 * A TR069Exception of type INVALID_PARAMETER_VALUE is expected if the value can't be 
	 * converted to the given format.
	 * 
	 * @throws Exception
	 */
	public void testTR069LongToDmtWithSingleFormat() throws Exception {

		// register a plugin that has nodes for each format
		prepareTestNodesWithSingleFormats();
		
		// get a session on the singleton node
		session = dmtAdmin.getSession(ROOT, DmtSession.LOCK_TYPE_ATOMIC);
		connector = factory.create(session);
		
		// long params can be set to nodes of LONG, FLOAT, INTEGER or STRING format 
		connector.setParameterValue(SINGLETON + "." + LONG, "123", TR069Connector.TR069_LONG );
		DmtData data = session.getNodeValue(SINGLETON + "/" + LONG);
		try {
			assertEquals( "Wrong value found in Dmt for value: " + 123, 123l, data.getLong() );
		} catch (DmtIllegalStateException e) {
			fail( "Expected DmtData value of FORMAT_LONG");
		}
		assertEquals( DmtData.FORMAT_LONG, data.getFormat());

		connector.setParameterValue(SINGLETON + "." + FLOAT, "123", TR069Connector.TR069_LONG );
		data = session.getNodeValue(SINGLETON + "/" + FLOAT);
		try {
			assertEquals( "Wrong value found in Dmt for value: " + 123, 123f, data.getFloat() );
		} catch (DmtIllegalStateException e) {
			fail( "Expected DmtData value of FORMAT_FLOAT");
		}
		assertEquals( DmtData.FORMAT_FLOAT, data.getFormat());

		connector.setParameterValue(SINGLETON + "." + INTEGER, "123", TR069Connector.TR069_LONG );
		data = session.getNodeValue(SINGLETON + "/" + INTEGER);
		try {
			assertEquals( "Wrong value found in Dmt for value: " + 123, 123, data.getInt() );
		} catch (DmtIllegalStateException e) {
			fail( "Expected DmtData value of FORMAT_INTEGER");
		}
		assertEquals( DmtData.FORMAT_INTEGER, data.getFormat());

		connector.setParameterValue(SINGLETON + "." + STRING, "123", TR069Connector.TR069_LONG );
		data = session.getNodeValue(SINGLETON + "/" + STRING);
		try {
			assertEquals( "Wrong value found in Dmt for value: " + 123, "123", data.getString() );
		} catch (DmtIllegalStateException e) {
			fail( "Expected DmtData value of FORMAT_STRING");
		}
		assertEquals( DmtData.FORMAT_STRING, data.getFormat());

		// non parsable values must be set directly to String nodes (see table 131.7 TR-069 Value to Dmt Data)
		try {
			connector.setParameterValue(SINGLETON + "." + STRING, "123xu", TR069Connector.TR069_LONG );
			data = session.getNodeValue(SINGLETON + "/" + STRING);
			assertEquals( "123xu", data.getString());
			assertEquals( DmtData.FORMAT_STRING, data.getFormat());
		} catch (TR069Exception e) {
			fail("Non number values must be set unchanged to Dmt nodes with FORMAT_STRING.");
		}

		// trying to set values that are not parsable as a number
		try {
			connector.setParameterValue(SINGLETON + "." + LONG, "123xu", TR069Connector.TR069_LONG );
			connector.setParameterValue(SINGLETON + "." + LONG, "hello", TR069Connector.TR069_LONG );
			connector.setParameterValue(SINGLETON + "." + INTEGER, "123xu", TR069Connector.TR069_LONG );
			connector.setParameterValue(SINGLETON + "." + INTEGER, "hello", TR069Connector.TR069_LONG );
			connector.setParameterValue(SINGLETON + "." + FLOAT, "123xu", TR069Connector.TR069_LONG );
			connector.setParameterValue(SINGLETON + "." + FLOAT, "hello", TR069Connector.TR069_LONG );
			fail("These conversions must not be possible because the given value can't be parsed as a number.");
		} catch (TR069Exception e) {
			assertEquals( TR069Exception.INVALID_PARAMETER_VALUE, e.getFaultCode() );
		}
		
		// try to set integer value to nodes with non-applicable formats
		try {
			connector.setParameterValue(SINGLETON + "." + BASE64, "123", TR069Connector.TR069_LONG );
			connector.setParameterValue(SINGLETON + "." + BINARY, "123", TR069Connector.TR069_LONG );
			connector.setParameterValue(SINGLETON + "." + BOOLEAN, "123", TR069Connector.TR069_LONG );
			connector.setParameterValue(SINGLETON + "." + DATE, "123", TR069Connector.TR069_LONG );
			connector.setParameterValue(SINGLETON + "." + DATETIME, "123", TR069Connector.TR069_LONG );
			connector.setParameterValue(SINGLETON + "." + RAWBINARY, "123", TR069Connector.TR069_LONG );
			connector.setParameterValue(SINGLETON + "." + RAWSTRING, "123", TR069Connector.TR069_LONG );
			connector.setParameterValue(SINGLETON + "." + TIME, "123", TR069Connector.TR069_LONG );
			connector.setParameterValue(SINGLETON + "." + XML, "123", TR069Connector.TR069_LONG );

			fail("Conversions to formats that are not listed in the nodes MetaData must not be possible.");
		} catch (TR069Exception e) {
			// TODO: what fault codes to check here exactly ? 
			assertTrue( e.getFaultCode() >= 9001 && e.getFaultCode() <= 9007 );
		}
	}

	
	/**
	 * This test checks that TR069 long values are set correctly to Dmt nodes
	 * for which no Metadata is available.
	 * This means that the node must generally accept all formats (see residential spec. 117.15.12.14),
	 * but must try to apply the "most applicable" format first. 
	 * The applicable formats and their precedence are specified in section 131.3.11.
	 * 
	 * The applicability for TR069 long values is:
	 * FORMAT_LONG, FORMAT_FLOAT, FORMAT_INTEGER, FORMAT_STRING
	 *
	 * TODO: Is that understanding correct?
	 * If the value can't be parsed as one of the number formats, then it is used directly and unchanged as String.
	 * 
	 * @throws Exception
	 */
	public void testTR069BoolToDmtWithoutFormats() throws Exception {
		// register a plugin that has nodes without associated metadata, i.e. no declared formats
		prepareTestNodeWithoutFormats();
		
		// get a session on the singleton node
		session = dmtAdmin.getSession(ROOT, DmtSession.LOCK_TYPE_ATOMIC);
		connector = factory.create(session);
		
		applyValues();
	}
	
	/**
	 * This test checks that TR069 int values are set correctly to Dmt nodes
	 * for which no Metadata reports multiple formats.
	 * The applicable formats and their precedence are specified in section 131.3.11.
	 * 
	 * The applicability for TR069 int values is:
	 * FORMAT_INTEGER, FORMAT_LONG, FORMAT_FLOAT, FORMAT_STRING
	 *
	 * This test provides a node that supports all formats OR-ed together.
	 * It is checked that the values are converted according to their applicability.
	 * 
	 * @throws Exception
	 */
	public void testTR069BoolToDmtWithMultipleFormats() throws Exception {
		// register a plugin that has nodes with the given OR-ed formats
		prepareTestNodeWithFormats(ALL_DMT_FORMATS);
		
		// get a session on the singleton node
		session = dmtAdmin.getSession(ROOT, DmtSession.LOCK_TYPE_ATOMIC);
		connector = factory.create(session);
		
		applyValues();
	}

	/**
	 * Applies different values that can be converted to different number formats.
	 * If parsing to a number fails then the value is set as FORMAT_STRING.
	 * @throws DmtException
	 */
	private void applyValues() throws DmtException {
		// set long value - expect long
		connector.setParameterValue(SINGLETON + "." + UNKNOWN, "123", TR069Connector.TR069_LONG );
		DmtData data = session.getNodeValue(SINGLETON + "/" + UNKNOWN);
		try {
			assertEquals( "Wrong value found in Dmt for value: " + 123, 123l, data.getLong() );
		} catch (DmtIllegalStateException e) {
			fail( "Expected DmtData value of FORMAT_LONG");
		}
		assertEquals( DmtData.FORMAT_LONG, data.getFormat());
		
		// set rational number - expect float
		float floatValue = 123.456f;
		connector.setParameterValue(SINGLETON + "." + UNKNOWN, "" + floatValue, TR069Connector.TR069_LONG );
		data = session.getNodeValue(SINGLETON + "/" + UNKNOWN);
		try {
			assertEquals( "Wrong value found in Dmt for value: " + floatValue, floatValue, data.getFloat() );
		} catch (DmtIllegalStateException e) {
			fail( "Expected DmtData value of FORMAT_FLOAT");
		}
		assertEquals( DmtData.FORMAT_FLOAT, data.getFormat());
		
		// TODO: no way to force an int value here, right?

		// set something that can't be parsed as number - expect same value as String
		String unparsableValue = "Hello World!";
		connector.setParameterValue(SINGLETON + "." + UNKNOWN, "" + unparsableValue, TR069Connector.TR069_LONG );
		data = session.getNodeValue(SINGLETON + "/" + UNKNOWN);
		try {
			assertEquals( "Wrong value found in Dmt for value: " + unparsableValue, unparsableValue, data.getString() );
		} catch (DmtIllegalStateException e) {
			fail( "Expected DmtData value of FORMAT_STRING");
		}
		assertEquals( DmtData.FORMAT_STRING, data.getFormat());
	}
}
