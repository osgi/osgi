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
 * 10/05/2005    Andre Assad
 * 76            Implement Test Cases for Deployment Configuration
 * ============  ==============================================================
 */

package org.osgi.test.cases.deploymentadmin.tc2.tbc.Configuration;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Vector;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.ConfigurationPermission;
import org.osgi.service.condpermadmin.ConditionInfo;
import org.osgi.service.condpermadmin.ConditionalPermissionInfo;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentConstants;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.util.MessagesConstants;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.util.TestingDeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.util.TestingManagedService;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.util.TestingManagedServiceFactory;

/**
 * @author Andre Assad
 *
 * This class tests Deployment Configuration
 */

public class Configuration {
	
	private DeploymentTestControl tbc;
	private DeploymentPackage dp;
	public Configuration(DeploymentTestControl tbc) {
		this.tbc = tbc;
	}
	
	public void run() {
		testConfiguration001();
		testConfiguration002();
		testConfiguration003();
		testConfiguration004();
	}



	/**
	 * Installs the auto config deployment package
	 */
	private DeploymentPackage installAutoConfigDP() {
		tbc.log("#Installing config deployment package");
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.AUTO_CONFIG_DP);
		ConditionalPermissionInfo info = null;
		try {
			info = tbc.getCondPermAdmin().addConditionalPermissionInfo(DeploymentConstants.CONDITION_SIGNER, DeploymentConstants.ALL_PERMISSION);
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL,
					new String[] { "Deployment Package" }), dp);
			return dp;
		} catch (Exception e) {
      
      e.printStackTrace();
      if (e instanceof DeploymentException) {
        if (((DeploymentException)e).getCause() != null) {
          System.out.println("The casue is: ");
          ((DeploymentException)e).getCause().printStackTrace();
        } else {
          System.out.println("The cause is null");
        }
      }
      
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { 
					e.getClass().getName() }));
		} finally {
			if (info!=null) {
				info.delete();
			}
		}
		return null;
	}

	/**
	 * This test case verifies that the Autoconf Resource Processor has
	 * updated ManagedService properties passing the same properties as
	 * extracted from AUTOCONF.xml
	 * 
	 * @spec 115.2 Configuration Data
	 */
	private void testConfiguration001() {
		tbc.log("#testConfiguration001");
		DeploymentPackage dp = null;
    Dictionary properties = null;
		try {
			dp = installAutoConfigDP();
      TestingManagedService msf = tbc.getManagedService();
      if(!msf.isUpdated()){
        for(int i=0;i<50&!msf.isUpdated();i++){
          Thread.sleep(100);
        }
      }
			properties = msf.getProperties();
			tbc.assertTrue("The Resource Processor updated the same Dictionary properties as received by the ManagedService",
					properties.get(TestingManagedService.ATTRIBUTE_A).equals(TestingManagedService.ATTRIBUTE_A_VALUE) &&
					properties.get(TestingManagedService.ATTRIBUTE_B).equals(TestingManagedService.ATTRIBUTE_B_VALUE) &&
					properties.get(TestingManagedService.ATTRIBUTE_C).equals(TestingManagedService.ATTRIBUTE_C_VALUE)
					);
		} catch (InvalidSyntaxException e) {
			tbc.fail("InvalidSyntaxException: Failed to get service");
		} catch (Exception e) {
      e.printStackTrace();
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { 
					e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp);
		}
	}
	
	/**
	 * This test case verifies that the Autoconf Resource Processor has
	 * called the updated method of the ManagedServiceFactory passing the
	 * same properties as extracted from AUTOCONF.xml
	 * 
	 * @spec 115.2 Configuration Data
	 */
	private void testConfiguration002() {
		tbc.log("#testConfiguration002");
		DeploymentPackage dp = null;
		try {
			Dictionary properties=null;
      dp = installAutoConfigDP();
      TestingManagedServiceFactory msf = tbc.getManagedServiceFactory();
      if(!msf.isUpdated()){
        for(int i=0;i<50&!msf.isUpdated();i++){
          Thread.sleep(100);
        }
      }
			properties = msf.getProperties();
			//We check only our properties because there are some automatic properties added by the Configuration Admin
			tbc.assertTrue("The Resource Processor updated the same Dictionary properties as received by the ManagedServiceFactory",
					properties.get(TestingManagedServiceFactory.ATTRIBUTE_A).equals(TestingManagedServiceFactory.ATTRIBUTE_A_VALUE) &&
					properties.get(TestingManagedServiceFactory.ATTRIBUTE_B).equals(TestingManagedServiceFactory.ATTRIBUTE_B_VALUE) &&
					properties.get(TestingManagedServiceFactory.ATTRIBUTE_C).equals(TestingManagedServiceFactory.ATTRIBUTE_C_VALUE)
					);
			
		} catch (InvalidSyntaxException e) {
			tbc.fail("InvalidSyntaxException: Failed to get service");
		} catch (Exception e) {
      e.printStackTrace();
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { 
					e.getClass().getName() }));
		} finally {
			tbc.uninstall(dp);
		}
	}
	
	/**
	 * This test case assures that a Deployment Package that requires any activity from 
	 * the Autoconf Resource processor must at least provide ConfigurationPermission[*,CONFIGURE].
	 * 
	 * @spec 115.4.2 Autoconf Resource Permissions
	 */
	private void testConfiguration003() {
	  tbc.log("#testConfiguration003");
    TestingDeploymentPackage testDP = tbc
        .getTestingDeploymentPackage(DeploymentConstants.AUTO_CONFIG_DP);
    Vector cpis = new Vector();
    ConditionalPermissionInfo cpi = null;
    try {
      Enumeration infos = tbc.getCondPermAdmin().getConditionalPermissionInfos();
      if (infos != null)
        while (infos.hasMoreElements()) {
          ConditionalPermissionInfo info = (ConditionalPermissionInfo) infos.nextElement();
          ConditionInfo[] conditions = info.getConditionInfos();
          if (conditions.length == 0) {
            info.delete();
            cpis.addElement(info);
          }
        }
      cpi = tbc.getCondPermAdmin().addConditionalPermissionInfo(
          DeploymentConstants.CONDITION_SIGNER, new PermissionInfo[0]);
      dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
      tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL,
          new String[] { "Deployment Package" }), dp);
      tbc.failException("", DeploymentException.class);
    } catch (DeploymentException e) {
      tbc
          .pass("A DeploymentException was correctly thrown if the Autoconf Resource processor does not have ConfigurationPermission[*,CONFIGURE]. Message: "
              + e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
      tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION,
          new String[] { e.getClass().getName() }));
    } finally {
      if (cpi != null) {
        cpi.delete();
      }
      for (int i = 0; i < cpis.size(); i++) {
        ConditionalPermissionInfo info = (ConditionalPermissionInfo) cpis.elementAt(i);
        tbc.getCondPermAdmin().setConditionalPermissionInfo(info.getName(),
            info.getConditionInfos(), info.getPermissionInfos());
      }
      tbc.uninstall(dp);
    }
	}
	
	/**
	 * This test case assures that a Deployment Package can be installed using only 
	 * ConfigurationPermission[*,CONFIGURE].
	 * 
	 * @spec 115.4.2 Autoconf Resource Permissions
	 */
	private void testConfiguration004() {
		tbc.log("#testConfiguration004");
		TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.AUTO_CONFIG_DP);
		ConditionalPermissionInfo info = null;
		try {
			info = tbc.getCondPermAdmin().addConditionalPermissionInfo(DeploymentConstants.CONDITION_SIGNER, new PermissionInfo[] { new PermissionInfo(ConfigurationPermission.class.getName(),"*",ConfigurationPermission.CONFIGURE)});
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			tbc.pass("A Deployment Package was installed using only ConfigurationPermission[*,CONFIGURE].");
		} catch (Exception e) {
      e.printStackTrace();
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { 
					e.getClass().getName() }));
		} finally {
			if (info!=null) {
				info.delete();
			}
			tbc.uninstall(dp);
		}
	}
	
}
