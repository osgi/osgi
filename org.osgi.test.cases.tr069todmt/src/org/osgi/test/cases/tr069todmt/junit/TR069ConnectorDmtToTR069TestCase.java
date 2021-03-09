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

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.service.tr069todmt.ParameterValue;
import org.osgi.service.tr069todmt.TR069Connector;
import org.osgi.service.tr069todmt.TR069ConnectorFactory;
import org.osgi.test.cases.tr069todmt.plugins.MetaNode;
import org.osgi.test.cases.tr069todmt.plugins.Node;
import org.osgi.test.cases.tr069todmt.plugins.TestDataPlugin;

/**
 * This TestCase checks the toUri and toPath methods of the TR069Connector including 
 * encoding and decoding. 
 * 
 * @author steffen.druesedow@telekom.de
 *
 */
public class TR069ConnectorDmtToTR069TestCase extends TR069ToDmtTestBase {
	// TODO: check conversions for Date and Time types

	
	final byte[] HEX_CHAR_TABLE = {
		(byte)'0', (byte)'1', (byte)'2', (byte)'3',
		(byte)'4', (byte)'5', (byte)'6', (byte)'7',
		(byte)'8', (byte)'9', (byte)'A', (byte)'B',
		(byte)'C', (byte)'D', (byte)'E', (byte)'F'
	};    


