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
 * Mar 11, 2005  Luiz Felipe Guimaraes
 * 26            Implement MEGTCK for the deployment RFC-88
 * ============  ==============================================================
 * Jul 15, 2005  Andre Assad
 * 145           Implement spec review issues
 * ============  ==============================================================
 * Aug 30, 2005  Andre Assad
 * 179           Implement Review Issues
 * ============  ==============================================================
 */

package org.osgi.test.cases.deploymentadmin.tc1.tb1.DeploymentAdmin;

import org.osgi.service.deploymentadmin.DeploymentAdminPermission;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentConstants;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.TestInterface;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.MessagesConstants;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.TestingDeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.TestingGetServiceRegistrationResourceProcessor;

/**
 * @author Luiz Felipe Guimaraes
 * 
 * This Test Class Validates the implementation of
 * <code>listDeploymentPackage<code> method, according to MEG reference
 * documentation.
 */
public class ListDeploymentPackage implements TestInterface {

	private DeploymentTestControl tbc;

	public ListDeploymentPackage(DeploymentTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
        prepare();
		testListDeploymentPackage001();
		testListDeploymentPackage002();
        testListDeploymentPackage003();
        testListDeploymentPackage004();
        testListDeploymentPackage005();
        testListDeploymentPackage006();
	}
    
    /**
     * Sets permission needed and wait for PermissionWorker
     */
    private void prepare() {
        try {
            tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
        } catch (Exception e) {
        	e.printStackTrace();
            tbc.fail("Failed to set Permission necessary for testing listDeploymentPackage");
        }
    }

