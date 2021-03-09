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
			new org.osgi.service.dmt.security.AlertPermission(null);
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
			new org.osgi.service.dmt.security.AlertPermission("");
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
			new org.osgi.service.dmt.security.AlertPermission(null,"*");
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
			new org.osgi.service.dmt.security.AlertPermission("","*");
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
			new org.osgi.service.dmt.security.AlertPermission(DmtConstants.REMOTE_SERVER,"");
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
			org.osgi.service.dmt.security.AlertPermission permission = new org.osgi.service.dmt.security.AlertPermission(DmtConstants.REMOTE_SERVER);
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
			org.osgi.service.dmt.security.AlertPermission permission = new org.osgi.service.dmt.security.AlertPermission(DmtConstants.REMOTE_SERVER,"*");
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
			new org.osgi.service.dmt.security.AlertPermission(DmtConstants.REMOTE_SERVER + "*");
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
			new org.osgi.service.dmt.security.AlertPermission(DmtConstants.REMOTE_SERVER + "*","*");
			pass("The target can end with '*'");
		} catch (Exception e) {
			failUnexpectedException(e);	
		}
			
	}
}
