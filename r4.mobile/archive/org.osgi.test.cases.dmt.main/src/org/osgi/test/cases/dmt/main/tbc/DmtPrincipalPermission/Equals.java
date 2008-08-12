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
 * Jun 27, 2005 Leonardo Barros
 * 34           Implement MEGTCK for the application RFC 
 * ===========  ==============================================================
 */
package org.osgi.test.cases.dmt.main.tbc.DmtPrincipalPermission;

import org.osgi.service.dmt.security.DmtPrincipalPermission;
import org.osgi.test.cases.dmt.main.tbc.DmtConstants;
import org.osgi.test.cases.dmt.main.tbc.DmtTestControl;

/**
 * This test class validates the implementation of <code>equals<code> method of DmtPrincipalPermission, 
 * according to MEG specification
 */
public class Equals {
	private DmtTestControl tbc;

	public Equals(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testEquals001();
		testEquals002();
		testEquals003();
		testEquals004();
        testEquals005();
	}

	/**
	 * Assert if two DmtPrincipalPermission instances are equal
	 * 
	 * DmtPrincipalPermission.equals(Object)
	 */
	private void testEquals001() {
		try {
			tbc.log("#testEquals001");

			DmtPrincipalPermission d1 = new DmtPrincipalPermission("*");
			DmtPrincipalPermission d2 = new DmtPrincipalPermission("*");

			tbc.assertTrue("Assert if two DmtPrincipalPermission instances are equal", d1.equals(d2));

		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}
	}
	
	/**
	 * Assert if two DmtPrincipalPermission instances are equal
	 * using the constructor with 2 parameters
	 * 
	 * DmtPrincipalPermission.equals(Object)
	 */
	private void testEquals002() {
		try {
			tbc.log("#testEquals002");

			DmtPrincipalPermission d1 = new DmtPrincipalPermission("*","*");
			DmtPrincipalPermission d2 = new DmtPrincipalPermission("*","*");

			tbc.assertTrue("Assert if two DmtPrincipalPermission instances are equal", d1.equals(d2));

		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}
	}

	/**
	 * Assert if two DmtPrincipalPermission instances are different
	 * 
	 * DmtPrincipalPermission.equals(Object)
	 */
	private void testEquals003() {
		try {
			tbc.log("#testEquals003");

			DmtPrincipalPermission d1 = new DmtPrincipalPermission(DmtConstants.PRINCIPAL);
			DmtPrincipalPermission d2 = new DmtPrincipalPermission("*");

			tbc.assertTrue("Assert if two DmtPrincipalPermission instances are not equal", !d1.equals(d2));

		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}
	}

	/**
	 * Assert if two DmtPrincipalPermission instances are different
	 * using the constructor with 2 parameters
	 * 
	 * DmtPrincipalPermission.equals(Object)
	 */
	private void testEquals004() {
		try {
			tbc.log("#testEquals004");

			DmtPrincipalPermission d1 = new DmtPrincipalPermission(DmtConstants.PRINCIPAL,"*");
			DmtPrincipalPermission d2 = new DmtPrincipalPermission("*","*");

			tbc.assertTrue("Assert if two DmtPrincipalPermission instances are not equal", !d1.equals(d2));

		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}
	}
    
    /**
     * Assert if two DmtPrincipalPermission instances are equal using different constructors
     * 
     * DmtPrincipalPermission.equals(Object)
     */
    private void testEquals005() {
        try {
            tbc.log("#testEquals005");

            DmtPrincipalPermission d1 = new DmtPrincipalPermission(DmtConstants.PRINCIPAL,"*");
            DmtPrincipalPermission d2 = new DmtPrincipalPermission(DmtConstants.PRINCIPAL);

            tbc.assertTrue("Assert if two DmtPrincipalPermission instances are equal using different constructors", d1.equals(d2));

        } catch (Exception e) {
            tbc.fail("Unexpected exception: " + e.getClass().getName());
        }
    }
}
