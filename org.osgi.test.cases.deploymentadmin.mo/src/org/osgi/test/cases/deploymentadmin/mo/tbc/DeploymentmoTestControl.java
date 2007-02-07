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
 * 24/05/2005   Eduardo Oliveira
 * 97           Implement MEGTCK for the DeploymentMO Spec 
 * ===========  ==============================================================
 */

package org.osgi.test.cases.deploymentadmin.mo.tbc;


import info.dmtree.Acl;
import info.dmtree.DmtAdmin;
import info.dmtree.DmtData;
import info.dmtree.DmtException;
import info.dmtree.DmtSession;
import info.dmtree.Uri;
import info.dmtree.notification.AlertItem;
import info.dmtree.security.DmtPermission;
import info.dmtree.security.DmtPrincipalPermission;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.SocketPermission;
import java.net.URL;
import java.util.HashMap;
import java.util.PropertyPermission;
import java.util.jar.JarFile;

import org.osgi.framework.AdminPermission;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.PackagePermission;
import org.osgi.framework.ServicePermission;
import org.osgi.framework.ServiceReference;
import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.deploymentadmin.DeploymentAdminPermission;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.deploymentadmin.spi.DeploymentCustomizerPermission;
import org.osgi.service.event.TopicPermission;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.deploymentadmin.mo.tbc.Areas.DeliveredSubtree;
import org.osgi.test.cases.deploymentadmin.mo.tbc.Areas.DeployedSubtree;
import org.osgi.test.cases.deploymentadmin.mo.tbc.Areas.DownloadSubtree;
import org.osgi.test.cases.deploymentadmin.mo.tbc.Areas.PermanentNodes;
import org.osgi.test.cases.deploymentadmin.mo.tbc.RemoteAlertSender.RemoteAlertSenderImpl;
import org.osgi.test.cases.deploymentadmin.mo.tbc.util.BundleListenerImpl;
import org.osgi.test.cases.deploymentadmin.mo.tbc.util.TestingArtifact;
import org.osgi.test.cases.deploymentadmin.mo.tbc.util.TestingBundle;
import org.osgi.test.cases.deploymentadmin.mo.tbc.util.TestingDeploymentPackage;
import org.osgi.test.cases.deploymentadmin.mo.tbc.util.TestingDlota;
import org.osgi.test.cases.deploymentadmin.mo.tbc.util.TestingResource;
import org.osgi.test.cases.util.DefaultTestBundleControl;

public class DeploymentmoTestControl extends DefaultTestBundleControl {

    private DmtAdmin da;
    
    private PermissionAdmin permissionAdmin;

    private TestInterface[] testClasses;
    
    private TB1Service tb1Srv;
    
    private boolean receivedAlert = false;

	private int code;

	private AlertItem alert;
	private DeploymentAdmin deploymentAdmin;
    
    private HashMap artifacts = new HashMap();
    private HashMap packages = new HashMap();
    private HashMap bundles = new HashMap();

    private BundleListenerImpl listener;

    public boolean receivedAlert() {
        return receivedAlert;
    }
 
    public void setReceivedAlert(boolean receivedAlert) {
        this.receivedAlert = receivedAlert;
    }
    
	public void prepare() throws Exception {
		BundleContext bc = getContext();

        installRemoteAlertSender();
		
		da = (DmtAdmin) bc.getService(bc.getServiceReference(DmtAdmin.class.getName()));
		permissionAdmin = (PermissionAdmin) bc.getService(bc.getServiceReference("org.osgi.service.permissionadmin.PermissionAdmin"));
		
		deploymentAdmin = (DeploymentAdmin) bc.getService(bc.getServiceReference(DeploymentAdmin.class.getName()));

		try {
			installBundle("tb1.jar");
		} catch (Exception e) {
			log("# Failed to install bundle tb1");
		}
		ServiceReference tb1Ref = bc.getServiceReference(TB1Service.class.getName());
		
        DeploymentmoConstants.LOCATION = tb1Ref.getBundle().getLocation();
        
        File file = bc.getDataFile("");
        DeploymentmoConstants.DLOTA_RELATIVE_PATH = file.getPath();
        DeploymentmoConstants.DLOTA_PATH = "file:///" + file.getAbsolutePath() + "//";
        
		tb1Srv = (TB1Service) bc.getService(tb1Ref);

		if (tb1Srv != null) {
			testClasses = tb1Srv.getTestClasses(this);
		}
		
        setPermissions(new PermissionInfo[] {});
        setBundlesPermissions();
        addBundleListener();
		DeploymentmoConstants.SERVER = getWebServer();
        
        generateArtifacts();
        mangleDPNames();
        
        try {
            //Sets all Acls for all principals, so METADATA_MISMATCH is not thrown.
            DmtSession session = getDmtAdmin().getSession(".");
            if (session.isNodeUri(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST)) {
            	session.deleteNode(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST);
            }
            session.setNodeAcl(DeploymentmoConstants.DEPLOYMENT,new Acl(new String[] {"*"}, new int[] {Acl.ALL_PERMISSION }));
            session.close();
        } catch (Exception e) {
			log("# Failed to set the acl");
        }
        
        for (int i=0;i<DeploymentmoConstants.MAP_CODE_TO_ARTIFACT.length;i++){
            String fileName = DeploymentmoConstants.MAP_CODE_TO_ARTIFACT[i];
            //Download failed does not use a deployment package, it uses a dlota containing an invalid url
            if (!"download_failed".equals(fileName)) { 
                if (fileName.indexOf(".")<0){
                    fileName = fileName+".dp";
                  }
                  try{
                    copyArtifact(fileName, ""+DeploymentmoConstants.DELIVERED_AREA);
                  }catch(Exception ex){
                    System.out.println(fileName+" not copied into delivered!");
                  }
            }
          }
        DeploymentmoConstants.init();

	}
    
