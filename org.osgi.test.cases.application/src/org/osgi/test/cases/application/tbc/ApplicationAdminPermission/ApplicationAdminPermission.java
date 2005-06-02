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
 * Apr 5, 2005  Alexandre Santos
 * 38           Implement MEGTCK for the application RFC 
 * ===========  ==============================================================
 */
package org.osgi.test.cases.application.tbc.ApplicationAdminPermission;

import org.osgi.test.cases.application.tbc.ApplicationTestControl;
import org.osgi.test.cases.application.tbc.util.MessagesConstants;

/**
 * @methodUnderTest org.osgi.service.application.ApplicationAdminPermission#ApplicationAdminPermission
 * @generalDescription This Test Class Validates the implementation of
 *                     <code>ApplicationAdminPermission<code> method, according to MEG reference
 *                     documentation.
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
	}

	/**
	 * @testID testApplicationAdminPermission001
	 * @testDescription Assert if a NullPointerException is thrown when pass
	 *                  null to actions parameter.
	 */
	public void testApplicationAdminPermission001() {
		try {
			tbc.log("#testApplicationAdminPermission001");
			new org.osgi.service.application.ApplicationAdminPermission(
					ApplicationTestControl.TEST1_PID, null);
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
	 * @testID testApplicationAdminPermission002
	 * @testDescription Assert if passing an invalid format as action parameter
	 *                  it will thrown an IllegalArgumentException.
	 */
	public void testApplicationAdminPermission002() {

		// TODO not specified what happens when pass invalid actions as
		// parameter
		try {
			tbc.log("#testApplicationAdminPermission002");
			new org.osgi.service.application.ApplicationAdminPermission(
					ApplicationTestControl.TEST1_PID,
					ApplicationTestControl.ACTIONS_INVALID_FORMAT);
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
	 * @testID testApplicationAdminPermission003
	 * @testDescription Assert if a NullPointerException is not thrown when pass
	 *                  null as pid.
	 */
	public void testApplicationAdminPermission003() {
		try {
			tbc.log("#testApplicationAdminPermission003");
			new org.osgi.service.application.ApplicationAdminPermission(null,
					ApplicationTestControl.ACTIONS1);
			tbc.pass("The ApplicationAdminPermission was created normally.");
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}

	/**
	 * @testID testApplicationAdminPermission004
	 * @testDescription Assert if no Exception is thrown when pass valid
	 *                  parameters.
	 * 
	 */
	public void testApplicationAdminPermission004() {
		try {
			tbc.log("#testApplicationAdminPermission004");
			new org.osgi.service.application.ApplicationAdminPermission(
					ApplicationTestControl.TEST1_PID,
					ApplicationTestControl.ACTIONS1);
			tbc.pass("The ApplicationAdminPermission was created normally.");
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}
}
