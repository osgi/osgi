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
 * Date          Author(s)
 * CR            Headline
 * ============  ==============================================================
 * Mar 11, 2005  Luiz Felipe Guimaraes
 * 26            Implement MEG TCK
 * ============  ==============================================================
 * Mar 24, 2005  Andre Assad
 * 26            Implement MEG TCK for the deployment RFC-88
 * ============  ==============================================================
 */


package org.osgi.test.cases.deploymentadmin.tbc.DeploymentAdminPermission;

import java.util.StringTokenizer;

import org.osgi.test.cases.deploymentadmin.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tbc.util.MessagesConstants;

/**
 * @author Luiz Felipe Guimaraes
 * 
 * @methodUnderTest org.osgi.service.deploymentadmin.DeploymentAdminPermission#DeploymentAdminPermission
 * @generalDescription This Test Class Validates the implementation of
 *                     <code>DeploymentAdminPermission</code> method, according to MEG reference
 *                     documentation (rfc0088).
 */
public class DeploymentAdminPermission {
	private DeploymentTestControl tbc;

	public DeploymentAdminPermission(DeploymentTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testDeploymentAdminPermission001();
		testDeploymentAdminPermission002();
		testDeploymentAdminPermission003();
		testDeploymentAdminPermission004();
		testDeploymentAdminPermission005();
		testDeploymentAdminPermission006();
	}

	/**
	 * @testID testDeploymentAdminPermission001
	 * @testDescription This method asserts if the target is correctly set on
	 *                  DeploymentAdminPermission.
	 */
	public void testDeploymentAdminPermission001() {
		tbc.log("#testDeploymentAdminPermission001");
		try {
			org.osgi.service.deploymentadmin.DeploymentAdminPermission dpAdmPerm = new org.osgi.service.deploymentadmin.DeploymentAdminPermission(
					DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME0,
					org.osgi.service.deploymentadmin.DeploymentAdminPermission.ACTION_INSTALL);

			tbc.assertEquals("DeploymentAdminPermission target was correctly set ", DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME0, dpAdmPerm.getName());
			tbc.assertEquals("DeploymentAdminPermission action was correctly set", org.osgi.service.deploymentadmin.DeploymentAdminPermission.ACTION_INSTALL, dpAdmPerm.getActions());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	
	/**
	 * @testID testDeploymentAdminPermission002
	 * @testDescription This method asserts that actions on a
	 *                  DeploymentAdminPermission may have combinations of
	 *                  actions.
	 */
	public void testDeploymentAdminPermission002() {
		tbc.log("#testDeploymentAdminPermission002");
		try {
			String actions = org.osgi.service.deploymentadmin.DeploymentAdminPermission.ACTION_INSTALL
					+ ","
					+ org.osgi.service.deploymentadmin.DeploymentAdminPermission.ACTION_LIST
					+ ","
					+ org.osgi.service.deploymentadmin.DeploymentAdminPermission.ACTION_UNINSTALL;
			
			org.osgi.service.deploymentadmin.DeploymentAdminPermission dpAdmPerm = new org.osgi.service.deploymentadmin.DeploymentAdminPermission(
					DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME0, actions);
			
			StringTokenizer token = new StringTokenizer(dpAdmPerm.getActions(),",");
			boolean isInstall, isList, isUninstall, errorFound;
			isInstall = isList = isUninstall = errorFound = false;
			
			while (token.hasMoreTokens()) {
				String action = token.nextToken().trim();
				if (action.equals(org.osgi.service.deploymentadmin.DeploymentAdminPermission.ACTION_INSTALL)) {
					isInstall=true;
				} else if (action.equals(org.osgi.service.deploymentadmin.DeploymentAdminPermission.ACTION_LIST)) {
					isList=true;
				} else if (action.equals(org.osgi.service.deploymentadmin.DeploymentAdminPermission.ACTION_UNINSTALL)) {
					isUninstall=true;
				} else if (action.length()>0){
					errorFound = true;
				}
			
			}
			
			tbc.assertTrue("Asserts that the correct action list is returned",isInstall && isList && isUninstall && !errorFound);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	
	/**
	 * @testID testDeploymentAdminPermission003
	 * @testDescription This method asserts that DeploymentAdminPermission
	 *                  cannot receive a null deployment package name.
	 */
	public void testDeploymentAdminPermission003() {
		tbc.log("#testDeploymentAdminPermission003");
		try {
			org.osgi.service.deploymentadmin.DeploymentAdminPermission dpAdmPerm = new org.osgi.service.deploymentadmin.DeploymentAdminPermission(
					null, org.osgi.service.deploymentadmin.DeploymentAdminPermission.ACTION_INSTALL);
			
			tbc.failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalArgumentException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(),
							e.getClass().getName() }));
		}
	}
	
	/**
	 * @testID testDeploymentAdminPermission004
	 * @testDescription This method asserts that DeploymentAdminPermission
	 *                  cannot receive a null action string.
	 */
	public void testDeploymentAdminPermission004() {
		tbc.log("#testDeploymentAdminPermission004");
		try {
			org.osgi.service.deploymentadmin.DeploymentAdminPermission dpAdmPerm = new org.osgi.service.deploymentadmin.DeploymentAdminPermission(
					DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME0, null);
			
			tbc.failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalArgumentException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {
					IllegalArgumentException.class.getName(),
							e.getClass().getName() }));
		}
	}
	
	/**
	 * @testID testDeploymentAdminPermission005
	 * @testDescription This method asserts that DeploymentAdminPermission
	 *                  throws IllegalArgumentException when an invalid action is passed.
	 */
	public void testDeploymentAdminPermission005() {
		tbc.log("#testDeploymentAdminPermission005");
		try {
			String invalidAction = "invalid";
			org.osgi.service.deploymentadmin.DeploymentAdminPermission dpAdmPerm = new org.osgi.service.deploymentadmin.DeploymentAdminPermission(
					DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME0, invalidAction);
			
			tbc.failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException  e) {
			tbc.pass(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalArgumentException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException .class.getName(),
							e.getClass().getName() }));
		}
	}	
	/**
	 * @testID testDeploymentAdminPermission006
	 * @testDescription This method asserts that DeploymentAdminPermission
	 *                  throws IllegalArgumentException when use a invalid format.
	 */
	public void testDeploymentAdminPermission006() {
		tbc.log("#testDeploymentAdminPermission006");
		try {
			org.osgi.service.deploymentadmin.DeploymentAdminPermission dpAdmPerm = new org.osgi.service.deploymentadmin.DeploymentAdminPermission(
					DeploymentTestControl.INVALID_DEPLOYMENT_PACKAGE_NAME, org.osgi.service.deploymentadmin.DeploymentAdminPermission.ACTION_INSTALL);
			
			tbc.failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException  e) {
			tbc.pass(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalArgumentException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(),
							e.getClass().getName() }));
		}
	}
}
