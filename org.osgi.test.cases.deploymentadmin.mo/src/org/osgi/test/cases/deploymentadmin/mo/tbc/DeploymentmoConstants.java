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
 * Date         Author(s)
 * CR           Headline
 * ===========  ==============================================================
 * 03/08/2005   Eduardo Oliveira
 * 162          Implement MEGTCK for the DeploymentMO Spec
 * ===========  ==============================================================
 * 18/08/2005   Andre Assad
 * 162          Refactoring
 * ===========  ==============================================================
 */

package org.osgi.test.cases.deploymentadmin.mo.tbc;

import info.dmtree.security.DmtPermission;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import junit.framework.Assert;

import org.osgi.test.support.OSGiTestCase;

public class DeploymentmoConstants {
    public static String DLOTA_PATH;
    public static String DLOTA_RELATIVE_PATH;
    public static String SERVER;
    public static String LOCATION;
    
    //True if the implementation supports cancelling  
    public static final boolean USER_CANCEL_IMPLEMENTED;
    
    //True if the implementation is streaming
    public static final boolean IS_STREAMING;

    //This flag must be true if the implementation reports progress on the Status node  
    public static final boolean STATUS_NODE_REPORTS_PROGRESS;

    public static final int TIMEOUT;
    
    public static final String PRINCIPAL = "admin";

    public static final File DELIVERED_AREA;
	static {
		String prop = System
				.getProperty("org.osgi.impl.service.deploymentadmin.deliveredarea");
		Assert
				.assertNotNull(
						"Must set property: org.osgi.impl.service.deploymentadmin.deliveredarea",
						prop);
		DELIVERED_AREA = new File(prop);
		Assert.assertFalse("deliveredarea is not a directory: "
				+ DELIVERED_AREA.getPath(), DELIVERED_AREA.exists()
				&& !DELIVERED_AREA.isDirectory());
		if (!DELIVERED_AREA.isDirectory())
			Assert.assertTrue("Could not create root directory: "
					+ DELIVERED_AREA.getPath(), DELIVERED_AREA.mkdirs());
	}

    public static File TEMP_DIR;
    
    //Specified values
    public static final String ENVTYPE = "OSGi.R4";
    public static final String ENVIRONMENT_DP = "http://www.osgi.org/xmlns/dd/DP";
    public static final String ENVIRONMENT_BUNDLE = "http://www.osgi.org/xmlns/dd/bundle";
    public static final int OSGI_DP = 1;
    public static final int OSGI_BUNDLE = 2;
    
    //Codes of the deployment's state
    public static final int CODE_IDLE = 10;
    public static final int CODE_DOWNLOAD_FAILED = 20;
    public static final int CODE_DOWNLOADING = 30;
    public static final int CODE_DOWNLOADED = 40;
    public static final int CODE_STREAMING = 50;
    public static final int CODE_DEPLOYING = 60;
    public static final int CODE_DEPLOYMENT_FAILED = 70;
    public static final int CODE_DEPLOYED = 80;
    
    //Alert types 
    public static final String ALERT_TYPE_DEPLOYED_REMOVE = "org.osgi.deployment.deployed.remove";
    public static final String ALERT_TYPE_DELIVERED_REMOVE = "org.osgi.deployment.delivered.remove";
    public static final String ALERT_TYPE_INSTALLANDACTIVATE = "org.osgi.deployment.installandactivate";
    public static final String ALERT_TYPE_DOWNLOADANDINSTALLANDACTIVATE = "org.osgi.deployment.downloadandinstallandactivate";
    
