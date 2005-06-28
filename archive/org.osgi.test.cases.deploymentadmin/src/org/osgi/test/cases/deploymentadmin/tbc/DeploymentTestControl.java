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
 * Mar 11, 2005  Luiz Felipe Guimaraes
 * 26            Implement MEG TCK 
 * ===========   ==============================================================
 * Mar 30, 2005  Andre Assad
 * 26            Implement MEGTCK for the deployment RFC-88 
 * ===========   ==============================================================
 * Apr 28, 2005  Eduardo Oliveira
 * 26            Implement MEGTCK for the deployment RFC-88 
 * ===========   ==============================================================
 */

package org.osgi.test.cases.deploymentadmin.tbc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.SocketPermission;
import java.net.URL;
import java.util.HashMap;
import java.util.PropertyPermission;

import org.osgi.framework.AdminPermission;
import org.osgi.framework.Bundle;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.PackagePermission;
import org.osgi.framework.ServicePermission;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.deploymentadmin.DeploymentAdminPermission;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.deploymentadmin.ResourceProcessor;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.deploymentadmin.tbc.Configuration.Configuration;
import org.osgi.test.cases.deploymentadmin.tbc.DeploymentAdminPermission.Implies;
import org.osgi.test.cases.deploymentadmin.tbc.DeploymentException.DeploymentExceptionConstants;
import org.osgi.test.cases.deploymentadmin.tbc.DeploymentPackage.Equals;
import org.osgi.test.cases.deploymentadmin.tbc.DeploymentPackage.GetBundle;
import org.osgi.test.cases.deploymentadmin.tbc.DeploymentPackage.GetBundleSymNameVersionPairs;
import org.osgi.test.cases.deploymentadmin.tbc.DeploymentPackage.GetHeader;
import org.osgi.test.cases.deploymentadmin.tbc.DeploymentPackage.GetName;
import org.osgi.test.cases.deploymentadmin.tbc.DeploymentPackage.GetResourceHeader;
import org.osgi.test.cases.deploymentadmin.tbc.DeploymentPackage.GetResourceProcessor;
import org.osgi.test.cases.deploymentadmin.tbc.DeploymentPackage.GetResources;
import org.osgi.test.cases.deploymentadmin.tbc.DeploymentPackage.GetVersion;
import org.osgi.test.cases.deploymentadmin.tbc.DeploymentPackage.IsStale;
import org.osgi.test.cases.deploymentadmin.tbc.DeploymentSession.DeploymentSession;
import org.osgi.test.cases.deploymentadmin.tbc.Event.BundleEventHandlerActivator;
import org.osgi.test.cases.deploymentadmin.tbc.Event.BundleEventHandlerImpl;
import org.osgi.test.cases.util.DefaultTestBundleControl;

/**
 * @author Luiz Felipe Guimaraes
 * 
 */
public class DeploymentTestControl extends DefaultTestBundleControl {

	private DeploymentAdmin deploymentAdmin;
	private ResourceProcessor resourceProcessor;
	private PermissionAdmin permissionAdmin;
	private TB1Service tb1Srv;
	private BundleEventHandlerImpl bundleEventHandler;
	
	private ServiceReference tb1ServiceRef;
	private TestInterface[] testClasses;
	private String bundleLocation;
	private String daLocation;
	private HashMap packages = new HashMap();
	
	public static final String SIGNER_FILTER = "CN=MEGTCK, OU=CESAR, O=MOTOROLA, L=RECIFE, C=BR";
	public static final String PID_RESOURCE_PROCESSOR1 = "org.osgi.test.cases.deployment.bundles.rp1";
	public static final String PID_RESOURCE_PROCESSOR2 = "org.osgi.test.cases.deployment.bundles.rp2";
	public static final String PID_RESOURCE_PROCESSOR3 = "org.osgi.test.cases.deployment.bundles.rp3";
	public static final String PID_RESOURCE_PROCESSOR4 = "org.osgi.test.cases.deployment.bundles.rp4";
	public static final String PID_MANAGED_SERVICE_FACTORY = "org.osgi.test.cases.deployment.tb2";
	public static final String PID_MANAGED_SERVICE = "br.org.cesar.bundles.tb4";
	
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
	public static final int RP_RESOURCE_INSTALL_DP = 16;
	public static final int RP_RESOURCE_UPDATE_DP = 17;
	public static final int RP_RESOURCE_UNINSTALL_DP = 18;
	public static final int ADD_BUNDLE_FIX_PACK_DP = 19;
	public static final int FIX_PACK_HIGHER_RANGE_DP = 20;
	public static final int SIMPLE_RESOURCE_DP = 21;
	public static final int ADD_RESOURCE_FIX_PACK = 22;
	public static final int SIMPLE_NONSIGNED_BUNDLE_DP = 23;
	public static final int SIMPLE_NONSIGNED_DP = 24;
	public static final int SIMPLE_NO_BUNDLE_DP = 25;
	public static final int SIMPLE_NO_RESOURCE_DP = 26;
	public static final int RESOURCE_PROCESSOR_CONFIG_DP = 27;
	public static final int FIX_PACK_LOWER_RANGE_DP = 28;
	public static final int MISSING_NAME_HEADER_DP = 29;
	public static final int SESSION_RESOURCE_PROCESSOR_DP = 30;
	
