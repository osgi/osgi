/*
 * Copyright (c) OSGi Alliance (2004, 2017). All Rights Reserved.
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

import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentTestControl;

/**
 * This Test Class Validates the DeploymentException Constants values according
 * to MEG reference documentation.
 */
public class DeploymentExceptionConstants extends DeploymentTestControl {
	/**
	 * This test case validates the DeploymentException codes (constants
	 * specified in DeploymentException) of the checked exception received when
	 * something fails during any deployment processes.
	 * 
	 * @spec 114.14.5
	 */
	public void testConstants001() {
		assertConstant(Integer.valueOf(452), "CODE_BAD_HEADER",
				DeploymentException.class);
		assertConstant(Integer.valueOf(457), "CODE_BUNDLE_NAME_ERROR",
				DeploymentException.class);
		assertConstant(Integer.valueOf(460), "CODE_BUNDLE_SHARING_VIOLATION",
				DeploymentException.class);
		assertConstant(Integer.valueOf(401), "CODE_CANCELLED",
				DeploymentException.class);
		assertConstant(Integer.valueOf(462), "CODE_COMMIT_ERROR",
				DeploymentException.class);
		assertConstant(Integer.valueOf(458), "CODE_FOREIGN_CUSTOMIZER",
				DeploymentException.class);
		assertConstant(Integer.valueOf(454), "CODE_MISSING_BUNDLE",
				DeploymentException.class);
		assertConstant(Integer.valueOf(453), "CODE_MISSING_FIXPACK_TARGET",
				DeploymentException.class);
		assertConstant(Integer.valueOf(451), "CODE_MISSING_HEADER",
				DeploymentException.class);
		assertConstant(Integer.valueOf(455), "CODE_MISSING_RESOURCE",
				DeploymentException.class);
		assertConstant(Integer.valueOf(404), "CODE_NOT_A_JAR",
				DeploymentException.class);
		assertConstant(Integer.valueOf(450), "CODE_ORDER_ERROR",
				DeploymentException.class);
		assertConstant(Integer.valueOf(463), "CODE_OTHER_ERROR",
				DeploymentException.class);
		assertConstant(Integer.valueOf(464), "CODE_PROCESSOR_NOT_FOUND",
				DeploymentException.class);
		assertConstant(Integer.valueOf(461), "CODE_RESOURCE_SHARING_VIOLATION",
				DeploymentException.class);
		assertConstant(Integer.valueOf(456), "CODE_SIGNING_ERROR",
				DeploymentException.class);
		assertConstant(Integer.valueOf(465), "CODE_TIMEOUT",
				DeploymentException.class);
	}

}
