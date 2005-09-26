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
 * Jul 15, 2005  Andre Assad
 * 145           Implement spec review issues
 * ============  ==============================================================
 */

package org.osgi.test.cases.deploymentadmin.tc1.tb1.DeploymentAdmin;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentConstants;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.TestInterface;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.Event.BundleListenerImpl;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.Event.DeploymentEventHandlerImpl;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.MessagesConstants;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.TestingBundle;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.TestingDeploymentPackage;

/**
 * @author Andre Assad
 * 
 * This Test Class Validates the implementation of
 * <code>installDeploymentPackage<code> method, according to MEG reference
 * documentation.
 */
public class InstallDeploymentPackageUseCases implements TestInterface {

	private DeploymentTestControl tbc;

	public InstallDeploymentPackageUseCases(DeploymentTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testInstallDeploymentPackageUseCases001();
		testInstallDeploymentPackageUseCases002();
		testInstallDeploymentPackageUseCases003();
		testInstallDeploymentPackageUseCases004();
		testInstallDeploymentPackageUseCases005();
		testInstallDeploymentPackageUseCases006();
		testInstallDeploymentPackageUseCases007();
		testInstallDeploymentPackageUseCases008();
		testInstallDeploymentPackageUseCases009();
		testInstallDeploymentPackageUseCases010();
		testInstallDeploymentPackageUseCases011();
		testInstallDeploymentPackageUseCases012();
		testInstallDeploymentPackageUseCases013();
        testInstallDeploymentPackageUseCases014();
        testInstallDeploymentPackageUseCases015();
        testInstallDeploymentPackageUseCases016();
	}

