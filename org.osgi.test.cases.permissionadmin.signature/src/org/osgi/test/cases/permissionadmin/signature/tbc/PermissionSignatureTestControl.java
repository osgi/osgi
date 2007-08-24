/*
 * $Id$
 * 
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
 */
package org.osgi.test.cases.permissionadmin.signature.tbc;

import org.osgi.framework.*;

import org.osgi.service.condpermadmin.ConditionalPermissionAdmin;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.service.startlevel.StartLevel;

//import org.osgi.service.cm.ConfigurationPermission;

import org.osgi.test.cases.util.DefaultTestBundleControl;
import org.osgi.test.cases.util.MethodCall;

import java.io.InputStream;

import java.net.URL;

import java.util.Vector;
//import java.util.Hashtable;

/**
 * Contains the test methods of the permission signature test case.
 * 
 * @author Petia Sotirova
 * @version 1.1
 */
public class PermissionSignatureTestControl extends DefaultTestBundleControl {
  
  private PermissionSignatureTBCService tbc;
  private PermissionAdmin               permissionAdmin;

  private Bundle                        testBundle;
  private String                        testBundleLocation;

  private Bundle                        testSignatureBundle;
  private String                        signatureBundleLocation;

  private String                        signatureBundleName;

  private PermissionSignatureUtility    utility;

  private String                        resourceName;
  private String                        resourcesName;
  private String                        entryName;
  private String                        entryPath;
  private String                        className;
  private String                        extensionEntryName;
  private String                        extensionEntryPath;
  private String                        extensionClassName;
  
  private int                           startLevel = 10;
  private int                           initialBundleStartLevel = 10;
  private boolean                       checkStartLevel = false;
  
  private String                        extensionBundleName;
  
  static String[] methods = new String[] {
    "test_Permission", 
    "test_AdminPermission", 
    "test_AdminPermission_metadata",
    "test_AdminPermission_resource", 
    "test_AdminPermission_class", 
    "test_AdminPermission_execute",
    "test_AdminPermission_listener",
    "test_AdminPermission_resolve",
    "test_AdminPermission_startlevel", 
    "test_AdminPermission_lifecycle",
    //"test_AdminPermission_extensionLifecycle",
    //"test_ConfigurationPermission_get",
    //"test_ConfigurationPermission_set"
  };

  
  public String[] getMethods() {
    return methods;
  } 
  
  
  public boolean checkPrerequisites() {
	    return securityNeeded(true) &&
		 serviceAvailable(PermissionAdmin.class); 
  }


  public void prepare() throws Exception {
    testBundle = installBundle("tb1.jar");
    testBundleLocation = testBundle.getLocation();
    
    signatureBundleName = SignatureResource.getString("bundle.name");
    resourceName = SignatureResource.getString("resource.name");
    resourcesName = SignatureResource.getString("resources.name");
    entryName = SignatureResource.getString("entry.name");
    entryPath = SignatureResource.getString("entry.path");
    className = SignatureResource.getString("load.class");
    
    extensionEntryName = SignatureResource.getString("extension.entry.name");
    extensionEntryPath =  SignatureResource.getString("extension.entry.path");
    extensionClassName = SignatureResource.getString("extension.load.class");
    
    extensionBundleName = SignatureResource.getString("extensionBundle.name");
    
    tbc = (PermissionSignatureTBCService)getRegistry().getService(PermissionSignatureTBCService.class);
    permissionAdmin = (PermissionAdmin)getRegistry().getService(PermissionAdmin.class);
    StartLevel startLevelService = (StartLevel)getRegistry().getService(StartLevel.class);
    if (startLevelService != null) {
      startLevel = startLevelService.getStartLevel();
      initialBundleStartLevel = startLevelService.getInitialBundleStartLevel();
      checkStartLevel = true;
    }

    utility = new PermissionSignatureUtility(this, tbc, getContext()); 
  }

  /**
   * <remove>Prepare for each method. It is important that each method can
   * be executed independently of each other method. Do not keep
   * state between methods, if possible. This method can be used
   * to clean up any possible remaining state.</remove> 
   */
  public void setState() throws Exception {
    permissionAdmin.setPermissions(testBundleLocation, null);
    //uninstallTestSignatureBundle();
    installTestSignatureBundle();
  }

  /**
   * Tests AdminPermission. 
   * Checks if a bundle with NoPermission can not execute anything that requires AdminPermission.
   */
  public void test_Permission() throws Throwable {
    permissionAdmin.setPermissions(testBundleLocation, new PermissionInfo[0]);
    printPermissions(testBundleLocation);
    String message = "";
    
    // Each bundle must be given AdminPermission(<bundle id>, "resource") so that it can access it's own resources. 
    utility.allowed_Bundle_getResource(message, testBundle, resourceName);
    utility.allowed_Bundle_getResources(message, testBundle, resourcesName);
    utility.allowed_Bundle_getEntry(message, testBundle, resourceName);
    utility.allowed_Bundle_getEntryPaths(message, testBundle, "/META-INF/");
    
    utility.not_allowed_Bundle_getHeaders(message, testSignatureBundle);
    utility.not_allowed_Bundle_getHeaders_byLocation(message, testSignatureBundle);
    utility.not_allowed_Bundle_getLocation(message, testSignatureBundle);
    utility.not_allowed_Bundle_getResource(message, testSignatureBundle, resourceName);
    utility.not_allowed_Bundle_getResources(message, testSignatureBundle, resourcesName);
    utility.not_allowed_Bundle_getEntry(message, testSignatureBundle, entryName);
    utility.not_allowed_Bundle_getEntryPaths(message, testSignatureBundle, entryPath);
    if (checkStartLevel) {
      utility.not_allowed_StartLevel_setBundleStartLevel(message, testSignatureBundle, startLevel);
      utility.not_allowed_StartLevel_setStartLevel(message, startLevel);
      utility.not_allowed_StartLevel_setInitialBundleStartLevel(message, initialBundleStartLevel);
    }
    utility.not_allowed_Bundle_loadClass(message, testSignatureBundle, className);
    utility.not_allowed_Bundle_stop(message, testSignatureBundle);
    utility.not_allowed_Bundle_start(message, testSignatureBundle);
    utility.not_allowed_Bundle_update(message, testSignatureBundle);
    utility.not_allowed_Bundle_update_by_InputStream(message, testSignatureBundle, getInputStream(signatureBundleName));
    utility.not_allowed_Bundle_uninstall(message, testSignatureBundle);
    utility.not_allowed_BundleContext_addBundleListener(message);
    utility.not_allowed_BundleContext_removeBundleListener(message);
    utility.not_allowed_PackageAdmin_refreshPackages(message, null);
    utility.not_allowed_PackageAdmin_resolveBundles(message, null);
    utility.not_allowed_PermissionAdmin_setPermissions(message, signatureBundleLocation, 
        new PermissionInfo[]{new PermissionInfo(AdminPermission.class.getName(), "*", "*")});
    utility.not_allowed_PermissionAdmin_setDefaultPermissions(message, permissionAdmin.getDefaultPermissions());
  } 