    //Deployment, Download and Deployed subtrees
    public static final String OSGI_ROOT = System.getProperty("info.dmtree.osgi.root");
    public static final String DEPLOYMENT = OSGI_ROOT + "/Deployment";
    public static final String DEPLOYMENT_DOWNLOAD = DEPLOYMENT + "/Download";
    public static final String DEPLOYMENT_INVENTORY = DEPLOYMENT + "/Inventory";
    public static final String DEPLOYMENT_INVENTORY_DELIVERED = DEPLOYMENT_INVENTORY + "/Delivered";
    public static final String DEPLOYMENT_INVENTORY_DEPLOYED = DEPLOYMENT_INVENTORY + "/Deployed";
    public static final String DEPLOYMENT_EXT = DEPLOYMENT + "/Ext";


    //Deployment packages
    public static final int SIMPLE_DP = 0;
    public static final int RESOURCE_PROCESSOR_DP = 1;
    public static final int DP_CONTAINING_A_BUNDLE_FROM_OTHER_DP = 2;
    public static final int DP_REMOVES_RESOURCE_FOR_RP4 = 3;
    public static final int MISSING_NAME_HEADER_DP = 4;
    public static final int SYMB_NAME_DIFFERENT_FROM_MANIFEST_DP = 5;
    public static final int SIMPLE_NO_BUNDLE_DP = 6;
    public static final int SIMPLE_NO_RESOURCE_DP = 7;
    public static final int DOWNLOAD_FAILED_DP = 8;
    public static final int DP_THROWS_RESOURCE_VIOLATION = 9;
    public static final int BUNDLE_FROM_OTHER_DP = 10;
    public static final int MANIFEST_NOT_1ST_FILE = 11;
    public static final int FIX_PACK_LOWER_RANGE_DP= 12;
    public static final int UNTRUSTED_DP = 13;
    public static final int RESOURCE_PROCESSOR_CUSTOMIZER = 14;
    public static final int RP_FROM_OTHER_DP = 15;
    public static final int BUNDLE_THROWS_EXCEPTION_DP = 16;
    public static final int NON_CUSTOMIZER_RP = 17;
    public static final int SIMPLE_RESOURCE_DP = 18;
    public static final int NOT_ACCEPTABLE_CONTENT = 19;
    public static final int SIMPLE_BUNDLE = 20;
    public static final int RP_NOT_ABLE_TO_COMMIT = 21; 
    public static final int MISSING_RESOURCE_FIX_PACK = 22;
    public static final int MISSING_BUNDLE_FIX_PACK = 23;
    public static final int BLOCK_SESSION_RESOURCE_PROCESSOR = 24;
    public static final int SIMPLE_FIX_PACK_DP = 25;
    public static final int RP_THROWS_NO_SUCH_RESOURCE = 26;
    public static final int DP_INSTALLS_RESOURCE_FOR_RP4 = 27;
    
    

    public static final String[] MAP_CODE_TO_ARTIFACT = {"simple",
        "resource_processor", "bundle_from_other",
        "dp_removes_resource_for_rp4", "missing_name_header", "symb_name_dif_from_manifest",
        "simple_no_bundle", "simple_no_resource", "download_failed","dp_throws_resource_violation",
        "bundle_from_other","manifest_not_1st_file","fix_pack_lower_range","untrusted",
        "resource_processor_customizer","rp_from_other_dp","bundle_throws_exception","non_customizer_rp",
        "simple_resource","java.gif","bundle001.jar","rp_not_able_to_commit","missing_resource_fix_pack",
        "missing_bundle_fix_pack", "block_session","simple_fix_pack","rp_throws_no_such_resource",
        "dp_installs_resource_for_rp4"};
    
    public static String[] MAP_CODE_TO_ARTIFACT_MANGLED = new String[MAP_CODE_TO_ARTIFACT.length];
    
    public static final String PID_RESOURCE_PROCESSOR1 = "org.osgi.test.cases.deployment.bundles.rp1";
    public static final String PID_RESOURCE_PROCESSOR2 = "org.osgi.test.cases.deployment.bundles.rp2";
    public static final String PID_RESOURCE_PROCESSOR3 = "org.osgi.test.cases.deployment.bundles.rp3";
    public static final String PID_RESOURCE_PROCESSOR4 = "org.osgi.test.cases.deployment.bundles.rp4";
    public static final String PID_RESOURCE_PROCESSOR5 = "org.osgi.test.cases.deployment.bundles.rp5";
    
