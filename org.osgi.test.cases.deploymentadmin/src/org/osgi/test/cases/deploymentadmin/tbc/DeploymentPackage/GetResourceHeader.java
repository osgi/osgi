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

package org.osgi.test.cases.deploymentadmin.tbc.DeploymentPackage;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tbc.TestingBundle;
import org.osgi.test.cases.deploymentadmin.tbc.TestingDeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tbc.util.MessagesConstants;

/**
 * @author Luiz Felipe Guimaraes
 *
 * @methodUnderTest org.osgi.service.deploymentadmin.DeploymentPackage#getResourceHeader
 * @generalDescription This Test Class Validates the implementation of
 *                     <code>getResourceHeader</code> method, according to MEG reference
 *                     documentation (rfc0088).
 */

public class GetResourceHeader {
	private DeploymentTestControl tbc;
	private DeploymentPackage dp = null;
	private TestingDeploymentPackage testDP= null;
	private TestingBundle testBundle= null;
	
	public GetResourceHeader(DeploymentTestControl tbc){
		this.tbc = tbc;
	}
	
	public void run() {
		try {
			installDeploymentPackage();
			testGetResourceHeader001();
			testGetResourceHeader002();
			testGetResourceHeader003();
			testGetResourceHeader004();
			testGetResourceHeader005();
		} finally {
			uninstallDeploymentPackage();
		}
	}
	
	private void installDeploymentPackage() {
		try {		
			testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_DP);
			testBundle = testDP.getBundles()[0];
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());		
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	/**
	 * @testID testGetResourceHeader001
	 * @testDescription Asserts that it returns the requested resource header
	 */
	private void testGetResourceHeader001() {
		tbc.log("#testGetResourceHeader001");
		try {
		String resourceHeader = dp.getResourceHeader(testBundle.getFilename(),DeploymentTestControl.BUNDLE_HEADER_SYMB_NAME);
		tbc.assertEquals("Asserts that it returns the value of the requested resource header (Bundle-SymbolicName)", 
				testBundle.getName(), resourceHeader);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	/**
	 * @testID testGetResourceHeader002
	 * @testDescription Asserts that resource header names are case insensitive
	 */
	private void testGetResourceHeader002() {
		tbc.log("#testGetResourceHeader002");
		try {
		String resourceHeader = dp.getResourceHeader(testBundle.getFilename(),DeploymentTestControl.BUNDLE_HEADER_SYMB_NAME.toUpperCase());
		tbc.assertEquals("Asserts that resource header names are case insensitive (BUNDLE-SYMBOLICNAME)",
				testBundle.getName(),resourceHeader);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	/**
	 * @testID testGetResourceHeader003
	 * @testDescription Asserts that it returns the requested resource header
	 */
	private void testGetResourceHeader003() {
		tbc.log("#testGetResourceHeader003");
		try {
		String resourceHeader = dp.getResourceHeader(testBundle.getFilename(),DeploymentTestControl.BUNDLE_HEADER_VERSION);
		tbc.assertEquals("Asserts that it returns the value of the requested resource header (Bundle-Version)", 
				testBundle.getVersionString(), resourceHeader);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	/**
	 * @testID testGetResourceHeader004
	 * @testDescription Asserts that resource header names are case insensitive
	 */
	private void testGetResourceHeader004() {
		tbc.log("#testGetResourceHeader004");
		try {
		String resourceHeader = dp.getResourceHeader(testBundle.getFilename(),DeploymentTestControl.BUNDLE_HEADER_VERSION.toUpperCase());
		tbc.assertEquals("Asserts that resource header names are case insensitive (BUNDLE-VERSION)",
				testBundle.getVersionString(),resourceHeader);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}	
	/**
	 * @testID testGetResourceHeader005
	 * @testDescription Asserts that it returns null if the requested resource header doesn't exist.
	 */
	private void testGetResourceHeader005() {
		tbc.log("#testGetResourceHeader005");
		try {
		String resourceHeader = dp.getResourceHeader(testBundle.getFilename(),DeploymentTestControl.INVALID_NAME);
		tbc.assertNull("Asserts that it returns null if the requested resource header doesn't exist.", resourceHeader);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	
	private void uninstallDeploymentPackage() {
		tbc.uninstall(dp);
	
	}
}
