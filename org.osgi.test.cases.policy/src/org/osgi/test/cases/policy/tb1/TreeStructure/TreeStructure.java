/*
 * Copyright (c) The OSGi Alliance (2004). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR 7IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 * 
 */

/*
 * REVISION HISTORY:
 *
 * Date         Author(s)
 * CR           Headline
 * ===========  ==============================================================
 * 07/03/2005   Leonardo Barros
 * CR 33        Implement MEG TCK
 * ===========  ==============================================================
 * 06/06/2005   Eduardo Oliveira
 * 101          Updates implementation after Inspection JSTD-MEGTCK-CODE-INSP017  
 * ===========  ==============================================================
 */
package org.osgi.test.cases.policy.tb1.TreeStructure;

import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.policy.tbc.PolicyTestControl;
import org.osgi.test.cases.policy.tbc.TestInterface;
import org.osgi.test.cases.policy.tbc.util.MessagesConstants;

/**
 * @generalDescription This class tests the management tree structure for
 *                     Permissions according with MEG specification (rfc0092)
 */
public class TreeStructure implements TestInterface {
	private PolicyTestControl tbc;

	public TreeStructure(PolicyTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testTreeStructure001();
		testTreeStructure002();
		testTreeStructure003();
		testTreeStructure004();
		testTreeStructure005();
	}

	/**
	 * @testID testTreeStructure001
	 * @testDescription Asserts if $/Policy/Java is a valid node
	 */
	public void testTreeStructure001() {
		tbc.log("#testTreeStructure001");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(PolicyTestControl.OSGI_ROOT,
					DmtSession.LOCK_TYPE_SHARED);

			tbc.assertTrue("Asserts if $/Policy/Java is a valid node", session
					.isNodeUri(PolicyTestControl.POLICY_JAVA_NODE));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			try {
				session.close();
			} catch (DmtException e) {
				tbc.fail(MessagesConstants.getMessage(
						MessagesConstants.UNEXPECTED_EXCEPTION,
						new String[] { e.getClass().getName() }));
			}
		}
	}

	/**
	 * @testID testTreeStructure002
	 * @testDescription Asserts if $/Policy/Java/LocationPermission is a valid
	 *                  node
	 */
	public void testTreeStructure002() {
		tbc.log("#testTreeStructure002");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(PolicyTestControl.OSGI_ROOT,
					DmtSession.LOCK_TYPE_SHARED);

			tbc
					.assertTrue(
							"Asserts if $/Policy/Java/LocationPermission is a valid node",
							session
									.isNodeUri(PolicyTestControl.LOCATION_PERMISSION_NODE));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			try {
				session.close();
			} catch (DmtException e) {
				tbc.fail(MessagesConstants.getMessage(
						MessagesConstants.UNEXPECTED_EXCEPTION,
						new String[] { e.getClass().getName() }));
			}
		}
	}

	/**
	 * @testID testTreeStructure003
	 * @testDescription Asserts if $/Policy/Java/PrincipalPermission is a valid
	 *                  node
	 */
	public void testTreeStructure003() {
		tbc.log("#testTreeStructure003");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(PolicyTestControl.OSGI_ROOT,
					DmtSession.LOCK_TYPE_SHARED);

			tbc
					.assertTrue(
							"Asserts if $/Policy/Java/PrincipalPermission is a valid node",
							session
									.isNodeUri(PolicyTestControl.PRINCIPAL_PERMISSION_NODE));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			try {
				session.close();
			} catch (DmtException e) {
				tbc.fail(MessagesConstants.getMessage(
						MessagesConstants.UNEXPECTED_EXCEPTION,
						new String[] { e.getClass().getName() }));
			}
		}
	}

	/**
	 * @testID testTreeStructure004
	 * @testDescription Asserts if $/Policy/Java/ConditionalPermission is a
	 *                  valid node
	 */
	public void testTreeStructure004() {
		tbc.log("#testTreeStructure004");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(PolicyTestControl.OSGI_ROOT,
					DmtSession.LOCK_TYPE_SHARED);

			tbc
					.assertTrue(
							"Asserts if $/Policy/Java/ConditionalPermission is a valid node",
							session
									.isNodeUri(PolicyTestControl.CONDITIONAL_PERMISSION_NODE));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			try {
				session.close();
			} catch (DmtException e) {
				tbc.fail(MessagesConstants.getMessage(
						MessagesConstants.UNEXPECTED_EXCEPTION,
						new String[] { e.getClass().getName() }));
			}
		}
	}

	/**
	 * @testID testTreeStructure005
	 * @testDescription Asserts if $/Policy/Java/LocationPermission/default is a
	 *                  valid node
	 */
	public void testTreeStructure005() {
		tbc.log("#testTreeStructure005");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(PolicyTestControl.OSGI_ROOT,
					DmtSession.LOCK_TYPE_SHARED);

			tbc
					.assertTrue(
							"Asserts if $/Policy/Java/LocationPermission/Default is a valid node",
							session
									.isNodeUri(PolicyTestControl.DEFAULT_PERMISSION_NODE));

			tbc
					.assertTrue(
							"Asserts if $/Policy/Java/LocationPermission/Default is a leaf node",
							session
									.isLeafNode(PolicyTestControl.DEFAULT_PERMISSION_NODE));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			try {
				session.close();
			} catch (DmtException e) {
				tbc.fail(MessagesConstants.getMessage(
						MessagesConstants.UNEXPECTED_EXCEPTION,
						new String[] { e.getClass().getName() }));
			}
		}
	}

}
