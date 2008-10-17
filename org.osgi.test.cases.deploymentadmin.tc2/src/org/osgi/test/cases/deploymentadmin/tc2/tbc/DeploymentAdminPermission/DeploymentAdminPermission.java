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


package org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentAdminPermission;

import java.util.StringTokenizer;

import org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentConstants;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.util.MessagesConstants;

/**
 * @author Luiz Felipe Guimaraes
 * 
 * This test class validates the implementation of <code>DeploymentAdminPermission</code> constructor, 
 * according to MEG specification.
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
		testDeploymentAdminPermission007();
        testDeploymentAdminPermission008();
        testDeploymentAdminPermission009();
	}

	/**
	 * This method asserts if the target and the actions are correctly set on DeploymentAdminPermission.
	 * 
	 * @spec DeploymentAdminPermission.DeploymentAdminPermission(String,String)
	 */
	private void testDeploymentAdminPermission001() {
		tbc.log("#testDeploymentAdminPermission001");
		try {
			org.osgi.service.deploymentadmin.DeploymentAdminPermission dpAdmPerm = new org.osgi.service.deploymentadmin.DeploymentAdminPermission(
					DeploymentConstants.DEPLOYMENT_PACKAGE_NAME0,
					org.osgi.service.deploymentadmin.DeploymentAdminPermission.INSTALL);

			tbc.assertEquals("DeploymentAdminPermission target was correctly set ", DeploymentConstants.DEPLOYMENT_PACKAGE_NAME0, dpAdmPerm.getName());
			tbc.assertEquals("DeploymentAdminPermission action was correctly set", org.osgi.service.deploymentadmin.DeploymentAdminPermission.INSTALL, dpAdmPerm.getActions());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	
	/**
	 * This method asserts that actions on a DeploymentAdminPermission may have combinations of actions.
	 * 
	 * @spec DeploymentAdminPermission.DeploymentAdminPermission(String,String) 
	 */
	private void testDeploymentAdminPermission002() {
		tbc.log("#testDeploymentAdminPermission002");
		try {
			String actions = org.osgi.service.deploymentadmin.DeploymentAdminPermission.INSTALL
					+ ","
					+ org.osgi.service.deploymentadmin.DeploymentAdminPermission.LIST
					+ ","
					+ org.osgi.service.deploymentadmin.DeploymentAdminPermission.UNINSTALL;
			
			org.osgi.service.deploymentadmin.DeploymentAdminPermission dpAdmPerm = new org.osgi.service.deploymentadmin.DeploymentAdminPermission(
					DeploymentConstants.DEPLOYMENT_PACKAGE_NAME0, actions);
			
			StringTokenizer token = new StringTokenizer(dpAdmPerm.getActions(),",");
			boolean isInstall, isList, isUninstall, errorFound;
			isInstall = isList = isUninstall = errorFound = false;
			
			while (token.hasMoreTokens()) {
				String action = token.nextToken().trim();
				if (action.equals(org.osgi.service.deploymentadmin.DeploymentAdminPermission.INSTALL)) {
					isInstall=true;
				} else if (action.equals(org.osgi.service.deploymentadmin.DeploymentAdminPermission.LIST)) {
					isList=true;
				} else if (action.equals(org.osgi.service.deploymentadmin.DeploymentAdminPermission.UNINSTALL)) {
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
	 * This method asserts that DeploymentAdminPermission cannot receive a null deployment package name.
	 * 
	 * @spec DeploymentAdminPermission.DeploymentAdminPermission(String,String)
	 */
	private void testDeploymentAdminPermission003() {
		tbc.log("#testDeploymentAdminPermission003");
		try {
			 new org.osgi.service.deploymentadmin.DeploymentAdminPermission(
					null, org.osgi.service.deploymentadmin.DeploymentAdminPermission.INSTALL);
			
			tbc.failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
//			tbc.pass(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
//					new String[] { IllegalArgumentException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(),
							e.getClass().getName() }));
		}
	}
	
	/**
	 * This method asserts that DeploymentAdminPermission cannot receive a null action string.
	 * 
 	 * @spec DeploymentAdminPermission.DeploymentAdminPermission(String,String)
	 */
	private void testDeploymentAdminPermission004() {
		tbc.log("#testDeploymentAdminPermission004");
		try {
			new org.osgi.service.deploymentadmin.DeploymentAdminPermission(
					DeploymentConstants.DEPLOYMENT_PACKAGE_NAME0, null);
			
			tbc.failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
//			tbc.pass(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
//					new String[] { IllegalArgumentException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {
					IllegalArgumentException.class.getName(),
							e.getClass().getName() }));
		}
	}
	
	/**
	 * This method asserts that DeploymentAdminPermission throws IllegalArgumentException when 
	 * an invalid action is passed.
	 * 
	 * @spec DeploymentAdminPermission.DeploymentAdminPermission(String,String)
	 */
	private void testDeploymentAdminPermission005() {
		tbc.log("#testDeploymentAdminPermission005");
		try {
			String invalidAction = "invalid";
			new org.osgi.service.deploymentadmin.DeploymentAdminPermission(
					DeploymentConstants.DEPLOYMENT_PACKAGE_NAME0, invalidAction);
			
			tbc.failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException  e) {
//			tbc.pass(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
//					new String[] { IllegalArgumentException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException .class.getName(),
							e.getClass().getName() }));
		}
	}	
	/**
	 * This method asserts that DeploymentAdminPermission throws IllegalArgumentException when 
	 * an invalid format is used.
	 * 
	 * @spec DeploymentAdminPermission.DeploymentAdminPermission(String,String)
	 */
	private void testDeploymentAdminPermission006() {
		tbc.log("#testDeploymentAdminPermission006");
		try {
			new org.osgi.service.deploymentadmin.DeploymentAdminPermission(
					DeploymentConstants.INVALID_DEPLOYMENT_PACKAGE_NAME, org.osgi.service.deploymentadmin.DeploymentAdminPermission.INSTALL);
			
			tbc.failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException  e) {
//			tbc.pass(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
//					new String[] { IllegalArgumentException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(),
							e.getClass().getName() }));
		}
	}
	
	/**
	 * This method asserts that DeploymentAdminPermission throws IllegalArgumentException when
	 * an empty filter name is passed
	 * @spec DeploymentAdminPermission.DeploymentAdminPermission(String,String)
	 */
	private void testDeploymentAdminPermission007() {
		tbc.log("#testDeploymentAdminPermission007");
		try {
			new org.osgi.service.deploymentadmin.DeploymentAdminPermission(
					"(name=)", org.osgi.service.deploymentadmin.DeploymentAdminPermission.INSTALL);
			tbc.failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
//			tbc.pass(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
//					new String[] { IllegalArgumentException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(),
							e.getClass().getName() }));
		}
	}
    
    /**
     * This method asserts that DeploymentAdminPermission throws IllegalArgumentException
     * when invalid characteres is used in certificate.
     * 
     * @spec DeploymentAdminPermission.DeploymentAdminPermission(String,String)
     */
    private void testDeploymentAdminPermission008() {
        tbc.log("#testDeploymentAdminPermission008");
        try {
            new org.osgi.service.deploymentadmin.DeploymentAdminPermission(
                    DeploymentConstants.DEPLOYMENT_PACKAGE_NAME4, org.osgi.service.deploymentadmin.DeploymentAdminPermission.INSTALL);
            tbc.failException("#", IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
//            tbc.pass(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
//                    new String[] { IllegalArgumentException.class.getName() }));
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {
                            IllegalArgumentException.class.getName(),
                            e.getClass().getName() }));
        }
    } 
    
    /**
     * This method asserts that DeploymentAdminPermission throws IllegalArgumentException
     * when the distinguished names are invalid.
     * 
     * @spec DeploymentAdminPermission.DeploymentAdminPermission(String,String)
     */
    private void testDeploymentAdminPermission009() {
        tbc.log("#testDeploymentAdminPermission009");
        try {
            new org.osgi.service.deploymentadmin.DeploymentAdminPermission(
                    DeploymentConstants.DEPLOYMENT_PACKAGE_NAME5, org.osgi.service.deploymentadmin.DeploymentAdminPermission.INSTALL);
            tbc.failException("#", IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
//            tbc.pass(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
//                    new String[] { IllegalArgumentException.class.getName() }));
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {
                            IllegalArgumentException.class.getName(),
                            e.getClass().getName() }));
        }
    }     
}
