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

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.service.tr069todmt.TR069Connector;
import org.osgi.service.tr069todmt.TR069ConnectorFactory;
import org.osgi.test.cases.tr069todmt.plugins.Node;
import org.osgi.test.cases.tr069todmt.plugins.TestDataPlugin;

/**
 * This testcase checks for the correct Constants in the TR069Connector interface.
 * 
 * @author steffen.druesedow@telekom.de
 *
 */
public class TR069ConnectorSessionsTestCase extends TR069ToDmtTestBase {

	/**
	 * Checks that the factory method does not return a valid connector instance, 
	 * if null was provided as argument.
	 * 
	 * It is expected that the method returns null and might throw an Exception.
	 * @throws Exception
	 */
	public void testFactoryWithNullSession() throws Exception {
		factory = getService(TR069ConnectorFactory.class);
		assertNotNull(factory);
		assertNull(connector);
		try {
			connector = factory.create(null); 
		} catch (Throwable e) {
			pass("A null session is not allowed as parameter for theTR069ConnectorFactory.create(..).");
		}
		finally {
			if ( connector != null )
				connector.close();
		}
		assertNull("A null session must not be allowed as parameter for theTR069ConnectorFactory.create(..).", connector);
	}
	
	/**
	 * Checks that closing a Connector does not close the underlying session. 
	 * 
	 * @throws Exception
	 */
	public void testSessionWhenConnectorClosed() throws Exception {
		factory = getService(TR069ConnectorFactory.class);
		assertNotNull(factory);
		assertNull(connector);
		
		session = dmtAdmin.getSession(".");
		assertNotNull(session);
		assertEquals( DmtSession.STATE_OPEN, session.getState() );
		
		connector = factory.create(session);
		assertNotNull(connector);
		assertEquals( DmtSession.STATE_OPEN, session.getState() );
		
		connector.close();
		assertEquals( "The DmtSession must not be closed, if the connector closes.", DmtSession.STATE_OPEN, session.getState() );
	}

	/**
	 * Checks that several Connectors can be associated to the same session. 
	 * 
	 * Creates a number of Connectors on the same session and checks that they are not null 
	 * and all connectors use the same session root for the URI mapping. 
	 * @throws Exception
	 */
	public void testConnectorsOnSameSession() throws Exception {
		factory = getService(TR069ConnectorFactory.class);
		assertNotNull(factory);
		assertNull(connector);

		// setup a simple Plugin structure and register the plugin
		Node root = new Node(null, "mapped plugin root", false, null, null );
		Node n = new Node(root, "A", false, null, null );
		new Node(n, "a1", true, new DmtData("leaf a1"), null);
		new Node(n, "a2", true, new DmtData("leaf a2"), null);
		Node n2 = new Node(n, "B", false, null, null );
		new Node(n2, "C", false, null, null );

		Dictionary<String, String> props = new Hashtable<String, String>();
		props.put(DataPlugin.DATA_ROOT_URIS, "./" + TR_069 );
		registerService(DataPlugin.class.getName(), new TestDataPlugin("TestPlugin", root), props);
		
		String sessionRoot = "./" + TR_069 + "/A";
		session = dmtAdmin.getSession(sessionRoot);
		assertNotNull(session);
		assertEquals(sessionRoot, session.getRootUri());
		assertEquals( DmtSession.STATE_OPEN, session.getState() );
		
		TR069Connector[] connectors = new TR069Connector[10];
		for (int i = 0; i < connectors.length; i++) {
			connectors[i] = factory.create(session);
			assertNotNull(connectors[i]);
			String uri = connectors[i].toURI("B.C.", false); 
			assertTrue( uri.equals("B/C"));
		}
		
		assertEquals( DmtSession.STATE_OPEN, session.getState() );
		for (TR069Connector c : connectors)
			c.close();
	}

	
}