	//This array index is very important, and must be the same as the deployment code
	//Use 31 characters. 
	public static final String[] MAP_CODE_TO_DP = { "simple_dp",
			"simple_clone_dp", "simple_fix_pack_dp",
			"missing_resource_fix_pack_dp", "missing_bundle_fix_pack",
			"simple_resource_processor_dp", "simple_higher_major_version_dp",
			"simple_higher_minor_version_dp", "simple_higher_micro_version_dp",
			"bundle_from_other_dp", "bundle_from_other_dp_dif_vers",
			"symb_name_dif_from_manifest_dp", "bad_header_dp",
			"resource_from_other_dp", "resource_processor_dp", "rp_bundle_dp",
			"rp_bundle_dp", "rp_bundle_dp", "add_bundle_fix_pack_dp",
			"fix_pack_higher_range_dp.jar", "simple_resource_dp",
			"add_resource_fix_pack_dp", "simple_nonsigned_bundle_dp",
			"simple_nonsigned_dp", "simple_no_bundle_dp",
			"simple_no_resource_dp", "resource_processor_config_dp",
			"fix_pack_lower_range_dp.jar", "missing_name_header_dp", "session_resource_processor_dp" };
	
	public static final String DEPLOYMENT_PACKAGE_NAME_ALL = "(name=*)";
	public static final String DEPLOYMENT_PACKAGE_NAME0 = "(name="+MAP_CODE_TO_DP[0]+")";
	public static final String DEPLOYMENT_PACKAGE_NAME1 = "(&(name="+MAP_CODE_TO_DP[1]+")"+"(signer=-;"+SIGNER_FILTER+"))";
	public static final String DEPLOYMENT_PACKAGE_NAME2 = "(&(name="+MAP_CODE_TO_DP[2]+")"+"(signer=-;"+SIGNER_FILTER+"))";
	public static final String DEPLOYMENT_PACKAGE_NAME3 = "(&(name="+MAP_CODE_TO_DP[3]+")"+"(signer=-;"+SIGNER_FILTER+"))";
	public static final String DEPLOYMENT_PACKAGE_NAME4 = "(&(name="+MAP_CODE_TO_DP[4]+")"+"(signer=-;"+SIGNER_FILTER+"))";
	public static final String DEPLOYMENT_PACKAGE_DIFFERENT_SIGNATURE = "(&(name="+MAP_CODE_TO_DP[0]+")"+"(signer=-;"+"CN=MEG TCK, OU=CIN, O=MOTOROLA, L=RECIFE, ST=PERNAMBUCO, C=BR))";
	public static final String INVALID_DEPLOYMENT_PACKAGE_NAME = "name;CESAR signer,CA_CESAR";
	
    public static final String DP_HEADER_NAME              = "DeploymentPackage-Name";
    public static final String DP_HEADER_VERSION           = "DeploymentPackage-Version";
    public static final String DP_HEADER_FIXPACK           = "DeploymentPackage-FixPack";
    public static final String DP_HEADER_COPYRIGHT         = "DeploymentPackage-Copyright";
    public static final String DP_HEADER_CONTACT_ADRESS    = "DeploymentPackage-ContactAddress";
    public static final String DP_HEADER_DESCRIPTION       = "DeploymentPackage-Description";
    public static final String DP_HEADER_DOC_URL           = "DeploymentPackage-DocURL";
    public static final String DP_HEADER_CUSTOMIZER    = "DeploymentPackage-Customizer";
    public static final String DP_HEADER_PROCESSOR     = "Resource-Processor";
    public static final String DP_HEADER_MISSING       = "DeploymentPackage-Missing";
    public static final String DP_HEADER_VENDOR            = "DeploymentPackage-Vendor";