    public static final String SIMPLE_DP_NAME = "simple.dp";
    public static final String SIMPLE_DP_XML = "download.xml";

    //Path to delivered subtree (with simple_dp.dp as nodeId)
    public static final String SIMPLE_DP_DELIVERED = DEPLOYMENT_INVENTORY_DELIVERED + "/" + SIMPLE_DP_NAME;
    public static final String SIMPLE_DP_DELIVERED_ID = SIMPLE_DP_DELIVERED + "/ID";
    public static final String SIMPLE_DP_DELIVERED_DATA = SIMPLE_DP_DELIVERED + "/Data";
    public static final String SIMPLE_DP_DELIVERED_DESCRIPTOR = SIMPLE_DP_DELIVERED + "/Descriptor";
    public static final String SIMPLE_DP_DELIVERED_ENVTYPE = SIMPLE_DP_DELIVERED + "/EnvType";
    public static final String SIMPLE_DP_DELIVERED_EXT = SIMPLE_DP_DELIVERED + "/Ext";
    public static final String SIMPLE_DP_DELIVERED_OPERATIONS = SIMPLE_DP_DELIVERED + "/Operations";
    public static final String SIMPLE_DP_DELIVERED_OPERATIONS_INSTALLANDACTIVATE = SIMPLE_DP_DELIVERED_OPERATIONS + "/InstallAndActivate";
    public static final String SIMPLE_DP_DELIVERED_OPERATIONS_REMOVE = SIMPLE_DP_DELIVERED_OPERATIONS + "/Remove";
    public static final String SIMPLE_DP_DELIVERED_OPERATIONS_EXT = SIMPLE_DP_DELIVERED_OPERATIONS + "/Ext";
    
    //The expected values of the simple_dp.dp after deployed
    public static final String SIMPLE_DP_SIGNER = "CN=John Smith, O=ACME Inc, OU=ACME Cert Authority, L=Austin, ST=Texas, C=US";
    
    
    public static final String OSGI_DP_LOCATION = "osgi-dp:"; 
    public static final String SIMPLE_DP_LOCATION = "";
    public static Manifest SIMPLE_DP_MANIFEST;
    public static Manifest SIMPLE_FIX_PACK_MANIFEST;
    
    public static final int SIMPLE_DP_APPLICATION_TYPE = 1;
    public static final String SIMPLE_DP_BUNDLE1_SYMBNAME = "bundles.tb1";
    public static final String SIMPLE_DP_BUNDLE1_LOCATION = "osgi-dp:" + SIMPLE_DP_BUNDLE1_SYMBNAME;
    public static Manifest SIMPLE_DP_BUNDLE1_MANIFEST;
    public static final int SIMPLE_DP_BUNDLE1_STATE = 32;
    
    public static final String SIMPLE_DP_BUNDLE2_SYMBNAME = "bundles.tb2";
    public static final String SIMPLE_DP_BUNDLE2_LOCATION = "osgi-dp:" + SIMPLE_DP_BUNDLE2_SYMBNAME;
    public static Manifest SIMPLE_DP_BUNDLE2_MANIFEST;
    public static final int SIMPLE_DP_BUNDLE2_STATE = 32;
    
    
    public static final String SIMPLE_FIX_PACK_BUNDLE1_SYMBNAME = "bundles.tb1";
    public static final String SIMPLE_FIX_PACK_BUNDLE1_LOCATION = "osgi-dp:" + SIMPLE_FIX_PACK_BUNDLE1_SYMBNAME;
    public static Manifest SIMPLE_FIX_PACK_BUNDLE1_MANIFEST;
    public static final int SIMPLE_FIX_PACK_BUNDLE1_STATE = 32;
    