  /**
   * Tests AdminPermission with all actions allowed on all. 
   * Checks if a bundle with AllPermission can execute all methods requiring AdminPermission.
   */
  public void test_AdminPermission() throws Throwable {
    PermissionInfo info = new PermissionInfo(AdminPermission.class.getName(), "*", "*");
    permissionAdmin.setPermissions(testBundleLocation, new PermissionInfo[]{info});
    printPermissions(testBundleLocation);
    String message = "";
  
    utility.allowed_Bundle_getHeaders(message, testSignatureBundle);
    utility.allowed_Bundle_getHeaders_byLocation(message, testSignatureBundle);
    utility.allowed_Bundle_getLocation(message, testSignatureBundle);
    utility.allowed_Bundle_getResource(message, testSignatureBundle, resourceName);
    utility.allowed_Bundle_getResources(message, testSignatureBundle, resourcesName);
    utility.allowed_Bundle_getEntry(message, testSignatureBundle, entryName);
    utility.allowed_Bundle_getEntryPaths(message, testSignatureBundle, entryPath);
    if (checkStartLevel) {
      utility.allowed_StartLevel_setStartLevel(message, startLevel);
      utility.allowed_StartLevel_setInitialBundleStartLevel(message, initialBundleStartLevel);
    }
    utility.allowed_Bundle_loadClass(message, testSignatureBundle, className);
    utility.allowed_Bundle_stop(message, testSignatureBundle);
    utility.allowed_Bundle_start(message, testSignatureBundle);
    utility.allowed_Bundle_update(message, testSignatureBundle);
    utility.allowed_Bundle_update_by_InputStream(message, testSignatureBundle, getInputStream(signatureBundleName));
    
    utility.allowed_BundleContext_addBundleListener(message);
    utility.allowed_BundleContext_removeBundleListener(message);
    utility.allowed_PackageAdmin_refreshPackages(message, null);
    utility.allowed_PackageAdmin_resolveBundles(message, null);
    utility.not_allowed_PermissionAdmin_setPermissions(message, signatureBundleLocation, 
        new PermissionInfo[]{new PermissionInfo(AdminPermission.class.getName(), "*", "*")});
    utility.not_allowed_PermissionAdmin_setDefaultPermissions(message, permissionAdmin.getDefaultPermissions());
    
//    utility.not_allowed_ConfigurationAdmin_createFactoryConfiguration(message, PermissionSignatureUtility.CONFIG_FPID);
//    utility.not_allowed_ConfigurationAdmin_createFactoryConfiguration(message, PermissionSignatureUtility.CONFIG_FPID, "");
//    utility.not_allowed_Configuration_update(message, PermissionSignatureUtility.CONFIG_FPID);
//    utility.not_allowed_Configuration_delete(message, PermissionSignatureUtility.CONFIG_FPID);
//    utility.not_allowed_ConfigurationAdmin_getConfiguration(message, PermissionSignatureUtility.CONFIG_FPID);
//    utility.not_allowed_ConfigurationAdmin_getConfiguration(message, PermissionSignatureUtility.CONFIG_FPID, "");
//    utility.not_allowed_ConfigurationAdmin_listConfigurations(message, null);
//    utility.not_allowed_Configuration_delete(message, PermissionSignatureUtility.CONFIG_FPID);
//    utility.not_allowed_Configuration_update(message, PermissionSignatureUtility.CONFIG_FPID, new Hashtable());
//    utility.not_allowed_Configuration_setBundleLocation(message, PermissionSignatureUtility.CONFIG_FPID);
  }
  
  
  