    /**
    *
    */
   private void setBundlesPermissions() {
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
       setPermissionInfo(DeploymentmoConstants.OSGI_DP_LOCATION + DeploymentmoConstants.PID_RESOURCE_PROCESSOR1, info);
       setPermissionInfo(DeploymentmoConstants.OSGI_DP_LOCATION + DeploymentmoConstants.PID_RESOURCE_PROCESSOR2, info);
       setPermissionInfo(DeploymentmoConstants.OSGI_DP_LOCATION + DeploymentmoConstants.PID_RESOURCE_PROCESSOR3, info);
       setPermissionInfo(DeploymentmoConstants.OSGI_DP_LOCATION + DeploymentmoConstants.PID_RESOURCE_PROCESSOR4, info);
   }

   /**
    * @param string
    * @param info
    */
   private void setPermissionInfo(String location, PermissionInfo[] info) {
       getPermissionAdmin().setPermissions(location, info);
       
   }
   
   /**
    * 
    */
   private void addBundleListener() {
       listener = new BundleListenerImpl(this);
   }

    /**
     * 
     */
    private void generateArtifacts() {
        TestingArtifact artifact = null;
        for (int i = 0; i < DeploymentmoConstants.MAP_CODE_TO_ARTIFACT.length; i++) {
            switch (i) {
            case DeploymentmoConstants.SIMPLE_DP: {
                TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar"), new TestingBundle("bundles.tb2", "1.0", "bundle002.jar")};
                TestingDeploymentPackage dp = new TestingDeploymentPackage(DeploymentmoConstants.MAP_CODE_TO_ARTIFACT[DeploymentmoConstants.SIMPLE_DP], "1.0.0", "simple.dp", bundles);
                packages.put(""+i, dp);
                //
                artifact = new TestingArtifact(new TestingDlota("simple_dp.xml", DeploymentmoConstants.SERVER
                            + "www/" + dp.getFilename(), 4768, DeploymentmoConstants.ENVIRONMENT_DP), dp);
                artifacts.put(""+i, artifact);
                break;
            }
            case DeploymentmoConstants.DP_CONTAINING_A_BUNDLE_FROM_OTHER_DP: {
                TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar"), new TestingBundle("bundles.tb3", "1.0", "bundle003.jar")};
                TestingDeploymentPackage dp = new TestingDeploymentPackage(
                        DeploymentmoConstants.MAP_CODE_TO_ARTIFACT[i], "1.0.0","bundle_from_other.dp", bundles);
                packages.put(""+i, dp);
                //
                artifact = new TestingArtifact(new TestingDlota("bundle_from_other_dp.xml",
                        DeploymentmoConstants.SERVER + "www/" +  dp.getFilename(), 4608,
                        DeploymentmoConstants.ENVIRONMENT_DP), dp);
                artifacts.put(""+i, artifact);
                break;
            }
           
            case DeploymentmoConstants.MISSING_NAME_HEADER_DP: {
                TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar")};
                TestingDeploymentPackage dp = new TestingDeploymentPackage(
                        DeploymentmoConstants.MAP_CODE_TO_ARTIFACT[i],
                        "1.0", "missing_name_header.dp", bundles);
                packages.put(""+i, dp);
                //
                artifact = new TestingArtifact(new TestingDlota("missing_name_header.xml", 
                		DeploymentmoConstants.SERVER + "www/" +  dp.getFilename(), 3090, DeploymentmoConstants.ENVIRONMENT_DP), dp);
                artifacts.put(""+i, artifact);
                break;
            }
            case DeploymentmoConstants.SIMPLE_NO_BUNDLE_DP: {
                TestingDeploymentPackage dp = new TestingDeploymentPackage(
                        DeploymentmoConstants.MAP_CODE_TO_ARTIFACT[DeploymentmoConstants.SIMPLE_DP],
                        "1.0", "simple_no_bundle.dp", null);
                packages.put(""+i, dp);
                //
                artifact = new TestingArtifact(new TestingDlota("simple_no_bundle.xml", DeploymentmoConstants.SERVER
                    + "www/" +  dp.getFilename(), 1881, DeploymentmoConstants.ENVIRONMENT_DP), dp);
                artifacts.put(""+i, artifact);
                break;
            }
			case DeploymentmoConstants.SYMB_NAME_DIFFERENT_FROM_MANIFEST_DP: {
				TestingBundle[] bundles = {new TestingBundle("bundle_different_name.tb1", "1.0", "bundle001.jar")};
				TestingDeploymentPackage dp = new TestingDeploymentPackage(DeploymentmoConstants.MAP_CODE_TO_ARTIFACT[i], "1.0.0", "symb_name_dif_from_manifest.dp", bundles);
				packages.put(""+i, dp);
				
                artifact = new TestingArtifact(new TestingDlota("symb_name_different_from_manifest.xml", DeploymentmoConstants.SERVER
                        + "www/" +  dp.getFilename(), 3123, DeploymentmoConstants.ENVIRONMENT_DP), dp);
                    artifacts.put(""+i, artifact);
				break;
			}

            case DeploymentmoConstants.SIMPLE_NO_RESOURCE_DP: {
                TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar")};
                TestingDeploymentPackage dp = new TestingDeploymentPackage(
                        DeploymentmoConstants.MAP_CODE_TO_ARTIFACT[DeploymentmoConstants.SIMPLE_NO_RESOURCE_DP],
                        "1.0", "simple_no_resource.dp", bundles);
                packages.put(""+i, dp);
                //
                artifact = new TestingArtifact(new TestingDlota("simple_no_resource.xml", DeploymentmoConstants.SERVER
                    + "www/" +  dp.getFilename(), 3108, DeploymentmoConstants.ENVIRONMENT_DP), dp);
                artifacts.put(""+i, artifact);
                break;
            }
            case DeploymentmoConstants.DOWNLOAD_FAILED_DP: {
                TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar"), new TestingBundle("bundles.tb2", "1.0", "bundle002.jar")};
                TestingDeploymentPackage dp = new TestingDeploymentPackage(
                        DeploymentmoConstants.MAP_CODE_TO_ARTIFACT[DeploymentmoConstants.SIMPLE_DP],"1.0.0", "simple.dp", bundles);
                packages.put(""+i, dp);
                //
                artifact = new TestingArtifact(new TestingDlota("download_failed.xml",
                        "neverfound.com/artifact.dp", 4768, DeploymentmoConstants.ENVIRONMENT_DP), dp);
                artifacts.put(""+i, artifact);
                break;
            }
            case DeploymentmoConstants.MANIFEST_NOT_1ST_FILE: {
                TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar"), new TestingBundle("bundles.tb2", "1.0", "bundle002.jar")};
                TestingDeploymentPackage dp = new TestingDeploymentPackage(DeploymentmoConstants.MAP_CODE_TO_ARTIFACT[DeploymentmoConstants.MANIFEST_NOT_1ST_FILE], "1.0", "manifest_not_1st_file.dp", bundles);
                packages.put(""+i, dp);
                artifact = new TestingArtifact(new TestingDlota("manifest_not_1st_file.xml",
                		DeploymentmoConstants.SERVER + "www/" +  dp.getFilename(), 5513, DeploymentmoConstants.ENVIRONMENT_DP), dp);
                artifacts.put(""+i, artifact);
                break;
            }
			case DeploymentmoConstants.FIX_PACK_LOWER_RANGE_DP: {
				TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.5", "bundle001.jar")};
				TestingDeploymentPackage dp = new TestingDeploymentPackage(DeploymentmoConstants.MAP_CODE_TO_ARTIFACT[DeploymentmoConstants.SIMPLE_DP], "1.0", "fix_pack_lower_range.dp", bundles);
				packages.put(""+i, dp);
				
                artifact = new TestingArtifact(new TestingDlota("fix_pack_lower_range.xml",
                		DeploymentmoConstants.SERVER + "www/" +  dp.getFilename(), 3134, DeploymentmoConstants.ENVIRONMENT_DP), dp);
                artifacts.put(""+i, artifact);
                break;
			}
			case DeploymentmoConstants.UNTRUSTED_DP: {
				TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.0", "bundle001.jar"),new TestingBundle("bundles.tb2", "1.0", "bundle002.jar")};
				TestingDeploymentPackage dp = new TestingDeploymentPackage(DeploymentmoConstants.MAP_CODE_TO_ARTIFACT[DeploymentmoConstants.UNTRUSTED_DP], "1.0", "untrusted.dp", bundles);
				packages.put(""+i, dp);
				
                artifact = new TestingArtifact(new TestingDlota("untrusted.xml",
                		DeploymentmoConstants.SERVER + "www/" +  dp.getFilename(), 1867, DeploymentmoConstants.ENVIRONMENT_DP), dp);
                artifacts.put(""+i, artifact);
				break;
			}
            case DeploymentmoConstants.RP_FROM_OTHER_DP: {
                TestingBundle[] bundles = {new TestingBundle(DeploymentmoConstants.PID_RESOURCE_PROCESSOR1, "1.0", "rp_bundle.jar")};
                TestingResource[] resources = {new TestingResource("conf.txt", DeploymentmoConstants.PID_RESOURCE_PROCESSOR1)};
                TestingDeploymentPackage dp = new TestingDeploymentPackage(DeploymentmoConstants.MAP_CODE_TO_ARTIFACT[DeploymentmoConstants.RP_FROM_OTHER_DP], "1.0", "rp_from_other_dp.dp", bundles, resources);
                packages.put(""+i, dp);
                
                artifact = new TestingArtifact(new TestingDlota("rp_from_other_dp.xml",
                		DeploymentmoConstants.SERVER + "www/" +  dp.getFilename(), 3626, DeploymentmoConstants.ENVIRONMENT_DP), dp);
                artifacts.put(""+i, artifact);
                break;
            }
			case DeploymentmoConstants.RESOURCE_PROCESSOR_CUSTOMIZER: {
				TestingBundle[] bundles = {
						new TestingBundle("org.osgi.test.cases.deployment.bundles.rp1","1.0", "rp_bundle.jar") };
				TestingResource[] resources = { 
						new TestingResource("simple_resource.xml", "org.osgi.test.cases.deployment.bundles.rp1") };
				TestingDeploymentPackage dp = new TestingDeploymentPackage(DeploymentmoConstants.MAP_CODE_TO_ARTIFACT[i], "1.0", "resource_processor_customizer.dp", bundles, resources);
				packages.put(""+i, dp);
				
                artifact = new TestingArtifact(new TestingDlota("resource_processor_customizer.xml",
                		DeploymentmoConstants.SERVER + "www/" +  dp.getFilename(), 3643, DeploymentmoConstants.ENVIRONMENT_DP), dp);
                artifacts.put(""+i, artifact);
				break;
			}
			
			case DeploymentmoConstants.BUNDLE_THROWS_EXCEPTION_DP: {
				TestingBundle[] bundles = {new TestingBundle("bundles.tb5", "1.0", "bundle005.jar")};
				TestingDeploymentPackage dp = new TestingDeploymentPackage(DeploymentmoConstants.MAP_CODE_TO_ARTIFACT[DeploymentmoConstants.BUNDLE_THROWS_EXCEPTION_DP], "1.0.0", "bundle_throws_exception.dp", bundles);
				packages.put(""+i, dp);
				
				artifact = new TestingArtifact(new TestingDlota("bundle_throws_exception.xml",
                		DeploymentmoConstants.SERVER + "www/" +  dp.getFilename(), 3269, DeploymentmoConstants.ENVIRONMENT_DP), dp);
                artifacts.put(""+i, artifact);
				break;
			}
            case DeploymentmoConstants.NON_CUSTOMIZER_RP: {
                TestingBundle[] bundles = {new TestingBundle("org.osgi.test.cases.deployment.bundles.rp1", "1.0", "rp_bundle.jar"), new TestingBundle("org.osgi.test.cases.deployment.bundles.rp2", "1.0", "rp_bundle2.jar")};
                TestingResource[] resources = {new TestingResource("simple_resource.xml","org.osgi.test.cases.deployment.bundles.rp1")};
                TestingDeploymentPackage dp = new TestingDeploymentPackage(DeploymentmoConstants.MAP_CODE_TO_ARTIFACT[DeploymentmoConstants.NON_CUSTOMIZER_RP], "1.0", "non_customizer_rp.dp", bundles, resources);
                packages.put(""+i, dp);
                
				artifact = new TestingArtifact(new TestingDlota("session_test.xml",
                		DeploymentmoConstants.SERVER + "www/" +  dp.getFilename(), 3627, DeploymentmoConstants.ENVIRONMENT_DP), dp);
                artifacts.put(""+i, artifact);
                break;
            }
			case DeploymentmoConstants.SIMPLE_RESOURCE_DP: {
				TestingResource[] resources = {new TestingResource("simple_resource.xml",DeploymentmoConstants.PID_RESOURCE_PROCESSOR1)};
				TestingDeploymentPackage dp = new TestingDeploymentPackage(DeploymentmoConstants.MAP_CODE_TO_ARTIFACT[DeploymentmoConstants.SIMPLE_RESOURCE_DP], "1.0", "simple_resource.dp", null, resources);
				packages.put(""+i, dp);
				
				artifact = new TestingArtifact(new TestingDlota("session_test.xml",
                		DeploymentmoConstants.SERVER + "www/" +  dp.getFilename(), 2157, DeploymentmoConstants.ENVIRONMENT_DP), dp);
                artifacts.put(""+i, artifact);
				break;
			}
			case DeploymentmoConstants.NOT_ACCEPTABLE_CONTENT: {
				TestingDeploymentPackage dp = new TestingDeploymentPackage(DeploymentmoConstants.MAP_CODE_TO_ARTIFACT[i], "1.0", "java.gif", null);
				packages.put(""+i, dp);
				
				artifact = new TestingArtifact(new TestingDlota("not_acceptable_content.xml",
                		DeploymentmoConstants.SERVER + "www/" +  dp.getFilename(), 34821, DeploymentmoConstants.ENVIRONMENT_DP), dp);
                artifacts.put(""+i, artifact);
				break;
			}
			case DeploymentmoConstants.SIMPLE_BUNDLE: {
				TestingBundle bundle = new TestingBundle("bundles.tb1", "1.0", "bundle001.jar");
				bundles.put(""+i, bundle);
				
				artifact = new TestingArtifact(new TestingDlota("simple_bundle.xml",
                		DeploymentmoConstants.SERVER + "www/" +  bundle.getFilename(), 3637, DeploymentmoConstants.ENVIRONMENT_BUNDLE), bundle);
                artifacts.put(""+i, artifact);
				break;
			}
			case DeploymentmoConstants.RP_NOT_ABLE_TO_COMMIT: {
				TestingBundle[] bundles = {new TestingBundle("org.osgi.test.cases.deployment.bundles.rp2", "1.0", "rp_bundle2.jar")};
				TestingDeploymentPackage dp = new TestingDeploymentPackage(DeploymentmoConstants.MAP_CODE_TO_ARTIFACT[i], "1.0", "rp_not_able_to_commit.dp", bundles);
				packages.put(""+i, dp);
				
				artifact = new TestingArtifact(new TestingDlota("rp_not_able_to_commit.xml",
                		DeploymentmoConstants.SERVER + "www/" +  dp.getFilename(), 3721, DeploymentmoConstants.ENVIRONMENT_DP), dp);
                artifacts.put(""+i, artifact);
				break;
			}
			case DeploymentmoConstants.RESOURCE_PROCESSOR_DP: {
				TestingBundle[] bundles = {new TestingBundle("org.osgi.test.cases.deployment.bundles.rp1", "1.0", "rp_bundle.jar")};
				TestingDeploymentPackage dp = new TestingDeploymentPackage(DeploymentmoConstants.MAP_CODE_TO_ARTIFACT[i], "1.0", "resource_processor.dp", bundles);
				packages.put(""+i, dp);
				
				artifact = new TestingArtifact(new TestingDlota("resource_processor.xml",
                		DeploymentmoConstants.SERVER + "www/" +  dp.getFilename(), 3367, DeploymentmoConstants.ENVIRONMENT_DP), dp);
                artifacts.put(""+i, artifact);
				break;
			}
			case DeploymentmoConstants.MISSING_RESOURCE_FIX_PACK: {
				TestingDeploymentPackage dp = new TestingDeploymentPackage(DeploymentmoConstants.MAP_CODE_TO_ARTIFACT[i], "1.0", "missing_resource_fix_pack.dp", null);
				packages.put(""+i, dp);
				
				artifact = new TestingArtifact(new TestingDlota("missing_resource_fix_pack.xml",
                		DeploymentmoConstants.SERVER + "www/" +  dp.getFilename(), 1665, DeploymentmoConstants.ENVIRONMENT_DP), dp);
                artifacts.put(""+i, artifact);
				break;
			}	
			case DeploymentmoConstants.MISSING_BUNDLE_FIX_PACK: {
				TestingDeploymentPackage dp = new TestingDeploymentPackage(DeploymentmoConstants.MAP_CODE_TO_ARTIFACT[i], "1.0", "missing_bundle_fix_pack.dp", null);
				packages.put(""+i, dp);
				
				artifact = new TestingArtifact(new TestingDlota("missing_bundle_fix_pack.xml",
                		DeploymentmoConstants.SERVER + "www/" +  dp.getFilename(), 1945, DeploymentmoConstants.ENVIRONMENT_DP), dp);
                artifacts.put(""+i, artifact);
				break;
			}		
            case DeploymentmoConstants.BLOCK_SESSION_RESOURCE_PROCESSOR: {
                TestingDeploymentPackage dp = new TestingDeploymentPackage(DeploymentmoConstants.MAP_CODE_TO_ARTIFACT[i], "1.0", "block_session.dp", null);
                packages.put(""+i, dp);
                
                artifact = new TestingArtifact(null, dp);
                artifacts.put(""+i, artifact);
                break;
            }    
			case DeploymentmoConstants.SIMPLE_FIX_PACK_DP: {
				TestingBundle[] bundles = {new TestingBundle("bundles.tb1", "1.5", "bundle001.jar")};
				TestingDeploymentPackage dp = new TestingDeploymentPackage(DeploymentmoConstants.MAP_CODE_TO_ARTIFACT[DeploymentmoConstants.SIMPLE_DP], "1.1.1", "simple_fix_pack.dp", bundles);
				packages.put(""+i, dp);
				
				artifact = new TestingArtifact(new TestingDlota("simple_fix_pack.xml",
                		DeploymentmoConstants.SERVER + "www/" +  dp.getFilename(), 3139, DeploymentmoConstants.ENVIRONMENT_DP), dp);
                artifacts.put(""+i, artifact);
				break;
            }
			case DeploymentmoConstants.RP_THROWS_NO_SUCH_RESOURCE: {
				TestingBundle[] bundles = {new TestingBundle("org.osgi.test.cases.deployment.bundles.rp4", "1.0", "rp_bundle4.jar")};
				TestingDeploymentPackage dp = new TestingDeploymentPackage(DeploymentmoConstants.MAP_CODE_TO_ARTIFACT[i], "1.0", "rp_throws_no_such_resource.dp", bundles);
				packages.put(""+i, dp);
				
				artifact = new TestingArtifact(new TestingDlota("rp_throws_no_such_resource.xml",
                		DeploymentmoConstants.SERVER + "www/" +  dp.getFilename(), 3397, DeploymentmoConstants.ENVIRONMENT_DP), dp);
                artifacts.put(""+i, artifact);
				break;
            }
			case DeploymentmoConstants.DP_INSTALLS_RESOURCE_FOR_RP4: {
				TestingResource[] resources = {new TestingResource("simple_resource.xml", DeploymentmoConstants.PID_RESOURCE_PROCESSOR4)};
				TestingDeploymentPackage dp = new TestingDeploymentPackage(DeploymentmoConstants.MAP_CODE_TO_ARTIFACT[i], "1.0", "dp_installs_resource_for_rp4.dp", null,resources);
				packages.put(""+i, dp);
				
				artifact = new TestingArtifact(new TestingDlota("dp_installs_resource_for_rp4.xml",
                		DeploymentmoConstants.SERVER + "www/" +  dp.getFilename(), 1896, DeploymentmoConstants.ENVIRONMENT_DP), dp);
                artifacts.put(""+i, artifact);
				break;
            }
			case DeploymentmoConstants.DP_THROWS_RESOURCE_VIOLATION: {
				TestingResource[] resources = {new TestingResource("simple_resource.xml", DeploymentmoConstants.PID_RESOURCE_PROCESSOR4)};
				TestingBundle[] bundles = {new TestingBundle("org.osgi.test.cases.deployment.bundles.rp4", "1.0", "rp_bundle4.jar")};
				TestingDeploymentPackage dp = new TestingDeploymentPackage(DeploymentmoConstants.MAP_CODE_TO_ARTIFACT[i], "1.0", "dp_throws_resource_violation.dp", bundles,resources);
				packages.put(""+i, dp);
				
				artifact = new TestingArtifact(new TestingDlota("dp_throws_resource_violation.xml",
                		DeploymentmoConstants.SERVER + "www/" +  dp.getFilename(), 3689, DeploymentmoConstants.ENVIRONMENT_DP), dp);
                artifacts.put(""+i, artifact);
				break;
            }
			case DeploymentmoConstants.DP_REMOVES_RESOURCE_FOR_RP4: {
				TestingDeploymentPackage dp = new TestingDeploymentPackage(DeploymentmoConstants.MAP_CODE_TO_ARTIFACT[DeploymentmoConstants.DP_INSTALLS_RESOURCE_FOR_RP4], "1.1.1", "dp_removes_resource_for_rp4.dp", null,null);
				packages.put(""+i, dp);
				
				artifact = new TestingArtifact(new TestingDlota("dp_removes_resource_for_rp4.xml",
                		DeploymentmoConstants.SERVER + "www/" +  dp.getFilename(), 1896, DeploymentmoConstants.ENVIRONMENT_DP), dp);
                artifacts.put(""+i, artifact);
				break;
            }
            }
        }
    }
    