    public static JarFile getJarFile(int dpCode) {
    	try {
			return new JarFile(new File(DELIVERED_AREA, MAP_CODE_TO_ARTIFACT[dpCode] + ((dpCode!=SIMPLE_BUNDLE)?".dp":"")));
		} catch (IOException e) {
			OSGiTestCase.fail(e.getMessage(), e);
		}
		return null;
    }
    
    public static JarFile getTempJarFile(String name) {
    	try {
			return new JarFile(TEMP_DIR.getAbsolutePath() + File.separatorChar + name);
		} catch (IOException e) {
			OSGiTestCase.fail(e.getMessage(), e);
		}
		return null;
    }
    
    public static void init(){
        JarFile simpleDp = null;
        JarFile tmpJarFile = null;
      	try {
      		//We need to get the Manifest from the jar because it is modified when it is generated.
      		if (DeploymentmoConstants.DELIVERED_AREA.getParent()!=null) {
      			TEMP_DIR = new File(DeploymentmoConstants.DELIVERED_AREA.getParent() + File.separatorChar + "tmp");
      		} else {
      			TEMP_DIR = new File("tmp");	
      		}
      			
      		TEMP_DIR.mkdir();
      		simpleDp =getJarFile(SIMPLE_DP);
      		SIMPLE_DP_MANIFEST = simpleDp.getManifest();
          tmpJarFile = getJarFile(SIMPLE_FIX_PACK_DP);
      		SIMPLE_FIX_PACK_MANIFEST = tmpJarFile.getManifest();
          tmpJarFile.close();
    
      		DeploymentmoTestControl.copyTempBundles();
          tmpJarFile = getTempJarFile(DeploymentmoTestControl.getTempFileName(SIMPLE_DP, "bundle001.jar"));
      		SIMPLE_DP_BUNDLE1_MANIFEST = tmpJarFile.getManifest();
          tmpJarFile.close();
          tmpJarFile = getTempJarFile(DeploymentmoTestControl.getTempFileName(SIMPLE_DP, "bundle002.jar"));
      		SIMPLE_DP_BUNDLE2_MANIFEST = tmpJarFile.getManifest();
          tmpJarFile.close();
          tmpJarFile = getTempJarFile(DeploymentmoTestControl.getTempFileName(SIMPLE_FIX_PACK_DP, "bundle001.jar"));
      		SIMPLE_FIX_PACK_BUNDLE1_MANIFEST = tmpJarFile.getManifest();
          tmpJarFile.close();
      	} catch (Exception e) {
			OSGiTestCase.fail(e.getMessage(), e);
      	} finally {
          try{
            simpleDp.close();
            tmpJarFile.close();
          }catch(Exception ex){
				OSGiTestCase.fail(ex.getMessage(), ex);
          }
        }
    }
      	
