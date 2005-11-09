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

package org.osgi.test.cases.deploymentadmin.tc1.tbc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.SocketPermission;
import java.net.URL;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.PropertyPermission;
import org.osgi.framework.AdminPermission;
import org.osgi.framework.Bundle;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.PackagePermission;
import org.osgi.framework.ServicePermission;
import org.osgi.framework.ServiceReference;
import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.deploymentadmin.DeploymentAdminPermission;
import org.osgi.service.deploymentadmin.spi.DeploymentCustomizerPermission;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.deploymentadmin.spi.ResourceProcessor;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentPackage.Equals;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentPackage.GetBundle;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentPackage.GetHeader;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentPackage.GetName;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentPackage.GetResourceHeader;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentPackage.GetResourceProcessor;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentPackage.GetResources;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentPackage.GetVersion;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentPackage.IsStale;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentPackage.ManifestFormat;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentPackage.SystemDeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.Event.BundleListenerImpl;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.Event.DeploymentEventHandlerActivator;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.Event.DeploymentEventHandlerImpl;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.TestingBundle;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.TestingDeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.TestingResource;
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
	
	private ServiceReference tb1ServiceRef;
	private BundleListenerImpl bundleListener;
	private TestInterface[] testClasses;
	private String bundleLocation;
	private HashMap packages = new HashMap();
	private Dictionary managedProps;
	private Dictionary managedFactoryProps;
    
    private DeploymentEventHandlerImpl deploymentEventHandler;
    private PermissionWorker permWorker;
	
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
        
        installHandlersAndListeners();
		try {
			install("tb1.jar");
			tb1Srv = (TB1Service) getContext().getService(getContext().getServiceReference(TB1Service.class.getName()));
			if(tb1Srv!=null) {
				testClasses = tb1Srv.getTestClasses(this);
			}
		} catch (Exception e) {
			log("Failed to install bundle tb1.jar");
		}
        
		createTestingDeploymentPackages();
		setBundleServicePermissions();
        startPermissionWorker();
	}
	
    /**
     * 
     */
    private void startPermissionWorker() {
        permWorker = new PermissionWorker(this);
        permWorker.start();
    }

    /**
	 * Sets permissions to resource processors bundles installed in deployment packages
	 */
	private void setBundleServicePermissions() {
		// Activators must have ServicePermission to create a ResourceProcessor instance
		PermissionInfo info[] = {
				new PermissionInfo(DeploymentCustomizerPermission.class.getName(),
						"(name=bundles.*)",DeploymentCustomizerPermission.PRIVATEAREA),
				new PermissionInfo(ServicePermission.class.getName(), "*",
						ServicePermission.GET + ","+ ServicePermission.REGISTER),
				new PermissionInfo(AdminPermission.class.getName(), "*", "*"), 
				new PermissionInfo(PackagePermission.class.getName(), "*", "EXPORT, IMPORT"),
				new PermissionInfo(FilePermission.class.getName(), "<<ALL FILES>>", "READ, WRITE, EXECUTE, DELETE"),
				};
		
		// set this permissions to all resource processors
        setPermissionInfo(DeploymentConstants.OSGI_DP_LOCATION + DeploymentConstants.PID_RESOURCE_PROCESSOR1, info);
        setPermissionInfo(DeploymentConstants.OSGI_DP_LOCATION + DeploymentConstants.PID_RESOURCE_PROCESSOR2, info);
        setPermissionInfo(DeploymentConstants.OSGI_DP_LOCATION + DeploymentConstants.PID_RESOURCE_PROCESSOR3, info);
        setPermissionInfo(DeploymentConstants.OSGI_DP_LOCATION + DeploymentConstants.PID_RESOURCE_PROCESSOR4, info);
	}

	private void installHandlersAndListeners() {
		try {
            bundleListener = new BundleListenerImpl(this);
            getContext().addBundleListener(bundleListener);
            
            deploymentEventHandler = new DeploymentEventHandlerImpl(this);
            DeploymentEventHandlerActivator act = new DeploymentEventHandlerActivator(deploymentEventHandler);
            act.start(getContext());
		} catch (Exception e) {
			log("#TestControl: Failed starting Handles and Listeners");
		}
	}
	
	/**
	 * Generate Testing Deployment Packages
	 */
	private void createTestingDeploymentPackages() {
		TestingDeploymentPackage dp = null;
		for (int i = 0; i < DeploymentConstants.MAP_CODE_TO_DP.length; i++) {
			switch (i) {
			case DeploymentConstants.SIMPLE_DP: {
				TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar"), new TestingBundle("bundles.tb2", "1.0", "bundle002.jar")};
				dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.SIMPLE_DP], "1.0.0", "simple.dp", bundles);
				packages.put(""+i, dp);
				break;
			}
			case DeploymentConstants.SIMPLE_DP_CLONE: {
				dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.SIMPLE_DP], "1.0.0", "simple_clone.dp", null);
				packages.put(""+i, dp);
				break;
			}
			case DeploymentConstants.SIMPLE_FIX_PACK_DP: {
				TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.5", "bundle001.jar")};
				dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.SIMPLE_DP], "1.1.1", "simple_fix_pack.dp", bundles);
				packages.put(""+i, dp);
				break;
			}
			case DeploymentConstants.MISSING_RESOURCE_FIX_PACK_DP: {
				dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.SIMPLE_RESOURCE_DP], "1.1.1", "missing_resource_fix_pack.dp", null);
				packages.put(""+i, dp);
				break;
			}
			case DeploymentConstants.MISSING_BUNDLE_FIX_PACK_DP: {
				TestingResource[] resources = {new TestingResource("simple_resource.xml", DeploymentConstants.PID_RESOURCE_PROCESSOR1)}; 
				dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.SIMPLE_DP], "1.1.1", "missing_bundle_fix_pack.dp", null, resources);
				packages.put(""+i, dp);
				break;
			}
			case DeploymentConstants.SIMPLE_RESOURCE_PROCESSOR_DP: {
				TestingBundle[] bundles = {new TestingBundle(DeploymentConstants.PID_RESOURCE_PROCESSOR1, "1.0", "rp_bundle.jar")};
				TestingResource[] resources = {new TestingResource("conf.txt",DeploymentConstants.PID_RESOURCE_PROCESSOR1)};
				dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.SIMPLE_RESOURCE_PROCESSOR_DP], "1.0.0", "simple_resource_processor.dp", bundles,resources);
				packages.put(""+i, dp);
				break;
			}
			case DeploymentConstants.SIMPLE_HIGHER_MAJOR_VERSION_DP: {
				TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar"), new TestingBundle("bundles.tb2", "1.0", "bundle002.jar")};
				dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.SIMPLE_DP], "2.0.0", "simple_higher_major_version.dp", bundles);
				packages.put(""+i, dp);
				break;
			}
			case DeploymentConstants.SIMPLE_HIGHER_MINOR_VERSION_DP: {
				TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar"), new TestingBundle("bundles.tb2", "1.0", "bundle002.jar")};
				dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.SIMPLE_DP], "1.1.0", "simple_higher_minor_version.dp", bundles);
				packages.put(""+i, dp);
				break;
			}
			case DeploymentConstants.SIMPLE_HIGHER_MICRO_VERSION_DP: {
				TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar"), new TestingBundle("bundles.tb2", "1.0", "bundle002.jar")};
				dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.SIMPLE_DP], "1.0.1", "simple_higher_micro_version.dp", bundles);
				packages.put(""+i, dp);
				break;
			}
			case DeploymentConstants.DP_CONTAINING_A_BUNDLE_FROM_OTHER_DP: {
				TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar"), new TestingBundle("bundles.tb3", "1.0", "bundle003.jar")};
				dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[i], "1.0.0", "bundle_from_other.dp", bundles);
				packages.put(""+i, dp);
				break;
			}
			case DeploymentConstants.DP_CONTAINING_A_BUNDLE_FROM_OTHER_DP_DIF_VERS: {
				TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "2.0", "bundle001.jar"), new TestingBundle("bundles.tb2", "1.5", "bundle002.jar")};
				dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[i], "1.0.0", "bundle_from_other_dp_dif_vers.dp", bundles);
				packages.put(""+i, dp);
				break;
			}
			case DeploymentConstants.SYMB_NAME_DIFFERENT_FROM_MANIFEST_DP: {
				TestingBundle[] bundles = {new TestingBundle("bundle_different_name.tb1", "1.0", "bundle001.jar")};
				dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[i], "1.0.0", "symb_name_dif_from_manifest.dp", bundles);
				packages.put(""+i, dp);
				break;
			}
			case DeploymentConstants.BAD_HEADER_DP: {
				TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar")};
				dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.BAD_HEADER_DP], "", "bad_header.dp", bundles);
				packages.put(""+i, dp);
				break;
			}
			case DeploymentConstants.RESOURCE_PROCESSOR_DP: {
				TestingBundle[] bundles = {new TestingBundle(DeploymentConstants.PID_RESOURCE_PROCESSOR1, "1.0", "rp_bundle.jar")};
				dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[i], "", "resource_processor.dp", bundles);
				packages.put(""+i, dp);
				break;
			}

			case DeploymentConstants.ADD_BUNDLE_FIX_PACK_DP: {
				TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar"), new TestingBundle("bundles.tb2", "1.0", "bundle002.jar"), new TestingBundle("bundles.tb3", "1.0", "bundle003.jar")};
				dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.SIMPLE_DP], "1.1.1", "add_bundle_fix_pack.dp", bundles);
				packages.put(""+i, dp);
				break;
			}
			case DeploymentConstants.FIX_PACK_HIGHER_RANGE_DP: {
				TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.5", "bundle001_higher_version.jar")};
				dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.SIMPLE_DP], "1.0", "fix_pack_higher_range.dp", bundles);
				packages.put(""+i, dp);
				break;
			}
			case DeploymentConstants.FIX_PACK_LOWER_RANGE_DP: {
				TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.5", "bundle001.jar")};
				dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.SIMPLE_DP], "1.0", "fix_pack_higher_range.dp", bundles);
				packages.put(""+i, dp);
				break;
			}
			case DeploymentConstants.SIMPLE_RESOURCE_DP: {
				TestingResource[] resources = {new TestingResource("simple_resource.xml",DeploymentConstants.PID_RESOURCE_PROCESSOR1)};
				dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.SIMPLE_RESOURCE_DP], "1.0", "simple_resource.dp", null, resources);
				packages.put(""+i, dp);
				break;
			}
			case DeploymentConstants.ADD_RESOURCE_FIX_PACK: {
				TestingResource[] resources = {new TestingResource("simple_resource.xml",DeploymentConstants.PID_RESOURCE_PROCESSOR1)};
				dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.SIMPLE_DP], "1.1.1", "add_resource_fix_pack.dp", null, resources);
				packages.put(""+i, dp);
				break;
			}
			case DeploymentConstants.SIMPLE_UNSIGNED_BUNDLE_DP: {
				TestingBundle[] bundles = {new TestingBundle("bundles.unsigned", "1.0", "unsignedbundle.jar")};
				dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.SIMPLE_UNSIGNED_BUNDLE_DP], "1.0", "simple_unsigned_bundle.dp", bundles);
				packages.put(""+i, dp);
				break;
			}
			case DeploymentConstants.SIMPLE_UNSIGNED_DP: {
				TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar")};
				dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.SIMPLE_UNSIGNED_DP], "1.0", "simple_unsigned.dp", bundles);
				packages.put(""+i, dp);
				break;
			}
			case DeploymentConstants.SIMPLE_NO_BUNDLE_DP: {
				TestingResource[] resources = {new TestingResource("simple_resource.xml",DeploymentConstants.PID_RESOURCE_PROCESSOR1)};
				dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.SIMPLE_DP], "1.0", "simple_no_bundle.dp", null, resources);
				packages.put(""+i, dp);
				break;
			}
			case DeploymentConstants.SIMPLE_NO_RESOURCE_DP: {
				TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar")};
				dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.SIMPLE_RESOURCE_DP], "1.0", "simple_no_resource.dp", bundles);
				packages.put(""+i, dp);
				break;
			}
			case DeploymentConstants.RESOURCE_FROM_OTHER_DP: {
				dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.RESOURCE_FROM_OTHER_DP], "1.0", "resource_from_other.dp", null);
				packages.put(""+i, dp);
				break;
			}
			case DeploymentConstants.MISSING_NAME_HEADER_DP: {
				TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar")};
				dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.MISSING_NAME_HEADER_DP], "1.0", "missing_name_header.dp", bundles);
				packages.put(""+i, dp);
				break;
			}
			
			case DeploymentConstants.SYSTEM_DP: {
				TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar"), new TestingBundle("bundles.tb2", "1.0", "bundle002.jar")};
				dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.SYSTEM_DP], "1.0", "system.dp", bundles);
				packages.put(""+i, dp);
				break;
			}
			case DeploymentConstants.WRONG_ORDER_DP: {
				TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar")};
				TestingResource[] resources = {new TestingResource("simple_resource.xml",DeploymentConstants.PID_RESOURCE_PROCESSOR1)};
				dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.WRONG_ORDER_DP], "1.0", "wrong_order.dp", bundles, resources);
				packages.put(""+i, dp);
				break;
			}
			case DeploymentConstants.WRONG_FORMAT_DP: {
				TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar"), new TestingBundle("bundles.tb2", "1.0", "bundle002.jar")};
				dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.WRONG_FORMAT_DP], "1.0", "wrong_format.wrg", bundles);
				packages.put(""+i, dp);
				break;
			}
			case DeploymentConstants.UNTRUSTED_DP: {
				TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar"), new TestingBundle("bundles.tb2", "1.0", "bundle002.jar")};
				dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.UNTRUSTED_DP], "1.0", "untrusted.dp", bundles);
				packages.put(""+i, dp);
				break;
			}
			case DeploymentConstants.WRONG_PATH_DP: {
				TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar")};
				dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.WRONG_PATH_DP], "1.0", "wrong_path.dp", bundles);
				packages.put(""+i, dp);
				break;
			}
			case DeploymentConstants.WRONG_VERSION_DP: {
				TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar")};
				dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.WRONG_VERSION_DP], "1.0", "wrong_version.dp", bundles);
				packages.put(""+i, dp);
				break;
			}
			case DeploymentConstants.SIMPLE_UNINSTALL_BUNDLE_DP: {
				dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.SIMPLE_DP], "1.1.1", "simple_uninstall_bundle.dp", null);
				packages.put(""+i, dp);
				break;
			}
			case DeploymentConstants.BUNDLE_THROWS_EXCEPTION_DP: {
				TestingBundle[] bundles = {new TestingBundle("bundles.tb5", "1.0", "bundle005.jar")};
				dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.BUNDLE_THROWS_EXCEPTION_DP], "1.0.0", "bundle_throws_exception.dp", bundles);
				packages.put(""+i, dp);
				break;
			}
			case DeploymentConstants.BUNDLE_THROWS_EXCEPTION_STOP_DP: {
				TestingBundle[] bundles = {new TestingBundle("bundles.tb6", "1.0", "bundle006.jar")};
				dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.BUNDLE_THROWS_EXCEPTION_STOP_DP], "1.0.0", "bundle_throws_exception.dp", bundles);
				packages.put(""+i, dp);
				break;
			}
			case DeploymentConstants.BUNDLE_DOESNT_THROW_EXCEPTION_DP: {
				TestingBundle[] bundles = {new TestingBundle("bundles.tb5", "1.0", "bundle005.jar")};
				dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.BUNDLE_THROWS_EXCEPTION_DP], "1.0.0", "bundle_doesnt_throw_exception.dp", bundles);
				packages.put(""+i, dp);
				break;
			}
            case DeploymentConstants.BLOCK_SESSION_RESOURCE_PROCESSOR: {
                TestingBundle[] bundles = {new TestingBundle(DeploymentConstants.PID_RESOURCE_PROCESSOR3, "1.0", "rp_bundle4.jar")};
                TestingResource[] resources = {new TestingResource("conf.txt",DeploymentConstants.PID_RESOURCE_PROCESSOR3)};
                dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.BLOCK_SESSION_RESOURCE_PROCESSOR], "1.0.0", "block_session.dp", bundles, resources);
                packages.put(""+i, dp);
                break;
            }
            case DeploymentConstants.VERSION_DIFFERENT_FROM_MANIFEST_DP: {
                TestingBundle[] bundles = {new TestingBundle("bundles.tb1","1.0", "bundle001.jar")};
                dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.VERSION_DIFFERENT_FROM_MANIFEST_DP], "1.0", "version_dif_from_manifest_dp.dp", bundles);
                packages.put("" + i, dp);
                break;
            }
            case DeploymentConstants.MISSING_B_VERSION_HEADER: {
                TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar")};
                dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.SIMPLE_DP], "1.0", "missing_name_header.dp", bundles);
                packages.put(""+i, dp);
                break;
            }
            case DeploymentConstants.MISSING_BSN_HEADER: {
                TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar")};
                dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.SIMPLE_DP], "1.0", "missing_name_header.dp", bundles);
                packages.put(""+i, dp);
                break;
            }
            case DeploymentConstants.MISSING_FIX_PACK_HEADER: {
                TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar")};
                dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.SIMPLE_DP], "1.0", "missing_name_header.dp", bundles);
                packages.put(""+i, dp);
                break;
            }
            case DeploymentConstants.MISSING_RES_NAME_HEADER: {
                TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar")};
                dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.SIMPLE_DP], "1.0", "missing_name_header.dp", bundles);
                packages.put(""+i, dp);
                break;
            }
            case DeploymentConstants.MISSING_VERSION_HEADER: {
                TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar")};
                dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.SIMPLE_DP], "1.0", "missing_name_header.dp", bundles);
                packages.put(""+i, dp);
                break;
            }
            case DeploymentConstants.SIMPLE_RESOURCE_PROCESSOR_UNINSTALL: {
                TestingBundle[] bundles = {new TestingBundle(DeploymentConstants.PID_RESOURCE_PROCESSOR4, "1.0", "rp_bundle4.jar")};
                dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[i], "1.0", "simple_res_proc_uninstall.dp", bundles);
                packages.put(""+i, dp);
                break;
            }           
            case DeploymentConstants.SIMPLE_RESOURCE_UNINSTALL_DP: {
                TestingResource[] resources = {new TestingResource("conf.txt",DeploymentConstants.PID_RESOURCE_PROCESSOR4)};
                dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[i], "1.0", "simple_resource_uninstall.dp", null, resources);
                packages.put(""+i, dp);
                break;
            }
            case DeploymentConstants.WRONG_BSN: {
                TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar")};
                dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.WRONG_BSN], "1.0", "wrong_bsn.dp", bundles);
                packages.put(""+i, dp);
                break;
            }
            case DeploymentConstants.WRONG_BVERSION: {
                TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar")};
                dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.WRONG_BVERSION], "1.0", "wrong_bversion.dp", bundles);
                packages.put(""+i, dp);
                break;
            }
            case DeploymentConstants.WRONG_CUSTOMIZER: {
                TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar")};
                dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.WRONG_CUSTOMIZER], "1.0", "wrong_customizer.dp", bundles);
                packages.put(""+i, dp);
                break;
            }
            case DeploymentConstants.WRONG_DP_MISSING: {
                TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar")};
                dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.WRONG_DP_MISSING], "1.0", "wrong_dp_missing.dp", bundles);
                packages.put(""+i, dp);
                break;
            }
            case DeploymentConstants.WRONG_FIX_PACK: {
                TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar")};
                dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.WRONG_FIX_PACK], "1.0", "wrong_fix_pack.dp", bundles);
                packages.put(""+i, dp);
                break;
            }
            case DeploymentConstants.WRONG_NAME: {
                TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar")};
                dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.WRONG_NAME], "1.0", "wrong_name.dp", bundles);
                packages.put(""+i, dp);
                break;
            }
            case DeploymentConstants.WRONG_RP: {
                TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar")};
                dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.WRONG_RP], "1.0", "wrong_rp.dp", bundles);
                packages.put(""+i, dp);
                break;
            }
            case DeploymentConstants.SESSION_TEST_DP: {
                TestingBundle[] bundles = {new TestingBundle("org.osgi.test.cases.deployment.bundles.rp1", "1.0", "rp_bundle.jar"), new TestingBundle("org.osgi.test.cases.deployment.bundles.rp2", "1.0", "rp_bundle2.jar")};
                TestingResource[] resources = {new TestingResource("simple_resource.xml","org.osgi.test.cases.deployment.bundles.rp1"), new TestingResource("conf.txt","org.osgi.test.cases.deployment.bundles.rp2")};
                dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.SESSION_TEST_DP], "1.0", "session_test.dp", bundles, resources);
                packages.put(""+i, dp);
                break;
            }
            case DeploymentConstants.RESOURCE_PROCESSOR_2_DP: {
                TestingBundle[] bundles = {new TestingBundle(DeploymentConstants.PID_RESOURCE_PROCESSOR2, "1.0", "rp_bundle2.jar")};
                dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.RESOURCE_PROCESSOR_2_DP], "1.0.0", "resource_processor2.dp", bundles);
                packages.put(""+i, dp);
                break;
            }
            case DeploymentConstants.STRANGE_PATH_DP: {
                TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar")};
                dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.STRANGE_PATH_DP], "1.0", "strange_path.dp", bundles);
                packages.put(""+i, dp);
                break;
            }
            case DeploymentConstants.LOCALIZED_DP: {
                TestingBundle[] bundles = {new TestingBundle("bundles.tb3", "1.0", "bundle003.jar")};
                TestingResource[] resources = {new TestingResource("OSGi-INF/I10n/dp.properties",null)};
                dp = new TestingDeploymentPackage(DeploymentConstants.MAP_CODE_TO_DP[DeploymentConstants.LOCALIZED_DP], "1.0", "localized.dp", bundles, resources);
                packages.put(""+i, dp);
                break;
            }
			}
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
		PermissionInfo info[] = {
				new PermissionInfo(ServicePermission.class.getName(), "*", ServicePermission.GET + ","
								+ ServicePermission.REGISTER),
				new PermissionInfo(AdminPermission.class.getName(), "*", "*"),
				new PermissionInfo(PackagePermission.class.getName(), "*", "EXPORT, IMPORT"), };

		setPermissionInfo(getWebServer() + bundle, info);
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

	// DeploymentAdmin Test Cases. Belong to tb1.jar bundle
	// InstallDeploymentPackageAPI
	public void testDeploymentAdminInstallDeploymentPackageAPI() {
		testClasses[0].run();
	}
	// InstallDeploymentPackageUseCases
	public void testDeploymentAdminInstallDeploymentPackageUseCases() {
		testClasses[1].run();
	}
	// InstallFixPack
	public void testDeploymentAdminInstallFixPack() {
		testClasses[2].run();
	}
	// ListDeploymentPackages
	public void testDeploymentAdminListDeploymentPackages() {
		testClasses[3].run();
	}
	// GetDeploymentPackage
	public void testDeploymentAdminGetDeploymentPackage() {
		testClasses[4].run();
	}
	// UninstallDeploymentPackage
	public void testDeploymentAdminUninstallDeploymentPackage() {
		testClasses[5].run();
	}
    
    // UninstallDeploymentPackage
    public void testDeploymentAdminCancel() {
//      testClasses[6].run();
    }
    
    // UninstallDeploymentPackage
    public void testInstallExceptions() {
        testClasses[7].run();
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
	
	//System Deployment Package
	public void testSystemDeploymentPackage() {
		new SystemDeploymentPackage(this).run();
	}
    
    //Manifest Format
    public void testManifestFormat() {
        new ManifestFormat(this).run();
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
    
    public DeploymentPackage installDeploymentPackageAndNotify(String urlStr) throws DeploymentException, SecurityException {
        InputStream in = null;
        URL url = null;
        try {
            url = new URL(urlStr);
            in = url.openStream();
            synchronized (this) {
                this.notifyAll();
            }
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
		URL url = null;
		try {
			url = new URL(urlStr);
			File file = getDPFile(url.openStream(), rename);
			return getDeploymentAdmin().installDeploymentPackage(file.toURL().openStream());
		} catch (MalformedURLException e) {
			fail("Failed to open the URL");
		} catch (IOException e) {
			fail("Failed to open an InputStream");
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
		OutputStream os = null;
		byte[] buffer = new byte[1024];
		int off = 0;
		try {
			os = new FileOutputStream(out);
			while ((off = in.read(buffer)) != -1) {
				os.write(buffer, 0, off);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				os.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return out;
	}
	public void uninstall(DeploymentPackage[] dps) {
		for (int i=0;i<dps.length;i++) {
			uninstall(dps[i]);
		}
	}
    
	public void uninstall(DeploymentPackage dp) {
		if ((dp != null)&&!dp.isStale()) {
			try {
				dp.uninstall();
			} catch (DeploymentException e) {
				log("#Deployment Package could not be uninstalled. Uninstalling forcefully...");
				try {
                    dp.uninstallForced();
                } catch (DeploymentException e1) {
                    log("# Failed to uninstall deployment package: "+dp.getName());
                }
			} 
		}
	}
    
	public Bundle getBundle(String name) {
		Bundle bundle = null;
		Bundle[] bundles = getContext().getBundles();
		String str = "";
		int i = 0;
		while ((bundle==null) && (i < bundles.length)) {
			str = bundles[i].getSymbolicName();
			if ((str != null) && (str.equals(name))) {
				bundle = bundles[i];
			}
			i++;
		}
		return bundle;
	}
    
    public synchronized void cleanUp(DeploymentPackage dp) {
        setDeploymentAdminPermission(
            DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL,
            DeploymentConstants.ALL_PERMISSION);
        try {
            wait(1000);
            uninstall(dp);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
    }

    /**
	 * Set the DeploymentAdminPermission for the caller
	 */
	public void setDeploymentAdminPermission(String name, String perm) {
        PermissionInfo[] info = new PermissionInfo[] {
            new PermissionInfo(PackagePermission.class.getName(), "*", "EXPORT, IMPORT"),
                // to find the Deployment Admin
                new PermissionInfo(ServicePermission.class.getName(), "*", "GET"),
                // to load files that are passed to the Deployment Admin
                new PermissionInfo(FilePermission.class.getName(), "<<ALL FILES>>", "READ, WRITE, EXECUTE, DELETE"),
                // to access deployment admin
                new PermissionInfo(DeploymentAdminPermission.class.getName(), name, perm),
                // to manipulate bundles
                new PermissionInfo(AdminPermission.class.getName(), "*", "*"),
                // to connect to director webserver
                new PermissionInfo(SocketPermission.class.getName(), "*", "accept,connect,listen,resolve"),
                // to read, write in properties whenever necessary
                new PermissionInfo(PropertyPermission.class.getName(), "*", "read,write"),
                };
        
        setAssyncPermission(info);
	}
    
    /**
     * Sets a PermissionInfo for a resource processor bundle
     * @param location
     * @param name filter
     */
    public void setCustomizerPermission(String location, String filter) {
        PermissionInfo info[] = {
                new PermissionInfo(DeploymentCustomizerPermission.class.getName(), filter,
                        DeploymentCustomizerPermission.PRIVATEAREA),
                new PermissionInfo(ServicePermission.class.getName(), "*",ServicePermission.GET + ","
                                + ServicePermission.REGISTER),
                new PermissionInfo(AdminPermission.class.getName(), "*", "*"),
                new PermissionInfo(PackagePermission.class.getName(), "*", "EXPORT, IMPORT"),
                new PermissionInfo(FilePermission.class.getName(), "<<ALL FILES>>", "READ, WRITE, EXECUTE, DELETE"), };

        setAssyncPermission(location, info);
    }
	
    /**
	 * Give's the Resource Processors the right to access a bundle's private
	 * area and set the DeploymentAdminPermission for the caller.
	 */
	public void setDeploymentAdminAndCustomizerPermission(String dpName, String DAaction, String bundleName, String customizerAction) {
        PermissionInfo[] info = new PermissionInfo[] {
            new PermissionInfo(PackagePermission.class.getName(), "*", "EXPORT, IMPORT"),
            // to find the Deployment Admin
            new PermissionInfo(ServicePermission.class.getName(), "*", "GET"),
            // to load files that are passed to the Deployment Admin
            new PermissionInfo(FilePermission.class.getName(), "<<ALL FILES>>", "READ, WRITE, EXECUTE, DELETE"),
            // to access deployment admin
            new PermissionInfo(DeploymentAdminPermission.class.getName(), dpName, DAaction),
            // to manipulate bundles
            new PermissionInfo(AdminPermission.class.getName(), "*", "*"),
            // to connect to director webserver
            new PermissionInfo(SocketPermission.class.getName(), "*", "accept,connect,listen,resolve"),
            // to read, write in properties whenever necessary
            new PermissionInfo(PropertyPermission.class.getName(), "*", "read,write"),
            // to give Resource Processors the right to access a bundle's private area
            new PermissionInfo(DeploymentCustomizerPermission.class.getName(), bundleName, customizerAction), 
            };
        
        setAssyncPermission(info);
    }
    
    /**
     * @param bundleLocation2
     * @param info
     */
    private void setAssyncPermission(PermissionInfo[] info) {
        permWorker.setLocation(bundleLocation);
        permWorker.setPermissions(info);
        synchronized (permWorker) {
            try {
                permWorker.notifyAll();
                // wait for worker to notify tha permission is set
                permWorker.wait(DeploymentConstants.TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * @param location
     * @param info
     */
    private void setAssyncPermission(String location, PermissionInfo[] info) {
        permWorker.setLocation(location);
        permWorker.setPermissions(info);
        synchronized (permWorker) {
            try {
                permWorker.notifyAll();
                // wait for worker to notify tha permission is set
                permWorker.wait(DeploymentConstants.TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Set the a PermissionInfo for a bundle location for the caller
     */
    public void setPermissionInfo(String location, PermissionInfo[] info) {
        getPermissionAdmin().setPermissions(location, info);
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
	 * @return Returns the deploymentEventHandler.
	 */
	public DeploymentEventHandlerImpl getDeploymentEventHandler() {
        if (deploymentEventHandler == null) {
            throw new NullPointerException("Deployment Event Handler is null"); 
        }
        return deploymentEventHandler;
    }

	/**
     * @return Returns the managedFactoryProps.
     */
	public Dictionary getManagedFactoryProps() {
		return managedFactoryProps;
	}
	
	/**
	 * @return Returns the managedProps.
	 */
	public Dictionary getManagedProps() {
		return managedProps;
	}
	/**
	 * @return Returns the bundleEventHandler.
	 */
	public BundleListenerImpl getBundleListener() {
		if (bundleListener==null)
			throw new NullPointerException("BundleListener implementation instance is null");
        return bundleListener;
	}
    
    /**
     * @return Returns a TestingSessionResourceProcessor instance.
     * @throws InvalidSyntaxException 
     */
    public Object getServiceInstance(String pid) throws InvalidSyntaxException {
        ServiceReference[] sr = getContext().getServiceReferences(
            ResourceProcessor.class.getName(), "(service.pid=" + pid + ")");
        
        return (sr!=null)?getContext().getService(sr[0]):null;
    }
}
