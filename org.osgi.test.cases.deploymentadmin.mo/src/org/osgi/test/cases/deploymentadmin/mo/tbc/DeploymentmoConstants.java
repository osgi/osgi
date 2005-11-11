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

import java.util.Vector;
import org.osgi.service.dmt.security.DmtPermission;

public class DeploymentmoConstants {
    public static String DLOTA_PATH;
    public static String DLOTA_RELATIVE_PATH;
    public static String SERVER;
    public static String LOCATION;
    
    public static final String PRINCIPAL = "admin";

    public static final String DELIVERED_AREA = System.getProperty("org.osgi.impl.service.deploymentadmin.deliveredarea");
    //Values specified
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
    public static final String OSGI_ROOT = "./OSGi";
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
    public static final int RESOURCE_FROM_OTHER_DP = 3;
    public static final int MISSING_NAME_HEADER_DP = 4;
    public static final int SYMB_NAME_DIFFERENT_FROM_MANIFEST_DP = 5;
    public static final int SIMPLE_NO_BUNDLE_DP = 6;
    public static final int SIMPLE_NO_RESOURCE_DP = 7;
    public static final int DOWNLOAD_FAILED_DP = 8;
    public static final int TIMEOUT_DP = 9;
    public static final int BUNDLE_FROM_OTHER_DP = 10;
    public static final int MANIFEST_NOT_1ST_FILE = 11;
    public static final int FIX_PACK_LOWER_RANGE_DP= 12;
    public static final int SIMPLE_UNSIGNED_DP = 13;
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
    public static final int DP_THROWS_RESOURCE_VIOLATION = 28;

    public static final String[] MAP_CODE_TO_ARTIFACT = {"simple",
        "resource_processor", "bundle_from_other_dp",
        "resource_from_other_dp", "missing_name_header", "symb_name_different_from_manifest",
        "simple_no_bundle", "simple_no_resource", "download_failed","timeout","bundle_from_other_dp",
        "manifest_not_1st_file","fix_pack_lower_range","simple_unsigned_dp",
        "resource_processor_customizer","rp_from_other_dp","bundle_throws_exception","non_customizer_rp",
        "simple_resource_dp","java.gif","bundle001.jar","rp_not_able_to_commit","missing_resource_fix_pack",
        "missing_bundle_fix_pack", "blocking_session","simple_fix_pack","rp_throws_no_such_resource",
        "dp_installs_resource_for_rp4","dp_throws_resource_violation"};
    
    public static final String PID_RESOURCE_PROCESSOR1 = "org.osgi.test.cases.deployment.bundles.rp1";
    public static final String PID_RESOURCE_PROCESSOR2 = "org.osgi.test.cases.deployment.bundles.rp2";
    public static final String PID_RESOURCE_PROCESSOR3 = "org.osgi.test.cases.deployment.bundles.rp3";
    public static final String PID_RESOURCE_PROCESSOR4 = "org.osgi.test.cases.deployment.bundles.rp4";
    
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
    public static final String SIMPLE_DP_SIGNER = "CN=John Smith, O=ACME Inc, OU=ACME Cert Authority, L=Austin, S=Texas, C=US";
    
    //Manifest header can be shown in a different order
    public static Vector simpleDpManifest = new Vector();
    public static Vector simpleFixPackManifest = new Vector();

    
    static {
        simpleDpManifest.add("Manifest-Version: 1.0");
	    simpleDpManifest.add("DeploymentPackage-ContactAddress: Rua Bione, 220, Cais do Apolo - Bair\n o do Recife, Recife - PE, CEP: 50.030-390.");
	    simpleDpManifest.add("DeploymentPackage-Description: A testing deployment package.");
	    simpleDpManifest.add("DeploymentPackage-SymbolicName: simple_dp");
	    simpleDpManifest.add("DeploymentPackage-Copyright: Motorola (c).");
	    simpleDpManifest.add("DeploymentPackage-Vendor: CESAR.");
	    simpleDpManifest.add("DeploymentPackage-License: www.cesar.org.br/megtck/license.html");
	    simpleDpManifest.add("DeploymentPackage-DocURL: www.cesar.org.br.");
	    simpleDpManifest.add("DeploymentPackage-Version: 1.0.0");
	    simpleDpManifest.add("Name: bundle001.jar");
	    simpleDpManifest.add("Bundle-Version: 1.0");
	    simpleDpManifest.add("Bundle-SymbolicName: bundles.tb1");
	    simpleDpManifest.add("Name: bundle002.jar");
	    simpleDpManifest.add("Bundle-Version: 1.0");
	    simpleDpManifest.add("Bundle-SymbolicName: bundles.tb2");
	    
	    simpleFixPackManifest.add("Manifest-Version: 1.0");
	    simpleFixPackManifest.add("DeploymentPackage-Version: 1.1.1");
	    simpleFixPackManifest.add("DeploymentPackage-SymbolicName: simple_dp");
	    simpleFixPackManifest.add("DeploymentPackage-FixPack: [1.0,2.0]");
	    simpleFixPackManifest.add("Name: bundle001.jar");
	    simpleFixPackManifest.add("Bundle-Version: 1.5");
	    simpleFixPackManifest.add("Bundle-SymbolicName: bundles.tb1");
	    simpleFixPackManifest.add("DeploymentPackage-Missing: false");

    }
    
