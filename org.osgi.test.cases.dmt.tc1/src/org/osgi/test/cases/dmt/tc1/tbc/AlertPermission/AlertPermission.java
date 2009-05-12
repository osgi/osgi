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
 */

/* REVISION HISTORY:
 *
 * Date         Author(s)
 * CR           Headline
 * ===========  ==============================================================
 * 05/07/2005   Luiz Felipe Guimaraes
 * 1            Implement TCK
 * ===========  ==============================================================
 */

package org.osgi.test.cases.dmt.tc1.tbc.AlertPermission;

import org.osgi.test.cases.dmt.tc1.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc1.tbc.DmtTestControl;

/**
 * 
 * This test case validates the implementation of <code>AlertPermission</code> constructor, 
 * according to MEG specification
 */
public class AlertPermission extends DmtTestControl {

	/**
	 * Asserts that NullPointerException is thrown if target is null
	 * 
	 * @spec AlertPermission.AlertPermission(String)
	 */
	public void testAlertPermission001() {
		try {		
			log("#testAlertPermission001");
			new info.dmtree.security.AlertPermission(null);
            failException("", NullPointerException.class);
		} catch(NullPointerException e) {
			pass(e.getClass().getName() + " correctly thrown");
		} catch(Exception e) {
			failExpectedOtherException(NullPointerException.class, e);
		}
			
	}
	/**
	 * Asserts that IllegalArgumentException is thrown if target is empty
	 * 
	 * @spec AlertPermission.AlertPermission(String)
	 */
	public void testAlertPermission002() {
		try {		
			log("#testAlertPermission002");
			new info.dmtree.security.AlertPermission("");
            failException("", IllegalArgumentException.class);
		} catch(IllegalArgumentException e) {
			pass(e.getClass().getName() + " correctly thrown");
		} catch(Exception e) {
			failExpectedOtherException(IllegalArgumentException.class, e);
		}
			
	}
	
	/**
	 * Asserts that NullPointerException is thrown if target is null
	 * using the constructor with 2 arguments
	 * 
	 * @spec AlertPermission.AlertPermission(String,String)
	 */
	public void testAlertPermission003() {
		try {		
			log("#testAlertPermission003");
			new info.dmtree.security.AlertPermission(null,"*");
            failException("", NullPointerException.class);
		} catch(NullPointerException e) {
			pass(e.getClass().getName() + " correctly thrown");
		} catch(Exception e) {
			failExpectedOtherException(NullPointerException.class, e);
		}
			
	}
	/**
	 * Asserts that IllegalArgumentException is thrown if target is empty
	 * 
	 * @spec AlertPermission.AlertPermission(String,String)
	 */
	public void testAlertPermission004() {
		try {		
			log("#testAlertPermission004");
			new info.dmtree.security.AlertPermission("","*");
            failException("", IllegalArgumentException.class);
		} catch(IllegalArgumentException e) {
			pass(e.getClass().getName() + " correctly thrown");
		} catch(Exception e) {
			failExpectedOtherException(IllegalArgumentException.class, e);
		}
			
	}
	/**
	 * Asserts that IllegalArgumentException is thrown if actions is not "*"
	 * 
	 * @spec AlertPermission.AlertPermission(String,String)
	 */
	public void testAlertPermission005() {
		try {		
			log("#testAlertPermission005");
			new info.dmtree.security.AlertPermission(DmtConstants.REMOTE_SERVER,"");
            failException("", IllegalArgumentException.class);
		} catch(IllegalArgumentException e) {
			pass(e.getClass().getName() + " correctly thrown");
		} catch(Exception e) {
			failExpectedOtherException(IllegalArgumentException.class, e);
		}
			
	}
	/**
	 * Asserts that getActions returns '*'
	 * 
	 * @spec AlertPermission.AlertPermission(String)
	 */
	public void testAlertPermission006() {
		try {		
			log("#testAlertPermission006");
			info.dmtree.security.AlertPermission permission = new info.dmtree.security.AlertPermission(DmtConstants.REMOTE_SERVER);
			assertEquals("Asserts that getActions returns '*'", "*", permission
					.getActions());
		} catch (Exception e) {
			failUnexpectedException(e);	
		}
			
	}
	/**
	 * Asserts that getActions returns '*'
	 * 
	 * @spec AlertPermission.AlertPermission(String,String)
	 */
	public void testAlertPermission007() {
		try {		
			log("#testAlertPermission007");
			info.dmtree.security.AlertPermission permission = new info.dmtree.security.AlertPermission(DmtConstants.REMOTE_SERVER,"*");
			assertEquals("Asserts that getActions returns '*'", "*", permission
					.getActions());
		} catch (Exception e) {
			failUnexpectedException(e);	
		}
			
	}
	/**
	 * Asserts that the target can end with '*'
	 * 
	 * @spec AlertPermission.AlertPermission(String)
	 */
	public void testAlertPermission008() {
		try {		
			log("#testAlertPermission008");
			new info.dmtree.security.AlertPermission(DmtConstants.REMOTE_SERVER + "*");
			pass("The target can end with '*'");
		} catch (Exception e) {
			failUnexpectedException(e);	
		}
			
	}
	/**
	 * Asserts that the target can end with '*'
	 * 
	 * @spec AlertPermission.AlertPermission(String,String)
	 */
	public void testAlertPermission009() {
		try {		
			log("#testAlertPermission009");
			new info.dmtree.security.AlertPermission(DmtConstants.REMOTE_SERVER + "*","*");
			pass("The target can end with '*'");
		} catch (Exception e) {
			failUnexpectedException(e);	
		}
			
	}
}
