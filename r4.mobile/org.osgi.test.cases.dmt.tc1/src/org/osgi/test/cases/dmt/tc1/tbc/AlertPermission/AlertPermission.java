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
public class AlertPermission {
	private DmtTestControl tbc;

	public AlertPermission(DmtTestControl tbc) {
		this.tbc = tbc;

	}

	public void run() {
		testAlertPermission001();
		testAlertPermission002();
		testAlertPermission003();
		testAlertPermission004();
		testAlertPermission005();
		testAlertPermission006();
		testAlertPermission007();
		testAlertPermission008();
		testAlertPermission009();
	}

	/**
	 * Asserts that NullPointerException is thrown if target is null
	 * 
	 * @spec AlertPermission.AlertPermission(String)
	 */
	private void testAlertPermission001() {
		try {		
			tbc.log("#testAlertPermission001");
			new info.dmtree.security.AlertPermission(null);
            tbc.failException("", NullPointerException.class);
		} catch(NullPointerException e) {
			tbc.pass(e.getClass().getName() + " correctly thrown");
		} catch(Exception e) {
			tbc.failExpectedOtherException(NullPointerException.class,e);
		}
			
	}
	/**
	 * Asserts that IllegalArgumentException is thrown if target is empty
	 * 
	 * @spec AlertPermission.AlertPermission(String)
	 */
	private void testAlertPermission002() {
		try {		
			tbc.log("#testAlertPermission002");
			new info.dmtree.security.AlertPermission("");
            tbc.failException("", IllegalArgumentException.class);
		} catch(IllegalArgumentException e) {
			tbc.pass(e.getClass().getName() + " correctly thrown");
		} catch(Exception e) {
			tbc.failExpectedOtherException(IllegalArgumentException.class,e);
		}
			
	}
	
	/**
	 * Asserts that NullPointerException is thrown if target is null
	 * using the constructor with 2 arguments
	 * 
	 * @spec AlertPermission.AlertPermission(String,String)
	 */
	private void testAlertPermission003() {
		try {		
			tbc.log("#testAlertPermission003");
			new info.dmtree.security.AlertPermission(null,"*");
            tbc.failException("", NullPointerException.class);
		} catch(NullPointerException e) {
			tbc.pass(e.getClass().getName() + " correctly thrown");
		} catch(Exception e) {
			tbc.failExpectedOtherException(NullPointerException.class,e);
		}
			
	}
	/**
	 * Asserts that IllegalArgumentException is thrown if target is empty
	 * 
	 * @spec AlertPermission.AlertPermission(String,String)
	 */
	private void testAlertPermission004() {
		try {		
			tbc.log("#testAlertPermission004");
			new info.dmtree.security.AlertPermission("","*");
            tbc.failException("", IllegalArgumentException.class);
		} catch(IllegalArgumentException e) {
			tbc.pass(e.getClass().getName() + " correctly thrown");
		} catch(Exception e) {
			tbc.failExpectedOtherException(IllegalArgumentException.class,e);
		}
			
	}
	/**
	 * Asserts that IllegalArgumentException is thrown if actions is not "*"
	 * 
	 * @spec AlertPermission.AlertPermission(String,String)
	 */
	private void testAlertPermission005() {
		try {		
			tbc.log("#testAlertPermission005");
			new info.dmtree.security.AlertPermission(DmtConstants.REMOTE_SERVER,"");
            tbc.failException("", IllegalArgumentException.class);
		} catch(IllegalArgumentException e) {
			tbc.pass(e.getClass().getName() + " correctly thrown");
		} catch(Exception e) {
			tbc.failExpectedOtherException(IllegalArgumentException.class,e);
		}
			
	}
	/**
	 * Asserts that getActions returns '*'
	 * 
	 * @spec AlertPermission.AlertPermission(String)
	 */
	private void testAlertPermission006() {
		try {		
			tbc.log("#testAlertPermission006");
			info.dmtree.security.AlertPermission permission = new info.dmtree.security.AlertPermission(DmtConstants.REMOTE_SERVER);
			tbc.assertEquals("Asserts that getActions returns '*'","*",permission.getActions());
		} catch (Exception e) {
			tbc.failUnexpectedException(e);	
		}
			
	}
	/**
	 * Asserts that getActions returns '*'
	 * 
	 * @spec AlertPermission.AlertPermission(String,String)
	 */
	private void testAlertPermission007() {
		try {		
			tbc.log("#testAlertPermission007");
			info.dmtree.security.AlertPermission permission = new info.dmtree.security.AlertPermission(DmtConstants.REMOTE_SERVER,"*");
			tbc.assertEquals("Asserts that getActions returns '*'","*",permission.getActions());
		} catch (Exception e) {
			tbc.failUnexpectedException(e);	
		}
			
	}
	/**
	 * Asserts that the target can end with '*'
	 * 
	 * @spec AlertPermission.AlertPermission(String)
	 */
	private void testAlertPermission008() {
		try {		
			tbc.log("#testAlertPermission008");
			new info.dmtree.security.AlertPermission(DmtConstants.REMOTE_SERVER + "*");
			tbc.pass("The target can end with '*'");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);	
		}
			
	}
	/**
	 * Asserts that the target can end with '*'
	 * 
	 * @spec AlertPermission.AlertPermission(String,String)
	 */
	private void testAlertPermission009() {
		try {		
			tbc.log("#testAlertPermission009");
			new info.dmtree.security.AlertPermission(DmtConstants.REMOTE_SERVER + "*","*");
			tbc.pass("The target can end with '*'");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);	
		}
			
	}
}