  /**
   * Tests AdminPermission with an action parameter - metadata. 
   * Checks if a bundle with AdminPrmission - metadata can execute: 
   *  - Bundle.getHeaders
   *  - Bundle.getLocation
   * and can not execute anything else that requires other AdminPermission.
   * 
   * The bundle is specified either by bundle id or by filter string.
   */
  public void test_AdminPermission_metadata() throws Throwable {
    String message = "";
    Vector permissions = utility.getPInfosForAdminPermisssion(
        AdminPermission.METADATA,
        testSignatureBundle.getBundleId(), 
        testSignatureBundle.getLocation(), 
        testSignatureBundle.getSymbolicName()); 
    
    PermissionInfo info; 
    for (int i = 0; i < permissions.size(); ++i) {
      info = (PermissionInfo)permissions.elementAt(i);
      permissionAdmin.setPermissions(testBundleLocation, new PermissionInfo[]{info});
      printPermissions(testBundleLocation);
      
      utility.allowed_Bundle_getHeaders(message, testSignatureBundle);
      utility.allowed_Bundle_getHeaders_byLocation(message, testSignatureBundle);
      utility.allowed_Bundle_getLocation(message, testSignatureBundle);

      utility.not_allowed_Bundle_getResource(message, testSignatureBundle, resourceName);
      utility.not_allowed_Bundle_getResources(message, testSignatureBundle, resourcesName);
      utility.not_allowed_Bundle_getEntry(message, testSignatureBundle, entryName);
      utility.not_allowed_Bundle_getEntryPaths(message, testSignatureBundle, entryPath);
      
      if (checkStartLevel) {
        utility.not_allowed_StartLevel_setBundleStartLevel(message, testSignatureBundle, startLevel);
        utility.not_allowed_StartLevel_setStartLevel(message, startLevel);
        utility.not_allowed_StartLevel_setInitialBundleStartLevel(message, initialBundleStartLevel);
      }
      utility.not_allowed_Bundle_loadClass(message, testSignatureBundle, className);
      utility.not_allowed_Bundle_stop(message, testSignatureBundle);
      utility.not_allowed_Bundle_start(message, testSignatureBundle);
      utility.not_allowed_Bundle_update(message, testSignatureBundle);
      utility.not_allowed_Bundle_update_by_InputStream(message, testSignatureBundle, getInputStream(signatureBundleName));
      utility.not_allowed_Bundle_uninstall(message, testSignatureBundle);
      utility.not_allowed_BundleContext_addBundleListener(message);
      utility.not_allowed_BundleContext_removeBundleListener(message);
      utility.not_allowed_PackageAdmin_refreshPackages(message, null);
      utility.not_allowed_PackageAdmin_resolveBundles(message, null);
      
    }
  }

  /**
   * Tests AdminPermission with an action parameter - resource. 
   * Checks if a bundle with AdminPrmission - resource can execute:
   *  - Bundle.getResource
   *  - Bundle.getEntry
   *  - Bundle.getEntryPaths
   * and can not execute anything else that requires other AdminPermission.
   * 
   * The bundle is specified either by bundle id or by filter string.
   */
  public void test_AdminPermission_resource() throws Throwable {
    String message = "";
    Vector permissions = utility.getPInfosForAdminPermisssion(
        AdminPermission.RESOURCE,
        testSignatureBundle.getBundleId(), 
        testSignatureBundle.getLocation(), 
        testSignatureBundle.getSymbolicName()); 

    
    PermissionInfo info; 
    for (int i = 0; i < permissions.size(); ++i) {
      info = (PermissionInfo)permissions.elementAt(i);

      permissionAdmin.setPermissions(testBundleLocation, new PermissionInfo[]{info});
      printPermissions(testBundleLocation);

      utility.allowed_Bundle_getResource(message, testSignatureBundle, resourceName);
      utility.allowed_Bundle_getResources(message, testSignatureBundle, resourcesName);
      utility.allowed_Bundle_getEntry(message, testSignatureBundle, entryName);
      utility.allowed_Bundle_getEntryPaths(message, testSignatureBundle, entryPath);
      
      utility.not_allowed_Bundle_getHeaders(message, testSignatureBundle);
      utility.not_allowed_Bundle_getHeaders_byLocation(message, testSignatureBundle);
      utility.not_allowed_Bundle_getLocation(message, testSignatureBundle);
      
      if (checkStartLevel) {
        utility.not_allowed_StartLevel_setBundleStartLevel(message, testSignatureBundle, startLevel);
        utility.not_allowed_StartLevel_setStartLevel(message, startLevel);
        utility.not_allowed_StartLevel_setInitialBundleStartLevel(message, initialBundleStartLevel);
      }
      utility.not_allowed_Bundle_loadClass(message, testSignatureBundle, className);
      utility.not_allowed_Bundle_stop(message, testSignatureBundle);
      utility.not_allowed_Bundle_start(message, testSignatureBundle);
      utility.not_allowed_Bundle_update(message, testSignatureBundle);
      utility.not_allowed_Bundle_update_by_InputStream(message, testSignatureBundle, getInputStream(signatureBundleName));
      utility.not_allowed_Bundle_uninstall(message, testSignatureBundle);
      utility.not_allowed_BundleContext_addBundleListener(message);
      utility.not_allowed_BundleContext_removeBundleListener(message);
      utility.not_allowed_PackageAdmin_refreshPackages(message, null);
      utility.not_allowed_PackageAdmin_resolveBundles(message, null);
      utility.not_allowed_PermissionAdmin_setPermissions(message, signatureBundleLocation, 
          new PermissionInfo[]{new PermissionInfo(AdminPermission.class.getName(), "*", "*")});
      utility.not_allowed_PermissionAdmin_setDefaultPermissions(message, permissionAdmin.getDefaultPermissions());
    }
  }
  