	/**
	 * Asserts that an event is sent when a installation is started.
	 * 
	 * @spec 114.8 Installing a Deployment Package
	 */		
	private void testInstallDeploymentPackageUseCases001() {
		tbc.log("#testInstallDeploymentPackageUseCases001");
		tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
		DeploymentPackage dp = null;
		DeploymentEventHandlerImpl deploymentEventHandler = tbc.getDeploymentEventHandler();
        deploymentEventHandler.setVerifying(true);

		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
			
			synchronized (tbc) {
				dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
				tbc.wait(DeploymentConstants.TIMEOUT);
			}
			String prop = (String) deploymentEventHandler.getProperty("deploymentpackage.name");
			tbc.assertTrue("An install event occured", deploymentEventHandler.isInstall());
			tbc.assertEquals("The installed deployment package is " + testDP.getName(), testDP.getName(), prop);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp);
			deploymentEventHandler.reset();
		}
	}
	
	/**
	 * Asserts that an event is sent when a uninstallation is started.
	 * 
	 * @spec 114.9 Uninstalling a Deployment Package
	 */			
	private void testInstallDeploymentPackageUseCases002() {
		tbc.log("#testInstallDeploymentPackageUseCases002");
		tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
		DeploymentPackage dp = null;
		DeploymentEventHandlerImpl deploymentEventHandler = tbc.getDeploymentEventHandler();
		deploymentEventHandler.setHandlingUninstall(true);
        deploymentEventHandler.setVerifying(true);

		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());

			synchronized (tbc) {
				tbc.uninstall(dp);
				// waiting for uninstall event
				tbc.wait(DeploymentConstants.TIMEOUT);
			}

			String prop = (String) deploymentEventHandler.getProperty("deploymentpackage.name");
			tbc.assertTrue("An uninstall event occured", deploymentEventHandler.isUninstall());
			tbc.assertEquals("The installed deployment package is " + testDP.getName(), testDP.getName(), prop);
			
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			deploymentEventHandler.reset();
		}
	}
	
	/**
	 * Asserts that an event is sent when a installation is completed.
	 * 
	 * @spec 114.11 Events
	 */				
	private void testInstallDeploymentPackageUseCases003() {
		tbc.log("#testInstallDeploymentPackageUseCases003");
		tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
		DeploymentPackage dp = null;
		DeploymentEventHandlerImpl deploymentEventHandler = tbc.getDeploymentEventHandler();
		deploymentEventHandler.setHandlingComplete(true);
        deploymentEventHandler.setVerifying(true);

		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
			
			synchronized (tbc) {
				dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
				tbc.wait(DeploymentConstants.TIMEOUT);
			}
			
			String dpNameProp = (String) deploymentEventHandler.getProperty("deploymentpackage.name");
			Boolean successProp = (Boolean) deploymentEventHandler.getProperty("successful");
			tbc.assertTrue("A complete event occured", deploymentEventHandler.isComplete());
			tbc.assertEquals("The installed deployment package is " + testDP.getName(), testDP.getName(), dpNameProp);
			tbc.assertTrue("The installation of deployment package was successfull ", successProp.booleanValue());
			
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp);
			deploymentEventHandler.reset();
		}
	}
	
	/**
	 * Asserts that an event is sent when an uninstallation is completed.
	 * 
	 * @spec 114.11 Events
	 */				
	private void testInstallDeploymentPackageUseCases004() {
		tbc.log("#testInstallDeploymentPackageUseCases004");
		tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
		DeploymentPackage dp = null;
		DeploymentEventHandlerImpl deploymentEventHandler = tbc.getDeploymentEventHandler();
		deploymentEventHandler.setHandlingComplete(true);
		deploymentEventHandler.setHandlingUninstall(true);
        deploymentEventHandler.setVerifying(true);

		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			
			synchronized (tbc) {
				tbc.uninstall(dp);
				tbc.wait(DeploymentConstants.TIMEOUT);
			}
			
			String dpNameProp = (String) deploymentEventHandler.getProperty("deploymentpackage.name");
			Boolean successProp = (Boolean) deploymentEventHandler.getProperty("successful");
			tbc.assertTrue("A complete event occured", deploymentEventHandler.isComplete());
			tbc.assertEquals("The installed deployment package is " + testDP.getName(), testDP.getName(), dpNameProp);
			tbc.assertTrue("The installation of deployment package was successfull ", successProp.booleanValue());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			deploymentEventHandler.reset();
		}
	}	

	/**
	 *  This test case asserts that Deployment Admin service
	 *  must set the bundle location to the following URL:
	 *  location ::= 'osgi-dp:' bsn
	 * 
	 * @spec 114.2.1 Resources
	 */		
	private void testInstallDeploymentPackageUseCases005() {
		tbc.log("#testInstallDeploymentPackageUseCases005");
		tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
		DeploymentPackage dp = null;
		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			
			String bsn = testDP.getBundles()[0].getName();
			Bundle b = dp.getBundle(bsn);
			String location = b.getLocation();
			tbc.assertTrue("Deployment Admin correctly set osgi-dp in location:", location.startsWith(DeploymentConstants.OSGI_DP_LOCATION));
			tbc.assertEquals("Deployment Admin correctly set bundle symbolic name in location:", bsn, location.substring(location.indexOf(':')+1));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp);
		}
	}
	
	/**
     * This test case asserts that the extension of a Deployment Package JAR
     * file name SHOULD be .dp
     * 
     * @spec 114.3 File Format
     */		
	private void testInstallDeploymentPackageUseCases006() {
		tbc.log("#testInstallDeploymentPackageUseCases006");
		tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
		DeploymentPackage dp = null;
		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.WRONG_FORMAT_DP);
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
            tbc.log("DeploymentAdmin accepted a deployment package without .dp format");
		} catch (DeploymentException e) {
			tbc.pass("Correctly failed to install a deployment package with wrong extension");
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp);
		}
	}
	
	/**
	 * Asserts that resources must come after bundles in a
	 * deployment package jar file.
	 * 
	 * @spec 114.3 File Format
	 */		
	private void testInstallDeploymentPackageUseCases007() {
		tbc.log("#testInstallDeploymentPackageUseCases007");
		tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
		DeploymentPackage dp = null;
		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.WRONG_ORDER_DP);
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			tbc.failException("#", DeploymentException.class);
		} catch (DeploymentException e) {
			tbc.pass("Correctly failed to install a deployment package with wrong extension");
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp);
		}
	}
	
	/**
	 * Tests that the Deployment Admin service must reject a Deployment
	 * Package that has an invalid signature.
	 * 
	 * @spec 114.3.1 Signing
	 */		
	private void testInstallDeploymentPackageUseCases008() {
		tbc.log("#testInstallDeploymentPackageUseCases008");
		tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
		DeploymentPackage dp = null;
		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.UNTRUSTED_DP);
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			tbc.failException("#", DeploymentException.class);
		} catch (DeploymentException e) {
			tbc.assertEquals("Correctly failed to install untrusted deployment package", DeploymentException.CODE_SIGNING_ERROR, e.getCode());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp);
		}
	}
	
	/**
	 * Tests that a path name in a deployment package file must
	 * not contain any character except: [A-Za-z0-9_.-]
	 * 
	 * @spec 114.3.2 Path Names
	 */		
	private void testInstallDeploymentPackageUseCases009() {
		tbc.log("#testInstallDeploymentPackageUseCases009");
		tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
		DeploymentPackage dp = null;
		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.WRONG_PATH_DP);
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			tbc.failException("#", DeploymentException.class);
		} catch (DeploymentException e) {
			tbc.pass("Correctly failed to install a deployment package with a wrong path name to a bundle in the jar file.");
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp);
		}
	}
	
	/**
	 * Asserts that when installing a deployment package all target bundles must
	 * be stopped in reverse target resource order and then installed according
	 * to the semantics of the OSGi Framework installBundle method.
	 * 
	 * @spec 114.8 Installing a Deployment Package
	 */
	private void testInstallDeploymentPackageUseCases010() {
		tbc.log("#testInstallDeploymentPackageUseCases010");
		tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);

		DeploymentPackage dp = null;
		Bundle currentBundle = null;
		int currentEventType = 0;
		//there are 2 bundles, count till 0
		int count = 1;
		
		BundleListenerImpl listener = tbc.getBundleListener();
		// makes sure no old parameters will influence this test case 
		listener.reset();
		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
			TestingBundle[] tb = testDP.getBundles();

			synchronized (tbc) {
				// asserts bundle stop
				dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
				do {
					tbc.wait(DeploymentConstants.TIMEOUT);

					currentBundle = listener.getCurrentBundle();
					currentEventType = listener.getCurrentType();

					if (currentEventType == BundleEvent.STOPPED) {
						tbc.assertEquals("The bundles were stopped in reverse target resource order",
										tb[count--].getName(), currentBundle.getSymbolicName());
					}
				} while ((listener.getCurrentType() == BundleEvent.STOPPED)
						&& currentBundle.getSymbolicName().equals(tb[0].getName()));
			}
			tbc.assertTrue("The two testing bundles in the deployment package were stopped", (count==-1));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp);
		}
	}
	
	/**
	 * Tests that Exceptions thrown during stopping of a bundle must be ignored
	 * 
	 * @spec 114.8 Installing a Deployment Package
	 */		
	private void testInstallDeploymentPackageUseCases011() {
		tbc.log("#testInstallDeploymentPackageUseCases011");
		tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
		DeploymentPackage dp = null;
		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.BUNDLE_THROWS_EXCEPTION_STOP_DP);
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			dp.uninstall();
			tbc.pass("Exception thrown during stopping of a bundle was correctly ignored");
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		}
	}
    
    /**
     * Asserts that DeploymentException is thrown when the Bundle Version is not
     * the same as defined by the deployment package manifest.
     * 
     * @spec DeploymentException.DeploymentException()
     */     
    private void testInstallDeploymentPackageUseCases012() {
        tbc.log("#testInstallDeploymentPackageUseCases012");
        tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
        DeploymentPackage dp = null;
        try {
            TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.VERSION_DIFFERENT_FROM_MANIFEST_DP);
            dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
            tbc.failException("#", DeploymentException.class);
        } catch (DeploymentException e) {
            //TODO what is the error code for this case?
            tbc.pass("DeploymentException correctly thrown");
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
        } finally {
            tbc.uninstall(dp);
        }
    }
    
    /**
     * Asserts that an event is sent when a installation is completed, and was not successful.
     * 
     * @spec 114.11 Events
     */             
    private void testInstallDeploymentPackageUseCases013() {
        tbc.log("#testInstallDeploymentPackageUseCases013");
        tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
        DeploymentPackage dp = null;
        DeploymentEventHandlerImpl deploymentEventHandler = tbc.getDeploymentEventHandler();
        deploymentEventHandler.setHandlingComplete(true);
        deploymentEventHandler.setVerifying(true);

        TestingDeploymentPackage testDP = null;
        try {
            testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.MISSING_NAME_HEADER_DP);
            
            synchronized (tbc) {
                dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
                tbc.wait(DeploymentConstants.TIMEOUT);
            }
            tbc.failException("#", DeploymentException.class);
        } catch (DeploymentException e) {
            String dpNameProp = (String) deploymentEventHandler.getProperty("deploymentpackage.name");
            Boolean successProp = (Boolean) deploymentEventHandler.getProperty("successful");
            tbc.assertTrue("A complete event occured", deploymentEventHandler.isComplete());
            tbc.assertEquals("The installed deployment package is " + testDP.getName(), testDP.getName(), dpNameProp);
            tbc.assertTrue("The installation of deployment package was NOT successfull ", !successProp.booleanValue());
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
        } finally {
            tbc.uninstall(dp);
            deploymentEventHandler.reset();
        }
    }
    
    /**
     * Asserts that an event is sent when an uninstallation is completed, and was NOT successful.
     * 
     * @spec 114.11 Events
     */             
    private void testInstallDeploymentPackageUseCases014() {
        tbc.log("#testInstallDeploymentPackageUseCases014");
        tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
        DeploymentPackage dp = null;
        DeploymentEventHandlerImpl deploymentEventHandler = tbc.getDeploymentEventHandler();
        deploymentEventHandler.setHandlingComplete(true);
        deploymentEventHandler.setHandlingUninstall(true);
        deploymentEventHandler.setVerifying(true);

        TestingDeploymentPackage testDP = null;
        try {
            testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_RESOURCE_PROCESSOR_DP);
            dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
            
            // uninstalls RP bundle so uninstallation fails
            Bundle b = tbc.getBundle(testDP.getBundles()[0].getName());
            b.uninstall();

            synchronized (tbc) {
                tbc.uninstall(dp);
                tbc.wait(DeploymentConstants.TIMEOUT);
            }
            tbc.failException("#", DeploymentException.class);
        } catch (DeploymentException e) {
            String dpNameProp = (String) deploymentEventHandler.getProperty("deploymentpackage.name");
            Boolean successProp = (Boolean) deploymentEventHandler.getProperty("successful");
            tbc.assertTrue("A complete event occured", deploymentEventHandler.isComplete());
            tbc.assertEquals("The installed deployment package is " + testDP.getName(), testDP.getName(), dpNameProp);
            tbc.assertTrue("The uninstallation of deployment package was NOT successfull ", !successProp.booleanValue());
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
        } finally {
            deploymentEventHandler.reset();
        }
    }
    
    /**
     * Asserts that a path name must not contain any character except:
     * [A-Za-z0-9_.-]. This test case installs a DP with a path that contains
     * valid characters that are not letters.
     * 
     * @spec 114.3.2 Path Names
     */
    private void testInstallDeploymentPackageUseCases015() {
        tbc.log("#testInstallDeploymentPackageUseCases015");
        tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
        DeploymentPackage dp = null;
        TestingDeploymentPackage testDP = null;
        try {
            testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.STRANGE_PATH_DP);
            dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
            tbc.pass("No exception is thrown during the installation of a DP with strange path to bundles");
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
        } finally {
            tbc.uninstall(dp);
        }
    }
    
    /**
     * Asserts if the Deployment Package is signed, subsequent files in the JAR
     * <b>must be the signature files</b> as defined in the manifest specification.
     * 
     * @spec 114.3 File Format
     */
    private void testInstallDeploymentPackageUseCases016() {
        tbc.log("#testInstallDeploymentPackageUseCases016");
        DeploymentPackage dp = null;
        try {
            TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIGNING_FILE_NOT_NEXT);
            dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
            tbc.failException("#", DeploymentException.class);
        } catch (DeploymentException e) {
            // TODO what is the error code for this case?
            tbc.pass("DeploymentException correctly thrown");
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            tbc.uninstall(dp);
        }
    }
}
