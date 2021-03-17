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

import java.util.Date;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.osgi.service.dmt.DmtAdmin;
import org.osgi.service.dmt.DmtConstants;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.service.tr069todmt.TR069Connector;
import org.osgi.service.tr069todmt.TR069ConnectorFactory;
import org.osgi.test.cases.tr069todmt.plugins.MetaNode;
import org.osgi.test.cases.tr069todmt.plugins.Node;
import org.osgi.test.cases.tr069todmt.plugins.TestDataPlugin;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 *
 * This is the base class for the TR069Connector Service TCK's.
 *
 * It defines a number of variables and implements a default behavior of the
 * setUp() and tearDown() methods.
 *
 * @author steffen.druesedow@telekom.de
 *
 */
public abstract class TR069ToDmtTestBase extends DefaultTestBundleControl {

	final static String ROOT = "./testplugin";
	final String TR_069 	= "TR-069";
	final String LISTNODE 	= "listnode";
	final String MAPNODE 	= "mapnode";
	final String SINGLETON  = "singleton";

	static final String BASE64 		= "base64";
	static final String BINARY 		= "binary";
	static final String BOOLEAN 	= "boolean";
	static final String DATE 		= "date";
	static final String DATETIME 	= "datetime";
	static final String FLOAT 		= "float";
	static final String INTEGER 	= "integer";
	static final String LONG 		= "long";
	static final String RAWBINARY 	= "rawbinary";
	static final String RAWSTRING 	= "rawstring";
	static final String STRING 		= "string";
	static final String TIME 		= "time";
	static final String XML 		= "xml";
	static final String UNKNOWN		= "unknown";
	static final String MULTIFORMAT = "multiformat";
	
	static final int ALL_DMT_FORMATS = 		
		DmtData.FORMAT_BASE64 | DmtData.FORMAT_BINARY | DmtData.FORMAT_BOOLEAN |
		DmtData.FORMAT_DATE | DmtData.FORMAT_DATE_TIME | DmtData.FORMAT_FLOAT |
		DmtData.FORMAT_INTEGER | DmtData.FORMAT_LONG | DmtData.FORMAT_NODE | 
		DmtData.FORMAT_NULL | DmtData.FORMAT_RAW_BINARY | DmtData.FORMAT_RAW_STRING |
		DmtData.FORMAT_STRING | DmtData.FORMAT_TIME | DmtData.FORMAT_XML;

	
	DmtAdmin dmtAdmin;
	DmtSession session;
	TR069ConnectorFactory factory;
	TR069Connector connector;

	protected void setUp() throws Exception {
		super.setUp();
		System.out.println("setting up");
		dmtAdmin = getService(DmtAdmin.class);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		System.out.println("tearing down");

		if (session != null && session.getState() == DmtSession.STATE_OPEN)
			session.close();
		unregisterAllServices();
		ungetAllServices();
	}

	void dumpTree(DmtSession session, String uri ) throws Exception {
		System.out.print( "" + uri );
		if ( session.isLeafNode(uri) )
			System.out.println( ":  " + session.getNodeValue(uri));
		else {
			System.out.println();
			String[] children = session.getChildNodeNames(uri);
			for (String child : children)
				dumpTree(session, uri + "/" + child );
		}
	}

