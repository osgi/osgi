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
		String root = System.getProperty("org.osgi.dmt.residential"); 
		assertNotNull( "The system property 'org.osgi.dmt.residential' is not set.", root );
		assertEquals("The configured root must be an absolute Uri.", Uri.isAbsoluteUri(root));
		assertTrue("The configured root uri is invalid.", Uri.isValidUri(root));
	}
	
	/**
	 * checks existence configured top-level node and tries to open a session on each of its children
	 * @throws Exception
	 */
	public void testExistenceOfRootNode() throws Exception {
		String root = System.getProperty("org.osgi.dmt.residential");
		
		session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
		assertNotNull( session );
		assertTrue( "The configured RMT-root node does not exist:" + root, session.isNodeUri(root) );
	}
	
	/**
	 * asserts that the root node has the mandatory and no undefined children 
	 * @throws Exception
	 */
	public void testChildrenOfRootNode() throws Exception {
		String root = System.getProperty("org.osgi.dmt.residential");
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
		
		assertTrue( "Mandatory child-nodes are missing for the root node: " + mandatory, mandatory.size() == 0 );
		assertTrue( "There are undefined child-nodes under thee root node: " + undefined, undefined.size() == 0 );
		session.close();
		
		// try to open session on each child
		for (String child : children) {
			session = dmtAdmin.getSession("./" + child);
			assertNotNull(session);
			session.close();
		}
	}
	
}
