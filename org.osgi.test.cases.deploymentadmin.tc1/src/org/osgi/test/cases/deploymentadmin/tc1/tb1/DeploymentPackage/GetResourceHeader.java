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
 * 14/04/2005    Luiz Felipe Guimaraes
 * 26            Implement MEG TCK for the deployment RFC-88
 * ============  ==============================================================
 * 26 May, 2005  Andre Assad
 * 26            Implement MEG TCK for the deployment RFC-88
 * ============  ==============================================================
 */

package org.osgi.test.cases.deploymentadmin.tc1.tb1.DeploymentPackage;
import org.osgi.service.deploymentadmin.DeploymentAdminPermission;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentConstants;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.TestInterface;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.MessagesConstants;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.TestingBundle;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.TestingDeploymentPackage;

/**
 * @author Luiz Felipe Guimaraes
 *
 * This Test Class Validates the implementation of <code>getResourceHeader</code> method, 
 * according to MEG reference documentation (rfc0088).
 */

public class GetResourceHeader implements TestInterface {
	private DeploymentTestControl tbc;
	private DeploymentPackage dp = null;
	private TestingDeploymentPackage testDP= null;
	private TestingBundle testBundle= null;
	
	public GetResourceHeader(DeploymentTestControl tbc){
		this.tbc = tbc;
	}
	
	public void run() {
		try {
			prepare();
			installDeploymentPackage();
			testGetResourceHeader001();
			testGetResourceHeader002();
			testGetResourceHeader003();
			testGetResourceHeader004();
			testGetResourceHeader005();
			testGetResourceHeader006();
		} finally {
			uninstallDeploymentPackage();
		}
	}
    private void prepare() {
        try {
            tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
        } catch (Exception e) {
            tbc.fail("Failed to set Permission necessary for testing #getDeploymentPackage",e);
        }
    }
	/**
	 * Installs a DeploymentPackage to be used by the methods below
	 * 
	 */
	private void installDeploymentPackage() {
		try {		
			testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
			testBundle = testDP.getBundles()[0];
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());		
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }),e);
		}
	}
	/**
	 * Asserts that it returns the requested resource header
	 * 
	 * @spec DeploymentPackage.getResourceHeader(String)
	 */
	private void testGetResourceHeader001() {
		tbc.log("#testGetResourceHeader001");
		try {
		String resourceHeader = dp.getResourceHeader(testBundle.getFilename(),DeploymentConstants.BUNDLE_HEADER_SYMB_NAME);
		tbc.assertEquals("Asserts that it returns the value of the requested resource header (Bundle-SymbolicName)", 
				testBundle.getName(), resourceHeader);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }),e);
		}
	}
	/**
	 * Asserts that resource header names are case insensitive
	 * 
	 * @spec DeploymentPackage.getResourceHeader(String)
	 */
	private void testGetResourceHeader002() {
		tbc.log("#testGetResourceHeader002");
		try {
		String resourceHeader = dp.getResourceHeader(testBundle.getFilename(),DeploymentConstants.BUNDLE_HEADER_SYMB_NAME.toUpperCase());
		tbc.assertEquals("Asserts that resource header names are case insensitive (BUNDLE-SYMBOLICNAME)",
				testBundle.getName(),resourceHeader);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }),e);
		}
	}
	/**
	 * Asserts that it returns the requested resource header
	 * 
	 * @spec DeploymentPackage.getResourceHeader(String) 
	 */
	private void testGetResourceHeader003() {
		tbc.log("#testGetResourceHeader003");
		try {
		String resourceHeader = dp.getResourceHeader(testBundle.getFilename(),DeploymentConstants.BUNDLE_HEADER_VERSION);
		tbc.assertEquals("Asserts that it returns the value of the requested resource header (Bundle-Version)", 
				testBundle.getVersionString(), resourceHeader);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }),e);
		}
	}
	/**
	 * Asserts that resource header names are case insensitive
	 * 
	 * @spec DeploymentPackage.getResourceHeader(String)
	 */
	private void testGetResourceHeader004() {
		tbc.log("#testGetResourceHeader004");
		try {
		String resourceHeader = dp.getResourceHeader(testBundle.getFilename(),DeploymentConstants.BUNDLE_HEADER_VERSION.toUpperCase());
		tbc.assertEquals("Asserts that resource header names are case insensitive (BUNDLE-VERSION)",
				testBundle.getVersionString(),resourceHeader);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }),e);
		}
	}	
	/**
	 * Asserts that it returns null if the requested resource header doesn't exist.
	 * It also tests if only DeploymentAdminPermission with "metadata" is needed.
	 * 
	 * @spec DeploymentPackage.getResourceHeader(String)
	 */
	private void testGetResourceHeader005() {
		tbc.log("#testGetResourceHeader005");
		try {
			tbc.setDeploymentAdminPermission(DeploymentConstants.getDPNameFilter(testDP.getName()), DeploymentAdminPermission.METADATA);	
			String resourceHeader = dp.getResourceHeader(testBundle.getFilename(),DeploymentConstants.INVALID_NAME);
			tbc.assertNull("Asserts that it returns null if the requested resource header doesn't exist.", resourceHeader);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }),e);
		}finally {
			prepare();
		}
	}
	/**
	 * Asserts that SecurityException is thrown if the caller doesn't have 
	 * DeploymentAdminPermission with "metadata" action 
	 * 
	 * @spec DeploymentPackage.getResourceHeader(String)
	 */

	private void testGetResourceHeader006() {
		tbc.log("#testGetResourceHeader006");
		try {
			tbc.setMininumPermission();	
			dp.getResourceHeader(testBundle.getFilename(),DeploymentConstants.INVALID_NAME);
			tbc.failException("", SecurityException.class);
        } catch (SecurityException e) {
//            tbc.pass(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_CORRECTLY_THROWN, new String[] { "SecurityException" }));			
		} catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {"SecurityException", e.getClass().getName() }),e);
		} finally {
			prepare();
		}
	}
	/**
	 * Uninstalls the deployment packages previously installed.
	 */
	private void uninstallDeploymentPackage() {
		tbc.uninstall(dp);
	
	}
}
