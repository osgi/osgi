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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.tr069todmt.ParameterInfo;
import org.osgi.service.tr069todmt.TR069Connector;
import org.osgi.service.tr069todmt.TR069ConnectorFactory;
import org.osgi.service.tr069todmt.TR069Exception;

/**
 * This TestCase checks the toUri and toPath methods of the TR069Connector including 
 * encoding and decoding. 
 * 
 * @author steffen.druesedow@telekom.de
 *
 */
public class TR069ConnectorEscapingTestCase extends TR069ToDmtTestBase {

	
	public void setUp() throws Exception {
		super.setUp();
		factory = getService(TR069ConnectorFactory.class);
		assertNotNull("Unable to get the TR069ConnectorFactory.", factory);
	}

	
	/**
	 * This test checks that the returned values of toPath and getParameterNames are correctly escaped.
	 * 
	 * For this test a number of additional nodes are added to the Singleton node of the 
	 * default testplugin.
	 * The tests are taken from table 131.4 "Escaping Parameter Names" of the spec.
	 * 
	 * @throws Exception
	 */
	public void testResultEscaping() throws Exception {
		// setup a simple Plugin structure and register the plugin
		registerDefaultTestPlugin(ROOT, false);	// don't set "eager" mime-type

		// get a session on the singleton node
		session = dmtAdmin.getSession(ROOT + "/" + SINGLETON, DmtSession.LOCK_TYPE_ATOMIC);
		connector = factory.create(session);
		
		Map<String, String> mapUriToPath = new HashMap<String, String>();
		mapUriToPath.put( "DeviceInfo", "DeviceInfo");
		mapUriToPath.put( "3x Hello World", "þ0033xþ0020Helloþ0020World");
		mapUriToPath.put( "þorn", "þ00FEorn");
		mapUriToPath.put( "application\\/bin", "applicationþ002Fbin" ); // uri is dmt-encoded
		mapUriToPath.put( "234", "þ003234"); // this is no instanceId here, just a normal leaf --> needs escaping
		mapUriToPath.put( "234x", "þ003234x"); // leading number must be escaped
		mapUriToPath.put( "þ00FEorn", "þ00FE00FEorn"); 
		
		try {
			// add special leafs to the singleton node
			for (String leaf : mapUriToPath.keySet()) 
				session.createLeafNode(leaf, new DmtData("value"));
		} catch (DmtException e) {
			fail( "Unexpected DmtException while adding example leaf nodes to the singleton." );
		}
		
		// check escaping of toPath() method on the leaf nodes
		for (String uri : mapUriToPath.keySet()) 
			assertEquals( "The segment name '" + uri + "' escaped to TR069 must result in: " + mapUriToPath.get(uri),
					mapUriToPath.get(uri), connector.toPath(uri));
		
		// check getParameterNames on Singleton node with nextLevel = true --> only the encoded leaf names must be returned
		List<String> expected = new ArrayList<String>();
		List<String> unexpected = new ArrayList<String>();
		for (String key : mapUriToPath.keySet()) 
			expected.add(mapUriToPath.get(key));
		expected.add( "name" );
		expected.add( "description" );
		
		Collection<ParameterInfo> infos = connector.getParameterNames("", true);
		assertNotNull(infos);
		for (ParameterInfo info : infos ) {
			if ( ! expected.contains(info.getPath() ))
				unexpected.add(info.getPath());
			expected.remove(info.getPath());
		}
		assertEquals("There are unexpected results in the returned ParameterNames: " + unexpected, 0, unexpected.size());
		assertEquals("Expected results are missing in the returned ParameterNames: " + expected, 0, expected.size());

		// getParameterNames on instanceId's was already tested in TR069ConnectorOperationsTestCase 
		session.close();

		
		// open new session on the testplugin root for testing instanceids
		session = dmtAdmin.getSession(ROOT);
		connector = factory.create(session);

		// check escaping of instance-ids from List and Map 
		// --> must not be encoded 
		String uriListInstance = LISTNODE + "/0";
		String uriMapInstance = MAPNODE + "/key0";
		assertTrue(session.isNodeUri(uriListInstance));
		assertTrue(session.isNodeUri(uriMapInstance));
		
		assertEquals( "The list index in '" + uriListInstance + "' must not be escaped!",
				LISTNODE + ".100.", connector.toPath(uriListInstance));
		assertEquals( "The map index in '" + uriMapInstance + "' must not be escaped!",
				MAPNODE + ".100.", connector.toPath(uriMapInstance));
	}

	
	/**
	 * This test checks that the connector only accepts escaped pathes.
	 * 
	 * - leaf that starts with digit
	 * - leaf that contains spaces
	 * - leaf that contains solidus "/"
	 * 
	 * @throws Exception
	 */
	public void testOnlyEncodedPathesAccepted() throws Exception {
		// setup a simple Plugin structure and register the plugin
		registerDefaultTestPlugin(ROOT, false);	// don't set "eager" mime-type

		// get a session on the singleton node
		session = dmtAdmin.getSession(ROOT + "/" + SINGLETON);
		connector = factory.create(session);
		
		try {
			// add some special leafs to the singleton node
			session.createLeafNode("Hello World", new DmtData("value"));
			session.createLeafNode("þorn", new DmtData("value"));
			session.createLeafNode("application\\/bin", new DmtData("value"));
			session.createLeafNode("234", new DmtData("value"));
		} catch (DmtException e) {
			fail( "Unexpected DmtException while adding example leaf nodes to the singleton." );
		}
		
		// check getParameterNames, getParameterValue, setParameterValue, toUri
		assertUnescapedParameterNotAccepted("Hello World");
		assertUnescapedParameterNotAccepted("þorn");
		assertUnescapedParameterNotAccepted("application/bin");
		assertUnescapedParameterNotAccepted("234x");
		assertUnescapedParameterNotAccepted("teþst");
		
	}

