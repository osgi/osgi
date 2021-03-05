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
package org.osgi.test.cases.residentialmanagement;

import java.util.HashSet;
import java.util.Set;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.Uri;

/**
 * This testcase checks that the root node of the RMT can be configured through the system property "org.osgi.dmt.residential".
 * @author steffen
 *
 */
public class RMTRootNodeTestCase extends RMTTestBase {
	
	

	/**
	 * asserts that the system property for the top-level location is set and contains an absolute and valid URI
	 * @throws Exception 
	 */
	public void testSystemProperty() throws Exception {
		String root = getProperty("org.osgi.dmt.residential");
		assertNotNull( "The system property 'org.osgi.dmt.residential' is not set.", root );
		assertTrue("The configured root must be an absolute Uri.", Uri.isAbsoluteUri(root));
		assertTrue("The configured root uri is invalid.", Uri.isValidUri(root));
	}
	
	/**
	 * checks existence configured top-level node and tries to open a session on each of its children
	 * @throws Exception
	 */
	public void testExistenceOfRootNode() throws Exception {
		String root = getProperty("org.osgi.dmt.residential");
		
		session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
		assertNotNull( session );
		assertTrue( "The configured RMT-root node does not exist:" + root, session.isNodeUri(root) );
	}
	
	/**
	 * asserts that the root node has the mandatory and no undefined children 
	 * @throws Exception
	 */
	public void testChildrenOfRootNode() throws Exception {
		String root = getProperty("org.osgi.dmt.residential");
		session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
		assertNotNull( session );
		
		Set<String> mandatory = new HashSet<String>();
		Set<String> optional = new HashSet<String>();
		Set<String> undefined = new HashSet<String>();
		mandatory.add(FRAMEWORK);
		optional.add(FILTER);
		optional.add(LOG);
		
		String[] children = session.getChildNodeNames(root);
		for (String child : children) {
			if ( ! mandatory.contains(child) && ! optional.contains(child))
				undefined.add(child);
			mandatory.remove(child);
		}
		
		assertEquals("Mandatory child-nodes are missing for the root node: " + mandatory, 0, mandatory.size());
		assertEquals("There are undefined child-nodes under thee root node: " + undefined, 0, undefined.size());
		session.close();
		
		// try to open session on each child
		for (String child : children) {
			session = dmtAdmin.getSession(root + "/" + child, DmtSession.LOCK_TYPE_SHARED);
			assertNotNull("Null DMT session for: " + root + "/" + child, session);
			session.close();
		}
	}
	
}
