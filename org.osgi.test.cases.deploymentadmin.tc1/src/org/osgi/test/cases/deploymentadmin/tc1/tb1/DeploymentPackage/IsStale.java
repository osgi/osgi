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
 * ============  ====================================================================
 * Jun 06, 2005  Andre Assad
 * 111           Implement/Change test cases according to Spec updates of 6th of June
 * ============  ====================================================================
 */

package org.osgi.test.cases.deploymentadmin.tc1.tb1.DeploymentPackage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentConstants;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.TestInterface;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.MessagesConstants;

/**
 * @author Andre Assad
 *
 * This Test Class Validates the implementation of <code>isStale<code> method of DeploymentPackage, 
 * according to MEG specification.
 */

public class IsStale implements TestInterface {

	private DeploymentTestControl tbc;

	public IsStale(DeploymentTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		prepare();
		testIsStale001();
		testIsStale002();
		testIsStale003();
	}
    private void prepare() {
        try {
            tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
        } catch (Exception e) {
            tbc.fail("Failed to set Permission necessary for testing #getDeploymentPackage",e);
        }
    }
	/**
	 * This test case asserts that a deployment package is not stale after installation.
	 * 
	 * @spec DeploymentPackage.isStale()
	 */
	private void testIsStale001() {
		tbc.log("#testIsStale001");
		DeploymentPackage dp = null;
		try {
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP).getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp);
			tbc.assertTrue("Deployment Package is not stale", !dp.isStale());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }),e);
		} finally {
			tbc.uninstall(dp);
		}
	}
	
	/**
	 * This test case asserts that a deployment package is stale after uninstallation forced.
	 * It also tests if no DeploymentAdminPermission is needed.
	 * 
	 * @spec DeploymentPackage.isStale()
	 */
	private void testIsStale002() {
		tbc.log("#testIsStale002");
		DeploymentPackage dp = null;
		try {
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP).getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp);
			dp.uninstallForced();
			
			tbc.setMininumPermission();
			tbc.assertTrue("Deployment Package is stale", dp.isStale());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }),e);
		} finally {
			prepare();
		}
	}
	
	/**
	 * This test case asserts 
	 * 1) A deployment package is stale after uninstallation.
	 * 2) getBundle(String), getResourceProcessor(String), uninstall() and uninstallForced()
	 *    throws IllegalStateException if called. 
	 * 3) All other methods do not throw this Exception if called.
	 * 
	 * @spec DeploymentPackage.isStale()
	 */
	private void testIsStale003() {
		tbc.log("#testIsStale003");
		DeploymentPackage dp = null;
		try {
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP).getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[]{"deployment package"}), dp);
			dp.uninstall();
			
			tbc.assertTrue("Deployment Package is stale", dp.isStale());
			ArrayList methodsThrowsException = new ArrayList();
			methodsThrowsException.add("getBundle");
			methodsThrowsException.add("getResourceProcessor");
			methodsThrowsException.add("uninstall");
			methodsThrowsException.add("uninstallForced");
			
			Method[] methods = DeploymentPackage.class.getMethods();
			for (int i=0;i<methods.length;i++) {
				if (methodsThrowsException.contains(methods[i].getName())) {
					tbc.assertTrue("Asserts that IllegalStateException is thrown if the Deployment Package is stale and " + methods[i].getName() +" is called", assertIllegalStateException(dp,methods[i]));
				} else {
					tbc.assertTrue("Asserts that no Exception is thrown if the Deployment Package is stale and " + methods[i].getName() +" is called", !assertIllegalStateException(dp,methods[i]));
				}
			}
			
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }),e);
		}
	}
	
	
	private boolean assertIllegalStateException(DeploymentPackage dp, Method method) {
		boolean threw=false;
		try {
			Object[] objs = getValuesToParameters(method);
			method.invoke(dp,objs);
		} catch (InvocationTargetException e) {
			Throwable exceptionReturned = e.getTargetException();
			if (exceptionReturned instanceof IllegalStateException) {
				threw=true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return threw;
	}
	
	private Object[] getValuesToParameters(Method method) {
		Class[] parameters = method.getParameterTypes();
		int numberOfParameters= parameters.length;
		Object[] objs = new Object[numberOfParameters];
		
		for (int i=0;i<numberOfParameters;i++) {
			objs[i]= "";
		}
		
		return objs;
	}
}
