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

import java.security.AllPermission;
import org.osgi.service.condpermadmin.BundleSignerCondition;
import org.osgi.service.condpermadmin.ConditionInfo;
import org.osgi.service.permissionadmin.PermissionInfo;



/**
 * @author Andre Assad
 * 
 * Constants class to improve readability
 * 
 */
public class DeploymentConstants {

	public static final String SIGNER_FILTER = "CN=John Smith, O=ACME Inc, OU=ACME Cert Authority, L=Austin, ST=Texas, C=US";
    public static final String SIGNER_FILTER_WILDCARD = "CN=*, O=ACME Inc, OU=ACME Cert Authority, L=Austin, ST=Texas, C=US";
    public static final String SIGNER_FILTER_INVALID1 = "NAME=CESAR.ORG.BR, OU=CESAR, O=MOTOROLA, L=RECIFE, C=BR";
    public static final String SIGNER_FILTER_INVALID2 = "2=*.ORG.BR, TX=CESAR, R=MOTOROLA, L=RECIFE, C=BR";
    
    // ConditionInfo for testing AutoConf
    public static final ConditionInfo[] CONDITION_SIGNER =  new ConditionInfo[] {new ConditionInfo(BundleSignerCondition.class.getName(),new String[] {SIGNER_FILTER})};
    public static final PermissionInfo[] ALL_PERMISSION = new PermissionInfo[] { new PermissionInfo(AllPermission.class.getName(),"*","*")};
    
	public static final String PID_RESOURCE_PROCESSOR1 = "org.osgi.test.cases.deployment.bundles.rp1";
	public static final String PID_RESOURCE_PROCESSOR2 = "org.osgi.test.cases.deployment.bundles.rp2";
	public static final String PID_RESOURCE_PROCESSOR3 = "org.osgi.test.cases.deployment.bundles.rp3";
    public static final String PID_RESOURCE_PROCESSOR4 = "org.osgi.test.cases.deployment.bundles.rp4";
    public static final String PID_RESOURCE_PROCESSOR5 = "org.osgi.test.cases.deployment.bundles.rp5";
    
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
    public static final int SIMPLE_UNINSTALL_BUNDLE = 23;
    public static final int RESOURCE_PROCESSOR_CUSTOMIZER = 24;
    public static final int RESOURCE_FROM_OTHER_DP = 25;
	