  /**
   * Tests AdminPermission with an action parameter - Class. 
   * Checks if a bundle with AdminPrmission - Class can execute:
   *  - Bundle.loadClass
   * and can not execute anything else that requires other AdminPermission.
   * 
   * The bundle is specified either by bundle id or by filter string.
   */
  public void test_AdminPermission_class() throws Throwable {
    String message = "";
    Vector permissions = utility.getPInfosForAdminPermisssion(
        AdminPermission.CLASS,
        testSignatureBundle.getBundleId(), 
        testSignatureBundle.getLocation(), 
        testSignatureBundle.getSymbolicName()); 

    
    PermissionInfo info; 
    for (int i = 0; i < permissions.size(); ++i) {
      info = (PermissionInfo)permissions.elementAt(i);

      permissionAdmin.setPermissions(testBundleLocation, new PermissionInfo[]{info});
      printPermissions(testBundleLocation);

      utility.allowed_Bundle_loadClass(message, testSignatureBundle, className);
      
      utility.not_allowed_Bundle_getHeaders(message, testSignatureBundle);
      utility.not_allowed_Bundle_getHeaders_byLocation(message, testSignatureBundle);
      utility.not_allowed_Bundle_getLocation(message, testSignatureBundle);
      utility.not_allowed_Bundle_getResource(message, testSignatureBundle, resourceName);
      utility.not_allowed_Bundle_getResources(message, testSignatureBundle, resourcesName);
      utility.not_allowed_Bundle_getEntry(message, testSignatureBundle, entryName);
      utility.not_allowed_Bundle_getEntryPaths(message, testSignatureBundle, entryPath);
      if (checkStartLevel) {
        utility.not_allowed_StartLevel_setBundleStartLevel(message, testSignatureBundle, startLevel);
        utility.not_allowed_StartLevel_setStartLevel(message, startLevel);
        utility.not_allowed_StartLevel_setInitialBundleStartLevel(message, initialBundleStartLevel);
      }
      utility.not_allowed_Bundle_stop(message, testSignatureBundle);
      utility.not_allowed_Bundle_start(message, testSignatureBundle);
      utility.not_allowed_Bundle_update(message, testSignatureBundle);
      utility.not_allowed_Bundle_update_by_InputStream(message, testSignatureBundle, getInputStream(signatureBundleName));
      utility.not_allowed_Bundle_uninstall(message, testSignatureBundle);
      utility.not_allowed_BundleContext_addBundleListener(message);
      utility.not_allowed_BundleContext_removeBundleListener(message);
      utility.not_allowed_PackageAdmin_refreshPackages(message, null);
      utility.not_allowed_PackageAdmin_resolveBundles(message, null);
      utility.not_allowed_PermissionAdmin_setPermissions(message, signatureBundleLocation, 
          new PermissionInfo[]{new PermissionInfo(AdminPermission.class.getName(), "*", "*")});
      utility.not_allowed_PermissionAdmin_setDefaultPermissions(message, permissionAdmin.getDefaultPermissions());
    }

  }
  
