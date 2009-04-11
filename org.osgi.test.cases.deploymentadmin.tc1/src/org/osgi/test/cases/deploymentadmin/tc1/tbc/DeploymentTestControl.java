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
 * Oct 14, 2008  Stoyan Boshev
 *             	 Added new TCK and appropriate changes to move to BND tool  
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
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.deploymentadmin.spi.DeploymentCustomizerPermission;
import org.osgi.service.deploymentadmin.spi.ResourceProcessor;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.BundleInfo.GetSymbolicName;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.BundleInfo.GetVersion;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.Event.BundleListenerImpl;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.Event.DeploymentEventHandlerActivator;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.Event.DeploymentEventHandlerImpl;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.TestingBundle;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.TestingDeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.TestingResource;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * @author Luiz Felipe Guimaraes
 * 
 */
public class DeploymentTestControl extends DefaultTestBundleControl {

	private DeploymentAdmin deploymentAdmin;
	private PermissionAdmin permissionAdmin;
	private TB1Service tb1Srv;
	
	private ServiceReference tb1ServiceRef;
	private BundleListenerImpl bundleListener;
	private TestInterface[] testClasses;
	private String bundleLocation;
	private HashMap packages = new HashMap();
    
    private DeploymentEventHandlerImpl deploymentEventHandler;
    DeploymentEventHandlerActivator deploymentEventHandlerActivator;
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
	public void setUp() {
		log("#before each run");
		
		permissionAdmin = (PermissionAdmin) getContext().getService(getContext().getServiceReference(PermissionAdmin.class.getName()));
		ServiceReference daServiveReference = getContext().getServiceReference(DeploymentAdmin.class.getName());
		deploymentAdmin = (DeploymentAdmin) getContext().getService(daServiveReference);
		
        installHandlersAndListeners();
		try {
			installTestBundle("tb1.jar");
			tb1Srv = (TB1Service) getContext().getService(getContext().getServiceReference(TB1Service.class.getName()));
			if(tb1Srv!=null) {
				testClasses = tb1Srv.getTestClasses(this);
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail("Failed to install bundle tb1.jar");
//			log("Failed to install bundle tb1.jar");
		}

		// set this permissions to all resource processors
		//---------
		createTestingDeploymentPackages();
		setBundleServicePermissions();
        startPermissionWorker();
        //
	}
	
	public String getWebServer() {
		return super.getWebServer() + "www/";
	}
	
    /**
     * 
     */
    private void startPermissionWorker() {
        permWorker = new PermissionWorker(this);
        permWorker.start();
        //make sure the thread has started
        synchronized (permWorker) {
          if (!permWorker.isRunning()) {
            try {
              permWorker.wait();
            } catch (InterruptedException ie) {
            }
          }
        }
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
            deploymentEventHandlerActivator = new DeploymentEventHandlerActivator(deploymentEventHandler);
            deploymentEventHandlerActivator.start(getContext());
		} catch (Exception e) {
        	e.printStackTrace();
			fail("Failed starting Handles and Listeners");
//			log("#TestControl: Failed starting Handles and Listeners");
		}
	}
	
	private void uninstallHandlersAndListeners() {
		try {
            getContext().removeBundleListener(bundleListener);
            deploymentEventHandlerActivator.stop(getContext());
		} catch (Exception e) {
        	e.printStackTrace();
			fail("Failed starting Handles and Listeners");
//			log("#TestControl: Failed starting Handles and Listeners");
		}
	}
	