    public static final String BUNDLE_HEADER_SYMB_NAME     = "Bundle-SymbolicName";
    public static final String BUNDLE_HEADER_VERSION       = "Bundle-Version";
    
    public static final String DP_MY_FIXPACK               = "[1.0,2.0]";
    public static final String DP_MY_COPYRIGHT             = "Motorola (c).";
    public static final String DP_MY_CONTACT_ADRESS        = "Rua Bione, 220, Cais do Apolo - Bairro do Recife, Recife - PE, CEP: 50.030-390.";
    public static final String DP_MY_DESCRIPTION           = "A testing deployment package.";
    public static final String DP_MY_DOC_URL               = "www.cesar.org.br.";
    public static final String DP_MY_VENDOR                = "CESAR.";
	
	public static final String INVALID_DEPLOYMENT_PACKAGE = "invalid.dp";
	public static final String INVALID_NAME = "invalid";
	public static final String EXCEPTION_MESSAGE = "Unknown failure";
	
	public static final String ALL_PERMISSION = DeploymentAdminPermission.ACTION_CANCEL + "," + DeploymentAdminPermission.ACTION_INSTALL + "," + DeploymentAdminPermission.ACTION_LIST + "," + DeploymentAdminPermission.ACTION_UNINSTALL+ "," + DeploymentAdminPermission.ACTION_UNINSTALL_FORCED;
	
	public static final int TIMEOUT = 5000;
	
	private DeploymentPackage rp_deployment_package = null;
	
	/**
	 * <remove>Prepare for each run. It is important that a test run is properly
	 * initialized and that each case can run standalone. To save a lot of time
	 * in debugging, clean up all possible persistent remains before the test is
	 * run. Clean up is better don in the prepare because debugging sessions can
	 * easily cause the unprepare never to be called. </remove>
	 * 
	 * @throws InvalidSyntaxException
	 */
	public void prepare() {
		log("#before each run");
		
		permissionAdmin = (PermissionAdmin) getContext().getService(getContext().getServiceReference(PermissionAdmin.class.getName()));
		ServiceReference daServiveReference = getContext().getServiceReference(DeploymentAdmin.class.getName());
		deploymentAdmin = (DeploymentAdmin) getContext().getService(daServiveReference);
		daLocation = daServiveReference.getBundle().getLocation();
		
		try {
			install("tb1.jar");
			tb1Srv = (TB1Service) getContext().getService(getContext().getServiceReference(TB1Service.class.getName()));
			if(tb1Srv!=null) {
				testClasses = tb1Srv.getTestClasses(this);
			}
		} catch (Exception e) {
			System.out.println("failed to install bundle tb1.jar");
		}
		
		createTestingDeploymentPackages();
		//install a deployment package for testing resource processors
		installResourceProcessor();
		installEventHandler();
	}
	
	private void installEventHandler() {
		try {
			bundleEventHandler = new BundleEventHandlerImpl();
			BundleEventHandlerActivator testBundleEventHandler = new BundleEventHandlerActivator(bundleEventHandler);
			testBundleEventHandler.start(getContext());
		} catch (Exception e) {
			log("#TestControl: Failed starting a Event Handler");
		}
	}
	