	/**
	 * Registers a plugin that is initialized with one list, one map and a normal interior node as siblings.
	 * The list nodee has the name "listnode" and 3 initial entries.
	 * Furthermore there is one node of type DDM_MAP and name "mapnode" and also 3 initial entries.
	 * The "normal" interior node has the name "plainnode" and with 2 leafs initially.
	 * The plugin is registered with the given dataRootUri. If for example dataRootUri is "./a/testplugin",
	 * then the nodes are accessible as Uris: "./a/testplugin/listnode", "./a/testplugin/mapnode" and "./a/testplugin/plainnode".
	 *
	 * All entries of lists and maps have an InstanceId node.
	 *
	 * The resulting structure looks like this:
	 * dataRootUri
	 *  - listnode
	 *  	- 0
	 *  		- name --> "name 0"
	 *  		- description --> "description 0"
	 *  		- InstanceId --> "100"
	 *  	- 1
	 *  		- name --> "name 1"
	 *  		- description --> "description 1"
	 *  		- InstanceId --> "101"
	 *  	- 2
	 *  		- name --> "name 2"
	 *  		- description --> "description 2"
	 *  		- InstanceId --> "102"
	 *  - mapnode
	 *  	- key0
	 *  		- name --> "name 0"
	 *  		- description --> "description 0"
	 *  		- InstanceId --> "100"
	 *  	- key1
	 *  		- name --> "name 1"
	 *  		- description --> "description 1"
	 *  		- InstanceId --> "101"
	 *  	- key2
	 *  		- name --> "name 2"
	 *  		- description --> "description 2"
	 *  		- InstanceId --> "102"
	 *  - singleton
	 *  		- name --> "plainname"
	 *  		- description --> "plaindescription"
	 *
	 * @param dataRootUri ... the dataRootUri
	 * @param eager ... if true, add mime type "application/x-tr-069-eager" to the list- and map-node
	 * @throws Exception
	 */
	void registerDefaultTestPlugin( String dataRootUri, boolean eager ) throws Exception {
		Node root = new Node(null, "mapped plugin root", false, null, null );

		// setup a simple list
		Node listNode = new Node(root, LISTNODE, false, null, DmtConstants.DDF_LIST );
		MetaNode listMetaNode = new MetaNode(false, MetaNode.PERMANENT, DmtData.FORMAT_NODE, new int[] {MetaNode.CMD_ADD, MetaNode.CMD_GET, MetaNode.CMD_REPLACE} );
		if ( eager )
			listMetaNode.setMimeTypes( new String[] {TR069Connector.TR069_MIME_EAGER} );
		listNode.setMetaNode(listMetaNode);
		// list elements
		Node entry0 = new Node(listNode, "0", false, null, null);
		new Node( entry0, "name", true, new DmtData("name 0"), null );
		new Node( entry0, "description", true, new DmtData("description 0"), null );
		new Node( entry0, "InstanceId", true, new DmtData(100L), null );
		Node entry1 = new Node(listNode, "1", false, null, null);
		new Node( entry1, "name", true, new DmtData("name 1"), null );
		new Node( entry1, "description", true, new DmtData("description 1"), null );
		new Node( entry1, "InstanceId", true, new DmtData(101L), null );
		Node entry2 = new Node(listNode, "2", false, null, null);
		new Node( entry2, "name", true, new DmtData("name 2"), null );
		new Node( entry2, "description", true, new DmtData("description 2"), null );
		new Node( entry2, "InstanceId", true, new DmtData(102L), null );

		// the plugin maintains metadata of its list entries
		MetaNode listElementMetaNode = new MetaNode(false, MetaNode.DYNAMIC, DmtData.FORMAT_STRING, new int[] {MetaNode.CMD_ADD, MetaNode.CMD_GET, MetaNode.CMD_REPLACE, MetaNode.CMD_DELETE} );
		listNode.setListElementMetaNode(listElementMetaNode);

		// setup a simple map
		Node mapNode = new Node(root, MAPNODE, false, null, DmtConstants.DDF_MAP );
		MetaNode mapMetaNode = new MetaNode(false, MetaNode.PERMANENT, DmtData.FORMAT_NODE, new int[] {MetaNode.CMD_ADD, MetaNode.CMD_GET, MetaNode.CMD_REPLACE} );
		if ( eager )
			mapMetaNode.setMimeTypes( new String[] {TR069Connector.TR069_MIME_EAGER} );
		mapNode.setMetaNode(mapMetaNode);
		// map elements
		entry0 = new Node(mapNode, "key0", false, null, null);
		new Node( entry0, "name", true, new DmtData("name 0"), null );
		new Node( entry0, "description", true, new DmtData("description 0"), null );
		new Node( entry0, "InstanceId", true, new DmtData(100L), null );
		entry1 = new Node(mapNode, "key1", false, null, null);
		new Node( entry1, "name", true, new DmtData("name 1"), null );
		new Node( entry1, "description", true, new DmtData("description 1"), null );
		new Node( entry1, "InstanceId", true, new DmtData(101L), null );
		entry2 = new Node(mapNode, "key2", false, null, null);
		new Node( entry2, "name", true, new DmtData("name 2"), null );
		new Node( entry2, "description", true, new DmtData("description 2"), null );
		new Node( entry2, "InstanceId", true, new DmtData(102L), null );

		// the plugin maintains metadata of its map entries
		MetaNode mapElementMetaNode = new MetaNode(false, MetaNode.DYNAMIC, DmtData.FORMAT_STRING, new int[] {MetaNode.CMD_ADD, MetaNode.CMD_GET, MetaNode.CMD_REPLACE, MetaNode.CMD_DELETE} );
		mapNode.setMapElementMetaNode(mapElementMetaNode);

		Node plainNode = new Node(root, SINGLETON, false, null, null );
		MetaNode plainMetaNode = new MetaNode(false, MetaNode.PERMANENT, DmtData.FORMAT_NODE, new int[] {MetaNode.CMD_ADD, MetaNode.CMD_GET, MetaNode.CMD_REPLACE} );
		plainNode.setMetaNode(plainMetaNode);
		new Node( plainNode, "name", true, new DmtData("plainname"), null );
		new Node( plainNode, "description", true, new DmtData("plaindescription"), null );

		TestDataPlugin plugin = new TestDataPlugin("testplugin", root);
		Dictionary<String, String> props = new Hashtable<String, String>();
		props.put(DataPlugin.DATA_ROOT_URIS, dataRootUri );
		registerService(DataPlugin.class.getName(), plugin, props);
	}

