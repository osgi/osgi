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
package org.osgi.test.cases.dmt.tc1.tbc.DmtPrincipalPermission;

import info.dmtree.security.DmtPrincipalPermission;

import org.osgi.test.cases.dmt.tc1.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc1.tbc.DmtTestControl;

/**
 * This test class validates the implementation of <code>equals<code> method of DmtPrincipalPermission, 
 * according to MEG specification
 */
public class Equals extends DmtTestControl {
	/**
	 * Assert if two DmtPrincipalPermission instances are equal
	 * 
	 * DmtPrincipalPermission.equals(Object)
	 */
	public void testEquals001() {
		try {
			log("#testEquals001");

			DmtPrincipalPermission d1 = new DmtPrincipalPermission("*");
			DmtPrincipalPermission d2 = new DmtPrincipalPermission("*");

			assertTrue(
					"Assert if two DmtPrincipalPermission instances are equal",
					d1.equals(d2));

		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}
	
	/**
	 * Assert if two DmtPrincipalPermission instances are equal
	 * using the constructor with 2 parameters
	 * 
	 * DmtPrincipalPermission.equals(Object)
	 */
	public void testEquals002() {
		try {
			log("#testEquals002");

			DmtPrincipalPermission d1 = new DmtPrincipalPermission("*","*");
			DmtPrincipalPermission d2 = new DmtPrincipalPermission("*","*");

			assertTrue(
					"Assert if two DmtPrincipalPermission instances are equal",
					d1.equals(d2));

		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}

	/**
	 * Assert if two DmtPrincipalPermission instances are different
	 * 
	 * DmtPrincipalPermission.equals(Object)
	 */
	public void testEquals003() {
		try {
			log("#testEquals003");

			DmtPrincipalPermission d1 = new DmtPrincipalPermission(DmtConstants.PRINCIPAL);
			DmtPrincipalPermission d2 = new DmtPrincipalPermission("*");

			assertTrue(
					"Assert if two DmtPrincipalPermission instances are not equal",
					!d1.equals(d2));

		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}

	/**
	 * Assert if two DmtPrincipalPermission instances are different
	 * using the constructor with 2 parameters
	 * 
	 * DmtPrincipalPermission.equals(Object)
	 */
	public void testEquals004() {
		try {
			log("#testEquals004");

			DmtPrincipalPermission d1 = new DmtPrincipalPermission(DmtConstants.PRINCIPAL,"*");
			DmtPrincipalPermission d2 = new DmtPrincipalPermission("*","*");

			assertTrue(
					"Assert if two DmtPrincipalPermission instances are not equal",
					!d1.equals(d2));

		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}
    
    /**
     * Assert if two DmtPrincipalPermission instances are equal using different constructors
     * 
     * DmtPrincipalPermission.equals(Object)
     */
    public void testEquals005() {
        try {
            log("#testEquals005");

            DmtPrincipalPermission d1 = new DmtPrincipalPermission(DmtConstants.PRINCIPAL,"*");
            DmtPrincipalPermission d2 = new DmtPrincipalPermission(DmtConstants.PRINCIPAL);

            assertTrue(
					"Assert if two DmtPrincipalPermission instances are equal using different constructors",
					d1.equals(d2));

        } catch (Exception e) {
            failUnexpectedException(e);
        }
    }
}
