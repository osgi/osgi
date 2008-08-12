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
 */

package org.osgi.test.cases.deploymentadmin.tbc.DeploymentPackage;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tbc.TestingDeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tbc.util.MessagesConstants;

/**
 * @author Luiz Felipe Guimaraes
 *
 * @methodUnderTest org.osgi.service.deploymentadmin.DeploymentPackage#getHeader
 * @generalDescription This Test Class Validates the implementation of
 *                     <code>getHeader</code> method, according to MEG reference
 *                     documentation (rfc0088).
 */

public class GetHeader {
	private DeploymentTestControl tbc;
	private DeploymentPackage dp = null, fixPackDP = null;
	private TestingDeploymentPackage testDP= null, testFixPackDP = null;
	
	public GetHeader(DeploymentTestControl tbc){
		this.tbc = tbc;
	}
	
	public void run() {
		try {
			installDeploymentPackage();
			testGetHeader001();
			testGetHeader002();
			testGetHeader003();
			testGetHeader004();
			testGetHeader005();
			testGetHeader006();
			testGetHeader007();
			testGetHeader008();
			testGetHeader009();
			testGetHeader010();
			testGetHeader011();
			testGetHeader012();
			testGetHeader013();
			testGetHeader014();
			testGetHeader015();
			testGetHeader016();
			testGetHeader017();
		} finally {
			uninstallDeploymentPackage();
		}
	} 
	/**
	 * Installs a FixPack DeploymentPackage, so DeploymentPackage-FixPack header
	 * can be gotten as well. We need to install a deployment package before
	 * else the instalation of the new deployment package would not occur
	 */
	private void installDeploymentPackage() {
		try {		
			testDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_DP);
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			testFixPackDP = tbc.getTestingDeploymentPackage(DeploymentTestControl.SIMPLE_FIX_PACK_DP);
			fixPackDP = tbc.installDeploymentPackage(tbc.getWebServer() + testFixPackDP.getFilename());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	/**
	 * @testID testGetHeader001
	 * @testDescription Asserts that it returns the requested deployment package manifest header (DeploymentPackage-Version)
	 * 					from the main section
	 */
	private void testGetHeader001() {
		tbc.log("#testGetHeader001");
		try {
		String header = dp.getHeader(DeploymentTestControl.DP_HEADER_VERSION);
		tbc.assertEquals("Asserts that it returns the requested deployment package manifest header (DeploymentPackage-Version) from the main section",
					testDP.getVersionString(),header);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	/**
	 * @testID testGetHeader002
	 * @testDescription Asserts that header names are case insensitive (DEPLOYMENTPACKAGE-VERSION)
	 */
	private void testGetHeader002() {
		tbc.log("#testGetHeader002");
		try {
		String header = dp.getHeader(DeploymentTestControl.DP_HEADER_VERSION.toUpperCase());
		tbc.assertEquals("Asserts that header names are case insensitive (DEPLOYMENTPACKAGE-VERSION)",testDP.getVersionString(),header);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}	
	
	/**
	 * @testID testGetHeader003
	 * @testDescription Asserts that it returns the requested deployment package manifest header (DeploymentPackage-Name)
	 * 					from the main section
	 */
	private void testGetHeader003() {
		tbc.log("#testGetHeader003");
		try {
		String header = dp.getHeader(DeploymentTestControl.DP_HEADER_NAME);
		tbc.assertEquals("Asserts that it returns the requested deployment package manifest header (DeploymentPackage-Name) from the main section",
					testDP.getName(),header);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	
	/**
	 * @testID testGetHeader004
	 * @testDescription Asserts that header names are case insensitive (DEPLOYMENTPACKAGE-NAME)
	 */
	private void testGetHeader004() {
		tbc.log("#testGetHeader004");
		try {
		String header = dp.getHeader(DeploymentTestControl.DP_HEADER_NAME.toUpperCase());
		tbc.assertEquals("Asserts that header names are case insensitive (DEPLOYMENTPACKAGE-NAME)",testDP.getName(),header);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	
	/**
	 * @testID testGetHeader005
	 * @testDescription Asserts that it returns null when the header doesn't exist.
	 */
	private void testGetHeader005() {
		tbc.log("#testGetHeader005");
		try {
		String header = dp.getHeader(DeploymentTestControl.INVALID_NAME);
		tbc.assertNull("Asserts that it returns null when the header doesn't exist.",header);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	
	/**
	 * @testID testGetHeader006
	 * @testDescription Asserts that it returns the requested deployment package manifest header (DeploymentPackage-Copyright)
	 * 					from the main section
	 */
	private void testGetHeader006() {
		tbc.log("#testGetHeader006");
		try {
		String header = dp.getHeader(DeploymentTestControl.DP_HEADER_COPYRIGHT);
		tbc.assertEquals("Asserts that it returns the requested deployment package manifest header (DeploymentPackage-Copyright) from the main section",
				DeploymentTestControl.DP_MY_COPYRIGHT,header);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	
	/**
	 * @testID testGetHeader007
	 * @testDescription Asserts that header names are case insensitive (deploymentpackage-copyright)
	 */
	private void testGetHeader007() {
		try {
		tbc.log("#testGetHeader007");		
		String header = dp.getHeader(DeploymentTestControl.DP_HEADER_COPYRIGHT.toLowerCase());
		tbc.assertEquals("Asserts that header names are case insensitive (deploymentpackage-copyright)",
				DeploymentTestControl.DP_MY_COPYRIGHT,header);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	
	/**
	 * @testID testGetHeader008
	 * @testDescription Asserts that it returns the requested deployment package manifest header (DeploymentPackage-ContactAdress) 
	 * 					from the main section
	 */
	private void testGetHeader008() {
		tbc.log("#testGetHeader008");
		try {
		String header = dp.getHeader(DeploymentTestControl.DP_HEADER_CONTACT_ADRESS);
		tbc.assertEquals("Asserts that it returns the requested deployment package manifest header (DeploymentPackage-ContactAdress) from the main section",
				DeploymentTestControl.DP_MY_CONTACT_ADRESS,header);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	
	/**
	 * @testID testGetHeader009
	 * @testDescription Asserts that header names are case insensitive (deploymentpackage-contactadress)
	 */
	private void testGetHeader009() {
		tbc.log("#testGetHeader009");
		try {
		String header = dp.getHeader(DeploymentTestControl.DP_HEADER_CONTACT_ADRESS.toLowerCase());
		tbc.assertEquals("Asserts that header names are case insensitive (deploymentpackage-contactadress)",
				DeploymentTestControl.DP_MY_CONTACT_ADRESS,header);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}	
	
	
	/**
	 * @testID testGetHeader010
	 * @testDescription Asserts that it returns the requested deployment package manifest header (DeploymentPackage-Description)
	 * 					from the main section
	 */
	private void testGetHeader010() {
		tbc.log("#testGetHeader010");
		try {
		String header = dp.getHeader(DeploymentTestControl.DP_HEADER_DESCRIPTION);
		tbc.assertEquals("Asserts that it returns the requested deployment package manifest header (DeploymentPackage-Description) from the main section",
				DeploymentTestControl.DP_MY_DESCRIPTION,header);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	
	/**
	 * @testID testGetHeader011
	 * @testDescription Asserts that header names are case insensitive (deploymentpackage-description)
	 */
	private void testGetHeader011() {
		tbc.log("#testGetHeader011");
		try {
		String header = dp.getHeader(DeploymentTestControl.DP_HEADER_DESCRIPTION.toLowerCase());
		tbc.assertEquals("Asserts that header names are case insensitive (deploymentpackage-description)",
				DeploymentTestControl.DP_MY_DESCRIPTION,header);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}

	/**
	 * @testID testGetHeader012
	 * @testDescription Asserts that it returns the requested deployment package manifest header (DeploymentPackage-DocURL)
	 * 					from the main section
	 */
	private void testGetHeader012() {
		tbc.log("#testGetHeader012");
		try {
		String header = dp.getHeader(DeploymentTestControl.DP_HEADER_DOC_URL);
		tbc.assertEquals("Asserts that it returns the requested deployment package manifest header (DeploymentPackage-DocURL) from the main section",
				DeploymentTestControl.DP_MY_DOC_URL,header);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	
	/**
	 * @testID testGetHeader013
	 * @testDescription Asserts that header names are case insensitive (DEPLOYMENTPACKAGE-DOCURL)
	 */
	private void testGetHeader013() {
		tbc.log("#testGetHeader013");
		try {
		String header = dp.getHeader(DeploymentTestControl.DP_HEADER_DOC_URL.toLowerCase());
		tbc.assertEquals("Asserts that header names are case insensitive (DEPLOYMENTPACKAGE-DOCURL)",
				DeploymentTestControl.DP_MY_DOC_URL,header);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}

	/**
	 * @testID testGetHeader014
	 * @testDescription Asserts that it returns the requested deployment package manifest header (DeploymentPackage-Vendor)
	 * 					from the main section
	 */
	private void testGetHeader014() {
		tbc.log("#testGetHeader014");
		try {
		String header = dp.getHeader(DeploymentTestControl.DP_HEADER_VENDOR);
		tbc.assertEquals("Asserts that it returns the requested deployment package manifest header (DeploymentPackage-Vendor) from the main section",
				DeploymentTestControl.DP_MY_VENDOR,header);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	
	/**
	 * @testID testGetHeader015
	 * @testDescription Asserts that header names are case insensitive (deploymentpackage-vendor)
	 */
	private void testGetHeader015() {
		tbc.log("#testGetHeader015");
		try {
		String header = dp.getHeader(DeploymentTestControl.DP_HEADER_VENDOR.toLowerCase());
		tbc.assertEquals("Asserts that header names are case insensitive (deploymentpackage-vendor)",
				DeploymentTestControl.DP_MY_VENDOR,header);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	/**
	 * @testID testGetHeader016
	 * @testDescription Asserts that it returns the requested deployment package manifest header (DeploymentPackage-FixPack)
	 * 					from the main section
	 */
	private void testGetHeader016() {
		tbc.log("#testGetHeader016");
		try {
		String header = fixPackDP.getHeader(DeploymentTestControl.DP_HEADER_FIXPACK);
		tbc.assertEquals("Asserts that it returns the requested deployment package manifest header (DeploymentPackage-FixPack) from the main section",
				testFixPackDP.getName(),header);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	
	/**
	 * @testID testGetHeader017
	 * @testDescription Asserts that header names are case insensitive (deploymentpackage-fixpack)
	 */
	private void testGetHeader017() {
		tbc.log("#testGetHeader017");
		try {
		String header = fixPackDP.getHeader(DeploymentTestControl.DP_HEADER_FIXPACK.toLowerCase());
		tbc.assertEquals("Asserts that header names are case insensitive (deploymentpackage-fixpack)",
				testFixPackDP.getName(),header);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	
	private void uninstallDeploymentPackage() {
		tbc.uninstall(dp);
		tbc.uninstall(fixPackDP);
	}
}
