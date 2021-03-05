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
package org.osgi.test.cases.dmt.tc4.rfc141;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.service.dmt.DmtAdmin;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.test.cases.dmt.tc4.rfc141.plugins.GenericDataPlugin;
import org.osgi.test.cases.dmt.tc4.rfc141.plugins.MetaNode;
import org.osgi.test.cases.dmt.tc4.rfc141.plugins.Node;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public class TestBug1658_PermanentNode extends DefaultTestBundleControl{
	
	DmtAdmin dmtAdmin;
	DmtSession session;
	GenericDataPlugin dataPlugin;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		System.out.println("setting up");
		dmtAdmin = getService(DmtAdmin.class);
	}


	

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		System.out.println( "tearing down");
		if ( session != null && session.getState() == DmtSession.STATE_OPEN )
			session.close();
		unregisterAllServices();
		ungetAllServices();
	}
	
	/**
	 */
	public void testSetValueOnNodeWithoutMetaData() throws Exception {

		// prepare the plugin and nodes without any attached meta data
		preparePlugin( false );
		
		setNodeValue();
		
	}
	
	/**
	 */
	public void testSetValueOnPermanentNode() throws Exception {
		
		// prepare the plugin and nodes wit meta data for node "./A/B", indicating 
		// that this is a permanent node
		preparePlugin( true );
		
		try {
			setNodeValue();
			pass("Setting a value on permanent node is correctly permitted." );
		} catch (DmtException e) {
			assertEquals("an unexpected exeption occured while performing setNodeValue: " + e.getMessage(),  DmtException.METADATA_MISMATCH, e.getCode() );
			fail("Setting a value on a permanent node must be permitted.");
		}
	}


	private void setNodeValue() throws DmtException  {
		String uri = "./A/B";
		String newValue = "B_value";
		
		session = dmtAdmin.getSession( uri, DmtSession.LOCK_TYPE_EXCLUSIVE);
		
		DmtData dmtData = new DmtData( newValue );
		session.setNodeValue(uri, dmtData );
		assertEquals( GenericDataPlugin.ACTION_SET_NODE_VALUE, dataPlugin.lastAction );
		assertEquals( newValue, dataPlugin.lastValue.toString() );
	}
	

	private void preparePlugin( boolean withMetadata ) throws Exception {
		String mountRoot = "./A";
		Node n2 = new Node(null, "A", null);
		Node n3 = new Node(n2, "B", "node B");
		dataPlugin = new GenericDataPlugin("P1", mountRoot, n2 );
		
		if ( withMetadata ) {
			int[] ops = { org.osgi.service.dmt.MetaNode.CMD_GET, org.osgi.service.dmt.MetaNode.CMD_REPLACE };
			MetaNode metaNode = new MetaNode(true, org.osgi.service.dmt.MetaNode.PERMANENT, DmtData.FORMAT_STRING, ops );
			n3.setMetaNode(metaNode);
		}
		
		Dictionary<String,Object> props = new Hashtable<>();
		props.put(DataPlugin.DATA_ROOT_URIS, new String[] {mountRoot});
		registerService(DataPlugin.class.getName(), dataPlugin, props );
	}
}