    public static final String OSGI_DP_LOCATION = "osgi-dp:"; 
    public static final String SIMPLE_DP_LOCATION = "";
    public static final int SIMPLE_DP_APPLICATION_TYPE = 1;
    public static final String SIMPLE_DP_BUNDLE1_SYMBNAME = "bundles.tb1";
    public static final String SIMPLE_DP_BUNDLE1_LOCATION = "osgi-dp:" + SIMPLE_DP_BUNDLE1_SYMBNAME;
    public static final String SIMPLE_DP_BUNDLE1_MANIFEST = "Manifest-Version: 1.0\nBundle-SymbolicName: bundles.tb1\nBundle-Version: 1.0\nBundle-Activator: br.org.cesar.bundles.tb1.TB1Activator";
    public static final int SIMPLE_DP_BUNDLE1_STATE = 32;
    
    public static final String SIMPLE_DP_BUNDLE2_SYMBNAME = "bundles.tb2";
    public static final String SIMPLE_DP_BUNDLE2_LOCATION = "osgi-dp:" + SIMPLE_DP_BUNDLE2_SYMBNAME;
    public static final String SIMPLE_DP_BUNDLE2_MANIFEST = "Manifest-Version: 1.0\nBundle-SymbolicName: bundles.tb2\nBundle-Version: 1.0\nBundle-Activator: br.org.cesar.bundles.tb2.TB2Activator";
    public static final int SIMPLE_DP_BUNDLE2_STATE = 32;
    
    public static final String SIMPLE_FIX_PACK_BUNDLE1_SYMBNAME = "bundles.tb1";
    public static final String SIMPLE_FIX_PACK_BUNDLE1_LOCATION = "osgi-dp:" + SIMPLE_FIX_PACK_BUNDLE1_SYMBNAME;
    public static final String SIMPLE_FIX_PACK_BUNDLE1_MANIFEST = "Manifest-Version: 1.0\nBundle-SymbolicName: bundles.tb1\nBundle-Version: 1.5\nBundle-Activator: br.org.cesar.bundles.tb1.TB1Activator";
    public static final int SIMPLE_FIX_PACK_BUNDLE1_STATE = 32;
    
    
    
    // Path to Downloaded Subtree
    public static final String DEPLOYMENT_DOWNLOAD_TEST = DEPLOYMENT_DOWNLOAD + "/test";
    public static final String DEPLOYMENT_DOWNLOAD_TEST_ID = DEPLOYMENT_DOWNLOAD_TEST + "/ID";
    public static final String DEPLOYMENT_DOWNLOAD_TEST_ENVTYPE = DEPLOYMENT_DOWNLOAD_TEST + "/EnvType";
    public static final String DEPLOYMENT_DOWNLOAD_TEST_URI = DEPLOYMENT_DOWNLOAD_TEST + "/URI";
    public static final String DEPLOYMENT_DOWNLOAD_TEST_EXT = DEPLOYMENT_DOWNLOAD_TEST + "/Ext";
    public static final String DEPLOYMENT_DOWNLOAD_TEST_STATUS = DEPLOYMENT_DOWNLOAD_TEST + "/Status";
    public static final String DEPLOYMENT_DOWNLOAD_TEST_OPERATIONS = DEPLOYMENT_DOWNLOAD_TEST + "/Operations";
    public static final String DEPLOYMENT_DOWNLOAD_TEST_OPERATIONS_EXT = DEPLOYMENT_DOWNLOAD_TEST_OPERATIONS + "/Ext";
    public static final String DEPLOYMENT_DOWNLOAD_TEST_DOWN_INST_ACTIV = DEPLOYMENT_DOWNLOAD_TEST_OPERATIONS + "/DownloadAndInstallAndActivate";

    public static final String SIGNER_FILTER = "CN=John Smith, O=ACME Inc, OU=ACME Cert Authority, L=Austin, S=Texas, C=US";
    
    //TODO Change for getting if the implementation is streaming from a properties file
    public static final boolean IS_STREAMING = true;

    //This flag must be true if the implementation reports progress on the Status node  
    public static final boolean STATUS_NODE_REPORTS_PROGRESS = true;
    
    public static final int TIMEOUT = 3000;
    
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
    	if (nodeId==DeploymentmoConstants.SIMPLE_BUNDLE) 
    		return DEPLOYMENT_INVENTORY_DELIVERED + "/" + MAP_CODE_TO_ARTIFACT[nodeId];
		else
			return DEPLOYMENT_INVENTORY_DELIVERED + "/" + MAP_CODE_TO_ARTIFACT[nodeId] + ".dp";
    }
    //Subtree $/Deployment/Inventory/Delivered/<node_id>/Operations/InstallAndActivate
    public static String getDeliveredOperationsInstallAndActivate(int nodeId) {
        return getDeliveredNodeId(nodeId) + "/Operations/InstallAndActivate";
    }
//  Subtree $/Deployment/Inventory/Delivered/<node_id>/Operations/Remove
    public static String getDeliveredOperationsRemove(int nodeId) {
    	return getDeliveredNodeId(nodeId) + "/Operations/Remove";
    }
}