    static { 
		//Properties files
		if (System.getProperty("org.osgi.test.cases.deploymentadmin.mo.timeout")!=null) {
			TIMEOUT = Integer.parseInt(System.getProperty("org.osgi.test.cases.deploymentadmin.mo.timeout"));  	
		} else {
			TIMEOUT = 2000;
		}
		if (System.getProperty("org.osgi.test.cases.deploymentadmin.mo.usercancel")!=null) {
			USER_CANCEL_IMPLEMENTED = Boolean.getBoolean("org.osgi.test.cases.deploymentadmin.mo.usercancel");  	
		} else {
			USER_CANCEL_IMPLEMENTED = false;
		}
		if (System.getProperty("org.osgi.test.cases.deploymentadmin.mo.status_node_progress")!=null) {
			STATUS_NODE_REPORTS_PROGRESS = Boolean.getBoolean("org.osgi.test.cases.deploymentadmin.mo.status_node_progress");  	
		} else {
			STATUS_NODE_REPORTS_PROGRESS = true;
		}
		if (System.getProperty("org.osgi.test.cases.deploymentadmin.mo.streaming")!=null) {
			IS_STREAMING = Boolean.getBoolean("org.osgi.test.cases.deploymentadmin.mo.streaming");  	
		} else {
			IS_STREAMING = true;
		}
	
		
		

    }
    //----------------------------------------------------------------------------------------------------
    // Path to Download Subtree
    public static final String DEPLOYMENT_DOWNLOAD_TEST = DEPLOYMENT_DOWNLOAD + "/test";
    public static final String DEPLOYMENT_DOWNLOAD_TEST_ID = DEPLOYMENT_DOWNLOAD_TEST + "/ID";
    public static final String DEPLOYMENT_DOWNLOAD_TEST_ENVTYPE = DEPLOYMENT_DOWNLOAD_TEST + "/EnvType";
    public static final String DEPLOYMENT_DOWNLOAD_TEST_URI = DEPLOYMENT_DOWNLOAD_TEST + "/URI";
    public static final String DEPLOYMENT_DOWNLOAD_TEST_EXT = DEPLOYMENT_DOWNLOAD_TEST + "/Ext";
    public static final String DEPLOYMENT_DOWNLOAD_TEST_STATUS = DEPLOYMENT_DOWNLOAD_TEST + "/Status";
    public static final String DEPLOYMENT_DOWNLOAD_TEST_OPERATIONS = DEPLOYMENT_DOWNLOAD_TEST + "/Operations";
    public static final String DEPLOYMENT_DOWNLOAD_TEST_OPERATIONS_EXT = DEPLOYMENT_DOWNLOAD_TEST_OPERATIONS + "/Ext";
    public static final String DEPLOYMENT_DOWNLOAD_TEST_DOWN_INST_ACTIV = DEPLOYMENT_DOWNLOAD_TEST_OPERATIONS + "/DownloadAndInstallAndActivate";

    public static final String SIGNER_FILTER = "CN=John Smith, O=ACME Inc, OU=ACME Cert Authority, L=Austin, ST=Texas, C=US";
    
    public static final String ALL_DMT_PERMISSION = DmtPermission.ADD + ","
        + DmtPermission.DELETE + "," + DmtPermission.EXEC + ","
        + DmtPermission.GET + "," + DmtPermission.REPLACE;
    
    
    
    //---------------------------------------------------------------------------------------------
    
    //Deployed subtree
    //Subtree $/Deployment/Inventory/Deployed/<node_id>
    public static String getDeployedNodeId(String nodeId) {
        return DEPLOYMENT_INVENTORY_DEPLOYED + "/" + nodeId;
    }
    //Subtree $/Deployment/Inventory/Deployed/<node_id>/Ext
    public static String getDeployedExt(String nodeId) {
        return getDeployedNodeId(nodeId) + "/Ext";
    }
    //Subtree $/Dseployment/Inventory/Deployed/<node_id>/Ext/Manifest
    public static String getDeployedExtManifest(String nodeId) {
        return getDeployedExt(nodeId) + "/Manifest";
    }
    //Subtree $/Deployment/Inventory/Deployed/<node_id>/Ext/Signers
    public static String getDeployedExtSigners(String nodeId) {
        return getDeployedExt(nodeId) + "/Signers";
    }
    //Subtree $/Deployment/Inventory/Deployed/<node_id>/Ext/Signers/<signer>
    public static String getDeployedExtSignersSignerId(String nodeId,String signer) {
        return getDeployedExtSigners(nodeId) + "/" + signer;
    }
    //Subtree $/Deployment/Inventory/Deployed/<node_id>/Ext/PackageType
    public static String getDeployedExtPackageType(String nodeId) {
        return getDeployedExt(nodeId) + "/PackageType";
    }
    //Subtree $/Deployment/Inventory/Deployed/<node_id>/Ext/Bundles
    public static String getDeployedExtBundles(String nodeId) {
        return getDeployedExt(nodeId) + "/Bundles";
    }
    //Subtree $/Deployment/Inventory/Deployed/<node_id>/Ext/Bundles/<bundle_id>
    public static String getDeployedExtBundlesBundleId(String nodeId,String bundleId) {
        return getDeployedExtBundles(nodeId) + "/" + bundleId;
    }
    
