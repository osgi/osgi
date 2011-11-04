/*
 * Copyright (c) OSGi Alliance (2000, 2010). All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.test.cases.residentialmanagement;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;


/**
 * This test case checks the possible operations on the Framework subtree
 * This includes 
 * - get and set the Framework-Startlevel
 * - install and uninstall a bundle
 * - change the state of an installed bundle
 * 
 * @author Steffen Druesedow (Deutsche Telekom Laboratories)
 */
public class FilterOperationsTestCase extends RMTTestBase {

	/**
	 * Check behavior for invalid targets.
	 * Attempts to get results for invalid targets. It is expected that a
	 * DmtException is thrown - otherwise the test fails.
	 * @throws Exception 
	 */
	public void testInvalidTargets() throws Exception {

		session = dmtAdmin.getSession(FILTER_ROOT, DmtSession.LOCK_TYPE_ATOMIC);
		assertNotNull(session);
		
		// create filter to initialize creation of the automatic nodes
		String uri = FILTER_ROOT + "/" + "InvalidTargetsTest";
		session.createInteriorNode( uri );
		session.commit(); // TODO: This commit should not be necessary - needs check
		
		try {
			assertInvalidTargetRejected(uri, "A/" );		// must be absolute
			assertInvalidTargetRejected(uri, "./A" );		// must end with slash
			assertInvalidTargetRejected(uri, "./A/-/" );	
			assertInvalidTargetRejected(uri, "./A/-/*/" );
			assertInvalidTargetRejected(uri, "./A/*/-/" );
		} finally {
			session.deleteNode(uri);
			session.commit();
		}
	}
	
	/**
	 * Checks behavior for invalid filters.
	 * @throws Exception
	 */
	public void testInvalidFilters() throws Exception {
		pass( "The behavior in case of invalid filter expressions is not specified!");
	}
	
	
	/**
	 * checks that the Result and ResultUriList is empty, if no target it set
	 * @throws Exception
	 */
	public void testEmptyResultIfNoTarget() throws Exception {
		session = dmtAdmin.getSession(FILTER_ROOT, DmtSession.LOCK_TYPE_ATOMIC);
		assertNotNull(session);
		
		// create filter to initialize creation of the automatic nodes
		String uri = FILTER_ROOT + "/" + "InvalidTargetsTest";
		session.createInteriorNode( uri );
		session.commit(); // TODO: This commit should not be necessary - needs check
		// force a search
		String[] children = session.getChildNodeNames(uri + "/" + RESULT_URI_LIST );
		assertEquals("The ResultUriList must be empty if no target is set.", 0, children.length);
		children = session.getChildNodeNames(uri + "/" + RESULT );
		assertEquals("The Result must be empty if no target is set.", 0, children.length);

	}
	
	
	private void assertInvalidTargetRejected( String uri, String target ) throws Exception {
		session.setNodeValue(uri + "/" + TARGET, new DmtData(target ));
		session.commit(); // TODO: This commit should not be necessary - needs check
		try {
			// force a search
			session.getChildNodeNames(uri + "/" + RESULT_URI_LIST );
			fail( "The target \"" + target + "\" must not be accepted." );
		} catch (DmtException e) {
			pass( "Success: Target \"" + target + "\" was correctly rejected." );
		}
	}

}