  /**
   * Tests AdminPermission with an action parameter - lifecycle. 
   * Checks if a bundle with AdminPrmission - lifecycle can execute:
   *  - BundleContext.installBundle
   *  - Bundle.update
   *  - Bundle.uninstall
   * and can not execute anything else that requires other AdminPermission.
   * 
   * The bundle is specified either by bundle id or by filter string.
   */
  public void test_AdminPermission_lifecycle() throws Throwable {
    String message = "";
    Vector permissions = utility.getPInfosForAdminPermisssion(
        AdminPermission.LIFECYCLE,
        testSignatureBundle.getBundleId(), 
        testSignatureBundle.getLocation(), 
        testSignatureBundle.getSymbolicName()); 

    PermissionInfo info; 
    for (int i = 0; i < permissions.size(); ++i) {
      info = (PermissionInfo)permissions.elementAt(i);

      permissionAdmin.setPermissions(testBundleLocation, new PermissionInfo[]{info});
      printPermissions(testBundleLocation);
      
      utility.not_allowed_Bundle_getHeaders(message, testSignatureBundle);
      utility.not_allowed_Bundle_getHeaders_byLocation(message, testSignatureBundle);
      utility.not_allowed_Bundle_getLocation(message, testSignatureBundle);
      utility.not_allowed_Bundle_getResource(message, testSignatureBundle, resourceName);
      utility.not_allowed_Bundle_getResources(message, testSignatureBundle, resourcesName);
      utility.not_allowed_Bundle_getEntry(message, testSignatureBundle, entryName);
      utility.not_allowed_Bundle_getEntryPaths(message, testSignatureBundle, entryPath);
      if (checkStartLevel) {
        utility.not_allowed_StartLevel_setBundleStartLevel(message, testSignatureBundle, startLevel);
        utility.not_allowed_StartLevel_setStartLevel(message, startLevel);
        utility.not_allowed_StartLevel_setInitialBundleStartLevel(message, initialBundleStartLevel);
      }
      utility.not_allowed_Bundle_loadClass(message, testSignatureBundle, className);
      utility.not_allowed_Bundle_stop(message, testSignatureBundle);
      utility.not_allowed_Bundle_start(message, testSignatureBundle);
      utility.not_allowed_BundleContext_addBundleListener(message);
      utility.not_allowed_BundleContext_removeBundleListener(message);
      utility.not_allowed_PackageAdmin_refreshPackages(message, null);
      utility.not_allowed_PackageAdmin_resolveBundles(message, null);
      utility.not_allowed_PermissionAdmin_setPermissions(message, signatureBundleLocation, 
          new PermissionInfo[]{new PermissionInfo(AdminPermission.class.getName(), "*", "*")});
      utility.not_allowed_PermissionAdmin_setDefaultPermissions(message, permissionAdmin.getDefaultPermissions());
    }
  }
  
  
  /**
   * Tests AdminPermission with an action parameter - lifecycle. 
   * Checks if a bundle with AdminPrmission - lifecycle can execute:
   *  - BundleContext.installBundle
   *  - Bundle.update
   *  - Bundle.uninstall
   * and can not execute anything else that requires other AdminPermission.
   * 
   * The bundle is specified either by bundle id or by filter string.
   */
  public void test_AdminPermission_extensionLifecycle() throws Throwable {
    String message = "";
    String extensionBundleLocation = getInstallBundleLocation(extensionBundleName);
    
    Vector permissions = utility.getPInfosForAdminPermisssion(
        AdminPermission.EXTENSIONLIFECYCLE,
        -1, // extension bundle is not yet installed  
        extensionBundleLocation,  // ?
        null);  

    
    PermissionInfo info; 
    for (int i = 0; i < permissions.size(); ++i) {
      info = (PermissionInfo)permissions.elementAt(i);

      permissionAdmin.setPermissions(testBundleLocation, new PermissionInfo[]{info});
      printPermissions(testBundleLocation);
      
      Bundle extensionBundle = (Bundle)utility.allowed_BundleContext_installBundle(message, extensionBundleLocation);
      utility.allowed_Bundle_update(message, extensionBundle);
      utility.allowed_Bundle_update_by_InputStream(message, extensionBundle, getInputStream(extensionBundleName));
      
      utility.not_allowed_Bundle_getHeaders(message, extensionBundle);
      utility.not_allowed_Bundle_getHeaders_byLocation(message, extensionBundle);
      utility.not_allowed_Bundle_getLocation(message, extensionBundle);
      
      utility.not_allowed_Bundle_getResource(message, extensionBundle, resourceName);
      utility.not_allowed_Bundle_getResources(message, extensionBundle, resourcesName);
      utility.not_allowed_Bundle_getEntry(message, extensionBundle, extensionEntryName);
      utility.not_allowed_Bundle_getEntryPaths(message, extensionBundle, extensionEntryPath);
      
      if (checkStartLevel) {
        utility.not_allowed_StartLevel_setBundleStartLevel(message, extensionBundle, startLevel);
        utility.not_allowed_StartLevel_setStartLevel(message, startLevel);
        utility.not_allowed_StartLevel_setInitialBundleStartLevel(message, initialBundleStartLevel);
      }
      
      utility.not_allowed_Bundle_loadClass(message, extensionBundle, extensionClassName);
      
      utility.not_allowed_Bundle_stop(message, extensionBundle);
      utility.not_allowed_Bundle_start(message, extensionBundle);
      utility.not_allowed_BundleContext_addBundleListener(message);
      utility.not_allowed_BundleContext_removeBundleListener(message);
      utility.not_allowed_PackageAdmin_refreshPackages(message, null);
      utility.not_allowed_PackageAdmin_resolveBundles(message, null);
      utility.not_allowed_PermissionAdmin_setPermissions(message, extensionBundleLocation, 
          new PermissionInfo[]{new PermissionInfo(AdminPermission.class.getName(), "*", "*")});
      utility.not_allowed_PermissionAdmin_setDefaultPermissions(message, permissionAdmin.getDefaultPermissions());

      utility.allowed_Bundle_uninstall(message, extensionBundle);
    }
  }

  
  /**
   * Tests AdminPermission with an action parameter - execute. 
   * Checks if a bundle with AdminPrmission - execute can execute:
   *  - Bundle.start
   *  - Bundle.stop
   *  - StartLevel.setBundleStartLevel
   * and can not execute anything else that requires other AdminPermission.
   * 
   * The bundle is specified either by bundle id or by filter string.
   */
  public void test_AdminPermission_execute() throws Throwable {
    String message = "";
    Vector permissions = utility.getPInfosForAdminPermisssion(
        AdminPermission.EXECUTE,
        testSignatureBundle.getBundleId(), 
        testSignatureBundle.getLocation(), 
        testSignatureBundle.getSymbolicName()); 

    
    PermissionInfo info; 
    for (int i = 0; i < permissions.size(); ++i) {
      info = (PermissionInfo)permissions.elementAt(i);

      permissionAdmin.setPermissions(testBundleLocation, new PermissionInfo[]{info});
      printPermissions(testBundleLocation);

      utility.allowed_Bundle_stop(message, testSignatureBundle);
      utility.allowed_Bundle_start(message, testSignatureBundle);
      
      utility.not_allowed_Bundle_getHeaders(message, testSignatureBundle);
      utility.not_allowed_Bundle_getHeaders_byLocation(message, testSignatureBundle);
      utility.not_allowed_Bundle_getLocation(message, testSignatureBundle);
      utility.not_allowed_Bundle_getResource(message, testSignatureBundle, resourceName);
      utility.not_allowed_Bundle_getResources(message, testSignatureBundle, resourcesName);
      utility.not_allowed_Bundle_getEntry(message, testSignatureBundle, entryName);
      utility.not_allowed_Bundle_getEntryPaths(message, testSignatureBundle, entryPath);
      if (checkStartLevel) {
        utility.not_allowed_StartLevel_setStartLevel(message, startLevel);
        utility.not_allowed_StartLevel_setInitialBundleStartLevel(message, initialBundleStartLevel);
      }
      utility.not_allowed_Bundle_loadClass(message, testSignatureBundle, className);
      utility.not_allowed_Bundle_update(message, testSignatureBundle);
      utility.not_allowed_Bundle_update_by_InputStream(message, testSignatureBundle, getInputStream(signatureBundleName));
      utility.not_allowed_Bundle_uninstall(message, testSignatureBundle);
      utility.not_allowed_BundleContext_addBundleListener(message);
      utility.not_allowed_BundleContext_removeBundleListener(message);
      utility.not_allowed_PackageAdmin_refreshPackages(message, null);
      utility.not_allowed_PackageAdmin_resolveBundles(message, null);
      utility.not_allowed_PermissionAdmin_setPermissions(message, signatureBundleLocation, 
          new PermissionInfo[]{new PermissionInfo(AdminPermission.class.getName(), "*", "*")});
      utility.not_allowed_PermissionAdmin_setDefaultPermissions(message, permissionAdmin.getDefaultPermissions());

    }
  }

