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

package org.osgi.test.cases.deploymentadmin.tc1.tbc;

import org.osgi.service.deploymentadmin.DeploymentAdminPermission;


/**
 * @author Andre Assad
 * 
 * Constants class to improve readability
 * 
 */
public class DeploymentConstants {
	public static int DROPALLRESOURCES_COUNT = 0;
	public static int COMMIT_COUNT = 0;
	
	public static final String SIGNER_FILTER = "CN=John Smith, O=ACME Inc, OU=ACME Cert Authority, L=Austin, ST=Texas, C=US";
	public static final String PID_RESOURCE_PROCESSOR1 = "org.osgi.test.cases.deployment.bundles.rp1";
    public static final String PID_RESOURCE_PROCESSOR2 = "org.osgi.test.cases.deployment.bundles.rp2";
    public static final String PID_RESOURCE_PROCESSOR3 = "org.osgi.test.cases.deployment.bundles.rp3";
    public static final String PID_RESOURCE_PROCESSOR4 = "org.osgi.test.cases.deployment.bundles.rp4";

	public static final int SIMPLE_DP = 0;
	public static final int SIMPLE_DP_CLONE = 1;
	public static final int SIMPLE_FIX_PACK_DP = 2;
	public static final int MISSING_RESOURCE_FIX_PACK_DP = 3;
	public static final int MISSING_BUNDLE_FIX_PACK_DP = 4;
	public static final int SIMPLE_RESOURCE_PROCESSOR_DP = 6;
	public static final int SIMPLE_HIGHER_MAJOR_VERSION_DP = 7;
	public static final int SIMPLE_HIGHER_MINOR_VERSION_DP = 8;
	public static final int SIMPLE_HIGHER_MICRO_VERSION_DP = 9;
	public static final int DP_CONTAINING_A_BUNDLE_FROM_OTHER_DP = 10;
	public static final int DP_CONTAINING_A_BUNDLE_FROM_OTHER_DP_DIF_VERS = 11;
	public static final int SYMB_NAME_DIFFERENT_FROM_MANIFEST_DP = 12;
	public static final int BAD_HEADER_DP = 13;
	public static final int RESOURCE_FROM_OTHER_DP = 14;
	public static final int RESOURCE_PROCESSOR_DP = 15;
	public static final int ADD_BUNDLE_FIX_PACK_DP = 16;
	public static final int FIX_PACK_HIGHER_RANGE_DP = 17;
	public static final int SIMPLE_RESOURCE_DP = 18;
	public static final int ADD_RESOURCE_FIX_PACK = 19;
	public static final int SIMPLE_UNSIGNED_BUNDLE_DP = 20;
	public static final int SIMPLE_UNSIGNED_DP = 21;
	public static final int SIMPLE_NO_BUNDLE_DP = 22;
	public static final int SIMPLE_NO_RESOURCE_DP = 23;
	public static final int FIX_PACK_LOWER_RANGE_DP = 24;
	public static final int MISSING_NAME_HEADER_DP = 25;
	public static final int SYSTEM_DP = 26;
	public static final int WRONG_ORDER_DP = 27;
	public static final int NON_CUSTOMIZER_DP = 28;
	public static final int UNTRUSTED_DP = 29;
	public static final int WRONG_PATH_DP = 30;
	public static final int WRONG_VERSION_DP = 31;
	public static final int SIMPLE_UNINSTALL_BUNDLE_DP = 32;
	public static final int BUNDLE_THROWS_EXCEPTION_DP = 33;
	public static final int BUNDLE_THROWS_EXCEPTION_STOP_DP = 34;
	public static final int BUNDLE_DOESNT_THROW_EXCEPTION_DP = 35;
    public static final int BLOCK_SESSION_RESOURCE_PROCESSOR = 36;
    public static final int VERSION_DIFFERENT_FROM_MANIFEST_DP = 37;
    public static final int MISSING_B_VERSION_HEADER = 38;
    public static final int MISSING_BSN_HEADER = 39;
    public static final int MISSING_FIX_PACK_HEADER = 40;
    public static final int MISSING_RES_NAME_HEADER = 41;
    public static final int MISSING_VERSION_HEADER = 42;
    public static final int SIMPLE_RESOURCE_PROCESSOR_UNINSTALL = 43;
    public static final int SIMPLE_RESOURCE_UNINSTALL_DP = 44;
    public static final int WRONG_BSN = 45;
    public static final int WRONG_BVERSION = 46;
    public static final int WRONG_CUSTOMIZER = 47;
    public static final int WRONG_DP_MISSING = 48;
    public static final int WRONG_FIX_PACK = 49;
    public static final int WRONG_NAME = 50;
    public static final int WRONG_RP = 51;
    public static final int SESSION_TEST_DP = 52;
    public static final int RESOURCE_PROCESSOR_2_DP = 53;
    public static final int STRANGE_PATH_DP = 54;
    public static final int LOCALIZED_DP = 55;
    public static final int SIGNING_FILE_NOT_NEXT = 56;
    