	/**
	 * Asserts that the list- or map-node at the expected uri has no child with an
	 * InstanceId node of value instanceId
	 *
	 * @param nodeUri ... the map- or list-uri to check for a child with the given InstanceId
	 * @param instanceId ... the instance id to check for
	 * @throws Exception
	 */
	void assertInstanceIdNodeNotExists(String nodeUri, String instanceId)  {
		try {
			// check all children of nodeUri for an InstanceId node with value instanceId
			String[] children = session.getChildNodeNames(nodeUri);
			for (String child : children) {
				String id = "" + session.getNodeValue(nodeUri + "/" + child + "/InstanceId" ).getLong();
				assertFalse( "There must be no list/map- entry with this InstanceId: " + instanceId,  instanceId.equals(id));
			}
		} catch (DmtException e) {
			fail( "Unexpected DmtException while checking for a child with InstanceId = " + instanceId + " on list/map: " + nodeUri);
		}
	}

	/**
	 * Asserts that the list/map at the given uri has a child with an InstanceId node of value instanceId.
	 * If nameValue and/or descriptionValue are given (not null) then the corresponding node values
	 * for this entry are also compared.
	 *
	 * @param nodeUri ... the uri of the new entry in the list/map
	 * @param instanceId ... the instanceId to look for in the child nodes
	 * @param nameValue ... the expected value of the new nodes leaf of name 'name'
	 * @param descriptionValue ... the expected value of the new nodes leaf of name 'description'
	 * @return the map-key or list index of the dmt node that holds the matching instanceId
	 *
	 */
	String assertInstanceIdNodeExists(String nodeUri, String instanceId, String nameValue, String descriptionValue ) throws Exception {
		String key = null;
		try {
			// check all children of nodeUri for an InstanceId node with value instanceId
			String[] children = session.getChildNodeNames(nodeUri);
			String newChildUri = null;
			for (String child : children) {
				String instanceIdUri = nodeUri + "/" + child + "/InstanceId";
				assertTrue( "The InstanceId node must exist at uri: " + instanceIdUri, session.isNodeUri(instanceIdUri));
				String id = "" + session.getNodeValue( instanceIdUri ).getLong();
				if ( instanceId.equals(id)) {
					newChildUri = nodeUri + "/" + child;
					key = child;
					break;
				}
			}
			assertNotNull( "No child of list/map at " + nodeUri + " found with an InstanceId of: " + instanceId, newChildUri );
			if ( nameValue != null )
				assertEquals( "The child node must have a leaf 'name' with value : " + nameValue, nameValue, session.getNodeValue(newChildUri + "/name").toString() );
			if ( descriptionValue != null )
				assertEquals( "The child node must have a leaf 'description' with value : " + descriptionValue, descriptionValue, session.getNodeValue(newChildUri + "/description").toString() );
		} catch (DmtException e) {
			fail( "Unexpected DmtException while checking for a child with InstanceId = " + instanceId + " on list/map: " + nodeUri);
		}
		return key;
	}