  /**
   * Tests AdminPermission with an action parameter - listener. 
   * Checks if a bundle with AdminPrmission - listener can execute:
   *  - BundleContext.addBundleListener 
   *  - BundleContext.removeBundleListener 
   * and can not execute anything else that requires other AdminPermission.
   * 
   * The bundle is specified either by bundle id or by filter string.
   */
  public void test_AdminPermission_listener() throws Throwable {
    String message = "";
    Vector permissions = utility.getPInfosForAdminPermisssion(
        AdminPermission.LISTENER,
        testSignatureBundle.getBundleId(), 
        testSignatureBundle.getLocation(), 
        testSignatureBundle.getSymbolicName()); 

    
    PermissionInfo info; 
    for (int i = 0; i < permissions.size(); ++i) {
      info = (PermissionInfo)permissions.elementAt(i);

      permissionAdmin.setPermissions(testBundleLocation, new PermissionInfo[]{info});
      printPermissions(testBundleLocation);
  
      utility.allowed_BundleContext_addBundleListener(message);
      utility.allowed_BundleContext_removeBundleListener(message);
      
      utility.not_allowed_Bundle_getHeaders(message, testSignatureBundle);
      utility.not_allowed_Bundle_getHeaders_byLocation(message, testSignatureBundle);
      utility.not_allowed_Bundle_getLocation(message, testSignatureBundle);
      utility.not_allowed_Bundle_getResource(message, testSignatureBundle, resourceName);
      utility.not_allowed_Bundle_getResources(message, testSignatureBundle, resourcesName);
      utility.not_allowed_Bundle_getEntry(message, testSignatureBundle, entryName);
      utility.not_allowed_Bundle_getEntryPaths(message, testSignatureBundle, entryPath);
      if (checkStartLevel) {
        utility.not_allowed_StartLevel_setBundleStartLevel(message, testSignatureBundle, startLevel);
        utility.not_allowed_StartLevel_setStartLevel(message, startLevel);
        utility.not_allowed_StartLevel_setInitialBundleStartLevel(message, initialBundleStartLevel);
      }
      utility.not_allowed_Bundle_loadClass(message, testSignatureBundle, className);
      utility.not_allowed_Bundle_stop(message, testSignatureBundle);
      utility.not_allowed_Bundle_start(message, testSignatureBundle);
      utility.not_allowed_Bundle_update(message, testSignatureBundle);
      utility.not_allowed_Bundle_update_by_InputStream(message, testSignatureBundle, getInputStream(signatureBundleName));
      utility.not_allowed_Bundle_uninstall(message, testSignatureBundle);
      utility.not_allowed_PackageAdmin_refreshPackages(message, null);
      utility.not_allowed_PackageAdmin_resolveBundles(message, null);
      utility.not_allowed_PermissionAdmin_setPermissions(message, signatureBundleLocation, 
          new PermissionInfo[]{new PermissionInfo(AdminPermission.class.getName(), "*", "*")});
      utility.not_allowed_PermissionAdmin_setDefaultPermissions(message, permissionAdmin.getDefaultPermissions());

    }
  
  }
  
  /**
   * Tests AdminPermission with an action parameter - resolve. 
   * Checks if a bundle with AdminPrmission - resolve can execute:
   *  - PackageAdmin.refreshPackages
   *  - PackageAdmin.resolveBundles
   * and can not execute anything else that requires other AdminPermission.
   * 
   * The bundle is specified either by bundle id or by filter string.
   */
  public void test_AdminPermission_resolve() throws Throwable {
    String message = "";
    Vector permissions = utility.getPInfosForAdminPermisssion(
        AdminPermission.RESOLVE,
        0, 
        testSignatureBundle.getLocation(), 
        testSignatureBundle.getSymbolicName()); 

    
    PermissionInfo info; 
    for (int i = 0; i < permissions.size(); ++i) {
      info = (PermissionInfo)permissions.elementAt(i);

      permissionAdmin.setPermissions(testBundleLocation, new PermissionInfo[]{info});
      printPermissions(testBundleLocation);
  
      
      if (i < 2) {
        utility.allowed_PackageAdmin_refreshPackages(message, null);
        utility.allowed_PackageAdmin_resolveBundles(message, null);
      } else {
        utility.not_allowed_PackageAdmin_refreshPackages(message, null);
        utility.not_allowed_PackageAdmin_resolveBundles(message, null);
      }
      
      utility.not_allowed_BundleContext_addBundleListener(message);
      utility.not_allowed_BundleContext_removeBundleListener(message);
      utility.not_allowed_Bundle_getHeaders(message, testSignatureBundle);
      utility.not_allowed_Bundle_getHeaders_byLocation(message, testSignatureBundle);
      utility.not_allowed_Bundle_getLocation(message, testSignatureBundle);
      utility.not_allowed_Bundle_getResource(message, testSignatureBundle, resourceName);
      utility.not_allowed_Bundle_getResources(message, testSignatureBundle, resourcesName);
      utility.not_allowed_Bundle_getEntry(message, testSignatureBundle, entryName);
      utility.not_allowed_Bundle_getEntryPaths(message, testSignatureBundle, entryPath);
      if (checkStartLevel) {
        utility.not_allowed_StartLevel_setBundleStartLevel(message, testSignatureBundle, startLevel);
        utility.not_allowed_StartLevel_setStartLevel(message, startLevel);
        utility.not_allowed_StartLevel_setInitialBundleStartLevel(message, initialBundleStartLevel);
      }
      utility.not_allowed_Bundle_loadClass(message, testSignatureBundle, className);
      utility.not_allowed_Bundle_stop(message, testSignatureBundle);
      utility.not_allowed_Bundle_start(message, testSignatureBundle);
      utility.not_allowed_Bundle_update(message, testSignatureBundle);
      utility.not_allowed_Bundle_update_by_InputStream(message, testSignatureBundle, getInputStream(signatureBundleName));
      utility.not_allowed_Bundle_uninstall(message, testSignatureBundle);
      utility.not_allowed_PermissionAdmin_setPermissions(message, signatureBundleLocation, 
          new PermissionInfo[]{new PermissionInfo(AdminPermission.class.getName(), "*", "*")});
      utility.not_allowed_PermissionAdmin_setDefaultPermissions(message, permissionAdmin.getDefaultPermissions());
    
    }
  
  }
  
