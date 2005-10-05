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
 * ===========   ==============================================================
 * Ago 02, 2005  Andre Assad
 * 26            Implement MEG TCK 
 * ===========   ==============================================================
*/

package org.osgi.test.cases.deploymentadmin.tc2.tbc;



/**
 * @author Andre Assad
 * 
 * Constants class to improve readability
 * 
 */
public class DeploymentConstants {

	public static final String SIGNER_FILTER = "CN=CESAR.ORG.BR, OU=CESAR, O=MOTOROLA, L=RECIFE, C=BR";
    public static final String SIGNER_FILTER_WILDCARD = "CN=*.ORG.BR, OU=CESAR, O=MOTOROLA, L=RECIFE, C=BR";
    public static final String SIGNER_FILTER_INVALID1 = "CN=#@$.ORG.BR, OU=CESAR, O=MOTOROLA, L=RECIFE, C=BR";
    public static final String SIGNER_FILTER_INVALID2 = "2=*.ORG.BR, TX=CESAR, R=MOTOROLA, L=RECIFE, C=BR";
	public static final String PID_RESOURCE_PROCESSOR1 = "org.osgi.test.cases.deployment.bundles.rp1";
	public static final String PID_RESOURCE_PROCESSOR2 = "org.osgi.test.cases.deployment.bundles.rp2";
	public static final String PID_RESOURCE_PROCESSOR3 = "org.osgi.test.cases.deployment.bundles.rp3";
    public static final String PID_RESOURCE_PROCESSOR4 = "org.osgi.test.cases.deployment.bundles.rp4";
	public static final String PID_MANAGED_SERVICE_FACTORY = "org.osgi.test.cases.deploymentadmin.tc2.tb1";
	public static final String PID_MANAGED_SERVICE = "br.org.cesar.bundles.tb4";
	
	public static final int SIMPLE_DP = 0;
	public static final int RP_RESOURCE_INSTALL_DP = 1;
	public static final int RP_RESOURCE_UPDATE_DP = 2;
	public static final int RP_RESOURCE_UNINSTALL_DP = 3;
	public static final int AUTO_CONFIG_DP = 4;
	public static final int SESSION_RESOURCE_PROCESSOR_DP = 5;
	public static final int SESSION_TEST_DP = 6;
	public static final int SESSION_UPDATE_TEST_DP = 7;
	public static final int TRANSACTIONAL_SESSION_DP = 8;
	public static final int RESOURCE_PROCESSOR_2_DP = 9;
    public static final int BLOCK_SESSION_RESOURCE_PROCESSOR = 10;
    public static final int MANIFEST_NOT_1ST_FILE = 11;
    public static final int RP_FROM_OTHER_DP = 12;
    public static final int INSTALL_FAIL_DP = 13;
    public static final int RESOURCE_PROCESSOR_RP3 = 14;
    public static final int BSN_DIFF_FROM_MANIFEST = 15;
    public static final int BVERSION_DIFF_FROM_MANIFEST = 16;
    public static final int SIMPLE_RESOURCE_RP3 = 17;
    public static final int SIMPLE_BUNDLE_RES_DP = 18;
    public static final int SIMPLE_NO_BUNDLE_DP = 19;
    public static final int SIMPLE_NO_RESOURCE_DP = 20;
    public static final int BUNDLE_FAIL_RES_DP = 21;
    public static final int BUNDLE_FAIL_ON_STOP = 22;
    public static final int SIGNING_FILE_NOT_NEXT = 23;
    public static final int SIMPLE_UNINSTALL_BUNDLE = 24;
	
	//This array index is very important, and must be the same as the deployment code
	//Use 31 characters. 
	public static final String[] MAP_CODE_TO_DP = {"simple_dp",
        "rp_resource_dp", "rp_resource_dp", "rp_resource_dp", "auto_config_dp",
        "session_resource_processor_dp", "session_test_dp",
        "session_update_test_dp", "transactional_session_dp",
        "resource_processor2_dp", "block_session", "manifest_not_1st_file",
        "rp_from_other_dp", "install_fail_dp", "resource_processor_rp3",
        "bsn_diff_from_manifest", "bversion_diff_from_manifest",
        "simple_resource_rp3", "simple_bundle_res_dp", "simple_no_bundle",
        "simple_no_resource", "bundle_fail_res_dp", "bundle_fail_on_stop_dp",
        "signing_file_not_next", "simple_uninstall_bundle"};
	
	public static final String DEPLOYMENT_PACKAGE_NAME_ALL = "(&(name=*))";
	public static final String BUNDLE_NAME_ALL = "(name=*)";
	public static final String DEPLOYMENT_PACKAGE_NAME0 = "(name="+MAP_CODE_TO_DP[0]+")";
	public static final String DEPLOYMENT_PACKAGE_NAME1 = "(&(name="+MAP_CODE_TO_DP[1]+")"+"(signer=-;"+SIGNER_FILTER+"))";
	public static final String DEPLOYMENT_PACKAGE_NAME2 = "(&(name="+MAP_CODE_TO_DP[2]+")"+"(signer=-;"+SIGNER_FILTER+"))";
    public static final String DEPLOYMENT_PACKAGE_NAME3 = "(&(name="+MAP_CODE_TO_DP[2]+")"+"(signer=-;"+SIGNER_FILTER_WILDCARD+"))";
    public static final String DEPLOYMENT_PACKAGE_NAME4 = "(&(name="+MAP_CODE_TO_DP[0]+")"+"(signer=-;"+SIGNER_FILTER_INVALID1+"))";
    public static final String DEPLOYMENT_PACKAGE_NAME5 = "(&(name="+MAP_CODE_TO_DP[0]+")"+"(signer=-;"+SIGNER_FILTER_INVALID2+"))";
	
	public static final String DEPLOYMENT_PACKAGE_DIFFERENT_SIGNATURE = "(&(name="+MAP_CODE_TO_DP[0]+")"+"(signer=-;"+"CN=CESAR.ORG.BR, OU=CIN, O=MOTOROLA, L=RECIFE, ST=PERNAMBUCO, C=BR))";
	public static final String INVALID_DEPLOYMENT_PACKAGE_NAME = "name;CESAR signer,CA_CESAR";
	public static final String RESOURCE_PROCESSOR_PROPERTY_KEY = "property.key";
    
    public static final String OSGI_DP_LOCATION = "osgi-dp:";
    public static final String MANAGED_BUNDLE_LOCATION = "BundleLocation/tb1.jar";
    
	public static final String BUNDLE_NAME_FILTER = "(name=bundle.tb1)";
	public static final String BUNDLE_NAME_WRONG_FILTER = "(&(name=bundle.tb1)(location=-;osgi-dp: bundle001.jar))";
	public static final String EXCEPTION_MESSAGE = "Unknown failure";
    
	// very large, we don't know what kind of devices the TCK will be executed
	public static final int TIMEOUT = 180000;
    
    //set default value and then reset if present in TCK properties file
    public static int SESSION_TIMEOUT = 1000;

}
