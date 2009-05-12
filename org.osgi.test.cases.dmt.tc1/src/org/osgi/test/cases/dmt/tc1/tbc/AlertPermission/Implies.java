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
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
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
 * Jul 12, 2005 Luiz Felipe Guimaraes
 * 1            Implement MEGTCK  
 * ===========  ==============================================================
 */
package org.osgi.test.cases.dmt.tc1.tbc.AlertPermission;

import info.dmtree.security.AlertPermission;

import org.osgi.test.cases.dmt.tc1.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc1.tbc.DmtTestControl;

/**
 * This test case validates the implementation of <code>implies<code> method of AlertPermission, 
 * according to MEG specification
 */
public class Implies extends DmtTestControl {

	/**
	 * This method asserts that an object implies other object using the same target
	 * 
	 * @spec AlertPermission.implies(Permission)
	 */
	public void testImplies001() {
		try {
			log("#testImplies001");

			AlertPermission d1 = new AlertPermission(DmtConstants.REMOTE_SERVER);
			AlertPermission d2 = new AlertPermission(DmtConstants.REMOTE_SERVER);

			assertTrue(
							"Asserts that an object implies other object using the same target",
							d1.implies(d2));

		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}

	/**
	 * This method asserts that an object implies other object using the same target and actions
	 * 
	 * @spec AlertPermission.implies(Permission)
	 */
	public void testImplies002() {
		try {
			log("#testImplies002");

			AlertPermission d1 = new AlertPermission(
					DmtConstants.REMOTE_SERVER, "*");
			AlertPermission d2 = new AlertPermission(
					DmtConstants.REMOTE_SERVER, "*");

			assertTrue(
							"Asserts that an object implies other object using the same target and actions",
							d1.implies(d2));

		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}

	/**
	 * This method asserts that an object does not imply other object using the different target and actions
	 * 
	 * @spec AlertPermission.implies(Permission)
	 */
	public void testImplies003() {
		try {
			log("#testImplies003");

			AlertPermission d1 = new AlertPermission(
					DmtConstants.REMOTE_SERVER, "*");
			AlertPermission d2 = new AlertPermission(DmtConstants.REMOTE_SERVER + "a", "*");

			assertTrue(
							"Asserts that an object does not imply other object using the different target and actions",
							!d1.implies(d2));

		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}
    
    /**
     * Asserts that an object implies other object using the '*' wildcard
     * 
     * @spec AlertPermission.implies(Permission)
     */
    public void testImplies004() {
        try {
            log("#testImplies004");

            AlertPermission d1 = new AlertPermission(
                    DmtConstants.REMOTE_SERVER+"*", "*");
            AlertPermission d2 = new AlertPermission(DmtConstants.REMOTE_SERVER + "a", "*");

            assertTrue(
                            "Asserts that an object implies other object using the '*' wildcard",
                            d1.implies(d2));

        } catch (Exception e) {
            failUnexpectedException(e);
        }
    }

}