	/**
	 * Generate Testing Deployment Packages
	 */
	private void createTestingDeploymentPackages() {
			TestingDeploymentPackage dp = null;
			TestingBundle[] bundles = null;
			TestingResource[] resources = null;
		
			//DeploymentConstants.SIMPLE_DP
			bundles = new TestingBundle[] {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar"), new TestingBundle("bundles.tb2", "1.0", "bundle002.jar")};
			dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.SIMPLE_DP), "1.0.0", "simple.dp", bundles);
			packages.put(""+DeploymentConstants.SIMPLE_DP, dp);
			//DeploymentConstants.SIMPLE_DP_CLONE
			dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.SIMPLE_DP), "1.0.0", "simple_clone.dp", null);
			packages.put(""+DeploymentConstants.SIMPLE_DP_CLONE, dp);

			//DeploymentConstants.SIMPLE_FIX_PACK_DP
			bundles = new TestingBundle[] { new TestingBundle("bundles.tb1", "1.5", "bundle001.jar")};
			dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.SIMPLE_DP), "1.1.1", "simple_fix_pack.dp", bundles);
			packages.put(""+DeploymentConstants.SIMPLE_FIX_PACK_DP, dp);

			//DeploymentConstants.MISSING_RESOURCE_FIX_PACK_DP
			dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.SIMPLE_RESOURCE_DP), "1.1.1", "missing_resource_fix_pack.dp", null);
			packages.put(""+DeploymentConstants.MISSING_RESOURCE_FIX_PACK_DP, dp);
			//DeploymentConstants.MISSING_BUNDLE_FIX_PACK_DP
			resources = new TestingResource[]  {new TestingResource("simple_resource.xml", DeploymentConstants.PID_RESOURCE_PROCESSOR1)}; 
			dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.SIMPLE_DP), "1.1.1", "missing_bundle_fix_pack.dp", null, resources);
			packages.put(""+DeploymentConstants.MISSING_BUNDLE_FIX_PACK_DP, dp);
			//DeploymentConstants.SIMPLE_RESOURCE_PROCESSOR_DP
			bundles = new TestingBundle[] {new TestingBundle(DeploymentConstants.PID_RESOURCE_PROCESSOR1, "1.0", "rp_bundle.jar")};
			resources = new TestingResource[]  {new TestingResource("conf.txt",DeploymentConstants.PID_RESOURCE_PROCESSOR1)};
			dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.SIMPLE_RESOURCE_PROCESSOR_DP), "1.0.0", "simple_resource_processor.dp", bundles,resources);
			packages.put(""+DeploymentConstants.SIMPLE_RESOURCE_PROCESSOR_DP, dp);
			//DeploymentConstants.SIMPLE_HIGHER_MAJOR_VERSION_DP
			bundles = new TestingBundle[] { new TestingBundle("bundles.tb1", "1.0", "bundle001.jar"), new TestingBundle("bundles.tb2", "1.0", "bundle002.jar")};
			dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.SIMPLE_DP), "2.0.0", "simple_higher_major_version.dp", bundles);
			packages.put(""+DeploymentConstants.SIMPLE_HIGHER_MAJOR_VERSION_DP, dp);
			//DeploymentConstants.SIMPLE_HIGHER_MINOR_VERSION_DP
			bundles = new TestingBundle[] { new TestingBundle("bundles.tb1", "1.0", "bundle001.jar"), new TestingBundle("bundles.tb2", "1.0", "bundle002.jar")};
			dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.SIMPLE_DP), "1.1.0", "simple_higher_minor_version.dp", bundles);
			packages.put(""+DeploymentConstants.SIMPLE_HIGHER_MINOR_VERSION_DP, dp);
			//DeploymentConstants.SIMPLE_HIGHER_MICRO_VERSION_DP
			bundles = new TestingBundle[] { new TestingBundle("bundles.tb1", "1.0", "bundle001.jar"), new TestingBundle("bundles.tb2", "1.0", "bundle002.jar")};
			dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.SIMPLE_DP), "1.0.1", "simple_higher_micro_version.dp", bundles);
			packages.put(""+DeploymentConstants.SIMPLE_HIGHER_MICRO_VERSION_DP, dp);
			//DeploymentConstants.DP_CONTAINING_A_BUNDLE_FROM_OTHER_DP
			bundles = new TestingBundle[] { new TestingBundle("bundles.tb1", "1.0", "bundle001.jar"), new TestingBundle("bundles.tb3", "1.0", "bundle003.jar")};
			dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.DP_CONTAINING_A_BUNDLE_FROM_OTHER_DP), "1.0.0", "bundle_from_other.dp", bundles);
			packages.put(""+DeploymentConstants.DP_CONTAINING_A_BUNDLE_FROM_OTHER_DP, dp);
			//DeploymentConstants.DP_CONTAINING_A_BUNDLE_FROM_OTHER_DP_DIF_VERS
			bundles = new TestingBundle[] { new TestingBundle("bundles.tb1", "2.0", "bundle001.jar"), new TestingBundle("bundles.tb2", "1.5", "bundle002.jar")};
			dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.DP_CONTAINING_A_BUNDLE_FROM_OTHER_DP_DIF_VERS), "1.0.0", "bundle_from_other_dp_dif_vers.dp", bundles);
			packages.put(""+DeploymentConstants.DP_CONTAINING_A_BUNDLE_FROM_OTHER_DP_DIF_VERS, dp);
			//DeploymentConstants.SYMB_NAME_DIFFERENT_FROM_MANIFEST_DP
			bundles = new TestingBundle[] { new TestingBundle("bundle_different_name.tb1", "1.0", "bundle001.jar")};
			dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.SYMB_NAME_DIFFERENT_FROM_MANIFEST_DP), "1.0.0", "symb_name_dif_from_manifest.dp", bundles);
			packages.put(""+DeploymentConstants.SYMB_NAME_DIFFERENT_FROM_MANIFEST_DP, dp);
			//DeploymentConstants.BAD_HEADER_DP
			bundles = new TestingBundle[] { new TestingBundle("bundles.tb1", "1.0", "bundle001.jar")};
			dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.BAD_HEADER_DP), "", "bad_header.dp", bundles);
			packages.put(""+DeploymentConstants.BAD_HEADER_DP, dp);
			//DeploymentConstants.RESOURCE_PROCESSOR_DP
			bundles = new TestingBundle[] { new TestingBundle(DeploymentConstants.PID_RESOURCE_PROCESSOR1, "1.0", "rp_bundle.jar")};
			dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.RESOURCE_PROCESSOR_DP), "", "resource_processor.dp", bundles);
			packages.put(""+DeploymentConstants.RESOURCE_PROCESSOR_DP, dp);
			//DeploymentConstants.ADD_BUNDLE_FIX_PACK_DP
			bundles = new TestingBundle[] { new TestingBundle("bundles.tb1", "1.0", "bundle001.jar"), new TestingBundle("bundles.tb2", "1.0", "bundle002.jar"), new TestingBundle("bundles.tb3", "1.0", "bundle003.jar")};
			dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.SIMPLE_DP), "1.1.1", "add_bundle_fix_pack.dp", bundles);
			packages.put(""+DeploymentConstants.ADD_BUNDLE_FIX_PACK_DP, dp);
			//DeploymentConstants.FIX_PACK_HIGHER_RANGE_DP
			bundles = new TestingBundle[] { new TestingBundle("bundles.tb1", "1.5", "bundle001_higher_version.jar")};
			dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.SIMPLE_DP), "1.0", "fix_pack_higher_range.dp", bundles);
			packages.put(""+DeploymentConstants.FIX_PACK_HIGHER_RANGE_DP, dp);
			//DeploymentConstants.FIX_PACK_LOWER_RANGE_DP
			bundles = new TestingBundle[] { new TestingBundle("bundles.tb1", "1.5", "bundle001.jar")};
			dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.SIMPLE_DP), "1.0", "fix_pack_higher_range.dp", bundles);
			packages.put(""+DeploymentConstants.FIX_PACK_LOWER_RANGE_DP, dp);
			//DeploymentConstants.SIMPLE_RESOURCE_DP
			resources = new TestingResource[]  {new TestingResource("simple_resource.xml",DeploymentConstants.PID_RESOURCE_PROCESSOR1)};
			dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.SIMPLE_RESOURCE_DP), "1.0", "simple_resource.dp", null, resources);
			packages.put(""+DeploymentConstants.SIMPLE_RESOURCE_DP, dp);
			//DeploymentConstants.ADD_RESOURCE_FIX_PACK
			resources = new TestingResource[]  {new TestingResource("simple_resource.xml",DeploymentConstants.PID_RESOURCE_PROCESSOR1)};
			dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.SIMPLE_DP), "1.1.1", "add_resource_fix_pack.dp", null, resources);
			packages.put(""+DeploymentConstants.ADD_RESOURCE_FIX_PACK, dp);
			//DeploymentConstants.SIMPLE_UNSIGNED_BUNDLE_DP
			bundles = new TestingBundle[] { new TestingBundle("bundles.unsigned", "1.0", "unsignedbundle.jar")};
			dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.SIMPLE_UNSIGNED_BUNDLE_DP), "1.0", "simple_unsigned_bundle.dp", bundles);
			packages.put(""+DeploymentConstants.SIMPLE_UNSIGNED_BUNDLE_DP, dp);
			//DeploymentConstants.SIMPLE_UNSIGNED_DP
			bundles = new TestingBundle[] { new TestingBundle("bundles.tb1", "1.0", "bundle001.jar")};
			dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.SIMPLE_UNSIGNED_DP), "1.0", "simple_unsigned.dp", bundles);
			packages.put(""+DeploymentConstants.SIMPLE_UNSIGNED_DP, dp);
			//DeploymentConstants.SIMPLE_NO_BUNDLE_DP
			resources = new TestingResource[]  {new TestingResource("simple_resource.xml",DeploymentConstants.PID_RESOURCE_PROCESSOR1)};
			dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.SIMPLE_DP), "1.0", "simple_no_bundle.dp", null, resources);
			packages.put(""+DeploymentConstants.SIMPLE_NO_BUNDLE_DP, dp);
			//DeploymentConstants.SIMPLE_NO_RESOURCE_DP
			bundles = new TestingBundle[] { new TestingBundle("bundles.tb1", "1.0", "bundle001.jar")};
			dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.SIMPLE_RESOURCE_DP), "1.0", "simple_no_resource.dp", bundles);
			packages.put(""+DeploymentConstants.SIMPLE_NO_RESOURCE_DP, dp);
			//DeploymentConstants.RESOURCE_FROM_OTHER_DP
			dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.RESOURCE_FROM_OTHER_DP), "1.0", "resource_from_other.dp", null);
			packages.put(""+DeploymentConstants.RESOURCE_FROM_OTHER_DP, dp);
			//DeploymentConstants.MISSING_NAME_HEADER_DP
			bundles = new TestingBundle[] { new TestingBundle("bundles.tb1", "1.0", "bundle001.jar")};
			dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.MISSING_NAME_HEADER_DP), "1.0", "missing_name_header.dp", bundles);
			packages.put(""+DeploymentConstants.MISSING_NAME_HEADER_DP, dp);
			//DeploymentConstants.SYSTEM_DP
			bundles = new TestingBundle[] { new TestingBundle("bundles.tb1", "1.0", "bundle001.jar"), new TestingBundle("bundles.tb2", "1.0", "bundle002.jar")};
			dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.SYSTEM_DP), "1.0", "system.dp", bundles);
			packages.put(""+DeploymentConstants.SYSTEM_DP, dp);
			//DeploymentConstants.WRONG_ORDER_DP
			bundles = new TestingBundle[] { new TestingBundle("bundles.tb1", "1.0", "bundle001.jar")};
			resources = new TestingResource[]  {new TestingResource("simple_resource.xml",DeploymentConstants.PID_RESOURCE_PROCESSOR1)};
			dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.WRONG_ORDER_DP), "1.0", "wrong_order.dp", bundles, resources);
			packages.put(""+DeploymentConstants.WRONG_ORDER_DP, dp);
			//DeploymentConstants.UNTRUSTED_DP
			bundles = new TestingBundle[] { new TestingBundle("bundles.tb1", "1.0", "bundle001.jar"), new TestingBundle("bundles.tb2", "1.0", "bundle002.jar")};
			dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.UNTRUSTED_DP), "1.0", "untrusted.dp", bundles);
			packages.put(""+DeploymentConstants.UNTRUSTED_DP, dp);
			//DeploymentConstants.WRONG_PATH_DP
			bundles = new TestingBundle[] { new TestingBundle("bundles.tb1", "1.0", "bundle001.jar")};
			dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.WRONG_PATH_DP), "1.0", "wrong_path.dp", bundles);
			packages.put(""+DeploymentConstants.WRONG_PATH_DP, dp);
			//DeploymentConstants.WRONG_VERSION_DP
			bundles = new TestingBundle[] { new TestingBundle("bundles.tb1", "1.0", "bundle001.jar")};
			dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.WRONG_VERSION_DP), "1.0", "wrong_version.dp", bundles);
			packages.put(""+DeploymentConstants.WRONG_VERSION_DP, dp);
			//DeploymentConstants.SIMPLE_UNINSTALL_BUNDLE_DP
			dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.SIMPLE_DP), "1.1.1", "simple_uninstall_bundle.dp", null);
			packages.put(""+DeploymentConstants.SIMPLE_UNINSTALL_BUNDLE_DP, dp);
			//DeploymentConstants.BUNDLE_THROWS_EXCEPTION_DP
			bundles = new TestingBundle[] { new TestingBundle("bundles.tb5", "1.0", "bundle005.jar")};
			dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.BUNDLE_THROWS_EXCEPTION_DP), "1.0.0", "bundle_throws_exception.dp", bundles);
			packages.put(""+DeploymentConstants.BUNDLE_THROWS_EXCEPTION_DP, dp);
			//DeploymentConstants.BUNDLE_THROWS_EXCEPTION_STOP_DP
			bundles = new TestingBundle[] { new TestingBundle("bundles.tb6", "1.0", "bundle006.jar")};
			dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.BUNDLE_THROWS_EXCEPTION_STOP_DP), "1.0.0", "bundle_throws_exception.dp", bundles);
			packages.put(""+DeploymentConstants.BUNDLE_THROWS_EXCEPTION_STOP_DP, dp);
			//DeploymentConstants.BUNDLE_DOESNT_THROW_EXCEPTION_DP
			bundles = new TestingBundle[] { new TestingBundle("bundles.tb5", "1.0", "bundle005.jar")};
			dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.BUNDLE_THROWS_EXCEPTION_DP), "1.0.0", "bundle_doesnt_throw_exception.dp", bundles);
			packages.put(""+DeploymentConstants.BUNDLE_DOESNT_THROW_EXCEPTION_DP, dp);
			//DeploymentConstants.BLOCK_SESSION_RESOURCE_PROCESSOR
            bundles = new TestingBundle[] { new TestingBundle(DeploymentConstants.PID_RESOURCE_PROCESSOR3, "1.0", "rp_bundle4.jar")};
            resources = new TestingResource[]  {new TestingResource("conf.txt",DeploymentConstants.PID_RESOURCE_PROCESSOR3)};
            dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.BLOCK_SESSION_RESOURCE_PROCESSOR), "1.0.0", "block_session.dp", bundles, resources);
            packages.put(""+DeploymentConstants.BLOCK_SESSION_RESOURCE_PROCESSOR, dp);
            //DeploymentConstants.VERSION_DIFFERENT_FROM_MANIFEST_DP
            bundles = new TestingBundle[] { new TestingBundle("bundles.tb1","1.0", "bundle001.jar")};
            dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.VERSION_DIFFERENT_FROM_MANIFEST_DP), "1.0", "version_dif_from_manifest.dp", bundles);
            packages.put("" + DeploymentConstants.VERSION_DIFFERENT_FROM_MANIFEST_DP, dp);
            //DeploymentConstants.MISSING_B_VERSION_HEADER
            bundles = new TestingBundle[] { new TestingBundle("bundles.tb1", "1.0", "bundle001.jar")};
            dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.SIMPLE_DP), "1.0", "missing_name_header.dp", bundles);
            packages.put(""+DeploymentConstants.MISSING_B_VERSION_HEADER, dp);
            //DeploymentConstants.MISSING_BSN_HEADER
            bundles = new TestingBundle[] { new TestingBundle("bundles.tb1", "1.0", "bundle001.jar")};
            dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.SIMPLE_DP), "1.0", "missing_name_header.dp", bundles);
            packages.put(""+DeploymentConstants.MISSING_BSN_HEADER, dp);
           //DeploymentConstants.MISSING_FIX_PACK_HEADER
            bundles = new TestingBundle[] { new TestingBundle("bundles.tb1", "1.0", "bundle001.jar")};
            dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.SIMPLE_DP), "1.0", "missing_name_header.dp", bundles);
            packages.put(""+DeploymentConstants.MISSING_FIX_PACK_HEADER, dp);
            //DeploymentConstants.MISSING_RES_NAME_HEADER
            bundles = new TestingBundle[] { new TestingBundle("bundles.tb1", "1.0", "bundle001.jar")};
            dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.SIMPLE_DP), "1.0", "missing_name_header.dp", bundles);
            packages.put(""+DeploymentConstants.MISSING_RES_NAME_HEADER, dp);
           //DeploymentConstants.MISSING_VERSION_HEADER
            bundles = new TestingBundle[] { new TestingBundle("bundles.tb1", "1.0", "bundle001.jar")};
            dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.SIMPLE_DP), "1.0", "missing_version_header.dp", bundles);
            packages.put(""+DeploymentConstants.MISSING_VERSION_HEADER, dp);
           //DeploymentConstants.SIMPLE_RESOURCE_PROCESSOR_UNINSTALL
            bundles = new TestingBundle[] { new TestingBundle(DeploymentConstants.PID_RESOURCE_PROCESSOR4, "1.0", "rp_bundle4.jar")};
            dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.SIMPLE_RESOURCE_PROCESSOR_UNINSTALL), "1.0", "simple_res_proc_uninstall.dp", bundles);
            packages.put(""+DeploymentConstants.SIMPLE_RESOURCE_PROCESSOR_UNINSTALL, dp);
           //DeploymentConstants.SIMPLE_RESOURCE_UNINSTALL_DP
            resources = new TestingResource[]  {new TestingResource("conf.txt",DeploymentConstants.PID_RESOURCE_PROCESSOR4)};
            dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.SIMPLE_RESOURCE_UNINSTALL_DP), "1.0", "simple_resource_uninstall.dp", null, resources);
            packages.put(""+DeploymentConstants.SIMPLE_RESOURCE_UNINSTALL_DP, dp);
           //DeploymentConstants.WRONG_BSN
            bundles = new TestingBundle[] { new TestingBundle("bundles.tb1", "1.0", "bundle001.jar")};
            dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.WRONG_BSN), "1.0", "wrong_bsn.dp", bundles);
            packages.put(""+DeploymentConstants.WRONG_BSN, dp);
            //DeploymentConstants.WRONG_BVERSION
            bundles = new TestingBundle[] { new TestingBundle("bundles.tb1", "1.0", "bundle001.jar")};
            dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.WRONG_BVERSION), "1.0", "wrong_bversion.dp", bundles);
            packages.put(""+DeploymentConstants.WRONG_BVERSION, dp);
            //DeploymentConstants.WRONG_CUSTOMIZER
            bundles = new TestingBundle[] { new TestingBundle("bundles.tb1", "1.0", "bundle001.jar")};
            dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.WRONG_CUSTOMIZER), "1.0", "wrong_customizer.dp", bundles);
            packages.put(""+DeploymentConstants.WRONG_CUSTOMIZER, dp);
           //DeploymentConstants.WRONG_DP_MISSING
            bundles = new TestingBundle[] { new TestingBundle("bundles.tb1", "1.0", "bundle001.jar")};
            dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.WRONG_DP_MISSING), "1.0", "wrong_dp_missing.dp", bundles);
            packages.put(""+DeploymentConstants.WRONG_DP_MISSING, dp);
           //DeploymentConstants.WRONG_FIX_PACK
            bundles = new TestingBundle[] { new TestingBundle("bundles.tb1", "1.0", "bundle001.jar")};
            dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.WRONG_FIX_PACK), "1.0", "wrong_fix_pack.dp", bundles);
            packages.put(""+DeploymentConstants.WRONG_FIX_PACK, dp);
           //DeploymentConstants.WRONG_NAME
            bundles = new TestingBundle[] { new TestingBundle("bundles.tb1", "1.0", "bundle001.jar")};
            dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.WRONG_NAME), "1.0", "wrong_name.dp", bundles);
            packages.put(""+DeploymentConstants.WRONG_NAME, dp);
            //DeploymentConstants.WRONG_RP
            bundles = new TestingBundle[] { new TestingBundle("bundles.tb1", "1.0", "bundle001.jar")};
            dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.WRONG_RP), "1.0", "wrong_rp.dp", bundles);
            packages.put(""+DeploymentConstants.WRONG_RP, dp);
            //DeploymentConstants.SESSION_TEST_DP
            bundles = new TestingBundle[] { new TestingBundle("org.osgi.test.cases.deployment.bundles.rp1", "1.0", "rp_bundle.jar"), new TestingBundle("org.osgi.test.cases.deployment.bundles.rp2", "1.0", "rp_bundle2.jar")};
            resources = new TestingResource[]  {new TestingResource("simple_resource.xml","org.osgi.test.cases.deployment.bundles.rp1"), new TestingResource("conf.txt","org.osgi.test.cases.deployment.bundles.rp2")};
            dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.SESSION_TEST_DP), "1.0", "session_test.dp", bundles, resources);
            packages.put(""+DeploymentConstants.SESSION_TEST_DP, dp);
            //DeploymentConstants.RESOURCE_PROCESSOR_2_DP
            bundles = new TestingBundle[] { new TestingBundle(DeploymentConstants.PID_RESOURCE_PROCESSOR2, "1.0", "rp_bundle2.jar")};
            dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.RESOURCE_PROCESSOR_2_DP), "1.0.0", "resource_processor2.dp", bundles);
            packages.put(""+DeploymentConstants.RESOURCE_PROCESSOR_2_DP, dp);
            //DeploymentConstants.STRANGE_PATH_DP
            bundles = new TestingBundle[] { new TestingBundle("bundles.tb1", "1.0", "bundle001.jar")};
            dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.STRANGE_PATH_DP), "1.0", "strange_path.dp", bundles);
            packages.put(""+DeploymentConstants.STRANGE_PATH_DP, dp);
           //DeploymentConstants.LOCALIZED_DP
            bundles = new TestingBundle[] { new TestingBundle("bundles.tb3", "1.0", "bundle003.jar")};
            resources = new TestingResource[]  {new TestingResource("OSGi-INF/I10n/dp.properties",null)};
            dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.LOCALIZED_DP), "1.0", "localized.dp", bundles, resources);
            packages.put(""+DeploymentConstants.LOCALIZED_DP, dp);
           //DeploymentConstants.SIGNING_FILE_NOT_NEXT
        	bundles = new TestingBundle[] { new TestingBundle("bundles.tb1", "1.0", "bundle001.jar"), new TestingBundle("bundles.tb2", "1.0", "bundle002.jar")};
            dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.SIGNING_FILE_NOT_NEXT), "1.0", "signing_files_not_next.dp", bundles);
            packages.put(""+DeploymentConstants.SIGNING_FILE_NOT_NEXT, dp);
           //DeploymentConstants.NON_CUSTOMIZER_DP
            bundles = new TestingBundle[] { new TestingBundle(DeploymentConstants.PID_RESOURCE_PROCESSOR1, "1.0", "rp_bundle.jar")};
            dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.NON_CUSTOMIZER_DP), "1.0", "non_customizer_rp.dp", bundles);
            packages.put(""+DeploymentConstants.NON_CUSTOMIZER_DP, dp);
			//DeploymentConstants.SIMPLE_DP2
			bundles = new TestingBundle[] {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar")};
			dp = new TestingDeploymentPackage(DeploymentConstants.getCodeName(DeploymentConstants.SIMPLE_DP2), "1.0.0", "simple2.dp", bundles);
            packages.put(""+DeploymentConstants.SIMPLE_DP2, dp);
	}

	/**
	 * @param name
	 * @return
	 */
	public TestingDeploymentPackage getTestingDeploymentPackage(int code) {
		return (TestingDeploymentPackage)packages.get(String.valueOf(code));
	}

	public void installTestBundle(String bundle) throws Exception {
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

//	/**
//	 * <remove>Prepare for each method. It is important that each method can be
//	 * executed independently of each other method. Do not keep state between
//	 * methods, if possible. This method can be used to clean up any possible
//	 * remaining state. </remove>
//	 * 
//	 */
//	public void setState() {
//		log("#before each method");
//	}
//
//	/**
//	 * Clean up after each method. Notice that during debugging many times the
//	 * unsetState is never reached.
//	 */
//	public void unsetState() {
//		log("#after each method");
//	}

	/**
	 * Clean up after a run. Notice that during debugging many times the
	 * unprepare is never reached.
	 */
	public void tearDown() {
//		log("#after each run");
		uninstallHandlersAndListeners();
		getContext().removeBundleListener(bundleListener);
	    synchronized (permWorker) {
	    	permWorker.setRunning(false);
	    	permWorker.notifyAll();
	    }
	}

	/**
	 * @return Returns the factory.
	 */
	public DeploymentAdmin getDeploymentAdmin() {
		if (deploymentAdmin == null)
			fail("DeploymentAdmin factory is null");
		return deploymentAdmin;
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
    
    // Cancel
    public void testDeploymentAdminCancel() {
      testClasses[6].run();
    }
    
    // UninstallDeploymentPackage
    public void testInstallExceptions() {
        testClasses[7].run();
    }
    
    // BundleInfo Test Cases
    // GetSymbolicName
    public void testBundleInfoGetSymbolicName() {
        new GetSymbolicName(this).run();
    }
    
    // GetSymbolicName
    public void testBundleInfoGetVersion() {
        new GetVersion(this).run();
    }

	// DeploymentPackage Test Cases
	// Equals
	public void testDeploymentPackageEquals() {
		testClasses[8].run();
	}
	
	// GetBundle
	public void testDeploymentPackageGetBundle() {
		testClasses[9].run();
	}

	// GetBundleInfos
	public void testDeploymentPackageGetBundleInfos() {
		testClasses[10].run();
	}
	
	// GetHeader
	public void testDeploymentPackageGetHeader() {
		testClasses[11].run();
	}
	
	// GetName
	public void testDeploymentPackageGetName() {
		testClasses[12].run();
	}

	//GetResourceHeader
	public void testDeploymentPackageGetResourceHeader() {
		testClasses[13].run();
	}
	
	// GetResourceProcessor
	public void testDeploymentPackageGetResourceProcessor() {
		testClasses[14].run();
	}

	// GetResources
	public void testDeploymentPackageGetResources() {
		testClasses[15].run();
	}	
	// GetVersion
	public void testDeploymentPackageGetVersion() {
		testClasses[16].run();
	}
	// IsStale
	public void testDeploymentPackageIsStale() {
		testClasses[17].run();
	}
    //GetDisplayName
    public void testDeploymentPackageGetDisplayName() {
    	testClasses[18].run();
    }
    
    //GetIcon
    public void testDeploymentPackageGetIcon() {
    	testClasses[19].run();
    }

    //Manifest Format
    public void testManifestFormat() {
    	testClasses[20].run();
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
        	e.printStackTrace();
			fail("Failed to open the URL");
		} catch (IOException e) {
		      System.out.println("Exception occured:");
		      e.printStackTrace();
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
        	e.printStackTrace();
			fail("Failed to open the URL");
		} catch (IOException e) {
        	e.printStackTrace();
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
//				e.printStackTrace();
//				log("#Deployment Package could not be uninstalled. Uninstalling forcefully...");
				try {
                    dp.uninstallForced();
                } catch (DeploymentException e1) {
                	e1.printStackTrace();
//                    log("# Failed to uninstall deployment package: "+dp.getName());
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
    
	public void setMininumPermission() {
        PermissionInfo[] info = new PermissionInfo[] {
            new PermissionInfo(PackagePermission.class.getName(), "*", "EXPORT, IMPORT"),
            // to find the Deployment Admin
            new PermissionInfo(ServicePermission.class.getName(), "*", "GET"),
            // to load files that are passed to the Deployment Admin
            new PermissionInfo(FilePermission.class.getName(), "<<ALL FILES>>", "READ, WRITE, EXECUTE, DELETE"),
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
     * @param bundleLocation2
     * @param info
     */
    private void setAssyncPermission(PermissionInfo[] info) {
        synchronized (permWorker) {
            try {
                permWorker.setLocation(bundleLocation);
                permWorker.setPermissions(info);
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
	 * @return Returns the deploymentEventHandler.
	 */
	public DeploymentEventHandlerImpl getDeploymentEventHandler() {
        if (deploymentEventHandler == null) {
            throw new NullPointerException("Deployment Event Handler is null"); 
        }
        return deploymentEventHandler;
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
        
        return (sr!=null)? getContext().getService(sr[0]):null;
    }
    
    public DeploymentEventHandlerActivator getEventHandlerActivator() {
    	return deploymentEventHandlerActivator;
    }
}