	/**
	 * Asserts that the InstanceIds of list/key nodes are unique.
	 *
	 * @param nodeUri ... the map- or list-uri to check the InstanceIds for
	 * @throws Exception
	 */
	void assertUniqueInstanceIds(String nodeUri)  {
		try {
			Set<String> instanceIds = new HashSet<String>();
			// check all children of nodeUri for an InstanceId node with value instanceId
			String[] children = session.getChildNodeNames(nodeUri);
			for (String child : children)
				instanceIds.add("" + session.getNodeValue(nodeUri + "/" + child + "/InstanceId" ).getLong());
			assertEquals("There are duplicate InstanceIds for list/map: " + nodeUri, children.length, instanceIds.size());
		} catch (DmtException e) {
			fail( "Unexpected DmtException while checking InstanceIds on list/map " + nodeUri);
		}
	}

	/**
	 * Asserts that the map-node at the given uri has a child with the given (alias) name.
	 * If nameValue and/or descriptionValue are given (not null) then the corresponding node values
	 * for this entry are also compared.
	 *
	 * @param nodeUri ... the uri of the map node
	 * @param alias ... the alias to look for (is the entry key in the map)
	 * @param nameValue ... the expected value of the new nodes leaf of name 'name'
	 * @param descriptionValue ... the expected value of the new nodes leaf of name 'description'
	 */
	void assertMapNodeExists(String nodeUri, String alias, String nameValue, String descriptionValue ) throws Exception {
		String uri = nodeUri + "/" + alias;
		assertTrue("The node must exist in the DMT: " + uri, session.isNodeUri(uri));
		if ( nameValue != null )
			assertEquals( "The node must have a leaf 'name' with value : " + nameValue, nameValue, session.getNodeValue(uri + "/name").toString() );
		if ( descriptionValue != null )
			assertEquals( "The node must have a leaf 'description' with value : " + descriptionValue, descriptionValue, session.getNodeValue(uri + "/description").toString() );
	}