	public static final String DEPLOYMENT_PACKAGE_NAME_ALL = "(name=*)";
	
	public static final String BUNDLE_NAME_ALL = "(name=*)";

	public static final String DEPLOYMENT_PACKAGE_NAME0 = "(name="+getCodeName(SIMPLE_DP)+")";

	public static final String DP_HEADER_NAME              = "DeploymentPackage-SymbolicName";
    public static final String DP_HEADER_VERSION           = "DeploymentPackage-Version";
    public static final String DP_HEADER_FIXPACK           = "DeploymentPackage-FixPack";
    public static final String DP_HEADER_COPYRIGHT         = "DeploymentPackage-Copyright";
    public static final String DP_HEADER_CONTACT_ADRESS    = "DeploymentPackage-ContactAddress";
    public static final String DP_HEADER_DESCRIPTION       = "DeploymentPackage-Description";
    public static final String DP_HEADER_DOC_URL           = "DeploymentPackage-DocURL";
    public static final String DP_HEADER_CUSTOMIZER    	   = "DeploymentPackage-Customizer";
    public static final String DP_HEADER_PROCESSOR     	   = "Resource-Processor";
    public static final String DP_HEADER_MISSING       	   = "DeploymentPackage-Missing";
    public static final String DP_HEADER_VENDOR            = "DeploymentPackage-Vendor";
    public static final String DP_HEADER_LICENSE           = "DeploymentPackage-License";

    public static final String BUNDLE_HEADER_SYMB_NAME     = "Bundle-SymbolicName";
    public static final String BUNDLE_HEADER_VERSION       = "Bundle-Version";
    
    public static final String DP_MY_FIXPACK               = "[1.0,2.0]";
    public static final String DP_MY_COPYRIGHT             = "Motorola (c).";
    public static final String DP_MY_CONTACT_ADRESS        = "Rua Bione, 220, Cais do Apolo - Bairro do Recife, Recife - PE, CEP: 50.030-390.";
    public static final String DP_MY_DESCRIPTION           = "A testing deployment package.";
    public static final String DP_MY_DOC_URL               = "www.cesar.org.br.";
    public static final String DP_MY_VENDOR                = "CESAR.";
    public static final String DP_MY_LICENSE			   = "www.cesar.org.br/megtck/license.html";
    
    public static final String OSGI_DP_LOCATION = "osgi-dp:";
    
	public static final String INVALID_NAME = "invalid";
	public static final String RESOURCE_PROCESSOR_PROPERTY_KEY = "property.key";
	