	/**
     * Installs a deployment package and assert that an array of
     * DeploymentPackage objects representing all the installed deployment
     * packages is returned. After that, uninstall the deployment package and
     * asserts that DeploymentPackages are not in the list.
     * 
     * @spec DeploymentAdmin.listDeploymentPackage()
     */			
	private void testListDeploymentPackage001() {
		tbc.log("#testListDeploymentPackage001");
		DeploymentPackage dp = null;
		int initialNumberOfPackages;
		int finalNumberOfPackages;
		boolean found;
		try {
			//Installs a deployment package
			initialNumberOfPackages = tbc.getDeploymentAdmin().listDeploymentPackages().length;
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			
			DeploymentPackage dps[] = tbc.getDeploymentAdmin().listDeploymentPackages();
			finalNumberOfPackages = dps.length;
			tbc.assertTrue("Asserts that after installing a deployment package there are more deployment packages installed than before",(finalNumberOfPackages==(initialNumberOfPackages+1)));
			
            found = false;
			for(int i=0; i<dps.length && !found ;i++) {
				if (dps[i].equals(dp)) {
					found = true;
				}
			}
			tbc.assertTrue("Asserts that a deployment package installed is really returned by the listDeploymentPackages",found);

			//Uninstalls a deployment package
			tbc.uninstall(dp);
			
			dps = tbc.getDeploymentAdmin().listDeploymentPackages();
			finalNumberOfPackages = dps.length;
			tbc.assertTrue("Asserts that after uninstalling a deployment package there are the same number of DP again",finalNumberOfPackages==initialNumberOfPackages);
			
			found = false;
			for(int i=0; i<dps.length && !found; i++) {
				if (dps[i].getName().equals(testDP.getName())) {
					found = true;
				}
			}
			tbc.assertTrue("Asserts that a deployment package uninstalled is not returned by the listDeploymentPackages",!found);
		} catch (Exception e) {
        	e.printStackTrace();
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp);
		}
	}
	
	/**
	 * Asserts if the installation fails, the source must never become
	 * visible, also not transiently.
	 * 
	 * @spec DeploymentAdmin.listDeploymentPackage()
	 */			
	private void testListDeploymentPackage002() {
		tbc.log("#testListDeploymentPackage002");

		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.WRONG_VERSION_DP);
		DeploymentPackage dp = null;
		try {
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			tbc.failException("#", Exception.class);
		} catch (Exception e) {
            DeploymentPackage found = findDP(testDP.getName());
			tbc.assertNull("The installed deployment package is not visible", found);
		} finally {
			tbc.uninstall(dp);
		}
	}
    
    /**
     * Asserts that only the latest version of the DP is returned when an update
     * operation is executed.
     * 
     * @spec DeploymentAdmin.listDeploymentPackage()
     */         
    private void testListDeploymentPackage003() {
        tbc.log("#testListDeploymentPackage003");
        DeploymentPackage dp = null, updateDP = null;
        try {
            TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
            dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
            
            TestingDeploymentPackage testUpdateDP = tbc.getTestingDeploymentPackage(DeploymentConstants.ADD_BUNDLE_FIX_PACK_DP);
            updateDP = tbc.installDeploymentPackage(tbc.getWebServer() + testUpdateDP.getFilename());
            
            DeploymentPackage found = findDP(testDP.getName());
            tbc.assertEquals(
                "During an update, the target deployment package remained the same",
                updateDP.getName().trim() + "_" + updateDP.getVersion().toString().trim(),
                found.getName().trim() + "_" + found.getVersion().toString().trim());
        } catch (Exception e) {
        	e.printStackTrace();
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
        } finally {
            tbc.uninstall(new DeploymentPackage[]{dp, updateDP});
        }
    }

      
   /**
    * Asserts that after an installation of an existing package (update) is
    * completed, the source is in the list of listDeploymentPackage method.
    * 
    * @spec DeploymentAdmin.listDeploymentPackage()
    */     
   private void testListDeploymentPackage004() {
       tbc.log("#testListDeploymentPackage004");
       DeploymentPackage dp = null;
       try {
           TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
           tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
           
           TestingDeploymentPackage updateDP = tbc.getTestingDeploymentPackage(DeploymentConstants.ADD_BUNDLE_FIX_PACK_DP);
           dp = tbc.installDeploymentPackage(tbc.getWebServer() + updateDP.getFilename());
           
           DeploymentPackage found = findDP(testDP.getName());
           
           tbc.assertEquals(
                   "During an update, the target deployment package remained the same",
                   dp.getName().trim() + "_" + dp.getVersion().toString().trim(), 
                   found.getName().trim() + "_" + found.getVersion().toString().trim());
           
       } catch (Exception e) {
    	   e.printStackTrace();
           tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
       } finally {
           tbc.uninstall(dp);
       }
   }
   
   /**
     * Asserts in case of missing permissions it <b>MAY</b> give back an empty array.
     * 
     * @spec DeploymentAdmin.listDeploymentPackage()
     */     
   private void testListDeploymentPackage005() {
       tbc.log("#testListDeploymentPackage005");
       tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentAdminPermission.INSTALL);
       try {
           DeploymentPackage[] dp = tbc.getDeploymentAdmin().listDeploymentPackages();
           if (dp.length==0) {
//               tbc.pass("The returned array is empty");
           } else {
               tbc.log("The returned array is NOT empty");
           }
       } catch (Exception e) {
    	   e.printStackTrace();
           tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
       }
   }
   
   /**
    * Asserts that during an installation of an existing package (update),
    * the target must remain in this list until the installation process is
    * completed, after which the source replaces the target.
    * 
    * @spec DeploymentAdmin.listDeploymentPackage()
    */ 
 private synchronized void testListDeploymentPackage006() {
     tbc.log("#testListDeploymentPackage006");
     tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);

     DeploymentPackage initialDP = null, fixDP = null, rp =null;
     TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
 	 TestingDeploymentPackage testFixDP = tbc.getTestingDeploymentPackage(DeploymentConstants.ADD_RESOURCE_FIX_PACK);
     TestingDeploymentPackage testRP = tbc.getTestingDeploymentPackage(DeploymentConstants.RESOURCE_PROCESSOR_DP);
     InstallPackageThread installThread = null;
     
     try {
			rp = tbc.installDeploymentPackage(tbc.getWebServer()+testRP.getFilename());
			
			TestingGetServiceRegistrationResourceProcessor test = (TestingGetServiceRegistrationResourceProcessor) tbc.getServiceInstance(DeploymentConstants.PID_RESOURCE_PROCESSOR1);
			
			tbc.assertNotNull("Resource Processor was registered", test);
			
			test.setRelease(false);
			
			initialDP = tbc.installDeploymentPackage(tbc.getWebServer()+testDP.getFilename());
			

			installThread = new InstallPackageThread(tbc,testFixDP);

            installThread.start();

            //Just to guarantee that this Thread is executed
            while (!installThread.isRunning()) {
            	this.wait(500);
            }
            
            tbc.assertTrue("Asserts that the installation was not completed", !installThread.isInstalled());
            DeploymentPackage middleDP = findDP(testDP.getName());
			
			tbc.assertTrue("Asserts that during an installation of an existing package (update)," +
					" the target must remain in this list until the installation process is " +
					"completed",middleDP.getName().equals(testDP.getName()) && 
					middleDP.getVersion().equals(testDP.getVersion()));
			
            test.setRelease(true);
			
            //Just to guarantee that the installation terminated
            while (!installThread.isInstalled()) {
            	this.wait(500);
            }
            
            fixDP = installThread.getDeploymentPackage();
            
			DeploymentPackage finalDP = findDP(testDP.getName());
			
			tbc.assertTrue("Asserts that after the installation process is " +
					"completed, the source replaces the target.",
					finalDP.getName().equals(testFixDP.getName()) && 
					finalDP.getVersion().equals(testFixDP.getVersion()));

     } catch (Exception e) {
		e.printStackTrace();
        tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
     } finally {
    	 installThread.uninstallDP(false);
         tbc.uninstall(new DeploymentPackage[] { rp, initialDP, fixDP });
     }
 }
    
    private DeploymentPackage findDP(String name) {
        DeploymentPackage dp = null;
        DeploymentPackage[] list = tbc.getDeploymentAdmin().listDeploymentPackages();
        for (int i=0; (dp==null) && i<list.length; i++) {
            if (list[i].getName().equals(name)) {
                dp = list[i];
            }
        }
        return dp;
    }
    /*
    class ListDeploymentPackageWorker extends Thread {
    		private TestingDeploymentPackage testDP;
    		private boolean installed=false;
    		private boolean uninstalled=false;
    		
    		private boolean uninstall=false;
    		private boolean running=false;
    		
    		DeploymentPackage dp = null;
    		
    		
            protected ListDeploymentPackageWorker(TestingDeploymentPackage testDP) {
                this.testDP = testDP;
            }
            
            public void uninstallDP() {
            	if (isAlive()) {
            		uninstall=true;
    	        	try {
    					this.join(DeploymentConstants.SHORT_TIMEOUT);
    				} catch (InterruptedException e) {
    					e.printStackTrace();
    				}
            	}
            	
            }
            
            public boolean isInstalled() {
            	return installed;
            }
            
            public boolean isRunning() {
            	return running;
            }
            public boolean isUninstalled() {
            	return uninstalled;
            }
            
            public DeploymentPackage getDeploymentPackage() {
            	return dp;
            }
    		public synchronized void run() {
    			try {
    				try {
    					running=true;
    					dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
    					installed=(dp==null?false:true);
    				} catch (Exception e) {
    					e.printStackTrace();
    				}
    			} finally {
    				while (!uninstall) {
    					try {
    						this.wait(100);
    					} catch (InterruptedException e) {
    						e.printStackTrace();
    					}
    				}
    				if (isInstalled()) {
    					try {
    						dp.uninstall();
    						uninstalled=true;
    					} catch (Exception e) {
    						e.printStackTrace();
    					}
    				}
    			}
    			
    		}
    	}*/
}