  /**
   * Tests AdminPermission with an action parameter - startlevel. 
   * Checks if a bundle with AdminPrmission - startlevel can execute:
   *  - StartLevel.setStartLevel
   *  - StartLevel.setInitialBundleStartLevel
   * and can not execute anything else that requires other AdminPermission.
   * 
   * The bundle is specified either by bundle id or by filter string.
   */
  public void test_AdminPermission_startlevel() throws Throwable {
    if (!checkStartLevel) {
      return; // this method shall not be excecuted - no StartLevel service detected
    }
    String message = "";
    Vector permissions = utility.getPInfosForAdminPermisssion(
        AdminPermission.STARTLEVEL,
        0, 
        testSignatureBundle.getLocation(), 
        testSignatureBundle.getSymbolicName()); 

    PermissionInfo info; 
    for (int i = 0; i < permissions.size(); ++i) {
      info = (PermissionInfo)permissions.elementAt(i);

      permissionAdmin.setPermissions(testBundleLocation, new PermissionInfo[]{info});
      printPermissions(testBundleLocation);
  
      if (i < 2) {
        utility.allowed_StartLevel_setStartLevel(message, startLevel);
        utility.allowed_StartLevel_setInitialBundleStartLevel(message, initialBundleStartLevel);
      } else {
        utility.not_allowed_StartLevel_setStartLevel(message, startLevel);
        utility.not_allowed_StartLevel_setInitialBundleStartLevel(message, initialBundleStartLevel);
      }
      
      utility.not_allowed_BundleContext_addBundleListener(message);
      utility.not_allowed_BundleContext_removeBundleListener(message);
      utility.not_allowed_Bundle_getHeaders(message, testSignatureBundle);
      utility.not_allowed_Bundle_getHeaders_byLocation(message, testSignatureBundle);
      utility.not_allowed_Bundle_getLocation(message, testSignatureBundle);
      utility.not_allowed_Bundle_getResource(message, testSignatureBundle, resourceName);
      utility.not_allowed_Bundle_getResources(message, testSignatureBundle, resourcesName);
      utility.not_allowed_Bundle_getEntry(message, testSignatureBundle, entryName);
      utility.not_allowed_Bundle_getEntryPaths(message, testSignatureBundle, entryPath);
      if (checkStartLevel) {
        utility.not_allowed_StartLevel_setBundleStartLevel(message, testSignatureBundle, startLevel);
      }
      utility.not_allowed_Bundle_loadClass(message, testSignatureBundle, className);
      utility.not_allowed_Bundle_stop(message, testSignatureBundle);
      utility.not_allowed_Bundle_start(message, testSignatureBundle);
      utility.not_allowed_Bundle_update(message, testSignatureBundle);
      utility.not_allowed_Bundle_update_by_InputStream(message, testSignatureBundle, getInputStream(signatureBundleName));
      utility.not_allowed_Bundle_uninstall(message, testSignatureBundle);
      utility.not_allowed_PackageAdmin_refreshPackages(message, null);
      utility.not_allowed_PackageAdmin_resolveBundles(message, null);
      utility.not_allowed_PermissionAdmin_setPermissions(message, signatureBundleLocation, 
          new PermissionInfo[]{new PermissionInfo(AdminPermission.class.getName(), "*", "*")});
      utility.not_allowed_PermissionAdmin_setDefaultPermissions(message, permissionAdmin.getDefaultPermissions());
    
    }
  }
  
  
  /**
   * Tests ConfigurationPermission with an action parameter - get. 
   * Checks if a bundle with ConfigurationPermission - get can execute:
   *  - ConfigurationAdmin.getConfiguration
   *  - ConfigurationAdmin.listConfigurations
   * and can not execute anything else that requires other ConfigurationPermission.
   */
  public void test_ConfigurationPermission_get() throws Exception {
    // TODO test case for configuration permission
//    Vector permissions = utility.createWildcardPermissionInfo(
//        ConfigurationPermission.class, 
//        "", ConfigurationPermission.SET, 
//        PermissionSignatureUtility.CONFIG_FPID);
//
//    String message = "";
//    PermissionInfo info; 
//    for (int i = 0; i < permissions.size(); ++i) {
//      info = (PermissionInfo)permissions.elementAt(i);
//      permissionAdmin.setPermissions(testBundleLocation, new PermissionInfo[]{info});
//      printPermissions(testBundleLocation);
//
//
//      utility.allowed_ConfigurationAdmin_getConfiguration(message, PermissionSignatureUtility.CONFIG_FPID);
//      utility.allowed_ConfigurationAdmin_getConfiguration(message, PermissionSignatureUtility.CONFIG_FPID, "");
//      utility.allowed_ConfigurationAdmin_listConfigurations(message, null);
//
//      utility.not_allowed_ConfigurationAdmin_createFactoryConfiguration(message, PermissionSignatureUtility.CONFIG_FPID);
//      utility.not_allowed_Configuration_update(message, PermissionSignatureUtility.CONFIG_FPID);
//      utility.not_allowed_Configuration_delete(message, PermissionSignatureUtility.CONFIG_FPID);
//      utility.not_allowed_ConfigurationAdmin_createFactoryConfiguration(message, PermissionSignatureUtility.CONFIG_FPID, "");
//      utility.not_allowed_Configuration_update(message, PermissionSignatureUtility.CONFIG_FPID, new Hashtable());
//      utility.not_allowed_Configuration_setBundleLocation(message, PermissionSignatureUtility.CONFIG_FPID);
//      utility.not_allowed_Configuration_delete(message, PermissionSignatureUtility.CONFIG_FPID);
//    }
  }