	/**
	 * 
	 */
	private void createTestingDeploymentPackages() {
		TestingDeploymentPackage dp = null;
		for (int i = 0; i < MAP_CODE_TO_DP.length; i++) {
			switch (i) {
			case SIMPLE_DP: {
				TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar"), new TestingBundle("bundles.tb2", "1.0", "bundle002.jar")};
				dp = new TestingDeploymentPackage(MAP_CODE_TO_DP[SIMPLE_DP], "1.0.0", "simple_dp.jar", bundles);
				packages.put(""+i, dp);
				break;
			}
			case SIMPLE_DP_CLONE: {
				dp = new TestingDeploymentPackage(MAP_CODE_TO_DP[SIMPLE_DP], "1.0.0", "simple_clone_dp.jar", null);
				packages.put(""+i, dp);
				break;
			}
			case SIMPLE_FIX_PACK_DP: {
				TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.5", "bundle001.jar")};
				dp = new TestingDeploymentPackage(MAP_CODE_TO_DP[SIMPLE_DP], "1.1.1", "simple_fix_pack_dp.jar", bundles);
				packages.put(""+i, dp);
				break;
			}
			case MISSING_RESOURCE_FIX_PACK_DP: {
				dp = new TestingDeploymentPackage(MAP_CODE_TO_DP[SIMPLE_RESOURCE_DP], "1.1.1", "missing_resource_fix_pack_dp.jar", null);
				packages.put(""+i, dp);
				break;
			}
			case MISSING_BUNDLE_FIX_PACK_DP: {
				TestingResource[] resources = {new TestingResource("simple_resource.xml", PID_RESOURCE_PROCESSOR1)}; 
				dp = new TestingDeploymentPackage(MAP_CODE_TO_DP[SIMPLE_DP], "1.1.1", "missing_bundle_fix_pack_dp.jar", null, resources);
				packages.put(""+i, dp);
				break;
			}
			case SIMPLE_RESOURCE_PROCESSOR_DP: {
				TestingBundle[] bundles = {new TestingBundle("org.osgi.test.cases.deployment.bundles.rp1", "1.0", "rp_bundle.jar")};
				TestingResource[] resources = {new TestingResource("conf.txt",PID_RESOURCE_PROCESSOR1)};
				dp = new TestingDeploymentPackage(MAP_CODE_TO_DP[SIMPLE_RESOURCE_PROCESSOR_DP], "1.0.0", "simple_resource_processor_dp.jar", bundles,resources);
				packages.put(""+i, dp);
				break;
			}
			case SIMPLE_HIGHER_MAJOR_VERSION_DP: {
				TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar"), new TestingBundle("bundles.tb2", "1.0", "bundle002.jar")};
				dp = new TestingDeploymentPackage(MAP_CODE_TO_DP[SIMPLE_DP], "2.0.0", "simple_higher_major_version_dp.jar", bundles);
				packages.put(""+i, dp);
				break;
			}
			case SIMPLE_HIGHER_MINOR_VERSION_DP: {
				TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar"), new TestingBundle("bundles.tb2", "1.0", "bundle002.jar")};
				dp = new TestingDeploymentPackage(MAP_CODE_TO_DP[SIMPLE_DP], "1.1.0", "simple_higher_minor_version_dp.jar", bundles);
				packages.put(""+i, dp);
				break;
			}
			case SIMPLE_HIGHER_MICRO_VERSION_DP: {
				TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar"), new TestingBundle("bundles.tb2", "1.0", "bundle002.jar")};
				dp = new TestingDeploymentPackage(MAP_CODE_TO_DP[SIMPLE_DP], "1.0.1", "simple_higher_micro_version_dp.jar", bundles);
				packages.put(""+i, dp);
				break;
			}
			case DP_CONTAINING_A_BUNDLE_FROM_OTHER_DP: {
				TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar"), new TestingBundle("bundles.tb3", "1.0", "bundle003.jar")};
				dp = new TestingDeploymentPackage(MAP_CODE_TO_DP[i], "1.0.0", "bundle_from_other_dp.jar", bundles);
				packages.put(""+i, dp);
				break;
			}
			case DP_CONTAINING_A_BUNDLE_FROM_OTHER_DP_DIF_VERS: {
				TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "2.0", "bundle001.jar"), new TestingBundle("bundles.tb2", "1.5", "bundle002.jar")};
				dp = new TestingDeploymentPackage(MAP_CODE_TO_DP[i], "1.0.0", "bundle_from_other_dp_dif_vers.jar", bundles);
				packages.put(""+i, dp);
				break;
			}
			case SYMB_NAME_DIFFERENT_FROM_MANIFEST_DP: {
				TestingBundle[] bundles = {new TestingBundle("bundle_different_name.tb1", "1.0", "bundle001.jar")};
				dp = new TestingDeploymentPackage(MAP_CODE_TO_DP[i], "1.0.0", "symb_name_dif_from_manifest_dp.jar", bundles);
				packages.put(""+i, dp);
				break;
			}
			case BAD_HEADER_DP: {
				TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar")};
				dp = new TestingDeploymentPackage(MAP_CODE_TO_DP[BAD_HEADER_DP], "", "bad_header_dp.jar", bundles);
				packages.put(""+i, dp);
				break;
			}
			case RESOURCE_PROCESSOR_DP: {
				TestingBundle[] bundles = {new TestingBundle(PID_RESOURCE_PROCESSOR2, "1.0", "rp_bundle2.jar")};
				dp = new TestingDeploymentPackage(MAP_CODE_TO_DP[i], "", "resource_processor_dp.jar", bundles);
				packages.put(""+i, dp);
				break;
			}
			case RP_RESOURCE_INSTALL_DP: {
				TestingBundle[] bundles = {new TestingBundle("bundles.tb3", "1.0", "bundle003.jar")};
				TestingResource[] resources = {new TestingResource("resource_processor_file.txt",PID_RESOURCE_PROCESSOR2)}; 
				dp = new TestingDeploymentPackage(MAP_CODE_TO_DP[i], "", "rp_resource_install_dp.jar", bundles,resources);
				packages.put(""+i, dp);
				break;
			}	
			case RP_RESOURCE_UPDATE_DP: {
				TestingResource[] resources = {new TestingResource("resource_processor_file.txt",PID_RESOURCE_PROCESSOR2)}; 
				dp = new TestingDeploymentPackage(MAP_CODE_TO_DP[i], "", "rp_resource_update_dp.jar", null,resources);
				packages.put(""+i, dp);
				break;
			}
			case RP_RESOURCE_UNINSTALL_DP: {
				dp = new TestingDeploymentPackage(MAP_CODE_TO_DP[i], "", "rp_resource_uninstall_dp.jar", null);
				packages.put(""+i, dp);
				break;
			}
			case ADD_BUNDLE_FIX_PACK_DP: {
				TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar"), new TestingBundle("bundles.tb2", "1.0", "bundle002.jar"), new TestingBundle("bundles.tb3", "1.0", "bundle003.jar")};
				dp = new TestingDeploymentPackage(MAP_CODE_TO_DP[SIMPLE_DP], "1.1.1", "add_bundle_fix_pack_dp.jar", bundles);
				packages.put(""+i, dp);
				break;
			}
			case FIX_PACK_HIGHER_RANGE_DP: {
				TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.5", "bundle001_higher_version.jar")};
				dp = new TestingDeploymentPackage(MAP_CODE_TO_DP[SIMPLE_DP], "1.0", "fix_pack_higher_range_dp.jar", bundles);
				packages.put(""+i, dp);
				break;
			}
			case FIX_PACK_LOWER_RANGE_DP: {
				TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.5", "bundle001_higher_version.jar")};
				dp = new TestingDeploymentPackage(MAP_CODE_TO_DP[SIMPLE_DP], "1.0", "fix_pack_higher_range_dp.jar", bundles);
				packages.put(""+i, dp);
				break;
			}
			case SIMPLE_RESOURCE_DP: {
				TestingResource[] resources = {new TestingResource("simple_resource.xml",PID_RESOURCE_PROCESSOR1)};
				dp = new TestingDeploymentPackage(MAP_CODE_TO_DP[SIMPLE_RESOURCE_DP], "1.0", "simple_resource_dp.jar", null, resources);
				packages.put(""+i, dp);
				break;
			}
			case ADD_RESOURCE_FIX_PACK: {
				TestingResource[] resources = {new TestingResource("simple_resource.xml",PID_RESOURCE_PROCESSOR1)};
				dp = new TestingDeploymentPackage(MAP_CODE_TO_DP[SIMPLE_DP], "1.1.1", "add_resource_fix_pack_dp.jar", null, resources);
				packages.put(""+i, dp);
				break;
			}
			case SIMPLE_NONSIGNED_BUNDLE_DP: {
				TestingBundle[] bundles = {new TestingBundle("bundles.unsigned", "1.0", "unsignedbundle.jar")};
				dp = new TestingDeploymentPackage(MAP_CODE_TO_DP[SIMPLE_NONSIGNED_BUNDLE_DP], "1.0", "simple_unsigned_bundle_dp.jar", bundles);
				packages.put(""+i, dp);
				break;
			}
			case SIMPLE_NONSIGNED_DP: {
				TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar")};
				dp = new TestingDeploymentPackage(MAP_CODE_TO_DP[SIMPLE_NONSIGNED_DP], "1.0", "simple_nonsigned_dp.jar", bundles);
				packages.put(""+i, dp);
				break;
			}
			case SIMPLE_NO_BUNDLE_DP: {
				TestingResource[] resources = {new TestingResource("simple_resource.xml",PID_RESOURCE_PROCESSOR1)};
				dp = new TestingDeploymentPackage(MAP_CODE_TO_DP[SIMPLE_DP], "1.0", "simple_no_bundle_dp.jar", null, resources);
				packages.put(""+i, dp);
				break;
			}
			case SIMPLE_NO_RESOURCE_DP: {
				TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar")};
				dp = new TestingDeploymentPackage(MAP_CODE_TO_DP[SIMPLE_RESOURCE_DP], "1.0", "simple_no_resource_dp.jar", bundles);
				packages.put(""+i, dp);
				break;
			}
			case RESOURCE_FROM_OTHER_DP: {
				dp = new TestingDeploymentPackage(MAP_CODE_TO_DP[RESOURCE_FROM_OTHER_DP], "1.0", "resource_from_other_dp.jar", null);
				packages.put(""+i, dp);
				break;
			}
			case RESOURCE_PROCESSOR_CONFIG_DP: {
				TestingBundle[] bundles = {new TestingBundle("org.osgi.test.cases.deployment.bundles.rp3", "1.0", "rp_bundle3.jar")};
				TestingResource[] resources = {new TestingResource("AUTOCONF.xml","org.osgi.test.cases.deployment.bundles.rp3")};
				dp = new TestingDeploymentPackage(MAP_CODE_TO_DP[RESOURCE_PROCESSOR_CONFIG_DP], "1.0", "resource_processor_config_dp.jar", bundles, resources);
				packages.put(""+i, dp);
				break;
			}
			case MISSING_NAME_HEADER_DP: {
				TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar")};
				dp = new TestingDeploymentPackage(MAP_CODE_TO_DP[MISSING_NAME_HEADER_DP], "1.0", "missing_name_header_dp.jar", bundles);
				packages.put(""+i, dp);
				break;
			}
			case SESSION_RESOURCE_PROCESSOR_DP: {
				TestingBundle[] bundles = {new TestingBundle("org.osgi.test.cases.deployment.bundles.rp4", "1.0", "rp_bundle4.jar")};
				TestingResource[] resources = {new TestingResource("simple_resource.xml","org.osgi.test.cases.deployment.bundles.rp4")};
				dp = new TestingDeploymentPackage(MAP_CODE_TO_DP[SIMPLE_DP], "1.0", "session_resource_processor_dp.jar", bundles, resources);
				packages.put(""+i, dp);
				break;
			}
			}
		}
	}

	/**
	 * Install a deployment package with a resource processor
	 */
	public void installResourceProcessor() {
		TestingDeploymentPackage testDp = getTestingDeploymentPackage(SIMPLE_RESOURCE_PROCESSOR_DP);
		setDeploymentAdminPermission(DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentAdminPermission.ACTION_INSTALL);
		try {
			rp_deployment_package = installDeploymentPackage(getWebServer()+testDp.getFilename());
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (DeploymentException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param name
	 * @return
	 */
	public TestingDeploymentPackage getTestingDeploymentPackage(int code) {
		return (TestingDeploymentPackage)packages.get(String.valueOf(code));
	}

	public void install(String bundle) throws Exception {
		installBundle(bundle);
		System.out.println("tb1 bundle installed");
		
		tb1ServiceRef = getContext().getServiceReference(TB1Service.class.getName());
		bundleLocation = (tb1ServiceRef!=null)?tb1ServiceRef.getBundle().getLocation():"";
	}

	/**
	 * <remove>Prepare for each method. It is important that each method can be
	 * executed independently of each other method. Do not keep state between
	 * methods, if possible. This method can be used to clean up any possible
	 * remaining state. </remove>
	 * 
	 */
	public void setState() {
		log("#before each method");
		setDeploymentAdminPermission(DeploymentTestControl.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentTestControl.ALL_PERMISSION);
	}
	

	/**
	 * Clean up after each method. Notice that during debugging many times the
	 * unsetState is never reached.
	 */
	public void unsetState() {
		log("#after each method");
	}

	/**
	 * Clean up after a run. Notice that during debugging many times the
	 * unprepare is never reached.
	 */
	public void unprepare() {
		log("#after each run");
		if (rp_deployment_package!=null)
			rp_deployment_package.uninstallForced();
	}

	/**
	 * @return Returns the factory.
	 */
	public DeploymentAdmin getDeploymentAdmin() {
		if (deploymentAdmin == null)
			fail("DeploymentAdmin factory is null");
		return deploymentAdmin;
	}

	/**
	 * @return Returns the resourceProcessor.
	 */
	public ResourceProcessor getResourceProcessor() {
		return resourceProcessor;
	}

	// DeploymentAdmin Test Cases
	// InstallDeploymentPackage
	public void testDeploymentAdminInstallDeploymentPackage() {
		testClasses[0].run();
	}
	
	// InstallFixPack
	public void testDeploymentAdminInstallFixPack() {
//		testClasses[1].run();
	}
	// ListDeploymentPackages
	public void testDeploymentAdminListDeploymentPackages() {
		testClasses[2].run();
	}
	// GetDeploymentPackage
	public void testDeploymentAdminGetDeploymentPackage() {
		testClasses[3].run();
	}
	
	// Configuration
	public void testConfiguration() {
		new Configuration(this).run();
	}

	//DeploymentAdminPermission Test Cases
	//TODO implement test cases for newPermissionCollection method
	public void testDeploymentAdminPermission() {
		new org.osgi.test.cases.deploymentadmin.tbc.DeploymentAdminPermission.DeploymentAdminPermission(this).run();
	}
	
	//Equals
	public void testDeploymentAdminPermissionEquals() {
		new org.osgi.test.cases.deploymentadmin.tbc.DeploymentAdminPermission.Equals(this).run();
	}
	
	//Implies
	public void testDeploymentAdminPermissionImplies() {
		new Implies(this).run();
	}
	
	// DeploymentException Test Cases
	// DeploymentException
	public void testDeploymentException() {
		new org.osgi.test.cases.deploymentadmin.tbc.DeploymentException.DeploymentException(this).run();
	}
	
	// DeploymentException Constants
	public void testDeploymentExceptionConstants() {
		new DeploymentExceptionConstants(this).run();
	}
	
	// DeploymentPackage Test Cases
	// Equals
	public void testDeploymentPackageEquals() {
		new Equals(this).run();
	}
	
	// GetBundle
	public void testDeploymentPackageGetBundle() {
		new GetBundle(this).run();
	}

	// GetBundleSymNameVersionPairs
	public void testDeploymentPackageGetBundleSymNameVersionPairs() {
		new GetBundleSymNameVersionPairs(this).run();
	}
	
	// GetHeader
	public void testDeploymentPackageGetHeader() {
		new GetHeader(this).run();
	}
	
	// IsStale
	public void testDeploymentPackageGetId() {
		new IsStale(this).run();
	}

	// GetName
	public void testDeploymentPackageGetName() {
		new GetName(this).run();
	}

	// GetResourceHeader
	public void testDeploymentPackageGetResourceHeader() {
		new GetResourceHeader(this).run();
	}
	
	// GetResourceProcessor
	public void testDeploymentPackageGetResourceProcessor() {
		//TODO installation of resource processor is failing
		new GetResourceProcessor(this).run();
	}

	// GetResources
	public void testDeploymentPackageGetResources() {
		new GetResources(this).run();
	}	
	// GetVersion
	public void testDeploymentPackageGetVersion() {
		new GetVersion(this).run();
	}
	//DeploymentSession Test Cases
	public void testDeploymentSession() {
		//TODO installation of resource processor is failing
		new DeploymentSession(this).run();
	}	
	
	// ResourceProcessor Test Cases
	public void testResourceProcessor() {
		//TODO installation of resource processor is failing
		new org.osgi.test.cases.deploymentadmin.tbc.ResourceProcessor.ResourceProcessor(this).run();
	}	

	/**
	 * @return Returns the permissionAdmin.
	 */
	public PermissionAdmin getPermissionAdmin() {
		if(permissionAdmin==null)
			throw new NullPointerException("PermissionAdmin service reference is null"); 
		return permissionAdmin;
	}
	
	public DeploymentPackage installDeploymentPackage(String urlStr) throws DeploymentException, SecurityException {
		InputStream in = null;
		URL url = null;
		try {
			url = new URL(urlStr);
			in = url.openStream();
			return getDeploymentAdmin().installDeploymentPackage(in);
		} catch (MalformedURLException e) {
			fail("Failed to open the URL");
		} catch (IOException e) {
			fail("Failed to open an InputStream");
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (Exception e1) {
				}
		}
		return null;
	}
	
	public DeploymentPackage installDeploymentPackage(String urlStr, String rename) throws DeploymentException, SecurityException {
		InputStream in = null;
		URL url = null;
		try {
			url = new URL(urlStr);
			File file = getDPFile(url.openStream(), rename);
			return getDeploymentAdmin().installDeploymentPackage(file.toURL().openStream());
		} catch (MalformedURLException e) {
			fail("Failed to open the URL");
		} catch (IOException e) {
			fail("Failed to open an InputStream");
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (Exception e1) {
				}
		}
		return null;
	}
	
	/**
	 * Gets a deployment package file from input stream
	 * @param in
	 * @return
	 */
	private File getDPFile(InputStream in, String rename) {
		File out = new File(rename);
		byte[] buffer = new byte[1024];
		int off = 0;
		try {
			OutputStream os = new FileOutputStream(out);
			while ((off = in.read(buffer)) != -1) {
				os.write(buffer, 0, off);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}		
		return out;
	}

	public void uninstall(DeploymentPackage dp) {
		if ((dp != null)&&!dp.isStale()) {
			try {
				dp.uninstall();
			} catch (DeploymentException e) {
				log("#Deployment Package could not be uninstalled. Uninstalling forcefuly...");
				dp.uninstallForced();
			}
		}
	}
	public Bundle getBundle(String name) {
		Bundle bundle = null;
		Bundle[] bundles = getContext().getBundles();
		String str = "";
		for (int i = 0; ((i < bundles.length)&&((str!=null)&&!str.equals(name))); i++) {
			str = bundles[i].getSymbolicName();
			bundle = bundles[i];
		}
		return bundle;
	}

	/**
	 * Set the DeploymentAdminPermission for the caller
	 */
	public void setDeploymentAdminPermission(String name, String perm) {
		getPermissionAdmin().setPermissions(bundleLocation,
						new PermissionInfo[] {
							new PermissionInfo(PackagePermission.class.getName(), "*", "EXPORT, IMPORT"),
								// to find the Deployment Admin
								new PermissionInfo(ServicePermission.class.getName(), "*", "GET"),
								// to load files that are passed to the Deployment Admin
								new PermissionInfo(FilePermission.class.getName(), "<<ALL FILES>>", "READ, WRITE, EXECUTE, DELETE"),
								// to access deployment admin
								new PermissionInfo(org.osgi.service.deploymentadmin.DeploymentAdminPermission.class.getName(), name, perm),
								// to manipulate bundles
								new PermissionInfo(AdminPermission.class.getName(), "*", "*"),
								// to connect to director webserver
								new PermissionInfo(SocketPermission.class.getName(), "*.br", "accept,connect,listen,resolve"),
								// to read, write in properties whenever necessary
								new PermissionInfo(PropertyPermission.class.getName(), "*", "read,write")
								});
	}
	
	/**
	 * @return Returns the tb1Srv.
	 */
	public TB1Service getTB1Service() {
		if (tb1Srv == null) {
			throw new NullPointerException("TB1Service service reference is null"); 
		}
		return tb1Srv;
	}

	/**
	 * @return Returns the testClasses.
	 */
	public TestInterface[] getTestClasses() {
		if (testClasses == null) {
			throw new NullPointerException("Test Classes instance is null"); 
		}
		return testClasses;
	}
	
	/**
	 * @return Returns the bundleEventHandler.
	 */
	public BundleEventHandlerImpl getBundleEventHandler() {
		if (bundleEventHandler==null)
			throw new NullPointerException("BundleEventHandler implementation instance is null");
		return bundleEventHandler;
	}
	
	/**
	 * @return Returns the bundleEventHandler.
	 */
	public DeploymentPackage getDeploymentPackage(String name) {
		DeploymentPackage[] dps = getDeploymentAdmin().listDeploymentPackages();
		for (int i=0; i<dps.length; i++) {
			if(name.equals(dps[i].getName())) {
				return dps[i];
			}
		}
		return null;
	}
	
	/**
	 * @return Returns TestingResourceProcessorConfigurator
	 * @throws InvalidSyntaxException 
	 */
	public TestingResourceProcessorConfigurator getResourceProcessorConfigurator() throws InvalidSyntaxException {
		return (TestingResourceProcessorConfigurator) getContext().getService(
				getContext().getServiceReferences(
						ResourceProcessor.class.getName(),
						"(service.pid=" + PID_RESOURCE_PROCESSOR3 + ")")[0]);
	}
	
	/**
	 * @return Returns a TestingManagedServiceFactory
	 * @throws InvalidSyntaxException 
	 */
	public TestingManagedServiceFactory getManagedServiceFactory() throws InvalidSyntaxException {
		return (TestingManagedServiceFactory) getContext().getService(
				getContext().getServiceReferences(
						ManagedServiceFactory.class.getName(),
						"(service.pid=" + PID_MANAGED_SERVICE_FACTORY + ")")[0]);
	}

	/**
	 * @return Returns a TestingManagedService
	 * @throws InvalidSyntaxException 
	 */
	public TestingManagedService getManagedService() throws InvalidSyntaxException {
		return (TestingManagedService) getContext().getService(
				getContext().getServiceReferences(
						ManagedService.class.getName(),
						"(service.pid=" + PID_MANAGED_SERVICE + ")")[0]);
	}
}
