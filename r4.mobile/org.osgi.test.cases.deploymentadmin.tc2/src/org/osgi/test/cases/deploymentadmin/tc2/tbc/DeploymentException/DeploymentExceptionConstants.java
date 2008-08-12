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
 * 12/04/2005   Alexandre Santos
 * 26           Implement MEGTCK 
 * ===========  ==============================================================
 */
package org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentException;

import org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentTestControl;
import org.osgi.service.deploymentadmin.DeploymentException;

/**
 * This Test Class Validates the DeploymentException Constants values according
 * to MEG reference documentation.
 */
public class DeploymentExceptionConstants {
	private DeploymentTestControl tbc;

	public DeploymentExceptionConstants(DeploymentTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testConstants001();
	}

	/**
	 * This test case validates the DeploymentException codes (constants
	 * specified in DeploymentException) of the checked exception received when
	 * something fails during any deployment processes.
	 * 
	 * @spec 114.14.5
	 */
	public void testConstants001() {
		tbc.log("#testConstants001");
		tbc.assertEquals("Asserting CODE_BAD_HEADER value", 452, DeploymentException.CODE_BAD_HEADER);
		tbc.assertEquals("Asserting CODE_BUNDLE_NAME_ERROR value", 457, DeploymentException.CODE_BUNDLE_NAME_ERROR);
		tbc.assertEquals("Asserting CODE_BUNDLE_SHARING_VIOLATION value", 460, DeploymentException.CODE_BUNDLE_SHARING_VIOLATION);
        tbc.assertEquals("Asserting CODE_CANCELLED value", 401, DeploymentException.CODE_CANCELLED);
        tbc.assertEquals("Asserting CODE_COMMIT_ERROR value", 462, DeploymentException.CODE_COMMIT_ERROR);
        tbc.assertEquals("Asserting CODE_FOREIGN_CUSTOMIZER value", 458, DeploymentException.CODE_FOREIGN_CUSTOMIZER);
		tbc.assertEquals("Asserting CODE_MISSING_BUNDLE value", 454, DeploymentException.CODE_MISSING_BUNDLE);
		tbc.assertEquals("Asserting CODE_MISSING_FIXPACK_TARGET value", 453, DeploymentException.CODE_MISSING_FIXPACK_TARGET);
		tbc.assertEquals("Asserting CODE_MISSING_HEADER value", 451, DeploymentException.CODE_MISSING_HEADER);
		tbc.assertEquals("Asserting CODE_MISSING_RESOURCE value", 455, DeploymentException.CODE_MISSING_RESOURCE);		
		tbc.assertEquals("Asserting CODE_NOT_A_JAR value", 404, DeploymentException.CODE_NOT_A_JAR);
		tbc.assertEquals("Asserting CODE_ORDER_ERROR value", 450, DeploymentException.CODE_ORDER_ERROR);
		tbc.assertEquals("Asserting CODE_OTHER_ERROR value", 463, DeploymentException.CODE_OTHER_ERROR);
        tbc.assertEquals("Asserting CODE_PROCESSOR_NOT_FOUND value", 464, DeploymentException.CODE_PROCESSOR_NOT_FOUND);
		tbc.assertEquals("Asserting CODE_RESOURCE_SHARING_VIOLATION value", 461, DeploymentException.CODE_RESOURCE_SHARING_VIOLATION);
		tbc.assertEquals("Asserting CODE_SIGNING_ERROR value", 456, DeploymentException.CODE_SIGNING_ERROR);
		tbc.assertEquals("Asserting CODE_TIMEOUT value", 465, DeploymentException.CODE_TIMEOUT);
	}

}
