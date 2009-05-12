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
 * Ago 03, 2005  Andre Assad
 * 166           Implement test cases for DeploymentCustomizerPermission
 * ============  ==============================================================
 */

package org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentCustomizerPermission;

import org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentConstants;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.util.MessagesConstants;


/**
 * @author Andre Assad
 * 
 * This Test Case Validates the implementation of
 * <code>DeploymentCustomizerPermission.equals<code> method, 
 * according to MEG specification.
 */
public class Equals {

	private DeploymentTestControl tbc;

	public Equals(DeploymentTestControl tbc) {
		this.tbc = tbc;
	}
	
	public void run() {
		testEquals001();
		testEquals002();
	}
	
	/**
	 * Checks two <code>DeploymentCustomizerPermission</code> objects are
	 * equal when their target filters are equal, and their actions are the
	 * same.
	 * 
	 * @spec DeploymentCustomizerPermission.equals(Object)
	 */
	public void testEquals001() {
		tbc.log("#testEquals001");
		try {
			org.osgi.service.deploymentadmin.spi.DeploymentCustomizerPermission deployPermission = new org.osgi.service.deploymentadmin.spi.DeploymentCustomizerPermission(
					DeploymentConstants.BUNDLE_NAME_FILTER,
					org.osgi.service.deploymentadmin.spi.DeploymentCustomizerPermission.PRIVATEAREA);
			
			org.osgi.service.deploymentadmin.spi.DeploymentCustomizerPermission deployPermission2 = new org.osgi.service.deploymentadmin.spi.DeploymentCustomizerPermission(
					DeploymentConstants.BUNDLE_NAME_FILTER,
					org.osgi.service.deploymentadmin.spi.DeploymentCustomizerPermission.PRIVATEAREA);

			tbc.assertTrue(
							"Both DeploymentCustomizerPermission objects are equal when both target and action are equals",
							deployPermission.equals(deployPermission2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		}
	}
	
	/**
	 * Checks two <code>DeploymentCustomizerPermission</code> objects are
	 * different when their target filters are different.
	 * 
	 * @spec DeploymentCustomizerPermission.equals(Object)
	 */
	public void testEquals002() {
		tbc.log("#testEquals002");
		try {
			org.osgi.service.deploymentadmin.spi.DeploymentCustomizerPermission deployPermission = new org.osgi.service.deploymentadmin.spi.DeploymentCustomizerPermission(
					"(name=bundle.tb1)",
					org.osgi.service.deploymentadmin.spi.DeploymentCustomizerPermission.PRIVATEAREA);
			
			org.osgi.service.deploymentadmin.spi.DeploymentCustomizerPermission deployPermission2 = new org.osgi.service.deploymentadmin.spi.DeploymentCustomizerPermission(
					"(name=bundle.tb2)",
					org.osgi.service.deploymentadmin.spi.DeploymentCustomizerPermission.PRIVATEAREA);

			tbc.assertTrue(
							"Both DeploymentCustomizerPermission objects are different when their targets are different",
							!deployPermission.equals(deployPermission2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		}
	}
}
