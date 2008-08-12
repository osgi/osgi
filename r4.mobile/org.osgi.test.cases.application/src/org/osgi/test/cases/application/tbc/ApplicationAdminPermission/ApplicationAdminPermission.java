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
 * 25/08/2005   Alexandre Santos
 * 153          Implement OAT test cases  
 * ===========  ==============================================================
 */
package org.osgi.test.cases.application.tbc.ApplicationAdminPermission;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.application.ApplicationDescriptor;
import org.osgi.test.cases.application.tbc.ApplicationConstants;
import org.osgi.test.cases.application.tbc.ApplicationTestControl;
import org.osgi.test.cases.application.tbc.util.MessagesConstants;

/**
 * @author Alexandre Santos
 * 
 * This test class validates the implementation of
 * <code>ApplicationAdminPermission</code> constructor, according to MEG reference
 * documentation.
 */
public class ApplicationAdminPermission {
	private ApplicationTestControl tbc;

	/**
	 * @param tbc
	 */
	public ApplicationAdminPermission(ApplicationTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testApplicationAdminPermission001();
		testApplicationAdminPermission002();
		testApplicationAdminPermission003();
		testApplicationAdminPermission004();
		testApplicationAdminPermission005();
		testApplicationAdminPermission006();
		testApplicationAdminPermission007();
		testApplicationAdminPermission008();
		testApplicationAdminPermission009();
	}
	
    /**
     * This method asserts if a NullPointerException is thrown when we pass
     * null as actions parameter.
     * 
     * @spec ApplicationAdminPermission.ApplicationAdminPermission(String,String)
     */         
	private void testApplicationAdminPermission001() {
		try {
			tbc.log("#testApplicationAdminPermission001");
			new org.osgi.service.application.ApplicationAdminPermission(
					ApplicationConstants.APPLICATION_PERMISSION_FILTER1, null);
			tbc.failException("", NullPointerException.class);
		} catch (NullPointerException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { e.getClass().getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							NullPointerException.class.getName(),
							e.getClass().getName() }));
		}
	}

    /**
     * This method asserts if passing an invalid format as action parameter
     * it will thrown an IllegalArgumentException.
     * 
     * @spec ApplicationAdminPermission.ApplicationAdminPermission(String,String)
     */    
	private void testApplicationAdminPermission002() {

		try {
			tbc.log("#testApplicationAdminPermission002");
			new org.osgi.service.application.ApplicationAdminPermission(
					ApplicationConstants.APPLICATION_PERMISSION_FILTER1,
					ApplicationConstants.ACTIONS_INVALID_FORMAT);
			tbc.failException("", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { e.getClass().getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(),
							e.getClass().getName() }));
		}
	}

    /**
     * This method asserts if no exception is thrown when pass
     * null as filter.
     * 
     * @spec ApplicationAdminPermission.ApplicationAdminPermission(String,String)
     */        
	private void testApplicationAdminPermission003() {
		try {
			tbc.log("#testApplicationAdminPermission003");
			org.osgi.service.application.ApplicationAdminPermission ap = new org.osgi.service.application.ApplicationAdminPermission((String) null,
					ApplicationConstants.ACTIONS);
			tbc.assertEquals("Asserting if the actions passed as parameter are the same returned.", ApplicationConstants.ACTIONS, ap.getActions());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}

    /**
     * This method asserts if no exception is thrown when we have passed
     * valid parameters.
     * 
     * @spec ApplicationAdminPermission.ApplicationAdminPermission(String,String)
     */ 
	private void testApplicationAdminPermission004() {
		try {
			tbc.log("#testApplicationAdminPermission004");
			org.osgi.service.application.ApplicationAdminPermission ap = new org.osgi.service.application.ApplicationAdminPermission(
					ApplicationConstants.APPLICATION_PERMISSION_FILTER1,
					ApplicationConstants.ACTIONS);
			tbc.assertEquals("Asserting if the actions passed as parameter are the same returned.", ApplicationConstants.ACTIONS, ap.getActions());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}
	
    /**
     * This method asserts if InvalidSyntaxException is thrown when we use
     * an option that does not exist.
     * 
     * @spec ApplicationAdminPermission.ApplicationAdminPermission(String,String)
     */ 
	private void testApplicationAdminPermission005() {
		try {
			tbc.log("#testApplicationAdminPermission005");
			new org.osgi.service.application.ApplicationAdminPermission(
					ApplicationConstants.APPLICATION_PERMISSION_FILTER_INVALID1,
					ApplicationConstants.ACTIONS);
			tbc.failException("", InvalidSyntaxException.class);			
		} catch (InvalidSyntaxException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { e.getClass().getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							InvalidSyntaxException.class.getName(),
							e.getClass().getName() }));
		}
	}	
	
    /**
     * This method asserts that no exception is thrown when we use
     * wildcard in parameters.
     * 
     * @spec ApplicationAdminPermission.ApplicationAdminPermission(String,String)
     */ 
	private void testApplicationAdminPermission006() {
		try {
			tbc.log("#testApplicationAdminPermission006");
			org.osgi.service.application.ApplicationAdminPermission ap = new org.osgi.service.application.ApplicationAdminPermission(
					ApplicationConstants.APPLICATION_PERMISSION_FILTER2,
					"*");
			tbc.assertEquals("Asserting if the actions passed as parameter are the same returned.", "*", ap.getActions());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}	
	
    /**
     * This method asserts that NullPointerException is thrown when null
     * is passed as application.
     * 
     * @spec ApplicationAdminPermission.ApplicationAdminPermission(ApplicationDescriptor,String)
     */ 
	private void testApplicationAdminPermission007() {
		try {
			tbc.log("#testApplicationAdminPermission007");
			new org.osgi.service.application.ApplicationAdminPermission((ApplicationDescriptor) null,
					"*");
			tbc.failException("", NullPointerException.class);
		} catch (NullPointerException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { e.getClass().getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							NullPointerException.class.getName(),
							e.getClass().getName() }));
		}
	}	
	
    /**
     * This method asserts that NullPointerException is thrown if the actions
     * parameter is null. 
     * 
     * @spec ApplicationAdminPermission.ApplicationAdminPermission(ApplicationDescriptor,String)
     */ 
	private void testApplicationAdminPermission008() {
		try {
			tbc.log("#testApplicationAdminPermission008");
			new org.osgi.service.application.ApplicationAdminPermission(tbc.getAppDescriptor(),
					null);
			tbc.failException("", NullPointerException.class);
		} catch (NullPointerException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { e.getClass().getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							NullPointerException.class.getName(),
							e.getClass().getName() }));
		}
	}	
    /**
     * This method asserts if no exception is thrown when we have passed
     * valid parameters. It also tests the getActions method.
     * 
     * @spec ApplicationAdminPermission.ApplicationAdminPermission(ApplicationDescriptor,String)
     */ 
	private void testApplicationAdminPermission009() {
		try {
			tbc.log("#testApplicationAdminPermission009");
			org.osgi.service.application.ApplicationAdminPermission ap = new org.osgi.service.application.ApplicationAdminPermission(
					tbc.getAppDescriptor(),
					ApplicationConstants.ACTIONS);
			tbc.assertEquals("Asserting if the actions passed as parameter are the same returned.", ApplicationConstants.ACTIONS, ap.getActions());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}
	
}