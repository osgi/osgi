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
 * Apr 12, 2005  Alexandre Santos
 * 26            Implement MEGTCK 
 * ============  ==============================================================
 * May 26, 2005  Alexandre Santos
 * 26            Implement MEGTCK 
 * ============  ==============================================================
 */
package org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentException;

import org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentConstants;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.util.MessagesConstants;

/**
 * This Test Class Validates the implementation of
 * <code>DeploymentException<code>, and the get methods: <code>getCode, getMessage, getCause<code>. 
 * According to MEG reference documentation.
 */
public class DeploymentException {
	private DeploymentTestControl tbc;

	public DeploymentException(DeploymentTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testDeploymentException001();
		testDeploymentException002();
		testDeploymentException003();
	}
	
	/**
	 * This test case asserts that when using DeploymentException( int code )
	 * constructor, the code is correctly set, and cause exception and message
	 * are implicitly set to null.
	 * 
	 * @spec DeploymentException.DeploymentException(int)
	 */
	public void testDeploymentException001() {
		tbc.log("#testDeploymentException001");
		try {
			org.osgi.service.deploymentadmin.DeploymentException e = new org.osgi.service.deploymentadmin.DeploymentException(
					org.osgi.service.deploymentadmin.DeploymentException.CODE_BAD_HEADER);
			
			tbc.assertEquals("The code of the exception was correctly set",
							org.osgi.service.deploymentadmin.DeploymentException.CODE_BAD_HEADER,e.getCode());
			tbc.assertNull("The cause of the exception is correctly set to null", e.getCause());
			tbc.assertNull("The message of the exception is correctly set to null", e.getMessage());
			
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}	
	
	/**
	 * This test case asserts that when using DeploymentException( int code,
	 * String message ) constructor, the code and message are correctly
	 * set, and cause exception is implicitly set to null.
	 * 
	 * @spec DeploymentException.DeploymentException(int, String)
	 */
	public void testDeploymentException002() {
		tbc.log("#testDeploymentException002");		
		try {
			org.osgi.service.deploymentadmin.DeploymentException e = new org.osgi.service.deploymentadmin.DeploymentException(org.osgi.service.deploymentadmin.DeploymentException.CODE_BAD_HEADER, DeploymentConstants.EXCEPTION_MESSAGE);
			tbc.assertEquals("The code of the exception was correctly set", org.osgi.service.deploymentadmin.DeploymentException.CODE_BAD_HEADER, e.getCode());
			tbc.assertEquals("The message of the exception was correctly set", DeploymentConstants.EXCEPTION_MESSAGE, e.getMessage());
			tbc.assertNull("The cause of the exception was correctly set to null", e.getCause());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}	
	
	/**
	 * This test case asserts that when using DeploymentException( int code,
	 * String message ) constructor, the code, message, and cause are correctly
	 * set.
	 * 
	 * @spec DeploymentException.DeploymentException(int, String, Throwable)
	 */
	public void testDeploymentException003() {
		tbc.log("#testDeploymentException003");
		try {
			Exception ex = new Exception("");
			org.osgi.service.deploymentadmin.DeploymentException e = new org.osgi.service.deploymentadmin.DeploymentException(org.osgi.service.deploymentadmin.DeploymentException.CODE_BAD_HEADER, DeploymentConstants.EXCEPTION_MESSAGE, ex);
			tbc.assertEquals("The code of the exception was correctly set", org.osgi.service.deploymentadmin.DeploymentException.CODE_BAD_HEADER, e.getCode());
			tbc.assertEquals("The message of the exception was correctly set", DeploymentConstants.EXCEPTION_MESSAGE, e.getMessage());
			tbc.assertEquals("The cause of the exception was correctly set", ex, e.getCause());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}	
}