    public void setPermissions(PermissionInfo[] permissions) {
            PermissionInfo[] defaults = new PermissionInfo[] {
                new PermissionInfo(PackagePermission.class.getName(), "*", "EXPORT, IMPORT"),
                    new PermissionInfo(ServicePermission.class.getName(), "*", ServicePermission.GET + ","+ ServicePermission.REGISTER),
                    new PermissionInfo(DmtPrincipalPermission.class.getName(), DeploymentmoConstants.PRINCIPAL,"*"),
                    new PermissionInfo(DmtPermission.class.getName(), DeploymentmoConstants.DEPLOYMENT + "/*", DeploymentmoConstants.ALL_DMT_PERMISSION),
                    new PermissionInfo(FilePermission.class.getName(), "<<ALL FILES>>", "read,write,execute,delete"),
                    new PermissionInfo(AdminPermission.class.getName(), "*", "*"),
                    new PermissionInfo(TopicPermission.class.getName(),"org/osgi/service/dmt/*", TopicPermission.PUBLISH),
                    new PermissionInfo(SocketPermission.class.getName(), "*", "accept,connect,listen,resolve"),
                    new PermissionInfo(DeploymentAdminPermission.class.getName(), "(name=*)", "install,uninstall,metadata"),
                    new PermissionInfo(PropertyPermission.class.getName(), "*", "read,write")
            };
            int size = permissions.length + defaults.length;
            
            PermissionInfo[] permission = new PermissionInfo[size];
            System.arraycopy(defaults, 0, permission, 0, defaults.length);
            System.arraycopy(permissions, 0, permission, defaults.length, permissions.length);

            getPermissionAdmin().setPermissions(DeploymentmoConstants.LOCATION, permission);     
        
    }
	private void installRemoteAlertSender() {
		try {
            RemoteAlertSenderImpl remoteAlertSenderActivator = new RemoteAlertSenderImpl(this);
			remoteAlertSenderActivator.start(getContext());
		}
		catch (Exception e) {
			this.fail("Unexpected exception at prepare(installRemoteAlertSender). "
					+ e.getClass());			
		}				
	}
	public DmtAdmin getDmtAdmin() {
		return da;
	}