    //Subtree $/Deployment/Inventory/Deployed/<node_id>/Ext/Bundles/<bundle_id>/Location
    public static String getDeployedExtBundlesLocation(String nodeId,String bundleId) {
        return getDeployedExtBundlesBundleId(nodeId,bundleId) + "/Location";
    }
    //Subtree $/Deployment/Inventory/Deployed/<node_id>/Ext/Bundles/<bundle_id>/Manifest
    public static String getDeployedExtBundlesManifest(String nodeId,String bundleId) {
        return getDeployedExtBundlesBundleId(nodeId,bundleId) + "/Manifest";
    }
    //Subtree $/Deployment/Inventory/Deployed/<node_id>/Ext/Bundles/<bundle_id>/State
    public static String getDeployedExtBundlesState(String nodeId,String bundleId) {
        return getDeployedExtBundlesBundleId(nodeId,bundleId) + "/State";
    }
    //Subtree $/Deployment/Inventory/Deployed/<node_id>/Ext/Bundles/<bundle_id>/Signers
    public static String getDeployedExtBundlesSigners(String nodeId,String bundleId) {
        return getDeployedExtBundlesBundleId(nodeId,bundleId) + "/Signers";
    }
    //Subtree $/Deployment/Inventory/Deployed/<node_id>/Ext/Bundles/<bundle_id>/Signers/<signer>
    public static String getDeployedExtBundlesSignersSignerId(String nodeId,String bundleId,String signer) {
        return getDeployedExtBundlesSigners(nodeId,bundleId) + "/" + signer;
    }    

    //Subtree $/Deployment/Inventory/Deployed/<node_id>/ID    
    public static String getDeployedID(String nodeId) {
        return getDeployedNodeId(nodeId) + "/ID";
    }
    //Subtree $/Deployment/Inventory/Deployed/<node_id>/EnvType
    public static String getDeployedEnvType(String nodeId) {
        return getDeployedNodeId(nodeId) + "/EnvType";
    }
    //Subtree $/Deployment/Inventory/Deployed/<node_id>/Operations
    public static String getDeployedOperations(String nodeId) {
        return getDeployedNodeId(nodeId) + "/Operations";
    }
    //Subtree $/Deployment/Inventory/Deployed/<node_id>/Operations/Remove
    public static String getDeployedOperationsRemove(String nodeId) {
        return getDeployedOperations(nodeId) + "/Remove";
    }
    //Subtree $/Deployment/Inventory/Deployed/<node_id>/Operations/Ext
    public static String getDeployedOperationsExt(String nodeId) {
        return getDeployedOperations(nodeId) + "/Ext";
    }
    //----------------------------------------------------------------------------
    //Delivered subtree
    //Subtree $/Deployment/Inventory/Delivered/<node_id>
    public static String getDeliveredNodeId(int nodeId) {
    		return DEPLOYMENT_INVENTORY_DELIVERED + "/" + MAP_CODE_TO_ARTIFACT_MANGLED[nodeId];
    }
    //Subtree $/Deployment/Inventory/Delivered/<node_id>/Operations/InstallAndActivate
    public static String getDeliveredOperationsInstallAndActivate(int nodeId) {
        return getDeliveredNodeId(nodeId) + "/Operations/InstallAndActivate";
    }
//  Subtree $/Deployment/Inventory/Delivered/<node_id>/Operations/Remove
    public static String getDeliveredOperationsRemove(int nodeId) {
    	return getDeliveredNodeId(nodeId) + "/Operations/Remove";
    }
    
    public static boolean RP4_SIMULATE_EXCEPTION_ON_DROPPED = false;
    public static boolean RP4_SIMULATE_EXCEPTION_ON_PROCESS = false;
}
