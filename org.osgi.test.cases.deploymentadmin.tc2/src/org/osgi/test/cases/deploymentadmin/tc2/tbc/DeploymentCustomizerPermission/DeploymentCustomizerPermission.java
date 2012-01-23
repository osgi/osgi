/*
 * Copyright (c) OSGi Alliance (2004, 2011). All Rights Reserved.
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
 * This test class validates the implementation of
 * <code>DeploymentCustomizerPermission</code> constructor, according to MEG
 * specification.
 */
public class DeploymentCustomizerPermission extends DeploymentTestControl {
	
	/**
	 * Asserts that a <code>DeploymentCustomizerPermission</code> object for the given name and action
	 * is correctly created.
	 * 
	 * @spec DeploymentCustomizerPermission.DeploymentCustomizerPermission(String, String)
	 */
	public void testDeploymentCustomizerPermission001() {
		log("#testDeploymentCustomizerPermission001");
		try {
			org.osgi.service.deploymentadmin.spi.DeploymentCustomizerPermission dpCustPerm = new org.osgi.service.deploymentadmin.spi.DeploymentCustomizerPermission(
					DeploymentConstants.BUNDLE_NAME_FILTER,
					org.osgi.service.deploymentadmin.spi.DeploymentCustomizerPermission.PRIVATEAREA);

			assertEquals(
					"DeploymentCustomizerPermission for a given name was correctly created ",
					DeploymentConstants.BUNDLE_NAME_FILTER, dpCustPerm.getName());
			
			assertEquals(
							"DeploymentCustomizerPermission for a given action was correctly created",
							org.osgi.service.deploymentadmin.spi.DeploymentCustomizerPermission.PRIVATEAREA,
							dpCustPerm.getActions());

		} catch (Exception e) {
			fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	
	/**
	 * Asserts the filter has the same syntax as an OSGi filter but only the
	 * "name" attribute is allowed. java.lang.IllegalArgumentException must be
	 * thrown.
	 * 
	 * @spec DeploymentCustomizerPermission.DeploymentCustomizerPermission(String, String)
	 */
	public void testDeploymentCustomizerPermission002() {
		log("#testDeploymentCustomizerPermission002");
		try {
			new org.osgi.service.deploymentadmin.spi.DeploymentCustomizerPermission(
					DeploymentConstants.BUNDLE_NAME_WRONG_FILTER,
					org.osgi.service.deploymentadmin.spi.DeploymentCustomizerPermission.PRIVATEAREA);
			
			failException("#", IllegalArgumentException.class);

		} catch (IllegalArgumentException e) {
			// pass("IllegalArgumentException correctly thrown, when passing an invalid filter name.");
		}catch (Exception e) {
			fail(MessagesConstants
					.getMessage(
					MessagesConstants.EXCEPTION_THROWN,
					new String[] { "IllegalArgumentException",e.getClass().getName() }));
		}
	}
	
	/**
	 * Asserts the only allowed action is the "privatearea" action. The
	 * java.lang.IllegalArgumentException must be thrown.
	 * 
	 * @spec DeploymentCustomizerPermission.DeploymentCustomizerPermission(String, String)
	 */
	public void testDeploymentCustomizerPermission003() {
		log("#testDeploymentCustomizerPermission003");
		try {
			new org.osgi.service.deploymentadmin.spi.DeploymentCustomizerPermission(
					DeploymentConstants.BUNDLE_NAME_FILTER, "install");
			
			failException("#", IllegalArgumentException.class);

		} catch (IllegalArgumentException e) {
			// pass("IllegalArgumentException correctly thrown, when the list of actions contains unknown operations.");
		}catch (Exception e) {
			fail(MessagesConstants
					.getMessage(
					MessagesConstants.EXCEPTION_THROWN,
					new String[] { "IllegalArgumentException",e.getClass().getName() }));
		}
	}
	
	/**
	 * Asserts that Symbolic name of the target bundle must not be null. The
	 * java.lang.IllegalArgumentException must be thrown.
	 * 
	 * @spec DeploymentCustomizerPermission.DeploymentCustomizerPermission(String, String)
	 */
	public void testDeploymentCustomizerPermission004() {
		log("#testDeploymentCustomizerPermission004");
		try {
			new org.osgi.service.deploymentadmin.spi.DeploymentCustomizerPermission(
					null, org.osgi.service.deploymentadmin.spi.DeploymentCustomizerPermission.PRIVATEAREA);
			
			failException("#", IllegalArgumentException.class);

		} catch (IllegalArgumentException e) {
			// pass("IllegalArgumentException correctly thrown, when Symbolic name of the target bundle is null.");
		}catch (Exception e) {
			fail(MessagesConstants
					.getMessage(
					MessagesConstants.EXCEPTION_THROWN,
					new String[] { "IllegalArgumentException",e.getClass().getName() }));
		}
	}
	
	/**
	 * Asserts that Action string must not be null. The
	 * java.lang.IllegalArgumentException must be thrown.
	 * 
	 * @spec DeploymentCustomizerPermission.DeploymentCustomizerPermission(String, String)
	 */
	public void testDeploymentCustomizerPermission005() {
		log("#testDeploymentCustomizerPermission005");
		try {
			new org.osgi.service.deploymentadmin.spi.DeploymentCustomizerPermission(
					DeploymentConstants.BUNDLE_NAME_FILTER, null);
			
			failException("#", IllegalArgumentException.class);

		} catch (IllegalArgumentException e) {
			// pass("IllegalArgumentException correctly thrown, when the Action string is null.");
		}catch (Exception e) {
			fail(MessagesConstants
					.getMessage(
					MessagesConstants.EXCEPTION_THROWN,
					new String[] { "IllegalArgumentException",e.getClass().getName() }));
		}
	}
	/**
	 * Asserts IllegalArgumentException is thrown if the argument actions is empty 
	 * 
	 * @spec DeploymentCustomizerPermission.DeploymentCustomizerPermission(String, String)
	 */
	public void testDeploymentCustomizerPermission006() {
		log("#testDeploymentCustomizerPermission006");
		try {
			new org.osgi.service.deploymentadmin.spi.DeploymentCustomizerPermission(
					"(name=)", org.osgi.service.deploymentadmin.spi.DeploymentCustomizerPermission.PRIVATEAREA);
			
			failException("#", IllegalArgumentException.class);

		} catch (IllegalArgumentException e) {
			// pass("IllegalArgumentException correctly thrown, when the list of actions contains unknown operations.");
		}catch (Exception e) {
			fail(MessagesConstants
					.getMessage(
					MessagesConstants.EXCEPTION_THROWN,
					new String[] { "IllegalArgumentException",e.getClass().getName() }));
		}
	}
}
