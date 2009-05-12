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
 * Apr 12, 2005  Alexandre Alves
 * 26            Implement MEG TCK
 * ============  ==============================================================
 * May 26, 2005  Andre Assad
 * 26            Implement MEG TCK
 * ============  ==============================================================
 */

package org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentAdminPermission;

import org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentConstants;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.util.MessagesConstants;
import org.osgi.service.deploymentadmin.DeploymentAdminPermission;

/**
 * @author Alexandre Alves
 * 
 * This Test Case Validates the implementation of
 * <code>implies<code> method of DeploymentAdminPermission, 
 * according to MEG specification.
 *
 */
public class Implies {
	private DeploymentTestControl tbc;

	public Implies(DeploymentTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testImplies001();
		testImplies002();
		testImplies003();
		testImplies004();
		testImplies005();
        testImplies006();
        testImplies007();
        testImplies008();
        testImplies009();
	}

	/**
	 * This method asserts that an object implies another object with the same
	 * actions.
	 * 
	 * @spec DeploymentAdminPermission.implies(Permission)
	 */
	public void testImplies001() {
		tbc.log("#testImplies001");
		try {
			DeploymentAdminPermission deployPermission = new DeploymentAdminPermission(
					DeploymentConstants.DEPLOYMENT_PACKAGE_NAME0,
					org.osgi.service.deploymentadmin.DeploymentAdminPermission.INSTALL);
			
            DeploymentAdminPermission deployPermission2 = new DeploymentAdminPermission(
					DeploymentConstants.DEPLOYMENT_PACKAGE_NAME0,
					org.osgi.service.deploymentadmin.DeploymentAdminPermission.INSTALL);
			
            tbc.assertTrue("Implies when both target and action are equal",
					deployPermission.implies(deployPermission2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		}
	}

	/**
	 * This method asserts that an object implies an object with has the actions
	 * that its contains.
	 * 
	 * @spec DeploymentAdminPermission.implies(Permission)
	 */
	public void testImplies002() {
		tbc.log("#testImplies002");
		try {
			DeploymentAdminPermission deployPermission = new DeploymentAdminPermission(
					DeploymentConstants.DEPLOYMENT_PACKAGE_NAME0,
					org.osgi.service.deploymentadmin.DeploymentAdminPermission.INSTALL
							+ ","
							+ org.osgi.service.deploymentadmin.DeploymentAdminPermission.CANCEL);
			DeploymentAdminPermission deployPermission2 = new DeploymentAdminPermission(
					DeploymentConstants.DEPLOYMENT_PACKAGE_NAME0,
					org.osgi.service.deploymentadmin.DeploymentAdminPermission.INSTALL);
			
			tbc.assertTrue(
							"Asserts implies when an object has the actions that the other object contains.",
							deployPermission.implies(deployPermission2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		}
	}

	/**
	 * This method asserts that an object does not implies an object with hasn't
	 * the actions that its contains.
	 * 
	 * @spec DeploymentAdminPermission.implies(Permission)
	 */
	public void testImplies003() {
		tbc.log("#testImplies003");
		try {
			DeploymentAdminPermission deployPermission = new DeploymentAdminPermission(
					DeploymentConstants.DEPLOYMENT_PACKAGE_NAME0,
					org.osgi.service.deploymentadmin.DeploymentAdminPermission.INSTALL);
			
            DeploymentAdminPermission deployPermission2 = new DeploymentAdminPermission(
					DeploymentConstants.DEPLOYMENT_PACKAGE_NAME0,
					org.osgi.service.deploymentadmin.DeploymentAdminPermission.INSTALL
							+ ","
							+ org.osgi.service.deploymentadmin.DeploymentAdminPermission.CANCEL);
			
            tbc.assertTrue(
							"Asserts implies when an object does not contains the actions that the other object contains.",
							!deployPermission.implies(deployPermission2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		}
	}

	/**
	 * This method asserts that an object does not implies other object with
	 * different signer.
	 * 
	 * @spec DeploymentAdminPermission.implies(Permission)
	 */
	public void testImplies004() {
		tbc.log("#testImplies004");
		try {
			DeploymentAdminPermission deployPermission = new DeploymentAdminPermission(
					DeploymentConstants.DEPLOYMENT_PACKAGE_NAME0,
					org.osgi.service.deploymentadmin.DeploymentAdminPermission.INSTALL);
			
            DeploymentAdminPermission deployPermission2 = new DeploymentAdminPermission(
					DeploymentConstants.DEPLOYMENT_PACKAGE_NAME2,
					org.osgi.service.deploymentadmin.DeploymentAdminPermission.INSTALL);
			
            tbc.assertTrue(
							"Asserts implies when an object hasn't the same signer of the other.",
							!deployPermission.implies(deployPermission2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		}
	}

	/**
	 * This method asserts that an object does not implies other object with
	 * different name.
	 * 
	 * @spec DeploymentAdminPermission.implies(Permission)
	 */
	public void testImplies005() {
		tbc.log("#testImplies005");
		try {
			DeploymentAdminPermission deployPermission = new DeploymentAdminPermission(
					DeploymentConstants.DEPLOYMENT_PACKAGE_NAME0,
					org.osgi.service.deploymentadmin.DeploymentAdminPermission.INSTALL);
			
            DeploymentAdminPermission deployPermission2 = new DeploymentAdminPermission(
					DeploymentConstants.DEPLOYMENT_PACKAGE_NAME2,
					org.osgi.service.deploymentadmin.DeploymentAdminPermission.INSTALL);
			
            tbc.assertTrue(
							"Asserts implies when an object hasn't the same name of the other.",
							!deployPermission.implies(deployPermission2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		}
	}
    
    /**
     * This method asserts that an object implies other object that
     * use a wildcard as filter name.
     * 
     * @spec DeploymentAdminPermission.implies(Permission)
     */
    public void testImplies006() {
        tbc.log("#testImplies006");
        try {
            DeploymentAdminPermission deployPermission2 = new DeploymentAdminPermission(
                DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL,
                org.osgi.service.deploymentadmin.DeploymentAdminPermission.INSTALL);

            DeploymentAdminPermission deployPermission = new DeploymentAdminPermission(
                    DeploymentConstants.DEPLOYMENT_PACKAGE_NAME2,
                    org.osgi.service.deploymentadmin.DeploymentAdminPermission.INSTALL);
            
            tbc.assertTrue(
                    "Asserts that an object implies other object that use wildcard as filter name.",
                    deployPermission.implies(deployPermission2));
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                    MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
                            .getClass().getName() }));
        }
    }    

    /**
     * This method asserts that an object does not implies other object that
     * use a more specific wildcard as filter name.
     * 
     * @spec DeploymentAdminPermission.implies(Permission)
     */
    public void testImplies007() {
        tbc.log("#testImplies007");
        try {
            DeploymentAdminPermission deployPermission = new DeploymentAdminPermission(
                "(&(name=a*))", org.osgi.service.deploymentadmin.DeploymentAdminPermission.INSTALL);
            
            DeploymentAdminPermission deployPermission2 = new DeploymentAdminPermission(
                DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL,
                org.osgi.service.deploymentadmin.DeploymentAdminPermission.INSTALL);
            
            tbc.assertTrue(
                    "Asserts that an object does not implies other object that use a more specific wildcard as filter name.",
                    !deployPermission.implies(deployPermission2));
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                    MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
                            .getClass().getName() }));
        }
    }    
    
    /**
     * This method asserts an object that has wildcard in certificate 
     * accept any certificate
     * 
     * @spec DeploymentAdminPermission.implies(Permission)
     */
    public void testImplies008() {
        tbc.log("#testImplies008");
        try {
            DeploymentAdminPermission deployPermission = new DeploymentAdminPermission(
                DeploymentConstants.DEPLOYMENT_PACKAGE_NAME3,
                    org.osgi.service.deploymentadmin.DeploymentAdminPermission.INSTALL);
            
            DeploymentAdminPermission deployPermission2 = new DeploymentAdminPermission(
                DeploymentConstants.DEPLOYMENT_PACKAGE_NAME2,
                    org.osgi.service.deploymentadmin.DeploymentAdminPermission.INSTALL);
            
            tbc.assertTrue(
                            "Asserts that an object that has wildcard in certificate accept any certificate.",
                            deployPermission2.implies(deployPermission));
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                    MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
                            .getClass().getName() }));
        }
    }   
    
    /**
     * This method asserts an object that has a specific certificate does
     * not hold a DeploymentAdminPermission that has a wildcard 
     * to accept any certificate.
     * 
     * @spec DeploymentAdminPermission.implies(Permission)
     */
    public void testImplies009() {
        tbc.log("#testImplies009");
        try {
            
            DeploymentAdminPermission deployPermission = new DeploymentAdminPermission(
                DeploymentConstants.DEPLOYMENT_PACKAGE_NAME3,
                    org.osgi.service.deploymentadmin.DeploymentAdminPermission.INSTALL);

            DeploymentAdminPermission deployPermission2 = new DeploymentAdminPermission(
                    DeploymentConstants.DEPLOYMENT_PACKAGE_NAME2,
                        org.osgi.service.deploymentadmin.DeploymentAdminPermission.INSTALL);

            tbc.assertTrue(
                            "Asserts that an object that has a specific certificate does not hold a DeploymentAdminPermission that has a wildcard to accept any certificate.",
                            !deployPermission.implies(deployPermission2));
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                    MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
                            .getClass().getName() }));
        }
    }    
    
}