    public static String getCodeName(int code) {
        switch (code) {
            case SIMPLE_DP: return "simple_dp";
            case RP_RESOURCE_INSTALL_DP: return "rp_resource_dp";
            case RP_RESOURCE_UPDATE_DP: return "rp_resource_dp";
            case RP_RESOURCE_UNINSTALL_DP: return "rp_resource_dp";
            case AUTO_CONFIG_DP: return "auto_config_dp";
            case SESSION_RESOURCE_PROCESSOR_DP: return "session_resource_processor_dp";
            case SESSION_TEST_DP: return "session_test_dp";
            case SESSION_UPDATE_TEST_DP: return "session_update_test_dp";
            case TRANSACTIONAL_SESSION_DP: return "transactional_session_dp";
            case RESOURCE_PROCESSOR_2_DP: return "resource_processor2_dp";
            case BLOCK_SESSION_RESOURCE_PROCESSOR: return "block_session";
            case MANIFEST_NOT_1ST_FILE: return "manifest_not_1st_file";
            case RP_FROM_OTHER_DP: return "rp_from_other_dp";
            case INSTALL_FAIL_DP: return "install_fail_dp";
            case RESOURCE_PROCESSOR_RP3: return "auto_config_dp";
            case BSN_DIFF_FROM_MANIFEST: return "bsn_diff_from_manifest";
            case BVERSION_DIFF_FROM_MANIFEST: return "bversion_diff_from_manifest";
            case SIMPLE_RESOURCE_RP3: return "resource_processor_rp3";
            case SIMPLE_BUNDLE_RES_DP: return "simple_bundle_res_dp";
            case SIMPLE_NO_BUNDLE_DP: return "simple_no_bundle";
            case SIMPLE_NO_RESOURCE_DP: return "simple_no_resource";
            case BUNDLE_FAIL_RES_DP: return "bundle_fail_res_dp";
            case BUNDLE_FAIL_ON_STOP: return "bundle_fail_on_stop_dp";
            case SIMPLE_UNINSTALL_BUNDLE: return "simple_uninstall_bundle";
            case RESOURCE_PROCESSOR_CUSTOMIZER: return "resource_processor_customizer";
            case RESOURCE_FROM_OTHER_DP: return "resource_from_other_dp";
        }
        return null;
    }
	public static final String DEPLOYMENT_PACKAGE_NAME_ALL = "(&(name=*))";
	public static final String BUNDLE_NAME_ALL = "(name=*)";
	public static final String DEPLOYMENT_PACKAGE_NAME0 = "(name="+getCodeName(SIMPLE_DP)+")";
	public static final String DEPLOYMENT_PACKAGE_NAME1 = "(&(name="+getCodeName(RP_RESOURCE_INSTALL_DP)+")"+"(signer=-;"+SIGNER_FILTER+"))";
	public static final String DEPLOYMENT_PACKAGE_NAME2 = "(&(name=*)(signer="+SIGNER_FILTER_WILDCARD+"))";
    public static final String DEPLOYMENT_PACKAGE_NAME3 = "(&(name=*)"+"(signer="+SIGNER_FILTER+"))";
    public static final String DEPLOYMENT_PACKAGE_NAME4 = "(&(name="+getCodeName(SIMPLE_DP)+")"+"(signer=-;"+SIGNER_FILTER_INVALID1+"))";
    public static final String DEPLOYMENT_PACKAGE_NAME5 = "(&(name="+getCodeName(SIMPLE_DP)+")"+"(signer=-;"+SIGNER_FILTER_INVALID2+"))";
	
	public static final String DEPLOYMENT_PACKAGE_DIFFERENT_SIGNATURE = "(&(name="+getCodeName(SIMPLE_DP)+")"+"(signer=-;"+"CN=CESAR.ORG.BR, OU=CIN, O=MOTOROLA, L=RECIFE, ST=PERNAMBUCO, C=BR))";
	public static final String INVALID_DEPLOYMENT_PACKAGE_NAME = "name;CESAR signer,CA_CESAR";
	public static final String RESOURCE_PROCESSOR_PROPERTY_KEY = "property.key";
    
    public static final String OSGI_DP_LOCATION = "osgi-dp:";
    public static final String MANAGED_BUNDLE_LOCATION = "BundleLocation:tb1.jar";
    
	public static final String BUNDLE_NAME_FILTER = "(name=bundle.tb1)";
	public static final String BUNDLE_NAME_WRONG_FILTER = "(&(name=bundle.tb1)(location=-;osgi-dp: bundle001.jar))";
	public static final String EXCEPTION_MESSAGE = "Unknown failure";
    
	public static final int TIMEOUT;
    public static final int SHORT_TIMEOUT;
    
    static {
    	if (System.getProperty("org.osgi.test.cases.deploymentadmin.timeout")!=null) {
    		TIMEOUT = Integer.parseInt(System.getProperty("org.osgi.test.cases.deploymentadmin.timeout"));
    	} else {
    		TIMEOUT = 180000;
    	}
    	
    	if (System.getProperty("org.osgi.test.cases.deploymentadmin.short_timeout")!=null) {
    		SHORT_TIMEOUT = Integer.parseInt(System.getProperty("org.osgi.test.cases.deploymentadmin.short_timeout"));
    	} else {
    		SHORT_TIMEOUT = 3500;
    	}
    }    
    
    public static final int FINISH=-1;
    public static final int BEGIN=0;
    public static final int PROCESS=1;
    public static final int DROPPED=2;
    public static final int DROP_ALL_RESOURCES=3;
    public static final int PREPARE=4;
    public static final int COMMIT=5;
    public static final int ROLLBACK=6;
    public static final int CANCEL=7;

}