	public static final String ALL_PERMISSION = DeploymentAdminPermission.CANCEL + ","
			+ DeploymentAdminPermission.INSTALL + ","
			+ DeploymentAdminPermission.LIST + ","
			+ DeploymentAdminPermission.UNINSTALL + ","
			+ DeploymentAdminPermission.UNINSTALL_FORCED + ","
            + DeploymentAdminPermission.METADATA;
	
	
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
    		SHORT_TIMEOUT = 2000;
    	}
    }
    
    public static String getDPNameFilter(String dpName) {
    	return "(name="+dpName+")";
    }
    public static String getCodeName(int code) {
        switch (code) {
            case SIMPLE_DP: return "simple_dp";
            case SIMPLE_DP_CLONE: return "simple_clone_dp";
            case SIMPLE_FIX_PACK_DP: return "simple_fix_pack_dp";
            case MISSING_RESOURCE_FIX_PACK_DP: return "missing_resource_fix_pack_dp";
            case MISSING_BUNDLE_FIX_PACK_DP: return "missing_bundle_fix_pack";
            case SIMPLE_RESOURCE_PROCESSOR_DP: return "simple_resource_processor_dp";
            case SIMPLE_HIGHER_MAJOR_VERSION_DP: return "simple_higher_major_version_dp";
            case SIMPLE_HIGHER_MINOR_VERSION_DP: return "simple_higher_minor_version_dp";
            case SIMPLE_HIGHER_MICRO_VERSION_DP: return "simple_higher_micro_version_dp";
            case DP_CONTAINING_A_BUNDLE_FROM_OTHER_DP: return "bunbundle_from_other_dp";
            case DP_CONTAINING_A_BUNDLE_FROM_OTHER_DP_DIF_VERS: return "bundle_from_other_dp_dif_vers";
            case SYMB_NAME_DIFFERENT_FROM_MANIFEST_DP: return "symb_name_dif_from_manifest_dp";
            case BAD_HEADER_DP: return "bad_header_dp";
            case RESOURCE_FROM_OTHER_DP: return "resource_from_other_dp";
            case RESOURCE_PROCESSOR_DP: return "resource_processor_dp";
            case ADD_BUNDLE_FIX_PACK_DP: return "add_bundle_fix_pack_dp";
            case FIX_PACK_HIGHER_RANGE_DP: return "fix_pack_higher_range_dp";
            case SIMPLE_RESOURCE_DP: return "simple_resource_dp";
            case ADD_RESOURCE_FIX_PACK: return "simple_dp";
            case SIMPLE_UNSIGNED_BUNDLE_DP: return "simple_unsigned_bundle_dp";
            case SIMPLE_UNSIGNED_DP: return "simple_unsigned_dp";
            case SIMPLE_NO_BUNDLE_DP: return "simple_no_bundle_dp";
            case SIMPLE_NO_RESOURCE_DP: return "simple_no_resource_dp";
            case FIX_PACK_LOWER_RANGE_DP: return "fix_pack_lower_range_dp";
            case MISSING_NAME_HEADER_DP: return "missing_name_header_dp";
            case SYSTEM_DP: return "system";
            case WRONG_ORDER_DP: return "wrong_order";
            case UNTRUSTED_DP: return "untrusted";
            case WRONG_PATH_DP: return "wrong_path";
            case WRONG_VERSION_DP: return "wrong_version";
            case SIMPLE_UNINSTALL_BUNDLE_DP: return "simple_uninstall_bundle_dp";
            case BUNDLE_THROWS_EXCEPTION_DP: return "bundle_throws_exception_dp";
            case BUNDLE_THROWS_EXCEPTION_STOP_DP: return "bundle_throws_exception_stop_dp";
            case BUNDLE_DOESNT_THROW_EXCEPTION_DP: return "bundle_doesnt_throw_exception_dp";
            case BLOCK_SESSION_RESOURCE_PROCESSOR: return "block_session";
            case VERSION_DIFFERENT_FROM_MANIFEST_DP: return "version_dif_from_manifest_dp";
            case MISSING_B_VERSION_HEADER: return "missing_b_version_header";
            case MISSING_BSN_HEADER: return "missing_bsn_header";
            case MISSING_FIX_PACK_HEADER: return "missing_fix_pack_header";
            case MISSING_RES_NAME_HEADER: return "missing_res_name_header";
            case MISSING_VERSION_HEADER: return "missing_version_header";
            case SIMPLE_RESOURCE_PROCESSOR_UNINSTALL: return "simple_res_proc_uninstall";
            case SIMPLE_RESOURCE_UNINSTALL_DP: return "simple_resource_uninstall_dp";
            case WRONG_BSN: return "wrong_bsn";
            case WRONG_BVERSION: return "wrong_bversion";
            case WRONG_CUSTOMIZER: return "wrong_customizer";
            case WRONG_DP_MISSING: return "wrong_dp_missing";
            case WRONG_FIX_PACK: return "wrong_fix_pack";
            case WRONG_NAME: return "wrong_name";
            case WRONG_RP: return "wrong_rp";
            case SESSION_TEST_DP: return "session_test_dp";
            case RESOURCE_PROCESSOR_2_DP: return "resource_processor2_dp";
            case STRANGE_PATH_DP: return "strange_path";
            case LOCALIZED_DP: return "localized";
            case SIGNING_FILE_NOT_NEXT: return "signed_file_not_next";
            case NON_CUSTOMIZER_DP: return "non_customizer_rp";
            default: return null;
        }
    }
}