	/**
	 * This test checks that the connector accepts also pathes that contain unrequired escapings.
	 * 
	 * @throws Exception
	 */
	public void testUnrequiredEncodingsAccepted() throws Exception {
		// setup a simple Plugin structure and register the plugin
		registerDefaultTestPlugin(ROOT, false);	// don't set "eager" mime-type

		// get a session on the singleton node
		session = dmtAdmin.getSession(ROOT + "/" + SINGLETON, DmtSession.LOCK_TYPE_ATOMIC);
		connector = factory.create(session);
		
		try {
			// add some special leafs to the singleton node
			session.createLeafNode("Hello World", new DmtData("value"));
			session.createLeafNode("123", new DmtData("value"));
		} catch (DmtException e) {
			fail( "Unexpected DmtException while adding example leaf nodes to the singleton." );
		}
		
		// check getParameterNames, getParameterValue, setParameterValue, toUri
		assertUnnecessaryEscapingAccepted("þ0048elloþ0020þ0057orld");
		assertUnnecessaryEscapingAccepted("þ0031þ0032þ0033");
	}

	/**
	 * Takes the given parameter and tries to invoke following connector methods on them:
	 * - getParameterNames
	 * - getParameterValues
	 * - setParameterValue
	 * - toUri
	 * It is expected that all invocations fail with a TR069Exception.INVALID_PARAMETER_NAME (9005)
	 * @param parameter
	 */
	private void assertUnescapedParameterNotAccepted(String parameter) {
		try {
			connector.getParameterNames(parameter, false);
			fail( "GetParameterNames must not accept unescaped parameters: " + parameter );
		} catch (TR069Exception e) {
			assertEquals( "getParameterNames must throw 9005 for param: " + parameter,
					TR069Exception.INVALID_PARAMETER_NAME, e.getFaultCode());
		}
		try {
			connector.getParameterValue(parameter);
			fail( "GetParameterValue must not accept unescaped parameters: " + parameter );
		} catch (TR069Exception e) {
			assertEquals( "getParameterNames must throw 9005 for param: " + parameter,
					TR069Exception.INVALID_PARAMETER_NAME, e.getFaultCode());
		}
		try {
			connector.setParameterValue(parameter, "newValue", TR069Connector.TR069_STRING );
			fail( "SetParameterValue must not accept unescaped parameters: " + parameter );
		} catch (TR069Exception e) {
			assertEquals( "getParameterNames must throw 9005 for param: " + parameter,
					TR069Exception.INVALID_PARAMETER_NAME, e.getFaultCode());
		}
		try {
			connector.toURI(parameter, false );
			fail( "toUri must not accept unescaped parameters: " + parameter );
		} catch (TR069Exception e) {
			assertEquals( "getParameterNames must throw 9005 for param: " + parameter,
					TR069Exception.INVALID_PARAMETER_NAME, e.getFaultCode());
		}
	}
	
	/**
	 * Check that the connector accepts un-necessary encodings in pathes
	 * - getParameterNames
	 * - getParameterValues
	 * - setParameterValue
	 * - toUri
	 *
	 * It is expected that all methods pass without an error
	 * @param parameter
	 */
	private void assertUnnecessaryEscapingAccepted( String parameter ) {
		try {
			connector.getParameterNames(parameter, false);
			pass( "GetParameterNames accepts unrequired escapings on parameters: " + parameter );
		} catch (TR069Exception e) {
			e.printStackTrace();
			fail( "GetParameterNames must accept unrequired escapings on parameters: " + parameter );
		}
		try {
			connector.getParameterValue(parameter);
			pass( "GetParameterValue accepts unrequired escapings on parameters: " + parameter );
		} catch (TR069Exception e) {
			e.printStackTrace();
			fail( "GetParameterValue must accept unrequired escapings on parameters: " + parameter );
		}
		try {
			connector.setParameterValue(parameter, "newValue", TR069Connector.TR069_STRING );
			pass( "SetParameterValue accepts unrequired escapings on parameters: " + parameter );
		} catch (TR069Exception e) {
			e.printStackTrace();
			fail( "SetParameterValue must accept unrequired escapings on parameters: " + parameter );
		}
		try {
			connector.toURI(parameter, false );
			pass( "toURI accepts unrequired escapings on parameters: " + parameter );
		} catch (TR069Exception e) {
			e.printStackTrace();
			fail( "toURI must accept unrequired escapings on parameters: " + parameter );
		}
	}
	
}