	public void setUp() throws Exception {
		super.setUp();
		factory = getService(TR069ConnectorFactory.class);
		assertNotNull("Unable to get the TR069ConnectorFactory.", factory);
	}

	
	/**
	 * This test checks the conversion of Dmt number formats to TR069 numbers.
	 * 
	 * @throws Exception
	 */
	public void testDmtNumbersToTR069() throws Exception {
		String expected = "123";
		Node rootNode = new Node(null, "mapped plugin root", false, null, null );
		
		Node singletonNode = new Node(rootNode, SINGLETON, false, null, null );
		Node integerNode = new Node( singletonNode, "integer", true, new DmtData(123), null );
		Node longNode = new Node( singletonNode, "long", true, new DmtData(123l), null );
		Node floatNode = new Node( singletonNode, "float", true, new DmtData(123.9f), null );
		
		TestDataPlugin plugin = new TestDataPlugin("testplugin", rootNode);
		Dictionary<String, String> props = new Hashtable<String, String>();
		props.put(DataPlugin.DATA_ROOT_URIS, ROOT );
		registerService(DataPlugin.class.getName(), plugin, props);

		// get a session on the singleton node
		session = dmtAdmin.getSession(ROOT);
		connector = factory.create(session);

		// check default conversions
		assertValue(connector.getParameterValue(SINGLETON + ".integer"), expected, TR069Connector.TR069_INT);
		assertValue(connector.getParameterValue(SINGLETON + ".long"), expected, TR069Connector.TR069_LONG);
		assertValue(connector.getParameterValue(SINGLETON + ".float"), expected, TR069Connector.TR069_LONG);
		
		// check allowed non-default conversions for INTEGER nodes
		assertValueWithMimetype(integerNode, TR069Connector.TR069_MIME_LONG, expected, TR069Connector.TR069_LONG);
		assertValueWithMimetype(integerNode, TR069Connector.TR069_MIME_STRING, expected, TR069Connector.TR069_STRING);
		assertValueWithMimetype(integerNode, TR069Connector.TR069_MIME_UNSIGNED_INT, expected, TR069Connector.TR069_UNSIGNED_INT);
		assertValueWithMimetype(integerNode, TR069Connector.TR069_MIME_UNSIGNED_LONG, expected, TR069Connector.TR069_UNSIGNED_LONG);

		// check allowed non-default conversions for LONG nodes
		assertValueWithMimetype(longNode, TR069Connector.TR069_MIME_INT, expected, TR069Connector.TR069_INT);
		assertValueWithMimetype(longNode, TR069Connector.TR069_MIME_STRING, expected, TR069Connector.TR069_STRING);
		assertValueWithMimetype(longNode, TR069Connector.TR069_MIME_UNSIGNED_INT, expected, TR069Connector.TR069_UNSIGNED_INT);
		assertValueWithMimetype(longNode, TR069Connector.TR069_MIME_UNSIGNED_LONG, expected, TR069Connector.TR069_UNSIGNED_LONG);

		// check allowed non-default conversions for FLOAT nodes
		assertValueWithMimetype(floatNode, TR069Connector.TR069_MIME_INT, expected, TR069Connector.TR069_INT);
		assertValueWithMimetype(floatNode, TR069Connector.TR069_MIME_STRING, "123.9", TR069Connector.TR069_STRING);
		assertValueWithMimetype(floatNode, TR069Connector.TR069_MIME_UNSIGNED_INT, expected, TR069Connector.TR069_UNSIGNED_INT);
		assertValueWithMimetype(floatNode, TR069Connector.TR069_MIME_UNSIGNED_LONG, expected, TR069Connector.TR069_UNSIGNED_LONG);
		
		// TODO: What reaction exactly is expected, if an invalid conversion is requested?
		// the spec says: "Cells that are empty in the table indicate an impossible conversion that must be reported."
		// --> Test that the result is null and/or an exception is thrown
		assertValueWithInvalidMimetype(integerNode, TR069Connector.TR069_MIME_BASE64 );
		assertValueWithInvalidMimetype(integerNode, TR069Connector.TR069_MIME_BOOLEAN );
		assertValueWithInvalidMimetype(integerNode, TR069Connector.TR069_MIME_DATETIME );
		assertValueWithInvalidMimetype(integerNode, TR069Connector.TR069_MIME_HEXBINARY );

		assertValueWithInvalidMimetype(longNode, TR069Connector.TR069_MIME_BASE64 );
		assertValueWithInvalidMimetype(longNode, TR069Connector.TR069_MIME_BOOLEAN );
		assertValueWithInvalidMimetype(longNode, TR069Connector.TR069_MIME_DATETIME );
		assertValueWithInvalidMimetype(longNode, TR069Connector.TR069_MIME_HEXBINARY );

		assertValueWithInvalidMimetype(floatNode, TR069Connector.TR069_MIME_BASE64 );
		assertValueWithInvalidMimetype(floatNode, TR069Connector.TR069_MIME_BOOLEAN );
		assertValueWithInvalidMimetype(floatNode, TR069Connector.TR069_MIME_DATETIME );
		assertValueWithInvalidMimetype(floatNode, TR069Connector.TR069_MIME_HEXBINARY );
	}

	
	/**
	 * This test checks the conversion of Dmt number formats to TR069 numbers.
	 * TODO: check result of discussion for bug 2204
	 * 
	 * @throws Exception
	 */
	public void testDmtBinaryToTR069() throws Exception {
		byte[] bytes = new byte[] {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
		Node rootNode = new Node(null, "mapped plugin root", false, null, null );
		
		Node singletonNode = new Node(rootNode, SINGLETON, false, null, null );
		Node binaryNode = new Node( singletonNode, "binary", true, new DmtData(bytes, DmtData.FORMAT_BINARY), null );
		Node base64Node = new Node( singletonNode, "base64", true, new DmtData(bytes, DmtData.FORMAT_BASE64), null );
		// raw binary is created with the 
		Node rawBinaryNode = new Node( singletonNode, "rawbinary", true, new DmtData("TEST_FORMAT_RAW_BINARY", bytes), null );
		
		TestDataPlugin plugin = new TestDataPlugin("testplugin", rootNode);
		Dictionary<String, String> props = new Hashtable<String, String>();
		props.put(DataPlugin.DATA_ROOT_URIS, ROOT );
		registerService(DataPlugin.class.getName(), plugin, props);

		// get a session on the singleton node
		session = dmtAdmin.getSession(ROOT);
		connector = factory.create(session);

		// check default conversions (default is Base64)
		String expected = new String(Base64.getEncoder().encode(bytes));
		assertValue(connector.getParameterValue(SINGLETON + ".binary"), expected, TR069Connector.TR069_BASE64);
		assertValue(connector.getParameterValue(SINGLETON + ".base64"), expected, TR069Connector.TR069_BASE64);
		assertValue(connector.getParameterValue(SINGLETON + ".rawbinary"), expected, TR069Connector.TR069_BASE64);

		// check allowed non-default conversions
		expected = toHex(bytes);
		assertValueWithMimetype(binaryNode, TR069Connector.TR069_MIME_HEXBINARY, expected, TR069Connector.TR069_HEXBINARY);
		assertValueWithMimetype(base64Node, TR069Connector.TR069_MIME_HEXBINARY, expected, TR069Connector.TR069_HEXBINARY);
		assertValueWithMimetype(rawBinaryNode, TR069Connector.TR069_MIME_HEXBINARY, expected, TR069Connector.TR069_HEXBINARY);

		assertValueWithInvalidMimetype(binaryNode, TR069Connector.TR069_MIME_BOOLEAN );
		assertValueWithInvalidMimetype(binaryNode, TR069Connector.TR069_MIME_DATETIME );
		assertValueWithInvalidMimetype(binaryNode, TR069Connector.TR069_MIME_INT );
		assertValueWithInvalidMimetype(binaryNode, TR069Connector.TR069_MIME_LONG );
		assertValueWithInvalidMimetype(binaryNode, TR069Connector.TR069_MIME_STRING );
		assertValueWithInvalidMimetype(binaryNode, TR069Connector.TR069_MIME_STRING_LIST );
		assertValueWithInvalidMimetype(binaryNode, TR069Connector.TR069_MIME_UNSIGNED_INT);
		assertValueWithInvalidMimetype(binaryNode, TR069Connector.TR069_MIME_UNSIGNED_LONG );

		assertValueWithInvalidMimetype(base64Node, TR069Connector.TR069_MIME_BOOLEAN );
		assertValueWithInvalidMimetype(base64Node, TR069Connector.TR069_MIME_DATETIME );
		assertValueWithInvalidMimetype(base64Node, TR069Connector.TR069_MIME_INT );
		assertValueWithInvalidMimetype(base64Node, TR069Connector.TR069_MIME_LONG );
		assertValueWithInvalidMimetype(base64Node, TR069Connector.TR069_MIME_STRING );
		assertValueWithInvalidMimetype(base64Node, TR069Connector.TR069_MIME_STRING_LIST );
		assertValueWithInvalidMimetype(base64Node, TR069Connector.TR069_MIME_UNSIGNED_INT);
		assertValueWithInvalidMimetype(base64Node, TR069Connector.TR069_MIME_UNSIGNED_LONG );

		assertValueWithInvalidMimetype(rawBinaryNode, TR069Connector.TR069_MIME_BOOLEAN );
		assertValueWithInvalidMimetype(rawBinaryNode, TR069Connector.TR069_MIME_DATETIME );
		assertValueWithInvalidMimetype(rawBinaryNode, TR069Connector.TR069_MIME_INT );
		assertValueWithInvalidMimetype(rawBinaryNode, TR069Connector.TR069_MIME_LONG );
		assertValueWithInvalidMimetype(rawBinaryNode, TR069Connector.TR069_MIME_STRING );
		assertValueWithInvalidMimetype(rawBinaryNode, TR069Connector.TR069_MIME_STRING_LIST );
		assertValueWithInvalidMimetype(rawBinaryNode, TR069Connector.TR069_MIME_UNSIGNED_INT);
		assertValueWithInvalidMimetype(rawBinaryNode, TR069Connector.TR069_MIME_UNSIGNED_LONG );
	}
	
	/**
	 * This test checks the conversion of Dmt String format to TR069 Strings
	 * 
	 * @throws Exception
	 */
	public void testDmtStringToTR069String() throws Exception {
		String expected = "This is the expected teststring."; 
		Node rootNode = new Node(null, "mapped plugin root", false, null, null );
		
		Node singletonNode = new Node(rootNode, SINGLETON, false, null, null );
		Node stringNode = new Node( singletonNode, "string", true, new DmtData(expected), null );
		Node rawStringNode = new Node( singletonNode, "rawstring", true, new DmtData("TEST_FORMAT_RAW_STRING", expected), null );
		
		TestDataPlugin plugin = new TestDataPlugin("testplugin", rootNode);
		Dictionary<String, String> props = new Hashtable<String, String>();
		props.put(DataPlugin.DATA_ROOT_URIS, ROOT );
		registerService(DataPlugin.class.getName(), plugin, props);

		// get a session on the singleton node
		session = dmtAdmin.getSession(ROOT);
		connector = factory.create(session);

		// check default conversions 
		assertValue(connector.getParameterValue(SINGLETON + ".string"), expected, TR069Connector.TR069_STRING);
		assertValue(connector.getParameterValue(SINGLETON + ".rawstring"), expected, TR069Connector.TR069_STRING);

		// check allowed non-default conversions

		assertValueWithInvalidMimetype(stringNode, TR069Connector.TR069_MIME_BASE64 );
		assertValueWithInvalidMimetype(stringNode, TR069Connector.TR069_MIME_BOOLEAN );
		assertValueWithInvalidMimetype(stringNode, TR069Connector.TR069_MIME_DATETIME );
		assertValueWithInvalidMimetype(stringNode, TR069Connector.TR069_MIME_HEXBINARY );
		assertValueWithInvalidMimetype(stringNode, TR069Connector.TR069_MIME_INT );
		assertValueWithInvalidMimetype(stringNode, TR069Connector.TR069_MIME_LONG );
		assertValueWithInvalidMimetype(stringNode, TR069Connector.TR069_MIME_STRING_LIST );
		assertValueWithInvalidMimetype(stringNode, TR069Connector.TR069_MIME_UNSIGNED_INT);
		assertValueWithInvalidMimetype(stringNode, TR069Connector.TR069_MIME_UNSIGNED_LONG );

		assertValueWithInvalidMimetype(rawStringNode, TR069Connector.TR069_MIME_BASE64 );
		assertValueWithInvalidMimetype(rawStringNode, TR069Connector.TR069_MIME_BOOLEAN );
		assertValueWithInvalidMimetype(rawStringNode, TR069Connector.TR069_MIME_DATETIME );
		assertValueWithInvalidMimetype(rawStringNode, TR069Connector.TR069_MIME_HEXBINARY );
		assertValueWithInvalidMimetype(rawStringNode, TR069Connector.TR069_MIME_INT );
		assertValueWithInvalidMimetype(rawStringNode, TR069Connector.TR069_MIME_LONG );
		assertValueWithInvalidMimetype(rawStringNode, TR069Connector.TR069_MIME_STRING_LIST );
		assertValueWithInvalidMimetype(rawStringNode, TR069Connector.TR069_MIME_UNSIGNED_INT);
		assertValueWithInvalidMimetype(rawStringNode, TR069Connector.TR069_MIME_UNSIGNED_LONG );
	}

	/**
	 * This test checks the conversion of Dmt Boolean format to TR069 
	 * 
	 * @throws Exception
	 */
	public void testDmtBooleanToTR069() throws Exception {
		Node rootNode = new Node(null, "mapped plugin root", false, null, null );
		
		Node singletonNode = new Node(rootNode, SINGLETON, false, null, null );
		Node boolNodeTrue = new Node( singletonNode, "trueNode", true, new DmtData(true), null );
		Node boolNodeFalse = new Node( singletonNode, "falseNode", true, new DmtData(false), null );
		
		TestDataPlugin plugin = new TestDataPlugin("testplugin", rootNode);
		Dictionary<String, String> props = new Hashtable<String, String>();
		props.put(DataPlugin.DATA_ROOT_URIS, ROOT );
		registerService(DataPlugin.class.getName(), plugin, props);

		// get a session on the singleton node
		session = dmtAdmin.getSession(ROOT);
		connector = factory.create(session);

		// check default conversions 
		assertValue(connector.getParameterValue(SINGLETON + ".trueNode"), "true", TR069Connector.TR069_BOOLEAN);
		assertValue(connector.getParameterValue(SINGLETON + ".falseNode"), "false", TR069Connector.TR069_BOOLEAN);

		assertValueWithMimetype(boolNodeTrue, TR069Connector.TR069_MIME_STRING, "true", TR069Connector.TR069_STRING);
		assertValueWithMimetype(boolNodeFalse, TR069Connector.TR069_MIME_STRING, "false", TR069Connector.TR069_STRING);

		assertValueWithInvalidMimetype(boolNodeTrue, TR069Connector.TR069_MIME_BASE64 );
		assertValueWithInvalidMimetype(boolNodeTrue, TR069Connector.TR069_MIME_DATETIME );
		assertValueWithInvalidMimetype(boolNodeTrue, TR069Connector.TR069_MIME_HEXBINARY );
		assertValueWithInvalidMimetype(boolNodeTrue, TR069Connector.TR069_MIME_INT );
		assertValueWithInvalidMimetype(boolNodeTrue, TR069Connector.TR069_MIME_LONG );
		assertValueWithInvalidMimetype(boolNodeTrue, TR069Connector.TR069_MIME_STRING_LIST );
		assertValueWithInvalidMimetype(boolNodeTrue, TR069Connector.TR069_MIME_UNSIGNED_INT);
		assertValueWithInvalidMimetype(boolNodeTrue, TR069Connector.TR069_MIME_UNSIGNED_LONG );
	}
		

	/**
	 * This test checks the conversion of Dmt Boolean format to TR069 
	 * 
	 * @throws Exception
	 */
	public void testDmtNullToTR069() throws Exception {
		Node rootNode = new Node(null, "mapped plugin root", false, null, null );
		
		Node singletonNode = new Node(rootNode, SINGLETON, false, null, null );
		Node nullNode = new Node( singletonNode, "nullNode", true, null, null );
		
		TestDataPlugin plugin = new TestDataPlugin("testplugin", rootNode);
		Dictionary<String, String> props = new Hashtable<String, String>();
		props.put(DataPlugin.DATA_ROOT_URIS, ROOT );
		registerService(DataPlugin.class.getName(), plugin, props);

		// get a session on the singleton node
		session = dmtAdmin.getSession(ROOT);
		connector = factory.create(session);

		// check default conversion 
		assertValue(connector.getParameterValue(SINGLETON + ".nullNode"), "null", TR069Connector.TR069_STRING);

		assertValueWithMimetype(nullNode, TR069Connector.TR069_MIME_BOOLEAN, "false", TR069Connector.TR069_BOOLEAN);
		assertValueWithMimetype(nullNode, TR069Connector.TR069_MIME_DATETIME, "0001-01-01T00:00:00Z", TR069Connector.TR069_DATETIME); // is this correct? (null stands for unknown time)
		assertValueWithMimetype(nullNode, TR069Connector.TR069_MIME_INT, "0", TR069Connector.TR069_INT);
		assertValueWithMimetype(nullNode, TR069Connector.TR069_MIME_LONG, "0", TR069Connector.TR069_LONG);
		assertValueWithMimetype(nullNode, TR069Connector.TR069_MIME_STRING, "null", TR069Connector.TR069_STRING);
		assertValueWithMimetype(nullNode, TR069Connector.TR069_MIME_UNSIGNED_INT, "0", TR069Connector.TR069_UNSIGNED_INT);
		assertValueWithMimetype(nullNode, TR069Connector.TR069_MIME_UNSIGNED_LONG, "0", TR069Connector.TR069_UNSIGNED_LONG);

		assertValueWithInvalidMimetype(nullNode, TR069Connector.TR069_MIME_BASE64 );
		assertValueWithInvalidMimetype(nullNode, TR069Connector.TR069_MIME_HEXBINARY );
	}
	
	/**
	 * This test checks the conversion of Dmt XML format to TR069
	 * 
	 * @throws Exception
	 */
	public void testDmtXMLToTR069() throws Exception {
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><rss></rss>"; 
		Node rootNode = new Node(null, "mapped plugin root", false, null, null );
		
		Node singletonNode = new Node(rootNode, SINGLETON, false, null, null );
		Node node = new Node( singletonNode, "xmlNode", true, new DmtData(expected, DmtData.FORMAT_XML), null );
		
		TestDataPlugin plugin = new TestDataPlugin("testplugin", rootNode);
		Dictionary<String, String> props = new Hashtable<String, String>();
		props.put(DataPlugin.DATA_ROOT_URIS, ROOT );
		registerService(DataPlugin.class.getName(), plugin, props);

		// get a session on the singleton node
		session = dmtAdmin.getSession(ROOT);
		connector = factory.create(session);

		// check default conversions 
		assertValue(connector.getParameterValue(SINGLETON + ".xmlNode"), expected, TR069Connector.TR069_STRING);

		assertValueWithInvalidMimetype(node, TR069Connector.TR069_MIME_BASE64 );
		assertValueWithInvalidMimetype(node, TR069Connector.TR069_MIME_BOOLEAN );
		assertValueWithInvalidMimetype(node, TR069Connector.TR069_MIME_DATETIME );
		assertValueWithInvalidMimetype(node, TR069Connector.TR069_MIME_HEXBINARY );
		assertValueWithInvalidMimetype(node, TR069Connector.TR069_MIME_INT );
		assertValueWithInvalidMimetype(node, TR069Connector.TR069_MIME_LONG );
		assertValueWithInvalidMimetype(node, TR069Connector.TR069_MIME_STRING_LIST );
		assertValueWithInvalidMimetype(node, TR069Connector.TR069_MIME_UNSIGNED_INT);
		assertValueWithInvalidMimetype(node, TR069Connector.TR069_MIME_UNSIGNED_LONG );
	}
	
	
	//******************* UTILITIES / HELPERS ****************************
	
	/**
	 * Checks that the given ParameterValue is not null and that its type and value are the same 
	 * as the expected ones. 
	 * @param value .. the ParameterValue to be checked
	 * @param expectedValue .. the expected value
	 * @param expectedType .. the expected type
	 */
	private void assertValue( ParameterValue value, String expectedValue, int expectedType ) {
		assertNotNull(value);
		assertEquals( expectedType, value.getType() );
		assertEquals( expectedValue, value.getValue() );
	}

	/**
	 * This method asserts that the type and value of the ParameterValue that is returned by getParameterValue
	 * is the same as the expected type and value.
	 * @param node .. the node to set the Metadata to
	 * @param mimetype .. the mimetype to set to the MetaNode
	 * @param expectedValue .. the expected value
	 * @param expectedType .. the expected type
	 */
	private void assertValueWithMimetype( Node node, String mimetype, String expectedValue, int expectedType ) {
		MetaNode mn = new MetaNode(true, MetaNode.PERMANENT, node.getValue().getFormat(), new int[] {MetaNode.CMD_GET});
		mn.setMimeTypes(new String[] { mimetype } );
		node.setMetaNode(mn);
		assertValue(connector.getParameterValue(SINGLETON + "." + node.getName()), expectedValue, expectedType);
	}

	/**
	 * Sets a new MetaNode to the given node (that has a fixed format). This MetaNode has the given mimetype.
	 * Then it checks that getParameterValue on this node does return a null value or throws an exception.
	 * @param node .. the node to set the Metadata to
	 * @param mimetype .. the mimetype to set in the MetaNode
	 */
	private void assertValueWithInvalidMimetype( Node node, String mimetype ) {
		log("Testing of invalid mime-types is disabled until decision for bug 2209 is made." );
//		MetaNode mn = new MetaNode(true, MetaNode.PERMANENT, node.getValue().getFormat(), new int[] {MetaNode.CMD_GET});
//		mn.setMimeTypes(new String[] { mimetype } );
//		node.setMetaNode(mn);
//		ParameterValue value = null;
//		try {
//			value = connector.getParameterValue(SINGLETON + "." + node.getName());
//		} catch (TR069Exception e) {
//			pass( "This conversion is detected as invalid: " + mimetype );
//			List<Integer> expectedFaultCodes = Arrays.asList(new Integer[] {9001, 9002, 9003, 9003, 9005});
//			assertTrue(expectedFaultCodes.contains( e.getFaultCode()) );
//		}
//		assertEquals("This conversion is invalid. The result must be null: " + mimetype, null, value);
	}

	private String toHex(byte[] raw) throws UnsupportedEncodingException 
	{
		byte[] hex = new byte[2 * raw.length];
		int index = 0;
		
		for (byte b : raw) {
			int v = b & 0xFF;
			hex[index++] = HEX_CHAR_TABLE[v >>> 4];
			hex[index++] = HEX_CHAR_TABLE[v & 0xF];
		}
		return new String(hex, "ASCII");
	}
}
