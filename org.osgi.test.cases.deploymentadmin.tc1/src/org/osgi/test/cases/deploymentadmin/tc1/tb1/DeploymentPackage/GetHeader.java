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

package org.osgi.test.cases.deploymentadmin.tc1.tb1.DeploymentPackage;
import org.osgi.service.deploymentadmin.DeploymentAdminPermission;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentConstants;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.TestInterface;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.MessagesConstants;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.TestingDeploymentPackage;

/**
 * @author Luiz Felipe Guimaraes
 *
 * This Test Class Validates the implementation of <code>getHeader</code> method of DeploymentPackage, 
 * according to MEG specification.
 */

public class GetHeader implements TestInterface {
	private DeploymentTestControl tbc;
	private DeploymentPackage dp = null, fixPackDP = null;
	private TestingDeploymentPackage testDP= null, testFixPackDP = null;
	
	public GetHeader(DeploymentTestControl tbc){
		this.tbc = tbc;
	}
	
	public void run() {
		try {
			prepare();
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
			testGetHeader018();
			testGetHeader019();
            testGetHeader020();
            testGetHeader021();
            testGetHeader022();
            testGetHeader023();
            testGetHeader024();
		} finally {
			uninstallDeploymentPackage();
		}
	} 
    private void prepare() {
        try {
            tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
        } catch (Exception e) {
            tbc.fail("Failed to set Permission necessary for testing #getDeploymentPackage");
        }
    }
	/**
	 * Installs a FixPack DeploymentPackage, so DeploymentPackage-FixPack header
	 * can be gotten as well. We need to install a deployment package before
	 * else the instalation of the new deployment package would not occur
	 * 
	 */
	private void installDeploymentPackage() {
		try {
			testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
    
	/**
	 * Asserts that it returns the requested deployment package manifest header (DeploymentPackage-Version) 
	 * from the main section
	 * 
	 * @spec DeploymentPackage.getHeader(String)
	 */
	private void testGetHeader001() {
		tbc.log("#testGetHeader001");
		try {
		String header = dp.getHeader(DeploymentConstants.DP_HEADER_VERSION);
		tbc.assertEquals("Asserts that it returns the requested deployment package manifest header (DeploymentPackage-Version) from the main section",
            testDP.getVersion().toString(), header);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	/**
	 * Asserts that header names are case insensitive (DEPLOYMENTPACKAGE-VERSION)
	 * 
	 * @spec DeploymentPackage.getHeader(String)
	 */
	private void testGetHeader002() {
		tbc.log("#testGetHeader002");
		try {
		String header = dp.getHeader(DeploymentConstants.DP_HEADER_VERSION.toUpperCase());
		tbc.assertEquals("Asserts that header names are case insensitive (DEPLOYMENTPACKAGE-VERSION)",testDP.getVersionString(),header);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}	
	
	/**
	 * Asserts that it returns the requested deployment package manifest header (DeploymentPackage-Name) from the main section
	 * 
	 * @spec DeploymentPackage.getHeader(String)
	 */
	private void testGetHeader003() {
		tbc.log("#testGetHeader003");
		try {
		String header = dp.getHeader(DeploymentConstants.DP_HEADER_NAME);
		tbc.assertEquals("Asserts that it returns the requested deployment package manifest header (DeploymentPackage-Name) from the main section",
					testDP.getName(),header);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	
	/**
	 * Asserts that header names are case insensitive (DEPLOYMENTPACKAGE-NAME)
	 * 
	 * @spec DeploymentPackage.getHeader(String)
	 */
	private void testGetHeader004() {
		tbc.log("#testGetHeader004");
		try {
		String header = dp.getHeader(DeploymentConstants.DP_HEADER_NAME.toUpperCase());
		tbc.assertEquals("Asserts that header names are case insensitive (DEPLOYMENTPACKAGE-NAME)",testDP.getName(),header);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	
	/**
	 * Asserts that it returns null when the header doesn't exist.
	 * 
	 * @spec DeploymentPackage.getHeader(String) 
	 */
	private void testGetHeader005() {
		tbc.log("#testGetHeader005");
		try {
		String header = dp.getHeader(DeploymentConstants.INVALID_NAME);
		tbc.assertNull("Asserts that it returns null when the header doesn't exist.",header);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	
	/**
	 * Asserts that it returns the requested deployment package manifest header (DeploymentPackage-Copyright) 
	 * from the main section
	 * 
	 * @spec DeploymentPackage.getHeader(String)
	 */
	private void testGetHeader006() {
		tbc.log("#testGetHeader006");
		try {
		String header = dp.getHeader(DeploymentConstants.DP_HEADER_COPYRIGHT);
		tbc.assertEquals("Asserts that it returns the requested deployment package manifest header (DeploymentPackage-Copyright) from the main section",
				DeploymentConstants.DP_MY_COPYRIGHT,header);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	
	/**
	 * Asserts that header names are case insensitive (deploymentpackage-copyright)
	 * 
	 * @spec DeploymentPackage.getHeader(String)
	 */
	private void testGetHeader007() {
		try {
		tbc.log("#testGetHeader007");		
		String header = dp.getHeader(DeploymentConstants.DP_HEADER_COPYRIGHT.toLowerCase());
		tbc.assertEquals("Asserts that header names are case insensitive (deploymentpackage-copyright)",
				DeploymentConstants.DP_MY_COPYRIGHT,header);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	
	/**
	 * Asserts that it returns the requested deployment package manifest header (DeploymentPackage-ContactAdress) 
	 * from the main section
	 * 
	 * @spec DeploymentPackage.getHeader(String)
	 */
	private void testGetHeader008() {
		tbc.log("#testGetHeader008");
		try {
		String header = dp.getHeader(DeploymentConstants.DP_HEADER_CONTACT_ADRESS);
		tbc.assertEquals("Asserts that it returns the requested deployment package manifest header (DeploymentPackage-ContactAdress) from the main section",
				DeploymentConstants.DP_MY_CONTACT_ADRESS,header);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	
	/**
	 * Asserts that header names are case insensitive (deploymentpackage-contactadress)
	 * 
	 * @spec DeploymentPackage.getHeader(String)
	 */
	private void testGetHeader009() {
		tbc.log("#testGetHeader009");
		try {
		String header = dp.getHeader(DeploymentConstants.DP_HEADER_CONTACT_ADRESS.toLowerCase());
		tbc.assertEquals("Asserts that header names are case insensitive (deploymentpackage-contactadress)",
				DeploymentConstants.DP_MY_CONTACT_ADRESS,header);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}	
	
	
	/**
	 * Asserts that it returns the requested deployment package manifest header (DeploymentPackage-Description) from the main section
	 * 
	 * @spec DeploymentPackage.getHeader(String)
	 */
	private void testGetHeader010() {
		tbc.log("#testGetHeader010");
		try {
		String header = dp.getHeader(DeploymentConstants.DP_HEADER_DESCRIPTION);
		tbc.assertEquals("Asserts that it returns the requested deployment package manifest header (DeploymentPackage-Description) from the main section",
				DeploymentConstants.DP_MY_DESCRIPTION,header);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	
	/**
	 * Asserts that header names are case insensitive (deploymentpackage-description)
	 * 
	 * @spec DeploymentPackage.getHeader(String)
	 */
	private void testGetHeader011() {
		tbc.log("#testGetHeader011");
		try {
		String header = dp.getHeader(DeploymentConstants.DP_HEADER_DESCRIPTION.toLowerCase());
		tbc.assertEquals("Asserts that header names are case insensitive (deploymentpackage-description)",
				DeploymentConstants.DP_MY_DESCRIPTION,header);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}

	/**
	 * Asserts that it returns the requested deployment package manifest header (DeploymentPackage-DocURL) from the main section
	 * 
	 * @spec DeploymentPackage.getHeader(String)
	 */
	private void testGetHeader012() {
		tbc.log("#testGetHeader012");
		try {
		String header = dp.getHeader(DeploymentConstants.DP_HEADER_DOC_URL);
		tbc.assertEquals("Asserts that it returns the requested deployment package manifest header (DeploymentPackage-DocURL) from the main section",
				DeploymentConstants.DP_MY_DOC_URL,header);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	
	/**
	 * Asserts that header names are case insensitive (DEPLOYMENTPACKAGE-DOCURL)
	 * 
	 * @spec DeploymentPackage.getHeader(String)
	 */
	private void testGetHeader013() {
		tbc.log("#testGetHeader013");
		try {
		String header = dp.getHeader(DeploymentConstants.DP_HEADER_DOC_URL.toLowerCase());
		tbc.assertEquals("Asserts that header names are case insensitive (DEPLOYMENTPACKAGE-DOCURL)",
				DeploymentConstants.DP_MY_DOC_URL,header);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}

	/**
	 * Asserts that it returns the requested deployment package manifest header (DeploymentPackage-Vendor) from the main section
	 * 
	 * @spec DeploymentPackage.getHeader(String)
	 */
	private void testGetHeader014() {
		tbc.log("#testGetHeader014");
		try {
		String header = dp.getHeader(DeploymentConstants.DP_HEADER_VENDOR);
		tbc.assertEquals("Asserts that it returns the requested deployment package manifest header (DeploymentPackage-Vendor) from the main section",
				DeploymentConstants.DP_MY_VENDOR,header);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	
	/**
	 * Asserts that header names are case insensitive (deploymentpackage-vendor)
	 * 
	 * @spec DeploymentPackage.getHeader(String)
	 */
	private void testGetHeader015() {
		tbc.log("#testGetHeader015");
		try {
		String header = dp.getHeader(DeploymentConstants.DP_HEADER_VENDOR.toLowerCase());
		tbc.assertEquals("Asserts that header names are case insensitive (deploymentpackage-vendor)",
				DeploymentConstants.DP_MY_VENDOR,header);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	
	/**
	 * Asserts that it returns the requested deployment package manifest header 
	 * (DeploymentPackage-License) from the main section.
	 * 
	 * @spec DeploymentPackage.getHeader(String)
	 */
	private void testGetHeader016() {
		tbc.log("#testGetHeader016");
		try {
		String header = dp.getHeader(DeploymentConstants.DP_HEADER_LICENSE);
		tbc.assertEquals("Asserts that it returns the requested deployment package manifest header (DeploymentPackage-License) from the main section",
				DeploymentConstants.DP_MY_LICENSE,header);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
	
	/**
	 * Asserts that header names are case insensitive (deploymentPackage-license)
	 * 
	 * @spec DeploymentPackage.getHeader(String)
	 */
	private void testGetHeader017() {
		tbc.log("#testGetHeader017");
		try {
		String header = dp.getHeader(DeploymentConstants.DP_HEADER_LICENSE.toLowerCase());
		tbc.assertEquals("Asserts that header names are case insensitive (deploymentpackage-license)",
				DeploymentConstants.DP_MY_LICENSE,header);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
    
    /**
     * Asserts that it returns the requested deployment package manifest header (DeploymentPackage-FixPack) from the main section
     * 
     * @spec DeploymentPackage.getHeader(String)
     */
    private void testGetHeader018() {
        tbc.log("#testGetHeader018");
        try {
        testFixPackDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_FIX_PACK_DP);
        fixPackDP = tbc.installDeploymentPackage(tbc.getWebServer() + testFixPackDP.getFilename());
        String header = fixPackDP.getHeader(DeploymentConstants.DP_HEADER_FIXPACK);
        tbc.assertEquals("Asserts that it returns the requested deployment package manifest header (DeploymentPackage-FixPack) from the main section",
                DeploymentConstants.DP_MY_FIXPACK, header);
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
        }
    }
    
    /**
     * Asserts that header names are case insensitive (deploymentpackage-fixpack)
     * 
     * @spec DeploymentPackage.getHeader(String)
     */
    private void testGetHeader019() {
        tbc.log("#testGetHeader019");
        try {
        String header = fixPackDP.getHeader(DeploymentConstants.DP_HEADER_FIXPACK.toLowerCase());
        tbc.assertEquals("Asserts that header names are case insensitive (deploymentpackage-fixpack)",
                DeploymentConstants.DP_MY_FIXPACK,header);
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
        }
    }
    
    /**
     * Asserts that it returns null for the requested deployment package
     * manifest header (DeploymentPackage-License) from the main section
     * 
     * @spec DeploymentPackage.getHeader(String)
     */    
    private void testGetHeader020() {
        tbc.log("#testGetHeader020");
        try {
        String header = fixPackDP.getHeader(DeploymentConstants.DP_HEADER_LICENSE);
        tbc.assertNull("Asserts that it returns null as License.", header);
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
        }    
    }
    
    /**
     * Asserts that it returns null the requested deployment package manifest
     * header (DeploymentPackage-Vendor) from the main section
     * 
     * @spec DeploymentPackage.getHeader(String)
     */    
    private void testGetHeader021() {
        tbc.log("#testGetHeader021");
        try {
        String header = fixPackDP.getHeader(DeploymentConstants.DP_HEADER_VENDOR);
        tbc.assertNull("Asserts that it returns the requested deployment package manifest header (DeploymentPackage-Vendor) from the main section", header);
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
        }   
    }  
    
    /**
     * Asserts that it returns null the requested deployment package manifest header 
     * (DeploymentPackage-ContactAddress) from the main section. It also tests if 
     * only DeploymentAdminPermission with "metadata" is needed.
     * 
     * @spec DeploymentPackage.getHeader(String)
     */    
    private void testGetHeader022() {
        tbc.log("#testGetHeader022");
        try {
        
	        tbc.setDeploymentAdminPermission(DeploymentConstants.getDPNameFilter(fixPackDP.getName()), DeploymentAdminPermission.METADATA);
	        String header = fixPackDP.getHeader(DeploymentConstants.DP_HEADER_CONTACT_ADRESS);
	        tbc.assertNull("Asserts that it returns the requested deployment package manifest header (DeploymentPackage-ContactAddress) from the main section", header);
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
        } finally {
        	prepare();
        }
    }
    
    /**
     * Asserts that all human readable headers can be localized using the same
     * mechanism as that is used to localize the manifest of a bundle.
     * 
     * @spec 114.3.5 Localization
     */    
    private void testGetHeader023() {
        tbc.log("#testGetHeader023");
        DeploymentPackage testDp = null;
        TestingDeploymentPackage testingDp= null;        
        try {
            testingDp = tbc.getTestingDeploymentPackage(DeploymentConstants.LOCALIZED_DP);
            testDp = tbc.installDeploymentPackage(tbc.getWebServer() + testingDp.getFilename());

            String header = testDp.getHeader(DeploymentConstants.DP_HEADER_COPYRIGHT);
            tbc.assertEquals("Asserting the DeploymentPackage-Copyright value.", DeploymentConstants.DP_MY_COPYRIGHT, header);
            header = testDp.getHeader(DeploymentConstants.DP_HEADER_VENDOR);
            tbc.assertEquals("Asserting the DeploymentPackage-Vendor value.", DeploymentConstants.DP_MY_VENDOR, header);
            header = testDp.getHeader(DeploymentConstants.DP_HEADER_LICENSE);
            tbc.assertEquals("DeploymentPackage-License value.", DeploymentConstants.DP_MY_LICENSE, header);
            header = testDp.getHeader(DeploymentConstants.DP_HEADER_DESCRIPTION);
            tbc.assertEquals("Asserting the DeploymentPackage-Description value.", DeploymentConstants.DP_MY_DESCRIPTION, header);
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
        } finally {
            tbc.uninstall(testDp);
        }
    }    
    
    /**
	 * Asserts that SecurityException is thrown if the caller doesn't have 
	 * DeploymentAdminPermission with "metadata" action 
     * 
     * @spec DeploymentPackage.getHeader(String)
     */    
    private void testGetHeader024() {
        tbc.log("#testGetHeader024");
        try {
        	tbc.setMininumPermission();
        	fixPackDP.getHeader(DeploymentConstants.DP_HEADER_CONTACT_ADRESS);
        	tbc.failException("", SecurityException.class);
        } catch (SecurityException e) {
            tbc.pass(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_CORRECTLY_THROWN, new String[] { "SecurityException" }));			
		} catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {"SecurityException", e.getClass().getName() }));
		} finally {
			prepare();
		}
    }
		
	
	/**
	 * Uninstalls the deployment packages previously installed.
	 */	
	private void uninstallDeploymentPackage() {
		tbc.uninstall(new DeploymentPackage[] { dp, fixPackDP });
	}
}