	/**
	 * Asserts that the map-node at the given uri has no child with the given (alias) name.
	 *
	 * @param nodeUri ... the uri of the map node
	 * @param alias ... the alias to look for (is the entry key in the map)
	 */
	void assertMapNodeNotExists(String nodeUri, String alias) throws Exception {
		String uri = nodeUri + "/" + alias;
		assertFalse("The node must not exist in the DMT: " + uri, session.isNodeUri(uri));
	}
	
	
	/**
	 * Prepares and registers a plugin that has one node for every DmtData format (except list) and
	 * a corresponding MetaNode that reports exactly this format.
	 * 
	 */
	void prepareTestNodesWithSingleFormats() throws Exception {
		Node rootNode = new Node(null, "mapped plugin root", false, null, null );
		
		Node singletonNode = new Node(rootNode, SINGLETON, false, null, null );
		createNodeWithMetadata(singletonNode, INTEGER, new DmtData(0), DmtData.FORMAT_INTEGER );
		createNodeWithMetadata(singletonNode, LONG, new DmtData(0), DmtData.FORMAT_LONG );
		createNodeWithMetadata(singletonNode, FLOAT, new DmtData(0), DmtData.FORMAT_FLOAT );
		createNodeWithMetadata(singletonNode, BASE64, new DmtData(new byte[]{0}), DmtData.FORMAT_BASE64 );
		createNodeWithMetadata(singletonNode, BINARY, new DmtData(new byte[]{0}), DmtData.FORMAT_BINARY );
		createNodeWithMetadata(singletonNode, RAWBINARY, new DmtData(new byte[]{0}), DmtData.FORMAT_RAW_BINARY );
		createNodeWithMetadata(singletonNode, BOOLEAN, new DmtData(false), DmtData.FORMAT_BOOLEAN );
		createNodeWithMetadata(singletonNode, DATE, new DmtData(new Date()), DmtData.FORMAT_DATE );
		createNodeWithMetadata(singletonNode, DATETIME, new DmtData(new Date()), DmtData.FORMAT_DATE_TIME );
		createNodeWithMetadata(singletonNode, TIME, new DmtData(new Date()), DmtData.FORMAT_TIME );
		createNodeWithMetadata(singletonNode, STRING, new DmtData(""), DmtData.FORMAT_STRING );
		createNodeWithMetadata(singletonNode, RAWSTRING, new DmtData(""), DmtData.FORMAT_RAW_STRING );
		createNodeWithMetadata(singletonNode, XML, new DmtData(""), DmtData.FORMAT_XML );

		TestDataPlugin plugin = new TestDataPlugin("testplugin", rootNode);
		Dictionary<String, String> props = new Hashtable<String, String>();
		props.put(DataPlugin.DATA_ROOT_URIS, ROOT );
		registerService(DataPlugin.class.getName(), plugin, props);
	}
	
	private void createNodeWithMetadata( Node parent, String name, DmtData data, int format ) {
		Node node = new Node( parent, name, true, data, null );
		node.setMetaNode(new MetaNode(false, MetaNode.PERMANENT, format, new int[] {MetaNode.CMD_ADD, MetaNode.CMD_GET, MetaNode.CMD_REPLACE} ));
	}

	/**
	 * Prepares and registers a plugin that has one node without a corresponding MetaNode. 
	 * That means there is no indication of the applicable formats and should in general 
	 * accept all formats (see residential spec. 117.15.12.14).
	 * 
	 * The registered plugin holds a node that is accessible as "./testplugin/singleton/unknown".
	 * 
	 */
	void prepareTestNodeWithoutFormats() throws Exception {
		Node rootNode = new Node(null, "mapped plugin root", false, null, null );
		
		Node singletonNode = new Node(rootNode, SINGLETON, false, null, null );
		new Node(singletonNode, UNKNOWN, true, new DmtData(""), null);

		TestDataPlugin plugin = new TestDataPlugin("testplugin", rootNode);
		Dictionary<String, String> props = new Hashtable<String, String>();
		props.put(DataPlugin.DATA_ROOT_URIS, ROOT );
		registerService(DataPlugin.class.getName(), plugin, props);
	}

	/**
	 * Prepares and registers a plugin that has one node with a corresponding MetaNode that has the given format(s).
	 * The formats are given as OR-ed format constants.   
	 * 
	 * The registered plugin holds a node that is accessible as "./testplugin/singleton/unknown".

	 * @param the OR-ed formats for the metanode 
	 */
	void prepareTestNodeWithFormats( int formats) throws Exception {
		Node rootNode = new Node(null, "mapped plugin root", false, null, null );
		
		Node singletonNode = new Node(rootNode, SINGLETON, false, null, null );
		Node multiNode = new Node(singletonNode, UNKNOWN, true, new DmtData(""), null);
		multiNode.setMetaNode(new MetaNode(false, MetaNode.PERMANENT, formats, new int[] {MetaNode.CMD_ADD, MetaNode.CMD_GET, MetaNode.CMD_REPLACE} ));

		TestDataPlugin plugin = new TestDataPlugin("testplugin", rootNode);
		Dictionary<String, String> props = new Hashtable<String, String>();
		props.put(DataPlugin.DATA_ROOT_URIS, ROOT );
		registerService(DataPlugin.class.getName(), plugin, props);
	}
}