	public void closeSession(DmtSession session) {
		if (null != session) {
			if (session.getState() == DmtSession.STATE_OPEN) {
				try {
					session.close();
				} catch (DmtException e) {
					log("#Exception closing the session: "
							+ e.getClass().getName() + "Message: ["
							+ e.getMessage() + "]");
				}
			}
		}
	}

	public void cleanUp(DmtSession session, String[] nodeUri) {
		if (session != null && session.getState() == DmtSession.STATE_OPEN) {
			if (nodeUri == null) {
				closeSession(session);
			} else {
				for (int i = 0; i < nodeUri.length; i++) {
					try {
						if(session.isNodeUri(nodeUri[i])){
						    session.deleteNode(nodeUri[i]);
                        }
					} catch (Throwable e) {
						log("#Exception at cleanUp: " + e.getClass().getName()
								+ " [Message: " + e.getMessage() + "]");
					}
				}
                closeSession(session);
			}
           
		}
	}
	
    /**
	 * Finds a bundle - throw bundle context - associated with the Symbolic Name
	 * 
	 * @param symbolicName
	 * @return bundle associated with the Symbolic Name
	 */
	public Bundle getBundle(String symbolicName) {
		Bundle bundle = null;
		Bundle[] bundles = getContext().getBundles();
		String str = "";
		int i = 0;
		while ((bundle==null) && (i < bundles.length)) {
			str = bundles[i].getSymbolicName();
			if ((str != null) && (str.equals(symbolicName))) {
				bundle = bundles[i];
			}
			i++;
		}
		return bundle;
	}

	
    public void generateDLOTA(TestingDlota dlota) throws IOException {
    	generateDLOTA(dlota,false);
    
    }
    public void generateDLOTA(TestingDlota dlota,boolean isBundle) throws IOException {
        OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(DeploymentmoConstants.DLOTA_RELATIVE_PATH + File.separatorChar + dlota.getFilename()),"UTF-8");
        
        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        out.write("<media xmlns=\"http://www.openmobilealliance.org/xmlns/dd\" xmlns:dd=\"http://www.osgi.org/xmlns/dd/v.1.0\" >\n");
        out.write("<objectURI>" + dlota.getUri() + "</objectURI>\n");
        out.write("<size>" + dlota.getSize() + "</size>\n");
        if (isBundle) {
        	out.write("<type>application/java-archive</type>\n");	
        } else {
        	out.write("<type>application/vnd.osgi.dp</type>\n");
        }
        out.write("<dd:environment envtype=\"" + dlota.getEnvironment() + "\"/>\n");
        out.write("</media>\n");
        out.flush();
        out.close();
    }
    

    public void assertAlertValues(String type,String source,DmtData data) {
    	assertTrue("Asserting if our implementation of RemoteAlertSender was called.", receivedAlert());
        assertTrue("Asserting alert code", getCode() == 1226);

        AlertItem item = getAlert();
        
        assertEquals("Asserting alert type", type, item.getType());
        assertEquals("Asserting source", source, item.getSource());

        assertEquals("Asserting data" ,data,item.getData());
        //Resets after the asserts.
        resetCommandValues();
    }
    
    public static File copyArtifact(String name) {
        return copyArtifact(name, ".");
      }
      

      public static File copyArtifact(String name, String destDirectory) {
      	InputStream in = null;
      	FileOutputStream fos = null;
      	File file = new File(destDirectory+ File.separator+name);
      	try {
  			URL url = new URL(DeploymentmoConstants.SERVER + "www/" + name);
  		    in = url.openStream();
  		    fos = new FileOutputStream(file);
  		    byte buffer[] = new byte[1024];
  		    int count;
  		    while ((count = in.read(buffer, 0, buffer.length)) > 0) {
  		        fos.write(buffer, 0, count);
  		    }
          }catch (IOException e) {
          	e.printStackTrace();
          } finally {
  	        try {
  				fos.close();
  				in.close();
  			} catch (IOException e) {
  				e.printStackTrace();
  			}
  	        in = null;
          }
          return file;
      }

    //It is needed because of the Manifest tests. We need to get the bundle's manifests after it is compiled,
    //otherwise these tests would fail.
    public static void copyTempBundles() {
    	try {
    		JarFile jarFile = DeploymentmoConstants.getJarFile(DeploymentmoConstants.SIMPLE_FIX_PACK_DP);
    		
    		InputStream in = jarFile.getInputStream(jarFile.getEntry("bundle001.jar"));
    		generateTempBundles(getTempFileName(DeploymentmoConstants.SIMPLE_FIX_PACK_DP,"bundle001.jar"), in);
    		jarFile.close();
    		
    		jarFile = DeploymentmoConstants.getJarFile(DeploymentmoConstants.SIMPLE_DP);
    		
    		in = jarFile.getInputStream(jarFile.getEntry("bundle001.jar"));
    		generateTempBundles(getTempFileName(DeploymentmoConstants.SIMPLE_DP,"bundle001.jar"), in);
    		
    		in = jarFile.getInputStream(jarFile.getEntry("bundle002.jar"));
    		generateTempBundles(getTempFileName(DeploymentmoConstants.SIMPLE_DP,"bundle002.jar"), in);
    		jarFile.close();
    		
    		
    	}catch (IOException e) {
        	e.printStackTrace();
    	}

    }
    public static String getTempFileName(int dp, String bundleName) {
    	return DeploymentmoConstants.MAP_CODE_TO_ARTIFACT[dp] + "_" + bundleName;
    }
    public static void generateTempBundles(String name, InputStream in) {
		FileOutputStream fos = null;
		try {
			File tmpFile = new File(DeploymentmoConstants.TEMP_DIR.getAbsolutePath() + File.separatorChar + name);
			fos = new FileOutputStream(tmpFile);
			byte buffer[] = new byte[1024];
			int count;
			while ((count = in.read(buffer, 0, buffer.length)) > 0) {
				fos.write(buffer, 0, count);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			in = null;
		}
	}
    
    public static File getFile(String archiveName) {
    	return new File(DeploymentmoConstants.DELIVERED_AREA,archiveName);
    }
    
    public static boolean renameFileForced(File fileSrc,File fileDestiny) {
    	if (fileDestiny.exists()) {
    		fileDestiny.delete();
    	}
    	return fileSrc.renameTo(fileDestiny);
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
	//Area test cases

    public void testAreaPermanentNodes(){
		new PermanentNodes(this).run();
	}

	public void testAreaDownloadSubtree(){
		new DownloadSubtree(this).run();
	}

	public void testAreaDeliveredSubtree(){
		new DeliveredSubtree(this).run();
	}

	public void testAreaDeployedSubtree(){
        new DeployedSubtree(this).run();
    }

	
    // Command Execution test cases
    public void testDownloadAndInstallAndActivate() {
      testClasses[1].run();    
	}

    public void testInstallAndActivate() {
      testClasses[0].run();
	}


	public void testRemove() {
      testClasses[2].run();
	}

    
	public void setCode(int i) {
		this.code = i;
	}

	public int getCode() {
		return code;
	}

	public AlertItem getAlert() {
		return alert;
	}
	public void setAlert(AlertItem item) {
		alert = item;		
	}
	
	public void resetCommandValues() {
		setAlert(null);
        setCode(0);
        setReceivedAlert(false);
	}
    
    public PermissionAdmin getPermissionAdmin() {
        return permissionAdmin;
    }
	/**
	 * @return Returns the deploymentAdmin.
	 */
	public DeploymentAdmin getDeploymentAdmin() {
		if (deploymentAdmin == null)
			throw new NullPointerException("Deployment Admin Service Instance is null");
		return deploymentAdmin;
	}
    
    public TestingArtifact getArtifact(int id) {
        return (TestingArtifact)artifacts.get(""+id);
    }
    /***
     * Returns the first node name from the finalNodes that 
     * does not exist in initialNodes or an empty string if none is found.
     * @param initialNodes An array containing the initial nodes 
     * @param finalNodes An array containing the final nodes 
     * @return The first different node name
     */
    public static String getNodeId(String[] initialNodes, String[] finalNodes) {
    	if (finalNodes.length!=initialNodes.length+1) 
    		throw new RuntimeException("The node was not created");
    	String nodeId = "";
        for (int i=0;i<finalNodes.length;i++) {
        	boolean found= false; 
            for (int j=0;j<initialNodes.length && !found;j++) {
                if (initialNodes[j].equals(finalNodes[i])) {
                    found=true;
                }
            }
            if (!found) {
                nodeId=finalNodes[i];
                break;
            }
        }
        return nodeId;
    }
    
    public void executeRemoveNode(DmtSession session, String removeNode) {
        if (null != session) {
            try {
                String[] initialChildren = session.getChildNodeNames(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED);
                
                synchronized (this) {
                    session.execute(removeNode,null);
                    this.wait(DeploymentmoConstants.TIMEOUT);
                }
                
                String[] finalChildren = session.getChildNodeNames(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED);
                
                if (initialChildren.length<=finalChildren.length) {
                    log("The Deployed node was not removed, further tests can be affected");
                }
            } catch (Exception e) {
                log("Failed executing the node "+ removeNode +". Further tests can be affected");
            }
        }
    }
    
    
    /**
     * @return Returns the listener.
     */
    public BundleListenerImpl getListener() {
        return listener;
    }
    
    public void unprepare() {
    	File[] files = DeploymentmoConstants.TEMP_DIR.listFiles();
    	for (int i=0;i<files.length;i++) {
    		files[i].delete();
    	}
    }
    //Some DP names can be longer than the DmtAdmin limit
	public void mangleDPNames() {
		for (int i=0;i<DeploymentmoConstants.MAP_CODE_TO_ARTIFACT_MANGLED.length;i++) {
	    	String name = DeploymentmoConstants.MAP_CODE_TO_ARTIFACT[i];
			if (i!=DeploymentmoConstants.SIMPLE_BUNDLE && i!=DeploymentmoConstants.NOT_ACCEPTABLE_CONTENT) {
				name = name + ".dp";
	    	}
			DeploymentmoConstants.MAP_CODE_TO_ARTIFACT_MANGLED[i] = Uri.mangle(name); 
		}
	}
	
	
}