  /**
   * Tests ConfigurationPermission with an action parameter - set. 
   * Checks if a bundle with ConfigurationPermission - set can execute:
   *  - Configuration.setBundleLocation
   *  - Configuration.delete
   *  - Configuration.update
   *  - ConfigurationAdmin.createFactoryConfiguration
   */
  public void test_ConfigurationPermission_set() throws Exception {
    // TODO ConfigurationPermission test
//    Vector permissions = utility.createWildcardPermissionInfo(
//                ConfigurationPermission.class, 
//                "", ConfigurationPermission.SET, 
//                PermissionSignatureUtility.CONFIG_FPID);
//    
//    String message = "";
//    PermissionInfo info; 
//    for (int i = 0; i < permissions.size(); ++i) {
//      info = (PermissionInfo)permissions.elementAt(i);
//      permissionAdmin.setPermissions(testBundleLocation, new PermissionInfo[]{info});
//      printPermissions(testBundleLocation);
//    
//      
//      utility.allowed_ConfigurationAdmin_createFactoryConfiguration(message, PermissionSignatureUtility.CONFIG_FPID);
//      utility.allowed_Configuration_update(message, PermissionSignatureUtility.CONFIG_FPID);
//      utility.allowed_Configuration_delete(message, PermissionSignatureUtility.CONFIG_FPID);
//      utility.allowed_ConfigurationAdmin_createFactoryConfiguration(message, PermissionSignatureUtility.CONFIG_FPID, "");
//      utility.allowed_Configuration_update(message, PermissionSignatureUtility.CONFIG_FPID, new Hashtable());
//      utility.allowed_Configuration_setBundleLocation(message, PermissionSignatureUtility.CONFIG_FPID);
//      utility.allowed_Configuration_delete(message, PermissionSignatureUtility.CONFIG_FPID);
//      
//      // dali?
//      utility.not_allowed_ConfigurationAdmin_getConfiguration(message, PermissionSignatureUtility.CONFIG_FPID);
//      utility.not_allowed_ConfigurationAdmin_getConfiguration(message, PermissionSignatureUtility.CONFIG_FPID, "");
//      utility.not_allowed_ConfigurationAdmin_listConfigurations(message, null);
//    }
//    
  }
  
  /**
   * Clean up after each method. Notice that during debugging
   * many times the unsetState is never reached.
   */
  public void unsetState() {
    log("#after each method");
  }

  /**
   * Clean up after a run. Notice that during debugging
   * many times the unprepare is never reached.
   */
  public void unprepare() {    
    log("#after each run");
    permissionAdmin.setPermissions(testBundleLocation, null);    
  }
  
  // returns true if 'method' failed
  boolean not_allowed_call(String message, String methodName,  Class[] paramClasses, Object[] paramObjects, Class wanted) throws Exception {
    try {
      MethodCall method = new MethodCall(methodName, paramClasses, paramObjects);
      method.invoke(tbc);
    } catch (Throwable e) {
      assertException(message, wanted, e);
      return true;
    }
    failException(message, wanted);
    return false;
  }
  
  boolean not_allowed_call_assertNull(String message, String methodName,  Class[] paramClasses, Object[] paramObjects) throws Throwable {
    MethodCall method = new MethodCall(methodName, paramClasses, paramObjects);
    Object result = method.invoke(tbc);
    if (result == null) {
      pass(message + " and correctly returns null");
      return true;
    } else {
      fail(message + " but returns not null");
      return false;
    }
  }

  boolean allowed_call_assertNotNull(String message, String methodName,  Class[] paramClasses, Object[] paramObjects) throws Throwable {
    MethodCall method = new MethodCall(methodName, paramClasses, paramObjects);
    Object result = method.invoke(tbc);
    if (result == null) {
      fail(message + " but returns null");
      return false;
    } else {
      pass(message + " and correctly returns not null");
      return true;
    }
  } 

  Object allowed_call(String message, String methodName,  Class[] paramClasses, Object[] paramObjects) {
    try {
      MethodCall method = new MethodCall(methodName, paramClasses, paramObjects);
      Object result = method.invoke(tbc);
      pass(message);
      return result;
    } catch (Throwable e) {
      fail(message + " but " + e.getClass().getName() + " was thrown");
      return null;
    }
  }
  
  private void installTestSignatureBundle() throws Exception {
    if (!isBundleInstalled(signatureBundleName)) {
      testSignatureBundle = installBundle(signatureBundleName);
      signatureBundleLocation = testSignatureBundle.getLocation();
    }
    signatureBundleLocation = testSignatureBundle.getLocation();
  }
  
  private InputStream getInputStream(String bundleName) throws Exception {
    return (new URL(getInstallBundleLocation(bundleName))).openStream();
  }
  
  private void printPermissions(String bundleLocation) {
//    PermissionInfo[] pi = permissionAdmin.getPermissions(bundleLocation);
//    for (int j = 0; pi != null && j < pi.length; ++j) {
//      log(pi[j].toString());
//    }
  }
  
  private String getInstallBundleLocation(String bundleName) {
    return getWebServer() + bundleName;
  }
}
