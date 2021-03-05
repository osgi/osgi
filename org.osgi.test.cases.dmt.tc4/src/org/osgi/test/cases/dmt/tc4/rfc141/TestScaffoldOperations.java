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

import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.MetaNode;

/**
 * Scaffold nodes must support all operations of org.osgi.service.dmt.spi.ReadableDataSession.
 * 
 * @author steffen
 *
 */
public class TestScaffoldOperations extends ScaffoldNodeHelper {
	
	/**
	 * 
	 * Checks metadata of a Scaffold node as described in RFC141
	 * (RFC141, section 6.3.1)
	 */
	public void testScaffoldNodeOperations() throws Exception {

		prepareScaffoldPlugin();
		
		String uri = "./A";
		session = dmtAdmin.getSession( uri, DmtSession.LOCK_TYPE_SHARED);
		
		// should provide correct array of node names
		getChildNodeNamesOperation(session, uri, new String[]{"B"} );
		
		// should provide non-null MetaNode
		getMetaNodeOperation(session, uri);
		
		// should throw DmtException of type COMMAND_NOT_ALLOWED
		getNodeSizeOperation(session, uri);
		
		getNodeTimeStampOperation(session, uri);
		
	}
	
	/**
	 * checks that the DmtAdmin throws a DmtExeption of type COMMAND_NOT_ALLOWED, if execute is invoked on a scaffold node
	 */
	public void testScaffoldNodeExecution() throws Exception {

		prepareScaffoldPlugin();
		
		String uri = "./A";
		session = dmtAdmin.getSession( uri, DmtSession.LOCK_TYPE_EXCLUSIVE);
		
		try {
			session.execute( uri, null );
			fail( "The DmtAdmin must throw a DmtException of type COMMAND_NOT_ALLOWED for execute() on Scaffold nodes." );
		} catch (DmtException e) {
			pass( "The DmtAdmin correctly throws a DmtException of type COMMAND_NOT_ALLOWED for execute() on Scaffold nodes." );
		} 
	}
	
	
	/**
	 * checks that the getChildNodeNames operation is supported and provides valid data
	 * @param session
	 * @param uri
	 * @param expected
	 * @throws Exception
	 */
	private void getChildNodeNamesOperation( DmtSession session, String uri, String[] expected ) throws Exception {
		String[] childNodeNames = session.getChildNodeNames(uri);
		assertStringArrayEquals(expected, childNodeNames);
	}
	
	/**
	 * checks that a Metanode can be obtained from the session.
	 * Does not check the content of the MetaData, because this has it's own testcase.
	 * @param session
	 * @param uri
	 * @throws Exception
	 */
	private void getMetaNodeOperation( DmtSession session, String uri ) throws Exception {
		MetaNode metaNode = session.getMetaNode(uri);
		assertNotNull("The DmtAdmin must provide a MetaNode for scaffold nodes!", metaNode);
	}

	/**
	 * checks that the DmtAdmin throws a DmtExeption of type COMMAND_NOT_ALLOWED, if getNodeSize is invoked on a scaffold node
	 * @param session
	 * @param uri
	 * @throws Exception
	 */
	private void getNodeSizeOperation( DmtSession session, String uri ) throws Exception {
		try {
			session.getNodeSize(uri);
			fail( "The DmtAdmin must throw a DmtException of type COMMAND_NOT_ALLOWED for getNodeSize() on Scaffold nodes." );
		} catch (DmtException e) {
			pass( "The DmtAdmin correctly throws a DmtException of type COMMAND_NOT_ALLOWED for getNodeSize() on Scaffold nodes." );
		} 
	}

	/**
	 * checks that the DmtAdmin returns a first create time, if getNodeTimestamp is invoked on a scaffold node
	 * @param session
	 * @param uri
	 * @throws Exception
	 */
	private void getNodeTimeStampOperation( DmtSession session, String uri ) throws Exception {
		try {
			assertNotNull(session.getNodeTimestamp(uri));
		} catch (DmtException e) {
			fail( "The DmtAdmin must not throw a DmtException for getNodeTimestamp() on Scaffold nodes." );
		} 
	}
}
